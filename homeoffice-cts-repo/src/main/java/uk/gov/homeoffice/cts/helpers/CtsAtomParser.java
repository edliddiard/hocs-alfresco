package uk.gov.homeoffice.cts.helpers;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class CtsAtomParser {

    public Node getNode(String tagName, NodeList nodes) {
        for ( int x = 0; x < nodes.getLength(); x++ ) {
            Node node = nodes.item(x);
            if (node.getNodeName().equalsIgnoreCase(tagName)) {
                return node;
            }
        }
        return null;
    }

    public Node getNodeByPropertyDefinition(String targetQueryName, NodeList nodes){
        for ( int x = 0; x < nodes.getLength(); x++ ) {
            Node node = nodes.item(x);

            if (node.getAttributes() != null) {
                Node comp = node.getAttributes().getNamedItem("propertyDefinitionId");
                if (comp.getNodeValue().equals(targetQueryName)) {
                    return node;
                }
            }
        }
        return null;
    }

    public String getNodeValue( Node node ) {
        NodeList childNodes = node.getChildNodes();
        for (int x = 0; x < childNodes.getLength(); x++ ) {
            Node data = childNodes.item(x);
            if ( data.getNodeType() == Node.TEXT_NODE )
                return data.getNodeValue();
        }
        return "";
    }

    public String getNodeValue(String tagName, NodeList nodes ) {
        for ( int x = 0; x < nodes.getLength(); x++ ) {
            Node node = nodes.item(x);
            if (node.getNodeName().equalsIgnoreCase(tagName)) {
                NodeList childNodes = node.getChildNodes();
                for (int y = 0; y < childNodes.getLength(); y++ ) {
                    Node data = childNodes.item(y);
                    if ( data.getNodeType() == Node.TEXT_NODE )
                        return data.getNodeValue();
                }
            }
        }
        return "";
    }

}