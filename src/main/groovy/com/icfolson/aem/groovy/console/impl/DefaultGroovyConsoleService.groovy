package com.icfolson.aem.groovy.console.impl

import static com.google.common.base.Preconditions.checkNotNull
import static com.icfolson.aem.groovy.console.constants.GroovyConsoleConstants.EXTENSION_GROOVY
import static com.icfolson.aem.groovy.console.constants.GroovyConsoleConstants.PARAMETER_DATA
import static com.icfolson.aem.groovy.console.constants.GroovyConsoleConstants.PATH_CONSOLE_ROOT
import static org.codehaus.groovy.control.customizers.builder.CompilerCustomizationBuilder.withConfig

import java.util.concurrent.CopyOnWriteArrayList

import javax.jcr.Node
import javax.jcr.Session

import org.apache.commons.lang3.CharEncoding
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.Reference
import org.apache.felix.scr.annotations.ReferenceCardinality
import org.apache.felix.scr.annotations.ReferencePolicy
import org.apache.felix.scr.annotations.Service
import org.apache.jackrabbit.util.Text
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.SlingHttpServletResponse
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.MultipleCompilationErrorsException

import com.day.cq.commons.jcr.JcrConstants
import com.day.cq.commons.jcr.JcrUtil
import com.google.common.net.MediaType
import com.icfolson.aem.groovy.console.GroovyConsoleService
import com.icfolson.aem.groovy.console.api.BindingVariable
import com.icfolson.aem.groovy.console.audit.AuditService
import com.icfolson.aem.groovy.console.configuration.ConfigurationService
import com.icfolson.aem.groovy.console.extension.ExtensionService
import com.icfolson.aem.groovy.console.notification.NotificationService
import com.icfolson.aem.groovy.console.response.RunScriptResponse
import com.icfolson.aem.groovy.console.response.SaveScriptResponse

import groovy.json.JsonException
import groovy.json.JsonSlurper
import groovy.transform.Synchronized
import groovy.util.logging.Slf4j

@Service(GroovyConsoleService)
@Component
@Slf4j("LOG")
class DefaultGroovyConsoleService implements GroovyConsoleService {

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

    @Reference(cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE,
        referenceInterface = NotificationService, policy = ReferencePolicy.DYNAMIC)
    private List<NotificationService> notificationServices = new CopyOnWriteArrayList<>()

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
        def session = request.resourceResolver.adaptTo(Session)

        def scriptContent

        if (scriptPath) {
            scriptContent = loadScriptContent(session, scriptPath)
        } else {
            scriptContent = request.getRequestParameter(PARAMETER_SCRIPT)?.getString(CharEncoding.UTF_8)
        }

        checkNotNull(scriptContent, "Script content cannot be empty.")

        def data = request.getRequestParameter(PARAMETER_DATA)?.getString(CharEncoding.UTF_8)
        def stream = new ByteArrayOutputStream()
        def runScriptResponse = null

        def printStream = new PrintStream(stream, true, CharEncoding.UTF_8)
        def binding = getBinding(extensionService.getBindingVariables(request, response, printStream), data, printStream)

        try {
            def script = new GroovyShell(binding, configuration).parse(scriptContent)

            extensionService.getScriptMetaClasses(request).each { meta ->
                script.metaClass(meta)
            }

            def result = null

            def runningTime = RUNNING_TIME {
                result = script.run()
            }

            LOG.debug("script execution completed, running time = {}", runningTime)

            runScriptResponse = RunScriptResponse.fromResult(scriptContent, data, result,
                stream.toString(CharEncoding.UTF_8), runningTime)

            auditAndNotify(session, runScriptResponse)
        } catch (MultipleCompilationErrorsException e) {
            LOG.error("script compilation error", e)

            runScriptResponse = RunScriptResponse.fromException(scriptContent, stream.toString(CharEncoding.UTF_8), e)
        } catch (Throwable t) {
            LOG.error("error running script", t)

            runScriptResponse = RunScriptResponse.fromException(scriptContent, stream.toString(CharEncoding.UTF_8), t)

            auditAndNotify(session, runScriptResponse)
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

    @Synchronized
    void bindNotificationService(NotificationService notificationService) {
        notificationServices.add(notificationService)

        LOG.info("added notification service = {}", notificationService.class.name)
    }

    @Synchronized
    void unbindNotificationServices(NotificationService notificationService) {
        notificationServices.remove(notificationService)

        LOG.info("removed notification service = {}", notificationService.class.name)
    }

    // internals

    private void auditAndNotify(Session session, RunScriptResponse response) {
        if (!configurationService.auditDisabled) {
            auditService.createAuditRecord(session, response)
        }

        notificationServices.each { notificationService ->
            notificationService.notify(session, response)
        }
    }

    private Binding getBinding(Map<String, BindingVariable> bindingVariables, String data, PrintStream stream) {
        def binding = new Binding()

        binding["out"] = stream

        bindingVariables.each { name, variable ->
            binding.setVariable(name, variable.value)
        }

        if (data) {
            try {
                binding["data"] = new JsonSlurper().parseText(data)
            } catch (JsonException ignored) {
                // if data cannot be parsed as a JSON object, bind it as a String
                binding["data"] = data
            }
        }

        binding
    }

    private CompilerConfiguration getConfiguration() {
        def configuration = new CompilerConfiguration()

        withConfig(configuration) {
            imports {
                star extensionService.starImports*.packageName as String[]
            }
        }
    }

    private String loadScriptContent(Session session, String scriptPath) {
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

        def stream = new ByteArrayInputStream(script.getBytes(CharEncoding.UTF_8))
        def binary = session.valueFactory.createBinary(stream)

        resourceNode.setProperty(JcrConstants.JCR_MIMETYPE, mimeType)
        resourceNode.setProperty(JcrConstants.JCR_ENCODING, CharEncoding.UTF_8)
        resourceNode.setProperty(JcrConstants.JCR_DATA, binary)
        resourceNode.setProperty(JcrConstants.JCR_LASTMODIFIED, date.time)
        resourceNode.setProperty(JcrConstants.JCR_LAST_MODIFIED_BY, session.userID)

        session.save()
        binary.dispose()
    }
}
