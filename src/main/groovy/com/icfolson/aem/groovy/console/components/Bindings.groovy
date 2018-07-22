package com.icfolson.aem.groovy.console.components

import com.icfolson.aem.groovy.console.extension.ExtensionService
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.resource.Resource
import org.apache.sling.models.annotations.Model


import javax.inject.Inject

@Model(adaptables = SlingHttpServletRequest)
class Bindings {

    @Inject
    private SlingHttpServletRequest request

    @Inject
    private ExtensionService extensionService

    String getDescription() {
      extensionService.getBindingDescription(request)
    }
}