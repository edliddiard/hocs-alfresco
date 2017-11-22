package uk.gov.homeoffice.ctsv2.dashboard;

import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.homeoffice.cts.model.CorrespondenceType;
import uk.gov.homeoffice.cts.model.TaskStatus;
import uk.gov.homeoffice.ctsv2.model.SummaryByStatus;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DashboardProcessorTest {

    private static SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'Z'");

    @Mock
    private SearchService searchService;

    @Spy
    private DashboardDataHolder dashboardDataHolder;

    @InjectMocks
    private DashboardProcessor dashboardProcessor;

    @Test
    public void testGetSummary() throws Exception {

        Map<String, Map<String, SummaryByStatus>> expectedResults = mockDBInteractions();

        Map<String, Map<String, SummaryByStatus>> resultMap = dashboardProcessor.getSummary();

        verifyDashBoardData(expectedResults, resultMap);
    }

    @Test
    public void testRefreshDashBoardData() {
        mockDBInteractions();
        dashboardProcessor.refreshDashBoardData();
        verify(dashboardDataHolder).clearData();
        verify(dashboardDataHolder, times(CorrespondenceType.getAllCaseTypes().size())).setSummaryForType(anyString(), anyMapOf(String.class, SummaryByStatus.class));
    }

    private Map<String, Map<String, SummaryByStatus>> mockDBInteractions() {
        dashboardProcessor.setDataSource("alfresco");
        Random random = new Random();
        DateTimeUtils.setCurrentMillisFixed(random.nextLong());

        Map<String, Map<String, SummaryByStatus>> expectedResults = new HashMap<>();

        for (String cType : CorrespondenceType.getAllCaseTypes()) {
            Map<String, SummaryByStatus> expectedSummaryResults = new HashMap<>();
            for (TaskStatus taskStatus : TaskStatus.values()) {
                int numOpen = random.nextInt();
                ResultSet resultSetOpen = mock(ResultSet.class);
                when(resultSetOpen.length()).thenReturn(numOpen);
                final SearchParameters spOpen = new SearchParameters();
                spOpen.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
                spOpen.setLanguage(SearchService.LANGUAGE_CMIS_ALFRESCO);
                String status = taskStatus.getStatus();
                status = status.replace("'", "\\'");
                spOpen.setQuery("SELECT c.cmis:objectId FROM cts:case as c WHERE  c.cts:correspondenceType = '" + cType + "' AND c.cts:caseTask = '" + status + "'");
                when(searchService.query(spOpen)).thenReturn(resultSetOpen);

                int numOpenAndOverDue = random.nextInt();
                ResultSet resultSetOpenAndOverdue = mock(ResultSet.class);
                when(resultSetOpenAndOverdue.length()).thenReturn(numOpenAndOverDue);
                String now = DATE_FORMATTER.format(new DateTime().toDate());
                final SearchParameters spOpenAndOverdue = new SearchParameters();
                spOpenAndOverdue.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
                spOpenAndOverdue.setLanguage(SearchService.LANGUAGE_CMIS_ALFRESCO);
                spOpenAndOverdue.setQuery("SELECT c.cmis:objectId FROM cts:case as c WHERE c.cts:correspondenceType = '" + cType + "' AND c.cts:caseTask = '" + status + "' AND c.cts:caseResponseDeadline < TIMESTAMP '" + now + "'");
                when(searchService.query(spOpenAndOverdue)).thenReturn(resultSetOpenAndOverdue);

                int numReturned = random.nextInt();
                ResultSet resultSetReturned = mock(ResultSet.class);
                when(resultSetReturned.length()).thenReturn(numReturned);
                final SearchParameters spReturned = new SearchParameters();
                spReturned.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
                spReturned.setLanguage(SearchService.LANGUAGE_CMIS_ALFRESCO);
                spReturned.setQuery("SELECT c.cmis:objectId FROM cts:case as c WHERE c.cts:correspondenceType = '" + cType + "' AND c.cts:caseTask = '" + status + "' AND c.cts:returnedCount > 0");
                when(searchService.query(spReturned)).thenReturn(resultSetReturned);

                SummaryByStatus statusSummary = new SummaryByStatus(numOpen, numOpenAndOverDue, numReturned);
                expectedSummaryResults.put(status, statusSummary);
            }
            expectedResults.put(cType, expectedSummaryResults);
        }
        return expectedResults;
    }

    private void verifyDashBoardData(Map<String, Map<String, SummaryByStatus>> expectedResults, Map<String, Map<String, SummaryByStatus>> resultMap) {
        for (Map.Entry<String, Map<String, SummaryByStatus>> entry : expectedResults.entrySet()) {
            for (Map.Entry<String, SummaryByStatus> sumEntry : entry.getValue().entrySet()) {
                SummaryByStatus statusSummary = sumEntry.getValue();
                assertThat(statusSummary.getOpen(), is(resultMap.get(entry.getKey()).get(sumEntry.getKey()).getOpen()));
                assertThat(statusSummary.getOpenAndOverdue(), is(resultMap.get(entry.getKey()).get(sumEntry.getKey()).getOpenAndOverdue()));
                assertThat(statusSummary.getReturned(), is(resultMap.get(entry.getKey()).get(sumEntry.getKey()).getReturned()));
            }
        }
    }

}
