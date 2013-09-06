package com.citytechinc.cq.groovyconsole.services.impl

import com.citytechinc.cq.groovyconsole.services.GroovyConsoleConfigurationService
import org.apache.felix.scr.annotations.*
import org.slf4j.LoggerFactory

@Service
@Component(name = "GroovyConsoleService", label = "Groovy Console configuration", immediate = true, metatype = true)
class GroovyConsoleConfigurationServiceImpl implements GroovyConsoleConfigurationService {

    private static final def LOGGER = LoggerFactory.getLogger(GroovyConsoleConfigurationServiceImpl)

    @Property(label = "Enable Email Notification",
            description = "Option to enable/disable Email Notification",
            boolValue = true)
    private static final String EMAIL_NOTIFICATION_ENABLED = "email.enable";

    @Property(label = "Email Recipients",
            description = "Set email recipients addresses",
            cardinality = 20)
    private static final String EMAIL_RECIPIENTS = "email.recipients";

    @Property(label = "Enable Saving Script Output to CRX",
            description = "Option to enable/disable saving script output to CRX",
            boolValue = true)
    private static final String SAVE_OUTPUT_TO_CRX_ENABLED = "outputToCRX.enable";

    @Property(label = "Default Output Folder",
            description = "Set default folder where groovy scripts' output will be stored")
    private static final String DEFAULT_OUTPUT_FODLER = "outputToCRX.defaultFolder";

    private Boolean enableEmailNotification;

    private Boolean saveOutputToCRX;

    private String[] emailRecipients;

    private String defaultOutputFolder;

    @Override
    def isEmailNotificationEnabled() {
        return enableEmailNotification
    }

    @Override
    String[] getEmailRecipients() {
        return emailRecipients
    }

    @Override
    def isSaveOutputToCRXEnabled() {
        return saveOutputToCRX
    }

    @Override
    def getDefaultOutputFolder() {
        return defaultOutputFolder
    }

    @Activate
    @Modified
    synchronized void configure(final Map<String, Object> props) {
        if (props != null) {
            this.enableEmailNotification = props.get(EMAIL_NOTIFICATION_ENABLED) ?: false;
            this.emailRecipients = props.get(EMAIL_RECIPIENTS) ?: [];
            this.saveOutputToCRX = props.get(SAVE_OUTPUT_TO_CRX_ENABLED) ?: false;
            this.defaultOutputFolder = props.get(DEFAULT_OUTPUT_FODLER) ?: "tmp/groovyconsole";
        }
    }
}
