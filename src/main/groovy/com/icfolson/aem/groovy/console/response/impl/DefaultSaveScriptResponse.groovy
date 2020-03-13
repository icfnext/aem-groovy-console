package com.icfolson.aem.groovy.console.response.impl

import com.icfolson.aem.groovy.console.response.SaveScriptResponse
import groovy.transform.Immutable

@Immutable
class DefaultSaveScriptResponse implements SaveScriptResponse {

    String scriptName
}
