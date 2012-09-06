package com.citytechinc.cqlibrary.groovyconsole.servlets

import com.citytechinc.cqlibrary.groovyconsole.AbstractRepositorySpec
import com.citytechinc.cqlibrary.groovyconsole.metaclass.GroovyConsoleMetaClassRegistry

import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.SlingHttpServletResponse

import com.day.cq.commons.jcr.JcrConstants

import javax.jcr.RepositoryException

import spock.lang.Shared

class ScriptSavingServletTest extends AbstractRepositorySpec {

    static final def SCRIPT_NAME = 'Script.groovy'

    @Shared servlet

    def setupSpec() {
        GroovyConsoleMetaClassRegistry.registerNodeMetaClass()

        servlet = new ScriptSavingServlet()

        servlet.session = session
    }

    def "save script"() {
        setup: "mock request with file name and script parameters"
        def request = Mock(SlingHttpServletRequest)
        def response = Mock(SlingHttpServletResponse)

        def stream = this.class.getResourceAsStream("/$SCRIPT_NAME")
        def script = stream.text

        request.getParameter(ScriptSavingServlet.FILE_NAME_PARAM) >> SCRIPT_NAME
        request.getParameter(ScriptSavingServlet.SCRIPT_CONTENT_PARAM) >> script

        stream.close()

        and: "create console node"
        def consoleNode = session.rootNode.getOrAddNode(ScriptSavingServlet.CONSOLE_ROOT.substring(1))

        session.save()

        when: "post to servlet"
        servlet.doPost(request, response)

        then: "script node has been created"
        scriptNodeCreated(consoleNode, SCRIPT_NAME, script)

        cleanup: "remove test nodes"
        session.getNode('/etc').remove()
        session.save()
    }

    void scriptNodeCreated(consoleNode, fileName, script) {
        assert consoleNode.hasNode(ScriptSavingServlet.SCRIPT_FOLDER_REL_PATH)

        def folderNode = consoleNode.getNode(ScriptSavingServlet.SCRIPT_FOLDER_REL_PATH)

        assert folderNode.primaryNodeType.name == JcrConstants.NT_FOLDER
        assert folderNode.hasNode(fileName)

        def fileNode = folderNode.getNode(fileName)

        assert fileNode.primaryNodeType.name == JcrConstants.NT_FILE
        assert fileNode.hasNode(JcrConstants.JCR_CONTENT)

        def contentNode = fileNode.getNode(JcrConstants.JCR_CONTENT)

        assert contentNode.primaryNodeType.name == JcrConstants.NT_RESOURCE
        assert contentNode.get(JcrConstants.JCR_MIMETYPE) == "application/octet-stream"
        assert contentNode.get(JcrConstants.JCR_DATA).stream.text == script
    }

    def "missing console root node"() {
        setup: "mock request with file name and script parameters"
        def request = Mock(SlingHttpServletRequest)
        def response = Mock(SlingHttpServletResponse)

        request.getParameter(ScriptSavingServlet.FILE_NAME_PARAM) >> SCRIPT_NAME

        this.class.getResourceAsStream("/$SCRIPT_NAME").withStream {
            request.getParameter(ScriptSavingServlet.SCRIPT_CONTENT_PARAM) >> it.text
        }

        when: "post to servlet"
        servlet.doPost(request, response)

        then: "exception getting console node"
        thrown(RepositoryException)
    }
}