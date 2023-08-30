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


package com.ait.lienzo.client.widget.panel;

import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.style.Style.Cursor;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.client.widget.panel.impl.BoundsProviderFactory;

public abstract class LienzoBoundsPanel<P extends LienzoBoundsPanel>
        extends LienzoPanel<P>
        implements IsResizable {

    private final LienzoPanel lienzoPanel;

    private final BoundsProvider boundsProvider;

    private final Bounds bounds;

    private Bounds defaultBounds;

    private Layer layer;

    public LienzoBoundsPanel(final LienzoPanel lienzoPanel,
                             final BoundsProvider boundsProvider) {
        this.lienzoPanel = lienzoPanel;
        this.boundsProvider = boundsProvider;
        this.bounds = Bounds.empty();
    }

    @Override
    public P add(final Layer layer) {
        if (null != this.layer) {
            throw new IllegalStateException("LienzoBoundsPanel type only allows a single layer.");
        }
        set(layer);
        return cast();
    }

    public P set(final Layer layer) {
        this.layer = layer;
        lienzoPanel.add(layer);
        return cast();
    }

    public final LienzoBoundsPanel refresh() {
        final Bounds bounds = getLayerBounds();
        final Bounds boundses = BoundsProviderFactory.join(bounds, getDefaultBounds());
        setBounds(boundses.getX(),
                  boundses.getY(),
                  boundses.getWidth(),
                  boundses.getHeight());
        if (null != getLayer()) {
            final int w = (int) Math.round(boundses.getWidth());
            final int h = (int) Math.round(boundses.getHeight());
            getLayer().getScratchPad().setPixelSize(w,
                                                    h);
        }
        return onRefresh();
    }

    protected void setBounds(final double x,
                             final double y,
                             final double width,
                             final double height) {
        bounds.setX(x);
        bounds.setY(y);
        bounds.setWidth(width);
        bounds.setHeight(height);
    }

    public abstract LienzoBoundsPanel onRefresh();

    public Bounds getLayerBounds() {
        return boundsProvider.get(getLayer());
    }

    public Bounds getBounds() {
        return bounds;
    }

    @Override
    public P setBackgroundLayer(Layer layer) {
        lienzoPanel.setBackgroundLayer(layer);
        return cast();
    }

    @Override
    public P setCursor(final Cursor cursor) {
        lienzoPanel.setCursor(cursor);
        return cast();
    }

    @Override
    public void onResize() {
    }

    public void batch() {
        if (null != getLayer()) {
            getLayer().batch();
        }
    }

    protected void doDestroy() {
    }

    @Override
    public final void destroy() {
        doDestroy();
        getLienzoPanel().destroy();
        layer = null;
        defaultBounds = null;
    }

    public Bounds getDefaultBounds() {
        return defaultBounds;
    }

    public void setDefaultBounds(final Bounds defaultBounds) {
        this.defaultBounds = defaultBounds;
    }

    public Layer getLayer() {
        return layer;
    }

    @Override
    public Viewport getViewport() {
        return null != layer ? layer.getViewport() : null;
    }

    protected Transform getTransform() {
        return null != getViewport() ? getViewport().getTransform() : null;
    }

    public LienzoPanel getLienzoPanel() {
        return lienzoPanel;
    }

    @Override
    public int getHighPx() {
        return lienzoPanel.getHighPx();
    }

    @Override
    public int getWidePx() {
        return lienzoPanel.getWidePx();
    }

    public BoundsProvider getBoundsProvider() {
        return boundsProvider;
    }

    @SuppressWarnings("unchecked")
    private P cast() {
        return (P) this;
    }
}
