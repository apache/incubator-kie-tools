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

import com.ait.lienzo.client.core.mediator.AbstractMediator;
import com.ait.lienzo.client.core.mediator.EventFilter;
import com.ait.lienzo.client.core.mediator.IEventFilter;
import com.ait.lienzo.client.core.mediator.MousePanMediator;
import com.ait.lienzo.client.core.mediator.MouseWheelZoomMediator;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.widget.panel.LienzoBoundsPanel;
import com.ait.lienzo.client.widget.panel.scrollbars.ScrollablePanel;
import com.ait.tooling.common.api.java.util.function.Supplier;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.shared.HandlerRegistration;

public class PanelMediators {

    static final IEventFilter EVENT_FILTER_ZOOM = EventFilter.CONTROL;
    static final IEventFilter EVENT_FILTER_PAN = EventFilter.ALT;
    static final double MIN_SCALE = 0.1;
    static final double MAX_SCALE = Double.MAX_VALUE;
    static final double ZOOM_FACTOR = 0.1;

    private Supplier<LienzoBoundsPanel> panelSupplier;
    private MouseWheelZoomMediator zoomMediator;
    private MousePanMediator panMediator;
    private PanelPreviewMediator previewMediator;
    private HandlerRegistration outHandler;

    public static PanelMediators build(final LienzoBoundsPanel panel) {
        return new PanelMediators().init(new Supplier<LienzoBoundsPanel>() {
            @Override
            public LienzoBoundsPanel get() {
                return panel;
            }
        });
    }

    public PanelMediators init(final Supplier<LienzoBoundsPanel> panelSupplier) {
        return init(panelSupplier,
                    new Supplier<PanelPreviewMediator>() {
                        @Override
                        public PanelPreviewMediator get() {
                            return PanelPreviewMediator.build((ScrollablePanel) panelSupplier.get());
                        }
                    });
    }

    public PanelMediators init(final Supplier<LienzoBoundsPanel> panelSupplier,
                               final Supplier<PanelPreviewMediator> previewMediatorBuilder) {
        this.panelSupplier = panelSupplier;
        final LienzoBoundsPanel panel = panelSupplier.get();

        final Viewport viewport = getViewport();

        zoomMediator = new MouseWheelZoomMediator(EVENT_FILTER_ZOOM)
                .setMinScale(MIN_SCALE)
                .setMaxScale(MAX_SCALE)
                .setZoomFactor(ZOOM_FACTOR);

        panMediator = new MousePanMediator(EVENT_FILTER_PAN)
                .setXConstrained(true)
                .setYConstrained(true);

        zoomMediator.setEnabled(true);
        panMediator.setEnabled(true);

        viewport.getMediators().push(zoomMediator);
        viewport.getMediators().push(panMediator);

        if (panel instanceof ScrollablePanel) {
            previewMediator = previewMediatorBuilder.get();
        }

        outHandler = panel.addMouseOutHandler(new MouseOutHandler() {
            @Override
            public void onMouseOut(MouseOutEvent mouseOutEvent) {
                disablePreview();
            }
        });
        return this;
    }

    public boolean enablePreview() {
        if (null == previewMediator) {
            return false;
        }
        zoomMediator.setEnabled(false);
        panMediator.setEnabled(false);
        previewMediator.enable();
        return true;
    }

    public void disablePreview() {
        if (null != previewMediator) {
            previewMediator.disable();
        }
        zoomMediator.setEnabled(true);
        panMediator.setEnabled(true);
    }

    public void destroy() {
        final Viewport viewport = getViewport();
        if (null != zoomMediator) {
            disableMediator(zoomMediator);
            zoomMediator.cancel();
            viewport.getMediators().remove(zoomMediator);
            zoomMediator = null;
        }
        if (null != panMediator) {
            disableMediator(panMediator);
            panMediator.cancel();
            viewport.getMediators().remove(panMediator);
            panMediator = null;
        }
        if (null != previewMediator) {
            previewMediator.removeHandler();
            previewMediator = null;
        }
        if (null != outHandler) {
            outHandler.removeHandler();
            outHandler = null;
        }
    }

    public MouseWheelZoomMediator getZoomMediator() {
        return zoomMediator;
    }

    public MousePanMediator getPanMediator() {
        return panMediator;
    }

    public PanelPreviewMediator getPreviewMediator() {
        return previewMediator;
    }

    private Layer getLayer() {
        return panelSupplier.get().getLayer();
    }

    private Viewport getViewport() {
        return getLayer().getViewport();
    }

    private static void disableMediator(final AbstractMediator mediator) {
        if (mediator.isEnabled()) {
            mediator.setEnabled(false);
        }
    }
}
