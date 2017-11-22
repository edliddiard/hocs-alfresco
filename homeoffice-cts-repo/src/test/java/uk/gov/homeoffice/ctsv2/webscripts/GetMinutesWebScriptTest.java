package uk.gov.homeoffice.ctsv2.webscripts;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.model.Repository;
import org.alfresco.repo.web.scripts.BaseWebScriptTest;
import org.alfresco.service.cmr.repository.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import uk.gov.homeoffice.cts.action.MinutesSyncAction;
import uk.gov.homeoffice.cts.helpers.CtsFolderHelper;
import uk.gov.homeoffice.cts.model.CtsModel;
import uk.gov.homeoffice.ctsv2.model.CtsMinuteModel;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Iterator;

import static org.hamcrest.core.AnyOf.anyOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

/**
 * Created by dawudr on 15/06/2016.
 */

@RunWith(MockitoJUnitRunner.class)
public class GetMinutesWebScriptTest extends BaseWebScriptTest {

    private NodeRef caseNode;
    private String mockTestMinutesCollatedJSONStr;
    // Expected Test Properties
    private String nodeRefString = "workspace://SpacesStore/myCase";
    private String nodeRefMyCaseString = "myCase";
    private String name = "Add Minute Aspect Test (" + System.currentTimeMillis() + ")";
    private String title = "Mock Case Document";
    private String userName = "username001";
    private String group = "GROUP_" + "Parliamentary Questions Team";
    private String unit = "GROUP_" + "GROUP_Parliamentary Questions";
    private String urnSuffix = "0002016/16";
    private String time = "2016-06-15T01:23:45.000+0100";

    //set mock objects
    @Mock private NodeService nodeService;
    @Mock private Repository repository;
    @Mock private ContentService contentService;
    @Mock private MinutesSyncAction minutesSyncAction;
    @Mock private CtsFolderHelper ctsFolderHelper;
    @Mock private ContentReader reader;
    @Mock private ContentWriter contentWriter;
    @Mock private WebScriptRequest request;
    @Mock private WebScriptResponse response;
    @Mock private NodeRef.Status status;


    private GetMinutesWebScript webscript;

    @Before
    public void setUp() throws Exception {

        MockitoAnnotations.initMocks(this);

        webscript = new GetMinutesWebScript();


        // Setup variables
        caseNode = mockCaseNode();
        populateMockMinutesCollated();

        // Mock test instance

        webscript.setCtsFolderHelper(ctsFolderHelper);
        webscript.setContentService(contentService);
        webscript.setNodeService(nodeService);
        webscript.setMinutesSyncAction(minutesSyncAction);

        when(request.getParameter("nodeRef")).thenReturn(nodeRefMyCaseString);
        when(ctsFolderHelper.getNodeRef(nodeRefMyCaseString)).thenReturn(nodeRefString);
    }

    /*
     * test case scenario: case minutes access through v2 at least once and
     * trying to access it again
     */

    @Test
    public void whenCaseContainsMinutesCollated() {
        // given
        String jsonString = null;
        JsonNode actualObj = null;

        try {
            when(nodeService.getNodeStatus(caseNode)).thenReturn(status);
            when(status.isDeleted()).thenReturn(false);
            when(nodeService.hasAspect(caseNode, CtsMinuteModel.ASPECT_MINUTE)).thenReturn(true);
            when(reader.getContentString()).thenReturn(mockTestMinutesCollatedJSONStr);
            when(contentService.getReader(caseNode, CtsMinuteModel.PROP_MINUTE_COLLATED)).thenReturn(reader);

            // mock the WebScriptResponse
            StringWriter writer = new StringWriter();
            when(response.getWriter()).thenReturn(writer);

            // when
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

        // then expectations
        verify(contentService).getReader(caseNode, CtsMinuteModel.PROP_MINUTE_COLLATED);
        assertResponse(actualObj);

    }

    /*
     * test case scenario: case and case minutes created in v1/current
     * system and trying to access minutes using new v2 end point
     */

    @Test
    public void whenCaseNotContainsMinutesCollated() {

        when(nodeService.hasAspect(caseNode, CtsMinuteModel.ASPECT_MINUTE)).thenReturn(false);
        try {
            // mock the WebScriptResponse
            StringWriter writer = new StringWriter();
            when(response.getWriter()).thenReturn(writer);

            // when
            webscript.execute(request, response);
        } catch (IOException e) {
            fail("Could not execute webscript.");
            e.printStackTrace();
        }

        // then expectations
        verify(contentService, never()).getReader(caseNode, CtsMinuteModel.PROP_MINUTE_COLLATED);
    }

    private void assertResponse(JsonNode actualObj) {
        // CtsCase Case Node JSON
        JsonNode jsonNodeRoot_minutesNode = actualObj.get("minutes");
        assertNotNull("Missing minutes node", jsonNodeRoot_minutesNode);
        assertEquals(JsonNodeType.ARRAY, jsonNodeRoot_minutesNode.getNodeType());
        ArrayNode arrayNode = (ArrayNode) jsonNodeRoot_minutesNode;
        Iterator<JsonNode> i = arrayNode.elements();
        while (i.hasNext()) {
            JsonNode jsonNode = i.next();
            assertEquals(JsonNodeType.OBJECT, jsonNode.getNodeType());
            assertThat(jsonNode.get("dbid").longValue(), anyOf(is(1111L), is(1112L)));
            assertEquals(jsonNode.get("minuteDateTime").textValue(), time);
            assertThat(jsonNode.get("minuteContent").textValue(), anyOf(is("Case Comment"), is("Case Created")));
            assertThat(jsonNode.get("minuteUpdatedBy").textValue(), anyOf(is("ManualUser"), is("SystemUser")));
            assertThat(jsonNode.get("minuteType").textValue(), anyOf(is("manual"), is("system")));
        }
    }

    private void populateMockMinutesCollated() {

        mockTestMinutesCollatedJSONStr = "{\"minutes\":[{\"dbid\":1111,\"minuteDateTime\":\"" + time + "\",\"minuteContent\":\"Case Created\",\"minuteUpdatedBy\":\"SystemUser\",\"minuteType\":\"system\"},{\"dbid\":1112,\"minuteDateTime\":\"" + time + "\",\"minuteContent\":\"Case Comment\",\"minuteUpdatedBy\":\"ManualUser\",\"minuteType\":\"manual\"}]}";
    }

    private NodeRef mockCaseNode() {
        final NodeRef mockCaseNode = new NodeRef(nodeRefString);
        nodeService.setProperty(mockCaseNode, ContentModel.PROP_NAME, name);
        nodeService.setProperty(mockCaseNode, ContentModel.PROP_TITLE, title);
        nodeService.setProperty(mockCaseNode, CtsModel.PROP_URN_SUFFIX, urnSuffix);
        nodeService.setProperty(mockCaseNode, CtsModel.PROP_DOCUMENT_USER, userName);
        nodeService.setProperty(mockCaseNode, CtsModel.PROP_DOCUMENT_TEAM, group);
        nodeService.setProperty(mockCaseNode, CtsModel.PROP_DOCUMENT_UNIT, unit);
        return mockCaseNode;
    }

}
