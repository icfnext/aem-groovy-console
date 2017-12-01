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
import org.apache.sling.jcr.api.SlingRepository

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
    private SlingRepository repository

    @Reference
    private ConfigurationService configurationService

    private Session adminSession

    @Override
    AuditRecord createAuditRecord(Session session, RunScriptResponse response) throws RepositoryException {
        def auditRecord

        try {
            adminSession.refresh(false)

            def auditRecordNode = addAuditRecordNode(session)

            auditRecordNode.setProperty(AuditRecord.PROPERTY_SCRIPT, response.script)
            auditRecordNode.setProperty(AuditRecord.PROPERTY_DATA, response.data)

            if (response.exceptionStackTrace) {
                auditRecordNode.setProperty(AuditRecord.PROPERTY_EXCEPTION_STACK_TRACE, response.exceptionStackTrace)
            } else {
                if (response.result) {
                    auditRecordNode.setProperty(AuditRecord.PROPERTY_RESULT, response.result)
                }

                if (response.output) {
                    auditRecordNode.setProperty(AuditRecord.PROPERTY_OUTPUT, response.output)
                }

                auditRecordNode.setProperty(AuditRecord.PROPERTY_RUNNING_TIME, response.runningTime)
            }

            adminSession.save()

            auditRecord = new AuditRecord(auditRecordNode)

            LOG.debug("created audit record = {}", auditRecord)
        } catch (RepositoryException e) {
            LOG.error("error creating audit record", e)

            throw e
        }

        auditRecord
    }

    @Override
    void deleteAllAuditRecords(Session session) throws RepositoryException {
        try {
            adminSession.refresh(false)

            def auditNodePath = getAuditNodePath(session)

            if (adminSession.nodeExists(auditNodePath)) {
                adminSession.getNode(auditNodePath).nodes*.remove()

                LOG.debug("deleted all audit record nodes for path = {}", auditNodePath)

                adminSession.save()
            }
        } catch (RepositoryException e) {
            LOG.error("error deleting audit records", e)

            throw e
        }
    }

    @Override
    void deleteAuditRecord(Session session, String relativePath) throws RepositoryException {
        deleteAuditRecord(session, session.userID, relativePath)
    }

    @Override
    void deleteAuditRecord(Session session, String userId, String relativePath) throws RepositoryException {
        try {
            adminSession.refresh(false)
            adminSession.getNode("$AUDIT_PATH/$userId").getNode(relativePath).remove()

            LOG.debug("deleted audit record for user = {} at relative path = {}", userId, relativePath)

            adminSession.save()
        } catch (RepositoryException e) {
            LOG.error("error deleting audit record", e)

            throw e
        }
    }

    @Override
    List<AuditRecord> getAllAuditRecords(Session session) throws RepositoryException {
        def auditRecords = []

        try {
            adminSession.refresh(false)

            def auditNodePath = getAuditNodePath(session)

            auditRecords.addAll(findAllAuditRecords(auditNodePath))
        } catch (RepositoryException e) {
            LOG.error("error getting audit records", e)

            throw e
        }

        auditRecords
    }

    @Override
    AuditRecord getAuditRecord(Session session, String relativePath) throws RepositoryException {
        getAuditRecord(session, session.userID, relativePath)
    }

    @Override
    AuditRecord getAuditRecord(Session session, String userId, String relativePath) throws RepositoryException {
        def auditRecord = null

        try {
            adminSession.refresh(false)

            def auditNode = adminSession.getNode("$AUDIT_PATH/$userId")

            if (auditNode.hasNode(relativePath)) {
                def auditRecordNode = auditNode.getNode(relativePath)

                auditRecord = new AuditRecord(auditRecordNode)

                LOG.debug("found audit record = {}", auditRecord)
            }
        } catch (RepositoryException e) {
            LOG.error("error getting audit record", e)

            throw e
        }

        auditRecord
    }

    @Override
    List<AuditRecord> getAuditRecords(Session session, Calendar startDate,
        Calendar endDate) throws RepositoryException {
        def auditRecords

        try {
            adminSession.refresh(false)

            def auditNodePath = getAuditNodePath(session)

            def visitor = new AuditRecordNodeVisitor()

            visitor.visit(adminSession.getNode(auditNodePath))

            auditRecords = visitor.auditRecords.findAll { auditRecord ->
                def auditRecordDate = auditRecord.date

                auditRecordDate.set(Calendar.HOUR_OF_DAY, 0)
                auditRecordDate.set(Calendar.MINUTE, 0)
                auditRecordDate.set(Calendar.SECOND, 0)
                auditRecordDate.set(Calendar.MILLISECOND, 0)

                !auditRecordDate.before(startDate) && !auditRecordDate.after(endDate)
            }
        } catch (RepositoryException e) {
            LOG.error("error getting audit records for date range", e)

            throw e
        }

        auditRecords
    }

    @Activate
    void activate() {
        adminSession = repository.loginService(null, null)

        checkAuditNode()
    }

    @Deactivate
    void deactivate() {
        adminSession?.logout()
    }

    @Synchronized
    private Node addAuditRecordNode(Session session) {
        def date = Calendar.instance
        def year = date.format(DATE_FORMAT_YEAR)
        def month = date.format(DATE_FORMAT_MONTH)
        def day = date.format(DATE_FORMAT_DAY)

        def auditRecordParentNode = JcrUtil.createPath("$AUDIT_PATH/${session.userID}/$year/$month/$day",
            NT_UNSTRUCTURED, adminSession)
        def auditRecordNode = JcrUtil.createUniqueNode(auditRecordParentNode, AUDIT_RECORD_NODE_PREFIX, NT_UNSTRUCTURED,
            adminSession)

        auditRecordNode.addMixin(MIX_CREATED)

        auditRecordNode
    }

    private void checkAuditNode() {
        def contentNode = adminSession.getNode(PATH_CONSOLE_ROOT).getNode(JCR_CONTENT)

        if (!contentNode.hasNode(AUDIT_NODE_NAME)) {
            LOG.info("audit node does not exist, adding")

            contentNode.addNode(AUDIT_NODE_NAME)

            adminSession.save()
        }
    }

    private String getAuditNodePath(Session session) {
        configurationService.displayAllAuditRecords ? AUDIT_PATH : "$AUDIT_PATH/${session.userID}"
    }

    private List<AuditRecord> findAllAuditRecords(String auditNodePath) {
        def auditRecords = []

        if (adminSession.nodeExists(auditNodePath)) {
            def visitor = new AuditRecordNodeVisitor()

            visitor.visit(adminSession.getNode(auditNodePath))

            auditRecords.addAll(visitor.auditRecords)
        }

        auditRecords
    }
}