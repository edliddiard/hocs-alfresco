package uk.gov.homeoffice.cts.behaviour;

import org.junit.Before;
import org.junit.Test;

import uk.gov.homeoffice.cts.model.CaseStatus;
import uk.gov.homeoffice.cts.model.CorrespondenceType;

import java.util.List;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;

/**
 * Created by jonathan on 13/03/2014.
 */
public class AssignedUnitBehaviourTest {

    @Before
    public void setUp(){

    }
    @Test
    public void testPQAssignedUnitChangeInDraftStatus(){
        AssignedUnitBehaviour assignedUnitBehaviour = new AssignedUnitBehaviour();

        String beforeAssignedUnit = (String) "BEFORE A";
        String afterAssignedUnit = (String) "AFTER A";
        String caseStatus = (String) CaseStatus.DRAFT.getStatus();
        List pqCases = CorrespondenceType.getUnitCaseTypes("PQ");

        for(Iterator<String> i = pqCases.iterator(); i.hasNext(); ) {
            String pqCase = i.next();
            Boolean markupUnitUpdateRequired = assignedUnitBehaviour.isMarkupUnitUpdateRequired(caseStatus, beforeAssignedUnit, afterAssignedUnit, pqCase);
            assertEquals("Should be true for " + pqCase, true, markupUnitUpdateRequired);
        }

    }

    @Test
    public void testPQAssignedUnitChangeInOtherStatus(){
        AssignedUnitBehaviour assignedUnitBehaviour = new AssignedUnitBehaviour();

        String beforeAssignedUnit = (String) "BEFORE A";
        String afterAssignedUnit = (String) "AFTER A";
        String caseType = (String) CorrespondenceType.getUnitCaseTypes("PQ").get(0);

        String caseStatus = (String) CaseStatus.NEW.getStatus();

        Boolean markupUnitUpdateRequired = assignedUnitBehaviour.isMarkupUnitUpdateRequired(caseStatus, beforeAssignedUnit, afterAssignedUnit, caseType);
        assertEquals("Should be false", false, markupUnitUpdateRequired);
    }

    @Test
    public void testPQAssignedUnitUnchangedInDraftStatus(){
        AssignedUnitBehaviour assignedUnitBehaviour = new AssignedUnitBehaviour();

        String beforeAssignedUnit = (String) "NOCHANGE";
        String afterAssignedUnit = beforeAssignedUnit;

        String caseStatus = (String) CaseStatus.DRAFT.getStatus();
        String caseType = (String) CorrespondenceType.getUnitCaseTypes("PQ").get(0);

        Boolean markupUnitUpdateRequired = assignedUnitBehaviour.isMarkupUnitUpdateRequired(caseStatus, beforeAssignedUnit, afterAssignedUnit, caseType);
        assertEquals("Should be false", false, markupUnitUpdateRequired);
    }

    @Test
    public void testFOIAssignedUnitChangeInDraftStatus(){
        AssignedUnitBehaviour assignedUnitBehaviour = new AssignedUnitBehaviour();

        String beforeAssignedUnit = (String) "BEFORE A";
        String afterAssignedUnit = (String) "AFTER A";
        String caseStatus = (String) CaseStatus.DRAFT.getStatus();

        String caseType = (String) CorrespondenceType.getUnitCaseTypes("FOI").get(0);

        Boolean markupUnitUpdateRequired = assignedUnitBehaviour.isMarkupUnitUpdateRequired(caseStatus, beforeAssignedUnit, afterAssignedUnit, caseType);
        assertEquals("Should be false", false, markupUnitUpdateRequired);
    }


}
