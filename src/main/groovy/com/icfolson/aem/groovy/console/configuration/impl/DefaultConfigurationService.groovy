package com.icfolson.aem.groovy.console.configuration.impl

import com.icfolson.aem.groovy.console.configuration.ConfigurationService
import groovy.transform.Synchronized
import groovy.util.logging.Slf4j
import org.apache.jackrabbit.api.security.user.User
import org.apache.jackrabbit.api.security.user.UserManager
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.resource.ResourceResolverFactory
import org.osgi.service.component.annotations.Activate
import org.osgi.service.component.annotations.Component
import org.osgi.service.component.annotations.Modified
import org.osgi.service.component.annotations.Reference
import org.osgi.service.metatype.annotations.Designate

@Component(service = ConfigurationService, immediate = true)
@Designate(ocd = ConfigurationServiceProperties)
@Slf4j("LOG")
class DefaultConfigurationService implements ConfigurationService {

    private static final String DEFAULT_PATH = "/apps/groovyconsole.html"

    private static final String VANITY_PATH = "/groovyconsole"

    @Reference
    private ResourceResolverFactory resourceResolverFactory

    boolean emailEnabled

    Set<String> emailRecipients

    Set<String> allowedGroups

    boolean vanityPathEnabled

    boolean auditDisabled

    boolean displayAllAuditRecords

    @Override
    boolean hasPermission(SlingHttpServletRequest request) {
        def resourceResolver = resourceResolverFactory.getServiceResourceResolver(null)

        def hasPermission = false

        try {
            def user = resourceResolver.adaptTo(UserManager).getAuthorizable(request.userPrincipal) as User
            def memberOfGroupIds = user.memberOf()*.ID

            LOG.debug("member of group IDs = {}, allowed group IDs = {}", memberOfGroupIds, allowedGroups)

            hasPermission = user.admin || (allowedGroups ? memberOfGroupIds.intersect(allowedGroups as Iterable) : false)
        } finally {
            resourceResolver.close()
        }

        hasPermission
    }

    @Override
    String getConsoleHref() {
        vanityPathEnabled ? VANITY_PATH : DEFAULT_PATH
    }

    @Activate
    @Modified
    @Synchronized
    void activate(ConfigurationServiceProperties properties) {
        emailEnabled = properties.emailEnabled()
        emailRecipients = (properties.emailRecipients() ?: []).findAll() as Set
        allowedGroups = (properties.allowedGroups() ?: []).findAll() as Set
        vanityPathEnabled = properties.vanityPathEnabled()
        auditDisabled = properties.auditDisabled()
        displayAllAuditRecords = properties.auditDisplayAll()
    }
}
