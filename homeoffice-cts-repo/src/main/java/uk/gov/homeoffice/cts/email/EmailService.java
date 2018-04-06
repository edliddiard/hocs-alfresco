package uk.gov.homeoffice.cts.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.SendEmailResponse;

import java.util.Map;

public class EmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);

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
        SendEmailResponse response = null;
        try {
            response = client.sendEmail(templateId, emailAddress, personalisation, null, null);
            LOGGER.debug("Notify response: " + response.getNotificationId());
        } catch (Exception e) {
            e.printStackTrace();
        }
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
