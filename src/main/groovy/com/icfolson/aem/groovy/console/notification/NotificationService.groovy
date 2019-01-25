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
}
