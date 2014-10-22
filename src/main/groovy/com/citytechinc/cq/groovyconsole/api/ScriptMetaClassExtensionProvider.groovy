package com.citytechinc.cq.groovyconsole.api

import org.apache.sling.api.SlingHttpServletRequest

/**
 * Services may implement this interface to supply additional metamethods to apply to the <code>Script</code> metaclass.
 */
interface ScriptMetaClassExtensionProvider {

    /**
     * Get a closure to register a metaclass for the script to be executed.
     *
     * @param request current request
     * @return a closure containing metamethods to register for scripts
     */
    Closure getScriptMetaClass(SlingHttpServletRequest request)
}
