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

import com.ait.lienzo.client.core.event.AbstractNodeMouseEvent;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class ShapeControlUtilsTest
{
    @Mock
    private Viewport  viewport;

    private Transform transform;

    @Before
    public void setup()
    {
        transform = new Transform();
        when(viewport.getTransform()).thenReturn(transform);
    }

    @Test
    public void testGetViewportRelativeLocation()
    {
        transform.scale(2, 3).translate(10, 100);
        Point2D location = ShapeControlUtils.getViewportRelativeLocation(viewport, 33, 67);
        assertEquals(6.5d, location.getX(), 0d);
        assertEquals(-77.66666666666667d, location.getY(), 0d);
    }

    @Test
    public void testGetViewportRelativeLocationByEvent()
    {
        AbstractNodeMouseEvent event = mock(AbstractNodeMouseEvent.class);
        when(event.getX()).thenReturn(33);
        when(event.getY()).thenReturn(67);
        transform.scale(2, 3).translate(10, 100);
        Point2D location = ShapeControlUtils.getViewportRelativeLocation(viewport, event);
        assertEquals(6.5d, location.getX(), 0d);
        assertEquals(-77.66666666666667d, location.getY(), 0d);
    }
}
