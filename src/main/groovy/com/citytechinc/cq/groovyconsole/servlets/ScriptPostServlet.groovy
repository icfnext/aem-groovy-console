package com.citytechinc.cq.groovyconsole.servlets
import com.citytechinc.cq.groovy.extension.builders.NodeBuilder
import com.citytechinc.cq.groovy.extension.builders.PageBuilder
import com.citytechinc.cq.groovy.extension.services.OsgiComponentService
import com.citytechinc.cq.groovyconsole.services.GroovyConsoleConfigurationService
import com.day.cq.commons.jcr.JcrConstants
import com.day.cq.mailer.MailService
import com.day.cq.replication.ReplicationActionType
import com.day.cq.replication.Replicator
import com.day.cq.wcm.api.PageManager
import com.day.cq.search.QueryBuilder
import com.day.cq.search.PredicateGroup
import groovy.json.JsonBuilder
import groovy.text.GStringTemplateEngine
import org.apache.commons.mail.HtmlEmail
import org.apache.felix.scr.annotations.Activate
import org.apache.felix.scr.annotations.Reference
import org.apache.felix.scr.annotations.ReferenceCardinality
import org.apache.felix.scr.annotations.sling.SlingServlet
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.SlingHttpServletResponse
import org.apache.sling.api.resource.ResourceResolverFactory
import org.apache.sling.jcr.api.SlingRepository
import org.codehaus.groovy.control.MultipleCompilationErrorsException
import org.osgi.framework.BundleContext
import org.slf4j.LoggerFactory

import javax.jcr.Session
import javax.servlet.ServletException

@SlingServlet(paths = "/bin/groovyconsole/post")
class ScriptPostServlet extends AbstractScriptServlet {

    static final long serialVersionUID = 1L

    protected static final def SCRIPT_PARAM = "script"

    static final def EMAIL_SUBJECT = "CQ Groovy Console Script Execution Result"

    static final def EMAIL_TEMPLATE_SUCCESS = "/email-success.template"

    static final def EMAIL_TEMPLATE_FAIL = "/email-fail.template"

    static final def FORMAT_TIMESTAMP = "yyyy-MM-dd hh:mm:ss"

    static final def LOG = LoggerFactory.getLogger(ScriptPostServlet)

    static final def RUNNING_TIME = { closure ->
        def start = System.currentTimeMillis()

        closure()

        def date = new Date()

        date.setTime(System.currentTimeMillis() - start)
        date.format("HH:mm:ss.SSS", TimeZone.getTimeZone("GMT"))
    }

    @Reference
    SlingRepository repository

    @Reference
    Replicator replicator

    @Reference
    OsgiComponentService componentService

    @Reference
    QueryBuilder queryBuilder

    @Reference
    ResourceResolverFactory resourceResolverFactory

    @Reference
    GroovyConsoleConfigurationService configurationService

    @Reference(cardinality = ReferenceCardinality.OPTIONAL_UNARY)
    MailService emailService

    def bundleContext

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws
        ServletException, IOException {
        def resourceResolver = request.resourceResolver
        def session = resourceResolver.adaptTo(Session)
        def pageManager = resourceResolver.adaptTo(PageManager)

        def stream = new ByteArrayOutputStream()
        def binding = createBinding(request, stream)
        def shell = new GroovyShell(binding)

        def stackTrace = new StringWriter()
        def errorWriter = new PrintWriter(stackTrace)

        def result = ""
        def runningTime = ""
        def output = ""
        def error = ""

        def scriptContent = request.getRequestParameter(SCRIPT_PARAM)?.getString(ENCODING)

        try {
            def script = shell.parse(scriptContent)

            addMetaClass(resourceResolver, session, pageManager, script)

            runningTime = RUNNING_TIME {
                result = script.run()
            }

            LOG.debug "doPost() script execution completed, running time = $runningTime"

            output = stream.toString(ENCODING);

            saveOutput(session, output)

            sendEmailSuccess(session, scriptContent, output, runningTime)
        } catch (MultipleCompilationErrorsException e) {
            LOG.error("script compilation error", e)

            e.printStackTrace(errorWriter)
        } catch (Throwable t) {
            LOG.error("error running script", t)

            t.printStackTrace(errorWriter)

            error = stackTrace.toString()

            sendEmailFail(session, scriptContent, error)
        } finally {
            stream.close()
            errorWriter.close()
        }

        response.contentType = "application/json"

        new JsonBuilder([
            executionResult: result as String,
            outputText: output,
            stacktraceText: error,
            runningTime: runningTime
        ]).writeTo(response.writer)
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
            nodeBuilder: new NodeBuilder(session),
            pageBuilder: new PageBuilder(session)
        ])
    }

    def addMetaClass(resourceResolver, session, pageManager, script) {
        script.metaClass {
            delegate.getNode = { path ->
                session.getNode(path)
            }

            delegate.getResource = { path ->
                resourceResolver.getResource(path)
            }

            delegate.getPage = { path ->
                pageManager.getPage(path)
            }

            delegate.move = { src ->
                ["to": { dst ->
                    session.move(src, dst)
                    session.save()
                }]
            }

            delegate.copy = { src ->
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

            delegate.activate = { path ->
                replicator.replicate(session, ReplicationActionType.ACTIVATE, path)
            }

            delegate.deactivate = { path ->
                replicator.replicate(session, ReplicationActionType.DEACTIVATE, path)
            }

            delegate.doWhileDisabled = { componentClassName, closure ->
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

    def sendEmailSuccess(session, scriptContent, output, runningTime) {
        sendEmail(session, EMAIL_TEMPLATE_SUCCESS, scriptContent, output, runningTime)
    }

    def sendEmailFail(session, scriptContent, error) {
        sendEmail(session, EMAIL_TEMPLATE_FAIL, scriptContent, error, null)
    }

    def sendEmail(session, emailTemplate, scriptContent, output, runningTime) {
        if (configurationService.emailEnabled) {
            def recipients = configurationService.emailRecipients

            if (recipients) {
                if (emailService) {
                    def email = new HtmlEmail()

                    email.charset = ENCODING

                    recipients.each { name ->
                        email.addTo(name)
                    }

                    email.subject = EMAIL_SUBJECT

                    def binding = [
                        userId: session.userID,
                        timestamp: new Date().format(FORMAT_TIMESTAMP),
                        script: scriptContent,
                        output: output,
                        runningTime: runningTime
                    ]

                    def template = new GStringTemplateEngine().createTemplate(this.class.getResource(emailTemplate))

                    email.htmlMsg = template.make(binding).toString()

                    Thread.start {
                        emailService.send(email)
                    }
                } else {
                    LOG.warn "email service not available"
                }
            } else {
                LOG.error "email enabled but no recipients configured"
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

            def fileName = date.format('hhmmss')

            new ByteArrayInputStream(output.getBytes(ENCODING)).withStream { stream ->
                session.valueFactory.createBinary(stream).withBinary { binary ->
                    saveFile(session, folderNode, fileName, "text/plain", binary)
                }
            }
        }
    }
}
