package com.icfolson.aem.groovy.console.api

import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.SlingHttpServletResponse

/**
 * Context variables for Groovy script execution.
 */
interface ScriptContext {

    SlingHttpServletRequest getRequest()

    SlingHttpServletResponse getResponse()

    PrintStream getPrintStream()

    String getScriptContent()

    String getData()
}
