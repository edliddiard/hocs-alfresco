package uk.gov.homeoffice.cts.model;


public class GroupAction {

    private String unitName;
    private String unitDisplayName;
    private String teamName;
    private String teamDisplayName;

    public GroupAction(String unitName, String teamName,String unitDisplayName, String teamDisplayName) {
        this.unitName = unitName;
        this.teamName = teamName;
        this.unitDisplayName = unitDisplayName;
        this.teamDisplayName = teamDisplayName;
    }

    public String getTeamRefName() {
        return teamName;
    }

    public String getUnitRefName() {
        return unitName;
    }

    public String getUnitDisplayName() {
        return unitDisplayName;
    }

    public String getTeamDisplayName() {
        return teamDisplayName;
    }
}
