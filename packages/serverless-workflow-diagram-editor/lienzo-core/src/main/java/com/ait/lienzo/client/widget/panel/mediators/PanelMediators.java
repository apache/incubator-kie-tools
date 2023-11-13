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

import java.util.function.Supplier;

import com.ait.lienzo.client.core.mediator.AbstractMediator;
import com.ait.lienzo.client.core.mediator.EventFilter;
import com.ait.lienzo.client.core.mediator.IEventFilter;
import com.ait.lienzo.client.core.mediator.MousePanMediator;
import com.ait.lienzo.client.core.mediator.MouseWheelZoomMediator;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.widget.panel.LienzoBoundsPanel;
import elemental2.dom.HTMLDivElement;

public class PanelMediators {

    static final IEventFilter DEFAULT_EVENT_FILTER_ZOOM = EventFilter.CONTROL;
    static final IEventFilter DEFAULT_EVENT_FILTER_PAN = EventFilter.ALT;
    static final double MIN_SCALE = 0.1;
    static final double MAX_SCALE = Double.MAX_VALUE;
    static final double ZOOM_FACTOR = 0.1;

    private Supplier<LienzoBoundsPanel> panelSupplier;
    private MouseWheelZoomMediator zoomMediator;
    private MousePanMediator panMediator;

    HTMLDivElement panelElement;

    public static PanelMediators build(final LienzoBoundsPanel panel) {

        return new PanelMediators().init(() -> panel,
                                         DEFAULT_EVENT_FILTER_ZOOM,
                                         DEFAULT_EVENT_FILTER_PAN);
    }

    public static PanelMediators build(final LienzoBoundsPanel panel,
                                       IEventFilter eventFilterZoom,
                                       IEventFilter eventFilterPan) {
        return (new PanelMediators()).init(() -> panel,
                                           eventFilterZoom,
                                           eventFilterPan);
    }

    public PanelMediators init(final Supplier<LienzoBoundsPanel> panelSupplier,
                               IEventFilter eventFilterZoom,
                               IEventFilter eventFilterPan) {
        this.panelSupplier = panelSupplier;
        final LienzoBoundsPanel panel = panelSupplier.get();
        this.panelElement = panel.getElement();
        final Viewport viewport = getViewport();

        zoomMediator = new MouseWheelZoomMediator(eventFilterZoom)
                .setMinScale(MIN_SCALE)
                .setMaxScale(MAX_SCALE)
                .setZoomFactor(ZOOM_FACTOR)
                .setXConstrained(true)
                .setYConstrained(true);

        panMediator = new MousePanMediator(eventFilterPan)
                .setXConstrained(true)
                .setYConstrained(true);

        zoomMediator.setEnabled(true);
        panMediator.setEnabled(true);

        viewport.getMediators().push(zoomMediator);
        viewport.getMediators().push(panMediator);

        return this;
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
    }

    public MouseWheelZoomMediator getZoomMediator() {
        return zoomMediator;
    }

    public MousePanMediator getPanMediator() {
        return panMediator;
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
