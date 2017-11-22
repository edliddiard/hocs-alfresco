package uk.gov.homeoffice.cts.autocreate;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.homeoffice.cts.helpers.CtsFolderHelper;
import uk.gov.homeoffice.cts.model.CorrespondenceType;
import uk.gov.homeoffice.cts.model.CtsModel;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * Created by dave on 31/12/2014.
 */
public class AutoCreateFromDocumentBehaviour implements NodeServicePolicies.OnCreateNodePolicy {
    private static final Logger LOGGER = LoggerFactory.getLogger(AutoCreateFromDocumentBehaviour.class);
    private NodeService nodeService;
    private PolicyComponent policyComponent;
    private CtsFolderHelper ctsFolderHelper;

    public void init() {
        LOGGER.debug("Registering Behaviour");
        Behaviour onCreateNode = new JavaBehaviour(this, "onCreateNode", Behaviour.NotificationFrequency.TRANSACTION_COMMIT);

        this.getPolicyComponent().bindClassBehaviour(
                QName.createQName(NamespaceService.ALFRESCO_URI, "onCreateNode"),
                ContentModel.TYPE_CONTENT,
                onCreateNode);
    }

    @Override
    public void onCreateNode(ChildAssociationRef childAssocRef) throws RuntimeException {
        NodeRef addedDocument = childAssocRef.getChildRef();
        NodeRef firstParent = childAssocRef.getParentRef();
        NodeRef secondParent = nodeService.getPrimaryParent(firstParent).getParentRef();
        String firstParentName = (String) nodeService.getProperty(firstParent, ContentModel.PROP_NAME);
        String secondParentName = (String) nodeService.getProperty(secondParent, ContentModel.PROP_NAME);
        // only create case if the document is dropped into a folder within the "Auto Create" folder
        // and it exists in the list of allowed auto create case folder names.
        if (secondParentName.equals(CtsFolderHelper.CTS_AUTO_CREATE_PARENT_FOLDER_NAME)) {
            if(ctsFolderHelper.getAutoCreateCaseFolderNames().contains(firstParentName)) {
                // catch any runtime exceptions and apply the failure aspect
                try {
                    createCase(addedDocument, firstParentName);
                } catch (RuntimeException e) {
                    LOGGER.error("Error with auto create",e);
                    applyFailureAspect(addedDocument, e.getMessage());
                    return;
                }
                // check if the document has moved from the original folder
                // if not, apply the failure aspect
                NodeRef newParent = nodeService.getPrimaryParent(addedDocument).getParentRef();
                if (newParent.toString().equals(firstParent.toString())) {
                    applyFailureAspect(addedDocument, "document parent did not change.");
                }
            } else {
                LOGGER.warn("Folder not in allowed folders list");
                applyFailureAspect(addedDocument, "Folder not in allowed folders list");
            }
        } else {
            //do nothing as it means it is not an auto created document
        }
    }

    /**
     * Apply the autoCreateFailure aspect to the document and set the message property.
     * @param addedDocument NodeRef
     * @param errorMessage String
     */
    private void applyFailureAspect(NodeRef addedDocument, String errorMessage) {
        Map<QName, Serializable> props = new HashMap();
        props.put(CtsModel.PROP_AUTO_CREATE_FAILURE_DATETIME, new Date());
        props.put(CtsModel.PROP_AUTO_CREATE_FAILURE_MESSAGE, "Auto create case failed: " + errorMessage);
        nodeService.addAspect(addedDocument, CtsModel.ASPECT_AUTO_CREATE_FAILURE, props);
    }

    /**
     * Given a document and case type, create a new cts case folder within Cases
     * and move the document into that folder.
     * @param addedDocument NodeRef
     * @param caseType String
     */
    private void createCase(NodeRef addedDocument, String caseType) {
        String year = Integer.toString(Calendar.getInstance().get(Calendar.YEAR));
        // get the Cases folder
        NodeRef containingNodeRef = ctsFolderHelper.getOrCreateCtsCaseContainerFolder(caseType, year);
        // create a cts case folder within Cases
        ChildAssociationRef caseFolderChildRef = nodeService.createNode(containingNodeRef, ContentModel.ASSOC_CONTAINS, ContentModel.ASSOC_CHILDREN, CtsModel.TYPE_CTS_CASE, getDefaultCaseProperties(caseType, addedDocument));
        NodeRef caseFolderNodeRef = caseFolderChildRef.getChildRef();
        // move the document into the folder and let the create case behaviour do the rest
        nodeService.moveNode(addedDocument, caseFolderNodeRef, ContentModel.ASSOC_CONTAINS, ContentModel.ASSOC_CHILDREN);
    }

    /**
     * Return a map of case properties required to create the case.
     * @param caseType String
     * @param addedDocument
     * @return Map<QName, Serializable>
     */
    private Map<QName, Serializable> getDefaultCaseProperties(String caseType, NodeRef addedDocument) {
        Map<QName, Serializable> props = new HashMap();
        props.put(CtsModel.PROP_AUTO_CREATED_CASE, true);
        props.put(CtsModel.PROP_CORRESPONDENCE_TYPE, caseType);
        props.put(CtsModel.PROP_DATE_RECEIVED, new Date());
        props.put(CtsModel.PROP_CASE_STATUS, "New");
        props.put(CtsModel.PROP_CASE_TASK, "Create case");
        if (CorrespondenceType.HMPO_COMPLAINT.getCode().equals(caseType)) {
            props.put(CtsModel.PROP_HMPO_STAGE, "Stage 1");
        }

        //now get the assigned* values from the document
        props.put(CtsModel.PROP_ASSIGNED_UNIT, getNodeService().getProperty(addedDocument, CtsModel.PROP_DOCUMENT_UNIT));
        props.put(CtsModel.PROP_ASSIGNED_TEAM, getNodeService().getProperty(addedDocument, CtsModel.PROP_DOCUMENT_TEAM));
        props.put(CtsModel.PROP_ASSIGNED_USER, getNodeService().getProperty(addedDocument, CtsModel.PROP_DOCUMENT_USER));
        return props;
    }

    private NodeService getNodeService() {
        return nodeService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    private PolicyComponent getPolicyComponent() {
        return policyComponent;
    }

    public void setPolicyComponent(PolicyComponent policyComponent) {
        this.policyComponent = policyComponent;
    }

    public CtsFolderHelper getCtsFolderHelper() {
        return ctsFolderHelper;
    }

    public void setCtsFolderHelper(CtsFolderHelper ctsFolderHelper) {
        this.ctsFolderHelper = ctsFolderHelper;
    }

}
