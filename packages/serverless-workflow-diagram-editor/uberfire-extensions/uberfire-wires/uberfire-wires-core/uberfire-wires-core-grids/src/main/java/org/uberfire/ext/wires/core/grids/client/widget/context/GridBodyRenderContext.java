/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.uberfire.ext.wires.core.grids.client.widget.context;

import java.util.List;

import com.ait.lienzo.client.core.types.Transform;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.SelectionsTransformer;

/**
 * The context of a Grid's body during the rendering phase.
 */
public class GridBodyRenderContext {

    private final double absoluteGridX;
    private final double absoluteGridY;
    private final double clipMinY;
    private final double clipMinX;
    private final List<GridColumn<?>> blockColumns;
    private final double absoluteColumnOffsetX;
    private final int minVisibleRowIndex;
    private final int maxVisibleRowIndex;
    private final Transform transform;
    private final GridRenderer renderer;
    private final SelectionsTransformer transformer;

    public GridBodyRenderContext(final double absoluteGridX,
                                 final double absoluteGridY,
                                 final double absoluteColumnOffsetX,
                                 final double clipMinY,
                                 final double clipMinX,
                                 final int minVisibleRowIndex,
                                 final int maxVisibleRowIndex,
                                 final List<GridColumn<?>> blockColumns,
                                 final Transform transform,
                                 final GridRenderer renderer,
                                 final SelectionsTransformer transformer) {
        this.absoluteGridX = absoluteGridX;
        this.absoluteGridY = absoluteGridY;
        this.absoluteColumnOffsetX = absoluteColumnOffsetX;
        this.clipMinY = clipMinY;
        this.clipMinX = clipMinX;
        this.minVisibleRowIndex = minVisibleRowIndex;
        this.maxVisibleRowIndex = maxVisibleRowIndex;
        this.blockColumns = blockColumns;
        this.transform = transform;
        this.renderer = renderer;
        this.transformer = transformer;
    }

    /**
     * Returns the canvas x-coordinate of the Grid; not transformed.
     * @return
     */
    public double getAbsoluteGridX() {
        return absoluteGridX;
    }

    /**
     * Returns the canvas y-coordinate of the Grid; not transformed
     * @return
     */
    public double getAbsoluteGridY() {
        return absoluteGridY;
    }

    /**
     * Returns the absolute offset from absoluteGridX that the first column needs to be rendered.
     * Only visible columns are rendered; and so the first column to be rendered needs to
     * be offset from the Grid's absolute X co-ordinate.
     * @return
     */
    public double getAbsoluteColumnOffsetX() {
        return absoluteColumnOffsetX;
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
     * Returns the columns to render for the block.
     * @return
     */
    public List<GridColumn<?>> getBlockColumns() {
        return blockColumns;
    }

    /**
     * TReturns the transformation of the Grid Widget.
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

    /**
     * Returns the Column Index transformer supporting Floating Columns.
     * @return
     */
    public SelectionsTransformer getTransformer() {
        return transformer;
    }
}
