package com.citytechinc.cq.groovyconsole.services

interface GroovyConsoleConfigurationService {

    def isEmailNotificationEnabled()

    String[] getEmailRecipients()

    def isSaveOutputToCRXEnabled()

    def getDefaultOutputFolder()
}