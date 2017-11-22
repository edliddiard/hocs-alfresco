package uk.gov.homeoffice.cts.helpers;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.model.Repository;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.homeoffice.cts.model.CorrespondenceType;

import java.util.ArrayList;
import java.util.List;

public class CtsFolderHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(CtsFolderHelper.class);

    private static final String WORKSPACE = "workspace://SpacesStore/";
    private static final String CTS_ROOT_FOLDER_NAME = "CTS";
    private static final String CTS_CASES_FOLDER_NAME = "Cases";
    private static final String CTS_DOCUMENT_TEMPLATES_FOLDER_NAME = "Document Templates";
    private static final String CTS_STANDARD_LINES_FOLDER_NAME = "Standard Lines";

    public static final String CTS_AUTO_CREATE_PARENT_FOLDER_NAME = "Auto Create";
    public static final String CTS_AUTO_CREATE_MIN_FOLDER_NAME = CorrespondenceType.DCU_MINISTERIAL.getCode();
    public static final String CTS_AUTO_CREATE_TRO_FOLDER_NAME = CorrespondenceType.DCU_TREAT_OFFICIAL.getCode();
    public static final String CTS_AUTO_CREATE_DTEN_FOLDER_NAME = CorrespondenceType.DCU_NUMBER_10.getCode();
    public static final String CTS_AUTO_CREATE_HMPO_COM_FOLDER_NAME = CorrespondenceType.HMPO_COMPLAINT.getCode();
    public static final String CTS_AUTO_CREATE_UKVI_M_REF_FOLDER_NAME = CorrespondenceType.UKVI_M_REF.getCode();
    public static final String CTS_AUTO_CREATE_UKVI_B_REF_FOLDER_NAME = CorrespondenceType.UKVI_B_REF.getCode();
    public static final String CTS_AUTO_CREATE_UKVI_UTEN_FOLDER_NAME = CorrespondenceType.UKVI_NUMBER_10.getCode();

    private ServiceRegistry registry;
    private Repository repository;
    private NodeService nodeService;

    public CtsFolderHelper(ServiceRegistry serviceRegistry, Repository repository, NodeService nodeService) {
        this.registry = serviceRegistry;
        this.repository = repository;
        this.nodeService = nodeService;
    }

    public String getNodeRef(String ref) {
        return WORKSPACE + ref;
    }

    private NodeRef createPath(List<String> pathElements) {
        NodeRef nodeRef = null;
        for (int i = 0; i < pathElements.size(); i++) {
            List<String> subPath = pathElements.subList(0, i+1);
            try {
                LOGGER.debug("Finding node... {}", subPath.toString());
                nodeRef = findNode(subPath);
            } catch (Exception e) {
                LOGGER.debug("Creating node... {}", subPath.toString());
                nodeRef = createNode(nodeRef, ContentModel.TYPE_FOLDER, pathElements.get(i));
            }
        }
        return nodeRef;
    }

    public NodeRef createNode(NodeRef parentNode, QName nodeTypeQName, String nodeName) {
        if (parentNode == null) {
            parentNode = repository.getCompanyHome();
        }
        NodeRef nodeRef = nodeService.createNode(parentNode, ContentModel.ASSOC_CONTAINS, QName.createQName(nodeName), nodeTypeQName).getChildRef();
        nodeService.setProperty(nodeRef, ContentModel.PROP_NAME, nodeName);
        
        return nodeRef;
    }

    public NodeRef findNode(List<String> pathElements) throws Exception {
        try {
            NodeRef companyHomeRef = repository.getCompanyHome();
            return registry.getFileFolderService().resolveNamePath(companyHomeRef, pathElements).getNodeRef();
        } catch (Exception ex) {
            throw new Exception("Node not found.");
        }
    }

    public NodeRef getOrCreateCtsCasesFolder() {
        List<String> pathElements = new ArrayList<>();
        pathElements.add(CTS_ROOT_FOLDER_NAME);
        pathElements.add(CTS_CASES_FOLDER_NAME);
        NodeRef nodeRef;
        try {
            nodeRef = findNode(pathElements);
        } catch (Exception e) {
            nodeRef = createPath(pathElements);
        }
        return nodeRef;
    }

    public NodeRef getOrCreateCtsCaseContainerFolder(String correspondenceType, String year) {
        List<String> pathElements = new ArrayList<>();
        pathElements.add(CTS_ROOT_FOLDER_NAME);
        pathElements.add(CTS_CASES_FOLDER_NAME);
        pathElements.add(correspondenceType);
        pathElements.add(year);
        NodeRef nodeRef;
        try {
            nodeRef = findNode(pathElements);
        } catch (Exception e) {
            nodeRef = createPath(pathElements);
        }
        return nodeRef;
    }

    public NodeRef getOrCreateCtsDocumentTemplatesFolder() {
        List<String> pathElements = new ArrayList<>();
        pathElements.add(CTS_ROOT_FOLDER_NAME);
        pathElements.add(CTS_DOCUMENT_TEMPLATES_FOLDER_NAME);
        NodeRef nodeRef;
        try {
            nodeRef = findNode(pathElements);
        } catch (Exception e) {
            nodeRef = createPath(pathElements);
        }
        return nodeRef;
    }

    public NodeRef getOrCreateCtsStandardLinesFolder() {
        List<String> pathElements = new ArrayList<>();
        pathElements.add(CTS_ROOT_FOLDER_NAME);
        pathElements.add(CTS_STANDARD_LINES_FOLDER_NAME);
        NodeRef nodeRef;
        try {
            nodeRef = findNode(pathElements);
        } catch (Exception e) {
            nodeRef = createPath(pathElements);
        }
        return nodeRef;
    }

    public NodeRef getOrCreateAutoCreateFolder() {
        List<String> pathElements = new ArrayList<>();
        pathElements.add(CTS_ROOT_FOLDER_NAME);
        pathElements.add(CTS_AUTO_CREATE_PARENT_FOLDER_NAME);
        NodeRef nodeRef;
        try {
            nodeRef = findNode(pathElements);
        } catch (Exception e) {
            nodeRef = createPath(pathElements);
        }
        return nodeRef;
    }

    public List<String> getAutoCreateCaseFolderNames() {
        ArrayList<String> folderNames = new ArrayList<>();
        folderNames.add(CTS_AUTO_CREATE_MIN_FOLDER_NAME);
        folderNames.add(CTS_AUTO_CREATE_TRO_FOLDER_NAME);
        folderNames.add(CTS_AUTO_CREATE_HMPO_COM_FOLDER_NAME);
        folderNames.add(CTS_AUTO_CREATE_UKVI_M_REF_FOLDER_NAME);
        folderNames.add(CTS_AUTO_CREATE_UKVI_B_REF_FOLDER_NAME);
        folderNames.add(CTS_AUTO_CREATE_DTEN_FOLDER_NAME);
        folderNames.add(CTS_AUTO_CREATE_UKVI_UTEN_FOLDER_NAME);
        return folderNames;
    }

    public NodeRef getTemplatesFolder(String templateName) {
        List<String> pathElements = new ArrayList<>();
        pathElements.add("Data Dictionary");
        pathElements.add("Email Templates");
        pathElements.add("Cts Templates");
        pathElements.add(templateName);
        NodeRef nodeRef = null;
        try {
            nodeRef = findNode(pathElements);
        } catch (Exception e) {
            LOGGER.error("Could not find template: " + templateName);
        }
        return nodeRef;
    }
}
