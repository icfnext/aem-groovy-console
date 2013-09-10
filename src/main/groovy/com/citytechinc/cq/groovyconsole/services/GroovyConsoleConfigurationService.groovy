package com.citytechinc.cq.groovyconsole.services

interface GroovyConsoleConfigurationService {

    boolean isEmailNotificationEnabled()

    String[] getEmailRecipients()

    boolean isSaveOutputToCRXEnabled()

    String getDefaultOutputFolder()
}