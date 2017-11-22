package uk.gov.homeoffice.cts.behaviour;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.homeoffice.cts.model.CtsModel;

/**
 * Class to capture when aa document is deleted to a case and add an entry
 * that will show up in the audit for the case.
 * Using the unprotected nodeService so that a user who does not have permission
 * to edit the case can still add documents and minutes
 * Created by jack on 30/10/2014.
 */
public class DeleteDocumentBehaviour implements NodeServicePolicies.BeforeDeleteNodePolicy{
    private static final Logger LOGGER = LoggerFactory.getLogger(DeleteDocumentBehaviour.class);
    private PolicyComponent policyComponent;
    //Using unprotected as the user may have permission to delete document but not to edit the case
    private NodeService unprotectedNodeService;

    public void init() {
        LOGGER.debug("Registering DeleteDocumentBehaviour");
        getPolicyComponent().bindClassBehaviour(NodeServicePolicies.BeforeDeleteNodePolicy.QNAME, CtsModel.TYPE_CTS_DOCUMENT, new JavaBehaviour(this, "beforeDeleteNode"));
    }

    private void addCommentToCase(NodeRef documentNodeRef) {
        LOGGER.debug("Bang went the behaviour on " + documentNodeRef);

        if (unprotectedNodeService.exists(documentNodeRef)) {
            if (getUnprotectedNodeService().getProperty(documentNodeRef, ContentModel.PROP_NAME) != null) {
                NodeRef caseNodeRef = getUnprotectedNodeService().getPrimaryParent(documentNodeRef).getParentRef();
                //Adding nodeRef so that it is different for the audit to pick up the change
                String message = "Document deleted: "
                        + getUnprotectedNodeService().getProperty(documentNodeRef, ContentModel.PROP_NAME)
                        + " " + documentNodeRef;
                getUnprotectedNodeService().setProperty(caseNodeRef,
                        CtsModel.PROP_DOCUMENT_DELETED,
                        message);
            }
        }
    }

    private PolicyComponent getPolicyComponent() {
        return policyComponent;
    }

    public void setPolicyComponent(PolicyComponent policyComponent) {
        this.policyComponent = policyComponent;
    }

    private NodeService getUnprotectedNodeService() {
        return unprotectedNodeService;
    }

    public void setUnprotectedNodeService(NodeService unprotectedNodeService) {
        this.unprotectedNodeService = unprotectedNodeService;
    }

    @Override
    public void beforeDeleteNode(NodeRef nodeRef) {
        addCommentToCase(nodeRef);
    }
}
