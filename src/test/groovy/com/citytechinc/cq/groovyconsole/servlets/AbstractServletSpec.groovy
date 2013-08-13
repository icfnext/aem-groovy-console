package com.citytechinc.cq.groovyconsole.servlets

import com.citytechinc.cq.groovy.extension.builders.NodeBuilder
import com.citytechinc.cq.groovy.extension.metaclass.GroovyExtensionMetaClassRegistry
import com.citytechinc.cq.groovy.testing.specs.AbstractSlingRepositorySpec
import spock.lang.Shared

abstract class AbstractServletSpec extends AbstractSlingRepositorySpec {

    @Shared nodeBuilder

    def setupSpec() {
        GroovyExtensionMetaClassRegistry.registerMetaClasses()

        nodeBuilder = new NodeBuilder(session)
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
