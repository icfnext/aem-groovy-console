package com.icfolson.aem.groovy.console.job.event.impl

import com.icfolson.aem.groovy.console.audit.AuditService
import com.icfolson.aem.groovy.console.job.event.AbstractGroovyConsoleScheduledJobEventHandler
import com.icfolson.aem.groovy.console.notification.EmailNotificationService
import com.icfolson.aem.groovy.console.response.RunScriptResponse
import groovy.util.logging.Slf4j
import org.apache.sling.event.jobs.NotificationConstants
import org.osgi.service.component.annotations.Component
import org.osgi.service.component.annotations.Reference
import org.osgi.service.event.EventHandler
import org.osgi.service.event.propertytypes.EventFilter
import org.osgi.service.event.propertytypes.EventTopics

@Component(service = EventHandler, immediate = true)
@EventTopics(NotificationConstants.TOPIC_JOB_FINISHED)
@EventFilter("(event.job.topic=groovyconsole/job*)")
@Slf4j("LOG")
class DefaultGroovyConsoleEmailNotificationEventHandler {

    @Reference
    private EmailNotificationService emailNotificationService

    @Reference
    private AuditService auditService

    @Override
    protected void handleScheduledJobEvent(RunScriptResponse response) {
        if (response.jobProperties?.emailTo) {
            emailNotificationService.notify(response, response.jobProperties.emailTo, true)
        } else {
            LOG.debug("missing job properties and/or email recipients for script execution, ignoring...")
        }
    }

    @Override
    protected AuditService getAuditService() {
        auditService
    }
}
