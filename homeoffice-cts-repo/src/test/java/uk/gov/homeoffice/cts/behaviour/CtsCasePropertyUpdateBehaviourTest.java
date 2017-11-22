package uk.gov.homeoffice.cts.behaviour;


import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.homeoffice.cts.model.CtsModel;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CtsCasePropertyUpdateBehaviourTest {

    private CtsCasePropertyUpdateBehaviour toTest;

    @Mock private PropertyUpdateBehaviour allocateBehaviour;

    @Mock private PropertyUpdateBehaviour assignedUnitBehaviour;

    @Mock private PropertyUpdateBehaviour complexBehaviour;

    @Mock private PropertyUpdateBehaviour deadlineBehaviour;

    @Mock private PropertyUpdateBehaviour markupDecisionBehaviour;

    @Mock private PropertyUpdateBehaviour minutesSyncBehaviour;

    @Mock private PropertyUpdateBehaviour originalDrafterBehaviour;

    @Mock private PropertyUpdateBehaviour pitLetterSentDateBehaviour;

    @Mock private PropertyUpdateBehaviour propertyPermissionsBehaviour;

    @Mock private PropertyUpdateBehaviour statusBehaviour;

    @Mock private PropertyUpdateBehaviour taskBehaviour;

    @Mock private PolicyComponent policyComponent;

    @Captor private ArgumentCaptor<Behaviour> captor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        toTest = new CtsCasePropertyUpdateBehaviour();
        toTest.setAllocateBehaviour(allocateBehaviour);
        toTest.setAssignedUnitBehaviour(assignedUnitBehaviour);
        toTest.setComplexBehaviour(complexBehaviour);
        toTest.setDeadlineBehaviour(deadlineBehaviour);
        toTest.setMarkupDecisionBehaviour(markupDecisionBehaviour);
        toTest.setMinutesSyncBehaviour(minutesSyncBehaviour);
        toTest.setOriginalDrafterBehaviour(originalDrafterBehaviour);
        toTest.setPitLetterSentDateBehaviour(pitLetterSentDateBehaviour);
        toTest.setPropertyPermissionsBehaviour(propertyPermissionsBehaviour);
        toTest.setStatusBehaviour(statusBehaviour);
        toTest.setTaskBehaviour(taskBehaviour);
        toTest.setPolicyComponent(policyComponent);
    }

    @Test
    public void whenRegisterBehaviour() {
        toTest.init();
        verify(policyComponent).bindClassBehaviour(eq(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME),
                eq(CtsModel.TYPE_CTS_CASE),
                captor.capture());
        Behaviour onUpdateProperties = captor.getValue();
        assertTrue(onUpdateProperties instanceof JavaBehaviour);
    }


    @Test
    public void whenHappyPath() {
        NodeRef nodeRef = new NodeRef("workspace://SpacesStore/132b4404-6e4b-4364-ba84-407c0f84338a");
        Map<QName, Serializable> before = new HashMap<>();
        Map<QName, Serializable> after = new HashMap<>();

        toTest.onUpdateProperties(nodeRef, before, after);

        verifyBehavioursInvocation(nodeRef, before, after);

    }

    private void verifyBehavioursInvocation(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
        verify(allocateBehaviour).onUpdateProperties(nodeRef, before, after);
        verify(assignedUnitBehaviour).onUpdateProperties(nodeRef, before, after);
        verify(complexBehaviour).onUpdateProperties(nodeRef, before, after);
        verify(deadlineBehaviour).onUpdateProperties(nodeRef, before, after);
        verify(markupDecisionBehaviour).onUpdateProperties(nodeRef, before, after);
        verify(minutesSyncBehaviour).onUpdateProperties(nodeRef, before, after);
        verify(originalDrafterBehaviour).onUpdateProperties(nodeRef, before, after);
        verify(pitLetterSentDateBehaviour).onUpdateProperties(nodeRef, before, after);
        verify(propertyPermissionsBehaviour).onUpdateProperties(nodeRef, before, after);
        verify(statusBehaviour).onUpdateProperties(nodeRef, before, after);
        verify(taskBehaviour).onUpdateProperties(nodeRef, before, after);
    }
}
