package com.citytechinc.aem.groovy.console.servlets

import com.citytechinc.aem.groovy.console.audit.AuditRecord
import com.citytechinc.aem.groovy.console.audit.AuditService
import com.citytechinc.aem.groovy.console.configuration.ConfigurationService
import org.apache.felix.scr.annotations.Reference
import org.apache.felix.scr.annotations.sling.SlingServlet
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.SlingHttpServletResponse

import javax.servlet.ServletException

import static com.citytechinc.aem.groovy.console.constants.GroovyConsoleConstants.PARAMETER_SCRIPT

@SlingServlet(paths = "/bin/groovyconsole/audit")
class AuditServlet extends AbstractJsonResponseServlet {

    private static final def PARAMETER_START_DATE = "startDate"

    private static final def PARAMETER_END_DATE = "endDate"

    private static final String DATE_FORMAT = "yyyy-MM-dd"

    private static final String DATE_FORMAT_DISPLAY = "yyyy-MM-dd HH:mm:ss"

    @Reference
    AuditService auditService

    @Reference
    ConfigurationService configurationService

    @Override
    protected void doGet(SlingHttpServletRequest request,
        SlingHttpServletResponse response) throws ServletException, IOException {
        def script = request.getParameter(PARAMETER_SCRIPT)

        if (script) {
            writeJsonResponse(response, auditService.getAuditRecord(script) ?: [:])
        } else {
            writeJsonResponse(response, loadAuditRecords(request))
        }
    }

    @Override
    protected void doPost(SlingHttpServletRequest request,
        SlingHttpServletResponse response) throws ServletException, IOException {
        LOG.info "parameters = {}", request.requestParameterMap
    }

    @Override
    protected void doDelete(SlingHttpServletRequest request,
        SlingHttpServletResponse response) throws ServletException, IOException {
        def script = request.getParameter(PARAMETER_SCRIPT)

        if (script) {
            auditService.deleteAuditRecord(script)
        } else {
            auditService.deleteAllAuditRecords()
        }
    }

    private def loadAuditRecords(SlingHttpServletRequest request) {
        def auditRecords = getAuditRecords(request)
        def consoleHref = configurationService.consoleHref

        def data = []

        auditRecords.each { auditRecord ->
            def lines = auditRecord.script.readLines()

            def map = [
                date         : auditRecord.date.format(DATE_FORMAT_DISPLAY),
                scriptPreview: lines.first() + (lines.size() > 1 ? " [...]" : ""),
                script       : auditRecord.script,
                exception    : getException(auditRecord),
                link         : "$consoleHref?script=${auditRecord.relativePath}",
                relativePath : auditRecord.relativePath
            ]

            data.add(map)
        }

        [data: data]
    }

    private List<AuditRecord> getAuditRecords(SlingHttpServletRequest request) {
        def startDateParameter = request.getParameter(PARAMETER_START_DATE)
        def endDateParameter = request.getParameter(PARAMETER_END_DATE)

        def auditRecords

        if (!startDateParameter || !endDateParameter) {
            auditRecords = auditService.allAuditRecords
        } else {
            def startDate = Date.parse(DATE_FORMAT, startDateParameter)
            def endDate = Date.parse(DATE_FORMAT, endDateParameter)

            auditRecords = auditService.getAuditRecords(startDate.toCalendar(), endDate.toCalendar())
        }

        auditRecords.sort { a, b -> b.date.timeInMillis <=> a.date.timeInMillis }
    }

    private static def getException(AuditRecord auditRecord) {
        def exceptionStackTrace = auditRecord.exceptionStackTrace

        def exception

        if (exceptionStackTrace) {
            def firstLine = exceptionStackTrace.readLines().first()

            if (firstLine.contains(":")) {
                exception = firstLine.substring(0, firstLine.indexOf(":"))
            } else {
                exception = firstLine
            }
        } else {
            exception = ""
        }

        exception
    }
}
