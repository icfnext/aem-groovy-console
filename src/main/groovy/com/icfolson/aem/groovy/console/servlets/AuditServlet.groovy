package com.icfolson.aem.groovy.console.servlets

import com.icfolson.aem.groovy.console.audit.AuditRecord
import com.icfolson.aem.groovy.console.audit.AuditService
import com.icfolson.aem.groovy.console.configuration.ConfigurationService
import org.apache.felix.scr.annotations.Reference
import org.apache.felix.scr.annotations.sling.SlingServlet
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.SlingHttpServletResponse

import javax.jcr.Session

import static com.icfolson.aem.groovy.console.constants.GroovyConsoleConstants.PARAMETER_SCRIPT

@SlingServlet(paths = "/bin/groovyconsole/audit")
class AuditServlet extends AbstractJsonResponseServlet {

    private static final String PARAMETER_START_DATE = "startDate"

    private static final String PARAMETER_END_DATE = "endDate"

    private static final String DATE_FORMAT = "yyyy-MM-dd"

    private static final String DATE_FORMAT_DISPLAY = "yyyy-MM-dd HH:mm:ss"

    @Reference
    private AuditService auditService

    @Reference
    private ConfigurationService configurationService

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        def session = request.resourceResolver.adaptTo(Session)
        def script = request.getParameter(PARAMETER_SCRIPT)

        if (script) {
            writeJsonResponse(response, auditService.getAuditRecord(session, script) ?: [:])
        } else {
            writeJsonResponse(response, loadAuditRecords(request))
        }
    }

    @Override
    protected void doDelete(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        def session = request.resourceResolver.adaptTo(Session)
        def script = request.getParameter(PARAMETER_SCRIPT)

        if (script) {
            auditService.deleteAuditRecord(session, script)
        } else {
            auditService.deleteAllAuditRecords(session)
        }
    }

    private def loadAuditRecords(SlingHttpServletRequest request) {
        def auditRecords = getAuditRecords(request)
        def consoleHref = configurationService.consoleHref

        def data = []

        auditRecords.each { auditRecord ->
            def lines = auditRecord.script.readLines()

            def map = [
                date: auditRecord.date.format(DATE_FORMAT_DISPLAY),
                scriptPreview: lines.first() + (lines.size() > 1 ? " [...]" : ""),
                script: auditRecord.script,
                exception: auditRecord.exception,
                link: "$consoleHref?script=${auditRecord.relativePath}",
                relativePath: auditRecord.relativePath
            ]

            data.add(map)
        }

        [data: data]
    }

    private List<AuditRecord> getAuditRecords(SlingHttpServletRequest request) {
        def session = request.resourceResolver.adaptTo(Session)

        def startDateParameter = request.getParameter(PARAMETER_START_DATE)
        def endDateParameter = request.getParameter(PARAMETER_END_DATE)

        def auditRecords

        if (!startDateParameter || !endDateParameter) {
            auditRecords = auditService.getAllAuditRecords(session)
        } else {
            def startDate = Date.parse(DATE_FORMAT, startDateParameter)
            def endDate = Date.parse(DATE_FORMAT, endDateParameter)

            auditRecords = auditService.getAuditRecords(session, startDate.toCalendar(), endDate.toCalendar())
        }

        auditRecords.sort { a, b -> b.date.timeInMillis <=> a.date.timeInMillis }
    }
}
