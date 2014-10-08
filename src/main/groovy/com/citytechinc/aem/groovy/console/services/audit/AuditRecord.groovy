package com.citytechinc.aem.groovy.console.services.audit

import com.citytechinc.aem.groovy.console.response.RunScriptResponse
import com.day.cq.commons.jcr.JcrConstants
import groovy.transform.ToString

import javax.jcr.Node

import static com.citytechinc.aem.groovy.console.constants.GroovyConsoleConstants.AUDIT_PATH

@ToString(includePackage = false, includes = ["path"])
class AuditRecord {

    public static final String PROPERTY_SCRIPT = "script"

    public static final String PROPERTY_RESULT = "result"

    public static final String PROPERTY_OUTPUT = "output"

    public static final String PROPERTY_EXCEPTION_STACK_TRACE = "exceptionStackTrace"

    public static final String PROPERTY_RUNNING_TIME = "runningTime"

    @Delegate
    final RunScriptResponse response

    final String path

    final Calendar date

    final String script

    AuditRecord(Node node) {
        path = node.path
        date = node.get(JcrConstants.JCR_CREATED)
        script = node.get(PROPERTY_SCRIPT)

        def result = node.get(PROPERTY_RESULT) ?: ""
        def output = node.get(PROPERTY_OUTPUT) ?: ""
        def exceptionStackTrace = node.get(PROPERTY_EXCEPTION_STACK_TRACE) ?: ""
        def runningTime = node.get(PROPERTY_RUNNING_TIME) ?: ""

        response = new RunScriptResponse(result, output, exceptionStackTrace, runningTime)
    }

    String getRelativePath() {
        (path - AUDIT_PATH).substring(1)
    }
}
