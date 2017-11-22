package uk.gov.homeoffice.cts.helpers;


import org.alfresco.service.cmr.repository.DuplicateChildNodeNameException;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.cmr.security.AuthorityType;
import uk.gov.homeoffice.cts.model.GroupAction;

import java.util.List;

public class CtsGroupHelper {

    private AuthorityService authorityService;
    private final static String GROUPS_UNITS = "GROUP_Units";

    public void setAuthorityService(AuthorityService authorityService) {
        this.authorityService = authorityService;
    }

    public void createUnits(List<String> logDetails, List<GroupAction> unitNames) {
        for (GroupAction groupAction : unitNames) {
            if (!authorityService.authorityExists(formatAuthorityName(groupAction.getUnitRefName()))) {
                String authority = authorityService.createAuthority(AuthorityType.GROUP, groupAction.getUnitRefName());
                addAuthority(formatAuthorityName(GROUPS_UNITS), authority);
                if(groupAction.getUnitDisplayName() != null) {
                    authorityService.setAuthorityDisplayName(formatAuthorityName(groupAction.getUnitRefName()), groupAction.getUnitDisplayName());
                }
                logDetails.add("Created group: " + groupAction.getUnitRefName());
            } else {
                logDetails.add("Error: group exists: " + groupAction.getUnitRefName());
            }
        }
    }

    public void createTeams(List<String> logDetails, List<GroupAction> groupNames) {
        for (GroupAction groupAction : groupNames) {
            if (!authorityService.authorityExists(formatAuthorityName(groupAction.getTeamRefName()))) {
                String authority = authorityService.createAuthority(AuthorityType.GROUP, groupAction.getTeamRefName());
                addAuthority(formatAuthorityName(groupAction.getUnitRefName()), authority);
                if(groupAction.getTeamDisplayName() != null) {
                    authorityService.setAuthorityDisplayName(formatAuthorityName(groupAction.getTeamRefName()), groupAction.getTeamDisplayName());
                }
                logDetails.add("Created group: " + groupAction.getTeamRefName());
            } else {
                logDetails.add("Error: group exists: " + groupAction.getTeamRefName());
            }
        }
    }

    public void removeUnits(List<String> logDetails, List<GroupAction> removeUnits) {
        for (GroupAction groupAction : removeUnits) {
            final String unitName = formatAuthorityName(groupAction.getUnitRefName());
            if (authorityService.authorityExists(unitName)) {
                authorityService.deleteAuthority(unitName);
                logDetails.add("Deleted unit: " + groupAction.getUnitRefName());
            } else {
                logDetails.add("Error: Unit not exist: " + groupAction.getUnitRefName());
            }
        }
    }

    public void removeTeams(List<String> logDetails, List<GroupAction> removeGroups) {
        for (GroupAction groupAction : removeGroups) {
            final String groupName = formatAuthorityName(groupAction.getTeamRefName());
            if (authorityService.authorityExists(groupName)) {
                authorityService.deleteAuthority(groupName);
                logDetails.add("Deleted team: " + groupAction.getTeamRefName());
            } else {
                logDetails.add("Error: Team not exist: " + groupAction.getTeamRefName());
            }
        }
    }

    private static String formatAuthorityName(String unitName) {
        if (unitName != null && !unitName.startsWith("GROUP_")) {
            return "GROUP_" + unitName;
        }
        return unitName;
    }

    private boolean addAuthority(String parentName, String childName) {
        try {
            authorityService.addAuthority(parentName, childName);
        } catch (DuplicateChildNodeNameException e) {
            //Alfresco not supporting to check authority exist between parent and child so hack
            return false;
        }
        return true;
    }
}