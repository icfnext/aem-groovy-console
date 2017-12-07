package com.icfolson.aem.groovy.console.configuration

import org.apache.sling.api.SlingHttpServletRequest

interface ConfigurationService {

    boolean hasPermission(SlingHttpServletRequest request)

    String getConsoleHref()

    Set<String> getEmailRecipients()

    boolean isAuditDisabled()

    boolean isEmailEnabled()

    boolean isDisplayAllAuditRecords()
}