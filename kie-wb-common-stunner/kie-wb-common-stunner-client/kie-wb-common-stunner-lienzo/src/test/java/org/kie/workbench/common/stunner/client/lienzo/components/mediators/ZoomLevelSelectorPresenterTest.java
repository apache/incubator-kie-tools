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

import com.ait.lienzo.client.core.event.ViewportTransformChangedHandler;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.client.widget.panel.Bounds;
import com.ait.lienzo.client.widget.panel.LienzoBoundsPanel;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.lienzo.canvas.LienzoCanvas;
import org.kie.workbench.common.stunner.client.lienzo.canvas.LienzoCanvasView;
import org.kie.workbench.common.stunner.client.lienzo.canvas.LienzoPanel;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.components.views.FloatingView;
import org.kie.workbench.common.stunner.core.client.components.views.FloatingWidgetView;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.i18n.CoreTranslationMessages;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.uberfire.mvp.Command;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class ZoomLevelSelectorPresenterTest {

    @Mock
    private LienzoCanvas canvas;

    @Mock
    private LienzoCanvasView canvasView;

    @Mock
    private LienzoPanel panel;

    @Mock
    private LienzoBoundsPanel panelView;

    @Mock
    private org.kie.workbench.common.stunner.client.lienzo.canvas.LienzoLayer stunnerLayer;

    @Mock
    private Viewport viewport;

    @Mock
    private ZoomLevelSelector.View selectorView;

    private ZoomLevelSelectorPresenter tested;
    private ClientTranslationService translationService;
    private FloatingView<IsWidget> floatingView;
    private Layer layer;
    private ZoomLevelSelector selector;

    @Before
    public void setUp() {
        translationService = new ClientTranslationService(mock(TranslationService.class),
                                                          mock(SessionManager.class),
                                                          mock(DefinitionUtils.class));
        selector = spy(new ZoomLevelSelector(selectorView));
        layer = spy(new Layer());
        when(layer.getViewport()).thenReturn(viewport);
        when(canvas.getView()).thenReturn(canvasView);
        when(canvasView.getPanel()).thenReturn(panel);
        when(canvasView.getLienzoPanel()).thenReturn(panel);
        when(canvasView.getLayer()).thenReturn(stunnerLayer);
        when(stunnerLayer.getLienzoLayer()).thenReturn(layer);
        when(panel.getView()).thenReturn(panelView);
        when(selectorView.asWidget()).thenReturn(mock(Widget.class));
        floatingView = spy(new FloatingWidgetView());
        tested = new ZoomLevelSelectorPresenter(translationService,
                                                floatingView,
                                                selector);
        tested.construct();
    }

    @Test
    public void testConstruct() {
        verify(floatingView, times(1)).clearTimeOut();
        verify(floatingView, times(1)).setOffsetX(eq(0d));
        verify(floatingView, times(1)).setOffsetY(eq(0d));
        verify(floatingView, times(1)).hide();
        verify(floatingView, never()).show();
    }

    @Test
    public void testInit() {
        tested.init(() -> canvas);
        verify(selector, times(1)).setText(eq("100%"));
        verify(selector, times(1)).dropUp();
        verify(selector, times(1)).onReset(any(Command.class));
        verify(selector, times(1)).onIncreaseLevel(any(Command.class));
        verify(selector, times(1)).onDecreaseLevel(any(Command.class));
        verify(selector, times(1)).add(eq(ZoomLevelSelectorPresenter.LEVEL_25), any(Command.class));
        verify(selector, times(1)).add(eq(ZoomLevelSelectorPresenter.LEVEL_50), any(Command.class));
        verify(selector, times(1)).add(eq(ZoomLevelSelectorPresenter.LEVEL_75), any(Command.class));
        verify(selector, times(1)).add(eq(ZoomLevelSelectorPresenter.LEVEL_100), any(Command.class));
        verify(selector, times(1)).add(eq(ZoomLevelSelectorPresenter.LEVEL_150), any(Command.class));
        verify(selector, times(1)).add(eq(ZoomLevelSelectorPresenter.LEVEL_200), any(Command.class));
        verify(selector, times(1)).add(eq(CoreTranslationMessages.FIT), any(Command.class));
        verify(floatingView, times(1)).add(eq(selector));
        verify(viewport, times(1)).addViewportTransformChangedHandler(any(ViewportTransformChangedHandler.class));
        verify(panelView, never()).addMouseOutHandler(any(MouseOutHandler.class));
        verify(panelView, never()).addMouseOverHandler(any(MouseOverHandler.class));
    }

    @Test
    public void testAt() {
        tested.at(50d, 25d);
        verify(floatingView, times(1)).setX(eq(50d));
        verify(floatingView, times(1)).setY(eq(25d));
    }

    @Test
    public void testShow() {
        tested.show();
        verify(floatingView, times(1)).show();
    }

    @Test
    public void testHide() {
        tested.hide();
        verify(floatingView, atLeastOnce()).hide();
    }

    @Test
    public void testDestroy() {
        tested.destroy();
        verify(floatingView, times(1)).destroy();
    }

    @Test
    public void testOnReset() {
        tested.init(() -> canvas);
        selector.onReset();
        verify(viewport, times(1)).setTransform(eq(new Transform()));
        verify(layer, times(1)).batch();
    }

    @Test
    public void testOnIncreaseLevel() {
        Transform viewportTransform = new Transform().translate(15, 35.5).scale(0.1, 0.3);
        when(viewport.getTransform()).thenReturn(viewportTransform);
        tested.init(() -> canvas);
        selector.onIncreaseLevel();
        verifyApplyTransform(15d, 35.5d, 0.2d, 0.2d);
    }

    @Test
    public void testOnDecreaseLevel() {
        Transform viewportTransform = new Transform().translate(15, 35.5).scale(0.1, 0.3);
        when(viewport.getTransform()).thenReturn(viewportTransform);
        tested.init(() -> canvas);
        selector.onDecreaseLevel();
        verifyApplyTransform(15d, 35.5d, 0d, 0d);
    }

    @Test
    public void testOnLevel25() {
        verifyApplyLevel(ZoomLevelSelectorPresenter.LEVEL_25, 0.25d);
    }

    @Test
    public void testOnLevel50() {
        verifyApplyLevel(ZoomLevelSelectorPresenter.LEVEL_50, 0.5d);
    }

    @Test
    public void testOnLevel75() {
        verifyApplyLevel(ZoomLevelSelectorPresenter.LEVEL_75, 0.75d);
    }

    @Test
    public void testOnLevel100() {
        verifyApplyLevel(ZoomLevelSelectorPresenter.LEVEL_100, 1d);
    }

    @Test
    public void testOnLevel150() {
        verifyApplyLevel(ZoomLevelSelectorPresenter.LEVEL_150, 1.5d);
    }

    @Test
    public void testOnLevel200() {
        verifyApplyLevel(ZoomLevelSelectorPresenter.LEVEL_200, 2d);
    }

    @Test
    public void testOnLevelFit() {
        when(panelView.getWidthPx()).thenReturn(300);
        when(panelView.getHeightPx()).thenReturn(600);
        when(panelView.getLayerBounds()).thenReturn(Bounds.build(0, 0, 600, 900));
        verifyApplyLevel(CoreTranslationMessages.FIT, 0.5d);
    }

    private void verifyApplyLevel(final String text,
                                  final double level) {
        Transform viewportTransform = new Transform().translate(0, 0).scale(0.1, 0.3);
        when(viewport.getTransform()).thenReturn(viewportTransform);
        tested.init(() -> canvas);
        ArgumentCaptor<Command> levelCaptor = ArgumentCaptor.forClass(Command.class);
        verify(selector, times(1)).add(eq(text), levelCaptor.capture());
        levelCaptor.getValue().execute();
        verifyApplyTransform(0d, 0d, level, level);
    }

    private void verifyApplyTransform(final double tx,
                                      final double ty,
                                      final double sx,
                                      final double sy) {
        ArgumentCaptor<Transform> tCaptor = ArgumentCaptor.forClass(Transform.class);
        verify(viewport, times(1)).setTransform(tCaptor.capture());
        Transform transform = tCaptor.getValue();
        assertEquals(sx, transform.getScaleX(), 0d);
        assertEquals(sy, transform.getScaleY(), 0d);
        assertEquals(tx, transform.getTranslateX(), 0d);
        assertEquals(ty, transform.getTranslateY(), 0d);
        verify(layer, times(1)).batch();
    }
}
