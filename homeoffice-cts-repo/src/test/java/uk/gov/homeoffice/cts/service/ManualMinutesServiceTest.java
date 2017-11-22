package uk.gov.homeoffice.cts.service;


import org.alfresco.model.ContentModel;
import org.alfresco.model.ForumModel;
import org.alfresco.service.cmr.repository.*;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.homeoffice.cts.model.CtsMinute;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ManualMinutesServiceTest {


    private ManualMinutesService toTest;

    @Mock private SearchService searchService;
    @Mock private NodeService nodeService;
    @Mock private ContentService contentService;
    @Mock private ChildAssociationRef childRef;
    @Mock private ContentReader reader;
    @Mock private ContentWriter writer;

    private NodeRef nodeRef;
    private NodeRef commentsNodeRef;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        toTest = new ManualMinutesService();
        toTest.setContentService(contentService);
        toTest.setNodeService(nodeService);
        toTest.setSearchService(searchService);
        nodeRef = new NodeRef("workspace://SpacesStore/3e4abc39-d0be-437e-a92f-343bdc845cf9");
        commentsNodeRef = new NodeRef("workspace://SpacesStore/132b4404-6e4b-4364-ba84-407c0f84338a");

    }

    @Test
    public void manualMinutesHappyPath() {
        mockServices();
        when(reader.getContentString()).thenReturn("Test-Comment-1");
        final List<CtsMinute> manualMinutes = toTest.getManualMinutes(nodeRef);

        assertEquals(1,manualMinutes.size());
        assertEquals("Test-Comment-1", manualMinutes.get(0).getText());

        //verify
        verify(reader).getContentString();

    }

    @Test
    public void whenContentFileMissingInS3() {
        mockServices();
        when(reader.getContentString()).thenThrow(new ContentIOException("Content file is missing in S3 bucket"));
        final List<CtsMinute> manualMinutes = toTest.getManualMinutes(nodeRef);

        assertEquals(0,manualMinutes.size());

        //verify
        verify(contentService).getReader(commentsNodeRef, ContentModel.PROP_CONTENT);
        verify(reader).getContentString();

    }

    private void mockServices() {
        when(nodeService.hasAspect(nodeRef, ForumModel.ASPECT_DISCUSSABLE)).thenReturn(true);
        when(nodeService.getChildAssocs(nodeRef, ForumModel.ASSOC_DISCUSSION, RegexQNamePattern.MATCH_ALL)).thenReturn(Collections.singletonList(childRef));
        when(nodeService.getChildByName(any(NodeRef.class), eq(ContentModel.ASSOC_CONTAINS), eq("Comments"))).thenReturn(commentsNodeRef);
        when(nodeService.getChildAssocs(commentsNodeRef)).thenReturn(Collections.singletonList(childRef));
        when(childRef.getChildRef()).thenReturn(commentsNodeRef);
        when(contentService.getReader(commentsNodeRef, ContentModel.PROP_CONTENT)).thenReturn(reader);
    }
}
