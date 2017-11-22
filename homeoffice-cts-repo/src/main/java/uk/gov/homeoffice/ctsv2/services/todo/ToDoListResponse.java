package uk.gov.homeoffice.ctsv2.services.todo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class ToDoListResponse implements Serializable {
    private int totalResults;

    private List<Map<String, Object>> caseList = new ArrayList<>();

    public int getTotalResults() {
        return totalResults;
    }

    public List<Map<String, Object>> getCaseList() {
        return caseList;
    }

    public void addCase(Map<String, Object> ctsCase){
        caseList.add(ctsCase);
    }

    public void totalResults(int totalResults) {
        this.totalResults = totalResults;
    }
}
