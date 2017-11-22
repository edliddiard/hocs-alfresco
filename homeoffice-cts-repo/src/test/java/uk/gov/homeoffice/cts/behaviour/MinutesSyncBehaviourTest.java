package uk.gov.homeoffice.cts.behaviour;


import org.alfresco.repo.policy.Behaviour;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ActionService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.homeoffice.cts.action.MinutesSyncAction;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MinutesSyncBehaviourTest {

    @Mock private ActionService actionService;
    private NodeRef nodeRef;
    @Mock private Action action;
    private MinutesSyncBehaviour toTest;


    @Captor private ArgumentCaptor<Behaviour> captor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        toTest = new MinutesSyncBehaviour();
        toTest.setActionService(actionService);
        nodeRef = new NodeRef("workspace://SpacesStore/MYNODEREF");
    }

    @Test
    public void testSynchroniseMinutes() {
        when(actionService.createAction(MinutesSyncAction.NAME)).thenReturn(action);
        toTest.onUpdateProperties(nodeRef, null, null);
        verify(actionService).createAction(MinutesSyncAction.NAME);
        verify(actionService).executeAction(action, nodeRef, false, true);
    }

}
