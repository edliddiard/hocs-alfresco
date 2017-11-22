package uk.gov.homeoffice.cts;


import org.activiti.engine.impl.calendar.DueDateBusinessCalendar;
import org.activiti.engine.impl.calendar.DurationBusinessCalendar;
import org.jbpm.calendar.BusinessCalendar;
import org.jbpm.calendar.CtsDuration;
import org.jbpm.calendar.Duration;
import org.junit.Ignore;
import org.junit.Test;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Tests around working out deadlines using a business calendar
 * Created by chris on 08/08/2014.
 */
public class BusinessCalendarTest {
    private Properties props;


    /**
     * Activiti Business Calendar with very simple functionality
     */
    @Test
    public void testDueDate(){
        DueDateBusinessCalendar businessCalendar = new DueDateBusinessCalendar();
        Date date = businessCalendar.resolveDuedate("P5D");
    }

    /**
     * Activiti Business Calendar with very simple functionality
     */
    @Test
    public void testDuration(){
        DurationBusinessCalendar businessCalendar = new DurationBusinessCalendar();
        Date date = businessCalendar.resolveDuedate("P5D");
    }

    /**
     * Check loading dates
     */
    @Test
    public void testJBpm(){
        Date belgianDate = new Date("Fri, 16 May 2005  13:30:00 GMT");
        Date todaysDate = new Date("Fri, 25 Aug 2017  16:30:00 GMT");

        BusinessCalendar businessCalendar = new BusinessCalendar();

        assertFalse(businessCalendar.isHoliday(belgianDate));

        Duration duration = new Duration("2 business days"); //26,27 is weekend + 28th Aug 2017 is holiday
        Date dueDate = businessCalendar.add(todaysDate, duration);

        assertEquals(30, dueDate.getDate());
        assertEquals(7, dueDate.getMonth());

    }

    /**
     * Test for something that arrives christmas eve, which is in the middle of a recess
     */
    @Test
    public void testJBpmChristmas() {
        org.jbpm.calendar.BusinessCalendar businessCalendar = new org.jbpm.calendar.BusinessCalendar(getProps());

        Date todayDate = new Date("Wed, 24 Dec 2014  13:30:00 GMT");

        CtsDuration duration = new CtsDuration(getProps(),"1 business days");
        Date dueDate = businessCalendar.add(todayDate, duration);

        assertEquals(5, dueDate.getDate());
        assertEquals(0, dueDate.getMonth());
    }

    /**
     * Test for a date in a recess
     */
    @Test
    public void testJBpmRecess() {
        org.jbpm.calendar.BusinessCalendar businessCalendar = new org.jbpm.calendar.BusinessCalendar(getProps());

        Date todayDate = new Date("12 Sep 2014  10:00:00 GMT");

        CtsDuration duration = new CtsDuration("1 business days");
        Date dueDate = businessCalendar.add(todayDate, duration);

        assertEquals(13, dueDate.getDate());
        assertEquals(9, dueDate.getMonth());
    }


    /**
     * simple one day test
     */
    @Test
    public void testOneDay() {
        org.jbpm.calendar.BusinessCalendar businessCalendar = new org.jbpm.calendar.BusinessCalendar(getProps());

        Date todayDate = new Date("10 Sep 2014  10:00:00 GMT");

        CtsDuration duration = new CtsDuration("1 business days");
        Date dueDate = businessCalendar.add(todayDate, duration);

        assertEquals(11, dueDate.getDate());
        assertEquals(Calendar.SEPTEMBER, dueDate.getMonth());
    }


    /**
     * Test for something received before the start of the day
     */
    @Test
    public void testOneDayEarly() {
        org.jbpm.calendar.BusinessCalendar businessCalendar = new org.jbpm.calendar.BusinessCalendar(getProps());

        Date todayDate = new Date("10 Sep 2014  07:00:00");
        CtsDuration duration = new CtsDuration("1 business days");
        Date dueDate = businessCalendar.add(todayDate, duration);

        assertEquals(10, dueDate.getDate());
        assertEquals(Calendar.SEPTEMBER, dueDate.getMonth());
    }

    /**
     * Test for something received at start of the day
     */
    @Test
    public void testOneDayEarlyish() {
        org.jbpm.calendar.BusinessCalendar businessCalendar = new org.jbpm.calendar.BusinessCalendar(getProps());

        Date todayDate = new Date("10 Sep 2014  09:00:00");

        CtsDuration duration = new CtsDuration("1 business days");
        Date dueDate = businessCalendar.add(todayDate, duration);

        //So the due date is 17:00, which is the same day
        assertEquals(10, dueDate.getDate());
        assertEquals(Calendar.SEPTEMBER, dueDate.getMonth());
    }

    /**
     * Testing the boundaries, the count seem to start from one minute in
     */
    @Test
    public void testOneDayUnderOneMinute() {
        org.jbpm.calendar.BusinessCalendar businessCalendar = new org.jbpm.calendar.BusinessCalendar(getProps());

        Date todayDate = new Date("10 Sep 2014  09:00:59");

        CtsDuration duration = new CtsDuration("1 business days");
        Date dueDate = businessCalendar.add(todayDate, duration);

        //So the due date is 17:00, the same day
        assertEquals(10, dueDate.getDate());
        assertEquals(Calendar.SEPTEMBER, dueDate.getMonth());
    }


    /**
     * Test the earliest rollover to the next day
     */
    @Test
    public void testOneDayOneMinute() {
        org.jbpm.calendar.BusinessCalendar businessCalendar = new org.jbpm.calendar.BusinessCalendar(getProps());

        Date todayDate = new Date("10 Sep 2014  00:01:00");

        Duration duration = new Duration("1 business days");

        Date dueDate = businessCalendar.add(todayDate, duration);

        //So the due date goes into the next day
        assertEquals(11, dueDate.getDate());
        assertEquals(Calendar.SEPTEMBER, dueDate.getMonth());
    }

    /**
     * Testing something arrived after close of business, which should be the same as start of business
     * next day
     */
    @Test
    public void testOneDayLate() {
        org.jbpm.calendar.BusinessCalendar businessCalendar = new org.jbpm.calendar.BusinessCalendar(getProps());

        Date todayDate = new Date("10 Sep 2014  20:00:00");
        CtsDuration duration = new CtsDuration("1 business days");
        Date dueDate = businessCalendar.add(todayDate, duration);

        assertEquals(11, dueDate.getDate());
        assertEquals(Calendar.SEPTEMBER, dueDate.getMonth());
    }

    /**
     * Example of properties that can be added
     * @return
     */
    private Properties getProps() {
        Properties props = new Properties();
        props.put("hour.format","HH:mm");
        props.put("weekday.monday","00:00-17:00");
        props.put("weekday.tuesday","00:00-17:00");
        props.put("weekday.wednesday","00:00-17:00");
        props.put("weekday.thursday","00:00-17:00");
        props.put("weekday.friday","00:00-17:00");
        props.put("weekday.saturday","");
        props.put("weekday.sunday","");

        props.put("day.format","dd/MM/yyyy");
        props.put("holiday.1","25/08/2014");
        props.put("holiday.2","25/12/2014");
        props.put("holiday.3","26/12/2014");
        props.put("holiday.4","12/09/2014-10/10/2014");
        props.put("holiday.5","11/11/2014-14/11/2014");
        props.put("holiday.6","18/12/2014-02/01/2015");

        props.put("business.day.expressed.in.hours","8");
        props.put("business.week.expressed.in.hours","40");
        props.put("business.month.expressed.in.business.days","21");
        props.put("business.year.expressed.in.business.days","220");

        return props;
    }
}
