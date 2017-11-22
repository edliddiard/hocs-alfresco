package uk.gov.homeoffice.ctsv2.webscripts;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.extensions.webscripts.servlet.FormData;
import uk.gov.homeoffice.cts.helpers.CreateCaseHelper;
import uk.gov.homeoffice.cts.model.FileDetails;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CreateCaseWebScriptTest {

    @Mock
    private CreateCaseHelper createCaseHelper;

    @Mock
    private WebScriptRequest request;

    @Mock
    private WebScriptResponse response;

    @InjectMocks
    private CreateCaseWebScript webScript;

    @Test
    public void testExecute() throws Exception {
        FormData formData = mock(FormData.class);
        FormData.FormField[] formFields = new FormData.FormField[2];

        FormData.FormField caseTypeField = mock(FormData.FormField.class);
        when(caseTypeField.getName()).thenReturn("caseType");
        when(caseTypeField.getValue()).thenReturn("LTQ");

        FormData.FormField fileField = mock(FormData.FormField.class);
        when(fileField.getName()).thenReturn("file");
        when(fileField.getIsFile()).thenReturn(true);
        when(fileField.getInputStream()).thenReturn(new ByteArrayInputStream("file".getBytes()));

        formFields[0] = caseTypeField;
        formFields[1] = fileField;

        when(formData.getFields()).thenReturn(formFields);
        when(request.parseContent()).thenReturn(formData);

        // convert to JSON for our test asserts
        StringWriter writer = new StringWriter();
        when(response.getWriter()).thenReturn(writer);

        when(createCaseHelper.createCase(anyListOf(FileDetails.class), eq("LTQ"))).thenReturn("123456");

        webScript.execute(request, response);

        String jsonString = writer.toString();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode actualObj = mapper.readTree(jsonString);
        assertThat(actualObj.get("caseRef").asText(), is("123456"));

        verify(createCaseHelper).createCase(anyListOf(FileDetails.class), eq("LTQ"));
    }
}
