package com.icfolson.aem.groovy.console.components

import com.icfolson.aem.groovy.console.constants.GroovyConsoleConstants
import com.icfolson.aem.groovy.console.extension.ExtensionService
import org.apache.sling.api.resource.Resource
import org.apache.sling.models.annotations.Model

import javax.inject.Inject

@Model(adaptables = Resource)
class Imports {

    @Inject
    private ExtensionService extensionService

    Set<String> getStarImports() {
        (extensionService.starImports - GroovyConsoleConstants.DEFAULT_STAR_IMPORTS) as TreeSet
    }
}
