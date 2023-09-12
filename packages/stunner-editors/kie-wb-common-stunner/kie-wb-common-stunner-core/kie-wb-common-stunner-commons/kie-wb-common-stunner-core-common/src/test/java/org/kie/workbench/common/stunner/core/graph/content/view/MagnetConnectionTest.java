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


package org.kie.workbench.common.stunner.core.graph.content.view;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MagnetConnectionTest {

    @Mock
    private Element<? extends View<?>> element;

    @Mock
    private Element<? extends View<?>> element2;

    @Mock
    private View content;
    @Mock
    private View content2;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        Bounds bounds = Bounds.create(10d, 20d, 100d, 200d);
        when(element.getContent()).thenReturn(content);
        when(content.getBounds()).thenReturn(bounds);
    }

    @Test
    public void testForLocation() {
        MagnetConnection m1 = MagnetConnection.Builder.at(123,
                                                          321);
        assertEquals(123,
                     m1.getLocation().getX(),
                     0);
        assertEquals(321,
                     m1.getLocation().getY(),
                     0);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAtCenter() {
        MagnetConnection m1 = MagnetConnection.Builder.atCenter(element);

        assertEquals(45,
                     m1.getLocation().getX(),
                     0);
        assertEquals(90,
                     m1.getLocation().getY(),
                     0);
        assertEquals(MagnetConnection.MAGNET_CENTER,
                     m1.getMagnetIndex().getAsInt());
        assertFalse(m1.isAuto());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testForTargetAtTop() {
        Bounds bounds2 = Bounds.create(0d, -100d, 200d, 0d);
        when(element2.getContent()).thenReturn(content2);
        when(content2.getBounds()).thenReturn(bounds2);

        MagnetConnection m1 = MagnetConnection.Builder.forTarget(element, element2);
        assertEquals(Point2D.create(45, 0), m1.getLocation());
        assertEquals(MagnetConnection.MAGNET_TOP,
                     m1.getMagnetIndex().getAsInt());
        assertTrue(m1.isAuto());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testForTargetAtBottom() {
        Bounds bounds2 = Bounds.create(0d, 210d, 200d, 310d);
        when(element2.getContent()).thenReturn(content2);
        when(content2.getBounds()).thenReturn(bounds2);

        MagnetConnection m1 = MagnetConnection.Builder.forTarget(element, element2);
        assertEquals(Point2D.create(45, 180), m1.getLocation());
        assertEquals(MagnetConnection.MAGNET_BOTTOM,
                     m1.getMagnetIndex().getAsInt());
        assertTrue(m1.isAuto());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testForTargetAtRight() {
        Bounds bounds2 = Bounds.create(120d, 30d, 200d, 300d);
        when(element2.getContent()).thenReturn(content2);
        when(content2.getBounds()).thenReturn(bounds2);

        MagnetConnection m1 = MagnetConnection.Builder.forTarget(element, element2);
        assertEquals(Point2D.create(90, 90), m1.getLocation());
        assertEquals(MagnetConnection.MAGNET_RIGHT,
                     m1.getMagnetIndex().getAsInt());
        assertTrue(m1.isAuto());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testForTargetAtLeft() {
        Bounds bounds2 = Bounds.create(-40d, 10d, 0d, 300d);
        when(element2.getContent()).thenReturn(content2);
        when(content2.getBounds()).thenReturn(bounds2);

        MagnetConnection m1 = MagnetConnection.Builder.forTarget(element, element2);
        assertEquals(Point2D.create(0, 90), m1.getLocation());
        assertEquals(MagnetConnection.MAGNET_LEFT,
                     m1.getMagnetIndex().getAsInt());
        assertTrue(m1.isAuto());
    }

    @Test
    public void testBuilder() {
        MagnetConnection m1 = new MagnetConnection.Builder()
                .atX(10)
                .atY(25)
                .magnet(4)
                .auto(true)
                .build();
        assertEquals(10,
                     m1.getLocation().getX(),
                     0);
        assertEquals(25,
                     m1.getLocation().getY(),
                     0);
        assertEquals(4,
                     m1.getMagnetIndex().getAsInt());
        assertTrue(m1.isAuto());
    }

    @Test
    public void testChangeLocationAndNotResetMagnet() {
        MagnetConnection m1 = new MagnetConnection.Builder()
                .atX(10)
                .atY(25)
                .magnet(4)
                .build();
        assertEquals(10,
                     m1.getLocation().getX(),
                     0);
        assertEquals(25,
                     m1.getLocation().getY(),
                     0);
        assertEquals(4,
                     m1.getMagnetIndex().getAsInt());
        assertFalse(m1.isAuto());
        m1.setLocation(new Point2D(100,
                                   200));
        assertEquals(100,
                     m1.getLocation().getX(),
                     0);
        assertEquals(200,
                     m1.getLocation().getY(),
                     0);
        assertTrue(m1.getMagnetIndex().isPresent());
    }
}
