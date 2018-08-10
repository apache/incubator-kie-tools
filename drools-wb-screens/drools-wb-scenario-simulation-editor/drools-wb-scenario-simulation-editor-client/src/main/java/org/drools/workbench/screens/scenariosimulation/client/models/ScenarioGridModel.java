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
package org.drools.workbench.screens.scenariosimulation.client.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import org.drools.workbench.screens.scenariosimulation.model.ExpressionIdentifier;
import org.drools.workbench.screens.scenariosimulation.model.FactIdentifier;
import org.drools.workbench.screens.scenariosimulation.model.FactMapping;
import org.drools.workbench.screens.scenariosimulation.model.FactMappingType;
import org.drools.workbench.screens.scenariosimulation.model.Scenario;
import org.drools.workbench.screens.scenariosimulation.model.Simulation;
import org.drools.workbench.screens.scenariosimulation.model.SimulationDescriptor;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;

public class ScenarioGridModel extends BaseGridData {

    private Simulation simulation;

    public ScenarioGridModel() {
        super();
    }

    public ScenarioGridModel(boolean isMerged) {
        super(isMerged);
    }

    /**
     * Method to bind the data serialized inside backend <code>ScenarioSimulationModel</code>
     * @param simulation
     */
    public void bindContent(Simulation simulation) {
        this.simulation = simulation;
        checkSimulation();
    }

    @Override
    public void appendColumn(GridColumn<?> column) {
        checkSimulation();
        super.appendColumn(column);

        SimulationDescriptor simulationDescriptor = simulation.getSimulationDescriptor();
        String title = column.getHeaderMetaData().get(0).getTitle();
        String columnId = title;
        FactIdentifier factIdentifier = FactIdentifier.create(columnId, String.class.getCanonicalName());
        int columnIndex = getColumnCount() - 1;
        ExpressionIdentifier ei = ExpressionIdentifier.create(columnId, FactMappingType.GIVEN);
        simulationDescriptor.addFactMapping(columnIndex, title, factIdentifier, ei);
    }

    @Override
    public Range setCell(int rowIndex, int columnIndex, Supplier<GridCell<?>> cellSupplier) {
        checkSimulation();
        Range toReturn = super.setCell(rowIndex, columnIndex, cellSupplier);

        Optional<?> optionalValue = getCellValue(getCell(rowIndex, columnIndex));
        if (!optionalValue.isPresent()) {
            return toReturn;
        }

        Object rawValue = optionalValue.get();
        if (rawValue instanceof String) { // Just to avoid unchecked cast - BaseGridData/GridRow should be generified
            final String cellValue = (String) rawValue;

            Scenario scenarioByIndex = simulation.getScenarioByIndex(rowIndex);
            FactMapping factMappingByIndex = simulation.getSimulationDescriptor().getFactMappingByIndex(columnIndex);
            FactIdentifier factIdentifier = factMappingByIndex.getFactIdentifier();
            ExpressionIdentifier expressionIdentifier = factMappingByIndex.getExpressionIdentifier();

            scenarioByIndex.addOrUpdateMappingValue(factIdentifier, expressionIdentifier, cellValue);
        }

        return toReturn;
    }

    public void clear() {
        // Deleting rows
        int to = getRowCount();
        IntStream.range(0, to)
                .map(i -> to - i - 1)
                .forEach(this::deleteRow);
        List<GridColumn<?>> copyList = new ArrayList<>(getColumns());
        copyList.forEach(this::deleteColumn);
        // clear can be called before bind
        if(simulation != null) {
            simulation.clear();
        }
    }

    private void checkSimulation() {
        Objects.requireNonNull(simulation, "Bind a simulation to the ScenarioGridModel to use it");
    }

    public Optional<Simulation> getSimulation() {
        return Optional.ofNullable(simulation);
    }

    // Helper method to avoid potential NPE
    private Optional<?> getCellValue(GridCell<?> gridCell) {
        return Optional.ofNullable(gridCell).map(GridCell::getValue).map(GridCellValue::getValue);
    }
}