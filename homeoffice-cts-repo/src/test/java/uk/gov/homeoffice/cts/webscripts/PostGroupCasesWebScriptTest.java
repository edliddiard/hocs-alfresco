package uk.gov.homeoffice.cts.webscripts;

import org.junit.Before;
import org.junit.Test;
import java.util.Date;
import static org.junit.Assert.assertEquals;

/**
 * Created by davidt on 19/09/2014.
 */
public class PostGroupCasesWebScriptTest {

    private PostGroupCasesWebScript instance;

    @Before
    public void setUp() {
        this.instance = new PostGroupCasesWebScript();
    }

    @Test
    public void testValidateSlaveAlreadyGrouped() {
        String error = instance.validateSlave("1234", Boolean.TRUE.toString(), null, null);
        assertEquals("1234: already grouped", error);
    }

    @Test
    public void testValidateSlaveMissingQuestionAndMember() {
        String error = instance.validateSlave("1234", Boolean.FALSE.toString(), null, null);
        assertEquals("1234: missing question and member", error);
    }

    @Test
    public void testValidateSlaveMissingQuestion() {
        String error = instance.validateSlave("1234", Boolean.FALSE.toString(), null, "Member");
        assertEquals("1234: missing question", error);
    }

    @Test
    public void testValidateSlaveMissingMember() {
        String error = instance.validateSlave("1234", Boolean.FALSE.toString(), "Question", null);
        assertEquals("1234: missing member", error);
    }

    @Test
    public void testGetMinDate() {
        Date date1 = new Date(2014, 9, 21);
        Date date2 = new Date(2014, 9, 20);
        assertEquals(date2, this.instance.getMinDate(date1, date2));
    }

}
