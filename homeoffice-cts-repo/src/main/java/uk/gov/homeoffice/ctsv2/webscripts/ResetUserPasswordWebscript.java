package uk.gov.homeoffice.ctsv2.webscripts;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.security.MutableAuthenticationService;
import org.alfresco.service.cmr.security.PersonService;
import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import uk.gov.homeoffice.cts.email.EmailService;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

public class ResetUserPasswordWebscript extends AbstractWebScript {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResetUserPasswordWebscript.class);

    private PersonService personService;
    private MutableAuthenticationService authenticationService;
    private EmailService emailService;
    private String ctsMailSubjectResetUserPassword;
    private String ctsMailTemplateResetUserPassword;

    @Override
    public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {
        LOGGER.debug("Running ResetUserPasswordWebscript");
        final String username = req.getParameter("username");

        Map<String, Object> resultMap = AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<Map<String, Object>>() {
            public Map<String, Object> doWork() throws Exception {
                boolean success;
                NodeRef personNode = personService.getPersonOrNull(username);
                if (personNode != null) {
                    //We retrieve the real username because users can enter their username case insensitive and the call
                    //to set the authentication later on expects the username to be case sensitive
                    String realUserName = personService.getPerson(personNode).getUserName();

                    final String newPassword = RandomStringUtils.randomAlphanumeric(12);
                    authenticationService.setAuthentication(realUserName, newPassword.toCharArray());

                    final Map<String, String> templateData = new HashMap<>();
                    templateData.put("password", newPassword);
                    emailService.sendEmail(realUserName, ctsMailSubjectResetUserPassword, ctsMailTemplateResetUserPassword, "/login", null, templateData);
                    LOGGER.info("Password reset for user " + realUserName);
                    success =true;
                } else {
                    success = false;
                }
                Map<String, Object> resultMap = new HashMap<>();
                resultMap.put("result", success);
                return resultMap;
            }
        });

        res.setContentType("application/json; charset=UTF-8");
        res.setContentEncoding("UTF-8");
        res.setHeader("Cache-Control", "must-revalidate,no-cache,no-store");
        res.getWriter().write(generateJsonResponse((Boolean)resultMap.get("result")));
    }

    private String generateJsonResponse(boolean success) throws IOException {
        StringWriter writer = new StringWriter();
        JsonFactory jsonFactory = new JsonFactory();
        JsonGenerator jsonGenerator = jsonFactory.createGenerator(writer);
        jsonGenerator.writeStartObject();
        jsonGenerator.writeFieldName("status");
        jsonGenerator.writeObject(success ? "SUCCESS" : "ERROR");
        jsonGenerator.writeEndObject();
        jsonGenerator.close();
        writer.close();
        return writer.toString();
    }

    public void setPersonService(PersonService personService) {
        this.personService = personService;
    }

    public void setAuthenticationService(MutableAuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }

    public void setCtsMailSubjectResetUserPassword(String ctsMailSubjectResetUserPassword) {
        this.ctsMailSubjectResetUserPassword = ctsMailSubjectResetUserPassword;
    }

    public void setCtsMailTemplateResetUserPassword(String ctsMailTemplateResetUserPassword) {
        this.ctsMailTemplateResetUserPassword = ctsMailTemplateResetUserPassword;
    }
}
