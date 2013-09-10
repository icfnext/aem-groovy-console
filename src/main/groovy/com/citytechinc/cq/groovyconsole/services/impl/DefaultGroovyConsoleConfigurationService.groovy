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

    @Property(label = "Enable Email Notification",
        description = "Option to enable/disable Email Notification",
        boolValue = true)
    static final String EMAIL_NOTIFICATION_ENABLED = "email.enabled";

    @Property(label = "Email Recipients",
        description = "Set email recipients addresses",
        cardinality = 20)
    static final String EMAIL_RECIPIENTS = "email.recipients";

    @Property(label = "Enable Saving Script Output to CRX",
        description = "Option to enable/disable saving script output to CRX",
        boolValue = true)
    static final String SAVE_OUTPUT_TO_CRX_ENABLED = "crx.output.enabled";

    @Property(label = "Default Output Folder",
        description = "Set default folder where groovy scripts' output will be stored")
    static final String DEFAULT_OUTPUT_FOLDER = "crx.output.folder";

    def enableEmailNotification

    def saveOutputToCRX

    def emailRecipients

    def defaultOutputFolder

    @Override
    boolean isEmailNotificationEnabled() {
        enableEmailNotification
    }

    @Override
    String[] getEmailRecipients() {
        emailRecipients
    }

    @Override
    boolean isSaveOutputToCRXEnabled() {
        saveOutputToCRX
    }

    @Override
    String getDefaultOutputFolder() {
        defaultOutputFolder
    }

    @Activate
    @Modified
    synchronized void modified(final Map<String, Object> properties) {
        enableEmailNotification = properties.get(EMAIL_NOTIFICATION_ENABLED) ?: false;
        emailRecipients = properties.get(EMAIL_RECIPIENTS) ?: [];
        saveOutputToCRX = properties.get(SAVE_OUTPUT_TO_CRX_ENABLED) ?: false;
        defaultOutputFolder = properties.get(DEFAULT_OUTPUT_FOLDER) ?: "tmp/groovyconsole";
    }
}
