package uk.gov.homeoffice.cts.behaviour;

import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ActionService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.homeoffice.cts.action.MinutesSyncAction;

import java.io.Serializable;
import java.util.Map;

/**
 * Class to watch over changes in the cts:case property and then update the cts:MinutesCollated property of the case
 * Created by dawudr on 28/06/2016.
 */
public class MinutesSyncBehaviour implements PropertyUpdateBehaviour {
    private static final Logger LOGGER = LoggerFactory.getLogger(MinutesSyncBehaviour.class);
    private ActionService actionService;

    @Override
    public void onUpdateProperties(final NodeRef nodeRef, final Map<QName, Serializable> before, final Map<QName, Serializable> after) {
        LOGGER.debug("Running synchronise minutes asynchronous action for [{}]", nodeRef);
        synchroniseMinutes(nodeRef);
    }

    private void synchroniseMinutes(NodeRef nodeRef) {
        Action action = actionService.createAction(MinutesSyncAction.NAME);
        action.setParameterValue(MinutesSyncAction.PARAM_CASE_NODE_REF, nodeRef);
        // 4th parameter makes this Action run in a background thread
        actionService.executeAction(action, nodeRef, false, true);
    }

    public ActionService getActionService() {
        return actionService;
    }

    public void setActionService(ActionService actionService) {
        this.actionService = actionService;
    }
}
