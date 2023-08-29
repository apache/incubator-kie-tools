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

import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(LienzoMockitoTestRunner.class)
public class BezierCurveTest {

    private static final Point2D START = new Point2D(50, 20);
    private static final Point2D CP1 = new Point2D(230, 30);
    private static final Point2D CP2 = new Point2D(150, 60);
    private static final Point2D END = new Point2D(50, 100);
    private static final Point2DArray POINTS = Point2DArray.fromArrayOfPoint2D(START, CP1, CP2, END);

    private BezierCurve curve;

    @Before
    public void setup() {
        curve = new BezierCurve(START, CP1, CP2, END);
    }

    @Test
    public void testBoundingBox() {
        BoundingBox boundingBox = curve.getBoundingBox();
        assertEquals(50, boundingBox.getMinX(), 0);
        assertEquals(20, boundingBox.getMinY(), 0);
        assertEquals(157.06256395286277, boundingBox.getMaxX(), 0);
        assertEquals(100, boundingBox.getMaxY(), 0);
    }
}
