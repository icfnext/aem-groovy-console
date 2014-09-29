package com.citytechinc.cq.groovyconsole.services.impl

import com.citytechinc.cq.groovyconsole.api.StarImportExtensionService
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.Service

@Service(StarImportExtensionService)
@Component(immediate = true)
class DefaultStarImportExtensionService implements StarImportExtensionService {

    private static final Set<String> DEFAULT_STAR_IMPORTS = ["javax.jcr",  "org.apache.sling.api",
        "org.apache.sling.api.resource", "com.day.cq.search", "com.day.cq.tagging", "com.day.cq.wcm.api"] as Set

    @Override
    Set<String> getStarImports() {
        DEFAULT_STAR_IMPORTS
    }
}