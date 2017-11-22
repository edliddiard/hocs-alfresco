package uk.gov.homeoffice.cts.behaviour;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.workflow.WorkflowModel;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.cmr.workflow.WorkflowDefinition;
import org.alfresco.service.cmr.workflow.WorkflowPath;
import org.alfresco.service.cmr.workflow.WorkflowService;
import org.alfresco.service.cmr.workflow.WorkflowTask;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.homeoffice.cts.helpers.BusinessCalendarProvider;
import uk.gov.homeoffice.cts.helpers.CtsFolderHelper;
import uk.gov.homeoffice.cts.helpers.NumberGenerator;
import uk.gov.homeoffice.cts.model.CtsModel;
import uk.gov.homeoffice.cts.model.TaskStatus;
import uk.gov.homeoffice.cts.permissions.CtsPermissions;

import java.io.Serializable;
import java.security.InvalidParameterException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Behaviour to fire when a case is created and set the URN and auto allocate to the
 * appropriate team
 * Created by chris on 25/07/2014.
 */
public class CreateCaseBehaviour implements NodeServicePolicies.OnCreateNodePolicy {
    private static final Logger LOGGER = LoggerFactory.getLogger(CreateCaseBehaviour.class);
    private NodeService nodeService;
    private AuthorityService authorityService;
    private PolicyComponent policyComponent;
    private PermissionService permissionService;
    private NumberGenerator numberGenerator;
    private CtsFolderHelper ctsFolderHelper;
    private Map<String,String> units;
    private Map<String, String> teams;
    private Map<String, String> workflows;
    private Map<String, String> hmpoWorkflows;

    private WorkflowService workflowService;

    public void init() {
        LOGGER.debug("Registering Behaviour");
        Behaviour onCreateNode = new JavaBehaviour(this, "onCreateNode", Behaviour.NotificationFrequency.TRANSACTION_COMMIT);

        this.getPolicyComponent().bindClassBehaviour(
                QName.createQName(NamespaceService.ALFRESCO_URI, "onCreateNode"),
                CtsModel.TYPE_CTS_CASE,
                onCreateNode);
    }

    @Override
    public void onCreateNode(ChildAssociationRef childAssocRef) {
        // create and apply the URN first otherwise it will try to apply
        // the property to the moved node which will create a lock in the db.
        createURN(childAssocRef.getChildRef());
        moveNode(childAssocRef.getChildRef());
        autoAllocate(childAssocRef.getChildRef());
        setPermissions(childAssocRef.getChildRef());
        startWorkflow(childAssocRef.getChildRef());
        setUpdatedDatetimes(childAssocRef.getChildRef());
    }

    private void setUpdatedDatetimes(NodeRef nodeRef) {
        Date now = new Date();
        nodeService.setProperty(nodeRef, CtsModel.PROP_STATUS_UPDATED_DATETIME, now);
        nodeService.setProperty(nodeRef, CtsModel.PROP_TASK_UPDATED_DATETIME, now);
        nodeService.setProperty(nodeRef, CtsModel.PROP_OWNER_UPDATED_DATETIME, now);
    }

    private void moveNode(NodeRef nodeRef) {
        String correspondenceType = (String)nodeService.getProperty(nodeRef, CtsModel.PROP_CORRESPONDENCE_TYPE);
        String year = Integer.toString(Calendar.getInstance().get(Calendar.YEAR));
        NodeRef containingNodeRef = ctsFolderHelper.getOrCreateCtsCaseContainerFolder(correspondenceType, year);
        nodeService.moveNode(nodeRef, containingNodeRef, ContentModel.ASSOC_CONTAINS, ContentModel.ASSOC_CHILDREN);
    }

    private void startWorkflow(NodeRef nodeRef) {
        String correspondenceType = (String) getNodeService().getProperty(nodeRef,CtsModel.PROP_CORRESPONDENCE_TYPE);

        String workflowName = getWorkflows().get(correspondenceType);

        if (workflowName == null) {
        	switch (correspondenceType) {
                case "COM" :
                    String hmpoStage = (String) getNodeService().getProperty(nodeRef, CtsModel.PROP_HMPO_STAGE);
                    workflowName = getHmpoWorkflows().get(correspondenceType+"_"+hmpoStage);
                    break;
        	}
        }
        
        if (workflowName != null) {
            //check if a workflow has been allocated to the type
            WorkflowDefinition workflowDef = getWorkflowService().getDefinitionByName(workflowName);

            // Create workflow parameters
            Map<QName, Serializable> params = new HashMap<>();
            NodeRef workflowPackage = getWorkflowService().createPackage(null);
            params.put(WorkflowModel.ASSOC_PACKAGE, workflowPackage);
            ChildAssociationRef childAssoc = getNodeService().getPrimaryParent(nodeRef);
            getNodeService().addChild(workflowPackage, nodeRef, WorkflowModel.ASSOC_PACKAGE_CONTAINS, childAssoc.getQName());

            //need to find out what the targets are
            getNodeService().setProperty(nodeRef, CtsModel.PROP_CASE_TASK, TaskStatus.CREATE_CASE);

            Date dueDate = new Date();
            params.put(WorkflowModel.PROP_WORKFLOW_DUE_DATE, dueDate);
            params.put(WorkflowModel.PROP_WORKFLOW_PRIORITY, 1);

            String allocateTeam = getTeams().get(correspondenceType);
            if(allocateTeam == null){
                allocateTeam = getUnits().get(correspondenceType);
            }
            params.put(WorkflowModel.ASSOC_GROUP_ASSIGNEE, getAuthorityService().getAuthorityNodeRef(allocateTeam));

            //set the property that will have possible responses.
            params.put(WorkflowModel.PROP_OUTCOME_PROPERTY_NAME, "{http://cts-beta.homeoffice.gov.uk/model/content/1.0}sendForDraft");

            // Start a workflow instance
            WorkflowPath path = getWorkflowService().startWorkflow(workflowDef.getId(), params);
            final String workflowInstanceId = path.getInstance().getId();

            // End start task to progress workflow
            WorkflowTask startTask = getWorkflowService().getStartTask(workflowInstanceId);
            String startTaskId = startTask.getId();
            getWorkflowService().endTask(startTaskId, null);
        } else {
            LOGGER.info("No workflow specified");
        }
    }

    private void setPermissions(NodeRef nodeRef) {
        getPermissionService().setPermission(nodeRef, PermissionService.ALL_AUTHORITIES, CtsPermissions.ACTUAL_CASE_VIEWER, Boolean.TRUE);
        //this will stop it inheriting permissions so no edit permissions would work other than admin/super user permissions
        getPermissionService().setInheritParentPermissions(nodeRef,false);
    }

    private void autoAllocate(NodeRef nodeRef) {
        String correspondenceType = (String) getNodeService().getProperty(nodeRef,CtsModel.PROP_CORRESPONDENCE_TYPE);
        String assignedUnit = (String) getNodeService().getProperty(nodeRef, CtsModel.PROP_ASSIGNED_UNIT);

        if(assignedUnit != null){
            LOGGER.debug("Not allocating to a unit or team as it is already assigned to unit " + assignedUnit);
            //then it has already been assigned so exit here, it has probably come from an auto-create doc
            return;
        }

        assignToUnitAndTeam(nodeRef, correspondenceType);


        if(!(Boolean)getNodeService().getProperty(nodeRef, CtsModel.PROP_PQ_API_CREATED_CASE)){
            //also need to allocate it to the user who created it so they can do any edits required
            getNodeService().setProperty(nodeRef,CtsModel.PROP_ASSIGNED_USER, AuthenticationUtil.getFullyAuthenticatedUser());
        }

    }

    private void assignToUnitAndTeam(NodeRef nodeRef, String correspondenceType) {
        String unit = getAutoAllocateUnit(correspondenceType);

        if(unit == null || !getAuthorityService().authorityExists(unit)){
            //throw exception
            LOGGER.error("Unit does not exist: " + unit);
            throw new InvalidParameterException("Unit "+unit+" does not exist");
        }else{
            getNodeService().setProperty(nodeRef, CtsModel.PROP_ASSIGNED_UNIT,unit);
        }

        String team = getAutoAllocateTeam(correspondenceType);

        if(team != null && getAuthorityService().authorityExists(team)){
            getNodeService().setProperty(nodeRef, CtsModel.PROP_ASSIGNED_TEAM,team);
        }
    }

    protected String getAutoAllocateUnit(String correspondenceType) {
        return getUnits().get(correspondenceType);
    }

    public String getAutoAllocateTeam(String correspondenceType) {
        return getTeams().get(correspondenceType);
    }

    private void createURN(NodeRef nodeRef) {
        //suffix looks like ddddddd/yy
        String year = Integer.toString(Calendar.getInstance().get(Calendar.YEAR));

        String nextNumber = getNumberGenerator().nextNumber(year,nodeRef.toString());

        String suffix =  nextNumber + "/" + year.substring(2);
        String suffixDash = nextNumber + "-" + year.substring(2);

        getNodeService().setProperty(nodeRef, CtsModel.PROP_URN_SUFFIX, suffix);
        getNodeService().setProperty(nodeRef, ContentModel.PROP_NAME, suffixDash);
    }

    private NodeService getNodeService() {
        return nodeService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    private PolicyComponent getPolicyComponent() {
        return policyComponent;
    }

    public void setPolicyComponent(PolicyComponent policyComponent) {
        this.policyComponent = policyComponent;
    }

    private PermissionService getPermissionService() {
        return permissionService;
    }

    public void setPermissionService(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    private NumberGenerator getNumberGenerator() {
        return numberGenerator;
    }

    public void setNumberGenerator(NumberGenerator numberGenerator) {
        this.numberGenerator = numberGenerator;
    }

    public CtsFolderHelper getCtsFolderHelper() {
        return ctsFolderHelper;
    }

    public void setCtsFolderHelper(CtsFolderHelper ctsFolderHelper) {
        this.ctsFolderHelper = ctsFolderHelper;
    }

    private Map<String,String> getUnits() {
        return units;
    }

    public void setUnits(Map<String,String> units) {
        this.units = units;
    }

    public void setTeams(Map<String, String> teams) {
        this.teams = teams;
    }

    private Map<String, String> getTeams() {
        return teams;
    }

    private AuthorityService getAuthorityService() {
        return authorityService;
    }

    public void setAuthorityService(AuthorityService authorityService) {
        this.authorityService = authorityService;
    }

    public WorkflowService getWorkflowService() {
        return workflowService;
    }

    public void setWorkflowService(WorkflowService workflowService) {
        this.workflowService = workflowService;
    }

    public Map<String, String> getWorkflows() {
        return workflows;
    }

    public void setWorkflows(Map<String, String> workflows) {
        this.workflows = workflows;
    }
    
    public Map<String, String> getHmpoWorkflows() {
        return hmpoWorkflows;
    }

    public void setHmpoWorkflows(Map<String, String> hmpoWorkflows) {
        this.hmpoWorkflows = hmpoWorkflows;
    }
}
