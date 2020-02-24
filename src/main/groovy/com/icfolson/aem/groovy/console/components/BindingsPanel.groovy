package com.icfolson.aem.groovy.console.components

import com.icfolson.aem.groovy.console.api.BindingVariable
import com.icfolson.aem.groovy.console.api.impl.DefaultScriptContext
import com.icfolson.aem.groovy.console.extension.ExtensionService
import groovy.transform.Memoized
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.SlingHttpServletResponse
import org.apache.sling.models.annotations.Model
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable
import org.apache.sling.models.annotations.injectorspecific.Self

import javax.inject.Inject

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
        def scriptContext = new DefaultScriptContext(
            request: request,
            response: response
        )

        extensionService.getBindingVariables(scriptContext)
    }
}
