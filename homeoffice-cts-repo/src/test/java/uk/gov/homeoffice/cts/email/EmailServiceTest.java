//package uk.gov.homeoffice.cts.email;
//
//import org.alfresco.model.ContentModel;
//import org.alfresco.repo.action.executer.MailActionExecuter;
//import org.alfresco.service.cmr.action.Action;
//import org.alfresco.service.cmr.action.ActionService;
//import org.alfresco.service.cmr.repository.NodeRef;
//import org.alfresco.service.cmr.repository.NodeService;
//import org.alfresco.service.cmr.security.PersonService;
//import org.alfresco.service.namespace.QName;
//import org.junit.Ignore;
//import org.junit.Test;
//import uk.gov.homeoffice.cts.helpers.CtsFolderHelper;
//
//import java.io.Serializable;
//import java.util.HashMap;
//import java.util.Map;
//
//import static org.mockito.Mockito.*;
//
///**
// * Created by chris on 09/04/2015.
// */
//@Ignore
//public class EmailServiceTest {
//    @Test
//    public void testSendUserEmailNullEmailServer(){
//        EmailService emailService = spy(new EmailService());
//        when(emailService.getMailServer()).thenReturn(null);
//        emailService.sendEmail("userB", "", "", "", new NodeRef("workspace://spaces/test"), null);
//
//        //check it doesn't send go past check
//        verify(emailService,never()).getPersonService();
//    }
//
//
//    @Test
//    public void testSendUserEmailEmptyEmailServer(){
//        EmailService emailService = spy(new EmailService());
//        when(emailService.getMailServer()).thenReturn("");
//        emailService.sendEmail("userB","","","",new NodeRef("workspace://spaces/test"),null);
//
//        //check it doesn't send go past check
//        verify(emailService,never()).getPersonService();
//    }
//
//    @Test
//    public void testSendUserEmail(){
//        String templateName = "template";
//        NodeRef personNodeRef = new NodeRef("workspace://spaces/user");
//
//        EmailService emailService = spy(new EmailService());
//        when(emailService.getMailServer()).thenReturn("server.mail");
//
//        NodeService nodeService = mock(NodeService.class);
//        Map<QName,Serializable> props = new HashMap();
//        props.put(ContentModel.PROP_EMAIL,"person@test.com");
//        when(nodeService.getProperties(personNodeRef)).thenReturn(props);
//        emailService.setNodeService(nodeService);
//
//        PersonService personService = mock(PersonService.class);
//        when(personService.getPerson("userB")).thenReturn(personNodeRef);
//        emailService.setPersonService(personService);
//
//        ActionService actionService = mock(ActionService.class);
//        Action mail = mock(Action.class);
//        when(actionService.createAction(MailActionExecuter.NAME)).thenReturn(mail);
//        emailService.setActionService(actionService);
//
//        CtsFolderHelper ctsFolderHelper = mock(CtsFolderHelper.class);
//        when(ctsFolderHelper.getTemplatesFolder(templateName)).thenReturn(new NodeRef("workspace://spaces/template"));
//        emailService.setCtsFolderHelper(ctsFolderHelper);
//
//        Map<String, String> additionalTemplateData = new HashMap<>();
//        emailService.sendEmail("userB","",templateName,"",new NodeRef("workspace://spaces/test"),additionalTemplateData);
//
//        //check it doesn't send go past check
//        verify(actionService).executeAction(mail, personNodeRef);
//    }
//
//
//}
