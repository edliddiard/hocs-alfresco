package uk.gov.homeoffice.ctsv2.webscripts;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import org.alfresco.repo.web.scripts.BaseWebScriptTest;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import uk.gov.homeoffice.ctsv2.model.CtsCase;
import uk.gov.homeoffice.ctsv2.services.todo.ToDoListResponse;
import uk.gov.homeoffice.ctsv2.services.todo.ToDoListService;

import java.io.IOException;
import java.io.StringWriter;
import java.util.*;

import static org.mockito.Mockito.*;

/**
 * Created by dawudr on 06/06/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class GetToDoListWebscriptTest extends BaseWebScriptTest {

    @Mock
    private ToDoListService toDoListService;

    @InjectMocks
    private GetToDoListWebScript webscript;

    @Test
    public void testExecuteWebScriptResponsePermissions() {
        String jsonString = null;
        JsonNode actualObj = null;

        // given
        try {
            WebScriptRequest request = mock(WebScriptRequest.class);
            ToDoListResponse toDoListResponse = new ToDoListResponse();
            toDoListResponse.totalResults(1);
            Map<String, Object> ctsCaseMap = new HashMap<>();
            Map<String, Object> allowableActionsMap = new HashMap<>();
            allowableActionsMap.put("canAssignUser", true);
            allowableActionsMap.put("canDeleteObject", true);
            allowableActionsMap.put("canUpdateProperties", true);
            allowableActionsMap.put("canGetFolderTree", true);
            allowableActionsMap.put("canGetProperties", true);
            allowableActionsMap.put("canGetObjectRelationships", true);
            allowableActionsMap.put("canGetObjectParents", true);
            allowableActionsMap.put("canGetFolderParent", true);
            allowableActionsMap.put("canGetDescendants", true);
            allowableActionsMap.put("canMoveObject", true);
            allowableActionsMap.put("canApplyPolicy", false);
            allowableActionsMap.put("canGetAppliedPolicies", true);
            allowableActionsMap.put("canRemovePolicy", false);
            allowableActionsMap.put("canGetChildren", true);
            allowableActionsMap.put("canCreateDocument", true);
            allowableActionsMap.put("canCreateFolder", true);
            allowableActionsMap.put("canCreateRelationship", true);
            allowableActionsMap.put("canDeleteTree", true);
            allowableActionsMap.put("canGetACL", true);
            allowableActionsMap.put("canApplyACL", true);
            allowableActionsMap.put("canDeleteObject", true);
            ctsCaseMap.put("allowableActions", allowableActionsMap);
            toDoListResponse.addCase(ctsCaseMap);
            when(toDoListService.getToDoList(request)).thenReturn(toDoListResponse);

            // mock the WebScriptResponse
            WebScriptResponse response = mock(WebScriptResponse.class);
            StringWriter writer = new StringWriter();
            doReturn(writer).when(response).getWriter();
            // when
            webscript.execute(request, response);

            // then
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
        // To Do List Cases JSON
        JsonNode totalResults = actualObj.get("totalResults");
        assertTrue(1 == totalResults.asInt());
        JsonNode jsonNodeRoot_ToDoListNode = actualObj.get("ctsCases");
        assertNotNull("Missing ctsCases node", jsonNodeRoot_ToDoListNode);
        assertEquals(JsonNodeType.ARRAY, jsonNodeRoot_ToDoListNode.getNodeType());
        ArrayNode arrayNode = (ArrayNode) jsonNodeRoot_ToDoListNode;
        Iterator<JsonNode> i = arrayNode.elements();
        while (i.hasNext()) {
            JsonNode jsonNode_ToDoItem = i.next();
            assertNotNull("Missing ToDoItem node", jsonNode_ToDoItem);

            // CtsCase Permissions
            JsonNode jsonNode_permissions = jsonNode_ToDoItem.get("allowableActions");
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

    @Test
    public void testAllFieldValues() {

        String jsonString = null;
        JsonNode actualObj = null;

        // given
        try {
            WebScriptRequest request = mock(WebScriptRequest.class);
            ToDoListResponse toDoListResponse = new ToDoListResponse();
            toDoListResponse.totalResults(1);
            Map<String, Object> ctsCaseMap = new HashMap<>();
            CtsCase ctsCase = new CtsCase();
            ctsCase.setUin("0009");
            ctsCase.setApplicantForename("Test-User-FirstName");
            ctsCase.setApplicantSurname("Test-User-SurName");
            ctsCase.setCorrespondenceType("OPQ");
            ctsCase.setIsGroupedSlave(false);
            ctsCaseMap.put("case", ctsCase);
            toDoListResponse.addCase(ctsCaseMap);
            when(toDoListService.getToDoList(request)).thenReturn(toDoListResponse);

            // mock the WebScriptResponse
            WebScriptResponse response = mock(WebScriptResponse.class);
            StringWriter writer = new StringWriter();
            doReturn(writer).when(response).getWriter();

            // when
            webscript.execute(request, response);

            // then
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

        JsonNode totalResults = actualObj.get("totalResults");
        assertTrue(1 == totalResults.asInt());
        JsonNode jsonNodeRoot_ToDoListNode = actualObj.get("ctsCases");
        ArrayNode arrayNode = (ArrayNode) jsonNodeRoot_ToDoListNode;
        Iterator<JsonNode> i = arrayNode.elements();
        while (i.hasNext()) {
            JsonNode jsonNode_ToDoItem = i.next();
            assertNotNull("Missing ToDoItem node", jsonNode_ToDoItem);
            assertEquals(JsonNodeType.OBJECT, jsonNode_ToDoItem.getNodeType());
            JsonNode jsonNode_ToDoItem_case = jsonNode_ToDoItem.get("case");
            System.out.println(jsonNode_ToDoItem_case);
            assertEquals("0009", jsonNode_ToDoItem_case.get("uin").textValue());
            assertEquals("Test-User-FirstName", jsonNode_ToDoItem_case.get("applicantForename").textValue());
            assertEquals("Test-User-SurName", jsonNode_ToDoItem_case.get("applicantSurname").textValue());
            assertEquals("OPQ", jsonNode_ToDoItem_case.get("correspondenceType").textValue());
            assertFalse(jsonNode_ToDoItem_case.get("isGroupedSlave").booleanValue());
        }
    }
}
