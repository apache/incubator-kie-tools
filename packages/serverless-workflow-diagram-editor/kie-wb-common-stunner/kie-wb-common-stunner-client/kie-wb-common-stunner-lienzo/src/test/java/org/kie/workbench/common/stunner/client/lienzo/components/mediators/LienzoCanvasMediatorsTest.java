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

import java.util.function.Consumer;

import com.ait.lienzo.client.core.mediator.MousePanMediator;
import com.ait.lienzo.client.core.mediator.MouseWheelZoomMediator;
import com.ait.lienzo.client.widget.panel.LienzoBoundsPanel;
import com.ait.lienzo.client.widget.panel.mediators.PanelMediators;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import elemental2.dom.HTMLDivElement;
import jakarta.enterprise.event.Event;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.lienzo.canvas.LienzoCanvas;
import org.kie.workbench.common.stunner.client.lienzo.canvas.LienzoCanvasView;
import org.kie.workbench.common.stunner.client.lienzo.canvas.LienzoPanel;
import org.kie.workbench.common.stunner.client.lienzo.components.mediators.preview.TogglePreviewEvent;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.KeyEventHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.KeyboardControl;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyboardEvent;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class LienzoCanvasMediatorsTest {

    @Mock
    private PanelMediators mediators;

    @Mock
    private MouseWheelZoomMediator zoomMediator;

    @Mock
    private MousePanMediator panMediator;

    @Mock
    private LienzoCanvas canvas;

    @Mock
    private LienzoCanvasView canvasView;

    @Mock
    private LienzoPanel panel;

    @Mock
    private LienzoBoundsPanel panelView;

    @Mock
    private Consumer<AbstractCanvas.Cursors> cursor;

    private LienzoCanvasMediators tested;

    @Mock
    private KeyEventHandler keyEventHandler;

    @Mock
    private HTMLDivElement element;

    @Mock
    private Event<TogglePreviewEvent> togglePreviewEvent;

    @Before
    public void setUp() {
        when(keyEventHandler.setTimerDelay(150)).thenReturn(keyEventHandler);
        when(canvas.getView()).thenReturn(canvasView);
        when(canvasView.getPanel()).thenReturn(panel);
        when(canvasView.getLienzoPanel()).thenReturn(panel);
        when(panel.getView()).thenReturn(panelView);
        when(panelView.getElement()).thenReturn(element);
        when(mediators.getZoomMediator()).thenReturn(zoomMediator);
        when(mediators.getPanMediator()).thenReturn(panMediator);
        tested = new LienzoCanvasMediators(keyEventHandler,
                                           p -> mediators,
                                           togglePreviewEvent);
        tested.init(() -> canvas);
        tested.cursor = cursor;
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testInit() {
        assertEquals(mediators, tested.getMediators());
        verify(zoomMediator, times(1)).setScaleAboutPoint(eq(true));
    }

    @Test
    public void testKeyBindings() {
        ArgumentCaptor<KeyboardControl.KeyShortcutCallback> callbackArgumentCaptor =
                ArgumentCaptor.forClass(KeyboardControl.KeyShortcutCallback.class);
        verify(keyEventHandler, times(4)).addKeyShortcutCallback(callbackArgumentCaptor.capture());
        KeyboardControl.KeyShortcutCallback callback = callbackArgumentCaptor.getValue();
        callback.onKeyUp(KeyboardEvent.Key.ALT);
        // CTRL.
        callback.onKeyShortcut(KeyboardEvent.Key.CONTROL);
        verify(cursor, never()).accept(any());
        // ALT.
        callback.onKeyShortcut(KeyboardEvent.Key.ALT);
        verify(cursor, never()).accept(any());
        // CTRL + ALT.
        callback.onKeyShortcut(KeyboardEvent.Key.CONTROL, KeyboardEvent.Key.ALT);
        callback.onKeyShortcut(KeyboardEvent.Key.ALT, KeyboardEvent.Key.CONTROL);
        verify(cursor, never()).accept(any());
    }

    @Test
    public void testSetMinScale() {
        tested.setMinScale(0.3d);
        verify(zoomMediator, times(1)).setMinScale(eq(0.3d));
    }

    @Test
    public void testSetMaxScale() {
        tested.setMaxScale(3d);
        verify(zoomMediator, times(1)).setMaxScale(eq(3d));
    }

    @Test
    public void testSetZoomFactor() {
        tested.setZoomFactor(0.5d);
        verify(zoomMediator, times(1)).setZoomFactor(eq(0.5d));
    }

    @Test
    public void testSetScaleAboutPoint() {
        tested.setScaleAboutPoint(true);
        verify(zoomMediator, atLeastOnce()).setScaleAboutPoint(eq(true));
        tested.setScaleAboutPoint(false);
        verify(zoomMediator, atLeastOnce()).setScaleAboutPoint(eq(false));
    }

    @Test
    public void testDestroy() {
        tested.destroy();
        verify(mediators, times(1)).destroy();
        assertNull(tested.getMediators());
    }
}
