package uk.gov.homeoffice.ctsv2.services.todo;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.homeoffice.cts.exceptions.SpreadsheetGeneratorException;
import uk.gov.homeoffice.ctsv2.model.CtsCase;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class ToDoListSpreadsheetGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(ToDoListSpreadsheetGenerator.class);

    private static final List<String> HEADERS = Arrays.asList("Case Ref", "Case Id", "UIN", "URN Suffix",
            "Date Received", "Date Owner Updated", "Correspondence Type", "Response Deadline", "Advice",
            "Priority", "Member", "Reply To", "Correspondent Forename", "Correspondent Surname",
            "Assigned Unit", "Assigned Team", "Assigned User", "Markup Unit", "Markup Topic",
            "Secondary Topic", "Case Status", "Date Status Updated", "Case Task", "Date Task Updated",
            "Is Grouped Master", "Is Grouped Slave", "Is Linked Case", "FOI is EIR", "HMPO Stage",
            "Master Node Ref", "Applicant Forename", "Applicant Surname", "Canonical Correspondent", "Returned Count");

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    public HSSFWorkbook generateWorkbook(ToDoListResponse toDoListResponse) throws SpreadsheetGeneratorException {
        LOGGER.debug("Starting spreadsheet generation.");
        final HSSFWorkbook workbook = new HSSFWorkbook();

        // Create a new font and alter it.
        final Font fontNormal = workbook.createFont();
        fontNormal.setFontHeightInPoints((short) 14);
        fontNormal.setFontName("Calibri");

        final Font fontBold = workbook.createFont();
        fontBold.setFontHeightInPoints((short) 14);
        fontBold.setFontName("Calibri");
        fontBold.setBoldweight(Font.BOLDWEIGHT_BOLD);

        // Fonts are set into a style so create a new one to use.
        final HSSFCellStyle bodyStyle = workbook.createCellStyle();
        bodyStyle.setFont(fontNormal);

        final HSSFCellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFont(fontBold);

        final HSSFSheet sheet = workbook.createSheet("Results");
        generateHeaderRow(sheet, headerStyle);
        generateDataRows(sheet, toDoListResponse, bodyStyle);
        formatSheet(sheet);
        LOGGER.debug("Completed spreadsheet generation.");
        return workbook;
    }

    private void generateHeaderRow(HSSFSheet sheet, HSSFCellStyle style) {
        final HSSFRow row = sheet.createRow((short) 0);
        int index = 0;
        for (String header : HEADERS) {
            index = generateHeaderRowCells(row, index, header, style);
        }
    }

    private int generateHeaderRowCells(HSSFRow row, int cellIndex, String name, HSSFCellStyle style) {
        final HSSFCell cell = row.createCell(cellIndex);
        cell.setCellValue(name);
        cell.setCellStyle(style);
        cellIndex++;
        return cellIndex;
    }

    private void generateDataRows(HSSFSheet sheet, ToDoListResponse toDoListResponse, HSSFCellStyle style) {
        int i = 1;
        for (Map<String, Object> caseMap : toDoListResponse.getCaseList()) {
            final CtsCase ctsCase = (CtsCase) caseMap.get("case");
            final HSSFRow row = sheet.createRow((short) i);
            generateDataRow(row, ctsCase, style);
            i++;
        }
    }

    private void generateDataRow(HSSFRow row, CtsCase ctsCase, HSSFCellStyle style) {
        final List<Serializable> data = new ArrayList<>();
        data.add(ctsCase.getCaseRef());
        data.add(ctsCase.getId());
        data.add(ctsCase.getUin());
        data.add(ctsCase.getUrnSuffix());
        data.add(ctsCase.getDateReceived());
        data.add(ctsCase.getOwnerUpdatedDatetime());
        data.add(ctsCase.getCorrespondenceType());
        data.add(ctsCase.getCaseResponseDeadline());
        data.add(ctsCase.getAdvice());
        data.add(ctsCase.getPriority());
        data.add(ctsCase.getMember());
        data.add(ctsCase.getReplyToName());
        data.add(ctsCase.getCorrespondentForename());
        data.add(ctsCase.getCorrespondentSurname());
        data.add(ctsCase.getAssignedUnit());
        data.add(ctsCase.getAssignedTeam());
        data.add(ctsCase.getAssignedUser());
        data.add(ctsCase.getMarkupUnit());
        data.add(ctsCase.getMarkupTopic());
        data.add(ctsCase.getSecondaryTopic());
        data.add(ctsCase.getDisplayStatus());
        data.add(ctsCase.getStatusUpdatedDatetime());
        data.add(ctsCase.getDisplayTask());
        data.add(ctsCase.getTaskUpdatedDatetime());
        data.add(ctsCase.getIsGroupedMaster());
        data.add(ctsCase.getIsGroupedSlave());
        data.add(ctsCase.getIsLinkedCase());
        data.add(ctsCase.getFoiIsEir());
        data.add(ctsCase.getHmpoStage());
        data.add(ctsCase.getMasterNodeRef());
        data.add(ctsCase.getApplicantForename());
        data.add(ctsCase.getApplicantSurname());
        data.add(ctsCase.getCanonicalCorrespondent());
        data.add(ctsCase.getReturnedCount());

        for (int i = 0;i < data.size();i++) {
            generateCellValue(row, i, data.get(i), style);
        }
    }

    private void generateCellValue(HSSFRow row, int index, Serializable propValue, HSSFCellStyle style) {
        final HSSFCell cell = row.createCell(index);
        if (propValue == null) {
            cell.setCellValue("");
        } else if (propValue.getClass() == Date.class) {
            cell.setCellValue(generateDateValue((Date) propValue));
        } else if (propValue.getClass() == Boolean.class) {
            cell.setCellValue(generateBooleanValue((Boolean) propValue));
        } else if (propValue.getClass() == String.class) {
            cell.setCellValue((String) propValue);
        } else if (propValue.getClass() == Integer.class) {
            cell.setCellValue(propValue.toString());
        }
        cell.setCellStyle(style);
    }

    private String generateBooleanValue(Boolean booleanValue) {
        if (booleanValue) {
            return "Yes";
        }
        return "No";
    }

    private String generateDateValue(Date dateValue) {
        return DATE_FORMAT.format(dateValue);
    }

    private void formatSheet(HSSFSheet sheet) {
        for (int i = 0; i < HEADERS.size(); i++) {
            sheet.autoSizeColumn(i);
        }
    }
}
