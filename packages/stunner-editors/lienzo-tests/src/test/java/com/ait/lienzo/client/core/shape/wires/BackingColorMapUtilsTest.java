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
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.types.PathPartEntryJSO;
import com.ait.lienzo.client.core.types.PathPartList;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.ait.lienzo.tools.client.collection.NFastArrayList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class BackingColorMapUtilsTest {

    @Mock
    private Context2D ctx;

    @Mock
    private WiresShape shape;

    private String color = "#000000";

    @Mock
    private MultiPath path;

    private NFastArrayList<PathPartList> pathPartLists;

    @Mock
    private PathPartList pathPartList;

    private Point2D location;

    private Point2DArray points;

    @Mock
    private PathPartEntryJSO entry;

    private static final Double X0 = 0d;
    private static final Double Y0 = 0d;
    private static final Double X1 = 10d;
    private static final Double Y1 = 10d;
    private static final Double X2 = 20d;
    private static final Double Y2 = 20d;
    private static final Double OFFSET_X = 5d;
    private static final Double OFFSET_Y = 5d;

    private double[] bezierPoints = new double[]{X0, Y0, X1, Y1, X2, Y2};

    @Before
    public void setUp() {
        location = new Point2D(OFFSET_X, OFFSET_Y);
        points = Point2DArray.fromArrayOfPoint2D(location);
        pathPartLists = new NFastArrayList<>();
        pathPartLists.add(pathPartList);
        when(shape.getPath()).thenReturn(path);
        when(path.getActualPathPartListArray()).thenReturn(pathPartLists);
        when(path.getComputedLocation()).thenReturn(location);
        when(pathPartList.getPoints()).thenReturn(points);
        when(pathPartList.size()).thenReturn(1);
        when(pathPartList.get(0)).thenReturn(entry);
        when(entry.getCommand()).thenReturn(PathPartEntryJSO.BEZIER_CURVETO_ABSOLUTE);
        when(entry.getPoints()).thenReturn(bezierPoints);
    }

    @Test
    public void drawShapeToBackingTest() {
        BackingColorMapUtils.drawShapeToBacking(ctx, shape, color);
        verify(ctx).bezierCurveTo(X0 + OFFSET_X, Y0 + OFFSET_Y,
                                  X1 + OFFSET_X, Y1 + OFFSET_Y,
                                  X2 + OFFSET_X, Y2 + OFFSET_Y);
    }
}
