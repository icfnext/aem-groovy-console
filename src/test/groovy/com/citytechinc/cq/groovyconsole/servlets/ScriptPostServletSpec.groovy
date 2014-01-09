package com.citytechinc.cq.groovyconsole.servlets
import com.citytechinc.cq.groovy.extension.services.OsgiComponentService
import com.citytechinc.cq.groovyconsole.services.ConfigurationService
import com.citytechinc.cq.groovyconsole.services.EmailService
import com.citytechinc.cq.groovyconsole.services.GroovyRunService
import com.citytechinc.cq.groovyconsole.services.impl.GroovyRunServiceImpl
import com.day.cq.replication.Replicator
import com.day.cq.search.QueryBuilder
import groovy.json.JsonSlurper
import org.osgi.framework.BundleContext
import spock.lang.Shared

import static com.citytechinc.cq.groovyconsole.servlets.ScriptPostServlet.SCRIPT_PARAM

class ScriptPostServletSpec extends AbstractServletSpec {

    @Shared servlet

    @Shared service

    @Shared script

    def setupSpec() {
        servlet = new ScriptPostServlet()

        service = new GroovyRunServiceImpl();

        service.replicator = Mock(Replicator)
        service.componentService = Mock(OsgiComponentService)
        service.bundleContext = Mock(BundleContext)
        service.configurationService = Mock(ConfigurationService)
        service.queryBuilder = Mock(QueryBuilder)
        service.emailService = Mock(EmailService)

        servlet.groovyRunService = service;

        script = getScriptAsString("Script")
    }

    def "run script"() {
        given: "mock request with script parameter"
        def parameterMap = [(SCRIPT_PARAM): [script]]

        def request = requestBuilder.build {
            parameters parameterMap
        }

        def response = responseBuilder.build()

        when: "post to servlet"
        servlet.doPost(request, response)

        then: "script is executed"
        assertJsonResponse(response)
    }

    void assertJsonResponse(response) {
        def json = new JsonSlurper().parseText(response.output.toString())

        assert !json.executionResult
        assert json.outputText == "BEER\n"
        assert !json.stacktraceText
        assert json.runningTime
    }
}