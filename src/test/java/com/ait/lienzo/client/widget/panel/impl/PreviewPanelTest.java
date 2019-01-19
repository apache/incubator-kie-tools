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
package com.ait.lienzo.client.widget.panel.impl;

import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.client.core.util.ScratchPad;
import com.ait.lienzo.client.widget.panel.Bounds;
import com.ait.lienzo.client.widget.panel.LienzoPanel;
import com.ait.lienzo.client.widget.panel.event.LienzoPanelBoundsChangedEventHandler;
import com.ait.lienzo.client.widget.panel.event.LienzoPanelResizeEventHandler;
import com.ait.lienzo.client.widget.panel.event.LienzoPanelScaleChangedEventHandler;
import com.ait.lienzo.client.widget.panel.event.LienzoPanelScrollEventHandler;
import com.ait.lienzo.client.widget.panel.scrollbars.ScrollBars;
import com.ait.lienzo.client.widget.panel.scrollbars.ScrollablePanel;
import com.ait.lienzo.client.widget.panel.scrollbars.ScrollablePanelHandler;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.ait.tooling.nativetools.client.event.HandlerRegistrationManager;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(LienzoMockitoTestRunner.class)
public class PreviewPanelTest
{
    private static final Bounds BOUNDS = Bounds.build(0d, 0d, 800d, 1200d);

    @Mock
    private HandlerRegistrationManager registrationManager;

    @Mock
    private LienzoPanel                lienzoPanel;

    @Mock
    private PreviewLayer               previewLayer;

    @Mock
    private Layer                      layer;

    @Mock
    private ScratchPad                 scratchPad;

    @Mock
    private Viewport                   viewport;

    @Mock
    private ScrollablePanel            observedPanel;

    @Mock
    private PreviewLayerDecorator      decorator;

    @Mock
    private HandlerManager             m_events;

    private PreviewPanel               tested;

    @Before
    public void setUp()
    {
        when(layer.getViewport()).thenReturn(viewport);
        when(layer.getScratchPad()).thenReturn(scratchPad);
        when(lienzoPanel.getWidthPx()).thenReturn(300);
        when(lienzoPanel.getHeightPx()).thenReturn(150);
        when(observedPanel.getWidthPx()).thenReturn(330);
        when(observedPanel.getHeightPx()).thenReturn(133);
        when(observedPanel.getLayerBounds()).thenReturn(BOUNDS);
        this.tested = spy(new PreviewPanel(lienzoPanel,
                                           previewLayer,
                                           decorator,
                                           m_events,
                                           registrationManager));
        tested.set(layer);
    }

    @Test
    public void testObserve()
    {
        ScrollablePanelHandler scrollhandler = mock(ScrollablePanelHandler.class);
        ScrollBars             scrollBars    = mock(ScrollBars.class);
        when(observedPanel.getScrollHandler()).thenReturn(scrollhandler);
        when(scrollhandler.scrollBars()).thenReturn(scrollBars);
        when(scrollBars.getHorizontalScrollPosition()).thenReturn(0.2d);
        when(scrollBars.getVerticalScrollPosition()).thenReturn(0.66d);
        tested.observe(observedPanel);
        assertEquals(observedPanel,
                     tested.getPreviewBoundsProvider().delegate);
        verify(scratchPad, times(1)).setPixelSize(eq(800), eq(1200));
        verify(observedPanel, times(1)).addLienzoPanelScrollEventHandler(any(LienzoPanelScrollEventHandler.class));
        verify(observedPanel, times(1)).addLienzoPanelResizeEventHandler(any(LienzoPanelResizeEventHandler.class));
        verify(observedPanel, times(1)).addLienzoPanelScaleChangedEventHandler(any(LienzoPanelScaleChangedEventHandler.class));
        verify(observedPanel, times(1)).addLienzoPanelBoundsChangedEventHandler(any(LienzoPanelBoundsChangedEventHandler.class));
        verify(tested, times(1)).addLienzoPanelScrollEventHandler(any(LienzoPanelScrollEventHandler.class));
        verify(registrationManager, times(5)).register(any(HandlerRegistration.class));
        assertEquals(330, tested.getDefaultBounds().getWidth(), 0d);
        assertEquals(133, tested.getDefaultBounds().getHeight(), 0d);
        assertEquals(-0.66d, tested.getVisibleBounds().getX(), 0d);
        assertEquals(-0.8778d, tested.getVisibleBounds().getY(), 0d);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSetLayer()
    {
        IPrimitive prim = mock(IPrimitive.class);
        when(decorator.asPrimitive()).thenReturn(prim);
        tested.set(layer);
        verify(previewLayer, times(1)).add(eq(prim));
        verify(lienzoPanel, times(2)).add(eq(previewLayer));
    }

    @Test
    public void testBatch()
    {
        tested.set(layer);
        tested.batch();
        verify(layer, times(1)).batch();
        verify(decorator, times(1)).update();
        verify(previewLayer, times(1)).batch();
    }

    @Test
    public void testObtainViewportBounds()
    {
        Transform transform = new Transform();
        transform.scale(2, 0.5);
        transform.translate(-10, 10);
        when(viewport.getTransform()).thenReturn(transform);
        when(viewport.getWidth()).thenReturn(300);
        when(viewport.getHeight()).thenReturn(150);
        Bounds bounds = PreviewPanel.obtainViewportBounds(layer);
        assertEquals(0d, bounds.getX(), 0);
        assertEquals(0d, bounds.getY(), 0);
        assertEquals(150d, bounds.getWidth(), 0);
        assertEquals(300d, bounds.getHeight(), 0);
    }

    @Test
    public void testDestroy()
    {
        tested.set(layer);
        tested.destroy();
        verify(decorator, times(1)).destroy();
        verify(previewLayer, times(1)).clear();
        verify(lienzoPanel, times(1)).destroy();
        assertNull(tested.getPreviewBoundsProvider().delegate);
        assertNull(tested.getLayer());
        assertNull(tested.getDefaultBounds());
    }
}
