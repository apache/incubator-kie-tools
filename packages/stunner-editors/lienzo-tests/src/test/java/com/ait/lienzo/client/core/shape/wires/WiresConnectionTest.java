/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package com.ait.lienzo.client.core.shape.wires;

import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.MultiPathDecorator;
import com.ait.lienzo.client.core.shape.PolyLine;
import com.ait.lienzo.client.core.shape.PolyMorphicLine;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.shared.core.types.ArrowEnd;
import com.ait.lienzo.shared.core.types.Direction;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    private WiresMagnet magnet;

    @Mock
    private IPrimitive aPrimitive;

    private WiresConnection tested;

    @Before
    public void setup() {
        when(headDecorator.getPath()).thenReturn(headPath);
        when(tailDecorator.getPath()).thenReturn(tailPath);
        when(magnet.getControl()).thenReturn(aPrimitive);
        when(aPrimitive.getComputedLocation()).thenReturn(new Point2D(5d, 10d));
    }

    private void createPolyLineConnection() {
        WiresConnector connector = new WiresConnector(new PolyLine(CP0, CP1, CP2),
                                                      headDecorator,
                                                      tailDecorator);
        tested = new WiresConnection(connector, new MultiPath().circle(15), ArrowEnd.HEAD);
    }

    private void createPolyMorphicLineConnection() {
        WiresConnector connector = new WiresConnector(new PolyMorphicLine(CP0, CP1, CP2),
                                                      headDecorator,
                                                      tailDecorator);
        tested = new WiresConnection(connector, new MultiPath().circle(15), ArrowEnd.HEAD);
    }

    @Test
    public void testDestroy() {
        createPolyLineConnection();

        tested.m_magnet = magnet;
        tested.destroy();
        assertNull(tested.getMagnet());
        verify(magnet, never()).destroy();
    }

    @Test
    public void testSetMagnetPolyLineHead() {
        createPolyLineConnection();

        when(magnet.getDirection()).thenReturn(Direction.NORTH);

        tested.setEnd(ArrowEnd.HEAD);
        tested.m_magnet = null;
        tested.setMagnet(magnet);

        assertEquals(magnet, tested.getMagnet());
        assertEquals(Direction.NORTH, tested.getLine().getHeadDirection());
        assertNull(tested.getLine().getTailDirection());
    }

    @Test
    public void testSetMagnetPolyLineTail() {
        createPolyLineConnection();

        when(magnet.getDirection()).thenReturn(Direction.SOUTH);

        tested.setEnd(ArrowEnd.TAIL);
        tested.m_magnet = null;
        tested.setMagnet(magnet);

        assertEquals(magnet, tested.getMagnet());
        assertEquals(Direction.SOUTH, tested.getLine().getTailDirection());
        assertNull(tested.getLine().getHeadDirection());
    }

    @Test
    public void testSetMagnetHeadMagnetNull() {
        createPolyLineConnection();

        tested.setEnd(ArrowEnd.HEAD);
        tested.m_magnet = null;
        tested.setMagnet(null);

        assertEquals(Direction.NONE, tested.getLine().getHeadDirection());
        assertNull(tested.getLine().getTailDirection());
    }

    @Test
    public void testSetMagnetTailMagnetNull() {
        createPolyLineConnection();

        tested.setEnd(ArrowEnd.TAIL);
        tested.m_magnet = null;
        tested.setMagnet(null);

        assertEquals(Direction.NONE, tested.getLine().getTailDirection());
        assertNull(tested.getLine().getHeadDirection());
    }

    @Test
    public void testSetMagnetPolyMorphicLineHead() {
        createPolyMorphicLineConnection();

        when(magnet.getIndex()).thenReturn(1);
        when(magnet.getDirection()).thenReturn(Direction.WEST);

        tested.setEnd(ArrowEnd.HEAD);
        tested.m_magnet = null;
        tested.setMagnet(magnet);

        assertEquals(magnet, tested.getMagnet());
        assertEquals(Direction.WEST, tested.getLine().getHeadDirection());
        assertEquals(Direction.NONE, tested.getLine().getTailDirection());
        assertTrue(((PolyMorphicLine) tested.getLine()).isFirstSegmentOrthogonal());
    }

    @Test
    public void testSetMagnetPolyMorphicLineTail() {
        createPolyMorphicLineConnection();

        when(magnet.getIndex()).thenReturn(1);
        when(magnet.getDirection()).thenReturn(Direction.SOUTH);

        tested.setEnd(ArrowEnd.TAIL);
        tested.m_magnet = null;
        tested.setMagnet(magnet);

        assertEquals(magnet, tested.getMagnet());
        assertEquals(Direction.NONE, tested.getLine().getHeadDirection());
        assertEquals(Direction.SOUTH, tested.getLine().getTailDirection());
        assertTrue(((PolyMorphicLine) tested.getLine()).isLastSegmentOrthogonal());
    }

    @Test
    public void testSetMagnetPolyMorphicLineHeadCenterMagnet() {
        createPolyMorphicLineConnection();

        when(magnet.getIndex()).thenReturn(0); //center
        when(magnet.getDirection()).thenReturn(Direction.WEST);

        tested.setEnd(ArrowEnd.HEAD);
        tested.m_magnet = null;
        tested.setMagnet(magnet);

        assertEquals(magnet, tested.getMagnet());
        assertEquals(Direction.WEST, tested.getLine().getHeadDirection());
        assertEquals(Direction.NONE, tested.getLine().getTailDirection());
        assertFalse(((PolyMorphicLine) tested.getLine()).isFirstSegmentOrthogonal());
    }

    @Test
    public void testSetMagnetPolyMorphicLineTailCenterMagnet() {
        createPolyMorphicLineConnection();

        when(magnet.getIndex()).thenReturn(0); //center
        when(magnet.getDirection()).thenReturn(Direction.SOUTH);

        tested.setEnd(ArrowEnd.TAIL);
        tested.m_magnet = null;
        tested.setMagnet(magnet);

        assertEquals(magnet, tested.getMagnet());
        assertEquals(Direction.NONE, tested.getLine().getHeadDirection());
        assertEquals(Direction.SOUTH, tested.getLine().getTailDirection());
        assertFalse(((PolyMorphicLine) tested.getLine()).isLastSegmentOrthogonal());
    }
}
