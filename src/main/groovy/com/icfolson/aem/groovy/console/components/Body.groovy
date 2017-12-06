package com.icfolson.aem.groovy.console.components

import com.icfolson.aem.groovy.console.audit.AuditRecord
import com.icfolson.aem.groovy.console.audit.AuditService
import groovy.json.JsonBuilder
import groovy.util.logging.Slf4j
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.models.annotations.Model

import javax.annotation.PostConstruct
import javax.inject.Inject
import javax.jcr.RepositoryException
import javax.jcr.Session

import static com.icfolson.aem.groovy.console.constants.GroovyConsoleConstants.PARAMETER_SCRIPT
import static com.icfolson.aem.groovy.console.constants.GroovyConsoleConstants.PARAMETER_USER_ID

@Model(adaptables = SlingHttpServletRequest)
@Slf4j("LOG")
class Body {

    @Inject
    private AuditService auditService

    @Inject
    private SlingHttpServletRequest request

    private AuditRecord auditRecord

    @PostConstruct
    void init() {
        def userId = request.getParameter(PARAMETER_USER_ID)
        def script = request.getParameter(PARAMETER_SCRIPT)

        if (script) {
            def session = request.resourceResolver.adaptTo(Session)

            try {
                auditRecord = auditService.getAuditRecord(session, userId, script)
            } catch (RepositoryException e) {
                LOG.error("audit record not found for user ID = {} and script = {}", userId, script)
            }
        }
    }

    String getAuditRecordJson() {
        auditRecord ? new JsonBuilder(auditRecord).toString() : null
    }
}
