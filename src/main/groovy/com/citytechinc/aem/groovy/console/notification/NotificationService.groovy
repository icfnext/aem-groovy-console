package com.citytechinc.aem.groovy.console.notification

import com.citytechinc.aem.groovy.console.response.RunScriptResponse

import javax.jcr.Session

/**
 * Services may implement this interface to provide additional notifications for Groovy Console script executions.
 */
interface NotificationService {

    /**
     * Send a notification for the given script and response.
     *
     * @param session request session
     * @param response script execution response
     */
    void notify(Session session, RunScriptResponse response)
}
