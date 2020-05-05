package com.icfolson.aem.groovy.console.audit.impl

import com.day.cq.commons.jcr.JcrUtil
import com.icfolson.aem.groovy.console.audit.AuditRecord
import com.icfolson.aem.groovy.console.audit.AuditService
import com.icfolson.aem.groovy.console.configuration.ConfigurationService
import com.icfolson.aem.groovy.console.constants.GroovyConsoleConstants
import com.icfolson.aem.groovy.console.response.RunScriptResponse
import groovy.transform.Synchronized
import groovy.util.logging.Slf4j
import org.apache.sling.api.resource.PersistenceException
import org.apache.sling.api.resource.ResourceResolver
import org.apache.sling.api.resource.ResourceResolverFactory
import org.osgi.service.component.annotations.Activate
import org.osgi.service.component.annotations.Component
import org.osgi.service.component.annotations.Reference

import javax.jcr.Node
import javax.jcr.RepositoryException
import javax.jcr.Session

import static com.day.cq.commons.jcr.JcrConstants.MIX_CREATED
import static com.day.cq.commons.jcr.JcrConstants.NT_UNSTRUCTURED
import static com.icfolson.aem.groovy.console.constants.GroovyConsoleConstants.AUDIT_JOB_PROPERTIES
import static com.icfolson.aem.groovy.console.constants.GroovyConsoleConstants.AUDIT_NODE_NAME
import static com.icfolson.aem.groovy.console.constants.GroovyConsoleConstants.AUDIT_PATH
import static com.icfolson.aem.groovy.console.constants.GroovyConsoleConstants.AUDIT_RECORD_NODE_PREFIX
import static com.icfolson.aem.groovy.console.constants.GroovyConsoleConstants.DATA
import static com.icfolson.aem.groovy.console.constants.GroovyConsoleConstants.EXCEPTION_STACK_TRACE
import static com.icfolson.aem.groovy.console.constants.GroovyConsoleConstants.JOB_ID
import static com.icfolson.aem.groovy.console.constants.GroovyConsoleConstants.OUTPUT
import static com.icfolson.aem.groovy.console.constants.GroovyConsoleConstants.PATH_CONSOLE_ROOT
import static com.icfolson.aem.groovy.console.constants.GroovyConsoleConstants.RESULT
import static com.icfolson.aem.groovy.console.constants.GroovyConsoleConstants.RUNNING_TIME
import static com.icfolson.aem.groovy.console.constants.GroovyConsoleConstants.SCRIPT

@Component(service = AuditService, immediate = true)
@Slf4j("LOG")
class DefaultAuditService implements AuditService {

    private static final String DATE_FORMAT_YEAR = "yyyy"

    private static final String DATE_FORMAT_MONTH = "MM"

    private static final String DATE_FORMAT_DAY = "dd"

    @Reference
    private ResourceResolverFactory resourceResolverFactory

    @Reference
    private ConfigurationService configurationService

    @Override
    AuditRecord createAuditRecord(RunScriptResponse response) {
        def auditRecord

        withResourceResolver { ResourceResolver resourceResolver ->
            try {
                def auditRecordNode = addAuditRecordNode(resourceResolver, response.userId)

                setAuditRecordNodeProperties(auditRecordNode, response)

                resourceResolver.commit()

                def auditRecordResource = resourceResolver.getResource(auditRecordNode.path)

                auditRecord = new AuditRecord(auditRecordResource)

                LOG.debug("created audit record : {}", auditRecord)

                auditRecord
            } catch (RepositoryException | PersistenceException e) {
                LOG.error("error creating audit record", e)

                throw e
            }
        }
    }

    @Override
    void deleteAllAuditRecords(String userId) {
        withResourceResolver { ResourceResolver resourceResolver ->
            try {
                def auditNodePath = getAuditNodePath(userId)
                def auditResource = resourceResolver.getResource(auditNodePath)

                if (auditResource) {
                    auditResource.listChildren().each { resource ->
                        resourceResolver.delete(resource)
                    }

                    LOG.debug("deleted all audit record resources for path : {}", auditNodePath)

                    resourceResolver.commit()
                } else {
                    LOG.debug("audit resource not found for user ID : {}", userId)
                }
            } catch (PersistenceException e) {
                LOG.error("error deleting audit records", e)

                throw e
            }
        }
    }

    @Override
    void deleteAuditRecord(String userId, String relativePath) {
        withResourceResolver { ResourceResolver resourceResolver ->
            try {
                def auditRecordResource = resourceResolver.getResource("$AUDIT_PATH/$userId/$relativePath")

                resourceResolver.delete(auditRecordResource)

                LOG.debug("deleted audit record for user : {} at relative path : {}", userId, relativePath)

                resourceResolver.commit()
            } catch (PersistenceException e) {
                LOG.error("error deleting audit record", e)

                throw e
            }
        }
    }

    @Override
    List<AuditRecord> getAllAuditRecords(String userId) {
        def auditNodePath = getAuditNodePath(userId)

        withResourceResolver { ResourceResolver resourceResolver ->
            findAllAuditRecords(resourceResolver, auditNodePath)
        }
    }

    @Override
    List<AuditRecord> getAllScheduledJobAuditRecords() {
        getAllAuditRecords(GroovyConsoleConstants.SYSTEM_USER_NAME)
    }

    @Override
    AuditRecord getAuditRecord(String jobId) {
        withResourceResolver { ResourceResolver resourceResolver ->
            findAllAuditRecords(resourceResolver, AUDIT_PATH).find { auditRecord ->
                auditRecord.jobId == jobId
            }
        }
    }

    @Override
    AuditRecord getAuditRecord(String userId, String relativePath) {
        def auditRecord = null

        withResourceResolver { ResourceResolver resourceResolver ->
            def auditRecordResource = resourceResolver.getResource("$AUDIT_PATH/$userId").getChild(relativePath)

            if (auditRecordResource) {
                auditRecord = new AuditRecord(auditRecordResource)

                LOG.debug("found audit record : {}", auditRecord)
            }
        }

        auditRecord
    }

    @Override
    List<AuditRecord> getAuditRecords(String userId, Calendar startDate, Calendar endDate) {
        getAuditRecordsForDateRange(getAllAuditRecords(userId), startDate, endDate)
    }

    @Override
    List<AuditRecord> getScheduledJobAuditRecords(Calendar startDate, Calendar endDate) {
        getAuditRecordsForDateRange(allScheduledJobAuditRecords, startDate, endDate)
    }

    @Activate
    void activate() {
        checkAuditNode()
    }

    @Synchronized
    private Node addAuditRecordNode(ResourceResolver resourceResolver, String userId) {
        def date = Calendar.instance
        def year = date.format(DATE_FORMAT_YEAR)
        def month = date.format(DATE_FORMAT_MONTH)
        def day = date.format(DATE_FORMAT_DAY)

        def adminSession = resourceResolver.adaptTo(Session)

        def auditRecordParentNode = JcrUtil.createPath("$AUDIT_PATH/$userId/$year/$month/$day", NT_UNSTRUCTURED,
            adminSession)

        def auditRecordNode = JcrUtil.createUniqueNode(auditRecordParentNode, AUDIT_RECORD_NODE_PREFIX, NT_UNSTRUCTURED,
            adminSession)

        auditRecordNode.addMixin(MIX_CREATED)

        auditRecordNode
    }

    private void checkAuditNode() {
        withResourceResolver { ResourceResolver resourceResolver ->
            def session = resourceResolver.adaptTo(Session)
            def consoleRootNode = session.getNode(PATH_CONSOLE_ROOT)

            if (!consoleRootNode.hasNode(AUDIT_NODE_NAME)) {
                LOG.info("audit node does not exist, adding")

                consoleRootNode.addNode(AUDIT_NODE_NAME, NT_UNSTRUCTURED)

                session.save()
            }
        }
    }

    private void setAuditRecordNodeProperties(Node auditRecordNode, RunScriptResponse response) {
        auditRecordNode.setProperty(SCRIPT, response.script)

        if (response.data) {
            auditRecordNode.setProperty(DATA, response.data)
        }

        if (response.jobId) {
            auditRecordNode.setProperty(JOB_ID, response.jobId)
        }

        if (response.jobProperties) {
            response.jobProperties.toMap()
                .findAll { entry -> AUDIT_JOB_PROPERTIES.contains(entry.key) }
                .each { entry ->
                    if (entry.value instanceof String) {
                        auditRecordNode.setProperty(entry.key, entry.value as String)
                    } else if (entry.value instanceof Calendar) {
                        auditRecordNode.setProperty(entry.key, entry.value as Calendar)
                    }
                }
        }

        if (response.exceptionStackTrace) {
            auditRecordNode.setProperty(EXCEPTION_STACK_TRACE, response.exceptionStackTrace)

            if (response.output) {
                auditRecordNode.setProperty(OUTPUT, response.output)
            }
        } else {
            if (response.result) {
                auditRecordNode.setProperty(RESULT, response.result)
            }

            if (response.output) {
                auditRecordNode.setProperty(OUTPUT, response.output)
            }

            auditRecordNode.setProperty(RUNNING_TIME, response.runningTime)
        }
    }

    private String getAuditNodePath(String userId) {
        configurationService.displayAllAuditRecords ? AUDIT_PATH : "$AUDIT_PATH/$userId"
    }

    private List<AuditRecord> findAllAuditRecords(ResourceResolver resourceResolver, String auditNodePath) {
        def auditRecords = []

        def auditResource = resourceResolver.getResource(auditNodePath)

        if (auditResource) {
            auditResource.listChildren().each { resource ->
                if (resource.name.startsWith(AUDIT_RECORD_NODE_PREFIX)) {
                    auditRecords.add(new AuditRecord(resource))
                }

                auditRecords.addAll(findAllAuditRecords(resourceResolver, resource.path))
            }
        }

        auditRecords
    }

    private List<AuditRecord> getAuditRecordsForDateRange(List<AuditRecord> auditRecords, Calendar startDate, Calendar endDate) {
        auditRecords.findAll { auditRecord ->
            def auditRecordDate = auditRecord.date

            auditRecordDate.set(Calendar.HOUR_OF_DAY, 0)
            auditRecordDate.set(Calendar.MINUTE, 0)
            auditRecordDate.set(Calendar.SECOND, 0)
            auditRecordDate.set(Calendar.MILLISECOND, 0)

            !auditRecordDate.before(startDate) && !auditRecordDate.after(endDate)
        }
    }

    private <T> T withResourceResolver(Closure<T> closure) {
        resourceResolverFactory.getServiceResourceResolver(null).withCloseable(closure)
    }
}