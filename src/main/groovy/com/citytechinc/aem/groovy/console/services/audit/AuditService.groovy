package com.citytechinc.aem.groovy.console.services.audit

import com.citytechinc.aem.groovy.console.response.RunScriptResponse

import javax.jcr.RepositoryException

interface AuditService {

    /**
     * Create an audit record for the given script and response.
     *
     * @param script
     * @param response response containing execution result or exception
     */
    AuditRecord createAuditRecord(String script, RunScriptResponse response)

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
