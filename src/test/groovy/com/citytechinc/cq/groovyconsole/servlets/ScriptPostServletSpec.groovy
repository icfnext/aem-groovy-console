package com.citytechinc.cq.groovyconsole.servlets

import com.citytechinc.cq.testing.AbstractRepositorySpec
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.SlingHttpServletResponse
import spock.lang.Ignore
import spock.lang.Shared

@Ignore
class ScriptPostServletSpec extends AbstractRepositorySpec {

    @Shared servlet

    def setupSpec() {
        servlet = new ScriptPostServlet()

        servlet.session = session
    }

    def "run script"() {
        setup: "mock request with script parameter"
        def request = mockRequest()
        def response = mockResponse()

        when: "post to servlet"
        servlet.doPost(request, response)

        then: "script node has been created"

    }

    def mockRequest() {
        def request = Mock(SlingHttpServletRequest)

        request.getParameter(ScriptSavingServlet.FILE_NAME_PARAM) >> SCRIPT_NAME
        request.getParameter(ScriptSavingServlet.SCRIPT_CONTENT_PARAM) >> script

        request
    }

    def mockResponse() {
        Mock(SlingHttpServletResponse)
    }
}