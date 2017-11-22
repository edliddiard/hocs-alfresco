package uk.gov.homeoffice.cts.webscripts;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.SearchService;
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
 * Class to get all of the units in the system with a list of all of their
 * teams underneath. Note the team structure is getting flattened so loses
 * its hierarchical structure.
 * Created by chris davidson on 02/09/2014.
 */
public class GetAllTeamsWebScript extends DeclarativeWebScript {
    private static final Logger LOGGER = LoggerFactory.getLogger(GetAllTeamsWebScript.class);
    private AuthorityService authorityService;
    private SearchService searchService;
    private NodeService nodeService;

    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
        super.executeImpl(req, status, cache);

        Map<String, Object> model = new HashMap<>();

        Set<String> unitNames = getAuthorityService().getContainedAuthorities(AuthorityType.GROUP, "GROUP_Units", true);

        JSONArray unitArray =new JSONArray();
        for (String unitName : unitNames) {
            NodeRef unitNodeRef = getAuthorityService().getAuthorityNodeRef(unitName);
            Map<QName, Serializable> unitProps = getNodeService().getProperties(unitNodeRef);
            JSONObject unitObject = groupToJson(unitProps);
            unitArray.put(unitObject);

            Set<String> teamNames = getAuthorityService().getContainedAuthorities(AuthorityType.GROUP,
                    (String) unitProps.get(ContentModel.PROP_AUTHORITY_NAME),
                    false);
            JSONArray teamArray = new JSONArray();
            try {
                unitObject.put("teams", teamArray);
                for (String teamName : teamNames) {
                    NodeRef teamNodeRef = getAuthorityService().getAuthorityNodeRef(teamName);
                        Map<QName, Serializable> teamProps = getNodeService().getProperties(teamNodeRef);
                        JSONObject teamObject = groupToJson(teamProps);
                        teamArray.put(teamObject);
                }
            } catch (JSONException e) {
                LOGGER.error("Json exception with teams in unit  " + unitProps.get(ContentModel.PROP_NAME), e);
            }
        }

        try {
            model.put("units",unitArray.toString(2));
        } catch (JSONException e) {
            LOGGER.error("Error formatting units to string ", e);
        }


        return model;
    }

    private JSONObject groupToJson(Map<QName, Serializable> props) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("authorityName", props.get(ContentModel.PROP_AUTHORITY_NAME));
            jsonObject.put("displayName",props.get(ContentModel.PROP_AUTHORITY_DISPLAY_NAME));
        } catch (JSONException e) {
            LOGGER.error("Error mapping group to JSON", e);
        }
        return jsonObject;
    }

    private AuthorityService getAuthorityService() {
        return authorityService;
    }

    public void setAuthorityService(AuthorityService authorityService) {
        this.authorityService = authorityService;
    }

    public SearchService getSearchService() {
        return searchService;
    }

    public void setSearchService(SearchService searchService) {
        this.searchService = searchService;
    }

    private NodeService getNodeService() {
        return nodeService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }
}
