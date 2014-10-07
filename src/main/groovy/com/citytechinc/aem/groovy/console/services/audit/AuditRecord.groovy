package com.citytechinc.aem.groovy.console.services.audit

import com.day.cq.commons.jcr.JcrConstants
import groovy.transform.ToString

import javax.jcr.Node

@ToString(includePackage = false, includes = ["path"])
class AuditRecord {

    public static final String PROPERTY_SCRIPT = "script"

    public static final String PROPERTY_RESULT = "result"

    public static final String PROPERTY_OUTPUT = "output"

    public static final String PROPERTY_EXCEPTION_STACK_TRACE = "exceptionStackTrace"

    public static final String PROPERTY_RUNNING_TIME = "runningTime"

    final String path

    final Calendar date

    final String script

    final String output

    final String result

    final String exceptionStackTrace

    final String runningTime

    AuditRecord(Node node) {
        path = node.path
        date = node.get(JcrConstants.JCR_CREATED)
        script = node.get(PROPERTY_SCRIPT)
        output = node.get(PROPERTY_OUTPUT) ?: ""
        result = node.get(PROPERTY_RESULT) ?: ""
        exceptionStackTrace = node.get(PROPERTY_EXCEPTION_STACK_TRACE) ?: ""
        runningTime = node.get(PROPERTY_RUNNING_TIME) ?: ""
    }
}
