package uk.gov.homeoffice.cts.service;

import org.alfresco.model.ContentModel;
import org.alfresco.model.ForumModel;
import org.alfresco.service.cmr.repository.*;
import org.alfresco.service.cmr.search.QueryConsistency;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.alfresco.util.ISO8601DateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.homeoffice.cts.model.CtsMinute;
import uk.gov.homeoffice.cts.model.CtsMinuteAudit;
import uk.gov.homeoffice.cts.model.CtsModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Service that gets the comments on a case and presents them as minutes
 * Created by chris on 06/08/2014.
 */
public class ManualMinutesService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ManualMinutesService.class);
    private NodeService nodeService;
    private ContentService contentService;
    private SearchService searchService;
    //default to 100
    private int maxSearchResults = 100;
    final private static String MANUAL = "manual";

    /**
     * Method to get all manual minutes in a format for reporting database
     * @param date
     * @return
     */
    public List<CtsMinuteAudit> getLatestMinutes(Date date){
        List<CtsMinuteAudit> minutes = new ArrayList<>();

        String cmisQuery = "SELECT * FROM fm:post WHERE cmis:lastModificationDate > TIMESTAMP \'"+
                ISO8601DateFormat.format(date)+
                "\' ORDER BY cmis:lastModificationDate ASC";
        LOGGER.debug(cmisQuery);

        SearchParameters searchParameters = new SearchParameters();
        searchParameters.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
        searchParameters.setLanguage(SearchService.LANGUAGE_CMIS_ALFRESCO);
        searchParameters.setMaxItems(getMaxSearchResults());
        searchParameters.setQuery(cmisQuery);
        //this makes it use SOLR
        searchParameters.setQueryConsistency(QueryConsistency.EVENTUAL);
        ResultSet resultSet = getSearchService().query(searchParameters);
        List<NodeRef> nodeRefs = resultSet.getNodeRefs();
        LOGGER.debug("Number of results in search for minutes = "+ nodeRefs.size());
        for (NodeRef nodeRef : nodeRefs) {
            Map<QName, Serializable> props = getNodeService().getProperties(nodeRef);
            //get the case
            NodeRef commentsNodeRef = getNodeService().getPrimaryParent(nodeRef).getParentRef();
            NodeRef discussionNodeRef = getNodeService().getPrimaryParent(commentsNodeRef).getParentRef();
            NodeRef caseNodeRef = getNodeService().getPrimaryParent(discussionNodeRef).getParentRef();
            if(getNodeService().getType(caseNodeRef).equals(CtsModel.TYPE_CTS_CASE)){
                //add this to the list
                CtsMinuteAudit ctsMinuteAudit = new CtsMinuteAudit(caseNodeRef,
                        nodeRef,
                        (Date)props.get(ContentModel.PROP_MODIFIED),
                        (Long)props.get(ContentModel.PROP_NODE_DBID),
                        (Date)props.get(ContentModel.PROP_CREATED),
                        getContentService().getReader(nodeRef,ContentModel.PROP_CONTENT).getContentString(),
                        (String)props.get(ContentModel.PROP_CREATOR),
                        MANUAL
                        );
                LOGGER.debug("Adding minute "+ctsMinuteAudit.getJsonObject().toString());
                minutes.add(ctsMinuteAudit);
            }
        }
        return minutes;
    }

    public List<CtsMinute> getManualMinutes(NodeRef nodeRef){
        List<CtsMinute> manualMinutes = new ArrayList<>();

        if(getNodeService().hasAspect(nodeRef, ForumModel.ASPECT_DISCUSSABLE)) {
            List<ChildAssociationRef> childAssocs = getNodeService().getChildAssocs(nodeRef, ForumModel.ASSOC_DISCUSSION, RegexQNamePattern.MATCH_ALL);
            ChildAssociationRef forumNode = childAssocs.get(0);
            NodeRef commentsNodeRef = getNodeService().getChildByName(forumNode.getChildRef(), ContentModel.ASSOC_CONTAINS, "Comments");
            List<ChildAssociationRef> commentsChildAssocs = getNodeService().getChildAssocs(commentsNodeRef);
            for (ChildAssociationRef childAssociationRef : commentsChildAssocs) {
                NodeRef commentNodeRef = childAssociationRef.getChildRef();
                Map<QName,Serializable> props = getNodeService().getProperties(commentNodeRef);
                Long dbid = (Long)props.get(ContentModel.PROP_NODE_DBID);
                Date date = (Date)props.get(ContentModel.PROP_CREATED);
                String content;
                try {
                    content = getContentService().getReader(commentNodeRef, ContentModel.PROP_CONTENT).getContentString();
                } catch (ContentIOException e) {
                    LOGGER.error("Minutes entry is missing for: " + commentNodeRef + " and case node ref:" +  nodeRef.getId() + " Error message: " + e.getMessage());
                    continue;
                }
                String user = (String) props.get(ContentModel.PROP_CREATOR);

                CtsMinute ctsMinute = new CtsMinute(
                    dbid,
                    date,
                    content,
                    user,
                    MANUAL
                );
                if(props.get(CtsModel.PROP_MINUTE_QA_REVIEW_OUTCOMES) != null){
                    String qaOutcomes = (String)props.get(CtsModel.PROP_MINUTE_QA_REVIEW_OUTCOMES);
                    ctsMinute.setMinuteQaReviewOutcomes(qaOutcomes);
                }
                if(props.get(CtsModel.PROP_MINUTE_QA_REVIEW_TASK) != null){
                    String qaTask = (String)props.get(CtsModel.PROP_MINUTE_QA_REVIEW_TASK);
                    ctsMinute.setQaTask(qaTask);
                }
                manualMinutes.add(ctsMinute);
            }
        }

        return manualMinutes;
    }

    public NodeService getNodeService() {
        return nodeService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public ContentService getContentService() {
        return contentService;
    }

    public void setContentService(ContentService contentService) {
        this.contentService = contentService;
    }

    public SearchService getSearchService() {
        return searchService;
    }

    public void setSearchService(SearchService searchService) {
        this.searchService = searchService;
    }

    public int getMaxSearchResults() {
        return maxSearchResults;
    }

    public void setMaxSearchResults(int maxSearchResults) {
        this.maxSearchResults = maxSearchResults;
    }
}
