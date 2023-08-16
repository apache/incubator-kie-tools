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
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class GwtDOMHandlerTest {

    @Mock
    private Document document;

    private GwtDOMHandler tested;

    @Before
    public void setup() {
        tested = new GwtDOMHandler(document);
    }

    @Test
    public void testGetDocument() {
        assertEquals(document, tested.getDocument());
    }

    @Test
    public void testCreateElement() {
        Element element = mock(Element.class);
        when(document.createElement(eq("tag1"))).thenReturn(element);
        Element result = tested.createElement("tag1");
        assertEquals(element, result);
        verify(document, times(1)).createElement(eq("tag1"));
    }

    @Test
    public void testCreateText() {
        String value = "someText";
        Text text = mock(Text.class);
        when(document.createTextNode(eq(value))).thenReturn(text);
        Text result = tested.createTextNode(value);
        assertEquals(text, result);
        verify(document, times(1)).createTextNode(eq(value));
        verify(document, never()).createCDATASection(anyString());
    }

    @Test
    public void testCreateCDATAText() {
        String value = "someText";
        CDATASection cdataSection = mock(CDATASection.class);
        when(document.createCDATASection(eq(value))).thenReturn(cdataSection);
        Text result = tested.createTextNode("<![CDATA[" + value + "]]>");
        assertEquals(cdataSection, result);
        verify(document, times(1)).createCDATASection(eq(value));
        verify(document, never()).createTextNode(anyString());
    }

    @Test
    public void testCreateComment() {
        String value = "comment1";
        Comment comment = mock(Comment.class);
        when(document.createComment(eq(value))).thenReturn(comment);
        Comment result = tested.createComment(value);
        assertEquals(comment, result);
        verify(document, times(1)).createComment(eq(value));
    }

    @Test
    public void testCreateCDATASectionWithDeclaration() {
        testCreateCDATASection("data1", true);
    }

    @Test
    public void testCreateCDATASectionWithoutDeclaration() {
        testCreateCDATASection("data1", false);
    }

    private void testCreateCDATASection(String value, boolean declare) {
        CDATASection cdataSection = mock(CDATASection.class);
        when(document.createCDATASection(eq(value))).thenReturn(cdataSection);
        CDATASection result = tested.createCDATASection(declare ? "<![CDATA[" + value + "]]>" : value);
        assertEquals(cdataSection, result);
        verify(document, times(1)).createCDATASection(eq(value));
        verify(document, never()).createTextNode(anyString());
        verify(document, never()).createComment(anyString());
    }

    @Test
    public void testCreateProcessingInstruction() {
        ProcessingInstruction pi = mock(ProcessingInstruction.class);
        when(document.createProcessingInstruction(eq("target"), eq("data"))).thenReturn(pi);
        ProcessingInstruction result = tested.createProcessingInstruction("target", "data");
        assertEquals(pi, result);
        verify(document, times(1)).createProcessingInstruction(eq("target"), eq("data"));
    }

    // TODO: Kogito - @Test
    public void testCreateElementNS() {
    }

    @Test
    public void testGwtDOMAttr() {
        String namespaceURI = "uri1";
        String qualifiedName = "fqn1";
        GwtDOMHandler.GwtDOMAttr attr = new GwtDOMHandler.GwtDOMAttr(namespaceURI, qualifiedName);
        assertEquals(namespaceURI, attr.getNamespaceURI());
        assertEquals(qualifiedName, attr.getName());
        assertTrue(attr.getSpecified());
        Node node = mock(Node.class);
        Node otherNode = mock(Node.class);
        attr.setNode(node);
        attr.appendChild(otherNode);
        verify(node, times(1)).appendChild(eq(otherNode));
        attr.cloneNode(true);
        verify(node, times(1)).cloneNode(eq(true));
        NamedNodeMap namedNodeMap = mock(NamedNodeMap.class);
        when(node.getAttributes()).thenReturn(namedNodeMap);
        assertEquals(namedNodeMap, attr.getAttributes());
        NodeList children = mock(NodeList.class);
        when(node.getChildNodes()).thenReturn(children);
        assertEquals(children, attr.getChildNodes());
        Node n = mock(Node.class);
        when(node.getFirstChild()).thenReturn(n);
        assertEquals(n, attr.getFirstChild());
        n = mock(Node.class);
        when(node.getLastChild()).thenReturn(n);
        assertEquals(n, attr.getLastChild());
        n = mock(Node.class);
        when(node.getNextSibling()).thenReturn(n);
        assertEquals(n, attr.getNextSibling());
        when(node.getNodeName()).thenReturn("n1");
        assertEquals("n1", attr.getNodeName());
        when(node.getNodeType()).thenReturn((short) 1);
        assertEquals((short) 1, attr.getNodeType());
        Document document = mock(Document.class);
        when(node.getOwnerDocument()).thenReturn(document);
        assertEquals(document, attr.getOwnerDocument());
        n = mock(Node.class);
        when(node.getParentNode()).thenReturn(n);
        assertEquals(n, attr.getParentNode());
        when(node.getPrefix()).thenReturn("p1");
        assertEquals("p1", attr.getPrefix());
        n = mock(Node.class);
        when(node.getPreviousSibling()).thenReturn(n);
        assertEquals(n, attr.getPreviousSibling());
        when(node.hasAttributes()).thenReturn(true);
        assertTrue(attr.hasAttributes());
        when(node.hasChildNodes()).thenReturn(true);
        assertTrue(attr.hasChildNodes());
        Node n1 = mock(Node.class);
        Node n2 = mock(Node.class);
        when(node.insertBefore(eq(n1), eq(n2))).thenReturn(node);
        assertEquals(node, attr.insertBefore(n1, n2));
        attr.normalize();
        verify(node, times(1)).normalize();
        n = mock(Node.class);
        when(node.removeChild(eq(n))).thenReturn(n);
        assertEquals(n, attr.removeChild(n));
        Node r1 = mock(Node.class);
        Node r2 = mock(Node.class);
        when(node.replaceChild(eq(r1), eq(r2))).thenReturn(r1);
        assertEquals(r1, attr.replaceChild(r1, r2));
        String value = "nodeValue1";
        attr.setNodeValue(value);
        assertEquals(value, attr.getValue());
        assertEquals(value, attr.getNodeValue());
    }

    @Test
    public void testCreateAttributeNS() {
        String namespaceURI = "uri1";
        String qualifiedName = "fqn1";
        Attr attr = tested.createAttributeNS(namespaceURI, qualifiedName);
        assertEquals(namespaceURI, attr.getNamespaceURI());
        assertEquals(qualifiedName, attr.getName());
        String value = "nodeValue1";
        attr.setNodeValue(value);
        assertEquals(value, attr.getValue());
        assertEquals(value, attr.getNodeValue());
    }

    @Test
    public void testSetAttributeNS() {
        Element node = mock(Element.class);
        String namespaceURI = "uri1";
        String qualifiedName = "fqn1";
        String value = "nodeValue1";
        Attr attr = mock(Attr.class);
        when(node.getAttributeNode(eq(qualifiedName))).thenReturn(attr);
        Attr result = tested.setAttributeNS(node, namespaceURI, qualifiedName, value);
        assertEquals(attr, result);
        verify(node, times(1)).setAttribute(eq(qualifiedName), eq(value));
    }

    @Test
    public void testSetAttributeNodeNS() {
        Element node = mock(Element.class);
        String namespaceURI = "uri1";
        String qualifiedName = "fqn1";
        String value = "nodeValue1";
        GwtDOMHandler.GwtDOMAttr gwtAttr = new GwtDOMHandler.GwtDOMAttr(namespaceURI, qualifiedName);
        gwtAttr.setNodeValue(value);
        GwtDOMHandler.GwtDOMAttr attr = spy(gwtAttr);
        Attr r = mock(Attr.class);
        when(node.getAttributeNode(eq(qualifiedName))).thenReturn(r);
        Attr result = tested.setAttributeNodeNS(node, attr);
        assertEquals(r, result);
        verify(node, times(1)).setAttribute(eq(qualifiedName), eq(value));
        verify(attr, times(1)).setNode(eq(node));
    }

    @Test
    public void testGetNamedItem() {
        Element node = mock(Element.class);
        String namespaceURI = "uri1";
        String name = "name1";
        NamedNodeMap map = mock(NamedNodeMap.class);
        when(map.getNamedItem(eq(name))).thenReturn(node);
        Node result = tested.getNamedItem(map, namespaceURI, name);
        assertEquals(node, result);
    }
}
