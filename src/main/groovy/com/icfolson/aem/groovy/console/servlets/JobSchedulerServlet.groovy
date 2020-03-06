package com.icfolson.aem.groovy.console.servlets

import com.icfolson.aem.groovy.console.GroovyConsoleService
import com.icfolson.aem.groovy.console.api.JobProperties
import com.icfolson.aem.groovy.console.configuration.ConfigurationService
import groovy.util.logging.Slf4j
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.SlingHttpServletResponse
import org.osgi.service.component.annotations.Component
import org.osgi.service.component.annotations.Reference

import javax.servlet.Servlet
import javax.servlet.ServletException

import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN

@Component(service = Servlet, immediate = true, property = [
    "sling.servlet.paths=/bin/groovyconsole/jobs"
])
@Slf4j("LOG")
class JobSchedulerServlet extends AbstractJsonResponseServlet {

    @Reference
    private ConfigurationService configurationService

    @Reference
    private GroovyConsoleService consoleService

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws
        ServletException, IOException {
        if (configurationService.hasPermission(request)) {
            def jobProperties = JobProperties.fromRequest(request)

            LOG.debug("adding job with properties : {}", jobProperties.toMap())

            consoleService.addScheduledJob(jobProperties)

            writeJsonResponse(response, jobProperties)
        } else {
            response.status = SC_FORBIDDEN
        }
    }

    @Override
    protected void doDelete(SlingHttpServletRequest request, SlingHttpServletResponse response) throws
        ServletException, IOException {
        // TODO
    }
}