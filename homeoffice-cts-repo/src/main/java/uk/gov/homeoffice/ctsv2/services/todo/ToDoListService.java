package uk.gov.homeoffice.ctsv2.services.todo;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.WebScriptRequest;
import uk.gov.homeoffice.cts.helpers.CtsCmisObjectHelper;
import uk.gov.homeoffice.ctsv2.model.CtsCase;
import uk.gov.homeoffice.ctsv2.webscripts.CaseMapperHelper;

import java.io.Serializable;
import java.util.*;

import static org.springframework.util.StringUtils.collectionToDelimitedString;

public class ToDoListService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ToDoListService.class);

    private NodeService nodeService;
    private SearchService searchService;
    private CtsCmisObjectHelper ctsCmisObjectHelper;
    private CaseMapperHelper caseMapperHelper;

    /**
     * Get To Do list of Cases for To do List page
     *
     * @param req
     */
    public ToDoListResponse getToDoList(WebScriptRequest req) {
        ToDoListSearchParams searchParams = extractSearchParamsByReq(req);

        LOGGER.info("Running GetToDoListWebScript Params[{}]", searchParams.toString());

        LOGGER.info("Fetching ToDo List Of Cases for user[{}]", searchParams.getAssignedUserNames());
        Date tStart = new Date();

        ToDoListResponse response = new ToDoListResponse();
        // Fetch list of to do ctsCase nodes
        String qCmis = createQuery(searchParams);

        SearchParameters sp = new SearchParameters();
        sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
        sp.setLanguage(SearchService.LANGUAGE_CMIS_ALFRESCO);
        sp.setQuery(qCmis);

        ResultSet rs = searchService.query(sp);
        response.totalResults(rs.length());

        sp.setMaxItems(searchParams.getPageSize());
        sp.setSkipCount(searchParams.getSkipCount());


        try {
            rs = searchService.query(sp);

            for (ResultSetRow r : rs) {
                Map<String, Object> todoItem = new HashMap<>();
                NodeRef caseNode = r.getNodeRef();

                // Fetch CaseModel
                Map<QName, Serializable> caseProps = nodeService.getProperties(caseNode);
                CtsCase ctsCase = getCtsCase( caseProps);
                todoItem.put("case", ctsCase);

                if (searchParams.isIncludeAllowableActions()) {
                    Map<String, Boolean> casePermissions = ctsCmisObjectHelper.getCasePermissions(caseNode);
                    todoItem.put("allowableActions", casePermissions);
                }
                response.addCase(todoItem);
            }

            LOGGER.info("Found [{}] ToDo Cases for user[{}]", rs.length(), searchParams.getAssignedUserNames());
            LOGGER.debug("GetToDoListWebscript Todo list Query time taken: {}ms", new Date().getTime() - tStart.getTime());

        } finally {
            if (rs != null) {
                rs.close();
            }
        }
        return response;
    }

    private ToDoListSearchParams extractSearchParamsByReq(WebScriptRequest req) {
        final ToDoListSearchParams params = new ToDoListSearchParams();
        params.setAssignedUserNames(req.getParameter("userNames"));
        params.setPageSize(req.getParameter("limit"));
        params.setSkipCount(req.getParameter("offset"));
        params.setIncludeAllowableActions(req.getParameter("includeAllowableActions"));
        params.setSortingOrder(req.getParameter("sortingOrder"));
        params.setCaseStatus(req.getParameter("caseStatus"));
        params.setAssignedTeams(req.getParameter("assignedTeams"));
        params.setCaseTasks(req.getParameter("caseTasks"));
        params.setCaseTypes(req.getParameter("caseTypes"));
        params.setAssignedUnits(req.getParameter("assignedUnits"));
        params.setFilterByPriority(Boolean.valueOf(req.getParameter("priorityOnly")));
        return params;
    }

    private String createQuery(ToDoListSearchParams searchParams) {

        final StringBuilder qCmis = new StringBuilder("SELECT c.cmis:objectId FROM cts:case as c JOIN cts:groupedMaster as gm ON gm.cmis:objectId = c.cmis:objectId JOIN cts:groupedSlave as gs ON gs.cmis:objectId = c.cmis:objectId JOIN cts:linkedCase as lc ON lc.cmis:objectId = c.cmis:objectId WHERE c.cts:caseStatus <> 'Completed' AND c.cts:caseStatus <> 'Deleted' AND c.cts:caseTask <> 'Defer' AND gs.cts:isGroupedSlave = 'false' ");

        if (searchParams.getAssignedUserNames() != null) {
            qCmis.append(" AND c.cts:assignedUser ").append(createInClauseFilter(searchParams.getAssignedUserNames()));
        }

        if (searchParams.getCaseStatus() != null) {
            qCmis.append(" AND c.cts:caseStatus ").append(createInClauseFilter(searchParams.getCaseStatus()));
        }

        if (searchParams.getAssignedTeams() != null) {
            qCmis.append(" AND c.cts:assignedTeam ").append(createInClauseFilter(searchParams.getAssignedTeams()));
        }

        if (searchParams.getCaseTasks() != null) {
            qCmis.append(" AND c.cts:caseTask ").append(createInClauseFilter(searchParams.getCaseTasks()));
        }

        if (searchParams.getCaseTypes() != null) {
            qCmis.append(" AND c.cts:correspondenceType ").append(createInClauseFilter(searchParams.getCaseTypes()));
        }

        if (searchParams.getAssignedUnits() != null) {
            qCmis.append(" AND c.cts:assignedUnit ").append(createInClauseFilter(searchParams.getAssignedUnits()));
        }

        if (searchParams.isFilterByPriority()) {
            qCmis.append(" AND c.cts:priority = true");
        }

        qCmis.append(" ORDER BY c.cts:caseResponseDeadline ")
                .append(searchParams.getSortingOrder());
        return qCmis.toString();
    }

    protected String createInClauseFilter(List<String> inputList) {

        List<String> escapedInputList = new ArrayList<>();
        for (String unescapedValue : inputList) {
            escapedInputList.add(unescapedValue.replace("'", "\\'"));
        }

        return "IN (" + collectionToDelimitedString(escapedInputList, ",", "'", "'") + ")";
    }

    protected CtsCase getCtsCase(Map<QName, Serializable> caseProps) {
        final CtsCase ctsCase = CtsCase.getCtsCase(caseProps);
        ctsCase.setDisplayStatus(caseMapperHelper.getCaseDisplayStatus(ctsCase.getCaseStatus()));
        ctsCase.setDisplayTask(caseMapperHelper.getCaseDisplayTask(ctsCase.getCaseTask()));
        ctsCase.populateCanonicalCorrespondent();

        return ctsCase;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setSearchService(SearchService searchService) {
        this.searchService = searchService;
    }

    public void setCtsCmisObjectHelper(CtsCmisObjectHelper ctsCmisObjectHelper) {
        this.ctsCmisObjectHelper = ctsCmisObjectHelper;
    }

    public void setCaseMapperHelper(CaseMapperHelper caseMapperHelper) {
        this.caseMapperHelper = caseMapperHelper;
    }
}
