package com.citytechinc.cq.groovyconsole.servlets

import com.citytechinc.cq.groovy.metaclass.GroovyMetaClassRegistry
import com.citytechinc.cq.testing.AbstractRepositorySpec

abstract class AbstractGroovyConsoleSpec extends AbstractRepositorySpec {

    def setupSpec() {
        GroovyMetaClassRegistry.registerMetaClasses()
    }

    def cleanupSpec() {
        GroovyMetaClassRegistry.removeMetaClasses()
    }

    def getScriptAsString(name) {
        def script = null

        this.class.getResourceAsStream("/${name}.groovy").withStream { stream ->
            script = stream.text
        }

        script
    }
}
