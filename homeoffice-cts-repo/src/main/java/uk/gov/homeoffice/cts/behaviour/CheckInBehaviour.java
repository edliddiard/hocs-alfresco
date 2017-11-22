package uk.gov.homeoffice.cts.behaviour;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.coci.CheckOutCheckInServicePolicies.OnCheckIn;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.homeoffice.cts.model.CtsModel;

import java.io.Serializable;

/**
 * When a new version of a document is added update the case minutes.
 * Created by chris on 27/10/2014.
 */
public class CheckInBehaviour implements OnCheckIn{
    private static final Logger LOGGER = LoggerFactory.getLogger(CheckInBehaviour.class);
    private NodeService nodeService;
    private PolicyComponent policyComponent;
    private NodeService unprotectedNodeService;

    public void init() {
        LOGGER.debug("Registering OnCheckIn");
        getPolicyComponent().bindClassBehaviour(OnCheckIn.QNAME, CtsModel.TYPE_CTS_DOCUMENT, new JavaBehaviour(this, "onCheckIn"));
    }

    @Override
    public void onCheckIn(NodeRef nodeRef) {
        updateCase(nodeRef);
    }

    private void updateCase(NodeRef nodeRef) {
        NodeRef caseNodeRef = getNodeService().getPrimaryParent(nodeRef).getParentRef();
        QName nodeType = getNodeService().getType(caseNodeRef);
        if(nodeType.toString().equals(CtsModel.TYPE_CTS_CASE.toString())){
            if (getNodeService().exists(nodeRef)) {
                Serializable label = getUnprotectedNodeService().getProperty(nodeRef, ContentModel.PROP_VERSION_LABEL);
                getUnprotectedNodeService().setProperty(caseNodeRef,
                        CtsModel.PROP_DOCUMENT_ADDED,
                        label + " Version added to document " +
                                nodeRef);
            }
        }
    }


    private NodeService getNodeService() {
        return nodeService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    private NodeService getUnprotectedNodeService() {
        return unprotectedNodeService;
    }

    public void setUnprotectedNodeService(NodeService unprotectedNodeService) {
        this.unprotectedNodeService = unprotectedNodeService;
    }

    private PolicyComponent getPolicyComponent() {
        return policyComponent;
    }

    public void setPolicyComponent(PolicyComponent policyComponent) {
        this.policyComponent = policyComponent;
    }



}
