package com.citytechinc.aem.groovy.console.servlets

import com.citytechinc.aem.groovy.console.services.ConfigurationService
import com.citytechinc.aem.groovy.console.services.GroovyConsoleService
import groovy.util.logging.Slf4j
import org.apache.felix.scr.annotations.Reference
import org.apache.felix.scr.annotations.sling.SlingServlet
import org.apache.jackrabbit.api.security.user.UserManager
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.SlingHttpServletResponse

import javax.servlet.ServletException

import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN

@SlingServlet(paths = "/bin/groovyconsole/post")
@Slf4j("LOG")
class ScriptPostServlet extends AbstractJsonResponseServlet {

    @Reference
    ConfigurationService configurationService

    @Reference
    GroovyConsoleService consoleService

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws
        ServletException, IOException {
        if (hasPermission(request)) {
            def result = consoleService.runScript(request)

            writeJsonResponse(response, result)
        } else {
            response.setStatus(SC_FORBIDDEN)
        }
    }

    boolean hasPermission(request) {
        def user = request.resourceResolver.adaptTo(UserManager).getAuthorizable(request.userPrincipal)

        def memberOfGroupIds = user.memberOf()*.getID()
        def allowedGroupIds = configurationService.allowedGroups

        LOG.debug "member of group IDs = {}, allowed group IDs = {}", memberOfGroupIds, allowedGroupIds

        allowedGroupIds ? memberOfGroupIds.intersect(allowedGroupIds) : true
    }
}
