package uk.gov.homeoffice.cts.helpers;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.model.Repository;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Properties;

/**
 * Class to provide calendars that are in content files in Alfresco
 * Created by chris on 12/09/2014.
 */
public class ContentPropertyProvider implements PropertyProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(ContentPropertyProvider.class);
    private NodeService nodeService;
    private ContentService contentService;
    private Repository repository;

    @Override
    public Properties getBusinessCalendarDefaultProps() {
        String content = getPropertiesContent("BusinessCalendarDefaultProps");
        return getProperties(content);
    }

    protected Properties getProperties(String content) {
        Properties props = new Properties();
        String[] lines = content.split("\n");
        for(int i = 0; i < lines.length ; i++) {
            String[] nameValue = lines[i].split("=");
            if(nameValue.length == 1) {
                if(nameValue[0].startsWith("#")) {
                    continue;
                }
                props.put(nameValue[0], "");
            } else if(nameValue.length == 2) {
                if(nameValue[0].startsWith("#")) {
                    continue;
                }
                props.put(nameValue[0], nameValue[1].trim());
            }
        }
        return props;
    }

    protected String getPropertiesContent(String calendarName) {
        String propertyString = "";
        NodeRef cts = nodeService.getChildByName(getRepository().getCompanyHome(), ContentModel.ASSOC_CONTAINS, "CTS");
        if(cts != null) {
            NodeRef files = nodeService.getChildByName(cts, ContentModel.ASSOC_CONTAINS, "Files");
            if(files!=null) {
                NodeRef calendarFolder = nodeService.getChildByName(files, ContentModel.ASSOC_CONTAINS, "Calendars");
                if(calendarFolder != null) {
                    NodeRef calendar = nodeService.getChildByName(calendarFolder, ContentModel.ASSOC_CONTAINS, calendarName);
                    if(calendar != null) {
                        propertyString = getContentService().getReader(calendar, ContentModel.PROP_CONTENT).getContentString();
                    }
                }
            }
        }
        return propertyString;
    }

    protected String getContent(InputStream is) throws IOException {
        StringWriter writer = new StringWriter();
        IOUtils.copy(is, writer);
        return writer.toString();
    }

    @Override
    public Properties getBusinessCalendarRecessProps() {
        String content = getPropertiesContent("BusinessCalendarRecessProps");
        return getProperties(content);
    }

    @Override
    public Properties getBusinessCalendarAllUKProps() {
        String content = getPropertiesContent("BusinessCalendarAllUKProps");
        return getProperties(content);
    }

    public NodeService getNodeService() {
        return nodeService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public ContentService getContentService() {
        return contentService;
    }

    public void setContentService(ContentService contentService) {
        this.contentService = contentService;
    }

    public Repository getRepository() {
        return repository;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }
}
