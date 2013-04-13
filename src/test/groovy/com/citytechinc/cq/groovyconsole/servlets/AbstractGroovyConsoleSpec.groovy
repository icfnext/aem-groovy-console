package com.citytechinc.cq.groovyconsole.servlets

import com.citytechinc.cq.testing.AbstractRepositorySpec

abstract class AbstractGroovyConsoleSpec extends AbstractRepositorySpec {

    def getScriptAsString(name) {
        def script = null

        this.class.getResourceAsStream("/${name}.groovy").withStream { stream ->
            script = stream.text
        }

        script
    }
}
