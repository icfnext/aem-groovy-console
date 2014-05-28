package com.citytechinc.aem.groovy.console.services

import javax.jcr.Session

interface EmailService {

    void sendEmail(Session session, String script, String output, String runningTime, boolean success)
}
