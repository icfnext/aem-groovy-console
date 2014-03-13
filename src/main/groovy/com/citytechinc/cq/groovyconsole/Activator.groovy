package com.citytechinc.cq.groovyconsole

import com.citytechinc.aem.groovy.extension.metaclass.GroovyExtensionMetaClassRegistry
import org.osgi.framework.BundleActivator
import org.osgi.framework.BundleContext

class Activator implements BundleActivator {

    @Override
    void start(BundleContext context) throws Exception {
        GroovyExtensionMetaClassRegistry.registerMetaClasses()
    }

    @Override
    void stop(BundleContext context) throws Exception {
        GroovyExtensionMetaClassRegistry.removeMetaClasses()
    }
}
