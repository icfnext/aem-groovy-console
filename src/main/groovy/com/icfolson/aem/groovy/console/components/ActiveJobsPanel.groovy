package com.icfolson.aem.groovy.console.components

import com.icfolson.aem.groovy.console.GroovyConsoleService
import com.icfolson.aem.groovy.console.api.ActiveJob
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.models.annotations.Model
import org.apache.sling.models.annotations.injectorspecific.OSGiService

@Model(adaptables = SlingHttpServletRequest)
class ActiveJobsPanel {

    @OSGiService
    private GroovyConsoleService groovyConsoleService

    List<ActiveJob> getActiveJobs() {
        groovyConsoleService.activeJobs
    }
}
