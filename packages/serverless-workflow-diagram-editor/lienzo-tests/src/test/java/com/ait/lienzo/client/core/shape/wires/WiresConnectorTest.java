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


package com.ait.lienzo.client.core.shape.wires;

import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.MultiPathDecorator;
import com.ait.lienzo.client.core.shape.PolyLine;
import com.ait.lienzo.client.core.shape.wires.handlers.impl.WiresConnectorControlImpl;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.client.core.util.ScratchPad;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class WiresConnectorTest {

    private static final Point2D CP0 = new Point2D(0, 50);
    private static final Point2D CP1 = new Point2D(50, 50);
    private static final Point2D CP2 = new Point2D(100, 50);
    private static final MultiPath headPath = new MultiPath().circle(10);
    private static final MultiPath tailPath = new MultiPath().circle(10);

    @Mock
    private MultiPathDecorator headDecorator;

    @Mock
    private MultiPathDecorator tailDecorator;

    @Mock
    private WiresConnectorControlImpl connectorControl;

    private PolyLine line;
    private WiresConnector tested;

    @Before
    public void setup() {
        when(headDecorator.getPath()).thenReturn(headPath);
        when(tailDecorator.getPath()).thenReturn(tailPath);
        line = spy(new PolyLine(CP0, CP1, CP2));
        tested = new WiresConnector(line,
                                    headDecorator,
                                    tailDecorator);
        tested.setControl(connectorControl);
    }

    // Asset than it handles no magnets set as well, it must not
    // throw any NullPointerException, as magnets are not mandatory to be present.
    @Test
    public void testGetMagnetsHandlesNulls() {
        WiresShape headShape = mock(WiresShape.class);
        WiresShape tailShape = mock(WiresShape.class);
        WiresMagnet[] magnets = tested.getMagnets(headShape, 1, tailShape, 3);
        assertNull(magnets[0]);
        assertNull(magnets[1]);
    }

    @Test
    public void testListen() {
        tested.listen(true);
        assertTrue(tested.isListening());
        tested.listen(false);
        assertFalse(tested.isListening());
    }

    @Test
    public void testDestroy() {
        tested.addToLayer(new Layer());
        assertNotNull(tested.getGroup().getLayer());
        WiresConnection headConnection = mock(WiresConnection.class);
        WiresConnection tailConnection = mock(WiresConnection.class);
        tested.setHeadConnection(headConnection);
        tested.setTailConnection(tailConnection);
        tested.destroy();
        assertNull(tested.getGroup().getLayer());
        verify(headConnection, times(1)).destroy();
        verify(tailConnection, times(1)).destroy();
        verify(connectorControl, times(1)).destroy();
    }

    @Test
    public void testAddControlPoint() {
        final Point2D point = new Point2D(25, 50);
        tested.addControlPoint(point.getX(), point.getY(), 1);
        final Point2DArray points = line.getPoints();
        assertEquals(4, points.size());
        assertEquals(CP0, points.get(0));
        assertEquals(point, points.get(1));
        assertEquals(CP1, points.get(2));
        assertEquals(CP2, points.get(3));
    }

    @Test
    public void testMoveControlPoint() {
        final Point2D point = new Point2D(25, 50);
        tested.moveControlPoint(0, point);
        final Point2DArray points = line.getPoints();
        assertEquals(3, points.size());
        assertEquals(point, points.get(0));
        assertEquals(CP1, points.get(1));
        assertEquals(CP2, points.get(2));
    }

    @Test
    public void testDestroyControlPoint() {
        tested.destroyControlPoints(new int[]{0, 2});
        final Point2DArray points = line.getPoints();
        assertEquals(1, points.size());
        assertEquals(CP1, points.get(0));
    }

    @Test
    public void testGetControlPointIndex() {
        assertEquals(0, tested.getControlPointIndex(CP0.getX(), CP0.getY()));
        assertEquals(1, tested.getControlPointIndex(CP1.getX(), CP1.getY()));
        assertEquals(2, tested.getControlPointIndex(CP2.getX(), CP2.getY()));
    }

    @Test
    public void testGetIndexForSelectedSegment() {
        ScratchPad scratch = mock(ScratchPad.class);
        Context2D context = mock(Context2D.class);
        when(line.getScratchPad()).thenReturn(scratch);
        when(scratch.getContext()).thenReturn(context);

        tested.getIndexForSelectedSegment(75, 50);

        // Prevent controls from breaking when line splicing
        verify(scratch, never()).clear();
    }
}