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
package org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import org.uberfire.ext.wires.core.grids.client.model.Bounds;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;

import static org.uberfire.ext.wires.core.grids.client.util.Logging.log;

/**
 * Helper for rendering a grid.
 */
public class BaseGridRendererHelper {

    private static final Logger LOGGER = Logger.getLogger(BaseGridRendererHelper.class.getName());

    private final GridWidget view;

    public BaseGridRendererHelper(final GridWidget view) {
        this.view = Objects.requireNonNull(view, "view");
    }

    /**
     * Get the x-offset for a given Column in the model relative to zero.
     * @param column The GridColumn.
     * @return
     */
    public double getColumnOffset(final GridColumn<?> column) {
        final GridData model = view.getModel();
        final int columnIndex = model.getColumns().indexOf(column);
        if (columnIndex == -1) {
            return 0;
        }
        return getColumnOffset(columnIndex);
    }

    /**
     * Get the x-offset for a given Column index in the model relative to zero.
     * @param columnIndex The index of the GridColumn.
     * @return
     */
    public double getColumnOffset(final int columnIndex) {
        double columnOffset = 0;
        final GridData model = view.getModel();
        final List<GridColumn<?>> columns = model.getColumns();
        for (int i = 0; i < columnIndex; i++) {
            final GridColumn column = columns.get(i);
            if (column.isVisible()) {
                columnOffset = columnOffset + column.getWidth();
            }
        }
        return columnOffset;
    }

    /**
     * Get the x-offset for a given Column index in a list of Columns relative to zero.
     * @param columns
     * @param columnIndex
     * @return
     */
    public double getColumnOffset(final List<GridColumn<?>> columns,
                                  final int columnIndex) {
        double columnOffset = 0;
        for (int idx = 0; idx < columnIndex; idx++) {
            final GridColumn<?> column = columns.get(idx);
            if (column.isVisible()) {
                columnOffset = columnOffset + column.getWidth();
            }
        }
        return columnOffset;
    }

    /**
     * Get the y-offset for a given Row.
     * @param row The GridRow.
     * @return
     */
    public double getRowOffset(final GridRow row) {
        final GridData model = view.getModel();
        final int rowIndex = model.getRows().indexOf(row);
        return getRowOffset(rowIndex);
    }

    /**
     * Get the y-offset for a given Row index.
     * @param rowIndex The index of the GridRow.
     * @return
     */
    public double getRowOffset(final int rowIndex) {
        double rowOffset = 0;
        final GridData model = view.getModel();
        for (int i = 0; i < rowIndex; i++) {
            final GridRow row = model.getRow(i);
            rowOffset = rowOffset + row.getHeight();
        }
        return rowOffset;
    }

    public double getRowOffset(final GridRow row,
                               final List<Double> rowHeights) {
        final GridData model = view.getModel();
        final int rowIndex = model.getRows().indexOf(row);
        return getRowOffset(rowIndex, rowHeights);
    }

    public double getRowOffset(final int rowIndex,
                               final List<Double> rowHeights) {
        double rowOffset = 0;
        for (int i = 0; i < rowIndex; i++) {
            rowOffset = rowOffset + rowHeights.get(i);
        }
        return rowOffset;
    }

    /**
     * Get the width of a set of columns, ignoring hidden columns.
     * @param columns The columns.
     * @return
     */
    public double getWidth(final List<GridColumn<?>> columns) {
        double width = 0;
        for (GridColumn<?> column : columns) {
            if (column.isVisible()) {
                width = width + column.getWidth();
            }
        }
        return width;
    }

    /**
     * Get rendering information about which columns are floating, which are visible. This method never returns null.
     * It returns a RenderingInformation object representing the columns that are visible and/or floating.
     * @return A RenderingInformation object or null if the GridWidget is not even partially visible.
     */
    public RenderingInformation getRenderingInformation() {
        final GridData model = view.getModel();
        final Bounds bounds = getVisibleBounds();
        final List<GridColumn<?>> allColumns = new ArrayList<>();
        final List<GridColumn<?>> bodyColumns = new ArrayList<>();
        final List<GridColumn<?>> floatingColumns = new ArrayList<>();

        double viewHeight = 0;
        final int rowCount = model.getRowCount();
        final List<Double> rowHeights = new ArrayList<>();
        for (GridRow row : model.getRows()) {
            final double rowHeight = row.getHeight();
            viewHeight = viewHeight + rowHeight;
            rowHeights.add(rowHeight);
        }

        final double vpX = bounds.getX();
        final double vpY = bounds.getY();
        final double vpWidth = bounds.getWidth();
        final double vpHeight = bounds.getHeight();
        final double headerOffsetY = getHeaderOffsetY();
        final GridRenderer renderer = view.getRenderer();

        //Simple bounds check
        if (view.getComputedLocation().getX() > vpX + vpWidth) {
            return null;
        } else if (view.getComputedLocation().getX() + view.getWidth() < vpX) {
            return null;
        } else if (view.getComputedLocation().getY() > vpY + vpHeight) {
            return null;
        } else if (view.getComputedLocation().getY() + viewHeight < vpY) {
            return null;
        }

        //Identify type of header
        boolean isFixedHeader = false;
        boolean isFloatingHeader = false;
        if (view.isSelected()) {
            if (view.getComputedLocation().getY() < vpY) {
                //GridWidget is selected and clipped at the top
                if (view.getComputedLocation().getY() + viewHeight > vpY + renderer.getHeaderHeight()) {
                    //GridWidget is taller than the Header; add floating header
                    isFloatingHeader = true;
                } else {
                    //GridWidget is shorter than the Header; add fixed header
                    isFixedHeader = true;
                }
            } else if (view.getComputedLocation().getY() <= vpY + vpHeight) {
                //GridWidget is selected and not clipped at the top; add fixed header
                isFixedHeader = true;
            }
        } else if (view.getComputedLocation().getY() + renderer.getHeaderHeight() > vpY && view.getComputedLocation().getY() < vpY + vpHeight) {
            //GridWidget is not selected; add fixed header
            isFixedHeader = true;
        }

        //Identify rows to render
        long currentTimeMillis = log(LOGGER, " - Pre- identify rows to render");

        int minVisibleRowIndex = 0;
        if (model.getRowCount() > 0) {
            double clipTop = vpY - view.getComputedLocation().getY() - (isFloatingHeader ? 0.0 : renderer.getHeaderHeight());
            while (rowHeights.get(minVisibleRowIndex) < clipTop && minVisibleRowIndex < rowCount - 1) {
                clipTop = clipTop - rowHeights.get(minVisibleRowIndex);
                minVisibleRowIndex++;
            }
        }

        int maxVisibleRowIndex = minVisibleRowIndex;
        if (model.getRowCount() > 0) {
            double clipBottom = vpY - view.getComputedLocation().getY() - renderer.getHeaderHeight() + vpHeight - getRowOffset(minVisibleRowIndex, rowHeights);
            while (rowHeights.get(maxVisibleRowIndex) < clipBottom && maxVisibleRowIndex < rowCount - 1) {
                clipBottom = clipBottom - rowHeights.get(maxVisibleRowIndex);
                maxVisibleRowIndex++;
            }
        }
        log(LOGGER, " - Post- identify rows to render", currentTimeMillis);

        //Identify columns to render
        currentTimeMillis = log(LOGGER, " - Pre- identify columns to render");

        double x = 0;
        for (GridColumn<?> column : model.getColumns()) {
            allColumns.add(column);
            final double floatingColumnsWidth = getWidth(floatingColumns);
            if (view.getComputedLocation().getX() + x + column.getWidth() >= vpX + floatingColumnsWidth) {
                if (view.getComputedLocation().getX() + x < vpX + vpWidth) {
                    bodyColumns.add(column);
                }
            }
            if (view.isSelected()) {
                if (column.isFloatable()) {
                    if (view.getComputedLocation().getX() + x < vpX + floatingColumnsWidth) {
                        allColumns.remove(column);
                        bodyColumns.remove(column);
                        floatingColumns.add(column);
                    }
                }
            }
            if (column.isVisible()) {
                x = x + column.getWidth();
            }
        }

        //If the floating columns obscure the body columns remove the float and just show the body columns
        if (view.getComputedLocation().getX() + x - vpX < getWidth(floatingColumns)) {
            allColumns.clear();
            bodyColumns.clear();
            floatingColumns.clear();
            allColumns.addAll(model.getColumns());

            x = 0;
            for (GridColumn<?> column : model.getColumns()) {
                if (view.getComputedLocation().getX() + x + column.getWidth() >= vpX) {
                    if (view.getComputedLocation().getX() + x < vpX + vpWidth) {
                        bodyColumns.add(column);
                    }
                }
                if (column.isVisible()) {
                    x = x + column.getWidth();
                }
            }
        }
        log(LOGGER, " - Post- identify columns to render", currentTimeMillis);

        //Construct details of Floating and Body blocks
        double visibleRowOffset = getRowOffset(minVisibleRowIndex, rowHeights);
        final double bodyOffsetY = visibleRowOffset + renderer.getHeaderHeight();
        final double offsetX = (bodyColumns.size() > 0 ? getColumnOffset(bodyColumns.get(0)) : 0);
        final double floatingOffsetX = getFloatingColumnOffset();

        final RenderingBlockInformation bodyBlockInformation = new RenderingBlockInformation(bodyColumns,
                                                                                             offsetX,
                                                                                             headerOffsetY,
                                                                                             bodyOffsetY,
                                                                                             getWidth(bodyColumns));
        final RenderingBlockInformation floatingBlockInformation = new RenderingBlockInformation(floatingColumns,
                                                                                                 floatingOffsetX,
                                                                                                 headerOffsetY,
                                                                                                 bodyOffsetY,
                                                                                                 getWidth(floatingColumns));

        // Construct "row offsets". The row offsets are based from zero; for each row to be rendered.
        // The minVisibleRowIndex corresponds to index zero and maxVisibleRowIndex corresponds to visibleRowOffsets.size() - 1.
        // This is useful to calculate the Y co-ordinate of each Row's top. It is calculated once and passed to
        // each column as an optimisation to prevent each column from recalculating the same values.
        currentTimeMillis = log(LOGGER, " - Pre- calculate row offsets");

        final List<Double> visibleRowOffsets = new ArrayList<>();
        if (model.getRowCount() > 0) {
            for (int rowIndex = minVisibleRowIndex; rowIndex <= maxVisibleRowIndex; rowIndex++) {
                visibleRowOffsets.add(visibleRowOffset);
                visibleRowOffset = visibleRowOffset + rowHeights.get(rowIndex);
            }
        }
        log(LOGGER, " - Post- calculate row offsets", currentTimeMillis);

        final int headerRowCount = model.getHeaderRowCount();
        final double headerHeight = renderer.getHeaderHeight();
        final double headerRowHeight = renderer.getHeaderRowHeight();
        final double headerRowsHeight = headerRowHeight * headerRowCount;
        final double headerRowsYOffset = headerHeight - headerRowsHeight;

        //Finally return all rendering information
        return new RenderingInformation(bounds,
                                        allColumns,
                                        bodyBlockInformation,
                                        floatingBlockInformation,
                                        minVisibleRowIndex,
                                        maxVisibleRowIndex,
                                        rowHeights,
                                        visibleRowOffsets,
                                        isFixedHeader,
                                        isFloatingHeader,
                                        headerRowCount,
                                        headerRowHeight,
                                        headerRowsHeight,
                                        headerRowsYOffset);
    }

    /**
     * Get information about a column corresponding to a grid-relative x-coordinate. This method never returns null.
     * It returns a ColumnInformation object representing the column corresponding to the grid-relative x-coordinate;
     * or an empty ColumnInformation object if no corresponding column was found.
     * @param cx An x-coordinate relative to the GridWidget.
     * @return A non-null ColumnInformation object.
     */
    public ColumnInformation getColumnInformation(final double cx) {
        //Gather information on columns
        final RenderingInformation renderingInformation = getRenderingInformation();
        if (renderingInformation == null) {
            return new ColumnInformation();
        }

        final GridData model = view.getModel();
        final List<GridColumn<?>> columns = model.getColumns();
        final RenderingBlockInformation bodyBlockInformation = renderingInformation.getBodyBlockInformation();
        final RenderingBlockInformation floatingBlockInformation = renderingInformation.getFloatingBlockInformation();
        final List<GridColumn<?>> bodyColumns = bodyBlockInformation.getColumns();
        final List<GridColumn<?>> floatingColumns = floatingBlockInformation.getColumns();
        final double floatingX = floatingBlockInformation.getX();
        final double floatingWidth = floatingBlockInformation.getWidth();

        //Check floating columns
        double offsetX = floatingX;
        GridColumn<?> column = null;
        for (GridColumn<?> gridColumn : floatingColumns) {
            if (gridColumn.isVisible()) {
                final double columnWidth = gridColumn.getWidth();
                if (cx > offsetX && cx < offsetX + columnWidth) {
                    column = gridColumn;
                    break;
                }
                offsetX = offsetX + columnWidth;
            }
        }
        if (column != null) {
            return new ColumnInformation(column,
                                         columns.indexOf(column),
                                         offsetX);
        }

        //Check all other columns
        offsetX = bodyBlockInformation.getX();
        for (GridColumn<?> gridColumn : bodyColumns) {
            if (gridColumn.isVisible()) {
                final double columnWidth = gridColumn.getWidth();
                if (offsetX + columnWidth > floatingX + floatingWidth) {
                    if (cx > offsetX && cx < offsetX + columnWidth) {
                        column = gridColumn;
                        break;
                    }
                }
                offsetX = offsetX + columnWidth;
            }
        }
        if (column == null) {
            return new ColumnInformation();
        }
        return new ColumnInformation(column,
                                     columns.indexOf(column),
                                     offsetX);
    }

    /**
     * Get the visible bounds (canvas coordinate system) of the given GridWidget.
     * @return
     */
    private Bounds getVisibleBounds() {
        final GridLayer gridLayer = ((GridLayer) view.getLayer());
        final Bounds bounds = gridLayer.getVisibleBounds();
        return bounds;
    }

    /**
     * Find the x-offset relative to the GridWidget origin where Floating columns are positioned.
     * @return
     */
    private double getFloatingColumnOffset() {
        final Bounds bounds = getVisibleBounds();
        return bounds.getX() - view.getComputedLocation().getX();
    }

    /**
     * Find the y-offset relative to the GridWidget origin where Floating Header is positioned.
     * @return
     */
    private double getHeaderOffsetY() {
        final double vpY = getVisibleBounds().getY();
        if (view.isSelected()) {
            if (view.getComputedLocation().getY() < vpY && view.getComputedLocation().getY() + view.getHeight() > vpY + view.getRenderer().getHeaderHeight()) {
                return vpY - view.getComputedLocation().getY();
            }
        }
        return 0.0;
    }

    /**
     * A container for Column Information.
     */
    public static class ColumnInformation {

        private GridColumn<?> column;
        private int uiColumnIndex = -1;
        private double offsetX = -1;

        ColumnInformation() {

        }

        public ColumnInformation(final GridColumn<?> column,
                                 final int uiColumnIndex,
                                 final double offsetX) {
            this.column = column;
            this.uiColumnIndex = uiColumnIndex;
            this.offsetX = offsetX;
        }

        /**
         * The GridWidget's column corresponding to the grid-relative x-coordinate, or null if none was found.
         * @return
         */
        public GridColumn<?> getColumn() {
            return column;
        }

        /**
         * The index of the GridWidget's column. This is equivalent to columns.indexOf(column).
         * @return
         */
        public int getUiColumnIndex() {
            return uiColumnIndex;
        }

        /**
         * The x-offset of the Column's left-hand edge relative to the GridWidget. i.e. column 0 has an x-offset of 0.
         * Floating columns canvas position is set dynamically depending on the GridWidget's position and the canvas's
         * Viewport. Therefore the x-offset of the first floating column is not zero but subject to the Viewport.
         * @return
         */
        public double getOffsetX() {
            return offsetX;
        }
    }

    /**
     * A container for Rendering Information.
     */
    public static class RenderingInformation {

        private final Bounds bounds;
        private final List<GridColumn<?>> allColumns;
        private final RenderingBlockInformation bodyBlockInformation;
        private final RenderingBlockInformation floatingBlockInformation;
        private final int minVisibleRowIndex;
        private final int maxVisibleRowIndex;
        private final List<Double> allRowHeights;
        private final List<Double> visibleRowOffsets;
        private final boolean isFixedHeader;
        private final boolean isFloatingHeader;
        private final int headerRowCount;
        private final double headerRowHeight;
        private final double headerRowsHeight;
        private final double headerRowsYOffset;

        public RenderingInformation(final Bounds bounds,
                                    final List<GridColumn<?>> allColumns,
                                    final RenderingBlockInformation bodyBlockInformation,
                                    final RenderingBlockInformation floatingBlockInformation,
                                    final int minVisibleRowIndex,
                                    final int maxVisibleRowIndex,
                                    final List<Double> allRowHeights,
                                    final List<Double> visibleRowOffsets,
                                    final boolean isFixedHeader,
                                    final boolean isFloatingHeader,
                                    final int headerRowCount,
                                    final double headerRowHeight,
                                    final double headerRowsHeight,
                                    final double headerRowsYOffset) {
            this.bounds = bounds;
            this.allColumns = allColumns;
            this.bodyBlockInformation = bodyBlockInformation;
            this.floatingBlockInformation = floatingBlockInformation;
            this.minVisibleRowIndex = minVisibleRowIndex;
            this.maxVisibleRowIndex = maxVisibleRowIndex;
            this.allRowHeights = allRowHeights;
            this.visibleRowOffsets = visibleRowOffsets;
            this.isFixedHeader = isFixedHeader;
            this.isFloatingHeader = isFloatingHeader;
            this.headerRowCount = headerRowCount;
            this.headerRowHeight = headerRowHeight;
            this.headerRowsHeight = headerRowsHeight;
            this.headerRowsYOffset = headerRowsYOffset;
        }

        public Bounds getBounds() {
            return bounds;
        }

        public List<GridColumn<?>> getAllColumns() {
            return allColumns;
        }

        public RenderingBlockInformation getBodyBlockInformation() {
            return bodyBlockInformation;
        }

        public RenderingBlockInformation getFloatingBlockInformation() {
            return floatingBlockInformation;
        }

        public int getMinVisibleRowIndex() {
            return minVisibleRowIndex;
        }

        public int getMaxVisibleRowIndex() {
            return maxVisibleRowIndex;
        }

        public List<Double> getAllRowHeights() {
            return Collections.unmodifiableList(allRowHeights);
        }

        public List<Double> getVisibleRowOffsets() {
            return Collections.unmodifiableList(visibleRowOffsets);
        }

        public boolean isFixedHeader() {
            return isFixedHeader;
        }

        public boolean isFloatingHeader() {
            return isFloatingHeader;
        }

        public int getHeaderRowCount() {
            return headerRowCount;
        }

        public double getHeaderRowHeight() {
            return headerRowHeight;
        }

        public double getHeaderRowsHeight() {
            return headerRowsHeight;
        }

        public double getHeaderRowsYOffset() {
            return headerRowsYOffset;
        }
    }

    /**
     * A container for Rendering Block Information.
     */
    public static class RenderingBlockInformation {

        private final List<GridColumn<?>> columns;
        private final double x;
        private final double headerY;
        private final double bodyY;
        private final double width;

        public RenderingBlockInformation(final List<GridColumn<?>> columns,
                                         final double x,
                                         final double headerY,
                                         final double bodyY,
                                         final double width) {
            this.columns = columns;
            this.x = x;
            this.headerY = headerY;
            this.bodyY = bodyY;
            this.width = width;
        }

        public List<GridColumn<?>> getColumns() {
            return Collections.unmodifiableList(columns);
        }

        public double getX() {
            return x;
        }

        public double getHeaderY() {
            return headerY;
        }

        public double getBodyY() {
            return bodyY;
        }

        public double getWidth() {
            return width;
        }
    }
}
