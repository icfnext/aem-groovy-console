package com.icfolson.aem.groovy.console.components

import com.icfolson.aem.groovy.console.audit.AuditService
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.models.annotations.Model
import org.apache.sling.models.annotations.injectorspecific.OSGiService

import javax.inject.Inject

@Model(adaptables = SlingHttpServletRequest)
class HistoryPanel {

    @OSGiService
    private AuditService auditService

    @Inject
    private SlingHttpServletRequest request

    Boolean isHasAuditRecords() {
        !auditService.getAllAuditRecords(request.resourceResolver.userID).empty
    }
}
