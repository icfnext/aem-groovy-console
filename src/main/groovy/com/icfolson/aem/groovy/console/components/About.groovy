package com.icfolson.aem.groovy.console.components

import org.apache.sling.api.resource.Resource
import org.apache.sling.models.annotations.Model

@Model(adaptables = Resource)
class About {

    String getVersion() {
        GroovySystem.version
    }
}