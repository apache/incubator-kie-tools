/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ait.lienzo.client.core.shape.wires.handlers.impl;

import com.ait.lienzo.client.core.event.NodeDragEndEvent;
import com.ait.lienzo.client.core.event.NodeDragMoveEvent;
import com.ait.lienzo.client.core.event.NodeDragStartEvent;
import com.ait.lienzo.client.core.event.NodeMouseClickEvent;
import com.ait.lienzo.client.core.event.NodeMouseDoubleClickEvent;
import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresConnectorControl;
import com.ait.lienzo.client.core.shape.wires.handlers.impl.WiresConnectorHandlerImpl.Event;
import com.ait.lienzo.client.widget.DragContext;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.ait.tooling.common.api.java.util.function.Consumer;
import com.google.gwt.user.client.Timer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class WiresConnectorHandlerImplTest {

    @Mock
    private WiresManager wiresManager;

    @Mock
    private WiresConnector connector;

    @Mock
    private WiresConnectorControl control;

    @Mock
    private Consumer<Event> clickEventConsumer;

    @Mock
    private Timer clickTimer;

    @Mock
    private Consumer<Event> doubleClickEventConsumer;

    private WiresConnectorHandlerImpl tested;

    @Before
    public void setup() {
        when(connector.getControl()).thenReturn(control);
        tested = new WiresConnectorHandlerImpl(connector,
                                               wiresManager,
                                               clickEventConsumer,
                                               doubleClickEventConsumer,
                                               clickTimer);
    }

    @Test
    public void testOnDragStart() {
        DragContext context = mockDragContext();
        NodeDragStartEvent event = mock(NodeDragStartEvent.class);
        when(event.getDragContext()).thenReturn(context);
        tested.onNodeDragStart(event);
        verify(control, times(1)).onMoveStart(1d, 2d);
        verify(control, never()).onMove(anyDouble(), anyDouble());
        verify(control, never()).onMoveComplete();
    }

    @Test
    public void testOnDragMove() {
        DragContext context = mockDragContext();
        NodeDragMoveEvent event = mock(NodeDragMoveEvent.class);
        when(event.getDragContext()).thenReturn(context);
        tested.onNodeDragMove(event);
        verify(control, times(1)).onMove(1d, 2d);
        verify(control, never()).onMoveStart(anyDouble(), anyDouble());
        verify(control, never()).onMoveComplete();
    }

    @Test
    public void testOnDragEndAccept() {
        DragContext context = mockDragContext();
        NodeDragEndEvent event = mock(NodeDragEndEvent.class);
        when(event.getDragContext()).thenReturn(context);
        when(control.onMoveComplete()).thenReturn(true);
        tested.onNodeDragEnd(event);
        verify(control, times(1)).execute();
        verify(control, times(1)).onMoveComplete();
        verify(control, never()).onMove(anyDouble(), anyDouble());
        verify(control, never()).onMoveStart(anyDouble(), anyDouble());
        verify(control, never()).reset();
    }

    @Test
    public void testOnDragEndReset() {
        DragContext context = mockDragContext();
        NodeDragEndEvent event = mock(NodeDragEndEvent.class);
        when(event.getDragContext()).thenReturn(context);
        when(control.onMoveComplete()).thenReturn(false);
        tested.onNodeDragEnd(event);
        verify(control, times(1)).reset();
        verify(control, times(1)).onMoveComplete();
        verify(control, never()).onMove(anyDouble(), anyDouble());
        verify(control, never()).onMoveStart(anyDouble(), anyDouble());
        verify(control, never()).execute();
    }

    @Test
    public void testOnNodeMouseClick() {
        when(clickTimer.isRunning()).thenReturn(true);
        NodeMouseClickEvent event = mock(NodeMouseClickEvent.class);
        tested.onNodeMouseClick(event);
        verify(clickTimer, times(1)).cancel();
        verify(clickTimer, times(1)).schedule(anyInt());
        verify(doubleClickEventConsumer, never()).accept(any(Event.class));
    }

    @Test
    public void testOnNodeMouseDoubleClick() {
        NodeMouseDoubleClickEvent event = mock(NodeMouseDoubleClickEvent.class);
        tested.onNodeMouseDoubleClick(event);
        verify(doubleClickEventConsumer, times(1)).accept(any(Event.class));
        verify(clickEventConsumer, never()).accept(any(Event.class));
    }

    private static DragContext mockDragContext() {
        DragContext context = mock(DragContext.class);
        when(context.getDragStartX()).thenReturn(1);
        when(context.getDragStartY()).thenReturn(2);
        return context;
    }

}