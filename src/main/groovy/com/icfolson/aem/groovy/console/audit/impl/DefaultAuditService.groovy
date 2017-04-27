package com.icfolson.aem.groovy.console.audit.impl

import com.day.cq.commons.jcr.JcrUtil
import com.icfolson.aem.groovy.console.audit.AuditRecord
import com.icfolson.aem.groovy.console.audit.AuditService
import com.icfolson.aem.groovy.console.response.RunScriptResponse
import groovy.transform.Synchronized
import groovy.util.logging.Slf4j
import org.apache.felix.scr.annotations.Activate
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.Deactivate
import org.apache.felix.scr.annotations.Reference
import org.apache.felix.scr.annotations.Service
import org.apache.sling.jcr.api.SlingRepository
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

    private static final String DATE_FORMAT = "yyyy/MM/dd"

    private static final String DATE_FORMAT_YEAR = "yyyy"

    private static final String DATE_FORMAT_MONTH = "MM"

    private static final String DATE_FORMAT_DAY = "dd"

    @Reference
    private SlingRepository repository

    private Session adminSession

    @Override
    AuditRecord createAuditRecord(Session session, RunScriptResponse response) {
        def auditRecord = null

        try {
            def auditRecordNode = addAuditRecordNode(session)

            auditRecordNode.setProperty(AuditRecord.PROPERTY_SCRIPT, response.script)

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
        }

        auditRecord
    }

    @Override
    void deleteAllAuditRecords(Session session) throws RepositoryException {
        try {
            if (adminSession.nodeExists("$AUDIT_PATH/${session.userID}")) {
                adminSession.getNode("$AUDIT_PATH/${session.userID}").nodes*.remove()

                LOG.debug("deleted all audit record nodes for user = {}", session.userID)

                adminSession.save()
            }
        } catch (RepositoryException e) {
            LOG.error("error deleting audit records for user = ${session.userID}", e)

            throw e
        }
    }

    @Override
    void deleteAuditRecord(Session session, String relativePath) {
        try {
            adminSession.getNode("$AUDIT_PATH/${session.userID}").getNode(relativePath).remove()

            LOG.debug("deleted audit record at relative path = {}", relativePath)

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
            if (adminSession.nodeExists("$AUDIT_PATH/${session.userID}")) {
                adminSession.getNode("$AUDIT_PATH/${session.userID}").recurse { Node node ->
                    if (node.name.startsWith(AUDIT_RECORD_NODE_PREFIX)) {
                        auditRecords.add(new AuditRecord(node))
                    }
                }
            }
        } catch (RepositoryException e) {
            LOG.error("error getting audit records", e)

            throw e
        }

        auditRecords
    }

    @Override
    AuditRecord getAuditRecord(Session session, String relativePath) {
        def auditRecord = null

        try {
            def auditNode = adminSession.getNode("$AUDIT_PATH/${session.userID}")

            if (auditNode.hasNode(relativePath)) {
                def auditRecordNode = auditNode.getNode(relativePath)

                auditRecord = new AuditRecord(auditRecordNode)

                LOG.debug("found audit record = {}", auditRecord)
            }
        } catch (RepositoryException e) {
            LOG.error("error getting audit record", e)
        }

        auditRecord
    }

    @Override
    List<AuditRecord> getAuditRecords(Session session, Calendar startDate,
        Calendar endDate) throws RepositoryException {
        def auditRecords = []

        try {
            def auditNode = adminSession.getNode("$AUDIT_PATH/${session.userID}")

            def currentDate = startDate

            while (!currentDate.after(endDate)) {
                def currentDateRelativePath = startDate.format(DATE_FORMAT)

                if (auditNode.hasNode(currentDateRelativePath)) {
                    def currentDateNode = auditNode.getNode(currentDateRelativePath)

                    currentDateNode.each { Node node ->
                        LOG.debug("found audit record for node = {}", node.path)

                        auditRecords.add(new AuditRecord(node))
                    }
                }

                currentDate.add(Calendar.DAY_OF_MONTH, 1)
            }
        } catch (RepositoryException e) {
            LOG.error("error getting audit records for date range", e)

            throw e
        }

        auditRecords
    }

    @Activate
    void activate() {
        //ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(null)
        //adminSession = resourceResolver.adaptTo(Session.class)
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
}
