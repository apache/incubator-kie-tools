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
package org.drools.workbench.screens.scenariosimulation.client.widgets;

import java.util.Map;

import com.ait.lienzo.client.core.event.NodeMouseDoubleClickHandler;
import com.ait.lienzo.shared.core.types.EventPropagationMode;
import org.drools.workbench.screens.scenariosimulation.client.handlers.ScenarioSimulationGridPanelDoubleClickHandler;
import org.drools.workbench.screens.scenariosimulation.client.models.ScenarioGridModel;
import org.drools.workbench.screens.scenariosimulation.client.renderers.ScenarioGridRenderer;
import org.drools.workbench.screens.scenariosimulation.client.values.ScenarioGridCellValue;
import org.uberfire.ext.wires.core.grids.client.widget.grid.impl.BaseGridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridSelectionManager;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.GridPinnedModeManager;

import static org.drools.workbench.screens.scenariosimulation.client.utils.ScenarioSimulationUtils.getScenarioGridColumn;

public class ScenarioGrid extends BaseGridWidget {

    private final ScenarioGridLayer scenarioGridLayer;
    private final ScenarioGridPanel scenarioGridPanel;

    public ScenarioGrid(ScenarioGridModel model, ScenarioGridLayer scenarioGridLayer, ScenarioGridRenderer renderer, ScenarioGridPanel scenarioGridPanel) {
        super(model, scenarioGridLayer, scenarioGridLayer, renderer);
        this.scenarioGridLayer = scenarioGridLayer;
        this.scenarioGridPanel = scenarioGridPanel;
        setDraggable(false);
        setEventPropagationMode(EventPropagationMode.NO_ANCESTORS);
    }

    public void setContent(Map<Integer, String> headersMap, Map<Integer, Map<Integer, String>> rowsMap) {
        ((ScenarioGridModel) model).clear();
        ((ScenarioGridModel) model).bindContent(headersMap, rowsMap);
        setHeaderColumns(headersMap);
        appendRows(rowsMap);
    }

    // Add for testing purpose
    public ScenarioGridLayer getScenarioGridLayer() {
        return scenarioGridLayer;
    }

//    @Override
//    public Group setDraggable(boolean draggable) {
//        GWT.log("setDraggable " + draggable);
//        return super.setDraggable(draggable);
//    }
//
//    @Override
//    public boolean isDraggable() {
//        return false; // FOR SOME REASON, event if setDraggable(false) in the constructor, the base method returns true
//    }

    @Override
    protected NodeMouseDoubleClickHandler getGridMouseDoubleClickHandler(final GridSelectionManager selectionManager,
                                                                         final GridPinnedModeManager pinnedModeManager) {
        return new ScenarioSimulationGridPanelDoubleClickHandler(this,
                                                                 selectionManager,
                                                                 pinnedModeManager,
                                                                 renderer);
    }

    private void setHeaderColumns(Map<Integer, String> headersMap) {
        headersMap.forEach((columnIndex, columnTitle) ->
                                   model.insertColumn(columnIndex, getScenarioGridColumn(columnTitle, scenarioGridPanel, scenarioGridLayer)));
    }

    private void appendRows(Map<Integer, Map<Integer, String>> rowsMap) {
        rowsMap.forEach((rowIndex, cellValueMap) -> {
            model.insertRow(rowIndex, new ScenarioGridRow());
            cellValueMap.forEach((columnIndex, cellValue) -> model.setCell(rowIndex, columnIndex, () -> new ScenarioGridCell(new ScenarioGridCellValue(cellValue))));
        });
    }
}