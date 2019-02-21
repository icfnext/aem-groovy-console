package com.icfolson.aem.groovy.console.impl

import com.day.cq.commons.jcr.JcrConstants
import com.day.cq.commons.jcr.JcrUtil
import com.google.common.base.Charsets
import com.google.common.net.MediaType
import com.icfolson.aem.groovy.console.GroovyConsoleService
import com.icfolson.aem.groovy.console.api.ScriptContext
import com.icfolson.aem.groovy.console.audit.AuditService
import com.icfolson.aem.groovy.console.configuration.ConfigurationService
import com.icfolson.aem.groovy.console.extension.ExtensionService
import com.icfolson.aem.groovy.console.notification.NotificationService
import com.icfolson.aem.groovy.console.response.RunScriptResponse
import com.icfolson.aem.groovy.console.response.SaveScriptResponse
import groovy.transform.Synchronized
import groovy.util.logging.Slf4j
import org.apache.jackrabbit.util.Text
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.SlingHttpServletResponse
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.MultipleCompilationErrorsException
import org.codehaus.groovy.control.customizers.CompilationCustomizer
import org.osgi.service.component.annotations.Component
import org.osgi.service.component.annotations.Reference
import org.osgi.service.component.annotations.ReferenceCardinality
import org.osgi.service.component.annotations.ReferencePolicy

import javax.jcr.Node
import javax.jcr.Session
import java.util.concurrent.CopyOnWriteArrayList

import static com.google.common.base.Preconditions.checkNotNull
import static com.icfolson.aem.groovy.console.constants.GroovyConsoleConstants.EXTENSION_GROOVY
import static com.icfolson.aem.groovy.console.constants.GroovyConsoleConstants.PARAMETER_DATA
import static com.icfolson.aem.groovy.console.constants.GroovyConsoleConstants.PATH_CONSOLE_ROOT

@Component(service = GroovyConsoleService)
@Slf4j("LOG")
class DefaultGroovyConsoleService implements GroovyConsoleService {

    private static final String CHARSET = Charsets.UTF_8.name()

    static final String RELATIVE_PATH_SCRIPT_FOLDER = "scripts"

    static final String PARAMETER_FILE_NAME = "fileName"

    static final String PARAMETER_SCRIPT = "script"

    static final String FORMAT_RUNNING_TIME = "HH:mm:ss.SSS"

    static final String TIME_ZONE_RUNNING_TIME = "GMT"

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

    @Override
    RunScriptResponse runScript(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        runScript(request, response, null)
    }

    @Override
    RunScriptResponse runScript(SlingHttpServletRequest request, SlingHttpServletResponse response, String scriptPath) {
        def stream = new ByteArrayOutputStream()

        def scriptContext = new ScriptContext(
            request: request,
            response: response,
            printStream: new PrintStream(stream, true, CHARSET),
            scriptContent: checkNotNull(getScriptContent(request, scriptPath), "Script content cannot be empty."),
            data: request.getRequestParameter(PARAMETER_DATA)?.getString(CHARSET)
        )

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

            runScriptResponse = RunScriptResponse.fromResult(scriptContext, result, stream.toString(CHARSET),
                runningTime)

            auditAndNotify(runScriptResponse)
        } catch (MultipleCompilationErrorsException e) {
            LOG.error("script compilation error", e)

            runScriptResponse = RunScriptResponse.fromException(scriptContext, stream.toString(CHARSET), e)
        } catch (Throwable t) {
            LOG.error("error running script", t)

            runScriptResponse = RunScriptResponse.fromException(scriptContext, stream.toString(CHARSET), t)

            auditAndNotify(runScriptResponse)
        } finally {
            stream.close()
        }

        runScriptResponse
    }

    @Override
    List<RunScriptResponse> runScripts(SlingHttpServletRequest request, SlingHttpServletResponse response,
        List<String> scriptPaths) {
        scriptPaths.collect { scriptPath ->
            runScript(request, response, scriptPath)
        }
    }

    @Override
    @Synchronized
    SaveScriptResponse saveScript(SlingHttpServletRequest request) {
        def session = request.resourceResolver.adaptTo(Session)
        def folderNode = JcrUtil.createPath("$PATH_CONSOLE_ROOT/$RELATIVE_PATH_SCRIPT_FOLDER", JcrConstants.NT_FOLDER,
            session)

        def name = request.getParameter(PARAMETER_FILE_NAME)
        def fileName = name.endsWith(EXTENSION_GROOVY) ? name : "$name$EXTENSION_GROOVY"

        if (folderNode.hasNode(fileName)) {
            folderNode.getNode(fileName).remove()
        }

        def script = request.getParameter(PARAMETER_SCRIPT)

        saveFile(session, folderNode, script, fileName, new Date(), MediaType.OCTET_STREAM.toString())

        new SaveScriptResponse(fileName)
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

    private String getScriptContent(SlingHttpServletRequest request, String scriptPath) {
        if (scriptPath) {
            loadScriptContent(request, scriptPath)
        } else {
            request.getRequestParameter(PARAMETER_SCRIPT)?.getString(CHARSET)
        }
    }

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

    private String loadScriptContent(SlingHttpServletRequest request, String scriptPath) {
        def session = request.resourceResolver.adaptTo(Session)

        def binary = session.getNode(scriptPath)
            .getNode(JcrConstants.JCR_CONTENT)
            .getProperty(JcrConstants.JCR_DATA)
            .binary

        def scriptContent = binary.stream.text

        binary.dispose()

        scriptContent
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
