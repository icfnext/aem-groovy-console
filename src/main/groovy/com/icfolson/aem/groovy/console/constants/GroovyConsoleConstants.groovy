package com.icfolson.aem.groovy.console.constants

import static com.day.cq.commons.jcr.JcrConstants.JCR_CONTENT

class GroovyConsoleConstants {

    public static final String PATH_CONSOLE_ROOT = "/etc/groovyconsole"

    public static final String EXTENSION_GROOVY = ".groovy"

    public static final String PARAMETER_SCRIPT = "script"

    public static final String PARAMETER_USER_ID = "userId"

    public static final String PARAMETER_START_DATE = "startDate"

    public static final String PARAMETER_END_DATE = "endDate"

    public static final String PARAMETER_DATA = "data"

    public static final String AUDIT_NODE_NAME = "audit"

    public static final String AUDIT_RECORD_NODE_PREFIX = "record"

    public static final String AUDIT_PATH = "$PATH_CONSOLE_ROOT/$JCR_CONTENT/$AUDIT_NODE_NAME"

    public static final Set<String> DEFAULT_STAR_IMPORTS = [
        "javax.jcr",
        "org.apache.sling.api",
        "org.apache.sling.api.resource",
        "com.day.cq.search",
        "com.day.cq.tagging",
        "com.day.cq.wcm.api",
        "com.day.cq.replication",
        "groovy.json"
    ] as Set

    private GroovyConsoleConstants() {

    }
}
