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

import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.MultiPathDecorator;
import com.ait.lienzo.client.core.shape.PolyLine;
import com.ait.lienzo.client.core.shape.wires.IControlHandleList;
import com.ait.lienzo.client.core.shape.wires.IControlPointsAcceptor;
import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.shape.wires.WiresLayer;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
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

    private static final MultiPath headPath = new MultiPath().circle(10);
    private static final MultiPath tailPath = new MultiPath().circle(10);

    @Mock
    private WiresLayer wiresLayer;

    @Mock
    private MultiPathDecorator headDecorator;

    @Mock
    private MultiPathDecorator tailDecorator;

    private WiresManager wiresManager;
    private Layer layer;
    private PolyLine line;
    private WiresConnector connector;
    private Point2D CP0 = new Point2D(0, 50);
    private Point2D CP1 = new Point2D(50, 50);
    private Point2D CP2 = new Point2D(100, 50);

    private WiresConnectorControlImpl tested;

    @Before
    public void setup() {
        layer = new Layer();
        wiresManager = WiresManager.get(layer);
        wiresManager.setControlPointsAcceptor(IControlPointsAcceptor.ALL);
        when(wiresLayer.getLayer()).thenReturn(layer);
        when(headDecorator.getPath()).thenReturn(headPath);
        when(tailDecorator.getPath()).thenReturn(tailPath);
        CP0 = new Point2D(0, 50);
        CP1 = new Point2D(50, 50);
        CP2 = new Point2D(100, 50);
        line = new PolyLine(CP0, CP1, CP2);
        connector = spy(new WiresConnector(line,
                                           headDecorator,
                                           tailDecorator));
        connector.addToLayer(layer);
        tested = new WiresConnectorControlImpl(connector, wiresManager);
    }

    @Test
    public void testAddControlPoint() {
        final Point2D point = new Point2D(25, 50);
        tested.addControlPoint(point.getX(), point.getY(), 1);
        ;
        verify(connector, times(1)).addControlPoint(point.getX(), point.getY(), 1);
    }

    @Test
    public void testAddControlPointNotAccept() {
        wiresManager.setControlPointsAcceptor(IControlPointsAcceptor.NONE);
        tested.addControlPoint(11, 22, 1);
        ;
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
        boolean moved1 = tested.moveControlPoint(0, p0);
        boolean moved2 = tested.moveControlPoint(1, p1);
        boolean moved3 = tested.moveControlPoint(2, p2);
        assertTrue(moved1);
        assertEquals(new Point2D(0, 50), CP0);
        assertTrue(moved2);
        assertEquals(p1, CP1);
        assertTrue(moved3);
        assertEquals(new Point2D(100, 50), CP2);
        verify(connector, times(1)).moveControlPoint(eq(1),
                                                     eq(p1));
    }

    @Test
    public void testMoveControlPointFailed() {
        wiresManager.setControlPointsAcceptor(IControlPointsAcceptor.NONE);
        Point2D p0 = new Point2D(1, 50);
        Point2D p1 = new Point2D(51, 50);
        Point2D p2 = new Point2D(101, 50);
        boolean moved1 = tested.moveControlPoint(0, p0);
        boolean moved2 = tested.moveControlPoint(1, p1);
        boolean moved3 = tested.moveControlPoint(2, p2);
        assertTrue(moved1);
        assertEquals(new Point2D(0, 50), CP0);
        assertFalse(moved2);
        assertEquals(new Point2D(50, 50), CP1);
        assertTrue(moved3);
        assertEquals(new Point2D(100, 50), CP2);
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
        tested.onMoveStart(50, 50);
        tested.onMove(5, 0);
        tested.onMoveComplete();
        IControlHandleList pointHandles = connector.getPointHandles();
        assertEquals(5, pointHandles.getHandle(0).getControl().getX(), 0);
        assertEquals(50, pointHandles.getHandle(0).getControl().getY(), 0);
        assertEquals(55, pointHandles.getHandle(1).getControl().getX(), 0);
        assertEquals(50, pointHandles.getHandle(1).getControl().getY(), 0);
        assertEquals(105, pointHandles.getHandle(2).getControl().getX(), 0);
        assertEquals(50, pointHandles.getHandle(2).getControl().getY(), 0);
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
        Point2DArray poitns = pointCaptor.getValue();
        assertEquals(CP0, poitns.get(0));
        assertEquals(CP1, poitns.get(1));
        assertEquals(CP2, poitns.get(2));
    }
}