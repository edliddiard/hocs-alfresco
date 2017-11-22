package uk.gov.homeoffice.cts.behaviour;

import org.alfresco.service.namespace.QName;
import org.junit.Before;
import org.junit.Test;
import uk.gov.homeoffice.cts.calendar.CtsBusinessCalendar;
import uk.gov.homeoffice.cts.helpers.BusinessCalendarProvider;
import uk.gov.homeoffice.cts.helpers.CodePropertyProvider;
import uk.gov.homeoffice.cts.helpers.PropertyProvider;
import uk.gov.homeoffice.cts.model.CtsModel;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import static org.junit.Assert.assertEquals;

/**
 * Created by davidt on 26/11/2014.
 */
public class PitLetterSentDateBehaviourTest {

    BusinessCalendarProvider businessCalendarProvider;
    PitLetterSentDateBehaviour instance;

    @Before
    public void setUp(){
        businessCalendarProvider = new BusinessCalendarProvider();
        PropertyProvider propertyProvider = new CodePropertyProvider();
        businessCalendarProvider.setPropertyProvider(propertyProvider);
        businessCalendarProvider.init();

        instance = new PitLetterSentDateBehaviour();
        instance.setBusinessCalendarProvider(businessCalendarProvider);
    }

    @Test
    public void testGetNewDeadlineAndTargets() {
        Date pitLetterSentDate = new Date("24 Sep 2014  12:00:00");
        CtsBusinessCalendar businessCalendar = new CtsBusinessCalendar(businessCalendarProvider.getDefaultProps());

        Map<QName,Date> datesMap = instance.getNewDeadlineAndTargets(pitLetterSentDate, businessCalendar);

        Date caseResponseDeadline = datesMap.get(CtsModel.PROP_CASE_RESPONSE_DEADLINE);
        assertEquals(22, caseResponseDeadline.getDate());
        assertEquals(Calendar.OCTOBER, caseResponseDeadline.getMonth());
        assertEquals(23, caseResponseDeadline.getHours());
        assertEquals(59, caseResponseDeadline.getMinutes());

        Date finalApprovalTarget = datesMap.get(CtsModel.PROP_FINAL_APPROVAL_TARGET);
        assertEquals(22, finalApprovalTarget.getDate());
        assertEquals(Calendar.OCTOBER, finalApprovalTarget.getMonth());
        assertEquals(23, finalApprovalTarget.getHours());
        assertEquals(59, finalApprovalTarget.getMinutes());

        Date allocateTarget = datesMap.get(CtsModel.PROP_ALLOCATE_TARGET);
        assertEquals(26, allocateTarget.getDate());
        assertEquals(Calendar.SEPTEMBER, allocateTarget.getMonth());
        assertEquals(23, allocateTarget.getHours());
        assertEquals(59, allocateTarget.getMinutes());

        Date draftResponseTarget = datesMap.get(CtsModel.PROP_DRAFT_RESPONSE_TARGET);
        assertEquals(15, draftResponseTarget.getDate());
        assertEquals(Calendar.OCTOBER, draftResponseTarget.getMonth());
        assertEquals(23, draftResponseTarget.getHours());
        assertEquals(59, draftResponseTarget.getMinutes());

        Date scsApprovalTarget = datesMap.get(CtsModel.PROP_SCS_APPROVAL_TARGET);
        assertEquals(17, scsApprovalTarget.getDate());
        assertEquals(Calendar.OCTOBER, scsApprovalTarget.getMonth());
        assertEquals(23, scsApprovalTarget.getHours());
        assertEquals(59, scsApprovalTarget.getMinutes());
    }
}
