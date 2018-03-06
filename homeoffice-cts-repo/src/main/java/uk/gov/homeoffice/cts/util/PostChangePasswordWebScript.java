package uk.gov.homeoffice.cts.util;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.web.scripts.person.ChangePasswordPost;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.cmr.security.MutableAuthenticationService;
import org.alfresco.service.cmr.security.PersonService;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import uk.gov.homeoffice.cts.model.CtsUserModel;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This is only usable by a user sending in their old password as they are not authenticated
 * Created by chris on 12/12/2014.
 */
public class PostChangePasswordWebScript extends ChangePasswordPost {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostChangePasswordWebScript.class);

    private static final String OLDPW_PARAM_ERROR = "Old password 'oldpw' is a required POST parameter.";
    private static final String SAME_PASSWORD_ERROR = "New password must not match old password.";
    private static final String PASSWORD_COMPLEXITY_ERROR = "Your password is not complex enough.";

    private PersonService personService;
    private NodeService nodeService;
    private AuthorityService authorityService;
    private AuthenticationService authenticationService;

    private String PARAM_OLDPW = "oldpw";
    private String PARAM_NEWPW = "newpw";

    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status){
        //authenticate the user from their details
        String[] authDetails = checkUserIsLoggedIn(req);
        validatePassword(req);

        Map<String, Object> map = super.executeImpl(req, status);

        final String userName = authDetails[0];

        // if we get here the users password has been updated to take the date off
        NodeRef nodeRef = getPersonService().getPerson(userName);
        // set the new expiry date to 3 months from now.
        Date newExpiryDate = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(newExpiryDate);
        c.add(Calendar.MONTH, 3);
        newExpiryDate = c.getTime();
        getNodeService().setProperty(nodeRef, CtsUserModel.PROPERTY_PASSWORD_EXPIRY_DATE, newExpiryDate);

        //log them out again
        authenticationService.clearCurrentSecurityContext();
        return map;
    }

    private void validatePassword(final WebScriptRequest req) {
        JSONObject json;
        try {
            String content = req.getContent().getContent();
            json = new JSONObject(content);

            String oldPassword;
            String newPassword;

            oldPassword = json.getString(PARAM_OLDPW);
            newPassword = json.getString(PARAM_NEWPW);
            if (oldPassword.equals(newPassword)) {
                throw new WebScriptException(Status.STATUS_BAD_REQUEST, SAME_PASSWORD_ERROR);
            }
            if (!validatePasswordComplexity(newPassword)) {
                throw new WebScriptException(Status.STATUS_BAD_REQUEST, PASSWORD_COMPLEXITY_ERROR);
            }
        } catch (JSONException e) {
            throw new WebScriptException(Status.STATUS_INTERNAL_SERVER_ERROR, "Unable to parse JSON POST body: " + e.getMessage());
        } catch (IOException e) {
            throw new WebScriptException(Status.STATUS_INTERNAL_SERVER_ERROR, "Unable to retrieve JSON POST body: " + e.getMessage());
        }
    }

    private boolean validatePasswordComplexity(String password) {
        if (password.length() < 8) {
            return false;
        }
        Pattern passwordPattern = Pattern.compile("(?=.*[a-z].*[a-z])(?=.*[A-Z].*[A-Z])(?=.*\\d.*\\d)(?=.*[^A-Za-z0-9].*[^A-Za-z0-9])(.*){8,}");
        Matcher passwordMatcher = passwordPattern.matcher(password);
        return passwordMatcher.find();
    }

    private String[] checkUserIsLoggedIn(final WebScriptRequest req) {
        String[] authDetails = AuthenticationUtil.runAs(new AuthenticationUtil.RunAsWork<String[]>() {
            @SuppressWarnings("synthetic-access")
            public String[] doWork() throws Exception {
                String userName = req.getExtensionPath();
                //authenticate the user so stuff works
                JSONObject json;
                try {
                    String content = req.getContent().getContent();
                    json = new JSONObject(content);

                    String oldPassword;

                    if (!json.has(PARAM_OLDPW) || json.getString(PARAM_OLDPW).length() == 0) {
                        throw new WebScriptException(Status.STATUS_BAD_REQUEST, OLDPW_PARAM_ERROR);
                    }
                    oldPassword = json.getString(PARAM_OLDPW);
                    return new String[]{userName,oldPassword};
                } catch (JSONException e) {
                    throw new WebScriptException(Status.STATUS_INTERNAL_SERVER_ERROR, "Unable to parse JSON POST body: " + e.getMessage());
                }
            }

        }, AuthenticationUtil.getSystemUserName());
        if(authDetails!=null) {
            authenticationService.authenticate(authDetails[0],authDetails[1].toCharArray());
        }
        return authDetails;
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

    public AuthorityService getAuthorityService() {
        return authorityService;
    }

    @Override
    public void setAuthorityService(AuthorityService authorityService) {
        super.setAuthorityService(authorityService);
        this.authorityService = authorityService;
    }

    public void setAuthenticationService(AuthenticationService authenticationService) {
        super.setAuthenticationService((MutableAuthenticationService)authenticationService);
        this.authenticationService = authenticationService;
    }
}
