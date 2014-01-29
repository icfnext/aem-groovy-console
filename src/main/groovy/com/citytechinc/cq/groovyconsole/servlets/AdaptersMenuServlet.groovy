package com.citytechinc.cq.groovyconsole.servlets

import com.citytechinc.cq.groovyconsole.services.ConfigurationService
import groovy.json.JsonBuilder
import org.apache.felix.scr.annotations.Reference
import org.apache.felix.scr.annotations.sling.SlingServlet
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.SlingHttpServletResponse
import org.apache.sling.api.servlets.SlingSafeMethodsServlet

import javax.servlet.ServletException

@SlingServlet(paths = "/bin/groovyconsole/adapters")
class AdaptersMenuServlet extends SlingSafeMethodsServlet {

    @Reference
    ConfigurationService configurationService

    @Override
    protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse
        response) throws ServletException, IOException {
        response.contentType = "application/json"

        new JsonBuilder(configurationService.adapters).writeTo(response.writer)
    }
}
