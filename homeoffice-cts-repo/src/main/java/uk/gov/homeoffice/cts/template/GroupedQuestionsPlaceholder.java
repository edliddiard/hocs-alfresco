package uk.gov.homeoffice.cts.template;

import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import uk.gov.homeoffice.cts.model.CtsCase;
import uk.gov.homeoffice.cts.model.CtsModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by davidt on 11/11/2014.
 */
public class GroupedQuestionsPlaceholder extends Placeholder {

    public GroupedQuestionsPlaceholder() {
    }

    public String getValue(NodeRef masterNodeRef, NodeService nodeService) {
        StringBuilder sb = new StringBuilder();
        for (CtsCase groupedCase : getGroupedCases(masterNodeRef, nodeService)) {
            sb.append(groupedCase.getQuestionText());
            sb.append(" [");
            sb.append(groupedCase.getUin());
            sb.append("]");
        }
        return sb.toString();
    }

    private List<CtsCase> getGroupedCases(NodeRef masterNodeRef, NodeService nodeService) {
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
}
