/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package com.ait.lienzo.client.core.shape.wires.handlers.impl;

import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(LienzoMockitoTestRunner.class)
public class WiresDockingControlImplTest extends AbstractWiresControlTest {

    private WiresDockingControlImpl wiresDockingControl;

    @Before
    public void setUp() {
        super.setUp();

        wiresDockingControl = new WiresDockingControlImpl(parentPicker);
    }

    @Test
    public void getAdjust() {
        wiresDockingControl.beforeMoveStart(0, 0);
        wiresDockingControl.afterMove(50, 50);
        Point2D adjust = wiresDockingControl.getAdjust();
        Point2D parentLocation = parent.getLocation();
        assertEquals(adjust.getX(), parentLocation.getX() - SHAPE_SIZE / 2, 0);
        assertEquals(adjust.getY(), parentLocation.getY() - SHAPE_SIZE / 2, 0);
    }

    @Test
    public void dock() {
        wiresDockingControl.dock(shape, parent, new Point2D(90, 90));
        Point2D location = shape.getLocation();
        assertEquals(location.getX(), 95, 0);
        assertEquals(location.getY(), 95, 0);

        wiresDockingControl.dock(shape, parent, new Point2D(20, 85));
        location = shape.getLocation();
        assertEquals(location.getX(), 45, 0);
        assertEquals(location.getY(), 95, 0);

        wiresDockingControl.dock(shape, parent, new Point2D(3, 92));
        location = shape.getLocation();
        assertEquals(location.getX(), -5, 0);
        assertEquals(location.getY(), 95, 0);

        wiresDockingControl.dock(shape, parent, new Point2D(90, 10));
        location = shape.getLocation();
        assertEquals(location.getX(), 95, 0);
        assertEquals(location.getY(), -5, 0);

        wiresDockingControl.dock(shape, parent, new Point2D(40, 15));
        location = shape.getLocation();
        assertEquals(location.getX(), 45, 0);
        assertEquals(location.getY(), -5, 0);

        wiresDockingControl.dock(shape, parent, new Point2D(5, 10));
        location = shape.getLocation();
        assertEquals(location.getX(), -5, 0);
        assertEquals(location.getY(), -5, 0);
    }
}