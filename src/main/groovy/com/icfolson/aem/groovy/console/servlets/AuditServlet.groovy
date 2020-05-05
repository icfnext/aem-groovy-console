package com.icfolson.aem.groovy.console.servlets

import com.icfolson.aem.groovy.console.audit.AuditRecord
import com.icfolson.aem.groovy.console.audit.AuditService
import com.icfolson.aem.groovy.console.configuration.ConfigurationService
import com.icfolson.aem.groovy.console.constants.GroovyConsoleConstants
import com.icfolson.aem.groovy.console.utils.GroovyScriptUtils
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.SlingHttpServletResponse
import org.osgi.service.component.annotations.Component
import org.osgi.service.component.annotations.Reference

import javax.servlet.Servlet

@Component(service = Servlet, immediate = true, property = [
    "sling.servlet.paths=/bin/groovyconsole/audit"
])
class AuditServlet extends AbstractJsonResponseServlet {

    private static final String DATE_FORMAT = "yyyy-MM-dd"

    @Reference
    private AuditService auditService

    @Reference
    private ConfigurationService configurationService

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        def script = request.getParameter(GroovyConsoleConstants.SCRIPT)
        def userId = request.getParameter(GroovyConsoleConstants.USER_ID)

        if (script) {
            writeJsonResponse(response, auditService.getAuditRecord(userId, script) ?: [:])
        } else {
            writeJsonResponse(response, getAuditRecordsData(request))
        }
    }

    @Override
    protected void doDelete(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        def script = request.getParameter(GroovyConsoleConstants.SCRIPT)
        def userId = request.getParameter(GroovyConsoleConstants.USER_ID)

        if (script) {
            auditService.deleteAuditRecord(userId, script)
        } else {
            auditService.deleteAllAuditRecords(request.resourceResolver.userID)
        }
    }

    private Map<String, Object> getAuditRecordsData(SlingHttpServletRequest request) {
        def auditRecords = getAuditRecords(request)
        def consoleHref = configurationService.consoleHref

        [data: auditRecords.collect { auditRecord ->
            [
                date: auditRecord.date.format(GroovyConsoleConstants.DATE_FORMAT_DISPLAY),
                scriptPreview: GroovyScriptUtils.getScriptPreview(auditRecord.script),
                jobTitle: auditRecord.jobProperties.jobTitle,
                userId: auditRecord.userId,
                script: auditRecord.script,
                data: auditRecord.data,
                exception: auditRecord.exception,
                link: "$consoleHref?userId=${auditRecord.userId}&script=${auditRecord.relativePath}",
                relativePath: auditRecord.relativePath,
                downloadUrl: auditRecord.downloadUrl
            ]
        }]
    }

    private List<AuditRecord> getAuditRecords(SlingHttpServletRequest request) {
        def startDateParameter = request.getParameter(GroovyConsoleConstants.START_DATE)
        def endDateParameter = request.getParameter(GroovyConsoleConstants.END_DATE)

        def auditRecords = [] as List<AuditRecord>

        if (!startDateParameter || !endDateParameter) {
            if (configurationService.hasScheduledJobPermission(request)) {
                auditRecords.addAll(auditService.allScheduledJobAuditRecords)
            }

            auditRecords.addAll(auditService.getAllAuditRecords(request.resourceResolver.userID))
        } else {
            def startDate = Date.parse(DATE_FORMAT, startDateParameter).toCalendar()
            def endDate = Date.parse(DATE_FORMAT, endDateParameter).toCalendar()

            if (configurationService.hasScheduledJobPermission(request)) {
                auditRecords.addAll(auditService.getScheduledJobAuditRecords(startDate, endDate))
            }

            auditRecords.addAll(auditService.getAuditRecords(request.resourceResolver.userID, startDate, endDate))
        }

        auditRecords.sort { a, b -> b.date.timeInMillis <=> a.date.timeInMillis }
    }
}
