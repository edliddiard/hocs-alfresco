package uk.gov.homeoffice.cts.behaviour;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import uk.gov.homeoffice.cts.model.AuditMessage;

import java.io.Serializable;
import java.net.URL;
import java.util.Map;

/**
 * Creates an event stream so that we don't need to master the data in Alfresco.
 */
public class AuditBehaviour implements PropertyUpdateBehaviour {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuditBehaviour.class);

    private ObjectMapper objectMapper = new ObjectMapper();

    private String reportingEndpoint = "";

    @Override
    public void onUpdateProperties(final NodeRef nodeRef, final Map<QName, Serializable> before, final Map<QName, Serializable> after) {

        ClassLoader classLoader = AuditBehaviour.class.getClassLoader();
        URL resource = classLoader.getResource("org/apache/http/message/BasicLineFormatter.class");
        LOGGER.error(resource.toString());

        AuditMessage auditMessage = new AuditMessage(after);
        postMessage(auditMessage);
    }

    public void setReportingEndpoint(String reportingEndpoint) {
        this.reportingEndpoint = reportingEndpoint;
    }

    private void postMessage(AuditMessage auditMessage){

        String json = "";
        try {
            json = objectMapper.writeValueAsString(auditMessage);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            LOGGER.error(auditMessage.toString());
        }

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> httpEntity = new HttpEntity<>(json, httpHeaders);

        ResponseEntity<String> response = restTemplate.exchange(reportingEndpoint, HttpMethod.POST, httpEntity, String.class);

        if(response.getStatusCode() != HttpStatus.OK)
        {
            LOGGER.error(auditMessage.toString());
        }
    }
}