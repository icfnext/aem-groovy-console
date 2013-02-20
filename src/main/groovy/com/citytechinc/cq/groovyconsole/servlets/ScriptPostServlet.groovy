package com.citytechinc.cq.groovyconsole.servlets

import com.citytechinc.cq.groovyconsole.builders.NodeBuilder
import com.citytechinc.cq.groovyconsole.builders.PageBuilder
import com.citytechinc.cq.groovyconsole.metaclass.GroovyConsoleMetaClassRegistry
import com.day.cq.wcm.api.PageManager
import groovy.json.JsonBuilder
import org.apache.felix.scr.annotations.Activate
import org.apache.felix.scr.annotations.Deactivate
import org.apache.felix.scr.annotations.Reference
import org.apache.felix.scr.annotations.sling.SlingServlet
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.SlingHttpServletResponse
import org.apache.sling.api.resource.ResourceResolverFactory
import org.apache.sling.api.servlets.SlingAllMethodsServlet
import org.apache.sling.jcr.api.SlingRepository
import org.codehaus.groovy.control.MultipleCompilationErrorsException
import org.osgi.framework.BundleContext
import org.slf4j.LoggerFactory

import javax.servlet.ServletException

@SlingServlet(paths = "/bin/groovyconsole/post", label = "Groovy Console POST Servlet", description = "Groovy script execution servlet.")
class ScriptPostServlet extends SlingAllMethodsServlet {

	static final long serialVersionUID = 1L

	static final def SCRIPT_PARAM = "script"

	static final def ENCODING = "UTF-8"

	static final def LOG = LoggerFactory.getLogger(ScriptPostServlet)

	static final def RUNNING_TIME = { closure ->
		def start = System.currentTimeMillis()

		closure.call()

		def date = new Date()

		date.setTime(System.currentTimeMillis() - start)
		date.format("HH:mm:ss.SSS", TimeZone.getTimeZone("GMT"))
	}

	@Reference
	SlingRepository repository

	@Reference
	ResourceResolverFactory resourceResolverFactory

	def session

	def resourceResolver

	def pageManager

	def bundleContext

	@Override
	protected void doPost(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws ServletException, IOException {
		def stream = new ByteArrayOutputStream()
		def binding = createBinding(request, stream)
		def shell = new GroovyShell(binding)

		def stackTrace = new StringWriter()
		def errorWriter = new PrintWriter(stackTrace)

		def oldClassLoader = Thread.currentThread().contextClassLoader

		Thread.currentThread().contextClassLoader = new GroovyClassLoader()

		def result = ""
		def runningTime = ""

		try {
			GroovyConsoleMetaClassRegistry.registerMetaClasses()

			def script = shell.parse(request.getRequestParameter(SCRIPT_PARAM).getString(ENCODING))

			addMetaClass(script)

			runningTime = RUNNING_TIME {
				result = script.run()
			}

			LOG.debug "doPost() script execution completed, running time = $runningTime"
		} catch (MultipleCompilationErrorsException e) {
			LOG.error("script compilation error", e)
			e.printStackTrace(errorWriter)
		} catch (Throwable t) {
			LOG.error("error running script", t)
			t.printStackTrace(errorWriter)
		} finally {
			Thread.currentThread().setContextClassLoader(oldClassLoader)
		}

		response.contentType = "application/json"

		def json = new JsonBuilder([
			executionResult: result as String,
			outputText: stream.toString(ENCODING),
			stacktraceText: stackTrace.toString(),
			runningTime: runningTime
		])

		json.writeTo(response.writer)
	}

	def createBinding(request, stream) {
		def printStream = new PrintStream(stream, true, ENCODING)

		new Binding([
			out: printStream,
			log: LoggerFactory.getLogger("groovyconsole"),
			session: session,
			slingRequest: request,
			pageManager: pageManager,
			resourceResolver: resourceResolver,
			nodeBuilder: new NodeBuilder(session),
			pageBuilder: new PageBuilder(session)
		])
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
		}
	}

	@Activate
	void activate(final BundleContext bundleContext) {
		this.bundleContext = bundleContext

		session = repository.loginAdministrative(null)
		resourceResolver = resourceResolverFactory.getAdministrativeResourceResolver(null)
		pageManager = resourceResolver.adaptTo(PageManager)
	}

	@Deactivate
	void deactivate() {
		if (session) {
			session.logout()
		}

		if (resourceResolver) {
			resourceResolver.close()
		}
	}
}
