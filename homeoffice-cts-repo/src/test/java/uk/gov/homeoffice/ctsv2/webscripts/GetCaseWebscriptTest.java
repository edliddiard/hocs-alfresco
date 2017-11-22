package uk.gov.homeoffice.ctsv2.webscripts;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.template.TemplateNode;
import org.alfresco.repo.web.scripts.BaseWebScriptTest;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AccessStatus;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.namespace.QName;
import org.apache.chemistry.opencmis.commons.data.CmisExtensionElement;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.CmisExtensionElementImpl;
import org.junit.Before;
import org.junit.Test;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import uk.gov.homeoffice.cts.helpers.CtsFolderHelper;
import uk.gov.homeoffice.cts.model.CorrespondenceType;
import uk.gov.homeoffice.cts.model.CtsModel;
import uk.gov.homeoffice.cts.permissions.CtsPermissions;
import uk.gov.homeoffice.cts.permissions.PermissionChecker;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.mockito.Mockito.*;

/**
 * Created by dawudr on 06/06/2016.
 */
public class GetCaseWebscriptTest extends BaseWebScriptTest {

    private NodeRef caseNode;
    private GetCaseWebScript webscript;
    private NodeService nodeService;
    private ServiceRegistry serviceRegistry;
    private CtsFolderHelper ctsFolderHelper;
    private PermissionChecker permissionChecker;
    private PermissionService permissionService;
    private CaseMapperHelper caseMapperHelper;

    // Expected Test Properties
    private static final String nodeRefString = "workspace://SpacesStore/myreference";
    private static final String folderName = "0000001-16";
    private static final String title = "Mock Case Document";
    private static final String firstName = "firstname_user001";
    private static final String surname = "lastname_user001";
    private static final String email = "hercule@donotreply.com";
    private static final String username = "username001";
    private static final String password = "password";
    private static final String group = "GROUP_" + "Parliamentary Questions Team";
    private static final String unit = "GROUP_" + "GROUP_Parliamentary Questions";
    private static final String correspondenceType = CorrespondenceType.ORDINARY_WRITTEN.getCode();
    private static final String caseStatus = "New";
    private static final String caseTask = "Create case";
    private static final String urnSuffix = "0002016/16";
    private static final String caseWorkflowStatus = "{\"transitions\":[{\"label\":\"Reallocate\",\"value\": \"Reallocate\",\"manualAllocate\": true,\"allocateHeader\": \"Reallocate\",\"colour\": \"green\"},{\"label\":\"Allocate for draft\",\"value\": \"Next\",\"manualAllocate\": true,\"colour\": \"green\",\"allocateHeader\": \"Allocate\"}]}";
    private static final String caseMandatoryFields = "{\"caseMandatoryValues\": [{\"name\": \"lordsMinister\",\"message\": \"Value is required\"}]}";
    private static final String caseMandatoryFieldStatus = "{\"caseMandatoryFieldStatus\": [\"Approval\"]}";
    private static final String caseMandatoryFieldTask = "{\"caseMandatoryFieldTask\": [\"Parly approval\"]}";
    private static final String uin = "0001";
    private static Date dateReceived, createdAt, statusUpdatedDatetime, taskUpdatedDatetime, ownerUpdatedDatetime = null;
    private static Date opDate, woDate = null;

    @Before
    public void setUp() throws Exception {
        ctsFolderHelper = mock(CtsFolderHelper.class);
        permissionChecker = mockPermissionChecker();
        permissionService = mockPermissionService();
        nodeService = mock(NodeService.class);
        serviceRegistry = mock(ServiceRegistry.class);
        caseNode = mockCtsCaseNode();
        caseMapperHelper = mock(CaseMapperHelper.class);

        // Setup
        webscript = mock(GetCaseWebScript.class);
        webscript.setCtsFolderHelper(ctsFolderHelper);
        webscript.setNodeService(nodeService);
        webscript.setServiceRegistry(serviceRegistry);
        webscript.setPermissionChecker(permissionChecker);
        webscript.setPermissionService(permissionService);

        Calendar calendar = new GregorianCalendar(2016, 5, 15, 01, 23, 45);
        dateReceived = calendar.getTime();
        createdAt = calendar.getTime();
        statusUpdatedDatetime = calendar.getTime();
        taskUpdatedDatetime = calendar.getTime();
        ownerUpdatedDatetime = calendar.getTime();
        opDate = calendar.getTime();
        woDate = calendar.getTime();
    }

    private NodeRef mockCtsCaseNode() {
        NodeRef mockCaseNode = new NodeRef(nodeRefString);
        nodeService.setProperty(mockCaseNode, ContentModel.PROP_NAME, folderName);
        nodeService.setProperty(mockCaseNode, ContentModel.PROP_TITLE, title);
        nodeService.setProperty(mockCaseNode, CtsModel.PROP_URN_SUFFIX, urnSuffix);
        nodeService.setProperty(mockCaseNode, CtsModel.PROP_DOCUMENT_USER, username);
        nodeService.setProperty(mockCaseNode, CtsModel.PROP_DOCUMENT_TEAM, group);
        nodeService.setProperty(mockCaseNode, CtsModel.PROP_DOCUMENT_UNIT, unit);
        return mockCaseNode;
    }

    private Map<QName, Serializable> mockCaseNodeProperties() {

        // CaseNode properties
        Map<QName, Serializable> props = new HashMap<>();
        props.put(ContentModel.PROP_TITLE, title);
        props.put(CtsModel.PROP_DOCUMENT_USER, username);
        props.put(CtsModel.PROP_DOCUMENT_TEAM, group);
        props.put(CtsModel.PROP_DOCUMENT_UNIT, unit);
        props.put(CtsModel.PROP_AUTO_CREATED_CASE, true);
        props.put(CtsModel.PROP_DATE_RECEIVED, dateReceived);

        // CtsCase properties
        props.put(ContentModel.PROP_NAME, folderName);
        props.put(ContentModel.PROP_NODE_UUID, caseNode.getId());
        props.put(ContentModel.PROP_CREATED, createdAt);
        props.put(CtsModel.PROP_CORRESPONDENCE_TYPE, correspondenceType);
        props.put(CtsModel.PROP_CASE_STATUS, caseStatus);
        props.put(CtsModel.PROP_CASE_TASK, caseTask);
        props.put(CtsModel.PROP_URN_SUFFIX, urnSuffix);
        props.put(CtsModel.PROP_MARKUP_DECISION, "");
        props.put(CtsModel.PROP_MARKUP_UNIT, "");
        props.put(CtsModel.PROP_MARKUP_TOPIC, "");
        props.put(CtsModel.PROP_MARKUP_MINISTER, "");
        props.put(CtsModel.PROP_SECONDARY_TOPIC, "");
        props.put(CtsModel.PROP_ASSIGNED_UNIT, unit);
        props.put(CtsModel.PROP_ASSIGNED_TEAM, group);
        props.put(CtsModel.PROP_ASSIGNED_USER, username);
        props.put(CtsModel.PROP_ORIGINAL_DRAFTER_UNIT, unit);
        props.put(CtsModel.PROP_ORIGINAL_DRAFTER_TEAM, group);
        props.put(CtsModel.PROP_ORIGINAL_DRAFTER_USER, username);
        props.put(CtsModel.PROP_IS_LINKED_CASE, false);
        props.put(CtsModel.PROP_CASE_WORKFLOW_STATUS, caseWorkflowStatus);
        props.put(CtsModel.PROP_CASE_MANDATORY_FIELDS, caseMandatoryFields);
        props.put(CtsModel.PROP_CASE_MANDATORY_FIELDS_DEPS, "");
        props.put(CtsModel.PROP_CASE_MANDATORY_FIELDS_STATUS, caseMandatoryFieldStatus);
        props.put(CtsModel.PROP_CASE_MANDATORY_FIELDS_TASK, caseMandatoryFieldTask);
        props.put(CtsModel.PROP_OGD_NAME, "");
        props.put(CtsModel.PROP_STATUS_UPDATED_DATETIME, statusUpdatedDatetime);
        props.put(CtsModel.PROP_TASK_UPDATED_DATETIME, taskUpdatedDatetime);
        props.put(CtsModel.PROP_OWNER_UPDATED_DATETIME, ownerUpdatedDatetime);
        props.put(CtsModel.PROP_AUTO_CREATED_CASE, true);
        // PQ specific
        props.put(CtsModel.PROP_UIN, uin);
        props.put(CtsModel.PROP_OP_DATE, opDate);
        props.put(CtsModel.PROP_WO_DATE, woDate);
        props.put(CtsModel.PROP_QUESTION_NUMBER, "");
        props.put(CtsModel.PROP_QUESTION_TEXT, "");
        props.put(CtsModel.PROP_RECEIVED_TYPE, "");
        props.put(CtsModel.PROP_ANSWER_TEXT, "");
        props.put(CtsModel.PROP_MEMBER, "");
        props.put(CtsModel.PROP_CONSTITUENCY, "");
        props.put(CtsModel.PROP_PARTY, "");
        props.put(CtsModel.PROP_SIGNED_BY_HOME_SEC, false);
        props.put(CtsModel.PROP_SIGNED_BY_LORDS_MINISTER, false);
        props.put(CtsModel.PROP_LORDS_MINISTER, "");
        props.put(CtsModel.PROP_REVIEWED_BY_PERM_SEC, false);
        props.put(CtsModel.PROP_REVIEWED_BY_SPADS, false);
        props.put(CtsModel.PROP_ROUND_ROBIN, false);
        props.put(CtsModel.PROP_CABINET_OFFICE_GUIDANCE, "");
        props.put(CtsModel.PROP_TRANSFER_DEPARTMENT_NAME, "");
        props.put(CtsModel.PROP_IS_GROUPED_SLAVE, false);
        props.put(CtsModel.PROP_IS_GROUPED_MASTER, false);
        props.put(CtsModel.PROP_MASTER_NODE_REF, "");
        props.put(CtsModel.PROP_ANSWERING_MINISTER, "");
        props.put(CtsModel.PROP_ANSWERING_MINISTER_ID, "");
        return props;
    }

    private PermissionChecker mockPermissionChecker() {
        PermissionChecker permissionChecker = mock(PermissionChecker.class);
        serviceRegistry = mock(ServiceRegistry.class);
        caseNode = new NodeRef(nodeRefString);
        TemplateNode node = new TemplateNode(caseNode, serviceRegistry, null);
        when(permissionChecker.hasPermission(node.getNodeRef(), CtsPermissions.ALLOCATE)).thenReturn(true);
        List<CmisExtensionElement> propertyPermissions = new ArrayList<CmisExtensionElement>();
        CmisExtensionElement propertyPermission = new CmisExtensionElementImpl(null, "canUpdateProperties", null, "true");
        propertyPermissions.add(propertyPermission);
        when(permissionChecker.getPropertyPermissions(caseNode)).thenReturn(propertyPermissions);
        String nodeRefString = caseNode.getId();
        AuthorityService as = mock(AuthorityService.class);
        when(permissionChecker.getAuthorityService()).thenReturn(as);
        when(as.authorityExists(nodeRefString)).thenReturn(false);
        return permissionChecker;
    }

    /**
     * Additional permissions to test for
     * x "canAssignUser": "true",
     * x "canDeleteObject": "true",
     * x "canUpdateProperties": "true",
     * "canGetFolderTree": "true",
     * "canGetProperties": "true",
     * "canGetObjectRelationships": "true",
     * "canGetObjectParents": "true",
     * "canGetFolderParent": "true",
     * "canGetDescendants": "true",
     * "canMoveObject": "true",
     * "canApplyPolicy": "false",
     * "canGetAppliedPolicies": "true",
     * "canRemovePolicy": "false",
     * "canGetChildren": "true",
     * "canCreateDocument": "true",
     * "canCreateFolder": "true",
     * "canCreateRelationship": "true",
     * "canDeleteTree": "true",
     * "canGetACL": "true",
     * "canApplyACL": "true"
     */
    private PermissionService mockPermissionService() {
        PermissionService permissionService = mock(PermissionService.class);
        when(permissionService.hasPermission(caseNode, PermissionService.WRITE_PROPERTIES)).thenReturn(AccessStatus.ALLOWED);
        when(permissionService.hasPermission(caseNode, PermissionService.DELETE)).thenReturn(AccessStatus.ALLOWED);
        when(permissionService.hasPermission(caseNode, PermissionService.READ_CHILDREN)).thenReturn(AccessStatus.ALLOWED);
        when(permissionService.hasPermission(caseNode, PermissionService.READ_PROPERTIES)).thenReturn(AccessStatus.ALLOWED);
        when(permissionService.hasPermission(caseNode, PermissionService.READ_ASSOCIATIONS)).thenReturn(AccessStatus.ALLOWED);
        when(permissionService.hasPermission(caseNode, PermissionService.READ_ASSOCIATIONS)).thenReturn(AccessStatus.ALLOWED);
        when(permissionService.hasPermission(caseNode, PermissionService.READ_ASSOCIATIONS)).thenReturn(AccessStatus.ALLOWED);
        when(permissionService.hasPermission(caseNode, PermissionService.READ)).thenReturn(AccessStatus.ALLOWED);
        when(permissionService.hasPermission(caseNode, PermissionService.WRITE)).thenReturn(AccessStatus.ALLOWED);
        when(permissionService.hasPermission(caseNode, PermissionService.READ_PERMISSIONS)).thenReturn(AccessStatus.ALLOWED);
        when(permissionService.hasPermission(caseNode, PermissionService.READ_CHILDREN)).thenReturn(AccessStatus.ALLOWED);
        when(permissionService.hasPermission(caseNode, PermissionService.ADD_CHILDREN)).thenReturn(AccessStatus.ALLOWED);
        when(permissionService.hasPermission(caseNode, PermissionService.CREATE_CHILDREN)).thenReturn(AccessStatus.ALLOWED);
        when(permissionService.hasPermission(caseNode, PermissionService.CREATE_ASSOCIATIONS)).thenReturn(AccessStatus.ALLOWED);
        when(permissionService.hasPermission(caseNode, PermissionService.DELETE_CHILDREN)).thenReturn(AccessStatus.ALLOWED);
        when(permissionService.hasPermission(caseNode, PermissionService.READ_PERMISSIONS)).thenReturn(AccessStatus.ALLOWED);
        when(permissionService.hasPermission(caseNode, PermissionService.CHANGE_PERMISSIONS)).thenReturn(AccessStatus.ALLOWED);
        return permissionService;
    }


    @Test
    public void testExecuteWebScript() {
        // Pre-condition Asserts
        //assertNotNull(caseNode);

        String jsonString = null;
        JsonNode actualObj = null;

        // Methods
        try {
            webscript = new GetCaseWebScript();
            webscript.setCtsFolderHelper(ctsFolderHelper);
            webscript.setNodeService(nodeService);
            webscript.setServiceRegistry(serviceRegistry);
            webscript.setPermissionChecker(permissionChecker);
            webscript.setPermissionService(permissionService);
            webscript.setCaseMapperHelper(caseMapperHelper);

            when(caseMapperHelper.getCaseDisplayStatus("New")).thenReturn("Create");
            when(caseMapperHelper.getCaseDisplayTask("Create case")).thenReturn("Create Case");
            when(caseMapperHelper.getCaseProgressStatus("New")).thenReturn(1);


            WebScriptRequest request = mock(WebScriptRequest.class);
            when(ctsFolderHelper.getNodeRef(request.getParameter("nodeRef"))).thenReturn(nodeRefString);

            NodeRef nodeRef = new NodeRef(nodeRefString);
            when(nodeService.getProperties(nodeRef)).thenReturn(mockCaseNodeProperties());

            // mock the WebScriptResponse
            WebScriptResponse response = mock(WebScriptResponse.class);
            StringWriter writer = new StringWriter();
            doReturn(writer).when(response).getWriter();
            webscript.execute(request, response);

            // convert to JSON for our test asserts
            jsonString = writer.toString();
            ObjectMapper mapper = new ObjectMapper();
            actualObj = mapper.readTree(jsonString);

        } catch (JsonProcessingException e) {
            fail("Invalid JSON Response:" + jsonString);
            e.printStackTrace();
        } catch (IOException e) {
            fail("Could not execute webscript.");
            e.printStackTrace();
        }

        // Post-condition Asserts
        // CtsCase Case Node JSON
        JsonNode jsonNodeRoot_caseNode = actualObj.get("ctsCase");
        assertNotNull("Missing ctsCase node", jsonNodeRoot_caseNode);
        assertEquals(JsonNodeType.OBJECT, jsonNodeRoot_caseNode.getNodeType());
        assertEquals(caseNode.getStoreRef() + "/" + caseNode.getId(), jsonNodeRoot_caseNode.get("id").textValue());
        assertEquals("Create", jsonNodeRoot_caseNode.get("displayStatus").textValue());
        assertEquals("Create Case", jsonNodeRoot_caseNode.get("displayTask").textValue());
        assertTrue(1 == jsonNodeRoot_caseNode.get("caseProgressStatus").intValue());

        assertEquals(urnSuffix, jsonNodeRoot_caseNode.get("urnSuffix").textValue());
        assertEquals(correspondenceType, jsonNodeRoot_caseNode.get("correspondenceType").textValue());

        Calendar calendar = new GregorianCalendar(2016, 5, 15, 01, 23, 45);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
        String expectedDateStr = df.format(calendar.getTime());
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        String expectedDateStrLon = df.format(calendar.getTime());
        assertEquals(expectedDateStrLon, jsonNodeRoot_caseNode.get("dateCreated").textValue());
        assertEquals(expectedDateStr, jsonNodeRoot_caseNode.get("statusUpdatedDatetime").textValue());
        assertEquals(expectedDateStr, jsonNodeRoot_caseNode.get("taskUpdatedDatetime").textValue());
        assertEquals(expectedDateStr, jsonNodeRoot_caseNode.get("ownerUpdatedDatetime").textValue());
        assertEquals(expectedDateStr, jsonNodeRoot_caseNode.get("opDate").textValue());
        assertEquals(expectedDateStr, jsonNodeRoot_caseNode.get("woDate").textValue());

        JsonNode jsonNodeRoot_properties = actualObj.get("properties");
        assertNotNull("Missing properties node", jsonNodeRoot_properties);
        assertEquals(JsonNodeType.OBJECT, jsonNodeRoot_properties.getNodeType());

        // CtsCase Permissions
        JsonNode jsonNode_permissions = jsonNodeRoot_properties.get("permissions");
        assertNotNull("Missing permissions child node", jsonNode_permissions);
        assertEquals(JsonNodeType.OBJECT, jsonNode_permissions.getNodeType());

        assertEquals(true, jsonNode_permissions.get("canAssignUser").asBoolean());
        assertEquals(true, jsonNode_permissions.get("canDeleteObject").asBoolean());
        assertEquals(true, jsonNode_permissions.get("canUpdateProperties").asBoolean());
        assertEquals(true, jsonNode_permissions.get("canGetFolderTree").asBoolean());
        assertEquals(true, jsonNode_permissions.get("canGetProperties").asBoolean());
        assertEquals(true, jsonNode_permissions.get("canGetObjectRelationships").asBoolean());
        assertEquals(true, jsonNode_permissions.get("canGetObjectParents").asBoolean());
        assertEquals(true, jsonNode_permissions.get("canGetFolderParent").asBoolean());
        assertEquals(true, jsonNode_permissions.get("canGetDescendants").asBoolean());
        assertEquals(true, jsonNode_permissions.get("canMoveObject").asBoolean());
        assertEquals(false, jsonNode_permissions.get("canApplyPolicy").asBoolean());
        assertEquals(true, jsonNode_permissions.get("canGetAppliedPolicies").asBoolean());
        assertEquals(false, jsonNode_permissions.get("canRemovePolicy").asBoolean());
        assertEquals(true, jsonNode_permissions.get("canGetChildren").asBoolean());
        assertEquals(true, jsonNode_permissions.get("canCreateDocument").asBoolean());
        assertEquals(true, jsonNode_permissions.get("canCreateFolder").asBoolean());
        assertEquals(true, jsonNode_permissions.get("canCreateRelationship").asBoolean());
        assertEquals(true, jsonNode_permissions.get("canDeleteTree").asBoolean());
        assertEquals(true, jsonNode_permissions.get("canGetACL").asBoolean());
        assertEquals(true, jsonNode_permissions.get("canApplyACL").asBoolean());
        assertEquals(true, jsonNode_permissions.get("canDeleteObject").asBoolean());
    }
}


