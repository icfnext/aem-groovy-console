package com.icfolson.aem.groovy.console.constants

import com.google.common.base.Charsets

class GroovyConsoleConstants {

    public static final String PATH_CONSOLE_ROOT = "/var/groovyconsole"

    public static final String PATH_SCRIPTS_FOLDER = "$PATH_CONSOLE_ROOT/scripts"

    public static final String EXTENSION_GROOVY = ".groovy"

    public static final String CHARSET = Charsets.UTF_8.name()

    public static final String FORMAT_RUNNING_TIME = "HH:mm:ss.SSS"

    public static final String TIME_ZONE_RUNNING_TIME = "GMT"

    public static final String DATE_FORMAT_DISPLAY = "yyyy-MM-dd HH:mm:ss"

    public static final String DATE_FORMAT_FILE_NAME = "yyyy-MM-dd'T'HHmmss"

    // request parameters/properties

    public static final String SCHEDULED_JOB_ID = "scheduledJobId"

    public static final String JOB_ID = "jobId"

    public static final String TITLE = "title"

    public static final String DESCRIPTION = "description"

    public static final String FILE_NAME = "fileName"

    public static final String SCRIPT_PATH = "scriptPath"

    public static final String SCRIPT_PATHS = "scriptPaths"

    public static final String SCRIPT = "script"

    public static final String USER_ID = "userId"

    public static final String START_DATE = "startDate"

    public static final String END_DATE = "endDate"

    public static final String DATA = "data"

    public static final String CRON_EXPRESSION = "cronExpression"

    public static final String EMAIL_TO = "emailTo"

    public static final String DATE_CREATED = "dateCreated"

    public static final String RESULT = "result"

    public static final String OUTPUT = "output"

    public static final String MEDIA_TYPE = "mediaType"

    public static final String EXCEPTION_STACK_TRACE = "exceptionStackTrace"

    public static final String RUNNING_TIME = "runningTime"

    // job properties

    public static final String JOB_TOPIC = "groovyconsole/job"

    public static final Set<String> JOB_PROPERTIES = [
        TITLE,
        DESCRIPTION,
        SCRIPT,
        DATA,
        CRON_EXPRESSION,
        EMAIL_TO,
        MEDIA_TYPE
    ] as Set

    // audit

    public static final String AUDIT_NODE_NAME = "audit"

    public static final String AUDIT_RECORD_NODE_PREFIX = "record"

    public static final String AUDIT_PATH = "$PATH_CONSOLE_ROOT/$AUDIT_NODE_NAME"

    private GroovyConsoleConstants() {

    }
}
