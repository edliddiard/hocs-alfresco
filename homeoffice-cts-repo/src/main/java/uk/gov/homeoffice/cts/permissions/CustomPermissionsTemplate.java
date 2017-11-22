package uk.gov.homeoffice.cts.permissions;

import freemarker.ext.beans.BeanModel;
import freemarker.template.TemplateModelException;
import org.alfresco.repo.template.BaseTemplateProcessorExtension;
import org.alfresco.repo.template.TemplateNode;
import org.alfresco.service.cmr.repository.NodeRef;
import org.apache.chemistry.opencmis.commons.data.CmisExtensionElement;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.CmisExtensionElementImpl;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Expose the custom permissions we have created for freemarker templates
 * Created by chris on 10/09/2014.
 */
public class CustomPermissionsTemplate extends BaseTemplateProcessorExtension {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(CustomPermissionsTemplate.class);


    private PermissionChecker permissionChecker;
    public List<CmisExtensionElement> getCustomPermissions(Object obj){
        List<CmisExtensionElement> extensions = new ArrayList<>();

        if (obj instanceof TemplateNode) {
            TemplateNode node = (TemplateNode)obj;

            //This needs to be a list and in configuration
            String permission = CtsPermissions.ALLOCATE;
                if(getPermissionChecker().hasPermission(node.getNodeRef(), permission)){
                    CmisExtensionElement cmisExtensionElement = new CmisExtensionElementImpl(null,permission,null,"true");
                    extensions.add(cmisExtensionElement);
                }
            List<CmisExtensionElement> propertyPermissions = getPermissionChecker().getPropertyPermissions(node.getNodeRef());
            for (CmisExtensionElement propertyPermission : propertyPermissions) {
                extensions.add(propertyPermission);
            }
        }



        return extensions;
    }

    public PermissionChecker getPermissionChecker() {
        return permissionChecker;
    }

    public void setPermissionChecker(PermissionChecker permissionChecker) {
        this.permissionChecker = permissionChecker;
    }

}
