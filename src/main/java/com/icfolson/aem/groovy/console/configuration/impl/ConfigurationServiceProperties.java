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

    @AttributeDefinition(name = "Allowed Groups (Script Execution)",
        description = "List of group names that are authorized to use the console.  By default, only the 'admin' user has permission to execute scripts.",
        cardinality = 20)
    String[] allowedGroups() default {};

    @AttributeDefinition(name = "Allowed Groups (Scheduled Jobs)",
        description = "List of group names that are authorized to schedule jobs.  By default, only the 'admin' user has permission to schedule jobs.",
        cardinality = 20)
    String[] allowedScheduledJobsGroups() default {};

    @AttributeDefinition(name = "Vanity Path Enabled?",
        description = "Enables /groovyconsole vanity path.")
    boolean vanityPathEnabled() default false;

    @AttributeDefinition(name = "Audit Disabled?", description = "Disables auditing of script execution history.")
    boolean auditDisabled() default false;

    @AttributeDefinition(name = "Display All Audit Records?",
        description = "If enabled, all audit records (including records for other users) will be displayed in the console history.")
    boolean auditDisplayAll() default false;
}