package com.icfolson.aem.groovy.console.servlets

import com.google.common.collect.ImmutableMap
import com.icfolson.aem.groovy.console.GroovyConsoleService
import com.icfolson.aem.groovy.console.api.JobProperties
import com.icfolson.aem.groovy.console.audit.AuditRecord
import com.icfolson.aem.groovy.console.audit.AuditService
import com.icfolson.aem.groovy.console.configuration.ConfigurationService
import com.icfolson.aem.groovy.console.constants.GroovyConsoleConstants
import com.icfolson.aem.groovy.console.utils.GroovyScriptUtils
import groovy.util.logging.Slf4j
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.SlingHttpServletResponse
import org.apache.sling.event.jobs.JobManager
import org.apache.sling.event.jobs.ScheduledJobInfo
import org.osgi.service.component.annotations.Component
import org.osgi.service.component.annotations.Reference

import javax.servlet.Servlet
import javax.servlet.ServletException

import static com.icfolson.aem.groovy.console.constants.GroovyConsoleConstants.DATE_CREATED
import static com.icfolson.aem.groovy.console.constants.GroovyConsoleConstants.SCHEDULED_JOB_ID
import static com.icfolson.aem.groovy.console.constants.GroovyConsoleConstants.SCRIPT
import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST
import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN

@Component(service = Servlet, immediate = true, property = [
    "sling.servlet.paths=/bin/groovyconsole/jobs"
])
@Slf4j("LOG")
class ScheduledJobsServlet extends AbstractJsonResponseServlet {

    @Reference
    private ConfigurationService configurationService

    @Reference
    private GroovyConsoleService consoleService

    @Reference
    private AuditService auditService

    @Reference
    private JobManager jobManager

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
        throws ServletException, IOException {
        def scheduledJob = findScheduledJobById(request)

        if (scheduledJob) {
            // single job
            writeJsonResponse(response, scheduledJob.jobProperties)
        } else {
            def scheduledJobAuditRecords = auditService.allScheduledJobAuditRecords

            // list all jobs
            def scheduledJobs = jobManager.getScheduledJobs(GroovyConsoleConstants.JOB_TOPIC, 0, null)
                .collect { scheduledJobInfo ->
                    def auditRecords = scheduledJobAuditRecords.findAll { record ->
                        isAuditRecordForScheduledJob(record, scheduledJobInfo)
                    }

                    new ImmutableMap.Builder<String, Object>()
                        .putAll(scheduledJobInfo.jobProperties)
                        .put("downloadUrl", auditRecords ? auditRecords.last().downloadUrl : null)
                        .put("scriptPreview", GroovyScriptUtils.getScriptPreview(scheduledJobInfo.jobProperties[SCRIPT] as String))
                        .put("nextExecutionDate", scheduledJobInfo.nextScheduledExecution.format(GroovyConsoleConstants.DATE_FORMAT_DISPLAY))
                        .build()
                }.sort { properties -> properties[DATE_CREATED] }

            writeJsonResponse(response, [data: scheduledJobs])
        }
    }

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
        throws ServletException, IOException {
        if (configurationService.hasScheduledJobPermission(request)) {
            def scheduledJob = findScheduledJobById(request)

            // 'edit' existing job by unscheduling and adding new
            if (scheduledJob) {
                LOG.info("found existing scheduled job, unscheduling and adding new job...")

                scheduledJob.unschedule()
            }

            def jobProperties = JobProperties.fromRequest(request)

            LOG.debug("adding job with properties : {}", jobProperties.toMap())

            if (consoleService.addScheduledJob(jobProperties)) {
                writeJsonResponse(response, jobProperties)
            } else {
                LOG.error("error adding job with properties : {}", jobProperties.toMap())

                response.status = SC_BAD_REQUEST
            }
        } else {
            response.status = SC_FORBIDDEN
        }
    }

    @Override
    protected void doDelete(SlingHttpServletRequest request, SlingHttpServletResponse response)
        throws ServletException, IOException {
        if (configurationService.hasScheduledJobPermission(request)) {
            def scheduledJob = findScheduledJobById(request)

            if (scheduledJob) {
                scheduledJob.unschedule()
            } else {
                response.status = SC_BAD_REQUEST
            }
        } else {
            response.status = SC_FORBIDDEN
        }
    }

    private ScheduledJobInfo findScheduledJobById(SlingHttpServletRequest request) {
        def id = request.getParameter(SCHEDULED_JOB_ID)

        def scheduledJobInfo = null

        if (id) {
            scheduledJobInfo = jobManager.scheduledJobs.find { job ->
                job.jobProperties[SCHEDULED_JOB_ID] == id
            }
        }

        scheduledJobInfo
    }

    private boolean isAuditRecordForScheduledJob(AuditRecord auditRecord, ScheduledJobInfo scheduledJobInfo) {
        def scheduledJobId = auditRecord.jobProperties?.scheduledJobId

        scheduledJobId && scheduledJobId == scheduledJobInfo.jobProperties.get(SCHEDULED_JOB_ID) as String
    }
}