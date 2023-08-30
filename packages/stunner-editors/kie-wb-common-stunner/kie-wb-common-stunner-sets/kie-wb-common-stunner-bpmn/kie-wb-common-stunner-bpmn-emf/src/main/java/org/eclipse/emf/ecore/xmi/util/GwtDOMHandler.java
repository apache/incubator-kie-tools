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


package org.eclipse.emf.ecore.xmi.util;

import com.google.gwt.xml.client.Attr;
import com.google.gwt.xml.client.CDATASection;
import com.google.gwt.xml.client.Comment;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.NamedNodeMap;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.ProcessingInstruction;
import com.google.gwt.xml.client.Text;

import static org.kie.workbench.common.stunner.bpmn.client.emf.Bpmn2Marshalling.logError;

public class GwtDOMHandler {

    private static final String CDATA_START = "<![CDATA[";

    private final Document document;

    public GwtDOMHandler(final Document document) {
        this.document = document;
    }

    public Element createElement(final String tagName) {
        return document.createElement(tagName);
    }

    public Text createTextNode(final String data) {
        if (isCData(data)) {
            return createCDATASection(removeCData(data));
        } else {
            return document.createTextNode(data);
        }
    }

    public Comment createComment(final String data) {
        return document.createComment(data);
    }

    public CDATASection createCDATASection(final String data) {
        String value = isCData(data) ? removeCData(data) : data;
        return document.createCDATASection(value);
    }

    public ProcessingInstruction createProcessingInstruction(final String target,
                                                             final String data) {
        return document.createProcessingInstruction(target, data);
    }

    public Element createElementNS(final String namespaceURI,
                                   final String qualifiedName) {
        String tagName = qualifiedName;
        int i = qualifiedName.lastIndexOf(':');
        if (i > -1) {
            String[] qn = new String[]{qualifiedName.substring(0, i), qualifiedName.substring(i + 1)};
            i = qn[0].lastIndexOf(':');
            if (i > -1) {
                // TODO: Kogito - Check this workaround
                logError("[WORKAROUND APPLIED]: Fixing NS '" + qualifiedName + "'");
                qn[0] = qn[0].substring(i + 1);
            }
            tagName = qn[0] + ":" + qn[1];
        }
        return document.createElement(tagName);
    }

    public Attr createAttributeNS(final String namespaceURI,
                                  final String qualifiedName) {
        return new GwtDOMAttr(namespaceURI, qualifiedName);
    }

    public Attr setAttributeNS(final Node node,
                               final String namespaceURI,
                               final String qualifiedName,
                               final String value) {
        final Element e = (Element) node;
        e.setAttribute(qualifiedName, value);
        return e.getAttributeNode(qualifiedName);
    }

    public Attr setAttributeNodeNS(final Node element,
                                   final Attr attr) {
        if (attr instanceof GwtDOMAttr) {
            ((GwtDOMAttr) attr).setNode(element);
        }
        return setAttributeNS(element,
                              attr.getNamespaceURI(),
                              attr.getName(),
                              attr.getValue());
    }

    public static Node getNamedItem(NamedNodeMap map,
                                    String uri,
                                    String localName) {
        return map.getNamedItem(localName);
    }

    public Document getDocument() {
        return document;
    }

    private static String removeCData(final String data) {
        return data.substring(9, data.length() - 3);
    }

    private static boolean isCData(final String data) {
        return data.startsWith(CDATA_START);
    }

    static class GwtDOMAttr implements Attr {

        private final String namespaceURI;
        private final String qualifiedName;
        private String value;
        private Node node;

        GwtDOMAttr(String namespaceURI,
                   String qualifiedName) {
            this.namespaceURI = namespaceURI;
            this.qualifiedName = qualifiedName;
        }

        public void setNode(Node node) {
            this.node = node;
        }

        @Override
        public String getName() {
            return qualifiedName;
        }

        @Override
        public boolean getSpecified() {
            return true;
        }

        @Override
        public String getValue() {
            return value;
        }

        @Override
        public Node appendChild(Node newChild) {
            return node.appendChild(newChild);
        }

        @Override
        public Node cloneNode(boolean deep) {
            return node.cloneNode(deep);
        }

        @Override
        public NamedNodeMap getAttributes() {
            return node.getAttributes();
        }

        @Override
        public NodeList getChildNodes() {
            return node.getChildNodes();
        }

        @Override
        public Node getFirstChild() {
            return node.getFirstChild();
        }

        @Override
        public Node getLastChild() {
            return node.getLastChild();
        }

        @Override
        public String getNamespaceURI() {
            return namespaceURI;
        }

        @Override
        public Node getNextSibling() {
            return node.getNextSibling();
        }

        @Override
        public String getNodeName() {
            return node.getNodeName();
        }

        @Override
        public short getNodeType() {
            return node.getNodeType();
        }

        @Override
        public String getNodeValue() {
            return null != value ? value : (null != node ? node.getNodeValue() : null);
        }

        @Override
        public Document getOwnerDocument() {
            return node.getOwnerDocument();
        }

        @Override
        public Node getParentNode() {
            return node.getParentNode();
        }

        @Override
        public String getPrefix() {
            return node.getPrefix();
        }

        @Override
        public Node getPreviousSibling() {
            return node.getPreviousSibling();
        }

        @Override
        public boolean hasAttributes() {
            return node.hasAttributes();
        }

        @Override
        public boolean hasChildNodes() {
            return node.hasChildNodes();
        }

        @Override
        public Node insertBefore(Node newChild, Node refChild) {
            return node.insertBefore(newChild, refChild);
        }

        @Override
        public void normalize() {
            node.normalize();
        }

        @Override
        public Node removeChild(Node oldChild) {
            return node.removeChild(oldChild);
        }

        @Override
        public Node replaceChild(Node newChild, Node oldChild) {
            return node.replaceChild(newChild, oldChild);
        }

        @Override
        public void setNodeValue(String nodeValue) {
            this.value = nodeValue;
        }
    }
}
