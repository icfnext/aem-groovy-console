package com.icfolson.aem.groovy.console.components

import com.icfolson.aem.groovy.console.api.StarImport
import com.icfolson.aem.groovy.console.extension.ExtensionService
import org.apache.sling.api.resource.Resource
import org.apache.sling.models.annotations.Model

import javax.inject.Inject

@Model(adaptables = Resource)
class ImportsPanel {

    @Inject
    private ExtensionService extensionService

    Set<StarImport> getStarImports() {
        extensionService.starImports as TreeSet
    }
}
