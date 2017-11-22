package uk.gov.homeoffice.cts.behaviour;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.jbpm.calendar.CtsDuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.homeoffice.cts.calendar.CtsBusinessCalendar;
import uk.gov.homeoffice.cts.helpers.BusinessCalendarProvider;
import uk.gov.homeoffice.cts.model.CtsModel;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by davidt on 25/11/2014.
 */
public class PitLetterSentDateBehaviour implements PropertyUpdateBehaviour {
    private static final Logger LOGGER = LoggerFactory.getLogger(PitLetterSentDateBehaviour.class);
    private NodeService nodeService;
    private BusinessCalendarProvider businessCalendarProvider;

    @Override
    public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
        Date pitLetterSentDateBefore = (Date) before.get(CtsModel.PROP_PIT_LETTER_SENT_DATE);
        Date pitLetterSentDateAfter = (Date) after.get(CtsModel.PROP_PIT_LETTER_SENT_DATE);

        //check it has changed
        if(BehaviourHelper.hasChangedDate(pitLetterSentDateBefore, pitLetterSentDateAfter)) {
            String caseType = (String)nodeService.getProperty(nodeRef, CtsModel.PROP_CORRESPONDENCE_TYPE);
            CtsBusinessCalendar businessCalendar = getBusinessCalendarProvider().getBusinessCalendar(caseType);
            Map<QName,Date> datesMap;
            datesMap = getNewDeadlineAndTargets(pitLetterSentDateAfter, businessCalendar);

            Set<QName> keyset = datesMap.keySet();
            for (QName qName : keyset) {
                Date date = datesMap.get(qName);
                if(date != null) {
                    getNodeService().setProperty(nodeRef, qName, date);
                }
            }
        }
    }

    protected Map<QName, Date> getNewDeadlineAndTargets(Date pitLetterSentDateAfter, CtsBusinessCalendar businessCalendar) {
        Map<QName, Date> datesMap = new HashMap<>();

        //20 days from letter sent date
        Date startDate = pitLetterSentDateAfter;
        //the deadline/targets are midnight on the due date
        startDate.setHours(23);
        startDate.setMinutes(59);
        CtsDuration duration = new CtsDuration(getBusinessCalendarProvider().getDefaultProps() ,"20 business days");
        Date deadlineDate = businessCalendar.add(startDate, duration);
        LOGGER.debug("Setting FOI case deadline and final approval target = " + deadlineDate.toString());
        datesMap.put(CtsModel.PROP_CASE_RESPONSE_DEADLINE, deadlineDate);
        datesMap.put(CtsModel.PROP_FINAL_APPROVAL_TARGET, deadlineDate);

        duration = new CtsDuration(getBusinessCalendarProvider().getDefaultProps() ,"2 business days");
        deadlineDate = businessCalendar.add(startDate, duration);
        LOGGER.debug("Setting FOI allocate target = " + deadlineDate.toString());
        datesMap.put(CtsModel.PROP_ALLOCATE_TARGET, deadlineDate);

        duration = new CtsDuration(getBusinessCalendarProvider().getDefaultProps() ,"15 business days");
        deadlineDate = businessCalendar.add(startDate, duration);
        LOGGER.debug("Setting FOI draft response target = " + deadlineDate.toString());
        datesMap.put(CtsModel.PROP_DRAFT_RESPONSE_TARGET, deadlineDate);

        duration = new CtsDuration(getBusinessCalendarProvider().getDefaultProps() ,"17 business days");
        deadlineDate = businessCalendar.add(startDate, duration);
        LOGGER.debug("Setting FOI SCS approval target = " + deadlineDate.toString());
        datesMap.put(CtsModel.PROP_SCS_APPROVAL_TARGET, deadlineDate);

        return datesMap;
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
}
