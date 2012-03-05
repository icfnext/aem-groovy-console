import com.citytechinc.cqlibrary.groovyconsole.builder.JcrBuilder
import com.citytechinc.cqlibrary.groovyconsole.metaclass.MetaClassRegistry

import groovy.json.JsonBuilder
import groovy.transform.Field

import javax.jcr.Session

import org.apache.commons.lang.time.StopWatch

import org.codehaus.groovy.control.MultipleCompilationErrorsException

import org.slf4j.LoggerFactory

import com.day.cq.wcm.api.PageManager

@Field log = LoggerFactory.getLogger('groovyconsole')

MetaClassRegistry.registerMetaClasses()

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
    }

    result = script.run()
} catch (MultipleCompilationErrorsException e) {
    log.error('script compilation error', e)

    stackTrace.append(e.message - 'startup failed, Script1.groovy: ')
} catch (Throwable t) {
    log.error('error running script', t)

    sanitizeStacktrace(t)

    def cause = t

    while (cause = cause?.cause) {
        sanitizeStacktrace(cause)
    }

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

def escape(object) {
    object ? object.toString().replaceAll(/\n/, /\\\n/).replaceAll(/"/, /\\"/) : ''
}

def sanitizeStacktrace(t) {
    def filtered = [
        'java.', 'javax.', 'sun.',
        'groovy.', 'org.codehaus.groovy.',
        'groovyconsole'
    ]

    def trace = t.stackTrace
    def newTrace = []

    trace.each { stackTraceElement ->
        if (filtered.every { !stackTraceElement.className.startsWith(it) }) {
            newTrace << stackTraceElement
        }
    }

    def clean = newTrace.toArray(newTrace as StackTraceElement[])

    t.stackTrace = clean
}