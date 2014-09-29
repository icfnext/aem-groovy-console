package com.citytechinc.cq.groovyconsole.services

import com.citytechinc.cq.groovyconsole.api.BindingExtensionService
import com.citytechinc.cq.groovyconsole.api.StarImportExtensionService
import org.apache.sling.api.SlingHttpServletRequest

/**
 * Service that dynamically binds extensions providing additional script bindings, star imports, and script metaclasses.
 */
interface ExtensionService extends BindingExtensionService, StarImportExtensionService {

    /**
     * Get a list of all script metaclass closures for bound extensions.
     *
     * @param request current request
     * @return list of metaclass closures
     */
    List<Closure> getScriptMetaClasses(SlingHttpServletRequest request)
}