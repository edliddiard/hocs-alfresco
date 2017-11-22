package uk.gov.homeoffice.cts.export;

import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.namespace.InvalidQNameException;
import org.alfresco.service.namespace.QName;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.homeoffice.cts.exceptions.SpreadsheetGeneratorException;
import uk.gov.homeoffice.cts.model.CorrespondenceType;
import uk.gov.homeoffice.cts.model.CtsModel;
import uk.gov.homeoffice.cts.model.TaskStatus;
import uk.gov.homeoffice.cts.service.SystemMinutesService;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

/**
 * Created by davidt on 29/01/2015.
 * To generate a workbook, you must first set the exportQNames and resultSet, then call generate.
 * The generated workbook will be available by calling getWorkbook().
 */
public class SpreadsheetGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpreadsheetGenerator.class);
    public static final String DATE = " date";
    public static final String EMPTY_STRING = "";
    private NodeService nodeService;
    private DictionaryService dictionaryService;
    private AuthorityService authorityService;

    private Map<String, List<String>> exportQNames;
    private ResultSet resultSet;
    private CellStyle headerStyle;
    private CellStyle bodyStyle;
    private HSSFWorkbook workbook;
    private SystemMinutesService systemMinutesService;

    private final List<QName> decodeGroupQNameList = Arrays.asList(CtsModel.PROP_ASSIGNED_UNIT, CtsModel.PROP_ASSIGNED_TEAM, CtsModel.PROP_MARKUP_UNIT, CtsModel.PROP_MARKUP_MINISTER);

    /**
     * Generate the workbook with the export QNames and result set.
     */
    public void generate() throws SpreadsheetGeneratorException {
        if (exportQNames == null || resultSet == null) {
            throw new SpreadsheetGeneratorException("exportQNames and resultSet must be specified in order to generate a workbook.");
        }
        LOGGER.debug("Starting spreadsheet generation.");
        workbook = new HSSFWorkbook();
        createStyles(workbook);
        HSSFSheet sheet = workbook.createSheet("Results");
        generateHeaderRow(sheet);
        generateDataRows(sheet, resultSet);
        formatSheet(sheet);
        LOGGER.debug("Completed spreadsheet generation.");
    }

    /**
     * Create styles for header and body text.
     * @param workbook HSSFWorkbook
     */
    private void createStyles(HSSFWorkbook workbook) {
        // Create a new font and alter it.
        Font fontNormal = workbook.createFont();
        fontNormal.setFontHeightInPoints((short) 14);
        fontNormal.setFontName("Calibri");

        Font fontBold = workbook.createFont();
        fontBold.setFontHeightInPoints((short) 14);
        fontBold.setFontName("Calibri");
        fontBold.setBoldweight(Font.BOLDWEIGHT_BOLD);

        // Fonts are set into a style so create a new one to use.
        this.bodyStyle = workbook.createCellStyle();
        bodyStyle.setFont(fontNormal);

        this.headerStyle = workbook.createCellStyle();
        headerStyle.setFont(fontBold);
    }


    /**
     * Generate the header row for the sheet, based on the required properties.
     * @param sheet HSSFSheet
     */
    private void generateHeaderRow(HSSFSheet sheet) {
        HSSFRow row = sheet.createRow((short) 0);
        int index = 0;
        for (Entry<String, List<String>> qNameSet : exportQNames.entrySet()) {
            index = generateHeaderRowCells(row, index, qNameSet.getValue(), qNameSet.getKey());
        }
    }

    /**
     * Add heading data to the specified row using the qnames specified.
     * @param row HSSFRow
     * @param cellIndex int
     * @param qnames List<String>
     * @param columnNamePrefix String
     * @return int
     */
    private int generateHeaderRowCells(HSSFRow row, int cellIndex, List<String> qnames, String columnNamePrefix) {
        for (String qname : qnames) {
            HSSFCell cell = row.createCell(cellIndex);
            PropertyDefinition propertyDefinition = getDictionaryService().getProperty(QName.createQName(qname));
            if (propertyDefinition == null) {
                cell.setCellValue(qname + DATE);
            } else {
                cell.setCellValue(columnNamePrefix + propertyDefinition.getTitle());
            }
            cell.setCellStyle(this.headerStyle);
            cellIndex++;
        }
        return cellIndex;
    }

    /**
     * Create a row for each result in the set and add them to the sheet given.
     * @param sheet HSSFSheet
     * @param resultSet ResultSet
     */
    private void generateDataRows(HSSFSheet sheet, ResultSet resultSet) {
        int i = 1;
        for (NodeRef result : resultSet.getNodeRefs()) {
            HSSFRow row = sheet.createRow((short) i);
            Map<QName, Serializable> props = nodeService.getProperties(result);
            generateDataRow(row, props, getSystemMinutesService().getSignedOffDates(result));
            i++;
        }
    }

    /**
     * Given a row and a map of properties, add the specified qnames to the row.
     * @param row HSSFRow
     * @param props Map<QName,Serializable>
     * @param signedOffDates
     */
    private void generateDataRow(HSSFRow row, Map<QName, Serializable> props, Map<String, Date> signedOffDates ) {
        int index = 0;
        for (Entry<String, List<String>> qNameSet : exportQNames.entrySet()) {
            index = generateDataRowCells(row, index, qNameSet.getValue(), props, CorrespondenceType.getAllCaseTypes(),signedOffDates);
        }
    }
    private int generateDataRowCellsForSignedOffFields(HSSFRow row, int cellIndex,Date date) {
        if(date == null) {
            generateCellValue(row, cellIndex, EMPTY_STRING);
        }else {
            generateCellValue(row, cellIndex, date);
        }
        return ++cellIndex;
    }

    /**
     * Add cell data to the specified row using the qnames and props passed in.
     * Only display the property value if the case type is contained in the list specified.
     * @param row HSSFRow
     * @param cellIndex int
     * @param qnames List<String>
     * @param props Map<QName,Serializable>
     * @param caseTypes List<String>
     * @return int
     */
    private int generateDataRowCells(HSSFRow row, int cellIndex, List<String> qnames, Map<QName,Serializable> props, List<String> caseTypes,Map<String, Date> signedOffDates) {
        for (String qname : qnames) {
            QName exportQName = QName.createQName(qname);
            PropertyDefinition propertyDefinition = getDictionaryService().getProperty(exportQName);
            if (propertyDefinition == null) {
                generateDataRowCellsForSignedOffFields(row, cellIndex, signedOffDates.get(qname));
            } else {
                String caseType = (String) props.get(CtsModel.PROP_CORRESPONDENCE_TYPE);
                Serializable propValue = props.get(exportQName);
                if (propValue != null && caseTypes.contains(caseType)) {
                    if (decodeGroupQNameList.contains(exportQName)) {
                        propValue = authorityService.getAuthorityDisplayName((String) propValue);
                    }
                    generateCellValue(row, cellIndex, propValue);
                }
            }
            cellIndex++;
        }
        return cellIndex;
    }

    /**
     * Given a row, index and value generate the cell contents.
     * @param row HSSFRow
     * @param index int
     * @param propValue Serializable
     */
    private void generateCellValue(HSSFRow row, int index, Serializable propValue) {
        HSSFCell cell = row.createCell(index);
        if (propValue.getClass() == Date.class) {
            cell.setCellValue(generateDateValue((Date) propValue));
        } else if (propValue.getClass() == Boolean.class) {
            cell.setCellValue(generateBooleanValue((Boolean) propValue));
        } else if (propValue.getClass() == String.class) {
            cell.setCellValue((String) propValue);
        }
        cell.setCellStyle(this.bodyStyle);
    }

    /**
     * Return a formatted string of the boolean passed in.
     * @param booleanValue Boolean
     * @return String
     */
    private String generateBooleanValue(Boolean booleanValue) {
        if (booleanValue) {
            return "Yes";
        }
        return "No";
    }

    /**
     * Return a formatted string of the date passed in.
     * @param dateValue Date
     * @return String
     */
    private String generateDateValue(Date dateValue) {
        DateFormat df = new SimpleDateFormat("HHmmss");
        if (df.format(dateValue).equals("000000")) {
            return new SimpleDateFormat("dd/MM/yyyy").format(dateValue);
        } else {
            return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(dateValue);
        }
    }

    /**
     * Adding formatting to the sheet.
     * @param sheet HSSFSheet
     */
    private void formatSheet(HSSFSheet sheet) {
        int index = 0;
        for (Entry<String, List<String>> qNameSet : exportQNames.entrySet()) {
            for (String qName : qNameSet.getValue()) {
                sheet.autoSizeColumn(index);
                index++;
            }
        }
    }

    public Map<String, List<String>> getExportQNames() {
        return exportQNames;
    }

    public void setExportQNames(Map<String, List<String>> exportQNames) {
        this.exportQNames = exportQNames;
    }

    public ResultSet getResultSet() {
        return resultSet;
    }

    public void setResultSet(ResultSet resultSet) {
        this.resultSet = resultSet;
    }

    public CellStyle getHeaderStyle() {
        return headerStyle;
    }

    public void setHeaderStyle(CellStyle headerStyle) {
        this.headerStyle = headerStyle;
    }

    public CellStyle getBodyStyle() {
        return bodyStyle;
    }

    public void setBodyStyle(CellStyle bodyStyle) {
        this.bodyStyle = bodyStyle;
    }

    public HSSFWorkbook getWorkbook() {
        return workbook;
    }

    public void setWorkbook(HSSFWorkbook workbook) {
        this.workbook = workbook;
    }

    public NodeService getNodeService() {
        return nodeService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public DictionaryService getDictionaryService() {
        return dictionaryService;
    }

    public void setDictionaryService(DictionaryService dictionaryService) {
        this.dictionaryService = dictionaryService;
    }

    public AuthorityService getAuthorityService() {
        return authorityService;
    }

    public void setAuthorityService(AuthorityService authorityService) {
        this.authorityService = authorityService;
    }

    public SystemMinutesService getSystemMinutesService() {
        return systemMinutesService;
    }

    public void setSystemMinutesService(SystemMinutesService systemMinutesService) {
        this.systemMinutesService = systemMinutesService;
    }
}
