package com.icfolson.aem.groovy.console.response

import com.day.text.Text
import com.icfolson.aem.groovy.console.api.JobScriptContext
import com.icfolson.aem.groovy.console.api.ScriptContext
import com.icfolson.aem.groovy.console.table.Table
import groovy.json.JsonBuilder
import groovy.transform.Immutable
import org.apache.commons.lang3.exception.ExceptionUtils
import org.apache.sling.api.resource.Resource
import org.apache.sling.api.resource.ResourceUtil

import static com.icfolson.aem.groovy.console.constants.GroovyConsoleConstants.DATA
import static com.icfolson.aem.groovy.console.constants.GroovyConsoleConstants.EXCEPTION_STACK_TRACE
import static com.icfolson.aem.groovy.console.constants.GroovyConsoleConstants.JOB_ID
import static com.icfolson.aem.groovy.console.constants.GroovyConsoleConstants.MEDIA_TYPE
import static com.icfolson.aem.groovy.console.constants.GroovyConsoleConstants.OUTPUT
import static com.icfolson.aem.groovy.console.constants.GroovyConsoleConstants.RESULT
import static com.icfolson.aem.groovy.console.constants.GroovyConsoleConstants.RUNNING_TIME
import static com.icfolson.aem.groovy.console.constants.GroovyConsoleConstants.SCRIPT

@Immutable
class RunScriptResponse {

    private static final int LEVEL_USERID = 4

    static RunScriptResponse fromResult(ScriptContext scriptContext, Object result, String output, String runningTime) {
        def resultString

        if (result instanceof Table) {
            resultString = new JsonBuilder([table: result]).toString()
        } else {
            resultString = result as String
        }

        new RunScriptResponse(
            script: scriptContext.script,
            data: scriptContext.data,
            result: resultString,
            output: output,
            exceptionStackTrace: "",
            runningTime: runningTime,
            userId: scriptContext.userId,
            jobId: scriptContext instanceof JobScriptContext ? scriptContext.jobId : null,
            mediaType: scriptContext instanceof JobScriptContext ? scriptContext.mediaType : null
        )
    }

    static RunScriptResponse fromException(ScriptContext scriptContext, String output, Throwable throwable) {
        def exceptionStackTrace = ExceptionUtils.getStackTrace(throwable)

        new RunScriptResponse(
            script: scriptContext.script,
            data: scriptContext.data,
            result: "",
            output: output,
            exceptionStackTrace: exceptionStackTrace,
            runningTime: "",
            userId: scriptContext.userId,
            jobId: scriptContext instanceof JobScriptContext ? scriptContext.jobId : null,
            mediaType: scriptContext instanceof JobScriptContext ? scriptContext.mediaType : null
        )
    }

    static RunScriptResponse fromAuditRecordResource(Resource resource) {
        def properties = resource.valueMap

        def exceptionStackTrace = properties.get(EXCEPTION_STACK_TRACE, "")
        def userIdResourcePath = ResourceUtil.getParent(resource.path, LEVEL_USERID)
        def userId = Text.getName(userIdResourcePath)

        new RunScriptResponse(
            script: properties.get(SCRIPT, ""),
            data: properties.get(DATA, ""),
            result: exceptionStackTrace ? "" : properties.get(RESULT, ""),
            output: properties.get(OUTPUT, ""),
            exceptionStackTrace: exceptionStackTrace ?: "",
            runningTime: exceptionStackTrace ? "" : properties.get(RUNNING_TIME, ""),
            userId: userId,
            jobId: properties.get(JOB_ID, String),
            mediaType: properties.get(MEDIA_TYPE, String)
        )
    }

    String script

    String data

    String result

    String output

    String exceptionStackTrace

    String runningTime

    String userId

    String jobId

    String mediaType
}
