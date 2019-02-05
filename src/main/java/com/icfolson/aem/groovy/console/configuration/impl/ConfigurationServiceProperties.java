package com.icfolson.aem.groovy.console.configuration.impl;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Groovy Console Configuration Service")
public @interface ConfigurationServiceProperties {

    @AttributeDefinition(name = "Email Enabled?",
        description = "Check to enable email notification on completion of script execution.")
    boolean emailEnabled() default false;

    @AttributeDefinition(name = "Email Recipients",
        description = "Email addresses to receive notification.", cardinality = 20)
    String[] emailRecipients() default {};

    @AttributeDefinition(name = "Allowed Groups",
        description = "List of group names that are authorized to use the console.  If empty, no authorization check is performed.",
        cardinality = 20)
    String[] allowedGroups() default {};

    @AttributeDefinition(name = "Vanity Path Enabled?",
        description = "Enables /groovyconsole vanity path.  Apache Sling Resource Resolver Factory OSGi configuration must also be updated to allow vanity paths from /etc (resource.resolver.vanitypath.whitelist).")
    boolean vanityPathEnabled() default false;

    @AttributeDefinition(name = "Audit Disabled?", description = "Disables auditing of script execution history.")
    boolean auditDisabled() default false;

    @AttributeDefinition(name = "Display All Audit Records?",
        description = "If enabled, all audit records (including records for other users) will be displayed in the console history.")
    boolean auditDisplayAll() default false;
}