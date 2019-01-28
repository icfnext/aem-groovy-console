package com.icfolson.aem.groovy.console.audit.impl

import com.day.cq.commons.jcr.JcrUtil
import com.icfolson.aem.groovy.console.audit.AuditRecord
import com.icfolson.aem.groovy.console.audit.AuditService
import com.icfolson.aem.groovy.console.configuration.ConfigurationService
import com.icfolson.aem.groovy.console.response.RunScriptResponse
import groovy.transform.Synchronized
import groovy.util.logging.Slf4j
import org.apache.felix.scr.annotations.Activate
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.Deactivate
import org.apache.felix.scr.annotations.Reference
import org.apache.felix.scr.annotations.Service
import org.apache.sling.api.resource.PersistenceException
import org.apache.sling.api.resource.ResourceResolver
import org.apache.sling.api.resource.ResourceResolverFactory

import javax.jcr.Node
import javax.jcr.RepositoryException
import javax.jcr.Session

import static com.day.cq.commons.jcr.JcrConstants.JCR_CONTENT
import static com.day.cq.commons.jcr.JcrConstants.MIX_CREATED
import static com.day.cq.commons.jcr.JcrConstants.NT_UNSTRUCTURED
import static com.icfolson.aem.groovy.console.constants.GroovyConsoleConstants.AUDIT_NODE_NAME
import static com.icfolson.aem.groovy.console.constants.GroovyConsoleConstants.AUDIT_PATH
import static com.icfolson.aem.groovy.console.constants.GroovyConsoleConstants.AUDIT_RECORD_NODE_PREFIX
import static com.icfolson.aem.groovy.console.constants.GroovyConsoleConstants.PATH_CONSOLE_ROOT

@Component(immediate = true)
@Service(AuditService)
@Slf4j("LOG")
class DefaultAuditService implements AuditService {

    private static final String DATE_FORMAT_YEAR = "yyyy"

    private static final String DATE_FORMAT_MONTH = "MM"

    private static final String DATE_FORMAT_DAY = "dd"

    @Reference
    private ResourceResolverFactory resourceResolverFactory

    @Reference
    private ConfigurationService configurationService

    private ResourceResolver resourceResolver

    @Override
    AuditRecord createAuditRecord(RunScriptResponse response)
        throws RepositoryException, PersistenceException {
        def auditRecord

        try {
            resourceResolver.refresh()

            def auditRecordNode = addAuditRecordNode(response.userId)

            setAuditRecordNodeProperties(auditRecordNode, response)

            resourceResolver.commit()

            def auditRecordResource = resourceResolver.getResource(auditRecordNode.path)

            auditRecord = new AuditRecord(auditRecordResource)

            LOG.debug("created audit record = {}", auditRecord)
        } catch (RepositoryException | PersistenceException e) {
            LOG.error("error creating audit record", e)

            throw e
        }

        auditRecord
    }

    @Override
    void deleteAllAuditRecords(String userId) throws PersistenceException {
        try {
            resourceResolver.refresh()

            def auditNodePath = getAuditNodePath(userId)

            def auditResource = resourceResolver.getResource(auditNodePath)

            if (auditResource) {
                auditResource.listChildren().each { resource ->
                    resourceResolver.delete(resource)
                }

                LOG.debug("deleted all audit record resources for path = {}", auditNodePath)

                resourceResolver.commit()
            } else {
                LOG.debug("audit resource not found for user ID = {}", userId)
            }
        } catch (PersistenceException e) {
            LOG.error("error deleting audit records", e)

            throw e
        }
    }

    @Override
    void deleteAuditRecord(String userId, String relativePath) throws PersistenceException {
        try {
            resourceResolver.refresh()

            def auditRecordResource = resourceResolver.getResource("$AUDIT_PATH/$userId/$relativePath")

            resourceResolver.delete(auditRecordResource)

            LOG.debug("deleted audit record for user = {} at relative path = {}", userId, relativePath)

            resourceResolver.commit()
        } catch (PersistenceException e) {
            LOG.error("error deleting audit record", e)

            throw e
        }
    }

    @Override
    List<AuditRecord> getAllAuditRecords(String userId) {
        resourceResolver.refresh()

        def auditNodePath = getAuditNodePath(userId)

        findAllAuditRecords(auditNodePath)
    }

    @Override
    AuditRecord getAuditRecord(String userId, String relativePath) {
        resourceResolver.refresh()

        def auditRecordResource = resourceResolver.getResource("$AUDIT_PATH/$userId").getChild(relativePath)

        def auditRecord = null

        if (auditRecordResource) {
            auditRecord = new AuditRecord(auditRecordResource)

            LOG.debug("found audit record = {}", auditRecord)
        }

        auditRecord
    }

    @Override
    List<AuditRecord> getAuditRecords(String userId, Calendar startDate, Calendar endDate) {
        resourceResolver.refresh()

        getAllAuditRecords(userId).findAll { auditRecord ->
            def auditRecordDate = auditRecord.date

            auditRecordDate.set(Calendar.HOUR_OF_DAY, 0)
            auditRecordDate.set(Calendar.MINUTE, 0)
            auditRecordDate.set(Calendar.SECOND, 0)
            auditRecordDate.set(Calendar.MILLISECOND, 0)

            !auditRecordDate.before(startDate) && !auditRecordDate.after(endDate)
        }
    }

    @Activate
    void activate() {
        resourceResolver = resourceResolverFactory.getServiceResourceResolver(null)

        checkAuditNode()
    }

    @Deactivate
    void deactivate() {
        resourceResolver?.close()
    }

    @Synchronized
    private Node addAuditRecordNode(String userId) {
        def date = Calendar.instance
        def year = date.format(DATE_FORMAT_YEAR)
        def month = date.format(DATE_FORMAT_MONTH)
        def day = date.format(DATE_FORMAT_DAY)

        def adminSession = resourceResolver.adaptTo(Session)

        def auditRecordParentNode = JcrUtil.createPath("$AUDIT_PATH/$userId/$year/$month/$day",
            NT_UNSTRUCTURED, adminSession)
        def auditRecordNode = JcrUtil.createUniqueNode(auditRecordParentNode, AUDIT_RECORD_NODE_PREFIX, NT_UNSTRUCTURED,
            adminSession)

        auditRecordNode.addMixin(MIX_CREATED)

        auditRecordNode
    }

    private void checkAuditNode() {
        def session = resourceResolver.adaptTo(Session)
        def contentNode = session.getNode(PATH_CONSOLE_ROOT).getNode(JCR_CONTENT)

        if (!contentNode.hasNode(AUDIT_NODE_NAME)) {
            LOG.info("audit node does not exist, adding")

            contentNode.addNode(AUDIT_NODE_NAME)

            session.save()
        }
    }

    private void setAuditRecordNodeProperties(Node auditRecordNode, RunScriptResponse response) {
        auditRecordNode.setProperty(AuditRecord.PROPERTY_SCRIPT, response.script)

        if (response.data) {
            auditRecordNode.setProperty(AuditRecord.PROPERTY_DATA, response.data)
        }

        if (response.exceptionStackTrace) {
            auditRecordNode.setProperty(AuditRecord.PROPERTY_EXCEPTION_STACK_TRACE, response.exceptionStackTrace)

            if (response.output) {
                auditRecordNode.setProperty(AuditRecord.PROPERTY_OUTPUT, response.output)
            }
        } else {
            if (response.result) {
                auditRecordNode.setProperty(AuditRecord.PROPERTY_RESULT, response.result)
            }

            if (response.output) {
                auditRecordNode.setProperty(AuditRecord.PROPERTY_OUTPUT, response.output)
            }

            auditRecordNode.setProperty(AuditRecord.PROPERTY_RUNNING_TIME, response.runningTime)
        }
    }

    private String getAuditNodePath(String userId) {
        configurationService.displayAllAuditRecords ? AUDIT_PATH : "$AUDIT_PATH/$userId"
    }

    private List<AuditRecord> findAllAuditRecords(String auditNodePath) {
        def auditRecords = []

        def auditResource = resourceResolver.getResource(auditNodePath)

        if (auditResource) {
            auditResource.listChildren().each { resource ->
                if (resource.name.startsWith(AUDIT_RECORD_NODE_PREFIX)) {
                    auditRecords.add(new AuditRecord(resource))
                }

                auditRecords.addAll(findAllAuditRecords(resource.path))
            }
        }

        auditRecords
    }
}