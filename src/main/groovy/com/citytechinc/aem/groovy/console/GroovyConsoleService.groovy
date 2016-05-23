package com.citytechinc.aem.groovy.console

import com.citytechinc.aem.groovy.console.response.RunScriptResponse
import com.citytechinc.aem.groovy.console.response.SaveScriptResponse
import org.apache.sling.api.SlingHttpServletRequest

interface GroovyConsoleService {

    RunScriptResponse runScript(SlingHttpServletRequest request)

    RunScriptResponse runScriptFile(SlingHttpServletRequest request)

    SaveScriptResponse saveScript(SlingHttpServletRequest request)
}
