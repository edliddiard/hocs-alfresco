package uk.gov.homeoffice.cts.email;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.action.ActionImpl;
import org.alfresco.repo.nodelocator.NodeLocatorService;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ActionService;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.subethamail.wiser.Wiser;
import uk.gov.homeoffice.cts.model.CorrespondenceType;
import uk.gov.homeoffice.cts.model.CtsMail;
import uk.gov.homeoffice.cts.model.CtsModel;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by dawud rahman on 19/02/2016.
 */
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration("classpath:alfresco/application-context.xml")
@RunWith(MockitoJUnitRunner.class)
public class EmailJobServiceTest {

    private NodeRef caseNode;
    private NodeRef rootNode;
    private NodeRef emailNode;
    private NodeRef scheduledActionsNode;
    private NodeRef personNode;

    private AssociationRef caseAssociation;
    private List<AssociationRef> children;
    private EmailJobService emailJobService;
    private EmailService emailService;

    @Mock
    private NodeService nodeService;
    @Mock
    private Properties globalProperties;
    @Mock
    private NodeLocatorService nodeLocatorService;
    @Mock
    private ActionService actionService;

    // Expected Test Properties
    private String nodeRefString = "workspace://SpacesStore/myCase";
    private String rootNodeRefString = "workspace://SpacesStore/companyhome";
    private String emailJobNodeRefString = "workspace://SpacesStore/companyhome/dictionary/scheduledactions/myEmail";
    private String scheduledActionsNodeRefString = "workspace://SpacesStore/companyhome/dictionary/scheduledactions";
    private String personNodeRefString = "workspace://SpacesStore/users/myPerson";

    // Expected Test Properties
    private String folderName = "0000001-16";
    private String title = "Mock Case Document";
    private String firstName = "firstname_user001";
    private String surname = "lastname_user001";
    private String email = "hercule@donotreply.com";
    private String userName = "username001";
    private String password = "password";
    private String group = "GROUP_" + "Parliamentary Questions Team";
    private String unit = "GROUP_" + "GROUP_Parliamentary Questions";
    private String correspondenceType = CorrespondenceType.ORDINARY_WRITTEN.getCode();
    private String caseStatus = "New";
    private String caseTask = "Create case";
    private String urnSuffix = "0002016/16";
    private String uin = "0001";
    private Date dateReceived = new Date();
    private Date createdAt = new Date();
    private Date statusUpdatedDatetime = new Date();
    private Date taskUpdatedDatetime = new Date();
    private Date ownerUpdatedDatetime = new Date();
    private Date sentDate = new Date();
    private Date opDate = new Date();
    private Date woDate = new Date();
    private String templateSubject = "Hercule: You have been allocated a task";
    private String templateName = "wf-email.html.ftl";
    private String urlExtension = "/cts/cases/view/myCase";
    private Map<String, String> templateData = new HashMap<String, String>();
    private String emailJobName = null;

    /**
     * see https://github.com/Alfresco/community-edition/blob/V4.2e/root/projects/remote-api/source/test-java/org/alfresco/repo/webdav/MoveMethodTest.java
     * http://blog.metasys.pl/category/alfresco/
      */

    @Before
    public void setUp() throws Exception {

        // Setup variables
        rootNode = new NodeRef(rootNodeRefString);
        scheduledActionsNode = new NodeRef(scheduledActionsNodeRefString);
        caseNode = mockCaseNode();
        emailNode = new NodeRef(emailJobNodeRefString);
        templateData.put("groupName", "Parliamentary Questions Team");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(CtsMail.MAIL_JOB_NAME_POSTFIX);
        final String date = simpleDateFormat.format(new Date());
        emailJobName = CtsMail.MAIL_JOB_NAME_PREFIX + date;
        ChildAssociationRef association = nodeService.createNode(
                scheduledActionsNode,
                ContentModel.ASSOC_CONTAINS,
                QName.createQName(NamespaceService.CONTENT_MODEL_PREFIX, emailJobName),
                ContentModel.TYPE_FOLDER,
                mockEmailJobNodeProperties()
        );
        emailService = mock(EmailService.class);
        globalProperties = new Properties();
        nodeService = mock(NodeService.class);

        caseAssociation = mock(AssociationRef.class);
        when(caseAssociation.getTargetRef()).thenReturn(caseNode);
        children = new ArrayList<AssociationRef>();
        children.add(caseAssociation);
    }

    private NodeRef mockCaseNode() {
        NodeRef mockCaseNode = new NodeRef(nodeRefString);
        nodeService = mock(NodeService.class);
        nodeService.setProperty(mockCaseNode, ContentModel.PROP_NAME, folderName);
        nodeService.setProperty(mockCaseNode, ContentModel.PROP_TITLE, title);
        nodeService.setProperty(mockCaseNode, CtsModel.PROP_URN_SUFFIX, urnSuffix);
        nodeService.setProperty(mockCaseNode, CtsModel.PROP_DOCUMENT_USER, userName);
        nodeService.setProperty(mockCaseNode, CtsModel.PROP_DOCUMENT_TEAM, group);
        nodeService.setProperty(mockCaseNode, CtsModel.PROP_DOCUMENT_UNIT, unit);
        return mockCaseNode;
    }

    private Map<QName, Serializable> mockEmailJobNodeProperties() {
        // CaseNode properties
        Map<QName, Serializable> props = new HashMap();
        props.put(ContentModel.PROP_TITLE, title);
        props.put(CtsModel.PROP_DOCUMENT_USER, userName);
        props.put(CtsModel.PROP_DOCUMENT_TEAM, group);
        props.put(CtsModel.PROP_DOCUMENT_UNIT, unit);
        props.put(CtsModel.PROP_AUTO_CREATED_CASE, true);
        props.put(CtsModel.PROP_DATE_RECEIVED, dateReceived);

        // EmailJob Node properties
        props.put(ContentModel.PROP_USERNAME, userName);
        props.put(CtsMail.PARAM_SUBJECT, templateSubject);
        props.put(CtsMail.PARAM_TEMPLATE, templateName);
        props.put(CtsMail.PARAM_TEMPLATE_MODEL, (Serializable) templateData);
        props.put(CtsMail.PROP_CASE_URL, urlExtension);
        props.put(ContentModel.PROP_SENTDATE, sentDate);

        // CtsCase properties
        props.put(ContentModel.PROP_NAME, folderName);
        props.put(ContentModel.PROP_NODE_UUID, caseNode.getId());
        props.put(ContentModel.PROP_CREATED, createdAt);
        props.put(CtsModel.PROP_CORRESPONDENCE_TYPE, correspondenceType);
        props.put(CtsModel.PROP_CASE_STATUS, caseStatus);
        props.put(CtsModel.PROP_CASE_TASK, caseTask);
        props.put(CtsModel.PROP_URN_SUFFIX, urnSuffix);
        props.put(CtsModel.PROP_MARKUP_DECISION, "");
        props.put(CtsModel.PROP_MARKUP_UNIT, "");
        props.put(CtsModel.PROP_MARKUP_TOPIC, "");
        props.put(CtsModel.PROP_MARKUP_MINISTER, "");
        props.put(CtsModel.PROP_SECONDARY_TOPIC, "");
        props.put(CtsModel.PROP_ASSIGNED_UNIT, unit);
        props.put(CtsModel.PROP_ASSIGNED_TEAM, group);
        props.put(CtsModel.PROP_ASSIGNED_USER, userName);
        props.put(CtsModel.PROP_ORIGINAL_DRAFTER_UNIT, unit);
        props.put(CtsModel.PROP_ORIGINAL_DRAFTER_TEAM, group);
        props.put(CtsModel.PROP_ORIGINAL_DRAFTER_USER, userName);
        props.put(CtsModel.PROP_IS_LINKED_CASE, false);
        props.put(CtsModel.PROP_STATUS_UPDATED_DATETIME,  statusUpdatedDatetime);
        props.put(CtsModel.PROP_TASK_UPDATED_DATETIME,  taskUpdatedDatetime);
        props.put(CtsModel.PROP_OWNER_UPDATED_DATETIME,  ownerUpdatedDatetime);
        props.put(CtsModel.PROP_AUTO_CREATED_CASE, true);
        // PQ specific
        props.put(CtsModel.PROP_UIN, uin);
        props.put(CtsModel.PROP_OP_DATE, opDate);
        props.put(CtsModel.PROP_WO_DATE, woDate);
        props.put(CtsModel.PROP_QUESTION_NUMBER, "");
        props.put(CtsModel.PROP_QUESTION_TEXT, "");
        props.put(CtsModel.PROP_RECEIVED_TYPE, "");
        props.put(CtsModel.PROP_ANSWER_TEXT, "");
        props.put(CtsModel.PROP_MEMBER, "");
        props.put(CtsModel.PROP_CONSTITUENCY, "");
        props.put(CtsModel.PROP_PARTY, "");
        props.put(CtsModel.PROP_SIGNED_BY_HOME_SEC, false);
        props.put(CtsModel.PROP_SIGNED_BY_LORDS_MINISTER, false);
        props.put(CtsModel.PROP_LORDS_MINISTER, "");
        props.put(CtsModel.PROP_REVIEWED_BY_PERM_SEC, false);
        props.put(CtsModel.PROP_REVIEWED_BY_SPADS, false);
        props.put(CtsModel.PROP_ROUND_ROBIN, false);
        props.put(CtsModel.PROP_CABINET_OFFICE_GUIDANCE, "");
        props.put(CtsModel.PROP_TRANSFER_DEPARTMENT_NAME, "");
        props.put(CtsModel.PROP_IS_GROUPED_SLAVE, false);
        props.put(CtsModel.PROP_IS_GROUPED_MASTER, false);
        props.put(CtsModel.PROP_MASTER_NODE_REF, "");
        props.put(CtsModel.PROP_ANSWERING_MINISTER, "");
        props.put(CtsModel.PROP_ANSWERING_MINISTER_ID, "");
        return props;
    }

    @Test
    public void testGetScheduledActionsFolder() {
        // given
        emailJobService = mock(EmailJobService.class);
        emailJobService.setNodeLocatorService(nodeLocatorService);
        emailJobService.setNodeService(nodeService);

        // when
        when(emailJobService.getScheduledActionsFolder()).thenReturn(scheduledActionsNode);

        // then
        assertNotNull(emailJobService.getScheduledActionsFolder());
        assertEquals(scheduledActionsNodeRefString, emailJobService.getScheduledActionsFolder().toString());
    }


    @Test
    public void testSendEmailAsynchronously() {

        // given
        Action action = new ActionImpl(caseNode, EmailJobActionExecuter.NAME, EmailJobActionExecuter.NAME);
        action.setParameterValue(EmailJobActionExecuter.PARAM_USERNAME, userName);
        action.setParameterValue(EmailJobActionExecuter.PARAM_TEMPLATE_SUBJECT,  "Hercule: You have been allocated a task");
        action.setParameterValue(EmailJobActionExecuter.PARAM_TEMPLATE, "wf-email.html.ftl");
        action.setParameterValue(EmailJobActionExecuter.PARAM_URL_EXTENSION, "/cts/cases/view/" + caseNode.getId());
        action.setParameterValue(EmailJobActionExecuter.PARAM_CASE_NODE_REF, caseNode);
        action.setParameterValue(EmailJobActionExecuter.PARAM_TEMPLATE_DATA,  (Serializable)templateData);
        ActionService actionService = mock(ActionService.class);
        when(actionService.createAction(EmailJobActionExecuter.NAME)).thenReturn(action);
        emailJobService = spy(new EmailJobService());
        emailJobService.setActionService(actionService);

        // when
        emailJobService.sendEmailAsynchronously(userName, templateSubject, templateName, urlExtension, caseNode, templateData);

        // then
        verify(actionService).executeAction(action, caseNode, false, true);
    }


    @Test
    public void testSendEmailJobSuccess() {
         // given
        emailJobService = spy(new EmailJobService());
        emailJobService.setEmailService(emailService);
        when(nodeService.hasAspect(emailNode, ContentModel.ASPECT_EMAILED)).thenReturn(true);
        when(nodeService.getTargetAssocs(emailNode, ContentModel.ASSOC_CONTAINS)).thenReturn(children);
        when(nodeService.getProperties(emailNode)).thenReturn(mockEmailJobNodeProperties());
        emailJobService.setNodeService(nodeService);
        when(emailJobService.fetchCaseFromEmailJob(emailNode)).thenReturn(caseNode);

        // when
        emailJobService.sendEmailJob(emailNode);

        // then
        verify(emailService).sendEmail(userName, templateSubject, templateName, urlExtension, caseNode, templateData);
        System.out.println("RESULT->" + emailService);
        verify(emailJobService).updateMailResponseSuccess(emailNode);
    }


    @Test
    public void testSendEmailJobFail() {
        // given
        emailJobService = spy(new EmailJobService());
        globalProperties.put(CtsMail.PROP_MAIL_MAX_RETRIES, "0");
        emailJobService.setGlobalProperties(globalProperties);
        doThrow(new RuntimeException("Mail Sending failed")).when(emailService).sendEmail(userName, templateSubject, templateName, urlExtension, caseNode, templateData);
        emailJobService.setEmailService(emailService);
        when(nodeService.hasAspect(emailNode, ContentModel.ASPECT_EMAILED)).thenReturn(true);
        when(nodeService.getTargetAssocs(emailNode, ContentModel.ASSOC_CONTAINS)).thenReturn(children);
        when(nodeService.getProperties(emailNode)).thenReturn(mockEmailJobNodeProperties());
        when(nodeService.getProperty(emailNode, CtsMail.PROP_FAILURE_COUNT)).thenReturn("0");
        emailJobService.setNodeService(nodeService);
        when(emailJobService.fetchCaseFromEmailJob(emailNode)).thenReturn(caseNode);

        // when
        emailJobService.sendEmailJob(emailNode);

        // then
        verify(emailService).sendEmail(userName, templateSubject, templateName, urlExtension, caseNode, templateData);
        verify(emailJobService, never()).updateMailResponseSuccess(emailNode);
        verify(emailJobService).updateMailResponseFail(emailNode, "Mail Sending failed");
    }


    @Test
    public void testSendEmailJobRetryWithMailException() {
        // given
        emailJobService = spy(new EmailJobService());
        globalProperties.put(CtsMail.PROP_MAIL_MAX_RETRIES, "1");
        emailJobService.setGlobalProperties(globalProperties);
        doThrow(new RuntimeException("Mail Sending failed")).when(emailService).sendEmail(userName, templateSubject, templateName, urlExtension, caseNode, templateData);
        emailJobService.setEmailService(emailService);
        when(nodeService.hasAspect(emailNode, ContentModel.ASPECT_EMAILED)).thenReturn(true);
        when(nodeService.getTargetAssocs(emailNode, ContentModel.ASSOC_CONTAINS)).thenReturn(children);
        when(nodeService.getProperties(emailNode)).thenReturn(mockEmailJobNodeProperties());
        when(nodeService.getProperty(emailNode, CtsMail.PROP_FAILURE_COUNT)).thenReturn("0");
        emailJobService.setNodeService(nodeService);
        when(emailJobService.fetchCaseFromEmailJob(emailNode)).thenReturn(caseNode);

        // when
        emailJobService.sendEmailJob(emailNode);

        // then
        verify(emailService).sendEmail(userName, templateSubject, templateName, urlExtension, caseNode, templateData);
        verify(emailJobService, never()).updateMailResponseSuccess(emailNode);
        verify(emailJobService).updateMailResponseFail(emailNode, "Mail Sending failed");
    }

    // Retries disabled
    @Test
    public void testSendEmailJobFailWithMailException() {
        // given
        emailJobService = spy(new EmailJobService());
        globalProperties.put(CtsMail.PROP_MAIL_MAX_RETRIES, "0");
        emailJobService.setGlobalProperties(globalProperties);
        doThrow(new RuntimeException("Mail Sending failed")).when(emailService).sendEmail(userName, templateSubject, templateName, urlExtension, caseNode, templateData);
        emailJobService.setEmailService(emailService);
        when(nodeService.hasAspect(emailNode, ContentModel.ASPECT_EMAILED)).thenReturn(true);
        when(nodeService.getTargetAssocs(emailNode, ContentModel.ASSOC_CONTAINS)).thenReturn(children);
        when(nodeService.getProperties(emailNode)).thenReturn(mockEmailJobNodeProperties());
        when(nodeService.getProperty(emailNode, CtsMail.PROP_FAILURE_COUNT)).thenReturn("0");
        emailJobService.setNodeService(nodeService);
        when(emailJobService.fetchCaseFromEmailJob(emailNode)).thenReturn(caseNode);

        // when
        emailJobService.sendEmailJob(emailNode);

        // then
        verify(emailService).sendEmail(userName, templateSubject, templateName, urlExtension, caseNode, templateData);
        verify(emailJobService, never()).updateMailResponseSuccess(emailNode);
        verify(emailJobService).updateMailResponseFail(emailNode, "Mail Sending failed");
    }


    @Test
    public void testUpdateMailResponseSuccess() {
        // given
        emailJobService = spy(new EmailJobService());
        emailJobService.setEmailService(emailService);
        emailJobService.setNodeService(nodeService);

        // when
        emailJobService.updateMailResponseSuccess(emailNode);

        // then
        verify(nodeService).setProperty(emailNode, CtsMail.PROP_STATUS, CtsMail.MAIL_RESPONSE_STATUS_SENT);
        verify(nodeService).removeProperty(emailNode, CtsMail.PROP_ERROR_MESSAGE);
    }

    @Test
    public void testUpdateMailResponseFailSetStatusRetry() {
        // given
        emailJobService = spy(new EmailJobService());
        emailJobService.setEmailService(emailService);
        when(nodeService.getProperty(emailNode, CtsMail.PROP_FAILURE_COUNT)).thenReturn("0");
        emailJobService.setNodeService(nodeService);
        globalProperties.put(CtsMail.PROP_MAIL_MAX_RETRIES, "1");
        emailJobService.setGlobalProperties(globalProperties);

        // when
        emailJobService.updateMailResponseFail(emailNode, "Mail Sending failed");

        // then
        verify(nodeService).setProperty(emailNode, CtsMail.PROP_ERROR_MESSAGE, "Mail Sending failed");
        verify(nodeService).setProperty(emailNode, CtsMail.PROP_STATUS, CtsMail.MAIL_RESPONSE_STATUS_RETRY);
    }

    @Test
    public void testUpdateMailResponseFailSetStatusFail() {
        // given
        emailJobService = spy(new EmailJobService());
        emailJobService.setEmailService(emailService);
        when(nodeService.getProperty(emailNode, CtsMail.PROP_FAILURE_COUNT)).thenReturn("0");
        emailJobService.setNodeService(nodeService);
        globalProperties.put(CtsMail.PROP_MAIL_MAX_RETRIES, "0");
        emailJobService.setGlobalProperties(globalProperties);

        // when
        emailJobService.updateMailResponseFail(emailNode, "Mail Sending failed");

        // then
        verify(nodeService).setProperty(emailNode, CtsMail.PROP_ERROR_MESSAGE, "Mail Sending failed");
        verify(nodeService, never()).setProperty(emailNode, CtsMail.PROP_STATUS, CtsMail.MAIL_RESPONSE_STATUS_RETRY);
        verify(nodeService).setProperty(emailNode, CtsMail.PROP_STATUS, CtsMail.MAIL_RESPONSE_STATUS_FAIL);
    }

    @Test
    public void testAddEmailedAspect() {
        // given
        emailJobService = spy(new EmailJobService());
        PersonService personService = mock(PersonService.class);
        personNode = new NodeRef(personNodeRefString);
        when(personService.getPerson(userName)).thenReturn(personNode);
        globalProperties.put(CtsMail.PROP_MAIL_ORIGINATOR_ADDRESS, "doNotReply@hercule.govserve.homeoffice.gov.uk");
        emailJobService.setGlobalProperties(globalProperties);

        Map<QName, Serializable> personProps = new HashMap<QName, Serializable>();
        personProps.put(ContentModel.PROP_EMAIL, email);

        when(nodeService.getProperties(personNode)).thenReturn(personProps);
        emailJobService.setPersonService(personService);
        when(nodeService.hasAspect(emailNode, ContentModel.ASPECT_EMAILED)).thenReturn(true);
        emailJobService.setNodeService(nodeService);

        Map<QName, Serializable> emailProps = new HashMap<QName, Serializable>();
        emailProps.put(ContentModel.PROP_ORIGINATOR, "doNotReply@hercule.govserve.homeoffice.gov.uk");
        emailProps.put(ContentModel.PROP_ADDRESSEE, email);
        emailProps.put(ContentModel.PROP_SUBJECT, templateSubject);

        // when
        emailJobService.addEmailedAspect(emailNode, userName, templateSubject);

        // then
        verify(nodeService).addAspect(emailNode, ContentModel.ASPECT_EMAILED, emailProps);
        assertTrue("Has Aspect Emailed", nodeService.hasAspect(emailNode, ContentModel.ASPECT_EMAILED));


    }

    public void testSaveEmailJob() throws Exception {
        // given
        emailJobService = spy(new EmailJobService());
        emailJobService.setEmailService(emailService);
        emailJobService.setNodeService(nodeService);
        Map<QName, Serializable> contentProps = mockEmailJobNodeProperties();
        contentProps.put(QName.createQName(CustomMailActionExecuter.PARAM_TEMPLATE_MODEL), (Serializable) templateData);
        when(emailJobService.getScheduledActionsFolder()).thenReturn(scheduledActionsNode);
        ChildAssociationRef association = mock(ChildAssociationRef.class);
        doReturn(association).when(nodeService).createNode(
                scheduledActionsNode,
                ContentModel.ASSOC_CONTAINS,
                QName.createQName(NamespaceService.CONTENT_MODEL_PREFIX, emailJobName),
                ContentModel.TYPE_FOLDER,
                mockEmailJobNodeProperties());
        doReturn(emailNode).when(association).getChildRef();

        // when
        NodeRef testEmailJob = emailJobService.saveEmailJob(userName,
                templateSubject,
                templateName,
                urlExtension,
                caseNode,
                templateData,
                CtsMail.MAIL_RESPONSE_STATUS_NEW);

        // then
        assertNotNull(testEmailJob);
        assertEquals("Should have userName:" + userName, userName, nodeService.getProperty(testEmailJob, ContentModel.PROP_USERNAME));
        assertEquals("Should have status: new", CtsMail.MAIL_RESPONSE_STATUS_NEW , nodeService.getProperty(testEmailJob, CtsMail.PROP_STATUS));
        assertTrue("Should have Aspect Emailed", nodeService.hasAspect(testEmailJob, ContentModel.ASPECT_EMAILED));
    }


    public void testSendEmailJobWithMailServerRunning() {

          // Start fake SMTP
        Wiser wiser = new Wiser();
        wiser.setPort(1025);
        wiser.setHostname("localhost");
        wiser.start();

        // given
        Action action = mock(Action.class);
        when(action.getParameterValue(EmailJobActionExecuter.PARAM_USERNAME)).thenReturn(userName);
        when(action.getParameterValue(EmailJobActionExecuter.PARAM_TEMPLATE_SUBJECT)).thenReturn(templateSubject);
        when(action.getParameterValue(EmailJobActionExecuter.PARAM_TEMPLATE)).thenReturn(templateName);
        when(action.getParameterValue(EmailJobActionExecuter.PARAM_URL_EXTENSION)).thenReturn(urlExtension);
        when(action.getParameterValue(EmailJobActionExecuter.PARAM_CASE_NODE_REF)).thenReturn(caseNode);
        when(action.getParameterValue(EmailJobActionExecuter.PARAM_TEMPLATE_DATA)).thenReturn((Serializable)templateData);
        nodeService = mock(NodeService.class);
        when(nodeService.exists(caseNode)).thenReturn(true);
        emailJobService = spy(new EmailJobService());
        emailJobService.setNodeService(nodeService);
        EmailService emailService = new EmailService();
        emailJobService.setEmailService(emailService);

        // when
        when(nodeService.hasAspect(emailNode, ContentModel.ASPECT_EMAILED)).thenReturn(true);
        when(nodeService.getTargetAssocs(emailNode, ContentModel.ASSOC_CONTAINS)).thenReturn(children);
        when(emailJobService.fetchCaseFromEmailJob(emailNode)).thenReturn(caseNode);
        when(nodeService.getProperties(emailNode)).thenReturn(mockEmailJobNodeProperties());
        when(nodeService.getProperty(emailNode, CtsMail.PROP_FAILURE_COUNT)).thenReturn("0");
        emailJobService.sendEmailJob(emailNode);

        // then
        assertTrue("Should have messages" , wiser.getMessages().size() > 0);

        try {
            MimeMessage message = null;
            message = wiser.getMessages().iterator().next().getMimeMessage();
            assertEquals(message.getSubject(),"Hercule: You have been allocated a task");
            assertTrue("Should have CASE URN:" + urnSuffix, message.getContent().toString().contains(urnSuffix));
            assertEquals(1, wiser.getMessages().size());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        wiser.stop();
    }

    public void testExecutionWithMailServerNotRunning() {

        // Immitate a case
        Map<String, String> additionalTemplateData = new HashMap<String, String>();
        additionalTemplateData.put("groupName", "Parliamentary Questions Team");

        // Call EmailJobService action
        Action action = actionService.createAction(EmailJobActionExecuter.NAME);
        action.setParameterValue(EmailJobActionExecuter.PARAM_USERNAME, userName);
        action.setParameterValue(EmailJobActionExecuter.PARAM_TEMPLATE_SUBJECT,  "Hercule: You have been allocated a task");
        action.setParameterValue(EmailJobActionExecuter.PARAM_TEMPLATE, "wf-email.html.ftl");
        action.setParameterValue(EmailJobActionExecuter.PARAM_URL_EXTENSION, "/cts/cases/view/" + caseNode.getId());
        action.setParameterValue(EmailJobActionExecuter.PARAM_CASE_NODE_REF, caseNode.toString());
        action.setParameterValue(EmailJobActionExecuter.PARAM_TEMPLATE_DATA,  (Serializable)additionalTemplateData);
        actionService.executeAction(action, caseNode, false, false);

        // Check for EmailJob nodes
        NodeRef scheduledActions = emailJobService.getScheduledActionsFolder();
        assertTrue(nodeService.getChildAssocs(scheduledActions).size() == 1);
        NodeRef emailJob = nodeService.getChildAssocs(scheduledActions).get(0).getChildRef();

        assertEquals("Should have userName:" + userName, userName, nodeService.getProperty(emailJob, ContentModel.PROP_USERNAME));
        assertEquals("Should have status:" + CtsMail.MAIL_RESPONSE_STATUS_RETRY, CtsMail.MAIL_RESPONSE_STATUS_RETRY , nodeService.getProperty(emailJob, CtsMail.PROP_STATUS));
    }

    @Test
    public void testvalidateEmailData() {

        emailJobService = spy(new EmailJobService());
        List<String> missingEmailData = emailJobService.validateEmailContents(userName, templateSubject, templateName, urlExtension, caseNode, templateData);
        assertTrue(missingEmailData.size() == 0);

        missingEmailData = emailJobService.validateEmailContents(null, templateSubject, templateName, urlExtension, caseNode, templateData);
        assertTrue(missingEmailData.size() == 1);
        assertTrue(missingEmailData.contains("UserName"));

        missingEmailData = emailJobService.validateEmailContents(userName, null, templateName, urlExtension, caseNode, templateData);
        assertTrue(missingEmailData.size() == 1);
        assertTrue(missingEmailData.contains("Subject"));

        missingEmailData = emailJobService.validateEmailContents(userName, templateSubject, null, urlExtension, caseNode, templateData);
        assertTrue(missingEmailData.size() == 1);
        assertTrue(missingEmailData.contains("TemplateName"));

        missingEmailData = emailJobService.validateEmailContents(userName, templateSubject, templateName, null, caseNode, templateData);
        assertTrue(missingEmailData.size() == 1);
        assertTrue(missingEmailData.contains("UrlExtension"));

        missingEmailData = emailJobService.validateEmailContents(userName, templateSubject, templateName, urlExtension, null, templateData);
        assertTrue(missingEmailData.size() == 1);
        assertTrue(missingEmailData.contains("CaseNodeRef"));

        missingEmailData = emailJobService.validateEmailContents(userName, templateSubject, templateName, urlExtension, caseNode, null);
        assertTrue(missingEmailData.size() == 1);
        assertTrue(missingEmailData.contains("TemplateData"));
    }

    public void setEmailJobService(EmailJobService emailJobService) {
        this.emailJobService = emailJobService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public ActionService getActionService() {
        return actionService;
    }

    public void setActionService(ActionService actionService) {
        this.actionService = actionService;
    }
}
