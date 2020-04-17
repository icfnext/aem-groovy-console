package com.icfolson.aem.groovy.console.servlets

import com.google.common.net.HttpHeaders
import com.icfolson.aem.groovy.console.audit.AuditService
import com.icfolson.aem.groovy.console.constants.GroovyConsoleConstants
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.SlingHttpServletResponse
import org.apache.sling.api.servlets.SlingSafeMethodsServlet
import org.osgi.service.component.annotations.Component
import org.osgi.service.component.annotations.Reference

import javax.servlet.Servlet

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST

@Component(service = Servlet, immediate = true, property = [
    "sling.servlet.paths=/bin/groovyconsole/download"
])
class ScriptDownloadServlet extends SlingSafeMethodsServlet {

    @Reference
    private AuditService auditService

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        def userId = request.getParameter(GroovyConsoleConstants.USER_ID)
        def script = request.getParameter(GroovyConsoleConstants.SCRIPT)

        def auditRecord = auditService.getAuditRecord(userId, script)

        if (auditRecord) {
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=${auditRecord.outputFileName}")

            response.contentType = auditRecord.mediaType
            response.characterEncoding = GroovyConsoleConstants.CHARSET
            response.contentLength = auditRecord.output.length()
            response.outputStream.write(auditRecord.output.getBytes(GroovyConsoleConstants.CHARSET))
        } else {
            response.status = SC_BAD_REQUEST
        }
    }
}
