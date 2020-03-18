package com.icfolson.aem.groovy.console.job.event

import com.icfolson.aem.groovy.console.audit.AuditService
import com.icfolson.aem.groovy.console.notification.EmailNotificationService
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
class GroovyConsoleEmailNotificationEventHandler implements EventHandler {

    @Reference
    private AuditService auditService

    @Reference
    private EmailNotificationService emailNotificationService

    @Override
    void handleEvent(Event event) {
        LOG.info("handling completed groovy console job with properties : {}", event.propertyNames
            .collectEntries { propertyName -> [propertyName, event.getProperty(propertyName)] })

        def jobId = event.getProperty(NotificationConstants.NOTIFICATION_PROPERTY_JOB_ID) as String
        def auditRecord = auditService.getAuditRecord(jobId)

        if (auditRecord) {
            if (auditRecord.jobProperties?.emailTo) {
                LOG.info("found audit record for job ID : {}, {}, sending notifications...", jobId, auditRecord)

                emailNotificationService.notify(auditRecord, auditRecord.jobProperties.emailTo, true)
            } else {
                LOG.info("missing job properties and/or email recipients for audit record, ignoring...")
            }
        } else {
            LOG.error("audit record not found for job ID : {}", jobId)
        }
    }
}
