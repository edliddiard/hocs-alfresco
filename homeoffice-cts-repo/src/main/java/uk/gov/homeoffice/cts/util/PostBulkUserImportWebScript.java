package uk.gov.homeoffice.cts.util;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.action.ActionService;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.cmr.security.MutableAuthenticationService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVStrategy;
import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.surf.util.Content;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.servlet.FormData;
import uk.gov.homeoffice.cts.email.EmailService;
import uk.gov.homeoffice.cts.exceptions.BulkUserImportException;
import uk.gov.homeoffice.cts.helpers.CtsFolderHelper;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostBulkUserImportWebScript extends DeclarativeWebScript {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostBulkUserImportWebScript.class);

    private static final int EMAIL_INDEX = 0;
    private static final int FIRST_NAME_INDEX = 1;
    private static final int SURNAME_INDEX = 2;
    private static final int GROUPS_INDEX = 3;
    private static final String SEND_EMAILS_POST_FIELD_NAME = "sendEmails";

    private PersonService personService;
    private AuthorityService authorityService;
    private NodeService nodeService;
    private ActionService actionService;
    private MutableAuthenticationService authenticationService;
    private CtsFolderHelper ctsFolderHelper;
    private EmailService emailService;

    private String ctsUrl;
    private String mailServer;
    private String replyAddress;
    private String ctsMailSubjectNewUser;
    private String ctsMailTemplateNewUser;

    private Boolean sendEmails = false;

    private Map<String, String> createdUsers;
    private List<String> errors;

    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
        super.executeImpl(req, status, cache);
        Map<String, Object> model = new HashMap<>();
        createdUsers = new HashMap<>();
        errors = new ArrayList<>();
        Content content;
        try {
            content = handleRequest(req);
        } catch (BulkUserImportException e) {
            LOGGER.error("Bulk user import CSV file not found in request.");
            status.setCode(Status.STATUS_BAD_REQUEST);
            status.setMessage("Bulk user import CSV file not found.");
            return model;
        }

        try {
            InputStreamReader reader = new InputStreamReader(content.getInputStream(), "UTF-8");
            CSVParser csv = new CSVParser(reader, CSVStrategy.DEFAULT_STRATEGY);
            String[][] data = csv.getAllValues();
            if (data != null && data.length > 0) {
                LOGGER.debug("Bulk user import started.");
                processBulkUserImport(data);
                sendEmails();
                LOGGER.debug("Bulk user import completed.");
            }
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("Unsupported encoding exception trying to read bulk upload csv: " + e.getMessage());
            status.setCode(Status.STATUS_INTERNAL_SERVER_ERROR);
            return model;
        } catch (IOException e) {
            LOGGER.error("IO exception trying to read bulk upload csv: " + e.getMessage());
            status.setCode(Status.STATUS_INTERNAL_SERVER_ERROR);
            return model;
        } finally {
            try {
                content.getInputStream().close();
            } catch (IOException e) {
                LOGGER.error("IO exception trying to close content input stream: " + e.getMessage());
            }
        }
        model.put("createdUsers", createdUsers);
        model.put("errors", errors);
        return model;
    }

    /**
     * Find and return the file from the request.
     * @param req WebScriptRequest
     * @return Content
     * @throws BulkUserImportException
     */
    private Content handleRequest(WebScriptRequest req) throws BulkUserImportException {
        FormData formData = (FormData)req.parseContent();
        if (formData == null) {
            throw new BulkUserImportException("Bulk user import CSV file not found.");
        }
        FormData.FormField[] fields = formData.getFields();
        Content content = null;
        for(FormData.FormField field : fields) {
            if (field.getIsFile()) {
                content = field.getContent();
            }
            if (field.getName().equals(SEND_EMAILS_POST_FIELD_NAME)) {
                sendEmails = Boolean.parseBoolean(field.getValue());
            }
        }
        if (content == null) {
            throw new BulkUserImportException("Bulk user import CSV file not found.");
        }
        return content;
    }

    /**
     * Process the array of users to bulk import.
     * @param data String[][]
     */
    protected void processBulkUserImport(String[][] data) {
        // ignore the first row
        LOGGER.debug("Found " + data.length + " user(s) to import.");
        for (int i = 1; i < data.length; i++) {
            String email = data[i][EMAIL_INDEX];
            String firstName = data[i][FIRST_NAME_INDEX];
            String surname = data[i][SURNAME_INDEX];
            String[] groups = data[i][GROUPS_INDEX].split(";");
            String password = RandomStringUtils.randomAlphanumeric(10);
            if (!email.isEmpty() && !firstName.isEmpty() && !surname.isEmpty()) {
                createPerson(email, firstName, surname, password);
                addPersonGroups(email, groups);
            } else {
                errors.add("Error found on row " + i);
            }
        }
    }

    /**
     * Optionally send emails to the created users.
     */
    protected void sendEmails() {
        if (sendEmails) {
            for (Map.Entry<String, String> user : createdUsers.entrySet()) {
                String email = user.getKey();
                String password = user.getValue();
                Map<String, String> templateData = new HashMap<>();
                templateData.put("password", password);
                emailService.sendEmail(email, ctsMailSubjectNewUser, ctsMailTemplateNewUser, "/login", null, templateData);
            }
        }
    }

    /**
     * Create a person with the specified properties.
     * @param email String
     * @param firstName String
     * @param surname String
     */
    protected void createPerson(String email, String firstName, String surname, String password) {
        Map<QName, Serializable> personProps = new HashMap<>();
        personProps.put(ContentModel.PROP_USERNAME, email);
        personProps.put(ContentModel.PROP_EMAIL, email);
        personProps.put(ContentModel.PROP_FIRSTNAME, firstName);
        personProps.put(ContentModel.PROP_LASTNAME, surname);
        if (!personService.personExists(email)) {
            LOGGER.debug("Creating user: " + email);
            personService.createPerson(personProps);
            authenticationService.createAuthentication(email, password.toCharArray());
            authenticationService.setAuthenticationEnabled(email, true);
            createdUsers.put(email, password);
        } else {
            errors.add("User already exists for username: " + email);
        }
    }

    /**
     * Add the person with email address to the groups specified.
     * @param email String
     * @param groups String[]
     */
    protected void addPersonGroups(String email, String[] groups) {
        for (int i = 0; i < groups.length; i++) {
            String group = groups[i].trim();
            if (group.isEmpty()) {
                continue;
            }
            if (!group.startsWith("GROUP_")) {
                group = "GROUP_" + group;
            }
            if (!authorityService.getAuthoritiesForUser(email).contains(group)) {
                LOGGER.debug("Adding user: " + email + " to group: " + group);
                if (authorityService.authorityExists(group)) {
                    authorityService.addAuthority(group, email);
                } else {
                    LOGGER.debug("Unknown group: " + group);
                    errors.add("Unknown group: " + group + " for user: " + email);
                }
            }
        }
    }

    public PersonService getPersonService() {
        return personService;
    }

    public void setPersonService(PersonService personService) {
        this.personService = personService;
    }

    public AuthorityService getAuthorityService() {
        return authorityService;
    }

    public void setAuthorityService(AuthorityService authorityService) {
        this.authorityService = authorityService;
    }

    public NodeService getNodeService() {
        return nodeService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public ActionService getActionService() {
        return actionService;
    }

    public AuthenticationService getAuthenticationService() {
        return authenticationService;
    }

    public void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = (MutableAuthenticationService)authenticationService;
    }

    public CtsFolderHelper getCtsFolderHelper() {
        return ctsFolderHelper;
    }

    public void setCtsFolderHelper(CtsFolderHelper ctsFolderHelper) {
        this.ctsFolderHelper = ctsFolderHelper;
    }

    public void setActionService(ActionService actionService) {
        this.actionService = actionService;
    }

    public EmailService getEmailService() {
        return emailService;
    }

    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
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

    public String getCtsMailSubjectNewUser() {
        return ctsMailSubjectNewUser;
    }

    public void setCtsMailSubjectNewUser(String ctsMailSubjectNewUser) {
        this.ctsMailSubjectNewUser = ctsMailSubjectNewUser;
    }

    public String getCtsMailTemplateNewUser() {
        return ctsMailTemplateNewUser;
    }

    public void setCtsMailTemplateNewUser(String ctsMailTemplateNewUser) {
        this.ctsMailTemplateNewUser = ctsMailTemplateNewUser;
    }

    public Boolean getSendEmails() {
        return sendEmails;
    }

    public void setSendEmails(Boolean sendEmails) {
        this.sendEmails = sendEmails;
    }
}
