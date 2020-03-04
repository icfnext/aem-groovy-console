package com.icfolson.aem.groovy.console.impl

import com.day.cq.commons.jcr.JcrConstants
import com.day.cq.commons.jcr.JcrUtil
import com.google.common.net.MediaType
import com.google.common.util.concurrent.FutureCallback
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.ListeningExecutorService
import com.google.common.util.concurrent.MoreExecutors
import com.icfolson.aem.groovy.console.GroovyConsoleService
import com.icfolson.aem.groovy.console.api.ScriptContext
import com.icfolson.aem.groovy.console.api.ScriptData
import com.icfolson.aem.groovy.console.api.impl.AsyncScriptContext
import com.icfolson.aem.groovy.console.audit.AuditService
import com.icfolson.aem.groovy.console.configuration.ConfigurationService
import com.icfolson.aem.groovy.console.configuration.impl.ConfigurationServiceProperties
import com.icfolson.aem.groovy.console.extension.ExtensionService
import com.icfolson.aem.groovy.console.notification.NotificationService
import com.icfolson.aem.groovy.console.response.RunScriptResponse
import com.icfolson.aem.groovy.console.response.SaveScriptResponse
import groovy.transform.Synchronized
import groovy.util.logging.Slf4j
import org.apache.jackrabbit.util.Text
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.MultipleCompilationErrorsException
import org.codehaus.groovy.control.customizers.CompilationCustomizer
import org.osgi.service.component.annotations.Activate
import org.osgi.service.component.annotations.Component
import org.osgi.service.component.annotations.Deactivate
import org.osgi.service.component.annotations.Reference
import org.osgi.service.component.annotations.ReferenceCardinality
import org.osgi.service.component.annotations.ReferencePolicy

import javax.jcr.Node
import javax.jcr.Session
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.Executors

import static com.icfolson.aem.groovy.console.constants.GroovyConsoleConstants.CHARSET
import static com.icfolson.aem.groovy.console.constants.GroovyConsoleConstants.FORMAT_RUNNING_TIME
import static com.icfolson.aem.groovy.console.constants.GroovyConsoleConstants.PATH_SCRIPTS_FOLDER
import static com.icfolson.aem.groovy.console.constants.GroovyConsoleConstants.TIME_ZONE_RUNNING_TIME

@Component(service = GroovyConsoleService, immediate = true)
@Slf4j("LOG")
class DefaultGroovyConsoleService implements GroovyConsoleService {

    static final def RUNNING_TIME = { closure ->
        def start = System.currentTimeMillis()

        closure()

        def date = new Date()

        date.time = System.currentTimeMillis() - start
        date.format(FORMAT_RUNNING_TIME, TimeZone.getTimeZone(TIME_ZONE_RUNNING_TIME))
    }

    @Reference
    private ConfigurationService configurationService

    private volatile List<NotificationService> notificationServices = new CopyOnWriteArrayList<>()

    @Reference
    private AuditService auditService

    @Reference
    private ExtensionService extensionService

    private ListeningExecutorService executorService

    @Override
    RunScriptResponse runScript(ScriptContext scriptContext) {
        def runScriptResponse

        if (scriptContext.async) {
            def asyncResourceResolver = scriptContext.resourceResolver.clone(null)
            def asyncScriptContext = new AsyncScriptContext(scriptContext, asyncResourceResolver)

            def asyncResult = executorService.submit {
                try {
                    getRunScriptResponse(asyncScriptContext)
                } finally {
                    asyncResourceResolver.close()
                }
            } as ListenableFuture<RunScriptResponse>

            Futures.addCallback(asyncResult, new FutureCallback<RunScriptResponse>() {
                @Override
                void onSuccess(RunScriptResponse result) {
                    LOG.info("asynchronous groovy script completed")
                }

                @Override
                void onFailure(Throwable throwable) {
                    LOG.error("error running asynchronous groovy script", throwable)
                }
            }, executorService)

            runScriptResponse = RunScriptResponse.fromAsync(scriptContext)
        } else {
            runScriptResponse = getRunScriptResponse(scriptContext)
        }

        runScriptResponse
    }

    private RunScriptResponse getRunScriptResponse(ScriptContext scriptContext) {
        def binding = getBinding(scriptContext)

        def runScriptResponse = null

        try {
            def script = new GroovyShell(binding, configuration).parse(scriptContext.scriptContent)

            extensionService.getScriptMetaClasses(scriptContext).each { meta ->
                script.metaClass(meta)
            }

            def result = null

            def runningTime = RUNNING_TIME {
                result = script.run()
            }

            LOG.debug("script execution completed, running time = {}", runningTime)

            runScriptResponse = RunScriptResponse.fromResult(scriptContext, result,
                scriptContext.outputStream.toString(CHARSET), runningTime)

            auditAndNotify(runScriptResponse)
        } catch (MultipleCompilationErrorsException e) {
            LOG.error("script compilation error", e)

            runScriptResponse = RunScriptResponse.fromException(scriptContext,
                scriptContext.outputStream.toString(CHARSET), e)
        } catch (Throwable t) {
            LOG.error("error running script", t)

            runScriptResponse = RunScriptResponse.fromException(scriptContext,
                scriptContext.outputStream.toString(CHARSET), t)

            auditAndNotify(runScriptResponse)
        } finally {
            scriptContext.outputStream.close()
        }

        runScriptResponse
    }

    @Override
    @Synchronized
    SaveScriptResponse saveScript(ScriptData scriptData) {
        def session = scriptData.resourceResolver.adaptTo(Session)
        def folderNode = JcrUtil.createPath(PATH_SCRIPTS_FOLDER, JcrConstants.NT_FOLDER, session)

        def fileName = scriptData.fileName

        if (folderNode.hasNode(fileName)) {
            folderNode.getNode(fileName).remove()
        }

        saveFile(session, folderNode, scriptData.scriptContent, fileName, new Date(), MediaType.OCTET_STREAM.toString())

        new SaveScriptResponse(fileName)
    }

    @Activate
    void activate(ConfigurationServiceProperties properties) {
        executorService = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(properties.threadPoolSize()))
    }

    @Deactivate
    void deactivate() {
        // ensure executor service is shutdown before allowing deactivation of the service
        executorService.shutdown()
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    @Synchronized
    void bindNotificationService(NotificationService notificationService) {
        notificationServices.add(notificationService)

        LOG.info("added notification service = {}", notificationService.class.name)
    }

    @Synchronized
    void unbindNotificationService(NotificationService notificationService) {
        notificationServices.remove(notificationService)

        LOG.info("removed notification service = {}", notificationService.class.name)
    }

    // internals

    private void auditAndNotify(RunScriptResponse response) {
        if (!configurationService.auditDisabled) {
            auditService.createAuditRecord(response)
        }

        notificationServices.each { notificationService ->
            notificationService.notify(response)
        }
    }

    private Binding getBinding(ScriptContext scriptContext) {
        def binding = new Binding()

        extensionService.getBindingVariables(scriptContext).each { name, variable ->
            binding.setVariable(name, variable.value)
        }

        binding
    }

    private CompilerConfiguration getConfiguration() {
        new CompilerConfiguration().addCompilationCustomizers(extensionService.compilationCustomizers
            as CompilationCustomizer[])
    }

    private void saveFile(Session session, Node folderNode, String script, String fileName, Date date,
        String mimeType) {
        def fileNode = folderNode.addNode(Text.escapeIllegalJcrChars(fileName), JcrConstants.NT_FILE)
        def resourceNode = fileNode.addNode(JcrConstants.JCR_CONTENT, JcrConstants.NT_RESOURCE)

        def stream = new ByteArrayInputStream(script.getBytes(CHARSET))
        def binary = session.valueFactory.createBinary(stream)

        resourceNode.setProperty(JcrConstants.JCR_MIMETYPE, mimeType)
        resourceNode.setProperty(JcrConstants.JCR_ENCODING, CHARSET)
        resourceNode.setProperty(JcrConstants.JCR_DATA, binary)
        resourceNode.setProperty(JcrConstants.JCR_LASTMODIFIED, date.time)
        resourceNode.setProperty(JcrConstants.JCR_LAST_MODIFIED_BY, session.userID)

        session.save()
        binary.dispose()
    }
}
