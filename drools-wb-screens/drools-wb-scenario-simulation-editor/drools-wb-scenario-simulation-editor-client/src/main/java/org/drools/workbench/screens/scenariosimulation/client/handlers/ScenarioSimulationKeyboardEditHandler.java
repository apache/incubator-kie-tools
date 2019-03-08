/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.scenariosimulation.client.handlers;

import org.drools.workbench.screens.scenariosimulation.client.models.ScenarioGridModel;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGrid;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.util.ColumnIndexUtilities;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.impl.KeyboardOperationEditCell;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;

public class ScenarioSimulationKeyboardEditHandler extends KeyboardOperationEditCell {

    public ScenarioSimulationKeyboardEditHandler(GridLayer gridLayer) {
        super(gridLayer);
    }

    @Override
    public boolean isExecutable(final GridWidget gridWidget) {
        final GridData model = gridWidget.getModel();
        if (model.getSelectedHeaderCells().size() == 1 && model.getSelectedCells().size() == 0) {
            return true;
        }
        if (model.getSelectedHeaderCells().size() == 0 && model.getSelectedCells().size() == 1) {
            return true;
        }
        return false;
    }

    @Override
    public boolean perform(final GridWidget gridWidget,
                           final boolean isShiftKeyDown,
                           final boolean isControlKeyDown) {
        ScenarioGrid scenarioGrid = (ScenarioGrid) gridWidget;
        final ScenarioGridModel scenarioGridModel = scenarioGrid.getModel();
        // Allows editing only if a single cell is selected
        GridData.SelectedCell selectedCell = null;
        boolean isHeader = true;
        if (scenarioGridModel.getSelectedHeaderCells().size() > 0) {
            selectedCell = scenarioGridModel.getSelectedHeaderCells().get(0);
        } else if (scenarioGridModel.getSelectedCells().size() > 0) {
            selectedCell = scenarioGridModel.getSelectedCellsOrigin();
            isHeader = false;
        }
        if (selectedCell == null) {
            return false;
        }
        final int uiRowIndex = selectedCell.getRowIndex();
        final int uiColumnIndex = ColumnIndexUtilities.findUiColumnIndex(scenarioGridModel.getColumns(),
                                                                         selectedCell.getColumnIndex());
        ScenarioGridColumn scenarioGridColumn = (ScenarioGridColumn) scenarioGridModel.getColumns().get(uiColumnIndex);
        if (scenarioGridColumn == null) {
            return false;
        }
        return CommonEditHandler.startEdit(scenarioGrid, uiColumnIndex, scenarioGridColumn, uiRowIndex, isHeader);
    }
}
