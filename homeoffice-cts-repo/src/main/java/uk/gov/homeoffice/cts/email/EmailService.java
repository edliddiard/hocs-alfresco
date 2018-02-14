package uk.gov.homeoffice.cts.email;

import org.alfresco.service.cmr.action.ActionService;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.PersonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.homeoffice.cts.helpers.CtsFolderHelper;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;
import uk.gov.service.notify.SendEmailResponse;

import java.util.Map;

public class EmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);

    private ActionService actionService;
    private PersonService personService;
    private NodeService nodeService;
    private CtsFolderHelper ctsFolderHelper;
    private String replyAddress;
    private String apiKey;
    NotificationClient client;


    public void sendEmail(String templateId, String emailAddress, Map<String, String> personalisation) {

        if (getClient() == null) {
            LOGGER.debug("Creating Notify Client");
            if (getApiKey() == null) {
                LOGGER.debug("API KEY NULL!!!!!!!!");
            } else {
                client = new NotificationClient(getApiKey());
            }
        }

        if (emailAddress == null) {
            //just stop
            return;
        }
        LOGGER.debug("sending Email: ");
//        LOGGER.debug("Notify details = templateId = " + templateId + " emailAddress = " + emailAddress + " personalisation = " + personalisation.toString());
        SendEmailResponse response = null;
        try {
            response = client.sendEmail(templateId, emailAddress, personalisation, null, null);
        } catch (NotificationClientException e) {
            e.printStackTrace();
        }

        LOGGER.debug("Notify response: " + response);

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

    public String getReplyAddress() {
        return replyAddress;
    }

    public void setReplyAddress(String replyAddress) {
        this.replyAddress = replyAddress;
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
