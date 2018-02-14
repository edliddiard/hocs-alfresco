//package uk.gov.homeoffice.cts.email;
//
//import org.alfresco.model.ContentModel;
//import org.alfresco.service.cmr.action.Action;
//import org.alfresco.service.cmr.action.ActionService;
//import org.alfresco.service.cmr.repository.NodeRef;
//import org.alfresco.service.cmr.repository.NodeService;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.Mock;
//import org.mockito.runners.MockitoJUnitRunner;
//import uk.gov.homeoffice.cts.model.CtsMail;
//import uk.gov.homeoffice.cts.model.CtsModel;
//
//import java.io.Serializable;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Properties;
//
//import static org.mockito.Mockito.*;
//
///**
// * Created by dawud on 10/06/2016.
// */
//@RunWith(MockitoJUnitRunner.class)
//public class EmailJobActionExecuterTest {
//    private NodeRef caseNode;
//    private EmailJobService emailJobService;
//    private EmailService emailService;
//    private EmailJobActionExecuter executer;
//
//    @Mock
//    private Action action;
//    @Mock
//    private NodeService nodeService;
//    @Mock
//    private Properties globalProperties;
//    @Mock
//    private ActionService actionService;
//
//    // Expected Test Properties
//    private String nodeRefString = "workspace://SpacesStore/myCase";
//    private String folderName = "0000001-16";
//    private String title = "Mock Case Document";
//    private String userName = "username001";
//    private String group = "GROUP_" + "Parliamentary Questions Team";
//    private String unit = "GROUP_" + "GROUP_Parliamentary Questions";
//    private String urnSuffix = "0002016/16";
//    private String templateSubject = "Hercule: You have been allocated a task";
//    private String templateName = "wf-email.html.ftl";
//    private String urlExtension = "/cts/cases/view/myCase";
//    private Map<String, String> templateData = new HashMap<String, String>();
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
//    @Before
//    public void setUp() {
//        // Setup variables
//        caseNode = mockCaseNode();
//        urlExtension = "/cts/cases/view/" + caseNode.getId();
//        templateData.put("groupName", "Parliamentary Questions Team");
//
//        // Mock services
//        nodeService = mock(NodeService.class);
//        when(nodeService.exists(caseNode)).thenReturn(true);
//        when(action.getParameterValue(EmailJobActionExecuter.PARAM_USERNAME)).thenReturn(userName);
//        when(action.getParameterValue(EmailJobActionExecuter.PARAM_TEMPLATE_SUBJECT)).thenReturn(templateSubject);
//        when(action.getParameterValue(EmailJobActionExecuter.PARAM_TEMPLATE)).thenReturn(templateName);
//        when(action.getParameterValue(EmailJobActionExecuter.PARAM_URL_EXTENSION)).thenReturn(urlExtension);
//        when(action.getParameterValue(EmailJobActionExecuter.PARAM_CASE_NODE_REF)).thenReturn(caseNode);
//        when(action.getParameterValue(EmailJobActionExecuter.PARAM_TEMPLATE_DATA)).thenReturn((Serializable) templateData);
//        actionService.createAction(EmailJobActionExecuter.NAME);
//        actionService.executeAction(action, caseNode, false, true);
//        globalProperties = new Properties();
//        emailService = mock(EmailService.class);
//        emailJobService = mock(EmailJobService.class);
//
//        // Mock test instance
//        executer = new EmailJobActionExecuter();
//        executer.setActionService(actionService);
//        executer.setNodeService(nodeService);
//        executer.setEmailService(emailService);
//        executer.setEmailJobService(emailJobService);
//    }
//
//    @Test
//    public void testEmailJobActionWhenSchedulerDisabled() {
//        // given
//        globalProperties.put(CtsMail.PROP_MAIL_JOB_SCHEDULER_ENABLED, "false");
//        globalProperties.put(CtsMail.PROP_MAIL_CLEAR_SENT_DURATION, "PT0H");
//        globalProperties.put(CtsMail.PROP_MAIL_MAX_RETRIES, "0");
//        executer.setGlobalProperties(globalProperties);
//        executer.init();
//
//        // when
//        executer.executeImpl(action, caseNode);
//
//        // then
//        verify(emailService).sendEmail(userName, templateSubject, templateName, urlExtension, caseNode, templateData);
//        verify(emailJobService, never()).saveEmailJob(userName, templateSubject, templateName, urlExtension, caseNode, templateData, CtsMail.MAIL_RESPONSE_STATUS_SENT);
//        verify(emailJobService, never()).saveEmailJob(userName, templateSubject, templateName, urlExtension, caseNode, templateData, CtsMail.MAIL_RESPONSE_STATUS_RETRY);
//    }
//
//    @Test
//    public void testEmailJobActionSendOnce() {
//        // given
//        globalProperties.put(CtsMail.PROP_MAIL_JOB_SCHEDULER_ENABLED, "true");
//        globalProperties.put(CtsMail.PROP_MAIL_CLEAR_SENT_DURATION, "PT0H");
//        globalProperties.put(CtsMail.PROP_MAIL_MAX_RETRIES, "0");
//        executer.setGlobalProperties(globalProperties);
//        executer.init();
//
//        // when
//        executer.executeImpl(action, caseNode);
//
//        // then
//        verify(emailService).sendEmail(userName, templateSubject, templateName, urlExtension, caseNode, templateData);
//        verify(emailJobService, never()).saveEmailJob(userName, templateSubject, templateName, urlExtension, caseNode, templateData, CtsMail.MAIL_RESPONSE_STATUS_SENT);
//        verify(emailJobService, never()).saveEmailJob(userName, templateSubject, templateName, urlExtension, caseNode, templateData, CtsMail.MAIL_RESPONSE_STATUS_RETRY);
//    }
//
//    @Test
//    public void testEmailJobActionSendOnceAndSaveSentStatus() {
//        // given
//        globalProperties.put(CtsMail.PROP_MAIL_JOB_SCHEDULER_ENABLED, "true");
//        globalProperties.put(CtsMail.PROP_MAIL_CLEAR_SENT_DURATION, "PT1H");
//        globalProperties.put(CtsMail.PROP_MAIL_MAX_RETRIES, "0");
//        executer.setGlobalProperties(globalProperties);
//        executer.init();
//
//        // when
//        executer.executeImpl(action, caseNode);
//
//        // then
//        verify(emailService).sendEmail(userName, templateSubject, templateName, urlExtension, caseNode, templateData);
//        verify(emailJobService, atLeastOnce()).saveEmailJob(userName, templateSubject, templateName, urlExtension, caseNode, templateData, CtsMail.MAIL_RESPONSE_STATUS_SENT);
//        verify(emailJobService, never()).saveEmailJob(userName, templateSubject, templateName, urlExtension, caseNode, templateData, CtsMail.MAIL_RESPONSE_STATUS_RETRY);
//    }
//
//
//    @Test
//    public void testEmailJobActionSendAndFailStatus() {
//        // given
//        globalProperties.put(CtsMail.PROP_MAIL_JOB_SCHEDULER_ENABLED, "true");
//        globalProperties.put(CtsMail.PROP_MAIL_CLEAR_SENT_DURATION, "PT1H");
//        globalProperties.put(CtsMail.PROP_MAIL_MAX_RETRIES, "0");
//        executer.setGlobalProperties(globalProperties);
//        executer.init();
//        doThrow(new RuntimeException("Mail Exception")).when(emailService).sendEmail(userName, templateSubject, templateName, urlExtension, caseNode, templateData);
//
//        // when
//        executer.executeImpl(action, caseNode);
//
//        // then
//        verify(emailService).sendEmail(userName, templateSubject, templateName, urlExtension, caseNode, templateData);
//        verify(emailJobService).saveEmailJob(userName, templateSubject, templateName, urlExtension, caseNode, templateData, CtsMail.MAIL_RESPONSE_STATUS_FAIL, new RuntimeException("Mail Exception").getMessage());
//        verify(emailJobService, never()).saveEmailJob(userName, templateSubject, templateName, urlExtension, caseNode, templateData, CtsMail.MAIL_RESPONSE_STATUS_RETRY, new RuntimeException("Mail Exception").getMessage());
//    }
//
//    @Test
//    public void testEmailJobActionSendAndRetryStatus() {
//        // given
//        globalProperties.put(CtsMail.PROP_MAIL_JOB_SCHEDULER_ENABLED, "true");
//        globalProperties.put(CtsMail.PROP_MAIL_CLEAR_SENT_DURATION, "PT1H");
//        globalProperties.put(CtsMail.PROP_MAIL_MAX_RETRIES, "1");
//        executer.setGlobalProperties(globalProperties);
//        executer.init();
//        doThrow(new RuntimeException("Mail Exception")).when(emailService).sendEmail(userName, templateSubject, templateName, urlExtension, caseNode, templateData);
//
//        // when
//        executer.executeImpl(action, caseNode);
//
//        // then
//        verify(emailService).sendEmail(userName, templateSubject, templateName, urlExtension, caseNode, templateData);
//        verify(emailJobService, never()).saveEmailJob(userName, templateSubject, templateName, urlExtension, caseNode, templateData, CtsMail.MAIL_RESPONSE_STATUS_FAIL);
//        verify(emailJobService, atLeastOnce()).saveEmailJob(userName, templateSubject, templateName, urlExtension, caseNode, templateData, CtsMail.MAIL_RESPONSE_STATUS_RETRY, new RuntimeException("Mail Exception").getMessage());
//    }
//
//}
