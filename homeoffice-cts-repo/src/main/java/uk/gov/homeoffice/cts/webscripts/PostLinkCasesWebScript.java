package uk.gov.homeoffice.cts.webscripts;

import org.alfresco.service.cmr.repository.*;
import org.alfresco.service.cmr.search.QueryConsistency;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import uk.gov.homeoffice.cts.exceptions.GroupCasesException;
import uk.gov.homeoffice.cts.model.CtsModel;

import java.util.*;

public class PostLinkCasesWebScript extends DeclarativeWebScript {
    private static final Logger LOGGER = LoggerFactory.getLogger(PostLinkCasesWebScript.class);
    private NodeService nodeService;
    private SearchService searchService;

    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
        LOGGER.info("Running PostLinkCasesWebScript");
        super.executeImpl(req, status, cache);
        Map<String, Object> model = new HashMap<>();

        String masterNodeRefString = req.getParameter("masterNodeRef");
        String linkedHrnList = req.getParameter("linkedHrnList");

        if (masterNodeRefString == null || linkedHrnList == null) {
            LOGGER.error("masterNodeRef and linkedHrnList must be set on the request.");
            status.setCode(Status.STATUS_INTERNAL_SERVER_ERROR, "masterNodeRef and linkedHrnList must be set on the request.");
            return model;
        }

        // validate and get master node ref
        NodeRef masterNodeRef = null;
        try {
            masterNodeRef = getMasterNodeRef(masterNodeRefString);
        } catch (Exception e) {
            LOGGER.error("No valid master reference was submitted: " + masterNodeRefString);
            status.setCode(Status.STATUS_INTERNAL_SERVER_ERROR, e.getMessage());
            return model;
        }

        // split UIN list and get their nodeRefs
        List<NodeRef> linkedNodeRefs;
        try {
            linkedNodeRefs = getLinkedHrnNodeRefs(linkedHrnList, masterNodeRef);
        } catch (GroupCasesException e) {
            LOGGER.info("Invalid link HRNs found: " + StringUtils.join(e.getErrorMessages(), ", "));
            status.setCode(Status.STATUS_BAD_REQUEST, StringUtils.join(e.getErrorMessages(), "\n"));
            return model;
        }

        if (linkedNodeRefs.isEmpty()) {
            LOGGER.info("No valid HRNs found");
            status.setCode(Status.STATUS_BAD_REQUEST, "No valid HRNs found");
            return model;
        }

        // associate the slaves to the master
        try {
            associateLinkedCases(masterNodeRef, linkedNodeRefs);
        } catch (Exception e) {
            LOGGER.error("Unexpected error creating associating linked cases", e);
            status.setCode(Status.STATUS_INTERNAL_SERVER_ERROR, e.getMessage());
            return model;
        }

        status.setCode(Status.STATUS_CREATED);
        return model;
    }

    private NodeRef getMasterNodeRef(String masterNodeRefString) throws Exception {
        // check masterNodeRef exists and it is a cts:case
        if (!NodeRef.isNodeRef(masterNodeRefString)) {
            throw new Exception(masterNodeRefString + " is not a valid nodeRef.");
        }
        NodeRef masterNodeRef = new NodeRef(masterNodeRefString);
        if (!nodeService.getType(masterNodeRef).isMatch(CtsModel.TYPE_CTS_CASE)) {
            throw new Exception(masterNodeRefString + " is not a cts:case nodeRef.");
        }
        return masterNodeRef;
    }

    private List<NodeRef> getLinkedHrnNodeRefs(String linkedHrnListString, NodeRef masterNodeRef) throws GroupCasesException {
        String[] hrnList = linkedHrnListString.split(",");
        List<NodeRef> nodeRefs = new ArrayList<>();
        List<String> invalidHrns = new ArrayList<>();
        for (String hrn : hrnList) {
            String hrnSuffix = hrn.substring(hrn.indexOf('/')+1);
            SearchParameters searchParameters = new SearchParameters();
            searchParameters.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
            searchParameters.setLanguage(SearchService.LANGUAGE_CMIS_ALFRESCO);
            String query = "SELECT * FROM cts:case where cts:urnSuffix" + " = '" + hrnSuffix.trim()+"'";
            searchParameters.setQuery(query);
            //this makes it use the database
            searchParameters.setQueryConsistency(QueryConsistency.TRANSACTIONAL);
            ResultSet resultSet = getSearchService().query(searchParameters);
            if (resultSet.length() == 1) {
                NodeRef nodeRef = resultSet.getNodeRef(0);
                if (nodeRef.equals(masterNodeRef)) {
                    invalidHrns.add(hrn + ": cannot link self");
                } else {
                    nodeRefs.add(nodeRef);
                }
            } else if (resultSet.length() == 0) {
                invalidHrns.add(hrn + ": not found");
            }
        }
        if (invalidHrns.size() > 0) {
            throw new GroupCasesException("Invalid HRNs found", invalidHrns);
        }
        return nodeRefs;
    }

    private void associateLinkedCases(NodeRef masterNodeRef, List<NodeRef> linkedNodeRefs) {
        List<String> addedLinkCaseNotes = new ArrayList<>();
        for (NodeRef linkedNodeRef : linkedNodeRefs) {
            try {
                nodeService.createAssociation(masterNodeRef, linkedNodeRef, CtsModel.ASSOC_LINKED_CASES);
            } catch (AssociationExistsException e) {
                LOGGER.info("Association between " + masterNodeRef.toString() + " and " + linkedNodeRef + " already exists.");
            }
            // set properties when slaves / masters are associated to show in the minutes
            addedLinkCaseNotes.add("Link case added "+new Date().toString()+" "+linkedNodeRef);
            nodeService.setProperty(linkedNodeRef, CtsModel.PROP_LINK_CASE_ADDED, "Link case added "+new Date().toString()+" "+masterNodeRef);
            nodeService.setProperty(linkedNodeRef, CtsModel.PROP_IS_LINKED_CASE, Boolean.TRUE);
        }
        nodeService.setProperty(masterNodeRef, CtsModel.PROP_LINK_CASE_ADDED, StringUtils.join(addedLinkCaseNotes, ';'));
        nodeService.setProperty(masterNodeRef, CtsModel.PROP_IS_LINKED_CASE, Boolean.TRUE);
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
