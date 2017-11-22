package uk.gov.homeoffice.cts.template;

import org.alfresco.service.namespace.QName;
import java.text.SimpleDateFormat;

/**
 * Created by davidt on 11/11/2014.
 */
public class DatePropertyPlaceholder extends PropertyPlaceholder {

    private SimpleDateFormat dateFormat;

    public DatePropertyPlaceholder(QName property, SimpleDateFormat dateFormat) {
        super(property);
        this.dateFormat = dateFormat;
    }

    public SimpleDateFormat getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(SimpleDateFormat dateFormat) {
        this.dateFormat = dateFormat;
    }
}
