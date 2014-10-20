package com.citytechinc.cq.groovyconsole.services.impl

import com.citytechinc.cq.groovyconsole.extension.ExtensionService
import com.citytechinc.cq.groovyconsole.services.ConfigurationService
import com.citytechinc.cq.groovyconsole.services.EmailService
import com.citytechinc.cq.groovyconsole.services.GroovyConsoleService
import com.day.cq.commons.jcr.JcrConstants
import groovy.util.logging.Slf4j
import org.apache.commons.lang3.CharEncoding
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.Reference
import org.apache.felix.scr.annotations.Service
import org.apache.jackrabbit.util.Text
import org.apache.sling.api.SlingHttpServletRequest
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.MultipleCompilationErrorsException

import javax.jcr.Session

import static org.codehaus.groovy.control.customizers.builder.CompilerCustomizationBuilder.withConfig

@Service(GroovyConsoleService)
@Component
@Slf4j("LOG")
class DefaultGroovyConsoleService implements GroovyConsoleService {

    static final String RELATIVE_PATH_SCRIPT_FOLDER = "scripts"

    static final String CONSOLE_ROOT = "/etc/groovyconsole"

    static final String PARAMETER_FILE_NAME = "fileName"

    static final String PARAMETER_SCRIPT = "script"

    static final String EXTENSION_GROOVY = ".groovy"

    static final def STAR_IMPORTS = ["javax.jcr", "org.apache.sling.api", "org.apache.sling.api.resource",
        "com.day.cq.search", "com.day.cq.tagging", "com.day.cq.wcm.api"]

    static final def RUNNING_TIME = { closure ->
        def start = System.currentTimeMillis()

        closure()

        def date = new Date()

        date.time = System.currentTimeMillis() - start
        date.format("HH:mm:ss.SSS", TimeZone.getTimeZone("GMT"))
    }

    @Reference
    ConfigurationService configurationService

    @Reference
    EmailService emailService

    @Reference
    ExtensionService extensionService

    @Override
    Map<String, String> runScript(SlingHttpServletRequest request) {
        def session = request.resourceResolver.adaptTo(Session)

        def stream = new ByteArrayOutputStream()

        def binding = extensionService.getBinding(request)

        binding["out"] = new PrintStream(stream, true, CharEncoding.UTF_8)

        def configuration = createConfiguration()
        def shell = new GroovyShell(binding, configuration)

        def stackTrace = new StringWriter()
        def errorWriter = new PrintWriter(stackTrace)

        def result = ""
        def runningTime = ""
        def output = ""
        def error = ""

        def scriptContent = request.getRequestParameter(PARAMETER_SCRIPT)?.getString(CharEncoding.UTF_8)

        try {
            def script = shell.parse(scriptContent)

            extensionService.getScriptMetaClasses(request).each {
                script.metaClass(it)
            }

            runningTime = RUNNING_TIME {
                result = script.run()
            }

            LOG.debug "script execution completed, running time = $runningTime"

            output = stream.toString(CharEncoding.UTF_8)

            saveOutput(session, output)

            emailService.sendEmail(session, scriptContent, output, runningTime, true)
        } catch (MultipleCompilationErrorsException e) {
            LOG.error("script compilation error", e)

            e.printStackTrace(errorWriter)

            error = stackTrace.toString()
        } catch (Throwable t) {
            LOG.error("error running script", t)

            t.printStackTrace(errorWriter)

            error = stackTrace.toString()

            emailService.sendEmail(session, scriptContent, error, null, false)
        } finally {
            stream.close()
            errorWriter.close()
        }

        [executionResult: result as String, outputText: output, stacktraceText: error, runningTime: runningTime]
    }

    @Override
    Map<String, String> saveScript(SlingHttpServletRequest request) {
        def name = request.getParameter(PARAMETER_FILE_NAME)
        def script = request.getParameter(PARAMETER_SCRIPT)

        def session = request.resourceResolver.adaptTo(Session)

        def folderNode = session.getNode(CONSOLE_ROOT).getOrAddNode(RELATIVE_PATH_SCRIPT_FOLDER, JcrConstants.NT_FOLDER)

        def fileName = name.endsWith(EXTENSION_GROOVY) ? name : "$name$EXTENSION_GROOVY"

        folderNode.removeNode(fileName)

        getScriptBinary(session, script).withBinary { binary ->
            saveFile(session, folderNode, fileName, "application/octet-stream", binary)
        }

        [scriptName: fileName]
    }

    def createConfiguration() {
        def configuration = new CompilerConfiguration()

        withConfig(configuration) {
            imports {
                star STAR_IMPORTS.toArray(new String[STAR_IMPORTS.size()])
            }
        }
    }

    def saveOutput(session, output) {
        if (configurationService.crxOutputEnabled) {
            def date = new Date()

            def folderPath = "${configurationService.crxOutputFolder}/${date.format('yyyy/MM/dd')}"

            def folderNode = session.rootNode

            folderPath.tokenize("/").each { name ->
                folderNode = folderNode.getOrAddNode(name, JcrConstants.NT_FOLDER)
            }

            def fileName = date.format("hhmmss")

            new ByteArrayInputStream(output.getBytes(CharEncoding.UTF_8)).withStream { stream ->
                session.valueFactory.createBinary(stream).withBinary { binary ->
                    saveFile(session, folderNode, fileName, "text/plain", binary)
                }
            }
        }
    }

    def getScriptBinary(session, script) {
        def binary = null

        new ByteArrayInputStream(script.getBytes(CharEncoding.UTF_8)).withStream { stream ->
            binary = session.valueFactory.createBinary(stream)
        }

        binary
    }

    void saveFile(session, folderNode, fileName, mimeType, binary) {
        def fileNode = folderNode.addNode(Text.escapeIllegalJcrChars(fileName), JcrConstants.NT_FILE)

        def resourceNode = fileNode.addNode(JcrConstants.JCR_CONTENT, JcrConstants.NT_RESOURCE)

        resourceNode.set(JcrConstants.JCR_MIMETYPE, mimeType)
        resourceNode.set(JcrConstants.JCR_ENCODING, CharEncoding.UTF_8)
        resourceNode.set(JcrConstants.JCR_DATA, binary)
        resourceNode.set(JcrConstants.JCR_LASTMODIFIED, new Date().time)
        resourceNode.set(JcrConstants.JCR_LAST_MODIFIED_BY, session.userID)

        session.save()
    }
}
