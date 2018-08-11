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
package org.drools.workbench.screens.scenariosimulation.client.utils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ait.lienzo.client.core.event.NodeMouseClickEvent;
import com.google.gwt.dom.client.NativeEvent;
import org.drools.workbench.screens.scenariosimulation.client.factories.FactoryProvider;
import org.drools.workbench.screens.scenariosimulation.client.metadata.ScenarioHeaderMetaData;
import org.drools.workbench.screens.scenariosimulation.client.renderers.ScenarioGridColumnRenderer;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGrid;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridColumn;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridLayer;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridPanel;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.widget.dom.single.impl.TextBoxSingletonDOMElementFactory;

public class ScenarioSimulationUtils {


    public static GridCell<String> getSelectedCell(final ScenarioGrid source) {
        GridData.SelectedCell selectedCell = source.getModel().getSelectedCells().get(0);
        return (GridCell<String>) source.getModel().getCell(selectedCell.getRowIndex(), selectedCell.getColumnIndex());
    }

    public static String getScenarioGridSelectedCellString(final ScenarioGrid source) {
        List<GridData.SelectedCell> selectedCells = source.getModel().getSelectedCells();
        return selectedCells.stream()
                .map(selectedCell -> (GridCell<String>) source.getModel().getCell(selectedCell.getRowIndex(), selectedCell.getColumnIndex()))
                .map(selectedCell -> selectedCell.getValue().getValue())
                .collect(Collectors.joining(","));
    }

    public static ScenarioGridColumn getScenarioGridColumn(String columnId, String columnTitle, ScenarioGridPanel scenarioGridPanel, ScenarioGridLayer gridLayer) {
        TextBoxSingletonDOMElementFactory factory = FactoryProvider.getHeaderHasNameTextBoxFactory(scenarioGridPanel, gridLayer);
        return new ScenarioGridColumn(new ScenarioHeaderMetaData(columnId, columnTitle, "", factory), new ScenarioGridColumnRenderer(), 100, false);
    }

    public static String getScenarioGridSelectedRowString(final ScenarioGrid source) {
        GridData model = source.getModel();
        GridData.SelectedCell singleCell = model.getSelectedCells().get(0);
        Map<Integer, GridCell<?>> cells = model.getRow(singleCell.getRowIndex()).getCells();
        return cells.values().stream()
                .map(cell -> ((GridCell<String>) cell))
                .map(cell -> cell.getValue().getValue())
                .collect(Collectors.joining(","));
    }


    public static boolean isRightClick(NodeMouseClickEvent event) {
        return event.getMouseEvent().getNativeButton() == NativeEvent.BUTTON_RIGHT;
    }
}
