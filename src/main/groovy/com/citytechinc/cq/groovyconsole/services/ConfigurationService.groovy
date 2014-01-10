package com.citytechinc.cq.groovyconsole.services

interface ConfigurationService {

    boolean isEmailEnabled()

    String[] getEmailRecipients()

    boolean isCrxOutputEnabled()

    String getCrxOutputFolder()
}