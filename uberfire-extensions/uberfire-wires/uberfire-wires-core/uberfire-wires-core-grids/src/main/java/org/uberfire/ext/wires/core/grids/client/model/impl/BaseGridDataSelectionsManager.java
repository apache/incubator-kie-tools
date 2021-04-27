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

package org.uberfire.ext.wires.core.grids.client.model.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;
import org.uberfire.ext.wires.core.grids.client.util.ColumnIndexUtilities;
import org.uberfire.ext.wires.core.grids.client.widget.grid.selections.impl.RowSelectionStrategy;

/**
 * Helper class that manages "selected cell" meta-data following different mutations to {@link GridData}
 */
public class BaseGridDataSelectionsManager {

    private final GridData gridData;

    public BaseGridDataSelectionsManager(final GridData gridData) {
        this.gridData = gridData;
    }

    public void onMerge(final boolean isMerged) {
        if (isMerged) {
            final List<GridData.SelectedCell> selectedCells = gridData.getSelectedCells();
            final List<GridData.SelectedCell> cloneSelectedCells = new ArrayList<GridData.SelectedCell>(selectedCells);
            gridData.clearSelections();
            for (GridData.SelectedCell cell : cloneSelectedCells) {
                gridData.selectCells(cell.getRowIndex(),
                                     ColumnIndexUtilities.findUiColumnIndex(gridData.getColumns(),
                                                                            cell.getColumnIndex()),
                                     1,
                                     1);
            }
        }
    }

    public void onInsertColumn(final int index) {
        final List<GridData.SelectedCell> selectedCells = gridData.getSelectedCells();
        final List<Integer> rowsWithASelection = selectedCells.stream()
                .filter(sc -> {
                    final int ri = sc.getRowIndex();
                    final int ci = sc.getColumnIndex();
                    final int _ci = ColumnIndexUtilities.findUiColumnIndex(gridData.getColumns(), ci);
                    final GridCell<?> cell = gridData.getCell(ri, _ci);
                    return cell != null && cell.getSelectionStrategy() instanceof RowSelectionStrategy;
                })
                .map(GridData.SelectedCell::getRowIndex)
                .collect(Collectors.toList());

        rowsWithASelection.forEach(rowIndex -> onSelectCell(rowIndex, index));
    }

    public void onDeleteColumn(final int index) {
        onDeleteColumn(index, gridData.getSelectedCells());
        onDeleteColumn(index, gridData.getSelectedHeaderCells());
    }

    private void onDeleteColumn(final int index,
                                final List<GridData.SelectedCell> selectedCells) {
        final List<GridData.SelectedCell> selectedCellsToRemove = new ArrayList<GridData.SelectedCell>();
        final List<GridData.SelectedCell> selectedCellsToUpdate = new ArrayList<GridData.SelectedCell>();
        for (GridData.SelectedCell sc : selectedCells) {
            if (sc.getColumnIndex() == index) {
                selectedCellsToRemove.add(sc);
            } else if (sc.getColumnIndex() > index) {
                selectedCellsToUpdate.add(sc);
            }
        }
        selectedCells.removeAll(selectedCellsToRemove);
        selectedCells.removeAll(selectedCellsToUpdate);
        for (GridData.SelectedCell sc : selectedCellsToUpdate) {
            selectedCells.add(new GridData.SelectedCell(sc.getRowIndex(),
                                                        sc.getColumnIndex() - 1));
        }
    }

    public void onInsertRow(final int rowIndex) {
        final List<GridData.SelectedCell> selectedCells = gridData.getSelectedCells();
        final List<GridData.SelectedCell> selectedCellsToUpdate = new ArrayList<GridData.SelectedCell>();
        for (GridData.SelectedCell sc : selectedCells) {
            if (sc.getRowIndex() >= rowIndex) {
                selectedCellsToUpdate.add(sc);
            }
        }
        selectedCells.removeAll(selectedCellsToUpdate);
        for (GridData.SelectedCell sc : selectedCellsToUpdate) {
            selectedCells.add(new GridData.SelectedCell(sc.getRowIndex() + 1,
                                                        sc.getColumnIndex()));
        }
    }

    public void onDeleteRow(final GridData.Range range) {
        final int minRowIndex = range.getMinRowIndex();
        final int maxRowIndex = range.getMaxRowIndex();
        final List<GridData.SelectedCell> selectedCells = gridData.getSelectedCells();
        final List<GridData.SelectedCell> selectedCellsToRemove = new ArrayList<GridData.SelectedCell>();
        final List<GridData.SelectedCell> selectedCellsToUpdate = new ArrayList<GridData.SelectedCell>();
        for (GridData.SelectedCell sc : selectedCells) {
            if (sc.getRowIndex() >= minRowIndex && sc.getRowIndex() <= maxRowIndex) {
                selectedCellsToRemove.add(sc);
            } else if (sc.getRowIndex() > maxRowIndex) {
                selectedCellsToUpdate.add(sc);
            }
        }
        selectedCells.removeAll(selectedCellsToRemove);
        selectedCells.removeAll(selectedCellsToUpdate);
        for (GridData.SelectedCell sc : selectedCellsToUpdate) {
            selectedCells.add(new GridData.SelectedCell(sc.getRowIndex() - 1,
                                                        sc.getColumnIndex()));
        }
    }

    public GridData.Range onSelectCell(final int rowIndex,
                                       final int columnIndex) {
        if (gridData.isMerged()) {
            return selectCellMerged(rowIndex,
                                    columnIndex);
        } else {
            selectCellNotMerged(rowIndex,
                                columnIndex);
            return new GridData.Range(rowIndex);
        }
    }

    public GridData.Range onSelectCells(final int rowIndex,
                                        final int columnIndex,
                                        final int width,
                                        final int height) {
        //If we're not merged just set the value of a single cell
        if (!gridData.isMerged()) {
            selectCellsNotMerged(rowIndex,
                                 columnIndex,
                                 width,
                                 height);
            return new GridData.Range(rowIndex);
        }

        //Find affected rows for merged data
        int _columnIndex;
        int minRowIndex = rowIndex;
        int maxRowIndex = rowIndex + height - 1;
        final List<GridColumn<?>> columns = gridData.getColumns();
        for (int ci = columnIndex; ci < columnIndex + width; ci++) {
            _columnIndex = columns.get(ci).getIndex();
            minRowIndex = Math.min(minRowIndex,
                                   findMinRowIndex(minRowIndex,
                                                   _columnIndex));
            maxRowIndex = Math.max(maxRowIndex,
                                   findMaxRowIndex(maxRowIndex,
                                                   _columnIndex));
        }

        //Select all applicable rows' cells
        selectCellsNotMerged(minRowIndex,
                             columnIndex,
                             width,
                             maxRowIndex - minRowIndex + 1);

        return new GridData.Range(minRowIndex,
                                  maxRowIndex);
    }

    public GridData.Range onSelectHeaderCell(final int headerRowIndex,
                                             final int headerColumnIndex) {
        return selectHeaderCell(headerRowIndex,
                                headerColumnIndex);
    }

    private GridData.Range selectCellMerged(final int rowIndex,
                                            final int columnIndex) {
        //Find affected rows for merged data
        final List<GridColumn<?>> columns = gridData.getColumns();
        final int _columnIndex = columns.get(columnIndex).getIndex();
        final int minRowIndex = findMinRowIndex(rowIndex,
                                                _columnIndex);
        final int maxRowIndex = findMaxRowIndex(rowIndex,
                                                _columnIndex);

        //Select all applicable rows' cells
        selectCellsNotMerged(minRowIndex,
                             columnIndex,
                             1,
                             maxRowIndex - minRowIndex + 1);

        return new GridData.Range(minRowIndex,
                                  maxRowIndex);
    }

    private GridData.Range selectCellNotMerged(final int rowIndex,
                                               final int columnIndex) {
        final List<GridRow> rows = gridData.getRows();
        final List<GridColumn<?>> columns = gridData.getColumns();
        final List<GridData.SelectedCell> selectedCells = gridData.getSelectedCells();
        final GridData.Range range = new GridData.Range(rowIndex);
        if (rowIndex < 0 || rowIndex > rows.size() - 1) {
            return range;
        }
        if (columnIndex < 0 || columnIndex > columns.size() - 1) {
            return range;
        }
        final int _columnIndex = columns.get(columnIndex).getIndex();
        final GridData.SelectedCell selectedCell = new GridData.SelectedCell(rowIndex,
                                                                             _columnIndex);

        if (!selectedCells.contains(selectedCell)) {
            selectedCells.add(selectedCell);
        }

        return range;
    }

    private GridData.Range selectCellsNotMerged(final int rowIndex,
                                                final int columnIndex,
                                                final int width,
                                                final int height) {
        final List<GridRow> rows = gridData.getRows();
        final List<GridColumn<?>> columns = gridData.getColumns();
        final List<GridData.SelectedCell> selectedCells = gridData.getSelectedCells();
        final GridData.Range range = new GridData.Range(rowIndex);
        if (rowIndex < 0 || rowIndex > rows.size() - 1) {
            return range;
        }
        if (columnIndex < 0 || columnIndex > columns.size() - 1) {
            return range;
        }
        if (width < 1) {
            return range;
        }
        if (height < 1) {
            return range;
        }
        for (int ri = rowIndex; ri < rowIndex + height; ri++) {
            for (int ci = columnIndex; ci < columnIndex + width; ci++) {
                final int _columnIndex = columns.get(ci).getIndex();
                final GridData.SelectedCell selectedCell = new GridData.SelectedCell(ri,
                                                                                     _columnIndex);
                if (!selectedCells.contains(selectedCell)) {
                    selectedCells.add(selectedCell);
                }
            }
        }

        return new GridData.Range(rowIndex,
                                  rowIndex + height - 1);
    }

    private GridData.Range selectHeaderCell(final int headerRowIndex,
                                            final int headerColumnIndex) {
        final List<GridColumn<?>> columns = gridData.getColumns();
        final List<GridData.SelectedCell> selectedHeaderCells = gridData.getSelectedHeaderCells();
        final GridData.Range range = new GridData.Range(headerRowIndex);
        if (headerColumnIndex < 0 || headerColumnIndex > columns.size() - 1) {
            return range;
        }
        final GridColumn<?> gridColumn = columns.get(headerColumnIndex);
        final List<GridColumn.HeaderMetaData> gridColumnHeaderMetaData = gridColumn.getHeaderMetaData();
        if (headerRowIndex < 0 || headerRowIndex > gridColumnHeaderMetaData.size() - 1) {
            return range;
        }

        final int _headerColumnIndex = columns.get(headerColumnIndex).getIndex();
        final GridData.SelectedCell selectedCell = new GridData.SelectedCell(headerRowIndex,
                                                                             _headerColumnIndex);

        if (!selectedHeaderCells.contains(selectedCell)) {
            selectedHeaderCells.add(selectedCell);
        }

        return range;
    }

    private int findMinRowIndex(final int rowIndex,
                                final int columnIndex) {
        int minRowIndex = rowIndex;
        final GridRow currentRow = gridData.getRow(rowIndex);
        final GridCell<?> currentRowCell = currentRow.getCells().get(columnIndex);

        //Find minimum row with a cell containing the same value as that being updated
        boolean foundTopSplitMarker = currentRowCell != null && currentRowCell.getMergedCellCount() > 0;
        while (minRowIndex > 0) {
            final GridRow previousRow = gridData.getRow(minRowIndex - 1);
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

    private int findMaxRowIndex(final int rowIndex,
                                final int columnIndex) {
        int maxRowIndex = rowIndex + 1;
        final GridRow currentRow = gridData.getRow(rowIndex);
        final GridCell<?> currentRowCell = currentRow.getCells().get(columnIndex);

        //Find maximum row with a cell containing the same value as that being updated
        boolean foundBottomSplitMarker = false;
        while (maxRowIndex < gridData.getRowCount()) {
            final GridRow nextRow = gridData.getRow(maxRowIndex);
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

    public void onMoveRows(final List<GridRow> rowsMoved,
                           final GridData.Range oldBlockExtent) {
        final List<GridRow> rows = gridData.getRows();
        final int oldBlockStart = oldBlockExtent.getMinRowIndex();
        final int oldBlockEnd = oldBlockExtent.getMaxRowIndex();
        final int newBlockStart = rows.indexOf(rowsMoved.get(0));
        final int newBlockEnd = rows.indexOf(rowsMoved.get(rowsMoved.size() - 1));
        final List<GridData.SelectedCell> selectedCells = gridData.getSelectedCells();
        final List<GridData.SelectedCell> selectedCellsToMoveUp = new ArrayList<GridData.SelectedCell>();
        final List<GridData.SelectedCell> selectedCellsToMoveDown = new ArrayList<GridData.SelectedCell>();
        final List<GridData.SelectedCell> selectedCellsToUpdate = new ArrayList<GridData.SelectedCell>();

        if (newBlockStart < oldBlockStart) {
            //Moving row(s) up
            for (GridData.SelectedCell sc : selectedCells) {
                if (sc.getRowIndex() >= oldBlockStart && sc.getRowIndex() <= oldBlockEnd) {
                    selectedCellsToMoveUp.add(sc);
                } else if (sc.getRowIndex() >= newBlockStart && sc.getRowIndex() <= newBlockEnd) {
                    selectedCellsToMoveDown.add(sc);
                } else if (sc.getRowIndex() > newBlockEnd && sc.getRowIndex() < oldBlockStart) {
                    selectedCellsToUpdate.add(sc);
                }
            }
            selectedCells.removeAll(selectedCellsToMoveUp);
            selectedCells.removeAll(selectedCellsToMoveDown);
            selectedCells.removeAll(selectedCellsToUpdate);
            for (GridData.SelectedCell sc : selectedCellsToMoveUp) {
                selectedCells.add(new GridData.SelectedCell(sc.getRowIndex() - (oldBlockStart - newBlockStart),
                                                            sc.getColumnIndex()));
            }
            for (GridData.SelectedCell sc : selectedCellsToMoveDown) {
                selectedCells.add(new GridData.SelectedCell(sc.getRowIndex() + (oldBlockEnd - oldBlockStart) + 1,
                                                            sc.getColumnIndex()));
            }
            for (GridData.SelectedCell sc : selectedCellsToUpdate) {
                selectedCells.add(new GridData.SelectedCell(sc.getRowIndex() + (oldBlockEnd - oldBlockStart) + 1,
                                                            sc.getColumnIndex()));
            }
        } else if (newBlockStart > oldBlockStart) {
            //Moving row(s) down
            for (GridData.SelectedCell sc : selectedCells) {
                if (sc.getRowIndex() >= oldBlockStart && sc.getRowIndex() <= oldBlockEnd) {
                    selectedCellsToMoveDown.add(sc);
                } else if (sc.getRowIndex() >= newBlockStart && sc.getRowIndex() <= newBlockEnd) {
                    selectedCellsToMoveUp.add(sc);
                } else if (sc.getRowIndex() > oldBlockEnd && sc.getRowIndex() < newBlockStart) {
                    selectedCellsToUpdate.add(sc);
                }
            }
            selectedCells.removeAll(selectedCellsToMoveUp);
            selectedCells.removeAll(selectedCellsToMoveDown);
            selectedCells.removeAll(selectedCellsToUpdate);
            for (GridData.SelectedCell sc : selectedCellsToMoveUp) {
                selectedCells.add(new GridData.SelectedCell(sc.getRowIndex() - (oldBlockEnd - oldBlockStart) - 1,
                                                            sc.getColumnIndex()));
            }
            for (GridData.SelectedCell sc : selectedCellsToMoveDown) {
                selectedCells.add(new GridData.SelectedCell(sc.getRowIndex() + (newBlockStart - oldBlockStart),
                                                            sc.getColumnIndex()));
            }
            for (GridData.SelectedCell sc : selectedCellsToUpdate) {
                selectedCells.add(new GridData.SelectedCell(sc.getRowIndex() - (newBlockEnd - newBlockStart) - 1,
                                                            sc.getColumnIndex()));
            }
        }
    }
}
