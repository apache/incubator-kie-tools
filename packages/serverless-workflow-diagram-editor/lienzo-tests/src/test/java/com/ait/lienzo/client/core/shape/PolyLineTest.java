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


package com.ait.lienzo.client.core.shape;

import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;

@RunWith(LienzoMockitoTestRunner.class)
public class PolyLineTest {

    private PolyLine polyLine;

    @Before
    public void setup() {
        polyLine = new PolyLine();
    }

    @Test
    public void testParseWhenPointsSizeIsEmpty() {
        assertTrue(new Point2DArray().getPoints().isEmpty());
    }

    @Test
    public void testParseWhenPointsSizeIsOne() {
        final Point2D point = new Point2D(1.0, 1.0);
        final Point2DArray point2DArray = makePointArray(point);
        polyLine.setPoint2DArray(point2DArray);
        polyLine.parse();
        assertEquals(point, polyLine.getTailOffsetPoint());
        assertEquals(point, polyLine.getHeadOffsetPoint());
    }

    @Test
    public void testParseWhenPointsSizeIsGreaterThanOne() {

        final Point2D point = new Point2D(1.0, 1.0);
        final Point2DArray point2DArray = makePointArray(point);

        polyLine.setPoint2DArray(point2DArray);
        final boolean parse = polyLine.parse();

        assertTrue(parse);
        assertEquals(point, polyLine.getTailOffsetPoint());
        assertEquals(point, polyLine.getHeadOffsetPoint());
    }

    @Test
    public void testParseWhenStartAndEndpointsAreTheSame() {

        final Point2D point1 = new Point2D(1, 1);
        final Point2D point2 = new Point2D(1, 1);
        Point2DArray point2DArray = makePointArray(point1, point2);
        polyLine.setPoint2DArray(point2DArray);
        polyLine.parse();
        assertEquals(point1, polyLine.getHeadOffsetPoint());
        assertEquals(point1, polyLine.getTailOffsetPoint());
    }

    private Point2DArray makePointArray(Point2D point, Point2D... points) {
        Point2DArray array = new Point2DArray();
        array.push(point);
        array.push(points);

        return spy(array);
    }
}
