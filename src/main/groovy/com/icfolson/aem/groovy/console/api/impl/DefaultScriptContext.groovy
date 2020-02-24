package com.icfolson.aem.groovy.console.api.impl

import com.icfolson.aem.groovy.console.api.ScriptContext
import groovy.transform.TupleConstructor
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.SlingHttpServletResponse
import org.apache.sling.api.resource.ResourceResolver

@TupleConstructor
class DefaultScriptContext implements ScriptContext {

    SlingHttpServletRequest request

    SlingHttpServletResponse response

    ByteArrayOutputStream outputStream

    PrintStream printStream

    String scriptContent

    String data

    @Override
    ResourceResolver getResourceResolver() {
        request.resourceResolver
    }

    @Override
    String getUserId() {
        request.resourceResolver.userID
    }
}
