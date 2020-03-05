package com.icfolson.aem.groovy.console.api

import com.google.common.base.Charsets
import org.apache.sling.api.SlingHttpServletRequest

import static com.icfolson.aem.groovy.console.constants.GroovyConsoleConstants.JOB_PROPERTIES
import static com.icfolson.aem.groovy.console.constants.GroovyConsoleConstants.JOB_PROPERTY_CRON_EXPRESSION
import static com.icfolson.aem.groovy.console.constants.GroovyConsoleConstants.JOB_PROPERTY_EMAIL_TO

class JobProperties implements Map<String, Object> {

    @Delegate
    Map<String, Object> properties = [:]

    static JobProperties fromRequest(SlingHttpServletRequest request) {
        def jobProperties = new JobProperties()

        JOB_PROPERTIES.each { propertyName ->
            jobProperties[propertyName] = request.getRequestParameter(propertyName)?.getString(Charsets.UTF_8.name())
        }

        jobProperties
    }

    List<String> getEmailTo() {
        def emailTo = get(JOB_PROPERTY_EMAIL_TO) as String

        emailTo ? emailTo.tokenize(",")*.trim() : []
    }

    String getCronExpression() {
        get(JOB_PROPERTY_CRON_EXPRESSION)
    }

    private JobProperties() {

    }
}