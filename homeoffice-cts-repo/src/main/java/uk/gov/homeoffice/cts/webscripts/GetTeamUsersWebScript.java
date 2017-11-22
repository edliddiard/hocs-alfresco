package uk.gov.homeoffice.cts.webscripts;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.cmr.security.AuthorityType;
import org.alfresco.service.namespace.QName;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Webscript to get all of the users that are under a team, this will include users allocated to sub groups
 * Created by chris on 02/09/2014.
 */
public class GetTeamUsersWebScript extends DeclarativeWebScript {
    private static final Logger LOGGER = LoggerFactory.getLogger(GetTeamUsersWebScript.class);

    private AuthorityService authorityService;
    private NodeService nodeService;

    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
        super.executeImpl(req, status, cache);

        Map<String, Object> model = new HashMap<>();

        String group = req.getParameter("group");
        if(group == null || group.equals("")){
            status.setCode(Status.STATUS_BAD_REQUEST,"The parameter group needs to have a value");
            return model;
        }
        if(!group.startsWith("GROUP_")){
            group = "GROUP_" + group;
        }

        if(!getAuthorityService().authorityExists(group)){
            status.setCode(Status.STATUS_BAD_REQUEST,"The group " + group + " does not exist");
            return model;
        }

        Set<String> users = getAuthorityService().getContainedAuthorities(AuthorityType.USER, group, true);
        JSONArray userArray = new JSONArray();
        for (String user : users){
            NodeRef userNodeRef = getAuthorityService().getAuthorityNodeRef(user);
            Map<QName, Serializable> userProps = getNodeService().getProperties(userNodeRef);
            JSONObject userJson = getUserFromJson(userProps);
            userArray.put(userJson);
        }

        model.put("users",userArray);
        return model;
    }

    private JSONObject getUserFromJson(Map<QName, Serializable> props) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userName",props.get(ContentModel.PROP_USERNAME));
            jsonObject.put("firstName",props.get(ContentModel.PROP_FIRSTNAME));
            jsonObject.put("lastName",props.get(ContentModel.PROP_LASTNAME));
            jsonObject.put("email",props.get(ContentModel.PROP_EMAIL));
        } catch (JSONException e) {
            e.printStackTrace();
            LOGGER.error("getUserFromJson error: " + e.getMessage());
        }
        return jsonObject;
    }

    public AuthorityService getAuthorityService() {
        return authorityService;
    }

    public void setAuthorityService(AuthorityService authorityService) {
        this.authorityService = authorityService;
    }

    public NodeService getNodeService() {
        return nodeService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }
}
