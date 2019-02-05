package com.icfolson.aem.groovy.console.servlets

import com.icfolson.aem.groovy.console.GroovyConsoleService
import com.icfolson.aem.groovy.console.configuration.ConfigurationService
import com.icfolson.aem.groovy.console.constants.GroovyConsoleConstants
import groovy.util.logging.Slf4j
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.SlingHttpServletResponse
import org.apache.sling.servlets.annotations.SlingServletPaths
import org.osgi.service.component.annotations.Component
import org.osgi.service.component.annotations.Reference

import javax.servlet.Servlet
import javax.servlet.ServletException

import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN

@Component(service = Servlet)
@SlingServletPaths("/bin/groovyconsole/post")
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
            def scriptPaths = request.getParameterValues(GroovyConsoleConstants.PARAMETER_SCRIPT_PATHS)

            if (scriptPaths) {
                LOG.debug("running scripts for paths = {}", scriptPaths)

                writeJsonResponse(response, consoleService.runScripts(request, response, scriptPaths as List))
            } else {
                def scriptPath = request.getParameter(GroovyConsoleConstants.PARAMETER_SCRIPT_PATH)

                if (scriptPath) {
                    LOG.debug("running script for path = {}", scriptPath)

                    writeJsonResponse(response, consoleService.runScript(request, response, scriptPath))
                } else {
                    writeJsonResponse(response, consoleService.runScript(request, response))
                }
            }
        } else {
            response.status = SC_FORBIDDEN
        }
    }
}