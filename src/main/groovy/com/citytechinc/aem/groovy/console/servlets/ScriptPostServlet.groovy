package com.citytechinc.aem.groovy.console.servlets

import com.citytechinc.aem.groovy.console.configuration.ConfigurationService
import com.citytechinc.aem.groovy.console.GroovyConsoleService
import groovy.util.logging.Slf4j
import org.apache.felix.scr.annotations.Activate
import org.apache.felix.scr.annotations.Deactivate
import org.apache.felix.scr.annotations.Reference
import org.apache.felix.scr.annotations.sling.SlingServlet
import org.apache.jackrabbit.api.security.user.UserManager
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.SlingHttpServletResponse
import org.apache.sling.api.resource.ResourceResolver
import org.apache.sling.api.resource.ResourceResolverFactory

import javax.servlet.ServletException

import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN

@SlingServlet(paths = "/bin/groovyconsole/post")
@Slf4j("LOG")
class ScriptPostServlet extends AbstractJsonResponseServlet {

    @Reference
    ConfigurationService configurationService

    @Reference
    GroovyConsoleService consoleService

    @Reference
    ResourceResolverFactory resourceResolverFactory

    private ResourceResolver resourceResolver

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws
        ServletException, IOException {
        if (hasPermission(request)) {
            writeJsonResponse(response, consoleService.runScript(request))
        } else {
            response.setStatus(SC_FORBIDDEN)
        }
    }

    boolean hasPermission(SlingHttpServletRequest request) {
        def user = resourceResolver.adaptTo(UserManager).getAuthorizable(request.userPrincipal)

        def memberOfGroupIds = user.memberOf()*.getID()
        def allowedGroupIds = configurationService.allowedGroups

        LOG.debug "member of group IDs = {}, allowed group IDs = {}", memberOfGroupIds, allowedGroupIds

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
