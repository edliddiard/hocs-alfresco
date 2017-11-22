package uk.gov.homeoffice.cts.webscripts;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
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
import uk.gov.homeoffice.cts.model.CtsModel;

import java.io.Serializable;
import java.util.*;

/**
 * Webscript to get all users that are in a given users teams
 * Created by jackm on 20/11/2014
 */
public class GetPeopleInUsersTeamsWebScript extends DeclarativeWebScript {
    private static final Logger LOGGER = LoggerFactory.getLogger(GetPeopleInUsersTeamsWebScript.class);

    private AuthorityService authorityService;
    private NodeService nodeService;
    public static final List<String> EXCLUDED_GROUPS = Arrays.asList("GROUP_Units", "GROUP_EVERYONE", "GROUP_ALFRESCO_ADMINISTRATORS", "GROUP_EMAIL_CONTRIBUTORS", "GROUP_Manager");

    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
        super.executeImpl(req, status, cache);

        Map<String, Object> model = new HashMap<>();

        Set<String> groups = getAuthorityService().getAuthorities();
        JSONArray userArray = new JSONArray();
        for (String group : groups){
            if (!EXCLUDED_GROUPS.contains(group)) {
                Set<String> zones = getAuthorityService().getAuthorityZones(group);
                if (zones != null && zones.contains(AuthorityService.ZONE_APP_DEFAULT)) {
                    NodeRef groupNodeRef = getAuthorityService().getAuthorityNodeRef(group);
                    if (groupNodeRef != null) {
                        Set<String> users = getAuthorityService().getContainedAuthorities(AuthorityType.USER, group, false);
                        for (String user : users) {
                            NodeRef userNodeRef = getAuthorityService().getAuthorityNodeRef(user);
                            Map<QName, Serializable> userProps = getNodeService().getProperties(userNodeRef);
                            JSONObject userJson = getUserFromJson(userProps);
                            if (!userExists(userArray, (String) userProps.get(ContentModel.PROP_USERNAME))) {
                                userArray.put(userJson);
                            }
                        }
                    }
                }

            }
        }

        model.put("users",userArray);
        return model;
    }

    private boolean userExists(JSONArray jsonArray, String usernameToFind){
        return jsonArray.toString().contains("\"userName\":\""+usernameToFind+"\"");
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
