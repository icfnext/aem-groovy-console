package com.icfolson.aem.groovy.console.servlets

import com.google.common.net.HttpHeaders
import com.google.common.net.MediaType
import com.icfolson.aem.groovy.console.audit.AuditService
import com.icfolson.aem.groovy.console.constants.GroovyConsoleConstants
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.SlingHttpServletResponse
import org.apache.sling.api.servlets.SlingSafeMethodsServlet
import org.osgi.service.component.annotations.Component
import org.osgi.service.component.annotations.Reference

import javax.servlet.Servlet

import static com.icfolson.aem.groovy.console.constants.GroovyConsoleConstants.DATE_FORMAT_FILE_NAME
import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST

@Component(service = Servlet, immediate = true, property = [
    "sling.servlet.paths=/bin/groovyconsole/download"
])
class ScriptDownloadServlet extends SlingSafeMethodsServlet {

    @Reference
    private AuditService auditService

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        def script = request.getParameter(GroovyConsoleConstants.SCRIPT)
        def userId = request.getParameter(GroovyConsoleConstants.USER_ID)

        def auditRecord = auditService.getAuditRecord(userId, script)

        if (auditRecord) {
            def result = request.getParameter(GroovyConsoleConstants.RESULT)

            def text = result ? auditRecord.result : auditRecord.output

            def fileName = new StringBuilder()

            fileName.append(result ? "result-" : "output-")
            fileName.append(auditRecord.date.format(DATE_FORMAT_FILE_NAME))
            fileName.append(".txt")

            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=${fileName.toString()}")

            response.contentType = MediaType.PLAIN_TEXT_UTF_8.withoutParameters().toString()
            response.characterEncoding = GroovyConsoleConstants.CHARSET
            response.contentLength = text.length()
            response.outputStream.write(text.getBytes(GroovyConsoleConstants.CHARSET))
        } else {
            response.status = SC_BAD_REQUEST
        }
    }
}
