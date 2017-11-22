package uk.gov.homeoffice.cts.export;

import org.alfresco.repo.web.scripts.content.ContentStreamer;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.QueryConsistency;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.util.TempFileProvider;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import uk.gov.homeoffice.cts.exceptions.SpreadsheetGeneratorException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jackm on 20/01/2015.
 */
public class GetExportSearchResultsWebScript extends AbstractWebScript {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetExportSearchResultsWebScript.class);
    private SearchService searchService;
    private AuthenticationService authenticationService;
    private SpreadsheetGenerator spreadsheetGenerator;
    private ContentStreamer contentStreamer;
    private int maxSearchResults;
    private List<String> exportQNamesAll;
    private List<String> exportQNamesMin;
    private List<String> exportQNamesTro;
    private List<String> exportQNamesPq;
    private List<String> exportQNamesFoi;
    private List<String> exportQNamesUkvi;
    private List<String> exportQNamesHmpo;
    private List<String> exportQNamesNo10;

    @Override
    public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {
        LOGGER.debug("Running ExportSearchResultsWebScript");
        String cmisQuery = req.getParameter("q");
        ResultSet resultSet = runCmisSearchQuery(cmisQuery);

        // map of export qnames, the key is used as a column prefix
        Map<String, List<String>> exportQNames = new HashMap<>();
        exportQNames.put("", exportQNamesAll);
        exportQNames.put("MIN ", exportQNamesMin);
        exportQNames.put("TRO ", exportQNamesTro);
        exportQNames.put("PQ ", exportQNamesPq);
        exportQNames.put("FOI ", exportQNamesFoi);
        exportQNames.put("UKVI ", exportQNamesUkvi);
        exportQNames.put("HMPO ", exportQNamesHmpo);
        exportQNames.put("NO10 ", exportQNamesNo10);

        spreadsheetGenerator.setExportQNames(exportQNames);
        spreadsheetGenerator.setResultSet(resultSet);
        try {
            spreadsheetGenerator.generate();
        } catch (SpreadsheetGeneratorException e) {
            LOGGER.error("Unable to generate spreadsheet: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }

        String fileName = generateFileName();
        File output = streamWorkbookToFile(spreadsheetGenerator.getWorkbook(), fileName);
        res.setHeader("Content-Disposition", "attachment; filename="+fileName);
        Map<String, Object> model = new HashMap<>();
        contentStreamer.streamContent(req, res, output, null, true, "", model);
    }

    /**
     * Create a filename including the user and timestamp.
     * @return String
     */
    private String generateFileName() {
        Date now = new Date();
        DateFormat df = new SimpleDateFormat("ddMMyyyyHHmmss");
        return authenticationService.getCurrentUserName() + "_" + df.format(now);
    }

    /**
     * Use the search service to run the query, return the result set.
     * @param cmisQuery String
     * @return ResultSet
     */
    private ResultSet runCmisSearchQuery(String cmisQuery) {
        SearchParameters searchParameters = new SearchParameters();
        searchParameters.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
        searchParameters.setLanguage(SearchService.LANGUAGE_CMIS_ALFRESCO);
        searchParameters.setMaxItems(maxSearchResults);
        searchParameters.setQuery(cmisQuery);
        //this makes it use the database
        searchParameters.setQueryConsistency(QueryConsistency.TRANSACTIONAL_IF_POSSIBLE);
        ResultSet resultSet = getSearchService().query(searchParameters);
        return resultSet;
    }

    /**
     * Stream workbook to file using specified name.
     * @param workbook HSSFWorkbook
     * @param fileName String
     */
    private File streamWorkbookToFile(HSSFWorkbook workbook, String fileName) {
        File file = TempFileProvider.createTempFile(fileName, "xls");
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

    public SearchService getSearchService() {
        return searchService;
    }

    public void setSearchService(SearchService searchService) {
        this.searchService = searchService;
    }

    public AuthenticationService getAuthenticationService() {
        return authenticationService;
    }

    public void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    public SpreadsheetGenerator getSpreadsheetGenerator() {
        return spreadsheetGenerator;
    }

    public void setSpreadsheetGenerator(SpreadsheetGenerator spreadsheetGenerator) {
        this.spreadsheetGenerator = spreadsheetGenerator;
    }

    public int getMaxSearchResults() {
        return maxSearchResults;
    }

    public void setMaxSearchResults(int maxSearchResults) {
        this.maxSearchResults = maxSearchResults;
    }

    public ContentStreamer getContentStreamer() {
        return contentStreamer;
    }

    public void setContentStreamer(ContentStreamer contentStreamer) {
        this.contentStreamer = contentStreamer;
    }

    public List getExportQNamesAll() {
        return exportQNamesAll;
    }

    public void setExportQNamesAll(List exportQNamesAll) {
        this.exportQNamesAll = exportQNamesAll;
    }

    public List<String> getExportQNamesMin() {
        return exportQNamesMin;
    }

    public void setExportQNamesMin(List<String> exportQNamesMin) {
        this.exportQNamesMin = exportQNamesMin;
    }

    public List<String> getExportQNamesTro() {
        return exportQNamesTro;
    }

    public void setExportQNamesTro(List<String> exportQNamesTro) {
        this.exportQNamesTro = exportQNamesTro;
    }

    public List<String> getExportQNamesPq() {
        return exportQNamesPq;
    }

    public void setExportQNamesPq(List<String> exportQNamesPq) {
        this.exportQNamesPq = exportQNamesPq;
    }

    public List<String> getExportQNamesFoi() {
        return exportQNamesFoi;
    }

    public void setExportQNamesFoi(List<String> exportQNamesFoi) {
        this.exportQNamesFoi = exportQNamesFoi;
    }

    public List<String> getExportQNamesUkvi() {
        return exportQNamesUkvi;
    }

    public void setExportQNamesUkvi(List<String> exportQNamesUkvi) {
        this.exportQNamesUkvi = exportQNamesUkvi;
    }

    public List<String> getExportQNamesHmpo() {
        return exportQNamesHmpo;
    }

    public void setExportQNamesHmpo(List<String> exportQNamesHmpo) {
        this.exportQNamesHmpo = exportQNamesHmpo;
    }

    public List<String> getExportQNamesNo10() {
        return exportQNamesNo10;
    }

    public void setExportQNamesNo10(List<String> exportQNamesNo10) {
        this.exportQNamesNo10 = exportQNamesNo10;
    }
}
