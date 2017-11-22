package uk.gov.homeoffice.ctsv2.webscripts;

import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import uk.gov.homeoffice.cts.action.MinutesSyncAction;
import uk.gov.homeoffice.cts.helpers.CtsFolderHelper;
import uk.gov.homeoffice.ctsv2.model.CtsMinuteModel;

import java.io.IOException;

/**
 * WebScript that will return the system and manual minutes for a case
 * Created by dawudr on 11/05/2016
 */
    public class GetMinutesWebScript extends AbstractWebScript {
    private static final Logger LOGGER = LoggerFactory.getLogger(GetMinutesWebScript.class);

    private NodeService nodeService;
    private ContentService contentService;
    private MinutesSyncAction minutesSyncAction;
    private CtsFolderHelper ctsFolderHelper;

    @Override
    public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {
        LOGGER.debug("Running GetMinutesWebScript");
        long tResStart = System.currentTimeMillis();
        String jsonResponseStr = "";

        String nodeRefString = ctsFolderHelper.getNodeRef(req.getParameter("nodeRef"));
        NodeRef caseNode = new NodeRef(nodeRefString);

        if (hasAspect(caseNode, CtsMinuteModel.ASPECT_MINUTE)) {
            // Get Minutes Property
            ContentReader reader = contentService.getReader(caseNode, CtsMinuteModel.PROP_MINUTE_COLLATED);
            jsonResponseStr = reader.getContentString();
            LOGGER.debug("Found minute aspect and retrieving minutes property");
        } else {
            jsonResponseStr = minutesSyncAction.getMinutesCollatedJSONStr(caseNode);
        }

        long tResEnd = System.currentTimeMillis();
        long tResponse = tResEnd - tResStart;
        LOGGER.debug("Retrieving minutes property. Total Time: {}ms", tResponse);

        res.setContentType("application/json; charset=UTF-8");
        res.setContentEncoding("UTF-8");
        res.setHeader("Cache-Control", "must-revalidate,no-cache,no-store");
        res.getWriter().write(jsonResponseStr);
    }

    private boolean hasAspect(NodeRef nodeRef, QName qname) {
        boolean hasAspect = false;
        NodeRef.Status status = nodeService.getNodeStatus(nodeRef);
        if (status != null && !status.isDeleted()) {
            hasAspect = nodeService.hasAspect(nodeRef, qname);
        }
        return hasAspect;
    }

    public NodeService getNodeService() {
        return nodeService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public ContentService getContentService() {
        return contentService;
    }

    public void setContentService(ContentService contentService) {
        this.contentService = contentService;
    }

    public MinutesSyncAction getMinutesSyncAction() {
        return minutesSyncAction;
    }

    public void setMinutesSyncAction(MinutesSyncAction minutesSyncAction) {
        this.minutesSyncAction = minutesSyncAction;
    }

    public void setCtsFolderHelper(CtsFolderHelper ctsFolderHelper) { this.ctsFolderHelper = ctsFolderHelper; }

    public CtsFolderHelper getCtsFolderHelper() { return ctsFolderHelper; }
}
