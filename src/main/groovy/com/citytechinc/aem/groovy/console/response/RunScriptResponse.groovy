package com.citytechinc.aem.groovy.console.response

import groovy.transform.Immutable

@Immutable
class RunScriptResponse {

    String executionResult

    String outputText

    String stackTraceText

    String runningTime
}
