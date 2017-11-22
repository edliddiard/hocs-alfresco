package uk.gov.homeoffice.cts.template;

import org.alfresco.service.namespace.QName;
import org.junit.Before;
import org.junit.Test;
import uk.gov.homeoffice.cts.model.CtsModel;
import static org.junit.Assert.assertEquals;

/**
 * Created by davidt on 11/11/2014.
 */
public class MultiPropertyPlaceholderTest {

    private MultiPropertyPlaceholder instance;

    private QName[] properties = {
            CtsModel.PROP_CORRESPONDENT_TITLE,
            CtsModel.PROP_CORRESPONDENT_FORENAME,
            CtsModel.PROP_CORRESPONDENT_SURNAME
    };

    @Before
    public void setUp() {
        this.instance = new MultiPropertyPlaceholder(properties, "\n");
    }

    @Test
    public void testGetProperties() {
        assertEquals(properties, instance.getProperties());
    }

    @Test
    public void testGetSeparator() {
        assertEquals("\n", instance.getSeparator());
    }
}
