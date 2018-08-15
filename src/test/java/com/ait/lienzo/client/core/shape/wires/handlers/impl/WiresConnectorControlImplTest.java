/*
 *
 *    Copyright (c) 2018 Ahome' Innovation Technologies. All rights reserved.
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
package com.ait.lienzo.client.core.shape.wires.handlers.impl;

import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.MultiPathDecorator;
import com.ait.lienzo.client.core.shape.PolyLine;
import com.ait.lienzo.client.core.shape.wires.IControlHandleList;
import com.ait.lienzo.client.core.shape.wires.IControlPointsAcceptor;
import com.ait.lienzo.client.core.shape.wires.MagnetManager;
import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.shape.wires.WiresLayer;
import com.ait.lienzo.client.core.shape.wires.WiresMagnet;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.types.DragBounds;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.client.widget.DefaultDragConstraintEnforcer;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyDouble;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class WiresConnectorControlImplTest {

    private static final Point2D CP0_INIT = new Point2D(0, 50);
    private static final Point2D CP1_INIT = new Point2D(50, 50);
    private static final Point2D CP2_INIT = new Point2D(100, 50);
    private static final Point2D CP3_INIT = new Point2D(150, 50);
    private static final Point2D CP4_INIT = new Point2D(200, 50);
    private static final MultiPath headPath = new MultiPath().circle(10);
    private static final MultiPath tailPath = new MultiPath().circle(10);
    private static final MultiPath shapePath = new MultiPath().rect(CP0_INIT.getX(), CP0_INIT.getY(), CP4_INIT.getX(), CP4_INIT.getY());
    private static final DragBounds DRAG_BOUNDS = new DragBounds(0, 0, 1000, 1000);

    @Mock
    private WiresLayer wiresLayer;

    @Mock
    private MultiPathDecorator headDecorator;

    @Mock
    private MultiPathDecorator tailDecorator;

    @Mock
    private WiresMagnet headMagnet;

    @Mock
    private WiresMagnet tailMagnet;

    private WiresManager wiresManager;
    private Layer layer;
    private PolyLine line;
    private WiresConnector connector;
    private Point2D CP0;
    private Point2D CP1;
    private Point2D CP2;
    private Point2D CP3;
    private Point2D CP4;

    private WiresConnectorControlImpl tested;

    @Mock
    private IPrimitive headPrimitive;

    @Mock
    private IPrimitive tailPrimitive;

    @Mock
    private MagnetManager.Magnets magnets;

    @Mock
    private WiresShape shape;

    @Before
    public void setup() {
        CP0 = new Point2D(CP0_INIT);
        CP1 = new Point2D(CP1_INIT);
        CP2 = new Point2D(CP2_INIT);
        CP3 = new Point2D(CP3_INIT);
        CP4 = new Point2D(CP4_INIT);
        layer = new Layer();
        wiresManager = WiresManager.get(layer);
        wiresManager.setControlPointsAcceptor(IControlPointsAcceptor.ALL);
        when(wiresLayer.getLayer()).thenReturn(layer);
        when(headDecorator.getPath()).thenReturn(headPath);
        when(tailDecorator.getPath()).thenReturn(tailPath);
        when(headMagnet.getControl()).thenReturn(headPrimitive);
        when(tailMagnet.getControl()).thenReturn(tailPrimitive);
        when(headPrimitive.getComputedLocation()).thenReturn(CP0);
        when(tailPrimitive.getComputedLocation()).thenReturn(CP4);
        when(headMagnet.getMagnets()).thenReturn(magnets);
        when(tailMagnet.getMagnets()).thenReturn(magnets);
        when(headMagnet.getIndex()).thenReturn(1);
        when(tailMagnet.getIndex()).thenReturn(1);
        when(magnets.getWiresShape()).thenReturn(shape);
        when(shape.getPath()).thenReturn(shapePath);

        line = spy(new PolyLine(CP0, CP1, CP2, CP3, CP4));

        connector = spy(new WiresConnector(headMagnet, tailMagnet, line, headDecorator, tailDecorator));
        connector.addToLayer(layer);
        connector.getGroup().setDragBounds(DRAG_BOUNDS);
        tested = new WiresConnectorControlImpl(connector, wiresManager);
    }

    @Test
    public void testShowPointHandles() {
        tested.showPointHandles();
        IControlHandleList pointHandles = connector.getPointHandles();
        assertEquals(pointHandles.getHandle(0).getControl().getDragBounds(), DRAG_BOUNDS);
        assertEquals(pointHandles.getHandle(1).getControl().getDragBounds(), DRAG_BOUNDS);
        assertEquals(pointHandles.getHandle(2).getControl().getDragBounds(), DRAG_BOUNDS);
        assertEquals(pointHandles.getHandle(3).getControl().getDragBounds(), DRAG_BOUNDS);
        assertEquals(pointHandles.getHandle(4).getControl().getDragBounds(), DRAG_BOUNDS);
        assertTrue(pointHandles.getHandle(0).getControl().getDragConstraints() instanceof WiresConnectorControlImpl.ConnectionHandler);
        assertTrue(pointHandles.getHandle(1).getControl().getDragConstraints() instanceof DefaultDragConstraintEnforcer);
        assertTrue(pointHandles.getHandle(2).getControl().getDragConstraints() instanceof DefaultDragConstraintEnforcer);
        assertTrue(pointHandles.getHandle(3).getControl().getDragConstraints() instanceof DefaultDragConstraintEnforcer);
        assertTrue(pointHandles.getHandle(4).getControl().getDragConstraints() instanceof WiresConnectorControlImpl.ConnectionHandler);
    }

    @Test
    public void testAddControlPoint() {
        final Point2D point = new Point2D(25, 50);
        tested.addControlPoint(point.getX(), point.getY(), 1);
        verify(connector, times(1)).addControlPoint(point.getX(), point.getY(), 1);
    }

    @Test
    public void testAddControlPointNotAccept() {
        wiresManager.setControlPointsAcceptor(IControlPointsAcceptor.NONE);
        tested.addControlPoint(11, 22, 1);
        verify(connector, never()).addControlPoint(anyDouble(), anyDouble(), anyInt());
    }

    @Test
    public void testAddControlPointAlreadyExist() {
        assertEquals(0, tested.addControlPoint(CP0.getX(), CP0.getY()));
        assertEquals(1, tested.addControlPoint(CP1.getX(), CP1.getY()));
        assertEquals(2, tested.addControlPoint(CP2.getX(), CP2.getY()));
        verify(connector, never()).addControlPoint(anyDouble(), anyDouble(), anyInt());
    }

    @Test
    public void testMoveControlPoint() {
        Point2D p0 = new Point2D(1, 50);
        Point2D p1 = new Point2D(51, 50);
        Point2D p2 = new Point2D(101, 50);
        Point2D p3 = new Point2D(151, 50);
        Point2D p4 = new Point2D(201, 50);
        boolean moved1 = tested.moveControlPoint(0, p0);
        boolean moved2 = tested.moveControlPoint(1, p1);
        boolean moved3 = tested.moveControlPoint(2, p2);
        boolean moved4 = tested.moveControlPoint(3, p3);
        boolean moved5 = tested.moveControlPoint(4, p4);
        assertTrue(moved1);
        assertTrue(moved2);
        assertTrue(moved3);
        assertTrue(moved4);
        assertTrue(moved5);
        assertEquals(CP0_INIT, CP0);
        assertEquals(p1, CP1);
        assertEquals(p2, CP2);
        assertEquals(p3, CP3);
        assertEquals(CP4_INIT, CP4);
        verify(connector, never()).moveControlPoint(eq(1), eq(p0));
        verify(connector, times(1)).moveControlPoint(eq(1), eq(p1));
        verify(connector, times(1)).moveControlPoint(eq(2), eq(p2));
        verify(connector, times(1)).moveControlPoint(eq(3), eq(p3));
        verify(connector, never()).moveControlPoint(eq(1), eq(p4));
    }

    @Test
    public void testMoveControlPointFailed() {
        wiresManager.setControlPointsAcceptor(IControlPointsAcceptor.NONE);
        Point2D p0 = new Point2D(1, 50);
        Point2D p1 = new Point2D(51, 50);
        Point2D p2 = new Point2D(101, 50);
        Point2D p3 = new Point2D(151, 50);
        Point2D p4 = new Point2D(201, 50);
        boolean moved1 = tested.moveControlPoint(0, p0);
        boolean moved2 = tested.moveControlPoint(1, p1);
        boolean moved3 = tested.moveControlPoint(2, p2);
        boolean moved4 = tested.moveControlPoint(3, p3);
        boolean moved5 = tested.moveControlPoint(4, p4);
        assertTrue(moved1);
        assertFalse(moved2);
        assertFalse(moved3);
        assertFalse(moved4);
        assertTrue(moved5);
        assertEquals(CP0_INIT, CP0);
        assertEquals(CP1_INIT, CP1);
        assertEquals(CP2_INIT, CP2);
        assertEquals(CP3_INIT, CP3);
        assertEquals(CP4_INIT, CP4);
        verify(connector, never()).moveControlPoint(anyInt(), any(Point2D.class));
    }

    @Test
    public void testDestroyControlPoint() {
        tested.destroyControlPoint(1);
        verify(connector, times(1)).destroyControlPoints(new int[]{1});
    }

    @Test
    public void testDestroyControlPointFailed() {
        wiresManager.setControlPointsAcceptor(IControlPointsAcceptor.NONE);
        tested.destroyControlPoint(1);
        verify(connector, never()).destroyControlPoints((int[]) any());
    }

    @Test
    public void testMove() {
        tested.onMoveStart(0, 0);
        tested.onMove(5, 0);
        tested.onMoveComplete();
        IControlHandleList pointHandles = connector.getPointHandles();

        assertEquals(CP0_INIT.getX(), pointHandles.getHandle(0).getControl().getX(), 0);
        assertEquals(CP0_INIT.getY(), pointHandles.getHandle(0).getControl().getY(), 0);

        assertEquals(CP1_INIT.getX() + 5, pointHandles.getHandle(1).getControl().getX(), 0);
        assertEquals(CP1_INIT.getY(), pointHandles.getHandle(1).getControl().getY(), 0);

        assertEquals(CP2_INIT.getX() + 5, pointHandles.getHandle(2).getControl().getX(), 0);
        assertEquals(CP2_INIT.getY(), pointHandles.getHandle(2).getControl().getY(), 0);

        assertEquals(CP3_INIT.getX() + 5, pointHandles.getHandle(3).getControl().getX(), 0);
        assertEquals(CP3_INIT.getY(), pointHandles.getHandle(3).getControl().getY(), 0);

        assertEquals(CP4_INIT.getX(), pointHandles.getHandle(4).getControl().getX(), 0);
        assertEquals(CP4_INIT.getY(), pointHandles.getHandle(4).getControl().getY(), 0);
    }

    @Test
    public void testReset() {
        tested.onMoveStart(50, 50);
        tested.onMove(5, 0);
        tested.onMoveComplete();
        tested.reset();
        IControlHandleList pointHandles = connector.getPointHandles();
        assertEquals(0, pointHandles.getHandle(0).getControl().getX(), 0);
        assertEquals(50, pointHandles.getHandle(0).getControl().getY(), 0);
        assertEquals(50, pointHandles.getHandle(1).getControl().getX(), 0);
        assertEquals(50, pointHandles.getHandle(1).getControl().getY(), 0);
        assertEquals(100, pointHandles.getHandle(2).getControl().getX(), 0);
        assertEquals(50, pointHandles.getHandle(2).getControl().getY(), 0);
    }

    @Test
    public void testClear() {
        tested.onMoveStart(50, 50);
        tested.clear();
        IControlHandleList pointHandles = connector.getPointHandles();
        assertEquals(0, pointHandles.getHandle(0).getControl().getX(), 0);
        assertEquals(50, pointHandles.getHandle(0).getControl().getY(), 0);
        assertEquals(50, pointHandles.getHandle(1).getControl().getX(), 0);
        assertEquals(50, pointHandles.getHandle(1).getControl().getY(), 0);
        assertEquals(100, pointHandles.getHandle(2).getControl().getX(), 0);
        assertEquals(50, pointHandles.getHandle(2).getControl().getY(), 0);
    }

    @Test
    public void testExecute() {
        IControlPointsAcceptor acceptor = mock(IControlPointsAcceptor.class);
        wiresManager.setControlPointsAcceptor(acceptor);
        ArgumentCaptor<Point2DArray> pointCaptor = ArgumentCaptor.forClass(Point2DArray.class);
        tested.execute();
        verify(acceptor, times(1)).move(eq(connector),
                                        pointCaptor.capture());
        Point2DArray points = pointCaptor.getValue();
        assertEquals(CP0, points.get(0));
        assertEquals(CP1, points.get(1));
        assertEquals(CP2, points.get(2));
    }
}