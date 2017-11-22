package uk.gov.homeoffice.cts;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.json.JSONArray;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import uk.gov.homeoffice.cts.model.CtsModel;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.UUID;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by chris davidson on 21/07/2014.
 * Testing Alfresco audits are working
 */
public class CaseAuditIntegrationTest extends CaseIntegrationTest {
    private static String applicationName = "alfresco-access";

    //@Before
    public void setup() throws Exception {
        super.setup();
    }

    /**
     * Checking the audit alfresco-access application is enabled
     * @throws Exception
     */
    //@Test
    public void confirmAuditEnabled() throws Exception{
        String ticket = loginUser();

        String url = "http://" + getAlfrescoHost() + ":" + getAlfrescoPort() + "/alfresco/service/api/audit/control?alf_ticket=" + ticket;
        HttpResponse<JsonNode> jsonNode = Unirest.get(url).asJson();

        System.out.println(jsonNode.getBody().toString());

        boolean foundIt = false;
        JSONArray applications = jsonNode.getBody().getObject().getJSONArray("applications");
        for (int i = 0; i < applications.length(); i++) {
            String name = applications.getJSONObject(i).getString("name");
            if(name.equals(applicationName)){
                boolean enabled = applications.getJSONObject(i).getBoolean("enabled");
                assertThat(enabled, equalTo(true));
                foundIt = true;
                break;
            }
        }
        assertThat("Application not active", foundIt);


    }

    /**
     * Checking that when a folder is created and deleted an audit entry is created
     * with the user name and time
     * //@throws Exception
     */
    //@Test
    public void checkFolderCreateAudit() throws Exception{
        String folderName = UUID.randomUUID().toString();

        try {
            createFolder(folderName);

            String url = "http://" + getAlfrescoHost() + ":" + getAlfrescoPort() + "/alfresco/service/api/audit/query/alfresco-access/alfresco-access/transaction/path?verbose=true&value=/app:company_home/cm:"+folderName+"&alf_ticket=" + ticket;
            HttpResponse<JsonNode> jsonNode = Unirest.get(url).asJson();

            int count = jsonNode.getBody().getObject().getInt("count");
            assertThat(count, equalTo(1));

            String user = jsonNode.getBody().getObject().getJSONArray("entries").getJSONObject(0).getString("user");
            assertThat(user, equalTo("admin"));

            String time = jsonNode.getBody().getObject().getJSONArray("entries").getJSONObject(0).getString("time");
            assertThat(time, notNullValue());

            String action = jsonNode.getBody().getObject().getJSONArray("entries").getJSONObject(0).getJSONObject("values").getString("/alfresco-access/transaction/action");
            assertThat(action, equalTo("CREATE"));

        } finally {
            deleteFolder(folderName);
        }
        String url = "http://" + getAlfrescoHost() + ":" + getAlfrescoPort() + "/alfresco/service/api/audit/query/alfresco-access/alfresco-access/transaction/path?verbose=true&value=/app:company_home/cm:"+folderName+"&alf_ticket=" + ticket;
        HttpResponse<JsonNode> jsonNode = Unirest.get(url).asJson();

        int count = jsonNode.getBody().getObject().getInt("count");
        assertThat(count, equalTo(2));

        String action = jsonNode.getBody().getObject().getJSONArray("entries").getJSONObject(1).getJSONObject("values").getString("/alfresco-access/transaction/action");
        assertThat(action, equalTo("DELETE"));
    }

    //@Test
    public void checkCaseCreateAudit() throws Exception{
        String folderName = UUID.randomUUID().toString();

        try {
            createFolder(folderName);

            createCase(folderName,ticket);

            String caseUrl = "http://" + getAlfrescoHost() + ":" + getAlfrescoPort() + "/alfresco/service/api/audit/query/alfresco-access/alfresco-access/transaction/path?verbose=true&value=/app:company_home/cm:"+folderName+"/cm:"+ caseName.replace(" " ,"%20")+"&alf_ticket=" + ticket;
            HttpResponse<JsonNode> jsonNode = Unirest.get(caseUrl).asJson();

            int count = jsonNode.getBody().getObject().getInt("count");
            assertThat(count, equalTo(1));

            String user = jsonNode.getBody().getObject().getJSONArray("entries").getJSONObject(0).getString("user");
            assertThat(user, equalTo("admin"));

            String time = jsonNode.getBody().getObject().getJSONArray("entries").getJSONObject(0).getString("time");
            assertThat(time, notNullValue());

            String action = jsonNode.getBody().getObject().getJSONArray("entries").getJSONObject(0).getJSONObject("values").getString("/alfresco-access/transaction/action");
            assertThat(action, equalTo("CREATE"));

            String type = jsonNode.getBody().getObject().getJSONArray("entries").getJSONObject(0).getJSONObject("values").getString("/alfresco-access/transaction/type");
            assertThat(type, equalTo("cts:case"));

        } finally {
            HttpResponse<String> response = Unirest
                    .delete("http://" + getAlfrescoHost()+ ":" + getAlfrescoPort()+ "/alfresco/service/cmis/p/" + folderName + "/"
                            + caseName + "?alf_ticket=" + ticket)
                    .asString();

            deleteFolder(folderName);
        }
        String url = "http://" + getAlfrescoHost() + ":" + getAlfrescoPort() + "/alfresco/service/api/audit/query/alfresco-access/alfresco-access/transaction/path?verbose=true&value=/app:company_home/cm:"+folderName+"/cm:New%20Case%20Test&alf_ticket=" + ticket;
        HttpResponse<JsonNode> jsonNode = Unirest.get(url).asJson();

        int count = jsonNode.getBody().getObject().getInt("count");
        assertThat(count, equalTo(2));

        String action = jsonNode.getBody().getObject().getJSONArray("entries").getJSONObject(1).getJSONObject("values").getString("/alfresco-access/transaction/action");
        assertThat(action, equalTo("DELETE"));
    }


    /**
     * Check that a property creation and update are picked up by system
     * @throws Exception
     */
    //@Test
    public void checkCaseEditStatusAudit() throws Exception{
        String folderName = UUID.randomUUID().toString();

        try {
            //create a case
            createFolder(folderName);

            HttpResponse<String> createResponse = createCase(folderName,ticket);

            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                    .parse(new InputSource(new StringReader(createResponse.getBody())));

            // Get the document's root XML node
            NodeList root = doc.getChildNodes();

            Node entryNode = ctsAtomParser.getNode("entry", root);
            String title = ctsAtomParser.getNodeValue("title", entryNode.getChildNodes());

            Node objectNode = ctsAtomParser.getNode("cmisra:object", entryNode.getChildNodes());
            Node propertiesNode = ctsAtomParser.getNode("cmis:properties", objectNode.getChildNodes());

            Node nodeRefNode = ctsAtomParser.getNodeByPropertyDefinition("cmis:objectId", propertiesNode.getChildNodes());
            String nodeRef = ctsAtomParser.getNodeValue("cmis:value", nodeRefNode.getChildNodes());
            String id = nodeRef.substring(24);
            //updates its status
            String status1 = "New";
            String status2 = "Active";
            updateStatus(id,status1 );

            String caseUrl = "http://" + getAlfrescoHost() + ":" + getAlfrescoPort()
                    + "/alfresco/service/api/audit/query/alfresco-access/alfresco-access/transaction/node?verbose=true&value="
                    +nodeRef+"&valueType=org.alfresco.service.cmr.repository.NodeRef&alf_ticket=" + ticket;
            HttpResponse<JsonNode> jsonNode = Unirest.get(caseUrl).asJson();

            JSONArray entriesArray = jsonNode.getBody().getObject().getJSONArray("entries");
            for (int i = 0; i < entriesArray.length(); i++) {
                String action = entriesArray.getJSONObject(i).getJSONObject("values").getString("/alfresco-access/transaction/action");
                if(action.equals("updateNodeProperties")){
                    String changes = entriesArray.getJSONObject(i).getJSONObject("values").getString("/alfresco-access/transaction/properties/add");
                    assertThat("Should contain our type",changes.contains(CtsModel.PROP_CASE_STATUS.toString()));
                    assertThat("Should contain our value",changes.contains(status1));
                }
            }

            //update again
            updateStatus(id,status2);
            HttpResponse<JsonNode> jsonNode2 = Unirest.get(caseUrl).asJson();

            JSONArray entriesArray2 = jsonNode2.getBody().getObject().getJSONArray("entries");
            for (int i = 0; i < entriesArray2.length(); i++) {
                String action = entriesArray2.getJSONObject(i).getJSONObject("values").getString("/alfresco-access/transaction/action");
                if(action.equals("updateNodeProperties")){
                    //check it is not the initial add
                    if (entriesArray2.getJSONObject(i).getJSONObject("values").has("/alfresco-access/transaction/properties/add")){
                        continue;
                    }

                    String from = entriesArray2.getJSONObject(i).getJSONObject("values").getString("/alfresco-access/transaction/properties/from");
                    assertThat("Should contain our type",from.contains(CtsModel.PROP_CASE_STATUS.toString()));
                    assertThat("Should contain old value",from.contains(status1));

                    String to = entriesArray2.getJSONObject(i).getJSONObject("values").getString("/alfresco-access/transaction/properties/to");
                    assertThat("Should contain our type",to.contains(CtsModel.PROP_CASE_STATUS.toString()));
                    assertThat("Should contain our new value",to.contains(status2));
                }
            }

        } finally {
            HttpResponse<String> response = Unirest
                    .delete("http://" + getAlfrescoHost()+ ":" + getAlfrescoPort()+ "/alfresco/service/cmis/p/" + folderName + "/"
                            + caseName + "?alf_ticket=" + ticket)
                    .asString();
            deleteFolder(folderName);
        }
    }
    //@Test
    public void checkViewProperties() throws Exception {
        String folderName = UUID.randomUUID().toString();

        try {
            //create a case
            createFolder(folderName);

            HttpResponse<String> createResponse = createCase(folderName, ticket);

            String nodeRef = getNoderef(createResponse);
            String id = nodeRef.substring(24);

            //now view the properties
            String url = "http:" + getAlfrescoHost()+ ":" + getAlfrescoPort()+ "/alfresco/service/cmis/i/"+id+"?alf_ticket=" + ticket;

            Unirest.get(url);

        }finally{
            HttpResponse<String> response = Unirest
                    .delete("http://" + getAlfrescoHost()+ ":" + getAlfrescoPort()+ "/alfresco/service/cmis/p/" + folderName + "/"
                            + caseName + "?alf_ticket=" + ticket)
                    .asString();
            deleteFolder(folderName);
        }
        //http://localhost:8080/alfresco/cmisatom/50d70085-6f2a-489c-94e7-bfca096d9b65/id?id=workspace%3A%2F%2FSpacesStore%2F2f79657e-bc4a-433b-af21-0c63afa2a684&filter=*&includeAllowableActions=true&includeACL=true&includePolicyIds=true&includeRelationships=both&renditionFilter=*

        //alfresco/service/cmis/i/

    }

    //@Test
    public void checkAddDocument() throws Exception {
        String folderName = UUID.randomUUID().toString();
        String documentName = "a document to add to a case";
        try {
            //create a case
            createFolder(folderName);

            HttpResponse<String> createResponse = createCase(folderName, ticket);

            String nodeRef = getNoderef(createResponse);
            String id = nodeRef.substring(24);

            //add a document

            String url = "http://" + getAlfrescoHost()+ ":" + getAlfrescoPort()+ "/alfresco/service/homeoffice/cts/document?alf_ticket=" + ticket;
            File file = new File("src/test/resources/content.txt");

            assertThat("File does not exist"+file.getAbsolutePath(),file.exists());

            System.out.println(file.getAbsolutePath());

            HttpResponse<String> postResponse = Unirest.post(url).field("name",documentName)
                    .field("destination",nodeRef).field("documenttype","cts:caseDocument")
                    .field("documentdescription", "some text").field("file",file).asString();

            assertThat("bad code"+postResponse.getCode(),postResponse.getCode() == 200);

            String caseUrl = "http://" + getAlfrescoHost() + ":" + getAlfrescoPort() + "/alfresco/service/api/audit/query/alfresco-access/alfresco-access/transaction/path?verbose=true&value=/app:company_home/cm:"+folderName+"/cm:"+ caseName.replace(" " ,"%20")+"/cm:"+ documentName.replace(" " ,"%20")+"&alf_ticket=" + ticket;
            HttpResponse<JsonNode> jsonNode = Unirest.get(caseUrl).asJson();

            int count = jsonNode.getBody().getObject().getInt("count");
            assertThat(count, equalTo(1));

            String user = jsonNode.getBody().getObject().getJSONArray("entries").getJSONObject(0).getString("user");
            assertThat(user, equalTo("admin"));

            String time = jsonNode.getBody().getObject().getJSONArray("entries").getJSONObject(0).getString("time");
            assertThat(time, notNullValue());

            String action = jsonNode.getBody().getObject().getJSONArray("entries").getJSONObject(0).getJSONObject("values").getString("/alfresco-access/transaction/action");
            assertThat(action, equalTo("CREATE"));

            String type = jsonNode.getBody().getObject().getJSONArray("entries").getJSONObject(0).getJSONObject("values").getString("/alfresco-access/transaction/type");
            assertThat(type, equalTo("cts:caseDocument"));
        }finally{
            HttpResponse<String> deleteDocResponse = Unirest
                    .delete("http://" + getAlfrescoHost()+ ":" + getAlfrescoPort()+ "/alfresco/service/cmis/p/" + folderName + "/"
                            + caseName  + "/"
                            + documentName + "?alf_ticket=" + ticket)
                    .asString();

            HttpResponse<String> response = Unirest
                    .delete("http://" + getAlfrescoHost()+ ":" + getAlfrescoPort()+ "/alfresco/service/cmis/p/" + folderName + "/"
                            + caseName + "?alf_ticket=" + ticket)
                    .asString();
            deleteFolder(folderName);
        }
        //http://localhost:8080/alfresco/cmisatom/50d70085-6f2a-489c-94e7-bfca096d9b65/id?id=workspace%3A%2F%2FSpacesStore%2F2f79657e-bc4a-433b-af21-0c63afa2a684&filter=*&includeAllowableActions=true&includeACL=true&includePolicyIds=true&includeRelationships=both&renditionFilter=*

        //alfresco/service/cmis/i/

    }

    private String getNoderef(HttpResponse<String> createResponse) throws ParserConfigurationException, IOException, SAXException {
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                .parse(new InputSource(new StringReader(createResponse.getBody())));

        // Get the document's root XML node
        NodeList root = doc.getChildNodes();

        Node entryNode = ctsAtomParser.getNode("entry", root);
        String title = ctsAtomParser.getNodeValue("title", entryNode.getChildNodes());

        Node objectNode = ctsAtomParser.getNode("cmisra:object", entryNode.getChildNodes());
        Node propertiesNode = ctsAtomParser.getNode("cmis:properties", objectNode.getChildNodes());

        Node nodeRefNode = ctsAtomParser.getNodeByPropertyDefinition("cmis:objectId", propertiesNode.getChildNodes());
        String nodeRef = ctsAtomParser.getNodeValue("cmis:value", nodeRefNode.getChildNodes());
        return nodeRef;
    }

    private void updateStatus(String id, String status) throws UnirestException {
        String url = "http://" + getAlfrescoHost()+ ":" + getAlfrescoPort()+ "/alfresco/service/cmis/s/workspace:SpacesStore/i/"+id;

        String putData = "<?xml version='1.0' encoding='utf-8'?>"
                + "<entry xmlns='http://www.w3.org/2005/Atom'"
                + " xmlns:cmis='http://docs.oasis-open.org/ns/cmis/core/200908/'"
                + " xmlns:cmisra='http://docs.oasis-open.org/ns/cmis/restatom/200908/'>"
                + "<cmisra:object><cmis:properties>"
                + "<cmis:propertyString queryName='cts:caseStatus' localName='cts:caseStatus' propertyDefinitionId='cts:caseStatus'>"
                + "<cmis:value>"+status+"</cmis:value>"
                + "</cmis:propertyString>"
                + "</cmis:properties></cmisra:object>"
                + "</entry>";


        HttpResponse<String> response = Unirest.put(url+"?alf_ticket=" + ticket)
                .header("Content-Type", "application/atom+xml;type=entry")
                .body(putData)
                .asString();


        assertThat(response.getCode(), equalTo(200));
    }
}
