package uk.gov.homeoffice.cts.behaviour;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.coci.CheckOutCheckInServiceImpl;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AuthorityType;
import org.alfresco.service.cmr.security.PermissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.homeoffice.cts.model.CtsModel;

import java.util.Map;

/**
 * Class to capture when aa document is added to a case and add an entry
 * that will show up in the audit for the case.
 * Using the unprotected nodeService so that a user who does not have permission
 * to edit the case can still add documents and minutes
 * Created by chris on 07/08/2014.
 */
public class AddDocumentBehaviour implements NodeServicePolicies.OnCreateChildAssociationPolicy {
    private static final Logger LOGGER = LoggerFactory.getLogger(AddDocumentBehaviour.class);
    private PolicyComponent policyComponent;
    private NodeService unprotectedNodeService;
    private PermissionService permissionService;

    public void init() {
        LOGGER.debug("Registering AddDocumentBehaviour");
        Behaviour onCreateChildAssociation = new JavaBehaviour(this, "onCreateChildAssociation",
                Behaviour.NotificationFrequency.TRANSACTION_COMMIT);

        this.getPolicyComponent().bindAssociationBehaviour(
                NodeServicePolicies.OnCreateChildAssociationPolicy.QNAME,
                CtsModel.TYPE_CTS_CASE,
                ContentModel.ASSOC_CONTAINS,
                onCreateChildAssociation);
    }

    @Override
    public void onCreateChildAssociation(final ChildAssociationRef childAssocRef, boolean isNewNode) {
        LOGGER.debug("Bang went the behaviour on parent " +
                childAssocRef.getParentRef() + " and on the child " + childAssocRef.getChildRef());

        if (unprotectedNodeService.exists(childAssocRef.getChildRef())) {
            getUnprotectedNodeService().setProperty(childAssocRef.getParentRef(),
                    CtsModel.PROP_DOCUMENT_ADDED,
                    "Document added " +
                            childAssocRef.getChildRef());

            //also need to set the permissions so anyone can edit this document
            AuthenticationUtil.runAs(new AuthenticationUtil.RunAsWork<Map<String, Object>>() {
                @SuppressWarnings("synthetic-access")
                public Map<String, Object> doWork() throws Exception {
                    getPermissionService().setPermission(childAssocRef.getChildRef(),
                            AuthorityType.EVERYONE.getFixedString(),
                            PermissionService.EDITOR, true);
                    return null;
                }
            }, AuthenticationUtil.getSystemUserName());
        }
    }

    private PolicyComponent getPolicyComponent() {
        return policyComponent;
    }

    public void setPolicyComponent(PolicyComponent policyComponent) {
        this.policyComponent = policyComponent;
    }

    public NodeService getUnprotectedNodeService() {
        return unprotectedNodeService;
    }

    public void setUnprotectedNodeService(NodeService unprotectedNodeService) {
        this.unprotectedNodeService = unprotectedNodeService;
    }
    public PermissionService getPermissionService() {
        return permissionService;
    }

    public void setPermissionService(PermissionService permissionService) {
        this.permissionService = permissionService;
    }
}
