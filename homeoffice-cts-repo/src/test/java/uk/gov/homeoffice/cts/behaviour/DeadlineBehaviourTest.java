package uk.gov.homeoffice.cts.behaviour;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.homeoffice.cts.calendar.CtsBusinessCalendar;
import uk.gov.homeoffice.cts.helpers.BusinessCalendarProvider;
import uk.gov.homeoffice.cts.helpers.CodePropertyProvider;
import uk.gov.homeoffice.cts.helpers.PropertyProvider;
import uk.gov.homeoffice.cts.model.CorrespondenceType;
import uk.gov.homeoffice.cts.model.CtsModel;

import java.io.Serializable;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static uk.gov.homeoffice.cts.model.CorrespondenceType.*;

/**
 * Test for the handling of PQs in the deadline behaviour
 * Created by chris on 11/08/2014.
 */
@RunWith(MockitoJUnitRunner.class)
public class DeadlineBehaviourTest {

    BusinessCalendarProvider businessCalendarProvider;
    DeadlineBehaviour deadlineBehaviour;

    @Mock private NodeService nodeService;

    @Captor ArgumentCaptor<Date> dateCaptor;

    private static  final Date dateReceived = new Date("24 Sep 2017  12:00:00");

    private NodeRef mockNode = new NodeRef("workspace://SpacesStore/3e4abc39-d0be-437e-a92f-343bdc845cf9");

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        businessCalendarProvider = new BusinessCalendarProvider();
        PropertyProvider propertyProvider = new CodePropertyProvider();
        businessCalendarProvider.setPropertyProvider(propertyProvider);
        businessCalendarProvider.init();

        deadlineBehaviour = new DeadlineBehaviour(){
            @Override
            public Date getToday() {
                return new Date("24 Sep 2017  12:00:00"); //set the today's date
            }
        };
        deadlineBehaviour.setBusinessCalendarProvider(businessCalendarProvider);
        deadlineBehaviour.setNodeService(nodeService);
    }
    @Test
    public void testHasPropertyChanged(){
        assertFalse(BehaviourHelper.hasChanged(null,null));
        assertFalse(BehaviourHelper.hasChanged("thing","thing"));
        assertFalse(BehaviourHelper.hasChanged(null,""));
        assertFalse(BehaviourHelper.hasChanged("",null));

        assertTrue(BehaviourHelper.hasChanged(null,"value"));
        assertTrue(BehaviourHelper.hasChanged("value", null));
        assertTrue(BehaviourHelper.hasChanged("", "valuo"));
        assertTrue(BehaviourHelper.hasChanged("value",""));
        assertTrue(BehaviourHelper.hasChanged("value", "valuo"));
    }

    @Test
    public void testUKVIMinisterial(){
        Map<QName, Serializable> afterValues = new HashMap<>();
        Date startDate = new Date("24 Sep 2014  12:00:00");
        afterValues.put(ContentModel.PROP_CREATED, startDate);
        CtsBusinessCalendar businessCalendar = new CtsBusinessCalendar(businessCalendarProvider.getDefaultProps());

        Map<QName,Date> datesMap = deadlineBehaviour.doUKVI(UKVI_M_REF, afterValues, businessCalendar);
        Date dueDate = datesMap.get(CtsModel.PROP_CASE_RESPONSE_DEADLINE);

        assertEquals(22, dueDate.getDate());
        assertEquals(Calendar.OCTOBER, dueDate.getMonth());
        assertEquals(23, dueDate.getHours());
        assertEquals(59, dueDate.getMinutes());
    }

    @Test
    public void testUKVIMinisterialBeforeStartOfDay(){
        Map<QName, Serializable> afterValues = new HashMap<>();
        Date startDate = new Date("24 Sep 2014  00:00:00");
        afterValues.put(ContentModel.PROP_CREATED, startDate);
        CtsBusinessCalendar businessCalendar = new CtsBusinessCalendar(businessCalendarProvider.getDefaultProps());

        Map<QName,Date> datesMap = deadlineBehaviour.doUKVI(UKVI_M_REF, afterValues, businessCalendar);
        Date dueDate = datesMap.get(CtsModel.PROP_CASE_RESPONSE_DEADLINE);

        assertEquals(21, dueDate.getDate());
        assertEquals(Calendar.OCTOBER, dueDate.getMonth());
        assertEquals(23, dueDate.getHours());
        assertEquals(59, dueDate.getMinutes());
    }

    @Test
    public void testUKVIOfficial(){
        Map<QName, Serializable> afterValues = new HashMap<>();
        Date startDate = new Date("24 Sep 2014  12:00:00");
        afterValues.put(ContentModel.PROP_CREATED, startDate);
        CtsBusinessCalendar businessCalendar = new CtsBusinessCalendar(businessCalendarProvider.getDefaultProps());

        Map<QName,Date> datesMap = deadlineBehaviour.doUKVI(UKVI_B_REF, afterValues, businessCalendar);
        Date dueDate = datesMap.get(CtsModel.PROP_CASE_RESPONSE_DEADLINE);

        assertEquals(22, dueDate.getDate());
        assertEquals(Calendar.OCTOBER, dueDate.getMonth());
        assertEquals(23, dueDate.getHours());
        assertEquals(59, dueDate.getMinutes());
    }


    @Test
    public void testFOI(){
        Map<QName, Serializable> afterValues = new HashMap<>();
        Date startDate = new Date("06 May 2015  00:00:00");
        afterValues.put(CtsModel.PROP_DATE_RECEIVED, startDate);
        CtsBusinessCalendar businessCalendar = new CtsBusinessCalendar(businessCalendarProvider.getAllUKProps());

        Map<QName,Date> datesMap = deadlineBehaviour.doFOITypes(afterValues, businessCalendar);
        Date dueDate = datesMap.get(CtsModel.PROP_CASE_RESPONSE_DEADLINE);

        assertEquals(04, dueDate.getDate());
        assertEquals(Calendar.JUNE, dueDate.getMonth());
        assertEquals(23, dueDate.getHours());
        assertEquals(59, dueDate.getMinutes());
    }

    @Test
    public void testFOIAllBankHolidays(){
        Map<QName, Serializable> afterValues = new HashMap<>();
        Date startDate = new Date("16 Nov 2015  00:00:00");
        afterValues.put(CtsModel.PROP_DATE_RECEIVED, startDate);
        CtsBusinessCalendar businessCalendar = new CtsBusinessCalendar(businessCalendarProvider.getAllUKProps());

        Map<QName,Date> datesMap = deadlineBehaviour.doFOITypes(afterValues, businessCalendar);
        Date dueDate = datesMap.get(CtsModel.PROP_CASE_RESPONSE_DEADLINE);

        assertEquals(15, dueDate.getDate());
        assertEquals(Calendar.DECEMBER, dueDate.getMonth());
        assertEquals(23, dueDate.getHours());
        assertEquals(59, dueDate.getMinutes());
    }

    @Test
    public void testDCUMinisterial(){
        Map<QName, Serializable> afterValues = new HashMap<>();
        Date startDate = new Date("24 Sep 2014  12:00:00");
        afterValues.put(ContentModel.PROP_CREATED, startDate);
        CtsBusinessCalendar businessCalendar = new CtsBusinessCalendar(businessCalendarProvider.getDefaultProps());

        Map<QName,Date> datesMap = deadlineBehaviour.doDCUTypes(DCU_MINISTERIAL, afterValues, businessCalendar);
        Date dueDate = datesMap.get(CtsModel.PROP_CASE_RESPONSE_DEADLINE);

        assertEquals(15, dueDate.getDate());
        assertEquals(Calendar.OCTOBER, dueDate.getMonth());
        assertEquals(23, dueDate.getHours());
        assertEquals(59, dueDate.getMinutes());
    }

    @Test
    public void testDCUTreatOfficial(){
        Map<QName, Serializable> afterValues = new HashMap<>();
        Date startDate = new Date("24 Sep 2014  12:00:00");
        afterValues.put(ContentModel.PROP_CREATED, startDate);
        CtsBusinessCalendar businessCalendar = new CtsBusinessCalendar(businessCalendarProvider.getDefaultProps());

        Map<QName,Date> datesMap = deadlineBehaviour.doDCUTypes(DCU_TREAT_OFFICIAL, afterValues, businessCalendar);
        Date dueDate = datesMap.get(CtsModel.PROP_CASE_RESPONSE_DEADLINE);

        assertEquals(22, dueDate.getDate());
        assertEquals(Calendar.OCTOBER, dueDate.getMonth());
        assertEquals(23, dueDate.getHours());
        assertEquals(59, dueDate.getMinutes());
    }

    @Test
    public void testDCUNo10(){
        Map<QName, Serializable> afterValues = new HashMap<>();
        Date startDate = new Date("24 Sep 2014  12:00:00");
        afterValues.put(CtsModel.PROP_DATE_RECEIVED, startDate);
        CtsBusinessCalendar businessCalendar = new CtsBusinessCalendar(businessCalendarProvider.getDefaultProps());
        Map<QName,Date> datesMap = deadlineBehaviour.doDCUTypes(DCU_NUMBER_10, afterValues, businessCalendar);
        Date dueDate = datesMap.get(CtsModel.PROP_CASE_RESPONSE_DEADLINE);

        assertNull(dueDate);
    }

    @Test
    public void testUKVINo10(){
        Map<QName, Serializable> afterValues = new HashMap<>();
        Date startDate = new Date("24 Sep 2014  12:00:00");
        afterValues.put(CtsModel.PROP_DATE_RECEIVED, startDate);
        CtsBusinessCalendar businessCalendar = new CtsBusinessCalendar(businessCalendarProvider.getDefaultProps());

        Map<QName,Date> datesMap = deadlineBehaviour.doDCUTypes(UKVI_NUMBER_10, afterValues, businessCalendar);
        Date dueDate = datesMap.get(CtsModel.PROP_CASE_RESPONSE_DEADLINE);

        assertNull(dueDate);
    }

    @Test
    public void testHMPOGeneralAndComplaint(){
        Map<QName, Serializable> afterValues = new HashMap<>();
        Date startDate = new Date("24 Sep 2014  12:00:00");
        afterValues.put(ContentModel.PROP_CREATED, startDate);
        CtsBusinessCalendar businessCalendar = new CtsBusinessCalendar(businessCalendarProvider.getDefaultProps());

        deadlineBehaviour.setHmpoDeadline("10 business days");

        Map<QName,Date> datesMap = deadlineBehaviour.doHMPO(afterValues, businessCalendar);
        Date dueDate = datesMap.get(CtsModel.PROP_CASE_RESPONSE_DEADLINE);

        assertEquals(8, dueDate.getDate());
        assertEquals(Calendar.OCTOBER, dueDate.getMonth());
        assertEquals(23, dueDate.getHours());
        assertEquals(59, dueDate.getMinutes());
    }

    @Test
    public void testHMPOComplaintStage3MPComplaint(){
        Map<QName, Serializable> afterValues = new HashMap<>();
        Date startDate = new Date("24 Sep 2014  12:00:00");
        afterValues.put(ContentModel.PROP_CREATED, startDate);
        afterValues.put(CtsModel.PROP_HMPO_STAGE, "MP complaint");
        CtsBusinessCalendar businessCalendar = new CtsBusinessCalendar(businessCalendarProvider.getDefaultProps());

        deadlineBehaviour.setHmpoStage3MPDeadline("15 business days");
        deadlineBehaviour.setHmpoDraftDeadline("8 business days");

        Map<QName,Date> datesMap = deadlineBehaviour.doHMPO(afterValues, businessCalendar);
        Date dueDate = datesMap.get(CtsModel.PROP_CASE_RESPONSE_DEADLINE);
        assertEquals(15, dueDate.getDate());
        assertEquals(Calendar.OCTOBER, dueDate.getMonth());
        assertEquals(23, dueDate.getHours());
        assertEquals(59, dueDate.getMinutes());


        Date draftDate = datesMap.get(CtsModel.PROP_DRAFT_RESPONSE_TARGET);
        assertEquals(6, draftDate.getDate());
        assertEquals(Calendar.OCTOBER, draftDate.getMonth());
        assertEquals(23, draftDate.getHours());
        assertEquals(59, draftDate.getMinutes());

        Date dispatchDate = datesMap.get(CtsModel.PROP_DISPATCH_TARGET);
        assertEquals(15, dispatchDate.getDate());
        assertEquals(Calendar.OCTOBER, dispatchDate.getMonth());
        assertEquals(23, dispatchDate.getHours());
        assertEquals(59, dispatchDate.getMinutes());
    }

    /**
     * As the date used is custom it is possible it could get created
     * without that date.
     */
    @Test
    public void testHMPOGeneralNullDate(){
        Map<QName, Serializable> afterValues = new HashMap<>();

        CtsBusinessCalendar businessCalendar = new CtsBusinessCalendar(businessCalendarProvider.getDefaultProps());

        deadlineBehaviour.setHmpoDeadline("10 business days");

        deadlineBehaviour.doHMPO(afterValues, businessCalendar);

    }

    @Test
    public void testHMPOComplaintsStage1(){
        Date dueDate = testHMPOComplaintsByType(HMPO_STAGE_1);
        assertDate(6, Calendar.OCTOBER,dueDate); //10 days
    }

    @Test
    public void testHMPOComplaintsStage2(){
        Date dueDate = testHMPOComplaintsByType(HMPO_STAGE_2);
        assertDate(6, Calendar.OCTOBER,dueDate); //10 days
    }

    @Test
    public void testHMPOComplaintsDGEN(){
        Date dueDate = testHMPOComplaintsByType(HMPO_DIRECT_GENRAL);
        assertDate(26, Calendar.SEPTEMBER,dueDate); //2 days

    }

    @Test
    public void testHMPOComplaintsGEN(){
        Date dueDate = testHMPOComplaintsByType(HMPO_GNR);
        assertDate(6, Calendar.OCTOBER,dueDate); //10 days
    }

    @Test
    public void testHMPOComplaintsDeferScenario(){

        Map<QName, Serializable> afterValues = mockCaseType(HMPO_STAGE_1);
        afterValues.put(CtsModel.PROP_CASE_RESPONSE_DEADLINE, dateReceived);
        afterValues.put(CtsModel.BRING_UP_DATE, dateReceived);
        Map<QName, Serializable> before = new HashMap<>();
        before.put(CtsModel.PROP_CORRESPONDENCE_TYPE, HMPO_STAGE_1.getCode());

        deadlineBehaviour.onUpdateProperties(mockNode, before, afterValues);

        verify(nodeService, times(1)).setProperty(eq(mockNode), eq(CtsModel.PROP_CASE_RESPONSE_DEADLINE), dateCaptor.capture());
        Date dueDate = dateCaptor.getValue();
        assertDate(2, Calendar.OCTOBER,dueDate); //6 days from bring up date


        //no change in bring up date
        Mockito.reset(nodeService);
        afterValues.put(CtsModel.BRING_UP_DATE, dueDate);
        before.put(CtsModel.BRING_UP_DATE, dueDate);

        deadlineBehaviour.onUpdateProperties(mockNode, before, afterValues);

        verify(nodeService, never()).setProperty(eq(mockNode), eq(CtsModel.PROP_CASE_RESPONSE_DEADLINE), dateCaptor.capture());

        //resetting in bring up date

        Mockito.reset(nodeService);
        afterValues.put(CtsModel.BRING_UP_DATE, new Date("2 Oct 2017  12:00:00"));
        before.put(CtsModel.BRING_UP_DATE, dueDate);

        deadlineBehaviour.onUpdateProperties(mockNode, before, afterValues);

        verify(nodeService, times(1)).setProperty(eq(mockNode), eq(CtsModel.PROP_CASE_RESPONSE_DEADLINE), dateCaptor.capture());
        dueDate = dateCaptor.getValue();
        assertDate(10, Calendar.OCTOBER,dueDate); //6 days from bring up date

        //cancel defer date

        Mockito.reset(nodeService);
        afterValues.put(CtsModel.BRING_UP_DATE, null);
        before.put(CtsModel.BRING_UP_DATE, dueDate);

        deadlineBehaviour.onUpdateProperties(mockNode, before, afterValues);

        verify(nodeService, never()).setProperty(eq(mockNode), eq(CtsModel.PROP_CASE_RESPONSE_DEADLINE), dateCaptor.capture());
    }

    @Test
    public void whenNoDepatureDateForHMPOCol() {

        Map<QName, Serializable> afterValues = new HashMap<>();
        afterValues.put(CtsModel.PROP_CORRESPONDENCE_TYPE, HMPO_COLLECTIVES.getCode());
        Map<QName, Serializable> before = new HashMap<>();

        deadlineBehaviour.onUpdateProperties(mockNode, before, afterValues);

         verify(nodeService, never()).setProperty(eq(mockNode), eq(CtsModel.PROP_CASE_RESPONSE_DEADLINE), dateCaptor.capture());

        final List<Date> allValues = dateCaptor.getAllValues();
        assertEquals(0,allValues.size());
    }

    @Test
    public void whenDepartureDateFormUKInFutureHMPOCol() {

        Map<QName, Serializable> afterValues = new HashMap<>();
        afterValues.put(CtsModel.PROP_CORRESPONDENCE_TYPE, HMPO_COLLECTIVES.getCode());
        afterValues.put(CtsModel.DEPARTURE_DATE_FROM_UK, new Date("10 Oct 2017  12:00:00"));
        Map<QName, Serializable> before = new HashMap<>();
        before.put(CtsModel.PROP_CORRESPONDENCE_TYPE, HMPO_COLLECTIVES.getCode());

        deadlineBehaviour.onUpdateProperties(mockNode, before, afterValues);

        verify(nodeService, times(1)).setProperty(eq(mockNode), eq(CtsModel.PROP_CASE_RESPONSE_DEADLINE), dateCaptor.capture());

        Date dueDate = dateCaptor.getValue();
        assertDate(30, Calendar.SEPTEMBER,dueDate); //departure date - 10 days
    }

    @Test
    public void whenDepartureDateFormUKIsLessThan5DaysHMPOCol() {

        Map<QName, Serializable> afterValues = new HashMap<>();
        afterValues.put(CtsModel.PROP_CORRESPONDENCE_TYPE, HMPO_COLLECTIVES.getCode());
        afterValues.put(CtsModel.DEPARTURE_DATE_FROM_UK, new Date("27 Sep 2017  12:00:00"));
        Map<QName, Serializable> before = new HashMap<>();
        before.put(CtsModel.PROP_CORRESPONDENCE_TYPE, HMPO_COLLECTIVES.getCode());

        deadlineBehaviour.onUpdateProperties(mockNode, before, afterValues);

        verify(nodeService, times(1)).setProperty(eq(mockNode), eq(CtsModel.PROP_CASE_RESPONSE_DEADLINE), dateCaptor.capture());

        Date dueDate = dateCaptor.getValue();
        assertDate(24, Calendar.SEPTEMBER,dueDate); //departure date - 5 days or today
    }

    private Date testHMPOComplaintsByType(CorrespondenceType type) {
        Map<QName, Serializable> afterValues = mockCaseType(type);
        Map<QName, Serializable> before = new HashMap<>();

        deadlineBehaviour.onUpdateProperties(mockNode, before, afterValues);

        verify(nodeService, times(1)).setProperty(eq(mockNode), eq(CtsModel.PROP_CASE_RESPONSE_DEADLINE), dateCaptor.capture());

        return dateCaptor.getValue();
    }

    private Map<QName, Serializable> mockCaseType(CorrespondenceType type) {
        Map<QName, Serializable> afterValues = new HashMap<>();
        afterValues.put(CtsModel.PROP_DATE_RECEIVED, dateReceived);
        afterValues.put(CtsModel.PROP_CORRESPONDENCE_TYPE, type.getCode());
        afterValues.put(CtsModel.PROP_DATE_RECEIVED, dateReceived);
        return afterValues;
    }

    private void assertDate(int day,int month,  Date dueDate) {
        assertEquals(day, dueDate.getDate());
        assertEquals(month, dueDate.getMonth());
        assertEquals(23, dueDate.getHours());
        assertEquals(59, dueDate.getMinutes());
    }
}
