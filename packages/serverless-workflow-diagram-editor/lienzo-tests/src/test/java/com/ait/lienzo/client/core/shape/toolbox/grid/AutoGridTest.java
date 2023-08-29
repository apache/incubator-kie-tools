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

import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.shared.core.types.Direction;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(LienzoMockitoTestRunner.class)
public class AutoGridTest {

    private AutoGrid tested;

    @Test
    public void testGrid1() {
        final double size = 10;
        final double padding = 5;
        final Direction direction = Direction.EAST;
        final BoundingBox boundingBox = BoundingBox.fromDoubles(0d,
                                                                0d,
                                                                100d,
                                                                100d);
        tested = new AutoGrid.Builder()
                .forBoundingBox(boundingBox)
                .withIconSize(size)
                .withPadding(padding)
                .towards(direction)
                .build();
        assertEquals(size,
                     tested.getIconSize(),
                     0);
        assertEquals(padding,
                     tested.getPadding(),
                     0);
        assertEquals(direction,
                     tested.getDirection());
        assertEquals(100,
                     tested.getMaxSize(),
                     0);
        assertEquals((size + padding) / 2,
                     tested.getMargin(),
                     0);
        final List<Point2D> expectedPoints = new ArrayList<>();
        final Iterator<Point2D> points = tested.iterator();
        int count = 5;
        for (int i = 0; i < count; i++) {
            final Point2D point = points.next();
            expectedPoints.add(point);
        }
        assertEquals(5,
                     expectedPoints.size());
        assertEquals(new Point2D(0d,
                                 -15d),
                     expectedPoints.get(0));
        assertEquals(new Point2D(15d,
                                 -15d),
                     expectedPoints.get(1));
        assertEquals(new Point2D(30d,
                                 -15d),
                     expectedPoints.get(2));
        assertEquals(new Point2D(45d,
                                 -15d),
                     expectedPoints.get(3));
        assertEquals(new Point2D(60d,
                                 -15d),
                     expectedPoints.get(4));
    }

    @Test
    public void testGrid2() {
        final double size = 2.5;
        final double padding = 0.5;
        final Direction direction = Direction.SOUTH;
        final BoundingBox boundingBox = BoundingBox.fromDoubles(0d,
                                                                0d,
                                                                100d,
                                                                100d);
        tested = new AutoGrid.Builder()
                .forBoundingBox(boundingBox)
                .withIconSize(size)
                .withPadding(padding)
                .towards(direction)
                .build();
        assertEquals(size,
                     tested.getIconSize(),
                     0);
        assertEquals(padding,
                     tested.getPadding(),
                     0);
        assertEquals(direction,
                     tested.getDirection());
        assertEquals(100,
                     tested.getMaxSize(),
                     0);
        assertEquals((size + padding) / 2,
                     tested.getMargin(),
                     0);
        final List<Point2D> expectedPoints = new ArrayList<>();
        final Iterator<Point2D> points = tested.iterator();
        int count = 5;
        for (int i = 0; i < count; i++) {
            final Point2D point = points.next();
            expectedPoints.add(point);
        }
        assertEquals(5,
                     expectedPoints.size());
        assertEquals(new Point2D(0d,
                                 0d),
                     expectedPoints.get(0));
        assertEquals(new Point2D(0d,
                                 3d),
                     expectedPoints.get(1));
        assertEquals(new Point2D(0d,
                                 6d),
                     expectedPoints.get(2));
        assertEquals(new Point2D(0d,
                                 9d),
                     expectedPoints.get(3));
        assertEquals(new Point2D(0d,
                                 12d),
                     expectedPoints.get(4));
    }

    @Test
    public void testUpdateSize() {
        final double size = 2.5;
        final double padding = 0.5;
        final double shapeSize = 100;
        final Direction direction = Direction.SOUTH;
        final BoundingBox boundingBox = BoundingBox.fromDoubles(0d,
                                                                0d,
                                                                shapeSize,
                                                                shapeSize);
        tested = new AutoGrid.Builder()
                .forBoundingBox(boundingBox)
                .withIconSize(size)
                .withPadding(padding)
                .towards(direction)
                .build();
        assertEquals(size,
                     tested.getIconSize(),
                     0);
        assertEquals(padding,
                     tested.getPadding(),
                     0);
        assertEquals(direction,
                     tested.getDirection());
        assertEquals(shapeSize,
                     tested.getMaxSize(),
                     0);
        assertEquals((size + padding) / 2,
                     tested.getMargin(),
                     0);
        final List<Point2D> expectedPoints = new ArrayList<>();
        final Iterator<Point2D> points = tested.iterator();
        int count = 5;
        for (int i = 0; i < count; i++) {
            final Point2D point = points.next();
            expectedPoints.add(point);
        }
        assertEquals(5,
                     expectedPoints.size());
        assertEquals(new Point2D(0d,
                                 0d),
                     expectedPoints.get(0));
        assertEquals(new Point2D(0d,
                                 3d),
                     expectedPoints.get(1));
        assertEquals(new Point2D(0d,
                                 6d),
                     expectedPoints.get(2));
        assertEquals(new Point2D(0d,
                                 9d),
                     expectedPoints.get(3));
        assertEquals(new Point2D(0d,
                                 12d),
                     expectedPoints.get(4));
        tested.setSize(6,
                       6);
        assertEquals(6,
                     tested.getMaxSize(),
                     0);
        expectedPoints.clear();
        final Iterator<Point2D> points2 = tested.iterator();
        for (int i = 0; i < count; i++) {
            final Point2D point = points2.next();
            expectedPoints.add(point);
        }
        assertEquals(5,
                     expectedPoints.size());
        assertEquals(new Point2D(0d,
                                 0d),
                     expectedPoints.get(0));
        assertEquals(new Point2D(0d,
                                 3d),
                     expectedPoints.get(1));
        assertEquals(new Point2D(3d,
                                 0d),
                     expectedPoints.get(2));
        assertEquals(new Point2D(3d,
                                 3d),
                     expectedPoints.get(3));
        assertEquals(new Point2D(6d,
                                 0d),
                     expectedPoints.get(4));
    }
}
