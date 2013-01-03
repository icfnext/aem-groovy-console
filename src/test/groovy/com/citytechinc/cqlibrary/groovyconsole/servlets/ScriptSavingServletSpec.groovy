package com.citytechinc.cqlibrary.groovyconsole.servlets

import javax.jcr.RepositoryException

import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.SlingHttpServletResponse

import spock.lang.Shared

import com.citytechinc.cqlibrary.groovyconsole.AbstractRepositorySpec
import com.day.cq.commons.jcr.JcrConstants

class ScriptSavingServletSpec extends AbstractRepositorySpec {

    static final def SCRIPT_NAME = 'Script'

    static final def SCRIPT_FILE_NAME = "${SCRIPT_NAME}.groovy"

    @Shared servlet

    @Shared script

    def setupSpec() {
        servlet = new ScriptSavingServlet()

        servlet.session = session

        this.class.getResourceAsStream("/$SCRIPT_FILE_NAME").withStream { stream ->
            script = stream.text
        }
    }

    def "save script"() {
        setup: "mock request with file name and script parameters"
        def request = mockRequest()
        def response = mockResponse()

        and: "create console root node"
        session.rootNode.getOrAddNode(ScriptSavingServlet.CONSOLE_ROOT.substring(1))
        session.save()

        when: "post to servlet"
        servlet.doPost(request, response)

        then: "script node has been created"
        scriptNodeCreated()
    }

    def "missing console root node"() {
        setup: "mock request with file name and script parameters"
        def request = mockRequest()
        def response = mockResponse()

        when: "post to servlet"
        servlet.doPost(request, response)

        then: "exception getting console node"
        thrown(RepositoryException)
    }

    void scriptNodeCreated() {
        def consoleNode = session.getNode(ScriptSavingServlet.CONSOLE_ROOT)

        assert consoleNode.hasNode(ScriptSavingServlet.SCRIPT_FOLDER_REL_PATH)

        def folderNode = consoleNode.getNode(ScriptSavingServlet.SCRIPT_FOLDER_REL_PATH)

        assert folderNode.primaryNodeType.name == JcrConstants.NT_FOLDER
        assert folderNode.hasNode(SCRIPT_FILE_NAME)

        def fileNode = folderNode.getNode(SCRIPT_FILE_NAME)

        assert fileNode.primaryNodeType.name == JcrConstants.NT_FILE
        assert fileNode.hasNode(JcrConstants.JCR_CONTENT)

        def contentNode = fileNode.getNode(JcrConstants.JCR_CONTENT)

        assert contentNode.primaryNodeType.name == JcrConstants.NT_RESOURCE
        assert contentNode.get(JcrConstants.JCR_MIMETYPE) == "application/octet-stream"
        assert contentNode.get(JcrConstants.JCR_DATA).stream.text == script
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