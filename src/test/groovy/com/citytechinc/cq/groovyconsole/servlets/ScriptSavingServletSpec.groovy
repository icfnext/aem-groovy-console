package com.citytechinc.cq.groovyconsole.servlets

import com.day.cq.commons.jcr.JcrConstants
import spock.lang.Shared

import javax.jcr.RepositoryException

import static com.citytechinc.cq.groovyconsole.servlets.ScriptSavingServlet.getFILE_NAME_PARAM
import static com.citytechinc.cq.groovyconsole.servlets.ScriptSavingServlet.getSCRIPT_CONTENT_PARAM
import static com.citytechinc.cq.groovyconsole.servlets.ScriptSavingServlet.getSCRIPT_FOLDER_REL_PATH

class ScriptSavingServletSpec extends AbstractServletSpec {

    static final def SCRIPT_NAME = 'Script'

    static final def SCRIPT_FILE_NAME = "${SCRIPT_NAME}.groovy"

    static final def PATH_FOLDER = "/etc/groovyconsole/$SCRIPT_FOLDER_REL_PATH"

    static final def PATH_FILE = "$PATH_FOLDER/$SCRIPT_FILE_NAME"

    static final def PATH_FILE_CONTENT = "$PATH_FILE/${JcrConstants.JCR_CONTENT}"

    @Shared servlet

    @Shared script

    def setupSpec() {
        servlet = new ScriptSavingServlet()

        servlet.session = session

        script = getScriptAsString(SCRIPT_NAME)
    }

    def cleanup() {
        removeAllNodes()
    }

    def "save script"() {
        given: "mock request with file name and script parameters"
        def request = buildRequest()
        def response = responseBuilder.build()

        and: "create console root node"
        nodeBuilder.etc {
            groovyconsole()
        }

        when: "post to servlet"
        servlet.doPost(request, response)

        then: "script node has been created"
        assertNodeExists(PATH_FOLDER, JcrConstants.NT_FOLDER)
        assertNodeExists(PATH_FILE, JcrConstants.NT_FILE)
        assertNodeExists(PATH_FILE_CONTENT, JcrConstants.NT_RESOURCE, [(JcrConstants.JCR_MIMETYPE):
            "application/octet-stream"])

        assert session.getNode(PATH_FILE_CONTENT).get(JcrConstants.JCR_DATA).stream.text == script
    }

    def "missing console root node"() {
        given: "mock request with file name and script parameters"
        def request = buildRequest()
        def response = responseBuilder.build()

        when: "post to servlet"
        servlet.doPost(request, response)

        then: "exception getting console node"
        thrown(RepositoryException)
    }

    def buildRequest() {
        def parameterMap = [(FILE_NAME_PARAM): [(SCRIPT_NAME)], (SCRIPT_CONTENT_PARAM): [script]]

        requestBuilder.build {
            parameters parameterMap
        }
    }
}