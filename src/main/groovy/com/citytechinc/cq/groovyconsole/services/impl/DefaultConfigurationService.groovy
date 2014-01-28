package com.citytechinc.cq.groovyconsole.services.impl
import com.citytechinc.cq.groovyconsole.services.ConfigurationService
import groovy.text.GStringTemplateEngine
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

    static final def DEFAULT_ADAPTERS = ["com.day.cq.wcm.api.PageManager",
        "com.adobe.cq.social.blog.BlogManager",
        "com.adobe.granite.xss.XSSAPI",
        "com.day.cq.search.SimpleSearch",
        "com.day.cq.search.QueryBuilder"]

    static final String ADAPTER_TEMPLATE_PATH = "/adapter.template"

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
        description = "Class names for additional Sling Resource Resolver adapters to populate dropdown list.", cardinality = Integer.MAX_VALUE)
    static final String RESOURCE_RESOLVER_ADAPTERS = "resource.resolver.adapters"

    def emailEnabled

    def emailRecipients

    def crxOutputEnabled

    def crxOutputFolder

    def resourceResolverAdapters

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
    Map<String, String> getResourceResolverAdapters() {
        def adapters = [:]

        def classNames = DEFAULT_ADAPTERS + (resourceResolverAdapters as List)

        def template = new GStringTemplateEngine().createTemplate(this.class.getResource(ADAPTER_TEMPLATE_PATH))

        classNames.sort().each { className ->
            try {
                def clazz = Class.forName(className)
                def simpleName = clazz.simpleName
                def binding = [className: className,
                    variableName: StringUtils.uncapitalize(simpleName),
                    simpleName: simpleName]

                adapters[className] = template.make(binding).toString()
            } catch (e) {
                LOG.error "error getting class for name = $className", e
            }
        }

        adapters
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
        resourceResolverAdapters = properties.get(RESOURCE_RESOLVER_ADAPTERS) ?: []
    }
}
