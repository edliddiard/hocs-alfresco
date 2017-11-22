package uk.gov.homeoffice.cts.behaviour;

import org.alfresco.service.namespace.QName;
import org.junit.Before;
import org.junit.Test;
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
import static uk.gov.homeoffice.cts.model.CorrespondenceType.LORDS_WRITTEN;
import static uk.gov.homeoffice.cts.model.CorrespondenceType.NAMED_DAY;
import static uk.gov.homeoffice.cts.model.CorrespondenceType.ORDINARY_WRITTEN;

/**
 * Created by chris on 11/09/2014.
 */
public class StatusBehaviourTest {
    BusinessCalendarProvider businessCalendarProvider;
    @Before
    public void setUp(){
        businessCalendarProvider = new BusinessCalendarProvider();

        PropertyProvider propertyProvider = new CodePropertyProvider();
        businessCalendarProvider.setPropertyProvider(propertyProvider);
        businessCalendarProvider.init();
    }
    @Test
    public void testBasicLordsPQ(){
        StatusBehaviour statusBehaviour = new StatusBehaviour();
        statusBehaviour.setBusinessCalendarProvider(businessCalendarProvider);

        Map<QName, Serializable> map = new HashMap<>();
        Date opDate = new Date("11 Sep 2014");
        map.put(CtsModel.PROP_OP_DATE,opDate);
        map.put(CtsModel.PROP_CORRESPONDENCE_TYPE, LORDS_WRITTEN.getCode());
        map.put(CtsModel.PROP_REVIEWED_BY_SPADS,false);
        map.put(CtsModel.PROP_REVIEWED_BY_PERM_SEC,false);
        Date targetDate = statusBehaviour.workOutDraftDeadlineDate(map);
        assertEquals("Should be 15th", 15, targetDate.getDate());
        assertEquals("Should be September", Calendar.SEPTEMBER, targetDate.getMonth());
        assertEquals("Should be 15:00", 15, targetDate.getHours());
    }

    @Test
    public void testLordsPQSPAD(){
        StatusBehaviour statusBehaviour = new StatusBehaviour();
        statusBehaviour.setBusinessCalendarProvider(businessCalendarProvider);

        Map<QName, Serializable> map = new HashMap<>();
        Date opDate = new Date("11 Sep 2014");
        map.put(CtsModel.PROP_OP_DATE,opDate);
        map.put(CtsModel.PROP_CORRESPONDENCE_TYPE, LORDS_WRITTEN.getCode());
        map.put(CtsModel.PROP_REVIEWED_BY_SPADS,true);
        map.put(CtsModel.PROP_REVIEWED_BY_PERM_SEC,false);

        Date targetDate = statusBehaviour.workOutDraftDeadlineDate(map);
        assertEquals("Should be 12th", 12, targetDate.getDate());
        assertEquals("Should be September", Calendar.SEPTEMBER, targetDate.getMonth());
        assertEquals("Should be 15:00", 15, targetDate.getHours());
    }

    @Test
    public void testLordsPQPermSec(){
        StatusBehaviour statusBehaviour = new StatusBehaviour();
        statusBehaviour.setBusinessCalendarProvider(businessCalendarProvider);

        Map<QName, Serializable> map = new HashMap<>();
        Date opDate = new Date("11 Sep 2014");
        map.put(CtsModel.PROP_OP_DATE,opDate);
        map.put(CtsModel.PROP_CORRESPONDENCE_TYPE, LORDS_WRITTEN.getCode());
        map.put(CtsModel.PROP_REVIEWED_BY_SPADS,false);
        map.put(CtsModel.PROP_REVIEWED_BY_PERM_SEC,true);

        Date targetDate = statusBehaviour.workOutDraftDeadlineDate(map);
        assertEquals("Should be 12th", 12, targetDate.getDate());
        assertEquals("Should be September", Calendar.SEPTEMBER, targetDate.getMonth());
        assertEquals("Should be 15:00", 15, targetDate.getHours());
    }

    @Test
    public void testLordsPQSPADWednesday(){
        StatusBehaviour statusBehaviour = new StatusBehaviour();
        statusBehaviour.setBusinessCalendarProvider(businessCalendarProvider);

        Map<QName, Serializable> map = new HashMap<>();
        Date opDate = new Date("10 Sep 2014");
        map.put(CtsModel.PROP_OP_DATE,opDate);
        map.put(CtsModel.PROP_CORRESPONDENCE_TYPE, LORDS_WRITTEN.getCode());
        map.put(CtsModel.PROP_REVIEWED_BY_SPADS,true);
        map.put(CtsModel.PROP_REVIEWED_BY_PERM_SEC,false);

        Date targetDate = statusBehaviour.workOutDraftDeadlineDate(map);
        assertEquals("Should be 11th", 11, targetDate.getDate());
        assertEquals("Should be 15:00", 12, targetDate.getHours());
    }
    @Test
    public void testLordsPQPermSecWednesday(){
        StatusBehaviour statusBehaviour = new StatusBehaviour();
        statusBehaviour.setBusinessCalendarProvider(businessCalendarProvider);

        Map<QName, Serializable> map = new HashMap<>();
        Date opDate = new Date("10 Sep 2014  12:00:00");
        map.put(CtsModel.PROP_OP_DATE,opDate);
        map.put(CtsModel.PROP_CORRESPONDENCE_TYPE, LORDS_WRITTEN.getCode());
        map.put(CtsModel.PROP_REVIEWED_BY_SPADS,false);
        map.put(CtsModel.PROP_REVIEWED_BY_PERM_SEC,true     );

        Date targetDate = statusBehaviour.workOutDraftDeadlineDate(map);
        assertEquals("Should be 11th", 11, targetDate.getDate());
        assertEquals("Should be 15:00", 12, targetDate.getHours());
    }

    @Test
    public void testBasicOrdinaryPQ(){
        StatusBehaviour statusBehaviour = new StatusBehaviour();
        statusBehaviour.setBusinessCalendarProvider(businessCalendarProvider);

        Map<QName, Serializable> map = new HashMap<>();
        Date opDate = new Date("11 Sep 2014  12:00:00");
        map.put(CtsModel.PROP_OP_DATE,opDate);
        map.put(CtsModel.PROP_CORRESPONDENCE_TYPE, ORDINARY_WRITTEN.getCode());
        map.put(CtsModel.PROP_REVIEWED_BY_SPADS,false);
        map.put(CtsModel.PROP_REVIEWED_BY_PERM_SEC,false);

        Date targetDate = statusBehaviour.workOutDraftDeadlineDate(map);
        assertEquals("Should be 15th", 15, targetDate.getDate());
        assertEquals("Should be September", Calendar.SEPTEMBER, targetDate.getMonth());
        assertEquals("Should be 15:00", 15, targetDate.getHours());
    }

    @Test
    public void testBasicOrdinaryPQAfterHours(){
        StatusBehaviour statusBehaviour = new StatusBehaviour();
        statusBehaviour.setBusinessCalendarProvider(businessCalendarProvider);

        Map<QName, Serializable> map = new HashMap<>();
        Date opDate = new Date("11 Sep 2014  18:00:00");
        map.put(CtsModel.PROP_OP_DATE,opDate);
        map.put(CtsModel.PROP_CORRESPONDENCE_TYPE, ORDINARY_WRITTEN.getCode());
        map.put(CtsModel.PROP_REVIEWED_BY_SPADS,false);
        map.put(CtsModel.PROP_REVIEWED_BY_PERM_SEC,false);

        Date targetDate = statusBehaviour.workOutDraftDeadlineDate(map);
        System.out.println(targetDate);
        assertEquals("Should be 15th", 15, targetDate.getDate());
        assertEquals("Should be September", Calendar.SEPTEMBER, targetDate.getMonth());
        assertEquals("Should be 15:00", 15, targetDate.getHours());
    }


    @Test
    public void testBasicOrdinaryPQBeforeHours(){
        StatusBehaviour statusBehaviour = new StatusBehaviour();
        statusBehaviour.setBusinessCalendarProvider(businessCalendarProvider);

        Map<QName, Serializable> map = new HashMap<>();
        Date opDate = new Date("11 Sep 2014  06:00:00");
        map.put(CtsModel.PROP_OP_DATE,opDate);
        map.put(CtsModel.PROP_CORRESPONDENCE_TYPE, ORDINARY_WRITTEN.getCode());
        map.put(CtsModel.PROP_REVIEWED_BY_SPADS,false);
        map.put(CtsModel.PROP_REVIEWED_BY_PERM_SEC,false);

        Date targetDate = statusBehaviour.workOutDraftDeadlineDate(map);
        System.out.println(targetDate);
        assertEquals("Should be 15th", 15, targetDate.getDate());
        assertEquals("Should be September", Calendar.SEPTEMBER, targetDate.getMonth());
        assertEquals("Should be 15:00", 15, targetDate.getHours());
    }

    @Test
    public void testOrdinaryPQSPAD(){
        StatusBehaviour statusBehaviour = new StatusBehaviour();
        statusBehaviour.setBusinessCalendarProvider(businessCalendarProvider);

        Map<QName, Serializable> map = new HashMap<>();
        Date opDate = new Date("11 Sep 2014  12:00:00");
        map.put(CtsModel.PROP_OP_DATE,opDate);
        map.put(CtsModel.PROP_CORRESPONDENCE_TYPE, ORDINARY_WRITTEN.getCode());
        map.put(CtsModel.PROP_REVIEWED_BY_SPADS,true);
        map.put(CtsModel.PROP_REVIEWED_BY_PERM_SEC,false);

        Date targetDate = statusBehaviour.workOutDraftDeadlineDate(map);
        assertEquals("Should be 12th", 12, targetDate.getDate());
        assertEquals("Should be September", Calendar.SEPTEMBER, targetDate.getMonth());
        assertEquals("Should be 15:00", 15, targetDate.getHours());
    }

    @Test
    public void testOrdinaryPQPermSec(){
        StatusBehaviour statusBehaviour = new StatusBehaviour();
        statusBehaviour.setBusinessCalendarProvider(businessCalendarProvider);

        Map<QName, Serializable> map = new HashMap<>();
        Date opDate = new Date("11 Sep 2014  12:00:00");
        map.put(CtsModel.PROP_OP_DATE,opDate);
        map.put(CtsModel.PROP_CORRESPONDENCE_TYPE, ORDINARY_WRITTEN.getCode());
        map.put(CtsModel.PROP_REVIEWED_BY_SPADS,false);
        map.put(CtsModel.PROP_REVIEWED_BY_PERM_SEC,true);

        Date targetDate = statusBehaviour.workOutDraftDeadlineDate(map);
        assertEquals("Should be 12th", 12, targetDate.getDate());
        assertEquals("Should be September", Calendar.SEPTEMBER, targetDate.getMonth());
        assertEquals("Should be 15:00", 15, targetDate.getHours());
    }

    @Test
    public void testOrdinaryPQSPADWednesday(){
        StatusBehaviour statusBehaviour = new StatusBehaviour();
        statusBehaviour.setBusinessCalendarProvider(businessCalendarProvider);

        Map<QName, Serializable> map = new HashMap<>();
        Date opDate = new Date("10 Sep 2014  12:00:00");
        map.put(CtsModel.PROP_OP_DATE,opDate);
        map.put(CtsModel.PROP_CORRESPONDENCE_TYPE, ORDINARY_WRITTEN.getCode());
        map.put(CtsModel.PROP_REVIEWED_BY_SPADS,true);
        map.put(CtsModel.PROP_REVIEWED_BY_PERM_SEC,false);

        Date targetDate = statusBehaviour.workOutDraftDeadlineDate(map);
        assertEquals("Should be 11th", 11, targetDate.getDate());
        //this has been updated to 15 in accordance with new PQ rules
        assertEquals("Should be 15:00", 15, targetDate.getHours());
    }
    @Test
    public void testOrdinaryPQPermSecWednesday(){
        StatusBehaviour statusBehaviour = new StatusBehaviour();
        statusBehaviour.setBusinessCalendarProvider(businessCalendarProvider);

        Map<QName, Serializable> map = new HashMap<>();
        Date opDate = new Date("10 Sep 2014  12:00:00");
        map.put(CtsModel.PROP_OP_DATE,opDate);
        map.put(CtsModel.PROP_CORRESPONDENCE_TYPE, ORDINARY_WRITTEN.getCode());
        map.put(CtsModel.PROP_REVIEWED_BY_SPADS,false);
        map.put(CtsModel.PROP_REVIEWED_BY_PERM_SEC,true);

        Date targetDate = statusBehaviour.workOutDraftDeadlineDate(map);
        assertEquals("Should be 12th", 11, targetDate.getDate());
        //this has been updated to 15 in accordance with new PQ rules
        assertEquals("Should be 15:00", 15, targetDate.getHours());
    }

    @Test
    public void testNamedDayPQ(){
        StatusBehaviour statusBehaviour = new StatusBehaviour();
        statusBehaviour.setBusinessCalendarProvider(businessCalendarProvider);

        Map<QName, Serializable> map = new HashMap<>();
        Date opDate = new Date("11 Sep 2014  12:00:00");
        map.put(CtsModel.PROP_OP_DATE,opDate);
        map.put(CtsModel.PROP_CORRESPONDENCE_TYPE, NAMED_DAY.getCode());
        map.put(CtsModel.PROP_REVIEWED_BY_SPADS,false);
        map.put(CtsModel.PROP_REVIEWED_BY_PERM_SEC,false);

        Date targetDate = statusBehaviour.workOutDraftDeadlineDate(map);
        assertEquals("Should be 12th", 12, targetDate.getDate());
        assertEquals("Should be October", Calendar.SEPTEMBER, targetDate.getMonth());
        assertEquals("Should be 15:00", 15, targetDate.getHours());
    }
}
