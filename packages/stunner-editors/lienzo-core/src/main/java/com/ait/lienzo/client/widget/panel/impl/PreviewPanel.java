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

package com.ait.lienzo.client.widget.panel.impl;

import java.util.function.Supplier;

import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.style.Style.Cursor;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.client.widget.panel.Bounds;
import com.ait.lienzo.client.widget.panel.BoundsProvider;
import com.ait.lienzo.client.widget.panel.LienzoBoundsPanel;
import com.ait.lienzo.client.widget.panel.LienzoPanel;
import com.ait.lienzo.tools.client.event.HandlerManager;
import elemental2.dom.EventListener;

public class PreviewPanel extends ScalablePanel {

    private final HandlerManager m_events;

    private final Bounds visibleBounds;

    private final Point2D visibleScaleFactor;

    private final PreviewLayer previewLayer;

    private final PreviewLayerDecorator decorator;

    public PreviewPanel(final int width,
                        final int height) {
        this(LienzoFixedPanel.newPanel(width, height));
    }

    PreviewPanel(final LienzoPanel panel) {
        super(panel, new PreviewBoundsProvider());
        m_events = new HandlerManager(this);
        visibleBounds = Bounds.empty();
        visibleScaleFactor = new Point2D(1, 1);
        previewLayer = new PreviewLayer(backgroundBoundsSupplier,
                                        visibleBoundsSupplier);
        decorator = new PreviewLayerDecorator(backgroundBoundsSupplier,
                                              visibleBoundsSupplier,
                                              new PreviewLayerDecorator.EventHandler() {
                                                  @Override
                                                  public void onMouseEnter() {
                                                      getLienzoPanel().getElement().style.cursor = Cursor.MOVE.getCssName();
                                                  }

                                                  @Override
                                                  public void onMouseExit() {
                                                      getLienzoPanel().getElement().style.cursor = Cursor.DEFAULT.getCssName();
                                                  }

                                                  @Override
                                                  public void onMove(final Point2D point) {
                                                      setVisibleBoundsAt(point.getX(),
                                                                         point.getY());
                                                      updatePanelScroll();
                                                      batch();
                                                  }
                                              });
    }

    PreviewPanel(final LienzoPanel panel,
                 final PreviewLayer previewLayer,
                 final PreviewLayerDecorator decorator,
                 final HandlerManager m_events) {
        super(panel, new PreviewBoundsProvider());
        this.m_events = m_events;
        this.visibleBounds = Bounds.empty();
        this.visibleScaleFactor = new Point2D(1, 1);
        this.previewLayer = previewLayer;
        this.decorator = decorator;
    }

    private final Supplier<Bounds> backgroundBoundsSupplier = this::getBackgroundBounds;

    private final Supplier<Bounds> visibleBoundsSupplier = new Supplier<Bounds>() {
        @Override
        public Bounds get() {
            if (!isDisplayVisibleArea()) {
                return Bounds.empty();
            }
            return Bounds.build(visibleBounds.getX(),
                                visibleBounds.getY(),
                                getVisibleWidth(),
                                getVisibleHeight());
        }
    };

    private void removeListeners() {
        if (null != observed) {
            observed.removeScrollEventListener(scrollEventListener);
            observed.removeScrollEventListener(resizeEventListener);
            observed.removeScrollEventListener(scaleEventListener);
            observed.removeScrollEventListener(boundsChangedEventListener);
            scrollEventListener = null;
            resizeEventListener = null;
            scaleEventListener = null;
            boundsChangedEventListener = null;
        }
    }

    private ScrollablePanel observed;
    private EventListener scrollEventListener;
    private EventListener resizeEventListener;
    private EventListener scaleEventListener;
    private EventListener boundsChangedEventListener;

    public PreviewPanel observe(final ScrollablePanel panel) {

        if (null != observed) {
            throw new IllegalStateException("Cannot observe twice");
        }

        getPreviewBoundsProvider().delegate(this, panel);

        observed = panel;

        scrollEventListener = panel.addScrollEventListener(event -> {
            if (!decorator.isDragging()) {
                scroll(panel.getHorizontalScrollRate(), panel.getVerticalScrollRate());
            }
        });

        resizeEventListener = panel.addResizeEventListener(event -> resize(observed.getWidePx(), observed.getHighPx()));

        scaleEventListener = panel.addScaleEventListener(event -> {

            if (!decorator.isDragging()) {
                visibleScaleFactor
                        .setX(1 / panel.getViewport().getTransform().getScaleX())
                        .setY(1 / panel.getViewport().getTransform().getScaleY());
            }
        });

        boundsChangedEventListener = panel.addBoundsChangedEventListener(event -> refresh());

        // Use actual panel's size.
        resize(panel.getWidePx(),
               panel.getHighPx());

        // Use actual panel's scroll position.
        scroll(panel.getHorizontalScrollRate(),
               panel.getVerticalScrollRate());

        return this;
    }

    @Override
    public LienzoBoundsPanel set(final Layer layer) {
        super.set(layer);
        getLienzoPanel().add(previewLayer);
        previewLayer.add(decorator.asPrimitive());
        return this;
    }

    void updatePanelScroll() {
        if (null != observed) {
            final Bounds backgroundBounds = getBackgroundBounds();
            final double bgWidth = backgroundBounds.getWidth();
            final double bgHeight = backgroundBounds.getHeight();
            final double x = visibleBounds.getX();
            final double y = visibleBounds.getY();
            final double width = bgWidth - getVisibleWidth();
            final double height = bgHeight - getVisibleHeight();
            final double pctX = width > 0 ? x / width * 100 : 0d;
            final double pctY = height > 0 ? y / height * 100 : 0d;

            observed.applyScrollRateToLayer(pctX, pctY);
            observed.onRefresh();
        }
    }

    public ScalablePanel adjustVisibleBounds(final double pctX,
                                             final double pctY) {
        final Bounds backgroundBounds = getBackgroundBounds();
        final double width = backgroundBounds.getWidth() - getVisibleWidth();
        final double height = backgroundBounds.getHeight() - getVisibleHeight();
        final double incX = width * pctX / 100;
        final double incY = height * pctY / 100;
        final double x = backgroundBounds.getX();
        final double y = backgroundBounds.getY();
        return setVisibleBoundsAt(x + incX,
                                  y + incY);
    }

    public ScalablePanel setVisibleBoundsAt(final double x,
                                            final double y) {
        visibleBounds
                .setX(x)
                .setY(y);
        return this;
    }

    public ScalablePanel setVisibleBoundsSize(final double width,
                                              final double height) {
        visibleBounds
                .setWidth(width)
                .setHeight(height);
        return this;
    }

    private void resize(final double width,
                        final double height) {
        setDefaultBounds(Bounds.relativeBox(width, height));
        setVisibleBoundsSize(width,
                             height);
        refresh();
    }

    private void scroll(final double pctX,
                        final double pctY) {
        adjustVisibleBounds(pctX,
                            pctY);
        batch();
    }

    @Override
    public void batch() {
        super.batch();
        if (null != getLayer()) {
            decorator.update();
            previewLayer.batch();
        }
    }

    @Override
    protected void doDestroy() {
        getPreviewBoundsProvider().destroy();
        decorator.destroy();
        previewLayer.clear();
        removeListeners();
        super.doDestroy();
    }

    PreviewBoundsProvider getPreviewBoundsProvider() {
        return (PreviewBoundsProvider) getBoundsProvider();
    }

    Bounds getVisibleBounds() {
        return visibleBounds;
    }

    public static class PreviewBoundsProvider implements BoundsProvider {

        LienzoBoundsPanel delegate;

        public PreviewBoundsProvider delegate(final PreviewPanel panel,
                                              final LienzoBoundsPanel delegate) {
            this.delegate = delegate;
            return this;
        }

        @Override
        public Bounds get(Layer layer) {
            if (null != delegate) {
                return delegate.getLayerBounds();
            }
            return Bounds.empty();
        }

        public void destroy() {
            this.delegate = null;
        }
    }

    private double getVisibleWidth() {
        return visibleBounds.getWidth() * visibleScaleFactor.getX();
    }

    private double getVisibleHeight() {
        return visibleBounds.getHeight() * visibleScaleFactor.getY();
    }

    private boolean isDisplayVisibleArea() {
        final double visibleWidth = getVisibleWidth();
        final double visibleHeight = getVisibleHeight();
        final double boundsWidth = getBounds().getWidth();
        final double boundseHeight = getBounds().getHeight();
        return !(boundsWidth <= visibleWidth && boundseHeight <= visibleHeight);
    }

    private Bounds getBackgroundBounds() {
        return obtainViewportBounds(getLayer());
    }

    static double[] getSafeScaleValues(final Layer layer) {
        final Viewport vp = layer.getViewport();
        final Transform transform = vp.getTransform();
        final double scaleX = null != transform ? transform.getScaleX() : 1d;
        final double scaleY = null != transform ? transform.getScaleY() : 1d;
        return new double[]{scaleX, scaleY};
    }

    static Bounds obtainViewportBounds(final Layer layer) {
        final double[] scale = getSafeScaleValues(layer);
        final Viewport vp = layer.getViewport();
        final double vw = vp.getWidth() * (1 / scale[0]);
        final double vh = vp.getHeight() * (1 / scale[1]);
        return Bounds.relativeBox(vw, vh);
    }
}
