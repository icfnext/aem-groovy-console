package com.citytechinc.aem.groovy.console.configuration

interface ConfigurationService {

    Set<String> getAllowedGroups()

    String getConsoleHref()

    Set<String> getEmailRecipients()

    boolean isAuditDisabled()

    boolean isEmailEnabled()
}