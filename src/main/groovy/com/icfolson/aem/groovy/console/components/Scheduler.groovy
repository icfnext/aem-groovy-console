package com.icfolson.aem.groovy.console.components

import com.icfolson.aem.groovy.console.configuration.ConfigurationService
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.models.annotations.Model
import org.apache.sling.models.annotations.injectorspecific.OSGiService

@Model(adaptables = SlingHttpServletRequest)
class Scheduler {

    @OSGiService
    private ConfigurationService configurationService

    boolean isEmailEnabled() {
        configurationService.emailEnabled
    }
}
