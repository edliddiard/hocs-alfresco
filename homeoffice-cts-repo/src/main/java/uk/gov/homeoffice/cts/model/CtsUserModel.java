package uk.gov.homeoffice.cts.model;

import org.alfresco.service.namespace.QName;

/**
 * Created by chris on 11/12/2014.
 */
public interface CtsUserModel {
    static final String CTS_USER_NAMESPACE = "http://cts-beta.homeoffice.gov.uk/model/user/1.0";

    static final QName ASPECT_PASSWORD = QName.createQName(CTS_USER_NAMESPACE, "passwordSettings");
    static final QName PROPERTY_PASSWORD_EXPIRY_DATE = QName.createQName(CTS_USER_NAMESPACE, "passwordExpiryDate");

}
