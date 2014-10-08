package com.citytechinc.aem.groovy.console.response

import groovy.transform.Immutable

@Immutable
class RunScriptResponse {

    String result

    String output

    String exceptionStackTrace

    String runningTime
}
