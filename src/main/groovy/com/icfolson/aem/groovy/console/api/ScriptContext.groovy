package com.icfolson.aem.groovy.console.api

import org.apache.sling.api.resource.ResourceResolver

/**
 * Context variables for Groovy script execution.
 */
interface ScriptContext {

    /**
     * Resource resolver for script execution.
     *
     * @return resource resolver
     */
    ResourceResolver getResourceResolver()

    /**
     * Stream for capturing script output.
     *
     * @return output stream
     */
    ByteArrayOutputStream getOutputStream()

    /**
     * Print stream for use in script binding.
     *
     * @return print stream
     */
    PrintStream getPrintStream()

    /**
     * Groovy script content to be executed.
     *
     * @return script content
     */
    String getScript()

    /**
     * JSON or String data to be consumed by script.
     *
     * @return data
     */
    String getData()

    /**
     * User ID for current request or session.
     *
     * @return user ID
     */
    String getUserId()
}
