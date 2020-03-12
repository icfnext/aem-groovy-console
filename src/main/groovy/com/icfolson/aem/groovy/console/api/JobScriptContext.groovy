package com.icfolson.aem.groovy.console.api

/**
 * Script context for scheduled jobs.
 */
interface JobScriptContext extends ScriptContext {

    /**
     *
     * @return
     */
    String getJobId()

    /**
     *
     * @return
     */
    String getMediaType()
}