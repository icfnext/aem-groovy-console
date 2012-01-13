import com.citytechinc.cqlibrary.groovyconsole.builder.JcrBuilder

import com.day.cq.wcm.api.Page
import com.day.cq.wcm.api.PageManager

import javax.jcr.Node
import javax.jcr.PropertyType
import javax.jcr.Value
import javax.jcr.Session

import groovy.json.JsonBuilder

import org.apache.commons.lang.time.StopWatch

import org.codehaus.groovy.control.MultipleCompilationErrorsException

import org.slf4j.LoggerFactory

final def resolver = resource.resourceResolver

registerMetaClasses()

session = resolver.adaptTo(Session)
pageManager = resolver.adaptTo(PageManager)

log = LoggerFactory.getLogger('groovyconsole')

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

def registerMetaClasses() {
    Node.metaClass {
        iterator {
            delegate.nodes
        }

        recurse { c ->
            c(delegate)

            delegate.nodes.each { node ->
                node.recurse(c)
            }
        }

        getNodeSafe { relativePath ->
            def node = delegate

            relativePath.split("/").each { path ->
                if (node.hasNode(path)) {
                    node = node.getNode(path)
                } else {
                    node = node.addNode(path)
                }
            }

            node
        }

        getNodeSafe { name, nodeTypeName ->
            delegate.hasNode(name) ? delegate.getNode(name) : delegate.addNode(name, nodeTypeName)
        }

        getProperty { String name ->
            def result = null

            if (delegate.hasProperty(name)) {
                def method = Node.class.getMethod('getProperty', String)
                def property = method.invoke(delegate, name)

                if (property.multiple) {
                    result = property.values.collect { getResult(it) }
                } else {
                    result = getResult(property.value)
                }
            } else {
                result = ""
            }

            result
        }

        setProperty { String name, value ->
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
        iterator {
            delegate.listChildren()
        }

        recurse { Closure c ->
            c(delegate)

            delegate.listChildren().each { child ->
                child.recurse(c)
            }
        }

        getNode {
            delegate.contentResource?.adaptTo(Node)
        }

        getProperty { String name ->
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