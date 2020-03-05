package com.icfolson.aem.groovy.console.response

import com.day.text.Text
import com.icfolson.aem.groovy.console.api.ScriptContext
import com.icfolson.aem.groovy.console.audit.AuditRecord
import com.icfolson.aem.groovy.console.table.Table
import groovy.json.JsonBuilder
import groovy.transform.Immutable
import org.apache.commons.lang3.exception.ExceptionUtils
import org.apache.sling.api.resource.Resource
import org.apache.sling.api.resource.ResourceUtil

import static com.icfolson.aem.groovy.console.audit.AuditRecord.PROPERTY_DATA
import static com.icfolson.aem.groovy.console.audit.AuditRecord.PROPERTY_EXCEPTION_STACK_TRACE
import static com.icfolson.aem.groovy.console.audit.AuditRecord.PROPERTY_SCRIPT

@Immutable
class RunScriptResponse {

    private static final int LEVEL_USERID = 4

    static RunScriptResponse fromAsync(ScriptContext scriptContext) {
        new RunScriptResponse(scriptContext.script, scriptContext.data, "", "", "", "", scriptContext.userId)
    }

    static RunScriptResponse fromResult(ScriptContext scriptContext, Object result, String output, String runningTime) {
        def resultString

        if (result instanceof Table) {
            resultString = new JsonBuilder([table: result]).toString()
        } else {
            resultString = result as String
        }

        new RunScriptResponse(scriptContext.script, scriptContext.data, resultString, output, "", runningTime,
            scriptContext.userId)
    }

    static RunScriptResponse fromException(ScriptContext scriptContext, String output, Throwable throwable) {
        def exceptionStackTrace = ExceptionUtils.getStackTrace(throwable)

        new RunScriptResponse(scriptContext.script, scriptContext.data, "", output, exceptionStackTrace, "",
            scriptContext.userId)
    }

    static RunScriptResponse fromAuditRecordResource(Resource resource) {
        def properties = resource.valueMap

        def script = properties.get(PROPERTY_SCRIPT, "")
        def data = properties.get(PROPERTY_DATA, "")
        def exceptionStackTrace = properties.get(PROPERTY_EXCEPTION_STACK_TRACE, "")
        def output = properties.get(AuditRecord.PROPERTY_OUTPUT, "")

        def userIdResourcePath = ResourceUtil.getParent(resource.path, LEVEL_USERID)
        def userId = Text.getName(userIdResourcePath)

        def response

        if (exceptionStackTrace) {
            response = new RunScriptResponse(script, data, "", output, exceptionStackTrace, "", userId)
        } else {
            def result = properties.get(AuditRecord.PROPERTY_RESULT, "")
            def runningTime = properties.get(AuditRecord.PROPERTY_RUNNING_TIME, "")

            response = new RunScriptResponse(script, data, result, output, "", runningTime, userId)
        }

        response
    }

    String script

    String data

    String result

    String output

    String exceptionStackTrace

    String runningTime

    String userId
}
