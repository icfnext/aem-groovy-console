package com.citytechinc.aem.groovy.console.services

interface ConfigurationService {

    Set<String> getAllowedGroups()

    String getCrxOutputFolder()

    Set<String> getEmailRecipients()

    boolean isCrxOutputEnabled()

    boolean isEmailEnabled()

    boolean isVanityPathEnabled()
}