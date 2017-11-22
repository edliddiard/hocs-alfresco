package uk.gov.homeoffice.ctsv2.webscripts;


import org.junit.Before;
import org.junit.Test;
import uk.gov.homeoffice.ctsv2.webscripts.CaseMapperHelper;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class CaseMapperHelperTest {

    private CaseMapperHelper toTest;

    @Before
    public void setUp(){
        toTest = new CaseMapperHelper();
        //populate caseStatusMapper and caseTaskMapper which spring inject at run time.
        toTest.setCaseStatusMapper(injectCaseStatusMapper());
        toTest.setCaseTaskMapper(injectCaseTaskMapper());
        toTest.setCaseProgressMapper(injectCaseProgressMapper());
    }


    @Test
    public void testMappingForCaseStatus(){
        assertNotNull(toTest);
        assertEquals("Approve", toTest.getCaseDisplayStatus("Approvals"));
        assertEquals("Dispatch", toTest.getCaseDisplayStatus("Dispatch"));
        assertEquals("Draft", toTest.getCaseDisplayStatus("Draft"));
        assertEquals("On Hold", toTest.getCaseDisplayStatus("Hold"));
        assertEquals("NFA", toTest.getCaseDisplayStatus("NFA"));
        assertEquals("Create", toTest.getCaseDisplayStatus("New"));
        assertEquals("OGD", toTest.getCaseDisplayStatus("OGD"));
        assertEquals("Sign Off", toTest.getCaseDisplayStatus("Obtain sign-off"));
        assertEquals("Completed", toTest.getCaseDisplayStatus("Completed"));

    }

    @Test
    public void testMappingForCaseProgress(){
        assertNotNull(toTest);
        assertTrue(1 == toTest.getCaseProgressStatus("New"));
        assertTrue(2 == toTest.getCaseProgressStatus("Draft"));
        assertTrue(2 == toTest.getCaseProgressStatus("NFA"));
        assertTrue(2 == toTest.getCaseProgressStatus("OGD"));
        assertTrue(3 == toTest.getCaseProgressStatus("Approvals"));
        assertTrue(4 == toTest.getCaseProgressStatus("Hold"));
        assertTrue(4 == toTest.getCaseProgressStatus("Obtain sign-off"));
        assertTrue(5 == toTest.getCaseProgressStatus("Dispatch"));
        assertTrue(5 == toTest.getCaseProgressStatus("Completed"));
    }

    @Test
    public void testMappingForAllCaseTask(){
        assertNotNull(toTest);
        assertEquals("Amend Case", toTest.getCaseDisplayTask("Amend case"));
        assertEquals("Amend Response", toTest.getCaseDisplayTask("Amend response"));
        assertEquals("Answered", toTest.getCaseDisplayTask("Answered"));
        assertEquals("Buff Print", toTest.getCaseDisplayTask("Buff print run"));
        assertEquals("Check and Buff Print", toTest.getCaseDisplayTask("Check and buff print"));
        assertEquals("Create Case", toTest.getCaseDisplayTask("Create case"));
        assertEquals("Dispatch", toTest.getCaseDisplayTask("Dispatch response"));
        assertEquals("Draft", toTest.getCaseDisplayTask("Draft response"));
        assertEquals("FOI Minister", toTest.getCaseDisplayTask("FOI Minister's sign-off"));
        assertEquals("HS Private Office", toTest.getCaseDisplayTask("HS Private Office approval"));
        assertEquals("Home Sec", toTest.getCaseDisplayTask("Home Sec's sign-off"));
        assertEquals("ICO/Tribunal", toTest.getCaseDisplayTask("ICO or tribunal outcome"));
        assertEquals("Lords Minister", toTest.getCaseDisplayTask("Lords Minister's sign-off"));
        assertEquals("Parliamentary Under Sec", toTest.getCaseDisplayTask("Parliamentary Under Secretary sign-off"));
        assertEquals("Parly", toTest.getCaseDisplayTask("Parly approval"));
        assertEquals("Perm Sec", toTest.getCaseDisplayTask("Perm Sec approval"));
        assertEquals("Press Office", toTest.getCaseDisplayTask("Press Office review"));
        assertEquals("Print", toTest.getCaseDisplayTask("Print run"));
        assertEquals("Private Office", toTest.getCaseDisplayTask("Private Office approval"));
        assertEquals("QA Case", toTest.getCaseDisplayTask("QA case"));
        assertEquals("QA Review", toTest.getCaseDisplayTask("QA review"));
        assertEquals("SCS", toTest.getCaseDisplayTask("SCS approval"));
        assertEquals("SpAds", toTest.getCaseDisplayTask("SpAds approval"));
        assertEquals("Transfer", toTest.getCaseDisplayTask("Transfer"));
        assertEquals("Minister", toTest.getCaseDisplayTask("Minister's sign-off"));
        assertEquals("Official", toTest.getCaseDisplayTask("Official approval"));
        assertEquals("CQT", toTest.getCaseDisplayTask("CQT approval"));
        assertEquals("QA Review", toTest.getCaseDisplayTask("QA"));
        assertEquals("Head of Unit", toTest.getCaseDisplayTask("Head of unit approval"));

    }

    private static Map<String,String> injectCaseStatusMapper(){
        final Map<String,String> caseStatusMapper = new HashMap<>();
        caseStatusMapper.put("Approvals","Approve");
        caseStatusMapper.put("Dispatch","Dispatch");
        caseStatusMapper.put("Draft","Draft");
        caseStatusMapper.put("Hold","On Hold");
        caseStatusMapper.put("NFA","NFA");
        caseStatusMapper.put("New","Create");
        caseStatusMapper.put("OGD","OGD");
        caseStatusMapper.put("Obtain sign-off","Sign Off");
        caseStatusMapper.put("Completed","Completed");
        return caseStatusMapper;
    }

    private static Map<String,Integer> injectCaseProgressMapper(){
        final Map<String,Integer> caseProgressMapper = new HashMap<>();
        caseProgressMapper.put("New",1);
        caseProgressMapper.put("Draft",2);
        caseProgressMapper.put("OGD",2);
        caseProgressMapper.put("NFA",2);
        caseProgressMapper.put("Approvals",3);
        caseProgressMapper.put("Hold",4);
        caseProgressMapper.put("Obtain sign-off",4);
        caseProgressMapper.put("Dispatch",5);
        caseProgressMapper.put("Completed",5);
        return caseProgressMapper;
    }

    private static Map<String,String> injectCaseTaskMapper(){
        final Map<String,String> caseTaskMapper = new HashMap<>();
        caseTaskMapper.put("Amend case","Amend Case");
        caseTaskMapper.put("Amend response","Amend Response");
        caseTaskMapper.put("Answered","Answered");
        caseTaskMapper.put("Buff print run","Buff Print");
        caseTaskMapper.put("Check and buff print","Check and Buff Print");
        caseTaskMapper.put("Create case","Create Case");
        caseTaskMapper.put("Dispatch response","Dispatch");
        caseTaskMapper.put("Draft response","Draft");
        caseTaskMapper.put("FOI Minister's sign-off","FOI Minister");
        caseTaskMapper.put("HS Private Office approval","HS Private Office");
        caseTaskMapper.put("Home Sec's sign-off","Home Sec");
        caseTaskMapper.put("ICO or tribunal outcome","ICO/Tribunal");
        caseTaskMapper.put("Lords Minister's sign-off","Lords Minister");
        caseTaskMapper.put("Parliamentary Under Secretary sign-off","Parliamentary Under Sec");
        caseTaskMapper.put("Parly approval","Parly");
        caseTaskMapper.put("Perm Sec approval","Perm Sec");
        caseTaskMapper.put("Press Office review","Press Office");
        caseTaskMapper.put("Print run","Print");
        caseTaskMapper.put("Private Office approval","Private Office");
        caseTaskMapper.put("QA case","QA Case");
        caseTaskMapper.put("QA review","QA Review");
        caseTaskMapper.put("SCS approval","SCS");
        caseTaskMapper.put("SpAds approval","SpAds");
        caseTaskMapper.put("Transfer","Transfer");
        caseTaskMapper.put("Minister's sign-off","Minister");
        caseTaskMapper.put("Official approval","Official");
        caseTaskMapper.put("CQT approval","CQT");
        caseTaskMapper.put("QA","QA Review");
        caseTaskMapper.put("Head of unit approval","Head of Unit");
        return caseTaskMapper;
    }


}


