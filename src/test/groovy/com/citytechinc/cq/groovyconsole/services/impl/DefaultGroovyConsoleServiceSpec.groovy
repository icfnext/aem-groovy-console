package com.citytechinc.cq.groovyconsole.services.impl

import com.citytechinc.cq.groovy.extension.builders.NodeBuilder
import com.citytechinc.cq.groovy.extension.metaclass.GroovyExtensionMetaClassRegistry
import com.citytechinc.cq.groovy.testing.specs.AbstractSlingRepositorySpec
import com.citytechinc.cq.groovyconsole.services.ConfigurationService
import com.citytechinc.cq.groovyconsole.services.EmailService
import com.day.cq.commons.jcr.JcrConstants
import com.day.cq.replication.Replicator
import com.day.cq.search.QueryBuilder
import org.osgi.framework.BundleContext
import spock.lang.Shared

import javax.jcr.RepositoryException

import static com.citytechinc.cq.groovyconsole.services.impl.DefaultGroovyConsoleService.PARAMETER_FILE_NAME
import static com.citytechinc.cq.groovyconsole.services.impl.DefaultGroovyConsoleService.PARAMETER_SCRIPT
import static com.citytechinc.cq.groovyconsole.services.impl.DefaultGroovyConsoleService.RELATIVE_PATH_SCRIPT_FOLDER

class DefaultGroovyConsoleServiceSpec extends AbstractSlingRepositorySpec {

    static final def SCRIPT_NAME = "Script"

    static final def SCRIPT_FILE_NAME = "${SCRIPT_NAME}.groovy"

    static final def PATH_FOLDER = "/etc/groovyconsole/$RELATIVE_PATH_SCRIPT_FOLDER"

    static final def PATH_FILE = "$PATH_FOLDER/$SCRIPT_FILE_NAME"

    static final def PATH_FILE_CONTENT = "$PATH_FILE/${JcrConstants.JCR_CONTENT}"

    @Shared nodeBuilder

    @Shared consoleService

    def setupSpec() {
        GroovyExtensionMetaClassRegistry.registerMetaClasses()

        nodeBuilder = new NodeBuilder(session)

        consoleService = new DefaultGroovyConsoleService()

        consoleService.replicator = Mock(Replicator)
        consoleService.bundleContext = Mock(BundleContext)
        consoleService.configurationService = Mock(ConfigurationService)
        consoleService.queryBuilder = Mock(QueryBuilder)
        consoleService.emailService = Mock(EmailService)
    }

    def cleanupSpec() {
        GroovyExtensionMetaClassRegistry.removeMetaClasses()
    }

    def "run script"() {
        setup:
        def script = getScriptAsString()
        def parameterMap = [(PARAMETER_SCRIPT): [script]]

        def request = requestBuilder.build {
            parameters parameterMap
        }

        when:
        def map = consoleService.runScript(request)

        then:
        assertScriptResult(map)
    }

    def "save script"() {
        setup:
        def script = getScriptAsString()
        def request = buildRequest(script)

        and:
        nodeBuilder.etc {
            groovyconsole()
        }

        when:
        consoleService.saveScript(request)

        then:
        assertNodeExists(PATH_FOLDER, JcrConstants.NT_FOLDER)
        assertNodeExists(PATH_FILE, JcrConstants.NT_FILE)
        assertNodeExists(PATH_FILE_CONTENT, JcrConstants.NT_RESOURCE, [(JcrConstants.JCR_MIMETYPE):
            "application/octet-stream"])

        assert session.getNode(PATH_FILE_CONTENT).get(JcrConstants.JCR_DATA).stream.text == script

        cleanup:
        removeAllNodes()
    }

    def "missing console root node"() {
        setup:
        def script = getScriptAsString()
        def request = buildRequest(script)

        when:
        consoleService.saveScript(request)

        then:
        thrown(RepositoryException)
    }

    def buildRequest(script) {
        def parameterMap = [(PARAMETER_FILE_NAME): [(SCRIPT_NAME)], (PARAMETER_SCRIPT): [script]]

        requestBuilder.build {
            parameters parameterMap
        }
    }

    void assertScriptResult(map) {
        assert !map.executionResult
        assert map.outputText == "BEER\n"
        assert !map.stacktraceText
        assert map.runningTime
    }


    def getScriptAsString() {
        def script = null

        this.class.getResourceAsStream("/$SCRIPT_FILE_NAME").withStream { stream ->
            script = stream.text
        }

        script
    }
}