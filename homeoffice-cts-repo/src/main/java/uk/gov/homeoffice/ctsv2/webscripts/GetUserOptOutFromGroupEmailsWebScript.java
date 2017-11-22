package uk.gov.homeoffice.ctsv2.webscripts;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.cmr.security.PersonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import uk.gov.homeoffice.cts.model.CtsModel;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetUserOptOutFromGroupEmailsWebScript extends AbstractWebScript {
    private static final Logger LOGGER = LoggerFactory.getLogger(GetUserOptOutFromGroupEmailsWebScript.class);

    private PersonService personService;
    private NodeService nodeService;
    private AuthenticationService authenticationService;

    @Override
    public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {
        LOGGER.debug("Running GetUserOptOutFromGroupEmailsWebScript");

        final List<Map<String, String>> groups = new ArrayList<>();

        final NodeRef personNodeRef = personService.getPerson(authenticationService.getCurrentUserName());

        final List<AssociationRef> userGroupNoEmailList = nodeService.getSourceAssocs(personNodeRef, CtsModel.ASSOC_USER);
        if (userGroupNoEmailList != null && !userGroupNoEmailList.isEmpty()) {
            List<AssociationRef> groupAssocRefList = nodeService.getTargetAssocs(userGroupNoEmailList.get(0).getSourceRef(), CtsModel.ASSOC_USERS_GROUPS);
            for (AssociationRef groupRef : groupAssocRefList) {
                Map<String, String> groupMap = new HashMap<>();
                groupMap.put("authorityName", (String)nodeService.getProperty(groupRef.getTargetRef(), ContentModel.PROP_AUTHORITY_NAME));
                groupMap.put("displayName", (String)nodeService.getProperty(groupRef.getTargetRef(), ContentModel.PROP_AUTHORITY_DISPLAY_NAME));
                groups.add(groupMap);
            }
        }

        res.setContentType("application/json; charset=UTF-8");
        res.setContentEncoding("UTF-8");
        res.setHeader("Cache-Control", "must-revalidate,no-cache,no-store");
        res.getWriter().write(generateJsonResponse(groups));
    }

    private String generateJsonResponse(List<Map<String, String>> groups) throws IOException {
        StringWriter writer = new StringWriter();
        JsonFactory jsonFactory = new JsonFactory();
        JsonGenerator jsonGenerator = jsonFactory.createGenerator(writer);
        jsonGenerator.writeStartObject();
        jsonGenerator.writeFieldName("groups");
        jsonGenerator.writeStartArray();
        for (Map<String, String> group : groups) {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeStringField("authorityName", group.get("authorityName"));
            jsonGenerator.writeStringField("displayName", group.get("displayName"));
            jsonGenerator.writeEndObject();
        }
        jsonGenerator.writeEndArray();
        jsonGenerator.writeEndObject();
        jsonGenerator.close();
        writer.close();
        return writer.toString();
    }

    public void setPersonService(PersonService personService) {
        this.personService = personService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }
}
