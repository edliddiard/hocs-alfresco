package uk.gov.homeoffice.cts.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(MockitoJUnitRunner.class)
public class CorrespondenceTypeTest {

    @Test
    public void whenValidCorrespondingTypeCode(){
        assertEquals(CorrespondenceType.DCU_MINISTERIAL, CorrespondenceType.getByCode("MIN"));
        assertEquals(CorrespondenceType.DCU_TREAT_OFFICIAL, CorrespondenceType.getByCode("TRO"));
        assertEquals(CorrespondenceType.DCU_NUMBER_10, CorrespondenceType.getByCode("DTEN"));
    }

    @Test
    public void whenInvalidCorrespondingTypeCode(){
        assertNull(CorrespondenceType.getByCode("Test"));
    }
}
