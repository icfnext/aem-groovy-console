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

    static final String DEFAULT_PATH = "/etc/groovyconsole.html"

    static final String VANITY_PATH = "/groovyconsole"

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

    @Property(label = "Allowed Groups",
        description = "List of group names that are authorized to use the console.  If empty, no authorization check is performed.",
        cardinality = 20)
    static final String ALLOWED_GROUPS = "groups.allowed"

    @Property(label = "Vanity Path Enabled?",
        description = "Enables /groovyconsole vanity path.  Apache Sling Resource Resolver Factory OSGi configuration must also be updated to allow vanity paths from /etc (resource.resolver.vanitypath.whitelist).", boolValue = false)
    static final String VANITY_PATH_ENABLED = "vanity.path.enabled"

    @Property(label = "Audit Enabled?", description = "Enables auditing to maintain history of script executions.",
        boolValue = true)
    static final String AUDIT_ENABLED = "audit.enabled"

    def emailEnabled

    def emailRecipients

    def crxOutputEnabled

    def crxOutputFolder

    def allowedGroups

    def vanityPathEnabled

    def auditEnabled

    @Override
    Set<String> getAllowedGroups() {
        allowedGroups
    }

    @Override
    String getConsoleHref() {
        vanityPathEnabled ? VANITY_PATH : DEFAULT_PATH
    }

    @Override
    boolean isEmailEnabled() {
        emailEnabled
    }

    @Override
    Set<String> getEmailRecipients() {
        emailRecipients
    }

    @Override
    boolean isAuditEnabled() {
        auditEnabled
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
    synchronized void modified(Map<String, Object> properties) {
        emailEnabled = properties.get(EMAIL_ENABLED) ?: false
        emailRecipients = (properties.get(EMAIL_RECIPIENTS) ?: []).findAll() as Set
        crxOutputEnabled = properties.get(CRX_OUTPUT_ENABLED) ?: false
        crxOutputFolder = properties.get(CRX_OUTPUT_FOLDER) ?: DEFAULT_CRX_OUTPUT_FOLDER
        allowedGroups = (properties.get(ALLOWED_GROUPS) ?: []).findAll() as Set
        vanityPathEnabled = properties.get(VANITY_PATH_ENABLED) ?: false
        auditEnabled = properties.get(AUDIT_ENABLED) ?: false
    }
}
