package com.icfolson.aem.groovy.console.api

import com.icfolson.aem.groovy.console.constants.GroovyConsoleConstants
import com.icfolson.aem.groovy.console.utils.GroovyScriptUtils
import groovy.transform.Memoized
import groovy.transform.TupleConstructor
import org.apache.sling.event.jobs.Job

@TupleConstructor
class ActiveJob {

    Job job

    String getFormattedStartTime() {
        job.processingStarted.format(GroovyConsoleConstants.DATE_FORMAT_DISPLAY)
    }

    String getId() {
        job.id
    }

    String getTitle() {
        jobProperties.jobTitle
    }

    String getDescription() {
        jobProperties.jobDescription
    }

    String getScript() {
        GroovyScriptUtils.getScriptPreview(jobProperties.script)
    }

    @Memoized
    JobProperties getJobProperties() {
        JobProperties.fromJob(job)
    }
}
