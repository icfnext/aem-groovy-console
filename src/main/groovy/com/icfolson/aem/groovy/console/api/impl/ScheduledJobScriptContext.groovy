package com.icfolson.aem.groovy.console.api.impl

import com.icfolson.aem.groovy.console.api.JobProperties
import com.icfolson.aem.groovy.console.api.JobScriptContext
import groovy.transform.TupleConstructor
import org.apache.sling.api.resource.ResourceResolver

@TupleConstructor
class ScheduledJobScriptContext implements JobScriptContext {

    ResourceResolver resourceResolver

    ByteArrayOutputStream outputStream

    PrintStream printStream

    String jobId

    JobProperties jobProperties

    @Override
    String getScript() {
        jobProperties.script
    }

    @Override
    String getData() {
        jobProperties.data
    }

    @Override
    String getUserId() {
        resourceResolver.userID
    }
}
