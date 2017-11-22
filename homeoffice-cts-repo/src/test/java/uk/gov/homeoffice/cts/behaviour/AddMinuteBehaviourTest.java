package uk.gov.homeoffice.cts.behaviour;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.*;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.homeoffice.ctsv2.webscripts.CaseMapperHelper;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by chris on 22/05/2015.
 */
public class AddMinuteBehaviourTest {

    private AddMinuteBehaviour toTest;

    @Mock private PolicyComponent policyComponent;
    @Mock private NodeService nodeService;
    @Mock private ContentService contentService;
    @Mock private ChildAssociationRef childRef;
    @Mock private ContentReader reader;
    @Mock private ContentWriter writer;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        toTest = new AddMinuteBehaviour();
        toTest.setContentService(contentService);
        toTest.setNodeService(nodeService);
        toTest.setPolicyComponent(policyComponent);
    }

    @Test
    public void whenMinutesInText() throws JSONException {

        //Given
        String content = "Test-minutes";
        when(contentService.getReader(childRef.getChildRef(), ContentModel.PROP_CONTENT)).thenReturn(reader);
        when(reader.getContentString()).thenReturn(content);

        when(contentService.getWriter(childRef.getChildRef(), ContentModel.PROP_CONTENT, true)).thenReturn(writer);
        doNothing().when(writer).putContent(content);

        //When
        toTest.onCreateNode(childRef);

        //verify
        verify(writer).putContent(content);

    }

    @Test
    public void testJson() throws JSONException {
        String json = "{\"content\":\"asdasdasd\",\"minuteQaReviewOutcomes\":[\"Structure\",\"Non error Miscellaneous\"\n" +
                "],\"task\":\"QA Review\"}";
        AddMinuteBehaviour addMinuteBehaviour = new AddMinuteBehaviour();
        AddMinuteBehaviour.ComplexMinute complexMinute = addMinuteBehaviour.buildComplexMinute(new JSONObject(json));

        assertEquals("asdasdasd",complexMinute.minute);
        assertEquals("Structure,Non error Miscellaneous",complexMinute.reviewOutcome);
        assertEquals("QA Review",complexMinute.task);
    }

    @Test
    public void testNormalMinute() throws JSONException {
        String json = "{\"content\":\"asdasdasd\",\"minuteQaReviewOutcomes\":[],\"task\":\"\"}";
        AddMinuteBehaviour addMinuteBehaviour = new AddMinuteBehaviour();
        AddMinuteBehaviour.ComplexMinute complexMinute = addMinuteBehaviour.buildComplexMinute(new JSONObject(json));

        assertEquals("asdasdasd",complexMinute.minute);
        assertEquals("",complexMinute.reviewOutcome);
        assertEquals("",complexMinute.task);
    }

    @Test
    public void testNormalMinuteWithNulls() throws JSONException {
        String json = "{\"content\":\"asdasdasd\",\"minuteQaReviewOutcomes\":null,\"task\":null}";
        AddMinuteBehaviour addMinuteBehaviour = new AddMinuteBehaviour();
        AddMinuteBehaviour.ComplexMinute complexMinute = addMinuteBehaviour.buildComplexMinute(new JSONObject(json));

        assertEquals("asdasdasd",complexMinute.minute);
        assertEquals(null,complexMinute.reviewOutcome);
        assertEquals(null,complexMinute.task);
    }
}
