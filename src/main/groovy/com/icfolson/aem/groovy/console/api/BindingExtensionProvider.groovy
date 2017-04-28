package com.icfolson.aem.groovy.console.api

import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.resource.ResourceResolver

/**
 * Services may implement this interface to supply additional binding values for Groovy script executions.
 */
interface BindingExtensionProvider {

    /**
     * Get the binding for this request.  All bindings provided by extension services will be merged prior to script
     * execution.
     *
     * @param request current request
     * @return binding map for request
     */
    Binding getBinding(SlingHttpServletRequest request)

    Binding getBinding(ResourceResolver resourceResolver)
}