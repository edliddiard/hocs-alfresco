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
                return new UnitAndTeamToAssign("GROUP_UKVI MP", "GROUP_CPR");
            case "UTEN":case "DTEN":
                return new UnitAndTeamToAssign("GROUP_DCU", "GROUP_TNT");
            case "TRO":case "COM":case "MIN":
                return new UnitAndTeamToAssign("GROUP_DCU", "GROUP_PPT");
            case "COL":
                return new UnitAndTeamToAssign("GROUP_HMPO Collective Passports", "GROUP_Collective Creators");
            case "FOI": case "FTC": case "FTCI": case "FSC": case "FSCI": case "FLT": case "FUT":
                return new UnitAndTeamToAssign("GROUP_FOI", "GROUP_IAT");
            case "COM1": case "COM2": case "DGEN":
                return new UnitAndTeamToAssign("GROUP_HMPO PCU", "GROUP_PCU Drafters");
            case "GNR":
                return new UnitAndTeamToAssign("GROUP_HMPO CCC", "GROUP_CCC Drafters");
            case "NPQ": case "LPQ": case "OPQ":
                return new UnitAndTeamToAssign("GROUP_Parliamentary Questions", "GROUP_Parly Team Drafters");
            default:
                return new UnitAndTeamToAssign(null, null);
        }
    }

}