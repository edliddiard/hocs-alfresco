package uk.gov.homeoffice.ctsv2.webscripts;

import org.alfresco.repo.web.scripts.content.ContentStreamer;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.util.TempFileProvider;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import uk.gov.homeoffice.cts.exceptions.SpreadsheetGeneratorException;
import uk.gov.homeoffice.ctsv2.services.todo.ToDoListResponse;
import uk.gov.homeoffice.ctsv2.services.todo.ToDoListService;
import uk.gov.homeoffice.ctsv2.services.todo.ToDoListSpreadsheetGenerator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public final class ExportToDoListWebScript extends AbstractWebScript {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExportToDoListWebScript.class);

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("ddMMyyyyHHmmss");

    private ToDoListService toDoListService;
    private ToDoListSpreadsheetGenerator toDoListSpreadsheetGenerator;
    private AuthenticationService authenticationService;
    private ContentStreamer contentStreamer;

    @Override
    public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {
        LOGGER.debug("Running GetToDoListWebScript");
        ToDoListResponse toDoListCases = toDoListService.getToDoList(req);

        try {
            String fileName = generateFileName();
            File output = streamWorkbookToFile(toDoListSpreadsheetGenerator.generateWorkbook(toDoListCases), fileName);
            Map<String, Object> model = new HashMap<>();
            contentStreamer.streamContent(req, res, output, null, true, fileName, model);
        } catch (SpreadsheetGeneratorException e) {
            LOGGER.error("Unable to generate spreadsheet: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Create a filename including the user and timestamp.
     *
     * @return String
     */
    private String generateFileName() {
        return "ToDoList_" + authenticationService.getCurrentUserName() + "_" + DATE_FORMAT.format(new Date()) + ".xls";
    }

    /**
     * Stream workbook to file using specified name.
     *
     * @param workbook HSSFWorkbook
     * @param fileName String
     */
    private File streamWorkbookToFile(HSSFWorkbook workbook, String fileName) {
        File file = TempFileProvider.createTempFile(fileName, "");
        FileOutputStream fileOut = null;
        try {
            fileOut = new FileOutputStream(file);
            workbook.write(fileOut);
        } catch (FileNotFoundException e) {
            LOGGER.error(e.getMessage());
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        } finally {
            if (fileOut != null) {
                try {
                    fileOut.close();
                } catch (IOException e) {
                    LOGGER.error(e.getMessage());
                }
            }
        }
        return file;
    }

    public void setToDoListService(ToDoListService toDoListService) {
        this.toDoListService = toDoListService;
    }

    public void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    public void setContentStreamer(ContentStreamer contentStreamer) {
        this.contentStreamer = contentStreamer;
    }

    public void setToDoListSpreadsheetGenerator(ToDoListSpreadsheetGenerator toDoListSpreadsheetGenerator) {
        this.toDoListSpreadsheetGenerator = toDoListSpreadsheetGenerator;
    }
}