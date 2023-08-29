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


package com.ait.lienzo.client.widget.panel.mediators;

import java.util.function.Consumer;
import java.util.function.Supplier;

import com.ait.lienzo.client.core.mediator.MouseBoxZoomMediator;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Scene;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.client.widget.panel.Bounds;
import com.ait.lienzo.client.widget.panel.LienzoPanel;
import com.ait.lienzo.client.widget.panel.impl.LienzoFixedPanel;
import com.ait.lienzo.client.widget.panel.impl.PreviewLayer;
import com.ait.lienzo.client.widget.panel.impl.ScrollablePanel;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import elemental2.dom.CSSStyleDeclaration;
import elemental2.dom.HTMLDivElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class PanelPreviewMediatorTest {

    private static final int WIDTH = 1200;
    private static final int HEIGHT = 600;
    private static final double MAX_SCALE = 0.5;

    @Mock
    private ScrollablePanel panel;

    @Mock
    private HTMLDivElement panelContainer;

    @Mock
    private HTMLDivElement scrollPanel;

    private PanelPreviewMediator tested;
    private LienzoFixedPanel previewPanel;
    private Layer layer;

    @Before
    public void setUp() {
        Scene scene = new Scene();
        layer = spy(new Layer());
        scene.add(layer);
        when(panel.getLayer()).thenReturn(layer);
        when(panel.getDomElementContainer()).thenReturn(panelContainer);
        when(panel.getElement()).thenReturn(scrollPanel);
        when(panel.getWidePx()).thenReturn(WIDTH);
        when(panel.getHighPx()).thenReturn(HEIGHT);

        previewPanel = spy(LienzoFixedPanel.newPanel(1, 1));

        tested = new PanelPreviewMediator(() -> panel,
                                          new Consumer<HTMLDivElement>() {
            @Override
            public void accept(HTMLDivElement htmlDivElement) {
                panelContainer.appendChild(htmlDivElement);
            }

            @Override
            public Consumer<HTMLDivElement> andThen(Consumer<? super HTMLDivElement> after) {
                return null;
            }
        }, ()-> previewPanel);
        tested.setMaxScale(MAX_SCALE);
    }

    @Test
    public void testConstruction() {
        verify(panelContainer, times(1)).appendChild(any(HTMLDivElement.class));
        assertNotNull(tested.getPreviewLayer());
        PreviewLayer previewLayer = tested.getPreviewLayer();
        assertTrue(previewLayer.isListening());
        assertTrue(previewLayer.isTransformable());
        assertNotNull(tested.getPreviewPanel());
        LienzoPanel previewPanel = tested.getPreviewPanel();
        assertEquals("none", previewPanel.getElement().style.display);
        assertEquals(previewLayer, previewLayer.getLayer());
        assertNotNull(tested.getMediator());
        MouseBoxZoomMediator mediator = tested.getMediator();
        assertEquals(mediator, previewLayer.getViewport().getMediators().pop());
        assertFalse(mediator.isEnabled());
    }

    @Test
    public void testMouseBoxZoomMediatorTransformCallback() {
        Viewport viewport = mock(Viewport.class);
        when(layer.getViewport()).thenReturn(viewport);
        tested.getMediator().setEnabled(true);
        Transform transform = new Transform().translate(2, 3).scaleWithXY(4, 5);
        tested.getMediator().getOnTransform().accept(transform);
        verify(viewport, times(1)).setTransform(eq(new Transform().translate(2, 3).scaleWithXY(4, 5)));
        assertFalse(tested.getMediator().isEnabled());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testEnable() {
        HTMLDivElement previewPanelElement = mock(HTMLDivElement.class);
        CSSStyleDeclaration previewPanelStyle = mock(CSSStyleDeclaration.class);
        when(previewPanel.getElement()).thenReturn(previewPanelElement);
        previewPanelElement.style = previewPanelStyle;
        when(panel.getLayerBounds()).thenReturn(Bounds.build(0, 0, WIDTH / 2, HEIGHT / 2));
        tested.enable();
        assertTrue(tested.getPreviewLayer().isListening());
        assertFalse(layer.isListening());
        assertFalse(layer.isVisible());
        assertEquals("absolute", previewPanelStyle.position);
        assertEquals("0px", previewPanelStyle.top);
        assertEquals("0px", previewPanelStyle.left);
        assertEquals("none", previewPanelStyle.borderStyle);
        assertEquals(PanelPreviewMediator.PREVIEW_BG_COLOR, previewPanelStyle.backgroundColor);
        verify(previewPanel, times(1)).setPixelSize(WIDTH, HEIGHT);
        Transform previewTransform = tested.getPreviewLayer().getViewport().getTransform();
        assertEquals(0.5833333333333334d, previewTransform.getScaleX(), 0d);
        assertEquals(0.5833333333333334d, previewTransform.getScaleY(), 0d);
        assertEquals(250d, previewTransform.getTranslateX(), 0d);
        assertEquals(250d, previewTransform.getTranslateY(), 0d);
        ArgumentCaptor<Supplier> transformCaptor = ArgumentCaptor.forClass(Supplier.class);
        verify(layer, times(1)).drawWithTransforms(eq(tested.getPreviewLayer().getContext()),
                                                   eq(1d),
                                                   eq(BoundingBox.fromDoubles(0, 0, WIDTH, HEIGHT)),
                                                   transformCaptor.capture());
        Supplier<Transform> drawTranform = transformCaptor.getValue();
        assertEquals(previewTransform, drawTranform.get());
        assertEquals(MAX_SCALE, tested.getMediator().getMaxScale(), 0d);
        assertTrue(tested.getMediator().isEnabled());
        assertTrue(tested.isEnabled());
    }

    @Test
    public void testDisable() {
        tested.onDisable();
        assertFalse(tested.getMediator().isEnabled());
        assertEquals(0, tested.getPreviewLayer().length());
        assertEquals(1, tested.getPreviewPanel().getWidePx());
        assertEquals(1, tested.getPreviewPanel().getHighPx());
        assertEquals("none", tested.getPreviewPanel().getElement().style.display);
        assertTrue(layer.isVisible());
        assertFalse(tested.isEnabled());
    }

    @Test
    public void testOnRemoveHandler() {
        tested.removeHandler();
        assertFalse(tested.getMediator().isEnabled());
        assertNull(tested.getMediator().getOnTransform());
        assertEquals(0, tested.getPreviewLayer().length());
        assertEquals(1, tested.getPreviewPanel().getWidePx());
        assertEquals(1, tested.getPreviewPanel().getHighPx());
        assertEquals("none", tested.getPreviewPanel().getElement().style.display);
        assertTrue(layer.isVisible());
        assertFalse(tested.isEnabled());
        verify(previewPanel, times(1)).removeAll();
    }
}
