/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package com.ait.lienzo.client.widget.panel.util;

import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.client.widget.panel.Bounds;
import com.ait.lienzo.client.widget.panel.LienzoBoundsPanel;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class PanelTransformUtilsTests {

    private Viewport viewport;

    @Before
    public void setUp() {
        viewport = new Viewport(1200, 900);
    }

    @Test
    public void testSetScaleLevel() {
        double level = 0.8d;
        viewport.setTransform(new Transform().translate(0.1d, 0.2d).scale(0.3d, 0.4d));
        PanelTransformUtils.setScaleLevel(viewport, level);
        Transform transform = viewport.getTransform();
        assertEquals(0.1d, transform.getTranslateX(), 0d);
        assertEquals(0.2d, transform.getTranslateY(), 0d);
        assertEquals(0.8d, transform.getScaleX(), 0d);
        assertEquals(0.8d, transform.getScaleY(), 0d);
    }

    @Test
    public void testReset() {
        viewport.setTransform(new Transform().translate(0.1d, 0.2d).scale(0.3d, 0.4d));
        PanelTransformUtils.reset(viewport);
        Transform transform = viewport.getTransform();
        assertEquals(0d, transform.getTranslateX(), 0d);
        assertEquals(0d, transform.getTranslateY(), 0d);
        assertEquals(1d, transform.getScaleX(), 0d);
        assertEquals(1d, transform.getScaleY(), 0d);
    }

    @Test
    public void testComputeZoomLevelFitToWidth() {
        LienzoBoundsPanel panel = mock(LienzoBoundsPanel.class);
        when(panel.getWidthPx()).thenReturn(800);
        when(panel.getHeightPx()).thenReturn(600);
        Bounds layerBounds = Bounds.build(0d, 0d, 1600, 1200);
        when(panel.getLayerBounds()).thenReturn(layerBounds);
        double level = PanelTransformUtils.computeZoomLevelFitToWidth(panel);
        assertEquals(0.5d, level, 0d);
    }

    @Test
    public void testComputeLevel() {
        viewport.setTransform(new Transform().translate(0.1d, 0.2d).scale(0.3d, 0.4d));
        double level = PanelTransformUtils.computeLevel(viewport);
        assertEquals(0.3d, level, 0d);
    }
}
