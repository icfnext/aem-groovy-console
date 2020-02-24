package com.icfolson.aem.groovy.console.api.impl

import com.icfolson.aem.groovy.console.api.ScriptContext
import groovy.transform.TupleConstructor
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.SlingHttpServletResponse
import org.apache.sling.api.resource.ResourceResolver

@TupleConstructor
class JobScriptContext implements ScriptContext {

    ResourceResolver resourceResolver

    ByteArrayOutputStream outputStream

    PrintStream printStream

    String scriptContent

    String data

    @Override
    SlingHttpServletRequest getRequest() {
        throw new UnsupportedOperationException()
    }

    @Override
    SlingHttpServletResponse getResponse() {
        throw new UnsupportedOperationException()
    }

    @Override
    String getUserId() {
        resourceResolver.userID
    }
}
