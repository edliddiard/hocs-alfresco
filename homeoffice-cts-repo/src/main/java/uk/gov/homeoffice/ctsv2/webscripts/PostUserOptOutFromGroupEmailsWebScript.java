package uk.gov.homeoffice.ctsv2.webscripts;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.cmr.site.SiteInfo;
import org.alfresco.service.cmr.site.SiteService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import uk.gov.homeoffice.cts.model.CtsModel;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

public class PostUserOptOutFromGroupEmailsWebScript extends AbstractWebScript {
    private static final Logger LOGGER = LoggerFactory.getLogger(PostUserOptOutFromGroupEmailsWebScript.class);

    private static final String USER_GROUP_NO_EMAIL_LIST_NAME = "userGroupNoEmailList";

    private SiteService siteService;
    private PersonService personService;
    private NodeService nodeService;
    private AuthorityService authorityService;
    private AuthenticationService authenticationService;

    private final String CTS_SITE_NAME = "cts";
    protected final QName DATA_LISTS_QNAME = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "dataLists");

    @Override
    public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {
        LOGGER.debug("Running PostUserOptOutFromGroupEmailsWebScript");

        final String json = req.getContent().getContent();

        boolean result = false;

        try {
            final String username = authenticationService.getCurrentUserName();
            final NodeRef personNodeRef = personService.getPerson(username);
            final List<AssociationRef> userGroupNoEmailList = nodeService.getSourceAssocs(personNodeRef, CtsModel.ASSOC_USER);

            AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<Map<String, Object>>() {
                public Map<String, Object> doWork() throws Exception {
                    final NodeRef sourceRef;
                    if (userGroupNoEmailList != null && !userGroupNoEmailList.isEmpty()) {
                        //There should only be one Association, delete any others that might've come from Share
                        if (userGroupNoEmailList.size() > 1) {
                            for (int i = 1;i < userGroupNoEmailList.size();i++) {
                                nodeService.deleteNode(userGroupNoEmailList.get(i).getSourceRef());
                            }
                        }

                        sourceRef = userGroupNoEmailList.get(0).getSourceRef();

                        //First delete all the groups
                        final List<AssociationRef> groupAssocRefList = nodeService.getTargetAssocs(sourceRef, CtsModel.ASSOC_USERS_GROUPS);
                        for (AssociationRef groupRef : groupAssocRefList) {
                            nodeService.removeAssociation(sourceRef, groupRef.getTargetRef(), CtsModel.ASSOC_USERS_GROUPS);
                        }
                    } else {
                        final NodeRef dataListRef = getDataListNodeRef(USER_GROUP_NO_EMAIL_LIST_NAME);
                        sourceRef = nodeService.createNode(dataListRef, ContentModel.ASSOC_CONTAINS,
                                QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, username),
                                CtsModel.TYPE_USER_GROUPS_NO_EMAILS).getChildRef();
                        nodeService.createAssociation(sourceRef, personNodeRef, CtsModel.ASSOC_USER);
                    }

                    //Then add the groups
                    JSONObject jsonObject = new JSONObject(json);
                    JSONArray groupsArr = jsonObject.getJSONArray("groups");

                    for (int i = 0; i < groupsArr.length(); i++) {
                        String group = groupsArr.getString(i);

                        if (!group.startsWith("GROUP_")) {
                            group = "GROUP_" + group;
                        }

                        final NodeRef groupNodeRef = authorityService.getAuthorityNodeRef(group);

                        if (groupNodeRef != null) {
                            nodeService.createAssociation(sourceRef, groupNodeRef, CtsModel.ASSOC_USERS_GROUPS);
                        } else {
                            LOGGER.warn("Group " + group + " does not exist");
                        }
                    }
                    return null;
                }
            });

            result = true;
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }

        res.setContentType("application/json; charset=UTF-8");
        res.setContentEncoding("UTF-8");
        res.setHeader("Cache-Control", "must-revalidate,no-cache,no-store");
        res.getWriter().write(generateJsonResponse(result));
    }

    private String generateJsonResponse(boolean success) throws IOException {
        StringWriter writer = new StringWriter();
        JsonFactory jsonFactory = new JsonFactory();
        JsonGenerator jsonGenerator = jsonFactory.createGenerator(writer);
        jsonGenerator.writeStartObject();
        jsonGenerator.writeFieldName("status");
        jsonGenerator.writeObject(success ? "SUCCESS" : "ERROR");
        jsonGenerator.writeEndObject();
        jsonGenerator.close();
        writer.close();
        return writer.toString();
    }

    public void setPersonService(PersonService personService) {
        this.personService = personService;
    }

    public void setSiteService(SiteService siteService) {
        this.siteService = siteService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setAuthorityService(AuthorityService authorityService) {
        this.authorityService = authorityService;
    }

    public void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    private NodeRef getDataListNodeRef(String dataListName) {
        SiteInfo ctsSite = siteService.getSite(CTS_SITE_NAME);
        List<ChildAssociationRef> dataListContainer = nodeService.getChildAssocs(ctsSite.getNodeRef(), ContentModel.ASSOC_CONTAINS, DATA_LISTS_QNAME);

        if(dataListContainer!=null && !dataListContainer.isEmpty()) {
            for (ChildAssociationRef dataListChildAssocRef : nodeService.getChildAssocs(dataListContainer.get(0).getChildRef())) {
                NodeRef dataListNodeRef = dataListChildAssocRef.getChildRef();
                String listTitle = (String) nodeService.getProperty(dataListNodeRef, ContentModel.PROP_TITLE);
                if (listTitle.equalsIgnoreCase(dataListName)) {
                    return dataListNodeRef;
                }
            }
        }
        return null;
    }
}
