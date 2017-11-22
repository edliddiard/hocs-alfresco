package uk.gov.homeoffice.cts.behaviour;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.jbpm.calendar.CtsDuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.homeoffice.cts.calendar.CtsBusinessCalendar;
import uk.gov.homeoffice.cts.exceptions.DeleteCaseException;
import uk.gov.homeoffice.cts.helpers.BusinessCalendarProvider;
import uk.gov.homeoffice.cts.model.CorrespondenceType;
import uk.gov.homeoffice.cts.model.CtsModel;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static uk.gov.homeoffice.cts.model.TaskStatus.AMEND_RESPONSE;

/**
 * Class to watch over changes in the cts:caseStatus property
 * Created by chris on 03/09/2014.
 */
public class StatusBehaviour implements PropertyUpdateBehaviour {
    private static final Logger LOGGER = LoggerFactory.getLogger(StatusBehaviour.class);
    private NodeService nodeService;
    private String statusToStartPQQA;
    private String statusToStartDrafting;
    private String statusWhenCompleting;
    private String statusWhenDeleting;
    private BusinessCalendarProvider businessCalendarProvider;

    @Override
    public void onUpdateProperties(final NodeRef nodeRef, final Map<QName, Serializable> before, final Map<QName, Serializable> after) {
        final String statusBefore = (String) before.get(CtsModel.PROP_CASE_STATUS);

        final String statusAfter = (String) after.get(CtsModel.PROP_CASE_STATUS);

        final String beforeTaskStatus = (String) before.get(CtsModel.PROP_CASE_TASK);

        final String afterTaskStatus = (String) after.get(CtsModel.PROP_CASE_TASK);

        String type = (String) after.get(CtsModel.PROP_CORRESPONDENCE_TYPE);


        //check it has changed

        if(BehaviourHelper.hasChanged(statusBefore,statusAfter) || BehaviourHelper.hasChanged(beforeTaskStatus,afterTaskStatus)) {
            LOGGER.debug("Status before " + statusBefore + " Status after " + statusAfter);
            LOGGER.debug("beforeTaskStatus  " + beforeTaskStatus + " afterTaskStatus " + afterTaskStatus);
            LOGGER.debug("Correspondence type " + type);

            //we want this to work automatically so do it as system
            AuthenticationUtil.runAs(new AuthenticationUtil.RunAsWork<Map<String, Object>>() {
                @SuppressWarnings("synthetic-access")
                public Map<String, Object> doWork() throws Exception {
                    //update the deadlines
                    LOGGER.debug("The status changed on " + nodeRef);

                    updateStatusUpdatedDatetime(nodeRef);

                    checkForGroupedCasesAndSetStatus(nodeRef, statusAfter, afterTaskStatus);

                    if(statusAfter.equals(getStatusToStartDrafting())) {
                        Date targetDate = workOutDraftDeadlineDate(after);
                        if (targetDate != null) {
                            getNodeService().setProperty(nodeRef, CtsModel.PROP_DRAFT_RESPONSE_TARGET, targetDate);

                            LOGGER.debug("Changing status on start drafting: " + statusAfter + " " + afterTaskStatus);

                        }
                    }
                    if (statusAfter.equals(getStatusWhenDeleting())) {
                        if (!checkDelete(nodeRef)) {
                            nodeService.setProperty(nodeRef, CtsModel.PROP_CASE_STATUS, statusBefore);
                            LOGGER.error("Node ref "+nodeRef.toString()+" cannot be deleted as it is part of a grouped case");
                            throw new DeleteCaseException("Node ref "+nodeRef.toString()+" cannot be deleted as it is part of a grouped case");
                        };
                    }
                    if (checkReturned(afterTaskStatus)) {
                        LOGGER.debug("Incrementing return count for case " + nodeRef);
                        Integer returnedCount = (before.get(CtsModel.PROP_RETURNED_COUNT) == null) ? 0 : (Integer)before.get(CtsModel.PROP_RETURNED_COUNT);
                        nodeService.setProperty(nodeRef, CtsModel.PROP_RETURNED_COUNT, returnedCount+1);
                    }
                    return null;
                }
            }, AuthenticationUtil.getSystemUserName());
        }

        if(CorrespondenceType.isPQType(type)) {
            //Set draft date
            Date beforeDate = (Date) before.get(CtsModel.PROP_OP_DATE);
            Date afterDate = (Date) after.get(CtsModel.PROP_OP_DATE);
            Serializable beforeSPAD = before.get(CtsModel.PROP_REVIEWED_BY_SPADS);
            Serializable afterSPAD = after.get(CtsModel.PROP_REVIEWED_BY_SPADS);

            Serializable beforePerm = before.get(CtsModel.PROP_REVIEWED_BY_PERM_SEC);
            Serializable afterPerm = after.get(CtsModel.PROP_REVIEWED_BY_PERM_SEC);

            String beforeType = (String) before.get(CtsModel.PROP_CORRESPONDENCE_TYPE);
            String afterType = (String) after.get(CtsModel.PROP_CORRESPONDENCE_TYPE);

            if (BehaviourHelper.hasChanged(beforeType, afterType) ||
                    BehaviourHelper.hasChanged(statusBefore, statusAfter) ||
                    BehaviourHelper.hasChangedSerializable(beforeDate, afterDate) ||
                    BehaviourHelper.hasChangedBoolean(beforeSPAD, afterSPAD) ||
                    BehaviourHelper.hasChangedBoolean(beforePerm, afterPerm)) {

                AuthenticationUtil.runAs(new AuthenticationUtil.RunAsWork<Map<String, Object>>() {
                    @SuppressWarnings("synthetic-access")
                    public Map<String, Object> doWork() throws Exception {
                        final Date targetDate = workOutDraftDeadlineDate(after);
                        if (targetDate != null) {
                            getNodeService().setProperty(nodeRef, CtsModel.PROP_DRAFT_RESPONSE_TARGET, targetDate);
                        }
                        return null;
                    }
                }, AuthenticationUtil.getSystemUserName());

            }
        }
    }

    private void updateStatusUpdatedDatetime(NodeRef nodeRef) {
        nodeService.setProperty(nodeRef, CtsModel.PROP_STATUS_UPDATED_DATETIME, new Date());
    }

    private void checkForGroupedCasesAndSetStatus(final NodeRef nodeRef, final String statusAfter, final String taskStatus) {
        LOGGER.debug("entered grouped " + statusAfter + " "+ taskStatus);

        AuthenticationUtil.runAs(new AuthenticationUtil.RunAsWork<Map<String, Object>>() {
            @SuppressWarnings("synthetic-access")
            public Map<String, Object> doWork() throws Exception {

        List<AssociationRef> groupedCaseAssocRefList = nodeService.getTargetAssocs(nodeRef, CtsModel.ASSOC_GROUPED_CASES);
        for (AssociationRef groupedCaseAssocRef : groupedCaseAssocRefList) {
            NodeRef groupedCaseNodeRef = groupedCaseAssocRef.getTargetRef();
            LOGGER.debug("Grouped case node ref ***********: " + groupedCaseNodeRef);
            nodeService.setProperty(groupedCaseNodeRef, CtsModel.PROP_CASE_STATUS, statusAfter);
            nodeService.setProperty(groupedCaseNodeRef, CtsModel.PROP_CASE_TASK, taskStatus);

        }
                LOGGER.debug("statusAfter: " + statusAfter+ " taskStatus: " + taskStatus);
                return null;
            }
        }, AuthenticationUtil.getAdminUserName());

    }

    private Boolean checkReturned(String afterTaskStatus) {
        return afterTaskStatus.equals(AMEND_RESPONSE.getStatus());
    }

    private Boolean checkDelete(NodeRef nodeRef) {
        List<AssociationRef> sourceAssocs = getNodeService().getSourceAssocs(nodeRef, CtsModel.ASSOC_GROUPED_CASES);
        List<AssociationRef> targetAssocs = getNodeService().getTargetAssocs(nodeRef, CtsModel.ASSOC_GROUPED_CASES);
        if(sourceAssocs.size() > 0 || targetAssocs.size() > 0){
            return false;
        }
        return true;
    }

    /**
     * Case has gone into drafting so need to set the deadline for the drafting.
     * @param after Map<QName, Serializable>
     */
    protected Date workOutDraftDeadlineDate(Map<QName, Serializable> after) {
        Date deadlineDate = null;
        if(after.get(CtsModel.PROP_OP_DATE) != null){
            Date opDate = (Date) after.get(CtsModel.PROP_OP_DATE);
            //if the date is that day it should be considered in working hours
            if(opDate.getHours()<9){
                opDate.setHours(9);
            }
            boolean itsWednesday = opDate.getDay() == 3;

            //calculate date
            final String correspondingType = (String) after.get(CtsModel.PROP_CORRESPONDENCE_TYPE);
            CtsBusinessCalendar businessCalendar = getBusinessCalendarProvider().getBusinessCalendar((String) correspondingType);

            Boolean reviewBySpads = (Boolean) after.get(CtsModel.PROP_REVIEWED_BY_SPADS);
            Boolean reviewByPermSec = (Boolean) after.get(CtsModel.PROP_REVIEWED_BY_PERM_SEC);
            //Lords written with SPAD or Perm Sec  review if OP date is a Wednesday
            if(correspondingType.equals(CorrespondenceType.LORDS_WRITTEN.getCode())
                && (((reviewBySpads) || ((reviewByPermSec)))
                && itsWednesday)){
                //1 days after OP date	Midday
                deadlineDate = getDeadLineDateByDefaultProps(opDate, businessCalendar, 1, 12);
            } else if(correspondingType.equals(CorrespondenceType.LORDS_WRITTEN.getCode())
                && ((reviewBySpads) || (reviewByPermSec))){
                //1 days after OP date	3pm
                deadlineDate = getDeadLineDateByDefaultProps(opDate, businessCalendar, 1, 15);
            } else if(correspondingType.equals(CorrespondenceType.LORDS_WRITTEN.getCode())){
                //2 days after OP date	3pm
                deadlineDate = getDeadLineDateByDefaultProps(opDate, businessCalendar, 2, 15);
            } else if(correspondingType.equals(CorrespondenceType.ORDINARY_WRITTEN.getCode())
                    && (reviewBySpads || reviewByPermSec)){
                //1 days after OP date	3pm
                deadlineDate = getDeadLineDateByRecessProps(opDate, businessCalendar, 1, 15);
            } else if(correspondingType.equals(CorrespondenceType.ORDINARY_WRITTEN.getCode())){
                //2 days after OP date	3pm
                deadlineDate = getDeadLineDateByRecessProps(opDate, businessCalendar, 2, 15);
            } else if(correspondingType.equals(CorrespondenceType.NAMED_DAY.getCode())){
                //1 days after OP date	3pm
                deadlineDate = getDeadLineDateByRecessProps(opDate, businessCalendar, 1, 15);
            }
        }
        return deadlineDate;

    }

    private Date getDeadLineDateByRecessProps(Date opDate, CtsBusinessCalendar businessCalendar, int numberOfDays, int hours) {
        final String duration1 = numberOfDays + " business days";
        CtsDuration duration = new CtsDuration(getBusinessCalendarProvider().getRecessProps(), duration1);
        Date deadlineDate = businessCalendar.add(opDate, duration);
        deadlineDate.setHours(hours);
        return deadlineDate;
    }


    private Date getDeadLineDateByDefaultProps(Date opDate, CtsBusinessCalendar businessCalendar, int numberOfDays, int hours) {
        final String duration1 = numberOfDays + " business days";
        CtsDuration duration = new CtsDuration(getBusinessCalendarProvider().getDefaultProps(), duration1);
        Date deadlineDate = businessCalendar.add(opDate, duration);
        deadlineDate.setHours(hours);
        return deadlineDate;
    }


    private NodeService getNodeService() {
        return nodeService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    private String getStatusToStartPQQA() {
        return statusToStartPQQA;
    }

    public void setStatusToStartPQQA(String statusToStartPQQA) {
        this.statusToStartPQQA = statusToStartPQQA;
    }

    public String getStatusToStartDrafting() {
        return statusToStartDrafting;
    }

    public void setStatusToStartDrafting(String statusToStartDrafting) {
        this.statusToStartDrafting = statusToStartDrafting;
    }

    public String getStatusWhenCompleting() {
        return statusWhenCompleting;
    }

    public void setStatusWhenCompleting(String statusWhenCompleting) {
        this.statusWhenCompleting = statusWhenCompleting;
    }

    public String getStatusWhenDeleting() {
        return statusWhenDeleting;
    }

    public void setStatusWhenDeleting(String statusWhenDeleting) {
        this.statusWhenDeleting = statusWhenDeleting;
    }

    public BusinessCalendarProvider getBusinessCalendarProvider() {
        return businessCalendarProvider;
    }

    public void setBusinessCalendarProvider(BusinessCalendarProvider businessCalendarProvider) {
        this.businessCalendarProvider = businessCalendarProvider;
    }
}
