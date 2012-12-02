package com.citytechinc.cqlibrary.groovyconsole.servlets

import org.apache.felix.scr.annotations.Activate
import org.apache.felix.scr.annotations.Deactivate
import org.apache.felix.scr.annotations.Reference
import org.apache.felix.scr.annotations.sling.SlingServlet
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.SlingHttpServletResponse
import org.apache.sling.api.servlets.SlingAllMethodsServlet
import org.apache.sling.jcr.api.SlingRepository

import com.day.cq.commons.jcr.JcrConstants

@SlingServlet(paths = '/bin/groovyconsole/save', description = 'Writes script to nt:file node.')
class ScriptSavingServlet extends SlingAllMethodsServlet {

    static final long serialVersionUID = 1L

    static final String SCRIPT_FOLDER_REL_PATH = 'scripts'

    static final String CONSOLE_ROOT = '/etc/groovyconsole'

    static final String FILE_NAME_PARAM = 'fileName'

    static final String SCRIPT_CONTENT_PARAM = 'scriptContent'

    @Reference
    SlingRepository repository

    def session

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        def name = request.getParameter(FILE_NAME_PARAM)
        def script = request.getParameter(SCRIPT_CONTENT_PARAM)

        def binary = getScriptBinary(script)

        def folderNode = getScriptFolderNode()

        if (folderNode.hasNode(name)) {
            folderNode.getNode(name).remove()
        }

        def fileNode = folderNode.addNode(name, JcrConstants.NT_FILE)
        def resNode = fileNode.addNode(JcrConstants.JCR_CONTENT, JcrConstants.NT_RESOURCE)

        resNode.setProperty(JcrConstants.JCR_MIMETYPE, 'application/octet-stream')
        resNode.setProperty(JcrConstants.JCR_DATA, binary)

        session.save()

        binary.dispose()
    }

    def getScriptFolderNode() {
        def consoleNode = session.getNode(CONSOLE_ROOT)

        def scriptFolderNode

        if (consoleNode.hasNode(SCRIPT_FOLDER_REL_PATH)) {
            scriptFolderNode = consoleNode.getNode(SCRIPT_FOLDER_REL_PATH)
        } else {
            scriptFolderNode = consoleNode.addNode(SCRIPT_FOLDER_REL_PATH, JcrConstants.NT_FOLDER)
        }

        scriptFolderNode
    }

    def getScriptBinary(script) {
        def binary = null

		new ByteArrayInputStream(script.getBytes('UTF-8')).withStream { stream ->
			binary = session.valueFactory.createBinary(stream)
		}

        return binary
    }

    @Activate
    void activate() {
        session = repository.loginAdministrative(null)
    }

    @Deactivate
    void deactivate() {
        if (session) {
            session.logout()
        }
    }
}