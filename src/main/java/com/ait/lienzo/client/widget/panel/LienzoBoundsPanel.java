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

package com.ait.lienzo.client.widget.panel;

import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.widget.panel.impl.BoundsProviderFactory;

public abstract class LienzoBoundsPanel<P extends LienzoBoundsPanel>
        extends LienzoPanel<P>
{
    private final LienzoPanel    lienzoPanel;

    private final BoundsProvider boundsProvider;

    private final Bounds         bounds;

    private       Bounds         defaultBounds;

    private       Layer          layer;

    public LienzoBoundsPanel(final LienzoPanel lienzoPanel,
                             final BoundsProvider boundsProvider)
    {
        this.lienzoPanel = lienzoPanel;
        this.boundsProvider = boundsProvider;
        this.bounds = Bounds.empty();
    }

    @Override
    public P add(final Layer layer)
    {
        if (null != this.layer)
        {
            throw new IllegalStateException("LienzoBoundsPanel type only allows a single layer.");
        }
        set(layer);
        return cast();
    }

    public P set(final Layer layer)
    {
        this.layer = layer;
        lienzoPanel.add(layer);
        return cast();
    }

    public final LienzoBoundsPanel refresh()
    {
        final Bounds bounds   = getLayerBounds();
        final Bounds boundses = BoundsProviderFactory.join(bounds, getDefaultBounds());
        setBounds(boundses.getX(),
                  boundses.getY(),
                  boundses.getWidth(),
                  boundses.getHeight());
        return onRefresh();
    }

    protected void setBounds(final double x,
                             final double y,
                             final double width,
                             final double height)
    {
        bounds.setX(x);
        bounds.setY(y);
        bounds.setWidth(width);
        bounds.setHeight(height);
    }

    public abstract LienzoBoundsPanel onRefresh();

    public Bounds getLayerBounds()
    {
        return boundsProvider.get(getLayer());
    }

    public Bounds getBounds()
    {
        return bounds;
    }

    @Override
    public P setBackgroundLayer(Layer layer)
    {
        lienzoPanel.setBackgroundLayer(layer);
        return cast();
    }

    @Override
    public void onResize()
    {
        refresh();
    }

    public void batch()
    {
        if (null != getLayer())
        {
            getLayer().batch();
        }
    }

    @Override
    public final void destroy()
    {
        doDestroy();
        getLienzoPanel().destroy();
        removeFromParent();
        layer = null;
        defaultBounds = null;
    }

    protected void doDestroy()
    {

    }

    public Bounds getDefaultBounds()
    {
        return defaultBounds;
    }

    public void setDefaultBounds(final Bounds defaultBounds)
    {
        this.defaultBounds = defaultBounds;
    }

    public Layer getLayer()
    {
        return layer;
    }

    public LienzoPanel getLienzoPanel()
    {
        return lienzoPanel;
    }

    @Override
    public int getHeight()
    {
        return lienzoPanel.getHeight();
    }

    @Override
    public int getWidth()
    {
        return lienzoPanel.getWidth();
    }

    public BoundsProvider getBoundsProvider()
    {
        return boundsProvider;
    }

    @SuppressWarnings("unchecked")
    private P cast()
    {
        return (P) this;
    }
}
