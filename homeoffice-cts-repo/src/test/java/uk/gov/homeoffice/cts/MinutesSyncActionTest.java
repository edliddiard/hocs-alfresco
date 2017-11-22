package uk.gov.homeoffice.cts;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.model.Repository;
import org.alfresco.repo.policy.BehaviourFilter;
import org.alfresco.service.cmr.audit.AuditService;
import org.alfresco.service.cmr.repository.*;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.ISO8601DateFormat;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import uk.gov.homeoffice.cts.action.MinutesSyncAction;
import uk.gov.homeoffice.cts.helpers.CtsFolderHelper;
import uk.gov.homeoffice.cts.model.CtsMinute;
import uk.gov.homeoffice.cts.model.CtsModel;
import uk.gov.homeoffice.cts.service.ManualMinutesService;
import uk.gov.homeoffice.cts.service.SystemMinutesService;
import uk.gov.homeoffice.ctsv2.model.CtsMinuteModel;

import java.io.Serializable;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;


/**
 * Created by dawud on 21/06/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class MinutesSyncActionTest {

    private NodeRef caseNode;
    private String mockTestMinutesCollatedJSONStr;
    // Expected Test Properties
    private String nodeRefString = "workspace://SpacesStore/myCase";
    private String name = "Add Minute Aspect Test (" + System.currentTimeMillis() + ")";
    private String title = "Mock Case Document";
    private String userName = "username001";
    private String group = "GROUP_" + "Parliamentary Questions Team";
    private String unit = "GROUP_" + "GROUP_Parliamentary Questions";
    private String urnSuffix = "0002016/16";
    private String time  = ISO8601DateFormat.format(getMockDate());


    //set mock objects
    @Mock
    private NodeService nodeService;
    @Mock
    private Repository repository;
    @Mock
    private ContentService contentService;
    @Mock
    private CtsFolderHelper ctsFolderHelper;
    @Mock
    private ContentReader reader;
    @Mock
    private ContentWriter contentWriter;
    @Mock
    private WebScriptRequest request;
    @Mock
    private WebScriptResponse response;
    @Mock
    private SystemMinutesService systemMinutesService;
    @Mock
    private ManualMinutesService manualMinutesService;

    @Mock NodeRef.Status status;

    private MinutesSyncAction minutesSyncAction;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        minutesSyncAction = new MinutesSyncAction();

        // Setup variables
        caseNode = mockCaseNode();
        mockTestMinutesCollatedJSONStr = mockMinutesCollated();
        
        // Mock services
        mockSystemMinutes();
        mockManualMinutes();

        // Mock test instance

        minutesSyncAction.setSystemMinutesService(systemMinutesService);
        minutesSyncAction.setManualMinutesService(manualMinutesService);

        minutesSyncAction.setContentService(contentService);
        minutesSyncAction.setAuditService(mock(AuditService.class));
        minutesSyncAction.setBehaviourFilter(mock(BehaviourFilter.class));
        minutesSyncAction.setNodeService(nodeService);

        caseNode = new NodeRef(nodeRefString);
        when(reader.getContentString()).thenReturn(mockTestMinutesCollatedJSONStr);
        when(contentService.getReader(caseNode, CtsMinuteModel.PROP_MINUTE_COLLATED)).thenReturn(reader);

        when(nodeService.hasAspect(caseNode, CtsMinuteModel.ASPECT_MINUTE)).thenReturn(false);
        when(contentService.getWriter(caseNode, CtsMinuteModel.PROP_MINUTE_COLLATED, true)).thenReturn(contentWriter);

    }


    @Test
    public void testSynchroniseMinutesInitialSyncWithMinutes() {

        when(nodeService.getNodeStatus(caseNode)).thenReturn(status);
        when(status.isDeleted()).thenReturn(false);
        // when
        minutesSyncAction.synchroniseMinutes(caseNode);

        // then
        verifyConentUpdated();
    }

    @Test
    public void whenUpdateMinuteProperty() {

        // when
        minutesSyncAction.updateMinuteProperty(caseNode);

        // then
        verifyConentUpdated();
    }


    @Test
    public void whenGetMinutesCollatedJSONStr() {

        // when
        String result = minutesSyncAction.getMinutesCollatedJSONStr(caseNode);

        // then
        assertEquals(mockTestMinutesCollatedJSONStr, result);
    }

    @Test
    public void whenGetMinutesCollated() {

        // when
        List<uk.gov.homeoffice.ctsv2.model.CtsMinute> result = minutesSyncAction.getMinutesCollated(caseNode);

        // then
        verify(systemMinutesService).getSystemMinutes(caseNode);
        verify(manualMinutesService).getManualMinutes(caseNode);
        assertEquals(2, result.size());
    }

    @Test
    public void toCtsMinuteV2List() {
        // given
        List<CtsMinute> minutes = systemMinutesService.getSystemMinutes(caseNode);
        List<CtsMinute> manualMinutes = manualMinutesService.getManualMinutes(caseNode);
        List<CtsMinute> completeList = new ArrayList<>();
        completeList.addAll(minutes);
        completeList.addAll(manualMinutes);
        Collections.sort(completeList, Collections.reverseOrder());

        // when
        List<uk.gov.homeoffice.ctsv2.model.CtsMinute> result = minutesSyncAction.toCtsMinuteV2List(completeList);

        // then
        Iterator before = completeList.iterator();
        Iterator after = result.iterator();
        while (before.hasNext() && after.hasNext()) {
            Object beforeCase = before.next();
            Object afterCase = after.next();
            assertTrue("Pre-condition - Should be of type CtsMinute v1", beforeCase instanceof CtsMinute);
            assertTrue("Post condition - Should be of type CtsMinute v2", afterCase instanceof uk.gov.homeoffice.ctsv2.model.CtsMinute);
            assertCaseMinutes((CtsMinute)beforeCase, (uk.gov.homeoffice.ctsv2.model.CtsMinute)afterCase);
        }
    }

    private void assertCaseMinutes(CtsMinute beforeCase, uk.gov.homeoffice.ctsv2.model.CtsMinute afterCase) {
        assertEquals(beforeCase.getDbid(), afterCase.getDbid());
        assertEquals(beforeCase.getMinuteType(), afterCase.getMinuteType());
        assertEquals(beforeCase.getText(), afterCase.getText());
        assertEquals(beforeCase.getMinuteQaReviewOutcomes(), afterCase.getMinuteQaReviewOutcomes());
    }

    private String mockMinutesCollated() {
        return "{\"minutes\":[{\"dbid\":1111,\"minuteDateTime\":\"" + time + "\",\"minuteContent\":\"Case Created\",\"minuteUpdatedBy\":\"SystemUser\",\"minuteType\":\"system\"},{\"dbid\":1112,\"minuteDateTime\":\"" + time + "\",\"minuteContent\":\"Case Comment\",\"minuteUpdatedBy\":\"ManualUser\",\"minuteType\":\"manual\"}]}";
    }

    private NodeRef mockCaseNode() {
        NodeRef mockCaseNode = new NodeRef(nodeRefString);
        nodeService.setProperty(mockCaseNode, ContentModel.PROP_NAME, name);
        nodeService.setProperty(mockCaseNode, ContentModel.PROP_TITLE, title);
        nodeService.setProperty(mockCaseNode, CtsModel.PROP_URN_SUFFIX, urnSuffix);
        nodeService.setProperty(mockCaseNode, CtsModel.PROP_DOCUMENT_USER, userName);
        nodeService.setProperty(mockCaseNode, CtsModel.PROP_DOCUMENT_TEAM, group);
        nodeService.setProperty(mockCaseNode, CtsModel.PROP_DOCUMENT_UNIT, unit);
        return mockCaseNode;
    }

    private void mockSystemMinutes() {
        List<CtsMinute> systemMinutes = Collections.singletonList(new CtsMinute(1111L, getMockDate(), "Case Created", "SystemUser", "system"));
        when(systemMinutesService.getSystemMinutes(caseNode)).thenReturn(systemMinutes);
    }

    private void mockManualMinutes() {
        List<CtsMinute> manualMinutes = Collections.singletonList(new CtsMinute(1112L, getMockDate(), "Case Comment", "ManualUser", "manual"));
        when(manualMinutesService.getManualMinutes(caseNode)).thenReturn(manualMinutes);
    }

    private Date getMockDate() {
        Calendar calendar = new GregorianCalendar(2016, 5, 15, 1, 23, 45);
        return calendar.getTime();
    }

    private void verifyConentUpdated() {
        verify(nodeService).addAspect(caseNode, CtsMinuteModel.ASPECT_MINUTE, Collections.<QName, Serializable>emptyMap());
        verify(contentService).getWriter(caseNode, CtsMinuteModel.PROP_MINUTE_COLLATED, true);
        verify(contentWriter).putContent(mockTestMinutesCollatedJSONStr);
    }


}
