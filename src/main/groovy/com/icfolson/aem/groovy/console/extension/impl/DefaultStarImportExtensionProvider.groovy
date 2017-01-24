package com.icfolson.aem.groovy.console.extension.impl

import com.icfolson.aem.groovy.console.api.StarImportExtensionProvider
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.Service

@Service(StarImportExtensionProvider)
@Component(immediate = true)
class DefaultStarImportExtensionProvider implements StarImportExtensionProvider {

    private static final Set<String> DEFAULT_STAR_IMPORTS = [
        "javax.jcr",
        "org.apache.sling.api",
        "org.apache.sling.api.resource",
        "com.day.cq.search",
        "com.day.cq.tagging",
        "com.day.cq.wcm.api",
        "com.day.cq.replication",
        "groovy.json"
    ] as Set

    @Override
    Set<String> getStarImports() {
        DEFAULT_STAR_IMPORTS
    }
}