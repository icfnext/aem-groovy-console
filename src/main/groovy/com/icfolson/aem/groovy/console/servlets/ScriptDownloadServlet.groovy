package com.icfolson.aem.groovy.console.servlets

import com.google.common.net.HttpHeaders
import com.google.common.net.MediaType
import com.icfolson.aem.groovy.console.audit.AuditRecord
import com.icfolson.aem.groovy.console.audit.AuditService
import com.icfolson.aem.groovy.console.constants.GroovyConsoleConstants
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.SlingHttpServletResponse
import org.apache.sling.api.servlets.SlingSafeMethodsServlet
import org.apache.sling.commons.mime.MimeTypeService
import org.osgi.service.component.annotations.Component
import org.osgi.service.component.annotations.Reference

import javax.servlet.Servlet

import static com.icfolson.aem.groovy.console.constants.GroovyConsoleConstants.DATE_FORMAT_FILE_NAME
import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST

@Component(service = Servlet, immediate = true, property = [
    "sling.servlet.paths=/bin/groovyconsole/download"
])
class ScriptDownloadServlet extends SlingSafeMethodsServlet {

    private static final String DEFAULT_MEDIA_TYPE = MediaType.PLAIN_TEXT_UTF_8.withoutParameters().toString()

    @Reference
    private AuditService auditService

    @Reference
    private MimeTypeService mimeTypeService

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        def userId = request.getParameter(GroovyConsoleConstants.USER_ID)
        def script = request.getParameter(GroovyConsoleConstants.SCRIPT)
        def result = request.getParameter(GroovyConsoleConstants.RESULT)

        def auditRecord = auditService.getAuditRecord(userId, script)

        if (auditRecord) {
            def mediaType = result ? DEFAULT_MEDIA_TYPE : getMediaType(auditRecord)

            def fileName = new StringBuilder()

            fileName.append(result ? "result-" : "output-")
            fileName.append(auditRecord.date.format(DATE_FORMAT_FILE_NAME))
            fileName.append(".")
            fileName.append(mimeTypeService.getExtension(mediaType))

            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=${fileName.toString()}")

            response.contentType = mediaType
            response.characterEncoding = GroovyConsoleConstants.CHARSET

            def text = result ? auditRecord.result : auditRecord.output

            response.contentLength = text.length()
            response.outputStream.write(text.getBytes(GroovyConsoleConstants.CHARSET))
        } else {
            response.status = SC_BAD_REQUEST
        }
    }

    private String getMediaType(AuditRecord auditRecord) {
        def mediaType

        if (auditRecord.mediaType) {
            mediaType = MediaType.parse(auditRecord.mediaType)
        } else {
            mediaType = MediaType.PLAIN_TEXT_UTF_8
        }

        mediaType.withoutParameters().toString()
    }
}
