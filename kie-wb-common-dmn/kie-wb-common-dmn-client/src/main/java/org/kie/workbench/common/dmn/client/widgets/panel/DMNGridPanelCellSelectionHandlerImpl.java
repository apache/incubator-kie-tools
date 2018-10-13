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

package org.kie.workbench.common.dmn.client.widgets.panel;

import java.util.stream.Stream;

import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.GridData.SelectedCell;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.selections.CellSelectionStrategy;
import org.uberfire.ext.wires.core.grids.client.widget.grid.selections.impl.RangeSelectionStrategy;

public class DMNGridPanelCellSelectionHandlerImpl implements DMNGridPanelCellSelectionHandler {

    private final DMNGridLayer gridLayer;

    public DMNGridPanelCellSelectionHandlerImpl(final DMNGridLayer gridLayer) {
        this.gridLayer = gridLayer;
    }

    @Override
    public void selectCellIfRequired(final int uiRowIndex,
                                     final int uiColumnIndex,
                                     final GridWidget gridWidget,
                                     final boolean isShiftKeyDown,
                                     final boolean isControlKeyDown) {
        // If the right-click did not occur in an already selected cell, ensure the cell is selected
        final GridData gridData = gridWidget.getModel();
        final GridColumn<?> column = gridData.getColumns().get(uiColumnIndex);
        final Stream<SelectedCell> modelColumnSelectedCells = gridData.getSelectedCells().stream().filter(sc -> sc.getColumnIndex() == column.getIndex());
        final boolean isContextMenuCellSelectedCell = modelColumnSelectedCells.map(SelectedCell::getRowIndex).anyMatch(ri -> ri == uiRowIndex);
        if (!isContextMenuCellSelectedCell) {
            selectCell(uiRowIndex,
                       uiColumnIndex,
                       gridWidget,
                       isShiftKeyDown,
                       isControlKeyDown);
        }
    }

    private void selectCell(final int uiRowIndex,
                            final int uiColumnIndex,
                            final GridWidget gridWidget,
                            final boolean isShiftKeyDown,
                            final boolean isControlKeyDown) {
        // Lookup CellSelectionManager for cell
        final GridData gridModel = gridWidget.getModel();

        CellSelectionStrategy selectionStrategy;
        final GridCell<?> cell = gridModel.getCell(uiRowIndex,
                                                   uiColumnIndex);
        if (cell == null) {
            selectionStrategy = RangeSelectionStrategy.INSTANCE;
        } else {
            selectionStrategy = cell.getSelectionStrategy();
        }
        if (selectionStrategy == null) {
            return;
        }

        gridLayer.select(gridWidget);

        // Handle selection
        if (selectionStrategy.handleSelection(gridModel,
                                              uiRowIndex,
                                              uiColumnIndex,
                                              isShiftKeyDown,
                                              isControlKeyDown)) {
            gridLayer.batch();
        }
    }
}
