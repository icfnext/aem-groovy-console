package com.citytechinc.cq.groovyconsole.servlets

import com.citytechinc.cq.groovy.extension.metaclass.GroovyExtensionMetaClassRegistry
import com.citytechinc.cq.groovy.testing.specs.AbstractSlingRepositorySpec

abstract class AbstractGroovyConsoleSpec extends AbstractSlingRepositorySpec {

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
