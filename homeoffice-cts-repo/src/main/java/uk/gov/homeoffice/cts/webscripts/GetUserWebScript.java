package uk.gov.homeoffice.cts.webscripts;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.template.TemplateNode;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.cmr.security.AuthorityType;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import uk.gov.homeoffice.cts.helpers.CtsFolderHelper;
import uk.gov.homeoffice.cts.model.CtsGroup;
import uk.gov.homeoffice.cts.model.CtsUser;

import java.io.Serializable;
import java.util.*;

/**
 * Webscript to get the team or unit a user is in, plus the users permissions.
 * Needs java API to get the groups the user is directly in, then filter then down
 * to the ones which are have the unit / team aspect.
 * Created by chris on 01/09/2014.
 */
public class GetUserWebScript extends DeclarativeWebScript {
    private static final Logger LOGGER = LoggerFactory.getLogger(GetUserWebScript.class);
    private AuthorityService authorityService;
    private NodeService nodeService;
    private PersonService personService;
    private CtsFolderHelper ctsFolderHelper;
    private ServiceRegistry serviceRegistry;

    public static final List<String> EXCLUDED_GROUPS = Arrays.asList("GROUP_Units", "GROUP_EVERYONE", "GROUP_ALFRESCO_ADMINISTRATORS", "GROUP_EMAIL_CONTRIBUTORS");


    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
        super.executeImpl(req, status, cache);

        LOGGER.debug("Running GetUserWebScript");

        Map<String, Object> model = new HashMap<>();

        String userName = req.getExtensionPath();

        Set<String> unitNames = getAuthorityService().getContainedAuthorities(AuthorityType.GROUP, "GROUP_Units", true);

        Set<String> authorities = getAuthorityService().getContainingAuthorities(AuthorityType.GROUP, userName, true);
        Set<CtsGroup> authNodeRefs = filterGroups(authorities, unitNames);

        NodeRef userNodeRef = personService.getPerson(userName);
        CtsUser user = new CtsUser(nodeService.getProperties(userNodeRef));

        NodeRef nodeRefCasesFolder = ctsFolderHelper.getOrCreateCtsCasesFolder();
        NodeRef nodeRefDocumentTemplatesFolder = ctsFolderHelper.getOrCreateCtsDocumentTemplatesFolder();
        NodeRef nodeRefStandardLinesFolder = ctsFolderHelper.getOrCreateCtsStandardLinesFolder();
        NodeRef nodeRefAutoCreateFolder = ctsFolderHelper.getOrCreateAutoCreateFolder();

        model.put("user", user);
        model.put("nodeCasesFolder", new TemplateNode(nodeRefCasesFolder, serviceRegistry, null));
        model.put("nodeDocumentTemplatesFolder", new TemplateNode(nodeRefDocumentTemplatesFolder, serviceRegistry, null));
        model.put("nodeStandardLinesFolder", new TemplateNode(nodeRefStandardLinesFolder, serviceRegistry, null));
        model.put("nodeAutoCreateFolder", new TemplateNode(nodeRefAutoCreateFolder, serviceRegistry, null));
        model.put("groups",authNodeRefs.toArray(new CtsGroup[authNodeRefs.size()]));
        model.put("manager",authorities.contains("GROUP_Manager"));

        return model;
    }

    /**
     * Filter the authorities down to CTS units / teams and return node refs in set.
     * @param authorities set<String>
     * @return Set<NodeRef>
     */
    private Set<CtsGroup> filterGroups(Set<String> authorities, Set<String> unitName) {
        Set<CtsGroup> authNodeRefs = new HashSet<>();
        for (String authority : authorities) {
            if (!EXCLUDED_GROUPS.contains(authority)) {
                NodeRef authNodeRef = authorityService.getAuthorityNodeRef(authority);
                CtsGroup group = getCtsGroup(nodeService.getProperties(authNodeRef), unitName);
                authNodeRefs.add(group);
            }
        }
        return authNodeRefs;
    }

    private CtsGroup getCtsGroup(Map<QName, Serializable> groupProp, Set<String> unitName) {
        CtsGroup group = new CtsGroup();
        group.setName((String) groupProp.get(ContentModel.PROP_NAME));
        group.setDisplayName((String) groupProp.get(ContentModel.PROP_AUTHORITY_DISPLAY_NAME));
        group.setAuthorityName((String) groupProp.get(ContentModel.PROP_AUTHORITY_NAME));
        if (unitName.contains(group.getAuthorityName())) {
            group.setUnit(true);
            group.setTeam(false);
        } else {
            group.setUnit(false);
            group.setTeam(true);
        }
        return group;
    }

    public void setCtsFolderHelper(CtsFolderHelper ctsFolderHelper) {
        this.ctsFolderHelper = ctsFolderHelper;
    }

    public void setServiceRegistry(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    public void setPersonService(PersonService personService) {
        this.personService = personService;
    }

    public AuthorityService getAuthorityService() {
        return authorityService;
    }

    public void setAuthorityService(AuthorityService authorityService) {
        this.authorityService = authorityService;
    }

    public NodeService getNodeService() {
        return nodeService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

}
