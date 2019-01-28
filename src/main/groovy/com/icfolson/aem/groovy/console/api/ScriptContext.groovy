package com.icfolson.aem.groovy.console.api

import groovy.transform.TupleConstructor
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.SlingHttpServletResponse

/**
 * Context variables for Groovy script execution.
 */
@TupleConstructor
class ScriptContext {

    SlingHttpServletRequest request

    SlingHttpServletResponse response

    PrintStream printStream

    String scriptContent

    String data

    String getUserId() {
        request.resourceResolver.userID
    }
}
