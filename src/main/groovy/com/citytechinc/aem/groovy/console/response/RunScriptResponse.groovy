package com.citytechinc.aem.groovy.console.response

import com.citytechinc.aem.groovy.console.audit.AuditRecord
import com.citytechinc.aem.groovy.console.table.Table
import groovy.json.JsonBuilder
import groovy.transform.Immutable
import org.apache.commons.lang3.exception.ExceptionUtils

import javax.jcr.Node

import static com.citytechinc.aem.groovy.console.audit.AuditRecord.PROPERTY_EXCEPTION_STACK_TRACE
import static com.citytechinc.aem.groovy.console.audit.AuditRecord.PROPERTY_SCRIPT

@Immutable
class RunScriptResponse {

    static RunScriptResponse fromResult(String script, Object result, String output, String runningTime) {
        def resultString

        if (result instanceof Table) {
            resultString = new JsonBuilder([table: result]).toString()
        } else {
            resultString = result as String
        }

        new RunScriptResponse(script, resultString, output, "", runningTime)
    }

    static RunScriptResponse fromException(String script, Throwable throwable) {
        def exceptionStackTrace = ExceptionUtils.getStackTrace(throwable)

        new RunScriptResponse(script, "", "", exceptionStackTrace, "")
    }

    static RunScriptResponse fromAuditRecordNode(Node node) {
        def script = node.get(PROPERTY_SCRIPT) as String
        def exceptionStackTrace = node.get(PROPERTY_EXCEPTION_STACK_TRACE) ?: ""

        def response

        if (exceptionStackTrace) {
            response = new RunScriptResponse(script, "", "", exceptionStackTrace, "")
        } else {
            def result = node.get(AuditRecord.PROPERTY_RESULT) ?: ""
            def output = node.get(AuditRecord.PROPERTY_OUTPUT) ?: ""
            def runningTime = node.get(AuditRecord.PROPERTY_RUNNING_TIME) ?: ""

            response = new RunScriptResponse(script, result, output, "", runningTime)
        }

        response
    }

    String script

    String result

    String output

    String exceptionStackTrace

    String runningTime
}
