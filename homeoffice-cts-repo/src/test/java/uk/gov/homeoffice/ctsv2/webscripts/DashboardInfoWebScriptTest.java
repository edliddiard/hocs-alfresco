package uk.gov.homeoffice.ctsv2.webscripts;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.alfresco.repo.web.scripts.BaseWebScriptTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import uk.gov.homeoffice.ctsv2.dashboard.DashboardProcessor;
import uk.gov.homeoffice.ctsv2.model.SummaryByStatus;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DashboardInfoWebScriptTest extends BaseWebScriptTest {

    //set mock objects
    @Mock private DashboardProcessor dashboardProcessor;
    @Mock private WebScriptRequest request;
    @Mock private WebScriptResponse response;

    @InjectMocks
    private DashboardInfoWebScript webscript;

    @Test
    public void testExecute() throws Exception {
        Map<String, Map<String, SummaryByStatus>> mockRes = new HashMap<>();
        Map<String, SummaryByStatus> mockSummary1 = new HashMap<>();
        mockSummary1.put("NEW", new SummaryByStatus(1, 2, 3));
        mockSummary1.put("QA", new SummaryByStatus(7, 8, 9));
        Map<String, SummaryByStatus> mockSummary2 = new HashMap<>();
        mockSummary2.put("NEW", new SummaryByStatus(10, 11, 12));
        mockSummary2.put("QA", new SummaryByStatus(4, 5, 6));
        mockRes.put("MIN", mockSummary1);
        mockRes.put("FOI", mockSummary2);
        when(dashboardProcessor.getSummary()).thenReturn(mockRes);

        // mock the WebScriptResponse
        StringWriter writer = new StringWriter();
        when(response.getWriter()).thenReturn(writer);

        // when
        webscript.execute(request, response);

        // convert to JSON for our test asserts
        String jsonString = writer.toString();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode actualObj = mapper.readTree(jsonString);

        // then expectations
        verify(dashboardProcessor).getSummary();
        assertResponse(actualObj);
    }

    private void assertResponse(JsonNode actualObj) {
        assertThat(actualObj.size(), is(2));

        JsonNode foiNode = actualObj.get("FOI");
        assertThat(foiNode.size(), is(2));
        JsonNode foiNewNode = foiNode.get("NEW");
        assertThat(foiNewNode.size(), is(3));
        assertThat(foiNewNode.get("open").intValue(), is(10));
        assertThat(foiNewNode.get("openAndOverdue").intValue(), is(11));
        assertThat(foiNewNode.get("returned").intValue(), is(12));
        JsonNode foiQANode = foiNode.get("QA");
        assertThat(foiQANode.size(), is(3));
        assertThat(foiQANode.get("open").intValue(), is(4));
        assertThat(foiQANode.get("openAndOverdue").intValue(), is(5));
        assertThat(foiQANode.get("returned").intValue(), is(6));

        JsonNode minNode = actualObj.get("MIN");
        assertThat(minNode.size(), is(2));
        JsonNode minNewNode = minNode.get("NEW");
        assertThat(minNewNode.size(), is(3));
        assertThat(minNewNode.get("open").intValue(), is(1));
        assertThat(minNewNode.get("openAndOverdue").intValue(), is(2));
        assertThat(minNewNode.get("returned").intValue(), is(3));
        JsonNode minQANode = minNode.get("QA");
        assertThat(minQANode.size(), is(3));
        assertThat(minQANode.get("open").intValue(), is(7));
        assertThat(minQANode.get("openAndOverdue").intValue(), is(8));
        assertThat(minQANode.get("returned").intValue(), is(9));
    }
}
