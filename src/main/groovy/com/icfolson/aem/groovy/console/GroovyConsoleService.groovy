package com.icfolson.aem.groovy.console

import com.icfolson.aem.groovy.console.response.RunScriptResponse
import com.icfolson.aem.groovy.console.response.SaveScriptResponse
import org.apache.sling.api.SlingHttpServletRequest

interface GroovyConsoleService {

    RunScriptResponse runScript(SlingHttpServletRequest request)

    RunScriptResponse runScriptFile(SlingHttpServletRequest request)

    SaveScriptResponse saveScript(SlingHttpServletRequest request)
}
