package com.icfolson.aem.groovy.console.servlets

import com.google.common.base.Charsets
import com.google.common.net.MediaType
import groovy.json.JsonBuilder
import org.apache.sling.api.SlingHttpServletResponse
import org.apache.sling.api.servlets.SlingAllMethodsServlet

abstract class AbstractJsonResponseServlet extends SlingAllMethodsServlet {

    void writeJsonResponse(SlingHttpServletResponse response, json) {
        response.contentType = MediaType.JSON_UTF_8.withoutParameters().toString()
        response.characterEncoding = Charsets.UTF_8.name()

        new JsonBuilder(json).writeTo(response.writer)
    }
}
