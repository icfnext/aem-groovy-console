package com.icfolson.aem.groovy.console.api

import org.apache.sling.api.resource.ResourceResolver

/**
 * Context variables for Groovy script execution.
 */
interface ScriptContext {

    ResourceResolver getResourceResolver()

    ByteArrayOutputStream getOutputStream()

    PrintStream getPrintStream()

    String getScriptContent()

    String getData()

    String getUserId()
}
