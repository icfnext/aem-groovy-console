package com.citytechinc.cq.groovyconsole.api

import org.apache.sling.api.SlingHttpServletRequest

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