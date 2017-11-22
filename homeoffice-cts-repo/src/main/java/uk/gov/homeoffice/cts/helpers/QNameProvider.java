package uk.gov.homeoffice.cts.helpers;

import org.alfresco.repo.processor.BaseProcessorExtension;
import org.alfresco.service.namespace.QName;

/**
 * Created by chris on 22/09/2014.
 */
public class QNameProvider extends BaseProcessorExtension {
    public QName getQname(String q){
        return QName.createQName(q);
    }
}
