package com.citytechinc.cq.groovyconsole.servlets

import com.day.cq.commons.jcr.JcrConstants
import org.apache.jackrabbit.util.Text
import org.apache.sling.api.servlets.SlingAllMethodsServlet

abstract class AbstractScriptServlet extends SlingAllMethodsServlet {

    static final def ENCODING = "UTF-8"

    void saveFile(session, folderNode, fileName, mimetype, binary) {
        def fileNode = folderNode.addNode(Text.escapeIllegalJcrChars(fileName), JcrConstants.NT_FILE)

        def resourceNode = fileNode.addNode(JcrConstants.JCR_CONTENT, JcrConstants.NT_RESOURCE)

        resourceNode.set(JcrConstants.JCR_MIMETYPE, mimetype)
        resourceNode.set(JcrConstants.JCR_ENCODING, ENCODING)
        resourceNode.set(JcrConstants.JCR_DATA, binary)
        resourceNode.set(JcrConstants.JCR_LASTMODIFIED, new Date().time)
        resourceNode.set(JcrConstants.JCR_LAST_MODIFIED_BY, session.userID)

        session.save()
    }
}
