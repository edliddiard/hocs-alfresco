package uk.gov.homeoffice.cts.workflow;


import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.workflow.WorkflowInstance;
import org.alfresco.service.cmr.workflow.WorkflowService;
import org.alfresco.service.cmr.workflow.WorkflowTask;
import org.alfresco.service.cmr.workflow.WorkflowTaskQuery;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.homeoffice.cts.model.CtsModel;
import uk.gov.homeoffice.cts.model.TaskStatus;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class HMPOProcessService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HMPOProcessService.class);

    private static SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'Z'");

    private SearchService searchService;

    private NodeService nodeService;

    private WorkflowService workflowService;

    public String processHMPOCases() {

        final long startTime = System.currentTimeMillis();
        LOGGER.info("HMPOProcessJob started at " + startTime);
        
        //we want this to work automatically so do it as system
        AuthenticationUtil.runAs(new AuthenticationUtil.RunAsWork<Map<String, Object>>() {
            @SuppressWarnings("synthetic-access")

            public Map<String, Object> doWork() throws Exception {
                moveCaseStatusToBringUp();
                checkAndCloseCaseByTravelDate();
                return null;
            }
        }, AuthenticationUtil.getSystemUserName());

        final String log = "HMPOProcessJob finished at " + (System.currentTimeMillis() - startTime) + "ms";
        LOGGER.info(log);
        return log;
    }

    protected void moveCaseStatusToBringUp() {

        String now = DATE_FORMATTER.format(new DateTime().toDate());

        String query = "SELECT c.cmis:objectId FROM cts:case as c WHERE c.cts:caseTask LIKE 'Defer%' AND c.cts:bringUpDate < TIMESTAMP '" + now + "'";

        ResultSet resultSet = null;
        try {
            resultSet = getResultSet(query);
            LOGGER.info("Found {} deferred cases", resultSet.getNumberFound());
            for (ResultSetRow row : resultSet) {
                NodeRef caseNode = row.getNodeRef();
                String caseStatus = (String)nodeService.getProperty(caseNode, CtsModel.PROP_CASE_TASK);
                if (Objects.equals(caseStatus, TaskStatus.DRAFT_DEFER.getStatus())) {
                    nodeService.setProperty(caseNode, CtsModel.PROP_CASE_TASK, TaskStatus.DRAFT_RESPONSE.getStatus());
                    LOGGER.info("Setting status from Defer Draft to Draft Response for case " + caseNode);
                } else if (Objects.equals(caseStatus, TaskStatus.DISPATCH_DEFER.getStatus())) {
                    nodeService.setProperty(caseNode, CtsModel.PROP_CASE_TASK, TaskStatus.DISPATCH_RESPONSE.getStatus());
                    LOGGER.info("Setting status from Defer Dispatch to Dispatch for case " + caseNode);
                }
            }
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
        }
    }

    protected void checkAndCloseCaseByTravelDate() {


        String now = DATE_FORMATTER.format(new DateTime().toDate());

        String query = "SELECT c.cmis:objectId FROM cts:case as c WHERE c.cts:correspondenceType = 'COL' AND c.cts:caseTask = 'Dispatched' AND c.cts:departureDateFromUK < TIMESTAMP '" + now + "'";
        ResultSet resultSet = null;

        try {
            resultSet = getResultSet(query);
            for (ResultSetRow r : resultSet) {

                NodeRef nodeRef = r.getNodeRef();

                LOGGER.info("Update {} to close case", nodeRef);
                List<WorkflowInstance> workflowInstances = workflowService.getWorkflowsForContent(nodeRef, true);

                for (WorkflowInstance workflowInstance : workflowInstances) {
                    final String workflowInstanceId = workflowInstance.getId();

                    List<WorkflowTask> allTasks;

                    WorkflowTaskQuery taskQuery = new WorkflowTaskQuery();
                    taskQuery.setActive(null);
                    taskQuery.setProcessId(workflowInstanceId);
                    taskQuery.setOrderBy(new WorkflowTaskQuery.OrderBy[]{WorkflowTaskQuery.OrderBy.TaskDue_Asc});

                    allTasks = workflowService.queryTasks(taskQuery);

                    for (WorkflowTask workflowTask : allTasks) {
                        String workflowTaskId = workflowTask.getId();
                        workflowService.endTask(workflowTaskId, "CompleteCase");
                        LOGGER.info("Task " + workflowTaskId + " ended");
                    }
                }
            }

        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
        }
    }


    private ResultSet getResultSet(String query) {
        final SearchParameters sp = new SearchParameters();
        sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
        sp.setLanguage(SearchService.LANGUAGE_CMIS_ALFRESCO);
        sp.setQuery(query);
        return searchService.query(sp);

    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setSearchService(SearchService searchService) {
        this.searchService = searchService;
    }

    public void setWorkflowService(WorkflowService workflowService) {
        this.workflowService = workflowService;
    }

}
