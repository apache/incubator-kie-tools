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


package org.kie.workbench.common.stunner.client.lienzo.canvas.controls;

import java.util.function.Supplier;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.lienzo.canvas.LienzoPanel;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresCanvas;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresCanvasView;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresLayer;
import org.kie.workbench.common.stunner.client.lienzo.components.mediators.LienzoPanelMediators;
import org.kie.workbench.common.stunner.core.client.canvas.event.CanvasFocusedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.CanvasLostFocusEvent;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class LienzoMediatorsControlTest {

    @Mock
    private LienzoPanelMediators mediators;

    @Mock
    private WiresCanvas canvas;

    @Mock
    private WiresCanvasView canvasView;

    @Mock
    private LienzoPanel panel;

    @Mock
    private WiresLayer layer;

    private LienzoMediatorsControl tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        when(canvas.getView()).thenReturn(canvasView);
        when(canvasView.getPanel()).thenReturn(panel);
        when(canvasView.getLayer()).thenReturn(layer);
        this.tested = new LienzoMediatorsControl(mediators);
        tested.init(canvas);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testInit() {
        ArgumentCaptor<Supplier> canvasSupplier = ArgumentCaptor.forClass(Supplier.class);
        verify(mediators, times(1)).init(canvasSupplier.capture());
        assertEquals(canvas, canvasSupplier.getValue().get());
    }

    @Test
    public void testSetMinScale() {
        tested.setMinScale(0.4d);
        verify(mediators, times(1)).setMinScale(eq(0.4d));
    }

    @Test
    public void testSetMaxScale() {
        tested.setMaxScale(0.4d);
        verify(mediators, times(1)).setMaxScale(eq(0.4d));
    }

    @Test
    public void testSetZoomFactor() {
        tested.setZoomFactor(0.4d);
        verify(mediators, times(1)).setZoomFactor(eq(0.4d));
    }

    @Test
    public void testScale() {
        tested.scale(0.2d, 0.4d);
        verify(layer, times(1)).scale(0.2d, 0.4d);
        tested.scale(0.5d);
        verify(layer, times(1)).scale(0.5d);
    }

    @Test
    public void testTranslate() {
        tested.translate(0.2d, 0.4d);
        verify(layer, times(1)).translate(0.2d, 0.4d);
    }

    @Test
    public void testOnCanvasFocusedEvent() {
        CanvasFocusedEvent event = new CanvasFocusedEvent(canvas);
        tested.onCanvasFocusedEvent(event);
        verify(mediators, times(1)).enable();
        verify(mediators, never()).disable();
    }

    @Test
    public void testOnCanvasLostFocusEvent() {
        CanvasLostFocusEvent event = new CanvasLostFocusEvent(canvas);
        tested.onCanvasLostFocusEvent(event);
        verify(mediators, times(1)).disable();
        verify(mediators, never()).enable();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDestroy() {
        ArgumentCaptor<Supplier> canvasSupplier = ArgumentCaptor.forClass(Supplier.class);
        verify(mediators, times(1)).init(canvasSupplier.capture());
        assertEquals(canvas, canvasSupplier.getValue().get());
        tested.destroy();
        verify(mediators, times(1)).destroy();
    }
}
