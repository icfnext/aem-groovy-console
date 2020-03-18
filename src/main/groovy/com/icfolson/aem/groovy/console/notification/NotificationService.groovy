package com.icfolson.aem.groovy.console.notification

import com.icfolson.aem.groovy.console.response.RunScriptResponse

/**
 * Services may implement this interface to provide additional notifications for Groovy Console script executions.
 */
interface NotificationService {

    /**
     * Send a notification for the given script response.
     *
     * @param response script execution response
     */
    void notify(RunScriptResponse response)

    /**
     * Send a notification for given script response and recipients, optionally attaching the script output.
     *
     * @param response script execution response
     * @param recipients email to recipients
     * @param attachOutput if true, attach the script output file
     */
    void notify(RunScriptResponse response, Set<String> recipients, boolean attachOutput)
}
