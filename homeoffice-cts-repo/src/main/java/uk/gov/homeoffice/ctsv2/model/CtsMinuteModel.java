package uk.gov.homeoffice.ctsv2.model;

import org.alfresco.service.namespace.QName;

/**
 * Created by dawudr on 05/06/2016.
 */
public interface CtsMinuteModel {
    static final String CTS_MINUTE_NAMESPACE = "http://cts-beta.homeoffice.gov.uk/model/minute/1.0";
    static final QName ASPECT_MINUTE = QName.createQName(CTS_MINUTE_NAMESPACE, "minutes");
    static final QName CTS_MODEL_NAME = QName.createQName(CTS_MINUTE_NAMESPACE,"ctsMinute");

    static final QName PROP_MINUTE_COLLATED = QName.createQName(CTS_MINUTE_NAMESPACE, "minutesCollated");
    static final QName PROP_MINUTE_DBID = QName.createQName(CTS_MINUTE_NAMESPACE, "dbid");
    static final QName PROP_MINUTE_CONTENT = QName.createQName(CTS_MINUTE_NAMESPACE, "minuteContent");

    static final QName PROP_MINUTE_TYPE = QName.createQName(CTS_MINUTE_NAMESPACE, "minuteType");
    static final QName PROP_MINUTE_DATE_TIME = QName.createQName(CTS_MINUTE_NAMESPACE, "minuteDateTime");
    static final QName PROP_MINUTE_UPDATED_BY = QName.createQName(CTS_MINUTE_NAMESPACE, "updatedBy");
    static final QName PROP_MINUTE_ACTION = QName.createQName(CTS_MINUTE_NAMESPACE, "minuteAction");
}
