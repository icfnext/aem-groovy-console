package com.icfolson.aem.groovy.console.audit

import com.icfolson.aem.groovy.console.response.RunScriptResponse

import javax.jcr.RepositoryException
import javax.jcr.Session

interface AuditService {

    /**
     * Create an audit record for the given script execution response.
     *
     * @param session request session for the user executing the script
     * @param response response containing execution result or exception
     */
    AuditRecord createAuditRecord(Session session, RunScriptResponse response)

    /**
     * Delete all audit records.
     *
     * @param session request session, only audit records for the current user will be deleted
     * @throws RepositoryException if an error occurs while deleting audit nodes
     */
    void deleteAllAuditRecords(Session session) throws RepositoryException

    /**
     * Delete an audit record.
     *
     * @param session request session, only audit records for the current user will be deleted
     * @param relativePath relative path to audit record from parent audit node
     * @throws RepositoryException if an error occurs while deleting the audit record node
     */
    void deleteAuditRecord(Session session, String relativePath) throws RepositoryException

    /**
     * Get all audit records.
     *
     * @param session request session, only audit records for the current user will be retrieved
     * @return all audit records
     * @throws RepositoryException if error occurs getting audit records
     */
    List<AuditRecord> getAllAuditRecords(Session session) throws RepositoryException

    /**
     * Get the audit record at the given relative path.
     *
     * @param session request session, only audit records for the current user will be retrieved
     * @param relativePath relative path to audit record from parent audit node
     * @return audit record or null if none exists
     */
    AuditRecord getAuditRecord(Session session, String relativePath)

    /**
     * Get a list of audit records for the given date range.
     *
     * @param session request session, only audit records for the current user will be retrieved
     * @param startDate start date
     * @param endDate end date
     * @return list of audit records in the given date range
     * @throws RepositoryException if error occurs getting audit records
     */
    List<AuditRecord> getAuditRecords(Session session, Calendar startDate, Calendar endDate) throws RepositoryException
}
