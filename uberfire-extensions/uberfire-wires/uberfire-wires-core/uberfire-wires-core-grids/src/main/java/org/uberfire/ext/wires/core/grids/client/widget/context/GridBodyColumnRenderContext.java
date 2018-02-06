/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.uberfire.ext.wires.core.grids.client.widget.context;

import com.ait.lienzo.client.core.types.Transform;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;

/**
 * The context of a Grid's column during the rendering phase.
 */
public class GridBodyColumnRenderContext {

    private final double x;
    private final double absoluteGridX;
    private final double absoluteGridY;
    private final double absoluteColumnX;
    private final double clipMinY;
    private final double clipMinX;
    private final int minVisibleRowIndex;
    private final int maxVisibleRowIndex;
    private final boolean isFloating;
    private final GridData model;
    private final Transform transform;
    private final GridRenderer renderer;

    public GridBodyColumnRenderContext(final double x,
                                       final double absoluteGridX,
                                       final double absoluteGridY,
                                       final double absoluteColumnX,
                                       final double clipMinY,
                                       final double clipMinX,
                                       final int minVisibleRowIndex,
                                       final int maxVisibleRowIndex,
                                       final boolean isFloating,
                                       final GridData model,
                                       final Transform transform,
                                       final GridRenderer renderer) {
        this.x = x;
        this.absoluteGridX = absoluteGridX;
        this.absoluteGridY = absoluteGridY;
        this.absoluteColumnX = absoluteColumnX;
        this.clipMinY = clipMinY;
        this.clipMinX = clipMinX;
        this.minVisibleRowIndex = minVisibleRowIndex;
        this.maxVisibleRowIndex = maxVisibleRowIndex;
        this.isFloating = isFloating;
        this.model = model;
        this.transform = transform;
        this.renderer = renderer;
    }

    /**
     * Returns the columns x-coordinate relative to the grids origin.
     * @return
     */
    public double getX() {
        return this.x;
    }

    /**
     * Returns the canvas x-coordinate of the Grid; not transformed.
     * @return
     */
    public double getAbsoluteGridX() {
        return absoluteGridX;
    }

    /**
     * Returns the canvas y-coordinate of the Grid; not transformed.
     * @return
     */
    public double getAbsoluteGridY() {
        return absoluteGridY;
    }

    /**
     * Returns the canvas x-coordinate of the Column; not transformed.
     * @return
     */
    public double getAbsoluteColumnX() {
        return absoluteColumnX;
    }

    /**
     * Returns the minimum Y coordinate for visible content. Content outside the region should be clipped.
     * @return
     */
    public double getClipMinY() {
        return clipMinY;
    }

    /**
     * Returns the minimum X coordinate for visible content. Content outside the region should be clipped.
     * @return
     */
    public double getClipMinX() {
        return clipMinX;
    }

    /**
     * Returns the index of the first row being rendered.
     * @return
     */
    public int getMinVisibleRowIndex() {
        return minVisibleRowIndex;
    }

    /**
     * Returns the index of the last row being rendered.
     * @return
     */
    public int getMaxVisibleRowIndex() {
        return maxVisibleRowIndex;
    }

    /**
     * Returns whether the column is floating.
     * @return true if the column is floating.
     */
    public boolean isFloating() {
        return isFloating;
    }

    /**
     * Returns the data model for the Grid Widget to which the Column relates.
     * @return
     */
    public GridData getModel() {
        return model;
    }

    /**
     * Returns the transformation of the Grid Widget.
     * @return
     */
    public Transform getTransform() {
        return transform;
    }

    /**
     * Returns the Renderer for the Grid Widget.
     * @return
     */
    public GridRenderer getRenderer() {
        return renderer;
    }
}
