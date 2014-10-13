package com.citytechinc.aem.groovy.console.audit.impl

import com.citytechinc.aem.groovy.console.audit.AuditRecord
import com.citytechinc.aem.groovy.console.audit.AuditService
import com.citytechinc.aem.groovy.console.response.RunScriptResponse
import com.day.cq.commons.jcr.JcrUtil
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

import static com.citytechinc.aem.groovy.console.constants.GroovyConsoleConstants.AUDIT_NODE_NAME
import static com.citytechinc.aem.groovy.console.constants.GroovyConsoleConstants.AUDIT_PATH
import static com.citytechinc.aem.groovy.console.constants.GroovyConsoleConstants.AUDIT_RECORD_NODE_PREFIX
import static com.citytechinc.aem.groovy.console.constants.GroovyConsoleConstants.PATH_CONSOLE_ROOT
import static com.day.cq.commons.jcr.JcrConstants.JCR_CONTENT
import static com.day.cq.commons.jcr.JcrConstants.MIX_CREATED
import static com.day.cq.commons.jcr.JcrConstants.NT_UNSTRUCTURED

@Component(immediate = true)
@Service(AuditService)
@Slf4j("LOG")
class DefaultAuditService implements AuditService {

    private static final String DATE_FORMAT = "yyyy/MM/dd"

    private static final String DATE_FORMAT_YEAR = "yyyy"

    private static final String DATE_FORMAT_MONTH = "MM"

    private static final String DATE_FORMAT_DAY = "dd"

    @Reference
    SlingRepository repository

    private Session session

    @Override
    AuditRecord createAuditRecord(String script, RunScriptResponse response) {
        def auditRecord = null

        try {
            def auditRecordNode = addAuditRecordNode()

            auditRecordNode.set(AuditRecord.PROPERTY_SCRIPT, script)

            if (response.exceptionStackTrace) {
                auditRecordNode.set(AuditRecord.PROPERTY_EXCEPTION_STACK_TRACE, response.exceptionStackTrace)
            } else {
                auditRecordNode.set(AuditRecord.PROPERTY_RESULT, response.result)
                auditRecordNode.set(AuditRecord.PROPERTY_OUTPUT, response.output)
                auditRecordNode.set(AuditRecord.PROPERTY_RUNNING_TIME, response.runningTime)
            }

            session.save()

            auditRecord = new AuditRecord(auditRecordNode)

            LOG.info "created audit record = {}", auditRecord
        } catch (RepositoryException e) {
            LOG.error "error creating audit record", e
        }

        auditRecord
    }

    @Override
    void deleteAllAuditRecords() throws RepositoryException {
        try {
            def auditNode = session.getNode(AUDIT_PATH)

            auditNode.nodes*.remove()

            LOG.info "deleted all audit record nodes"

            session.save()
        } catch (RepositoryException e) {
            LOG.error "error deleting audit records", e

            throw e
        }
    }

    @Override
    void deleteAuditRecord(String relativePath) {
        try {
            session.getNode(AUDIT_PATH).getNode(relativePath).remove()

            LOG.info "deleted audit record at relative path = {}", relativePath

            session.save()
        } catch (RepositoryException e) {
            LOG.error "error deleting audit record", e

            throw e
        }
    }

    @Override
    List<AuditRecord> getAllAuditRecords() throws RepositoryException {
        def auditRecords = []

        try {
            def auditNode = session.getNode(AUDIT_PATH)

            auditNode.recurse { Node node ->
                if (node.name.startsWith(AUDIT_RECORD_NODE_PREFIX)) {
                    auditRecords.add(new AuditRecord(node))
                }
            }
        } catch (RepositoryException e) {
            LOG.error "error getting audit records", e

            throw e
        }

        auditRecords
    }

    @Override
    AuditRecord getAuditRecord(String relativePath) {
        def auditRecord = null

        try {
            def auditNode = session.getNode(AUDIT_PATH)

            if (auditNode.hasNode(relativePath)) {
                def auditRecordNode = auditNode.getNode(relativePath)

                auditRecord = new AuditRecord(auditRecordNode)

                LOG.info "found audit record = {}", auditRecord
            }
        } catch (RepositoryException e) {
            LOG.error "error getting audit record", e
        }

        auditRecord
    }

    @Override
    List<AuditRecord> getAuditRecords(Calendar startDate, Calendar endDate) throws RepositoryException {
        def auditRecords = []

        try {
            def auditNode = session.getNode(AUDIT_PATH)

            def currentDate = startDate

            while (!currentDate.after(endDate)) {
                def currentDateRelativePath = startDate.format(DATE_FORMAT)

                if (auditNode.hasNode(currentDateRelativePath)) {
                    def currentDateNode = auditNode.getNode(currentDateRelativePath)

                    currentDateNode.each { Node node ->
                        LOG.debug "found audit record for node = {}", node.path

                        auditRecords.add(new AuditRecord(node))
                    }
                }

                currentDate.add(Calendar.DAY_OF_MONTH, 1)
            }
        } catch (RepositoryException e) {
            LOG.error "error getting audit records for date range", e

            throw e
        }

        auditRecords
    }

    @Activate
    @SuppressWarnings("deprecated")
    void activate() {
        session = repository.loginAdministrative(null)

        checkAuditNode()
    }

    @Deactivate
    void deactivate() {
        session?.logout()
    }

    private Node addAuditRecordNode() {
        def auditNode = session.getNode(AUDIT_PATH)

        def date = Calendar.instance

        def yearNode = auditNode.getOrAddNode(date.format(DATE_FORMAT_YEAR)) as Node
        def monthNode = yearNode.getOrAddNode(date.format(DATE_FORMAT_MONTH)) as Node
        def dayNode = monthNode.getOrAddNode(date.format(DATE_FORMAT_DAY)) as Node

        def auditRecordNode = JcrUtil.createUniqueNode(dayNode, AUDIT_RECORD_NODE_PREFIX, NT_UNSTRUCTURED, session)

        auditRecordNode.addMixin(MIX_CREATED)

        auditRecordNode
    }

    private void checkAuditNode() {
        def contentNode = session.getNode(PATH_CONSOLE_ROOT).getNode(JCR_CONTENT)

        if (!contentNode.hasNode(AUDIT_NODE_NAME)) {
            LOG.info "audit node does not exist, adding"

            contentNode.addNode(AUDIT_NODE_NAME)

            session.save()
        }
    }
}
