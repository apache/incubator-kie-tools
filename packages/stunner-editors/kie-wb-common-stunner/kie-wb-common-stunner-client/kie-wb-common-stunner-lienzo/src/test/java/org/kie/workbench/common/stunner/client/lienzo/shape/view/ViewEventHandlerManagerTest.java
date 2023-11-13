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


package org.kie.workbench.common.stunner.client.lienzo.shape.view;

import com.ait.lienzo.client.core.event.NodeMouseClickEvent;
import com.ait.lienzo.client.core.event.NodeMouseClickHandler;
import com.ait.lienzo.client.core.event.NodeMouseDoubleClickEvent;
import com.ait.lienzo.client.core.event.NodeMouseDoubleClickHandler;
import com.ait.lienzo.client.core.event.NodeMouseEnterEvent;
import com.ait.lienzo.client.core.event.NodeMouseEnterHandler;
import com.ait.lienzo.client.core.event.NodeMouseExitEvent;
import com.ait.lienzo.client.core.event.NodeMouseExitHandler;
import com.ait.lienzo.client.core.shape.Node;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.ait.lienzo.tools.client.event.HandlerRegistration;
import elemental2.dom.MouseEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.shape.view.event.MouseClickEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.event.MouseDoubleClickEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.event.MouseEnterEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.event.MouseExitEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ShapeViewSupportedEvents;
import org.kie.workbench.common.stunner.core.client.shape.view.event.TextDoubleClickEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewEventType;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewHandler;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.uberfire.mvp.Command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class ViewEventHandlerManagerTest {

    @Mock
    private Node<?> node;

    @Mock
    private Shape shape;

    private ViewEventHandlerManager tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        when(shape.setListening(anyBoolean())).thenReturn(shape);
        when(shape.setFillBoundsForSelection(anyBoolean())).thenReturn(shape);
        this.tested = new ViewEventHandlerManager(node,
                                                  shape,
                                                  new ViewEventHandlerManager.GWTTimer(1) {
                                                      @Override
                                                      public void run(Command callback) {
                                                          callback.execute();
                                                      }
                                                  },
                                                  ShapeViewSupportedEvents.ALL_EVENT_TYPES);
    }

    @Test
    public void testEnabledByDefault() {
        verify(shape,
               times(1)).setListening(eq(true));
    }

    @Test
    public void testEnable() {
        tested.enable();
        verify(shape,
               times(2)).setListening(eq(true));
    }

    @Test
    public void testDisable() {
        tested.disable();
        verify(shape,
               times(1)).setListening(eq(false));
    }

    @Test
    public void testSupportedEventTypes() {
        this.tested = new ViewEventHandlerManager(node,
                                                  shape,
                                                  ViewEventType.MOUSE_CLICK,
                                                  ViewEventType.MOUSE_DBL_CLICK,
                                                  ViewEventType.TEXT_DBL_CLICK);
        assertTrue(tested.supports(ViewEventType.MOUSE_CLICK));
        assertTrue(tested.supports(ViewEventType.MOUSE_DBL_CLICK));
        assertTrue(tested.supports(ViewEventType.TEXT_DBL_CLICK));
        assertFalse(tested.supports(ViewEventType.TEXT_CLICK));
        assertFalse(tested.supports(ViewEventType.TEXT_ENTER));
        assertFalse(tested.supports(ViewEventType.TEXT_EXIT));
        assertFalse(tested.supports(ViewEventType.MOUSE_ENTER));
        assertFalse(tested.supports(ViewEventType.DRAG));
        assertFalse(tested.supports(ViewEventType.MOUSE_EXIT));
        assertFalse(tested.supports(ViewEventType.MOUSE_MOVE));
        assertFalse(tested.supports(ViewEventType.RESIZE));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testClickHandler() {
        final ViewHandler<ViewEvent> clickHandler = mock(ViewHandler.class);
        final HandlerRegistration handlerRegistration = mock(HandlerRegistration.class);
        when(node.addNodeMouseClickHandler(any(NodeMouseClickHandler.class))).thenReturn(handlerRegistration);
        tested.addHandler(ViewEventType.MOUSE_CLICK,
                          clickHandler);
        final ArgumentCaptor<NodeMouseClickHandler> clickHandlerArgumentCaptor =
                ArgumentCaptor.forClass(NodeMouseClickHandler.class);
        verify(node,
               times(1)).addNodeMouseClickHandler(clickHandlerArgumentCaptor.capture());
        final NodeMouseClickHandler nodeCLickHandler = clickHandlerArgumentCaptor.getValue();
        final NodeMouseClickEvent clickEvent = mock(NodeMouseClickEvent.class);
        final MouseEvent mouseEvent = mock(MouseEvent.class);
        final int x = 102;
        final int y = 410;
        when(clickEvent.getX()).thenReturn(x);
        when(clickEvent.getY()).thenReturn(y);
        when(clickEvent.isButtonLeft()).thenReturn(true);
        when(clickEvent.isButtonRight()).thenReturn(true);
        when(clickEvent.isButtonMiddle()).thenReturn(true);
        when(clickEvent.isShiftKeyDown()).thenReturn(true);
        when(clickEvent.isAltKeyDown()).thenReturn(true);
        when(clickEvent.isMetaKeyDown()).thenReturn(true);
        when(clickEvent.getNativeEvent()).thenReturn(mouseEvent);
        mouseEvent.clientX = x;
        mouseEvent.clientY = y;

        nodeCLickHandler.onNodeMouseClick(clickEvent);
        final ArgumentCaptor<ViewEvent> eventArgumentCaptor =
                ArgumentCaptor.forClass(ViewEvent.class);
        verify(clickHandler,
               times(1)).handle(eventArgumentCaptor.capture());
        final MouseClickEvent viewEvent = (MouseClickEvent) eventArgumentCaptor.getValue();
        assertEquals(x,
                     viewEvent.getX(),
                     0d);
        assertEquals(y,
                     viewEvent.getY(),
                     0d);
        assertEquals(x,
                     viewEvent.getClientX(),
                     0d);
        assertEquals(y,
                     viewEvent.getClientY(),
                     0d);
        assertTrue(viewEvent.isButtonLeft());
        assertTrue(viewEvent.isButtonRight());
        assertTrue(viewEvent.isButtonMiddle());
        assertTrue(viewEvent.isAltKeyDown());
        assertTrue(viewEvent.isMetaKeyDown());
        assertTrue(viewEvent.isShiftKeyDown());
        assertNotNull(tested.getRegistrationsByType().get(ViewEventType.MOUSE_CLICK));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDoubleClickHandler() {
        final ViewHandler<ViewEvent> clickHandler = mock(ViewHandler.class);
        final HandlerRegistration handlerRegistration = mock(HandlerRegistration.class);
        when(node.addNodeMouseDoubleClickHandler(any(NodeMouseDoubleClickHandler.class))).thenReturn(handlerRegistration);
        tested.addHandler(ViewEventType.MOUSE_DBL_CLICK,
                          clickHandler);
        final ArgumentCaptor<NodeMouseDoubleClickHandler> clickHandlerArgumentCaptor =
                ArgumentCaptor.forClass(NodeMouseDoubleClickHandler.class);
        verify(node,
               times(1)).addNodeMouseDoubleClickHandler(clickHandlerArgumentCaptor.capture());
        final NodeMouseDoubleClickHandler nodeCLickHandler = clickHandlerArgumentCaptor.getValue();
        final NodeMouseDoubleClickEvent clickEvent = mock(NodeMouseDoubleClickEvent.class);
        final MouseEvent mouseEvent = mock(MouseEvent.class);
        final int x = 102;
        final int y = 410;
        when(clickEvent.getX()).thenReturn(x);
        when(clickEvent.getY()).thenReturn(y);
        when(clickEvent.isButtonLeft()).thenReturn(true);
        when(clickEvent.isButtonRight()).thenReturn(true);
        when(clickEvent.isButtonMiddle()).thenReturn(true);
        when(clickEvent.isShiftKeyDown()).thenReturn(true);
        when(clickEvent.isAltKeyDown()).thenReturn(true);
        when(clickEvent.isMetaKeyDown()).thenReturn(true);
        when(clickEvent.getNativeEvent()).thenReturn(mouseEvent);
        mouseEvent.clientX = x;
        mouseEvent.clientY = y;
        nodeCLickHandler.onNodeMouseDoubleClick(clickEvent);
        final ArgumentCaptor<ViewEvent> eventArgumentCaptor =
                ArgumentCaptor.forClass(ViewEvent.class);
        verify(clickHandler,
               times(1)).handle(eventArgumentCaptor.capture());
        final MouseDoubleClickEvent viewEvent = (MouseDoubleClickEvent) eventArgumentCaptor.getValue();
        assertEquals(x,
                     viewEvent.getX(),
                     0d);
        assertEquals(y,
                     viewEvent.getY(),
                     0d);
        assertEquals(x,
                     viewEvent.getClientX(),
                     0d);
        assertEquals(y,
                     viewEvent.getClientY(),
                     0d);
        assertTrue(viewEvent.isButtonLeft());
        assertTrue(viewEvent.isButtonRight());
        assertTrue(viewEvent.isButtonMiddle());
        assertTrue(viewEvent.isAltKeyDown());
        assertTrue(viewEvent.isMetaKeyDown());
        assertTrue(viewEvent.isShiftKeyDown());
        assertNotNull(tested.getRegistrationsByType().get(ViewEventType.MOUSE_DBL_CLICK));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testTextDoubleClickHandler() {
        final ViewHandler<ViewEvent> clickHandler = mock(ViewHandler.class);
        final HandlerRegistration handlerRegistration = mock(HandlerRegistration.class);
        when(node.addNodeMouseDoubleClickHandler(any(NodeMouseDoubleClickHandler.class))).thenReturn(handlerRegistration);
        tested.addHandler(ViewEventType.TEXT_DBL_CLICK,
                          clickHandler);
        final ArgumentCaptor<NodeMouseDoubleClickHandler> clickHandlerArgumentCaptor =
                ArgumentCaptor.forClass(NodeMouseDoubleClickHandler.class);
        verify(node,
               times(1)).addNodeMouseDoubleClickHandler(clickHandlerArgumentCaptor.capture());
        final NodeMouseDoubleClickHandler nodeCLickHandler = clickHandlerArgumentCaptor.getValue();
        final NodeMouseDoubleClickEvent clickEvent = mock(NodeMouseDoubleClickEvent.class);
        final MouseEvent mouseEvent = mock(MouseEvent.class);
        final int x = 102;
        final int y = 410;
        when(clickEvent.getX()).thenReturn(x);
        when(clickEvent.getY()).thenReturn(y);
        when(clickEvent.isShiftKeyDown()).thenReturn(true);
        when(clickEvent.isAltKeyDown()).thenReturn(true);
        when(clickEvent.isMetaKeyDown()).thenReturn(true);
        when(clickEvent.getNativeEvent()).thenReturn(mouseEvent);
        mouseEvent.clientX = x;
        mouseEvent.clientY = y;
        nodeCLickHandler.onNodeMouseDoubleClick(clickEvent);
        final ArgumentCaptor<ViewEvent> eventArgumentCaptor =
                ArgumentCaptor.forClass(ViewEvent.class);
        verify(clickHandler,
               times(1)).handle(eventArgumentCaptor.capture());
        final TextDoubleClickEvent viewEvent = (TextDoubleClickEvent) eventArgumentCaptor.getValue();
        assertEquals(x,
                     viewEvent.getX(),
                     0d);
        assertEquals(y,
                     viewEvent.getY(),
                     0d);
        assertEquals(x,
                     viewEvent.getClientX(),
                     0d);
        assertEquals(y,
                     viewEvent.getClientY(),
                     0d);
        assertTrue(viewEvent.isAltKeyDown());
        assertTrue(viewEvent.isMetaKeyDown());
        assertTrue(viewEvent.isShiftKeyDown());
        assertNotNull(tested.getRegistrationsByType().get(ViewEventType.TEXT_DBL_CLICK));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMouseEnterHandler() {
        final ViewHandler<ViewEvent> clickHandler = mock(ViewHandler.class);
        final HandlerRegistration handlerRegistration = mock(HandlerRegistration.class);
        when(shape.addNodeMouseEnterHandler(any(NodeMouseEnterHandler.class))).thenReturn(handlerRegistration);
        tested.addHandler(ViewEventType.MOUSE_ENTER,
                          clickHandler);
        final ArgumentCaptor<NodeMouseEnterHandler> clickHandlerArgumentCaptor =
                ArgumentCaptor.forClass(NodeMouseEnterHandler.class);
        verify(shape,
               times(1)).addNodeMouseEnterHandler(clickHandlerArgumentCaptor.capture());
        final NodeMouseEnterHandler nodeCLickHandler = clickHandlerArgumentCaptor.getValue();
        final NodeMouseEnterEvent clickEvent = mock(NodeMouseEnterEvent.class);
        final MouseEvent mouseEvent = mock(MouseEvent.class);
        final int x = 102;
        final int y = 410;
        when(clickEvent.getX()).thenReturn(x);
        when(clickEvent.getY()).thenReturn(y);
        when(clickEvent.isShiftKeyDown()).thenReturn(true);
        when(clickEvent.isAltKeyDown()).thenReturn(true);
        when(clickEvent.isMetaKeyDown()).thenReturn(true);
        when(clickEvent.getNativeEvent()).thenReturn(mouseEvent);
        mouseEvent.clientX = x;
        mouseEvent.clientY = y;
        nodeCLickHandler.onNodeMouseEnter(clickEvent);
        final ArgumentCaptor<ViewEvent> eventArgumentCaptor =
                ArgumentCaptor.forClass(ViewEvent.class);
        verify(clickHandler,
               times(1)).handle(eventArgumentCaptor.capture());
        final MouseEnterEvent viewEvent = (MouseEnterEvent) eventArgumentCaptor.getValue();
        assertEquals(x,
                     viewEvent.getX(),
                     0d);
        assertEquals(y,
                     viewEvent.getY(),
                     0d);
        assertEquals(x,
                     viewEvent.getClientX(),
                     0d);
        assertEquals(y,
                     viewEvent.getClientY(),
                     0d);
        assertTrue(viewEvent.isAltKeyDown());
        assertTrue(viewEvent.isMetaKeyDown());
        assertTrue(viewEvent.isShiftKeyDown());
        assertNotNull(tested.getRegistrationsByType().get(ViewEventType.MOUSE_ENTER));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMouseEXitHandler() {
        final ViewHandler<ViewEvent> clickHandler = mock(ViewHandler.class);
        final HandlerRegistration handlerRegistration = mock(HandlerRegistration.class);
        when(shape.addNodeMouseExitHandler(any(NodeMouseExitHandler.class))).thenReturn(handlerRegistration);
        tested.addHandler(ViewEventType.MOUSE_EXIT,
                          clickHandler);
        final ArgumentCaptor<NodeMouseExitHandler> clickHandlerArgumentCaptor =
                ArgumentCaptor.forClass(NodeMouseExitHandler.class);
        verify(shape,
               times(1)).addNodeMouseExitHandler(clickHandlerArgumentCaptor.capture());
        final NodeMouseExitHandler nodeCLickHandler = clickHandlerArgumentCaptor.getValue();
        final NodeMouseExitEvent clickEvent = mock(NodeMouseExitEvent.class);
        final MouseEvent mouseEvent = mock(MouseEvent.class);
        final int x = 102;
        final int y = 410;
        when(clickEvent.getX()).thenReturn(x);
        when(clickEvent.getY()).thenReturn(y);
        when(clickEvent.isShiftKeyDown()).thenReturn(true);
        when(clickEvent.isAltKeyDown()).thenReturn(true);
        when(clickEvent.isMetaKeyDown()).thenReturn(true);
        when(clickEvent.getNativeEvent()).thenReturn(mouseEvent);
        mouseEvent.clientX = x;
        mouseEvent.clientY = y;
        nodeCLickHandler.onNodeMouseExit(clickEvent);
        final ArgumentCaptor<ViewEvent> eventArgumentCaptor =
                ArgumentCaptor.forClass(ViewEvent.class);
        verify(clickHandler,
               times(1)).handle(eventArgumentCaptor.capture());
        final MouseExitEvent viewEvent = (MouseExitEvent) eventArgumentCaptor.getValue();
        assertEquals(x,
                     viewEvent.getX(),
                     0d);
        assertEquals(y,
                     viewEvent.getY(),
                     0d);
        assertEquals(x,
                     viewEvent.getClientX(),
                     0d);
        assertEquals(y,
                     viewEvent.getClientY(),
                     0d);
        assertTrue(viewEvent.isAltKeyDown());
        assertTrue(viewEvent.isMetaKeyDown());
        assertTrue(viewEvent.isShiftKeyDown());
        assertNotNull(tested.getRegistrationsByType().get(ViewEventType.MOUSE_EXIT));
    }
}
