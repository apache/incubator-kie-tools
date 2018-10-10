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
import org.drools.workbench.screens.scenariosimulation.client.factories.FactoryProvider;
import org.drools.workbench.screens.scenariosimulation.client.factories.ScenarioHeaderTextBoxSingletonDOMElementFactory;
import org.drools.workbench.screens.scenariosimulation.client.handlers.ScenarioSimulationGridPanelDoubleClickHandler;
import org.drools.workbench.screens.scenariosimulation.client.models.ScenarioGridModel;
import org.drools.workbench.screens.scenariosimulation.client.renderers.ScenarioGridRenderer;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.drools.workbench.screens.scenariosimulation.client.utils.ScenarioSimulationBuilders;
import org.drools.workbench.screens.scenariosimulation.client.utils.ScenarioSimulationUtils;
import org.drools.workbench.screens.scenariosimulation.model.FactIdentifier;
import org.drools.workbench.screens.scenariosimulation.model.FactMapping;
import org.drools.workbench.screens.scenariosimulation.model.FactMappingType;
import org.drools.workbench.screens.scenariosimulation.model.Scenario;
import org.drools.workbench.screens.scenariosimulation.model.Simulation;
import org.uberfire.ext.wires.core.grids.client.widget.grid.impl.BaseGridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridSelectionManager;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.GridPinnedModeManager;

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
    public ScenarioGridModel getModel() {
        return (ScenarioGridModel) model;
    }

    /**
     * Unselect all cells/columns from model {@see GridData.clearSelections()}
     */
    public void clearSelections() {
        model.clearSelections();
    }

    /**
     * Select all the cells of the given column
     * @param columnIndex
     */
    public void selectColumn(int columnIndex) {
        ((ScenarioGridModel) model).selectColumn(columnIndex);
    }

    /**
     * Select all the cells of the given row
     * @param rowIndex
     */
    public void selectRow(int rowIndex) {
        ((ScenarioGridModel) model).selectRow(rowIndex);
    }

    @Override
    protected NodeMouseDoubleClickHandler getGridMouseDoubleClickHandler(final GridSelectionManager selectionManager,
                                                                         final GridPinnedModeManager pinnedModeManager) {
        return new ScenarioSimulationGridPanelDoubleClickHandler(this,
                                                                 selectionManager,
                                                                 pinnedModeManager,
                                                                 renderer);
    }

    protected void setHeaderColumns(Simulation simulation) {
        final List<FactMapping> factMappings = simulation.getSimulationDescriptor().getUnmodifiableFactMappings();
        IntStream.range(0, factMappings.size())
                .forEach(columnIndex -> {
                    setHeaderColumn(columnIndex, factMappings.get(columnIndex));
                });
    }

    protected void setHeaderColumn(int columnIndex, FactMapping factMapping) {
        String columnId = factMapping.getExpressionIdentifier().getName();
        String columnTitle = factMapping.getExpressionAlias();
        String columnGroup = factMapping.getExpressionIdentifier().getType().name();
        ScenarioHeaderTextBoxSingletonDOMElementFactory factoryHeader = getScenarioHeaderTextBoxSingletonDOMElementFactory();
        ScenarioSimulationBuilders.HeaderBuilder headerBuilder = getHeaderBuilderLocal(columnTitle, columnId, columnGroup, factMapping.getExpressionIdentifier().getType(), factoryHeader);
        boolean readOnly = FactIdentifier.EMPTY.equals(factMapping.getFactIdentifier()) || FactIdentifier.INDEX.equals(factMapping.getFactIdentifier());
        String placeHolder = readOnly ? ScenarioSimulationEditorConstants.INSTANCE.defineValidType() : ScenarioSimulationEditorConstants.INSTANCE.insertValue();
        ScenarioGridColumn scenarioGridColumn = getScenarioGridColumnLocal(headerBuilder, readOnly, placeHolder);
        ((ScenarioGridModel) model).insertColumnGridOnly(columnIndex, scenarioGridColumn);
    }

    protected ScenarioHeaderTextBoxSingletonDOMElementFactory getScenarioHeaderTextBoxSingletonDOMElementFactory() {
        return FactoryProvider.getHeaderTextBoxFactory(scenarioGridPanel, scenarioGridLayer);
    }

    protected ScenarioSimulationBuilders.HeaderBuilder getHeaderBuilderLocal(String columnTitle, String columnId, String columnGroup, FactMappingType factMappingType, ScenarioHeaderTextBoxSingletonDOMElementFactory factoryHeader) {
        return ScenarioSimulationUtils.getHeaderBuilder(columnTitle, columnId, columnGroup, factMappingType, factoryHeader);
    }

    protected ScenarioGridColumn getScenarioGridColumnLocal(ScenarioSimulationBuilders.HeaderBuilder headerBuilder, boolean readOnly, String placeHolder) {
        return ScenarioSimulationUtils.getScenarioGridColumn(headerBuilder, scenarioGridPanel, scenarioGridLayer, readOnly, placeHolder);
    }

    protected void appendRows(Simulation simulation) {
        List<Scenario> scenarios = simulation.getUnmodifiableScenarios();
        IntStream.range(0, scenarios.size()).forEach(rowIndex -> appendRow(rowIndex, scenarios.get(rowIndex)));
    }

    protected void appendRow(int rowIndex, Scenario scenario) {
        ((ScenarioGridModel) model).insertRowGridOnly(rowIndex, new ScenarioGridRow(), scenario);
    }
}