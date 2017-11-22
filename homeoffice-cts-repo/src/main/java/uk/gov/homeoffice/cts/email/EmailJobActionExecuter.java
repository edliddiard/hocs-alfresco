package uk.gov.homeoffice.cts.email;

import org.alfresco.repo.action.ParameterDefinitionImpl;
import org.alfresco.repo.action.executer.ActionExecuterAbstractBase;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ActionService;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.homeoffice.cts.model.CtsMail;

import java.util.List;
import java.util.Map;
import java.util.Properties;


/**
 * ActionExecutor to call the EmailJobService as asynchronous background task
 * <p>
 * Created by dawud on 19/02/2016.
 */
public class EmailJobActionExecuter extends ActionExecuterAbstractBase {

    public static final String NAME = "EmailJobAction";
    public static final String PARAM_USERNAME = "userName";
    public static final String PARAM_TEMPLATE_SUBJECT = "subject";
    public static final String PARAM_TEMPLATE = "templateName";
    public static final String PARAM_URL_EXTENSION = "urlExtension";
    public static final String PARAM_CASE_NODE_REF = "caseNodeRef";
    public static final String PARAM_TEMPLATE_DATA = "templateData";

    private static Logger LOGGER = LoggerFactory.getLogger(EmailJobActionExecuter.class);
    private Properties globalProperties;
    private boolean mailJobSchedulerEnabled = CtsMail.MAIL_DEFAULT_JOB_SCHEDULER_ENABLED;
    private String mailClearFailedDuration = CtsMail.MAIL_DEFAULT_CLEAR_FAILED_DURATION;
    private String mailClearSentDuration = CtsMail.MAIL_DEFAULT_CLEAR_SENT_DURATION;
    private int mailMaxRetries = CtsMail.MAIL_DEFAULT_MAX_RETRIES;
    private String mailRetryDuration = CtsMail.MAIL_DEFAULT_RETRY_DURATION;

    private ActionService actionService;
    @Autowired
    private NodeService nodeService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private EmailJobService emailJobService;

    public void init() {
        mailJobSchedulerEnabled = globalProperties.get(CtsMail.PROP_MAIL_JOB_SCHEDULER_ENABLED) != null ?
                Boolean.parseBoolean(globalProperties.get(CtsMail.PROP_MAIL_JOB_SCHEDULER_ENABLED).toString()) : CtsMail.MAIL_DEFAULT_JOB_SCHEDULER_ENABLED;
        mailMaxRetries = globalProperties.get(CtsMail.PROP_MAIL_MAX_RETRIES) != null ?
                Integer.parseInt(globalProperties.get(CtsMail.PROP_MAIL_MAX_RETRIES).toString()) : CtsMail.MAIL_DEFAULT_MAX_RETRIES;
        mailClearSentDuration = globalProperties.get(CtsMail.PROP_MAIL_CLEAR_SENT_DURATION) != null ?
                globalProperties.get(CtsMail.PROP_MAIL_CLEAR_SENT_DURATION).toString() : CtsMail.MAIL_DEFAULT_CLEAR_SENT_DURATION;
    }

    /**
     * @see ActionExecuterAbstractBase#executeImpl(Action, NodeRef)
     * Sends out Email first.
     * If sent mail clear duration is > 0 then save a copy of the sent emailJob
     * If message fails to send then
     * schedule for retry if retries is > 0 or else fail
     */
    public void executeImpl(Action action, NodeRef caseNode) {
        LOGGER.debug("Running EmailJobAction CaseNode [{}]", caseNode);
        long startTime = System.currentTimeMillis();
        init();

        // Check that the Case node still exists
        if (nodeService.exists(caseNode) == true) {
            String userName = (String) action.getParameterValue(PARAM_USERNAME);
            String templateSubject = (String) action.getParameterValue(PARAM_TEMPLATE_SUBJECT);
            String templateName = (String) action.getParameterValue(PARAM_TEMPLATE);
            String urlExtension = (String) action.getParameterValue(PARAM_URL_EXTENSION);
            NodeRef caseNodeRef = (NodeRef) action.getParameterValue(PARAM_CASE_NODE_REF);
            Map<String, String> templateData = (Map<String, String>) action.getParameterValue(PARAM_TEMPLATE_DATA);

            LOGGER.debug("MailJob Scheduler Enabled[{}]", mailJobSchedulerEnabled);
            // Check cts.homeoffice.mail.mailJobSchedulerEnabled property is enabled
            if (mailJobSchedulerEnabled) {
                try {
                        // Send email out using EmailService
                        emailService.sendEmail(userName, templateSubject, templateName, urlExtension, caseNodeRef, templateData);
                        // Check if we need to save sent emails audit trail.
                        if (!mailClearSentDuration.equals("PT0H")) {
                            LOGGER.debug("Archiving sent EmailJob for duration: [{}]", mailClearSentDuration);
                            emailJobService.saveEmailJob(userName, templateSubject, templateName, urlExtension, caseNodeRef, templateData, CtsMail.MAIL_RESPONSE_STATUS_SENT);
                        }
                    } catch (Exception mailException) {
                        LOGGER.error("Mail Exception while running EmailJobAction: [{}]", mailException.getMessage());

                        if (mailMaxRetries > 0) {
                            LOGGER.debug("Saving un-sent EmailJob, and setting status to Retry");
                            emailJobService.saveEmailJob(userName, templateSubject, templateName, urlExtension, caseNodeRef, templateData, CtsMail.MAIL_RESPONSE_STATUS_RETRY, mailException.getMessage());
                        } else {
                            LOGGER.error("Saving un-sent EmailJob, and setting status to Fail");
                            emailJobService.saveEmailJob(userName, templateSubject, templateName, urlExtension, caseNodeRef, templateData, CtsMail.MAIL_RESPONSE_STATUS_FAIL, mailException.getMessage());
                        }
                    }
                    LOGGER.debug("EmailJob ran: [{}] completed. TIME: {}ms", EmailJobActionExecuter.NAME, System.currentTimeMillis() - startTime);

            } else {
                emailService.sendEmail(userName, templateSubject, templateName, urlExtension, caseNodeRef, templateData);
                LOGGER.debug("Email Sent. TIME: {}ms", System.currentTimeMillis() - startTime);
            }
        }
        LOGGER.debug("End EmailJobAction CaseNode[{}]", caseNode);
    }


    /**
     * @see org.alfresco.repo.action.ParameterizedItemAbstractBase#addParameterDefinitions(List)
     */
    @Override
    protected void addParameterDefinitions(List<ParameterDefinition> paramList) {
        // Add definitions for action parameters
        paramList.add(
                new ParameterDefinitionImpl(                       // Create a new parameter defintion to add to the list
                        PARAM_USERNAME,                              // The name used to identify the parameter
                        DataTypeDefinition.TEXT,                       // The parameter value type
                        true,                                           // Indicates whether the parameter is mandatory
                        getParamDisplayLabel(PARAM_USERNAME)));      // The parameters display label
        paramList.add(new ParameterDefinitionImpl(PARAM_TEMPLATE_SUBJECT, DataTypeDefinition.TEXT, true, this.getParamDisplayLabel(PARAM_TEMPLATE_SUBJECT)));
        paramList.add(new ParameterDefinitionImpl(PARAM_TEMPLATE, DataTypeDefinition.TEXT, true, this.getParamDisplayLabel(PARAM_TEMPLATE)));
        paramList.add(new ParameterDefinitionImpl(PARAM_URL_EXTENSION, DataTypeDefinition.TEXT, true, this.getParamDisplayLabel(PARAM_URL_EXTENSION)));
        paramList.add(new ParameterDefinitionImpl(PARAM_CASE_NODE_REF, DataTypeDefinition.NODE_REF, true, this.getParamDisplayLabel(PARAM_CASE_NODE_REF)));
        paramList.add(new ParameterDefinitionImpl(PARAM_TEMPLATE_DATA, DataTypeDefinition.ANY, true, this.getParamDisplayLabel(PARAM_TEMPLATE_DATA), true));
    }

    public ActionService getActionService() {
        return actionService;
    }

    public void setActionService(ActionService actionService) {
        this.actionService = actionService;
    }

    public NodeService getNodeService() {
        return nodeService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public EmailService getEmailService() {
        return emailService;
    }

    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }

    public EmailJobService getEmailJobService() {
        return emailJobService;
    }

    public void setEmailJobService(EmailJobService emailJobService) {
        this.emailJobService = emailJobService;
    }

    public Properties getGlobalProperties() {
        return globalProperties;
    }

    public void setGlobalProperties(Properties globalProperties) {
        this.globalProperties = globalProperties;
    }
}
