package com.citytechinc.cq.groovyconsole.servlets

import groovy.json.JsonBuilder
import org.apache.felix.scr.annotations.sling.SlingServlet
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.SlingHttpServletResponse
import org.apache.sling.api.servlets.SlingSafeMethodsServlet

import javax.servlet.ServletException

@SlingServlet(paths = "/bin/groovyconsole/adapters/list")
class AdaptersListServlet extends SlingSafeMethodsServlet {

    @Override
    protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse
    response) throws ServletException, IOException {

        response.contentType = "application/json"

        new JsonBuilder([]).writeTo(response.writer)
    }
}
