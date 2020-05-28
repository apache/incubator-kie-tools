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

package com.ait.lienzo.client.widget.panel.mediators;

import com.ait.lienzo.client.core.mediator.EventFilter;
import com.ait.lienzo.client.core.mediator.IMediator;
import com.ait.lienzo.client.core.mediator.MousePanMediator;
import com.ait.lienzo.client.core.mediator.MouseWheelZoomMediator;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Scene;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.widget.panel.LienzoBoundsPanel;
import com.ait.lienzo.client.widget.panel.scrollbars.ScrollablePanel;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.ait.tooling.common.api.java.util.function.Supplier;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class PanelMediatorsTest {

    @Mock
    private ScrollablePanel panel;

    @Mock
    private PanelPreviewMediator previewMediator;

    private PanelMediators tested;
    private Viewport viewport;

    @Before
    public void setUp() {
        Layer layer = new Layer();
        Scene scene = new Scene();
        viewport = new Viewport(scene, 1200, 1200);
        scene.add(layer);
        when(panel.getLayer()).thenReturn(layer);
        tested = new PanelMediators().init(new Supplier<LienzoBoundsPanel>() {
            @Override
            public LienzoBoundsPanel get() {
                return panel;
            }
        }, new Supplier<PanelPreviewMediator>() {
            @Override
            public PanelPreviewMediator get() {
                return previewMediator;
            }
        }, EventFilter.CONTROL, EventFilter.ALT);
    }

    @Test
    public void testInit() {
        assertNotNull(tested.getZoomMediator());
        MouseWheelZoomMediator zoomMediator = tested.getZoomMediator();
        assertEquals(PanelMediators.MIN_SCALE, zoomMediator.getMinScale(), 0d);
        assertEquals(PanelMediators.MAX_SCALE, zoomMediator.getMaxScale(), 0d);
        assertEquals(PanelMediators.ZOOM_FACTOR, zoomMediator.getZoomFactor(), 0d);
        assertTrue(zoomMediator.isEnabled());
        assertNotNull(tested.getPanMediator());
        MousePanMediator panMediator = tested.getPanMediator();
        assertTrue(panMediator.isXConstrained());
        assertTrue(panMediator.isYConstrained());
        assertTrue(panMediator.isEnabled());
        assertNotNull(tested.getPreviewMediator());
        PanelPreviewMediator previewMediator = tested.getPreviewMediator();
        assertFalse(previewMediator.isEnabled());
        IMediator mediator1 = viewport.getMediators().pop();
        IMediator mediator2 = viewport.getMediators().pop();
        assertEquals(zoomMediator, mediator1);
        assertEquals(panMediator, mediator2);
    }

    @Test
    public void testEnablePreview() {
        tested.enablePreview();
        assertFalse(tested.getZoomMediator().isEnabled());
        assertFalse(tested.getPanMediator().isEnabled());
        verify(previewMediator, times(1)).enable();
    }

    @Test
    public void testDisablePreview() {
        tested.disablePreview();
        assertTrue(tested.getZoomMediator().isEnabled());
        assertTrue(tested.getPanMediator().isEnabled());
        verify(previewMediator, times(1)).disable();
    }

    @Test
    public void testDestroy() {
        tested.destroy();
        assertFalse(viewport.getMediators().iterator().hasNext());
        assertNull(tested.getZoomMediator());
        assertNull(tested.getPanMediator());
        assertNull(tested.getPreviewMediator());
        verify(previewMediator, times(1)).removeHandler();
    }
}
