package com.icfolson.aem.groovy.console.impl

import com.icfolson.aem.groovy.console.api.ScriptContext
import groovy.transform.TupleConstructor
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.SlingHttpServletResponse

@TupleConstructor
class DefaultScriptContext implements ScriptContext {

    SlingHttpServletRequest request

    SlingHttpServletResponse response

    PrintStream printStream

    String scriptContent

    String data
}
