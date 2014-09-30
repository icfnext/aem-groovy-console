package com.citytechinc.aem.groovy.console.services.audit.impl

import com.citytechinc.aem.groovy.console.services.audit.AuditRecord
import com.citytechinc.aem.groovy.console.services.audit.AuditService
import groovy.util.logging.Slf4j
import org.apache.commons.lang3.exception.ExceptionUtils
import org.apache.felix.scr.annotations.Activate
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.Deactivate
import org.apache.felix.scr.annotations.Reference
import org.apache.felix.scr.annotations.Service
import org.apache.sling.jcr.api.SlingRepository

import javax.jcr.Node
import javax.jcr.RepositoryException
import javax.jcr.Session

import static com.citytechinc.aem.groovy.console.constants.GroovyConsoleConstants.PATH_CONSOLE_ROOT
import static com.day.cq.commons.jcr.JcrConstants.JCR_CONTENT
import static com.day.cq.commons.jcr.JcrConstants.MIX_CREATED

@Component(immediate = true)
@Service(AuditService)
@Slf4j("LOG")
class DefaultAuditService implements AuditService {

    private static final String DATE_FORMAT = "yyyy/MM/dd"

    private static final String NODE_NAME_AUDIT = "audit"

    private static final String NODE_NAME_PREFIX_RECORD = "record"

    private static final String PATH_AUDIT = "$PATH_CONSOLE_ROOT/$JCR_CONTENT/$NODE_NAME_AUDIT"

    private static final String DATE_FORMAT_YEAR = "yyyy"

    private static final String DATE_FORMAT_MONTH = "MM"

    private static final String DATE_FORMAT_DAY = "dd"

    @Reference
    SlingRepository repository

    private Session session

    @Override
    AuditRecord createAuditRecord(String script, String result, String output) {
        createAuditRecordInternal(script, { auditRecordNode ->
            auditRecordNode.set(AuditRecord.PROPERTY_RESULT, result)
            auditRecordNode.set(AuditRecord.PROPERTY_OUTPUT, output)
        })
    }

    @Override
    AuditRecord createAuditRecord(String script, Throwable throwable) {
        createAuditRecordInternal(script, { auditRecordNode ->
            auditRecordNode.set(AuditRecord.PROPERTY_EXCEPTION_STACK_TRACE, ExceptionUtils.getStackTrace(throwable))
        })
    }

    @Override
    List<AuditRecord> getAllAuditRecords() throws RepositoryException {
        def auditRecords = []

        try {
            def auditNode = session.getNode(PATH_AUDIT)

            auditNode.recurse { Node node ->
                if (node.name.startsWith(NODE_NAME_PREFIX_RECORD)) {
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
    List<AuditRecord> getAuditRecords(Calendar startDate, Calendar endDate) throws RepositoryException {
        def auditRecords = []

        try {
            def auditNode = session.getNode(PATH_AUDIT)

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

    private AuditRecord createAuditRecordInternal(String script, Closure closure) {
        def auditRecord = null

        try {
            def auditRecordNode = addAuditRecordNode()

            auditRecordNode.set(AuditRecord.PROPERTY_SCRIPT, script)

            closure(auditRecordNode)

            session.save()

            auditRecord = new AuditRecord(auditRecordNode)

            LOG.info "created audit record = {}", auditRecord
        } catch (RepositoryException e) {
            LOG.error "error creating audit record", e
        }

        auditRecord
    }

    private Node addAuditRecordNode() {
        def auditNode = session.getNode(PATH_AUDIT)

        def date = Calendar.instance

        def yearNode = auditNode.getOrAddNode(date.format(DATE_FORMAT_YEAR)) as Node
        def monthNode = yearNode.getOrAddNode(date.format(DATE_FORMAT_MONTH)) as Node
        def dayNode = monthNode.getOrAddNode(date.format(DATE_FORMAT_DAY)) as Node

        def index = dayNode.nodes.size() as String

        def auditRecordNode = dayNode.addNode(NODE_NAME_PREFIX_RECORD + index)

        auditRecordNode.addMixin(MIX_CREATED)

        auditRecordNode
    }

    private void checkAuditNode() {
        def contentNode = session.getNode(PATH_CONSOLE_ROOT).getNode(JCR_CONTENT)

        if (!contentNode.hasNode(NODE_NAME_AUDIT)) {
            LOG.info "audit node does not exist, adding"

            contentNode.addNode(NODE_NAME_AUDIT)

            session.save()
        }
    }
}
