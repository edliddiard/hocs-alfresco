package uk.gov.homeoffice.cts.helpers;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by chris on 12/09/2014.
 */
public class ContentPropertyProviderTest {
    @Test
    public void testPropertiesFromString(){
        ContentPropertyProvider contentPropertyProvider = new ContentPropertyProvider();
        Properties properties = contentPropertyProvider.getProperties("hour.format=HH:mm\n" +
                "hour.format=HH:mm\n" +
                "another=thing\n" +
                "emptyvalue=\n" +
                "aaaaaa=bbbbbb\n" +
                "cccccccc=ddddddd\n");
        assertEquals(5, properties.size());
        assertEquals("HH:mm",properties.get("hour.format"));
        assertEquals("ddddddd",properties.get("cccccccc"));
        assertEquals("",properties.get("emptyvalue"));
    }

    @Test
    public void testPropertiesFromFile() throws IOException {
        File file = new File("src/test/resources/org/jbpm/calendar/jbpm.business.calendar.properties");
        System.out.println(file.getAbsolutePath());
        InputStream fis = FileUtils.openInputStream(file);
        ContentPropertyProvider contentPropertyProvider = new ContentPropertyProvider();
        String content = contentPropertyProvider.getContent(fis);
        assertTrue("There should be some content", content.length() > 0);

        Properties properties = contentPropertyProvider.getProperties(content);
        assertEquals(33,properties.size());
    }
}
