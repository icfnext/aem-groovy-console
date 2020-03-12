package com.icfolson.aem.groovy.console.api

import groovy.transform.TupleConstructor
import org.apache.sling.api.SlingHttpServletRequest

import static com.icfolson.aem.groovy.console.constants.GroovyConsoleConstants.CRON_EXPRESSION
import static com.icfolson.aem.groovy.console.constants.GroovyConsoleConstants.DATE_CREATED
import static com.icfolson.aem.groovy.console.constants.GroovyConsoleConstants.EMAIL_TO
import static com.icfolson.aem.groovy.console.constants.GroovyConsoleConstants.JOB_PROPERTIES
import static com.icfolson.aem.groovy.console.constants.GroovyConsoleConstants.SCHEDULED_JOB_ID

@TupleConstructor
class JobProperties {

    Map<String, Object> properties

    static JobProperties fromRequest(SlingHttpServletRequest request) {
        def properties = JOB_PROPERTIES.collectEntries { propertyName ->
            [propertyName, request.getParameter(propertyName)]
        }.findAll { it.value != null }

        properties[DATE_CREATED] = Calendar.instance
        properties[SCHEDULED_JOB_ID] = UUID.randomUUID().toString()

        new JobProperties(properties)
    }

    List<String> getEmailTo() {
        def emailTo = properties.get(EMAIL_TO) as String

        emailTo ? emailTo.tokenize(",")*.trim() : []
    }

    String getCronExpression() {
        properties.get(CRON_EXPRESSION)
    }

    Map<String, Object> toMap() {
        properties
    }
}