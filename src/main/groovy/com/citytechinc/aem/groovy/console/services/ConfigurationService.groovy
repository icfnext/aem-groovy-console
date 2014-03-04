package com.citytechinc.aem.groovy.console.services

interface ConfigurationService {

    String getCrxOutputFolder()

    String[] getEmailRecipients()

    boolean isCrxOutputEnabled()

    boolean isEmailEnabled()
}