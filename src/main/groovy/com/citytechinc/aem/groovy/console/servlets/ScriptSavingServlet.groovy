package com.citytechinc.aem.groovy.console.servlets

import com.citytechinc.aem.groovy.console.GroovyConsoleService
import org.apache.felix.scr.annotations.Reference
import org.apache.felix.scr.annotations.sling.SlingServlet
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.SlingHttpServletResponse

import javax.servlet.ServletException

@SlingServlet(paths = "/bin/groovyconsole/save")
class ScriptSavingServlet extends AbstractJsonResponseServlet {

    @Reference
    protected GroovyConsoleService consoleService

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws
        ServletException, IOException {
        writeJsonResponse(response, consoleService.saveScript(request))
    }
}