/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.dmn.client.commands.util;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionCellValue;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionEditorColumn;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;
import org.uberfire.ext.wires.core.grids.client.widget.dnd.IsRowDragHandle;

public class CommandUtils {

    public static void updateRowNumbers(final GridData uiModel,
                                        final IntStream rangeOfRowsToUpdate) {
        final Optional<GridColumn<?>> rowNumberColumn = uiModel
                .getColumns()
                .stream()
                .filter(column -> column instanceof IsRowDragHandle)
                .findFirst();

        rowNumberColumn.ifPresent(c -> {
            final int columnIndex = uiModel.getColumns().indexOf(c);
            rangeOfRowsToUpdate.forEach(
                    rowIndex -> uiModel.setCellValue(rowIndex,
                                                     columnIndex,
                                                     new BaseGridCellValue<>(rowIndex + 1))

            );
        });
    }

    public static <T> void moveRows(final List<T> allRows,
                                    final List<T> rowsToMove,
                                    final int index) {
        final int oldBlockStart = allRows.indexOf(rowsToMove.get(0));

        allRows.removeAll(rowsToMove);

        if (index < oldBlockStart) {
            allRows.addAll(index,
                           rowsToMove);
        } else if (index > oldBlockStart) {
            allRows.addAll(index - rowsToMove.size() + 1,
                           rowsToMove);
        }
    }

    public static void moveComponentWidths(final int index,
                                           final int oldIndex,
                                           final List<Double> componentWidths,
                                           final List<Integer> uiColumnIndexes) {
        final java.util.List<Double> componentWidthsToMove = uiColumnIndexes
                .stream()
                .map(componentWidths::get)
                .collect(Collectors.toList());

        uiColumnIndexes.forEach(i -> componentWidths.remove(oldIndex));
        if (index < oldIndex) {
            componentWidths.addAll(index,
                                   componentWidthsToMove);
        } else if (index > oldIndex) {
            componentWidths.addAll(oldIndex + 1,
                                   componentWidthsToMove);
        }
    }

    public static void updateParentInformation(final GridData uiModel) {
        final Optional<ExpressionEditorColumn> expressionColumn = uiModel
                .getColumns()
                .stream()
                .filter(c -> c instanceof ExpressionEditorColumn)
                .map(c -> (ExpressionEditorColumn) c)
                .findFirst();

        expressionColumn.ifPresent(c -> {
            final int columnIndex = uiModel.getColumns().indexOf(c);
            for (int rowIndex = 0; rowIndex < uiModel.getRowCount(); rowIndex++) {
                final GridCell<?> cell = uiModel.getCell(rowIndex, columnIndex);
                if (cell != null) {
                    final GridCellValue<?> value = cell.getValue();
                    if (value instanceof ExpressionCellValue) {
                        final ExpressionCellValue ecv = (ExpressionCellValue) value;
                        if (ecv.getValue().isPresent()) {
                            final BaseExpressionGrid beg = ecv.getValue().get();
                            beg.getParentInformation().setRowIndex(rowIndex);
                            beg.getParentInformation().setColumnIndex(columnIndex);
                        }
                    }
                }
            }
        });
    }

    public static Optional<GridCellValue<?>> extractGridCellValue(final GridCellTuple cellTuple) {
        final GridCell<?> cell = cellTuple.getGridWidget().getModel().getCell(cellTuple.getRowIndex(),
                                                                              cellTuple.getColumnIndex());
        return Optional.ofNullable(cell == null ? null : cell.getValue());
    }
}
