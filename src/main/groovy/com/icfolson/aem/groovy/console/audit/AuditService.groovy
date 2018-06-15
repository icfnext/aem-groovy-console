package com.icfolson.aem.groovy.console.audit

import com.icfolson.aem.groovy.console.response.RunScriptResponse
import org.apache.sling.api.resource.PersistenceException

import javax.jcr.RepositoryException
import javax.jcr.Session

interface AuditService {

    /**
     * Create an audit record for the given script execution response.
     *
     * @param session request session for the user executing the script
     * @param response response containing execution result or exception
     * @throws RepositoryException if error occurs creating audit record
     * @throws PersistenceException if error occurs creating audit record
     */
    AuditRecord createAuditRecord(Session session,
        RunScriptResponse response) throws RepositoryException, PersistenceException

    /**
     * Delete all audit records.
     *
     * @param session request session, only audit records for the current user will be deleted
     * @throws PersistenceException if an error occurs while deleting audit resources
     */
    void deleteAllAuditRecords(Session session) throws PersistenceException

    /**
     * Delete an audit record.
     *
     * @param session request session, only audit records for the current user will be deleted
     * @param userId user that owns the audit record
     * @param relativePath relative path to audit record from parent audit resource
     * @throws PersistenceException if an error occurs while deleting the audit record resource
     */
    void deleteAuditRecord(Session session, String userId, String relativePath) throws PersistenceException

    /**
     * Get all audit records.
     *
     * @param session request session, only audit records for the current user will be retrieved
     * @return all audit records
     */
    List<AuditRecord> getAllAuditRecords(Session session)

    /**
     * Get the audit record at the given relative path.
     *
     * @param session request session, only audit records for the current user will be retrieved
     * @param userId user that owns the audit record
     * @param relativePath relative path to audit record from parent audit node
     * @return audit record or null if none exists
     */
    AuditRecord getAuditRecord(Session session, String userId, String relativePath)

    /**
     * Get a list of audit records for the given date range.
     *
     * @param session request session, only audit records for the current user will be retrieved
     * @param startDate start date
     * @param endDate end date
     * @return list of audit records in the given date range
     */
    List<AuditRecord> getAuditRecords(Session session, Calendar startDate, Calendar endDate)
}
