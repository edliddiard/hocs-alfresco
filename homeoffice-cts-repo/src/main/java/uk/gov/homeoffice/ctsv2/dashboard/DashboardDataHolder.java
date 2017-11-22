package uk.gov.homeoffice.ctsv2.dashboard;

import uk.gov.homeoffice.ctsv2.model.SummaryByStatus;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DashboardDataHolder {
    private Map<String, Map<String, SummaryByStatus>> inMemoryDataHolder = new HashMap<>();

    public void setSummaryForType(String cType, Map<String, SummaryByStatus> summary) {
        inMemoryDataHolder.put(cType, summary);
    }

    public Map<String, Map<String, SummaryByStatus>> getSummary() {
        return Collections.unmodifiableMap(inMemoryDataHolder);
    }

    public void clearData() {
        inMemoryDataHolder.clear();
    }
}
