package uk.gov.homeoffice.cts.webscripts;

import org.alfresco.service.cmr.repository.*;
import org.alfresco.service.cmr.search.SearchService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import uk.gov.homeoffice.cts.model.CtsModel;
import java.util.*;

public class PostUngroupCasesWebScript extends DeclarativeWebScript {
    private static final Logger LOGGER = LoggerFactory.getLogger(PostUngroupCasesWebScript.class);
    private NodeService nodeService;
    private SearchService searchService;

    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
        LOGGER.info("Running PostUngroupCasesWebScript");
        super.executeImpl(req, status, cache);
        Map<String, Object> model = new HashMap<>();

        String masterNodeRefString = req.getParameter("masterNodeRef");
        String slaveNodeRefList = req.getParameter("slaveNodeRefList");

        if (masterNodeRefString == null || slaveNodeRefList == null) {
            LOGGER.error("masterNodeRef and slaveNodeRefList must be set on the request.");
            status.setCode(Status.STATUS_INTERNAL_SERVER_ERROR, "masterNodeRef and slaveNodeRefList must be set on the request.");
            return model;
        }

        // validate and get master node ref
        NodeRef masterNodeRef = null;
        try {
            masterNodeRef = getNodeRef(masterNodeRefString);
        } catch (Exception e) {
            LOGGER.error("No valid master reference was submitted: " + masterNodeRefString);
            status.setCode(Status.STATUS_INTERNAL_SERVER_ERROR, e.getMessage());
            return model;
        }

        // split slave node ref list and get their nodeRefs
        List<NodeRef> slaveNodeRefs = getSlaveNodeRefs(slaveNodeRefList);

        if (slaveNodeRefs.isEmpty()) {
            LOGGER.info("No valid slave node refs found");
            status.setCode(Status.STATUS_BAD_REQUEST, "No valid UINs found to ungroup");
            return model;
        }

        // unassociate the slaves from the master
        try {
            unassociateGroupedCases(masterNodeRef, slaveNodeRefs);
        } catch (Exception e) {
            LOGGER.error("Unexpected error creating unassociating grouped cases", e);
            status.setCode(Status.STATUS_INTERNAL_SERVER_ERROR, e.getMessage());
            return model;
        }

        status.setCode(Status.STATUS_CREATED);
        return model;
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

    private List<NodeRef> getSlaveNodeRefs(String slaveNodeRefListString) {
        String[] nodeRefList = slaveNodeRefListString.split(",");
        List<NodeRef> nodeRefs = new ArrayList<>();
        for (String nodeRefString : nodeRefList) {
            NodeRef nodeRef = new NodeRef(nodeRefString.trim());
            nodeRefs.add(nodeRef);
        }
        return nodeRefs;
    }

    private void unassociateGroupedCases(NodeRef masterNodeRef, List<NodeRef> slaveNodeRefs) {
        List<String> removedSlaveNotes = new ArrayList<>();
        for (NodeRef slaveNodeRef : slaveNodeRefs) {
            try {
                nodeService.removeAssociation(masterNodeRef, slaveNodeRef, CtsModel.ASSOC_GROUPED_CASES);
            } catch (InvalidNodeRefException e) {
                LOGGER.info("Cannot remove association between " + masterNodeRef.toString() + " and " + slaveNodeRef + ", invalid node ref.");
            }
            nodeService.setProperty(slaveNodeRef, CtsModel.PROP_IS_GROUPED_SLAVE, Boolean.FALSE.toString());
            nodeService.setProperty(slaveNodeRef, CtsModel.PROP_MASTER_NODE_REF, null);
            // set properties when slaves / masters are removed to show in the minutes
            removedSlaveNotes.add("Slave removed "+new Date().toString()+" "+slaveNodeRef);
            nodeService.setProperty(slaveNodeRef, CtsModel.PROP_MASTER_REMOVED, "Master removed "+new Date().toString()+" "+masterNodeRef);
        }
        nodeService.setProperty(masterNodeRef, CtsModel.PROP_SLAVE_REMOVED, StringUtils.join(removedSlaveNotes, ';'));

        List<AssociationRef> groupedCaseAssocRefList = nodeService.getTargetAssocs(masterNodeRef, CtsModel.ASSOC_GROUPED_CASES);
        if (groupedCaseAssocRefList.isEmpty()) {
            nodeService.setProperty(masterNodeRef, CtsModel.PROP_IS_GROUPED_MASTER, Boolean.FALSE.toString());
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
