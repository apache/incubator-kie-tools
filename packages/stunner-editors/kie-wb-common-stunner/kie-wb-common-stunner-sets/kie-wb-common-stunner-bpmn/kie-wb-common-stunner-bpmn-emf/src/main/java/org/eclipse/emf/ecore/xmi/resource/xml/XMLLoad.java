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

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.gwt.xml.client.CDATASection;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.NamedNodeMap;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.ProcessingInstruction;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.util.GwtDOMHandler;

/**
 * This class begins parsing with the given input stream using the XML
 * deserializer.
 */
public class XMLLoad {

    protected static final String SAX_LEXICAL_PROPERTY = "http://xml.org/sax/properties/lexical-handler";
    protected static final int BUFFER_SIZE = 200;
    protected XMLResource resource;
    protected XMLHelper helper;
    protected Map<?, ?> options;
    protected boolean namespaceAware;

    public XMLLoad(XMLHelper helper) {
        this.helper = helper;
    }

    protected void handleErrors() throws IOException {
        if (!resource.getErrors().isEmpty()) {
            Resource.Diagnostic error = resource.getErrors().get(0);
            if (error instanceof Exception) {
                throw new Resource.IOWrappedException((Exception) error);
            } else {
                throw new IOException(error.getMessage());
            }
        }
    }

    protected XMLHandler makeDefaultHandler() {
        return new XMLHandlerImpl(resource, helper, options);
    }

    public void load(XMLResource resource, Node node, Map<?, ?> options) throws IOException {
        this.resource = resource;
        this.options = options;
        this.namespaceAware = Boolean.FALSE.equals(options.get(XMLResource.OPTION_USE_DEPRECATED_METHODS));
        XMLHandler handler;
        {
            handler = makeDefaultHandler();
        }

        AttributesProxy attributesProxy = new AttributesProxy();
        try {
            short type = node.getNodeType();
            if (type == Node.ELEMENT_NODE) {
                handler.startDocument();
                if (Boolean.TRUE.equals(options.get(XMLResource.OPTION_DOM_USE_NAMESPACES_IN_SCOPE))) {
                    traverseElement((Element) node, attributesProxy, handler);
                } else {
                    traverse(node, attributesProxy, handler);
                }
                handler.endDocument();
            } else {
                traverse(node, attributesProxy, handler);
            }
        } catch (XMLParseException e) {
            // ignore
        }

//    if (pool != null)
//    {
//      pool.releaseDefaultHandler((XMLDefaultHandler)handler, options);
//    }

        attributesProxy = null;
        handler = null;
        helper = null;

        handleErrors();
    }

    /**
     * Special case: traversing root element using namespaces in scope
     */
    protected void traverseElement(Element element, AttributesProxy attributesProxy, XMLHandler handler) throws XMLParseException {
        // temporary structure to hold node's attributes + namespaces in scope
        AttributesImpl attrs = new AttributesImpl();
        Set<String> prefixes = new HashSet<String>();

        // record node's attributes
        if (element.hasAttributes()) {
            NamedNodeMap attributes = element.getAttributes();
            for (int i = 0; i < attributes.getLength(); i++) {
                Node attr = attributes.item(i);
                String namespaceURI = attr.getNamespaceURI();
                if (namespaceURI == null) {
                    namespaceURI = "";
                }
                String nodeName = attr.getNodeName();
                String localName = getLocalName(attr);
                String nodeValue = attr.getNodeValue();
                if (ExtendedMetaData.XMLNS_URI.equals(namespaceURI)) {
                    // Include only non-duplicate namespace declarations.
                    //
                    if (namespaceAware) {
                        if (prefixes.add(localName)) {
                            handler.startPrefixMapping(localName, nodeValue);
                        }
                    } else if (attrs.getIndex(nodeName) < 0) {
                        attrs.addAttribute(namespaceURI, localName, nodeName, "CDATA", nodeValue);
                    }
                } else {
                    attrs.addAttribute(namespaceURI, localName, nodeName, "CDATA", nodeValue);
                }
            }
        }

        // record namespaces in scope
        //
        for (Node parent = element.getParentNode(); parent != null && parent.getNodeType() != Node.DOCUMENT_NODE; parent = parent.getParentNode()) {
            if (parent.hasAttributes()) {
                NamedNodeMap attributes = parent.getAttributes();
                for (int i = 0; i < attributes.getLength(); i++) {
                    Node attr = attributes.item(i);
                    String namespaceURI = attr.getNamespaceURI();
                    if (ExtendedMetaData.XMLNS_URI.equals(namespaceURI)) {
                        // Include only non-duplicate namespace declarations.
                        //
                        String localName = getLocalName(attr);
                        String nodeValue = attr.getNodeValue();
                        if (namespaceAware) {
                            if (prefixes.add(localName)) {
                                handler.startPrefixMapping(localName, nodeValue);
                            }
                        } else {
                            String nodeName = attr.getNodeName();
                            if (attrs.getIndex(nodeName) < 0) {
                                attrs.addAttribute(namespaceURI, localName, nodeName, "CDATA", nodeValue);
                            }
                        }
                    }
                }
            }
        }

        // traverse element node
        String namespaceURI = element.getNamespaceURI();
        if (namespaceURI == null) {
            namespaceURI = "";
        }
        String localName = getLocalName(element);
        String qname = element.getNodeName();

        handler.startElement(namespaceURI, localName, qname, attrs);
        Node child = element.getFirstChild();
        while (child != null) {
            traverse(child, attributesProxy, handler);
            child = child.getNextSibling();
        }
        handler.endElement(namespaceURI, localName, qname);
    }

    public static String getLocalName(Node attr) {
        return attr.getNodeName().split(":")[1];
    }

    protected void traverse(Node node, AttributesProxy attributesProxy, XMLHandler handler) throws XMLParseException {
        if (node == null) {
            return;
        }

        short type = node.getNodeType();
        switch (type) {
            case Node.DOCUMENT_NODE: {
                Document document = (Document) node;
                handler.startDocument();
                Node root = document.getDocumentElement();
                traverse(root, attributesProxy, handler);
                handler.endDocument();
                break;
            }
            case Node.ELEMENT_NODE: {
                AttributesImpl filteredAttributes = null;
                NamedNodeMap attributes = node.getAttributes();
                if (namespaceAware) {
                    for (int i = 0, length = attributes.getLength(); i < length; i++) {
                        Node attr = attributes.item(i);
                        String namespaceURI = attr.getNamespaceURI();
                        if (ExtendedMetaData.XMLNS_URI.equals(namespaceURI)) {
                            handler.startPrefixMapping(getLocalName(attr), attr.getNodeValue());
                            if (filteredAttributes == null) {
                                filteredAttributes = new AttributesImpl();
                                for (int j = 0; j < i; ++j) {
                                    attr = attributes.item(j);
                                    namespaceURI = attr.getNamespaceURI();
                                    if (namespaceURI == null) {
                                        namespaceURI = "";
                                    }
                                    filteredAttributes.addAttribute(namespaceURI, getLocalName(attr), attr.getNodeName(), "CDATA", attr.getNodeValue());
                                }
                            }
                        } else if (filteredAttributes != null) {
                            if (namespaceURI == null) {
                                namespaceURI = "";
                            }
                            filteredAttributes.addAttribute(namespaceURI, getLocalName(attr), attr.getNodeName(), "CDATA", attr.getNodeValue());
                        }
                    }
                }
                if (filteredAttributes == null) {
                    attributesProxy.setAttributes(attributes);
                }
                String namespaceURI = node.getNamespaceURI();
                if (namespaceURI == null) {
                    namespaceURI = "";
                }
                String localName = getLocalName(node);
                String qname = node.getNodeName();

                handler.startElement(namespaceURI, localName, qname, filteredAttributes == null ? attributesProxy : filteredAttributes);

                Node child = node.getFirstChild();
                while (child != null) {
                    traverse(child, attributesProxy, handler);
                    child = child.getNextSibling();
                }
                handler.endElement(namespaceURI, localName, qname);
                break;
            }

            case Node.CDATA_SECTION_NODE: {
                char[] chars = ((CDATASection) node).getData().toCharArray();
                handler.characters(chars, 0, chars.length);
                break;
            }
            case Node.TEXT_NODE: {
                char[] chars = node.getNodeValue().toCharArray();
                handler.characters(chars, 0, chars.length);
                break;
            }
            case Node.COMMENT_NODE: {
                break;
            }
            case Node.PROCESSING_INSTRUCTION_NODE: {
                ProcessingInstruction pi = (ProcessingInstruction) node;
                handler.processingInstruction(pi.getTarget(), pi.getData());
                break;
            }
        }
    }

    protected static final class AttributesProxy implements Attributes {

        /**
         * DOM attributes.
         */
        protected NamedNodeMap attributes;

        /**
         * Sets the DOM attributes.
         */
        public void setAttributes(NamedNodeMap attributes) {
            this.attributes = attributes;
        }

        public int getLength() {
            return attributes.getLength();
        }

        public String getQName(int index) {
            Node node = attributes.item(index);
            return (node != null) ? node.getNodeName() : null;
        }

        public String getURI(int index) {
            Node node = attributes.item(index);
            if (node != null) {
                String namespaceURI = node.getNamespaceURI();
                if (ExtendedMetaData.XMLNS_URI.equals(namespaceURI)) {
                    return "";
                }
                return namespaceURI;
            }
            return null;
        }

        public String getLocalName(int index) {
            Node node = attributes.item(index);
            if (node != null) {
                String prefix = node.getPrefix();
                if (ExtendedMetaData.XMLNS_PREFIX.equals(prefix)) {
                    return "";
                }
                return XMLLoad.getLocalName(node);
            }
            return null;
        }

        public String getType(int i) {
            return "CDATA";
        }

        public String getType(String name) {
            return "CDATA";
        }

        public String getType(String uri, String localName) {
            return "CDATA";
        }

        public String getValue(int i) {
            Node node = attributes.item(i);
            return (node != null) ? node.getNodeValue() : null;
        }

        public String getValue(String name) {
            Node node = attributes.getNamedItem(name);
            return (node != null) ? node.getNodeValue() : null;
        }

        public String getValue(String uri, String localName) {
            Node node = GwtDOMHandler.getNamedItem(attributes, uri, localName);
            return (node != null) ? node.getNodeValue() : null;
        }

        public int getIndex(String qName) {
            Node node = attributes.getNamedItem(qName);
            if (node != null) {
                for (int i = 0; i < attributes.getLength(); i++) {
                    Node item = attributes.item(i);
                    if (item == node) {
                        return i;
                    }
                }
            }
            return -1;
        }

        public int getIndex(String uri, String localPart) {
            Node node = GwtDOMHandler.getNamedItem(attributes, uri, localPart);
            if (node != null) {
                for (int i = 0; i < attributes.getLength(); i++) {
                    Node item = attributes.item(i);
                    if (item == node) {
                        return i;
                    }
                }
            }
            return -1;
        }
    } // class AttributesProxy
} // XMLLoad
