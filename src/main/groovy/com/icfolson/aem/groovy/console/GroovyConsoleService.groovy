package com.icfolson.aem.groovy.console

import com.icfolson.aem.groovy.console.api.JobProperties
import com.icfolson.aem.groovy.console.api.ScriptContext
import com.icfolson.aem.groovy.console.api.ScriptData
import com.icfolson.aem.groovy.console.response.RunScriptResponse
import com.icfolson.aem.groovy.console.response.SaveScriptResponse
import org.apache.sling.event.jobs.Job

/**
 * Service for executing and saving Groovy scripts.
 */
interface GroovyConsoleService {

    /**
     * Run a Groovy script with the given script context.
     *
     * @param scriptContext script context
     * @return response containing script output
     */
    RunScriptResponse runScript(ScriptContext scriptContext)

    /**
     * Save a Groovy script with the file name and content provided in the given script data.
     *
     * @param scriptData script data
     * @return response containing the name of the saved script
     */
    SaveScriptResponse saveScript(ScriptData scriptData)

    /**
     * Schedule a Groovy Console job execution.
     *
     * @param jobProperties map of job properties
     * @return job
     */
    Job addScheduledJob(JobProperties jobProperties)
}