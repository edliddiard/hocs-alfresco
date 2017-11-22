package uk.gov.homeoffice.cts.template;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.domain.node.ContentDataWithId;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.*;
import org.odftoolkit.simple.TextDocument;
import java.io.IOException;

/**
 * Created by davidt on 10/11/2014.
 */
public class GetPopulatedTemplateWebScript extends AbstractWebScript {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetPopulatedTemplateWebScript.class);
    private static final String ODT_MIMETYPE = "application/vnd.oasis.opendocument.text";

    private NodeService nodeService;
    private TemplateManipulator templateManipulator;
    private ServiceRegistry serviceRegistry;

    @Override
    public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {
        LOGGER.debug("Running GetPopulatedTemplateWebScript");
        String templateNodeRefString = req.getParameter("documentNodeRef");
        NodeRef templateNodeRef = new NodeRef(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, templateNodeRefString);
        String caseNodeRefString = req.getParameter("caseNodeRef");
        NodeRef caseNodeRef = null;
        if (caseNodeRefString != null) {
            caseNodeRef = new NodeRef(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, caseNodeRefString);
        }
        String mimeType = getMimeType(templateNodeRef);
        String fileName = getFilename(templateNodeRef);
        if (mimeType.equalsIgnoreCase(ODT_MIMETYPE) && caseNodeRef != null) {
            LOGGER.info("Populating template "+templateNodeRef.toString()+" with details from case "+caseNodeRef.toString());
            templateManipulator.setTemplateNodeRef(templateNodeRef);
            templateManipulator.setCaseNodeRef(caseNodeRef);
            TextDocument populatedDocument = templateManipulator.populate();
            output(res, null, populatedDocument, mimeType, fileName);
        } else {
            LOGGER.info("Returning unpopulated template "+templateNodeRef.toString());
            ContentReader reader = serviceRegistry.getContentService().getReader(templateNodeRef, ContentModel.PROP_CONTENT);
            output(res, reader, null, mimeType, fileName);
        }
    }

    private String getMimeType(NodeRef templateNodeRef) {
        ContentDataWithId content = (ContentDataWithId)nodeService.getProperty(templateNodeRef, ContentModel.PROP_CONTENT);
        return content.getMimetype();
    }

    private String getFilename(NodeRef templateNodeRef) {
        return (String)nodeService.getProperty(templateNodeRef, ContentModel.PROP_NAME);
    }

    private void output(WebScriptResponse res, ContentReader standardTemplate, TextDocument populatedTemplate, String mimeType, String fileName) {
        res.setContentType(mimeType);
        res.setHeader("content-disposition","filename="+fileName);
        // stream back
        try {
            if (standardTemplate != null) {
                standardTemplate.getContent(res.getOutputStream());
            }
            if (populatedTemplate != null) {
                populatedTemplate.save(res.getOutputStream());
            }
        } catch (Exception ex) {
            String errorMessage = "Unable to stream output for template with file name "+fileName+" and mime type "+mimeType;
            LOGGER.error(errorMessage+". Error: "+ex.getMessage());
            throw new WebScriptException(errorMessage);
        } finally {
            try {
                if (res.getOutputStream() != null) {
                    res.getOutputStream().close();
                }
            } catch (IOException e) {
                LOGGER.error("Failed to close output stream. Error: "+e.getMessage());
            }
            populatedTemplate.close();
        }
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setServiceRegistry(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    public void setTemplateManipulator(TemplateManipulator templateManipulator) {
        this.templateManipulator = templateManipulator;
    }
}
