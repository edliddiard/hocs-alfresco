package uk.gov.homeoffice.cts.permissions;

import org.alfresco.repo.security.permissions.impl.AccessPermissionImpl;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.security.AccessPermission;
import org.alfresco.service.cmr.security.AccessStatus;
import org.alfresco.service.namespace.QName;
import org.apache.chemistry.opencmis.commons.data.CmisExtensionElement;
import org.junit.Test;
import org.junit.Assert;
import org.mockito.Mockito;
import org.mockito.stubbing.OngoingStubbing;
import uk.gov.homeoffice.cts.model.CtsModel;

import java.util.*;

import static org.mockito.Mockito.*;

/**
 * Created by chris on 26/03/2015.
 */
public class PermissionCheckerTest {
    /**
     * A user has a role in the restricted list
     */
    @Test
    public void checkRestrictedUser(){
        //String permission, AccessStatus accessStatus, String authority, int position
        AccessPermission accessPermission1 = new AccessPermissionImpl("PQDrafter",AccessStatus.ALLOWED,"chris",0);
        AccessPermission accessPermission2 = new AccessPermissionImpl("CaseViewer",AccessStatus.ALLOWED,"EVERYONE",0);
        Set<AccessPermission> nodePermissions = new HashSet<>(2);
        nodePermissions.add(accessPermission1);
        nodePermissions.add(accessPermission2);

        String user = "chris";

        PermissionChecker permissionChecker = new PermissionChecker();
        String[] roles = {"Other", "PQDrafter"};
        permissionChecker.setRestrictedRoles(Arrays.asList(roles));

        Assert.assertTrue("User should be restricted",permissionChecker.restrictedRoleInPermissions(user,nodePermissions));
    }

    /**
     * A user has no permissions at all
     */
    @Test
    public void checkNoPermissions(){
        //String permission, AccessStatus accessStatus, String authority, int position
        AccessPermission accessPermission1 = new AccessPermissionImpl("Drafter",AccessStatus.ALLOWED,"Paul",0);
        AccessPermission accessPermission2 = new AccessPermissionImpl("CaseViewer",AccessStatus.ALLOWED,"EVERYONE",0);
        Set<AccessPermission> nodePermissions = new HashSet<>(2);
        nodePermissions.add(accessPermission1);
        nodePermissions.add(accessPermission2);

        String user = "chris";

        PermissionChecker permissionChecker = new PermissionChecker();
        String[] roles = {"Other", "PQDrafter"};
        permissionChecker.setRestrictedRoles(Arrays.asList(roles));

        Assert.assertFalse("User should not be restricted", permissionChecker.restrictedRoleInPermissions(user, nodePermissions));
    }

    /**
     * A user has a non restricted role
     */
    @Test
    public void checkNonRestricted(){
        //String permission, AccessStatus accessStatus, String authority, int position
        AccessPermission accessPermission1 = new AccessPermissionImpl("Drafter",AccessStatus.ALLOWED,"Chris",0);
        AccessPermission accessPermission2 = new AccessPermissionImpl("CaseViewer",AccessStatus.ALLOWED,"EVERYONE",0);
        AccessPermission accessPermission3 = new AccessPermissionImpl("PQDrafter",AccessStatus.ALLOWED,"Paul",0);
        Set<AccessPermission> nodePermissions = new HashSet<>(2);
        nodePermissions.add(accessPermission1);
        nodePermissions.add(accessPermission2);
        nodePermissions.add(accessPermission3);

        String user = "chris";

        PermissionChecker permissionChecker = new PermissionChecker();
        String[] roles = {"Other", "PQDrafter"};
        permissionChecker.setRestrictedRoles(Arrays.asList(roles));

        Assert.assertFalse("User should not be restricted",permissionChecker.restrictedRoleInPermissions(user,nodePermissions));
    }

    /**
     * A user is denied the restricted role
     */
    @Test
    public void checkDeniedPermission(){
        //String permission, AccessStatus accessStatus, String authority, int position
        AccessPermission accessPermission1 = new AccessPermissionImpl("PQDrafter",AccessStatus.DENIED,"Chris",0);
        AccessPermission accessPermission2 = new AccessPermissionImpl("CaseViewer",AccessStatus.ALLOWED,"EVERYONE",0);
        Set<AccessPermission> nodePermissions = new HashSet<>(2);
        nodePermissions.add(accessPermission1);
        nodePermissions.add(accessPermission2);

        String user = "chris";

        PermissionChecker permissionChecker = new PermissionChecker();
        String[] roles = {"Other", "PQDrafter"};
        permissionChecker.setRestrictedRoles(Arrays.asList(roles));

        Assert.assertFalse("User should not be restricted",permissionChecker.restrictedRoleInPermissions(user,nodePermissions));
    }

    /**
     * A user has a role in the restricted list
     */
    @Test
    public void checkGroup(){
        //String permission, AccessStatus accessStatus, String authority, int position
        AccessPermission accessPermission1 = new AccessPermissionImpl("PQDrafter",AccessStatus.ALLOWED,"GROUP_chris",0);
        AccessPermission accessPermission2 = new AccessPermissionImpl("CaseViewer",AccessStatus.ALLOWED,"EVERYONE",0);
        Set<AccessPermission> nodePermissions = new HashSet<>(2);
        nodePermissions.add(accessPermission1);
        nodePermissions.add(accessPermission2);

        String user = "GROUP_chris";

        PermissionChecker permissionChecker = new PermissionChecker();
        String[] roles = {"Other", "PQDrafter"};
        permissionChecker.setRestrictedRoles(Arrays.asList(roles));

        Assert.assertFalse("Groups should not work with restricted", permissionChecker.restrictedRoleInPermissions(user, nodePermissions));
    }

    /**
     * Test that only cts properties get added to permissions list to be sent out
     */
    @Test
    public void testPermissionList(){
        NodeRef nodeRef = new NodeRef("workspace://SpacesStore/MYNODEREF");
        QName[] qNames = {QName.createQName(CtsModel.CTS_NAMESPACE,"title"),QName.createQName(CtsModel.CTS_NAMESPACE,"name"),
                QName.createQName("cm","another"),QName.createQName("cm","prop")};
        Collection<QName> properties = Arrays.asList(qNames);

        List<CmisExtensionElement> elementList = new ArrayList<>();

        Set<String> permissions = new HashSet<>();
        permissions.add("title");

        PermissionChecker permissionChecker = mock(PermissionChecker.class);


        Mockito.when(permissionChecker.hasPermission(nodeRef, "title")).thenReturn(true);
        Mockito.when(permissionChecker.hasPermission(nodeRef, "name")).thenReturn(true);
        Mockito.when(permissionChecker.hasPermission(nodeRef, "another")).thenReturn(false);
        Mockito.when(permissionChecker.hasPermission(nodeRef, "prop")).thenReturn(false);
        doCallRealMethod().when(permissionChecker).addPermissionsToList(nodeRef, properties, elementList);
        Mockito.when(permissionChecker.getPermissions()).thenReturn(permissions);



        Assert.assertEquals(0,elementList.size());

        permissionChecker.addPermissionsToList(nodeRef, properties, elementList);

        Assert.assertEquals(2,elementList.size());
        Assert.assertEquals("title",elementList.get(0).getName());
        Assert.assertEquals("true",elementList.get(0).getValue());
        Assert.assertEquals("name",elementList.get(1).getName());
        //this is not in the list of settable property permissions so should be false
        Assert.assertEquals("false",elementList.get(1).getValue());
    }


}
