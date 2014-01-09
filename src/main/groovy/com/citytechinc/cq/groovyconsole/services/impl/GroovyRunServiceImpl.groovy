package com.citytechinc.cq.groovyconsole.services.impl

import com.citytechinc.cq.groovy.extension.builders.PageBuilder
import com.citytechinc.cq.groovy.extension.services.OsgiComponentService
import com.citytechinc.cq.groovyconsole.services.ConfigurationService
import com.citytechinc.cq.groovyconsole.services.EmailService
import com.citytechinc.cq.groovyconsole.services.GroovyRunService
import com.day.cq.commons.jcr.JcrConstants
import com.day.cq.replication.ReplicationActionType
import com.day.cq.replication.Replicator
import com.day.cq.search.PredicateGroup
import com.day.cq.search.QueryBuilder
import com.day.cq.wcm.api.PageManager
import groovy.json.JsonBuilder
import groovy.util.logging.Slf4j
import org.apache.felix.scr.annotations.Activate
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.Reference
import org.apache.felix.scr.annotations.Service
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.SlingHttpServletResponse
import org.apache.sling.api.resource.ResourceResolver
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.MultipleCompilationErrorsException
import org.osgi.framework.BundleContext
import org.slf4j.LoggerFactory

import javax.jcr.Session
import javax.servlet.ServletException

import static org.codehaus.groovy.control.customizers.builder.CompilerCustomizationBuilder.withConfig

/**
 * @author Daniel Madejek
 */
@Service
@Component
@Slf4j("LOG")
public class GroovyRunServiceImpl implements GroovyRunService{


    static final def ENCODING = "UTF-8"

    static final def EMAIL_SUBJECT = "CQ Groovy Console Script Execution Result"

    static final def EMAIL_TEMPLATE_SUCCESS = "/email-success.template"

    static final def EMAIL_TEMPLATE_FAIL = "/email-fail.template"

    static final def FORMAT_TIMESTAMP = "yyyy-MM-dd hh:mm:ss"

    static final def STAR_IMPORTS = ["javax.jcr", "org.apache.sling.api", "org.apache.sling.api.resource",
            "com.day.cq.search", "com.day.cq.tagging", "com.day.cq.wcm.api"].toArray(new String[0])

    static final def RUNNING_TIME = { closure ->
        def start = System.currentTimeMillis()

        closure()

        def date = new Date()

        date.time = System.currentTimeMillis() - start
        date.format("HH:mm:ss.SSS", TimeZone.getTimeZone("GMT"))
    }

    @Reference
    Replicator replicator

    @Reference
    OsgiComponentService componentService

    @Reference
    QueryBuilder queryBuilder

    @Reference
    ConfigurationService configurationService

    @Reference
    EmailService emailService

    def bundleContext

    @Override
    public JsonBuilder runGroovyScript(String scriptContent,SlingHttpServletRequest request) {

        def resourceResolver = request.resourceResolver
        def session = resourceResolver.adaptTo(Session)
        def pageManager = resourceResolver.adaptTo(PageManager)

        def stream = new ByteArrayOutputStream()
        def binding = createBinding(request, stream)
        def configuration = createConfiguration()
        def shell = new GroovyShell(binding, configuration)

        def stackTrace = new StringWriter()
        def errorWriter = new PrintWriter(stackTrace)

        def result = ""
        def runningTime = ""
        def output = ""
        def error = ""

        try {
            def script = shell.parse(scriptContent)

            addMetaClass(resourceResolver, session, pageManager, script)

            runningTime = RUNNING_TIME {
                result = script.run()
            }

            LOG.debug "doPost() script execution completed, running time = $runningTime"

            output = stream.toString(ENCODING);

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

            emailService.sendEmail(session, scriptContent, output, null, false)
        } finally {
            stream.close()
            errorWriter.close()
        }

        return new JsonBuilder([
                executionResult: result as String,
                outputText: output,
                stacktraceText: error,
                runningTime: runningTime
        ])
    }

    def createConfiguration() {
        def configuration = new CompilerConfiguration()

        withConfig(configuration) {
            imports {
                star STAR_IMPORTS
            }
        }
    }

    def createBinding(request, stream) {
        def printStream = new PrintStream(stream, true, ENCODING)

        def resourceResolver = request.resourceResolver
        def session = resourceResolver.adaptTo(Session)

        new Binding([
                out: printStream,
                log: LoggerFactory.getLogger("groovyconsole"),
                session: session,
                slingRequest: request,
                pageManager: resourceResolver.adaptTo(PageManager),
                resourceResolver: resourceResolver,
                queryBuilder: queryBuilder,
                nodeBuilder: new com.citytechinc.cq.groovy.extension.builders.NodeBuilder(session),
                pageBuilder: new PageBuilder(session)
        ])
    }

    def addMetaClass(resourceResolver, session, pageManager, script) {
        script.metaClass {
            delegate.getNode = { String path ->
                session.getNode(path)
            }

            delegate.getResource = { String path ->
                resourceResolver.getResource(path)
            }

            delegate.getPage = { String path ->
                pageManager.getPage(path)
            }

            delegate.move = { String src ->
                ["to": { String dst ->
                    session.move(src, dst)
                    session.save()
                }]
            }

            delegate.copy = { String src ->
                ["to": { dst ->
                    session.workspace.copy(src, dst)
                }]
            }

            delegate.save = {
                session.save()
            }

            delegate.getService = { serviceType ->
                def ref = bundleContext.getServiceReference(serviceType)

                bundleContext.getService(ref)
            }

            delegate.activate = { String path ->
                replicator.replicate(session, ReplicationActionType.ACTIVATE, path)
            }

            delegate.deactivate = { String path ->
                replicator.replicate(session, ReplicationActionType.DEACTIVATE, path)
            }

            delegate.doWhileDisabled = { String componentClassName, Closure closure ->
                componentService.doWhileDisabled(componentClassName, closure)
            }

            delegate.createQuery { Map predicates ->
                queryBuilder.createQuery(PredicateGroup.create(predicates), session)
            }
        }
    }

    @Activate
    void activate(BundleContext bundleContext) {
        this.bundleContext = bundleContext
    }

    def saveOutput(session, output) {
        if (configurationService.crxOutputEnabled) {
            def date = new Date()

            def folderPath = "${configurationService.crxOutputFolder}/${date.format('yyyy/MM/dd')}"

            def folderNode = session.rootNode

            folderPath.tokenize("/").each { name ->
                folderNode = folderNode.getOrAddNode(name, JcrConstants.NT_FOLDER)
            }

            def fileName = date.format('hhmmss')

            new ByteArrayInputStream(output.getBytes(ENCODING)).withStream { stream ->
                session.valueFactory.createBinary(stream).withBinary { binary ->
                    saveFile(session, folderNode, fileName, "text/plain", binary)
                }
            }
        }
    }
}
