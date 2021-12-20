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

import com.ait.lienzo.client.core.types.Transform;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;

/**
 * The context of a Grid's cell during the rendering phase.
 */
public class GridBodyCellRenderContext {

    private final double absoluteCellX;
    private final double absoluteCellY;
    private final double cellWidth;
    private final double cellHeight;
    private final double clipMinY;
    private final double clipMinX;
    private final int rowIndex;
    private final int columnIndex;
    private final boolean isFloating;
    private final Transform transform;
    private final GridRenderer renderer;

    public GridBodyCellRenderContext(final double absoluteCellX,
                                     final double absoluteCellY,
                                     final double cellWidth,
                                     final double cellHeight,
                                     final double clipMinY,
                                     final double clipMinX,
                                     final int rowIndex,
                                     final int columnIndex,
                                     final boolean isFloating,
                                     final Transform transform,
                                     final GridRenderer renderer) {
        this.absoluteCellX = absoluteCellX;
        this.absoluteCellY = absoluteCellY;
        this.cellWidth = cellWidth;
        this.cellHeight = cellHeight;
        this.clipMinY = clipMinY;
        this.clipMinX = clipMinX;
        this.rowIndex = rowIndex;
        this.columnIndex = columnIndex;
        this.isFloating = isFloating;
        this.transform = transform;
        this.renderer = renderer;
    }

    /**
     * Returns the cell's canvas x-coordinate; not transformed.
     * @return
     */
    public double getAbsoluteCellX() {
        return absoluteCellX;
    }

    /**
     * Returns the cell's canvas y-coordinate; not transformed.
     * @return
     */
    public double getAbsoluteCellY() {
        return absoluteCellY;
    }

    /**
     * Returns the width of the cell.
     * @return
     */
    public double getCellWidth() {
        return cellWidth;
    }

    /**
     * Returns the height of the cell.
     * @return
     */
    public double getCellHeight() {
        return cellHeight;
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
     * Returns the index of the row this cell represents.
     * @return
     */
    public int getRowIndex() {
        return rowIndex;
    }

    /**
     * Returns the index of the column this cell represents.
     * @return
     */
    public int getColumnIndex() {
        return columnIndex;
    }

    /**
     * Returns a flag indicating whether the Cell is contained within a floating column.
     * @return
     */
    public boolean isFloating() {
        return isFloating;
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
