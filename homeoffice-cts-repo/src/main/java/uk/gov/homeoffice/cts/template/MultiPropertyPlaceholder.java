package uk.gov.homeoffice.cts.template;

import org.alfresco.service.namespace.QName;

/**
 * Created by davidt on 11/11/2014.
 */
public class MultiPropertyPlaceholder extends Placeholder {

    private QName[] properties;

    private String separator;

    public MultiPropertyPlaceholder(QName[] properties, String separator) {
        this.properties = properties;
        this.separator = separator;
    }

    public QName[] getProperties() {
        return properties;
    }

    public void setProperties(QName[] properties) {
        this.properties = properties;
    }

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }
}
