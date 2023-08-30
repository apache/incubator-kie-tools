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

import com.ait.lienzo.client.core.shape.IDirectionalMultiPointShape;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.MultiPathDecorator;
import com.ait.lienzo.client.core.shape.PolyLine;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.shared.core.types.ArrowEnd;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// TODO: Merge last changes from Kirill in this test class.

@RunWith(LienzoMockitoTestRunner.class)
public class WiresConnectionTest {

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
    private Layer layer;

    private WiresConnection tested;

    private IDirectionalMultiPointShape<?> line;

    @Before
    public void setup() {
        when(headDecorator.getPath()).thenReturn(headPath);
        when(tailDecorator.getPath()).thenReturn(tailPath);

        line = spy(new PolyLine(CP0, CP1, CP2));
        WiresConnector connector = new WiresConnector(line,
                                                      headDecorator,
                                                      tailDecorator);
        tested = spy(new WiresConnection(connector, new MultiPath().circle(15), ArrowEnd.HEAD));
    }

    @Test
    public void testDestroy() {
        WiresMagnet magnet = mock(WiresMagnet.class);
        tested.m_magnet = magnet;
        tested.destroy();
        assertNull(tested.getMagnet());
        verify(magnet, never()).destroy();
    }

    @Test
    public void testMoveNoBatch() {
        when(line.getLayer()).thenReturn(layer);
        tested.move(10d, 10d);
        verify(line.getLayer(), never()).batch();
    }
}
