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

package org.kie.workbench.common.dmn.client.editors.types.listview.constraint.enumeration;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.CSSStyleDeclaration;
import elemental2.dom.Element;
import elemental2.dom.Event;
import elemental2.dom.HTMLElement;
import elemental2.dom.MouseEvent;
import elemental2.dom.NodeList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.dmn.client.editors.types.listview.constraint.enumeration.DragAndDropHelper.PX;
import static org.kie.workbench.common.dmn.client.editors.types.listview.constraint.enumeration.DragAndDropHelper.TOP;
import static org.kie.workbench.common.dmn.client.editors.types.listview.constraint.enumeration.item.DataTypeConstraintEnumerationItemView.DATA_POSITION;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DragAndDropHelperTest {

    @Mock
    private HTMLElement dragArea;

    @Mock
    private HTMLElement addButtonContainer;

    @Mock
    private CSSStyleDeclaration addButtonContainerStyle;

    private DragAndDropHelper helper;

    @Before
    public void setup() {
        helper = spy(new DragAndDropHelper(dragArea, addButtonContainer));
        addButtonContainer.style = addButtonContainerStyle;
    }

    @Test
    public void testRefreshItemsPosition() {

        final HTMLElement element1 = mock(HTMLElement.class);
        final HTMLElement element2 = mock(HTMLElement.class);
        final HTMLElement element3 = mock(HTMLElement.class);
        final CSSStyleDeclaration element1Style = mock(CSSStyleDeclaration.class);
        final CSSStyleDeclaration element2Style = mock(CSSStyleDeclaration.class);
        final CSSStyleDeclaration element3Style = mock(CSSStyleDeclaration.class);

        element1.style = element1Style;
        element2.style = element2Style;
        element3.style = element3Style;
        element1.offsetHeight = 50;

        final NodeList<Element> draggableItems = spy(new NodeList<>());

        doReturn(element1).when(draggableItems).getAt(0);
        doReturn(element2).when(draggableItems).getAt(1);
        doReturn(element3).when(draggableItems).getAt(2);

        when(element1.getAttribute(DATA_POSITION)).thenReturn("0");
        when(element2.getAttribute(DATA_POSITION)).thenReturn("1");
        when(element3.getAttribute(DATA_POSITION)).thenReturn("2");

        draggableItems.length = 3;

        when(dragArea.querySelectorAll(DragAndDropHelper.DRAGGABLE_ITEM_CLASS)).thenReturn(draggableItems);

        helper.refreshItemsPosition();

        verify(element1Style).setProperty(TOP, 0 + PX);
        verify(element2Style).setProperty(TOP, 50 + PX);
        verify(element3Style).setProperty(TOP, 100 + PX);
        verify(addButtonContainerStyle).setProperty(TOP, 150 + PX);
    }

    @Test
    public void testPosition() {

        final HTMLElement element = mock(HTMLElement.class);
        final int expected = 1;

        when(element.getAttribute(DATA_POSITION)).thenReturn(String.valueOf(expected));

        final int actual = helper.position(element);

        assertEquals(expected, actual);
    }

    @Test
    public void testFindElementByPosition() {

        final int position = 1;

        helper.findElementByPosition(position);

        verify(dragArea).querySelector("[" + DATA_POSITION + "=\"" + position + "\"]");
    }

    @Test
    public void testSwapElements() {

        final Element a = mock(Element.class);
        final Element b = mock(Element.class);

        doReturn(2).when(helper).position(a);
        doReturn(1).when(helper).position(b);
        doNothing().when(helper).refreshItemsPosition();

        helper.swapElements(a, b);

        verify(a).setAttribute(DATA_POSITION, 1);
        verify(b).setAttribute(DATA_POSITION, 2);
    }

    @Test
    public void testOnDragAreaMouseDown() {

        final int clientY = 123;
        final int startYPosition = 1;

        final MouseEvent mouseEvent = mock(MouseEvent.class);
        final Element dragGrabber = mock(Element.class);
        final Element target = mock(Element.class);
        final HTMLElement draggable = mock(HTMLElement.class);

        mouseEvent.clientY = clientY;
        mouseEvent.target = target;

        when(target.closest(".drag-grabber")).thenReturn(dragGrabber);
        when(dragGrabber.closest(DragAndDropHelper.DRAGGABLE_ITEM_CLASS)).thenReturn(draggable);
        doReturn(startYPosition).when(helper).getTop(draggable);

        helper.onDragAreaMouseDown(mouseEvent);

        assertEquals(startYPosition, helper.getStartYPosition());
        assertEquals(clientY, helper.getClickedYPosition());
        assertEquals(draggable, helper.getDragging());
    }

    @Test
    public void testOnDragAreaMouseUp() {

        final Event event = mock(Event.class);

        doNothing().when(helper).refreshItemsPosition();

        helper.onDragAreaMouseUp(event);

        assertEquals(null, helper.getDragging());
        assertEquals(0, helper.getClickedYPosition());
        assertEquals(0, helper.getStartYPosition());

        verify(helper).refreshItemsPosition();
    }

    @Test
    public void testGetTop() {

        final HTMLElement element = mock(HTMLElement.class);
        final CSSStyleDeclaration style = mock(CSSStyleDeclaration.class);
        final int expected = 123;

        element.style = style;

        when(style.getPropertyValue(TOP)).thenReturn("123" + PX);

        final int actual = helper.getTop(element);

        assertEquals(expected, actual);
    }

    @Test
    public void testSetTop() {

        final HTMLElement element = mock(HTMLElement.class);
        final CSSStyleDeclaration style = mock(CSSStyleDeclaration.class);
        final int expected = 123;

        element.style = style;

        helper.setTop(element, expected);

        verify(style).setProperty(TOP, expected + PX);
    }

    @Test
    public void testOnDragAreaMouseMove() {

        final HTMLElement dragging = mock(HTMLElement.class);
        final HTMLElement oldElement = mock(HTMLElement.class);
        final MouseEvent event = mock(MouseEvent.class);
        final int newPosition = 2;
        final int oldPosition = 1;
        final int delta = 444;

        doReturn(dragging).when(helper).getDragging();
        doReturn(delta).when(helper).getDelta(event);
        doReturn(newPosition).when(helper).getNewPosition();
        doReturn(oldPosition).when(helper).position(dragging);
        doReturn(oldElement).when(helper).findElementByPosition(newPosition);
        doNothing().when(helper).swapElements(oldElement, dragging);
        doNothing().when(helper).setTop(dragging, delta);

        helper.onDragAreaMouseMove(event);

        verify(helper).swapElements(oldElement, dragging);
        verify(helper).setTop(dragging, delta);
    }

    @Test
    public void testGetNewPositionFirstPosition() {

        final int expected = 1;
        final HTMLElement dragging = mock(HTMLElement.class);
        dragging.offsetHeight = 40;
        doReturn(dragging).when(helper).getDragging();
        doReturn(59).when(helper).getTop(dragging);

        final int actual = helper.getNewPosition();

        assertEquals(expected, actual);
    }

    @Test
    public void testGetNewPositionSecondPosition() {

        final int expected = 2;
        final HTMLElement dragging = mock(HTMLElement.class);
        dragging.offsetHeight = 40;
        doReturn(dragging).when(helper).getDragging();
        doReturn(60).when(helper).getTop(dragging);

        final int actual = helper.getNewPosition();

        assertEquals(expected, actual);
    }

    @Test
    public void testGetDelta() {

        final int clickedYPosition = 20;
        final int mousePosition = 10;
        final int expected = mousePosition - clickedYPosition;
        final MouseEvent mouseEvent = mock(MouseEvent.class);
        mouseEvent.clientY = mousePosition;

        doReturn(clickedYPosition).when(helper).getClickedYPosition();

        final int actual = helper.getDelta(mouseEvent);

        assertEquals(expected, actual);
    }
}
