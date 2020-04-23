package com.icfolson.aem.groovy.console.components

import com.icfolson.aem.groovy.console.api.BindingVariable
import com.icfolson.aem.groovy.console.api.impl.RequestScriptContext
import com.icfolson.aem.groovy.console.extension.ExtensionService
import groovy.transform.Memoized
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.SlingHttpServletResponse
import org.apache.sling.models.annotations.Model
import org.apache.sling.models.annotations.injectorspecific.OSGiService
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable
import org.apache.sling.models.annotations.injectorspecific.Self

@Model(adaptables = SlingHttpServletRequest)
class BindingsPanel {

    @Self
    private SlingHttpServletRequest request

    @ScriptVariable
    private SlingHttpServletResponse response

    @OSGiService
    private ExtensionService extensionService

    @Memoized
    Map<String, BindingVariable> getBindingVariables() {
        def scriptContext = new RequestScriptContext(
            request: request,
            response: response
        )

        extensionService.getBindingVariables(scriptContext)
    }
}
