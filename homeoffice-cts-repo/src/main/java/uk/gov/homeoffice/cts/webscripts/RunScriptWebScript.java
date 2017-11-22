package uk.gov.homeoffice.cts.webscripts;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.action.executer.ScriptActionExecuter;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ActionService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.repo.model.Repository;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

import java.util.Map;

/**
 * WebScript that will run a javascript in the Data Dictionary/Scripts folder
 * Specific use is to run a javascript that will delete all of the CTS content
 * for testing.
 * Created by chris on 11/09/2014.
 */
public class RunScriptWebScript extends DeclarativeWebScript {
    private static Log LOGGER = LogFactory.getLog(RunScriptWebScript.class);
    private NodeService nodeService;
    private Repository repository;
    private ActionService actionService;
    private AuthenticationService authenticationService;

    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {

        String scriptName = req.getParameter("scriptname");
        if(scriptName == null || scriptName.equals("")){
            status.setCode(Status.STATUS_BAD_REQUEST,"Need to supply a scriptname parameter");
            throw new AlfrescoRuntimeException("Missing parameter");
        }
        String user = req.getParameter("user");
        if(user == null || user.equals("")){
            status.setCode(Status.STATUS_BAD_REQUEST,"Need to supply a user parameter");
            throw new AlfrescoRuntimeException("Missing parameter");
        }
        String password = req.getParameter("password");
        if(password == null || password.equals("")){
            status.setCode(Status.STATUS_BAD_REQUEST,"Need to supply a password parameter");
            throw new AlfrescoRuntimeException("Missing parameter");
        }
        //so that they have to pass a username and password
        getAuthenticationService().authenticate(user, password.toCharArray());

        NodeRef ddNodeRef = getNodeService().getChildByName(getRepository().getCompanyHome(), ContentModel.ASSOC_CONTAINS,"Data Dictionary");
        NodeRef scriptsNodeRef = getNodeService().getChildByName(ddNodeRef, ContentModel.ASSOC_CONTAINS,"Scripts");
        NodeRef runScriptNodeRef = getNodeService().getChildByName(scriptsNodeRef, ContentModel.ASSOC_CONTAINS,scriptName);

        Action action1 = this.getActionService().createAction(ScriptActionExecuter.NAME);
        action1.setParameterValue(ScriptActionExecuter.PARAM_SCRIPTREF, runScriptNodeRef);

        // Execute the action
        this.getActionService().executeAction(action1, scriptsNodeRef);

        return super.executeImpl(req, status, cache);
    }

    public NodeService getNodeService() {
        return nodeService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public Repository getRepository() {
        return repository;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    public ActionService getActionService() {
        return actionService;
    }

    public void setActionService(ActionService actionService) {
        this.actionService = actionService;
    }

    public AuthenticationService getAuthenticationService() {
        return authenticationService;
    }

    public void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }
}
