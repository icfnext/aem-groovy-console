package com.icfolson.aem.groovy.console.configuration.impl

import com.icfolson.aem.groovy.console.configuration.ConfigurationService
import groovy.transform.Synchronized
import groovy.util.logging.Slf4j
import org.apache.felix.scr.annotations.Activate
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.Deactivate
import org.apache.felix.scr.annotations.Modified
import org.apache.felix.scr.annotations.Property
import org.apache.felix.scr.annotations.Reference
import org.apache.felix.scr.annotations.Service
import org.apache.jackrabbit.api.security.user.UserManager
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.resource.ResourceResolver
import org.apache.sling.api.resource.ResourceResolverFactory

@Service(ConfigurationService)
@Component(immediate = true, metatype = true, label = "Groovy Console Configuration Service")
@Slf4j("LOG")
class DefaultConfigurationService implements ConfigurationService {

    private static final String DEFAULT_PATH = "/etc/groovyconsole.html"

    private static final String VANITY_PATH = "/groovyconsole"

    @Property(label = "Email Enabled?",
        description = "Check to enable email notification on completion of script execution.",
        boolValue = false)
    private static final String EMAIL_ENABLED = "email.enabled"

    @Property(label = "Email Recipients",
        description = "Email addresses to receive notification.", cardinality = 20)
    private static final String EMAIL_RECIPIENTS = "email.recipients"

    @Property(label = "Allowed Groups",
        description = "List of group names that are authorized to use the console.  If empty, no authorization check is performed.",
        cardinality = 20)
    private static final String ALLOWED_GROUPS = "groups.allowed"

    @Property(label = "Vanity Path Enabled?",
        description = "Enables /groovyconsole vanity path.  Apache Sling Resource Resolver Factory OSGi configuration must also be updated to allow vanity paths from /etc (resource.resolver.vanitypath.whitelist).", boolValue = false)
    private static final String VANITY_PATH_ENABLED = "vanity.path.enabled"

    @Property(label = "Audit Disabled?", description = "Disables auditing of script execution history.",
        boolValue = false)
    private static final String AUDIT_DISABLED = "audit.disabled"

    @Reference
    private ResourceResolverFactory resourceResolverFactory

    private ResourceResolver resourceResolver

    boolean emailEnabled

    Set<String> emailRecipients

    Set<String> allowedGroups

    boolean vanityPathEnabled

    boolean auditDisabled

    @Override
    boolean hasPermission(SlingHttpServletRequest request) {
        resourceResolver.refresh()

        def user = resourceResolver.adaptTo(UserManager).getAuthorizable(request.userPrincipal)

        def memberOfGroupIds = user.memberOf()*.ID

        LOG.debug("member of group IDs = {}, allowed group IDs = {}", memberOfGroupIds, allowedGroups)

        allowedGroups ? memberOfGroupIds.intersect(allowedGroups) : true
    }

    @Override
    String getConsoleHref() {
        vanityPathEnabled ? VANITY_PATH : DEFAULT_PATH
    }

    @Activate
    void activate(Map<String, Object> properties) {
        resourceResolver = resourceResolverFactory.getServiceResourceResolver(null)

        modified(properties)
    }

    @Modified
    @Synchronized
    void modified(Map<String, Object> properties) {
        emailEnabled = properties.get(EMAIL_ENABLED) ?: false
        emailRecipients = (properties.get(EMAIL_RECIPIENTS) ?: []).findAll() as Set
        allowedGroups = (properties.get(ALLOWED_GROUPS) ?: []).findAll() as Set
        vanityPathEnabled = properties.get(VANITY_PATH_ENABLED) ?: false
        auditDisabled = properties.get(AUDIT_DISABLED) ?: false
    }

    @Deactivate
    void deactivate() {
        resourceResolver?.close()
    }
}
