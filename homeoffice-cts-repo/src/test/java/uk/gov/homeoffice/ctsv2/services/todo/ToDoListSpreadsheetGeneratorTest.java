package uk.gov.homeoffice.ctsv2.services.todo;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.joda.time.DateTime;
import org.junit.Test;
import uk.gov.homeoffice.ctsv2.model.CtsCase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class ToDoListSpreadsheetGeneratorTest {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    private static final List<String> HEADERS = Arrays.asList("Case Ref", "Case Id", "UIN", "URN Suffix",
            "Date Received", "Date Owner Updated", "Correspondence Type", "Response Deadline", "Advice",
            "Priority", "Member", "Reply To", "Correspondent Forename", "Correspondent Surname",
            "Assigned Unit", "Assigned Team", "Assigned User", "Markup Unit", "Markup Topic",
            "Secondary Topic", "Case Status", "Date Status Updated", "Case Task", "Date Task Updated",
            "Is Grouped Master", "Is Grouped Slave", "Is Linked Case", "FOI is EIR", "HMPO Stage",
            "Master Node Ref", "Applicant Forename", "Applicant Surname", "Canonical Correspondent", "Returned Count");

    private ToDoListSpreadsheetGenerator toDoListSpreadsheetGenerator = new ToDoListSpreadsheetGenerator();

    @Test
    public void testGenerateWorkbook() throws Exception {
        final Date dateReceived = new DateTime().minusDays(15).toDate();
        final Date ownerUpdatedDatetime = new DateTime().minusDays(10).toDate();
        final Date caseResponseDeadline = new DateTime().plusDays(5).toDate();
        final Date statusUpdatedDatetime = new DateTime().minusDays(5).toDate();
        final Date taskUpdatedDatetime = new DateTime().minusDays(4).toDate();

        final List<String> EXPECTED_DATA = Arrays.asList("CaseRef", "CaseId", "0009", "UrnSuffix", DATE_FORMAT.format(dateReceived),
                DATE_FORMAT.format(ownerUpdatedDatetime), "MIN", DATE_FORMAT.format(caseResponseDeadline), "Yes", "Yes",
                "Member", "ReplyToName", "Correspondent-Forename", "Correspondent-Surname", "Test Unit", "Test Team",
                "Test User", "Test Markup Unit", "Test Markup Topic", "Test Secondary Topic", "Open",
                DATE_FORMAT.format(statusUpdatedDatetime), "Draft", DATE_FORMAT.format(taskUpdatedDatetime),
                "No", "No", "Yes", "No", "HMPO Test Stage", "", "Test-User-FirstName", "Test-User-SurName", "", "2");

        final ToDoListResponse toDoListResponse = new ToDoListResponse();
        toDoListResponse.totalResults(1);
        final Map<String, Object> ctsCaseMap = new HashMap<>();
        final CtsCase ctsCase = new CtsCase();
        ctsCase.setCaseRef("CaseRef");
        ctsCase.setId("CaseId");
        ctsCase.setUin("0009");
        ctsCase.setUrnSuffix("UrnSuffix");
        ctsCase.setDateReceived(dateReceived);
        ctsCase.setOwnerUpdatedDatetime(ownerUpdatedDatetime);
        ctsCase.setCorrespondenceType("MIN");
        ctsCase.setCaseResponseDeadline(caseResponseDeadline);
        ctsCase.setAdvice(true);
        ctsCase.setPriority(true);
        ctsCase.setMember("Member");
        ctsCase.setReplyToName("ReplyToName");
        ctsCase.setCorrespondentForename("Correspondent-Forename");
        ctsCase.setCorrespondentSurname("Correspondent-Surname");
        ctsCase.setAssignedUnit("Test Unit");
        ctsCase.setAssignedTeam("Test Team");
        ctsCase.setAssignedUser("Test User");
        ctsCase.setMarkupUnit("Test Markup Unit");
        ctsCase.setMarkupTopic("Test Markup Topic");
        ctsCase.setSecondaryTopic("Test Secondary Topic");
        ctsCase.setDisplayStatus("Open");
        ctsCase.setStatusUpdatedDatetime(statusUpdatedDatetime);
        ctsCase.setDisplayTask("Draft");
        ctsCase.setTaskUpdatedDatetime(taskUpdatedDatetime);
        ctsCase.setIsGroupedMaster(false);
        ctsCase.setIsGroupedSlave(false);
        ctsCase.setIsLinkedCase(true);
        ctsCase.setFoiIsEir(false);
        ctsCase.setHmpoStage("HMPO Test Stage");
        ctsCase.setMasterNodeRef(null);
        ctsCase.setApplicantForename("Test-User-FirstName");
        ctsCase.setApplicantSurname("Test-User-SurName");
        ctsCase.setReturnedCount(2);
        ctsCaseMap.put("case", ctsCase);
        toDoListResponse.addCase(ctsCaseMap);
        final HSSFWorkbook workbook = toDoListSpreadsheetGenerator.generateWorkbook(toDoListResponse);

        assertThat(workbook.getNumberOfSheets(), is(1));
        final HSSFSheet sheet = workbook.getSheet("Results");
        assertThat(sheet, notNullValue());
        assertThat(sheet.getPhysicalNumberOfRows(), is(2));

        //Check Headers
        final HSSFRow headersRow = sheet.getRow(0);
        assertThat(headersRow.getPhysicalNumberOfCells(), is(HEADERS.size()));
        int i = 0;
        for (String header : HEADERS) {
            assertThat(headersRow.getCell(i).getStringCellValue(), is(header));
            i++;
        }

        //Check Data
        final HSSFRow dataRow = sheet.getRow(1);
        assertThat(dataRow.getPhysicalNumberOfCells(), is(EXPECTED_DATA.size()));
        i = 0;
        for (String data : EXPECTED_DATA) {
            assertThat(dataRow.getCell(i).getStringCellValue(), is(data));
            i++;
        }
    }
}