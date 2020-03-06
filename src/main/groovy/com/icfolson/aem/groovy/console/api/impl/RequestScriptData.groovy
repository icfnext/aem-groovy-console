package com.icfolson.aem.groovy.console.api.impl

import com.icfolson.aem.groovy.console.api.ScriptData
import groovy.transform.TupleConstructor
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.resource.ResourceResolver

import static com.icfolson.aem.groovy.console.constants.GroovyConsoleConstants.EXTENSION_GROOVY
import static com.icfolson.aem.groovy.console.constants.GroovyConsoleConstants.PARAMETER_FILE_NAME
import static com.icfolson.aem.groovy.console.constants.GroovyConsoleConstants.PARAMETER_SCRIPT

@TupleConstructor
class RequestScriptData implements ScriptData {

    SlingHttpServletRequest request

    @Override
    ResourceResolver getResourceResolver() {
        request.resourceResolver
    }

    @Override
    String getFileName() {
        def name = request.getParameter(PARAMETER_FILE_NAME)

        name.endsWith(EXTENSION_GROOVY) ? name : "$name$EXTENSION_GROOVY"
    }

    @Override
    String getScript() {
        request.getParameter(PARAMETER_SCRIPT)
    }
}
