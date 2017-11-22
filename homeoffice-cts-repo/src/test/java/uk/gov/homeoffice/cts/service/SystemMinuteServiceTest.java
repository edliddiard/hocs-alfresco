package uk.gov.homeoffice.cts.service;

import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.junit.Test;
import org.alfresco.service.cmr.dictionary.DictionaryService;

import java.util.InvalidPropertiesFormatException;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * test for the String formatting in system minutes
 * Created by chris on 07/08/2014.
 */
public class SystemMinuteServiceTest {
    private final String properties = "{{http:\\/\\/www.alfresco.org\\/model\\/content\\/1.0}name=0000069," +
            "{http:\\/\\/www.alfresco.org\\/model\\/system\\/1.0}node-dbid=1944, " +
            "{http:\\/\\/www.alfresco.org\\/model\\/system\\/1.0}store-identifier=SpacesStore, " +
            "{http:\\/\\/www.alfresco.org\\/model\\/content\\/1.0}modified=Thu Aug 07 09:35:34 BST 2014, " +
            "{http:\\/\\/www.alfresco.org\\/model\\/system\\/1.0}node-uuid=01de1385-a4eb-41d9-b578-375fe72d61ba, " +
            "{http:\\/\\/www.alfresco.org\\/model\\/system\\/1.0}locale=en_GB, " +
            "{http:\\/\\/www.alfresco.org\\/model\\/content\\/1.0}created=Thu Aug 07 09:35:34 BST 2014, " +
            "{http:\\/\\/www.alfresco.org\\/model\\/system\\/1.0}store-protocol=workspace, " +
            "{http:\\/\\/www.alfresco.org\\/model\\/content\\/1.0}creator=admin, " +
            "{http:\\/\\/www.alfresco.org\\/model\\/content\\/1.0}modifier=admin}";

    @Test
    public void testGetProperties(){
        SystemMinutesService systemMinutesService = new SystemMinutesService();
        String[] props = systemMinutesService.getPropertiesAsAnArray(properties);
        assertEquals("{http:\\/\\/www.alfresco.org\\/model\\/content\\/1.0}name=0000069", props[0] );
        assertEquals("{http:\\/\\/www.alfresco.org\\/model\\/system\\/1.0}node-dbid=1944", props[1] );
        assertEquals("{http:\\/\\/www.alfresco.org\\/model\\/system\\/1.0}store-identifier=SpacesStore", props[2] );
        assertEquals("{http:\\/\\/www.alfresco.org\\/model\\/content\\/1.0}modified=Thu Aug 07 09:35:34 BST 2014", props[3] );
        assertEquals("{http:\\/\\/www.alfresco.org\\/model\\/system\\/1.0}node-uuid=01de1385-a4eb-41d9-b578-375fe72d61ba", props[4] );
        assertEquals("{http:\\/\\/www.alfresco.org\\/model\\/system\\/1.0}locale=en_GB", props[5] );
        assertEquals("{http:\\/\\/www.alfresco.org\\/model\\/content\\/1.0}created=Thu Aug 07 09:35:34 BST 2014", props[6] );
        assertEquals("{http:\\/\\/www.alfresco.org\\/model\\/system\\/1.0}store-protocol=workspace", props[7] );
        assertEquals("{http:\\/\\/www.alfresco.org\\/model\\/content\\/1.0}creator=admin", props[8] );
        assertEquals("{http:\\/\\/www.alfresco.org\\/model\\/content\\/1.0}modifier=admin", props[9] );
    }



    @Test
    public void testWorkflowProperties() throws InvalidPropertiesFormatException {
        String properties = "{{http://cts-beta.homeoffice.gov.uk/model/content/1.0}caseWorkflowStatus={\"transitions\":[{\"label\":\"Approve\",\"value\": \"Approve\",\"manualAllocate\": false,colour: \"green\"},{\"label\":\"Return\",\"value\": \"Reject\",\"manualAllocate\" = true,colour: \"red\",\"allocateHeader\": \"Reallocate for drafter\"}]}, {http://cts-beta.homeoffice.gov.uk/model/content/1.0}caseStatus=Obtain sign-off, {http://www.alfresco.org/model/content/1.0}modified=Mon Nov 03 10:23:06 GMT 2014, {http://cts-beta.homeoffice.gov.uk/model/content/1.0}caseTask=Lords Minister's sign-off, {http://cts-beta.homeoffice.gov.uk/model/content/1.0}assignedUser=admin, {http://cts-beta.homeoffice.gov.uk/model/content/1.0}assignedTeam=null}";

        SystemMinutesService systemMinutesService = new SystemMinutesService();
        Map<String,String> props = systemMinutesService.parsePropertiesAsAnArray(properties);
        System.out.println(props.keySet());
        assertEquals(5, props.size());
        assertNull(props.get("{http://cts-beta.homeoffice.gov.uk/model/content/1.0}caseWorkflowStatus"));
        assertEquals("Obtain sign-off",props.get("{http://cts-beta.homeoffice.gov.uk/model/content/1.0}caseStatus"));
        assertEquals("Lords Minister's sign-off",props.get("{http://cts-beta.homeoffice.gov.uk/model/content/1.0}caseTask"));
        assertEquals("admin",props.get("{http://cts-beta.homeoffice.gov.uk/model/content/1.0}assignedUser"));
        assertEquals("null",props.get("{http://cts-beta.homeoffice.gov.uk/model/content/1.0}assignedTeam"));
        assertEquals("Mon Nov 03 10:23:06 GMT 2014",props.get("{http://www.alfresco.org/model/content/1.0}modified"));
    }

    @Test
    public void testWorkflowPropertiesAgain() throws InvalidPropertiesFormatException {
        String properties = "{{http://cts-beta.homeoffice.gov.uk/model/content/1.0}caseStatus=Obtain sign-off, {http://www.alfresco.org/model/content/1.0}modified=Mon Nov 03 10:23:06 GMT 2014, {http://cts-beta.homeoffice.gov.uk/model/content/1.0}caseTask=Lords Minister's sign-off, {http://cts-beta.homeoffice.gov.uk/model/content/1.0}assignedUser=admin, {http://cts-beta.homeoffice.gov.uk/model/content/1.0}assignedTeam=null}";

        SystemMinutesService systemMinutesService = new SystemMinutesService();
        Map<String,String> props = systemMinutesService.parsePropertiesAsAnArray(properties);
        System.out.println(props.keySet());
        assertEquals(5, props.size());

    }
    @Test
    public void testFastForward() throws InvalidPropertiesFormatException {
        SystemMinutesService systemMinutesService = new SystemMinutesService();
        String s = "{{\"foo\": \"boo\",\"boo\": \"foo\"},[{},{}]}then some more stuff";
        int i = systemMinutesService.fastForwardThroughJSON(s,0);
        assertEquals("then some more stuff",s.substring(i));

    }

    @Test
    public void testGetProperty(){
        SystemMinutesService systemMinutesService = new SystemMinutesService();
        // mock dictionary service and property definition
        DictionaryService dictionaryService = mock(DictionaryService.class);
        PropertyDefinition propDef = mock(PropertyDefinition.class);
        when(propDef.getTitle()).thenReturn("name");
        when(dictionaryService.getProperty(QName.createQName("{http:\\/\\/www.alfresco.org\\/model\\/content\\/1.0}name"))).thenReturn(propDef);
        systemMinutesService.setDictionaryService(dictionaryService);
        assertEquals("name", systemMinutesService.getPropertyWithoutNamespace("{http:\\/\\/www.alfresco.org\\/model\\/content\\/1.0}name"));
    }

    @Test
    public void testGetPropertyNull(){
        SystemMinutesService systemMinutesService = new SystemMinutesService();
        assertNull(systemMinutesService.getPropertyWithoutNamespace(null));
    }

    @Test
    public void testGetPropertyEmpty(){
        SystemMinutesService systemMinutesService = new SystemMinutesService();
        assertEquals("", systemMinutesService.getPropertyWithoutNamespace(""));
    }

    @Test
    public void testGetPropertyNoNameSpace(){
        SystemMinutesService systemMinutesService = new SystemMinutesService();
        // mock dictionary service and property definition
        DictionaryService dictionaryService = mock(DictionaryService.class);
        PropertyDefinition propDef = mock(PropertyDefinition.class);
        when(propDef.getTitle()).thenReturn("aproperty");
        when(dictionaryService.getProperty(QName.createQName("{}aproperty"))).thenReturn(propDef);
        systemMinutesService.setDictionaryService(dictionaryService);
        assertEquals("aproperty",systemMinutesService.getPropertyWithoutNamespace("aproperty"));
    }

    @Test
    public void testGetNodeRefFromCommentDocument(){
        SystemMinutesService systemMinutesService = new SystemMinutesService();
        NodeRef nodeRef = systemMinutesService.getNodeRefFromComment("Document added workspace://SpacesStore/132b4404-6e4b-4364-ba84-407c0f84338a");
        assertEquals(new NodeRef("workspace://SpacesStore/132b4404-6e4b-4364-ba84-407c0f84338a"),nodeRef);
    }

    @Test
    public void testGetNodeRefFromCommentSlaveAdded(){
        SystemMinutesService systemMinutesService = new SystemMinutesService();
        NodeRef nodeRef = systemMinutesService.getNodeRefFromComment("Slave added workspace://SpacesStore/132b4404-6e4b-4364-ba84-407c0f84338a");
        assertEquals(new NodeRef("workspace://SpacesStore/132b4404-6e4b-4364-ba84-407c0f84338a"),nodeRef);
    }

    @Test
    public void testGetNodeRefFromCommentSlaveRemoved(){
        SystemMinutesService systemMinutesService = new SystemMinutesService();
        NodeRef nodeRef = systemMinutesService.getNodeRefFromComment("Slave removed workspace://SpacesStore/132b4404-6e4b-4364-ba84-407c0f84338a");
        assertEquals(new NodeRef("workspace://SpacesStore/132b4404-6e4b-4364-ba84-407c0f84338a"),nodeRef);
    }

    @Test
    public void testGetNodeRefFromCommentMasterAdded(){
        SystemMinutesService systemMinutesService = new SystemMinutesService();
        NodeRef nodeRef = systemMinutesService.getNodeRefFromComment("Master added workspace://SpacesStore/132b4404-6e4b-4364-ba84-407c0f84338a");
        assertEquals(new NodeRef("workspace://SpacesStore/132b4404-6e4b-4364-ba84-407c0f84338a"),nodeRef);
    }

    @Test
    public void testGetNodeRefFromCommentMasterRemoved(){
        SystemMinutesService systemMinutesService = new SystemMinutesService();
        NodeRef nodeRef = systemMinutesService.getNodeRefFromComment("Master removed workspace://SpacesStore/132b4404-6e4b-4364-ba84-407c0f84338a");
        assertEquals(new NodeRef("workspace://SpacesStore/132b4404-6e4b-4364-ba84-407c0f84338a"),nodeRef);
    }

    @Test
    public void testGetNodeRefFromCommentNoNodeRef(){
        SystemMinutesService systemMinutesService = new SystemMinutesService();
        NodeRef nodeRef = systemMinutesService.getNodeRefFromComment("Document added 132b4404-6e4b-4364-ba84-407c0f84338a");
        assertNull(nodeRef);
    }

    @Test
    public void testCreateGroupedCaseMinuteText() {
        SystemMinutesService systemMinutesService = new SystemMinutesService();
        StringBuilder sb = new StringBuilder();
        systemMinutesService.createGroupedCaseMinuteText(sb, "Minute text", "UIN1234", "URN5678");
        assertEquals("Minute text - UIN: UIN1234, HRN: URN5678\n", sb.toString());
    }

    @Test
    public void testCreateLinkedCaseMinuteText() {
        SystemMinutesService systemMinutesService = new SystemMinutesService();
        StringBuilder sb = new StringBuilder();
        systemMinutesService.createLinkedCaseMinuteText(sb, "Minute text", "URN5678");
        assertEquals("Minute text - HRN: URN5678\n", sb.toString());
    }

    @Test
    public void testGetNodeRefFromCommentEmpty(){
        SystemMinutesService systemMinutesService = new SystemMinutesService();
        NodeRef nodeRef = systemMinutesService.getNodeRefFromComment("");
        assertNull(nodeRef);
    }

    @Test
    public void testGetNodeRefFromCommentNull(){
        SystemMinutesService systemMinutesService = new SystemMinutesService();
        NodeRef nodeRef = systemMinutesService.getNodeRefFromComment(null);
        assertNull(nodeRef);
    }

    @Test
    public void testSplitComment() {
        SystemMinutesService systemMinutesService = new SystemMinutesService();
        String comment = "comment 1;comment 2, comment 2.1;comment 3";
        String[] expectedSplitComments = {"comment 1", "comment 2, comment 2.1", "comment 3"};
        String[] splitComments = systemMinutesService.splitComment(comment);
        assertEquals(3, splitComments.length);
        assertArrayEquals(expectedSplitComments, splitComments);
    }
}
