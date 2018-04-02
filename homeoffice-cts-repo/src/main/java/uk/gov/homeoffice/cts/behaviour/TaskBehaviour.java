package uk.gov.homeoffice.cts.behaviour;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.homeoffice.cts.model.CtsModel;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * Class to watch over changes in the cts:caseTask property
 * Created by dave on 23/12/2014.
 */
public class TaskBehaviour implements PropertyUpdateBehaviour {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskBehaviour.class);
    private NodeService nodeService;

    @Override
    public void onUpdateProperties(final NodeRef nodeRef, final Map<QName, Serializable> before, final Map<QName, Serializable> after) {
        final String taskBefore = (String) before.get(CtsModel.PROP_CASE_TASK);

        final String taskAfter = (String) after.get(CtsModel.PROP_CASE_TASK);

        //check it has changed
        if(BehaviourHelper.hasChanged(taskBefore, taskAfter)) {
            //we want this to work automatically so do it as system
            AuthenticationUtil.runAs(new AuthenticationUtil.RunAsWork<Map<String, Object>>() {
                @SuppressWarnings("synthetic-access")
                public Map<String, Object> doWork() throws Exception {
                    LOGGER.debug("Task before " + taskBefore);
                    LOGGER.debug("Task after " + taskAfter);
                    updateTaskUpdatedDatetime(nodeRef);
                    return null;
                }
            }, AuthenticationUtil.getSystemUserName());

        }
    }

    private void updateTaskUpdatedDatetime(NodeRef nodeRef) {
        nodeService.setProperty(nodeRef, CtsModel.PROP_TASK_UPDATED_DATETIME, new Date());
    }

    private NodeService getNodeService() {
        return nodeService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }
}
