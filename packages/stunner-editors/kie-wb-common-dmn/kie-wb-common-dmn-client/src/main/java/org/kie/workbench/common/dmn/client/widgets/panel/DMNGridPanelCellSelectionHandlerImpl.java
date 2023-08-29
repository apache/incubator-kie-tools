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

package org.kie.workbench.common.dmn.client.widgets.panel;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.GridData.SelectedCell;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;

public class DMNGridPanelCellSelectionHandlerImpl implements DMNGridPanelCellSelectionHandler {

    private final DMNGridLayer gridLayer;

    public DMNGridPanelCellSelectionHandlerImpl(final DMNGridLayer gridLayer) {
        this.gridLayer = gridLayer;
    }

    @Override
    public void selectHeaderCellIfRequired(final int uiHeaderRowIndex,
                                           final int uiHeaderColumnIndex,
                                           final GridWidget gridWidget,
                                           final boolean isShiftKeyDown,
                                           final boolean isControlKeyDown) {
        final GridData gridData = gridWidget.getModel();
        final GridColumn<?> column = gridData.getColumns().get(uiHeaderColumnIndex);
        doSelectCellIfRequired(uiHeaderRowIndex,
                               column.getIndex(),
                               gridWidget,
                               gridData.getSelectedHeaderCells(),
                               () -> gridWidget.selectHeaderCell(uiHeaderRowIndex,
                                                                 uiHeaderColumnIndex,
                                                                 isShiftKeyDown,
                                                                 isControlKeyDown));
    }

    @Override
    public void selectCellIfRequired(final int uiRowIndex,
                                     final int uiColumnIndex,
                                     final GridWidget gridWidget,
                                     final boolean isShiftKeyDown,
                                     final boolean isControlKeyDown) {
        final GridData gridData = gridWidget.getModel();
        final GridColumn<?> column = gridData.getColumns().get(uiColumnIndex);
        doSelectCellIfRequired(uiRowIndex,
                               column.getIndex(),
                               gridWidget,
                               gridData.getSelectedCells(),
                               () -> gridWidget.selectCell(uiRowIndex,
                                                           uiColumnIndex,
                                                           isShiftKeyDown,
                                                           isControlKeyDown));
    }

    private void doSelectCellIfRequired(final int uiRowIndex,
                                        final int uiColumnIndex,
                                        final GridWidget gridWidget,
                                        final List<SelectedCell> selectedCells,
                                        final Supplier<Boolean> isSelectionChanged) {
        // If the right-click did not occur in an already selected cell, ensure the cell is selected
        final Stream<SelectedCell> modelColumnSelectedCells = selectedCells.stream().filter(sc -> sc.getColumnIndex() == uiColumnIndex);
        final boolean isContextMenuCellSelectedCell = modelColumnSelectedCells.map(SelectedCell::getRowIndex).anyMatch(ri -> ri == uiRowIndex);
        if (!isContextMenuCellSelectedCell) {
            gridLayer.select(gridWidget);
            if (isSelectionChanged.get()) {
                gridLayer.batch();
            }
        }
    }
}
