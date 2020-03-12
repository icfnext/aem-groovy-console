package com.icfolson.aem.groovy.console.api
/**
 * Services may implement this interface to supply additional metamethods to apply to the <code>Script</code> metaclass.
 */
interface ScriptMetaClassExtensionProvider {

    /**
     * Get a closure to register a metaclass for the script to be executed.
     *
     * @param scriptContext current script execution context
     * @return a closure containing metamethods to register for scripts
     */
    Closure getScriptMetaClass(ScriptContext scriptContext)
}
