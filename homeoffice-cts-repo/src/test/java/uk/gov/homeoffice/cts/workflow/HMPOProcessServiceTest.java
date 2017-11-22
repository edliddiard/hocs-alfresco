package uk.gov.homeoffice.cts.workflow;


import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.workflow.WorkflowInstance;
import org.alfresco.service.cmr.workflow.WorkflowService;
import org.alfresco.service.cmr.workflow.WorkflowTask;
import org.alfresco.service.cmr.workflow.WorkflowTaskQuery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.homeoffice.cts.model.CtsModel;
import uk.gov.homeoffice.cts.model.TaskStatus;

import java.util.Collections;
import java.util.Iterator;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HMPOProcessServiceTest {

    private HMPOProcessService toTest;

    @Mock private SearchService searchService;

    @Mock private NodeService nodeService;

    @Mock private WorkflowService workflowService;

    private ArgumentCaptor<SearchParameters> spArgumentCaptor;

    @Mock private SearchParameters sp;

    @Mock private ResultSet rs;

    @Mock private Iterator<ResultSetRow> iterator;

    @Mock private ResultSetRow row_first;

    @Mock private ResultSetRow row_second;

    @Mock private WorkflowInstance workflowInstance_first_node;

    @Mock private WorkflowInstance workflowInstance_second_node;

    @Mock private WorkflowTask workflowTask;


    private NodeRef nodeRef_first = new NodeRef("workspace://SpacesStore/3e4abc39-d0be-437e-a92f-343bdc845cf9");
    private NodeRef nodeRef_second = new NodeRef("workspace://SpacesStore/132b4404-6e4b-4364-ba84-407c0f84338a");

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        toTest = new HMPOProcessService();
        toTest.setNodeService(nodeService);
        toTest.setSearchService(searchService);
        toTest.setWorkflowService(workflowService);

        spArgumentCaptor = ArgumentCaptor.forClass(SearchParameters.class);


        when(searchService.query(spArgumentCaptor.capture())).thenReturn(rs);
        when(rs.iterator()).thenReturn(iterator);
        when(iterator.hasNext()).thenReturn(true,true,false);
        when(iterator.next()).thenReturn(row_first, row_second);
        when(row_first.getNodeRef()).thenReturn(nodeRef_first);
        when(row_second.getNodeRef()).thenReturn(nodeRef_second);

    }

    @Test
    public void testMoveCaseStatusToBringUp() {

        toTest.moveCaseStatusToBringUp();

        final SearchParameters searchParameters = spArgumentCaptor.getValue();
        assertTrue(searchParameters.getQuery().startsWith("SELECT c.cmis:objectId FROM cts:case as c WHERE c.cts:caseTask = 'Defer' AND c.cts:bringUpDate < TIMESTAMP "));
        Mockito.verify(nodeService).setProperty(nodeRef_first, CtsModel.PROP_CASE_TASK, TaskStatus.BRING_UP.getStatus());
        Mockito.verify(nodeService).setProperty(nodeRef_second, CtsModel.PROP_CASE_TASK, TaskStatus.BRING_UP.getStatus());

    }


    @Test
    public void testCheckAndCloseCaseByTravelDate(){
        when(workflowService.getWorkflowsForContent(nodeRef_first, true)).thenReturn(Collections.singletonList(workflowInstance_first_node));
        when(workflowService.getWorkflowsForContent(nodeRef_second, true)).thenReturn(Collections.singletonList(workflowInstance_second_node));

        when(workflowService.queryTasks(any(WorkflowTaskQuery.class))).thenReturn(Collections.singletonList(workflowTask), Collections.singletonList(workflowTask));
        when(workflowTask.getId()).thenReturn("first_case_workflow_task_id", "second_case_workflow_task_id");


        toTest.checkAndCloseCaseByTravelDate();

        final SearchParameters searchParameters = spArgumentCaptor.getValue();
        assertTrue(searchParameters.getQuery().startsWith("SELECT c.cmis:objectId FROM cts:case as c WHERE c.cts:correspondenceType = 'COL' AND c.cts:caseTask = 'Dispatched' AND c.cts:departureDateFromUK < TIMESTAMP "));

        verify(workflowService).endTask("first_case_workflow_task_id", "CompleteCase");
        verify(workflowService).endTask("second_case_workflow_task_id", "CompleteCase");
    }

}
