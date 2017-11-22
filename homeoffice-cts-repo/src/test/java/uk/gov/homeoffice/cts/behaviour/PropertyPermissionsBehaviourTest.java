package uk.gov.homeoffice.cts.behaviour;


import org.alfresco.repo.security.permissions.AccessDeniedException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.apache.chemistry.opencmis.commons.data.CmisExtensionElement;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.CmisExtensionElementImpl;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import uk.gov.homeoffice.cts.model.CtsModel;
import uk.gov.homeoffice.cts.permissions.PermissionChecker;

import java.io.Serializable;
import java.util.*;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PropertyPermissionsBehaviourTest {


    private PropertyPermissionsBehaviour behaviour;

    private NodeRef nodeRef;

    @Before
    public void setUp() {
        behaviour = new PropertyPermissionsBehaviour();

        //create nodeRef
        nodeRef = new NodeRef("workspace://SpacesStore/myreference");

        //mock permissionChecker
        PermissionChecker permissionChecker = mock(PermissionChecker.class);
        behaviour.setPermissionChecker(permissionChecker);

        when(permissionChecker.getPermissions()).thenReturn(new HashSet<String>());
        when(permissionChecker.isUserRestricted((NodeRef) Mockito.any())).thenReturn(true);
        List<CmisExtensionElement> propertyPermissions= new ArrayList<>();
        CmisExtensionElement propertyPermission = new CmisExtensionElementImpl(null, "opDate", null, "false");
        propertyPermissions.add(propertyPermission);
        when(permissionChecker.getPropertyPermissions(nodeRef)).thenReturn(propertyPermissions);

    }


    @Test(expected = AccessDeniedException.class)
    public void testChangeOpDateToDifferentDate() {
        //create before and after opDate
        Calendar calendar = new GregorianCalendar(2016, 5, 15, 1, 23, 45);
        Map<QName, Serializable> beforeNodeProd = createPropMap(calendar.getTime());
        calendar.add(Calendar.DAY_OF_MONTH, 2);
        Map<QName, Serializable> afterNodeProd = createPropMap(calendar.getTime());
        behaviour.onUpdateProperties(nodeRef, beforeNodeProd, afterNodeProd);
    }

    @Test
    public void testChangeOpDateToDifferentTimeStamp() {
        //create before and after opDate
        Calendar calendar = new GregorianCalendar(2016, 5, 15, 1, 23, 45);
        Map<QName, Serializable> beforeNodeProd = createPropMap(calendar.getTime());
        calendar.add(Calendar.HOUR_OF_DAY, 2);
        Map<QName, Serializable> afterNodeProd = createPropMap(calendar.getTime());

        try {
            behaviour.onUpdateProperties(nodeRef, beforeNodeProd, afterNodeProd);
        } catch (AccessDeniedException e) {
            Assert.fail("It should ignore timestamp before compare opDate");
        }
    }

    private Map<QName, Serializable> createPropMap(Date opDate) {
        Map<QName, Serializable> datesMap = new HashMap<>();
        datesMap.put(CtsModel.PROP_ASSIGNED_USER, "Test User");
        datesMap.put(CtsModel.PROP_OP_DATE, opDate);
        return datesMap;
    }

    @After
    public void tearDown() {
        behaviour = null;
        nodeRef= null;
    }

}
