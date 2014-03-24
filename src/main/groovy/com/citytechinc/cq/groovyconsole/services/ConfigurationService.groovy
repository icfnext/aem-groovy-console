package com.citytechinc.cq.groovyconsole.services

interface ConfigurationService {

    String getCrxOutputFolder()

    String[] getEmailRecipients()

    boolean isCrxOutputEnabled()

    boolean isEmailEnabled()

	String getAllowedGroup()
}