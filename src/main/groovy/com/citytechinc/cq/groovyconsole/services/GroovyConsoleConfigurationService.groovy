package com.citytechinc.cq.groovyconsole.services

interface GroovyConsoleConfigurationService {

    boolean isEmailEnabled()

    String[] getEmailRecipients()

    boolean isCrxOutputEnabled()

    String getCrxOutputFolder()
}