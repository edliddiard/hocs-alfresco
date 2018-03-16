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
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

/**
 * Creates an event stream so that we don't need to master the data in Alfresco.
 */
public class AuditBehaviour implements PropertyUpdateBehaviour {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuditBehaviour.class);

    private String reportingEndpoint;
    private String port;
    private String username;
    private String password;


    private static ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onUpdateProperties(final NodeRef nodeRef, final Map<QName, Serializable> before, final Map<QName, Serializable> after) {

        // allow alfresco to run without reporting service
        if(getReportingEndpoint() != null && !getReportingEndpoint().equals("")) {
            try {
                AuditMessage auditMessage = new AuditMessage(after);
                postMessage(auditMessage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String getReportingEndpoint(){ return  reportingEndpoint;}

    public void setReportingEndpoint(String reportingEndpoint) {
        this.reportingEndpoint = reportingEndpoint;
    }

    public String getPort(){ return  port;}

    public void setPort(String port) {
        this.port = port;
    }

    public String getUsername(){ return  username;}

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword(){ return  password;}

    public void setPassword(String password) {
        this.password = password;
    }

    private void postMessage(AuditMessage auditMessage) throws NoSuchAlgorithmException, KeyManagementException, URISyntaxException {

        try {

            LOGGER.info("Sending Reporting Event");

            String str = objectMapper.writeValueAsString(auditMessage);
            sendViaRest(str);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }

    private void sendViaRest(String msg) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> httpEntity = new HttpEntity<>(msg, httpHeaders);
        ResponseEntity<String> response = restTemplate.exchange(reportingEndpoint+ "/event/add", HttpMethod.POST, httpEntity, String.class);
        if(response.getStatusCode() != HttpStatus.OK)
        {
            System.out.println("Sent via Rest FAILED");
            LOGGER.info(msg);
        } else {
            System.out.println("Sent via Rest");
        }
    }
}