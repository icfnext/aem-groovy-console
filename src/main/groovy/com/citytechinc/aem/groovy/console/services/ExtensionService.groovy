package com.citytechinc.aem.groovy.console.services

import com.citytechinc.aem.groovy.console.api.BindingExtensionProvider
import com.citytechinc.aem.groovy.console.api.StarImportExtensionProvider
import org.apache.sling.api.SlingHttpServletRequest

/**
 * Service that dynamically binds extensions providing additional script bindings, star imports, and script metaclasses.
 */
interface ExtensionService extends BindingExtensionProvider, StarImportExtensionProvider {

    /**
     * Get a list of all script metaclass closures for bound extensions.
     *
     * @param request current request
     * @return list of metaclass closures
     */
    List<Closure> getScriptMetaClasses(SlingHttpServletRequest request)
}