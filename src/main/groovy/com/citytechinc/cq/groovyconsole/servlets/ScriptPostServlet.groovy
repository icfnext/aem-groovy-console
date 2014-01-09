package com.citytechinc.cq.groovyconsole.servlets

import com.citytechinc.cq.groovyconsole.services.GroovyRunService
import groovy.json.JsonBuilder
import groovy.util.logging.Slf4j
import org.apache.felix.scr.annotations.Reference
import org.apache.felix.scr.annotations.sling.SlingServlet
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.SlingHttpServletResponse

import javax.servlet.ServletException



@SlingServlet(paths = "/bin/groovyconsole/post")
@Slf4j("LOG")
class ScriptPostServlet extends AbstractScriptServlet {


    @Reference
    GroovyRunService groovyRunService

    protected static final def SCRIPT_PARAM = "script"

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws
        ServletException, IOException {

        def scriptContent = request.getRequestParameter(SCRIPT_PARAM)?.getString(ENCODING)

        def jsonBuilder = groovyRunService.runGroovyScript(scriptContent,request)

        response.contentType = "application/json"

        jsonBuilder.writeTo(response.writer)
    }


}
