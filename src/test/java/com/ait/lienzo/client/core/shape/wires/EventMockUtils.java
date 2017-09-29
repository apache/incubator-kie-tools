/*
 *
 *    Copyright (c) 2017 Ahome' Innovation Technologies. All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package com.ait.lienzo.client.core.shape.wires;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.ait.lienzo.client.core.event.INodeXYEvent;
import com.ait.lienzo.client.core.event.NodeDragEndEvent;
import com.ait.lienzo.client.core.event.NodeDragMoveEvent;
import com.ait.lienzo.client.core.event.NodeDragStartEvent;
import com.ait.lienzo.client.core.event.NodeMouseClickEvent;
import com.ait.lienzo.client.core.event.NodeMouseDoubleClickEvent;
import com.ait.lienzo.client.core.event.NodeMouseDownEvent;
import com.ait.lienzo.client.core.event.NodeMouseEnterEvent;
import com.ait.lienzo.client.core.event.NodeMouseExitEvent;
import com.ait.lienzo.client.core.event.NodeMouseMoveEvent;
import com.ait.lienzo.client.core.event.NodeMouseOutEvent;
import com.ait.lienzo.client.core.event.NodeMouseOverEvent;
import com.ait.lienzo.client.core.event.NodeMouseUpEvent;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.widget.DragContext;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;

/**
 * Utils for mocking and firing events on nodes / shapes.
 * Note: Lienzo events cannot be mocked as each one contains the dispatch logic.
 */
public class EventMockUtils
{
    public static void click(final IPrimitive<?> node, final double x, final double y)
    {
        final ClickEvent clickEvent = mock(ClickEvent.class);
        setUpMouseEvent(clickEvent, x, y);
        node.fireEvent(new NodeMouseClickEvent(clickEvent));
    }

    public static void dblClick(final IPrimitive<?> node, final double x, final double y)
    {
        final DoubleClickEvent clickEvent = mock(DoubleClickEvent.class);
        setUpMouseEvent(clickEvent, x, y);
        node.fireEvent(new NodeMouseDoubleClickEvent(clickEvent));
    }

    public static void dragStart(final IPrimitive<?> node, final double x, final double y)
    {
        final DragContext dragContext = createDragContext(node, x, y);
        node.fireEvent(new NodeDragStartEvent(dragContext));
    }

    public static void dragMove(final IPrimitive<?> node, final double x, final double y)
    {
        final DragContext dragContext = createDragContext(node, x, y);
        node.fireEvent(new NodeDragMoveEvent(dragContext));
    }

    public static void dragEnd(final IPrimitive<?> node, final double x, final double y)
    {
        final DragContext dragContext = createDragContext(node, x, y);
        node.fireEvent(new NodeDragEndEvent(dragContext));
    }

    public static void mouseMove(final IPrimitive<?> node, final double x, final double y)
    {
        final MouseMoveEvent event = mock(MouseMoveEvent.class);
        setUpMouseEvent(event, x, y);
        node.fireEvent(new NodeMouseMoveEvent(event));
    }

    public static void mouseEnter(final IPrimitive<?> node, final double x, final double y)
    {
        final MouseMoveEvent event = mock(MouseMoveEvent.class);
        setUpMouseEvent(event, x, y);
        node.fireEvent(new NodeMouseEnterEvent(event, (int) x, (int) y));
    }

    public static void mouseOut(final IPrimitive<?> node, final double x, final double y)
    {
        final MouseOutEvent event = mock(MouseOutEvent.class);
        setUpMouseEvent(event, x, y);
        node.fireEvent(new NodeMouseOutEvent(event));
    }

    public static void mouseDown(final IPrimitive<?> node, final double x, final double y)
    {
        final MouseDownEvent event = mock(MouseDownEvent.class);
        setUpMouseEvent(event, x, y);
        node.fireEvent(new NodeMouseDownEvent(event));
    }

    public static void mouseUp(final IPrimitive<?> node, final double x, final double y)
    {
        final MouseUpEvent event = mock(MouseUpEvent.class);
        setUpMouseEvent(event, x, y);
        node.fireEvent(new NodeMouseUpEvent(event));
    }

    public static void mouseOver(final IPrimitive<?> node, final double x, final double y)
    {
        final MouseOverEvent event = mock(MouseOverEvent.class);
        setUpMouseEvent(event, x, y);
        node.fireEvent(new NodeMouseOverEvent(event));
    }

    public static void mouseExit(final IPrimitive<?> node, final double x, final double y)
    {
        final MouseOverEvent event = mock(MouseOverEvent.class);
        setUpMouseEvent(event, x, y);
        node.fireEvent(new NodeMouseExitEvent(event, (int) x, (int) y));
    }

    private static void setUpMouseEvent(final MouseEvent<?> mouseEvent, final double x, final double y)
    {
        final int _x = (int) x;
        final int _y = (int) y;
        when(mouseEvent.getX()).thenReturn(_x);
        when(mouseEvent.getClientX()).thenReturn(_x);
        when(mouseEvent.getRelativeX(any(Element.class))).thenReturn(_x);
        when(mouseEvent.getRelativeY(any(Element.class))).thenReturn(_y);
        when(mouseEvent.getY()).thenReturn(_y);
        when(mouseEvent.getClientY()).thenReturn(_y);
        when(mouseEvent.isAltKeyDown()).thenReturn(false);
        when(mouseEvent.isControlKeyDown()).thenReturn(false);
        when(mouseEvent.isMetaKeyDown()).thenReturn(false);
        when(mouseEvent.isShiftKeyDown()).thenReturn(false);
    }

    private static DragContext createDragContext(final IPrimitive<?> node, final double x, final double y)
    {
        final int _x = (int) x;
        final int _y = (int) y;

        /*DragConstraintEnforcer dragConstraintEnforcer = mock(DragConstraintEnforcer.class);
        DragContext dragContext = mock(DragContext.class);
        when(dragContext.getDragConstraints()).thenReturn(dragConstraintEnforcer);
        when(dragContext.getNode()).thenReturn(node);
        when(dragContext.getDragStartX()).thenReturn(_x);
        when(dragContext.getDragStartY()).thenReturn(_y);
        when(dragContext.getEventX()).thenReturn(_x);
        when(dragContext.getEventY()).thenReturn(_y);
        when(dragContext.getDx()).thenReturn(_x);
        when(dragContext.getDy()).thenReturn(_y);
        return dragContext;*/

        final INodeXYEvent iNodeXYEvent = mock(INodeXYEvent.class);
        when(iNodeXYEvent.getX()).thenReturn(_x);
        when(iNodeXYEvent.getY()).thenReturn(_y);
        when(iNodeXYEvent.isAlive()).thenReturn(true);
        return new DragContext(iNodeXYEvent, node);
    }
}
