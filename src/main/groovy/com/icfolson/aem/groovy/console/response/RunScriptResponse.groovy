package com.icfolson.aem.groovy.console.response

import com.icfolson.aem.groovy.console.audit.AuditRecord
import com.icfolson.aem.groovy.console.table.Table
import groovy.json.JsonBuilder
import groovy.transform.Immutable
import org.apache.commons.lang3.exception.ExceptionUtils
import org.apache.sling.api.resource.Resource

import static com.icfolson.aem.groovy.console.audit.AuditRecord.PROPERTY_DATA
import static com.icfolson.aem.groovy.console.audit.AuditRecord.PROPERTY_EXCEPTION_STACK_TRACE
import static com.icfolson.aem.groovy.console.audit.AuditRecord.PROPERTY_SCRIPT

@Immutable
class RunScriptResponse {

    static RunScriptResponse fromResult(String script, String data, Object result, String output, String runningTime) {
        def resultString

        if (result instanceof Table) {
            resultString = new JsonBuilder([table: result]).toString()
        } else {
            resultString = result as String
        }

        new RunScriptResponse(script, data, resultString, output, "", runningTime)
    }

    static RunScriptResponse fromException(String script, Throwable throwable) {
        def exceptionStackTrace = ExceptionUtils.getStackTrace(throwable)

        new RunScriptResponse(script, "", "", "", exceptionStackTrace, "")
    }

    static RunScriptResponse fromAuditRecordResource(Resource resource) {
        def properties = resource.valueMap

        def script = properties.get(PROPERTY_SCRIPT, "")
        def data = properties.get(PROPERTY_DATA, "")
        def exceptionStackTrace = properties.get(PROPERTY_EXCEPTION_STACK_TRACE, "")

        def response

        if (exceptionStackTrace) {
            response = new RunScriptResponse(script, data, "", "", exceptionStackTrace, "")
        } else {
            def result = properties.get(AuditRecord.PROPERTY_RESULT, "")
            def output = properties.get(AuditRecord.PROPERTY_OUTPUT, "")
            def runningTime = properties.get(AuditRecord.PROPERTY_RUNNING_TIME, "")

            response = new RunScriptResponse(script, data, result, output, "", runningTime)
        }

        response
    }

    String script

    String data

    String result

    String output

    String exceptionStackTrace

    String runningTime
}
