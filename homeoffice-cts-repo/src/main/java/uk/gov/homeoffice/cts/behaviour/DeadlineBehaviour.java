package uk.gov.homeoffice.cts.behaviour;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.jbpm.calendar.CtsDuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.homeoffice.cts.calendar.CtsBusinessCalendar;
import uk.gov.homeoffice.cts.helpers.BusinessCalendarProvider;
import uk.gov.homeoffice.cts.model.CorrespondenceType;
import uk.gov.homeoffice.cts.model.CtsModel;

import java.io.Serializable;
import java.util.*;

import static uk.gov.homeoffice.cts.model.CorrespondenceType.*;
import static uk.gov.homeoffice.cts.model.DeadlineConfiguration.*;

/**
 * Behaviour that will pick up the setting of correspondence type and set or adjust the
 * deadline date as per rules.
 * Created by chris on 11/08/2014.
 */
public class DeadlineBehaviour implements PropertyUpdateBehaviour {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeadlineBehaviour.class);
    private NodeService nodeService;
    private BusinessCalendarProvider businessCalendarProvider;
    private String hmpoDeadline;
    private String hmpoStage3MPDeadline;
    private String hmpoDraftDeadline;

    @Override
    public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
        String beforeType = (String) before.get(CtsModel.PROP_CORRESPONDENCE_TYPE);
        String afterType = (String) after.get(CtsModel.PROP_CORRESPONDENCE_TYPE);
        Map<QName,Date> datesMap = new HashMap<>();
        //check it has changed
        if(BehaviourHelper.hasChanged(beforeType,afterType)){
            //update the deadlines
            CtsBusinessCalendar businessCalendar = getBusinessCalendarProvider().getBusinessCalendar(afterType);
            final CorrespondenceType correspondenceType = CorrespondenceType.getByCode(afterType);
            if(correspondenceType !=null) {
                switch (correspondenceType) {
                    //DCU types
                    case DCU_MINISTERIAL:
                    case DCU_TREAT_OFFICIAL: {
                        datesMap = doDCUTypes(correspondenceType, after, businessCalendar);
                        break;
                    }

                    //FOI types
                    case FOI: {
                        datesMap = doFOITypes(after, businessCalendar);
                        break;
                    }

                    //FOI FTC type
                    case FOI_FTC: {
                        datesMap = doFoiFtcType(after, businessCalendar);
                        break;
                    }

                    //FOI FTS type
                    case FOI_FSC: {
                        datesMap = doFoiFtsType(after, businessCalendar);
                        break;
                    }

                    //UKVI types
                    case UKVI_B_REF:
                    case UKVI_M_REF: {
                        datesMap = doUKVI(correspondenceType, after, businessCalendar);
                        break;
                    }
                    case HMPO_COLLECTIVES: {
                        datesMap = doHMPOCOL(after, businessCalendar);
                        break;
                    }
                    //HMPO types
                    case HMPO_GENERAL:
                    case HMPO_COMPLAINT: {
                        datesMap = doHMPO(after, businessCalendar);
                        break;
                    }

                    case HMPO_STAGE_1:
                    case HMPO_STAGE_2:
                    case HMPO_DIRECT_GENRAL:
                    case HMPO_GNR:{
                        datesMap = doHMPOComplaints(after, businessCalendar);
                        break;
                    }
                    default: {
                        //do nothing
                        break;
                    }
                }

            }
        } else if (CorrespondenceType.isHMPOComplaints(afterType)) {
            Date beforeBringUpDate = (Date) before.get(CtsModel.BRING_UP_DATE);
            Date afterBringUpDate = (Date) after.get(CtsModel.BRING_UP_DATE);
            if (afterBringUpDate != null && BehaviourHelper.hasChangedDate(beforeBringUpDate, afterBringUpDate)) {
                CtsBusinessCalendar businessCalendar = getBusinessCalendarProvider().getBusinessCalendar(afterType);
                Date deadlineDate = getDeadLineDateByDefaultProps(afterBringUpDate, businessCalendar, HMPO_COMPLAINTS_DEFER_CASE_RESPONSE_DEADLINE);
                getNodeService().setProperty(nodeRef, CtsModel.PROP_CASE_RESPONSE_DEADLINE, deadlineDate);
            }
        } else if (CorrespondenceType.HMPO_COLLECTIVES.getCode().equals(afterType)) {
            Date beforeDepartureDateFromUK = (Date) before.get(CtsModel.DEPARTURE_DATE_FROM_UK);
            Date afterDepartureDateFromUK = (Date) after.get(CtsModel.DEPARTURE_DATE_FROM_UK);
            if (afterDepartureDateFromUK != null && BehaviourHelper.hasChangedDate(beforeDepartureDateFromUK, afterDepartureDateFromUK)) {
                CtsBusinessCalendar businessCalendar = getBusinessCalendarProvider().getBusinessCalendar(afterType);
                datesMap = doHMPOCOL(after, businessCalendar);
            }
        }

        if (!datesMap.isEmpty()) {
            updateDeadlineProperties(nodeRef, datesMap);
        }

    }

    private void updateDeadlineProperties(NodeRef nodeRef, Map<QName, Date> datesMap) {
        Set<QName> keyset = datesMap.keySet();
        for (QName qName : keyset) {
            Date date = datesMap.get(qName);
            if (date != null) {
                getNodeService().setProperty(nodeRef, qName, date);
            }
        }
    }

    protected Map<QName, Date> doHMPOComplaints(Map<QName, Serializable> after,CtsBusinessCalendar businessCalendar) {

        Date startDate = (Date) after.get(CtsModel.PROP_DATE_RECEIVED);
        String afterType = (String) after.get(CtsModel.PROP_CORRESPONDENCE_TYPE);
        Date deadlineDate = null;
        final CorrespondenceType correspondenceType = CorrespondenceType.getByCode(afterType);
        if (correspondenceType == HMPO_STAGE_1 || correspondenceType == HMPO_STAGE_2) {
            deadlineDate = getDeadLineDateByDefaultProps(startDate, businessCalendar, HMPO_STAGE_1_CASE_RESPONSE_DEADLINE);
        } else if (correspondenceType == HMPO_DIRECT_GENRAL) {
            deadlineDate = getDeadLineDateByDefaultProps(startDate, businessCalendar, HMPO_DIRECT_GENRAL_CASE_RESPONSE_DEADLINE);
        } else if (correspondenceType == HMPO_GNR) {
            deadlineDate = getDeadLineDateByDefaultProps(startDate, businessCalendar, HMPO_GNR_CASE_RESPONSE_DEADLINE);
        }
        return Collections.singletonMap(CtsModel.PROP_CASE_RESPONSE_DEADLINE, deadlineDate);
    }

    private Map<QName, Date> doFoiFtcType(Map<QName, Serializable> after, CtsBusinessCalendar businessCalendar) {
        //5 days
        Date startDate = (Date) after.get(CtsModel.PROP_DATE_RECEIVED);
        Date deadlineDate = getDeadLineDateByDefaultProps(startDate, businessCalendar, FOI_FTC_CASE_RESPONSE_DEADLINE);
        return Collections.singletonMap(CtsModel.PROP_CASE_RESPONSE_DEADLINE, deadlineDate);
    }

    private Map<QName, Date> doFoiFtsType(Map<QName, Serializable> after, CtsBusinessCalendar businessCalendar) {
        //20 days
        Date startDate = (Date) after.get(CtsModel.PROP_DATE_RECEIVED);
        Date deadlineDate = getDeadLineDateByDefaultProps(startDate, businessCalendar, FOI_FSC_CASE_RESPONSE_DEADLINE);
        return Collections.singletonMap(CtsModel.PROP_CASE_RESPONSE_DEADLINE, deadlineDate);
    }

    protected Map<QName, Date> doHMPO(Map<QName, Serializable> after, CtsBusinessCalendar businessCalendar) {
        Map<QName, Date> datesMap = new HashMap<>();
        Date startDate = (Date) after.get(ContentModel.PROP_CREATED);
        if(startDate == null){
            //can't set deadline
            return datesMap;
        }
        CtsDuration duration;
        if(after.get(CtsModel.PROP_HMPO_STAGE) != null && after.get(CtsModel.PROP_HMPO_STAGE).equals("MP complaint")){
            duration = new CtsDuration(getBusinessCalendarProvider().getDefaultProps() , getHmpoDraftDeadline());
            Date draftDeadlineDate = businessCalendar.add(startDate, duration);
            //the deadline is midnight on the due date
            draftDeadlineDate.setHours(23);
            draftDeadlineDate.setMinutes(59);
            datesMap.put(CtsModel.PROP_DRAFT_RESPONSE_TARGET,draftDeadlineDate);

            duration = new CtsDuration(getBusinessCalendarProvider().getDefaultProps() , getHmpoStage3MPDeadline());
            Date dispatchDeadlineDate = businessCalendar.add(startDate, duration);
            //the deadline is midnight on the due date
            dispatchDeadlineDate.setHours(23);
            dispatchDeadlineDate.setMinutes(59);
            datesMap.put(CtsModel.PROP_DISPATCH_TARGET,dispatchDeadlineDate);

            duration = new CtsDuration(getBusinessCalendarProvider().getDefaultProps() , getHmpoStage3MPDeadline());
        }else{
            duration = new CtsDuration(getBusinessCalendarProvider().getDefaultProps() , getHmpoDeadline());
        }

        Date deadlineDate = businessCalendar.add(startDate, duration);

        //the deadline is midnight on the due date
        deadlineDate.setHours(23);
        deadlineDate.setMinutes(59);
        datesMap.put(CtsModel.PROP_CASE_RESPONSE_DEADLINE,deadlineDate);

        return datesMap;
    }

    protected Map<QName, Date> doHMPOCOL(Map<QName, Serializable> after, CtsBusinessCalendar businessCalendar) {
        Map<QName, Date> datesMap = new HashMap<>();
        Date departureDate = (Date) after.get(CtsModel.DEPARTURE_DATE_FROM_UK);
        if(departureDate!=null) {
            Date deadlineDate = getDeadLineDateByDefaultProps(departureDate, businessCalendar, HMPO_COLLECTIVES_CASE_RESPONSE_DEADLINE);
            Date today = getToday();
            if(today.after(deadlineDate)){
                deadlineDate.setDate(today.getDate());
            }
            datesMap.put(CtsModel.PROP_CASE_RESPONSE_DEADLINE,deadlineDate);
     }
        return datesMap;
    }

    protected Map<QName, Date> doUKVI(CorrespondenceType correspondenceType, Map<QName, Serializable> after, CtsBusinessCalendar businessCalendar) {
        Map<QName, Date> datesMap = new HashMap<>();

        Date startDate = (Date) after.get(ContentModel.PROP_CREATED);
        Date deadlineDate = getDeadLineDateByDefaultProps(startDate, businessCalendar, UKVI_B_M_REF_CASE_RESPONSE_DEADLINE);

        datesMap.put(CtsModel.PROP_CASE_RESPONSE_DEADLINE,deadlineDate);

        deadlineDate = getDeadLineDateByDefaultProps(startDate, businessCalendar, UKVI_B_M_REF_ALLOCATE_TO_RESPONDER_TARGET);
        datesMap.put(CtsModel.PROP_ALLOCATE_TO_RESPONDER_TARGET,deadlineDate);

        deadlineDate = getDeadLineDateByDefaultProps(startDate, businessCalendar, UKVI_B_M_REF_RESPONDER_HUB_TARGET);
        datesMap.put(CtsModel.PROP_RESPONDER_HUB_TARGET,deadlineDate);

        if (correspondenceType == CorrespondenceType.UKVI_B_REF) {
            deadlineDate = getDeadLineDateByDefaultProps(startDate, businessCalendar, UKVI_B_REF_DRAFT_RESPONSE_TARGET);
        } else if (correspondenceType == CorrespondenceType.UKVI_M_REF) {
            deadlineDate = getDeadLineDateByDefaultProps(startDate, businessCalendar, UKVI_M_REF_DRAFT_RESPONSE_TARGET);
        }

        datesMap.put(CtsModel.PROP_DRAFT_RESPONSE_TARGET, deadlineDate);

        return datesMap;
    }

        /**
         * Rules for FOI
         * allocation 2 days, draft response 15 days (35 if extended), SCS approval 17 days (37 days), final approval 20 (40) days
         * @param after
         * @param businessCalendar
         * @return
         */
        protected Map<QName, Date> doFOITypes(Map<QName, Serializable> after, CtsBusinessCalendar businessCalendar) {
                    Map<QName, Date> datesMap = new HashMap<>();

                //20 days
                Date startDate = (Date) after.get(CtsModel.PROP_DATE_RECEIVED);
                startDate.setHours(9);
                startDate.setMinutes(00);
                Date deadlineDate = getDeadLineDateByAllUKProps(startDate, businessCalendar, FOI_CASE_RESPONSE_DEADLINE);
                datesMap.put(CtsModel.PROP_CASE_RESPONSE_DEADLINE,deadlineDate);
                datesMap.put(CtsModel.PROP_FINAL_APPROVAL_TARGET,deadlineDate);

                deadlineDate =  getDeadLineDateByAllUKProps(startDate, businessCalendar, FOI_ALLOCATE_TARGET);
                datesMap.put(CtsModel.PROP_ALLOCATE_TARGET,deadlineDate);

                deadlineDate =  getDeadLineDateByAllUKProps(startDate, businessCalendar, FOI_DRAFT_RESPONSE_TARGET);
                datesMap.put(CtsModel.PROP_DRAFT_RESPONSE_TARGET,deadlineDate);

                deadlineDate =  getDeadLineDateByAllUKProps(startDate, businessCalendar, FOI_SCS_APPROVAL_TARGET);
                datesMap.put(CtsModel.PROP_SCS_APPROVAL_TARGET,deadlineDate);

                return datesMap;
    }


    /**
     * Ministerial
     * 2 days to allocate, 5 days to draft, 4 days for private office sign-off and dispatch
     * @param correspondenceType
     * @param after
     * @param businessCalendar
     * @return
     */
    protected Map<QName,Date> doDCUTypes(CorrespondenceType correspondenceType, Map<QName, Serializable> after, CtsBusinessCalendar businessCalendar) {
        Map<QName,Date> datesMap = new HashMap<>();
        if(correspondenceType == CorrespondenceType.DCU_MINISTERIAL) {
                Date startDate = (Date) after.get(ContentModel.PROP_CREATED);
                Date deadlineDate = getDeadLineDateByDefaultProps(startDate, businessCalendar, DCU_MINISTERIAL_CASE_RESPONSE_DEADLINE);
                datesMap.put(CtsModel.PROP_CASE_RESPONSE_DEADLINE,deadlineDate);

                deadlineDate = getDeadLineDateByDefaultProps(startDate, businessCalendar, DCU_MINISTERIAL_ALLOCATE_TARGET);
                datesMap.put(CtsModel.PROP_ALLOCATE_TARGET,deadlineDate);

                deadlineDate = getDeadLineDateByDefaultProps(startDate, businessCalendar, DCU_MINISTERIAL_DRAFT_RESPONSE_TARGET);
                datesMap.put(CtsModel.PROP_DRAFT_RESPONSE_TARGET,deadlineDate);

                deadlineDate = getDeadLineDateByDefaultProps(startDate, businessCalendar, DCU_MINISTERIAL_PO_TARGET);
                datesMap.put(CtsModel.PROP_PO_TARGET,deadlineDate);

                //TODO DISPATCH TARGET? IS THAT JUST THE DEADLINE?
                deadlineDate = getDeadLineDateByDefaultProps(startDate, businessCalendar, DCU_MINISTERIAL_DISPATCH_TARGET);
                datesMap.put(CtsModel.PROP_DISPATCH_TARGET,deadlineDate);

            } else if (correspondenceType == CorrespondenceType.DCU_TREAT_OFFICIAL)  {
                //20 days from date added to Alfresco
                Date startDate = (Date) after.get(ContentModel.PROP_CREATED);
                Date deadlineDate = getDeadLineDateByDefaultProps(startDate, businessCalendar, DCU_TREAT_OFFICIAL_CASE_RESPONSE_DEADLINE);
                datesMap.put(CtsModel.PROP_CASE_RESPONSE_DEADLINE,deadlineDate);

            }
        return datesMap;
    }

    private Date getDeadLineDateByDefaultProps(Date startDate, CtsBusinessCalendar businessCalendar, int numberOfDays) {
        return getDeadLineDate(startDate, businessCalendar, numberOfDays, getBusinessCalendarProvider().getDefaultProps());
    }

    private Date getDeadLineDateByAllUKProps(Date startDate, CtsBusinessCalendar businessCalendar, int numberOfDays) {
        return getDeadLineDate(startDate, businessCalendar, numberOfDays, getBusinessCalendarProvider().getAllUKProps());
    }

    private Date getDeadLineDate(Date startDate, CtsBusinessCalendar businessCalendar, int numberOfDays,Properties businessCalendarProperties) {
        CtsDuration duration = new CtsDuration(businessCalendarProperties , numberOfDays + " business days");
        Date deadlineDate = businessCalendar.add(startDate, duration);
        deadlineDate.setHours(23);
        deadlineDate.setMinutes(59);
        return deadlineDate;
    }

    private NodeService getNodeService() {
        return nodeService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    private BusinessCalendarProvider getBusinessCalendarProvider() {
        return businessCalendarProvider;
    }

    public void setBusinessCalendarProvider(BusinessCalendarProvider businessCalendarProvider) {
        this.businessCalendarProvider = businessCalendarProvider;
    }

    public String getHmpoDeadline() {
        return hmpoDeadline;
    }

    public void setHmpoDeadline(String hmpoDeadline) {
        this.hmpoDeadline = hmpoDeadline;
    }

    public String getHmpoStage3MPDeadline() {
        return hmpoStage3MPDeadline;
    }

    public void setHmpoStage3MPDeadline(String hmpoStage3MPDeadline) {
        this.hmpoStage3MPDeadline = hmpoStage3MPDeadline;
    }

    public String getHmpoDraftDeadline() {
        return hmpoDraftDeadline;
    }

    public void setHmpoDraftDeadline(String hmpoDraftDeadline) {
        this.hmpoDraftDeadline = hmpoDraftDeadline;
    }

  /*this allows us to mock today's date in unit test cases otherwise
   unit test cases will depends on actual today's date */
    public Date getToday() {
        return new Date();
    }
}
