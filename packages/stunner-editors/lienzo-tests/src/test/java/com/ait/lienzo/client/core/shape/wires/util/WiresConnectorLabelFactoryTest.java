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


package com.ait.lienzo.client.core.shape.wires.util;

import java.util.function.BiConsumer;

import com.ait.lienzo.client.core.shape.IDirectionalMultiPointShape;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.client.core.shape.TextBoundsWrap;
import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.shape.wires.util.WiresConnectorLabelFactory.Segment;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class WiresConnectorLabelFactoryTest {

    @Mock
    private WiresConnector connector;

    @Mock
    private IDirectionalMultiPointShape line;

    private Text text;
    private Point2DArray points;

    @Before
    @SuppressWarnings("unchecked")
    public void init() {
        text = new Text("Doing some testing here");
        points = Point2DArray.fromArrayOfPoint2D(new Point2D(0d, 0d), new Point2D(12.4d, 65.3d), new Point2D(312.4d, 432.d));
        when(connector.getLine()).thenReturn(line);
        when(line.getPoint2DArray()).thenReturn(points);
    }

    @Test
    public void testSegmentLabelExecutor() {
        Point2D start = new Point2D(10d, 10d);
        Point2D end = new Point2D(20d, 30.5d);
        WiresConnectorLabelFactory.SegmentLabelExecutor executor = new WiresConnectorLabelFactory.SegmentLabelExecutor();
        Segment segment = new Segment(0, start, end);
        executor.consumer().accept(segment, text);

        assertNotNull(text.getWrapper());
        TextBoundsWrap wrapper = (TextBoundsWrap) text.getWrapper();
        assertEquals(BoundingBox.fromDoubles(0d, 0d, 22.808989455914087d, 11d), wrapper.getWrapBoundaries());
        assertEquals(new Point2D(18.987684456439d, 15.865763679785857d), text.getLocation());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testFirstSegmentLabelExecutor() {
        final boolean[] delegated = new boolean[]{false};
        BiConsumer<Segment, Text> delegate = (segment, text) -> {
            assertEquals(0, segment.getIndex());
            assertEquals(66.4669090600729d, segment.getLength(), 0d);
            assertEquals(-4.900047462313102d, segment.getTetha(), 0d);
            assertEquals(new Point2D(0d, 0d), segment.getStart());
            assertEquals(new Point2D(12.4d, 65.3d), segment.getEnd());
            delegated[0] = true;
        };
        WiresConnectorLabelFactory.FirstSegmentLabelExecutor executor = new WiresConnectorLabelFactory.FirstSegmentLabelExecutor(delegate);
        executor.consumer().accept(connector, text);
        assertTrue(delegated[0]);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testLongestSegmentLabelExecutor() {
        final boolean[] delegated = new boolean[]{false};
        BiConsumer<Segment, Text> delegate = (segment, text) -> {
            assertEquals(1, segment.getIndex());
            assertEquals(473.7814791652371d, segment.getLength(), 0d);
            assertEquals(-5.398073939261661d, segment.getTetha(), 0d);
            assertEquals(new Point2D(12.4d, 65.3d), segment.getStart());
            assertEquals(new Point2D(312.4d, 432.d), segment.getEnd());
            delegated[0] = true;
        };
        WiresConnectorLabelFactory.LongestSegmentLabelExecutor executor = new WiresConnectorLabelFactory.LongestSegmentLabelExecutor(delegate);
        executor.consumer().accept(connector, text);
        assertTrue(delegated[0]);
    }
}
