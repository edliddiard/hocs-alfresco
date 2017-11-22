package uk.gov.homeoffice.cts.helpers;

import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.json.JSONUtils;
import uk.gov.homeoffice.cts.model.CtsCase;
import uk.gov.homeoffice.cts.model.CtsModel;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Created by davidt on 15/10/2014.
 */
public class CtsHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(CtsHelper.class);
    private JSONUtils jsonUtils;

    public void setJsonUtils(JSONUtils jsonUtils) {
        this.jsonUtils = jsonUtils;
    }

    public void encodeStringsForJson(Map<QName, Serializable> caseProps) {
        for (Map.Entry<QName, Serializable> prop : caseProps.entrySet()) {
            if (prop.getValue() instanceof String) {
                String jsonString;
                try {
                    jsonString = jsonUtils.toJSONString(prop.getValue());
                    prop.setValue(jsonString);
                } catch (IOException e) {
                    LOGGER.error("Unexpected error converting string " + prop.getValue() + " to JSON. " + e.getMessage());
                }
            }
        }
    }

    public static ResourceBundle getCtsProperties() {
        return ResourceBundle.getBundle("CtsProperties");
    }

}
