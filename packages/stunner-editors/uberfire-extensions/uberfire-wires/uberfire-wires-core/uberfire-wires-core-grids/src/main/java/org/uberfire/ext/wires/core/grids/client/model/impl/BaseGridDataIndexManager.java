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

import java.util.List;

import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;

/**
 * Helper class that manages "merge" and "group" meta-data following different mutations to {@link GridData}
 */
public class BaseGridDataIndexManager {

    private final GridData gridData;

    public BaseGridDataIndexManager(final GridData gridData) {
        this.gridData = gridData;
    }

    public void onMerge(final boolean isMerged) {
        if (isMerged) {
            fullIndex();
        } else {
            reset();
        }
    }

    //Update all merge meta-data
    private void fullIndex() {
        final List<GridColumn<?>> columns = gridData.getColumns();
        for (int columnIndex = 0; columnIndex < columns.size(); columnIndex++) {
            indexColumn(columnIndex);
        }
    }

    //Update merge meta-data for a single column
    public void indexColumn(final int columnIndex) {
        final List<GridRow> rows = gridData.getRows();
        for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
            final GridCell<?> currentRowCell = gridData.getCell(rowIndex,
                                                                columnIndex);
            if (currentRowCell == null) {
                continue;
            }

            currentRowCell.reset();

            int maxRowIndex = rowIndex + 1;
            while (maxRowIndex < rows.size()) {
                final GridCell<?> nextRowCell = gridData.getCell(maxRowIndex,
                                                                 columnIndex);
                if (nextRowCell == null) {
                    break;
                }
                if (!nextRowCell.equals(currentRowCell)) {
                    break;
                }
                maxRowIndex++;
            }

            //Update merge meta-data
            if (maxRowIndex - rowIndex > 1) {
                for (int i = rowIndex; i < maxRowIndex; i++) {
                    final GridRow row = rows.get(i);
                    final GridCell<?> cell = gridData.getCell(i,
                                                              columnIndex);
                    ((BaseGridCell) cell).setMergedCellCount(0);
                    updateRowMergedCells(row);
                }

                final GridRow row = rows.get(rowIndex);
                final GridCell<?> cell = gridData.getCell(rowIndex,
                                                          columnIndex);
                ((BaseGridCell) cell).setMergedCellCount(maxRowIndex - rowIndex);
                updateRowMergedCells(row);

                rowIndex = maxRowIndex - 1;
            }
        }
    }

    //Clear all merge meta-data
    private void reset() {
        final List<GridRow> rows = gridData.getRows();
        for (GridRow row : rows) {
            row.reset();
        }
    }

    public void onInsertRow(final int rowIndex) {
        if (!gridData.isMerged()) {
            return;
        }
        final List<GridColumn<?>> columns = gridData.getColumns();
        for (int columnIndex = 0; columnIndex < columns.size(); columnIndex++) {
            final GridColumn<?> column = columns.get(columnIndex);
            final int _columnIndex = column.getIndex();
            if (rowIndex > 0) {
                updateMergeMetaData(rowIndex - 1,
                                    _columnIndex);
            }
            if (rowIndex < gridData.getRowCount() - 1) {
                updateMergeMetaData(rowIndex + 1,
                                    _columnIndex);
            }
        }
    }

    public void onDeleteRow(final GridData.Range range) {
        if (!gridData.isMerged()) {
            return;
        }
        final int minRowIndex = range.getMinRowIndex();
        final int maxRowIndex = range.getMaxRowIndex();
        final List<GridColumn<?>> columns = gridData.getColumns();
        for (int _rowIndex = minRowIndex; _rowIndex <= maxRowIndex; _rowIndex++) {
            for (int columnIndex = 0; columnIndex < columns.size(); columnIndex++) {
                final GridColumn<?> column = columns.get(columnIndex);
                final int _columnIndex = column.getIndex();
                if (minRowIndex < gridData.getRowCount()) {
                    updateMergeMetaData(minRowIndex,
                                        _columnIndex);
                } else if (minRowIndex > 0) {
                    updateMergeMetaData(minRowIndex - 1,
                                        _columnIndex);
                }
            }
        }
    }

    private void updateMergeMetaData(final int rowIndex,
                                     final int columnIndex) {
        updateMergeMetaData(rowIndex,
                            columnIndex,
                            0,
                            gridData.getRowCount());
    }

    private void updateMergeMetaData(final int rowIndex,
                                     final int columnIndex,
                                     final int minRowIndex,
                                     final int maxRowIndex) {
        //Find the cell's current value
        final GridRow currentRow = gridData.getRow(rowIndex);
        final GridCell<?> currentRowCell = currentRow.getCells().get(columnIndex);

        //Find minimum row with a cell containing the same value as that being updated
        final int minBlockRowIndex = findMinRowIndex(rowIndex,
                                                     columnIndex,
                                                     minRowIndex,
                                                     currentRowCell);
        //Find maximum row with a cell containing the same value as that being updated
        final int maxBlockRowIndex = findMaxRowIndex(rowIndex,
                                                     columnIndex,
                                                     maxRowIndex,
                                                     currentRowCell);

        //Update merge meta-data
        updateMergeMetaData(minBlockRowIndex,
                            maxBlockRowIndex,
                            columnIndex);
    }

    public void onSetCell(final GridData.Range range,
                          final int columnIndex) {
        final int minRowIndex = range.getMinRowIndex();
        updateMergeMetaData(minRowIndex,
                            columnIndex);
    }

    public void onDeleteCell(final GridData.Range range,
                             final int columnIndex) {
        final int minRowIndex = range.getMinRowIndex();
        final int maxRowIndex = range.getMaxRowIndex();
        for (int i = minRowIndex; i <= maxRowIndex; i++) {
            final GridRow row = gridData.getRow(i);
            updateRowMergedCells(row);
        }

        updateMergeMetaData(minRowIndex,
                            columnIndex);
    }

    public void onCollapseCell(final int rowIndex,
                               final int columnIndex) {
        int minRowIndex = rowIndex;
        int maxRowIndex = rowIndex + 1;
        final List<GridRow> rows = gridData.getRows();
        final List<GridColumn<?>> columns = gridData.getColumns();
        final GridRow currentRow = rows.get(rowIndex);
        final GridCell<?> currentRowCell = currentRow.getCells().get(columnIndex);

        if (currentRowCell == null) {
            return;
        }

        if (currentRowCell.getMergedCellCount() == 0) {
            do {
                minRowIndex--;
                final GridRow previousRow = rows.get(minRowIndex);
                final GridCell<?> previousRowCell = previousRow.getCells().get(columnIndex);
                if (previousRowCell.getMergedCellCount() > 0) {
                    break;
                }
            }
            while (minRowIndex > 0);
        }

        while (maxRowIndex < rows.size()) {
            final GridRow nextRow = rows.get(maxRowIndex);
            final GridCell<?> nextRowCell = nextRow.getCells().get(columnIndex);
            if (nextRowCell == null) {
                break;
            }
            if (nextRowCell.getMergedCellCount() > 0) {
                break;
            }
            maxRowIndex++;
        }

        for (int i = minRowIndex + 1; i < maxRowIndex; i++) {
            rows.get(i).collapse();
        }

        for (int i = 0; i < columns.size(); i++) {
            final int _columnIndex = columns.get(i).getIndex();
            if (_columnIndex == columnIndex) {
                continue;
            }
            updateMergeMetaDataOnCollapseTopSplitRows(minRowIndex,
                                                      maxRowIndex,
                                                      _columnIndex);
            updateMergeMetaDataOnCollapseBottomSplitRows(minRowIndex,
                                                         maxRowIndex,
                                                         _columnIndex);
        }
    }

    private void updateMergeMetaDataOnCollapseTopSplitRows(final int minRowIndex,
                                                           final int maxRowIndex,
                                                           final int columnIndex) {
        if (minRowIndex < 1) {
            return;
        }

        final List<GridRow> rows = gridData.getRows();
        final GridRow checkTopRow = gridData.getRow(minRowIndex - 1);
        final GridCell<?> checkTopCell = checkTopRow.getCells().get(columnIndex);

        if (checkTopCell == null) {
            return;
        }

        if (checkTopCell.getMergedCellCount() == 1) {
            return;
        }

        // Scan from the first row before the start of collapsed block downwards to the end of the
        // collapsed block. If any cell is not identical to first then we need to split the cell.
        boolean splitTopSection = false;
        for (int collapsedRowIndex = minRowIndex; collapsedRowIndex < maxRowIndex; collapsedRowIndex++) {
            final GridRow collapsedRow = gridData.getRow(collapsedRowIndex);
            final GridCell<?> collapsedCell = collapsedRow.getCells().get(columnIndex);
            if (collapsedCell == null) {
                break;
            }
            if (!collapsedCell.equals(checkTopCell)) {
                break;
            }
            splitTopSection = collapsedRowIndex < maxRowIndex - 1;
        }

        if (splitTopSection) {

            //Find minimum row with a cell containing the same value as the split-point
            int checkMinRowIndex = minRowIndex - 1;
            if (checkTopCell.getMergedCellCount() == 0) {
                while (checkMinRowIndex > 0) {
                    final GridRow previousRow = rows.get(checkMinRowIndex);
                    final GridCell<?> previousRowCell = previousRow.getCells().get(columnIndex);
                    if (previousRowCell == null) {
                        break;
                    }
                    if (previousRowCell.getMergedCellCount() > 0) {
                        break;
                    }
                    checkMinRowIndex--;
                }
            }

            //Update merge meta-data for top part of split cell
            if (minRowIndex > checkMinRowIndex) {
                for (int i = checkMinRowIndex; i < minRowIndex; i++) {
                    final GridRow row = rows.get(i);
                    final BaseGridCell cell = ((BaseGridCell) row.getCells().get(columnIndex));
                    if (cell != null) {
                        cell.setMergedCellCount(0);
                    }
                    updateRowMergedCells(row);
                }

                final GridRow topSplitRow = rows.get(checkMinRowIndex);
                final BaseGridCell topSplitRowCell = ((BaseGridCell) topSplitRow.getCells().get(columnIndex));
                if (topSplitRowCell != null) {
                    topSplitRowCell.setMergedCellCount(minRowIndex - checkMinRowIndex);
                }
                updateRowMergedCells(topSplitRow);
            }

            //Find maximum row with a cell containing the same value as the split-point
            int checkMaxRowIndex = minRowIndex;
            boolean foundBottomSplitMarker = false;
            while (checkMaxRowIndex < rows.size()) {
                final GridRow nextRow = rows.get(checkMaxRowIndex);
                final GridCell<?> nextRowCell = nextRow.getCells().get(columnIndex);
                if (nextRowCell == null) {
                    break;
                }
                if (nextRowCell.isCollapsed() && foundBottomSplitMarker) {
                    checkMaxRowIndex--;
                    break;
                }
                if (!nextRowCell.equals(checkTopCell)) {
                    break;
                }
                if (nextRowCell.getMergedCellCount() > 0) {
                    foundBottomSplitMarker = true;
                }
                checkMaxRowIndex++;
            }

            //Update merge meta-data for bottom part of split cell
            if (checkMaxRowIndex > minRowIndex) {
                for (int i = minRowIndex; i < checkMaxRowIndex; i++) {
                    final GridRow row = rows.get(i);
                    final BaseGridCell cell = ((BaseGridCell) row.getCells().get(columnIndex));
                    if (cell != null) {
                        cell.setMergedCellCount(0);
                    }
                    updateRowMergedCells(row);
                }

                final GridRow bottomSplitRow = rows.get(minRowIndex);
                final BaseGridCell bottomSplitRowCell = ((BaseGridCell) bottomSplitRow.getCells().get(columnIndex));
                if (bottomSplitRowCell != null) {
                    bottomSplitRowCell.setMergedCellCount(checkMaxRowIndex - minRowIndex);
                }
                updateRowMergedCells(bottomSplitRow);
            }
        }
    }

    private void updateMergeMetaDataOnCollapseBottomSplitRows(final int minRowIndex,
                                                              final int maxRowIndex,
                                                              final int columnIndex) {
        final List<GridRow> rows = gridData.getRows();
        if (maxRowIndex == rows.size()) {
            return;
        }

        final GridRow checkBottomRow = gridData.getRow(maxRowIndex);
        final GridCell<?> checkBottomCell = checkBottomRow.getCells().get(columnIndex);

        if (checkBottomCell == null) {
            return;
        }

        if (checkBottomCell.getMergedCellCount() == 1) {
            return;
        }

        // Scan from the first row after the end of collapsed block upwards to the beginning of the
        // collapsed block. If any cell is not identical to first then we need to split the cell.
        boolean splitBottomSection = false;
        for (int collapsedRowIndex = maxRowIndex - 1; collapsedRowIndex >= minRowIndex; collapsedRowIndex--) {
            final GridRow collapsedRow = gridData.getRow(collapsedRowIndex);
            final GridCell<?> collapsedCell = collapsedRow.getCells().get(columnIndex);
            if (collapsedCell == null) {
                break;
            }
            if (!collapsedCell.equals(checkBottomCell)) {
                break;
            }
            splitBottomSection = collapsedRowIndex > minRowIndex;
        }

        if (splitBottomSection) {

            //Find minimum row with a cell containing the same value as the split-point
            int checkMinRowIndex = maxRowIndex - 1;
            if (checkBottomCell.getMergedCellCount() == 0) {
                while (checkMinRowIndex > 0) {
                    final GridRow previousRow = rows.get(checkMinRowIndex);
                    final GridCell<?> previousRowCell = previousRow.getCells().get(columnIndex);
                    if (previousRowCell == null) {
                        break;
                    }
                    if (previousRowCell.getMergedCellCount() > 0) {
                        break;
                    }
                    checkMinRowIndex--;
                }
            }

            //Update merge meta-data for top part of split cell
            if (maxRowIndex > checkMinRowIndex) {
                for (int i = checkMinRowIndex; i < maxRowIndex; i++) {
                    final GridRow row = rows.get(i);
                    final BaseGridCell cell = ((BaseGridCell) row.getCells().get(columnIndex));
                    if (cell != null) {
                        cell.setMergedCellCount(0);
                    }
                    updateRowMergedCells(row);
                }

                final GridRow topSplitRow = rows.get(checkMinRowIndex);
                final BaseGridCell topSplitRowCell = ((BaseGridCell) topSplitRow.getCells().get(columnIndex));
                if (topSplitRowCell != null) {
                    topSplitRowCell.setMergedCellCount(maxRowIndex - checkMinRowIndex);
                }
                updateRowMergedCells(topSplitRow);
            }

            //Find maximum row with a cell containing the same value as the split-point
            int checkMaxRowIndex = maxRowIndex;
            boolean foundBottomSplitMarker = false;
            while (checkMaxRowIndex < rows.size()) {
                final GridRow nextRow = rows.get(checkMaxRowIndex);
                final GridCell<?> nextRowCell = nextRow.getCells().get(columnIndex);
                if (nextRowCell == null) {
                    break;
                }
                if (nextRowCell.isCollapsed() && foundBottomSplitMarker) {
                    checkMaxRowIndex--;
                    break;
                }
                if (!nextRowCell.equals(checkBottomCell)) {
                    break;
                }
                if (nextRowCell.getMergedCellCount() > 0) {
                    foundBottomSplitMarker = true;
                }
                checkMaxRowIndex++;
            }

            //Update merge meta-data for bottom part of split cell
            if (checkMaxRowIndex > maxRowIndex) {
                for (int i = maxRowIndex; i < checkMaxRowIndex; i++) {
                    final GridRow row = rows.get(i);
                    final BaseGridCell cell = ((BaseGridCell) row.getCells().get(columnIndex));
                    if (cell != null) {
                        cell.setMergedCellCount(0);
                    }
                    updateRowMergedCells(row);
                }

                //Only split bottom if it isn't already split
                final GridRow bottomSplitRow = rows.get(maxRowIndex);
                if (bottomSplitRow.getCells().get(columnIndex).getMergedCellCount() == 0) {
                    final BaseGridCell bottomSplitRowCell = ((BaseGridCell) bottomSplitRow.getCells().get(columnIndex));
                    if (bottomSplitRowCell != null) {
                        bottomSplitRowCell.setMergedCellCount(checkMaxRowIndex - maxRowIndex);
                    }
                    updateRowMergedCells(bottomSplitRow);
                }
            }
        }
    }

    public void onExpandCell(final int rowIndex,
                             final int columnIndex) {
        int minRowIndex = rowIndex;
        int maxRowIndex = rowIndex + 1;
        final List<GridRow> rows = gridData.getRows();
        final List<GridColumn<?>> columns = gridData.getColumns();
        final GridRow currentRow = rows.get(rowIndex);
        final GridCell<?> currentRowCell = currentRow.getCells().get(columnIndex);

        if (currentRowCell == null) {
            return;
        }

        if (currentRowCell.getMergedCellCount() == 0) {
            do {
                minRowIndex--;
                final GridRow previousRow = rows.get(minRowIndex);
                final GridCell<?> previousRowCell = previousRow.getCells().get(columnIndex);
                if (previousRowCell == null) {
                    break;
                }
                if (previousRowCell.getMergedCellCount() > 0) {
                    break;
                }
            }
            while (minRowIndex > 0);
        }

        while (maxRowIndex < rows.size()) {
            final GridRow nextRow = rows.get(maxRowIndex);
            final GridCell<?> nextRowCell = nextRow.getCells().get(columnIndex);
            if (nextRowCell == null) {
                break;
            }
            if (nextRowCell.getMergedCellCount() > 0) {
                break;
            }
            maxRowIndex++;
        }

        for (int i = minRowIndex + 1; i < maxRowIndex; i++) {
            rows.get(i).expand();
        }

        for (int i = 0; i < columns.size(); i++) {
            final int _columnIndex = columns.get(i).getIndex();
            updateMergeMetaDataOnExpand(minRowIndex,
                                        maxRowIndex,
                                        _columnIndex);
            updateMergeMetaDataOnExpand(maxRowIndex - 1,
                                        maxRowIndex,
                                        _columnIndex);
        }
    }

    private void updateMergeMetaDataOnExpand(final int expandMinRowIndex,
                                             final int expandMaxRowIndex,
                                             final int columnIndex) {
        //Find the cell's current value
        final List<GridRow> rows = gridData.getRows();
        final GridRow currentRow = gridData.getRow(expandMinRowIndex);
        final GridCell<?> currentRowCell = currentRow.getCells().get(columnIndex);

        //Find minimum row with a cell containing the same value as that being updated
        final int minRowIndex = findMinRowIndex(expandMinRowIndex,
                                                columnIndex,
                                                0,
                                                currentRowCell);
        //Find maximum row with a cell containing the same value as that being updated
        final int maxRowIndex = findMaxRowIndex(expandMinRowIndex,
                                                columnIndex,
                                                rows.size(),
                                                currentRowCell);

        //Update merge meta-data
        updateMergeMetaData(minRowIndex,
                            maxRowIndex,
                            columnIndex);

        //If merged block is partially collapsed split it
        final GridRow row = rows.get(minRowIndex);
        final BaseGridCell cell = ((BaseGridCell) row.getCells().get(columnIndex));
        if (maxRowIndex > expandMaxRowIndex) {
            final GridRow bottomSplitRow = rows.get(expandMaxRowIndex);
            if (bottomSplitRow.isCollapsed()) {
                final BaseGridCell bottomSplitRowCell = ((BaseGridCell) bottomSplitRow.getCells().get(columnIndex));
                if (bottomSplitRowCell != null) {
                    bottomSplitRowCell.setMergedCellCount(maxRowIndex - expandMaxRowIndex);
                }
                updateRowMergedCells(bottomSplitRow);
                bottomSplitRow.expand();
                if (cell != null) {
                    cell.setMergedCellCount(expandMaxRowIndex - minRowIndex);
                }
                updateRowMergedCells(row);
            }
        }
    }

    public void onMoveRows(final List<GridRow> rowsMoved,
                           final GridData.Range oldBlockExtent) {
        if (!gridData.isMerged()) {
            return;
        }

        final List<GridRow> rows = gridData.getRows();
        final List<GridColumn<?>> columns = gridData.getColumns();
        final int oldBlockEnd = oldBlockExtent.getMaxRowIndex();
        final int newBlockStart = rows.indexOf(rowsMoved.get(0));
        final int newBlockEnd = rows.indexOf(rowsMoved.get(rowsMoved.size() - 1));

        boolean isCollapsedBlock = false;
        for (GridRow rowMoved : rowsMoved) {
            if (rowMoved.isCollapsed()) {
                isCollapsedBlock = true;
                break;
            }
        }

        //Update indexes for where rows were removed
        for (GridColumn<?> column : columns) {
            final int _columnIndex = column.getIndex();
            if (oldBlockEnd > 0) {
                updateMergeMetaData(oldBlockEnd - 1,
                                    _columnIndex);
            }
            if (oldBlockEnd < rows.size() - 1) {
                updateMergeMetaData(oldBlockEnd + 1,
                                    _columnIndex);
            }
            if (oldBlockEnd == rows.size() - 1) {
                updateMergeMetaData(oldBlockEnd, _columnIndex);
            }
        }

        //Update indexes for where rows were inserted
        for (GridColumn<?> column : columns) {
            final int _columnIndex = column.getIndex();
            if (isCollapsedBlock) {
                updateMergedMetaDataRowMove(newBlockStart,
                                            newBlockEnd,
                                            _columnIndex);
            } else {

                if (newBlockStart > 0) {
                    updateMergeMetaData(newBlockStart - 1,
                                        _columnIndex);
                }
                if (newBlockStart < rows.size() - 1) {
                    updateMergeMetaData(newBlockStart + 1,
                                        _columnIndex);
                }
                if (newBlockEnd > 0) {
                    updateMergeMetaData(newBlockEnd - 1,
                                        _columnIndex);
                }
                if (newBlockEnd < rows.size() - 1) {
                    updateMergeMetaData(newBlockEnd + 1,
                                        _columnIndex);
                }
            }
        }
    }

    private void updateMergedMetaDataRowMove(final int blockStart,
                                             final int blockEnd,
                                             final int columnIndex) {
        //Back track from the row prior to the BlockStart, correcting MetaData
        if (blockStart > 0) {
            final GridRow currentRow = gridData.getRow(blockStart - 1);
            final GridCell<?> currentRowCell = currentRow.getCells().get(columnIndex);

            int minBlockRowIndex = findMinRowIndex(blockStart - 1,
                                                   columnIndex,
                                                   0,
                                                   currentRowCell);
            final int maxBlockRowIndex = blockStart;

            //Update merge meta-data
            updateMergeMetaData(minBlockRowIndex,
                                maxBlockRowIndex,
                                columnIndex);
        }

        //Forward track form the row after the BlockEnd, correcting MetaData
        if (blockEnd < gridData.getRowCount() - 1) {
            final GridRow currentRow = gridData.getRow(blockEnd + 1);
            final GridCell<?> currentRowCell = currentRow.getCells().get(columnIndex);

            final int minBlockRowIndex = blockEnd + 1;
            final int maxBlockRowIndex = findMaxRowIndex(blockEnd,
                                                         columnIndex,
                                                         gridData.getRowCount(),
                                                         currentRowCell);

            //Update merge meta-data
            updateMergeMetaData(minBlockRowIndex,
                                maxBlockRowIndex,
                                columnIndex);
        }

        //Update the moved block
        updateMergeMetaData(blockStart,
                            columnIndex,
                            blockStart,
                            blockEnd + 1);
    }

    private int findMinRowIndex(final int rowIndex,
                                final int columnIndex,
                                final int minRowIndex,
                                final GridCell<?> currentRowCell) {
        int minBlockRowIndex = rowIndex;

        if (currentRowCell != null) {

            boolean foundTopSplitMarker = currentRowCell.getMergedCellCount() > 0;

            while (minBlockRowIndex > minRowIndex) {
                final GridRow previousRow = gridData.getRow(minBlockRowIndex - 1);
                final GridCell<?> previousRowCell = previousRow.getCells().get(columnIndex);
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
                minBlockRowIndex--;
            }
        }

        return minBlockRowIndex;
    }

    private int findMaxRowIndex(final int rowIndex,
                                final int columnIndex,
                                final int maxRowIndex,
                                final GridCell<?> currentRowCell) {

        int maxBlockRowIndex = rowIndex + 1;

        boolean foundBottomSplitMarker = false;
        while (maxBlockRowIndex < maxRowIndex) {
            final GridRow nextRow = gridData.getRow(maxBlockRowIndex);
            final GridCell<?> nextRowCell = nextRow.getCells().get(columnIndex);
            if (nextRowCell == null) {
                break;
            }
            if (nextRowCell.isCollapsed() && foundBottomSplitMarker) {
                maxBlockRowIndex--;
                break;
            }
            if (!nextRowCell.equals(currentRowCell)) {
                break;
            }
            if (nextRowCell.getMergedCellCount() > 0) {
                foundBottomSplitMarker = true;
            }
            maxBlockRowIndex++;
        }

        return maxBlockRowIndex;
    }

    private void updateMergeMetaData(final int minBlockRowIndex,
                                     final int maxBlockRowIndex,
                                     final int columnIndex) {
        for (int i = minBlockRowIndex; i < maxBlockRowIndex; i++) {
            final GridRow row = gridData.getRow(i);
            final BaseGridCell cell = ((BaseGridCell) row.getCells().get(columnIndex));
            if (cell != null) {
                cell.setMergedCellCount(0);
            }
            updateRowMergedCells(row);
        }

        final GridRow row = gridData.getRow(minBlockRowIndex);
        final BaseGridCell cell = ((BaseGridCell) row.getCells().get(columnIndex));
        if (cell != null) {
            cell.setMergedCellCount(maxBlockRowIndex - minBlockRowIndex);
        }
        updateRowMergedCells(row);
    }

    private void updateRowMergedCells(final GridRow row) {
        for (GridCell<?> cell : row.getCells().values()) {
            if (cell.isMerged()) {
                ((BaseGridRow) row).setHasMergedCells(true);
                return;
            }
        }
        ((BaseGridRow) row).setHasMergedCells(false);
    }
}
