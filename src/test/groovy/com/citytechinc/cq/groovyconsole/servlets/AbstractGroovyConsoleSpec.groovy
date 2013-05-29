package com.citytechinc.cq.groovyconsole.servlets

import com.citytechinc.cq.groovy.metaclass.GroovyExtensionMetaClassRegistry
import com.citytechinc.cq.groovy.testing.AbstractRepositorySpec

abstract class AbstractGroovyConsoleSpec extends AbstractRepositorySpec {

    def setupSpec() {
        GroovyExtensionMetaClassRegistry.registerMetaClasses()
    }

    def cleanupSpec() {
        GroovyExtensionMetaClassRegistry.removeMetaClasses()
    }

    def getScriptAsString(name) {
        def script = null

        this.class.getResourceAsStream("/${name}.groovy").withStream { stream ->
            script = stream.text
        }

        script
    }
}
