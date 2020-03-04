package com.icfolson.aem.groovy.console.impl

import com.day.cq.commons.jcr.JcrConstants
import com.day.cq.replication.Replicator
import com.day.cq.search.QueryBuilder
import com.google.common.base.Charsets
import com.icfolson.aem.groovy.console.GroovyConsoleService
import com.icfolson.aem.groovy.console.api.impl.RequestScriptContext
import com.icfolson.aem.groovy.console.api.impl.RequestScriptData
import com.icfolson.aem.groovy.console.audit.AuditService
import com.icfolson.aem.groovy.console.configuration.ConfigurationService
import com.icfolson.aem.groovy.console.constants.GroovyConsoleConstants
import com.icfolson.aem.groovy.console.extension.impl.DefaultBindingExtensionProvider
import com.icfolson.aem.groovy.console.extension.impl.DefaultExtensionService
import com.icfolson.aem.groovy.console.extension.impl.DefaultScriptMetaClassExtensionProvider
import com.icfolson.aem.prosper.specs.ProsperSpec
import org.apache.sling.jcr.resource.JcrResourceConstants

import static com.icfolson.aem.groovy.console.constants.GroovyConsoleConstants.PARAMETER_SCRIPT
import static com.icfolson.aem.groovy.console.constants.GroovyConsoleConstants.PATH_SCRIPTS_FOLDER

class DefaultGroovyConsoleServiceSpec extends ProsperSpec {

    static final def SCRIPT_NAME = "Script"

    static final def SCRIPT_FILE_NAME = "${SCRIPT_NAME}.groovy"

    static final def PATH_FILE = "$PATH_SCRIPTS_FOLDER/$SCRIPT_FILE_NAME"

    static final def PATH_FILE_CONTENT = "$PATH_FILE/${JcrConstants.JCR_CONTENT}"

    def setupSpec() {
        slingContext.registerService(QueryBuilder, Mock(QueryBuilder))
        slingContext.registerService(ConfigurationService, Mock(ConfigurationService))
        slingContext.registerService(AuditService, Mock(AuditService))
        slingContext.registerService(Replicator, Mock(Replicator))
        slingContext.registerInjectActivateService(new DefaultBindingExtensionProvider())
        slingContext.registerInjectActivateService(new DefaultExtensionService())
        slingContext.registerInjectActivateService(new DefaultScriptMetaClassExtensionProvider())
        slingContext.registerInjectActivateService(new DefaultGroovyConsoleService())
    }

    def "run script"() {
        setup:
        def consoleService = slingContext.getService(GroovyConsoleService)

        def request = requestBuilder.build()
        def response = responseBuilder.build()

        def outputStream = new ByteArrayOutputStream()

        def scriptContext = new RequestScriptContext(
            request: request,
            response: response,
            outputStream: outputStream,
            printStream: new PrintStream(outputStream, true, Charsets.UTF_8.name()),
            scriptContent: scriptAsString
        )

        when:
        def map = consoleService.runScript(scriptContext)

        then:
        assertScriptResult(map)
    }

    def "save script"() {
        setup:
        nodeBuilder.var {
            groovyconsole(JcrResourceConstants.NT_SLING_FOLDER)
        }

        def consoleService = slingContext.getService(GroovyConsoleService)

        def request = requestBuilder.build {
            parameterMap = this.parameterMap
        }

        def scriptData = new RequestScriptData(request)

        when:
        consoleService.saveScript(scriptData)

        then:
        assertNodeExists(PATH_SCRIPTS_FOLDER, JcrConstants.NT_FOLDER)
        assertNodeExists(PATH_FILE, JcrConstants.NT_FILE)
        assertNodeExists(PATH_FILE_CONTENT, JcrConstants.NT_RESOURCE,
            [(JcrConstants.JCR_MIMETYPE): "application/octet-stream"])

        and:
        assert session.getNode(PATH_FILE_CONTENT).get(JcrConstants.JCR_DATA).stream.text == scriptAsString
    }

    void assertScriptResult(map) {
        assert !map.result
        assert map.output == "BEER" + System.getProperty("line.separator")
        assert !map.exceptionStackTrace
        assert map.runningTime
    }

    private String getScriptAsString() {
        def scriptAsString = null

        this.class.getResourceAsStream("/$SCRIPT_FILE_NAME").withStream { stream ->
            scriptAsString = stream.text
        }

        scriptAsString
    }

    private Map<String, Object> getParameterMap() {
        [(GroovyConsoleConstants.PARAMETER_FILE_NAME): (SCRIPT_NAME), (PARAMETER_SCRIPT): scriptAsString]
    }
}