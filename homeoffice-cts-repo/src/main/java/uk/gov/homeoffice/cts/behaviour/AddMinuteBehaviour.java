package uk.gov.homeoffice.cts.behaviour;

import org.alfresco.model.ContentModel;
import org.alfresco.model.ForumModel;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.homeoffice.cts.model.CtsModel;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Class to capture when a minute is added, it will look for a list of quality reviews
 * and strip them out and store in a field, this makes MI much easier.
 * Saves us having to write a custom webscript we can just use the standard CMIS webscript
 * Created by chris on 22/05/2015.
 */
public class AddMinuteBehaviour implements NodeServicePolicies.OnCreateNodePolicy {
    private static final Logger LOGGER = LoggerFactory.getLogger(AddMinuteBehaviour.class);
    private PolicyComponent policyComponent;
    private NodeService nodeService;
    private ContentService contentService;

    public void init() {
        LOGGER.debug("Registering AddMinuteBehaviour");
        Behaviour onCreateNode = new JavaBehaviour(this, "onCreateNode", Behaviour.NotificationFrequency.TRANSACTION_COMMIT);

        this.getPolicyComponent().bindClassBehaviour(
                QName.createQName(NamespaceService.ALFRESCO_URI, "onCreateNode"),
                ForumModel.TYPE_POST,
                onCreateNode);
    }

    @Override
    public void onCreateNode(ChildAssociationRef childAssocRef) {
        LOGGER.debug("Minute added to case");

        NodeRef postNodeRef = childAssocRef.getChildRef();
        String content = getContentService().getReader(postNodeRef,ContentModel.PROP_CONTENT).getContentString();

        //our minutes will be JSON objects
        try {
            if (content.contains("{")) {
                JSONObject jsonObject = new JSONObject(content);

                ComplexMinute complexMinute = buildComplexMinute(jsonObject);
                getContentService().getWriter(postNodeRef, ContentModel.PROP_CONTENT, true).putContent(complexMinute.minute);

                if (complexMinute.reviewOutcome != null && complexMinute.reviewOutcome.length() > 0) {
                    Map<QName, Serializable> props = new HashMap<>();
                    props.put(CtsModel.PROP_MINUTE_QA_REVIEW_OUTCOMES, complexMinute.reviewOutcome);
                    props.put(CtsModel.PROP_MINUTE_QA_REVIEW_TASK, complexMinute.task);
                    getNodeService().addAspect(postNodeRef, CtsModel.ASPECT_MINUTE_QA_REVIEW, props);
                }
            } else {
                getContentService().getWriter(postNodeRef, ContentModel.PROP_CONTENT, true).putContent(content);
            }
            //makes the minute just the minute rather than the JSON
        } catch (JSONException e) {
            //this may occur when a user adds a comment in Share so is not necessarily an error
            LOGGER.debug("Error with JSON: "+content);
        }
    }

    protected ComplexMinute buildComplexMinute(JSONObject jsonObject) throws JSONException {
        String minute = jsonObject.getString("content");


        String reviewOutcome = null;
        if(jsonObject.has("minuteQaReviewOutcomes") && !jsonObject.isNull("minuteQaReviewOutcomes")) {
            StringBuilder sb = new StringBuilder();
            JSONArray reviewOutcomeArray = jsonObject.getJSONArray("minuteQaReviewOutcomes");
            for (int i = 0; i < reviewOutcomeArray.length(); i++) {
                String value = reviewOutcomeArray.getString(i);
                sb.append(value);
                if (i + 1 < reviewOutcomeArray.length()) {
                    sb.append(',');
                }
            }
            reviewOutcome = sb.toString();
        }

        String task = null;
        if(jsonObject.has("task") && !jsonObject.isNull("task")) {
            task = jsonObject.getString("task");
        }
        return new ComplexMinute(minute, reviewOutcome, task);
    }

    public class ComplexMinute{
        String minute,reviewOutcome,task;

        public ComplexMinute(String minute, String reviewOutcome, String task) {
            this.minute = minute;
            this.reviewOutcome = reviewOutcome;
            this.task = task;
        }
    }

    private PolicyComponent getPolicyComponent() {
        return policyComponent;
    }

    public void setPolicyComponent(PolicyComponent policyComponent) {
        this.policyComponent = policyComponent;
    }

    public ContentService getContentService() {
        return contentService;
    }

    public void setContentService(ContentService contentService) {
        this.contentService = contentService;
    }

    public NodeService getNodeService() {
        return nodeService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }
}
