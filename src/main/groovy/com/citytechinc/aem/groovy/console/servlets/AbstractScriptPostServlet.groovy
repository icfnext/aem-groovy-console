package com.citytechinc.aem.groovy.console.servlets

import com.citytechinc.aem.groovy.console.configuration.ConfigurationService
import com.citytechinc.aem.groovy.console.response.RunScriptResponse
import groovy.util.logging.Slf4j
import org.apache.felix.scr.annotations.Activate
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.Deactivate
import org.apache.felix.scr.annotations.Reference
import org.apache.jackrabbit.api.security.user.UserManager
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.SlingHttpServletResponse
import org.apache.sling.api.resource.ResourceResolver
import org.apache.sling.api.resource.ResourceResolverFactory

import javax.servlet.ServletException

import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN

@Component(componentAbstract = true)
@Slf4j("LOG")
abstract class AbstractScriptPostServlet extends AbstractJsonResponseServlet {

    @Reference
    ConfigurationService configurationService

    @Reference
    ResourceResolverFactory resourceResolverFactory

    private ResourceResolver resourceResolver

    protected abstract RunScriptResponse runScript(SlingHttpServletRequest request)

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws
        ServletException, IOException {
        if (hasPermission(request)) {
            writeJsonResponse(response, runScript(request))
        } else {
            response.status = SC_FORBIDDEN
        }
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

    private boolean hasPermission(SlingHttpServletRequest request) {
        def user = resourceResolver.adaptTo(UserManager).getAuthorizable(request.userPrincipal)

        def memberOfGroupIds = user.memberOf()*.ID
        def allowedGroupIds = configurationService.allowedGroups

        LOG.debug("member of group IDs = {}, allowed group IDs = {}", memberOfGroupIds, allowedGroupIds)

        allowedGroupIds ? memberOfGroupIds.intersect(allowedGroupIds) : true
    }
}
