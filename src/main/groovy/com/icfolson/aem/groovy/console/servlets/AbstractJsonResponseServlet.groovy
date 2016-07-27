package com.icfolson.aem.groovy.console.servlets

import groovy.json.JsonBuilder
import org.apache.sling.api.SlingHttpServletResponse
import org.apache.sling.api.servlets.SlingAllMethodsServlet

abstract class AbstractJsonResponseServlet extends SlingAllMethodsServlet {

    void writeJsonResponse(SlingHttpServletResponse response, json) {
        response.contentType = "application/json"
        response.characterEncoding = "UTF-8"

        new JsonBuilder(json).writeTo(response.writer)
    }
}
