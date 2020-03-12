package com.icfolson.aem.groovy.console.api

import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.SlingHttpServletResponse

/**
 * Script context for scripts executed by a servlet (e.g. the default POST servlet execution).
 */
interface ServletScriptContext extends ScriptContext {

    /**
     * Get the servlet request.
     *
     * @return request
     */
    SlingHttpServletRequest getRequest()

    /**
     * Get the servlet response.
     *
     * @return response
     */
    SlingHttpServletResponse getResponse()
}