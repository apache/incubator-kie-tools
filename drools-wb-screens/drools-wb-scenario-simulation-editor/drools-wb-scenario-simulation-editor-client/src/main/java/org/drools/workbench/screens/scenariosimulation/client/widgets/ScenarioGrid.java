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

import java.util.List;
import java.util.stream.IntStream;

import com.ait.lienzo.client.core.event.NodeMouseDoubleClickHandler;
import com.ait.lienzo.shared.core.types.EventPropagationMode;
import org.drools.workbench.screens.scenariosimulation.client.handlers.ScenarioSimulationGridPanelDoubleClickHandler;
import org.drools.workbench.screens.scenariosimulation.client.models.ScenarioGridModel;
import org.drools.workbench.screens.scenariosimulation.client.renderers.ScenarioGridRenderer;
import org.drools.workbench.screens.scenariosimulation.client.values.ScenarioGridCellValue;
import org.drools.workbench.screens.scenariosimulation.model.ExpressionIdentifier;
import org.drools.workbench.screens.scenariosimulation.model.FactIdentifier;
import org.drools.workbench.screens.scenariosimulation.model.FactMapping;
import org.drools.workbench.screens.scenariosimulation.model.Scenario;
import org.drools.workbench.screens.scenariosimulation.model.Simulation;
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

    public void setContent(Simulation simulation) {
        ((ScenarioGridModel) model).clear();
        ((ScenarioGridModel) model).bindContent(simulation);
        setHeaderColumns(simulation);
        appendRows(simulation);
    }

    @Override
    protected NodeMouseDoubleClickHandler getGridMouseDoubleClickHandler(final GridSelectionManager selectionManager,
                                                                         final GridPinnedModeManager pinnedModeManager) {
        return new ScenarioSimulationGridPanelDoubleClickHandler(this,
                                                                 selectionManager,
                                                                 pinnedModeManager,
                                                                 renderer);
    }

    private void setHeaderColumns(Simulation simulation) {
        List<FactMapping> factMappings = simulation.getSimulationDescriptor().getFactMappings();
        IntStream.range(0, factMappings.size()).forEach(i -> {
            FactMapping fact = factMappings.get(i);
            String columnId = fact.getExpressionIdentifier().getName();
            String columnTitle = fact.getExpressionAlias();
            String columnGroup = fact.getExpressionIdentifier().getType().name();
            model.insertColumn(i,
                               getScenarioGridColumn(columnId, columnTitle, columnGroup, scenarioGridPanel, scenarioGridLayer));
        });
    }

    private void appendRows(Simulation simulation) {
        List<Scenario> scenarios = simulation.getScenarios();
        IntStream.range(0, scenarios.size()).forEach(rowIndex -> {
            Scenario scenario = scenarios.get(rowIndex);
            model.insertRow(rowIndex, new ScenarioGridRow());
            scenario.getFactMappingValues().forEach(value -> {
                FactIdentifier factIdentifier = value.getFactIdentifier();
                ExpressionIdentifier expressionIdentifier = value.getExpressionIdentifier();
                if (value.getRawValue() instanceof String) {
                    String stringValue = (String) value.getRawValue();
                    int columnIndex = simulation.getSimulationDescriptor().getIndexByIdentifier(factIdentifier, expressionIdentifier);
                    model.setCell(rowIndex, columnIndex,
                                  () -> new ScenarioGridCell(new ScenarioGridCellValue(stringValue)));
                } else {
                    throw new UnsupportedOperationException("Only string is supported at the moment");
                }
            });
        });
    }
}