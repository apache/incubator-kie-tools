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
package org.uberfire.ext.wires.core.grids.client.model;

import java.util.List;
import java.util.function.Consumer;

import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellEditContext;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.columns.GridColumnRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.selections.CellSelectionStrategy;
import org.uberfire.ext.wires.core.grids.client.widget.grid.selections.impl.HeaderSingleCellSelectionStrategy;

/**
 * Defines a Column within a grid.
 */
public interface GridColumn<T> {

    /**
     * Returns the MetaData for the Header. Each entry represents a row in the header.
     * Index 0 represents the first (top-down) row, Index 1 represents the second row etc.
     * @return
     */
    List<HeaderMetaData> getHeaderMetaData();

    /**
     * Returns the Render for the column
     * @return
     */
    GridColumnRenderer<T> getColumnRenderer();

    /**
     * Edit the cell (normally in response to a mouse double-click event)
     * @param cell
     * @param context
     * @param callback
     */
    default void edit(final GridCell<T> cell,
                      final GridBodyCellRenderContext context,
                      final Consumer<GridCellValue<T>> callback) {
    }

    /**
     * Edit the cell (normally in response to a mouse double-click event)
     * @param cell
     * @param context
     * @param callback
     */
    default void edit(final GridCell<T> cell,
                      final GridBodyCellEditContext context,
                      final Consumer<GridCellValue<T>> callback) {
        edit(cell,
             (GridBodyCellRenderContext) context,
             callback);
    }

    /**
     * Returns the column's width
     * @return
     */
    double getWidth();

    /**
     * Sets the columns width
     * @param width
     */
    void setWidth(final double width);

    /**
     * Returns a flag indicating this column is linked to another
     * @return
     */
    boolean isLinked();

    /**
     * Returns the column to which this column is linked
     * @return
     */
    GridColumn<?> getLink();

    /**
     * Sets the column to which this column is linked
     * @param link
     */
    void setLink(final GridColumn<?> link);

    /**
     * Returns the logical index to which this column relates. Columns may be re-ordered and therefore, to
     * avoid manipulating the underlying row data, the logical index of the column may be different to their
     * physical index (i.e. the order in which they were added to the grid).
     * @return
     */
    int getIndex();

    /**
     * Sets the logical index of the column, to support indirection of columns' access to row data.
     * @param index
     */
    void setIndex(final int index);

    /**
     * Returns a flag indicating whether a column can be re-sized.
     * @return true if the column can be re-sized.
     */
    boolean isResizable();

    /**
     * Sets whether the column can be re-sized.
     * @param isResizable true if the column can be re-sized.
     */
    void setResizable(final boolean isResizable);

    /**
     * Returns a flag indicating whether a column can be moved.
     * @return true if the column can be moved.
     */
    boolean isMovable();

    /**
     * Sets whether the column can be moved.
     * @param isMovable true if the column can be moved.
     */
    void setMovable(final boolean isMovable);

    /**
     * Returns a flag indicating whether a column is capable of floating on the left-hand side of the table when clipped horizontally.
     * @return true if the column is capable of floating.
     */
    boolean isFloatable();

    /**
     * Sets whether the column is capable of floating on the left-hand side of the table when clipped horizontally.
     * @param isFloatable true if the column can be floated.
     */
    void setFloatable(final boolean isFloatable);

    /**
     * Returns a flag indicating whether a column is visible.
     * @return true if the column is visible.
     */
    boolean isVisible();

    /**
     * Sets whether the column is visible.
     * @param isVisible true if the column is visible.
     */
    void setVisible(final boolean isVisible);

    /**
     * Returns the minimum width to which the column can be re-sized
     * @return null if no minimum
     */
    Double getMinimumWidth();

    /**
     * Sets the minimum width to which the column can be re-sized
     * @param minimumWidth Minimum width, or null if no minimum width
     */
    void setMinimumWidth(final Double minimumWidth);

    /**
     * Returns the maximum width to which the column can be re-sized
     * @return null if no maximum
     */
    Double getMaximumWidth();

    /**
     * Sets the maximum width to which the column can be re-sized
     * @param maximumWidth Maximum width, or null if no minimum width
     */
    void setMaximumWidth(final Double maximumWidth);

    /**
     * MetaData for the column's header
     */
    interface HeaderMetaData {

        /**
         * Returns an identifier for a group of Columns. Columns in one group cannot be moved to another group.
         * @return The group identifier. It should not be null.
         */
        String getColumnGroup();

        /**
         * Sets the identifier for a group of Columns. Columns in one group cannot be moved to another group.
         * @The group identifier. It should not be null.
         */
        void setColumnGroup(final String columnGroup);

        /**
         * Returns the column's title
         * @return
         */
        String getTitle();

        /**
         * Sets the column's title
         * @param title
         */
        void setTitle(final String title);

        /**
         * Returns the CellSelectionStrategy to handle selections of the header cell.
         * @return
         */
        default CellSelectionStrategy getSelectionStrategy() {
            return HeaderSingleCellSelectionStrategy.INSTANCE;
        }

        /**
         * Puts the {@link HeaderMetaData} into 'edit' mode.
         * @param context The context of a Grid's cell header during the rendering phase.
         */
        default void edit(final GridBodyCellEditContext context) {
          // do nothing by default
        }
    }

    /**
     * Get column width mode
     * @return
     */
    ColumnWidthMode getColumnWidthMode();

    /**
     * Set column width mode
     * @return
     */
    void setColumnWidthMode(ColumnWidthMode columnWidthMode);

    /**
     * Enum that identify the width mode of a column
     */
    enum ColumnWidthMode {
        // FIXED means that no automatic resize will be done, only the user can manually resize it
        FIXED,
        // AUTO means that its width will be calculate to fit all the available space
        AUTO;

        static public boolean isAuto(GridColumn<?> column) {
            return column != null && AUTO.equals(column.getColumnWidthMode());
        }

        static public boolean isFixed(GridColumn<?> column) {
            return column != null && FIXED.equals(column.getColumnWidthMode());
        }
    }
}
