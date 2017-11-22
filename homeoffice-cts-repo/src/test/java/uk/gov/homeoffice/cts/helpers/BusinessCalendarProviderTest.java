package uk.gov.homeoffice.cts.helpers;


import org.jbpm.calendar.Duration;
import org.junit.Before;
import org.junit.Test;
import uk.gov.homeoffice.cts.calendar.CtsBusinessCalendar;
import org.jbpm.calendar.CtsDuration;

import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static uk.gov.homeoffice.cts.model.CorrespondenceType.LORDS_WRITTEN;
import static uk.gov.homeoffice.cts.model.CorrespondenceType.NAMED_DAY;
import static uk.gov.homeoffice.cts.model.CorrespondenceType.ORDINARY_WRITTEN;

/**
 * Test to show two different calendars working at the same time returning different
 * due dates.
 * Created by chris on 11/08/2014.
 */
@SuppressWarnings("deprecation")
public class BusinessCalendarProviderTest {
    BusinessCalendarProvider businessCalendarProvider;
    @Before
    public void setUp(){
        businessCalendarProvider = new BusinessCalendarProvider();
        PropertyProvider propertyProvider = new CodePropertyProvider();
        businessCalendarProvider.setPropertyProvider(propertyProvider);
        businessCalendarProvider.init();
    }

    @Test
    public void testBothCalendarsNamedDay(){
        CtsBusinessCalendar businessCalendarDefault = new CtsBusinessCalendar(businessCalendarProvider.getDefaultProps());
        businessCalendarProvider.setDefaultCalendar(businessCalendarDefault);
        CtsBusinessCalendar businessCalendarRecess = new CtsBusinessCalendar(businessCalendarProvider.getRecessProps());
        businessCalendarProvider.setPqCalendar(businessCalendarRecess);

        CtsBusinessCalendar businessCalendar = businessCalendarProvider.getBusinessCalendar(null);

        Date christmasEveDate = new Date("24 Dec 2014  12:00:00");

        CtsDuration duration = new CtsDuration(businessCalendarProvider.getDefaultProps(), "1 business days");
        Date dueDate = businessCalendar.add(christmasEveDate, duration);

        assertEquals("Should be 29th", 29, dueDate.getDate());
        assertEquals("Should be December", Calendar.DECEMBER, dueDate.getMonth());

        CtsBusinessCalendar businessCalendarPQ = businessCalendarProvider.getBusinessCalendar(NAMED_DAY.getCode());

        CtsDuration ppqDuration = new CtsDuration(businessCalendarProvider.getRecessProps(),"1 business days");

        Date ppqDueDate = businessCalendarPQ.add(christmasEveDate, ppqDuration);

        assertEquals("Should be 29th", 29, ppqDueDate.getDate());
        assertEquals("Should be January", Calendar.DECEMBER, ppqDueDate.getMonth());
    }

    @Test
    public void testOrdinaryPQs(){
        CtsBusinessCalendar businessCalendarDefault = new CtsBusinessCalendar(businessCalendarProvider.getDefaultProps());
        businessCalendarProvider.setDefaultCalendar(businessCalendarDefault);
        CtsBusinessCalendar businessCalendarRecess = new CtsBusinessCalendar(businessCalendarProvider.getRecessProps());
        businessCalendarProvider.setPqCalendar(businessCalendarRecess);

        Date christmasEveDate = new Date("24 Dec 2014  12:00:00");
        Duration duration = new Duration("5 business days");

        CtsBusinessCalendar businessCalendarPQ = businessCalendarProvider.getBusinessCalendar(ORDINARY_WRITTEN.getCode());
        Date ppqDueDate = businessCalendarPQ.add(christmasEveDate, duration);

        assertEquals("Should be 5th", 5, ppqDueDate.getDate());
        assertEquals("Should be January", Calendar.JANUARY, ppqDueDate.getMonth());
    }

    @Test
    public void testOPQSampleData() {
        CtsBusinessCalendar businessCalendarPQ = businessCalendarProvider.getBusinessCalendar(ORDINARY_WRITTEN.getCode());
        Date ppqDueDate = businessCalendarPQ.add(new Date("10 Sep 2015 12:00:00"), new Duration("6 business days"));
        assertEquals("Should be 18th", 18, ppqDueDate.getDate());
        assertEquals("Should be September", Calendar.SEPTEMBER, ppqDueDate.getMonth());

        ppqDueDate = businessCalendarPQ.add(new Date("11 Sep 2015 12:00:00"), new Duration("6 business days"));
        assertEquals("Should be 21st", 21, ppqDueDate.getDate());
        assertEquals("Should be September", Calendar.SEPTEMBER, ppqDueDate.getMonth());

        //An Order Paper date of 04/09/15 would equate to an Answering Date of 14/09/15.
        ppqDueDate = businessCalendarPQ.add(new Date("4 Sep 2015 12:00:00"), new Duration("6 business days"));
        assertEquals("Should be 14th", 14, ppqDueDate.getDate());
        assertEquals("Should be September", Calendar.SEPTEMBER, ppqDueDate.getMonth());
    }

    @Test
    public void testLordsWrittenPQs(){
        CtsBusinessCalendar businessCalendarDefault = new CtsBusinessCalendar(businessCalendarProvider.getDefaultProps());
        businessCalendarProvider.setDefaultCalendar(businessCalendarDefault);
        CtsBusinessCalendar businessCalendarRecess = new CtsBusinessCalendar(businessCalendarProvider.getRecessProps());
        businessCalendarProvider.setPqCalendar(businessCalendarRecess);

        Date christmasEveDate = new Date("24 Dec 2014  12:00:00");
        Duration duration = new Duration("15 business days");

        CtsBusinessCalendar businessCalendarPQ = businessCalendarProvider.getBusinessCalendar(LORDS_WRITTEN.getCode());
        Date ppqDueDate = businessCalendarPQ.add(christmasEveDate, duration);

        assertEquals("Should be 19th", 19, ppqDueDate.getDate());
        assertEquals("Should be January", Calendar.JANUARY, ppqDueDate.getMonth());
    }

}
