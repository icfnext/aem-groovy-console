package com.citytechinc.cq.groovyconsole.servlets

import com.citytechinc.cq.groovyconsole.services.ConfigurationService
import com.citytechinc.cq.groovyconsole.services.GroovyConsoleService
import groovy.json.JsonBuilder
import org.apache.felix.scr.annotations.Activate
import org.apache.felix.scr.annotations.Deactivate
import org.apache.felix.scr.annotations.Reference
import org.apache.felix.scr.annotations.sling.SlingServlet
import org.apache.jackrabbit.api.security.user.UserManager
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.SlingHttpServletResponse
import org.apache.sling.api.resource.ResourceResolver
import org.apache.sling.api.resource.ResourceResolverFactory
import org.apache.sling.api.servlets.SlingAllMethodsServlet

import javax.servlet.ServletException

import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN

@SlingServlet(paths = "/bin/groovyconsole/post")
class ScriptPostServlet extends SlingAllMethodsServlet {

    @Reference
    protected ConfigurationService configurationService

    @Reference
    protected GroovyConsoleService consoleService

    @Reference
    protected ResourceResolverFactory resourceResolverFactory

    private ResourceResolver resourceResolver

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

    boolean hasPermission(SlingHttpServletRequest request) {
        def user = resourceResolver.adaptTo(UserManager).getAuthorizable(request.userPrincipal)

        def memberOfGroupIds = user.memberOf()*.getID()
        def allowedGroupIds = configurationService.allowedGroups

        allowedGroupIds ? memberOfGroupIds.intersect(allowedGroupIds) : true
    }

    @Activate
    @SuppressWarnings("deprecated")
    void activate() {
        resourceResolver = resourceResolverFactory.getAdministrativeResourceResolver(null)
    }

    @Deactivate
    void deactivate() {
        resourceResolver?.close()
    }
}
