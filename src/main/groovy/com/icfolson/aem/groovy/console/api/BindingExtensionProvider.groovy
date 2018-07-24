package com.icfolson.aem.groovy.console.api

import org.apache.sling.api.SlingHttpServletRequest

/**
 * Services may implement this interface to supply additional binding values for Groovy script executions.
 */
interface BindingExtensionProvider {

    /**
     * Get the binding variables for this request.  All bindings provided by extension services will be merged prior to
     * script execution.
     *
     * @param request current request
     * @return map of binding variables for request
     */
    Map<String, BindingVariable> getBindingVariables(SlingHttpServletRequest request)
}