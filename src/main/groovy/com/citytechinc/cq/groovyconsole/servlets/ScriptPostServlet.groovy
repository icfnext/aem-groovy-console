package com.citytechinc.cq.groovyconsole.servlets

import com.citytechinc.cq.groovyconsole.services.ConfigurationService
import com.citytechinc.cq.groovyconsole.services.GroovyConsoleService
import groovy.json.JsonBuilder
import org.apache.felix.scr.annotations.Reference
import org.apache.felix.scr.annotations.sling.SlingServlet
import org.apache.jackrabbit.api.JackrabbitSession
import org.apache.jackrabbit.api.security.user.Group
import org.apache.jackrabbit.api.security.user.UserManager
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.SlingHttpServletResponse
import org.apache.sling.api.servlets.SlingAllMethodsServlet

import javax.jcr.Session
import javax.servlet.ServletException

@SlingServlet(paths = "/bin/groovyconsole/post")
class ScriptPostServlet extends SlingAllMethodsServlet {

	@Reference
	ConfigurationService configurationService

    @Reference
    GroovyConsoleService consoleService

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws
        ServletException, IOException {
	    def result
	    if (hasPermission(request)) {
		    result = consoleService.runScript(request)
	    } else {
		    result = [executionResult:"", outputText:"", stacktraceText: "Error - you do not have permission to use the groovy console"]
	    }

        response.contentType = "application/json"

        new JsonBuilder(result).writeTo(response.writer)
    }

	private boolean hasPermission(SlingHttpServletRequest request) {
		def allowedGroupId = configurationService.allowedGroup
		if(!allowedGroupId){
			return true
		}

		def session = (JackrabbitSession)request.resourceResolver.adaptTo(Session.class)
		UserManager userManager = session.userManager
		Group allowedGroup = userManager.getAuthorizable(allowedGroupId)
		def currentUser = userManager.getAuthorizable(request.userPrincipal)
		allowedGroup && allowedGroup.isMember(currentUser)
	}
}
