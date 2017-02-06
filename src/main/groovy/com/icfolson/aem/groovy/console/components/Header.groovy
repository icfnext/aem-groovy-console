package com.icfolson.aem.groovy.console.components

import com.icfolson.aem.groovy.console.configuration.ConfigurationService
import org.apache.sling.api.resource.Resource
import org.apache.sling.models.annotations.Model

import javax.inject.Inject

@Model(adaptables = Resource)
class Header {

    @Inject
    private ConfigurationService configurationService

    String getHref() {
        configurationService.consoleHref
    }
}
