package uk.gov.homeoffice.cts.permissions;

import org.alfresco.opencmis.CMISConnector;
import org.alfresco.opencmis.dictionary.CMISNodeInfo;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.NodeRef;
import org.apache.chemistry.opencmis.commons.data.CmisExtensionElement;
import org.apache.chemistry.opencmis.commons.data.ObjectData;
import org.apache.chemistry.opencmis.commons.enums.IncludeRelationships;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.CmisExtensionElementImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * Overriding the Alfresco object so I can inject custom permissions into the extension
 * Created by chris on 14/08/2014.
 */
public class CtsCMISConnector extends CMISConnector {
    PermissionChecker permissionChecker;
    @Override
    public ObjectData createCMISObject(CMISNodeInfo info, FileInfo node, String filter, boolean includeAllowableActions, IncludeRelationships includeRelationships, String renditionFilter, boolean includePolicyIds, boolean includeAcl) {
        ObjectData objectData = super.createCMISObject(info, node, filter, includeAllowableActions, includeRelationships, renditionFilter, includePolicyIds, includeAcl);
        if(includeAllowableActions) {
            checkPermissions(objectData);
        }
        return objectData;
    }

    @Override
    public ObjectData createCMISObject(CMISNodeInfo info, String filter, boolean includeAllowableActions, IncludeRelationships includeRelationships, String renditionFilter, boolean includePolicyIds, boolean includeAcl) {
        ObjectData objectData =  super.createCMISObject(info, filter, includeAllowableActions, includeRelationships, renditionFilter, includePolicyIds, includeAcl);

        if(includeAllowableActions) {
            checkPermissions(objectData);
        }

        return objectData;
    }


    private void checkPermissions(ObjectData objectData) {
        //here we can add custom permissions
        List<CmisExtensionElement> extensions = new ArrayList<>();

        //This needs to be a list and in configuration
        String permission = CtsPermissions.ALLOCATE;

        if(getPermissionChecker().hasPermission(new NodeRef(objectData.getId()), permission)){
            CmisExtensionElement cmisExtensionElement = new CmisExtensionElementImpl(null,permission,null,"true");
            extensions.add(cmisExtensionElement);
        }
        List<CmisExtensionElement> propertyPermissions = getPermissionChecker().getPropertyPermissions(new NodeRef(objectData.getId()));
        for (CmisExtensionElement propertyPermission : propertyPermissions) {
            extensions.add(propertyPermission);
        }


        if(extensions.size()>0 && objectData.getAllowableActions()!=null) {
            objectData.getAllowableActions().setExtensions(extensions);
        }
    }

    public PermissionChecker getPermissionChecker() {
        return permissionChecker;
    }

    public void setPermissionChecker(PermissionChecker permissionChecker) {
        this.permissionChecker = permissionChecker;
    }

}
