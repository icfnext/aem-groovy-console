package com.citytechinc.aem.groovy.console.audit

import com.citytechinc.aem.groovy.console.response.RunScriptResponse

import javax.jcr.RepositoryException

interface AuditService {

    /**
     * Create an audit record for the given script execution response.
     *
     * @param response response containing execution result or exception
     */
    AuditRecord createAuditRecord(RunScriptResponse response)

    /**
     * Delete all audit records.
     *
     * @throws RepositoryException if an error occurs while deleting audit nodes
     */
    void deleteAllAuditRecords() throws RepositoryException

    /**
     * Delete an audit record.
     *
     * @param relativePath relative path to audit record from parent audit node
     * @throws RepositoryException if an error occurs while deleting the audit record node
     */
    void deleteAuditRecord(String relativePath) throws RepositoryException

    /**
     * Get all audit records.
     *
     * @return all audit records
     * @throws RepositoryException if error occurs getting audit records
     */
    List<AuditRecord> getAllAuditRecords() throws RepositoryException

    /**
     * Get the audit record at the given relative path.
     *
     * @param relativePath relative path to audit record from parent audit node
     * @return audit record or null if none exists
     */
    AuditRecord getAuditRecord(String relativePath)

    /**
     * Get a list of audit records for the given date range.
     *
     * @param startDate start date
     * @param endDate end date
     * @return list of audit records in the given date range
     * @throws RepositoryException if error occurs getting audit records
     */
    List<AuditRecord> getAuditRecords(Calendar startDate, Calendar endDate) throws RepositoryException
}
