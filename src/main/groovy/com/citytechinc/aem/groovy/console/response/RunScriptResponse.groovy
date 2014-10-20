package com.citytechinc.aem.groovy.console.response

import com.citytechinc.aem.groovy.console.audit.AuditRecord
import com.citytechinc.aem.groovy.console.table.Table
import groovy.json.JsonBuilder
import groovy.transform.Immutable
import org.apache.commons.lang3.exception.ExceptionUtils

import javax.jcr.Node

@Immutable
class RunScriptResponse {

    static RunScriptResponse fromResult(Object result, String output, String runningTime) {
        def resultString

        if (result instanceof Table) {
            resultString = new JsonBuilder([table: result]).toString()
        } else {
            resultString = result as String
        }

        new RunScriptResponse(resultString, output, "", runningTime)
    }

    static RunScriptResponse fromException(Throwable throwable) {
        def exceptionStackTrace = ExceptionUtils.getStackTrace(throwable)

        new RunScriptResponse("", "", exceptionStackTrace, "")
    }

    static RunScriptResponse fromAuditRecordNode(Node node) {
        def exceptionStackTrace = node.get(AuditRecord.PROPERTY_EXCEPTION_STACK_TRACE) ?: ""

        def response

        if (exceptionStackTrace) {
            response = new RunScriptResponse("", "", exceptionStackTrace, "")
        } else {
            def result = node.get(AuditRecord.PROPERTY_RESULT) ?: ""
            def output = node.get(AuditRecord.PROPERTY_OUTPUT) ?: ""
            def runningTime = node.get(AuditRecord.PROPERTY_RUNNING_TIME) ?: ""

            response = new RunScriptResponse(result, output, "", runningTime)
        }

        response
    }

    String result

    String output

    String exceptionStackTrace

    String runningTime
}
