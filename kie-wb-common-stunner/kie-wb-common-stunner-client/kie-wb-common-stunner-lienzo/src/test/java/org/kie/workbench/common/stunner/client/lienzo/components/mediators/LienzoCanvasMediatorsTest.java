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

package org.kie.workbench.common.stunner.client.lienzo.components.mediators;

import java.util.function.Consumer;
import java.util.function.Supplier;

import com.ait.lienzo.client.core.mediator.MousePanMediator;
import com.ait.lienzo.client.core.mediator.MouseWheelZoomMediator;
import com.ait.lienzo.client.widget.panel.LienzoBoundsPanel;
import com.ait.lienzo.client.widget.panel.mediators.PanelMediators;
import com.ait.lienzo.client.widget.panel.mediators.PanelPreviewMediator;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.lienzo.canvas.LienzoCanvas;
import org.kie.workbench.common.stunner.client.lienzo.canvas.LienzoCanvasView;
import org.kie.workbench.common.stunner.client.lienzo.canvas.LienzoPanel;
import org.kie.workbench.common.stunner.client.lienzo.components.views.LienzoCanvasNotification;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.KeyEventHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.KeyboardControl;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyboardEvent;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.i18n.CoreTranslationMessages;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class LienzoCanvasMediatorsTest {

    @Mock
    private LienzoCanvasNotification notification;

    @Mock
    private PanelMediators mediators;

    @Mock
    private MouseWheelZoomMediator zoomMediator;

    @Mock
    private MousePanMediator panMediator;

    @Mock
    private PanelPreviewMediator previewMediator;

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
    private KeyEventHandler keyEventHandler;

    @Before
    public void setUp() {
        keyEventHandler = spy(new KeyEventHandler());
        when(canvas.getView()).thenReturn(canvasView);
        when(canvasView.getPanel()).thenReturn(panel);
        when(canvasView.getLienzoPanel()).thenReturn(panel);
        when(panel.getView()).thenReturn(panelView);
        when(mediators.getZoomMediator()).thenReturn(zoomMediator);
        when(mediators.getPanMediator()).thenReturn(panMediator);
        when(mediators.getPreviewMediator()).thenReturn(previewMediator);
        tested = new LienzoCanvasMediators(keyEventHandler,
                                           new ClientTranslationService(mock(TranslationService.class),
                                                                        mock(SessionManager.class),
                                                                        mock(DefinitionUtils.class)),
                                           notification,
                                           p -> mediators);
        tested.init(() -> canvas);
        tested.cursor = cursor;
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testInit() {
        assertEquals(mediators, tested.getMediators());
        ArgumentCaptor<Supplier> panelCaptor = ArgumentCaptor.forClass(Supplier.class);
        verify(notification, times(1)).init(panelCaptor.capture());
        assertEquals(panel, panelCaptor.getValue().get());
        verify(zoomMediator, times(1)).setScaleAboutPoint(eq(false));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testKeyBindings() {
        ArgumentCaptor<KeyboardControl.KeyShortcutCallback> callbackArgumentCaptor =
                ArgumentCaptor.forClass(KeyboardControl.KeyShortcutCallback.class);
        verify(keyEventHandler, times(1)).addKeyShortcutCallback(callbackArgumentCaptor.capture());
        KeyboardControl.KeyShortcutCallback callback = callbackArgumentCaptor.getValue();
        callback.onKeyUp(KeyboardEvent.Key.ALT);
        verify(mediators, times(1)).disablePreview();
        // CTRL.
        when(mediators.enablePreview()).thenReturn(false);
        callback.onKeyShortcut(KeyboardEvent.Key.CONTROL);
        verify(cursor, times(1)).accept(eq(LienzoCanvasMediators.CURSOR_ZOOM));
        // ALT.
        when(mediators.enablePreview()).thenReturn(false);
        callback.onKeyShortcut(KeyboardEvent.Key.ALT);
        verify(cursor, times(1)).accept(eq(LienzoCanvasMediators.CURSOR_PAN));
        // CTRL + ALT.
        when(mediators.enablePreview()).thenReturn(true);
        callback.onKeyShortcut(KeyboardEvent.Key.CONTROL, KeyboardEvent.Key.ALT);
        callback.onKeyShortcut(KeyboardEvent.Key.ALT, KeyboardEvent.Key.CONTROL);
        verify(cursor, times(2)).accept(eq(LienzoCanvasMediators.CURSOR_PREVIEW));
        verify(notification, times(2)).show(eq(CoreTranslationMessages.MEDIATOR_PREVIEW));
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
        verify(previewMediator, times(1)).setMaxScale(eq(3d));
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
    public void testDisable() {
        tested.disable();
        verify(mediators, times(1)).disablePreview();
        verify(cursor, times(1)).accept(eq(LienzoCanvasMediators.CURSOR_DEFAULT));
        verify(notification, times(1)).hide();
    }

    @Test
    public void testDestroy() {
        tested.destroy();
        verify(mediators, times(1)).destroy();
        assertNull(tested.getMediators());
    }
}
