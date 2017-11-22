package uk.gov.homeoffice.cts.behaviour;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by chris on 24/09/2014.
 */
public class BehaviourHelperTest {
    @Test
    public void testSerializable(){
        long t = System.currentTimeMillis();
        Date date1 = new Date(t);
        Date date2 = new Date(t+1000);
        assertFalse(BehaviourHelper.hasChangedSerializable(date1, date1));
        assertTrue(BehaviourHelper.hasChangedSerializable(date1, date2));

        assertFalse(BehaviourHelper.hasChangedSerializable(null, null));

        assertTrue(BehaviourHelper.hasChangedSerializable(date1, null));
    }

    @Test
    public void testBoolean(){
        boolean b1 = true;
        assertTrue(BehaviourHelper.hasChangedBoolean(true, false));

        assertFalse(BehaviourHelper.hasChangedSerializable(null, null));

        assertTrue(BehaviourHelper.hasChangedSerializable(b1, null));

        assertTrue(BehaviourHelper.hasChangedSerializable(null, b1));
    }

    @Test
    public void testDate(){
        long t = System.currentTimeMillis();
        Date date1 = new Date(t);
        Date date2 = new Date(t+1000);

        assertFalse(BehaviourHelper.hasChangedDate(new Date(), new Date()));
        assertFalse(BehaviourHelper.hasChangedDate(null, null));
        assertTrue(BehaviourHelper.hasChangedDate(date1, null));
        assertTrue(BehaviourHelper.hasChangedDate(null, date1));
        assertTrue(BehaviourHelper.hasChangedDate(date1, date2));
        assertFalse(BehaviourHelper.hasChangedDate(date1, date1));
    }
}
