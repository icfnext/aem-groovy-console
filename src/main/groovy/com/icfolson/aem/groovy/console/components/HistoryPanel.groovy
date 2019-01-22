package com.icfolson.aem.groovy.console.components

import com.icfolson.aem.groovy.console.audit.AuditService
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.models.annotations.Model

import javax.inject.Inject
import javax.jcr.Session

@Model(adaptables = SlingHttpServletRequest)
class HistoryPanel {

    @Inject
    private AuditService auditService

    @Inject
    private SlingHttpServletRequest request

    Boolean isHasAuditRecords() {
        def session = request.resourceResolver.adaptTo(Session)

        !auditService.getAllAuditRecords(session).empty
    }
}
