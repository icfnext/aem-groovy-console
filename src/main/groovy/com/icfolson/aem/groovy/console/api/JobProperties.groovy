package com.icfolson.aem.groovy.console.api

import com.google.common.base.Charsets
import groovy.transform.TupleConstructor
import org.apache.sling.api.SlingHttpServletRequest

import static com.icfolson.aem.groovy.console.constants.GroovyConsoleConstants.JOB_PROPERTIES
import static com.icfolson.aem.groovy.console.constants.GroovyConsoleConstants.JOB_PROPERTY_CRON_EXPRESSION
import static com.icfolson.aem.groovy.console.constants.GroovyConsoleConstants.JOB_PROPERTY_EMAIL_TO

@TupleConstructor
class JobProperties {

    Map<String, Object> properties

    static JobProperties fromRequest(SlingHttpServletRequest request) {
        def properties = JOB_PROPERTIES.collectEntries { propertyName ->
            [propertyName, request.getRequestParameter(propertyName)?.getString(Charsets.UTF_8.name())]
        }.findAll { it.value != null }

        new JobProperties(properties)
    }

    List<String> getEmailTo() {
        def emailTo = properties.get(JOB_PROPERTY_EMAIL_TO) as String

        emailTo ? emailTo.tokenize(",")*.trim() : []
    }

    String getCronExpression() {
        properties.get(JOB_PROPERTY_CRON_EXPRESSION)
    }

    Map<String, Object> toMap() {
        properties
    }
}