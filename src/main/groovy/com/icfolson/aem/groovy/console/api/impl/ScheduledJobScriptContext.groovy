package com.icfolson.aem.groovy.console.api.impl

import com.icfolson.aem.groovy.console.api.JobScriptContext
import groovy.transform.TupleConstructor
import org.apache.sling.api.resource.ResourceResolver

@TupleConstructor
class ScheduledJobScriptContext implements JobScriptContext {

    ResourceResolver resourceResolver

    ByteArrayOutputStream outputStream

    PrintStream printStream

    String script

    String data

    String jobId

    String mediaType

    @Override
    String getUserId() {
        resourceResolver.userID
    }
}
