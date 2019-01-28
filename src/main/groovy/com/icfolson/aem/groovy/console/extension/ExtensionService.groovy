package com.icfolson.aem.groovy.console.extension

import com.icfolson.aem.groovy.console.api.BindingExtensionProvider
import com.icfolson.aem.groovy.console.api.ScriptContext
import com.icfolson.aem.groovy.console.api.StarImportExtensionProvider

/**
 * Service that dynamically binds extensions providing additional script bindings, star imports, and script metaclasses.
 */
interface ExtensionService extends BindingExtensionProvider, StarImportExtensionProvider {

    /**
     * Get a list of all script metaclass closures for bound extensions.
     *
     * @param scriptContext current script execution context
     * @return list of metaclass closures
     */
    List<Closure> getScriptMetaClasses(ScriptContext scriptContext)
}