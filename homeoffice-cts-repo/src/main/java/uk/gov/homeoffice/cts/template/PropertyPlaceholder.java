package uk.gov.homeoffice.cts.template;

import org.alfresco.service.namespace.QName;

/**
 * Created by davidt on 11/11/2014.
 */
public class PropertyPlaceholder extends Placeholder {

    private QName property;

    public PropertyPlaceholder(QName property) {
        this.property = property;
    }

    public QName getProperty() {
        return property;
    }

    public void setProperty(QName property) {
        this.property = property;
    }

}
