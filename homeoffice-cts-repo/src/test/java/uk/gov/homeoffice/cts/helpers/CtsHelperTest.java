package uk.gov.homeoffice.cts.helpers;

import org.alfresco.service.namespace.QName;
import org.junit.Before;
import org.junit.Test;
import org.springframework.extensions.webscripts.json.JSONUtils;
import uk.gov.homeoffice.cts.model.CtsModel;
import uk.gov.homeoffice.cts.webscripts.GetCaseWebScript;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.assertEquals;

/**
 * Created by davidt on 15/10/2014.
 */
public class CtsHelperTest {

    private CtsHelper instance;

    @Before
    public void setUp() {
        this.instance = new CtsHelper();
        instance.setJsonUtils(new JSONUtils());
    }

    @Test
    public void testEncodeStringsForJson() {
        Map<QName, Serializable> caseProps = new HashMap<>();
        caseProps.put(CtsModel.PROP_QUESTION_TEXT, "This is the question. It's got some \"quotes\" and some \\slashes/!");
        caseProps.put(CtsModel.PROP_CASE_STATUS, "Status");
        instance.encodeStringsForJson(caseProps);
        assertEquals("\"This is the question. It's got some \\\"quotes\\\" and some \\\\slashes\\/!\"", caseProps.get(CtsModel.PROP_QUESTION_TEXT));
        assertEquals("\"Status\"", caseProps.get(CtsModel.PROP_CASE_STATUS));
    }
}
