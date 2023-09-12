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


package com.ait.lienzo.client.core.shape.wires.handlers.impl;

import com.ait.lienzo.client.core.shape.wires.WiresContainer;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.ait.lienzo.tools.client.event.HandlerRegistration;
import com.ait.lienzo.tools.client.event.HandlerRegistrationManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class WiresDockingControlImplTest extends AbstractWiresControlTest {

    @Mock
    private HandlerRegistrationManager handlerRegistrationManager;

    private WiresDockingControlImpl wiresDockingControl;

    @Before
    public void setUp() {
        super.setUp();
        wiresDockingControl = new WiresDockingControlImpl(() -> parentPicker, handlerRegistrationManager);
    }

    @Test
    public void testUndockOnceStartingDrag() {
        shape.setDockedTo(mock(WiresContainer.class));
        wiresDockingControl.doMoveStart(0d, 0d);
        assertNull(shape.getDockedTo());
    }

    @Test
    public void getAdjust() {
        wiresDockingControl.doMoveStart(0, 0);
        wiresDockingControl.doMove(50, 50);
        Point2D adjust = wiresDockingControl.getAdjust();
        assertEquals(adjust.getX(), 95d, 0);
        assertEquals(adjust.getY(), 95d, 0);
    }

    @Test
    public void getAdjustWhenNoIntercepting() {
        Point2D adjust = wiresDockingControl.getAdjust();
        assertEquals(0, adjust.getX(), 0);
        assertEquals(0, adjust.getY(), 0);
    }

    @Test
    public void testDock() {
        final WiresContainer oldParent = mock(WiresContainer.class);
        shape.setParent(oldParent);
        shape.setDockedTo(oldParent);
        wiresDockingControl.dock(parent);
        verify(oldParent, atLeastOnce()).remove(eq(shape));
        verify(handlerRegistrationManager, times(2)).register(any(HandlerRegistration.class));
        assertEquals(parent, shape.getDockedTo());
    }

    @Test
    public void ensureSetRelativeLocationFromParentOnDocking() {
        wiresDockingControl.dock(parent);

        wiresDockingControl.move(new Point2D(90, 90));
        Point2D location = shape.getLocation();
        assertEquals(90, location.getX(), 0);
        assertEquals(90, location.getY(), 0);

        wiresDockingControl.move(new Point2D(20, 85));
        location = shape.getLocation();
        assertEquals(20, location.getX(), 0);
        assertEquals(85, location.getY(), 0);

        parent.setLocation(new Point2D(50, 50));

        wiresDockingControl.move(new Point2D(90, 90));
        location = shape.getLocation();
        assertEquals(40, location.getX(), 0);
        assertEquals(40, location.getY(), 0);
    }

    @Test
    public void testGetCandidateLocation() {
        wiresDockingControl.dock(parent);
        Point2D candidateLocation = wiresDockingControl.getCandidateLocation();
        assertEquals(-5d, candidateLocation.getX(), 0d);
        assertEquals(-5d, candidateLocation.getY(), 0d);
    }

    @Test
    public void testNoCandidateParentSoNoLocation() {
        assertNull(wiresDockingControl.getCandidateLocation());
    }

    @Test
    public void testDestroy() {
        wiresDockingControl.destroy();
        verify(handlerRegistrationManager, times(1)).destroy();
    }
}