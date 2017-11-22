package uk.gov.homeoffice.cts.behaviour;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.homeoffice.cts.model.CtsModel;

import java.io.Serializable;
import java.util.Map;

public class CtsCasePropertyUpdateBehaviour implements NodeServicePolicies.OnUpdatePropertiesPolicy {

    private static final Logger LOGGER = LoggerFactory.getLogger(CtsCasePropertyUpdateBehaviour.class);

    private PropertyUpdateBehaviour allocateBehaviour;

    private PropertyUpdateBehaviour assignedUnitBehaviour;

    private PropertyUpdateBehaviour complexBehaviour;

    private PropertyUpdateBehaviour deadlineBehaviour;

    private PropertyUpdateBehaviour markupDecisionBehaviour;

    private PropertyUpdateBehaviour minutesSyncBehaviour;

    private PropertyUpdateBehaviour originalDrafterBehaviour;

    private PropertyUpdateBehaviour pitLetterSentDateBehaviour;

    private PropertyUpdateBehaviour propertyPermissionsBehaviour;

    private PropertyUpdateBehaviour statusBehaviour;

    private PropertyUpdateBehaviour taskBehaviour;

    private PolicyComponent policyComponent;

    public void init() {
        LOGGER.debug("Registering  CtsCasePropertyUpdateBehaviour Behaviour");
        Behaviour onUpdateProperties = new JavaBehaviour(this, "onUpdateProperties", Behaviour.NotificationFrequency.TRANSACTION_COMMIT);
        this.policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME, CtsModel.TYPE_CTS_CASE, onUpdateProperties);
    }

    @Override
    public void onUpdateProperties(final NodeRef nodeRef, final Map<QName, Serializable> before, final Map<QName, Serializable> after) {
        allocateBehaviour.onUpdateProperties(nodeRef, before, after);
        assignedUnitBehaviour.onUpdateProperties(nodeRef, before, after);
        complexBehaviour.onUpdateProperties(nodeRef, before, after);
        deadlineBehaviour.onUpdateProperties(nodeRef, before, after);
        markupDecisionBehaviour.onUpdateProperties(nodeRef, before, after);
        minutesSyncBehaviour.onUpdateProperties(nodeRef, before, after);
        originalDrafterBehaviour.onUpdateProperties(nodeRef, before, after);
        pitLetterSentDateBehaviour.onUpdateProperties(nodeRef, before, after);
        propertyPermissionsBehaviour.onUpdateProperties(nodeRef, before, after);
        statusBehaviour.onUpdateProperties(nodeRef, before, after);
        taskBehaviour.onUpdateProperties(nodeRef, before, after);
    }

    public void setAllocateBehaviour(PropertyUpdateBehaviour allocateBehaviour) {
        this.allocateBehaviour = allocateBehaviour;
    }

    public void setAssignedUnitBehaviour(PropertyUpdateBehaviour assignedUnitBehaviour) {
        this.assignedUnitBehaviour = assignedUnitBehaviour;
    }

    public void setComplexBehaviour(PropertyUpdateBehaviour complexBehaviour) {
        this.complexBehaviour = complexBehaviour;
    }

    public void setDeadlineBehaviour(PropertyUpdateBehaviour deadlineBehaviour) {
        this.deadlineBehaviour = deadlineBehaviour;
    }

    public void setMarkupDecisionBehaviour(PropertyUpdateBehaviour markupDecisionBehaviour) {
        this.markupDecisionBehaviour = markupDecisionBehaviour;
    }

    public void setMinutesSyncBehaviour(PropertyUpdateBehaviour minutesSyncBehaviour) {
        this.minutesSyncBehaviour = minutesSyncBehaviour;
    }

    public void setOriginalDrafterBehaviour(PropertyUpdateBehaviour originalDrafterBehaviour) {
        this.originalDrafterBehaviour = originalDrafterBehaviour;
    }

    public void setPitLetterSentDateBehaviour(PropertyUpdateBehaviour pitLetterSentDateBehaviour) {
        this.pitLetterSentDateBehaviour = pitLetterSentDateBehaviour;
    }

    public void setStatusBehaviour(PropertyUpdateBehaviour statusBehaviour) {
        this.statusBehaviour = statusBehaviour;
    }

    public void setTaskBehaviour(PropertyUpdateBehaviour taskBehaviour) {
        this.taskBehaviour = taskBehaviour;
    }

    public void setPolicyComponent(PolicyComponent policyComponent) {
        this.policyComponent = policyComponent;
    }

    public void setPropertyPermissionsBehaviour(PropertyUpdateBehaviour propertyPermissionsBehaviour) {
        this.propertyPermissionsBehaviour = propertyPermissionsBehaviour;
    }
}
