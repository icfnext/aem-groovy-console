package com.icfolson.aem.groovy.console.api.impl

import com.google.common.base.Charsets
import com.icfolson.aem.groovy.console.api.ServletScriptContext
import groovy.transform.TupleConstructor
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.SlingHttpServletResponse
import org.apache.sling.api.resource.ResourceResolver

import static com.icfolson.aem.groovy.console.constants.GroovyConsoleConstants.PARAMETER_DATA

@TupleConstructor
class RequestScriptContext implements ServletScriptContext {

    SlingHttpServletRequest request

    SlingHttpServletResponse response

    ByteArrayOutputStream outputStream

    PrintStream printStream

    String script

    @Override
    ResourceResolver getResourceResolver() {
        request.resourceResolver
    }

    @Override
    String getUserId() {
        request.resourceResolver.userID
    }

    @Override
    String getData() {
        request.getRequestParameter(PARAMETER_DATA)?.getString(Charsets.UTF_8.name())
    }
}
