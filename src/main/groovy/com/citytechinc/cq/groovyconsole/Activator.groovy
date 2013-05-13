package com.citytechinc.cq.groovyconsole

import com.citytechinc.cq.groovy.metaclass.GroovyMetaClassRegistry
import org.osgi.framework.BundleActivator
import org.osgi.framework.BundleContext

class Activator implements BundleActivator {

    @Override
    void start(BundleContext context) throws Exception {
        GroovyMetaClassRegistry.registerMetaClasses()
    }

    @Override
    void stop(BundleContext context) throws Exception {
        GroovyMetaClassRegistry.removeMetaClasses()
    }
}
