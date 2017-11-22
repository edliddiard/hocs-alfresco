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
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.homeoffice.cts.helpers.BusinessCalendarProvider;
import uk.gov.homeoffice.cts.helpers.CtsFolderHelper;
import uk.gov.homeoffice.cts.helpers.NumberGenerator;
import uk.gov.homeoffice.cts.model.CtsModel;
import uk.gov.homeoffice.cts.model.CtsUserModel;
import uk.gov.homeoffice.cts.model.TaskStatus;
import uk.gov.homeoffice.cts.permissions.CtsPermissions;

import java.io.Serializable;
import java.security.InvalidParameterException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Behaviour to fire when a user is created and add details that
 * forces users to reset their password
 * Created by chris on 11/12/2014.
 */
public class CreateUserBehaviour implements NodeServicePolicies.OnCreateNodePolicy {
    private static final Logger LOGGER = LoggerFactory.getLogger(CreateUserBehaviour.class);
    private NodeService nodeService;
    private PolicyComponent policyComponent;

    public void init() {
        LOGGER.debug("Registering Behaviour");
        Behaviour onCreateNode = new JavaBehaviour(this, "onCreateNode", Behaviour.NotificationFrequency.TRANSACTION_COMMIT);

        this.getPolicyComponent().bindClassBehaviour(
                QName.createQName(NamespaceService.ALFRESCO_URI, "onCreateNode"),
                ContentModel.TYPE_PERSON,
                onCreateNode);
    }

    @Override
    public void onCreateNode(ChildAssociationRef childAssocRef) {
        NodeRef personNode = childAssocRef.getChildRef();
        Map<QName, Serializable> map = new HashMap();
        map.put(CtsUserModel.PROPERTY_PASSWORD_EXPIRY_DATE, new DateTime().minusDays(1).toDate());
        getNodeService().addAspect(personNode, CtsUserModel.ASPECT_PASSWORD,map);
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
}
