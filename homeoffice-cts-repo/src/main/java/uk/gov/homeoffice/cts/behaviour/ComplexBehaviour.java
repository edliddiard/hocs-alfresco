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
 * Created by davidt on 30/12/2014.
 */
public class ComplexBehaviour implements PropertyUpdateBehaviour {
    private static final Logger LOGGER = LoggerFactory.getLogger(ComplexBehaviour.class);
    private NodeService nodeService;
    private BusinessCalendarProvider businessCalendarProvider;

    @Override
    public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
        Boolean complexBefore = (Boolean) before.get(CtsModel.PROP_COMPLEX);
        Boolean complexAfter = (Boolean) after.get(CtsModel.PROP_COMPLEX);

        //check it has changed
        if(BehaviourHelper.hasChangedBoolean(complexBefore, complexAfter) && complexAfter) {
            String caseType = (String) nodeService.getProperty(nodeRef, CtsModel.PROP_CORRESPONDENCE_TYPE);
            Date currentDeadline = (Date) nodeService.getProperty(nodeRef, CtsModel.PROP_CASE_RESPONSE_DEADLINE);
            if (currentDeadline == null) {
                currentDeadline = new Date();
            }
            CtsBusinessCalendar businessCalendar = getBusinessCalendarProvider().getBusinessCalendar(caseType);
            Map<QName, Date> datesMap;
            datesMap = getNewDeadlineAndTargets(currentDeadline, businessCalendar);

            Set<QName> keyset = datesMap.keySet();
            for (QName qName : keyset) {
                Date date = datesMap.get(qName);
                if (date != null) {
                    getNodeService().setProperty(nodeRef, qName, date);
                }
            }
        }
    }

    protected Map<QName, Date> getNewDeadlineAndTargets(Date currentDeadline, CtsBusinessCalendar businessCalendar) {
        Map<QName, Date> datesMap = new HashMap<>();

        //20 days from current deadline
        Date startDate = currentDeadline;
        //the deadline/targets are midnight on the due date
        startDate.setHours(23);
        startDate.setMinutes(59);
        CtsDuration duration = new CtsDuration(getBusinessCalendarProvider().getDefaultProps() ,"20 business days");
        Date deadlineDate = businessCalendar.add(startDate, duration);
        LOGGER.debug("Setting FOI case deadline and final approval target = " + deadlineDate.toString());
        datesMap.put(CtsModel.PROP_CASE_RESPONSE_DEADLINE, deadlineDate);

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
