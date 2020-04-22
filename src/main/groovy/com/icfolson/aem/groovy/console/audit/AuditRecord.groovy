package com.icfolson.aem.groovy.console.audit

import com.day.text.Text
import com.icfolson.aem.groovy.console.response.RunScriptResponse
import com.icfolson.aem.groovy.console.response.impl.DefaultRunScriptResponse
import groovy.transform.ToString
import org.apache.sling.api.resource.Resource

@ToString(includePackage = false, includes = ["path"])
class AuditRecord implements RunScriptResponse {

    private static final Integer DEPTH_RELATIVE_PATH = 3

    final String path

    @Delegate
    final RunScriptResponse response

    AuditRecord(Resource resource) {
        path = resource.path
        response = DefaultRunScriptResponse.fromAuditRecordResource(resource)
    }

    String getDownloadUrl() {
        def downloadUrl = null

        if (output) {
            downloadUrl = "/bin/groovyconsole/download?userId=$userId&script=$relativePath"
        }

        downloadUrl
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
