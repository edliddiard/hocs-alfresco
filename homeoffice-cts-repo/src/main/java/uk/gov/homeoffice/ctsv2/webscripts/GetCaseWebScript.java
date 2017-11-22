package uk.gov.homeoffice.ctsv2.webscripts;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.namespace.QName;
import org.apache.chemistry.opencmis.commons.data.CmisExtensionElement;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.CmisExtensionElementImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import uk.gov.homeoffice.cts.helpers.CtsFolderHelper;
import uk.gov.homeoffice.cts.model.CtsModel;
import uk.gov.homeoffice.cts.permissions.CtsPermissions;
import uk.gov.homeoffice.cts.permissions.PermissionChecker;
import uk.gov.homeoffice.ctsv2.model.CtsCase;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.*;

public class GetCaseWebScript extends AbstractWebScript {
    private static final Logger LOGGER = LoggerFactory.getLogger(GetCaseWebScript.class);
    private NodeService nodeService;
    private CtsFolderHelper ctsFolderHelper;
    private ServiceRegistry serviceRegistry;
    private PermissionChecker permissionChecker;
    private PermissionService permissionService;
    private CaseMapperHelper caseMapperHelper;


    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setCtsFolderHelper(CtsFolderHelper ctsFolderHelper) {
        this.ctsFolderHelper = ctsFolderHelper;
    }

    public void setServiceRegistry(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {
        LOGGER.debug("Running GetCaseWebScript");
        String nodeRefString = ctsFolderHelper.getNodeRef(req.getParameter("nodeRef"));
        NodeRef nodeRef = new NodeRef(nodeRefString);
        // Fetch all the properties
        Map<QName, Serializable> caseProps = nodeService.getProperties(nodeRef);

        // Create CtsCase object for template
        CtsCase ctsCase = CtsCase.getCtsCase(caseProps);
        ctsCase.setId(nodeRefString);
        ctsCase.setGroupedCases(getGroupedCases(nodeRef));
        ctsCase.setLinkedCases(getLinkedCases(nodeRef));
        ctsCase.setDisplayStatus(caseMapperHelper.getCaseDisplayStatus(ctsCase.getCaseStatus()));
        ctsCase.setDisplayTask(caseMapperHelper.getCaseDisplayTask(ctsCase.getCaseTask()));
        ctsCase.setCaseProgressStatus(caseMapperHelper.getCaseProgressStatus(ctsCase.getCaseStatus()));
        // Generate JSON response
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        // jackson library defaults
        mapper.enable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        mapper.enable(SerializationFeature.WRAP_EXCEPTIONS);
        mapper.enable(SerializationFeature.FLUSH_AFTER_WRITE_VALUE);
        // custom options
        // Note: we must use TwoLetterISO8601TimeZone format for all dates to keep consistent with v1 api and frontend
        // where T denotes time and X is the 2 digit ISO 8601 time zone
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX"));
        mapper.setTimeZone(TimeZone.getDefault());
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.enable(SerializationFeature.CLOSE_CLOSEABLE);
        mapper.enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY);

        /**
         * Generate JSON response
         */
        // converting object to string value
        StringWriter writer = new StringWriter();

        // Add the CtsCase properties to the CtsCase object
        JsonFactory jsonFactory = new JsonFactory();
        JsonGenerator jsonGenerator = jsonFactory.createGenerator(writer);
        jsonGenerator.setCodec(mapper);
        jsonGenerator.writeStartObject();
        jsonGenerator.writeFieldName("ctsCase");
        jsonGenerator.writeObject(ctsCase);
        jsonGenerator.writeFieldName("properties");
        jsonGenerator.writeStartObject();
        jsonGenerator.writeFieldName("permissions");
        jsonGenerator.writeObject(getCasePermissions(nodeRef));
        jsonGenerator.writeEndObject();
        jsonGenerator.writeEndObject();
        jsonGenerator.close();
        writer.close();

        res.setContentType("application/json; charset=UTF-8");
        res.setContentEncoding("UTF-8");
        res.setHeader("Cache-Control", "must-revalidate,no-cache,no-store");
        res.getWriter().write(writer.toString());
    }

    /**
     * Add CtsCase Permissions
     * This Permissions list is inherited from the cts.lib.json.ftl template which retrieves CMIS Document AllowableActions
     */
    private Map<String, Object> getCasePermissions(NodeRef nodeRef) {

        //TemplateNode node = new TemplateNode(nodeRef, serviceRegistry, null);
        List<CmisExtensionElement> extensions = new ArrayList<>();
        //This needs to be a list and in configuration
        String permission = CtsPermissions.ALLOCATE;
        if (getPermissionChecker().hasPermission(nodeRef, permission)) {
            CmisExtensionElement cmisExtensionElement = new CmisExtensionElementImpl(null, permission, null, "true");
            extensions.add(cmisExtensionElement);
        }
        List<CmisExtensionElement> propertyPermissions = getPermissionChecker().getPropertyPermissions(nodeRef);
        for (CmisExtensionElement propertyPermission : propertyPermissions) {
            extensions.add(propertyPermission);
        }

        /**
         * Add Permissions
         */
        Map<String, Object> propertyPermissionsMap = new HashMap<String, Object>();
        for (CmisExtensionElement element : extensions) {
            if (element.getName().equals("canAssignUser")) {
                propertyPermissionsMap.put(element.getName(), Boolean.parseBoolean(element.getValue()));
            }
        }

        /**
         * Add AllowedActions - retrieved from PermissionService
         * Only these allowable actions are checked in the PHP front end
         * - canAssignUser
         * - canUpdateProperties
         * - canDeleteObject
         * See: https://gist.github.com/dawudr/7033fcfdf209036b3674d7b8bdebc008
         */
        propertyPermissionsMap.put("canUpdateProperties", getPermissionService().hasPermission(nodeRef, PermissionService.WRITE_PROPERTIES).toString().equalsIgnoreCase("ALLOWED"));
        propertyPermissionsMap.put("canDeleteObject", getPermissionService().hasPermission(nodeRef, PermissionService.DELETE).toString().equalsIgnoreCase("ALLOWED"));

        /**
         * Additional permissions
         */
        propertyPermissionsMap.put("canGetFolderTree", getPermissionService().hasPermission(nodeRef, PermissionService.READ_CHILDREN).toString().equalsIgnoreCase("ALLOWED"));
        propertyPermissionsMap.put("canGetProperties", getPermissionService().hasPermission(nodeRef, PermissionService.READ_PROPERTIES).toString().equalsIgnoreCase("ALLOWED"));
        propertyPermissionsMap.put("canGetObjectRelationships", getPermissionService().hasPermission(nodeRef, PermissionService.READ_ASSOCIATIONS).toString().equalsIgnoreCase("ALLOWED"));
        propertyPermissionsMap.put("canGetObjectParents", getPermissionService().hasPermission(nodeRef, PermissionService.READ_ASSOCIATIONS).toString().equalsIgnoreCase("ALLOWED"));
        propertyPermissionsMap.put("canGetFolderParent", getPermissionService().hasPermission(nodeRef, PermissionService.READ_ASSOCIATIONS).toString().equalsIgnoreCase("ALLOWED"));
        propertyPermissionsMap.put("canGetDescendants", getPermissionService().hasPermission(nodeRef, PermissionService.READ).toString().equalsIgnoreCase("ALLOWED"));
        propertyPermissionsMap.put("canMoveObject", getPermissionService().hasPermission(nodeRef, PermissionService.WRITE).toString().equalsIgnoreCase("ALLOWED"));
        propertyPermissionsMap.put("canApplyPolicy", getPermissionChecker().getAuthorityService().authorityExists(nodeRef.toString()));
        propertyPermissionsMap.put("canGetAppliedPolicies", getPermissionService().hasPermission(nodeRef, PermissionService.READ_PERMISSIONS).toString().equalsIgnoreCase("ALLOWED"));
        propertyPermissionsMap.put("canRemovePolicy", getPermissionChecker().getAuthorityService().authorityExists(nodeRef.toString()));
        propertyPermissionsMap.put("canGetChildren", getPermissionService().hasPermission(nodeRef, PermissionService.READ_CHILDREN).toString().equalsIgnoreCase("ALLOWED"));
        propertyPermissionsMap.put("canCreateDocument", getPermissionService().hasPermission(nodeRef, PermissionService.ADD_CHILDREN).toString().equalsIgnoreCase("ALLOWED"));
        propertyPermissionsMap.put("canCreateFolder", getPermissionService().hasPermission(nodeRef, PermissionService.CREATE_CHILDREN).toString().equalsIgnoreCase("ALLOWED"));
        propertyPermissionsMap.put("canCreateRelationship", getPermissionService().hasPermission(nodeRef, PermissionService.CREATE_ASSOCIATIONS).toString().equalsIgnoreCase("ALLOWED"));
        propertyPermissionsMap.put("canDeleteTree", getPermissionService().hasPermission(nodeRef, PermissionService.DELETE_CHILDREN).toString().equalsIgnoreCase("ALLOWED"));
        propertyPermissionsMap.put("canGetACL", getPermissionService().hasPermission(nodeRef, PermissionService.READ_PERMISSIONS).toString().equalsIgnoreCase("ALLOWED"));
        propertyPermissionsMap.put("canApplyACL", getPermissionService().hasPermission(nodeRef, PermissionService.CHANGE_PERMISSIONS).toString().equalsIgnoreCase("ALLOWED"));
        return propertyPermissionsMap;
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

    public PermissionChecker getPermissionChecker() {
        return permissionChecker;
    }

    public void setPermissionChecker(PermissionChecker permissionChecker) {
        this.permissionChecker = permissionChecker;
    }

    public PermissionService getPermissionService() {
        return permissionService;
    }

    public void setPermissionService(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    public void setCaseMapperHelper(CaseMapperHelper caseMapperHelper) {
        this.caseMapperHelper = caseMapperHelper;
    }
}
