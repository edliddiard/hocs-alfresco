package uk.gov.homeoffice.cts.email;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.nodelocator.CompanyHomeNodeLocator;
import org.alfresco.repo.nodelocator.NodeLocatorService;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ActionService;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.homeoffice.cts.model.CtsMail;
import uk.gov.homeoffice.cts.model.CtsModel;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * The Class EmailJobService intercepts SendEmail calls and saves them in the
 * Company Home/Data Dictionary/Scheduled Actions folder for sending later
 * <p>
 * Created by dawud on 19/02/2016.
 */
public class EmailJobService {

    public static final String SCHEDULED_ACTIONS_NODE_NAME = "Scheduled Actions";
    public static final String SCHEDULED_ACTIONS_NODE_PATH = "/Company Home/Data Dictionary/Scheduled Actions";
    public static final String DATA_DICTIONARY_NODE_NAME = "Data Dictionary";
    public static final String SCHEDULED_ACTIONS_LUCENE_SEARCH_QUERY = "PATH:\"/app:company_home/app:dictionary/cm:Scheduled_x0020_Actions\"";

    private static Logger LOGGER = LoggerFactory.getLogger(EmailJobService.class);
    private Properties globalProperties;

    @Autowired
    private ActionService actionService;

    @Autowired
    private NodeLocatorService nodeLocatorService;

    @Autowired
    private FileFolderService fileFolderService;

    @Autowired
    private NodeService nodeService;

    @Autowired
    public EmailService emailService;

    @Autowired
    private PersonService personService;

    /**
     * Send Email as an asynchronous action.
     */
    public void sendEmailAsynchronously(String userName, String templateSubject, String templateName, String urlExtension, NodeRef caseNode, Map<String, String> templateData) {
        LOGGER.debug("Creating EmailJobAction CaseNode[{}]", caseNode);
        long startTime = System.currentTimeMillis();

        // Execute email job Action Executer
        Action action = actionService.createAction(EmailJobActionExecuter.NAME);
        action.setParameterValue(EmailJobActionExecuter.PARAM_USERNAME, userName);
        action.setParameterValue(EmailJobActionExecuter.PARAM_TEMPLATE_SUBJECT, templateSubject);
        action.setParameterValue(EmailJobActionExecuter.PARAM_TEMPLATE, templateName);
        action.setParameterValue(EmailJobActionExecuter.PARAM_URL_EXTENSION, urlExtension);
        action.setParameterValue(EmailJobActionExecuter.PARAM_CASE_NODE_REF, caseNode);
        action.setParameterValue(EmailJobActionExecuter.PARAM_TEMPLATE_DATA, (Serializable) templateData);
        actionService.executeAction(action, caseNode, false, true);
        long elapsedTime = System.currentTimeMillis() - startTime;
        LOGGER.debug("Creating EmailJobAction: [{}] completed. TIME: {}ms", EmailJobActionExecuter.NAME, elapsedTime);
        actionService.removeAction(caseNode, action);
    }

    /**
     * Maps all properties from the Email parameters and Case for the EmailJob
     */
    private Map<QName, Serializable> createEmailJobProperties(String userName, String subject, String templateName, String urlExtension, NodeRef caseNodeRef, Map<String, String> additionalTemplateData, String emailJobName, String mailResponseStatus) {

        Map<QName, Serializable> contentProps = new HashMap<QName, Serializable>();
        // Collect Email message properties
        contentProps.put(ContentModel.PROP_TITLE, emailJobName);
        contentProps.put(ContentModel.PROP_USERNAME, userName);
        contentProps.put(CtsMail.PARAM_SUBJECT, subject);
        contentProps.put(CtsMail.PARAM_TEMPLATE, templateName);
        contentProps.put(CtsMail.PROP_CASE_URL, urlExtension);
        contentProps.put(CtsMail.PARAM_TEMPLATE_MODEL, (Serializable) additionalTemplateData);
        contentProps.put(CtsMail.PROP_STATUS, mailResponseStatus);
        contentProps.put(CtsMail.PROP_FAILURE_COUNT, 0);

        // Show properties about the Case for the EmailJobs webscript page
        try {
            AuthenticationUtil.setRunAsUserSystem();
            contentProps.put(CtsModel.PROP_CORRESPONDENCE_TYPE, nodeService.getProperty(caseNodeRef, CtsModel.PROP_CORRESPONDENCE_TYPE));
            contentProps.put(CtsModel.PROP_URN_SUFFIX, nodeService.getProperty(caseNodeRef, CtsModel.PROP_URN_SUFFIX));
        } finally {
            AuthenticationUtil.clearCurrentSecurityContext();
        }
        return contentProps;
    }


    /**
     * Add Aspects with all the Emailed Aspect Properties
     */
    public void addEmailedAspect(NodeRef emailJobNode, String userName, String subject) {
        try {
            AuthenticationUtil.setRunAsUserSystem();
            // Update emailed addresse
            NodeRef personNodeRef = personService.getPerson(userName);
            Map<QName, Serializable> personProps = nodeService.getProperties(personNodeRef);
            String emailAddress = (String) personProps.get(ContentModel.PROP_EMAIL);
            String mailDoNotReplyAddress = (globalProperties.get(CtsMail.PROP_MAIL_ORIGINATOR_ADDRESS) != null) ? (String) globalProperties.get(CtsMail.PROP_MAIL_ORIGINATOR_ADDRESS) : "";

            // Collect Email Aspect properties
            Map<QName, Serializable> emailProps = new HashMap<QName, Serializable>();
            emailProps.put(ContentModel.PROP_ORIGINATOR, mailDoNotReplyAddress);
            emailProps.put(ContentModel.PROP_ADDRESSEE, emailAddress);
            emailProps.put(ContentModel.PROP_SUBJECT, subject);

            // Add the ASPECTS EMAILED and TEMPORARY so no archive is created and quick to delete.
            nodeService.addAspect(emailJobNode, ContentModel.ASPECT_EMAILED, emailProps);
            nodeService.addAspect(emailJobNode, ContentModel.ASPECT_TEMPORARY, null);
            LOGGER.debug("Emailed aspect has been added.");

        } finally {
            AuthenticationUtil.clearCurrentSecurityContext();
        }
    }

    public List<String> validateEmailContents(String userName, String subject, String templateName, String urlExtension, NodeRef caseNodeRef, Map<String, String> templateData) {
        LOGGER.debug("Validating Email Contents");

        List<String> missingEmailData = new ArrayList<String>();
        if (userName == null || userName.isEmpty()) {
            missingEmailData.add("UserName");
        }
        if (subject == null || subject.isEmpty()) {
            missingEmailData.add("Subject");
        }
        if (templateName == null || templateName.isEmpty()) {
            missingEmailData.add("TemplateName");
        }
        if (urlExtension == null || urlExtension.isEmpty()) {
            missingEmailData.add("UrlExtension");
        }
        if (caseNodeRef == null) {
            missingEmailData.add("CaseNodeRef");
        }
        if (templateData == null || (templateData != null && templateData.size() == 0)) {
            missingEmailData.add("TemplateData");
        }
        String errorMessage = (missingEmailData.size() == 0) ? "OK" : Arrays.toString(missingEmailData.toArray());
        if (missingEmailData.size() > 0) {
            LOGGER.error("Missing Email content items: [{}]", errorMessage);
        } else {
            LOGGER.debug("Email Contents validated: [{}]", errorMessage);
        }
        return missingEmailData;
    }

    /**
     * Serialises email message and saves mail exception message on failure
     */
    public NodeRef saveEmailJob(String userName, String subject, String templateName, String urlExtension, NodeRef caseNode, Map<String, String> templateData, String mailStatus, String mailException) {
        LOGGER.debug("Saving Email Message as scheduled job with Exception Message: [{}] " + mailException);
        NodeRef emailJob = saveEmailJob(userName, subject, templateName, urlExtension, caseNode, templateData, mailStatus);
        updateMailResponseFail(emailJob, mailException);
        return emailJob;
    }


        /**
         * Serialises email message and saves it as a Job in the Scheduled Actions folder.
         */
    public NodeRef saveEmailJob(String userName, String subject, String templateName, String urlExtension, NodeRef caseNode, Map<String, String> templateData, String mailStatus) {
        LOGGER.debug("Saving Email Message as scheduled job");

        // Create Job with the date as unique document name and title
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(CtsMail.MAIL_JOB_NAME_POSTFIX);
        final String date = simpleDateFormat.format(new Date());
        final String emailJobName = CtsMail.MAIL_JOB_NAME_PREFIX + date;
        final Map<QName, Serializable> emailJobProperties = createEmailJobProperties(userName, subject, templateName, urlExtension, caseNode, templateData, emailJobName, mailStatus);

        final NodeRef caseNodeFinal = caseNode;
        NodeRef emailJobNode = AuthenticationUtil.runAs(new AuthenticationUtil.RunAsWork<NodeRef>() {
            @SuppressWarnings("synthetic-access")
            public NodeRef doWork() throws Exception {
                NodeRef scheduledActionsFolder = getScheduledActionsFolder();
                // Create Email Job Node Scheduled Actions folder
                ChildAssociationRef association = nodeService.createNode(
                        scheduledActionsFolder,
                        ContentModel.ASSOC_CONTAINS,
                        QName.createQName(NamespaceService.CONTENT_MODEL_PREFIX, emailJobName),
                        ContentModel.TYPE_FOLDER,
                        emailJobProperties
                );

                // Now Add the Case Document Association
                AssociationRef associationRef = nodeService.createAssociation(association.getChildRef(),
                        caseNodeFinal,
                        ContentModel.ASSOC_CONTAINS
                );
                // return EmailJobNode
                return association.getChildRef();
            }
        }, AuthenticationUtil.getSystemUserName());

        // Add Email Job Node properties and Aspects
        addEmailedAspect(emailJobNode, userName, subject);

        // Update properties for archiving sent EmailJobs
        if (mailStatus.equals(CtsMail.MAIL_RESPONSE_STATUS_SENT)) {
            updateMailResponseSuccess(emailJobNode);
        }

        LOGGER.debug("Saved Email Message as scheduled job file[{}] EmailJob node: [{}] Mail Status: [" + mailStatus + "]", emailJobName, emailJobNode.toString());
        return emailJobNode;
    }


    /**
     * De-serialise email Job from the Scheduled Actions folder and sends it out.
     * Synchronised so only one thread can execute this method to prevent EmailJobScheduler sending the same emailjob out.
     */
    public synchronized void sendEmailJob(NodeRef emailJob) {
        LOGGER.debug("Sending Email Job Noderef:[{}]", emailJob.toString());

        // Fetch the case document
        NodeRef caseNodeRef = fetchCaseFromEmailJob(emailJob);

        // Fetch the email properties
        Map<QName, Serializable> emailProps = nodeService.getProperties(emailJob);
        String userName = (String) emailProps.get(ContentModel.PROP_USERNAME);
        String subject = (String) emailProps.get(CtsMail.PARAM_SUBJECT);
        String templateName = (String) emailProps.get(CtsMail.PARAM_TEMPLATE);
        String urlExtension = (String) emailProps.get(CtsMail.PROP_CASE_URL);

        // Put the Email templates parameter values
        Map<String, String> additionalTemplateData = (Map<String, String>) emailProps.get(CtsMail.PARAM_TEMPLATE_MODEL);
        StringBuilder sb = new StringBuilder();
        Iterator<Map.Entry<QName, Serializable>> entries = emailProps.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<QName, Serializable> entry = entries.next();
            additionalTemplateData.put(entry.getKey().getLocalName(), entry.getValue().toString());
            sb.append(entry.getKey().getLocalName() + ":" + entry.getValue().toString());
            if (entries.hasNext()) {
                sb.append(" ,");
            }
        }

        try {
            // Send email out using EmailService
            emailService.sendEmail(userName, subject, templateName, urlExtension, caseNodeRef, additionalTemplateData);
            updateMailResponseSuccess(emailJob);
        } catch (Exception mailException) {
            LOGGER.error("Mail not sent, error[{}]", mailException.getMessage());
            updateMailResponseFail(emailJob, mailException.getMessage());
        }
    }


    /**
     * Updates EmailJob properties after sending i.e record status
     */
    public void updateMailResponseSuccess(NodeRef emailJob) {
        try {
            AuthenticationUtil.setRunAsUserSystem();
            // Update email sent date
            nodeService.setProperty(emailJob, ContentModel.PROP_SENTDATE, new Date());
            nodeService.setProperty(emailJob, CtsMail.PROP_STATUS, CtsMail.MAIL_RESPONSE_STATUS_SENT);
            nodeService.removeProperty(emailJob, CtsMail.PROP_ERROR_MESSAGE);
        } finally {
            AuthenticationUtil.clearCurrentSecurityContext();
        }
    }

    /**
     * Updates EmailJob properties if failed
     */
    public void updateMailResponseFail(NodeRef emailJob, String errorMessage) {
        int mailMaxRetriesLimit = globalProperties.get(CtsMail.PROP_MAIL_MAX_RETRIES) != null ? Integer.parseInt(globalProperties.get(CtsMail.PROP_MAIL_MAX_RETRIES).toString()) : CtsMail.MAIL_DEFAULT_MAX_RETRIES;
        try {
            AuthenticationUtil.setRunAsUserSystem();
            nodeService.setProperty(emailJob, CtsMail.PROP_ERROR_MESSAGE, errorMessage);

            // limit retries
            int failureCount = Integer.parseInt(nodeService.getProperty(emailJob, CtsMail.PROP_FAILURE_COUNT).toString());
            if (failureCount < mailMaxRetriesLimit) {
                failureCount++;
                nodeService.setProperty(emailJob, CtsMail.PROP_FAILURE_COUNT, failureCount);
                nodeService.setProperty(emailJob, CtsMail.PROP_STATUS, CtsMail.MAIL_RESPONSE_STATUS_RETRY);
            } else {
                nodeService.setProperty(emailJob, CtsMail.PROP_STATUS, CtsMail.MAIL_RESPONSE_STATUS_FAIL);
            }
        } finally {
            AuthenticationUtil.clearCurrentSecurityContext();
        }
    }


    /**
     * Fetch the CaseNodeRef from the EmailJob Node
     */
    public NodeRef fetchCaseFromEmailJob(NodeRef emailJob) {
        // Fetch the case document
        NodeRef caseNodeRef = null;
        List<AssociationRef> children = nodeService.getTargetAssocs(emailJob, ContentModel.ASSOC_CONTAINS);

        for (AssociationRef childAssoc : children) {
            caseNodeRef = childAssoc.getTargetRef();
        }
        return caseNodeRef;
    }


    /**
     * Helper method to get Scheduled Actions folder from Data Dictionary folder
     *
     * @return
     */
    public NodeRef getScheduledActionsFolder() {
        NodeRef parentNode = nodeLocatorService.getNode(CompanyHomeNodeLocator.NAME, null, null);

        List<String> pathElements = new ArrayList<>();
        pathElements.add(EmailJobService.DATA_DICTIONARY_NODE_NAME);
        pathElements.add(EmailJobService.SCHEDULED_ACTIONS_NODE_NAME);
        NodeRef scheduledActions = null;
        try {
            scheduledActions = fileFolderService.resolveNamePath(parentNode, pathElements).getNodeRef();
        } catch (FileNotFoundException e) {
            LOGGER.error("Folder location: \"" + EmailJobService.SCHEDULED_ACTIONS_NODE_PATH + "\" not found. Check this folder exists");
        }
        return scheduledActions;
    }

    public void setNodeLocatorService(NodeLocatorService nodeLocatorService) {
        this.nodeLocatorService = nodeLocatorService;
    }

    public void setFileFolderService(FileFolderService fileFolderService) {
        this.fileFolderService = fileFolderService;
    }

    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }

    public EmailService getEmailService() {
        return emailService;
    }

    public void setPersonService(PersonService personService) {
        this.personService = personService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public NodeService getNodeService() {
        return nodeService;
    }

    public void setGlobalProperties(final Properties globalProperties) {
        this.globalProperties = globalProperties;
    }

    public ActionService getActionService() {
        return actionService;
    }

    public void setActionService(ActionService actionService) {
        this.actionService = actionService;
    }
}
