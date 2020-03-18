package com.icfolson.aem.groovy.console.notification

import com.icfolson.aem.groovy.console.response.RunScriptResponse

/**
 * Services may implement this interface to send email notifications for Groovy Console script executions.
 */
interface EmailNotificationService extends NotificationService {

    /**
     * Send an email notification for given script response and recipients, optionally attaching the script output.
     *
     * @param response script execution response
     * @param recipients email to recipients
     * @param attachOutput if true, attach the script output file
     */
    void notify(RunScriptResponse response, Set<String> recipients, boolean attachOutput)
}
