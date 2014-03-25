package com.citytechinc.cq.groovyconsole.servlets

import com.citytechinc.cq.groovyconsole.services.ConfigurationService
import com.citytechinc.cq.groovyconsole.services.GroovyConsoleService
import groovy.json.JsonBuilder
import groovy.util.logging.Slf4j
import org.apache.felix.scr.annotations.Reference
import org.apache.felix.scr.annotations.sling.SlingServlet
import org.apache.jackrabbit.api.security.user.UserManager
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.SlingHttpServletResponse
import org.apache.sling.api.servlets.SlingAllMethodsServlet

import javax.servlet.ServletException

import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN

@SlingServlet(paths = "/bin/groovyconsole/post")
@Slf4j("LOG")
class ScriptPostServlet extends SlingAllMethodsServlet {

	@Reference
	ConfigurationService configurationService

    @Reference
    GroovyConsoleService consoleService

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws
        ServletException, IOException {
        if (hasPermission(request)) {
            def result = consoleService.runScript(request)

            response.contentType = "application/json"

            new JsonBuilder(result).writeTo(response.writer)
        } else {
            response.setStatus(SC_FORBIDDEN)
        }
    }

    boolean hasPermission(request) {
        def user = request.resourceResolver.adaptTo(UserManager).getAuthorizable(request.userPrincipal)

        def memberOfGroupIds = user.memberOf()*.getID()
        def allowedGroupIds = configurationService.allowedGroups

        allowedGroupIds ? memberOfGroupIds.intersect(allowedGroupIds) : true
    }
}
