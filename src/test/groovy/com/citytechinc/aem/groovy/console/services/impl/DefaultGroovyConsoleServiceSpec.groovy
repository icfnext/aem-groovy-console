package com.citytechinc.aem.groovy.console.services.impl

import com.citytechinc.aem.groovy.console.services.audit.AuditService
import com.citytechinc.aem.prosper.specs.ProsperSpec
import com.citytechinc.aem.groovy.console.services.ConfigurationService
import com.citytechinc.aem.groovy.console.services.EmailService
import com.day.cq.commons.jcr.JcrConstants
import com.day.cq.replication.Replicator
import com.day.cq.search.QueryBuilder
import org.osgi.framework.BundleContext
import spock.lang.Shared

import javax.jcr.RepositoryException

import static com.citytechinc.aem.groovy.console.services.impl.DefaultGroovyConsoleService.PARAMETER_FILE_NAME
import static com.citytechinc.aem.groovy.console.services.impl.DefaultGroovyConsoleService.PARAMETER_SCRIPT
import static com.citytechinc.aem.groovy.console.services.impl.DefaultGroovyConsoleService.RELATIVE_PATH_SCRIPT_FOLDER

class DefaultGroovyConsoleServiceSpec extends ProsperSpec {

    static final def SCRIPT_NAME = "Script"

    static final def SCRIPT_FILE_NAME = "${SCRIPT_NAME}.groovy"

    static final def PATH_FOLDER = "/etc/groovyconsole/$RELATIVE_PATH_SCRIPT_FOLDER"

    static final def PATH_FILE = "$PATH_FOLDER/$SCRIPT_FILE_NAME"

    static final def PATH_FILE_CONTENT = "$PATH_FILE/${JcrConstants.JCR_CONTENT}"

    @Shared consoleService

    @Shared scriptAsString

    @Shared parameterMap

    def setupSpec() {
        consoleService = new DefaultGroovyConsoleService()

        with(consoleService) {
            replicator = Mock(Replicator)
            bundleContext = Mock(BundleContext)
            configurationService = Mock(ConfigurationService)
            queryBuilder = Mock(QueryBuilder)
            emailService = Mock(EmailService)
            auditService = Mock(AuditService)
        }

        this.class.getResourceAsStream("/$SCRIPT_FILE_NAME").withStream { stream ->
            scriptAsString = stream.text
        }

        parameterMap = [(PARAMETER_FILE_NAME): (SCRIPT_NAME), (PARAMETER_SCRIPT): scriptAsString]
    }

    def "run script"() {
        setup:
        def script = scriptAsString
        def request = requestBuilder.build {
            parameters = [(PARAMETER_SCRIPT): script]
        }

        when:
        def map = consoleService.runScript(request)

        then:
        assertScriptResult(map)
    }

    def "save script"() {
        setup:
        def map = parameterMap
        def request = requestBuilder.build {
            parameters = map
        }

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

        assert session.getNode(PATH_FILE_CONTENT).get(JcrConstants.JCR_DATA).stream.text == scriptAsString

        cleanup:
        removeAllNodes()
    }

    def "missing console root node"() {
        setup:
        def map = parameterMap
        def request = requestBuilder.build {
            parameters = map
        }

        when:
        consoleService.saveScript(request)

        then:
        thrown(RepositoryException)
    }

    void assertScriptResult(map) {
        assert !map.executionResult
        assert map.outputText == "BEER\n"
        assert !map.stacktraceText
        assert map.runningTime
    }
}