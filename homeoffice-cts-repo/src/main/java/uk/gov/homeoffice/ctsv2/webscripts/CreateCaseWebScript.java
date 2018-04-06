package uk.gov.homeoffice.ctsv2.webscripts;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.extensions.webscripts.servlet.FormData;
import uk.gov.homeoffice.cts.email.EmailService;
import uk.gov.homeoffice.cts.helpers.CreateCaseHelper;
import uk.gov.homeoffice.cts.model.FileDetails;
import uk.gov.homeoffice.cts.util.FileIOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CreateCaseWebScript extends AbstractWebScript {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateCaseWebScript.class);
    private CreateCaseHelper createCaseHelper;
    private EmailService emailService;
    private String caseCreatedEmailTemplateId;
    private String ctsUrl;



    @Override
    public void execute(WebScriptRequest webScriptRequest, WebScriptResponse res) throws IOException {
        LOGGER.debug("Running CreateCaseWebScript");

        String caseType = null;
        FileDetails fileDetails = null;
        String emailAddress = null;
        String numberOfAtachements = null;

        FormData formData = (FormData) webScriptRequest.parseContent();
        for (FormData.FormField formField : formData.getFields()) {
            switch (formField.getName()) {
                case "caseType":
                    caseType = formField.getValue();
                    break;
                case "fromEmail":
                    emailAddress = formField.getValue();
                    break;
                case "numberFiles":
                    numberOfAtachements = formField.getValue();
                    break;
                case "file":
                    if (formField.getIsFile()) {
                        File file = File.createTempFile("uploaded_file", "");
                        InputStream is = formField.getInputStream();
                        FileIOUtils.writeToFile(formField.getInputStream(), file);
                        is.close();
                        fileDetails = new FileDetails(file, formField.getFilename(), formField.getMimetype());
                    }
                    break;
            }
        }

        if (fileDetails == null) {
            res.setStatus(400);
            res.getWriter().write("Uploaded file cannot be located in request");
        } else if (caseType == null || caseType.isEmpty()) {
            res.setStatus(400);
            res.getWriter().write("caseType cannot be located in request");
        } else {
            List<FileDetails> files = new ArrayList<>();
            files.add(fileDetails);
            try {
                String caseRef = createCaseHelper.createCase(files, caseType.toUpperCase());
                for (FileDetails fd : files) {
                    fd.getFile().delete();
                }
                res.setHeader("Cache-Control", "must-revalidate,no-cache,no-store");
                res.setContentType("application/json; charset=UTF-8");
                res.setContentEncoding("UTF-8");
                res.getWriter().write(generateJsonResponse(caseRef));
                LOGGER.debug("Completed CreateCaseWebScript");

                HashMap<String, String> personalisation = new HashMap<>();
                personalisation.put("caseType", caseType);
                personalisation.put("caseRef", caseRef);
                personalisation.put("noOfAttachments", numberOfAtachements);
                personalisation.put("link", getCtsUrl() + "/cts/cases/view/" + caseRef);

                LOGGER.debug("templateid = " + getCaseCreatedEmailTemplateId() + " | emailAddress = " + emailAddress + " | Personalisation = " + personalisation);
                emailService.sendEmail(getCaseCreatedEmailTemplateId(), emailAddress, personalisation);

            } catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
                res.setStatus(500);
                res.getWriter().write(ex.getMessage());
                LOGGER.debug("Unsuccessfully Completed CreateCaseWebScript");
            }
        }
    }

    private String generateJsonResponse(String caseRef) throws IOException {
        // Generate JSON response
        ObjectMapper mapper = new ObjectMapper();
        StringWriter writer = new StringWriter();
        JsonFactory jsonFactory = new JsonFactory();
        JsonGenerator jsonGenerator = jsonFactory.createGenerator(writer);
        jsonGenerator.setCodec(mapper);

        jsonGenerator.writeStartObject();
        jsonGenerator.writeFieldName("caseRef");
        jsonGenerator.writeObject(caseRef);
        jsonGenerator.writeEndObject();

        jsonGenerator.close();
        writer.close();
        return writer.toString();
    }

    public void setCreateCaseHelper(CreateCaseHelper createCaseHelper) {
        this.createCaseHelper = createCaseHelper;
    }

    public String getCaseCreatedEmailTemplateId() {
        return caseCreatedEmailTemplateId;
    }

    public void setWorkFlowEmailTemplateId(String caseCreatedEmailTemplateId) {
        this.caseCreatedEmailTemplateId = caseCreatedEmailTemplateId;
    }

    private String getCtsUrl() {
        return ctsUrl;
    }

    public void setCtsUrl(String ctsUrl) {
        this.ctsUrl = ctsUrl;
    }


}
