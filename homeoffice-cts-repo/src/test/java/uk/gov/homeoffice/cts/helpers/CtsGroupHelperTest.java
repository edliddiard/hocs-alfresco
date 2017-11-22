package uk.gov.homeoffice.cts.helpers;


import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.cmr.security.AuthorityType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.homeoffice.cts.model.GroupAction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CtsGroupHelperTest {


    protected static CtsGroupHelper toTest;
    private final static String GROUPS_UNITS = "GROUP_Units";

    @Mock private AuthorityService authorityService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        toTest = new CtsGroupHelper();
        toTest.setAuthorityService(authorityService);
    }

    @Test
    public void shouldNotBeNullInstance() {
        assertNotNull(toTest);
    }

    @Test
    public void shouldCreateNewUnits() {
        List<String> logDetails = new ArrayList<>();
        List<GroupAction> unitNames= new ArrayList<>();
        unitNames.add(new GroupAction("Test-1", null, "Test One", null));
        unitNames.add(new GroupAction("Test-2", null, "Test Two", null));

        when(authorityService.createAuthority(AuthorityType.GROUP, "Test-1")).thenReturn("GROUP_Test-1");
        when(authorityService.createAuthority(AuthorityType.GROUP, "Test-2")).thenReturn("GROUP_Test-2");
        toTest.createUnits(logDetails,unitNames);
        assertEquals("[Created group: Test-1, Created group: Test-2]", logDetails.toString());

        //verify
        verify(authorityService).createAuthority(AuthorityType.GROUP, "Test-1");
        verify(authorityService).addAuthority(GROUPS_UNITS, "GROUP_Test-1");


        verify(authorityService).createAuthority(AuthorityType.GROUP, "Test-2");
        verify(authorityService).addAuthority(GROUPS_UNITS, "GROUP_Test-2");

    }

    @Test
    public void shouldNotCreateUnitsWhenExist() {
        List<String> logDetails = new ArrayList<>();
        List<GroupAction> unitNames= new ArrayList<>();
        unitNames.add(new GroupAction("Test-1", null, null, null));
        unitNames.add(new GroupAction("Test-2", null, null, null));

        when(authorityService.authorityExists("GROUP_Test-1")).thenReturn(true);
        when(authorityService.authorityExists("GROUP_Test-2")).thenReturn(false);
        when(authorityService.createAuthority(AuthorityType.GROUP, "Test-2")).thenReturn("GROUP_Test-2");
        toTest.createUnits(logDetails,unitNames);
        assertEquals("[Error: group exists: Test-1, Created group: Test-2]", logDetails.toString());

        //verify
        verify(authorityService,never()).createAuthority(AuthorityType.GROUP, "Test-1");
        verify(authorityService,never()).addAuthority(GROUPS_UNITS, "GROUP_Test-1");


        verify(authorityService).createAuthority(AuthorityType.GROUP, "Test-2");
        verify(authorityService).addAuthority(GROUPS_UNITS, "GROUP_Test-2");

    }

    @Test
    public void shouldCreateNewGroup() {
        List<String> logDetails = new ArrayList<>();
        List<GroupAction> groupList= new ArrayList<>();
        groupList.add(new GroupAction("Unit-1", "group-1", null, null));
        groupList.add(new GroupAction("Unit-2", "group-2", null, null));

        when(authorityService.authorityExists("GROUP_Unit-1")).thenReturn(true);
        when(authorityService.authorityExists("GROUP_Unit-2")).thenReturn(true);

        when(authorityService.createAuthority(AuthorityType.GROUP, "group-1")).thenReturn("GROUP_group-1");
        when(authorityService.createAuthority(AuthorityType.GROUP, "group-2")).thenReturn("GROUP_group-2");

        toTest.createTeams(logDetails,groupList);
        assertEquals("[Created group: group-1, Created group: group-2]", logDetails.toString());

        //verify
        verify(authorityService).createAuthority(AuthorityType.GROUP, "group-1");
        verify(authorityService).addAuthority("GROUP_Unit-1", "GROUP_group-1");

        verify(authorityService).createAuthority(AuthorityType.GROUP, "group-2");
        verify(authorityService).addAuthority("GROUP_Unit-2", "GROUP_group-2");
    }

    @Test
    public void shouldIgnoreGroupIfAlreadyExist() {
        List<String> logDetails = new ArrayList<>();
        List<GroupAction> groupList= populateGroupAction("Unit-1", "group-1", null, null);

        when(authorityService.authorityExists("GROUP_Unit-1")).thenReturn(true);
        when(authorityService.authorityExists("GROUP_group-1")).thenReturn(true);

        toTest.createTeams(logDetails,groupList);
        assertEquals("[Error: group exists: group-1]", logDetails.toString());

        //verify
        verify(authorityService,never()).createAuthority(AuthorityType.GROUP, "group-1");
        verify(authorityService,never()).addAuthority("GROUP_Unit-1", "GROUP_group-1");
    }


    @Test
    public void shouldIgnoreRemoveGroupIfNotExist() {
        List<String> logDetails = new ArrayList<>();

        List<GroupAction> removeGroups= populateGroupAction(null, "test-group",null, null);

        when(authorityService.authorityExists("GROUP_test-group")).thenReturn(false);

        toTest.removeTeams(logDetails,removeGroups);
        assertEquals("[Error: Team not exist: test-group]", logDetails.toString());

        verify(authorityService, never()).deleteAuthority("GROUP_test-group");
    }

    @Test
    public void shouldRemoveGroupIfExist() {
        List<String> logDetails = new ArrayList<>();

        List<GroupAction> removeGroups= populateGroupAction(null, "test-group",null, null);

        when(authorityService.authorityExists("GROUP_test-group")).thenReturn(true);

        toTest.removeTeams(logDetails,removeGroups);
        assertEquals("[Deleted team: test-group]", logDetails.toString());

        verify(authorityService).deleteAuthority("GROUP_test-group");
    }

    @Test
    public void shouldIgnoreRemoveUnitIfNotExist() {
        List<String> logDetails = new ArrayList<>();

        List<GroupAction> removeUnits= populateGroupAction("test-unit", null,null, null);

        when(authorityService.authorityExists("GROUP_test-unit")).thenReturn(false);

        toTest.removeUnits(logDetails,removeUnits);
        assertEquals("[Error: Unit not exist: test-unit]", logDetails.toString());

        verify(authorityService, never()).deleteAuthority("GROUP_test-unit");
    }

    @Test
    public void shouldRemoveUnitIfExist() {
        List<String> logDetails = new ArrayList<>();

        List<GroupAction> removeUnits= populateGroupAction("test-unit", null,null, null);

        when(authorityService.authorityExists("GROUP_test-unit")).thenReturn(true);

        toTest.removeUnits(logDetails,removeUnits);
        assertEquals("[Deleted unit: test-unit]", logDetails.toString());

        verify(authorityService).deleteAuthority("GROUP_test-unit");
    }


    private List<GroupAction> populateGroupAction(String unitName, String groupName, String unitNewName, String groupNewName) {
        return Collections.singletonList(new GroupAction(unitName, groupName, unitNewName, groupNewName));
    }
}
