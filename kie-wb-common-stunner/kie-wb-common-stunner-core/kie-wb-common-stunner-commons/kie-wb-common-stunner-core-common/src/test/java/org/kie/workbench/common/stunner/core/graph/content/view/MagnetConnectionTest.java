/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.graph.content.view;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MagnetConnectionTest {

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
    public void testForElement() {
        Element element = mock(Element.class);
        View<?> content = mock(View.class);
        BoundsImpl bounds = new BoundsImpl(new BoundImpl(10d,
                                                         20d),
                                           new BoundImpl(100d,
                                                         200d));
        when(element.getContent()).thenReturn(content);
        when(content.getBounds()).thenReturn(bounds);
        MagnetConnection m1 = MagnetConnection.Builder.forElement(element);

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

    @Test(expected = AssertionError.class)
    public void testBuilderEmptyLocation() {
        new MagnetConnection.Builder()
                .magnet(4)
                .auto(true)
                .build();
    }

    @Test
    public void testChangeLocationAndResetMagnet() {
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
        assertFalse(m1.getMagnetIndex().isPresent());
    }
}
