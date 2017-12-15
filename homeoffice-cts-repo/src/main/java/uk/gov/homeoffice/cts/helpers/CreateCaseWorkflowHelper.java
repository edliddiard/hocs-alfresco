package uk.gov.homeoffice.cts.helpers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class CreateCaseWorkflowHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateCaseWorkflowHelper.class);

    protected class UnitAndTeamToAssign {
        String Unit;
        String Team;

        UnitAndTeamToAssign(String unit, String team) {
            Unit = unit;
            Team = team;
        }

        public String getUnit() {
            return Unit;
        }
        public String getTeam() {
            return Team;
        }
    }

    UnitAndTeamToAssign getTeamAndUnitForCaseType(String caseType) {

        LOGGER.debug("Getting team and unit for :: " + caseType);

        switch(caseType) {
            case "IMCM":case "IMCB":
                return new UnitAndTeamToAssign("GROUP_UKVI", "GROUP_UKVI_CUSTOMER_CORRESPONDENCE_HUB");
            case "UTEN":case "DTEN":
                return new UnitAndTeamToAssign("GROUP_DCU", "GROUP_DCU_TRANSFERS_NO10_TEAM");
            case "TRO":case "COM":case "MIN":
                return new UnitAndTeamToAssign("GROUP_DCU", "GROUP_DCU_PERFORMANCE_AND_PROCESS_TEAM");
            case "COL":
                return new UnitAndTeamToAssign("GROUP_HMPO_COLLECTIVES", "GROUP_HMPO_COLLECTIVES_COLLECTIVES_CREATORS");
            case "FOI": case "FTC": case "FTCI": case "FSC": case "FSCI": case "FLT": case "FUT":
                return new UnitAndTeamToAssign("GROUP_FOI", "GROUP_FOI_INFORMATION_RIGHTS_TEAM");
            case "COM1": case "COM2": case "DGEN":
                return new UnitAndTeamToAssign("GROUP_HMPO_CORRESPONDENCE_AND_COMPLAINTS", "GROUP_HMPO_CORRESPONDENCE_AND_COMPLAINTS_COMPLAINT_DRAFTERS");
            case "GNR":
                return new UnitAndTeamToAssign("GROUP_HMPO_CORRESPONDENCE_AND_COMPLAINTS", "GROUP_HMPO_CORRESPONDENCE_AND_COMPLAINTS_CORRESPONDENCE_DRAFTERS");
            default:
                return new UnitAndTeamToAssign(null, null);
        }
    }

}