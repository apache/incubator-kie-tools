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

package com.ait.lienzo.client.core.shape.wires.handlers.impl;

import java.util.HashSet;
import java.util.function.Consumer;

import com.ait.lienzo.client.core.event.NodeDragEndEvent;
import com.ait.lienzo.client.core.event.NodeDragMoveEvent;
import com.ait.lienzo.client.core.event.NodeDragStartEvent;
import com.ait.lienzo.client.core.event.NodeMouseClickEvent;
import com.ait.lienzo.client.core.event.NodeMouseDownEvent;
import com.ait.lienzo.client.core.event.NodeMouseEnterEvent;
import com.ait.lienzo.client.core.event.NodeMouseExitEvent;
import com.ait.lienzo.client.core.event.NodeMouseMoveEvent;
import com.ait.lienzo.client.core.shape.IDirectionalMultiPointShape;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.shape.wires.SelectionManager;
import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.shape.wires.WiresLayer;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.decorator.PointHandleDecorator;
import com.ait.lienzo.client.core.shape.wires.handlers.impl.WiresConnectorHandlerImpl.Event;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.BoundingPoints;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.client.widget.DragContext;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.ait.lienzo.tools.client.Timer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class WiresConnectorHandlerImplTest {

    private static final String LAYER_ID = "LAYER_ID";

    @Mock
    private WiresManager wiresManager;

    @Mock
    private WiresConnector connector;

    @Mock
    private WiresConnectorControlImpl control;

    @Mock
    private Consumer<Event> clickEventConsumer;

    @Mock
    private Consumer<Event> mouseDownConsumer;

    @Mock
    private WiresConnectorControlPointBuilder controlPointBuilder;

    @Mock
    private PointHandleDecorator pointHandleDecorator;

    @Mock
    private IDirectionalMultiPointShape line;

    @Mock
    private Layer layer;

    @Mock
    private Viewport viewPort;

    @Mock
    private SelectionManager selectionManager;

    @Mock
    private SelectionManager.SelectedItems selectedItems;

    @Mock
    private WiresLayer wiresLayer;

    @Mock
    private NodeMouseMoveEvent moveEvent;

    private WiresConnectorHandlerImpl tested;

    private Transform transform;

    @Before
    public void setup() {
        when(layer.uuid()).thenReturn(LAYER_ID);
        when(layer.getViewport()).thenReturn(viewPort);
        transform = new Transform();
        when(viewPort.getTransform()).thenReturn(transform);
        Point2DArray linePoints = Point2DArray.fromArrayOfPoint2D(new Point2D(0, 0), new Point2D(100, 100));
        BoundingBox boundingBox = BoundingBox.fromDoubles(0, 0, 100, 100);
        when(connector.getControl()).thenReturn(control);
        when(control.getControlPointBuilder()).thenReturn(controlPointBuilder);
        when(control.areControlPointsVisible()).thenReturn(true);
        when(control.getPointHandleDecorator()).thenReturn(pointHandleDecorator);
        when(connector.getLine()).thenReturn(line);
        when(line.getLayer()).thenReturn(layer);
        when(line.getPoint2DArray()).thenReturn(linePoints);
        when(line.getComputedBoundingPoints()).thenReturn(new BoundingPoints(boundingBox));
        when(line.isControlPointShape()).thenReturn(true);
        when(wiresManager.getSelectionManager()).thenReturn(selectionManager);
        when(selectionManager.getSelectedItems()).thenReturn(selectedItems);
        when(selectedItems.getConnectors()).thenReturn(new HashSet<WiresConnector>());
        when(wiresManager.getLayer()).thenReturn(wiresLayer);
        when(wiresLayer.getLayer()).thenReturn(layer);
        tested = new WiresConnectorHandlerImpl(connector,
                                               wiresManager,
                                               clickEventConsumer,
                                               mouseDownConsumer);
    }

    @Test
    public void testOnDragStart() {
        DragContext context = mockDragContext();
        NodeDragStartEvent event = mock(NodeDragStartEvent.class);
        when(event.getDragContext()).thenReturn(context);
        tested.onNodeDragStart(event);
        verify(control, times(1)).hideControlPoints();
        verify(control, never()).showControlPoints();
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
        when(control.onMove(anyDouble(), anyDouble())).thenReturn(false);
        when(control.accept()).thenReturn(true);
        tested.onNodeDragEnd(event);

        verify(control, times(1)).execute();
        verify(control, times(1)).onMoveComplete();
        verify(control, times(1)).onMove(anyDouble(), anyDouble());
        verify(control, never()).onMoveStart(anyDouble(), anyDouble());
        verify(control, never()).reset();
    }

    @Test
    public void testOnDragEndReset() {
        DragContext context = mockDragContext();
        NodeDragEndEvent event = mock(NodeDragEndEvent.class);
        when(event.getDragContext()).thenReturn(context);
        when(control.onMove(anyDouble(), anyDouble())).thenReturn(false);
        tested.onNodeDragEnd(event);

        verify(control, times(1)).reset();
        verify(control, times(1)).onMove(anyDouble(), anyDouble());
        verify(control, never()).onMoveComplete();
        verify(control, never()).onMoveStart(anyDouble(), anyDouble());
        verify(control, never()).execute();
    }

    @Test
    public void testOnNodeMouseClick() {
        Timer mouseDownTimer = mock(Timer.class);
        when(mouseDownTimer.isRunning()).thenReturn(true);
        tested.mouseDownTimer = mouseDownTimer;
        NodeMouseClickEvent event = mock(NodeMouseClickEvent.class);
        when(event.getX()).thenReturn(120);
        when(event.getY()).thenReturn(454);
        transform.scaleWithXY(2, 4);
        tested.onNodeMouseClick(event);
        verify(mouseDownTimer, times(1)).cancel();
        verify(mouseDownTimer, never()).run();
        verify(mouseDownTimer, never()).schedule(anyInt());
        verify(mouseDownConsumer, never()).accept(any(Event.class));
        ArgumentCaptor<Event> clickEventCaptor = ArgumentCaptor.forClass(Event.class);
        verify(clickEventConsumer, times(1)).accept(clickEventCaptor.capture());
        Event clickEvent = clickEventCaptor.getValue();
        assertEquals(60d, clickEvent.getX(), 0d);
        assertEquals(113.5d, clickEvent.getY(), 0d);
        assertFalse(clickEvent.isShiftKeyDown);
    }

    @Test
    public void testSelectionManagerIsNull() {
        // no assert intentionally, before the fix this tests failed with NullPointer Exception
        when(wiresManager.getSelectionManager()).thenReturn(null);
        tested.onNodeMouseMove(moveEvent);
    }

    @Test
    public void testOnNodeMouseEnter() {
        NodeMouseEnterEvent enterEvent = mock(NodeMouseEnterEvent.class);
        tested.onNodeMouseEnter(enterEvent);
        verify(controlPointBuilder, times(1)).enable();
    }

    @Test
    public void testOnNodeMouseExit() {
        NodeMouseExitEvent event = mock(NodeMouseExitEvent.class);
        tested.onNodeMouseExit(event);
        verify(controlPointBuilder, times(1)).disable();
    }

    @Test
    public void testOnNodeMouseDown() {
        NodeMouseDownEvent event = mock(NodeMouseDownEvent.class);
        when(event.getX()).thenReturn(55);
        when(event.getY()).thenReturn(234);
        transform.scale(2);
        tested.onNodeMouseDown(event);
        verify(controlPointBuilder, times(1)).scheduleControlPointBuildAnimation(eq(WiresConnectorHandlerImpl.MOUSE_DOWN_TIMER_DELAY));
        tested.mouseDownTimer.run();
        verify(controlPointBuilder, times(1)).createControlPointAt(eq(55), eq(234));
        ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
        verify(mouseDownConsumer, times(1)).accept(eventCaptor.capture());
        Event downEvent = eventCaptor.getValue();
        assertEquals(27.5d, downEvent.getX(), 0d);
        assertEquals(117d, downEvent.getY(), 0d);
        assertFalse(downEvent.isShiftKeyDown);
    }

    @Test
    public void testMoveAndCreateControlPoint() {
        Timer mouseDownTimer = mock(Timer.class);
        when(mouseDownTimer.isRunning()).thenReturn(true);
        tested.mouseDownTimer = mouseDownTimer;
        when(moveEvent.getX()).thenReturn(321);
        when(moveEvent.getY()).thenReturn(435);
        tested.onNodeMouseMove(moveEvent);
        verify(mouseDownTimer, times(1)).run();
        verify(mouseDownTimer, never()).cancel();
        verify(mouseDownTimer, never()).schedule(anyInt());
        verify(controlPointBuilder, times(1)).moveControlPointTo(eq(321), eq(435));
    }

    private static DragContext mockDragContext() {
        DragContext context = mock(DragContext.class);
        when(context.getDragStartX()).thenReturn(1);
        when(context.getDragStartY()).thenReturn(2);
        return context;
    }
}