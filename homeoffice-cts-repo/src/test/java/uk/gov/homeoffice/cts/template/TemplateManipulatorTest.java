package uk.gov.homeoffice.cts.template;

import org.junit.Before;
import org.junit.Test;
import org.odftoolkit.simple.TextDocument;
import org.odftoolkit.simple.common.navigation.TextNavigation;
import java.io.File;
import static org.junit.Assert.assertEquals;

/**
 * Created by davidt on 11/11/2014.
 */
public class TemplateManipulatorTest {

    private TemplateManipulator instance;

    @Before
    public void setUp() {
        this.instance = new TemplateManipulator();
    }

    @Test
    public void testFindAndReplace() throws Exception {
        File template = new File("src/test/resources/template.odt");
        TextDocument document = TextDocument.loadDocument(template);
        instance.findAndReplace(document, "<<REPLACEME1>>", "REPLACED1");
        instance.findAndReplace(document, "<<REPLACEME2>>", "REPLACED2");
        assertEquals(count(document, "Start content"), 1);
        assertEquals(count(document, "End content"), 1);
        assertEquals(count(document, "REPLACED1"), 1);
        assertEquals(count(document, "REPLACED2"), 2);
    }

    private int count(TextDocument document, String findText) {
        int count = 0;
        TextNavigation search = new TextNavigation(findText, document);
        while (search.hasNext()) {
            search.nextSelection();
            count++;
        }
        return count;
    }
}
