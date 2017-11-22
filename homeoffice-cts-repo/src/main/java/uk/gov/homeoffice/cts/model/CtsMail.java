package uk.gov.homeoffice.cts.model;

import org.alfresco.service.namespace.QName;

/**
 * Created by dawud on 24/02/2016.
 */
public interface CtsMail {

    // Mail message name format
    public static final String MAIL_JOB_NAME_PREFIX = "cts-mail.";
    public static final String MAIL_JOB_NAME_POSTFIX = "yyyyMMddHHmmssSSS";

    // Mail Properties
    public static final String PROP_MAIL_JOB_SCHEDULER_ENABLED = "cts.homeoffice.mail.mailJobSchedulerEnabled";
    public static final String PROP_MAIL_MAX_RETRIES = "cts.homeoffice.mail.mailMaxRetries";
    public static final String PROP_MAIL_CLEAR_FAILED_DURATION = "cts.homeoffice.mail.mailJobSchedulerEnabled";
    public static final String PROP_MAIL_CLEAR_SENT_DURATION = "cts.homeoffice.mail.mailClearSentDuration";
    public static final String PROP_MAIL_RETRY_DURATION = "cts.homeoffice.mail.mailJobSchedulerEnabled";
    public static final String PROP_MAIL_ORIGINATOR_ADDRESS = "cts.doNotReplyAddress";
    // Mail statuses
    public static final String MAIL_RESPONSE_STATUS_NEW = "new";
    public static final String MAIL_RESPONSE_STATUS_SENT = "sent";
    public static final String MAIL_RESPONSE_STATUS_RETRY = "retrying";
    public static final String MAIL_RESPONSE_STATUS_FAIL = "fail";
    public static final String MAIL_RESPONSE_STATUS_UNKNOWN = "unknown";

    // Mail handling defaults (in hours for duration)
    public static final boolean MAIL_DEFAULT_JOB_SCHEDULER_ENABLED = true;
    public static final String MAIL_DEFAULT_CLEAR_FAILED_DURATION = "PT12H";
    public static final String MAIL_DEFAULT_CLEAR_SENT_DURATION = "PT0H";
    public static final int MAIL_DEFAULT_MAX_RETRIES = 0;
    public static final String MAIL_DEFAULT_RETRY_DURATION = "PT1H";
    public static final String MAIL_DEFAULT_JOB_SCHEDULER_USER = "admin";

    // Mail node properties
    static final String CTS_MAIL_NAMESPACE = "http://cts-beta.homeoffice.gov.uk/model/mail/1.0";
    static final QName PROP_CASE_URL = QName.createQName(CTS_MAIL_NAMESPACE, "caseUrl");
    static final QName PROP_STATUS = QName.createQName(CTS_MAIL_NAMESPACE, "status");
    static final QName PROP_FAILURE_COUNT = QName.createQName(CTS_MAIL_NAMESPACE, "failureCount");
    static final QName PROP_ERROR_MESSAGE = QName.createQName(CTS_MAIL_NAMESPACE, "error");
    static final QName PARAM_SUBJECT = QName.createQName(CTS_MAIL_NAMESPACE, "subject");
    static final QName PARAM_TEMPLATE = QName.createQName(CTS_MAIL_NAMESPACE, "template");
    static final QName PARAM_TEMPLATE_MODEL = QName.createQName(CTS_MAIL_NAMESPACE, "template_model");

}
