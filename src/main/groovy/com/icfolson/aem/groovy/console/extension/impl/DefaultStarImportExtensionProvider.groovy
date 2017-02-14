package com.icfolson.aem.groovy.console.extension.impl

import com.icfolson.aem.groovy.console.api.StarImportExtensionProvider
import com.icfolson.aem.groovy.console.constants.GroovyConsoleConstants
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.Service

@Service(StarImportExtensionProvider)
@Component(immediate = true)
class DefaultStarImportExtensionProvider implements StarImportExtensionProvider {

    @Override
    Set<String> getStarImports() {
        GroovyConsoleConstants.DEFAULT_STAR_IMPORTS
    }
}