package com.citytechinc.aem.groovy.console.services.impl

import com.citytechinc.aem.groovy.console.services.ConfigurationService
import groovy.util.logging.Slf4j
import org.apache.felix.scr.annotations.Activate
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.Modified
import org.apache.felix.scr.annotations.Property
import org.apache.felix.scr.annotations.Service

@Service(ConfigurationService)
@Component(immediate = true, metatype = true, label = "Groovy Console Configuration Service")
@Slf4j("LOG")
class DefaultConfigurationService implements ConfigurationService {

    static final String DEFAULT_CRX_OUTPUT_FOLDER = "/tmp/groovyconsole"

    @Property(label = "Email Enabled?",
        description = "Check to enable email notification on completion of script execution.",
        boolValue = false)
    static final String EMAIL_ENABLED = "email.enabled"

    @Property(label = "Email Recipients",
        description = "Email addresses to receive notification.", cardinality = 20)
    static final String EMAIL_RECIPIENTS = "email.recipients"

    @Property(label = "Save Script Output to CRX Enabled?",
        description = "Check to enable saving script output to CRX.", boolValue = false)
    static final String CRX_OUTPUT_ENABLED = "crx.output.enabled"

    @Property(label = "Script Output Folder",
        description = "CRX path to root folder for script output.  Will be created if it does not exist.",
        value = "/tmp/groovyconsole")
    static final String CRX_OUTPUT_FOLDER = "crx.output.folder"

    def emailEnabled

    def emailRecipients

    def crxOutputEnabled

    def crxOutputFolder

    @Override
    boolean isEmailEnabled() {
        emailEnabled
    }

    @Override
    String[] getEmailRecipients() {
        emailRecipients
    }

    @Override
    boolean isCrxOutputEnabled() {
        crxOutputEnabled
    }

    @Override
    String getCrxOutputFolder() {
        crxOutputFolder
    }

    @Activate
    @Modified
    synchronized void modified(final Map<String, Object> properties) {
        emailEnabled = properties.get(EMAIL_ENABLED) ?: false
        emailRecipients = properties.get(EMAIL_RECIPIENTS) ?: []
        crxOutputEnabled = properties.get(CRX_OUTPUT_ENABLED) ?: false
        crxOutputFolder = properties.get(CRX_OUTPUT_FOLDER) ?: DEFAULT_CRX_OUTPUT_FOLDER
    }
}
