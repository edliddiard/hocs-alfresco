//package uk.gov.homeoffice.cts.behaviour;
//
//import org.alfresco.model.ContentModel;
//import org.alfresco.service.cmr.repository.NodeRef;
//import org.junit.Before;
//import org.junit.Test;
//import uk.gov.homeoffice.cts.model.CaseStatus;
//import uk.gov.homeoffice.cts.model.CtsModel;
//import uk.gov.homeoffice.cts.model.TaskStatus;
//import java.util.HashMap;
//import java.util.Map;
//
//import static org.mockito.Mockito.*;
//
///**
// * Created by chris on 09/04/2015.
// */
//public class AllocateBehaviourTest {
//    NodeRef nodeRef;
//    Map before;
//    @Before
//    public void setUp(){
//        nodeRef = new NodeRef("workspace://SpacesStore/myreference");
//        before = new HashMap();
//        before.put(CtsModel.PROP_ASSIGNED_USER,"userA");
//        before.put(CtsModel.PROP_ASSIGNED_TEAM,"teamA");
//        before.put(CtsModel.PROP_ASSIGNED_UNIT,"unitA");
//    }

//    @Test
//    public void testUserEmail(){
//        Map after = new HashMap();
//        after.put(CtsModel.PROP_ASSIGNED_USER,"userB");
//        after.put(CtsModel.PROP_ASSIGNED_TEAM,"teamB");
//        after.put(CtsModel.PROP_ASSIGNED_UNIT,"unitB");
//
//        AllocateBehaviour allocateBehaviour = mock(AllocateBehaviour.class);
//
//        doCallRealMethod().when(allocateBehaviour).checkEmail(nodeRef,before,after);
//        allocateBehaviour.checkEmail(nodeRef, before, after);
//
//        //check it goes to the user
//        verify(allocateBehaviour).sendEmailToUser(nodeRef,after);
//    }
//
//    @Test
//    public void testTeamEmail(){
//        Map after = new HashMap();
//        after.put(CtsModel.PROP_ASSIGNED_USER,null);
//        after.put(CtsModel.PROP_ASSIGNED_TEAM,"teamB");
//        after.put(CtsModel.PROP_ASSIGNED_UNIT,"unitB");
//        AllocateBehaviour allocateBehaviour = mock(AllocateBehaviour.class);
//
//        doCallRealMethod().when(allocateBehaviour).checkEmail(nodeRef,before,after);
//        allocateBehaviour.checkEmail(nodeRef, before, after);
//
//        //check it goes to the group
//        verify(allocateBehaviour).sendEmailToGroup("teamB",nodeRef,after);
//    }
//
//    @Test
//    public void testUnitEmail(){
//        Map after = new HashMap();
//        after.put(CtsModel.PROP_ASSIGNED_USER,null);
//        after.put(CtsModel.PROP_ASSIGNED_TEAM,null);
//        after.put(CtsModel.PROP_ASSIGNED_UNIT,"unitB");
//        AllocateBehaviour allocateBehaviour = mock(AllocateBehaviour.class);
//
//        doCallRealMethod().when(allocateBehaviour).checkEmail(nodeRef, before, after);
//        allocateBehaviour.checkEmail(nodeRef, before, after);
//
//        //check it goes to the group
//        verify(allocateBehaviour).sendEmailToGroup("unitB",nodeRef,after);
//    }
//
//
//    @Test
//    public void testNoChange(){
//        Map after = new HashMap();
//        after.put(CtsModel.PROP_ASSIGNED_USER,"userA");
//        after.put(CtsModel.PROP_ASSIGNED_TEAM,"teamA");
//        after.put(CtsModel.PROP_ASSIGNED_UNIT,"unitA");
//        AllocateBehaviour allocateBehaviour = spy(new AllocateBehaviour());
//        allocateBehaviour.checkEmail(nodeRef, before, after);
//
//        //check it doesn't send email
//        verify(allocateBehaviour,never()).sendEmailToUser(nodeRef,after);
//        verify(allocateBehaviour,never()).sendEmailToGroup("unitB",nodeRef,after);
//        verify(allocateBehaviour,never()).sendEmailToGroup("teamB",nodeRef,after);
//    }
//
//
//
//    /**
//     * Test that when a user creates a case they don't get an email
//     */
//    @Test
//    public void testSendUserEmailCreatedCase(){
//        Map after = new HashMap();
//        after.put(CtsModel.PROP_ASSIGNED_USER,"userB");
//        after.put(CtsModel.PROP_ASSIGNED_TEAM,"teamA");
//        after.put(CtsModel.PROP_ASSIGNED_UNIT,"unitA");
//
//        after.put(CtsModel.PROP_CASE_STATUS, CaseStatus.NEW.getStatus());
//        after.put(CtsModel.PROP_CASE_TASK, TaskStatus.CREATE_CASE.getStatus());
//        after.put(ContentModel.PROP_CREATOR,"userB");
//
//        AllocateBehaviour allocateBehaviour = spy(new AllocateBehaviour());
//        allocateBehaviour.sendEmailToUser(nodeRef, after);
//
//        //check it doesn't send email
////        verify(allocateBehaviour,never()).sendEmail("userB", nodeRef, "user", null, after);
//    }
//
//}
