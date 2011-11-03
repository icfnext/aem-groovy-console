import com.citytechinc.cqlibrary.groovyconsole.JcrBuilder

import com.day.cq.wcm.api.Page
import com.day.cq.wcm.api.PageManager

import javax.jcr.Node
import javax.jcr.PropertyType
import javax.jcr.Session
import javax.jcr.Value

import groovy.json.JsonBuilder

import org.apache.commons.lang.time.StopWatch

import org.codehaus.groovy.control.MultipleCompilationErrorsException

import org.slf4j.LoggerFactory

final def resolver = resource.resourceResolver

session = resolver.adaptTo(Session.class)
pageManager = resolver.adaptTo(PageManager.class)
valueFactory = session.valueFactory
log = LoggerFactory.getLogger('groovyconsole')

registerMetaClasses()

def encoding = 'UTF-8'
def stream = new ByteArrayOutputStream()
def printStream = new PrintStream(stream, true, encoding)

def scriptBinding = new Binding([
    out: printStream,
    log: log,
    session: session,
    slingRequest: request,
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
        delegate.node = { path ->
            session.getNode(path)
        }

        delegate.page = { path ->
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

stopWatch.stop()

response.contentType = 'application/json'

def json = new JsonBuilder()

json {
    executionResult result as String
    outputText stream.toString(encoding)
    stacktraceText stackTrace.toString()
    runningTime stopWatch.toString()
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

def registerMetaClasses() {
    Node.metaClass {
        delegate.recurse = { Closure c ->
            c(delegate)

            delegate.nodes.each { node ->
                node.recurse(c)
            }
        }

        delegate.getNodeSafe = { name ->
            delegate.hasNode(name) ? delegate.getNode(name) : delegate.addNode(name)
        }

        delegate.getNodeSafe = { name, nodeTypeName ->
            delegate.hasNode(name) ? delegate.getNode(name) : delegate.addNode(name, nodeTypeName)
        }

        delegate.getProperty = { String name ->
            def result = null

            if (delegate.hasProperty(name)) {
                def method = Node.class.getMethod('getProperty', String)
                def property = method.invoke(delegate, name)

                if (property.multiple) {
                    def values = property.values

                    result = values.collect { getResult(it) }
                } else {
                    def value = property.value

                    result = getResult(value)
                }
            } else {
                result = ""
            }

            result
        }

        delegate.setProperty = { String name, value ->
            if (value) {
                if (value instanceof Object[]) {
                    def values = value.collect { valueFactory.createValue(it) }.toArray(new Value[0])

                    def method = Node.class.getMethod('setProperty', String, Value[])

                    method.invoke(delegate, name, values)
                } else {
                    def jcrValue = valueFactory.createValue(value)

                    def method = Node.class.getMethod('setProperty', String, Value)

                    method.invoke(delegate, name, jcrValue)
                }
            } else {
                if (delegate.hasProperty(name)) {
                    def method = Node.class.getMethod('getProperty', String)

                    def property = method.invoke(delegate, name)

                    property.remove()
                }
            }
        }
    }

    Page.metaClass {
        delegate.getNode = {
            delegate.contentResource?.adaptTo(Node)
        }

        delegate.recurse = { Closure c ->
            c(delegate)

            delegate.listChildren().each { child ->
                child.recurse(c)
            }
        }

        delegate.getProperty = { String name ->
            def node = delegate.contentResource?.adaptTo(Node)

            node ? node[name] : null
        }
    }
}

def getResult(value) {
    def result = null

    switch(value.type) {
        case PropertyType.BINARY:
            result = value.binary
            break
        case PropertyType.BOOLEAN:
            result = value.boolean
            break
        case PropertyType.DATE:
            result = value.date
            break
        case PropertyType.DECIMAL:
            result = value.decimal
            break
        case PropertyType.DOUBLE:
            result = value.double
            break
        case PropertyType.LONG:
            result = value.long
            break
        case PropertyType.STRING:
            result = value.string
    }

    result
}