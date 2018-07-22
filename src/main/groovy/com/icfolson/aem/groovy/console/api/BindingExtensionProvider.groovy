package com.icfolson.aem.groovy.console.api

import org.apache.commons.lang3.CharEncoding
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.resource.Resource

/**
 * Services may implement this interface to supply additional binding values for Groovy script executions.
 */
trait BindingExtensionProvider {

    /**
     * Get the binding for this request.  All bindings provided by extension services will be merged prior to script
     * execution.
     *
     * @param request current request
     * @return binding map for request
     */
    abstract Binding getBinding(SlingHttpServletRequest request)

    /**
     * Get the description html for this binding provider bindings
     *
     */
    String getBindingDescription(SlingHttpServletRequest request) {
        String filePath = this.bindingDescriptionFilePath
        String description = null

        if (filePath) {
            Resource fileResource = request.resourceResolver.getResource(filePath)
            description = fileResource?.adaptTo(InputStream)?.getText(CharEncoding.UTF_8)
        }

        description
    }

    String getBindingDescriptionFilePath() {
        null
    }
}