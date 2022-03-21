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

import com.ait.lienzo.client.core.event.AbstractNodeHumanInputEvent;
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
import elemental2.dom.HTMLElement;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/**
 * Utils for mocking and firing events on nodes / shapes.
 * Note: Lienzo events cannot be mocked as each one contains the dispatch logic.
 */
public class EventMockUtils {

    public static void click(final IPrimitive<?> node, final double x, final double y) {
        final HTMLElement clickEvent = mock(HTMLElement.class);
        NodeMouseClickEvent event = spy(new NodeMouseClickEvent(clickEvent));
        setUpMouseEvent(event, x, y);
        node.fireEvent(new NodeMouseClickEvent(clickEvent));
    }

    public static void dblClick(final IPrimitive<?> node, final double x, final double y) {

        final HTMLElement clickEvent = mock(HTMLElement.class);
        NodeMouseDoubleClickEvent event = spy(new NodeMouseDoubleClickEvent(clickEvent));
        setUpMouseEvent(event, x, y);
        node.fireEvent(new NodeMouseDoubleClickEvent(clickEvent));
    }

    public static void dragStart(final IPrimitive<?> node, final double x, final double y) {
        final HTMLElement dragEvent = mock(HTMLElement.class);
        NodeDragStartEvent nodeDragStartEvent = spy(new NodeDragStartEvent(dragEvent));
        createDragContext(nodeDragStartEvent, x, y);
        node.fireEvent(nodeDragStartEvent);
    }

    public static void dragMove(final IPrimitive<?> node, final double x, final double y) {
        final HTMLElement dragEvent = mock(HTMLElement.class);
        NodeDragMoveEvent nodeDragStartEvent = spy(new NodeDragMoveEvent(dragEvent));
        createDragContext(nodeDragStartEvent, x, y);
        node.fireEvent(nodeDragStartEvent);
    }

    public static void dragEnd(final IPrimitive<?> node, final double x, final double y) {
        final HTMLElement dragEvent = mock(HTMLElement.class);
        NodeDragEndEvent nodeDragStartEvent = spy(new NodeDragEndEvent(dragEvent));
        createDragContext(nodeDragStartEvent, x, y);
        node.fireEvent(nodeDragStartEvent);
    }

    public static void mouseMove(final IPrimitive<?> node, final double x, final double y) {

        final HTMLElement clickEvent = mock(HTMLElement.class);
        NodeMouseMoveEvent event = spy(new NodeMouseMoveEvent(clickEvent));
        setUpMouseEvent(event, x, y);
        node.fireEvent(new NodeMouseMoveEvent(clickEvent));
    }

    public static void mouseEnter(final IPrimitive<?> node, final double x, final double y) {
        final HTMLElement clickEvent = mock(HTMLElement.class);
        NodeMouseMoveEvent event = spy(new NodeMouseMoveEvent(clickEvent));
        setUpMouseEvent(event, x, y);
        node.fireEvent(new NodeMouseEnterEvent(clickEvent));
    }

    public static void mouseOut(final IPrimitive<?> node, final double x, final double y) {
        final HTMLElement clickEvent = mock(HTMLElement.class);
        NodeMouseMoveEvent event = spy(new NodeMouseMoveEvent(clickEvent));
        setUpMouseEvent(event, x, y);
        node.fireEvent(new NodeMouseOutEvent(clickEvent));
    }

    public static void mouseDown(final IPrimitive<?> node, final double x, final double y) {
        final HTMLElement clickEvent = mock(HTMLElement.class);
        NodeMouseDownEvent event = spy(new NodeMouseDownEvent(clickEvent));
        setUpMouseEvent(event, x, y);
        node.fireEvent(new NodeMouseDownEvent(clickEvent));
    }

    public static void mouseUp(final IPrimitive<?> node, final double x, final double y) {
        final HTMLElement clickEvent = mock(HTMLElement.class);
        NodeMouseUpEvent event = spy(new NodeMouseUpEvent(clickEvent));
        setUpMouseEvent(event, x, y);
        node.fireEvent(new NodeMouseUpEvent(clickEvent));
    }

    public static void mouseOver(final IPrimitive<?> node, final double x, final double y) {
        final HTMLElement clickEvent = mock(HTMLElement.class);
        NodeMouseOverEvent event = spy(new NodeMouseOverEvent(clickEvent));
        setUpMouseEvent(event, x, y);
        node.fireEvent(new NodeMouseOverEvent(clickEvent));
    }

    public static void mouseExit(final IPrimitive<?> node, final double x, final double y) {
        final HTMLElement clickEvent = mock(HTMLElement.class);
        NodeMouseOverEvent event = spy(new NodeMouseOverEvent(clickEvent));
        setUpMouseEvent(event, x, y);
        node.fireEvent(new NodeMouseExitEvent(clickEvent));
    }

    private static void setUpMouseEvent(final AbstractNodeHumanInputEvent mouseEvent, final double x, final double y) {
        final int _x = (int) x;
        final int _y = (int) y;

        when(mouseEvent.getX()).thenReturn(_x);
        when(mouseEvent.getY()).thenReturn(_y);
        when(mouseEvent.isAltKeyDown()).thenReturn(false);
        when(mouseEvent.isCtrlKeyDown()).thenReturn(false);
        when(mouseEvent.isMetaKeyDown()).thenReturn(false);
        when(mouseEvent.isShiftKeyDown()).thenReturn(false);
    }

    private static AbstractNodeHumanInputEvent createDragContext(final AbstractNodeHumanInputEvent node, final double x, final double y) {
        final int _x = (int) x;
        final int _y = (int) y;

        when(node.getX()).thenReturn(_x);
        when(node.getY()).thenReturn(_y);
        when(node.isAlive()).thenReturn(true);

        return node;
    }
}
