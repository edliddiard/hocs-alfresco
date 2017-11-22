package uk.gov.homeoffice.cts.behaviour;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.homeoffice.cts.model.CaseStatus;
import uk.gov.homeoffice.cts.model.CorrespondenceType;
import uk.gov.homeoffice.cts.model.CtsModel;

import java.io.Serializable;
import java.util.Map;

/**
 * Class to watch over changes in the cts:assignedUnit property
 * Created by jonathan on 12/03/2014.
 */
public class AssignedUnitBehaviour implements PropertyUpdateBehaviour {
    private static final Logger LOGGER = LoggerFactory.getLogger(AssignedUnitBehaviour.class);
    private NodeService nodeService;


    @Override
    public void onUpdateProperties(final NodeRef nodeRef, final Map<QName, Serializable> before, final Map<QName, Serializable> after) {

        final String beforeAssignedUnit = (String) before.get(CtsModel.PROP_ASSIGNED_UNIT);

        final String afterAssignedUnit = (String) after.get(CtsModel.PROP_ASSIGNED_UNIT);

        final String caseStatus = (String) after.get(CtsModel.PROP_CASE_STATUS);

        String caseType = (String) after.get(CtsModel.PROP_CORRESPONDENCE_TYPE);

        if (isMarkupUnitUpdateRequired(caseStatus, beforeAssignedUnit, afterAssignedUnit, caseType)) {
            LOGGER.debug("assignedUnit before: " + beforeAssignedUnit + " after: "+ afterAssignedUnit+ " Correspondence type: " + caseType);
            AuthenticationUtil.runAs(new AuthenticationUtil.RunAsWork<Map<String, Object>>() {
                @SuppressWarnings("synthetic-access")
                public Map<String, Object> doWork() throws Exception {
                    if (afterAssignedUnit != null) {
                        getNodeService().setProperty(nodeRef, CtsModel.PROP_MARKUP_UNIT, afterAssignedUnit);
                        LOGGER.debug("Update markupUnit with assignedUnit " + afterAssignedUnit);
                    }
                    return null;
                }
            }, AuthenticationUtil.getAdminUserName());
        }
    }

    protected boolean isMarkupUnitUpdateRequired(String caseStatus, String beforeAssignedUnit, String afterAssignedUnit, String caseType) {
        // Checks for PQ type, assignedUnit change AND case status is draft
        return (BehaviourHelper.hasChanged(beforeAssignedUnit, afterAssignedUnit) &&
                CorrespondenceType.getUnitCaseTypes("PQ").contains(caseType) &&
                CaseStatus.DRAFT.getStatus().equals(caseStatus));
    }

    private NodeService getNodeService() {
        return nodeService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

}
