package com.icfolson.aem.groovy.console

import com.icfolson.aem.groovy.console.response.RunScriptResponse
import com.icfolson.aem.groovy.console.response.SaveScriptResponse
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.SlingHttpServletResponse

interface GroovyConsoleService {

    RunScriptResponse runScript(SlingHttpServletRequest request, SlingHttpServletResponse response)

    RunScriptResponse runScript(SlingHttpServletRequest request, SlingHttpServletResponse response, String scriptPath)

    List<RunScriptResponse> runScripts(SlingHttpServletRequest request, SlingHttpServletResponse response,
        List<String> scriptPaths)

    SaveScriptResponse saveScript(SlingHttpServletRequest request)
}