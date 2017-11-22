package uk.gov.homeoffice.cts.behaviour;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.permissions.AccessDeniedException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.namespace.QName;
import org.apache.chemistry.opencmis.commons.data.CmisExtensionElement;
import org.joda.time.DateTimeComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.homeoffice.cts.model.CtsModel;
import uk.gov.homeoffice.cts.permissions.CtsPermissions;
import uk.gov.homeoffice.cts.permissions.PermissionChecker;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A class to check property changes and whether a user has permission to change them
 * Created by chris on 12/08/2014.
 */
public class PropertyPermissionsBehaviour implements PropertyUpdateBehaviour {
    private static final Logger LOGGER = LoggerFactory.getLogger(PropertyPermissionsBehaviour.class);
    private AuthorityService authorityService;
    private PermissionChecker permissionChecker;


    @Override
    public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
        String beforeUser = (String) before.get(CtsModel.PROP_ASSIGNED_USER);
        String afterUser = (String) after.get(CtsModel.PROP_ASSIGNED_USER);

        if (BehaviourHelper.hasChanged(beforeUser, afterUser)) {
            LOGGER.debug(CtsModel.PROP_ASSIGNED_USER + " before " + beforeUser);
            LOGGER.debug(CtsModel.PROP_ASSIGNED_USER + " after " + afterUser);
            //check if the user can change it
            String authenticatedUser = AuthenticationUtil.getFullyAuthenticatedUser();
            if (getAuthorityService().isAdminAuthority(authenticatedUser)) {
                //that's ok so do nothing
            } else if (getPermissionChecker().hasPermission(nodeRef, CtsPermissions.ALLOCATE)) {
                //that's ok so do nothing
            } else {
                throw new AccessDeniedException("You do not have permission to change the assigned user property");
            }
        }
        checkPropertiesPermissions(nodeRef, before, after);
    }

    /**
     * Method to check whether the user has permission to edit specific properties,
     * these are our own implementation and not supported by Alfresco. If the user does
     * not have permission to edit properties at all Alfresco will throw an exception.
     * We have the concept of restricted roles where they can edit some of the properties
     *
     * @param nodeRef
     * @param before
     * @param after
     */
    private void checkPropertiesPermissions(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
        //check if they have restricted role
        if (getPermissionChecker().isUserRestricted(nodeRef)) {
            List<CmisExtensionElement> restrictedPermissions = getPermissionChecker().getPropertyPermissions(nodeRef);
            //putting the permissions in a map so they are quick to lookup.
            Map<String, String> mapOfRestrictedPermissions = new HashMap<>();
            for (CmisExtensionElement restrictedPermission : restrictedPermissions) {
                mapOfRestrictedPermissions.put(restrictedPermission.getName(), restrictedPermission.getValue());
            }
            for (QName qName : after.keySet()) {
                if (qName.equals(CtsModel.PROP_MARKUP_UNIT)) {
                    //having to hard code this in. The user can't edit directly but it gets edited in the background
                    //by a behaviour
                    continue;
                }

                if (qName.equals(CtsModel.PROP_OP_DATE)) {
                    if (before.get(qName) != null
                            && before.get(qName) instanceof Date
                            && after.get(qName) != null) {

                        // check if the days in the date are different
                        if (DateTimeComparator.getDateOnlyInstance().compare(before.get(qName), after.get(qName)) != 0) {
                            //hack to compare the date only and ignoring the timestamp
                            checkPermissionToUpdateProp(mapOfRestrictedPermissions, qName);
                        }
                    }

                } else if (BehaviourHelper.hasChangedSerializable(before.get(qName), after.get(qName))) {
                    //check if it is a cts property
                    checkPermissionToUpdateProp(mapOfRestrictedPermissions, qName);
                }
            }
        }
    }

    private void checkPermissionToUpdateProp(Map<String, String> mapOfRestrictedPermissions, QName qName) {
        if (qName.getNamespaceURI().equals(CtsModel.CTS_NAMESPACE)) {
            if (mapOfRestrictedPermissions.get(qName.getLocalName()).equals("false")) {
                //they don't have permission for it
                throw new AccessDeniedException("You do not have permission to change the property " + qName.toString());
            }
        }
    }

    AuthorityService getAuthorityService() {
        return authorityService;
    }

    public void setAuthorityService(AuthorityService authorityService) {
        this.authorityService = authorityService;
    }

    PermissionChecker getPermissionChecker() {
        return permissionChecker;
    }

    public void setPermissionChecker(PermissionChecker permissionChecker) {
        this.permissionChecker = permissionChecker;
    }
}
