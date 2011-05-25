import com.day.cq.wcm.api.Page
import com.day.cq.wcm.api.PageManager

import javax.jcr.Node
import javax.jcr.Session

import groovy.json.JsonBuilder

import org.codehaus.groovy.control.MultipleCompilationErrorsException

import org.slf4j.LoggerFactory

final def resolver = resource.resourceResolver

session = resolver.adaptTo(Session.class)
pageManager = resolver.adaptTo(PageManager.class)

log = LoggerFactory.getLogger('groovyconsole')

Node.metaClass {
    delegate.recurse = { Closure c ->
        c(delegate)

        delegate.nodes.each { node ->
            node.recurse(c)
        }
    }
}

Page.metaClass {
    delegate.recurse = { Closure c ->
        c(delegate)

        delegate.listChildren().each { child ->
            child.recurse(c)
        }
    }
}

def encoding = 'UTF-8'
def stream = new ByteArrayOutputStream()
def printStream = new PrintStream(stream, true, encoding)

def scriptBinding = new Binding([
    out: printStream,
    log: log,
    session: session,
    pageManager: pageManager
])

def shell = new GroovyShell(scriptBinding)

def stackTrace = new StringWriter()
def errWriter = new PrintWriter(stackTrace)

def originalOut = System.out
def originalErr = System.err

System.setOut(printStream)
System.setErr(printStream)

def result = ''

try {
    def script = shell.parse(request.getParameter('script'))

    script.metaClass {
        delegate.node = { String path ->
            session.getNode(path)
        }

        delegate.page = { String path ->
            pageManager.getPage(path)
        }
    }

    result = script.run()

    log.info('script result = ' + result)
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

response.contentType = 'application/json'

def json = new JsonBuilder()

json {
    executionResult result as String
    outputText stream.toString(encoding)
    stacktraceText stackTrace.toString()
}

log.info('json response = ' + json.toString())

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