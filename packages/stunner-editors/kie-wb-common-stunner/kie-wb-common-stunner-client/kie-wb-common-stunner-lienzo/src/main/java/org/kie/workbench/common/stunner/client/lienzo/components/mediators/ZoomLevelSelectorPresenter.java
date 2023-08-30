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

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.widget.panel.impl.ScrollablePanel;
import com.ait.lienzo.client.widget.panel.util.PanelTransformUtils;
import com.ait.lienzo.tools.client.event.EventType;
import com.ait.lienzo.tools.client.event.HandlerRegistration;
import com.ait.lienzo.tools.client.event.MouseEventUtil;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.IsWidget;
import elemental2.dom.Element;
import elemental2.dom.EventListener;
import jsinterop.base.Js;
import org.kie.workbench.common.stunner.client.lienzo.canvas.LienzoCanvas;
import org.kie.workbench.common.stunner.client.lienzo.canvas.LienzoCanvasView;
import org.kie.workbench.common.stunner.client.lienzo.canvas.LienzoPanel;
import org.kie.workbench.common.stunner.core.client.components.views.FloatingView;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.i18n.CoreTranslationMessages;

import static com.ait.lienzo.client.widget.panel.util.PanelTransformUtils.computeLevel;
import static com.ait.lienzo.client.widget.panel.util.PanelTransformUtils.computeZoomLevelFitToWidth;
import static com.ait.lienzo.client.widget.panel.util.PanelTransformUtils.setScaleLevel;

@Dependent
public class ZoomLevelSelectorPresenter {

    private static final double LEVEL_STEP = 0.1d;
    static final String LEVEL_25 = "25%";
    static final String LEVEL_50 = "50%";
    static final String LEVEL_75 = "75%";
    static final String LEVEL_100 = "100%";
    static final String LEVEL_150 = "150%";
    static final String LEVEL_200 = "200%";
    static final String ON_MOUSE_OVER = EventType.MOUSE_OVER.getType();

    private final ClientTranslationService translationService;
    private final FloatingView<IsWidget> floatingView;
    private final ZoomLevelSelector selector;
    private final Element selectorElement;
    private Supplier<LienzoCanvas> canvas;
    private double minScale;
    private double maxScale;
    private double zoomFactor;
    private EventListener panelResizeEventListener;
    private EventListener selectorMouseOverEventListener;
    private HandlerRegistration transformChangedHandler;

    private Timer hideTimer;
    private boolean zoomLevelInit = true;

    @Inject
    public ZoomLevelSelectorPresenter(final ClientTranslationService translationService,
                                      final FloatingView<IsWidget> floatingView,
                                      final ZoomLevelSelector selector) {
        this(translationService, floatingView, selector, Js.cast(selector.asWidget().getElement()));
    }

    ZoomLevelSelectorPresenter(final ClientTranslationService translationService,
                               final FloatingView<IsWidget> floatingView,
                               final ZoomLevelSelector selector,
                               final Element selectorElement) {
        this.translationService = translationService;
        this.floatingView = floatingView;
        this.selector = selector;
        this.selectorElement = selectorElement;
        this.minScale = 0;
        this.maxScale = Double.MAX_VALUE;
        this.zoomFactor = LEVEL_STEP;
    }

    public ZoomLevelSelectorPresenter init(final Supplier<LienzoCanvas> canvas) {
        floatingView
                .clearTimeOut()
                .setOffsetX(0)
                .setOffsetY(0)
                .hide();
        hideTimer = new Timer() {
            @Override
            public void run() {
                hide();
            }
        };
        this.canvas = canvas;
        final Layer layer = getLayer();
        final LienzoPanel panel = getPanel();
        selector
                .setText(parseLevel(1))
                .dropUp()
                .onReset(this::reset)
                .onIncreaseLevel(this::increaseLevel)
                .onDecreaseLevel(this::decreaseLevel)
                .add(LEVEL_25, () -> setLevel(0.25))
                .add(LEVEL_50, () -> setLevel(0.5))
                .add(LEVEL_75, () -> setLevel(0.75))
                .add(LEVEL_100, () -> setLevel(1))
                .add(LEVEL_150, () -> setLevel(1.5))
                .add(LEVEL_200, () -> setLevel(2))
                .add(translationService.getNotNullValue(CoreTranslationMessages.FIT),
                     () -> setLevel(computeZoomLevelFitToWidth(panel.getView())));

        floatingView.add(selector);

        if (panel.getView() instanceof ScrollablePanel) {
            final ScrollablePanel scrollablePanel = (ScrollablePanel) panel.getView();
            panelResizeEventListener =
                    scrollablePanel.addResizeEventListener(evt -> onPanelResize(scrollablePanel.getWidePx(),
                                                                                scrollablePanel.getHighPx()));
        }

        reposition();

        transformChangedHandler = layer.getViewport().addViewportTransformChangedHandler(event -> onViewportTransformChanged());

        selectorMouseOverEventListener = mouseOverEvent -> cancelHide();
        selectorElement.addEventListener(ON_MOUSE_OVER, selectorMouseOverEventListener);

        return this;
    }

    public ZoomLevelSelectorPresenter setMinScale(double minScale) {
        this.minScale = minScale;
        return this;
    }

    public ZoomLevelSelectorPresenter setMaxScale(double maxScale) {
        this.maxScale = maxScale;
        return this;
    }

    public ZoomLevelSelectorPresenter setZoomFactor(double zoomFactor) {
        this.zoomFactor = zoomFactor;
        return this;
    }

    public ZoomLevelSelectorPresenter at(final double x,
                                         final double y) {
        floatingView.setX(x).setY(y);
        return this;
    }

    public ZoomLevelSelectorPresenter show() {
        if (zoomLevelInit) {
            zoomLevelInit = false;
        } else {
            cancelHide();
            floatingView.show();
        }
        return this;
    }

    public ZoomLevelSelectorPresenter hide() {
        floatingView.hide();
        return this;
    }

    void cancelHide() {
        hideTimer.cancel();
    }

    void scheduleHide() {
        cancelHide();
        hideTimer.schedule(250);
    }

    public void destroy() {
        cancelHide();
        if (null != panelResizeEventListener) {
            ((ScrollablePanel) getPanel().getView()).removeResizeEventListener(panelResizeEventListener);
            panelResizeEventListener = null;
        }
        if (null != transformChangedHandler) {
            transformChangedHandler.removeHandler();
            transformChangedHandler = null;
        }
        if (null != selectorMouseOverEventListener) {
            selectorElement.removeEventListener(ON_MOUSE_OVER, selectorMouseOverEventListener);
            selectorMouseOverEventListener = null;
        }
        floatingView.destroy();
        canvas = null;
        hideTimer = null;
    }

    private void onViewportTransformChanged() {
        final double level = computeLevel(getLayer().getViewport());
        updateSelectorLevel(level);
    }

    private void reposition() {
        final LienzoPanel panel = getPanel();
        onPanelResize(panel.getView().getWidePx(),
                      panel.getView().getHighPx());
    }

    private ZoomLevelSelectorPresenter onPanelResize(final double width,
                                                     final double height) {
        final LienzoPanel panel = getPanel();
        final int absoluteLeft = MouseEventUtil.getAbsoluteLeft(panel.getView().getElement());
        final int absoluteTop = MouseEventUtil.getAbsoluteTop(panel.getView().getElement());
        final double x = absoluteLeft + width - 174;
        final double y = absoluteTop + height - 50;
        return at(x, y);
    }

    private double increaseLevel() {
        return setLevel(getLevel() + zoomFactor);
    }

    private double decreaseLevel() {
        return setLevel(getLevel() - zoomFactor);
    }

    private double getLevel() {
        return computeLevel(getLayer().getViewport());
    }

    private void reset() {
        PanelTransformUtils.reset(getLayer().getViewport());
        getLayer().batch();
    }

    private double setLevel(final double level) {
        if (level < minScale) {
            return _setLevel(minScale);
        } else if (level > maxScale) {
            return _setLevel(maxScale);
        } else {
            return _setLevel(level);
        }
    }

    private double _setLevel(final double level) {
        setScaleLevel(getLayer().getViewport(), level);
        getLayer().batch();
        updateSelectorLevel(level);
        return level;
    }

    private void updateSelectorLevel(final double level) {
        selector.setText(parseLevel(level));
    }

    private Layer getLayer() {
        return ((LienzoCanvasView) canvas.get().getView()).getLayer().getLienzoLayer();
    }

    private LienzoPanel getPanel() {
        return (LienzoPanel) canvas.get().getView().getPanel();
    }

    private static String parseLevel(final double level) {
        return Double.valueOf(level * 100).intValue() + "%";
    }
}
