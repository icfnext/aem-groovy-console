package com.citytechinc.cq.groovyconsole.services

interface ConfigurationService {

    String getCrxOutputFolder()

    String[] getEmailRecipients()

    Map<String, String> getAdapters()

    Map<String, String> getServices()

    boolean isCrxOutputEnabled()

    boolean isEmailEnabled()
}