package uk.gov.homeoffice.cts.model;

public class CtsGroup {

    private String name;
    private String displayName;
    private String authorityName;
    private Boolean isTeam;
    private Boolean isUnit;

    public String getJson() {
        return "      \"name\": \""+name+"\"," +
                "      \"displayName\": \""+displayName+"\",\n" +
                "      \"authorityName\": \""+authorityName+"\",\n" +
                "      \"isTeam\": "+isTeam+",\n" +
                "      \"isUnit\": "+isUnit+"";
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setAuthorityName(String authorityName) {
        this.authorityName = authorityName;
    }

    public void setTeam(Boolean team) {
        isTeam = team;
    }

    public void setUnit(Boolean unit) {
        isUnit = unit;
    }

    public String getAuthorityName() {
        return authorityName;
    }
}
