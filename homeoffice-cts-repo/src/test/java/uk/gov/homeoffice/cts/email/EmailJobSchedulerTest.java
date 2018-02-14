//package uk.gov.homeoffice.cts.email;
//
//import org.alfresco.model.ContentModel;
//import org.alfresco.service.cmr.repository.ChildAssociationRef;
//import org.alfresco.service.cmr.repository.NodeRef;
//import org.alfresco.service.cmr.repository.NodeService;
//import org.joda.time.DateTime;
//import org.junit.Before;
//import org.junit.Test;
//import org.mockito.InOrder;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.quartz.*;
//import uk.gov.homeoffice.cts.model.CorrespondenceType;
//import uk.gov.homeoffice.cts.model.CtsMail;
//import uk.gov.homeoffice.cts.model.CtsModel;
//
//import java.text.SimpleDateFormat;
//import java.util.*;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNotNull;
//import static org.mockito.Mockito.*;
//
///**
// * A simple class demonstrating how to run out-of-container tests
// * loading Alfresco application context.
// * <p>
// * This class uses the RemoteTestRunner to try and connect to
// * localhost:4578 and send the test name and method to be executed on
// * a running Alfresco. One or more hostnames can be configured in the @Remote
// * annotation.
// * <p>
// * If there is no available remote server to run the test, it falls
// * back on local running of JUnits.
// * <p>
// * Created by dawud on 12/02/2016.
// */
//
//public class EmailJobSchedulerTest {
//
//    private NodeRef caseNode;
//    private NodeRef rootNode;
//    private NodeRef emailNode;
//    private NodeRef scheduledActionsNode;
//    private EmailJobScheduler scheduler;
//    private JobDataMap jobDataMap;
//    private List<NodeRef> emailJobs;
//
//    @Mock private EmailJobService emailJobService;
//    @Mock private NodeService nodeService;
//    @Mock private Properties globalProperties;
//    @Mock JobExecutionContext jobExecutionContext;
//    @Mock JobDetail jobDetail;
//    @Mock Trigger trigger;
//    @Mock private NodeRef.Status status;
//
//
//    // Expected Test Properties
//    private String nodeRefString = "workspace://SpacesStore/myCase";
//    private String rootNodeRefString = "workspace://SpacesStore/companyhome";
//    private String emailJobNodeRefString = "workspace://SpacesStore/companyhome/dictionary/scheduledactions/myEmail";
//    private String scheduledActionsNodeRefString = "workspace://SpacesStore/companyhome/dictionary/scheduledactions";
//
//    // Expected Test Properties
//    private String folderName = "0000001-16";
//    private String title = "Mock Case Document";
//    private String firstName = "firstname_user001";
//    private String surname = "lastname_user001";
//    private String email = "hercule@donotreply.com";
//    private String userName = "username001";
//    private String password = "password";
//    private String group = "GROUP_" + "Parliamentary Questions Team";
//    private String unit = "GROUP_" + "GROUP_Parliamentary Questions";
//    private String correspondenceType = CorrespondenceType.ORDINARY_WRITTEN.getCode();
//    private String caseStatus = "New";
//    private String caseTask = "Create case";
//    private String urnSuffix = "0002016/16";
//    private String uin = "0001";
//    private Date dateReceived = new Date();
//    private Date createdAt = new Date();
//    private Date statusUpdatedDatetime = new Date();
//    private Date taskUpdatedDatetime = new Date();
//    private Date ownerUpdatedDatetime = new Date();
//    private Date opDate = new Date();
//    private Date woDate = new Date();
//    private String templateSubject = "Hercule: You have been allocated a task";
//    private String templateName = "wf-email.html.ftl";
//    private String urlExtension = "/cts/cases/view/myCase";
//    private Map<String, String> templateData = new HashMap<String, String>();
//    private String emailJobName = null;
//
//
//    @Before
//    public void setUp() {
//
//        MockitoAnnotations.initMocks(this);
//        // Setup variables
//        rootNode = new NodeRef(rootNodeRefString);
//        scheduledActionsNode = new NodeRef(scheduledActionsNodeRefString);
//        caseNode = mockCaseNode();
//        emailNode = new NodeRef(emailJobNodeRefString);
//        urlExtension = "/cts/cases/view/" + caseNode.getId();
//        templateData.put("groupName", "Parliamentary Questions Team");
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(CtsMail.MAIL_JOB_NAME_POSTFIX);
//        final String date = simpleDateFormat.format(new Date());
//        emailJobName = CtsMail.MAIL_JOB_NAME_PREFIX + date;
//        emailJobs = new ArrayList<NodeRef>();
//
//        // Mock services
//        when(trigger.getNextFireTime()).thenReturn(DateTime.now().plusMinutes(1).toDate());
//        when(jobExecutionContext.getFireTime()).thenReturn(DateTime.now().toDate());
//
//        // Mock test instance
//        scheduler = spy(new EmailJobScheduler());
//    }
//
//    private NodeRef mockCaseNode() {
//        NodeRef mockCaseNode = new NodeRef(nodeRefString);
//        nodeService = mock(NodeService.class);
//        nodeService.setProperty(mockCaseNode, ContentModel.PROP_NAME, folderName);
//        nodeService.setProperty(mockCaseNode, ContentModel.PROP_TITLE, title);
//        nodeService.setProperty(mockCaseNode, CtsModel.PROP_URN_SUFFIX, urnSuffix);
//        nodeService.setProperty(mockCaseNode, CtsModel.PROP_DOCUMENT_USER, userName);
//        nodeService.setProperty(mockCaseNode, CtsModel.PROP_DOCUMENT_TEAM, group);
//        nodeService.setProperty(mockCaseNode, CtsModel.PROP_DOCUMENT_UNIT, unit);
//        return mockCaseNode;
//    }
//
//    @Test
//    public void testEmailJobSchedulerDisabled() throws JobExecutionException {
//        // given
//        jobDataMap = new JobDataMap();
//        jobDataMap.put("mailJobSchedulerEnabled", "false");
//        jobDetail.setJobDataMap(jobDataMap);
//        when(jobExecutionContext.getJobDetail()).thenReturn(jobDetail);
//        when(jobDetail.getJobDataMap()).thenReturn(jobDataMap);
//        emailJobs.add(emailNode);
//        scheduler.setNodeService(nodeService);
//        scheduler.setEmailJobService(emailJobService);
//
//        // when
//        scheduler.execute(jobExecutionContext);
//
//        // then
//        verify(scheduler, never()).doWorkOnEmailJobs();
//        verify(scheduler, never()).fetchAllEmailJobs();
//        verify(emailJobService, never()).sendEmailJob(emailNode);
//        verify(nodeService, never()).deleteNode(emailNode);
//    }
//
//    @Test
//    public void testEmailJobSchedulerEnabledAndStatusUnknown() throws JobExecutionException {
//        // given
//        jobDataMap = new JobDataMap();
//        jobDataMap.put("mailJobSchedulerEnabled", "true");
//        jobDetail.setJobDataMap(jobDataMap);
//        when(jobExecutionContext.getJobDetail()).thenReturn(jobDetail);
//        when(jobDetail.getJobDataMap()).thenReturn(jobDataMap);
//        emailJobs.add(emailNode);
//        doReturn(emailJobs).when(scheduler).fetchAllEmailJobs();
//        scheduler.setNodeService(nodeService);
//        scheduler.setEmailJobService(emailJobService);
//
//        // when
//        scheduler.execute(jobExecutionContext);
//
//        // then
//        InOrder inOrder = inOrder(scheduler);
//        inOrder.verify(scheduler).doWorkOnEmailJobs();
//        inOrder.verify(scheduler).fetchAllEmailJobs();
//        verify(nodeService, never()).deleteNode(emailNode);
//        verify(emailJobService, never()).sendEmailJob(emailNode);
//    }
//
//    @Test
//    public void testEmailJobSchedulerEnabledAndStatusSent() throws JobExecutionException {
//        // given
//        jobDataMap = new JobDataMap();
//        jobDataMap.put("mailJobSchedulerEnabled", "true");
//        jobDataMap.put("mailClearSentDuration", "PT0H");
//        jobDetail.setJobDataMap(jobDataMap);
//        when(jobExecutionContext.getJobDetail()).thenReturn(jobDetail);
//        when(jobDetail.getJobDataMap()).thenReturn(jobDataMap);
//        when(nodeService.getProperty(emailNode, CtsMail.PROP_STATUS)).thenReturn(CtsMail.MAIL_RESPONSE_STATUS_SENT);
//        when(nodeService.getProperty(emailNode, ContentModel.PROP_SENTDATE)).thenReturn(DateTime.now().minusHours(2).toDate());
//        emailJobs.add(emailNode);
//        doReturn(emailJobs).when(scheduler).fetchAllEmailJobs();
//        scheduler.setNodeService(nodeService);
//        scheduler.setEmailJobService(emailJobService);
//
//        // when
//        scheduler.execute(jobExecutionContext);
//
//        // then
//        InOrder inOrder = inOrder(scheduler);
//        inOrder.verify(scheduler).doWorkOnEmailJobs();
//        inOrder.verify(scheduler).fetchAllEmailJobs();
//
//        assert (nodeService.getProperty(emailNode, CtsMail.PROP_STATUS).toString().equals(CtsMail.MAIL_RESPONSE_STATUS_SENT));
//        verify(nodeService).deleteNode(emailNode);
//        verify(emailJobService, never()).sendEmailJob(emailNode);
//    }
//
//    @Test
//    public void testEmailJobSchedulerEnabledAndStatusFail() throws JobExecutionException {
//        // given
//        jobDataMap = new JobDataMap();
//        jobDataMap.put("mailJobSchedulerEnabled", "true");
//        jobDataMap.put("mailClearFailedDuration", "PT1H");
//        jobDetail.setJobDataMap(jobDataMap);
//        when(jobExecutionContext.getJobDetail()).thenReturn(jobDetail);
//        when(jobDetail.getJobDataMap()).thenReturn(jobDataMap);
//        when(nodeService.getProperty(emailNode, CtsMail.PROP_STATUS)).thenReturn(CtsMail.MAIL_RESPONSE_STATUS_FAIL);
//        when(nodeService.getProperty(emailNode, ContentModel.PROP_CREATED)).thenReturn(DateTime.now().minusHours(2).toDate());
//        emailJobs.add(emailNode);
//        doReturn(emailJobs).when(scheduler).fetchAllEmailJobs();
//        scheduler.setNodeService(nodeService);
//        scheduler.setEmailJobService(emailJobService);
//
//        // when
//        scheduler.execute(jobExecutionContext);
//
//        // then
//        InOrder inOrder = inOrder(scheduler);
//        inOrder.verify(scheduler).doWorkOnEmailJobs();
//        inOrder.verify(scheduler).fetchAllEmailJobs();
//
//        assert(nodeService.getProperty(emailNode, CtsMail.PROP_STATUS).toString().equals(CtsMail.MAIL_RESPONSE_STATUS_FAIL));
//        verify(nodeService).deleteNode(emailNode);
//        verify(emailJobService, never()).sendEmailJob(emailNode);
//    }
//
//    @Test
//    public void testEmailJobSchedulerEnabledAndStatusRetry() throws JobExecutionException {
//        // given
//        jobDataMap = new JobDataMap();
//        jobDataMap.put("mailJobSchedulerEnabled", "true");
//        jobDataMap.put("mailRetryDuration", "PT1H");
//        jobDataMap.put("mailMaxRetries", "1");
//        jobDetail.setJobDataMap(jobDataMap);
//        when(jobExecutionContext.getJobDetail()).thenReturn(jobDetail);
//        when(jobDetail.getJobDataMap()).thenReturn(jobDataMap);
//        when(nodeService.getProperty(emailNode, CtsMail.PROP_STATUS)).thenReturn(CtsMail.MAIL_RESPONSE_STATUS_RETRY);
//        when(nodeService.getProperty(emailNode, ContentModel.PROP_CREATED)).thenReturn(DateTime.now().minusHours(2).toDate());
//        emailJobs.add(emailNode);
//        doReturn(emailJobs).when(scheduler).fetchAllEmailJobs();
//        scheduler.setNodeService(nodeService);
//        scheduler.setEmailJobService(emailJobService);
//
//        // when
//        scheduler.execute(jobExecutionContext);
//
//        // then
//        InOrder inOrder = inOrder(scheduler);
//        inOrder.verify(scheduler).doWorkOnEmailJobs();
//        inOrder.verify(scheduler).fetchAllEmailJobs();
//
//        assert (nodeService.getProperty(emailNode, CtsMail.PROP_STATUS).toString().equals(CtsMail.MAIL_RESPONSE_STATUS_RETRY));
//        verify(emailJobService).sendEmailJob(emailNode);
//    }
//
//
//    @Test
//    public void testFetchAllEmailJobs() {
//        //given
//        when(emailJobService.getScheduledActionsFolder()).thenReturn(scheduledActionsNode);
//        ChildAssociationRef association = mock(ChildAssociationRef.class);
//        List<ChildAssociationRef> children = new ArrayList<ChildAssociationRef>();
//        children.add(association);
//        when(nodeService.getNodeStatus(emailNode)).thenReturn(status);
//        when(status.isDeleted()).thenReturn(false);
//        when(nodeService.getChildAssocs(scheduledActionsNode)).thenReturn(children);
//        when(association.getChildRef()).thenReturn(emailNode);
//        when(nodeService.hasAspect(emailNode, ContentModel.ASPECT_EMAILED)).thenReturn(true);
//        emailJobs.add(emailNode);
//        scheduler.setNodeService(nodeService);
//        scheduler.setEmailJobService(emailJobService);
//
//        // when
//        List<NodeRef> list = scheduler.fetchAllEmailJobs();
//
//        // then
//        assertNotNull(emailJobService.getScheduledActionsFolder());
//        assertEquals("EmailJob should have one item", 1, list.size());
//    }
//}
//
