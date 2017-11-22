package uk.gov.homeoffice.ctsv2.webscripts;

import org.alfresco.repo.web.scripts.BaseWebScriptTest;
import org.alfresco.repo.web.scripts.content.ContentStreamer;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import uk.gov.homeoffice.ctsv2.model.CtsCase;
import uk.gov.homeoffice.ctsv2.services.todo.ToDoListResponse;
import uk.gov.homeoffice.ctsv2.services.todo.ToDoListService;
import uk.gov.homeoffice.ctsv2.services.todo.ToDoListSpreadsheetGenerator;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ExportToDoListWebScriptTest extends BaseWebScriptTest {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("ddMMyyyyHHmmss");

    @Mock
    private ToDoListService toDoListService;

    @Mock
    private ToDoListSpreadsheetGenerator toDoListSpreadsheetGenerator;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private ContentStreamer contentStreamer;

    @InjectMocks
    private ExportToDoListWebScript webscript;

    @Test
    public void testExecute() throws Exception {

        final WebScriptRequest request = mock(WebScriptRequest.class);
        final ToDoListResponse toDoListResponse = new ToDoListResponse();
        toDoListResponse.totalResults(1);
        final Map<String, Object> ctsCaseMap = new HashMap<>();
        final CtsCase ctsCase = new CtsCase();
        ctsCase.setUin("0009");
        ctsCaseMap.put("case", ctsCase);
        toDoListResponse.addCase(ctsCaseMap);
        when(toDoListService.getToDoList(request)).thenReturn(toDoListResponse);

        when(authenticationService.getCurrentUserName()).thenReturn("testUser");

        final HSSFWorkbook workbook = new HSSFWorkbook();
        when(toDoListSpreadsheetGenerator.generateWorkbook(toDoListResponse)).thenReturn(workbook);

        // mock the WebScriptResponse
        final WebScriptResponse response = mock(WebScriptResponse.class);

        // when
        webscript.execute(request, response);

        final String fileName = "ToDoList_testUser_";

        verify(toDoListService).getToDoList(request);
        verify(authenticationService).getCurrentUserName();
        verify(toDoListSpreadsheetGenerator).generateWorkbook(toDoListResponse);
        verify(contentStreamer).streamContent(eq(request), eq(response), any(File.class), isNull(Long.class), eq(true), startsWith(fileName), eq(new HashMap<String, Object>()));
        verifyNoMoreInteractions(toDoListService);
        verifyNoMoreInteractions(toDoListSpreadsheetGenerator);
        verifyNoMoreInteractions(contentStreamer);
        verifyNoMoreInteractions(authenticationService);
    }
}