package com.citytechinc.cq.groovyconsole.services

interface ConfigurationService {

    Set<String> getAllowedGroups()

    String getCrxOutputFolder()

    Set<String> getEmailRecipients()

    boolean isCrxOutputEnabled()

    boolean isEmailEnabled()
}