package com.icfolson.aem.groovy.console.api

import com.icfolson.aem.groovy.console.api.context.ScriptContext

/**
 * Services may implement this interface to supply additional binding values for Groovy script executions.
 */
interface BindingExtensionProvider {

    /**
     * Get the binding variables for this script execution.  All bindings provided by extension services will be merged
     * prior to script execution.
     *
     * @param scriptContext context for current script execution
     * @return map of binding variables for request
     */
    Map<String, BindingVariable> getBindingVariables(ScriptContext scriptContext)
}