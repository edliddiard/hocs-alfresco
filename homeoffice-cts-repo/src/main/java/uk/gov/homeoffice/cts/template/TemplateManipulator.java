package uk.gov.homeoffice.cts.template;

import org.alfresco.service.namespace.QName;
import org.alfresco.model.ContentModel;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.odftoolkit.simple.TextDocument;
import org.odftoolkit.simple.common.navigation.InvalidNavigationException;
import org.odftoolkit.simple.common.navigation.TextNavigation;
import org.odftoolkit.simple.common.navigation.TextSelection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.WebScriptException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * Created by davidt on 11/11/2014.
 */
public class TemplateManipulator {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetPopulatedTemplateWebScript.class);
    private static final String PLACEHOLDER_START = "<<";
    private static final String PLACEHOLDER_END = ">>";

    private PlaceholderList placeholderList;
    private NodeService nodeService;
    private ServiceRegistry serviceRegistry;

    private NodeRef templateNodeRef;
    private NodeRef caseNodeRef;

    /**
     * Get the document template and populate it with the placeholders.
     * @return TextDocument
     */
    public TextDocument populate() {
        ContentReader reader = serviceRegistry.getContentService().getReader(templateNodeRef, ContentModel.PROP_CONTENT);
        InputStream contentInputStream = reader.getContentInputStream();
        TextDocument document;
        try {
            // load from stream
            document = TextDocument.loadDocument(contentInputStream);
        } catch (Exception ex) {
            LOGGER.error("Error loading document. Error: "+ ex.getMessage());
            throw new WebScriptException("Error loading document");
        }
        finally {
            if (contentInputStream != null) {
                try {
                    contentInputStream.close();
                } catch (IOException e) {
                    LOGGER.error("Failed to close input stream. Error: "+e.getMessage());
                }
            }
        }
        Map<String, Placeholder> placeholders = placeholderList.getAllPlaceholders();
        for (String placeholderEntry : placeholders.keySet()) {
            Placeholder superPlaceholder = placeholders.get(placeholderEntry);
            String placeholderValue = getPlaceholderValue(superPlaceholder);

            if (placeholderEntry == "MARKUP_MINISTER"){
                int MinisterIndexInt = placeholderValue.indexOf('_') +1;
                placeholderValue = placeholderValue.substring(MinisterIndexInt);
            }

            findAndReplace(document, PLACEHOLDER_START+placeholderEntry+PLACEHOLDER_END, placeholderValue);
        }
        return document;
    }

    /**
     * Get the value for the placeholder.
     * @param superPlaceholder Placeholder
     * @return String
     */
    private String getPlaceholderValue(Placeholder superPlaceholder) {
        String value = null;
        if (superPlaceholder.getClass() == MultiPropertyPlaceholder.class) {
            value = getMultiPropertyPlaceholderValue((MultiPropertyPlaceholder) superPlaceholder);
        }
        if (superPlaceholder.getClass() == PropertyPlaceholder.class) {
            value = getPropertyPlaceholderValue((PropertyPlaceholder) superPlaceholder);
        }
        if (superPlaceholder.getClass() == StaticValuePlaceholder.class) {
            StaticValuePlaceholder placeholder = (StaticValuePlaceholder) superPlaceholder;
            value = placeholder.getValue();
        }
        if (superPlaceholder.getClass() == DatePropertyPlaceholder.class) {
            value = getDatePropertyPlaceholderValue((DatePropertyPlaceholder) superPlaceholder);
        }
        if (superPlaceholder.getClass() == GroupedQuestionsPlaceholder.class) {
            GroupedQuestionsPlaceholder placeholder = (GroupedQuestionsPlaceholder) superPlaceholder;
            value = placeholder.getValue(caseNodeRef, nodeService);
        }
        return value;
    }

    /**
     * Get value for placeholder.
     * @param placeholder MultiPropertyPlaceholder
     * @return String
     */
    private String getMultiPropertyPlaceholderValue(MultiPropertyPlaceholder placeholder) {
        StringBuilder replaceTextSb = new StringBuilder();
        for (int i=0; i<placeholder.getProperties().length; i++) {
            Serializable propertyValue = nodeService.getProperty(caseNodeRef, placeholder.getProperties()[i]);
            if (propertyValue != null) {
                replaceTextSb.append(propertyValue);
                if (i < placeholder.getProperties().length-1 && propertyValue != null) {
                    replaceTextSb.append(placeholder.getSeparator());
                }
            }
        }
        return replaceTextSb.toString();
    }

    /**
     * Get value for placeholder.
     * @param placeholder PropertyPlaceholder
     * @return String
     */
    private String getPropertyPlaceholderValue(PropertyPlaceholder placeholder) {
        String propertyValue = (String) nodeService.getProperty(caseNodeRef, placeholder.getProperty());
        if (propertyValue != null) {
            return propertyValue;
        }
        return "";
    }

    /**
     * Get value for placeholder.
     * @param placeholder DatePropertyPlaceholder
     * @return String
     */
    private String getDatePropertyPlaceholderValue(DatePropertyPlaceholder placeholder) {
        Date value = (Date) nodeService.getProperty(caseNodeRef, placeholder.getProperty());
        String propertyValue = "";
        if(value != null && !value.equals("")) {
            propertyValue = placeholder.getDateFormat().format(value);
        }
        return propertyValue;
    }

    /**
     * Find and replace strings within the document.
     * @param document TextDocument
     * @param findText String
     * @param replaceText String
     */
    protected void findAndReplace(TextDocument document, String findText, String replaceText) {
        TextNavigation search = new TextNavigation(findText, document);
        // iterate through the search results
        while (search.hasNext()) {
            // for each match, add a hyperlink to it
            TextSelection item = (TextSelection) search.nextSelection();
            try {
                item.replaceWith(replaceText);
            } catch (InvalidNavigationException e) {
                String errorMessage = "Error searching ODT file for "+findText+" and replacing with "+replaceText;
                LOGGER.error(errorMessage+". Error: "+ e.getMessage());
                throw new WebScriptException(errorMessage);
            }
        }
    }

    public NodeRef getTemplateNodeRef() {
        return templateNodeRef;
    }

    public void setTemplateNodeRef(NodeRef templateNodeRef) {
        this.templateNodeRef = templateNodeRef;
    }

    public NodeRef getCaseNodeRef() {
        return caseNodeRef;
    }

    public void setCaseNodeRef(NodeRef caseNodeRef) {
        this.caseNodeRef = caseNodeRef;
    }

    public void setPlaceholderList(PlaceholderList placeholderList) {
        this.placeholderList = placeholderList;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setServiceRegistry(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }
}
