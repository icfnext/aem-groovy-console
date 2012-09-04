import org.osgi.service.cm.ConfigurationAdmin

def admin = getService(ConfigurationAdmin)

def config = admin.getConfiguration('org.apache.sling.jcr.resource.internal.JcrResourceResolverFactoryImpl')

def properties = config.properties

def mappings = properties.get('resource.resolver.mapping') as List

mappings.add('/foo:/bar')

properties.put('resource.resolver.mapping', mappings.toArray(new String[0]))

config.update(properties)