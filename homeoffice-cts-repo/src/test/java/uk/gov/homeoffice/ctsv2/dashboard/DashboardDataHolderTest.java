package uk.gov.homeoffice.ctsv2.dashboard;

import org.junit.Test;
import uk.gov.homeoffice.ctsv2.model.SummaryByStatus;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class DashboardDataHolderTest {
    private DashboardDataHolder dashboardDataHolder = new DashboardDataHolder();

    @Test
    public void testGetSummaryWhenEmpty() throws Exception {
        Map<String, Map<String, SummaryByStatus>> result = dashboardDataHolder.getSummary();

        assertThat(result.size(), is(0));
    }

    @Test
    public void testGetSummaryWhenNotEmpty() throws Exception {
        Map<String, SummaryByStatus> map1 = new HashMap<>();
        map1.put("New", new SummaryByStatus(1, 2, 3));
        Map<String, SummaryByStatus> map2 = new HashMap<>();
        map1.put("QA", new SummaryByStatus(4, 5, 6));
        dashboardDataHolder.setSummaryForType("MIN", map1);
        dashboardDataHolder.setSummaryForType("FOI", map2);

        Map<String, Map<String, SummaryByStatus>> result = dashboardDataHolder.getSummary();

        assertThat(result.size(), is(2));
        assertThat(result.get("MIN"), is(map1));
        assertThat(result.get("FOI"), is(map2));
    }

    @Test
    public void testClearData() throws Exception {
        Map<String, SummaryByStatus> map = new HashMap<>();
        map.put("New", new SummaryByStatus(1, 2, 3));
        dashboardDataHolder.setSummaryForType("MIN", map);

        assertThat(dashboardDataHolder.getSummary().size(), is(1));

        dashboardDataHolder.clearData();

        assertThat(dashboardDataHolder.getSummary().size(), is(0));
    }
}
