package uk.gov.homeoffice.cts.workflow;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import uk.gov.homeoffice.cts.model.CtsModel;
import uk.gov.homeoffice.cts.permissions.PermissionChecker;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Putting the code to allocate cases into one place
 * Created by chris on 13/10/2014.
 */
public class AllocationService {
    private static NodeService nodeService;


    private PermissionChecker permissionChecker;

    /**
     * Made this static so it is easy to use in the workflow delegates
     * @param nodeRef
     * @param assignedUser
     * @param assignedTeam
     * @param assignedUnit
     */
    public void allocateCase(final NodeRef nodeRef, final String assignedUnit, final String assignedTeam, final String assignedUser){

        final Map<QName, Serializable> caseProperties = new HashMap();
        caseProperties.put(CtsModel.PROP_ASSIGNED_UNIT, assignedUnit);
        caseProperties.put(CtsModel.PROP_ASSIGNED_TEAM, assignedTeam);
        caseProperties.put(CtsModel.PROP_ASSIGNED_USER, assignedUser);

        if(getPermissionChecker().hasPermission(nodeRef, "canAssignUser")) {
            AuthenticationUtil.runAs(new AuthenticationUtil.RunAsWork<Map<String, Object>>() {
                @SuppressWarnings("synthetic-access")
                public Map<String, Object> doWork() throws Exception {
                    getNodeService().addProperties(nodeRef, caseProperties);
                    return null;
                }
            }, //AuthenticationUtil.getSystemUserName()
                    AuthenticationUtil.getAdminUserName());
        }else{
            getNodeService().addProperties(nodeRef, caseProperties);
        }
    }

    public static NodeService getNodeService() {
        return nodeService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public PermissionChecker getPermissionChecker() {
        return permissionChecker;
    }

    public void setPermissionChecker(PermissionChecker permissionChecker) {
        this.permissionChecker = permissionChecker;
    }
}
