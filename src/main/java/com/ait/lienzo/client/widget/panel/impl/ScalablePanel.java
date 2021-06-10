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

package com.ait.lienzo.client.widget.panel.impl;

import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.client.widget.panel.Bounds;
import com.ait.lienzo.client.widget.panel.BoundsProvider;
import com.ait.lienzo.client.widget.panel.LienzoBoundsPanel;
import com.ait.lienzo.client.widget.panel.LienzoPanel;
import elemental2.dom.HTMLDivElement;

public class ScalablePanel extends LienzoBoundsPanel
{
    private static final boolean KEEP_ASPECT_RATIO = true;

    public ScalablePanel(final BoundsProvider layerBoundsProvider)
    {
        this(LienzoFixedPanel.newPanel(),
             layerBoundsProvider);
    }

    public ScalablePanel(final BoundsProvider layerBoundsProvider,
                         final int width,
                         final int height)
    {
        this(LienzoFixedPanel.newPanel(width, height),
             layerBoundsProvider);
        setDefaultBounds(Bounds.relativeBox(width, height));
    }

    ScalablePanel(final LienzoPanel panel,
                  final BoundsProvider layerBoundsProvider)
    {
        super(panel,
              layerBoundsProvider);
    }

    @Override
    public ScalablePanel onRefresh()
    {
        scale();
        batch();
        return this;
    }

    private void scale()
    {
        final Layer layer = getLayer();
        if (null != layer)
        {
            final double  width    = getLienzoPanel().getWidePx();
            final double  height   = getLienzoPanel().getHighPx();
            final Bounds  current  = getBounds();
            final double  toWidth  = current.getX() + current.getWidth();
            final double  toHeight = current.getY() + current.getHeight();
            final boolean doScale  = toWidth > width || toHeight > height;
            if (doScale)
            {
                final double[] scaleFactor = getScaleFactor(width, height, toWidth, toHeight);
                scale(scaleFactor[0], scaleFactor[1], KEEP_ASPECT_RATIO);
            }
            else
            {
                scale(1, 1, false);
            }
        }
    }

    protected void scale(final double scaleX,
                         final double scaleY,
                         final boolean keepAspectRatio)
    {
        if (null != getLayer())
        {
            double factorX = scaleX;
            double factorY = scaleY;
            if (keepAspectRatio)
            {
                double factor = scaleX <= scaleY ? scaleY : scaleX;
                factorX = factor;
                factorY = factor;
            }
            final Transform transform = new Transform();
            transform.scaleWithXY(1 / factorX, 1 / factorY);
            getLayer().getViewport().setTransform(transform);
        }
    }

    private static double[] getScaleFactor(final double width,
                                           final double height,
                                           final double targetWidth,
                                           final double targetHeight)
    {
        return new double[]{
                width > 0 ? targetWidth / width : 1,
                height > 0 ? targetHeight / height : 1};
    }

    @Override
    public HTMLDivElement getElement() {
        return getLienzoPanel().getElement();
    }
}
