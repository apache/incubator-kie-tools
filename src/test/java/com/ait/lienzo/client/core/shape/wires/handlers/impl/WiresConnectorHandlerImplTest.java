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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.ait.lienzo.client.core.event.NodeDragEndEvent;
import com.ait.lienzo.client.core.event.NodeDragMoveEvent;
import com.ait.lienzo.client.core.event.NodeDragStartEvent;
import com.ait.lienzo.client.core.event.NodeMouseClickEvent;
import com.ait.lienzo.client.core.event.NodeMouseMoveEvent;
import com.ait.lienzo.client.core.shape.Circle;
import com.ait.lienzo.client.core.shape.IDirectionalMultiPointShape;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.shape.wires.SelectionManager;
import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.shape.wires.WiresLayer;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.decorator.PointHandleDecorator;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresConnectorControl;
import com.ait.lienzo.client.core.shape.wires.handlers.impl.WiresConnectorHandlerImpl.Event;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.BoundingPoints;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.client.widget.DragContext;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.ait.tooling.common.api.java.util.function.Consumer;
import com.google.gwt.user.client.Timer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.Whitebox;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
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
    private WiresConnectorControl control;

    @Mock
    private Consumer<Event> clickEventConsumer;

    @Mock
    private Timer clickTimer;

    @Mock
    private Consumer<Event> mouseDownConsumer;

    private WiresConnectorHandlerImpl tested;

    @Mock
    private PointHandleDecorator pointHandleDecorator;

    @Mock
    private IDirectionalMultiPointShape line;

    @Mock
    private Layer layer;

    private Point2DArray linePoints;

    @Mock
    private SelectionManager selectionManager;

    @Mock
    private SelectionManager.SelectedItems selectedItems;

    private BoundingBox boundingBox;

    @Mock
    private WiresLayer wiresLayer;

    private Shape<?> transientPoint;

    @Captor
    private ArgumentCaptor<Consumer> createConsumer;

    @Mock
    private NodeMouseMoveEvent moveEvent;

    @Before
    public void setup() {
        linePoints = new Point2DArray(new Point2D(0, 0), new Point2D(100, 100));
        boundingBox = new BoundingBox(0, 0, 100, 100);
        transientPoint = spy(new Circle(1));
        when(connector.getControl()).thenReturn(control);
        when(control.areControlPointsVisible()).thenReturn(true);
        when(control.createTransientControlHandle(any(Consumer.class))).thenReturn(transientPoint);
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
        when(layer.uuid()).thenReturn(LAYER_ID);
        tested = new WiresConnectorHandlerImpl(connector,
                                               wiresManager,
                                               clickEventConsumer,
                                               mouseDownConsumer,
                                               clickTimer);

        //clear token that controls concurrency
        Map<String, Boolean> transientControlHandleTokenMap = (HashMap<String, Boolean>) Whitebox.getInternalState(tested, "transientControlHandleTokenMap");
        transientControlHandleTokenMap.clear();
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
        verify(mouseDownConsumer, never()).accept(any(Event.class));
    }

    @Test
    public void testMoveAndCreateControlPoint() {
        when(moveEvent.getX()).thenReturn(20);
        when(moveEvent.getY()).thenReturn(20);

        tested.onNodeMouseMove(moveEvent);
        verify(control).showControlPoints();
        verify(control).createTransientControlHandle(createConsumer.capture());
        verify(transientPoint).setX(20);
        verify(transientPoint).setY(20);
        verify(control, never()).destroyTransientControlHandle();
    }

    @Test
    public void testMoveOutsideConnectorBoundary() {
        when(moveEvent.getX()).thenReturn(101);
        when(moveEvent.getY()).thenReturn(20);

        tested.onNodeMouseMove(moveEvent);
        verify(control, never()).showControlPoints();
        verify(control, never()).createTransientControlHandle(createConsumer.capture());
        verify(transientPoint, never()).setX(anyDouble());
        verify(transientPoint, never()).setY(anyDouble());
        verify(control).destroyTransientControlHandle();
    }

    @Test
    public void testMoveClosestPointIsFarFromConnector() {
        when(moveEvent.getX()).thenReturn(50);
        when(moveEvent.getY()).thenReturn(1);

        tested.onNodeMouseMove(moveEvent);
        verify(control).showControlPoints();
        verify(control, never()).createTransientControlHandle(createConsumer.capture());
        verify(transientPoint, never()).setX(anyDouble());
        verify(transientPoint, never()).setY(anyDouble());
        verify(control).destroyTransientControlHandle();
    }

    @Test
    public void testMoveTransientPointOverlapping() {
        Point2D overlapPoint = linePoints.get(0);
        when(moveEvent.getX()).thenReturn((int) overlapPoint.getX());
        when(moveEvent.getY()).thenReturn((int) overlapPoint.getY());

        tested.onNodeMouseMove(moveEvent);
        verify(control).showControlPoints();
        verify(control, never()).createTransientControlHandle(createConsumer.capture());
        verify(transientPoint, never()).setX(anyDouble());
        verify(transientPoint, never()).setY(anyDouble());
        verify(control).destroyTransientControlHandle();
    }

    @Test
    public void testMoveNonControlPointShape() {
        when(line.isControlPointShape()).thenReturn(false);
        when(moveEvent.getX()).thenReturn(20);
        when(moveEvent.getY()).thenReturn(20);

        tested.onNodeMouseMove(moveEvent);
        verify(control, never()).showControlPoints();
        verify(control, never()).createTransientControlHandle(createConsumer.capture());
        verify(transientPoint, never()).setX(anyDouble());
        verify(transientPoint, never()).setY(anyDouble());
        verify(control, never()).destroyTransientControlHandle();
    }

    @Test
    public void testMoveWithConcurrency() {

        WiresConnectorHandlerImpl tested2 = new WiresConnectorHandlerImpl(connector,
                                                                          wiresManager,
                                                                          clickEventConsumer,
                                                                          mouseDownConsumer,
                                                                          clickTimer);

        when(moveEvent.getX()).thenReturn(20);
        when(moveEvent.getY()).thenReturn(20);

        //tested2 get token
        tested2.onNodeMouseMove(moveEvent);
        verify(control).showControlPoints();
        verify(control).createTransientControlHandle(createConsumer.capture());
        verify(transientPoint).setX(20);
        verify(transientPoint).setY(20);
        verify(control, never()).destroyTransientControlHandle();

        //reseting the mocks to clean verify
        reset(control);
        reset(transientPoint);

        //tested is not able to get token
        tested.onNodeMouseMove(moveEvent);
        verify(control, never()).showControlPoints();
        verify(control, never()).createTransientControlHandle(createConsumer.capture());
        verify(transientPoint, never()).setX(anyDouble());
        verify(transientPoint, never()).setY(anyDouble());
        verify(control).destroyTransientControlHandle();
    }

    private static DragContext mockDragContext() {
        DragContext context = mock(DragContext.class);
        when(context.getDragStartX()).thenReturn(1);
        when(context.getDragStartY()).thenReturn(2);
        return context;
    }
}