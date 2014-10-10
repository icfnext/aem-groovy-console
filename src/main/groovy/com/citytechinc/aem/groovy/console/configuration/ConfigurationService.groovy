package com.citytechinc.aem.groovy.console.configuration

interface ConfigurationService {

    Set<String> getAllowedGroups()

    String getConsoleHref()

    String getCrxOutputFolder()

    Set<String> getEmailRecipients()

    boolean isAuditEnabled()

    boolean isCrxOutputEnabled()

    boolean isEmailEnabled()
}