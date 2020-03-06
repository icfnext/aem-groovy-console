package com.icfolson.aem.groovy.console.constants

import com.google.common.base.Charsets

class GroovyConsoleConstants {

    public static final String PATH_CONSOLE_ROOT = "/var/groovyconsole"

    public static final String PATH_SCRIPTS_FOLDER = "$PATH_CONSOLE_ROOT/scripts"

    public static final String EXTENSION_GROOVY = ".groovy"

    public static final String CHARSET = Charsets.UTF_8.name()

    public static final String FORMAT_RUNNING_TIME = "HH:mm:ss.SSS"

    public static final String TIME_ZONE_RUNNING_TIME = "GMT"

    // request parameters

    public static final String PARAMETER_TITLE = "title"

    public static final String PARAMETER_DESCRIPTION = "description"

    public static final String PARAMETER_FILE_NAME = "fileName"

    public static final String PARAMETER_SCRIPT_PATH = "scriptPath"

    public static final String PARAMETER_SCRIPT_PATHS = "scriptPaths"

    public static final String PARAMETER_SCRIPT = "script"

    public static final String PARAMETER_USER_ID = "userId"

    public static final String PARAMETER_START_DATE = "startDate"

    public static final String PARAMETER_END_DATE = "endDate"

    public static final String PARAMETER_DATA = "data"

    // job properties

    public static final String JOB_TOPIC = "groovyconsole/job"

    public static final String JOB_PROPERTY_CRON_EXPRESSION = "cronExpression"

    public static final String JOB_PROPERTY_EMAIL_TO = "emailTo"

    public static final Set<String> JOB_PROPERTIES = [
        PARAMETER_TITLE,
        PARAMETER_DESCRIPTION,
        PARAMETER_SCRIPT,
        PARAMETER_DATA,
        JOB_PROPERTY_CRON_EXPRESSION,
        JOB_PROPERTY_EMAIL_TO
    ] as Set

    // audit

    public static final String AUDIT_NODE_NAME = "audit"

    public static final String AUDIT_RECORD_NODE_PREFIX = "record"

    public static final String AUDIT_PATH = "$PATH_CONSOLE_ROOT/$AUDIT_NODE_NAME"

    private GroovyConsoleConstants() {

    }
}
