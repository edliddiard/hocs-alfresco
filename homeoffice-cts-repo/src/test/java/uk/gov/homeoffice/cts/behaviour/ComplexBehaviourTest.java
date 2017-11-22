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
 * Created by davidt on 30/12/2014.
 */
public class ComplexBehaviourTest {

    BusinessCalendarProvider businessCalendarProvider;
    ComplexBehaviour instance;

    @Before
    public void setUp(){
        businessCalendarProvider = new BusinessCalendarProvider();
        PropertyProvider propertyProvider = new CodePropertyProvider();
        businessCalendarProvider.setPropertyProvider(propertyProvider);
        businessCalendarProvider.init();

        instance = new ComplexBehaviour();
        instance.setBusinessCalendarProvider(businessCalendarProvider);
    }

    @Test
    public void testGetNewDeadlineAndTargets() {
        Date currentDeadline = new Date("24 Sep 2014  12:00:00");
        CtsBusinessCalendar businessCalendar = new CtsBusinessCalendar(businessCalendarProvider.getDefaultProps());

        Map<QName,Date> datesMap = instance.getNewDeadlineAndTargets(currentDeadline, businessCalendar);

        Date caseResponseDeadline = datesMap.get(CtsModel.PROP_CASE_RESPONSE_DEADLINE);
        assertEquals(22, caseResponseDeadline.getDate());
        assertEquals(Calendar.OCTOBER, caseResponseDeadline.getMonth());
        assertEquals(23, caseResponseDeadline.getHours());
        assertEquals(59, caseResponseDeadline.getMinutes());
    }
}
