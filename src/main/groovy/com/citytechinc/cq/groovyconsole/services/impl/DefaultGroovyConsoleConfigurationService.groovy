package com.citytechinc.cq.groovyconsole.services.impl

import com.citytechinc.cq.groovyconsole.services.GroovyConsoleConfigurationService
import org.apache.felix.scr.annotations.Activate
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.Modified
import org.apache.felix.scr.annotations.Property
import org.apache.felix.scr.annotations.Service

@Service
@Component(immediate = true, metatype = true)
class DefaultGroovyConsoleConfigurationService implements GroovyConsoleConfigurationService {

    @Property(label = "Email Enabled?",
        description = "Check to enable email notification on completion of script execution.", boolValue = true)
    static final String EMAIL_ENABLED = "email.enabled"

    @Property(label = "Email Recipients",
        description = "Email addresses to receive notification.", cardinality = 20)
    static final String EMAIL_RECIPIENTS = "email.recipients"

    @Property(label = "Save Script Output to CRX Enabled?",
        description = "Check to enable saving script output to CRX.", boolValue = true)
    static final String CRX_OUTPUT_ENABLED = "crx.output.enabled"

    @Property(label = "Script Output Folder",
        description = "CRX path to root folder for script output.  Will be created if it does not exist.")
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
        emailEnabled = properties.get(EMAIL_ENABLED) ?: false;
        emailRecipients = properties.get(EMAIL_RECIPIENTS) ?: [];
        crxOutputEnabled = properties.get(CRX_OUTPUT_ENABLED) ?: false;
        crxOutputFolder = properties.get(CRX_OUTPUT_FOLDER) ?: "/tmp/groovyconsole";
    }
}
