package com.citytechinc.aem.groovy.console.servlets

import com.citytechinc.aem.groovy.console.services.ConfigurationService
import com.citytechinc.aem.groovy.console.services.audit.AuditRecord
import com.citytechinc.aem.groovy.console.services.audit.AuditService
import groovy.util.logging.Slf4j
import org.apache.felix.scr.annotations.Reference
import org.apache.felix.scr.annotations.sling.SlingServlet
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.SlingHttpServletResponse

import javax.servlet.ServletException

import static com.citytechinc.aem.groovy.console.constants.GroovyConsoleConstants.PARAMETER_SCRIPT

@SlingServlet(paths = "/bin/groovyconsole/audit")
@Slf4j("LOG")
class AuditServlet extends AbstractJsonResponseServlet {

    private static final def PARAMETER_START_DATE = "startDate"

    private static final def PARAMETER_END_DATE = "endDate"

    private static final String DATE_FORMAT = "yyyy-MM-dd"

    private static final String DATE_FORMAT_DISPLAY = "yyyy-MM-dd HH:mm:ss z"

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
            def auditRecords = getAuditRecords(request)
            def consoleHref = configurationService.consoleHref

            def data = []

            auditRecords.each { record ->
                def lines = record.script.readLines()

                def map = [
                    date   : record.date.format(DATE_FORMAT_DISPLAY),
                    script : lines.first() + (lines.size() > 1 ? "[...]" : ""),
                    success: !record.exceptionStackTrace,
                    link   : "$consoleHref?script=${record.relativePath}",
                    relativePath: record.relativePath
                ]

                data.add(map)
            }

            writeJsonResponse(response, [data: data])
        }
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
}
