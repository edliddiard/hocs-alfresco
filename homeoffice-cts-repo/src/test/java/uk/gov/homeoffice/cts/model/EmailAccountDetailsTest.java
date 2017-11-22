package uk.gov.homeoffice.cts.model;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class EmailAccountDetailsTest {

    private static EmailAccountDetails accountDetails;

    @Before
    public void setUp(){
        accountDetails = new EmailAccountDetails();
        accountDetails.setHostName("imap.host.com");
        accountDetails.setUserName("test@test.com");
        accountDetails.setPassword("password");
        accountDetails.setSrcFolderName("HCOS");
        accountDetails.setDestFolderName("DCU");
        accountDetails.setErrorFolderName("ERROR");
        accountDetails.setCaseQueueName("MIN");
    }

    @Test
    public void whenValidAccountDetails(){
        assertTrue(accountDetails.validate());
    }

    @Test
    public void whenHostNameNull(){
        accountDetails.setHostName(null);
        assertFalse(accountDetails.validate());
    }

    @Test
    public void whenUserNameNull(){
        accountDetails.setUserName(null);
        assertFalse(accountDetails.validate());
    }

    @Test
    public void whenPasswordNull(){
        accountDetails.setPassword(null);
        assertFalse(accountDetails.validate());
    }

    @Test
    public void whenSrcFolderNameNull(){
        accountDetails.setSrcFolderName(null);
        assertFalse(accountDetails.validate());
    }

    @Test
    public void whenDestFolderNameNull(){
        accountDetails.setDestFolderName(null);
        assertFalse(accountDetails.validate());
    }

    @Test
    public void whenCaseQueueNameNull(){
        accountDetails.setCaseQueueName(null);
        assertFalse(accountDetails.validate());
    }

    @Test
    public void whenCaseQueueNameInvalid(){
        assertTrue(accountDetails.validate());
        accountDetails.setCaseQueueName("Test");
        assertFalse(accountDetails.validate());
    }

    @Test
    @Ignore
    public void whenInValidEmail(){
        accountDetails.setUserName("test");
        assertFalse(accountDetails.validate());

        accountDetails.setUserName("test.com");
        assertFalse(accountDetails.validate());

        accountDetails.setUserName("test@");
        assertFalse(accountDetails.validate());

        accountDetails.setUserName("test@test");
        assertFalse(accountDetails.validate());


        accountDetails.setUserName("@test.com");
        assertFalse(accountDetails.validate());


        accountDetails.setUserName("test@test.com");
        assertTrue(accountDetails.validate());
    }

}
