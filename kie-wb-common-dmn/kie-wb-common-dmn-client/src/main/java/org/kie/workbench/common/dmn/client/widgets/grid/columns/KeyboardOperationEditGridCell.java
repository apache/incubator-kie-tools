/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.dmn.client.widgets.grid.columns;

import java.util.Optional;

import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionCellValue;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.util.ColumnIndexUtilities;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.impl.KeyboardOperationEditCell;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;

public class KeyboardOperationEditGridCell extends KeyboardOperationEditCell {

    public KeyboardOperationEditGridCell(final GridLayer gridLayer) {
        super(gridLayer);
    }

    @Override
    protected void editCell(final GridWidget gridWidget) {
        final GridData model = gridWidget.getModel();
        if (model.getSelectedCells().size() > 1) {
            // just one data cell can be selected before edit operation is invoked
            return;
        }

        super.editCell(gridWidget);

        if (model.getSelectedCells().size() == 1) {
            final GridData.SelectedCell selectedCell = model.getSelectedCells().get(0);
            final GridCellValue<?> value =
                    model.getCell(selectedCell.getRowIndex(),
                                  ColumnIndexUtilities.findUiColumnIndex(model.getColumns(),
                                                                         selectedCell.getColumnIndex())).getValue();
            if (value instanceof ExpressionCellValue) {
                final Optional<BaseExpressionGrid> grid = ((ExpressionCellValue) value).getValue();
                grid.ifPresent(baseExpressionGrid -> {
                    gridLayer.select(baseExpressionGrid);
                    baseExpressionGrid.selectFirstCell();
                });
            }
        } else if (model.getSelectedHeaderCells().size() > 0) {
            final GridData.SelectedCell selectedCell = model.getSelectedHeaderCells().get(0);
            final int uiHeaderRowIndex = selectedCell.getRowIndex();
            final int uiColumnIndex = ColumnIndexUtilities.findUiColumnIndex(model.getColumns(),
                                                                             selectedCell.getColumnIndex());

            final GridColumn<?> column = model.getColumns().get(uiColumnIndex);

            if (column instanceof DMNGridColumn) {
                ((DMNGridColumn) column).startEditingHeaderCell(uiHeaderRowIndex);
            }
        }
    }
}
