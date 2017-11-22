package uk.gov.homeoffice.ctsv2.webscripts;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.alfresco.repo.web.scripts.BaseWebScriptTest;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.security.MutableAuthenticationService;
import org.alfresco.service.cmr.security.PersonService;
import org.junit.Test;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import uk.gov.homeoffice.cts.email.EmailService;
import uk.gov.homeoffice.ctsv2.webscripts.ResetUserPasswordWebscript;

import java.io.IOException;
import java.io.StringWriter;

import static org.mockito.Mockito.*;

public class ResetUserPasswordWebscriptTest extends BaseWebScriptTest {

    private ResetUserPasswordWebscript resetUserPasswordWebscript;
    private MutableAuthenticationService authenticationService;
    private PersonService personService;
    private EmailService emailService;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        resetUserPasswordWebscript = new ResetUserPasswordWebscript();
        authenticationService = mock(MutableAuthenticationService.class);
        personService = mock(PersonService.class);
        emailService = mock(EmailService.class);
        resetUserPasswordWebscript.setAuthenticationService(authenticationService);
        resetUserPasswordWebscript.setPersonService(personService);
        resetUserPasswordWebscript.setEmailService(emailService);
        resetUserPasswordWebscript.setCtsMailSubjectResetUserPassword("Password Reset");
        resetUserPasswordWebscript.setCtsMailTemplateResetUserPassword("Password Reset Template");
    }

    @Test
    public void testExecuteWebScriptReturnsErrorWhenUserNotFound() throws IOException {
        // given
        WebScriptRequest request = mockRequestParams();
        when(personService.getPersonOrNull("testUser")).thenReturn(null);

        // mock the WebScriptResponse
        WebScriptResponse response = mock(WebScriptResponse.class);
        StringWriter writer = new StringWriter();
        doReturn(writer).when(response).getWriter();

        // when
        resetUserPasswordWebscript.execute(request, response);

        // then
        // convert to JSON for our test asserts
        String jsonString = writer.toString();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode actualObj = mapper.readTree(jsonString);

        String status = actualObj.get("status").textValue();
        assertEquals(status, "ERROR");

        verify(personService).getPersonOrNull("testUser");
        verifyNoMoreInteractions(personService);
        verifyZeroInteractions(authenticationService);
        verifyZeroInteractions(emailService);
    }

    @Test
    public void testExecuteWebScriptReturnsSuccessWhenUserFound() throws IOException {
        // given
        WebScriptRequest request = mockRequestParams();
        NodeRef personNode = new NodeRef("workspace://SpacesStore/person");
        when(personService.getPersonOrNull("testUser")).thenReturn(personNode);
        PersonService.PersonInfo personInfo = mock(PersonService.PersonInfo.class);
        when(personInfo.getUserName()).thenReturn("TestUser");
        when(personService.getPerson(personNode)).thenReturn(personInfo);

        // mock the WebScriptResponse
        WebScriptResponse response = mock(WebScriptResponse.class);
        StringWriter writer = new StringWriter();
        doReturn(writer).when(response).getWriter();

        // when
        resetUserPasswordWebscript.execute(request, response);

        // then
        // convert to JSON for our test asserts
        String jsonString = writer.toString();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode actualObj = mapper.readTree(jsonString);

        String status = actualObj.get("status").textValue();
        assertEquals(status, "SUCCESS");

        verify(personService).getPersonOrNull("testUser");
        verify(personService).getPerson(personNode);
        verifyNoMoreInteractions(personService);
        verify(authenticationService).setAuthentication(eq("TestUser"), any(char[].class));
        verify(emailService).sendEmail(eq("TestUser"), eq("Password Reset"), eq("Password Reset Template"), eq("/login"), isNull(NodeRef.class), anyMap());
    }

    private WebScriptRequest mockRequestParams() {
        WebScriptRequest request = mock(WebScriptRequest.class);
        when(request.getParameter("username")).thenReturn("testUser");
        return request;
    }
}
