import org.osgi.service.cm.ConfigurationAdmin

def loggers = [
    "com.day.cq.dam": "dam",
    "com.icfolson.aem.groovy.console": "groovyconsole"
]

def admin = getService(ConfigurationAdmin)

loggers.each { loggerName, fileName ->
    def config = admin.createFactoryConfiguration("org.apache.sling.commons.log.LogManager.factory.config",
        "slinginstall:org.apache.sling.commons.log-4.0.0.jar")

    def properties = [
        "org.apache.sling.commons.log.level": "debug",
        "org.apache.sling.commons.log.file": "logs/" + fileName + ".log",
        "org.apache.sling.commons.log.names": loggerName
    ]

    config.update(properties as Hashtable)
}