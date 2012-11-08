import org.osgi.service.cm.ConfigurationAdmin

// depths:
//
// 0 = single page
// 1 = page with direct children
// -1 = page with subtree

def paths = ['/apps/test', '/content/test', '/etc/test']

def admin = getService(ConfigurationAdmin)

admin.listConfigurations('(service.factoryPid=com.day.cq.compat.migration.factory.location)')*.delete()

paths.each { path ->
    def config = admin.createFactoryConfiguration('com.day.cq.compat.migration.factory.location', 'jcrinstall:/libs/cq/compat/install/cq-compat-migration-1.1.10.jar')

    def properties = ['location.path':path,
        'location.depth':'-1',
        'location.version.num':'0',
        'location.version.age':'-1']

    config.update(properties as Hashtable)
}