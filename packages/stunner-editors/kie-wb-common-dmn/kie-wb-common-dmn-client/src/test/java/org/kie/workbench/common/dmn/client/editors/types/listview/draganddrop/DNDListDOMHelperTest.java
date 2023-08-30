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

package org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop;

import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.CSSStyleDeclaration;
import elemental2.dom.DOMTokenList;
import elemental2.dom.Element;
import elemental2.dom.HTMLDocument;
import elemental2.dom.HTMLElement;
import elemental2.dom.NodeList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListDOMHelper.Position;
import org.mockito.Mock;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListDOMHelper.DATA_X_POSITION;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListDOMHelper.DATA_Y_POSITION;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListDOMHelper.DRAGGABLE;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListDOMHelper.DRAGGING;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListDOMHelper.Factory.ELLIPSIS_CLASS;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListDOMHelper.Factory.ICON_CLASS;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListDOMHelper.Factory.createDiv;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListDOMHelper.Factory.createGripElement;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListDOMHelper.GRIP;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListDOMHelper.HOVER;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListDOMHelper.asDraggable;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListDOMHelper.asDragging;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListDOMHelper.asHover;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListDOMHelper.asNonDragging;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListDOMHelper.asNonHover;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListDOMHelper.getCSSPaddingLeft;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListDOMHelper.getCSSTop;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListDOMHelper.getCSSWidth;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListDOMHelper.isDraggingElement;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListDOMHelper.isGrip;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListDOMHelper.parseDouble;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListDOMHelper.parseInt;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListDOMHelper.querySelector;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListDOMHelper.setCSSPaddingLeft;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListDOMHelper.setCSSTop;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListDOMHelper.setCSSWidth;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DNDListDOMHelperTest {

    @Mock
    private HTMLElement element;

    // -- Position

    @Test
    public void testGetX() {

        when(element.getAttribute(DATA_X_POSITION)).thenReturn("42.5px");

        final Integer actual = Position.getX(element);
        final Integer expected = 42;

        assertEquals(expected, actual);
    }

    @Test
    public void testSetX() {

        Position.setX(element, 42);

        verify(element).setAttribute(DATA_X_POSITION, 42);
    }

    @Test
    public void testGetY() {

        when(element.getAttribute(DATA_Y_POSITION)).thenReturn("42.5px");

        final Integer actual = Position.getY(element);
        final Integer expected = 42;

        assertEquals(expected, actual);
    }

    @Test
    public void testGetDoubleY() {

        when(element.getAttribute(DATA_Y_POSITION)).thenReturn("42.5px");

        final Double actual = Position.getDoubleY(element);
        final Double expected = 42.5;

        assertEquals(expected, actual);
    }

    @Test
    public void testSetY() {

        Position.setY(element, 42);

        verify(element).setAttribute(DATA_Y_POSITION, 42);
    }

    // -- QuerySelector

    @Test
    public void testGetDraggableElement() {

        final String selector = ".kie-dnd-draggable[data-y-position=\"42\"]";
        final HTMLElement expectedElement = mock(HTMLElement.class);

        when(element.querySelector(selector)).thenReturn(expectedElement);

        final HTMLElement actualElement = querySelector(element).getDraggableElement(42).orElseThrow(RuntimeException::new);

        assertEquals(expectedElement, actualElement);
    }

    @Test
    public void testGetHoverElement() {

        final String selector = ".kie-dnd-hover";
        final HTMLElement expectedElement = mock(HTMLElement.class);

        when(element.querySelector(selector)).thenReturn(expectedElement);

        final HTMLElement actualElement = querySelector(element).getHoverElement().orElseThrow(RuntimeException::new);

        assertEquals(expectedElement, actualElement);
    }

    @Test
    public void testGetDraggableElements() {

        final String selector = ".kie-dnd-draggable";
        final HTMLElement element0 = mock(HTMLElement.class);
        final HTMLElement element1 = mock(HTMLElement.class);
        final HTMLElement element2 = mock(HTMLElement.class);
        final NodeList<Element> nodeList = spy(new NodeList<>());

        nodeList.length = 3;
        doReturn(element0).when(nodeList).getAt(0);
        doReturn(element1).when(nodeList).getAt(1);
        doReturn(element2).when(nodeList).getAt(2);

        doReturn(nodeList).when(element).querySelectorAll(selector);

        final List<HTMLElement> actualElements = querySelector(element).getDraggableElements();
        final List<HTMLElement> expectElements = asList(element0, element1, element2);

        assertEquals(expectElements, actualElements);
    }

    @Test
    public void testGetSortedDraggableElements() {

        final String selector = ".kie-dnd-draggable";
        final HTMLElement element0 = mock(HTMLElement.class);
        final HTMLElement element1 = mock(HTMLElement.class);
        final HTMLElement element2 = mock(HTMLElement.class);
        final NodeList<Element> nodeList = spy(new NodeList<>());

        nodeList.length = 3;
        doReturn(element0).when(nodeList).getAt(0);
        doReturn(element1).when(nodeList).getAt(1);
        doReturn(element2).when(nodeList).getAt(2);
        doReturn("1").when(element0).getAttribute(DATA_Y_POSITION);
        doReturn("0").when(element1).getAttribute(DATA_Y_POSITION);
        doReturn("-1").when(element2).getAttribute(DATA_Y_POSITION);

        doReturn(nodeList).when(element).querySelectorAll(selector);

        final List<HTMLElement> actualElements = querySelector(element).getSortedDraggableElements();
        final List<HTMLElement> expectElements = asList(element2, element1, element0);

        assertEquals(expectElements, actualElements);
    }

    @Test
    public void testGetVisibleDraggableElements() {

        final String selector = ".kie-dnd-draggable";
        final HTMLElement element0 = mock(HTMLElement.class);
        final HTMLElement element1 = mock(HTMLElement.class);
        final HTMLElement element2 = mock(HTMLElement.class);
        final NodeList<Element> nodeList = spy(new NodeList<>());

        nodeList.length = 3;
        doReturn(element0).when(nodeList).getAt(0);
        doReturn(element1).when(nodeList).getAt(1);
        doReturn(element2).when(nodeList).getAt(2);
        doReturn("1").when(element0).getAttribute(DATA_Y_POSITION);
        doReturn("0").when(element1).getAttribute(DATA_Y_POSITION);
        doReturn("-1").when(element2).getAttribute(DATA_Y_POSITION);

        doReturn(nodeList).when(element).querySelectorAll(selector);

        final List<HTMLElement> actualElements = querySelector(element).getVisibleDraggableElements();
        final List<HTMLElement> expectElements = asList(element0, element1);

        assertEquals(expectElements, actualElements);
    }

    @Test
    public void testGetVisibleAndSortedDraggableElements() {

        final String selector = ".kie-dnd-draggable";
        final HTMLElement element0 = mock(HTMLElement.class);
        final HTMLElement element1 = mock(HTMLElement.class);
        final HTMLElement element2 = mock(HTMLElement.class);
        final NodeList<Element> nodeList = spy(new NodeList<>());

        nodeList.length = 3;
        doReturn(element0).when(nodeList).getAt(0);
        doReturn(element1).when(nodeList).getAt(1);
        doReturn(element2).when(nodeList).getAt(2);
        doReturn("1").when(element0).getAttribute(DATA_Y_POSITION);
        doReturn("0").when(element1).getAttribute(DATA_Y_POSITION);
        doReturn("-1").when(element2).getAttribute(DATA_Y_POSITION);

        doReturn(nodeList).when(element).querySelectorAll(selector);

        final List<HTMLElement> actualElements = querySelector(element).getVisibleAndSortedDraggableElements();
        final List<HTMLElement> expectElements = asList(element1, element0);

        assertEquals(expectElements, actualElements);
    }

    // -- Property handlers

    @Test
    public void testSetCSSTop() {

        element.style = mock(CSSStyleDeclaration.class);

        setCSSTop(element, 123);

        verify(element.style).setProperty("top", "123px");
    }

    @Test
    public void testSetCSSWidth() {

        element.style = mock(CSSStyleDeclaration.class);

        setCSSWidth(element, 321);

        verify(element.style).setProperty("width", "calc(100% - 321px)");
    }

    @Test
    public void testSetCSSPaddingLeft() {

        element.style = mock(CSSStyleDeclaration.class);

        setCSSPaddingLeft(element, 321);

        verify(element.style).setProperty("padding-left", "321px");
    }

    @Test
    public void testGetCSSTop() {

        element.style = mock(CSSStyleDeclaration.class);
        when(element.style.getPropertyValue("top")).thenReturn("123px");

        final int actualTop = getCSSTop(element);
        final int expectedTop = 123;

        assertEquals(expectedTop, actualTop);
    }

    @Test
    public void testGetCSSWidth() {

        element.style = mock(CSSStyleDeclaration.class);
        when(element.style.getPropertyValue("width")).thenReturn("calc(100% - 321px)");

        final int actualWidth = getCSSWidth(element);
        final int expectedWidth = 321;

        assertEquals(expectedWidth, actualWidth);
    }

    @Test
    public void testGetCSSPaddingLeft() {

        element.style = mock(CSSStyleDeclaration.class);
        when(element.style.getPropertyValue("padding-left")).thenReturn("321");

        final int actualPadding = getCSSPaddingLeft(element);
        final int expectedPadding = 321;

        assertEquals(expectedPadding, actualPadding);
    }

    // -- Class handlers

    @Test
    public void testAsHover() {

        element.classList = mock(DOMTokenList.class);

        final HTMLElement actual = asHover(element);
        final HTMLElement expected = element;

        verify(element.classList).add(HOVER);
        assertEquals(expected, actual);
    }

    @Test
    public void testAsNonHover() {

        element.classList = mock(DOMTokenList.class);

        final HTMLElement actual = asNonHover(element);
        final HTMLElement expected = element;

        verify(element.classList).remove(HOVER);
        assertEquals(expected, actual);
    }

    @Test
    public void testAsDragging() {

        element.classList = mock(DOMTokenList.class);

        final HTMLElement actual = asDragging(element);
        final HTMLElement expected = element;

        verify(element.classList).add(DRAGGING);
        assertEquals(expected, actual);
    }

    @Test
    public void testAsNonDragging() {

        element.classList = mock(DOMTokenList.class);

        final HTMLElement actual = asNonDragging(element);
        final HTMLElement expected = element;

        verify(element.classList).remove(DRAGGING);
        assertEquals(expected, actual);
    }

    @Test
    public void testAsDraggable() {

        element.classList = mock(DOMTokenList.class);

        final HTMLElement actual = asDraggable(element);
        final HTMLElement expected = element;

        verify(element.classList).add(DRAGGABLE);
        assertEquals(expected, actual);
    }

    @Test
    public void testIsDraggingElementWhenItsDragging() {

        element.classList = mock(DOMTokenList.class);
        when(element.classList.contains(DRAGGING)).thenReturn(true);

        assertTrue(isDraggingElement(element));
    }

    @Test
    public void testIsDraggingElementWhenItsNotDragging() {

        element.classList = mock(DOMTokenList.class);
        when(element.classList.contains(DRAGGING)).thenReturn(false);

        assertFalse(isDraggingElement(element));
    }

    @Test
    public void testIsGripWhenItsGrip() {

        element.classList = mock(DOMTokenList.class);
        when(element.classList.contains(GRIP)).thenReturn(true);

        assertTrue(isGrip(element));
    }

    @Test
    public void testIsGripWhenItsNotGrip() {

        element.classList = mock(DOMTokenList.class);
        when(element.classList.contains(GRIP)).thenReturn(false);

        assertFalse(isGrip(element));
    }

    // -- Factory

    @Test
    public void testCreateDiv() {

        final HTMLDocument document = mock(HTMLDocument.class);
        final HTMLElement expectedDiv = mock(HTMLElement.class);
        final String tagName = "div";

        DNDListDOMHelper.Factory.DOCUMENT = document;
        when(document.createElement(tagName)).thenReturn(expectedDiv);

        final HTMLElement actualDiv = createDiv();

        assertEquals(expectedDiv, actualDiv);
    }

    @Test
    public void testCreateGripElement() {

        final HTMLDocument document = mock(HTMLDocument.class);
        final HTMLElement expectedGrip = mock(HTMLElement.class);
        final HTMLElement firstI = mock(HTMLElement.class);
        final HTMLElement secondI = mock(HTMLElement.class);
        final String div = "div";
        final String i = "i";

        DNDListDOMHelper.Factory.DOCUMENT = document;
        expectedGrip.classList = mock(DOMTokenList.class);
        firstI.classList = mock(DOMTokenList.class);
        secondI.classList = mock(DOMTokenList.class);
        when(document.createElement(div)).thenReturn(expectedGrip);
        when(document.createElement(i)).thenReturn(firstI, secondI);

        final HTMLElement actualGrip = createGripElement();

        verify(expectedGrip).appendChild(firstI);
        verify(expectedGrip).appendChild(secondI);

        verify(expectedGrip.classList).add(GRIP);

        verify(firstI.classList).add(ICON_CLASS);
        verify(secondI.classList).add(ICON_CLASS);

        verify(firstI.classList).add(ELLIPSIS_CLASS);
        verify(secondI.classList).add(ELLIPSIS_CLASS);

        assertEquals(expectedGrip, actualGrip);
    }

    // -- Parsers

    @Test
    public void testParseDouble() {
        assertEquals(parseDouble("10"), new Double(10));
        assertEquals(parseDouble("10.5"), new Double(10.5));
        assertEquals(parseDouble("10.5px"), new Double(10.5));
        assertEquals(parseDouble("something..."), new Double(0));
    }

    @Test
    public void testParseInt() {
        assertEquals(parseInt("10"), new Integer(10));
        assertEquals(parseInt("10.5"), new Integer(10));
        assertEquals(parseInt("10.5px"), new Integer(10));
        assertEquals(parseInt("something..."), new Integer(0));
    }
}
