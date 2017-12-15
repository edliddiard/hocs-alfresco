package uk.gov.homeoffice.cts.permissions;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.*;
import org.alfresco.service.namespace.QName;
import org.apache.chemistry.opencmis.commons.data.CmisExtensionElement;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.CmisExtensionElementImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.homeoffice.cts.model.CtsModel;

import java.util.*;

/**
 * Class to check the permissions, it just to save me having to edit the config in Spring
 * as the permissionService in the parent object is private access.
 * Created by chris on 14/08/2014.
 */
public class PermissionChecker {
    private static final Logger LOGGER = LoggerFactory.getLogger(PermissionChecker.class);
    private PermissionService permissionService;

    DictionaryService getDictionaryService() {
        return dictionaryService;
    }

    public void setDictionaryService(DictionaryService dictionaryService) {
        this.dictionaryService = dictionaryService;
    }

    private DictionaryService dictionaryService;

    public NodeService getNodeService() {
        return nodeService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    private NodeService nodeService;

    private List<String> restrictedRoles;

    private Set<String> permissions ;

    private AuthorityService authorityService;


    public boolean hasPermission(NodeRef nodeRef, String permission){
        AccessStatus accessStatus = getPermissionService().hasPermission(nodeRef,permission);
        return accessStatus != AccessStatus.DENIED;
    }

    /**
     * Get the permissions for individual properties, this is only for the roles listed on restrictedRoles
     * It locks down all properties unless a permission has been allocated for the user role
     * @param nodeRef
     * @return
     */
    public List<CmisExtensionElement> getPropertyPermissions(NodeRef nodeRef) {
        List<CmisExtensionElement> elementList = new ArrayList<>();

        //only do this for user roles listed as restricted.
        boolean restrictedUserRole = isUserRestricted(nodeRef);

        if(restrictedUserRole) {
            //Only doing this for restricted roles as other users will use canUpdateProperties permission
            QName typeQName = getNodeService().getType(nodeRef);
            if (typeQName.equals(CtsModel.TYPE_CTS_CASE)) {
                Collection<QName> properties = getDictionaryService().getProperties(CtsModel.CTS_MODEL_NAME);
                Collection<QName> aspects = getDictionaryService().getAspects(CtsModel.CTS_MODEL_NAME);
                addPermissionsToList(nodeRef, properties, elementList);
                addPermissionsToList(nodeRef, aspects, elementList);
            }
        }

        return elementList;
    }

    public boolean isUserRestricted(final NodeRef nodeRef) {
        if(getAuthorityService().hasAdminAuthority()){
            //if they are admin don't restrict
            return false;
        }
        String user = AuthenticationUtil.getFullyAuthenticatedUser();
        //get all the permissions allocated to the case
        Set<AccessPermission> nodePermissions = AuthenticationUtil.runAs(new AuthenticationUtil.RunAsWork<Set<AccessPermission>>() {
            @SuppressWarnings("synthetic-access")
            public Set<AccessPermission> doWork() throws Exception {
                Set<AccessPermission> nodePermissions = getPermissionService().getAllSetPermissions(nodeRef);
                return nodePermissions;
            }
        }, AuthenticationUtil.getSystemUserName());

        return restrictedRoleInPermissions(user, nodePermissions);
    }

    boolean restrictedRoleInPermissions(String user, Set<AccessPermission> nodePermissions) {
        for (AccessPermission accessPermission : nodePermissions) {
            //check for the authenticated user having a restricted role on the case
            if(accessPermission.getAuthorityType() == AuthorityType.USER &&
                    accessPermission.getAuthority().equals(user) &&
                    getRestrictedRoles().contains(accessPermission.getPermission() ) &&
                    accessPermission.getAccessStatus() == AccessStatus.ALLOWED){
                return true;
            }
        }
        return false;
    }

    protected void addPermissionsToList(NodeRef nodeRef, Collection<QName> properties, List<CmisExtensionElement> elementList) {
        for (QName property : properties) {
            if(property.getNamespaceURI().equals(CtsModel.CTS_NAMESPACE)){
                if(getPermissions().contains(property.getLocalName())) {
                    String permission = property.getLocalName();
                    boolean hasPermission = hasPermission(nodeRef, permission);
                    CmisExtensionElement cmisExtensionElement = new CmisExtensionElementImpl(null, property.getLocalName(), null, Boolean.toString(hasPermission));
                    elementList.add(cmisExtensionElement);
                }else {
                    //if they are not in the permissions at all then user can't edit
                    CmisExtensionElement cmisExtensionElement = new CmisExtensionElementImpl(null, property.getLocalName(), null, "false");
                    elementList.add(cmisExtensionElement);
                }
            }
        }
    }

    public PermissionService getPermissionService() {
        return permissionService;
    }


    public void setPermissionService(PermissionService permissionService) {
        this.permissionService = permissionService;
    }
    List<String> getRestrictedRoles() {
        return restrictedRoles;
    }

    public void setRestrictedRoles(List<String> restrictedRoles) {
        this.restrictedRoles = restrictedRoles;
    }

    public Set<String> getPermissions() {
        if(permissions == null){
            //cache them so we don't have to keep looking them up. They would only change on restart
            setPermissions(getPermissionService().getSettablePermissions(ContentModel.TYPE_FOLDER));
        }
        return permissions;
    }

    protected void setPermissions(Set<String> permissions) {
        this.permissions = permissions;
    }

    public AuthorityService getAuthorityService() {
        return authorityService;
    }

    public void setAuthorityService(AuthorityService authorityService) {
        this.authorityService = authorityService;
    }
}
