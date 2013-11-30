import com.citytechinc.cqlibrary.groovyconsole.builders.NodeBuilder
import com.citytechinc.cqlibrary.groovyconsole.builders.PageBuilder
import com.citytechinc.cqlibrary.groovyconsole.metaclass.GroovyConsoleMetaClassRegistry
import com.day.cq.wcm.api.PageManager

import groovy.json.JsonBuilder

import javax.jcr.Session

import org.apache.sling.api.resource.ResourceResolverFactory
import org.apache.sling.jcr.api.SlingRepository
import org.apache.sling.jcr.resource.JcrResourceConstants
import org.codehaus.groovy.control.MultipleCompilationErrorsException
import org.slf4j.LoggerFactory

// global variables
log = LoggerFactory.getLogger('groovyconsole')
session = getSession()
resourceResolver = getResourceResolver()
pageManager = resourceResolver.adaptTo(PageManager)

def encoding = 'UTF-8'
def stream = new ByteArrayOutputStream()
def printStream = new PrintStream(stream, true, encoding)

def scriptBinding = new Binding([
    out: printStream,
    log: log,
    session: session,
    slingRequest: request,
    sling: sling,
    pageManager: pageManager,
    resourceResolver: resourceResolver,
    nodeBuilder: new NodeBuilder(session),
    pageBuilder: new PageBuilder(session)
])

def shell = new GroovyShell(scriptBinding)

def stackTrace = new StringWriter()
def errWriter = new PrintWriter(stackTrace)

def originalOut = System.out
def originalErr = System.err

System.setOut(printStream)
System.setErr(printStream)

def result = ''

def startTime = System.currentTimeMillis()

log.debug "script execution started"

ClassLoader oldClassLoader = Thread.currentThread().contextClassLoader
Thread.currentThread().contextClassLoader = new GroovyClassLoader()

try {
    requestSession = request.getResourceResolver().adaptTo(Session)
    if (requestSession == null || !requestSession.hasPermission("/", "set_property")) {
      errWriter.println("You don't have administrative access to repository. Not sufficient privileges.")
      errWriter.println("Please login with administrative access.")
    } else {
        GroovyConsoleMetaClassRegistry.registerMetaClasses()

        def script = shell.parse(request.getRequestParameter('script').getString('UTF-8'))

        addMetaClass(script)

        result = script.run()
    }
} catch (MultipleCompilationErrorsException e) {
    log.error('script compilation error', e)

    e.printStackTrace(errWriter)
} catch (Throwable t) {
    log.error('error running script', t)

    t.printStackTrace(errWriter)
} finally {
    Thread.currentThread().setContextClassLoader(oldClassLoader)

    System.setOut(originalOut)
    System.setErr(originalErr)

    resourceResolver.close()
    session.logout()
}

def time = getRunningTime(startTime)

log.debug "script execution completed, running time: $time"

response.contentType = 'application/json'

def json = new JsonBuilder()

json {
    executionResult result as String
    outputText stream.toString(encoding)
    stacktraceText stackTrace.toString()
    runningTime time
}

out.println json.toString()

def getSession() {
    def repository = sling.getService(SlingRepository)

    repository.loginAdministrative(null)
}

def getResourceResolver() {
    def authenticationInfo = [:]

    authenticationInfo[JcrResourceConstants.AUTHENTICATION_INFO_SESSION] = session

    sling.getService(ResourceResolverFactory).getResourceResolver(authenticationInfo)
}

def addMetaClass(script) {
    script.metaClass {
        delegate.getNode = { path ->
            session.getNode(path)
        }

        delegate.getPage = { path ->
            pageManager.getPage(path)
        }

        delegate.move = { src ->
            ['to': { dst ->
                session.move(src, dst)
                session.save()
            }]
        }

        delegate.copy = { src ->
            ['to': { dst ->
                session.workspace.copy(src, dst)
            }]
        }

        delegate.save = {
            session.save()
        }

        delegate.getService = { serviceType ->
            sling.getService(serviceType)
        }
    }
}

def getRunningTime(startTime) {
    def date = new Date()

    date.setTime(System.currentTimeMillis() - startTime)

    date.format('HH:mm:ss.SSS', TimeZone.getTimeZone('GMT'))
}