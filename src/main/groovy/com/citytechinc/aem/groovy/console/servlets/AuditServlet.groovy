package com.citytechinc.aem.groovy.console.servlets

import com.citytechinc.aem.groovy.console.services.audit.AuditRecord
import com.citytechinc.aem.groovy.console.services.audit.AuditService
import groovy.util.logging.Slf4j
import org.apache.felix.scr.annotations.Reference
import org.apache.felix.scr.annotations.sling.SlingServlet
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.SlingHttpServletResponse

import javax.servlet.ServletException

@SlingServlet(paths = "/bin/groovyconsole/audit")
@Slf4j("LOG")
class AuditServlet extends AbstractJsonResponseServlet {

    private static final def PARAMETER_START_DATE = "startDate"

    private static final def PARAMETER_END_DATE = "endDate"

    private static final String DATE_FORMAT = "yyyy-MM-dd"

    private static final String DATE_FORMAT_DISPLAY = "yyyy-MM-dd HH:mm:ss z"

    @Reference
    AuditService auditService

    @Override
    protected void doGet(SlingHttpServletRequest request,
        SlingHttpServletResponse response) throws ServletException, IOException {
        def auditRecords = getAuditRecords(request)

        def data = []

        auditRecords.each { record ->
            def map = [
                date               : record.date.format(DATE_FORMAT_DISPLAY),
                script             : record.script,
                result             : record.result,
                output             : record.output,
                exceptionStackTrace: record.exceptionStackTrace
            ]

            data.add(map)
        }

        writeJsonResponse(response, [data: data])
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
