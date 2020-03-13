package com.icfolson.aem.groovy.console.audit

import com.day.text.Text
import com.icfolson.aem.groovy.console.response.RunScriptResponse
import com.icfolson.aem.groovy.console.response.impl.DefaultRunScriptResponse
import groovy.transform.ToString
import org.apache.sling.api.resource.Resource

import static com.day.cq.commons.jcr.JcrConstants.JCR_CREATED

@ToString(includePackage = false, includes = ["path"])
class AuditRecord implements RunScriptResponse {

    private static final Integer DEPTH_RELATIVE_PATH = 3

    final String path

    final Calendar date

    @Delegate
    final RunScriptResponse response

    AuditRecord(Resource resource) {
        path = resource.path
        date = resource.valueMap.get(JCR_CREATED, Calendar)
        response = DefaultRunScriptResponse.fromAuditRecordResource(resource)
    }

    String getRelativePath() {
        (path - Text.getAbsoluteParent(path, DEPTH_RELATIVE_PATH)).substring(1)
    }

    String getException() {
        def exception = ""

        if (exceptionStackTrace) {
            def firstLine = exceptionStackTrace.readLines().first()

            if (firstLine.contains(":")) {
                exception = firstLine.substring(0, firstLine.indexOf(":"))
            } else {
                exception = firstLine
            }
        }

        exception
    }
}
