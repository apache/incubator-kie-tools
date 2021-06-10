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

package com.ait.lienzo.client.widget.panel.util;

import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.client.widget.panel.Bounds;
import com.ait.lienzo.client.widget.panel.LienzoBoundsPanel;

public class PanelTransformUtils {

    private PanelTransformUtils()
    {

    }

    public static void setScaleLevel(final Viewport viewport,
                                     final double level) {
        final Transform viewportTransform = viewport.getTransform();
        final Transform transform = new Transform();
        transform.translate(viewportTransform.getTranslateX(),
                            viewportTransform.getTranslateY());
        transform.scale(level);
        viewport.setTransform(transform);
    }

    public static void reset(final Viewport viewport) {
        viewport.setTransform(new Transform());
    }

    public static double computeZoomLevelFitToWidth(final LienzoBoundsPanel panel) {
        final double panelWidth = panel.getWidePx();
        final double panelHeight = panel.getHighPx();
        return computeZoomLevelFitToWidth(panelWidth, panelHeight, panel);
    }

    public static double computeZoomLevelFitToWidth(final double width,
                                                    final double height,
                                                    final LienzoBoundsPanel panel) {
        final Bounds layerBounds = panel.getLayerBounds();
        final double layerWidth = layerBounds.getWidth();
        final double layerHeight = layerBounds.getHeight();
        double widthRatio = width / layerWidth;
        double heightRatio = height / layerHeight;
        final double level = widthRatio < heightRatio ? widthRatio : heightRatio;
        return level < 1 ? level : 1;
    }

    public static double computeLevel(final Viewport viewport) {
        final Transform transform = viewport.getTransform();
        final double scaleX = transform.getScaleX();
        final double scaleY = transform.getScaleY();
        return scaleX < scaleY ? scaleX : scaleY;
    }

}
