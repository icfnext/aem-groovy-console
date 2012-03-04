package com.citytechinc.cqlibrary.groovyconsole.script;

import com.day.cq.commons.servlets.HtmlStatusResponseHelper;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.ValueFactory;
import javax.servlet.ServletException;
import org.apache.felix.scr.annotations.*;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.jcr.api.SlingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component
@Service
@Properties({
    @Property(name = "sling.servlet.methods", value = "POST"),
    @Property(name = "sling.servlet.paths", value = "/bin/groovyconsole/save"),
    @Property(name = "service.description", value = "Writes script to nt:file node")
})
public class ScriptSavingServlet extends SlingAllMethodsServlet {
    
    public static final String SCRIPT_FOLDER_REL_PATH = "scripts";
    public static final String CONSOLE_ROOT = "/etc/groovyconsole";
    
    private final static Logger LOG = LoggerFactory.getLogger(ScriptSavingServlet.class);
    
    @Reference
    private SlingRepository repository;
    
    private static final String FILE_NAME_PARAM = "fileName";
    private static final String SCRIPT_CONTENT_PARAM = "scriptContent";
	
    @Override
    protected final void doPost(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
        throws ServletException, IOException 
    {
        
        String fileName = request.getParameter(FILE_NAME_PARAM);
        if (null == fileName || fileName.trim().isEmpty()) {
            String errStr = "POST error: Missing required '${FILE_NAME_PARAM}' parameter.";
            HtmlStatusResponseHelper.createStatusResponse(412, errStr).send(response, false);
        }
        
        // TODO: check for jcr-illegal characters in filename
        
        String scriptContent = request.getParameter(SCRIPT_CONTENT_PARAM);
        if (null == scriptContent || scriptContent.trim().isEmpty()) {
            String errStr = "POST error: Missing required '${SCRIPT_CONTENT_PARAM}' parameter.";
            HtmlStatusResponseHelper.createStatusResponse(412, errStr).send(response, false);
        }
        
        Session session = null;
        
        try {
            session = repository.loginAdministrative(null);
            
            Binary scriptBinary = getScriptBinary(session, scriptContent);
            
            Node folderNode = getScriptFolderNode(session);
            
            if (folderNode.hasNode(fileName)) {
                // TODO: prompt for overwrite?
                folderNode.getNode(fileName).remove();
            }
            
            Node fileNode = folderNode.addNode(fileName, "nt:file");
            Node resNode = fileNode.addNode ("jcr:content", "nt:resource");
            resNode.setProperty ("jcr:mimeType", "application/octet-stream");
            resNode.setProperty ("jcr:data", scriptBinary);
            
            session.save();
        } catch (RepositoryException e) {
            LOG.error("Problem creating script node: ", e);
        } finally {
            if (null != session) {
                session.logout();
            }
        }
    }
    
    private Node getScriptFolderNode(Session session) throws RepositoryException {     
        if (!session.nodeExists(CONSOLE_ROOT)) {
            throw new RuntimeException("Missing expected groovy console ");
        }
        Node consoleNode = session.getNode(CONSOLE_ROOT);
        
        if (consoleNode.hasNode(SCRIPT_FOLDER_REL_PATH)) {
            return consoleNode.getNode(SCRIPT_FOLDER_REL_PATH);
        } else {
            Node folderNode = consoleNode.addNode(SCRIPT_FOLDER_REL_PATH, "nt:folder");
            session.save();
            return folderNode;
        }
    }
    
    private Binary getScriptBinary(Session session, String scriptContent) throws RepositoryException {
        ByteArrayInputStream scriptStream = null;
        try {
            ValueFactory valueFactory = session.getValueFactory();
            scriptStream = new ByteArrayInputStream(scriptContent.getBytes("UTF-8"));
            return valueFactory.createBinary(scriptStream);
        } catch (UnsupportedEncodingException ex) {
            throw new RepositoryException(ex);
        } finally {
            try {
                scriptStream.close();
            } catch (IOException ignored) {

            }
        }
    }
}

