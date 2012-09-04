import com.citytechinc.cqlibrary.groovyconsole.builder.JcrBuilder
import com.citytechinc.cqlibrary.groovyconsole.metaclass.GroovyConsoleMetaClassRegistry

import groovy.json.JsonBuilder
import groovy.transform.Field

import javax.jcr.Session

import org.apache.commons.lang.time.StopWatch

import org.codehaus.groovy.control.MultipleCompilationErrorsException

import org.slf4j.LoggerFactory

import com.day.cq.wcm.api.PageManager

@Field log = LoggerFactory.getLogger('groovyconsole')

GroovyConsoleMetaClassRegistry.registerMetaClasses()

resolver = resource.resourceResolver
session = resolver.adaptTo(Session)
pageManager = resolver.adaptTo(PageManager)

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
    resourceResolver: resolver,
    builder: new JcrBuilder(session)
])

def shell = new GroovyShell(scriptBinding)

def stackTrace = new StringWriter()
def errWriter = new PrintWriter(stackTrace)

def originalOut = System.out
def originalErr = System.err

System.setOut(printStream)
System.setErr(printStream)

def result = ''

def stopWatch = new StopWatch()

stopWatch.start()

try {
    def script = shell.parse(request.getRequestParameter('script').getString('UTF-8'))

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

    result = script.run()
} catch (MultipleCompilationErrorsException e) {
    log.error('script compilation error', e)

    e.printStackTrace(errWriter)
} catch (Throwable t) {
    log.error('error running script', t)

    t.printStackTrace(errWriter)
} finally {
    System.setOut(originalOut)
    System.setErr(originalErr)
}

stopWatch.stop()

response.contentType = 'application/json'

def json = new JsonBuilder()

json {
    executionResult result as String
    outputText stream.toString(encoding)
    stacktraceText stackTrace.toString()
    runningTime stopWatch.toString()
}

out.println json.toString()