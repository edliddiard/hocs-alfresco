package uk.gov.homeoffice.cts.behaviour;


import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

import java.io.Serializable;
import java.util.Map;

public interface PropertyUpdateBehaviour {

    void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after);
}
