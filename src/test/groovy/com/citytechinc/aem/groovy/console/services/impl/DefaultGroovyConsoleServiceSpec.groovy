package com.citytechinc.aem.groovy.console.services.impl

import com.citytechinc.aem.groovy.console.configuration.ConfigurationService

import com.citytechinc.aem.groovy.console.audit.AuditService
import com.citytechinc.aem.groovy.console.extension.impl.DefaultBindingExtensionProvider
import com.citytechinc.aem.groovy.console.extension.impl.DefaultExtensionService
import com.citytechinc.aem.groovy.console.extension.impl.DefaultScriptMetaClassExtensionProvider
import com.citytechinc.aem.groovy.console.impl.DefaultGroovyConsoleService
import com.citytechinc.aem.prosper.specs.ProsperSpec
import com.day.cq.commons.jcr.JcrConstants
import com.day.cq.replication.Replicator
import com.day.cq.search.QueryBuilder
import org.apache.felix.scr.ScrService
import org.osgi.framework.BundleContext

import javax.jcr.RepositoryException

import static com.citytechinc.aem.groovy.console.impl.DefaultGroovyConsoleService.PARAMETER_FILE_NAME
import static com.citytechinc.aem.groovy.console.impl.DefaultGroovyConsoleService.PARAMETER_SCRIPT
import static com.citytechinc.aem.groovy.console.impl.DefaultGroovyConsoleService.RELATIVE_PATH_SCRIPT_FOLDER

class DefaultGroovyConsoleServiceSpec extends ProsperSpec {

    static final def SCRIPT_NAME = "Script"

    static final def SCRIPT_FILE_NAME = "${SCRIPT_NAME}.groovy"

    static final def PATH_FOLDER = "/etc/groovyconsole/$RELATIVE_PATH_SCRIPT_FOLDER"

    static final def PATH_FILE = "$PATH_FOLDER/$SCRIPT_FILE_NAME"

    static final def PATH_FILE_CONTENT = "$PATH_FILE/${JcrConstants.JCR_CONTENT}"

    def "run script"() {
        setup:
        def consoleService = createConsoleService()

        def request = requestBuilder.build {
            parameters = this.parameterMap
        }

        when:
        def map = consoleService.runScript(request)

        then:
        assertScriptResult(map)
    }

    def "run script with additional star imports"() {

    }

    def "save script"() {
        setup:
        def consoleService = createConsoleService()

        def request = requestBuilder.build {
            parameters = this.parameterMap
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
        assertNodeExists(PATH_FILE_CONTENT, JcrConstants.NT_RESOURCE, [(JcrConstants.JCR_MIMETYPE): "application/octet-stream"])

        assert session.getNode(PATH_FILE_CONTENT).get(JcrConstants.JCR_DATA).stream.text == scriptAsString

        cleanup:
        removeAllNodes()
    }

    def "missing console root node"() {
        setup:
        def consoleService = createConsoleService()

        def request = requestBuilder.build {
            parameters = this.parameterMap
        }

        when:
        consoleService.saveScript(request)

        then:
        thrown(RepositoryException)
    }

    void assertScriptResult(map) {
        assert !map.result
        assert map.output == "BEER\n"
        assert !map.exceptionStackTrace
        assert map.runningTime
    }

    private def createConsoleService() {
        def extensionService = new DefaultExtensionService()

        def bindingExtensionProvider = new DefaultBindingExtensionProvider()

        bindingExtensionProvider.with {
            queryBuilder = Mock(QueryBuilder)
            bundleContext = Mock(BundleContext)
        }

        extensionService.bindBindingExtensionProvider(bindingExtensionProvider)

        def scriptMetaClassExtensionProvider = new DefaultScriptMetaClassExtensionProvider()

        scriptMetaClassExtensionProvider.with {
            replicator = Mock(Replicator)
            scrService = Mock(ScrService)
            queryBuilder = Mock(QueryBuilder)
            bundleContext = Mock(BundleContext)
        }

        extensionService.bindScriptMetaClassExtensionProvider(scriptMetaClassExtensionProvider)

        def consoleService = new DefaultGroovyConsoleService()

        with(consoleService) {
            configurationService = Mock(ConfigurationService)
            auditService = Mock(AuditService)
        }

        consoleService.extensionService = extensionService

        consoleService
    }

    private def getScriptAsString() {
        def scriptAsString = null

        this.class.getResourceAsStream("/$SCRIPT_FILE_NAME").withStream { stream ->
            scriptAsString = stream.text
        }

        scriptAsString
    }

    private def getParameterMap() {
        [(PARAMETER_FILE_NAME): (SCRIPT_NAME), (PARAMETER_SCRIPT): scriptAsString]
    }
}