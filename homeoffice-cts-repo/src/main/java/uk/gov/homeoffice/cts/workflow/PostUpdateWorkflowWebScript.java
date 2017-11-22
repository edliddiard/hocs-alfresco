package uk.gov.homeoffice.cts.workflow;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.datatype.DefaultTypeConverter;
import org.alfresco.service.cmr.workflow.WorkflowInstance;
import org.alfresco.service.cmr.workflow.WorkflowService;
import org.alfresco.service.cmr.workflow.WorkflowTask;
import org.alfresco.service.cmr.workflow.WorkflowTaskQuery;
import org.alfresco.service.namespace.QName;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

import uk.gov.homeoffice.cts.behaviour.BehaviourHelper;
import uk.gov.homeoffice.cts.model.CaseStatus;
import uk.gov.homeoffice.cts.model.CtsModel;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Webscript to signal workflow of updates and also set properties
 * at the same time
 * Created by chris on 22/09/2014.
 */
public class
        PostUpdateWorkflowWebScript extends DeclarativeWebScript {
    private static final Logger LOGGER = LoggerFactory.getLogger(PostUpdateWorkflowWebScript.class);
    private static final String DELETE = "Deleted";
    private static final String REALLOCATE = "Reallocate";
    private WorkflowService workflowService;
    private NodeService nodeService;
    private DictionaryService dictionaryService;
    private AllocationService allocationService;
    private List workflows;

    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
        super.executeImpl(req, status, cache);
        String json = null;

        LOGGER.debug("Executing webscript update");

        Map<String, Object> model = new HashMap<>();
        try {
            json = req.getContent().getContent();
        } catch (IOException e) {
            LOGGER.error("No content", e);
            status.setCode(Status.STATUS_BAD_REQUEST, "Could not read body");
            return model;
        }
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(json);
        } catch (JSONException e) {
            LOGGER.error("Error with JSON in body:"+json, e);
            status.setCode(Status.STATUS_BAD_REQUEST,"Could not JSON in body");
            return model;
        }

        // get nodeRef from request
        NodeRef nodeRef = null;
        if (jsonObject != null) {
            try {
                nodeRef = new NodeRef(jsonObject.getString("nodeRef"));
            } catch (JSONException e) {
                LOGGER.error("No NodeRef in body:" + json, e);
                status.setCode(Status.STATUS_BAD_REQUEST,"Could not find NodeRef");
                return model;
            }
        }
        String transition = null;
        if (jsonObject != null) {
            try {
                if(jsonObject.has("transition")) {
                    transition = jsonObject.getString("transition");
                }
            } catch (JSONException e) {
                LOGGER.error("JSON Error with transition:"+json,e);
                status.setCode(Status.STATUS_BAD_REQUEST,"JSON error with transition");
                return model;
            }
        }
        JSONArray props = null;
        if (jsonObject != null) {
            try {
                if(jsonObject.has("properties")) {
                    props = jsonObject.getJSONArray("properties");
                }
            } catch (JSONException e) {
                LOGGER.error("JSON Error with properties:"+json,e);
                status.setCode(Status.STATUS_BAD_REQUEST,"JSON error with properties");
                return model;
            }
        }
        Map<QName, Serializable> propertiesToChange = null;
        if (props != null) {
            Map<QName, Serializable> existingProperties = getNodeService().getProperties(nodeRef);
            propertiesToChange = new HashMap<>();

            for (int i = 0; i < props.length(); i++) {
                try {
                    Iterator iterator = props.getJSONObject(i).keys();
                    while(iterator.hasNext()){
                        final String key = (String) iterator.next();
                        PropertyDefinition propertyDefinition = getDictionaryService().getProperty(QName.createQName(key));

                       Serializable value = null;
                        if(!props.getJSONObject(i).isNull(key)){
                            value = (Serializable) DefaultTypeConverter.INSTANCE.convert(propertyDefinition.getDataType(), props.getJSONObject(i).get(key));
                        }
                        //these are the only properties that can get set by this script
                        if(key.indexOf("assignedUser") > -1 || key.indexOf("assignedTeam") > -1 || key.indexOf("assignedUnit") > -1){
                            //now check it has changed
                            QName qName = QName.createQName(key);
                            if(BehaviourHelper.hasChangedSerializable(existingProperties.get(qName), value)){
                                propertiesToChange.put(qName, value);
                            }
                        }
                    }
                } catch (JSONException e) {
                    LOGGER.error("JSON Error with properties:"+json,e);
                    status.setCode(Status.STATUS_BAD_REQUEST,"JSON problem with properties");
                    return model;
                }
            }
        }

        if(transition.equalsIgnoreCase(DELETE)) {
            //set status to deleted
            getNodeService().setProperty(nodeRef, CtsModel.PROP_CASE_STATUS, CaseStatus.DELETED.getStatus());
            getNodeService().setProperty(nodeRef, CtsModel.PROP_CASE_TASK, CaseStatus.DELETED.getStatus());
        }

        //we need to check if the user has allocate permission and if so do it as system user
        //need to do this as SystemUser as the user might not have edit permission for the properties but has the custom permission
        final Map<QName, Serializable> changedProperties = propertiesToChange;
        final NodeRef finalNodeRef = nodeRef;

        if(changedProperties!=null) {
            AuthenticationUtil.runAs(new AuthenticationUtil.RunAsWork<Map<String, Object>>() {
                @SuppressWarnings("synthetic-access")
                public Map<String, Object> doWork() throws Exception {
                    if(LOGGER.isDebugEnabled()) {
                        Set<QName> keys = changedProperties.keySet();
                        for (QName key : keys) {
                            LOGGER.debug("Updating property " + key.toString() + " has changed to :" + changedProperties.get(key));
                        }
                    }

                    getNodeService().addProperties(finalNodeRef,changedProperties);
                    //getNodeService().setProperties(finalNodeRef,changedProperties);
                    return null;
                }
            }, AuthenticationUtil.getAdminUserName());
        }

        if(!transition.equalsIgnoreCase(REALLOCATE)) {
            //don't touch the workflow if it is REALLOCATE

            // list all active workflows for nodeRef
            List<WorkflowInstance> workflowInstances = getWorkflowService().getWorkflowsForContent(nodeRef, true);

            LOGGER.debug("Found " + workflowInstances.size() + " workflows");

            for (WorkflowInstance workflowInstance : workflowInstances) {
                //need to get the actual name and check it is the right type of workflow:
                String name = workflowInstance.getDefinition().getName();
                if (!getWorkflows().contains(name)) {
                    //only work on declared workflows
                    LOGGER.debug("Ignoring this workflow:" + name);
                    break;
                }

                final String workflowInstanceId = workflowInstance.getId();

                if (transition.equalsIgnoreCase(DELETE)) {
                    //cancel workflow
                    getWorkflowService().cancelWorkflow(workflowInstanceId);
                } else {
                    final String finalTransition = transition;
                    List<WorkflowTask> allTasks = AuthenticationUtil.runAs(new AuthenticationUtil.RunAsWork<List<WorkflowTask>>() {
                        @Override
                        public List<WorkflowTask> doWork() throws Exception {
                            List<WorkflowTask> allTasks;

                            WorkflowTaskQuery taskQuery = new WorkflowTaskQuery();
                            taskQuery.setActive(null);
                            taskQuery.setProcessId(workflowInstanceId);
                            taskQuery.setOrderBy(new WorkflowTaskQuery.OrderBy[]{WorkflowTaskQuery.OrderBy.TaskDue_Asc});

                            allTasks = workflowService.queryTasks(taskQuery);

                            LOGGER.debug("Found " + allTasks.size() + " workflow tasks for case");
                            for (WorkflowTask workflowTask : allTasks) {
                                String workflowTaskId = workflowTask.getId();
                                getWorkflowService().endTask(workflowTaskId, finalTransition);
                                LOGGER.debug("Task " + workflowTaskId + " ended");
                            }

                            return allTasks;
                        }
                    }, AuthenticationUtil.getAdminUserName());
                }
            }
        }

        return model;
    }



    private WorkflowService getWorkflowService() {
        return workflowService;
    }

    public void setWorkflowService(WorkflowService workflowService) {
        this.workflowService = workflowService;
    }

    private NodeService getNodeService() {
        return nodeService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public DictionaryService getDictionaryService() {
        return dictionaryService;
    }

    public void setDictionaryService(DictionaryService dictionaryService) {
        this.dictionaryService = dictionaryService;
    }

    public AllocationService getAllocationService() {
        return allocationService;
    }

    public void setAllocationService(AllocationService allocationService) {
        this.allocationService = allocationService;
    }

    public List getWorkflows() {
        return workflows;
    }

    public void setWorkflows(List workflows) {
        this.workflows = workflows;
    }
}
