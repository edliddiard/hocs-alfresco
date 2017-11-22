package uk.gov.homeoffice.cts.email;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.Hours;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.homeoffice.cts.model.CtsMail;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The Class EmailJobScheduler processes EmailJob nodes saved in the
 * Company Home/Data Dictionary/Scheduled Actions folder.
 * <p>
 * For Duration textual respresentation see
 * https://docs.oracle.com/javase/8/docs/api/java/time/Duration.html#parse-java.lang.CharSequence-
 * <p>
 * Created by dawud on 13/02/2016.
 */
public class EmailJobScheduler implements Job {

    private static Log logger = LogFactory.getLog(EmailJobScheduler.class);

    private boolean mailJobSchedulerEnabled = CtsMail.MAIL_DEFAULT_JOB_SCHEDULER_ENABLED;
    private String mailClearFailedDuration = CtsMail.MAIL_DEFAULT_CLEAR_FAILED_DURATION;
    private String mailClearSentDuration = CtsMail.MAIL_DEFAULT_CLEAR_SENT_DURATION;
    private int mailMaxRetries = CtsMail.MAIL_DEFAULT_MAX_RETRIES;
    private String mailRetryDuration = CtsMail.MAIL_DEFAULT_RETRY_DURATION;
    private String mailJobSchedulerUser = CtsMail.MAIL_DEFAULT_JOB_SCHEDULER_USER;
    private JobExecutionContext jobExecutionContext;

    @Autowired
    private NodeService nodeService;

    @Autowired
    private EmailJobService emailJobService;

    private void init() {
        // Initialise mail defaults if defined in properties file
        JobDataMap jobData = jobExecutionContext.getJobDetail().getJobDataMap();
        mailJobSchedulerEnabled = (jobData.get("mailJobSchedulerEnabled") != null) ? Boolean.parseBoolean((String) jobData.get("mailJobSchedulerEnabled")) : mailJobSchedulerEnabled;
        mailClearFailedDuration = (jobData.get("mailClearFailedDuration") != null) ? ((String) jobData.get("mailClearFailedDuration")) : mailClearFailedDuration;
        mailClearSentDuration = (jobData.get("mailClearSentDuration") != null) ? ((String) jobData.get("mailClearSentDuration")) : mailClearSentDuration;
        mailMaxRetries = (jobData.get("mailMaxRetries") != null) ? Integer.parseInt((String) jobData.get("mailMaxRetries")) : mailMaxRetries;
        mailRetryDuration = (jobData.get("mailRetryDuration") != null) ? (String) jobData.get("mailRetryDuration") : mailRetryDuration;
        mailJobSchedulerUser = (jobData.get("mailJobSchedulerUser") != null) ? (String) jobData.get("mailJobSchedulerUser") : mailJobSchedulerUser;

    }

    /**
     * Scheduled Action initialisation.
     *
     * @param jobExecutionContext
     * @throws JobExecutionException
     */
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        // Initialise mail defaults if defined in properties file
        this.jobExecutionContext = jobExecutionContext;
        init();

        if (mailJobSchedulerEnabled) {

            logger.debug("Running EmailJobScheduler with params: " +
                    "Enabled[" + mailJobSchedulerEnabled + "] " +
                    "mailClearFailedDuration[" + mailClearFailedDuration + "] " +
                    "mailClearSentDuration[" + mailClearSentDuration + "] " +
                    "mailMaxRetries[" + mailMaxRetries + "] " +
                    "mailRetryDuration[" + mailRetryDuration + "]");
            doWorkOnEmailJobs();
            logger.debug("EmailJobScheduler ran in " +
                    (new Date().getTime() - jobExecutionContext.getFireTime().getTime()) + "ms. " +
                    "Next EmailJobScheduler will run on [" + jobExecutionContext.getNextFireTime() + "]");

        } else {
            logger.debug("EmailJobScheduler not enabled");
        }
    }// execute end


    /**
     * Fetch EmailJob Nodes in the Scheduled Action folder queue and send these as email messages
     */
    public void doWorkOnEmailJobs() {

        List<NodeRef> emailJobs = fetchAllEmailJobs();
        logger.debug("Found [" + emailJobs.size() + "] EmailJobs in Scheduled Actions folder");

        if (emailJobs != null && emailJobs.size() > 0) {

            try {
                AuthenticationUtil.setRunAsUserSystem();

                for (NodeRef emailJob : emailJobs) {
                    String mailStatus = nodeService.getProperty(emailJob, CtsMail.PROP_STATUS) != null ? nodeService.getProperty(emailJob, CtsMail.PROP_STATUS).toString() : CtsMail.MAIL_RESPONSE_STATUS_UNKNOWN;
                    logger.debug("Running EmailJob [" + emailJob.toString() + "] Sending Status: [" + mailStatus + "]");
                    switch (mailStatus) {
                        case CtsMail.MAIL_RESPONSE_STATUS_RETRY:
                            if (nodeService.getProperty(emailJob, ContentModel.PROP_CREATED) != null) {
                                Date date = (Date) nodeService.getProperty(emailJob, ContentModel.PROP_CREATED);

                                Date now = new Date();
                                Date dateAtRetryPeriod = new Date(date.getTime() + Hours.parseHours(mailRetryDuration).toStandardDuration().getMillis());

                                if (now.after(dateAtRetryPeriod)) {
                                    logger.trace("Retry sending EmailJob [" + emailJob.toString() + "] after Retry Period: [" + mailRetryDuration.toString() + "]");
                                    emailJobService.sendEmailJob(emailJob);
                                }
                            }
                            break;

                        case CtsMail.MAIL_RESPONSE_STATUS_SENT:
                            if (nodeService.getProperty(emailJob, ContentModel.PROP_SENTDATE) != null) {
                                Date date = (Date) nodeService.getProperty(emailJob, ContentModel.PROP_SENTDATE);

                                Date now = new Date();
                                Date dateAtSent = new Date(date.getTime() + Hours.parseHours(mailClearSentDuration).toStandardDuration().getMillis());

                                if (now.after(dateAtSent)) {
                                    logger.trace("Clearing sent EmailJob [" + emailJob.toString() + "] after Time limit: [" + mailClearSentDuration + "]");
                                    final NodeRef emailJobFinal = emailJob;
                                    nodeService.deleteNode(emailJobFinal);
                                }
                            }
                            break;

                        case CtsMail.MAIL_RESPONSE_STATUS_FAIL:
                            if (nodeService.getProperty(emailJob, ContentModel.PROP_CREATED) != null) {
                                Date date = (Date) nodeService.getProperty(emailJob, ContentModel.PROP_CREATED);

                                Date now = new Date();
                                Date dateAtFailedDays = new Date(date.getTime() + Hours.parseHours(mailClearFailedDuration).toStandardDuration().getMillis());

                                if (now.after(dateAtFailedDays)) {
                                    logger.trace("Clearing failed EmailJob [" + emailJob.toString() + "] after Time limit: [" + mailClearFailedDuration + "]");
                                    final NodeRef emailJobFinal = emailJob;
                                    nodeService.deleteNode(emailJobFinal);
                                }
                            }
                            break;

                        default:
                            logger.error("Found invalid EmailJob with status: [" + mailStatus + "] EmailJob Ref: [" + emailJob.toString() + "] . Please remove EmailJob using Alfresco Share / Alfresco Explorer client.");
                    }
                }

            } finally {
                AuthenticationUtil.clearCurrentSecurityContext();
            }
        }
    }


    /**
     * Help method to return all nodes in the Scheduled Action Folder that has Aspect EMAILED.
     *
     * @return
     */
    public List<NodeRef> fetchAllEmailJobs() {
        List<NodeRef> emailJobList = new ArrayList<NodeRef>();
        try {
            AuthenticationUtil.setRunAsUserSystem();
            NodeRef scheduledActions = emailJobService.getScheduledActionsFolder();
            List<ChildAssociationRef> children = nodeService.getChildAssocs(scheduledActions);
            for (ChildAssociationRef childAssoc : children) {
                NodeRef childNodeRef = childAssoc.getChildRef();

                if (hasAspect(childNodeRef, ContentModel.ASPECT_EMAILED)) {
                    emailJobList.add(childNodeRef);
                }
            }
        } finally {
            AuthenticationUtil.clearCurrentSecurityContext();
        }
        return emailJobList;
    }

    private boolean hasAspect(NodeRef nodeRef, QName qname) {
        boolean hasAspect = false;
        NodeRef.Status status = nodeService.getNodeStatus(nodeRef);
        if (status != null && !status.isDeleted()) {
            hasAspect = nodeService.hasAspect(nodeRef, qname);
        }
        return hasAspect;
    }

    public void setEmailJobService(EmailJobService emailJobService) {
        this.emailJobService = emailJobService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }
}