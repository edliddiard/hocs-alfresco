package uk.gov.homeoffice.cts.behaviour;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.homeoffice.cts.model.CaseStatus;
import uk.gov.homeoffice.cts.model.CtsModel;
import uk.gov.homeoffice.cts.model.TaskStatus;

import java.io.Serializable;
import java.util.Map;

/**
 * Class to watch changes in the assignedUser property. This will store who the drafter was
 * Created by chris davidson on 10/10/2014.
 */
public class OriginalDrafterBehaviour implements PropertyUpdateBehaviour {
    private static final Logger LOGGER = LoggerFactory.getLogger(OriginalDrafterBehaviour.class);
    private NodeService nodeService;

    @Override
    public void onUpdateProperties(final NodeRef nodeRef, final Map<QName, Serializable> before, final Map<QName, Serializable> after) {
            //need to do this as SystemUser as the user might not have edit permission for the properties but has the custom permission
        AuthenticationUtil.runAs(new AuthenticationUtil.RunAsWork<Map<String, Object>>() {
            @SuppressWarnings("synthetic-access")
            public Map<String, Object> doWork() throws Exception {
                storeDrafters(nodeRef, before, after);
                return null;
            }
        }, AuthenticationUtil.getSystemUserName());
    }
    private void storeDrafters(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after){
        String assignedUserAfter = (String) after.get(CtsModel.PROP_ASSIGNED_USER);
        String assignedTeamAfter = (String) after.get(CtsModel.PROP_ASSIGNED_TEAM);
        String assignedUnitAfter = (String) after.get(CtsModel.PROP_ASSIGNED_UNIT);

        //store the drafters details so we can send things back to them from later in the workflow
        String afterStatus = (String) after.get(CtsModel.PROP_CASE_STATUS);
        LOGGER.debug(nodeRef+ " beforeStatus: "+before.get(CtsModel.PROP_CASE_STATUS)+" afterStatus: "+afterStatus);
        String afterTask = (String) after.get(CtsModel.PROP_CASE_TASK);
        LOGGER.debug(nodeRef+ " beforeTask: "+after.get(CtsModel.PROP_CASE_STATUS)+" afterTask: "+afterTask);

        if (getNodeService().exists(nodeRef)
                && CaseStatus.DRAFT.getStatus().equals(afterStatus)
                && ((TaskStatus.DRAFT_RESPONSE.getStatus().equals(afterTask) || (TaskStatus.DRAFT_AND_CLEAR.getStatus().equals(afterTask)))
                || TaskStatus.AMEND_RESPONSE.getStatus().equals(afterTask))) {
            getNodeService().setProperty(nodeRef, CtsModel.PROP_ORIGINAL_DRAFTER_UNIT, assignedUnitAfter);
            getNodeService().setProperty(nodeRef, CtsModel.PROP_ORIGINAL_DRAFTER_TEAM, assignedTeamAfter);
            getNodeService().setProperty(nodeRef, CtsModel.PROP_ORIGINAL_DRAFTER_USER, assignedUserAfter);
        }
    }

    private NodeService getNodeService() {
        return nodeService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

}
