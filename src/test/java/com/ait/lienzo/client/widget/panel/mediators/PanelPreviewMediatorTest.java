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

import com.ait.lienzo.client.core.mediator.MouseBoxZoomMediator;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Scene;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.client.widget.panel.Bounds;
import com.ait.lienzo.client.widget.panel.LienzoBoundsPanel;
import com.ait.lienzo.client.widget.panel.LienzoPanel;
import com.ait.lienzo.client.widget.panel.impl.LienzoPanelImpl;
import com.ait.lienzo.client.widget.panel.impl.PreviewLayer;
import com.ait.lienzo.client.widget.panel.scrollbars.ScrollablePanel;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.ait.tooling.common.api.java.util.function.Consumer;
import com.ait.tooling.common.api.java.util.function.Supplier;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.IsWidget;
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
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
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
    private AbsolutePanel panelContainer;

    @Mock
    private AbsolutePanel scrollPanel;

    private PanelPreviewMediator tested;
    private LienzoPanel previewPanel;
    private Layer layer;

    @Before
    public void setUp() {
        Scene scene = new Scene();
        layer = spy(new Layer());
        scene.add(layer);
        when(panel.getLayer()).thenReturn(layer);
        when(panel.getDomElementContainer()).thenReturn(panelContainer);
        when(panel.getScrollPanel()).thenReturn(scrollPanel);
        when(panel.getWidthPx()).thenReturn(WIDTH);
        when(panel.getHeightPx()).thenReturn(HEIGHT);

        previewPanel = spy(new LienzoPanelImpl(1, 1));

        tested = new PanelPreviewMediator(new Supplier<LienzoBoundsPanel>() {
            @Override
            public LienzoBoundsPanel get() {
                return panel;
            }
        }, new Consumer<IsWidget>() {
            @Override
            public void accept(IsWidget widget) {
                panelContainer.add(widget);
            }
        }, new Supplier<LienzoPanel>() {
            @Override
            public LienzoPanel get() {
                return previewPanel;
            }
        });
        tested.setMaxScale(MAX_SCALE);
    }

    @Test
    public void testConstruction() {
        verify(panelContainer, times(1)).add(any(IsWidget.class));
        assertNotNull(tested.getPreviewLayer());
        PreviewLayer previewLayer = tested.getPreviewLayer();
        assertTrue(previewLayer.isListening());
        assertTrue(previewLayer.isTransformable());
        assertNotNull(tested.getPreviewPanel());
        LienzoPanel previewPanel = tested.getPreviewPanel();
        assertFalse(previewPanel.isVisible());
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
        Transform transform = new Transform().translate(2, 3).scale(4, 5);
        tested.getMediator().getOnTransform().accept(transform);
        verify(viewport, times(1)).setTransform(eq(new Transform().translate(2, 3).scale(4, 5)));
        assertFalse(tested.getMediator().isEnabled());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testEnable() {
        Element previewPanelElement = mock(Element.class);
        Style previewPanelStyle = mock(Style.class);
        when(previewPanel.getElement()).thenReturn(previewPanelElement);
        when(previewPanelElement.getStyle()).thenReturn(previewPanelStyle);
        when(panel.getLayerBounds()).thenReturn(Bounds.build(0, 0, WIDTH / 2, HEIGHT / 2));
        tested.enable();
        assertTrue(tested.getPreviewLayer().isListening());
        assertFalse(layer.isListening());
        assertFalse(layer.isVisible());
        verify(previewPanelStyle, times(1)).setPosition(eq(Style.Position.ABSOLUTE));
        verify(previewPanelStyle, times(1)).setTop(eq(0d), eq(Style.Unit.PX));
        verify(previewPanelStyle, times(1)).setLeft(eq(0d), eq(Style.Unit.PX));
        verify(previewPanelStyle, times(1)).setBorderStyle(eq(Style.BorderStyle.NONE));
        verify(previewPanelStyle, times(1)).setBackgroundColor(eq(PanelPreviewMediator.PREVIEW_BG_COLOR));
        verify(previewPanel, times(1)).setPixelSize(WIDTH, HEIGHT);
        verify(previewPanel, times(1)).setVisible(eq(false));
        Transform previewTransform = tested.getPreviewLayer().getViewport().getTransform();
        assertEquals(0.5833333333333334d, previewTransform.getScaleX(), 0d);
        assertEquals(0.5833333333333334d, previewTransform.getScaleY(), 0d);
        assertEquals(250d, previewTransform.getTranslateX(), 0d);
        assertEquals(250d, previewTransform.getTranslateY(), 0d);
        ArgumentCaptor<Supplier> transformCaptor = ArgumentCaptor.forClass(Supplier.class);
        verify(layer, times(1)).drawWithTransforms(eq(tested.getPreviewLayer().getContext()),
                                                   eq(1d),
                                                   eq(new BoundingBox(0, 0, WIDTH, HEIGHT)),
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
        assertEquals(1, tested.getPreviewPanel().getWidthPx());
        assertEquals(1, tested.getPreviewPanel().getHeightPx());
        assertFalse(tested.getPreviewPanel().isVisible());
        assertTrue(layer.isVisible());
        assertFalse(tested.isEnabled());
    }

    @Test
    public void testOnRemoveHandler() {
        tested.removeHandler();
        assertFalse(tested.getMediator().isEnabled());
        assertNull(tested.getMediator().getOnTransform());
        assertEquals(0, tested.getPreviewLayer().length());
        assertEquals(1, tested.getPreviewPanel().getWidthPx());
        assertEquals(1, tested.getPreviewPanel().getHeightPx());
        assertFalse(tested.getPreviewPanel().isVisible());
        assertTrue(layer.isVisible());
        assertFalse(tested.isEnabled());
        verify(previewPanel, times(1)).removeFromParent();
    }
}
