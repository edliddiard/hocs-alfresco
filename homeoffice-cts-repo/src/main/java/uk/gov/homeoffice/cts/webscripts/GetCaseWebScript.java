package uk.gov.homeoffice.cts.webscripts;

import java.io.Serializable;
import java.util.*;
import org.alfresco.repo.template.TemplateNode;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.Cache;
import uk.gov.homeoffice.cts.helpers.CtsFolderHelper;
import uk.gov.homeoffice.cts.helpers.CtsHelper;
import uk.gov.homeoffice.cts.model.CtsCase;
import uk.gov.homeoffice.cts.model.CtsModel;

public class GetCaseWebScript extends DeclarativeWebScript {
    private static final Logger LOGGER = LoggerFactory.getLogger(GetCaseWebScript.class);
    private NodeService nodeService;
    private CtsFolderHelper ctsFolderHelper;
    private CtsHelper ctsHelper;
    private ServiceRegistry serviceRegistry;

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setCtsFolderHelper(CtsFolderHelper ctsFolderHelper) {
        this.ctsFolderHelper = ctsFolderHelper;
    }

    public void setServiceRegistry(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    public void setCtsHelper(CtsHelper ctsHelper) {
        this.ctsHelper = ctsHelper;
    }

    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
        LOGGER.debug("Running GetCaseWebScript");
        String nodeRefString = ctsFolderHelper.getNodeRef(req.getParameter("nodeRef"));
        NodeRef nodeRef = new NodeRef(nodeRefString);
        // Fetch all the properties
        Map<QName, Serializable> caseProps = nodeService.getProperties(nodeRef);
        ctsHelper.encodeStringsForJson(caseProps);

        // Create CtsCase object for template
        CtsCase ctsCase = new CtsCase(caseProps);
        ctsCase.setId(nodeRefString);
        ctsCase.setGroupedCases(getGroupedCases(nodeRef));
        ctsCase.setLinkedCases(getLinkedCases(nodeRef));

        Map<String, Object> model = new HashMap<>();
        model.put("ctsCase", ctsCase);
        model.put("node", new TemplateNode(nodeRef, serviceRegistry, null));
        return model;
    }

    private List<CtsCase> getGroupedCases(NodeRef masterNodeRef) {
        List<AssociationRef> groupedCaseAssocRefList = nodeService.getTargetAssocs(masterNodeRef, CtsModel.ASSOC_GROUPED_CASES);
        List<CtsCase> groupedCases = new ArrayList<>();
        for (AssociationRef groupedCaseAssocRef : groupedCaseAssocRefList) {
            NodeRef groupedCaseNodeRef = groupedCaseAssocRef.getTargetRef();
            Map<QName, Serializable> groupedCaseProps = nodeService.getProperties(groupedCaseNodeRef);
            CtsCase groupedCase = new CtsCase(groupedCaseProps);
            groupedCase.setId(groupedCaseNodeRef.toString());
            groupedCases.add(groupedCase);
        }
        return groupedCases;
    }

    private List<CtsCase> getLinkedCases(NodeRef nodeRef) {
        List<AssociationRef> targetLinkedCaseAssocRefList = nodeService.getTargetAssocs(nodeRef, CtsModel.ASSOC_LINKED_CASES);
        List<AssociationRef> sourceLinkedCaseAssocRefList = nodeService.getSourceAssocs(nodeRef, CtsModel.ASSOC_LINKED_CASES);
        List<CtsCase> linkedCases = new ArrayList<>();
        for (AssociationRef linkedCaseAssocRef : targetLinkedCaseAssocRefList) {
            NodeRef linkedCaseNodeRef = linkedCaseAssocRef.getTargetRef();
            addLinkedCase(linkedCases, linkedCaseNodeRef);
        }
        for (AssociationRef linkedCaseAssocRef : sourceLinkedCaseAssocRefList) {
            NodeRef linkedCaseNodeRef = linkedCaseAssocRef.getSourceRef();
            addLinkedCase(linkedCases, linkedCaseNodeRef);
        }
        return linkedCases;
    }

    private void addLinkedCase(List<CtsCase> linkedCases, NodeRef linkedCaseNodeRef) {
        Map<QName, Serializable> linkedCaseProps = nodeService.getProperties(linkedCaseNodeRef);
        CtsCase linkedCase = new CtsCase(linkedCaseProps);
        linkedCase.setId(linkedCaseNodeRef.toString());
        linkedCases.add(linkedCase);
    }

}