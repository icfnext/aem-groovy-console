package com.citytechinc.aem.groovy.console.services

import javax.jcr.Session

interface EmailService {

    void sendEmail(Session session, String script, String result, String output, String runningTime)

    void sendEmail(Session session, String script, String stackTrace)
}
