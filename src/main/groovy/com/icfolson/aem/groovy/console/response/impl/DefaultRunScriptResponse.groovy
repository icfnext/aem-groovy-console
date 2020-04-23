package com.icfolson.aem.groovy.console.response.impl

import com.day.cq.commons.jcr.JcrConstants
import com.day.cq.commons.jcr.JcrUtil
import com.day.text.Text
import com.google.common.net.MediaType
import com.icfolson.aem.groovy.console.api.JobProperties
import com.icfolson.aem.groovy.console.api.context.JobScriptContext
import com.icfolson.aem.groovy.console.api.context.ScriptContext
import com.icfolson.aem.groovy.console.response.RunScriptResponse
import com.icfolson.aem.groovy.console.table.Table
import groovy.json.JsonBuilder
import groovy.transform.TupleConstructor
import org.apache.commons.lang3.exception.ExceptionUtils
import org.apache.sling.api.resource.Resource
import org.apache.sling.api.resource.ResourceUtil

import static com.icfolson.aem.groovy.console.constants.GroovyConsoleConstants.DATA
import static com.icfolson.aem.groovy.console.constants.GroovyConsoleConstants.DATE_FORMAT_FILE_NAME
import static com.icfolson.aem.groovy.console.constants.GroovyConsoleConstants.EXCEPTION_STACK_TRACE
import static com.icfolson.aem.groovy.console.constants.GroovyConsoleConstants.JOB_ID
import static com.icfolson.aem.groovy.console.constants.GroovyConsoleConstants.MEDIA_TYPE_EXTENSIONS
import static com.icfolson.aem.groovy.console.constants.GroovyConsoleConstants.OUTPUT
import static com.icfolson.aem.groovy.console.constants.GroovyConsoleConstants.RESULT
import static com.icfolson.aem.groovy.console.constants.GroovyConsoleConstants.RUNNING_TIME
import static com.icfolson.aem.groovy.console.constants.GroovyConsoleConstants.SCRIPT

@TupleConstructor
class DefaultRunScriptResponse implements RunScriptResponse {

    private static final int LEVEL_USERID = 4

    static RunScriptResponse fromResult(ScriptContext scriptContext, Object result, String output, String runningTime) {
        def resultString

        if (result instanceof Table) {
            resultString = new JsonBuilder([table: result]).toString()
        } else {
            resultString = result as String
        }

        new DefaultRunScriptResponse(
            date: Calendar.instance,
            script: scriptContext.script,
            data: scriptContext.data,
            result: resultString,
            output: output,
            exceptionStackTrace: "",
            runningTime: runningTime,
            userId: scriptContext.userId,
            jobId: scriptContext instanceof JobScriptContext ? scriptContext.jobId : null,
            jobProperties: scriptContext instanceof JobScriptContext ? scriptContext.jobProperties : null
        )
    }

    static RunScriptResponse fromException(ScriptContext scriptContext, String output, Throwable throwable) {
        def exceptionStackTrace = ExceptionUtils.getStackTrace(throwable)

        new DefaultRunScriptResponse(
            date: Calendar.instance,
            script: scriptContext.script,
            data: scriptContext.data,
            result: "",
            output: output,
            exceptionStackTrace: exceptionStackTrace,
            runningTime: "",
            userId: scriptContext.userId,
            jobId: scriptContext instanceof JobScriptContext ? scriptContext.jobId : null,
            jobProperties: scriptContext instanceof JobScriptContext ? scriptContext.jobProperties : null
        )
    }

    static RunScriptResponse fromAuditRecordResource(Resource resource) {
        def properties = resource.valueMap

        def exceptionStackTrace = properties.get(EXCEPTION_STACK_TRACE, "")
        def userIdResourcePath = ResourceUtil.getParent(resource.path, LEVEL_USERID)
        def userId = Text.getName(userIdResourcePath)

        new DefaultRunScriptResponse(
            date: properties.get(JcrConstants.JCR_CREATED, Calendar),
            script: properties.get(SCRIPT, ""),
            data: properties.get(DATA, ""),
            result: exceptionStackTrace ? "" : properties.get(RESULT, ""),
            output: properties.get(OUTPUT, ""),
            exceptionStackTrace: exceptionStackTrace ?: "",
            runningTime: exceptionStackTrace ? "" : properties.get(RUNNING_TIME, ""),
            userId: userId,
            jobId: properties.get(JOB_ID, String),
            jobProperties: JobProperties.fromValueMap(properties)
        )
    }

    Calendar date

    String script

    String data

    String result

    String output

    String exceptionStackTrace

    String runningTime

    String userId

    String jobId

    JobProperties jobProperties

    @Override
    String getMediaType() {
        def mediaType

        if (jobProperties?.mediaType) {
            mediaType = MediaType.parse(jobProperties.mediaType)
        } else {
            mediaType = MediaType.PLAIN_TEXT_UTF_8
        }

        mediaType.withoutParameters().toString()
    }

    @Override
    String getOutputFileName() {
        new StringBuilder()
            .append(outputFileNamePrefix)
            .append(date.format(DATE_FORMAT_FILE_NAME))
            .append(".")
            .append(MEDIA_TYPE_EXTENSIONS[mediaType])
            .toString()
    }

    private String getOutputFileNamePrefix() {
        def outputFileNamePrefix

        if (jobProperties?.jobTitle) {
            outputFileNamePrefix = JcrUtil.createValidName(jobProperties.jobTitle,
                JcrUtil.HYPHEN_LABEL_CHAR_MAPPING).toLowerCase() + "-"
        } else {
            outputFileNamePrefix = "output-"
        }

        outputFileNamePrefix
    }
}
