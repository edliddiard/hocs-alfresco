package uk.gov.homeoffice.cts.email;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.action.executer.MailActionExecuter;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ActionService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.homeoffice.cts.helpers.CtsFolderHelper;
import uk.gov.homeoffice.cts.model.CorrespondenceType;
import uk.gov.homeoffice.cts.model.CtsCase;
import uk.gov.homeoffice.cts.model.CtsModel;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;
import uk.gov.service.notify.SendEmailResponse;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class EmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);

    private ActionService actionService;
    private PersonService personService;
    private NodeService nodeService;
    private CtsFolderHelper ctsFolderHelper;
    private String templateId;
    private String ctsUrl;
    private String mailServer;
    private String replyAddress;
    private String workFlowEmailTemplateId;
    private String resetPWTemplateId;
    private String apiKey;
    NotificationClient client;


    public void sendEmail(String userName, String subject, String templateName, String urlExtension, NodeRef caseNodeRef, Map<String, String> additionalTemplateData) {

        if (getClient() == null) {
            LOGGER.debug("Creating Notify Client");
            if (getApiKey() == null) {
                LOGGER.debug("API KEY NULL!!!!!!!!");
            } else {
                client = new NotificationClient(getApiKey());
            }
        }

        NodeRef personNodeRef = getPersonService().getPerson(userName);
        Map<QName, Serializable> personProps = getNodeService().getProperties(personNodeRef);
        String emailAddress = (String) personProps.get(ContentModel.PROP_EMAIL);

        LOGGER.debug("Email to be sent to: " + emailAddress);
        if (emailAddress == null) {
            //just stop
            return;
        }
        HashMap<String, String> personalisation = new HashMap<>();
        LOGGER.debug("templateName = " + templateName);
        if (templateName.equals("wf-email.html.ftl")) {
            LOGGER.debug("workflow email personalisation");
            templateId = getWorkFlowEmailTemplateId();
            personalisation.put("user", userName);
            personalisation.put("link", getCtsUrl() + "/cts/cases/view/" + caseNodeRef.getId());
        } else if (templateName.equals("reset-user-password-email.html.ftl")) {
            LOGGER.debug("reset password email personalisation");
            templateId = getResetPWTemplateId();
            personalisation.put("user", userName);
            personalisation.put("url", getCtsUrl());
            personalisation.put("link", getCtsUrl() + "/login");
            personalisation.put("password", additionalTemplateData.get("password"));
        }
        LOGGER.debug("sending Email: ");
        LOGGER.debug("Notify details = templateId = " + templateId + " emailAddress = " + emailAddress + " personalisation = " + personalisation.toString());
        SendEmailResponse response = null;
        try {
            response = client.sendEmail(templateId, emailAddress, personalisation, null, null);
            LOGGER.debug("sent Email ");
        } catch (NotificationClientException e) {
            e.printStackTrace();
        }

        LOGGER.debug("Notify response: " + response);

    }


    public void sendExternalResponseEmail(String subject, String templateName, NodeRef caseNodeRef, String replyAddress, Map<QName, Serializable> props) {
        if (getMailServer() == null || getMailServer().equals("")) {
            //there is no email server so don't try
            LOGGER.debug("Mail server is null" + getMailServer());
            return;
        }

        String emailAddress = null;

        if (props.get(CtsModel.PROP_CORRESPONDENCE_TYPE).equals(CorrespondenceType.DCU_MINISTERIAL.getCode())) {
            emailAddress = (String) props.get(CtsModel.PROP_REPLY_TO_EMAIL);
        }
        if (props.get(CtsModel.PROP_CORRESPONDENCE_TYPE).equals(CorrespondenceType.DCU_TREAT_OFFICIAL.getCode())) {
            emailAddress = (String) props.get(CtsModel.PROP_CORRESPONDENT_EMAIL);
        }

        if (emailAddress == null) {
            throw new AlfrescoRuntimeException("No response email address is available for this case");
        }


        Action mail = getActionService().createAction(MailActionExecuter.NAME);

        mail.setParameterValue(MailActionExecuter.PARAM_TO, emailAddress);
        mail.setParameterValue(MailActionExecuter.PARAM_SUBJECT, subject);
        mail.setParameterValue(MailActionExecuter.PARAM_FROM, replyAddress);
        mail.setParameterValue(CustomMailActionExecuter.PARAM_ATTACH_RESPONSE, "true");

        NodeRef templateNodeRef = getCtsFolderHelper().getTemplatesFolder(templateName);
        if (templateNodeRef == null) {
            //stop
            return;
        }
        mail.setParameterValue(MailActionExecuter.PARAM_TEMPLATE, templateNodeRef);

        Map<String, Object> model = new HashMap<>();
        model.put("case", new CtsCase(props));

        mail.setParameterValue(MailActionExecuter.PARAM_TEMPLATE_MODEL, (Serializable) model);

        LOGGER.debug("Sending email");
//        getActionService().executeAction(mail, caseNodeRef);
    }


    public ActionService getActionService() {
        return actionService;
    }

    public void setActionService(ActionService actionService) {
        this.actionService = actionService;
    }

    public PersonService getPersonService() {
        return personService;
    }

    public void setPersonService(PersonService personService) {
        this.personService = personService;
    }

    public NodeService getNodeService() {
        return nodeService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public CtsFolderHelper getCtsFolderHelper() {
        return ctsFolderHelper;
    }

    public void setCtsFolderHelper(CtsFolderHelper ctsFolderHelper) {
        this.ctsFolderHelper = ctsFolderHelper;
    }

    public String getCtsUrl() {
        return ctsUrl;
    }

    public void setCtsUrl(String ctsUrl) {
        this.ctsUrl = ctsUrl;
    }

    public String getMailServer() {
        return mailServer;
    }

    public void setMailServer(String mailServer) {
        this.mailServer = mailServer;
    }

    public String getReplyAddress() {
        return replyAddress;
    }

    public void setReplyAddress(String replyAddress) {
        this.replyAddress = replyAddress;
    }

    public String getWorkFlowEmailTemplateId() {
        return workFlowEmailTemplateId;
    }

    public void setWorkFlowEmailTemplateId(String workFlowEmailTemplateId) {
        this.workFlowEmailTemplateId = workFlowEmailTemplateId;
    }

    public String getResetPWTemplateId() {
        return resetPWTemplateId;
    }

    public void setResetPWTemplateId(String resetPWTemplateId) {
        this.resetPWTemplateId = resetPWTemplateId;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public NotificationClient getClient() {
        return client;
    }
}
