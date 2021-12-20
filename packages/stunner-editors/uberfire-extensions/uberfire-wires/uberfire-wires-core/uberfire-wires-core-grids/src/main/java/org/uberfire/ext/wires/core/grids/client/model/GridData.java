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
import java.util.function.Supplier;

/**
 * An interface defining a generic grid of data.
 */
public interface GridData {

    /**
     * Returns the columns associated with the grid.
     * @return
     */
    List<GridColumn<?>> getColumns();

    /**
     * Returns the total number of columns in the grid, including hidden columns.
     * @return
     */
    int getColumnCount();

    /**
     * Appends a column to the end of the grid. End being considered the far most right.
     * @param column
     */
    void appendColumn(final GridColumn<?> column);

    /**
     * Inserts a column to the grid at the specified index.
     * @param index
     * @param column
     */
    void insertColumn(final int index,
                      final GridColumn<?> column);

    /**
     * Updates a column in the grid at the specified index. All existing row data will be cleared.
     * @param index
     * @param column
     */
    void updateColumn(final int index,
                      final GridColumn<?> column);

    /**
     * Deletes a column from the grid.
     * @param column
     */
    void deleteColumn(final GridColumn<?> column);

    /**
     * Moves a column to a new index within the grid
     * @param index
     * @param column
     */
    void moveColumnTo(final int index,
                      final GridColumn<?> column);

    /**
     * Moves columns to a new index within the grid
     * @param index
     * @param columns
     */
    void moveColumnsTo(final int index,
                       final List<GridColumn<?>> columns);

    /**
     * Returns the rows associated with the grid.
     * @return
     */
    List<GridRow> getRows();

    /**
     * Returns the row at the specified index.
     * @param rowIndex
     * @return
     */
    GridRow getRow(final int rowIndex);

    /**
     * Appends a row to the end of the grid.
     * @param row
     */
    void appendRow(final GridRow row);

    /**
     * Inserts a row to the grid at the specified index.
     * @param rowIndex
     * @param row
     */
    void insertRow(final int rowIndex,
                   final GridRow row);

    /**
     * Deletes a row from the grid at the specified index.
     * @param rowIndex
     * @return The Range of rows affected by the operation.
     */
    Range deleteRow(final int rowIndex);

    /**
     * Moves a row to a new index within the grid
     * @param index
     * @param row
     */
    void moveRowTo(final int index,
                   final GridRow row);

    /**
     * Moves rowss to a new index within the grid
     * @param index
     * @param rows
     */
    void moveRowsTo(final int index,
                    final List<GridRow> rows);

    /**
     * Returns the total number of rows in the grid, including collapsed rows.
     * @return
     */
    int getRowCount();

    /**
     * Returns the number of rows in the grid's header.
     * @return The number of rows in the header; greater than zero.
     */
    int getHeaderRowCount();

    /**
     * Sets the number of rows in the grid's header.
     * @param headerRowCount Cannot be less than 1.
     */
    void setHeaderRowCount(final int headerRowCount);

    /**
     * Returns a cell at the specified physical coordinate.
     * @param rowIndex
     * @param columnIndex
     * @return
     */
    GridCell<?> getCell(final int rowIndex,
                        final int columnIndex);

    /**
     * Sets a cell at the specified physical coordinate.
     * @param rowIndex
     * @param columnIndex
     * @param cellSupplier A supplier of new cell instances
     * @return The Range of rows affected by the operation.
     */
    Range setCell(final int rowIndex,
                  final int columnIndex,
                  final Supplier<GridCell<?>> cellSupplier);

    /**
     * Sets a cell value at the specified physical coordinate.
     * @param rowIndex
     * @param columnIndex
     * @param value
     * @return The Range of rows affected by the operation.
     */
    Range setCellValue(final int rowIndex,
                       final int columnIndex,
                       final GridCellValue<?> value);

    /**
     * Deletes a cell at the specified physical coordinate.
     * @param rowIndex
     * @param columnIndex
     * @return The Range of rows affected by the operation.
     */
    Range deleteCell(final int rowIndex,
                     final int columnIndex);

    /**
     * Selects a cell at the specified physical coordinate.
     * @param rowIndex
     * @param columnIndex
     * @return The Range of rows affected by the operation.
     */
    Range selectCell(final int rowIndex,
                     final int columnIndex);

    /**
     * Selects a cell at the specified physical coordinate.
     * @param rowIndex
     * @param columnIndex
     * @param width
     * @param height
     * @return The Range of rows affected by the operation.
     */
    Range selectCells(final int rowIndex,
                      final int columnIndex,
                      final int width,
                      final int height);

    /**
     * Returns the origin of a selected range.
     * @return null if no origin has been defined.
     */
    SelectedCell getSelectedCellsOrigin();

    /**
     * Returns all selected cells.
     * @return
     */
    List<SelectedCell> getSelectedCells();

    /**
     * Selects a header cell at the specified physical coordinate.
     * @param headerRowIndex
     * @param headerColumnIndex
     * @return The Range of rows affected by the operation.
     */
    Range selectHeaderCell(final int headerRowIndex,
                           final int headerColumnIndex);

    /**
     * Returns all selected header cells.
     * @return
     */
    List<SelectedCell> getSelectedHeaderCells();

    /**
     * Clears all cell selections.
     */
    void clearSelections();

    /**
     * Returns whether the data in a merged state.
     * @return true if merged.
     */
    boolean isMerged();

    /**
     * Sets whether the data is in merged state.
     * @param isMerged
     */
    void setMerged(final boolean isMerged);

    /**
     * Returns whether rows can be repositioned by dragging and dropping.
     * @return true if drag and drop is enabled.
     */
    boolean isRowDraggingEnabled();

    /**
     * Set whether rows can be repositioned by dragging and dropping.
     * @param enabled true if drag and drop is enabled.
     */
    void setRowDraggingEnabled(final boolean enabled);

    /**
     * Returns whether columns can be repositioned by dragging and dropping.
     * @return true if drag and drop is enabled.
     */
    boolean isColumnDraggingEnabled();

    /**
     * Sets whether columns can be repositioned by dragging and dropping.
     * @param enabled true if drag and drop is enabled.
     */
    void setColumnDraggingEnabled(final boolean enabled);

    /**
     * Collapses a cell and corresponding rows. The cell being collapsed has all other merged
     * cells below it collapsed into the single cell. The cell itself remains not collapsed.
     * @param rowIndex
     * @param columnIndex
     */
    void collapseCell(final int rowIndex,
                      final int columnIndex);

    /**
     * Expands a cell and corresponding rows. The cell being collapsed has all other merged cells
     * below it expanded. Expanding collapsed cells should not expand nested collapsed cells.
     * @param rowIndex
     * @param columnIndex
     */
    void expandCell(final int rowIndex,
                    final int columnIndex);

    /**
     * Updates the width of columns with {@link GridColumn.ColumnWidthMode#AUTO}
     * @return a boolean that indicates if grid need to be redraw or not
     */
    boolean refreshWidth();

    /**
     * Updates the width of columns with {@link GridColumn.ColumnWidthMode#AUTO}
     * @param currentWidth is the grid width before this resize iteration
     * @return a boolean that indicates if grid need to be redraw or not
     */
    boolean refreshWidth(double currentWidth);

    /**
     * Update visible size information and refresh columns width. See {@link GridData#refreshWidth()}
     * @param width
     * @param height
     * @return a boolean that indicates if grid need to be redraw or not
     */
    boolean setVisibleSizeAndRefresh(int width, int height);

    /**
     * Get visible width
     * @return
     */
    int getVisibleWidth();

    /**
     * Get visible height
     * @return
     */
    int getVisibleHeight();

    /**
     * A range of rows.
     */
    class Range {

        private int minRowIndex;
        private int maxRowIndex;

        public Range(final int rowIndex) {
            this(rowIndex,
                 rowIndex);
        }

        public Range(final int minRowIndex,
                     final int maxRowIndex) {
            this.minRowIndex = minRowIndex;
            this.maxRowIndex = maxRowIndex;
        }

        public int getMinRowIndex() {
            return minRowIndex;
        }

        public int getMaxRowIndex() {
            return maxRowIndex;
        }
    }

    /**
     * A selected cell within the data. Selected state is not stored in the GridCell implementation
     * as we'd need to scan the whole grid to retrieve selected cells. The assumption is that the number
     * of selected cells is invariably far, far fewer than the total number of cells in the grid.
     */
    class SelectedCell {

        private final int rowIndex;
        private final int columnIndex;

        public SelectedCell(final int rowIndex,
                            final int columnIndex) {
            this.rowIndex = rowIndex;
            this.columnIndex = columnIndex;
        }

        public int getRowIndex() {
            return rowIndex;
        }

        public int getColumnIndex() {
            return columnIndex;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof SelectedCell)) {
                return false;
            }

            SelectedCell that = (SelectedCell) o;

            if (rowIndex != that.rowIndex) {
                return false;
            }
            if (columnIndex != that.columnIndex) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int result = rowIndex;
            result = ~~result;
            result = 31 * result + columnIndex;
            result = ~~result;
            return result;
        }
    }
}
