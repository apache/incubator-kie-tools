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
package com.ait.lienzo.client.core.mediator;

import com.ait.lienzo.client.core.event.NodeMouseWheelEvent;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class MouseWheelZoomMediatorTest
{
    @Mock
    private NodeMouseWheelEvent    mouseWheelEvent;

    private MouseWheelZoomMediator tested;

    private Viewport               viewport;

    @Before
    public void setUp()
    {
        this.viewport = new Viewport();
        this.tested = new MouseWheelZoomMediator();
        this.tested.setZoomFactor(0.3);
        this.tested.setViewport(viewport);
    }

    @Test
    public void testOnMouseWheelAboutPoint()
    {
        when(mouseWheelEvent.isSouth()).thenReturn(true);
        when(mouseWheelEvent.isNorth()).thenReturn(false);
        when(mouseWheelEvent.getX()).thenReturn(110);
        when(mouseWheelEvent.getY()).thenReturn(323);
        tested.setScaleAboutPoint(true);
        tested.onMouseWheel(mouseWheelEvent);
        assertEquals(0.7692307692307692d, viewport.getTransform().getScaleX(), 0d);
        assertEquals(0.7692307692307692d, viewport.getTransform().getScaleY(), 0d);
        assertEquals(25.384615384615387d, viewport.getTransform().getTranslateX(), 0d);
        assertEquals(74.53846153846155d, viewport.getTransform().getTranslateY(), 0d);
    }

    @Test
    public void testOnMouseWheelRelativeToCenter()
    {
        when(mouseWheelEvent.isSouth()).thenReturn(true);
        when(mouseWheelEvent.isNorth()).thenReturn(false);
        when(mouseWheelEvent.getX()).thenReturn(110);
        when(mouseWheelEvent.getY()).thenReturn(323);
        tested.setScaleAboutPoint(false);
        tested.onMouseWheel(mouseWheelEvent);
        assertEquals(0.7692307692307692d, viewport.getTransform().getScaleX(), 0d);
        assertEquals(0.7692307692307692d, viewport.getTransform().getScaleY(), 0d);
        assertEquals(0d, viewport.getTransform().getTranslateX(), 0d);
        assertEquals(0d, viewport.getTransform().getTranslateY(), 0d);
    }
}
