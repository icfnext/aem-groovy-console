package com.citytechinc.aem.groovy.console.servlets

import com.citytechinc.aem.groovy.console.GroovyConsoleService
import com.citytechinc.aem.groovy.console.response.RunScriptResponse
import org.apache.felix.scr.annotations.Reference
import org.apache.felix.scr.annotations.sling.SlingServlet
import org.apache.sling.api.SlingHttpServletRequest

@SlingServlet(paths = "/bin/groovyconsole/post/file")
class ScriptFilePostServlet extends AbstractScriptPostServlet {

    @Reference
    GroovyConsoleService consoleService

    @Override
    protected RunScriptResponse runScript(SlingHttpServletRequest request) {
        consoleService.runScriptFile(request)
    }
}
