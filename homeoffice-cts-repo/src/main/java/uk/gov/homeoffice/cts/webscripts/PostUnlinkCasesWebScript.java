package uk.gov.homeoffice.cts.webscripts;

import org.alfresco.service.cmr.repository.*;
import org.alfresco.service.cmr.search.QueryConsistency;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.search.SearchParameters;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import uk.gov.homeoffice.cts.model.CtsModel;
import uk.gov.homeoffice.cts.exceptions.GroupCasesException;
import java.util.*;

public class PostUnlinkCasesWebScript extends DeclarativeWebScript {
    private static final Logger loggerInstance = LoggerFactory.getLogger(PostUnlinkCasesWebScript.class);
    private NodeService nodeService;
    private SearchService searchService;

    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
        loggerInstance.info("Running PostUnlinkCasesWebScript");
        super.executeImpl(req, status, cache);
        Map<String, Object> model = new HashMap<>();

        String masterNodeRefString = req.getParameter("masterNodeRef");
        String linkedHrnList = req.getParameter("linkedHrnList");

        if (masterNodeRefString == null || linkedHrnList == null) {
            loggerInstance.error("masterNodeRef and linkedHrnList must be set on the request.");
            status.setCode(
                Status.STATUS_INTERNAL_SERVER_ERROR,
                "masterNodeRef and linkedHrnList must be set on the request."
            );

            return model;
        }

        // validate and get master node ref
        NodeRef masterNodeRef = null;
        try {
            masterNodeRef = getNodeRef(masterNodeRefString);
        } catch (Exception e) {
            loggerInstance.error("No valid master reference was submitted: " + masterNodeRefString);
            status.setCode(Status.STATUS_INTERNAL_SERVER_ERROR, e.getMessage());
            return model;
        }

        // split UIN list and get their nodeRefs
        List<NodeRef> linkedNodeRefs = getLinkedHrnNodeRefs(linkedHrnList);

        if (linkedNodeRefs.isEmpty()) {
            loggerInstance.info("No valid HRNs found");
            status.setCode(Status.STATUS_BAD_REQUEST, "No valid HRNs found");

            return model;
        }

        // unassociate the slaves from the master
        try {
            unassociateLinkedCases(masterNodeRef, linkedNodeRefs);
        } catch (Exception e) {
            loggerInstance.error("Unexpected error unassociating linked cases", e);
            status.setCode(Status.STATUS_INTERNAL_SERVER_ERROR, e.getMessage());

            return model;
        }

        status.setCode(Status.STATUS_CREATED);

        return model;
    }

    private List<NodeRef> getLinkedHrnNodeRefs(String linkedHrnListString) {
        String[] nodeRefList = linkedHrnListString.split(",");
        ArrayList nodeRefs = new ArrayList();

        for (int i = 0; i < nodeRefList.length; ++i) {
            String nodeRefString = nodeRefList[i];
            NodeRef nodeRef = new NodeRef(nodeRefString.trim());
            nodeRefs.add(nodeRef);
        }

        return nodeRefs;
    }

    private NodeRef getNodeRef(String nodeRefString) throws Exception {
        // check masterNodeRef exists and it is a cts:case
        if (!NodeRef.isNodeRef(nodeRefString)) {
            throw new Exception(nodeRefString + " is not a valid nodeRef.");
        }
        NodeRef nodeRef = new NodeRef(nodeRefString);
        if (!nodeService.getType(nodeRef).isMatch(CtsModel.TYPE_CTS_CASE)) {
            throw new Exception(nodeRefString + " is not a cts:case nodeRef.");
        }
        return nodeRef;
    }

    private void unassociateLinkedCases(NodeRef masterNodeRef, List<NodeRef> linkedNodeRefs) {

        List<String> removedLinkedCases = new ArrayList<>();

        for (NodeRef linkedNodeRef : linkedNodeRefs) {
            try {

                //Relationship is single sided, we need to unset it from both sides as we are not sure which side
                //contains the reference
                nodeService.removeAssociation(
                    masterNodeRef,
                    linkedNodeRef,
                    CtsModel.ASSOC_LINKED_CASES
                );

                nodeService.removeAssociation(
                    linkedNodeRef,
                    masterNodeRef,
                    CtsModel.ASSOC_LINKED_CASES
                );

            } catch (InvalidNodeRefException e) {

                loggerInstance.info(
                    "Cannot remove association between " +
                    masterNodeRef.toString() +
                    " and " +
                    linkedNodeRef +
                    ", invalid node ref."
                );
            }

            nodeService.setProperty(linkedNodeRef, CtsModel.PROP_IS_LINKED_CASE, Boolean.FALSE.toString());
            // set properties when slaves / masters are removed to show in the minutes
            removedLinkedCases.add("Linked case removed " + new Date().toString() + " "+linkedNodeRef);

            nodeService.setProperty(
                linkedNodeRef,
                CtsModel.PROP_LINK_CASE_REMOVED,
                "Linked case removed " + new Date().toString() + " " + masterNodeRef
            );
        }

        nodeService.setProperty(
            masterNodeRef,
            CtsModel.PROP_LINK_CASE_REMOVED,
            StringUtils.join(removedLinkedCases, ';')
        );

        List<AssociationRef> linkedCaseAssocRefList =
                nodeService.getTargetAssocs(masterNodeRef, CtsModel.ASSOC_LINKED_CASES);

        if (linkedCaseAssocRefList.isEmpty()) {
            nodeService.setProperty(masterNodeRef, CtsModel.PROP_IS_LINKED_CASE, Boolean.FALSE.toString());
        }
    }

    private NodeService getNodeService() {
        return nodeService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public SearchService getSearchService() {
        return searchService;
    }

    public void setSearchService(SearchService searchService) {
        this.searchService = searchService;
    }
}
