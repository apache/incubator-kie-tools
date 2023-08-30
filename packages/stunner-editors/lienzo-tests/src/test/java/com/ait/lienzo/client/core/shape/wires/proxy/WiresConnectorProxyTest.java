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


package com.ait.lienzo.client.core.shape.wires.proxy;

import java.util.function.Consumer;

import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.MultiPathDecorator;
import com.ait.lienzo.client.core.shape.PolyLine;
import com.ait.lienzo.client.core.shape.wires.WiresConnection;
import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.shape.wires.WiresLayer;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.handlers.impl.WiresConnectionControlImpl;
import com.ait.lienzo.client.core.shape.wires.handlers.impl.WiresConnectorControlImpl;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class WiresConnectorProxyTest {

    private final Point2D CP0 = new Point2D(0, 50);
    private final Point2D CP1 = new Point2D(50, 50);
    private final Point2D CP2 = new Point2D(100, 50);
    private final MultiPath headPath = new MultiPath().circle(10);
    private final MultiPath tailPath = new MultiPath().circle(10);

    @Mock
    private WiresManager wiresManager;

    @Mock
    private WiresLayer wiresLayer;

    @Mock
    private Consumer<WiresConnector> connectorAcceptor;

    @Mock
    private Consumer<WiresConnector> connectorDestroyer;

    @Mock
    private WiresConnectorControlImpl connectorControl;

    @Mock
    private WiresConnectionControlImpl headConnectionControl;

    @Mock
    private WiresConnection headConnection;

    @Mock
    private WiresConnectionControlImpl tailConnectionControl;

    @Mock
    private WiresConnection tailConnection;

    @Mock
    private MultiPathDecorator headDecorator;

    @Mock
    private MultiPathDecorator tailDecorator;

    private WiresConnectorProxy tested;
    private WiresConnector connector;
    private PolyLine line;
    private Layer layer;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        layer = spy(new Layer());
        when(wiresManager.getLayer()).thenReturn(wiresLayer);
        when(wiresLayer.getLayer()).thenReturn(layer);
        when(headDecorator.getPath()).thenReturn(headPath);
        when(tailDecorator.getPath()).thenReturn(tailPath);
        line = spy(new PolyLine(CP0, CP1, CP2));
        connector = new WiresConnector(line,
                                       headDecorator,
                                       tailDecorator);
        connector.setTailConnection(tailConnection);
        connector.setHeadConnection(headConnection);
        when(headConnection.getConnector()).thenReturn(connector);
        when(tailConnection.getConnector()).thenReturn(connector);
        when(headConnection.getControl()).thenReturn((IPrimitive) headPath);
        when(tailConnection.getControl()).thenReturn((IPrimitive) tailPath);
        connector.setControl(connectorControl);
        layer.add(connector.getGroup());
        when(connectorControl.getHeadConnectionControl()).thenReturn(headConnectionControl);
        when(connectorControl.getTailConnectionControl()).thenReturn(tailConnectionControl);
        tested = new WiresConnectorProxy(wiresManager,
                                         () -> connector,
                                         connectorAcceptor,
                                         connectorDestroyer);
    }

    @Test
    public void testStart() {
        double x = 1.1d;
        double y = 2.3d;
        Point2D location = new Point2D(x, y);
        tested.start(x, y);
        assertTrue(connector.getPointHandles().isVisible());
        verify(connectorControl, times(1)).initHeadConnection();
        verify(connectorControl, times(1)).initTailConnection();
        Point2DArray points = connector.getLine().getPoint2DArray();
        assertEquals(CP0, points.get(0));
        assertEquals(CP1, points.get(1));
        assertEquals(location, points.get(2));
        assertEquals(location, tailPath.getLocation());
        InOrder updateLineAndThenCallControlMethods = Mockito.inOrder(line, tailConnectionControl, layer);
        updateLineAndThenCallControlMethods.verify(line, times(1)).refresh();
        updateLineAndThenCallControlMethods.verify(tailConnectionControl, times(1)).onMoveStart(eq(x), eq(y));
        updateLineAndThenCallControlMethods.verify(layer, atLeastOnce()).batch();
    }

    @Test
    public void testMove() {
        double sx = 1.1d;
        double sy = 2.3d;
        Point2D startLocation = new Point2D(sx, sy);
        double dx = 0.1d;
        double dy = 0.2d;
        Point2D offset = startLocation.copy().offset(dx, dy);
        tested.start(sx, sy);
        tested.move(dx, dy);
        verify(tailConnectionControl, times(1)).onMove(eq(dx), eq(dy));
        Point2DArray points = connector.getLine().getPoint2DArray();
        assertEquals(CP0, points.get(0));
        assertEquals(CP1, points.get(1));
        assertEquals(offset, points.get(2));
        assertEquals(offset, tailPath.getLocation());
        verify(layer, atLeastOnce()).batch();
    }

    @Test
    public void testEndSuccess() {
        double sx = 1.1d;
        double sy = 2.3d;
        double dx = 0.1d;
        double dy = 0.2d;
        when(tailConnectionControl.isAllowed()).thenReturn(true);
        tested.start(sx, sy);
        tested.move(dx, dy);
        tested.end();
        verify(tailConnectionControl, times(1)).onMoveComplete();
        assertFalse(connector.getPointHandles().isVisible());
        verify(connectorAcceptor, times(1)).accept(eq(connector));
        verify(connectorDestroyer, never()).accept(any(WiresConnector.class));
    }

    @Test
    public void testEndFailed() {
        double sx = 1.1d;
        double sy = 2.3d;
        double dx = 0.1d;
        double dy = 0.2d;
        when(tailConnectionControl.isAllowed()).thenReturn(false);
        tested.start(sx, sy);
        tested.move(dx, dy);
        tested.end();
        verify(tailConnectionControl, times(1)).onMoveComplete();
        assertTrue(connector.getPointHandles().isVisible());
        verify(connectorDestroyer, times(1)).accept(eq(connector));
        verify(connectorAcceptor, never()).accept(any(WiresConnector.class));
    }
}
