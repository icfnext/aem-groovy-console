package com.icfolson.aem.groovy.console.api.impl

import com.icfolson.aem.groovy.console.api.ScriptContext
import groovy.transform.TupleConstructor
import org.apache.sling.api.resource.ResourceResolver

@TupleConstructor
class AsyncScriptContext implements ScriptContext {

    @Delegate
    ScriptContext scriptContext

    ResourceResolver resourceResolver

    @Override
    ResourceResolver getResourceResolver() {
        resourceResolver
    }

    @Override
    String getUserId() {
        resourceResolver.userID
    }
}
