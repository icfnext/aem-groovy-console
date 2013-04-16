import org.osgi.service.cm.ConfigurationAdmin

def admin = sling.getService(ConfigurationAdmin)

admin.listConfigurations("(service.factoryPid=com.day.cq.compat.migration.factory.location)")*.delete()