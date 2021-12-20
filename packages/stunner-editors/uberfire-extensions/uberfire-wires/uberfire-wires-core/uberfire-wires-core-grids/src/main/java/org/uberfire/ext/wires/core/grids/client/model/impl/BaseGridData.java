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
package org.uberfire.ext.wires.core.grids.client.model.impl;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;
import org.uberfire.ext.wires.core.grids.client.widget.dom.HasDOMElementResources;

/**
 * Base implementation of a grid to avoid boiler-plate for more specific implementations.
 */
public class BaseGridData implements GridData {

    protected boolean isMerged = true;
    protected boolean isRowDraggingEnabled = true;
    protected boolean isColumnDraggingEnabled = true;
    protected List<GridRow> rows = new ArrayList<>();
    protected List<GridColumn<?>> columns = new ArrayList<>();
    protected List<SelectedCell> selectedCells = new ArrayList<>();
    protected List<SelectedCell> selectedHeaderCells = new ArrayList<>();
    protected int headerRowCount = 0;

    protected BaseGridDataIndexManager indexManager = new BaseGridDataIndexManager(this);
    protected BaseGridDataSelectionsManager selectionsManager = new BaseGridDataSelectionsManager(this);
    private int visibleWidth;
    private int visibleHeight;
    private int previousVisibleWidth;

    public BaseGridData() {
        this(true);
    }

    public BaseGridData(final boolean isMerged) {
        this.isMerged = isMerged;
    }

    @Override
    public List<GridColumn<?>> getColumns() {
        return Collections.unmodifiableList(columns);
    }

    @Override
    public int getColumnCount() {
        return columns.size();
    }

    @Override
    public void appendColumn(final GridColumn<?> column) {
        double originalWidth = getWidth();
        column.setIndex(columns.size());
        columns.add(column);

        OptionalDouble optionalOriginalWidth = OptionalDouble.of(originalWidth);

        if (GridColumn.ColumnWidthMode.isAuto(column)) {
            column.setWidth(calculateInitWidth(column, optionalOriginalWidth));
            internalRefreshWidth(true, optionalOriginalWidth);
        }
        selectionsManager.onInsertColumn(columns.size() - 1);
    }

    @Override
    public void insertColumn(final int index,
                             final GridColumn<?> column) {
        double originalWidth = getWidth();
        column.setIndex(columns.size());
        columns.add(index,
                    column);

        OptionalDouble optionalOriginalWidth = OptionalDouble.of(originalWidth);

        if (GridColumn.ColumnWidthMode.isAuto(column)) {
            column.setWidth(calculateInitWidth(column, optionalOriginalWidth));
            internalRefreshWidth(true, optionalOriginalWidth);
        }
        selectionsManager.onInsertColumn(index);
    }

    @Override
    public void deleteColumn(final GridColumn<?> column) {
        final int index = column.getIndex();
        for (GridColumn<?> c : columns) {
            if (c.getIndex() > index) {
                c.setIndex(c.getIndex() - 1);
            }
        }

        //Destroy column
        if (column.getColumnRenderer() instanceof HasDOMElementResources) {
            ((HasDOMElementResources) column.getColumnRenderer()).destroyResources();
        }

        removeColumn(column);

        //Destroy column related cell
        for (GridRow row : rows) {
            ((BaseGridRow) row).deleteCell(index);
            //Shift all cells according to the removed one
            final Map<Integer, GridCell<?>> clone = new TreeMap<>(row.getCells());
            for (Map.Entry<Integer, GridCell<?>> e : clone.entrySet()) {
                if (e.getKey() > index) {
                    ((BaseGridRow) row).deleteCell(e.getKey());
                    ((BaseGridRow) row).setCell(e.getKey() - 1,
                                                e.getValue());
                }
            }
        }

        internalRefreshWidth(true, OptionalDouble.empty());

        selectionsManager.onDeleteColumn(index);
    }

    void removeColumn(final GridColumn<?> column) {
        final IntStream indexes = IntStream.range(0, columns.size());
        final OptionalInt columnIndex = indexes.filter(i -> column == columns.get(i)).findFirst();

        if (columnIndex.isPresent()) {
            columns.remove(columnIndex.getAsInt());
        } else {
            columns.remove(column);
        }

        internalRefreshWidth(true, OptionalDouble.empty());
    }

    @Override
    public void moveColumnTo(final int index,
                             final GridColumn<?> column) {
        moveColumnsTo(index,
                      new ArrayList<GridColumn<?>>() {{
                          add(column);
                      }});
    }

    @Override
    public void moveColumnsTo(final int index,
                              final List<GridColumn<?>> columns) {
        if (columns == null || columns.isEmpty()) {
            return;
        }
        final int currentIndex = this.columns.indexOf(columns.get(0));

        //Moving left
        if (index < currentIndex) {
            this.columns.removeAll(columns);
            this.columns.addAll(index,
                                columns);
        }

        //Moving right
        if (index > currentIndex) {
            this.columns.removeAll(columns);
            this.columns.addAll(index - columns.size() + 1,
                                columns);
        }
    }

    @Override
    public List<GridRow> getRows() {
        return Collections.unmodifiableList(rows);
    }

    @Override
    public GridRow getRow(final int rowIndex) {
        return rows.get(rowIndex);
    }

    @Override
    public void appendRow(final GridRow row) {
        this.rows.add(row);
    }

    @Override
    public void insertRow(final int rowIndex,
                          final GridRow row) {
        this.rows.add(rowIndex,
                      row);

        indexManager.onInsertRow(rowIndex);
        selectionsManager.onInsertRow(rowIndex);
    }

    @Override
    public Range deleteRow(final int rowIndex) {
        //Find row that is the "lead" in a merged collapsed block
        GridRow row;
        int minRowIndex = rowIndex;
        int maxRowIndex = rowIndex;
        while ((row = rows.get(minRowIndex)).isMerged() && row.isCollapsed() && minRowIndex > 0) {
            minRowIndex--;
        }

        //Find last row in a merged collapsed block
        do {
            maxRowIndex++;
        }
        while (maxRowIndex < rows.size() && rows.get(maxRowIndex).isCollapsed());
        maxRowIndex--;

        final Range range = new Range(minRowIndex,
                                      maxRowIndex);

        for (int _rowIndex = minRowIndex; _rowIndex <= maxRowIndex; _rowIndex++) {
            rows.remove(minRowIndex);
        }

        indexManager.onDeleteRow(range);
        selectionsManager.onDeleteRow(range);

        return range;
    }

    @Override
    public void moveRowTo(final int index,
                          final GridRow row) {
        moveRowsTo(index,
                   new ArrayList<GridRow>() {{
                       add(row);
                   }});
    }

    @Override
    public void moveRowsTo(final int index,
                           final List<GridRow> rows) {
        if (rows == null || rows.isEmpty()) {
            return;
        }

        //Get extent of block being moved
        final int oldBlockStart = this.rows.indexOf(rows.get(0));
        final int oldBlockEnd = this.rows.indexOf(rows.get(rows.size() - 1));

        //If we're attempting to move it to its current index just exit
        if (index == oldBlockStart) {
            return;
        }

        this.rows.removeAll(rows);

        if (index < oldBlockStart) {
            this.rows.addAll(index,
                             rows);
        } else if (index > oldBlockStart) {
            this.rows.addAll(index - rows.size() + 1,
                             rows);
        }

        final Range oldBlockExtent = new Range(oldBlockStart,
                                               oldBlockEnd);
        indexManager.onMoveRows(rows,
                                oldBlockExtent);
        selectionsManager.onMoveRows(rows,
                                     oldBlockExtent);
    }

    @Override
    public int getRowCount() {
        return rows.size();
    }

    @Override
    public int getHeaderRowCount() {
        int headerRowCount = this.headerRowCount;
        for (GridColumn<?> column : columns) {
            headerRowCount = Math.max(headerRowCount, column.getHeaderMetaData().size());
        }
        return headerRowCount;
    }

    @Override
    public void setHeaderRowCount(final int headerRowCount) {
        if (!(headerRowCount >= 0)) {
            throw new IllegalStateException("headerRowCount");
        }
        this.headerRowCount = headerRowCount;
    }

    @Override
    public GridCell<?> getCell(final int rowIndex,
                               final int columnIndex) {
        if (rowIndex < 0 || rowIndex > rows.size() - 1) {
            return null;
        }
        final int _columnIndex = columns.get(columnIndex).getIndex();
        return rows.get(rowIndex).getCells().get(_columnIndex);
    }

    @Override
    public SelectedCell getSelectedCellsOrigin() {
        return selectedCells.isEmpty() ? null : selectedCells.get(0);
    }

    @Override
    public List<SelectedCell> getSelectedCells() {
        return selectedCells;
    }

    @Override
    public List<SelectedCell> getSelectedHeaderCells() {
        return selectedHeaderCells;
    }

    @Override
    public void clearSelections() {
        selectedCells.clear();
        selectedHeaderCells.clear();
    }

    @Override
    public void updateColumn(final int index,
                             final GridColumn<?> column) {
        //Destroy existing column
        final GridColumn<?> existing = columns.get(index);
        if (existing.getColumnRenderer() instanceof HasDOMElementResources) {
            ((HasDOMElementResources) existing.getColumnRenderer()).destroyResources();
        }

        //Replace existing with new column
        column.setIndex(columns.get(index).getIndex());
        columns.set(index,
                    column);

        //Clear column data
        for (GridRow row : rows) {
            ((BaseGridRow) row).deleteCell(column.getIndex());
        }
    }

    @Override
    public boolean isMerged() {
        return this.isMerged;
    }

    @Override
    public void setMerged(final boolean isMerged) {
        if (this.isMerged == isMerged) {
            return;
        }
        this.isMerged = isMerged;
        indexManager.onMerge(isMerged);
        selectionsManager.onMerge(isMerged);
    }

    @Override
    public boolean isRowDraggingEnabled() {
        return this.isRowDraggingEnabled;
    }

    @Override
    public void setRowDraggingEnabled(final boolean enabled) {
        this.isRowDraggingEnabled = enabled;
    }

    @Override
    public boolean isColumnDraggingEnabled() {
        return this.isColumnDraggingEnabled;
    }

    @Override
    public void setColumnDraggingEnabled(final boolean enabled) {
        this.isColumnDraggingEnabled = enabled;
    }

    @Override
    public Range setCell(final int rowIndex,
                         final int columnIndex,
                         final Supplier<GridCell<?>> cellSupplier) {
        return doSetCell(rowIndex,
                         columnIndex,
                         (pair) -> cellSupplier.get());
    }

    @Override
    @SuppressWarnings("unchecked")
    public Range setCellValue(final int rowIndex,
                              final int columnIndex,
                              final GridCellValue<?> value) {
        return doSetCell(rowIndex,
                         columnIndex,
                         (pair) -> {
                             final Optional<BaseGridCell> cell = Optional.ofNullable((BaseGridCell) getCell(pair.getKey(), pair.getValue()));
                             final BaseGridCell c = cell.orElse(new BaseGridCell<>(value));
                             c.setValue(value);
                             return c;
                         });
    }

    protected Range doSetCell(final int rowIndex,
                              final int columnIndex,
                              final Function<Map.Entry<Integer, Integer>, GridCell<?>> cellSupplier) {
        if (rowIndex < 0 || rowIndex > rows.size() - 1) {
            return new Range(rowIndex);
        }
        if (columnIndex < 0 || columnIndex > columns.size() - 1) {
            return new Range(rowIndex);
        }

        final int _columnIndex = columns.get(columnIndex).getIndex();

        //If we're not merged just set the value of a single cell
        if (!isMerged) {
            ((BaseGridRow) rows.get(rowIndex)).setCell(_columnIndex,
                                                       cellSupplier.apply(new AbstractMap.SimpleEntry<>(rowIndex, columnIndex)));
            return new Range(rowIndex);
        }

        //Find affected rows for merged data
        final int minRowIndex = findMinRowIndexForCellUpdate(rowIndex,
                                                             _columnIndex);
        final int maxRowIndex = findMaxRowIndexForCellUpdate(rowIndex,
                                                             _columnIndex);

        //Update all rows' value
        final Range range = new Range(minRowIndex,
                                      maxRowIndex);
        for (int i = minRowIndex; i <= maxRowIndex; i++) {
            final GridRow row = rows.get(i);
            ((BaseGridRow) row).setCell(_columnIndex,
                                        cellSupplier.apply(new AbstractMap.SimpleEntry<>(i, columnIndex)));
        }

        indexManager.onSetCell(range,
                               _columnIndex);

        return range;
    }

    @Override
    public Range deleteCell(final int rowIndex,
                            final int columnIndex) {
        if (rowIndex < 0 || rowIndex > rows.size() - 1) {
            return new Range(rowIndex);
        }
        if (columnIndex < 0 || columnIndex > columns.size() - 1) {
            return new Range(rowIndex);
        }

        final int _columnIndex = columns.get(columnIndex).getIndex();

        //If we're not merged just set the value of a single cell
        if (!isMerged) {
            ((BaseGridRow) rows.get(rowIndex)).deleteCell(_columnIndex);
            return new Range(rowIndex);
        }

        //Find affected rows for merged data
        final int minRowIndex = findMinRowIndexForCellUpdate(rowIndex,
                                                             _columnIndex);
        final int maxRowIndex = findMaxRowIndexForCellUpdate(rowIndex,
                                                             _columnIndex);

        //Update all rows' value
        final Range range = new Range(minRowIndex,
                                      maxRowIndex);
        for (int i = minRowIndex; i <= maxRowIndex; i++) {
            final GridRow row = rows.get(i);
            ((BaseGridRow) row).deleteCell(_columnIndex);
            row.expand();
        }

        indexManager.onDeleteCell(range,
                                  _columnIndex);

        return range;
    }

    @Override
    public Range selectCell(final int rowIndex,
                            final int columnIndex) {
        if (rowIndex < 0 || rowIndex > rows.size() - 1) {
            return new Range(rowIndex);
        }
        if (columnIndex < 0 || columnIndex > columns.size() - 1) {
            return new Range(rowIndex);
        }

        return selectionsManager.onSelectCell(rowIndex,
                                              columnIndex);
    }

    @Override
    public Range selectCells(final int rowIndex,
                             final int columnIndex,
                             final int width,
                             final int height) {
        if (rowIndex < 0 || rowIndex > rows.size() - 1) {
            return new Range(rowIndex);
        }
        if (columnIndex < 0 || columnIndex > columns.size() - 1) {
            return new Range(rowIndex);
        }

        return selectionsManager.onSelectCells(rowIndex,
                                               columnIndex,
                                               width,
                                               height);
    }

    @Override
    public Range selectHeaderCell(final int headerRowIndex,
                                  final int headerColumnIndex) {
        if (headerColumnIndex < 0 || headerColumnIndex > columns.size() - 1) {
            return new Range(headerRowIndex);
        }
        final GridColumn<?> gridColumn = getColumns().get(headerColumnIndex);
        final List<GridColumn.HeaderMetaData> gridColumnHeaderMetaData = gridColumn.getHeaderMetaData();
        if (headerRowIndex < 0 || headerRowIndex > gridColumnHeaderMetaData.size() - 1) {
            return new Range(headerRowIndex);
        }

        return selectionsManager.onSelectHeaderCell(headerRowIndex,
                                                    headerColumnIndex);
    }

    @Override
    public void collapseCell(final int rowIndex,
                             final int columnIndex) {
        //Data needs to be merged to collapse cells
        if (!isMerged) {
            return;
        }

        final int _columnIndex = columns.get(columnIndex).getIndex();
        final GridRow row = rows.get(rowIndex);
        final GridCell<?> cell = row.getCells().get(_columnIndex);
        if (cell == null) {
            return;
        }
        if (!cell.isMerged()) {
            return;
        }
        indexManager.onCollapseCell(rowIndex,
                                    _columnIndex);
    }

    @Override
    public void expandCell(final int rowIndex,
                           final int columnIndex) {
        //Data needs to be merged to expand cells
        if (!isMerged) {
            return;
        }

        final int _columnIndex = columns.get(columnIndex).getIndex();
        final GridRow row = rows.get(rowIndex);
        final GridCell<?> cell = row.getCells().get(_columnIndex);
        if (cell == null) {
            return;
        }
        indexManager.onExpandCell(rowIndex,
                                  _columnIndex);
    }

    private int findMinRowIndexForCellUpdate(final int rowIndex,
                                             final int columnIndex) {
        int minRowIndex = rowIndex;
        final GridRow currentRow = getRow(rowIndex);
        final GridCell<?> currentRowCell = currentRow.getCells().get(columnIndex);

        //Find minimum row with a cell containing the same value as that being updated
        boolean foundTopSplitMarker = currentRowCell != null && currentRowCell.getMergedCellCount() > 0;
        while (minRowIndex > 0) {
            final GridRow previousRow = rows.get(minRowIndex - 1);
            final GridCell<?> previousRowCell = previousRow.getCells().get(columnIndex);
            if (!(previousRow.isCollapsed() && currentRow.isCollapsed())) {
                if (previousRowCell == null) {
                    break;
                }
                if (previousRowCell.isCollapsed() && foundTopSplitMarker) {
                    break;
                }
                if (!previousRowCell.equals(currentRowCell)) {
                    break;
                }
                if (previousRowCell.getMergedCellCount() > 0) {
                    foundTopSplitMarker = true;
                }
            }
            minRowIndex--;
        }
        return minRowIndex;
    }

    private int findMaxRowIndexForCellUpdate(final int rowIndex,
                                             final int columnIndex) {
        int maxRowIndex = rowIndex + 1;
        final GridRow currentRow = getRow(rowIndex);
        final GridCell<?> currentRowCell = currentRow.getCells().get(columnIndex);

        //Find maximum row with a cell containing the same value as that being updated
        boolean foundBottomSplitMarker = false;
        while (maxRowIndex < rows.size()) {
            final GridRow nextRow = rows.get(maxRowIndex);
            final GridCell<?> nextRowCell = nextRow.getCells().get(columnIndex);
            if (!nextRow.isCollapsed()) {
                if (nextRowCell == null) {
                    break;
                }
                if (nextRowCell.isCollapsed() && foundBottomSplitMarker) {
                    maxRowIndex--;
                    break;
                }
                if (!nextRowCell.equals(currentRowCell)) {
                    break;
                }
                if (nextRowCell.getMergedCellCount() > 0) {
                    foundBottomSplitMarker = true;
                }
            }
            maxRowIndex++;
        }
        return maxRowIndex - 1;
    }

    @Override
    public boolean refreshWidth() {
        return internalRefreshWidth(false, OptionalDouble.empty());
    }

    @Override
    public boolean refreshWidth(double currentWidth) {
        return internalRefreshWidth(false, OptionalDouble.of(currentWidth));
    }

    protected boolean internalRefreshWidth(boolean changedNumberOfColumn, OptionalDouble optionalCurrentWidth) {

        double visibleWidth = getVisibleWidth();
        // this happens during initialization
        if (visibleWidth == 0) {
            return false;
        }
        // refresh is not needed if it has not been added a column and visibleWidth doesn't change (except if
        // previousVisibleWidth is 0 so it is the first refresh)
        if (!changedNumberOfColumn && previousVisibleWidth != 0 && visibleWidth == previousVisibleWidth) {
            return false;
        }

        GridWidthMetadata gridWidthMetadata = new GridWidthMetadata(optionalCurrentWidth);

        // if there are no columns with AUTO width no need to continue
        if (gridWidthMetadata.numberOfAutoColumn == 0) {
            return false;
        }

        // verify if grid was 100% width with a delta
        boolean wasFullWidth = Math.abs(gridWidthMetadata.currentGrossWidth - previousVisibleWidth) < 0.1;

        // keep 100% width or max between visible area and grid width
        double targetGrossWidth = wasFullWidth ? visibleWidth : Math.max(visibleWidth, gridWidthMetadata.currentGrossWidth);

        double currentWidth = getWidth() - gridWidthMetadata.fixedWidth;
        double targetWidth = targetGrossWidth - gridWidthMetadata.fixedWidth;

        // if grid is greater than visible panel and it wasn't at full width no refresh is needed
        if (visibleWidth < gridWidthMetadata.currentGrossWidth && !wasFullWidth) {
            return false;
        }

        boolean toRedraw = false;
        for (GridColumn<?> column : getColumns()) {
            if (!column.isVisible() || !GridColumn.ColumnWidthMode.isAuto(column)) {
                continue;
            }
            double oldWidth = column.getWidth();
            double ratio = oldWidth / currentWidth;
            double newWidth = ratio * targetWidth;
            // this could happen during initialization when columns can be added before the first call to setVisibleSizeAndRefresh
            if (oldWidth == 0) {
                newWidth = calculateInitWidth(column, OptionalDouble.empty());
            }
            if (newWidth < column.getMinimumWidth()) {
                newWidth = column.getMinimumWidth();
            }
            // if nothing changed no need to update nor refresh
            if (newWidth != oldWidth) {
                column.setWidth(newWidth);
                toRedraw = true;
            }
        }
        return toRedraw;
    }

    protected double getWidth() {
        return getColumns().stream().filter(GridColumn::isVisible).mapToDouble(GridColumn::getWidth).sum();
    }

    @Override
    public boolean setVisibleSizeAndRefresh(int width, int height) {
        this.previousVisibleWidth = this.visibleWidth;
        this.visibleWidth = width;
        this.visibleHeight = height;
        return refreshWidth();
    }

    @Override
    public int getVisibleWidth() {
        return this.visibleWidth;
    }

    @Override
    public int getVisibleHeight() {
        return this.visibleHeight;
    }

    double calculateInitWidth(GridColumn<?> column, OptionalDouble optionalCurrentWidth) {
        if (!GridColumn.ColumnWidthMode.isAuto(column)) {
            return column.getWidth();
        }
        GridWidthMetadata gridWidthMetadata = new GridWidthMetadata(optionalCurrentWidth);
        int visibleWidth = getVisibleWidth();

        double calculatedWidth;
        if (gridWidthMetadata.numberOfAutoColumn < 2) {
            calculatedWidth = visibleWidth - gridWidthMetadata.fixedWidth;
        } else {
            calculatedWidth = (visibleWidth - gridWidthMetadata.fixedWidth) / (gridWidthMetadata.numberOfAutoColumn - 1);
        }
        return Math.max(calculatedWidth, column.getMinimumWidth());
    }

    private class GridWidthMetadata {

        // total size of the grid
        private double currentGrossWidth = 0;
        private double previousWidth = 0;
        private long numberOfAutoColumn = 0;
        // total size of FIXED column
        private double fixedWidth = 0;

        private GridWidthMetadata(OptionalDouble optionalCurrentWidth) {
            for (GridColumn<?> column : getColumns()) {
                if (!column.isVisible()) {
                    continue;
                }
                double columnWidth = column.getWidth();

                currentGrossWidth += columnWidth;
                numberOfAutoColumn = GridColumn.ColumnWidthMode.isAuto(column) ? numberOfAutoColumn + 1 : numberOfAutoColumn;
                fixedWidth = GridColumn.ColumnWidthMode.isFixed(column) ? fixedWidth + columnWidth : fixedWidth;
            }
            previousWidth = optionalCurrentWidth.orElse(currentGrossWidth);
            if (optionalCurrentWidth.isPresent()) {
                currentGrossWidth = optionalCurrentWidth.getAsDouble();
            }
        }
    }
}
