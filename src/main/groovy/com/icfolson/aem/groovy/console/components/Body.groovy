package com.icfolson.aem.groovy.console.components

import com.icfolson.aem.groovy.console.audit.AuditRecord
import com.icfolson.aem.groovy.console.audit.AuditService
import com.icfolson.aem.groovy.console.configuration.ConfigurationService
import groovy.json.JsonBuilder
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.models.annotations.Model
import org.apache.sling.models.annotations.injectorspecific.OSGiService

import javax.annotation.PostConstruct
import javax.inject.Inject

import static com.icfolson.aem.groovy.console.constants.GroovyConsoleConstants.SCRIPT
import static com.icfolson.aem.groovy.console.constants.GroovyConsoleConstants.USER_ID

@Model(adaptables = SlingHttpServletRequest)
class Body {

    @OSGiService
    private AuditService auditService

    @Inject
    private SlingHttpServletRequest request

    @OSGiService
    private ConfigurationService configurationService

    private AuditRecord auditRecord

    @PostConstruct
    void init() {
        def userId = request.getParameter(USER_ID)
        def script = request.getParameter(SCRIPT)

        if (script) {
            auditRecord = auditService.getAuditRecord(userId, script)
        }
    }

    String getAuditRecordJson() {
        auditRecord ? new JsonBuilder(auditRecord).toString() : null
    }

    boolean isHasScheduledJobPermission() {
        configurationService.hasScheduledJobPermission(request)
    }
}
