package uk.gov.homeoffice.ctsv2.model;

import java.util.HashMap;
import java.util.Map;

public class DashboardSummary {
    //should be driven by search filter dates
    //should contains List<Unit, SummaryEntity>

    Map<String,SummaryEntity> dashboardSummaryList= new HashMap<>();

}

class SummaryEntity {
    private String unit;

    private Integer open;
    private Integer openAndOverDue;
    private Integer closedAndOverDue;
    private Integer rejected;

    //TODO should generate dynamically
    private Map<String, Integer> infoAtUnitLevel;


}