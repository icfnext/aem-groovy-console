package com.icfolson.aem.groovy.console.job.eventhandlers

import com.icfolson.aem.groovy.console.audit.AuditService
import groovy.util.logging.Slf4j
import org.apache.sling.event.jobs.NotificationConstants
import org.osgi.service.component.annotations.Component
import org.osgi.service.component.annotations.Reference
import org.osgi.service.event.Event
import org.osgi.service.event.EventHandler
import org.osgi.service.event.propertytypes.EventFilter
import org.osgi.service.event.propertytypes.EventTopics

@Component(service = EventHandler, immediate = true)
@EventTopics(NotificationConstants.TOPIC_JOB_FINISHED)
@EventFilter("(event.job.topic=groovyconsole/job*)")
@Slf4j("LOG")
class GroovyConsoleNotificationEventHandler implements EventHandler {

    @Reference
    private AuditService auditService

    @Override
    void handleEvent(Event event) {
        def eventProperties = event.propertyNames.collectEntries { propertyName ->
            [propertyName, event.getProperty(propertyName)]
        }

        LOG.info("handling completed groovy console job with properties : {}", eventProperties)

        def jobId = event.getProperty(NotificationConstants.NOTIFICATION_PROPERTY_JOB_ID) as String

        def auditRecord = auditService.getAuditRecord(jobId)

        if (auditRecord) {
            LOG.info("found audit record for job ID : {}, {}", jobId, auditRecord)

            // TODO
        } else {
            LOG.error("audit record not found for job ID : {}", jobId)
        }
    }
}
