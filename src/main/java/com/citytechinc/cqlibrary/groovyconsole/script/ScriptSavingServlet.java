package com.citytechinc.cqlibrary.groovyconsole.script;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.ValueFactory;
import javax.servlet.ServletException;

import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.jcr.api.SlingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.commons.servlets.HtmlStatusResponseHelper;

@SlingServlet(paths = "/bin/groovyconsole/save", methods = "POST", description = "Writes script to nt:file node")
public class ScriptSavingServlet extends SlingAllMethodsServlet {

    private static final long serialVersionUID = 1L;

    public static final String SCRIPT_FOLDER_REL_PATH = "scripts";

    public static final String CONSOLE_ROOT = "/etc/groovyconsole";

    private static final String FILE_NAME_PARAM = "fileName";

    private static final String SCRIPT_CONTENT_PARAM = "scriptContent";

    private final static Logger LOG = LoggerFactory.getLogger(ScriptSavingServlet.class);

    @Reference
    private SlingRepository repository;

    @Override
    protected final void doPost(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
        throws ServletException, IOException {

        final String fileName = request.getParameter(FILE_NAME_PARAM);

        if (null == fileName || fileName.trim().isEmpty()) {
            final String errStr = "POST error: Missing required " + FILE_NAME_PARAM + " parameter.";

            HtmlStatusResponseHelper.createStatusResponse(412, errStr).send(response, false);
        }

        // TODO: check for jcr-illegal characters in filename

        final String scriptContent = request.getParameter(SCRIPT_CONTENT_PARAM);

        if (null == scriptContent || scriptContent.trim().isEmpty()) {
            final String errStr = "POST error: Missing required " + SCRIPT_CONTENT_PARAM + " parameter.";

            HtmlStatusResponseHelper.createStatusResponse(412, errStr).send(response, false);
        }

        Session session = null;

        try {
            session = repository.loginAdministrative(null);

            final Binary scriptBinary = getScriptBinary(session, scriptContent);

            final Node folderNode = getScriptFolderNode(session);

            if (folderNode.hasNode(fileName)) {
                // TODO: prompt for overwrite?
                folderNode.getNode(fileName).remove();
            }

            final Node fileNode = folderNode.addNode(fileName, JcrConstants.NT_FILE);
            final Node resNode = fileNode.addNode(JcrConstants.JCR_CONTENT, JcrConstants.NT_RESOURCE);

            resNode.setProperty(JcrConstants.JCR_MIMETYPE, "application/octet-stream");
            resNode.setProperty(JcrConstants.JCR_DATA, scriptBinary);

            session.save();
        } catch (final RepositoryException e) {
            LOG.error("error creating script node", e);
        } finally {
            if (null != session) {
                session.logout();
            }
        }
    }

    private Node getScriptFolderNode(final Session session) throws RepositoryException {
        if (!session.nodeExists(CONSOLE_ROOT)) {
            throw new RuntimeException("Missing expected groovy console root node");
        }

        final Node consoleNode = session.getNode(CONSOLE_ROOT);

        if (consoleNode.hasNode(SCRIPT_FOLDER_REL_PATH)) {
            return consoleNode.getNode(SCRIPT_FOLDER_REL_PATH);
        } else {
            final Node folderNode = consoleNode.addNode(SCRIPT_FOLDER_REL_PATH, JcrConstants.NT_FOLDER);

            session.save();

            return folderNode;
        }
    }

    private Binary getScriptBinary(final Session session, final String scriptContent) throws RepositoryException {
        ByteArrayInputStream scriptStream = null;

        try {
            final ValueFactory valueFactory = session.getValueFactory();

            scriptStream = new ByteArrayInputStream(scriptContent.getBytes("UTF-8"));

            return valueFactory.createBinary(scriptStream);
        } catch (final UnsupportedEncodingException ex) {
            throw new RepositoryException(ex);
        } finally {
            try {
                scriptStream.close();
            } catch (final IOException ignored) {

            }
        }
    }
}
