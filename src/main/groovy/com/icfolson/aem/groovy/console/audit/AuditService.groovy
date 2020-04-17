package com.icfolson.aem.groovy.console.audit

import com.icfolson.aem.groovy.console.response.RunScriptResponse

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
     * @param userId user that owns the audit records
     */
    void deleteAllAuditRecords(String userId)

    /**
     * Delete an audit record.
     *
     * @param userId user that owns the audit record
     * @param relativePath relative path to audit record from parent audit resource
     */
    void deleteAuditRecord(String userId, String relativePath)

    /**
     * Get all audit records.
     *
     * @param userId user that owns the audit records
     * @return all audit records
     */
    List<AuditRecord> getAllAuditRecords(String userId)

    /**
     * Get the audit record for the given job ID.
     *
     * @param jobId Sling-generated ID for the job
     * @return audit record or null if not found
     */
    AuditRecord getAuditRecord(String jobId)

    /**
     * Get the audit record at the given relative path.
     *
     * @param userId user that owns the audit record
     * @param relativePath relative path to audit record from parent audit node
     * @return audit record or null if none exists
     */
    AuditRecord getAuditRecord(String userId, String relativePath)

    /**
     * Get a list of audit records for the given date range.
     *
     * @param userId user that owns the audit records
     * @param startDate start date
     * @param endDate end date
     * @return list of audit records in the given date range
     */
    List<AuditRecord> getAuditRecords(String userId, Calendar startDate, Calendar endDate)
}
