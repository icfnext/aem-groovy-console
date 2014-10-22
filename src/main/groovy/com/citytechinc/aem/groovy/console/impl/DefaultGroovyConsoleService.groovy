package com.citytechinc.aem.groovy.console.impl

import com.citytechinc.aem.groovy.console.GroovyConsoleService
import com.citytechinc.aem.groovy.console.audit.AuditService
import com.citytechinc.aem.groovy.console.configuration.ConfigurationService
import com.citytechinc.aem.groovy.console.extension.ExtensionService
import com.citytechinc.aem.groovy.console.notification.NotificationService
import com.citytechinc.aem.groovy.console.response.RunScriptResponse
import com.citytechinc.aem.groovy.console.response.SaveScriptResponse
import com.day.cq.commons.jcr.JcrConstants
import groovy.util.logging.Slf4j
import org.apache.commons.lang3.CharEncoding
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.Reference
import org.apache.felix.scr.annotations.ReferenceCardinality
import org.apache.felix.scr.annotations.ReferencePolicy
import org.apache.felix.scr.annotations.Service
import org.apache.jackrabbit.util.Text
import org.apache.sling.api.SlingHttpServletRequest
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.MultipleCompilationErrorsException

import javax.jcr.Binary
import javax.jcr.Node
import javax.jcr.Session

import static com.citytechinc.aem.groovy.console.constants.GroovyConsoleConstants.EXTENSION_GROOVY
import static com.citytechinc.aem.groovy.console.constants.GroovyConsoleConstants.PATH_CONSOLE_ROOT
import static org.codehaus.groovy.control.customizers.builder.CompilerCustomizationBuilder.withConfig

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
    ConfigurationService configurationService

    @Reference(cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE,
        referenceInterface = NotificationService, policy = ReferencePolicy.DYNAMIC)
    List<NotificationService> notificationServices = []

    @Reference
    AuditService auditService

    @Reference
    ExtensionService extensionService

    @Override
    RunScriptResponse runScript(SlingHttpServletRequest request) {
        def stream = new ByteArrayOutputStream()

        def binding = extensionService.getBinding(request)

        binding["out"] = new PrintStream(stream, true, CharEncoding.UTF_8)

        def session = request.resourceResolver.adaptTo(Session)
        def configuration = createConfiguration()
        def shell = new GroovyShell(binding, configuration)
        def scriptContent = request.getRequestParameter(PARAMETER_SCRIPT)?.getString(CharEncoding.UTF_8)

        def response = null

        try {
            def script = shell.parse(scriptContent)

            extensionService.getScriptMetaClasses(request).each {
                script.metaClass(it)
            }

            def result = null

            def runningTime = RUNNING_TIME {
                result = script.run()
            }

            LOG.debug "script execution completed, running time = $runningTime"

            response = RunScriptResponse.fromResult(scriptContent, result, stream.toString(CharEncoding.UTF_8), runningTime)

            auditAndNotify(session, response)
        } catch (MultipleCompilationErrorsException e) {
            LOG.error "script compilation error", e

            response = RunScriptResponse.fromException(scriptContent, e)
        } catch (Throwable t) {
            LOG.error "error running script", t

            response = RunScriptResponse.fromException(scriptContent, t)

            auditAndNotify(session, response)
        } finally {
            stream.close()
        }

        response
    }

    @Override
    SaveScriptResponse saveScript(SlingHttpServletRequest request) {
        def name = request.getParameter(PARAMETER_FILE_NAME)
        def script = request.getParameter(PARAMETER_SCRIPT)

        def session = request.resourceResolver.adaptTo(Session)

        def folderNode = session.getNode(PATH_CONSOLE_ROOT).getOrAddNode(RELATIVE_PATH_SCRIPT_FOLDER,
            JcrConstants.NT_FOLDER) as Node

        def fileName = name.endsWith(EXTENSION_GROOVY) ? name : "$name$EXTENSION_GROOVY"

        folderNode.removeNode(fileName)

        getScriptBinary(session, script).withBinary { Binary binary ->
            saveFile(session, folderNode, fileName, new Date(), "application/octet-stream", binary)
        }

        new SaveScriptResponse(fileName)
    }

    void bindNotificationService(NotificationService notificationService) {
        notificationServices.add(notificationService)

        LOG.info "added notification service = {}", notificationService.class.name
    }

    void unbindNotificationServices(NotificationService notificationService) {
        notificationServices.remove(notificationService)

        LOG.info "removed notification service = {}", notificationService.class.name
    }

    // internals

    private void auditAndNotify(Session session, RunScriptResponse response) {
        if (!configurationService.auditDisabled) {
            auditService.createAuditRecord(response)
        }

        notificationServices.each { notificationService ->
            notificationService.notify(session, response)
        }
    }

    private def createConfiguration() {
        def configuration = new CompilerConfiguration()

        withConfig(configuration) {
            imports {
                star extensionService.starImports as String[]
            }
        }
    }

    private static def getScriptBinary(Session session, String script) {
        def binary = null

        new ByteArrayInputStream(script.getBytes(CharEncoding.UTF_8)).withStream { stream ->
            binary = session.valueFactory.createBinary(stream)
        }

        binary
    }

    private static void saveFile(Session session, Node folderNode, String fileName, Date date, String mimeType,
        Binary binary) {
        def fileNode = folderNode.addNode(Text.escapeIllegalJcrChars(fileName), JcrConstants.NT_FILE)

        def resourceNode = fileNode.addNode(JcrConstants.JCR_CONTENT, JcrConstants.NT_RESOURCE)

        resourceNode.set(JcrConstants.JCR_MIMETYPE, mimeType)
        resourceNode.set(JcrConstants.JCR_ENCODING, CharEncoding.UTF_8)
        resourceNode.set(JcrConstants.JCR_DATA, binary)
        resourceNode.set(JcrConstants.JCR_LASTMODIFIED, date.time)
        resourceNode.set(JcrConstants.JCR_LAST_MODIFIED_BY, session.userID)

        session.save()
    }
}
