package uk.gov.homeoffice.cts.helpers;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.security.PermissionService;
import org.apache.chemistry.opencmis.commons.data.CmisExtensionElement;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.CmisExtensionElementImpl;
import uk.gov.homeoffice.cts.permissions.CtsPermissions;
import uk.gov.homeoffice.cts.permissions.PermissionChecker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by dawud on 01/07/2016.
 */
public class CtsCmisObjectHelper {

    private PermissionChecker permissionChecker;
    private PermissionService permissionService;

    /**
     * Add CtsCase Permissions
     * This Permissions list is inherited from the cts.lib.json.ftl template which retrieves CMIS Document AllowableActions
     */
    public Map<String, Boolean> getCasePermissions(NodeRef nodeRef) {

        List<CmisExtensionElement> extensions = new ArrayList<>();
        //This needs to be a list and in configuration
        String permission = CtsPermissions.ALLOCATE;
        if (permissionChecker.hasPermission(nodeRef, permission)) {
            CmisExtensionElement cmisExtensionElement = new CmisExtensionElementImpl(null, permission, null, "true");
            extensions.add(cmisExtensionElement);
        }
        List<CmisExtensionElement> propertyPermissions = permissionChecker.getPropertyPermissions(nodeRef);
        for (CmisExtensionElement propertyPermission : propertyPermissions) {
            extensions.add(propertyPermission);
        }

        /**
         * Add Permissions
         */
        Map<String, Boolean> propertyPermissionsMap = new HashMap<String, Boolean>();
        for (CmisExtensionElement element : extensions) {
            if (element.getName().equals("canAssignUser")) {
                propertyPermissionsMap.put(element.getName(), Boolean.parseBoolean(element.getValue()));
            }
        }

        /**
         * Add AllowedActions - retrieved from PermissionService
         * Only these allowable actions are checked in the PHP front end
         * - canAssignUser
         * - canUpdateProperties
         * - canDeleteObject
         * See: https://gist.github.com/dawudr/7033fcfdf209036b3674d7b8bdebc008
         */
        propertyPermissionsMap.put("canUpdateProperties", getPermissionService().hasPermission(nodeRef, PermissionService.WRITE_PROPERTIES).toString().equalsIgnoreCase("ALLOWED"));
        propertyPermissionsMap.put("canDeleteObject", getPermissionService().hasPermission(nodeRef, PermissionService.DELETE).toString().equalsIgnoreCase("ALLOWED"));

        /**
         * Additional permissions
         */
        propertyPermissionsMap.put("canGetFolderTree", getPermissionService().hasPermission(nodeRef, PermissionService.READ_CHILDREN).toString().equalsIgnoreCase("ALLOWED"));
        propertyPermissionsMap.put("canGetProperties", getPermissionService().hasPermission(nodeRef, PermissionService.READ_PROPERTIES).toString().equalsIgnoreCase("ALLOWED"));
        propertyPermissionsMap.put("canGetObjectRelationships", getPermissionService().hasPermission(nodeRef, PermissionService.READ_ASSOCIATIONS).toString().equalsIgnoreCase("ALLOWED"));
        propertyPermissionsMap.put("canGetObjectParents", getPermissionService().hasPermission(nodeRef, PermissionService.READ_ASSOCIATIONS).toString().equalsIgnoreCase("ALLOWED"));
        propertyPermissionsMap.put("canGetFolderParent", getPermissionService().hasPermission(nodeRef, PermissionService.READ_ASSOCIATIONS).toString().equalsIgnoreCase("ALLOWED"));
        propertyPermissionsMap.put("canGetDescendants", getPermissionService().hasPermission(nodeRef, PermissionService.READ).toString().equalsIgnoreCase("ALLOWED"));
        propertyPermissionsMap.put("canMoveObject", getPermissionService().hasPermission(nodeRef, PermissionService.WRITE).toString().equalsIgnoreCase("ALLOWED"));
        propertyPermissionsMap.put("canApplyPolicy", getPermissionChecker().getAuthorityService().authorityExists(nodeRef.toString()));
        propertyPermissionsMap.put("canGetAppliedPolicies", getPermissionService().hasPermission(nodeRef, PermissionService.READ_PERMISSIONS).toString().equalsIgnoreCase("ALLOWED"));
        propertyPermissionsMap.put("canRemovePolicy", getPermissionChecker().getAuthorityService().authorityExists(nodeRef.toString()));
        propertyPermissionsMap.put("canGetChildren", getPermissionService().hasPermission(nodeRef, PermissionService.READ_CHILDREN).toString().equalsIgnoreCase("ALLOWED"));
        propertyPermissionsMap.put("canCreateDocument", getPermissionService().hasPermission(nodeRef, PermissionService.ADD_CHILDREN).toString().equalsIgnoreCase("ALLOWED"));
        propertyPermissionsMap.put("canCreateFolder", getPermissionService().hasPermission(nodeRef, PermissionService.CREATE_CHILDREN).toString().equalsIgnoreCase("ALLOWED"));
        propertyPermissionsMap.put("canCreateRelationship", getPermissionService().hasPermission(nodeRef, PermissionService.CREATE_ASSOCIATIONS).toString().equalsIgnoreCase("ALLOWED"));
        propertyPermissionsMap.put("canDeleteTree", getPermissionService().hasPermission(nodeRef, PermissionService.DELETE_CHILDREN).toString().equalsIgnoreCase("ALLOWED"));
        propertyPermissionsMap.put("canGetACL", getPermissionService().hasPermission(nodeRef, PermissionService.READ_PERMISSIONS).toString().equalsIgnoreCase("ALLOWED"));
        propertyPermissionsMap.put("canApplyACL", getPermissionService().hasPermission(nodeRef, PermissionService.CHANGE_PERMISSIONS).toString().equalsIgnoreCase("ALLOWED"));
        return propertyPermissionsMap;
    }

    public PermissionChecker getPermissionChecker() {
        return permissionChecker;
    }

    public void setPermissionChecker(PermissionChecker permissionChecker) {
        this.permissionChecker = permissionChecker;
    }

    public PermissionService getPermissionService() {
        return permissionService;
    }

    public void setPermissionService(PermissionService permissionService) {
        this.permissionService = permissionService;
    }
}
