package com.icfolson.aem.groovy.console.configuration

import org.apache.sling.api.SlingHttpServletRequest

/**
 * Groovy console configuration service.
 */
interface ConfigurationService {

    /**
     * Check if the current user has permission to execute Groovy scripts in the console.
     *
     * @param request current execution request
     * @return true if user has permission
     */
    boolean hasPermission(SlingHttpServletRequest request)

    /**
     * Check if the current user has permission to scheduled jobs in the console.
     *
     * @param request current execution request
     * @return true if user has permission
     */
    boolean hasScheduledJobPermission(SlingHttpServletRequest request)

    /**
     * Get the Groovy Console URL.
     *
     * @return URL to the Groovy Console on the current instance
     */
    String getConsoleHref()

    /**
     * Check if email is enabled.
     *
     * @return true if email is enabled
     */
    boolean isEmailEnabled()

    /**
     * Get the set of configured email recipients for Groovy script notifications.
     *
     * @return set of email addresses
     */
    Set<String> getEmailRecipients()

    /**
     * Check if auditing is disabled.
     *
     * @return true if auditing is disabled
     */
    boolean isAuditDisabled()

    /**
     * Check if all audit records should be displayed in the History panel.  By default,
     * only records for the current user will be displayed.
     *
     * @return if true, display all audit records
     */
    boolean isDisplayAllAuditRecords()
}