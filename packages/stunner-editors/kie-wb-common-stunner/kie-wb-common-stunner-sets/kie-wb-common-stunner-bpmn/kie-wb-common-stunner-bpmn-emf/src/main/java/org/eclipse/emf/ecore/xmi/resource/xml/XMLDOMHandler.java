/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.eclipse.emf.ecore.xmi.resource.xml;

import java.util.HashMap;

import com.google.gwt.xml.client.Attr;
import com.google.gwt.xml.client.Node;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.ExtendedMetaData;

import static org.eclipse.emf.ecore.xmi.resource.xml.XMLLoad.getLocalName;

public class XMLDOMHandler {

    /**
     * store node to actual value mapping
     */
    protected final HashMap<Node, Object> nodeToObject = new HashMap<Node, Object>();

    /**
     * store node to containment feature mapping
     */
    protected final HashMap<Node, EStructuralFeature> nodeToFeature = new HashMap<Node, EStructuralFeature>();

    /**
     * store node to container. used only to record some text/cdata nodes
     */
    protected final HashMap<Node, EObject> nodeToContainer = new HashMap<Node, EObject>();

    protected ExtendedMetaData extendedMetaData;

    void setExtendedMetaData(ExtendedMetaData extendedMetaData) {
        this.extendedMetaData = extendedMetaData;
    }

    public EObject getContainer(Node node) {
        short type = node.getNodeType();
        switch (type) {
            case Node.ELEMENT_NODE: {
                Object o = nodeToObject.get(node);
                if (o != null && o instanceof EObject) {
                    return ((EObject) o).eContainer();
                }
                return (EObject) nodeToObject.get(node.getParentNode());
            }
            case Node.TEXT_NODE:
            case Node.CDATA_SECTION_NODE: {
                Object o = nodeToContainer.get(node);
                if (o != null) {
                    return (EObject) o;
                }
                return (EObject) nodeToObject.get(node.getParentNode().getParentNode());
            }
            case Node.ATTRIBUTE_NODE:
                return (EObject) nodeToObject.get(((Attr) node).getParentNode()); //fixme getOwnerElement()
            default:
                return null;
        }
    }

    public Object getValue(Node node) {
        Object value = nodeToObject.get(node);
        if (value == null) {
            if (node.getNodeType() == Node.TEXT_NODE) {
                value = nodeToObject.get(node.getParentNode());
            }
        }
        return value;
    }

    public EStructuralFeature getEStructuralFeature(Node node) {
        short type = node.getNodeType();
        switch (type) {
            case Node.ELEMENT_NODE:
                return nodeToFeature.get(node);
            case Node.ATTRIBUTE_NODE: {
                EObject obj = (EObject) nodeToObject.get(((Attr) node).getParentNode()); //fixme getOwnerElement()
                if (extendedMetaData == null) {
                    return obj.eClass().getEStructuralFeature(getLocalName(node));
                } else if (obj != null) {
                    return extendedMetaData.getAttribute(obj.eClass(), node.getNamespaceURI(), getLocalName(node));
                }
            }
            case Node.TEXT_NODE:
            case Node.CDATA_SECTION_NODE: {
                EStructuralFeature feature = nodeToFeature.get(node);
                if (feature == null) {
                    feature = nodeToFeature.get(node.getParentNode());
                }
                return feature;
            }
            default:
                return null;
        }
    }

    public void recordValues(Node node, EObject container, EStructuralFeature feature, Object value) {
        debug(node, container, feature, value);

        short type = node.getNodeType();
        switch (type) {
            case Node.ELEMENT_NODE: {
                nodeToFeature.put(node, feature);
                // fall through
            }
            case Node.ATTRIBUTE_NODE: {
                if (value != null) {
                    nodeToObject.put(node, value);
                }
                break;
            }
            case Node.TEXT_NODE: {
                if (nodeToObject.get(node.getParentNode()) == value) {
                    break;
                }
                //fall through...
            }
            case Node.CDATA_SECTION_NODE: {
                nodeToFeature.put(node, feature);
                nodeToContainer.put(node, container);
                nodeToObject.put(node, value);
            }
        }
    }

    final static boolean DEBUG = false;

    private static final void debug(Node node, EObject container, EStructuralFeature feature, Object value) {
        if (DEBUG) {
            StringBuffer buf = new StringBuffer();

            buf.append("recordValues( ");
            buf.append(" {");
            switch (node.getNodeType()) {
                case Node.ELEMENT_NODE:
                    buf.append("ELEMENT_NODE ");
                    break;
                case Node.ATTRIBUTE_NODE:
                    buf.append("ATTRIBUTE_NODE ");
                    break;
                case Node.TEXT_NODE:
                    buf.append("TEXT_NODE ");
                    break;
                case Node.CDATA_SECTION_NODE:
                    buf.append("CDATA_SECTION_NODE ");
                    break;
                default:
                    buf.append("UNKNOWN ");
                    break;
            }
            buf.append(node.getNodeName());
            buf.append("{ " + node.getNodeValue() + " }, ");
            if (container != null) {
                buf.append(container.eClass().getName() + ", ");
            } else {
                buf.append("null, ");
            }
            if (feature != null) {
                buf.append(feature.getName() + ", ");
            } else {
                buf.append("null, ");
            }
            if (value != null) {
                buf.append(value.getClass().getName() + ": " + value.toString() + ");");
            } else {
                buf.append("null);");
            }
            System.out.println(buf.toString());
        }
    }
}
