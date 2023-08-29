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


package com.ait.lienzo.client.core.shape.toolbox.grid;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.shared.core.types.Direction;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(LienzoMockitoTestRunner.class)
public class FixedLayoutGridTest {

    private FixedLayoutGrid tested;

    @Test
    public void testGrid1() {
        final double size = 10;
        final double padding = 5;
        final Direction direction = Direction.EAST;
        final int rows = 2;
        final int cols = 2;
        tested = new FixedLayoutGrid(padding,
                                     size,
                                     direction,
                                     rows,
                                     cols);
        assertEquals(size,
                     tested.getIconSize(),
                     0);
        assertEquals(padding,
                     tested.getPadding(),
                     0);
        assertEquals(direction,
                     tested.getTowards());
        assertEquals(rows,
                     tested.getRows());
        assertEquals(cols,
                     tested.getCols());
        final List<Point2D> expectedPoints = new ArrayList<>();
        for (Point2D point2D : tested) {
            expectedPoints.add(point2D);
        }
        assertEquals(4,
                     expectedPoints.size());
        assertEquals(new Point2D(0d,
                                 -15d),
                     expectedPoints.get(0));
        assertEquals(new Point2D(15d,
                                 -15d),
                     expectedPoints.get(1));
        assertEquals(new Point2D(0d,
                                 -30d),
                     expectedPoints.get(2));
        assertEquals(new Point2D(15d,
                                 -30d),
                     expectedPoints.get(3));
    }

    @Test
    public void testGrid2() {
        final double size = 10.5;
        final double padding = 3.5;
        final Direction direction = Direction.EAST;
        final int rows = 4;
        final int cols = 1;
        tested = new FixedLayoutGrid(padding,
                                     size,
                                     direction,
                                     rows,
                                     cols);
        assertEquals(size,
                     tested.getIconSize(),
                     0);
        assertEquals(padding,
                     tested.getPadding(),
                     0);
        assertEquals(direction,
                     tested.getTowards());
        assertEquals(rows,
                     tested.getRows());
        assertEquals(cols,
                     tested.getCols());
        final List<Point2D> expectedPoints = new ArrayList<>();
        for (Point2D point2D : tested) {
            expectedPoints.add(point2D);
        }
        assertEquals(4,
                     expectedPoints.size());
        assertEquals(new Point2D(0d,
                                 -14d),
                     expectedPoints.get(0));
        assertEquals(new Point2D(0d,
                                 -28d),
                     expectedPoints.get(1));
        assertEquals(new Point2D(0d,
                                 -42d),
                     expectedPoints.get(2));
        assertEquals(new Point2D(0d,
                                 -56d),
                     expectedPoints.get(3));
    }

    @Test(expected = NoSuchElementException.class)
    public void testGridExceedBounds() {
        final double size = 10.5;
        final double padding = 3.5;
        final Direction direction = Direction.EAST;
        final int rows = 4;
        final int cols = 1;
        tested = new FixedLayoutGrid(padding,
                                     size,
                                     direction,
                                     rows,
                                     cols);
        final Iterator<Point2D> points = tested.iterator();
        final List<Point2D> expectedPoints = new ArrayList<>();
        while (points.hasNext()) {
            expectedPoints.add(points.next());
        }
        assertFalse(points.hasNext());
        points.next();
    }
}
