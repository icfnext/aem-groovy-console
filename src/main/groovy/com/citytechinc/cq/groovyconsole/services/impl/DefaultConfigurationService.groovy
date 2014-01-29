package com.citytechinc.cq.groovyconsole.services.impl

import com.citytechinc.cq.groovyconsole.services.ConfigurationService
import groovy.util.logging.Slf4j
import org.apache.commons.lang3.StringUtils
import org.apache.felix.scr.annotations.Activate
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.Modified
import org.apache.felix.scr.annotations.Property
import org.apache.felix.scr.annotations.Service

@Service
@Component(immediate = true, metatype = true, label = "Groovy Console Configuration Service")
@Slf4j("LOG")
class DefaultConfigurationService implements ConfigurationService {

    static final String DEFAULT_CRX_OUTPUT_FOLDER = "/tmp/groovyconsole"

    static final def DEFAULT_ADAPTERS = ["com.adobe.cq.social.blog.BlogManager",
        "com.adobe.granite.xss.XSSAPI",
        "com.day.cq.search.SimpleSearch",
        "com.day.cq.tagging.TagManager",
        "com.adobe.cq.launches.api.LaunchManager",
        "com.day.cq.wcm.msm.api.BlueprintManager",
        "com.adobe.granite.asset.api.AssetManager",
        "com.adobe.cq.social.calendar.CalendarManager",
        "com.adobe.cq.social.journal.JournalManager",
        "org.apache.jackrabbit.api.security.user.UserManager",
        "com.day.cq.wcm.api.components.ComponentManager",
        "com.day.cq.wcm.api.designer.Designer",
        "com.adobe.granite.workflow.WorkflowSession"]

    static final def DEFAULT_SERVICES = ["com.citytechinc.cq.groovy.extension.services.OsgiComponentService",
        "com.day.cq.commons.Externalizer",
        "com.day.cq.dam.api.DamManager",
        "com.day.cq.replication.AgentManager",
        "com.day.cq.replication.Replicator",
        "com.day.cq.rewriter.linkchecker.ExternalLinkChecker",
        "com.day.cq.wcm.api.LanguageManager",
        "com.day.cq.wcm.msm.api.RolloutManager",
        "com.day.cq.widget.HtmlLibraryManager",
        "com.day.cq.workflow.WorkflowService",
        "org.apache.felix.scr.ScrService",
        "org.apache.sling.commons.mime.MimeTypeService",
        "org.apache.sling.event.jobs.JobManager",
        "org.apache.sling.jcr.api.SlingRepository",
        "org.apache.sling.settings.SlingSettingsService",
        "org.osgi.service.cm.ConfigurationAdmin"]

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

    @Property(label = "Resource Resolver Adapters",
        description = "Class names for additional Sling Resource Resolver adapters to populate Adapters menu.", cardinality = Integer.MAX_VALUE)
    static final String RESOURCE_RESOLVER_ADAPTERS = "resource.resolver.adapters"

    @Property(label = "OSGi Services",
        description = "Class names for additional OSGi services to populate Services menu.", cardinality = Integer.MAX_VALUE)
    static final String OSGI_SERVICES = "osgi.services"

    def emailEnabled

    def emailRecipients

    def crxOutputEnabled

    def crxOutputFolder

    def resourceResolverAdapters

    def osgiServices

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
    Map<String, String> getAdapters() {
        def adapters = [:]

        loadClassMapping(DEFAULT_ADAPTERS, resourceResolverAdapters).collect { mapping ->
            def className = mapping.className

            adapters[className] = [import: "import $className",
                declaration: "def ${mapping.variableName} = resourceResolver.adaptTo(${mapping.simpleName})"]
        }

        adapters
    }

    @Override
    Map<String, String> getServices() {
        def services = [:]

        loadClassMapping(DEFAULT_SERVICES, osgiServices).collect { mapping ->
            def className = mapping.className

            services[className] = [import: "import $className",
                declaration: "def ${mapping.variableName} = getService(${mapping.simpleName})"]
        }

        services
    }

    @Override
    String getCrxOutputFolder() {
        crxOutputFolder
    }

    static def loadClassMapping(defaultClassNames, additionalClassNames) {
        def mapping = []

        def classNames = defaultClassNames + (additionalClassNames as List)

        classNames.sort().each { className ->
            try {
                def clazz = Class.forName(className)
                def simpleName = clazz.simpleName
                def variableName = StringUtils.uncapitalize(simpleName)

                mapping.add([className: className, variableName: variableName, simpleName: simpleName])
            } catch (e) {
                LOG.error "error getting class for name = $className", e
            }
        }

        mapping
    }

    @Activate
    @Modified
    synchronized void modified(final Map<String, Object> properties) {
        emailEnabled = properties.get(EMAIL_ENABLED) ?: false
        emailRecipients = properties.get(EMAIL_RECIPIENTS) ?: []
        crxOutputEnabled = properties.get(CRX_OUTPUT_ENABLED) ?: false
        crxOutputFolder = properties.get(CRX_OUTPUT_FOLDER) ?: DEFAULT_CRX_OUTPUT_FOLDER
        resourceResolverAdapters = properties.get(RESOURCE_RESOLVER_ADAPTERS) ?: []
        osgiServices = properties.get(OSGI_SERVICES) ?: []
    }
}
