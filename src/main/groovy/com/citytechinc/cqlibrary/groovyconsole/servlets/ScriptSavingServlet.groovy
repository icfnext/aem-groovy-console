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

@SlingServlet(paths = "/bin/groovyconsole/save", methods = "POST", description = "Writes script to nt:file node.")
class ScriptSavingServlet extends SlingAllMethodsServlet {

    private static final long serialVersionUID = 1L

    private static final String SCRIPT_FOLDER_REL_PATH = "scripts"

    private static final String CONSOLE_ROOT = "/etc/groovyconsole"

    private static final String FILE_NAME_PARAM = "fileName"

    private static final String SCRIPT_CONTENT_PARAM = "scriptContent"

    @Reference
    private SlingRepository repository

    private def session

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

        resNode.set(JcrConstants.JCR_MIMETYPE, "application/octet-stream")
        resNode.set(JcrConstants.JCR_DATA, binary)

        session.save()

        binary.dispose()
    }

    private def getScriptFolderNode() {
        def consoleNode = session.getNode(CONSOLE_ROOT)

        consoleNode.getOrAddNode(SCRIPT_FOLDER_REL_PATH, JcrConstants.NT_FOLDER)
    }

    private def getScriptBinary(script) {
        def valueFactory = session.valueFactory

        def stream = null
        def binary = null

        try {
            stream = new ByteArrayInputStream(script.getBytes("UTF-8"))

            binary = valueFactory.createBinary(stream)
        } finally {
            if (stream) {
                stream.close()
            }
        }

        return binary
    }

    @Activate
    private void activate() {
        session = repository.loginAdministrative(null)
    }

    @Deactivate
    private void deactivate() {
        if (session) {
            session.logout()
        }
    }
}