package uk.gov.homeoffice.ctsv2.services.todo;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.template.TemplateNode;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.security.AccessStatus;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.namespace.QName;
import org.apache.chemistry.opencmis.commons.data.CmisExtensionElement;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.CmisExtensionElementImpl;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.extensions.webscripts.WebScriptRequest;
import uk.gov.homeoffice.cts.helpers.CtsCmisObjectHelper;
import uk.gov.homeoffice.cts.model.CorrespondenceType;
import uk.gov.homeoffice.cts.model.CtsModel;
import uk.gov.homeoffice.cts.permissions.CtsPermissions;
import uk.gov.homeoffice.cts.permissions.PermissionChecker;
import uk.gov.homeoffice.ctsv2.model.CtsCase;
import uk.gov.homeoffice.ctsv2.webscripts.CaseMapperHelper;

import java.io.Serializable;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ToDoListServiceTest {

    private static final String nodeRefString = "workspace://SpacesStore/8737883a-1679-4b1c-84e9-8d51f95cceca";
    private static final String title = "Mock Case Document";
    private static final String group = "GROUP_" + "Parliamentary Questions Team";

    private final static Map<Query, String> queryList = new HashMap<>();

    private static final String uin = "0001";
    //    private static String userNames = null;
    private static final int pageLimit = 2;
    private static final int pageOffset = 1;
    private static final String sortingOrder = "ASC";
    private static final String urnSuffix = "0000001/16";
    private static final String unit = "GROUP_" + "GROUP_Parliamentary Questions";
    private String correspondenceType = CorrespondenceType.ORDINARY_WRITTEN.getCode();
    private String caseStatus = "Draft";
    private String caseTasks = "Draft response";
    private String includeAllowableActions = "true";
    private String assignedUser;
    private String caseStatusParam;
    private String assignedTeams;
    private String assignedUnits;
    private String caseTasksProp;
    private String caseTypesProp;
    private String filterByPriorityParam;
    private ToDoListSearchParams params;
    private ResultSet rs;
    private ResultSet resultSetForTotalCount;
    private Iterator iterator;
    private ResultSetRow row;
    private ServiceRegistry serviceRegistry;

    private enum Query {GlobalSearchQuery, AssignedTeamsSearchQuery, CaseStatusSearchQuery, CaseTaskSearchQuery, AssignedUserSearchQuery, AssignedUnitsSearchQuery, CaseTypeSearchQuery, PrioritySearchQuery}

    @Mock
    protected PermissionChecker permissionChecker;
    @Mock
    protected PermissionService permissionService;
    @Mock
    protected NodeService nodeService;
    @Spy
    protected CtsCmisObjectHelper ctsCmisObjectHelper = new CtsCmisObjectHelper();
    @Mock
    protected CaseMapperHelper caseMapperHelper;
    @Mock
    protected SearchService searchService;
    @InjectMocks
    private ToDoListService toDoListService;

    @BeforeClass
    public static void setUpOnce() {
        createQueryList();
    }

    @Before
    public void setUp() throws Exception {
        // Mock services
        mockPermissionChecker();
        ctsCmisObjectHelper.setPermissionChecker(permissionChecker);
        ctsCmisObjectHelper.setPermissionService(permissionService);
        params = createMockSearchParam();
        rs = mock(ResultSet.class);
        resultSetForTotalCount = mock(ResultSet.class);
        iterator = mock(Iterator.class);
        row = mock(ResultSetRow.class);
        when(rs.iterator()).thenReturn(iterator);
    }

    @Test
    public void testGetToDoListPermissions() {
        includeAllowableActions = "true";

        WebScriptRequest request = mockRequestParams();

        SearchParameters sp = createSearchParameters(Query.GlobalSearchQuery);
        SearchParameters spForTotalResults = createSearchParametersForTR(Query.GlobalSearchQuery);
        mockResultsSet(spForTotalResults, sp);

        // when
        ToDoListResponse response = toDoListService.getToDoList(request);

        // then
        // Post-condition Asserts
        assertEquals(2, response.getTotalResults());
        assertNotNull("Missing ctsCases node", response.getCaseList());
        assertEquals(2, response.getCaseList().size());

        for (Map<String, Object> ctsCaseMap : response.getCaseList()) {
            // CtsCase Permissions
            Map<String, Object> permissionsMap = (Map) ctsCaseMap.get("allowableActions");
            assertEquals(true, permissionsMap.get("canAssignUser"));
            assertEquals(true, permissionsMap.get("canDeleteObject"));
            assertEquals(true, permissionsMap.get("canUpdateProperties"));
            assertEquals(true, permissionsMap.get("canGetFolderTree"));
            assertEquals(true, permissionsMap.get("canGetProperties"));
            assertEquals(true, permissionsMap.get("canGetObjectRelationships"));
            assertEquals(true, permissionsMap.get("canGetObjectParents"));
            assertEquals(true, permissionsMap.get("canGetFolderParent"));
            assertEquals(true, permissionsMap.get("canGetDescendants"));
            assertEquals(true, permissionsMap.get("canMoveObject"));
            assertEquals(false, permissionsMap.get("canApplyPolicy"));
            assertEquals(true, permissionsMap.get("canGetAppliedPolicies"));
            assertEquals(false, permissionsMap.get("canRemovePolicy"));
            assertEquals(true, permissionsMap.get("canGetChildren"));
            assertEquals(true, permissionsMap.get("canCreateDocument"));
            assertEquals(true, permissionsMap.get("canCreateFolder"));
            assertEquals(true, permissionsMap.get("canCreateRelationship"));
            assertEquals(true, permissionsMap.get("canDeleteTree"));
            assertEquals(true, permissionsMap.get("canGetACL"));
            assertEquals(true, permissionsMap.get("canApplyACL"));
            assertEquals(true, permissionsMap.get("canDeleteObject"));
        }
    }

    @Test
    public void testGetToDoListWithOutPermissions() {
        assignedUser = null;
        includeAllowableActions = "false";

        WebScriptRequest request = mockRequestParams();
        params.setIncludeAllowableActions(false);

        SearchParameters sp = createSearchParameters(Query.GlobalSearchQuery);
        SearchParameters spForTotalResults = createSearchParametersForTR(Query.GlobalSearchQuery);
        mockResultsSet(spForTotalResults, sp);

        // when
        ToDoListResponse response = toDoListService.getToDoList(request);

        // then
        // Post-condition Asserts
        assertEquals(2, response.getTotalResults());
        assertNotNull("Missing ctsCases node", response.getCaseList());
        assertEquals(2, response.getCaseList().size());

        for (Map<String, Object> ctsCaseMap : response.getCaseList()) {
            assertFalse(ctsCaseMap.containsKey("allowableActions"));
        }
    }

    @Test
    public void testGetToDoListWithStatusFilter() {
        caseStatusParam = "TEST_CASE_APPROVED";
        WebScriptRequest request = mockRequestParams();

        params.setCaseStatus("TEST_CASE_APPROVED");
        caseStatus = "TEST_CASE_APPROVED";

        SearchParameters sp = createSearchParameters(Query.CaseStatusSearchQuery);
        SearchParameters sp1 = createSearchParametersForTR(Query.CaseStatusSearchQuery);
        mockResultsSet("0003", sp1, sp);

        // when
        ToDoListResponse response = toDoListService.getToDoList(request);

        // then
        // Post-condition Asserts
        assertEquals(1, response.getTotalResults());
        assertNotNull("Missing ctsCases node", response.getCaseList());
        assertEquals(1, response.getCaseList().size());

        for (Map<String, Object> ctsCaseMap : response.getCaseList()) {
            CtsCase ctsCase = (CtsCase) ctsCaseMap.get("case");
            assertNotNull("Missing case child node", ctsCase);
            assertEquals("0003", ctsCase.getUin());
            assertEquals(urnSuffix, ctsCase.getUrnSuffix());
            assertEquals("TEST_CASE_APPROVED", ctsCase.getCaseStatus());
        }
    }

    @Test
    public void testGetToDoListWithTeamFilter() {
        assignedTeams = "TEST_ASSIGNED_TEAM";
        WebScriptRequest request = mockRequestParams();

        params.setAssignedTeams("TEST_ASSIGNED_TEAM");
        assignedTeams = "TEST_ASSIGNED_TEAM";

        SearchParameters sp = createSearchParameters(Query.AssignedTeamsSearchQuery);
        SearchParameters spForTotalResults = createSearchParametersForTR(Query.AssignedTeamsSearchQuery);
        mockResultsSet("0004", spForTotalResults, sp);

        // when
        ToDoListResponse response = toDoListService.getToDoList(request);

        // then
        // Post-condition Asserts
        assertEquals(1, response.getTotalResults());
        assertNotNull("Missing ctsCases node", response.getCaseList());
        assertEquals(1, response.getCaseList().size());

        for (Map<String, Object> ctsCaseMap : response.getCaseList()) {
            CtsCase ctsCase = (CtsCase) ctsCaseMap.get("case");
            assertNotNull("Missing case child node", ctsCase);
            assertEquals("0004", ctsCase.getUin());
            assertEquals(urnSuffix, ctsCase.getUrnSuffix());
            assertEquals("TEST_ASSIGNED_TEAM", ctsCase.getAssignedTeam());
        }
    }

    @Test
    public void testGetToDoListWithCaseTypesFilter() {
        caseTypesProp = "TEST_CASE_TYPE";
        WebScriptRequest request = mockRequestParams();
        params.setCaseTypes("TEST_CASE_TYPE");
        correspondenceType = "TEST_CASE_TYPE";

        SearchParameters sp = createSearchParameters(Query.CaseTypeSearchQuery);
        SearchParameters spForTotalResults = createSearchParametersForTR(Query.CaseTypeSearchQuery);

        mockResultsSet("0005", spForTotalResults, sp);

        // when
        ToDoListResponse response = toDoListService.getToDoList(request);

        // then
        // Post-condition Asserts
        assertEquals(1, response.getTotalResults());
        assertNotNull("Missing ctsCases node", response.getCaseList());
        assertEquals(1, response.getCaseList().size());

        for (Map<String, Object> ctsCaseMap : response.getCaseList()) {
            CtsCase ctsCase = (CtsCase) ctsCaseMap.get("case");
            assertNotNull("Missing case child node", ctsCase);
            assertEquals("0005", ctsCase.getUin());
            assertEquals(urnSuffix, ctsCase.getUrnSuffix());
            assertEquals("TEST_CASE_TYPE", ctsCase.getCorrespondenceType());
        }
    }

    @Test
    public void testGetToDoListWithTaskFilter() {
        caseTasksProp = "TEST_CASE_TASK";
        WebScriptRequest request = mockRequestParams();
        params.setCaseTasks("TEST_CASE_TASK");
        caseTasks = "TEST_CASE_TASK";

        SearchParameters sp = createSearchParameters(Query.CaseTaskSearchQuery);
        SearchParameters spForTotalResults = createSearchParametersForTR(Query.CaseTaskSearchQuery);
        mockResultsSet("0006", spForTotalResults, sp);

        // when
        ToDoListResponse response = toDoListService.getToDoList(request);

        // then
        // Post-condition Asserts
        assertEquals(1, response.getTotalResults());
        assertNotNull("Missing ctsCases node", response.getCaseList());
        assertEquals(1, response.getCaseList().size());

        for (Map<String, Object> ctsCaseMap : response.getCaseList()) {
            CtsCase ctsCase = (CtsCase) ctsCaseMap.get("case");
            assertNotNull("Missing case child node", ctsCase);
            assertEquals("0006", ctsCase.getUin());
            assertEquals(urnSuffix, ctsCase.getUrnSuffix());
            assertEquals("TEST_CASE_TASK", ctsCase.getCaseTask());
        }
    }

    @Test
    public void testGetToDoListWithUserFilter() {
        assignedUser = "TEST_ASSIGNED_USER";
        //  WebScriptRequest request = mockRequestParams("TEST_ASSIGNED_USER", includeAllowableActions, null, null, null, null, assignedUnits);
        WebScriptRequest request = mockRequestParams();
        params.setAssignedUserNames("TEST_ASSIGNED_USER");
        assignedUser = "TEST_ASSIGNED_USER";

        SearchParameters sp = createSearchParameters(Query.AssignedUserSearchQuery);
        SearchParameters spForTotalResults = createSearchParametersForTR(Query.AssignedUserSearchQuery);
        mockResultsSet("0007", spForTotalResults, sp);

        // when
        ToDoListResponse response = toDoListService.getToDoList(request);

        // then
        // Post-condition Asserts
        assertEquals(1, response.getTotalResults());
        assertNotNull("Missing ctsCases node", response.getCaseList());
        assertEquals(1, response.getCaseList().size());

        for (Map<String, Object> ctsCaseMap : response.getCaseList()) {
            CtsCase ctsCase = (CtsCase) ctsCaseMap.get("case");
            assertNotNull("Missing case child node", ctsCase);
            assertEquals("0007", ctsCase.getUin());
            assertEquals(urnSuffix, ctsCase.getUrnSuffix());
            assertEquals("TEST_ASSIGNED_USER", ctsCase.getAssignedUser());
        }
    }

    @Test
    public void testGetToDoListWithUnitFilter() {
        assignedUnits = "TEST_ASSIGN_UNIT";
        WebScriptRequest request = mockRequestParams();
        params.setAssignedUnits("TEST_ASSIGN_UNIT");

        SearchParameters sp = createSearchParameters(Query.AssignedUnitsSearchQuery);
        SearchParameters spForTotalResults = createSearchParametersForTR(Query.AssignedUnitsSearchQuery);
        mockResultsSet("0008", spForTotalResults, sp);

        // when
        ToDoListResponse response = toDoListService.getToDoList(request);

        // then
        // Post-condition Asserts
        assertEquals(1, response.getTotalResults());
        assertNotNull("Missing ctsCases node", response.getCaseList());
        assertEquals(1, response.getCaseList().size());

        for (Map<String, Object> ctsCaseMap : response.getCaseList()) {
            CtsCase ctsCase = (CtsCase) ctsCaseMap.get("case");
            assertNotNull("Missing case child node", ctsCase);
            assertEquals("0008", ctsCase.getUin());
            assertEquals(urnSuffix, ctsCase.getUrnSuffix());
            assertEquals("TEST_ASSIGN_UNIT", ctsCase.getAssignedUnit());

        }
    }


    @Test
    public void testExecuteWebScriptWithPriorityFilter() {
        filterByPriorityParam = "true";
        WebScriptRequest request = mockRequestParams();
        params.setFilterByPriority(true);

        SearchParameters sp = createSearchParameters(Query.PrioritySearchQuery);
        SearchParameters spForTotalResults = createSearchParametersForTR(Query.PrioritySearchQuery);
        mockResultsSet("0009", spForTotalResults, sp);

        // when
        ToDoListResponse response = toDoListService.getToDoList(request);

        // then
        // Post-condition Asserts
        assertEquals(1, response.getTotalResults());
        assertNotNull("Missing ctsCases node", response.getCaseList());
        assertEquals(1, response.getCaseList().size());

        for (Map<String, Object> ctsCaseMap : response.getCaseList()) {
            CtsCase ctsCase = (CtsCase) ctsCaseMap.get("case");
            assertNotNull("Missing case child node", ctsCase);
            assertEquals("0009", ctsCase.getUin());
            assertEquals(urnSuffix, ctsCase.getUrnSuffix());
            assertEquals(true, ctsCase.getPriority());
        }
    }

    @Test
    public void testGetToDoListAllFieldValues() {

        includeAllowableActions = "false";
        WebScriptRequest request = mockRequestParams();

        SearchParameters sp = createSearchParameters(Query.GlobalSearchQuery);
        SearchParameters spForTotalResults = createSearchParametersForTR(Query.GlobalSearchQuery);
        mockResultsSet("0010", spForTotalResults, sp);

        // when
        ToDoListResponse response = toDoListService.getToDoList(request);

        // then
        // Post-condition Asserts
        assertEquals(1, response.getTotalResults());
        assertNotNull("Missing ctsCases node", response.getCaseList());
        assertEquals(1, response.getCaseList().size());

        for (Map<String, Object> ctsCaseMap : response.getCaseList()) {
            CtsCase ctsCase = (CtsCase) ctsCaseMap.get("case");
            assertNotNull("Missing case child node", ctsCase);
            assertEquals("0010", ctsCase.getUin());
            assertEquals("Test-User-FirstName", ctsCase.getApplicantForename());
            assertEquals("Test-User-SurName", ctsCase.getApplicantSurname());
            assertEquals("OPQ", ctsCase.getCanonicalCorrespondent());
            assertFalse(ctsCase.getIsGroupedSlave());
        }
    }

    @Test
    public void testCanonicalCorrespondent() {
        final NodeRef caseNode = new NodeRef(nodeRefString);
        final Map<QName, Serializable> qNameSerializableMap = mockCaseNodeProperties(caseNode);
        CtsCase ctsCase = toDoListService.getCtsCase(qNameSerializableMap);
        assertEquals("OPQ", ctsCase.getCanonicalCorrespondent());

        //IMCB|IMCM => PROP_REPLY_TO_NAME
        qNameSerializableMap.put(CtsModel.PROP_MEMBER, "Test-reply-to-name");
        qNameSerializableMap.put(CtsModel.PROP_CORRESPONDENT_FORENAME, "Forename");
        qNameSerializableMap.put(CtsModel.PROP_CORRESPONDENT_SURNAME, "Surname");

        qNameSerializableMap.put(CtsModel.PROP_CORRESPONDENCE_TYPE, "IMCB");
        ctsCase = toDoListService.getCtsCase(qNameSerializableMap);
        assertEquals("Test-reply-to-name", ctsCase.getCanonicalCorrespondent());

        qNameSerializableMap.put(CtsModel.PROP_CORRESPONDENCE_TYPE, "IMCM");
        ctsCase = toDoListService.getCtsCase(qNameSerializableMap);
        assertEquals("Test-reply-to-name", ctsCase.getCanonicalCorrespondent());

        qNameSerializableMap.put(CtsModel.PROP_CORRESPONDENCE_TYPE, "FTC");
        ctsCase = toDoListService.getCtsCase(qNameSerializableMap);
        assertEquals("Forename Surname", ctsCase.getCanonicalCorrespondent());

        qNameSerializableMap.put(CtsModel.PROP_CORRESPONDENCE_TYPE, "FTCI");
        ctsCase = toDoListService.getCtsCase(qNameSerializableMap);
        assertEquals("Forename Surname", ctsCase.getCanonicalCorrespondent());

        qNameSerializableMap.put(CtsModel.PROP_CORRESPONDENCE_TYPE, "FSC");
        ctsCase = toDoListService.getCtsCase(qNameSerializableMap);
        assertEquals("Forename Surname", ctsCase.getCanonicalCorrespondent());

        qNameSerializableMap.put(CtsModel.PROP_CORRESPONDENCE_TYPE, "FLT");
        ctsCase = toDoListService.getCtsCase(qNameSerializableMap);
        assertEquals("Forename Surname", ctsCase.getCanonicalCorrespondent());

        qNameSerializableMap.put(CtsModel.PROP_CORRESPONDENCE_TYPE, "FUT");
        ctsCase = toDoListService.getCtsCase(qNameSerializableMap);
        assertEquals("Forename Surname", ctsCase.getCanonicalCorrespondent());

        qNameSerializableMap.put(CtsModel.PROP_CORRESPONDENCE_TYPE, "FTC");
        ctsCase = toDoListService.getCtsCase(qNameSerializableMap);
        assertEquals("Forename Surname", ctsCase.getCanonicalCorrespondent());

        //DTEN|UTEN|NPQ|LPQ|OPQ  => PROP_MEMBER
        qNameSerializableMap.put(CtsModel.PROP_MEMBER, "Test-member");
        qNameSerializableMap.put(CtsModel.PROP_CORRESPONDENCE_TYPE, "DTEN");
        ctsCase = toDoListService.getCtsCase(qNameSerializableMap);
        assertEquals("Test-member", ctsCase.getCanonicalCorrespondent());

        qNameSerializableMap.put(CtsModel.PROP_CORRESPONDENCE_TYPE, "UTEN");
        ctsCase = toDoListService.getCtsCase(qNameSerializableMap);
        assertEquals("Test-member", ctsCase.getCanonicalCorrespondent());

        qNameSerializableMap.put(CtsModel.PROP_CORRESPONDENCE_TYPE, "NPQ");
        ctsCase = toDoListService.getCtsCase(qNameSerializableMap);
        assertEquals("Test-member", ctsCase.getCanonicalCorrespondent());

        qNameSerializableMap.put(CtsModel.PROP_CORRESPONDENCE_TYPE, "LPQ");
        ctsCase = toDoListService.getCtsCase(qNameSerializableMap);
        assertEquals("Test-member", ctsCase.getCanonicalCorrespondent());


        //COM|GEN => PROP_APPLICANT_FORENAME + PROP_APPLICANT_SURNAME if it is null then =>
        qNameSerializableMap.put(CtsModel.PROP_APPLICANT_FORENAME, "Applicant-FirstName");
        qNameSerializableMap.put(CtsModel.PROP_APPLICANT_SURNAME, "Applicant-LastName");
        qNameSerializableMap.put(CtsModel.PROP_CORRESPONDENCE_TYPE, "COM");
        ctsCase = toDoListService.getCtsCase(qNameSerializableMap);
        assertEquals("Applicant-FirstName Applicant-LastName", ctsCase.getCanonicalCorrespondent());

        qNameSerializableMap.put(CtsModel.PROP_CORRESPONDENCE_TYPE, "GEN");
        ctsCase = toDoListService.getCtsCase(qNameSerializableMap);
        assertEquals("Applicant-FirstName Applicant-LastName", ctsCase.getCanonicalCorrespondent());

        qNameSerializableMap.put(CtsModel.PROP_APPLICANT_FORENAME, null);
        qNameSerializableMap.put(CtsModel.PROP_APPLICANT_SURNAME, null);
        qNameSerializableMap.put(CtsModel.PROP_CORRESPONDENT_FORENAME, "Correspondent-FirstName");
        qNameSerializableMap.put(CtsModel.PROP_CORRESPONDENT_SURNAME, "Correspondent-LastName");
        ctsCase = toDoListService.getCtsCase(qNameSerializableMap);
        assertEquals("Correspondent-FirstName Correspondent-LastName", ctsCase.getCanonicalCorrespondent());
    }

    @Test
    public void testCanonicalCorrespondentMinWithNoMinister() {
        final NodeRef caseNode = new NodeRef(nodeRefString);
        final Map<QName, Serializable> qNameSerializableMap = mockCaseNodeProperties(caseNode);

        qNameSerializableMap.put(CtsModel.PROP_MEMBER, null);

        qNameSerializableMap.put(CtsModel.PROP_CORRESPONDENT_FORENAME, "Forename");
        qNameSerializableMap.put(CtsModel.PROP_CORRESPONDENT_SURNAME, "Surname");
        qNameSerializableMap.put(CtsModel.PROP_CORRESPONDENCE_TYPE, "MIN");
        CtsCase ctsCase = toDoListService.getCtsCase(qNameSerializableMap);
        assertEquals("Forename Surname", ctsCase.getCanonicalCorrespondent());
    }

    @Test
    public void testCanonicalCorrespondentMinWithMinister() {
        final NodeRef caseNode = new NodeRef(nodeRefString);
        final Map<QName, Serializable> qNameSerializableMap = mockCaseNodeProperties(caseNode);

        qNameSerializableMap.put(CtsModel.PROP_MEMBER, "Minister Name");

        qNameSerializableMap.put(CtsModel.PROP_CORRESPONDENT_FORENAME, "Forename");
        qNameSerializableMap.put(CtsModel.PROP_CORRESPONDENT_SURNAME, "Surname");
        qNameSerializableMap.put(CtsModel.PROP_CORRESPONDENCE_TYPE, "MIN");
        CtsCase ctsCase = toDoListService.getCtsCase(qNameSerializableMap);
        assertEquals("Minister Name", ctsCase.getCanonicalCorrespondent());
    }

    @Test
    public void testCanonicalCorrespondentTROWithMinister() {
        final NodeRef caseNode = new NodeRef(nodeRefString);
        final Map<QName, Serializable> qNameSerializableMap = mockCaseNodeProperties(caseNode);

        qNameSerializableMap.put(CtsModel.PROP_MEMBER, "Minister Name");

        qNameSerializableMap.put(CtsModel.PROP_CORRESPONDENT_FORENAME, "Forename");
        qNameSerializableMap.put(CtsModel.PROP_CORRESPONDENT_SURNAME, "Surname");
        qNameSerializableMap.put(CtsModel.PROP_CORRESPONDENCE_TYPE, "TRO");
        CtsCase ctsCase = toDoListService.getCtsCase(qNameSerializableMap);
        assertEquals("Minister Name", ctsCase.getCanonicalCorrespondent());
    }

    @Test
    public void testCanonicalCorrespondentFOIWithMinister() {
        final NodeRef caseNode = new NodeRef(nodeRefString);
        final Map<QName, Serializable> qNameSerializableMap = mockCaseNodeProperties(caseNode);

        qNameSerializableMap.put(CtsModel.PROP_MEMBER, "Minister Name");

        qNameSerializableMap.put(CtsModel.PROP_CORRESPONDENT_FORENAME, "Forename");
        qNameSerializableMap.put(CtsModel.PROP_CORRESPONDENT_SURNAME, "Surname");
        qNameSerializableMap.put(CtsModel.PROP_CORRESPONDENCE_TYPE, "FOI");
        CtsCase ctsCase = toDoListService.getCtsCase(qNameSerializableMap);
        assertEquals("Forename Surname", ctsCase.getCanonicalCorrespondent());
    }

    @Test
    public void testCMISDelimiterAndEscaping() {
        List<String> unescapedList = Arrays.asList("xyz", "abc", "Dave O'Geoff");

        String result = toDoListService.createInClauseFilter(unescapedList);
        assertEquals("IN ('xyz','abc','Dave O\\'Geoff')", result);
    }





    private void mockPermissionChecker() {
        permissionChecker = mock(PermissionChecker.class);
        serviceRegistry = mock(ServiceRegistry.class);
        NodeRef caseNode = new NodeRef(nodeRefString);
        TemplateNode node = new TemplateNode(caseNode, serviceRegistry, null);
        when(permissionChecker.hasPermission(node.getNodeRef(), CtsPermissions.ALLOCATE)).thenReturn(true);
        List<CmisExtensionElement> propertyPermissions = new ArrayList<CmisExtensionElement>();
        CmisExtensionElement propertyPermission = new CmisExtensionElementImpl(null, "canUpdateProperties", null, "true");
        propertyPermissions.add(propertyPermission);
        when(permissionChecker.getPropertyPermissions(caseNode)).thenReturn(propertyPermissions);
        String nodeRefString = caseNode.getId();
        AuthorityService as = mock(AuthorityService.class);
        when(permissionChecker.getAuthorityService()).thenReturn(as);
        when(as.authorityExists(nodeRefString)).thenReturn(false);
    }

    private Map<QName, Serializable> mockCaseNodeProperties(NodeRef caseNode) {
        // CaseNode properties
        Map<QName, Serializable> props = new HashMap<>();
        props.put(ContentModel.PROP_TITLE, title);
        props.put(CtsModel.PROP_DOCUMENT_USER, assignedUser);
        props.put(CtsModel.PROP_DOCUMENT_TEAM, group);
        props.put(CtsModel.PROP_DOCUMENT_UNIT, unit);
        props.put(CtsModel.PROP_AUTO_CREATED_CASE, true);
        props.put(CtsModel.PROP_DATE_RECEIVED, new Date());

        // CtsCase properties
        props.put(ContentModel.PROP_NODE_UUID, caseNode.getId());
        props.put(ContentModel.PROP_CREATED, new Date());
        props.put(CtsModel.PROP_CORRESPONDENCE_TYPE, correspondenceType);
        props.put(CtsModel.PROP_CASE_STATUS, caseStatus);
        props.put(CtsModel.PROP_CASE_TASK, caseTasks);
        props.put(CtsModel.PROP_URN_SUFFIX, urnSuffix);
        props.put(CtsModel.PROP_MARKUP_UNIT, "");
        props.put(CtsModel.PROP_MARKUP_TOPIC, "");
        props.put(CtsModel.PROP_SECONDARY_TOPIC, "");
        props.put(CtsModel.PROP_ASSIGNED_UNIT, unit);
        props.put(CtsModel.PROP_ASSIGNED_TEAM, group);
        props.put(CtsModel.PROP_ASSIGNED_USER, assignedUser);
        props.put(CtsModel.PROP_STATUS_UPDATED_DATETIME, new Date());
        props.put(CtsModel.PROP_TASK_UPDATED_DATETIME, new Date());
        props.put(CtsModel.PROP_OWNER_UPDATED_DATETIME, new Date());
        props.put(CtsModel.PROP_CASE_RESPONSE_DEADLINE, new Date());
        props.put(CtsModel.PROP_ADVICE, true);
        props.put(CtsModel.PROP_PRIORITY, true);
        props.put(CtsModel.PROP_REPLY_TO_NAME, "");
        props.put(CtsModel.PROP_CORRESPONDENT_FORENAME, "");
        props.put(CtsModel.PROP_CORRESPONDENT_SURNAME, "");
        props.put(CtsModel.PROP_IS_LINKED_CASE, "false");
        props.put(CtsModel.PROP_FOI_IS_EIR, "false");
        props.put(CtsModel.PROP_HMPO_STAGE, "false");

        // PQ specific
        props.put(CtsModel.PROP_UIN, uin);
        props.put(CtsModel.PROP_MEMBER, "OPQ");
        props.put(CtsModel.PROP_IS_GROUPED_MASTER, "false");
        props.put(CtsModel.PROP_IS_GROUPED_SLAVE, "false");

        // FOI
        props.put(CtsModel.PROP_FOI_IS_EIR, false);


        props.put(CtsModel.PROP_MASTER_NODE_REF, "12345678-9900-4b1c-84e9-8d51f95cceca");
        props.put(CtsModel.PROP_APPLICANT_FORENAME, "Test-User-FirstName");
        props.put(CtsModel.PROP_APPLICANT_SURNAME, "Test-User-SurName");

        return props;
    }

    private ToDoListSearchParams createMockSearchParam() {
        final ToDoListSearchParams params = new ToDoListSearchParams();
        params.setAssignedUserNames(assignedUser);
        params.setPageSize(pageLimit);
        params.setSkipCount(pageOffset);
        params.setIncludeAllowableActions(includeAllowableActions);
        params.setSortingOrder(sortingOrder);
        params.setCaseStatus(caseStatusParam);
        params.setAssignedTeams(assignedTeams);
        params.setCaseTasks(caseTasksProp);
        params.setCaseTypes(caseTypesProp);
        params.setFilterByPriority(Boolean.valueOf(filterByPriorityParam));
        return params;
    }

    private NodeRef addCaseToResultSet(String nodeRefString, String uin) {
        NodeRef firstNode = new NodeRef(nodeRefString);
        addPermissionsForCase(firstNode);
        Map<QName, Serializable> qNameSerializableMap = mockCaseNodeProperties(firstNode);
        qNameSerializableMap.put(CtsModel.PROP_UIN, uin);
        if (correspondenceType != null) {
            qNameSerializableMap.put(CtsModel.PROP_CORRESPONDENCE_TYPE, correspondenceType);
        }
        if (caseStatus != null) {
            qNameSerializableMap.put(CtsModel.PROP_CASE_STATUS, caseStatus);
        }
        if (caseTasks != null) {
            qNameSerializableMap.put(CtsModel.PROP_CASE_TASK, caseTasks);
        }
        if (assignedTeams != null) {
            qNameSerializableMap.put(CtsModel.PROP_ASSIGNED_TEAM, assignedTeams);
        }
        if (assignedUnits != null) {
            qNameSerializableMap.put(CtsModel.PROP_ASSIGNED_UNIT, assignedUnits);
        }
        if (assignedUser != null) {
            qNameSerializableMap.put(CtsModel.PROP_ASSIGNED_USER, assignedUser);
        }
        when(caseMapperHelper.getCaseDisplayStatus(caseStatus)).thenReturn("Draft");
        when(caseMapperHelper.getCaseDisplayTask(caseTasks)).thenReturn("Draft");
        when(nodeService.getProperties(firstNode)).thenReturn(qNameSerializableMap);
        return firstNode;
    }

    private PermissionChecker addPermissionsForCase(NodeRef caseNode) {
        TemplateNode node = new TemplateNode(caseNode, serviceRegistry, null);
        when(permissionChecker.hasPermission(node.getNodeRef(), CtsPermissions.ALLOCATE)).thenReturn(true);
        List<CmisExtensionElement> propertyPermissions = new ArrayList<CmisExtensionElement>();
        CmisExtensionElement propertyPermission = new CmisExtensionElementImpl(null, "canUpdateProperties", null, "true");
        propertyPermissions.add(propertyPermission);
        when(permissionChecker.getPropertyPermissions(caseNode)).thenReturn(propertyPermissions);
        AuthorityService as = mock(AuthorityService.class);
        when(permissionChecker.getAuthorityService()).thenReturn(as);
        when(as.authorityExists(caseNode.getId())).thenReturn(false);
        mockPermissionService(caseNode);
        return permissionChecker;
    }

    private static void createQueryList() {

        final String commonQueryPart = "SELECT c.cmis:objectId FROM cts:case as c JOIN cts:groupedMaster as gm ON gm.cmis:objectId = c.cmis:objectId JOIN cts:groupedSlave as gs ON gs.cmis:objectId = c.cmis:objectId JOIN cts:linkedCase as lc ON lc.cmis:objectId = c.cmis:objectId WHERE c.cts:caseStatus <> 'Completed' AND c.cts:caseStatus <> 'Deleted' AND c.cts:caseTask <> 'Defer' AND gs.cts:isGroupedSlave = 'false' ";
        String endQuery = " ORDER BY c.cts:caseResponseDeadline ASC";
        queryList.put(Query.GlobalSearchQuery, commonQueryPart + endQuery);
        queryList.put(Query.CaseTypeSearchQuery, commonQueryPart + " AND c.cts:correspondenceType IN ('TEST_CASE_TYPE')" + endQuery);
        queryList.put(Query.AssignedTeamsSearchQuery, commonQueryPart + " AND c.cts:assignedTeam IN ('TEST_ASSIGNED_TEAM')" + endQuery);
        queryList.put(Query.CaseStatusSearchQuery, commonQueryPart + " AND c.cts:caseStatus IN ('TEST_CASE_APPROVED')" + endQuery);
        queryList.put(Query.CaseTaskSearchQuery, commonQueryPart + " AND c.cts:caseTask IN ('TEST_CASE_TASK')" + endQuery);
        queryList.put(Query.AssignedUserSearchQuery, commonQueryPart + " AND c.cts:assignedUser IN ('TEST_ASSIGNED_USER')" + endQuery);
        queryList.put(Query.AssignedUnitsSearchQuery, commonQueryPart + " AND c.cts:assignedUnit IN ('TEST_ASSIGN_UNIT')" + endQuery);
        queryList.put(Query.PrioritySearchQuery, commonQueryPart + " AND c.cts:priority = true" + endQuery);
    }

    // Set up a list of CtsCases whose properties we can test with search parameters
    private void mockResultsSet(String uri, SearchParameters spForTotalResults, SearchParameters sp) {
        Mockito.when(iterator.hasNext()).thenReturn(true, false);
        NodeRef firstNode = addCaseToResultSet(nodeRefString, uri);
        when(row.getNodeRef()).thenReturn(firstNode);
        Mockito.when(iterator.next()).thenReturn(row);
        when(searchService.query(spForTotalResults)).thenReturn(resultSetForTotalCount);
        when(searchService.query(sp)).thenReturn(rs);
        when(resultSetForTotalCount.length()).thenReturn(1);
    }

    private void mockResultsSet(SearchParameters spForTotalResults, SearchParameters sp) {
        //always have two records
        Mockito.when(iterator.hasNext()).thenReturn(true, true, false);

        NodeRef firstNode = addCaseToResultSet("workspace://SpacesStore/8737883a-1679-4b1c-84e9-8d51f95cceca", "0001");
        when(row.getNodeRef()).thenReturn(firstNode);

        NodeRef secondNode = addCaseToResultSet("workspace://SpacesStore/12345678-1679-4b1c-84e9-8d51f95cceca", "0002");
        addPermissionsForCase(secondNode);
        when(row.getNodeRef()).thenReturn(secondNode);

        Mockito.when(iterator.next()).thenReturn(row);
        when(searchService.query(sp)).thenReturn(rs);

        when(searchService.query(spForTotalResults)).thenReturn(resultSetForTotalCount);
        when(resultSetForTotalCount.length()).thenReturn(2);

    }

    private PermissionService mockPermissionService(NodeRef caseNode) {
        when(permissionService.hasPermission(caseNode, PermissionService.WRITE_PROPERTIES)).thenReturn(AccessStatus.ALLOWED);
        when(permissionService.hasPermission(caseNode, PermissionService.DELETE)).thenReturn(AccessStatus.ALLOWED);
        when(permissionService.hasPermission(caseNode, PermissionService.READ_CHILDREN)).thenReturn(AccessStatus.ALLOWED);
        when(permissionService.hasPermission(caseNode, PermissionService.READ_PROPERTIES)).thenReturn(AccessStatus.ALLOWED);
        when(permissionService.hasPermission(caseNode, PermissionService.READ_ASSOCIATIONS)).thenReturn(AccessStatus.ALLOWED);
        when(permissionService.hasPermission(caseNode, PermissionService.READ_ASSOCIATIONS)).thenReturn(AccessStatus.ALLOWED);
        when(permissionService.hasPermission(caseNode, PermissionService.READ_ASSOCIATIONS)).thenReturn(AccessStatus.ALLOWED);
        when(permissionService.hasPermission(caseNode, PermissionService.READ)).thenReturn(AccessStatus.ALLOWED);
        when(permissionService.hasPermission(caseNode, PermissionService.WRITE)).thenReturn(AccessStatus.ALLOWED);
        when(permissionService.hasPermission(caseNode, PermissionService.READ_PERMISSIONS)).thenReturn(AccessStatus.ALLOWED);
        when(permissionService.hasPermission(caseNode, PermissionService.READ_CHILDREN)).thenReturn(AccessStatus.ALLOWED);
        when(permissionService.hasPermission(caseNode, PermissionService.ADD_CHILDREN)).thenReturn(AccessStatus.ALLOWED);
        when(permissionService.hasPermission(caseNode, PermissionService.CREATE_CHILDREN)).thenReturn(AccessStatus.ALLOWED);
        when(permissionService.hasPermission(caseNode, PermissionService.CREATE_ASSOCIATIONS)).thenReturn(AccessStatus.ALLOWED);
        when(permissionService.hasPermission(caseNode, PermissionService.DELETE_CHILDREN)).thenReturn(AccessStatus.ALLOWED);
        when(permissionService.hasPermission(caseNode, PermissionService.READ_PERMISSIONS)).thenReturn(AccessStatus.ALLOWED);
        when(permissionService.hasPermission(caseNode, PermissionService.CHANGE_PERMISSIONS)).thenReturn(AccessStatus.ALLOWED);
        return permissionService;
    }


    private SearchParameters createSearchParameters(Query query) {
        SearchParameters sp = new SearchParameters();
        sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
        sp.setLanguage(SearchService.LANGUAGE_CMIS_ALFRESCO);
        sp.setQuery(queryList.get(query));
        sp.setMaxItems(pageLimit);
        sp.setSkipCount(pageOffset);
        return sp;
    }

    private SearchParameters createSearchParametersForTR(Query query) {
        SearchParameters sp = new SearchParameters();
        sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
        sp.setLanguage(SearchService.LANGUAGE_CMIS_ALFRESCO);
        sp.setQuery(queryList.get(query));
        return sp;
    }

    private WebScriptRequest mockRequestParams() {
        WebScriptRequest request = mock(WebScriptRequest.class);
        when(request.getParameter("userNames")).thenReturn(assignedUser);
        when(request.getParameter("limit")).thenReturn("2");
        when(request.getParameter("offset")).thenReturn("1");
        when(request.getParameter("includeAllowableActions")).thenReturn(includeAllowableActions);
        when(request.getParameter("sortingOrder")).thenReturn("ASC");
        when(request.getParameter("caseStatus")).thenReturn(caseStatusParam);
        when(request.getParameter("assignedTeams")).thenReturn(assignedTeams);
        when(request.getParameter("assignedUnits")).thenReturn(assignedUnits);
        when(request.getParameter("caseTasks")).thenReturn(caseTasksProp);
        when(request.getParameter("caseTypes")).thenReturn(caseTypesProp);
        when(request.getParameter("priorityOnly")).thenReturn(filterByPriorityParam);
        return request;
    }
}