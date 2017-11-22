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
import uk.gov.homeoffice.cts.model.CtsModel;
import java.util.*;
import uk.gov.homeoffice.cts.exceptions.GroupCasesException;

public class PostGroupCasesWebScript extends DeclarativeWebScript {
    private static final Logger LOGGER = LoggerFactory.getLogger(PostGroupCasesWebScript.class);
    private NodeService nodeService;
    private SearchService searchService;

    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
        LOGGER.info("Running PostGroupCasesWebScript");
        super.executeImpl(req, status, cache);
        Map<String, Object> model = new HashMap<>();

        String masterNodeRefString = req.getParameter("masterNodeRef");
        String slaveUinList = req.getParameter("slaveUinList");

        if (masterNodeRefString == null || slaveUinList == null) {
            LOGGER.error("masterNodeRef and slaveUinList must be set on the request.");
            status.setCode(Status.STATUS_INTERNAL_SERVER_ERROR, "masterNodeRef and slaveUinList must be set on the request.");
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
        List<NodeRef> slaveNodeRefs;
        try {
            slaveNodeRefs = getSlaveUinNodeRefs(slaveUinList);
        } catch (GroupCasesException e) {
            LOGGER.info("Invalid slave UINs found: " + StringUtils.join(e.getErrorMessages(), ", "));
            status.setCode(Status.STATUS_BAD_REQUEST, StringUtils.join(e.getErrorMessages(), "\n"));
            return model;
        }

        if (slaveNodeRefs.isEmpty()) {
            LOGGER.info("No valid UINs found");
            status.setCode(Status.STATUS_BAD_REQUEST, "No valid UINs found");
            return model;
        }

        // once we have valid node refs for the uins we validate them
        try {
            validateSlaveNodeRefs(slaveNodeRefs, masterNodeRef);
        } catch (GroupCasesException e) {
            LOGGER.info("Invalid slave UINs found: " + StringUtils.join(e.getErrorMessages(), ", "));
            status.setCode(Status.STATUS_BAD_REQUEST, StringUtils.join(e.getErrorMessages(), "\n"));
            return model;
        }

        // associate the slaves to the master
        try {
            associateGroupedCases(masterNodeRef, slaveNodeRefs);
            adjustDeadlinesAndTargets(masterNodeRef);
        } catch (Exception e) {
            LOGGER.error("Unexpected error creating associating grouped cases", e);
            status.setCode(Status.STATUS_INTERNAL_SERVER_ERROR, e.getMessage());
            return model;
        }

        status.setCode(Status.STATUS_CREATED);
        return model;
    }

    private void validateSlaveNodeRefs(List<NodeRef> slaveNodeRefs, NodeRef masterNodeRef) throws GroupCasesException {
        List<String> invalidUins = new ArrayList<>();
        // does that slave have a question and member set
        for (NodeRef slaveNodeRef : slaveNodeRefs) {
            String uin = (String) nodeService.getProperty(slaveNodeRef, CtsModel.PROP_UIN);
            String questionText = (String) nodeService.getProperty(slaveNodeRef, CtsModel.PROP_QUESTION_TEXT);
            String member = (String) nodeService.getProperty(slaveNodeRef, CtsModel.PROP_MEMBER);
            String isGroupedSlave = (String) nodeService.getProperty(slaveNodeRef, CtsModel.PROP_IS_GROUPED_SLAVE);
            if (slaveNodeRef.equals(masterNodeRef)) {
                invalidUins.add(uin + ": cannot group self");
                continue;
            }
            String error = validateSlave(uin, isGroupedSlave, questionText, member);
            if (!error.isEmpty()) {
                invalidUins.add(error);
            }
        }
        if (invalidUins.size() > 0) {
            throw new GroupCasesException("Invalid UINs found", invalidUins);
        }
    }

    protected String validateSlave(String uin, String isGroupedSlave, String questionText, String member) {
        if (isGroupedSlave.equals(Boolean.TRUE.toString())) {
            return uin + ": already grouped";
        }
        if (questionText == null && member == null) {
            return uin + ": missing question and member";
        } else if (questionText == null) {
            return uin + ": missing question";
        } else if (member == null) {
            return uin + ": missing member";
        }
        return "";
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

    private List<NodeRef> getSlaveUinNodeRefs(String slaveUinListString) throws GroupCasesException {
        String[] uinList = slaveUinListString.split(",");
        List<NodeRef> nodeRefs = new ArrayList<>();
        List<String> invalidUins = new ArrayList<>();
        for (String uin : uinList) {
            SearchParameters searchParameters = new SearchParameters();
            searchParameters.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
            searchParameters.setLanguage(SearchService.LANGUAGE_CMIS_ALFRESCO);
            String query = "SELECT * FROM cts:case where cts:uin" + " = '" + uin.trim()+"'";
            searchParameters.setQuery(query);
            //this makes it use the database
            searchParameters.setQueryConsistency(QueryConsistency.TRANSACTIONAL);
            ResultSet resultSet = getSearchService().query(searchParameters);
            if (resultSet.length() == 1) {
                NodeRef nodeRef = resultSet.getNodeRef(0);
                nodeRefs.add(nodeRef);
            } else if (resultSet.length() == 0) {
                invalidUins.add(uin + ": not found");
            }
        }
        if (invalidUins.size() > 0) {
            throw new GroupCasesException("Invalid UINs found", invalidUins);
        }
        return nodeRefs;
    }

    private void associateGroupedCases(NodeRef masterNodeRef, List<NodeRef> slaveNodeRefs) {
        nodeService.setProperty(masterNodeRef, CtsModel.PROP_IS_GROUPED_MASTER, Boolean.TRUE.toString());
        List<String> addedSlaveNotes = new ArrayList<>();
        for (NodeRef slaveNodeRef : slaveNodeRefs) {
            try {
                nodeService.createAssociation(masterNodeRef, slaveNodeRef, CtsModel.ASSOC_GROUPED_CASES);
            } catch (AssociationExistsException e) {
                LOGGER.info("Association between " + masterNodeRef.toString() + " and " + slaveNodeRef + " already exists.");
            }
            nodeService.setProperty(slaveNodeRef, CtsModel.PROP_IS_GROUPED_SLAVE, Boolean.TRUE.toString());
            nodeService.setProperty(slaveNodeRef, CtsModel.PROP_MASTER_NODE_REF, masterNodeRef.getId());
            // set properties when slaves / masters are associated to show in the minutes
            addedSlaveNotes.add("Dependent grouped case added "+new Date().toString()+" " + slaveNodeRef);
            nodeService.setProperty(slaveNodeRef, CtsModel.PROP_MASTER_ADDED, "Master added "+new Date().toString()+" "+masterNodeRef);
        }
        nodeService.setProperty(masterNodeRef, CtsModel.PROP_SLAVE_ADDED, StringUtils.join(addedSlaveNotes, ';'));
    }

    private void adjustDeadlinesAndTargets(NodeRef masterNodeRef) {
        Date minCaseResponseDeadline = (Date) nodeService.getProperty(masterNodeRef, CtsModel.PROP_CASE_RESPONSE_DEADLINE);
        Date minDraftDate = (Date) nodeService.getProperty(masterNodeRef, CtsModel.PROP_DRAFT_RESPONSE_TARGET);
        List<AssociationRef> groupedCaseAssocRefList = nodeService.getTargetAssocs(masterNodeRef, CtsModel.ASSOC_GROUPED_CASES);
        for (AssociationRef groupedCaseAssocRef : groupedCaseAssocRefList) {
            NodeRef groupedCaseNodeRef = groupedCaseAssocRef.getTargetRef();
            Date caseResponseDeadline = (Date) nodeService.getProperty(groupedCaseNodeRef, CtsModel.PROP_CASE_RESPONSE_DEADLINE);
            Date draftDate = (Date) nodeService.getProperty(groupedCaseNodeRef, CtsModel.PROP_DRAFT_RESPONSE_TARGET);
            if (caseResponseDeadline != null) {
                minCaseResponseDeadline = getMinDate(caseResponseDeadline, minCaseResponseDeadline);
            }
            if (draftDate != null) {
                minDraftDate = getMinDate(draftDate, minDraftDate);
            }
        }
        nodeService.setProperty(masterNodeRef, CtsModel.PROP_CASE_RESPONSE_DEADLINE, minCaseResponseDeadline);
        nodeService.setProperty(masterNodeRef, CtsModel.PROP_DRAFT_RESPONSE_TARGET, minDraftDate);
        for (AssociationRef groupedCaseAssocRef : groupedCaseAssocRefList) {
            NodeRef groupedCaseNodeRef = groupedCaseAssocRef.getTargetRef();
            nodeService.setProperty(groupedCaseNodeRef, CtsModel.PROP_CASE_RESPONSE_DEADLINE, minCaseResponseDeadline);
            nodeService.setProperty(groupedCaseNodeRef, CtsModel.PROP_DRAFT_RESPONSE_TARGET, minDraftDate);
        }
    }

    protected Date getMinDate(Date caseResponseDeadline, Date minCaseResponseDeadline) {
        if (caseResponseDeadline.before(minCaseResponseDeadline)) {
            return caseResponseDeadline;
        } else {
            return minCaseResponseDeadline;
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
