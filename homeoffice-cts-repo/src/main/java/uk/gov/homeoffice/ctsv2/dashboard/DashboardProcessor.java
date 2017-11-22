package uk.gov.homeoffice.ctsv2.dashboard;

import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.homeoffice.cts.model.CorrespondenceType;
import uk.gov.homeoffice.cts.model.TaskStatus;
import uk.gov.homeoffice.ctsv2.model.SummaryByStatus;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DashboardProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(DashboardProcessor.class);
    private static SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'Z'");

    private SearchService searchService;

    private DashboardDataHolder dashboardDataHolder;

    private String dataSource;

    public Map<String, Map<String, SummaryByStatus>> getSummary(){
        Map<String, Map<String, SummaryByStatus>> map = dashboardDataHolder.getSummary();
        if (map.isEmpty()) {
            createDataHolder();
            return dashboardDataHolder.getSummary();
        } else {
            return map;
        }
    }

    public void refreshDashBoardData() {
        dashboardDataHolder.clearData();
        createDataHolder();
    }

    private void createDataHolder() {
        LOGGER.info("Retrieving dashboard data from DB at: "+ new Date());
        for (String cType : CorrespondenceType.getAllCaseTypes()) {
            dashboardDataHolder.setSummaryForType(cType, getSummaryByUnit(cType));
        }
    }

    private Integer getCount(SearchParameters sp) {
        return searchService.query(sp).length();
    }

    protected Map<String, SummaryByStatus> getSummaryByUnit(String correspondenceType) {
        Map<String, SummaryByStatus> statusMap = new HashMap<>();
        String now = DATE_FORMATTER.format(new DateTime().toDate());

        final SearchParameters sp = new SearchParameters();
        sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
        String lang = dataSource.equalsIgnoreCase("solr") ? SearchService.LANGUAGE_SOLR_CMIS : SearchService.LANGUAGE_CMIS_ALFRESCO;
        sp.setLanguage(lang);

        for (TaskStatus caseStatus : TaskStatus.values()) {
            String status = caseStatus.getStatus();
            status = status.replace("'", "\\'");
            //open and overdue
            String queryOpenAndOverdue = "SELECT c.cmis:objectId FROM cts:case as c WHERE c.cts:correspondenceType = '" + correspondenceType + "' AND c.cts:caseTask = '" + status + "' AND c.cts:caseResponseDeadline < TIMESTAMP '" + now + "'";
            sp.setQuery(queryOpenAndOverdue);
            Integer countOpenAndOverdue = getCount(sp);

            //rejected
            String queryReturned = "SELECT c.cmis:objectId FROM cts:case as c WHERE c.cts:correspondenceType = '" + correspondenceType + "' AND c.cts:caseTask = '" + status + "' AND c.cts:returnedCount > 0";
            sp.setQuery(queryReturned);
            Integer countReturned = getCount(sp);

            //open
            String query = "SELECT c.cmis:objectId FROM cts:case as c WHERE  c.cts:correspondenceType = '" + correspondenceType + "' AND c.cts:caseTask = '" + status + "'";
            sp.setQuery(query);
            Integer countOpenCases = getCount(sp);
            if(countOpenAndOverdue==0 && countReturned ==0 && countOpenCases==0){
                continue;
            }

            statusMap.put(status, new SummaryByStatus(countOpenCases, countOpenAndOverdue, countReturned));
        }
        return statusMap;
    }

    public void setSearchService(SearchService searchService) {
        this.searchService = searchService;
    }

    public void setDashboardDataHolder(DashboardDataHolder dashboardDataHolder) {
        this.dashboardDataHolder = dashboardDataHolder;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }
}


