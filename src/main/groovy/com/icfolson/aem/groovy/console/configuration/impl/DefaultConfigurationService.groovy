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

    private boolean emailEnabled

    private Set<String> emailRecipients

    private Set<String> allowedGroups

    private Set<String> allowedScheduledJobsGroups

    private boolean vanityPathEnabled

    private boolean auditDisabled

    private boolean displayAllAuditRecords

    @Override
    boolean hasPermission(SlingHttpServletRequest request) {
        isAdminOrAllowedGroupMember(request, allowedGroups)
    }

    @Override
    boolean hasScheduledJobPermission(SlingHttpServletRequest request) {
        isAdminOrAllowedGroupMember(request, allowedScheduledJobsGroups)
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
    boolean isAuditDisabled() {
        auditDisabled
    }

    @Override
    boolean isDisplayAllAuditRecords() {
        displayAllAuditRecords
    }

    @Activate
    @Modified
    @Synchronized
    void activate(ConfigurationServiceProperties properties) {
        emailEnabled = properties.emailEnabled()
        emailRecipients = (properties.emailRecipients() ?: []).findAll() as Set
        allowedGroups = (properties.allowedGroups() ?: []).findAll() as Set
        allowedScheduledJobsGroups = (properties.allowedScheduledJobsGroups() ?: []).findAll() as Set
        vanityPathEnabled = properties.vanityPathEnabled()
        auditDisabled = properties.auditDisabled()
        displayAllAuditRecords = properties.auditDisplayAll()
    }

    private boolean isAdminOrAllowedGroupMember(SlingHttpServletRequest request, Set<String> groupIds) {
        resourceResolverFactory.getServiceResourceResolver(null).withCloseable { resourceResolver ->
            def user = resourceResolver.adaptTo(UserManager).getAuthorizable(request.userPrincipal) as User
            def memberOfGroupIds = user.memberOf()*.ID

            LOG.debug("member of group IDs : {}, allowed group IDs : {}", memberOfGroupIds, groupIds)

            user.admin || (groupIds ? memberOfGroupIds.intersect(groupIds as Iterable) : false)
        }
    }
}
