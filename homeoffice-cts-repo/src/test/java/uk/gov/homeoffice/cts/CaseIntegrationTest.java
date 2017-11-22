package uk.gov.homeoffice.cts;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.NodeList;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import uk.gov.homeoffice.cts.helpers.AlfrescoPropertiesFileReader;
import uk.gov.homeoffice.cts.helpers.CtsAtomParser;
import java.util.UUID;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.notNullValue;

public class CaseIntegrationTest {

    protected static final String caseName = "New Case Test";
    private static final String dateReceived = "2014-07-18T00:00:00.000+01:00";
    private static final String correspondenceType = "UKVI_MINISTERIAL";
    protected CtsAtomParser ctsAtomParser;
    private AlfrescoPropertiesFileReader propertiesReader = new AlfrescoPropertiesFileReader();
    protected String ticket;

    //@Before
    public void setup() throws Exception {
        ctsAtomParser = new CtsAtomParser();
        ticket = loginUser();
    }
    
    public String getAlfrescoHost() {
        return propertiesReader.getProperty("test.hostname");
    }
    
    public int getAlfrescoPort() {
        return Integer.parseInt(propertiesReader.getProperty("test.port"));
    }

    protected String loginUser() throws Exception {
        HttpResponse<JsonNode> response = Unirest
                .post("http://" + getAlfrescoHost()+ ":" + getAlfrescoPort()+ "/alfresco/service/api/login")
                .body("{\"username\":\"admin\",\"password\":\"admin\"}")
                .asJson();

        return response.getBody().getObject().getJSONObject("data").getString("ticket");
    }

    protected void createFolder(String folderName) throws Exception{
        String postData = "" +
                "<?xml version='1.0' encoding='utf-8'?>\n" +
                "<entry xmlns=\"http://www.w3.org/2005/Atom\" xmlns:cmisra=\"http://docs.oasis-open.org/ns/cmis/restatom/200908/\" xmlns:cmis=\"http://docs.oasis-open.org/ns/cmis/core/200908/\">\n" +
                " <title>" + folderName + "</title>\n" +
                " <cmisra:object>\n" +
                "   <cmis:properties>\n" +
                "     <cmis:propertyId propertyDefinitionId=\"cmis:objectTypeId\">\n" +
                "       <cmis:value>cmis:folder</cmis:value>\n" +
                "     </cmis:propertyId>\n" +
                "   </cmis:properties>\n" +
                " </cmisra:object>\n" +
                "</entry>";

        HttpResponse<String> response = Unirest
                .post("http://" + getAlfrescoHost()+ ":" + getAlfrescoPort()+ "/alfresco/service/cmis/p/children?alf_ticket=" + ticket)
                .header("Content-Type", "application/atom+xml;type=entry")
                .body(postData)
                .asString();

        assertThat(response.getCode(), equalTo(201));
    }

    //@Test
    public void getFolderTest() throws Exception{
        String folderName = UUID.randomUUID().toString();

        try {
            createFolder(folderName);
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                    .parse("http://" + getAlfrescoHost()+ ":" + getAlfrescoPort()+ "/alfresco/service/cmis/p/" + folderName + "?alf_ticket=" + ticket);

            // Get the document's root XML node
            NodeList root = doc.getChildNodes();

            Node entryNode = ctsAtomParser.getNode("entry", root);
            String title = ctsAtomParser.getNodeValue("title", entryNode.getChildNodes());

            assertThat(title, equalTo(folderName));
        } finally {
            deleteFolder(folderName);
        }

    }

    //@Test
    public void createCaseTest() throws Exception{
        String folderName = UUID.randomUUID().toString();

        try {
            createFolder(folderName);

            HttpResponse<String> response = createCase(folderName,ticket);

            assertThat(response.getCode(), equalTo(201));

            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                    .parse("http://" + getAlfrescoHost()+ ":" + getAlfrescoPort()+ "/alfresco/service/cmis/p/" + folderName + "/"
                            + caseName + "?alf_ticket=" + ticket);

            // Get the document's root XML node
            NodeList root = doc.getChildNodes();

            Node entryNode = ctsAtomParser.getNode("entry", root);
            String title = ctsAtomParser.getNodeValue("title", entryNode.getChildNodes());

            Node objectNode = ctsAtomParser.getNode("cmisra:object", entryNode.getChildNodes());
            Node propertiesNode = ctsAtomParser.getNode("cmis:properties", objectNode.getChildNodes());

            Node dateReceivedNode = ctsAtomParser.getNodeByPropertyDefinition("cts:dateReceived", propertiesNode.getChildNodes());
            String dateReceivedResponse = ctsAtomParser.getNodeValue("cmis:value", dateReceivedNode.getChildNodes());

            Node correspondenceNode = ctsAtomParser.getNodeByPropertyDefinition("cts:correspondenceType", propertiesNode.getChildNodes());
            String correspondenceTypeResponse = ctsAtomParser.getNodeValue("cmis:value", correspondenceNode.getChildNodes());

            Node urnSuffixNode = ctsAtomParser.getNodeByPropertyDefinition("cts:urnSuffix", propertiesNode.getChildNodes());
            String urnSuffixNodeResponse = ctsAtomParser.getNodeValue("cmis:value", urnSuffixNode.getChildNodes());

            assertThat(title, equalTo(caseName));
            assertThat(dateReceivedResponse, equalTo(dateReceived));
            assertThat(correspondenceTypeResponse, equalTo(correspondenceType));
            assertThat(urnSuffixNodeResponse, notNullValue());

        } finally {
            HttpResponse<String> response = Unirest
                    .delete("http://" + getAlfrescoHost()+ ":" + getAlfrescoPort()+ "/alfresco/service/cmis/p/" + folderName + "/"
                            + caseName + "?alf_ticket=" + ticket)
                    .asString();

            assertThat(response.getCode(), equalTo(204));
            deleteFolder(folderName);
        }
    }

    protected HttpResponse<String> createCase(String folderName, String ticket) throws UnirestException {
        String postData = "" +
                "<?xml version='1.0' encoding='utf-8'?>\n" +
                "<entry xmlns=\"http://www.w3.org/2005/Atom\" xmlns:cmisra=\"http://docs.oasis-open.org/ns/cmis/restatom/200908/\" xmlns:cmis=\"http://docs.oasis-open.org/ns/cmis/core/200908/\">\n" +
                " <title>" + caseName + "</title>\n" +
                " <cmisra:object>\n" +
                "   <cmis:properties>\n" +
                "     <cmis:propertyId propertyDefinitionId=\"cmis:objectTypeId\">\n" +
                "       <cmis:value>F:cts:case</cmis:value>\n" +
                "     </cmis:propertyId>\n" +
                "     <cmis:propertyString propertyDefinitionId=\"cts:correspondenceType\">\n" +
                "       <cmis:value>" + correspondenceType + "</cmis:value>\n" +
                "     </cmis:propertyString>\n" +
                "     \n" +
                "     <cmis:propertyDateTime propertyDefinitionId=\"cts:dateReceived\">\n" +
                "       <cmis:value>" + dateReceived + "</cmis:value>\n" +
                "     </cmis:propertyDateTime>\n" +
                "     <cmis:propertyBoolean propertyDefinitionId=\"cts:priority\">\n" +
                "       <cmis:value>true</cmis:value>\n" +
                "     </cmis:propertyBoolean>\n" +
                "     \n" +
                "   </cmis:properties>\n" +
                " </cmisra:object>\n" +
                "</entry>";

        HttpResponse<String> response = Unirest
                .post("http://" + getAlfrescoHost()+ ":" + getAlfrescoPort()+ "/alfresco/service/cmis/p/" + folderName + "/children?alf_ticket=" + ticket)
                .header("Content-Type", "application/atom+xml;type=entry")
                .body(postData)
                .asString();
        return response;
    }

    protected void deleteFolder(String folderName) throws Exception {
        HttpResponse<String> response = Unirest
                .delete("http://" + getAlfrescoHost()+ ":" + getAlfrescoPort()+ "/alfresco/service/cmis/p/" + folderName + "?alf_ticket=" + ticket)
                .asString();

        assertThat(response.getCode(), equalTo(204));
    }
}
