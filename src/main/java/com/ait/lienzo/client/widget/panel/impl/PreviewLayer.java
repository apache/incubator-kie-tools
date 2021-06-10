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

import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.client.widget.panel.Bounds;
import com.ait.lienzo.shared.core.types.ColorName;
import java.util.function.Supplier;

public class PreviewLayer extends Layer
{
    static final  double           ALPHA      = 0.5d;

    static final  String           FILL_COLOR = ColorName.LIGHTGREY.getColorString();

    private final Supplier<Bounds> backgroundBounds;

    private final Supplier<Bounds> visibleBounds;

    public PreviewLayer(final Supplier<Bounds> backgroundBounds,
                        final Supplier<Bounds> visibleBounds)
    {
        this.backgroundBounds = backgroundBounds;
        this.visibleBounds = visibleBounds;
        setTransformable(true);
        setListening(true);
    }


    @Override
    public void drawWithTransforms(final Context2D context,
                                   final double alpha,
                                   final BoundingBox bounds,
                                   final Supplier<Transform> transformSupplier)
    {
        drawBackground(context);
        super.drawWithTransforms(context, alpha, bounds, transformSupplier);
    }

    private void drawBackground(final Context2D context)
    {
        final Bounds clearBounds = visibleBounds.get();
        if (clearBounds.getWidth() > 0 || clearBounds.getHeight() > 0)
        {
            final Bounds bgBounds = backgroundBounds.get();
            context.save();
            context.setGlobalAlpha(ALPHA);
            context.setFillColor(FILL_COLOR);
            context.fillRect(bgBounds.getX(),
                             bgBounds.getY(),
                             bgBounds.getWidth(),
                             bgBounds.getHeight());
            context.clearRect(clearBounds.getX(),
                              clearBounds.getY(),
                              clearBounds.getWidth(),
                              clearBounds.getHeight());
            context.restore();
        }
    }
}
