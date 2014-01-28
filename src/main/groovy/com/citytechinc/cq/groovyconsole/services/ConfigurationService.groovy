package com.citytechinc.cq.groovyconsole.services

interface ConfigurationService {

    Map<String, String> getResourceResolverAdapters()

    String getCrxOutputFolder()

    String[] getEmailRecipients()

    boolean isCrxOutputEnabled()

    boolean isEmailEnabled()
}