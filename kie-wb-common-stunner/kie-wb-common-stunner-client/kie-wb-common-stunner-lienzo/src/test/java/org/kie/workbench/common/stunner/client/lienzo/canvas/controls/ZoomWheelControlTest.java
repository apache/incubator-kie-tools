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

package org.kie.workbench.common.stunner.client.lienzo.canvas.controls;

import com.ait.lienzo.client.core.event.AbstractNodeHumanInputEvent;
import com.ait.lienzo.client.core.mediator.IEventFilter;
import com.ait.lienzo.client.core.mediator.MouseWheelZoomMediator;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresCanvas;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresCanvasView;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresLayer;
import org.mockito.Mock;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class ZoomWheelControlTest {

    @Mock
    private WiresCanvas canvas;

    @Mock
    private WiresCanvasView canvasView;

    @Mock
    private WiresLayer wiresLayer;

    private ZoomWheelControl tested;
    private Viewport viewport;
    private Layer layer;

    @Before
    public void setup() throws Exception {
        viewport = new Viewport();
        layer = new Layer();
        viewport.getScene().add(layer);
        when(canvas.getView()).thenReturn(canvasView);
        when(canvasView.getLayer()).thenReturn(wiresLayer);
        when(wiresLayer.getLienzoLayer()).thenReturn(layer);
        this.tested = new ZoomWheelControl();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testInit() {
        tested.init(canvas);
        assertNotNull(tested.getMediator());
        assertTrue(tested.getMediator() instanceof MouseWheelZoomMediator);
        MouseWheelZoomMediator zoomMediator = (MouseWheelZoomMediator) tested.getMediator();
        assertFalse(zoomMediator.isScaleAboutPoint());
        IEventFilter eventFilter = zoomMediator.getEventFilter();
        AbstractNodeHumanInputEvent event = mock(AbstractNodeHumanInputEvent.class);
        when(event.isControlKeyDown()).thenReturn(true);
        assertTrue(eventFilter.test(event));
        when(event.isControlKeyDown()).thenReturn(false);
        assertFalse(eventFilter.test(event));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSetScaleRange() {
        MouseWheelZoomMediator mediator = mock(MouseWheelZoomMediator.class);
        tested.setMediator(mediator);
        tested.setMaxScale(0.12d);
        tested.setMinScale(0.05d);
        verify(mediator, times(1)).setMaxScale(eq(0.12d));
        verify(mediator, times(1)).setMinScale(eq(0.05d));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSetScaleFactor() {
        MouseWheelZoomMediator mediator = mock(MouseWheelZoomMediator.class);
        tested.setMediator(mediator);
        tested.setZoomFactory(0.666d);
        verify(mediator, times(1)).setZoomFactor(eq(0.666d));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testScaleBySingleFactor() {
        tested.init(canvas);
        tested.scale(0.666d);
        verify(wiresLayer, times(1)).scale(eq(0.666d));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testScaleByMultipleFactors() {
        tested.init(canvas);
        tested.scale(0.666d, 0.333d);
        verify(wiresLayer, times(1)).scale(eq(0.666d), eq(0.333d));
    }
}
