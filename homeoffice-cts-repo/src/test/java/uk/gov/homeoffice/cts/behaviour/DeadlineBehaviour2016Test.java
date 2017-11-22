package uk.gov.homeoffice.cts.behaviour;

import org.alfresco.model.ContentModel;
import org.alfresco.service.namespace.QName;
import org.junit.Before;
import org.junit.Test;
import uk.gov.homeoffice.cts.calendar.CtsBusinessCalendar;
import uk.gov.homeoffice.cts.helpers.BusinessCalendarProvider;
import uk.gov.homeoffice.cts.helpers.CodePropertyProvider;
import uk.gov.homeoffice.cts.helpers.PropertyProvider;
import uk.gov.homeoffice.cts.model.CtsModel;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static uk.gov.homeoffice.cts.model.CorrespondenceType.*;

/**
 * Test for the handling of PQs in the deadline behaviour
 * Created by chris on 11/08/2014.
 */
public class DeadlineBehaviour2016Test {

    BusinessCalendarProvider businessCalendarProvider;
    DeadlineBehaviour deadlineBehaviour;

    @Before
    public void setUp(){
        businessCalendarProvider = new BusinessCalendarProvider();
        PropertyProvider propertyProvider = new CodePropertyProvider();
        businessCalendarProvider.setPropertyProvider(propertyProvider);
        businessCalendarProvider.init();

        deadlineBehaviour = new DeadlineBehaviour();
        deadlineBehaviour.setBusinessCalendarProvider(businessCalendarProvider);
    }


    @Test
    public void testUKVIMinisterial8am(){
        Map<QName, Serializable> afterValues = new HashMap<>();
        Date startDate = new Date("21 Oct 2016  08:00:00");
        afterValues.put(ContentModel.PROP_CREATED, startDate);
        CtsBusinessCalendar businessCalendar = new CtsBusinessCalendar(businessCalendarProvider.getDefaultProps());

        Map<QName,Date> datesMap = deadlineBehaviour.doUKVI(UKVI_M_REF, afterValues, businessCalendar);
        Date dueDate = datesMap.get(CtsModel.PROP_CASE_RESPONSE_DEADLINE);

        assertEquals(18, dueDate.getDate());
        assertEquals(Calendar.NOVEMBER, dueDate.getMonth());
        assertEquals(23, dueDate.getHours());
        assertEquals(59, dueDate.getMinutes());
    }

    @Test
    public void testUKVIMinisterial9am(){
        Map<QName, Serializable> afterValues = new HashMap<>();
        Date startDate = new Date("24 Oct 2016  09:00:00");
        afterValues.put(ContentModel.PROP_CREATED, startDate);
        CtsBusinessCalendar businessCalendar = new CtsBusinessCalendar(businessCalendarProvider.getDefaultProps());

        Map<QName,Date> datesMap = deadlineBehaviour.doUKVI(UKVI_M_REF, afterValues, businessCalendar);
        Date dueDate = datesMap.get(CtsModel.PROP_CASE_RESPONSE_DEADLINE);

        assertEquals(21, dueDate.getDate());
        assertEquals(Calendar.NOVEMBER, dueDate.getMonth());
        assertEquals(23, dueDate.getHours());
        assertEquals(59, dueDate.getMinutes());
    }

    @Test
    public void testUKVIMinisterial2pm(){
        Map<QName, Serializable> afterValues = new HashMap<>();
        Date startDate = new Date("24 Oct 2016  14:00:00");
        afterValues.put(ContentModel.PROP_CREATED, startDate);
        CtsBusinessCalendar businessCalendar = new CtsBusinessCalendar(businessCalendarProvider.getDefaultProps());

        Map<QName,Date> datesMap = deadlineBehaviour.doUKVI(UKVI_M_REF, afterValues, businessCalendar);
        Date dueDate = datesMap.get(CtsModel.PROP_CASE_RESPONSE_DEADLINE);

        assertEquals(21, dueDate.getDate());
        assertEquals(Calendar.NOVEMBER, dueDate.getMonth());
        assertEquals(23, dueDate.getHours());
        assertEquals(59, dueDate.getMinutes());
    }

    @Test
    public void testUKVIMinisterial18thOctober9am(){
        Map<QName, Serializable> afterValues = new HashMap<>();
        Date startDate = new Date("18 Oct 2016  09:00:00");
        afterValues.put(ContentModel.PROP_CREATED, startDate);
        CtsBusinessCalendar businessCalendar = new CtsBusinessCalendar(businessCalendarProvider.getDefaultProps());

        Map<QName,Date> datesMap = deadlineBehaviour.doUKVI(UKVI_M_REF, afterValues, businessCalendar);
        Date dueDate = datesMap.get(CtsModel.PROP_CASE_RESPONSE_DEADLINE);

        assertEquals(15, dueDate.getDate());
        assertEquals(Calendar.NOVEMBER, dueDate.getMonth());
        assertEquals(23, dueDate.getHours());
        assertEquals(59, dueDate.getMinutes());
    }

    @Test
    public void testUKVIMinisterial18thOctober2pm(){
        Map<QName, Serializable> afterValues = new HashMap<>();
        Date startDate = new Date("18 Oct 2016  14:00:00");
        afterValues.put(ContentModel.PROP_CREATED, startDate);
        CtsBusinessCalendar businessCalendar = new CtsBusinessCalendar(businessCalendarProvider.getDefaultProps());

        Map<QName,Date> datesMap = deadlineBehaviour.doUKVI(UKVI_M_REF, afterValues, businessCalendar);
        Date dueDate = datesMap.get(CtsModel.PROP_CASE_RESPONSE_DEADLINE);

        assertEquals(15, dueDate.getDate());
        assertEquals(Calendar.NOVEMBER, dueDate.getMonth());
        assertEquals(23, dueDate.getHours());
        assertEquals(59, dueDate.getMinutes());
    }

    @Test
    public void testUKVIMinisterial18thOctober6pm(){
        Map<QName, Serializable> afterValues = new HashMap<>();
        Date startDate = new Date("18 Oct 2016  18:00:00");
        afterValues.put(ContentModel.PROP_CREATED, startDate);
        CtsBusinessCalendar businessCalendar = new CtsBusinessCalendar(businessCalendarProvider.getDefaultProps());

        Map<QName,Date> datesMap = deadlineBehaviour.doUKVI(UKVI_M_REF, afterValues, businessCalendar);
        Date dueDate = datesMap.get(CtsModel.PROP_CASE_RESPONSE_DEADLINE);

        assertEquals(15, dueDate.getDate());
        assertEquals(Calendar.NOVEMBER, dueDate.getMonth());
        assertEquals(23, dueDate.getHours());
        assertEquals(59, dueDate.getMinutes());
    }

    @Test
    public void testUKVIBref18thOctober9am(){
        Map<QName, Serializable> afterValues = new HashMap<>();
        Date startDate = new Date("18 Oct 2016  09:00:00");
        afterValues.put(ContentModel.PROP_CREATED, startDate);
        CtsBusinessCalendar businessCalendar = new CtsBusinessCalendar(businessCalendarProvider.getDefaultProps());

        Map<QName,Date> datesMap = deadlineBehaviour.doUKVI(UKVI_B_REF, afterValues, businessCalendar);
        Date dueDate = datesMap.get(CtsModel.PROP_CASE_RESPONSE_DEADLINE);

        assertEquals(15, dueDate.getDate());
        assertEquals(Calendar.NOVEMBER, dueDate.getMonth());
        assertEquals(23, dueDate.getHours());
        assertEquals(59, dueDate.getMinutes());
    }

    @Test
    public void testUKVIBref18thOctober2pm(){
        Map<QName, Serializable> afterValues = new HashMap<>();
        Date startDate = new Date("18 Oct 2016  14:00:00");
        afterValues.put(ContentModel.PROP_CREATED, startDate);
        CtsBusinessCalendar businessCalendar = new CtsBusinessCalendar(businessCalendarProvider.getDefaultProps());

        Map<QName,Date> datesMap = deadlineBehaviour.doUKVI(UKVI_B_REF, afterValues, businessCalendar);
        Date dueDate = datesMap.get(CtsModel.PROP_CASE_RESPONSE_DEADLINE);

        assertEquals(15, dueDate.getDate());
        assertEquals(Calendar.NOVEMBER, dueDate.getMonth());
        assertEquals(23, dueDate.getHours());
        assertEquals(59, dueDate.getMinutes());
    }

    @Test
    public void testUKVIBref18thOctober6pm(){
        Map<QName, Serializable> afterValues = new HashMap<>();
        Date startDate = new Date("18 Oct 2016  18:00:00");
        afterValues.put(ContentModel.PROP_CREATED, startDate);
        CtsBusinessCalendar businessCalendar = new CtsBusinessCalendar(businessCalendarProvider.getDefaultProps());

        Map<QName,Date> datesMap = deadlineBehaviour.doUKVI(UKVI_B_REF, afterValues, businessCalendar);
        Date dueDate = datesMap.get(CtsModel.PROP_CASE_RESPONSE_DEADLINE);

        assertEquals(15, dueDate.getDate());
        assertEquals(Calendar.NOVEMBER, dueDate.getMonth());
        assertEquals(23, dueDate.getHours());
        assertEquals(59, dueDate.getMinutes());
    }

    @Test
    public void testUKVIMref29thNovember10am(){
        Map<QName, Serializable> afterValues = new HashMap<>();
        Date startDate = new Date("29 Nov 2016  10:00:00");
        afterValues.put(ContentModel.PROP_CREATED, startDate);
        CtsBusinessCalendar businessCalendar = new CtsBusinessCalendar(businessCalendarProvider.getDefaultProps());

        Map<QName,Date> datesMap = deadlineBehaviour.doUKVI(UKVI_M_REF, afterValues, businessCalendar);
        Date dueDate = datesMap.get(CtsModel.PROP_CASE_RESPONSE_DEADLINE);

        assertEquals(29, dueDate.getDate());
        assertEquals(Calendar.DECEMBER, dueDate.getMonth());
        assertEquals(23, dueDate.getHours());
        assertEquals(59, dueDate.getMinutes());
    }

    @Test
    public void testDCUMinisterialJanuary17(){
        Map<QName, Serializable> afterValues = new HashMap<>();
        Date startDate = new Date("31 January 2017  10:00:00");
        afterValues.put(ContentModel.PROP_CREATED, startDate);
        CtsBusinessCalendar businessCalendar = new CtsBusinessCalendar(businessCalendarProvider.getDefaultProps());

        Map<QName,Date> datesMap = deadlineBehaviour.doDCUTypes(DCU_MINISTERIAL, afterValues, businessCalendar);
        Date dueDate = datesMap.get(CtsModel.PROP_DRAFT_RESPONSE_TARGET);

        assertEquals(14, dueDate.getDate());
        assertEquals(Calendar.FEBRUARY, dueDate.getMonth());
        assertEquals(23, dueDate.getHours());
        assertEquals(59, dueDate.getMinutes());
    }

    @Test
    public void testDCUMinisterialFebruary17(){
        Map<QName, Serializable> afterValues = new HashMap<>();
        Date startDate = new Date("10 February 2017  10:00:00");
        afterValues.put(ContentModel.PROP_CREATED, startDate);
        CtsBusinessCalendar businessCalendar = new CtsBusinessCalendar(businessCalendarProvider.getDefaultProps());

        Map<QName,Date> datesMap = deadlineBehaviour.doDCUTypes(DCU_MINISTERIAL, afterValues, businessCalendar);
        Date dueDate = datesMap.get(CtsModel.PROP_DRAFT_RESPONSE_TARGET);

        assertEquals(24, dueDate.getDate());
        assertEquals(Calendar.FEBRUARY, dueDate.getMonth());
        assertEquals(23, dueDate.getHours());
        assertEquals(59, dueDate.getMinutes());
    }

}
