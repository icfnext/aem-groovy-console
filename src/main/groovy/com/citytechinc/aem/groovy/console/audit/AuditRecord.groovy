package com.citytechinc.aem.groovy.console.audit

import com.citytechinc.aem.groovy.console.response.RunScriptResponse
import com.day.text.Text
import groovy.transform.ToString

import javax.jcr.Node

import static com.day.cq.commons.jcr.JcrConstants.JCR_CREATED

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

    AuditRecord(Node node) {
        path = node.path
        date = node.getProperty(JCR_CREATED).date
        response = RunScriptResponse.fromAuditRecordNode(node)
    }

    String getRelativePath() {
        (path - Text.getAbsoluteParent(path, 4)).substring(1)
    }

    String getException() {
        def exception = ""

        if (exceptionStackTrace) {
            def firstLine = exceptionStackTrace.readLines().first()

            if (firstLine.contains(":")) {
                exception = firstLine.substring(0, firstLine.indexOf(":"))
            } else {
                exception = firstLine
            }
        }

        exception
    }
}
