package com.icfolson.aem.groovy.console.servlets

import com.icfolson.aem.groovy.console.GroovyConsoleService
import com.icfolson.aem.groovy.console.configuration.ConfigurationService
import com.icfolson.aem.groovy.console.constants.GroovyConsoleConstants
import groovy.util.logging.Slf4j
import org.apache.felix.scr.annotations.Reference
import org.apache.felix.scr.annotations.sling.SlingServlet
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.SlingHttpServletResponse

import javax.servlet.ServletException

import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN

@SlingServlet(paths = "/bin/groovyconsole/post")
@Slf4j("LOG")
class ScriptPostServlet extends AbstractJsonResponseServlet {

    @Reference
    private ConfigurationService configurationService

    @Reference
    private GroovyConsoleService consoleService

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws
        ServletException, IOException {
        if (configurationService.hasPermission(request)) {
            def async = Boolean.valueOf(request.getParameter(GroovyConsoleConstants.PARAMETER_ASYNC))

            if (async) {
                LOG.info("running script asynchronously...")

                Thread.start {
                    consoleService.runScript(request)
                }

                writeJsonResponse(response, [:])
            } else {
                writeJsonResponse(response, consoleService.runScript(request))
            }
        } else {
            response.status = SC_FORBIDDEN
        }
    }
}