package com.icfolson.aem.groovy.console.job.event

import com.icfolson.aem.groovy.console.audit.AuditService
import com.icfolson.aem.groovy.console.response.RunScriptResponse
import groovy.util.logging.Slf4j
import org.apache.sling.event.jobs.NotificationConstants
import org.osgi.service.event.Event
import org.osgi.service.event.EventHandler

/**
 * Event handler base class that can be extended to implement custom handling of
 * completed scheduled job executions.
 */
@Slf4j("LOG")
abstract class AbstractGroovyConsoleScheduledJobEventHandler implements EventHandler {

    @Override
    final void handleEvent(Event event) {
        LOG.debug("handling completed scheduled job with properties : {}", event.propertyNames
            .collectEntries { propertyName -> [propertyName, event.getProperty(propertyName)] })

        def jobId = event.getProperty(NotificationConstants.NOTIFICATION_PROPERTY_JOB_ID) as String
        def auditRecord = getAuditService().getAuditRecord(jobId)

        if (auditRecord) {
            LOG.info("found audit record for job ID : {}, {}, handling event...", jobId, auditRecord)

            handleScheduledJobEvent(auditRecord)
        } else {
            LOG.error("audit record not found for job ID : {}", jobId)
        }
    }

    /**
     * Handle the scheduled job completion event for the given script execution response.
     *
     * @param response script execution response
     */
    protected abstract void handleScheduledJobEvent(RunScriptResponse response)

    /**
     * Get the audit service.
     *
     * @return audit service
     */
    protected abstract AuditService getAuditService()
}
