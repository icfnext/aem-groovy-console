package com.icfolson.aem.groovy.console.components

import javax.inject.Inject

import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.SlingHttpServletResponse
import org.apache.sling.models.annotations.Model
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable
import org.apache.sling.models.annotations.injectorspecific.Self

import com.icfolson.aem.groovy.console.api.BindingVariable
import com.icfolson.aem.groovy.console.extension.ExtensionService

import groovy.transform.Memoized

@Model(adaptables = SlingHttpServletRequest)
class BindingsPanel {

    @Self
    private SlingHttpServletRequest request

    @ScriptVariable
    private SlingHttpServletResponse response

    @Inject
    private ExtensionService extensionService

    @Memoized
    Map<String, BindingVariable> getBindingVariables() {
        OutputStream stream = new ByteArrayOutputStream();
        def printStream = new PrintStream(stream, true, CharEncoding.UTF_8)
        extensionService.getBindingVariables(request, response, printStream)
    }
}
