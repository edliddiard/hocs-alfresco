package uk.gov.homeoffice.cts.behaviour;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.workflow.WorkflowInstance;
import org.alfresco.service.cmr.workflow.WorkflowService;
import org.alfresco.service.cmr.workflow.WorkflowTask;
import org.alfresco.service.cmr.workflow.WorkflowTaskQuery;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.homeoffice.cts.model.CtsModel;
import uk.gov.homeoffice.cts.model.MarkupDecisions;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Class to watch over changes in the cts:markupDecision property
 * Created by jack on 24/11/2014.
 */
public class MarkupDecisionBehaviour implements PropertyUpdateBehaviour {
    private static final Logger LOGGER = LoggerFactory.getLogger(MarkupDecisionBehaviour.class);
    private WorkflowService workflowService;
    private List<String> workflows;

    @Override
    public void onUpdateProperties(final NodeRef nodeRef, final Map<QName, Serializable> before, final Map<QName, Serializable> after) {
        final String decisionBefore = (String) before.get(CtsModel.PROP_MARKUP_DECISION);

        final String decisionAfter = (String) after.get(CtsModel.PROP_MARKUP_DECISION);

        //check it has changed
        if(BehaviourHelper.hasChanged(decisionBefore,decisionAfter)) {

            LOGGER.debug("Decision before " + decisionBefore + " After "+ decisionAfter);
            //we want this to work automatically so do it as system
            AuthenticationUtil.runAs(new AuthenticationUtil.RunAsWork<Map<String, Object>>() {
                @SuppressWarnings("synthetic-access")
                public Map<String, Object> doWork() throws Exception {
                    //update the deadlines
                    LOGGER.debug("The decision changed on " + nodeRef);

                    if (decisionAfter != null && (decisionAfter.equals(MarkupDecisions.REFER_TO_OGD.getDecision()) ||
                        decisionAfter.equals(MarkupDecisions.NO_REPLY_NEEDED.getDecision()) ||
                        decisionAfter.equals(MarkupDecisions.REQUEST_UNCLEAR.getDecision()) ||
                        decisionAfter.equals(MarkupDecisions.WITHDRAW_QUESTION.getDecision()) ||
                        decisionAfter.equals(MarkupDecisions.PHONE_CALL_RESOLUTION.getDecision()) ||
                        decisionAfter.equals(MarkupDecisions.REFER_TO_DCU.getDecision()) ||
                        decisionAfter.equals(MarkupDecisions.INFORMALLY_RESOLVED.getDecision()))) {

                        List<WorkflowInstance> workflowInstances = getWorkflowService().getWorkflowsForContent(nodeRef, true);

                        for (WorkflowInstance workflowInstance : workflowInstances){
                            //need to get the actual name and check it is the right type of workflow:
                            String name = workflowInstance.getDefinition().getName();
                            if(!getWorkflows().contains(name)){
                                //only work on declared workflows
                                LOGGER.debug("Ignoring this workflow:"+name);
                                break;
                            }

                            final String workflowInstanceId = workflowInstance.getId();

                            List<WorkflowTask> allTasks;

                            WorkflowTaskQuery taskQuery = new WorkflowTaskQuery();
                            taskQuery.setActive(null);
                            taskQuery.setProcessId(workflowInstanceId);
                            taskQuery.setOrderBy(new WorkflowTaskQuery.OrderBy[]{WorkflowTaskQuery.OrderBy.TaskDue_Asc});

                            allTasks = workflowService.queryTasks(taskQuery);

                            LOGGER.debug("Found " + allTasks.size() + " workflow tasks");
                            for (WorkflowTask workflowTask : allTasks) {
                                LOGGER.debug("claimable"+getWorkflowService().isTaskClaimable(workflowTask,AuthenticationUtil.getFullyAuthenticatedUser()));
                                LOGGER.debug("editable"+getWorkflowService().isTaskEditable(workflowTask,AuthenticationUtil.getFullyAuthenticatedUser()));
                                String workflowTaskId = workflowTask.getId();
                                getWorkflowService().endTask(workflowTaskId, decisionAfter);
                                LOGGER.debug("Task " + workflowTaskId + " ended");
                            }
                        }
                    }
                    return null;
                }
            }, AuthenticationUtil.getSystemUserName());
        }
    }

    private WorkflowService getWorkflowService() {
        return workflowService;
    }

    public void setWorkflowService(WorkflowService workflowService) {
        this.workflowService = workflowService;
    }

    public List getWorkflows() {
        return workflows;
    }

    public void setWorkflows(List workflows) {
        this.workflows = workflows;
    }
}
