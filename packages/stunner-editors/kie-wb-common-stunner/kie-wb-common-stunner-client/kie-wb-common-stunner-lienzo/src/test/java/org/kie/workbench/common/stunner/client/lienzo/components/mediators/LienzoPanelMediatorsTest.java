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


package org.kie.workbench.common.stunner.client.lienzo.components.mediators;

import java.util.function.Supplier;

import com.ait.lienzo.client.widget.panel.LienzoBoundsPanel;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import elemental2.dom.HTMLDivElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.lienzo.canvas.LienzoPanel;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresCanvas;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresCanvasView;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresLayer;
import org.kie.workbench.common.stunner.client.lienzo.components.views.LienzoPanelFocusHandler;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class LienzoPanelMediatorsTest {

    @Mock
    private LienzoCanvasMediators mediators;

    @Mock
    private ZoomLevelSelectorPresenter selector;

    @Mock
    private WiresCanvas canvas;

    @Mock
    private WiresCanvasView canvasView;

    @Mock
    private LienzoPanel panel;

    @Mock
    private LienzoBoundsPanel panelView;

    @Mock
    private WiresLayer layer;

    @Mock
    private HTMLDivElement panelViewElement;

    private LienzoPanelMediators tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        when(canvas.getView()).thenReturn(canvasView);
        when(canvasView.getPanel()).thenReturn(panel);
        when(canvasView.getLayer()).thenReturn(layer);
        when(panel.getView()).thenReturn(panelView);
        when(panelView.getElement()).thenReturn(panelViewElement);
        this.tested = new LienzoPanelMediators(mediators, selector);
        tested.init(() -> canvas);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testInit() {
        ArgumentCaptor<Supplier> canvasSupplier = ArgumentCaptor.forClass(Supplier.class);
        verify(mediators, times(1)).init(canvasSupplier.capture());
        assertEquals(canvas, canvasSupplier.getValue().get());
        verify(selector, times(1)).init(canvasSupplier.capture());
        assertEquals(canvas, canvasSupplier.getValue().get());
        verify(selector, times(1)).setZoomFactor(eq(LienzoPanelMediators.ZOOM_FACTOR));
        verify(mediators, times(1)).setZoomFactor(eq(LienzoPanelMediators.ZOOM_FACTOR));
        verify(selector, times(1)).setMinScale(eq(LienzoPanelMediators.MIN_SCALE));
        verify(mediators, times(1)).setMinScale(eq(LienzoPanelMediators.MIN_SCALE));
        verify(selector, times(1)).setMaxScale(eq(LienzoPanelMediators.MAX_SCALE));
        verify(mediators, times(1)).setMaxScale(eq(LienzoPanelMediators.MAX_SCALE));
        verify(selector, times(1)).show();
    }

    @Test
    public void testSetMinScale() {
        tested.setMinScale(0.4d);
        verify(selector, times(1)).setMinScale(eq(0.4d));
        verify(mediators, times(1)).setMinScale(eq(0.4d));
    }

    @Test
    public void testSetMaxScale() {
        tested.setMaxScale(0.4d);
        verify(selector, times(1)).setMaxScale(eq(0.4d));
        verify(mediators, times(1)).setMaxScale(eq(0.4d));
    }

    @Test
    public void testSetZoomFactor() {
        tested.setZoomFactor(0.4d);
        verify(selector, times(1)).setZoomFactor(eq(0.4d));
        verify(mediators, times(1)).setZoomFactor(eq(0.4d));
    }

    @Test
    public void testEnable() {
        tested.enable();
        verify(mediators, atLeastOnce()).enable();
        verify(selector, atLeastOnce()).show();
        verify(selector, never()).hide();
    }

    @Test
    public void testDisable() {
        tested.disable();
        verify(mediators, atLeastOnce()).disable();
        verify(selector, times(1)).scheduleHide();
        verify(mediators, times(1)).disable();
    }

    @Test
    public void testDestroy() {
        tested.destroy();
        LienzoPanelFocusHandler focusHandler = mock(LienzoPanelFocusHandler.class);
        tested.focusHandler = focusHandler;
        tested.destroy();
        verify(focusHandler, times(1)).clear();
        assertNull(tested.focusHandler);
    }
}
