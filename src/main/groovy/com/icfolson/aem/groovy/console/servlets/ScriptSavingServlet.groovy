package com.icfolson.aem.groovy.console.servlets

import com.icfolson.aem.groovy.console.GroovyConsoleService
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.SlingHttpServletResponse
import org.apache.sling.servlets.annotations.SlingServletPaths
import org.osgi.service.component.annotations.Component
import org.osgi.service.component.annotations.Reference

import javax.servlet.Servlet
import javax.servlet.ServletException

@Component(service = Servlet)
@SlingServletPaths("/bin/groovyconsole/save")
class ScriptSavingServlet extends AbstractJsonResponseServlet {

    @Reference
    private GroovyConsoleService consoleService

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws
        ServletException, IOException {
        writeJsonResponse(response, consoleService.saveScript(request))
    }
}