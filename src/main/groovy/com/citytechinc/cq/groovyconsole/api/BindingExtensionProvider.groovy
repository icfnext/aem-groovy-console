package com.citytechinc.cq.groovyconsole.api

import org.apache.sling.api.SlingHttpServletRequest

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
}