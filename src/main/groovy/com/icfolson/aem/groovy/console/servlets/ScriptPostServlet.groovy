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
            def scriptPaths = request.getParameterValues(GroovyConsoleConstants.PARAMETER_SCRIPT_PATHS)

            if (scriptPaths) {
                LOG.debug("running scripts for paths = {}", scriptPaths)

                writeJsonResponse(response, consoleService.runScripts(request, scriptPaths as List))
            } else {
                def scriptPath = request.getParameter(GroovyConsoleConstants.PARAMETER_SCRIPT_PATH)

                if (scriptPath) {
                    LOG.debug("running script for path = {}", scriptPath)

                    writeJsonResponse(response, consoleService.runScript(request, scriptPath))
                } else {
                    writeJsonResponse(response, consoleService.runScript(request))
                }
            }
        } else {
            response.status = SC_FORBIDDEN
        }
    }
}