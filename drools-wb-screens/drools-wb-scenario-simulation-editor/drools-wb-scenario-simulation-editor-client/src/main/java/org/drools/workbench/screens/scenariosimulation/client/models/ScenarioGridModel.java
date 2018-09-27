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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import com.google.gwt.event.shared.EventBus;
import org.drools.workbench.screens.scenariosimulation.client.events.ScenarioGridReloadEvent;
import org.drools.workbench.screens.scenariosimulation.client.metadata.ScenarioHeaderMetaData;
import org.drools.workbench.screens.scenariosimulation.client.values.ScenarioGridCellValue;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridCell;
import org.drools.workbench.screens.scenariosimulation.model.ExpressionIdentifier;
import org.drools.workbench.screens.scenariosimulation.model.FactIdentifier;
import org.drools.workbench.screens.scenariosimulation.model.FactMapping;
import org.drools.workbench.screens.scenariosimulation.model.FactMappingType;
import org.drools.workbench.screens.scenariosimulation.model.FactMappingValue;
import org.drools.workbench.screens.scenariosimulation.model.Scenario;
import org.drools.workbench.screens.scenariosimulation.model.Simulation;
import org.drools.workbench.screens.scenariosimulation.model.SimulationDescriptor;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;

public class ScenarioGridModel extends BaseGridData {

    Simulation simulation;

    EventBus eventBus;

    AtomicInteger columnCounter = new AtomicInteger(0);

    public ScenarioGridModel() {
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
        columnCounter.set(simulation.getSimulationDescriptor().getUnmodifiableFactMappings().size());
    }

    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public int nextColumnCount() {
        return columnCounter.getAndIncrement();
    }

    /**
     * This method <i>append</i> a new column to the grid <b>and</b> to the underlying model
     */
    public void appendNewColumn(final GridColumn<?> column) {
        checkSimulation();
        commonAddColumn(-1, column);
    }

    /**
     * This method <i>append</i> a new row to the grid <b>and</b> to the underlying model
     * @param row
     */
    public void appendNewRow(GridRow row) {
        checkSimulation();
        super.appendRow(row);
        int rowIndex = getRowCount() - 1;
        commonAddRow(rowIndex);
    }

    /**
     * This method <i>insert</i> a row to the grid and populate it with values taken from given <code>Scenario</code>
     * @param row
     */
    public void insertRow(final int rowIndex,
                          final GridRow row, final Scenario scenario) {
        checkSimulation();
        insertRow(rowIndex, row);
        scenario.getUnmodifiableFactMappingValues().forEach(value -> {
            FactIdentifier factIdentifier = value.getFactIdentifier();
            ExpressionIdentifier expressionIdentifier = value.getExpressionIdentifier();
            if (value.getRawValue() instanceof String) {
                String stringValue = (String) value.getRawValue();
                int columnIndex = simulation.getSimulationDescriptor().getIndexByIdentifier(factIdentifier, expressionIdentifier);
                setCell(rowIndex, columnIndex, () -> new ScenarioGridCell(new ScenarioGridCellValue(stringValue)));
            } else {
                throw new UnsupportedOperationException("Only string is supported at the moment");
            }
        });
    }

    /**
     * This method <i>insert</i> a new row to the grid <b>and</b> to the underlying model
     * @param row
     */
    public void insertNewRow(int rowIndex, GridRow row) {
        checkSimulation();
        super.insertRow(rowIndex, row);
        commonAddRow(rowIndex);
    }

    /**
     * This method <i>delete</i> the row at the given index from both the grid <b>and</b> the underlying model
     * @param rowIndex
     */
    public Range deleteNewRow(int rowIndex) {
        checkSimulation();
        Range toReturn = super.deleteRow(rowIndex);
        simulation.removeScenarioByIndex(rowIndex);
        return toReturn;
    }

    /**
     * This method <i>duplicate</i> the row at the given index from both the grid <b>and</b> the underlying model
     * and insert just below the original one
     * @param rowIndex
     */
    public void duplicateNewRow(int rowIndex, GridRow row) {
        checkSimulation();
        int newRowIndex = rowIndex + 1;
        final Scenario toDuplicate = simulation.cloneScenario(rowIndex, newRowIndex);
        insertRow(newRowIndex, row, toDuplicate);
    }

    /**
     * This method <i>insert</i> a new column to the grid <b>without</b> modify underlying model
     * @param index
     * @param column
     */
    @Override
    public void insertColumn(final int index, final GridColumn<?> column) {
        checkSimulation();
        super.insertColumn(index, column);
    }

    /**
     * This method <i>insert</i> a new column to the grid <b>and</b> to the underlying model
     * @param index
     * @param column
     */
    public void insertNewColumn(final int index, final GridColumn<?> column) {
        checkSimulation();
        commonAddColumn(index, column);
    }

    /**
     * This method <i>delete</i> the column at the given index from both the grid <b>and</b> the underlying model
     * @param columnIndex
     */
    public void deleteNewColumn(int columnIndex) {
        checkSimulation();
        final GridColumn<?> toDelete = getColumns().get(columnIndex);
        deleteColumn(toDelete);
        simulation.removeFactMappingByIndex(columnIndex);
    }

    /**
     * This method update the type mapped inside a give column and updates the underlying model
     * @param columnIndex
     * @param value
     */
    public void updateColumnType(int columnIndex, final GridColumn<?> column, String fullPackage, String value, String lastLevelClassName) {
        checkSimulation();
        deleteNewColumn(columnIndex);
        ScenarioHeaderMetaData scenarioHeaderMetaData = (ScenarioHeaderMetaData) column.getHeaderMetaData().get(1);
        String group = scenarioHeaderMetaData.getColumnGroup();
        String columnId = scenarioHeaderMetaData.getColumnId();

        String[] elements = value.split("\\.");
        if (!fullPackage.endsWith(".")) {
            fullPackage += ".";
        }
        String canonicalClassName = fullPackage + elements[0];
        FactIdentifier factIdentifier = FactIdentifier.create(columnId, canonicalClassName);
        ExpressionIdentifier ei = ExpressionIdentifier.create(columnId, FactMappingType.valueOf(group));
        commonAddColumn(columnIndex, column, factIdentifier, ei);
        final FactMapping factMappingByIndex = simulation.getSimulationDescriptor().getFactMappingByIndex(columnIndex);
        factMappingByIndex.setExpressionAlias(value);
        IntStream.range(1, elements.length)
                .forEach(stepIndex ->
                                 factMappingByIndex.addExpressionElement(elements[stepIndex], lastLevelClassName));
        selectColumn(columnIndex);
    }

    /**
     * This method <i>set</i> a cell value to the grid <b>without</b> modify underlying model
     * @param rowIndex
     * @param columnIndex
     * @param cellSupplier
     */
    @Override
    public Range setCell(int rowIndex, int columnIndex, Supplier<GridCell<?>> cellSupplier) {
        checkSimulation();
        return super.setCell(rowIndex, columnIndex, cellSupplier);
    }

    /**
     * This method <i>set</i> a cell value to the grid <b>and</b> to the underlying model
     * @param rowIndex
     * @param columnIndex
     * @param cellSupplier
     */
    public Range setNewCell(int rowIndex, int columnIndex, Supplier<GridCell<?>> cellSupplier) {
        checkSimulation();
        Range toReturn = setCell(rowIndex, columnIndex, cellSupplier);
        try {
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
            } else {
                throw new IllegalArgumentException("Type not supported " + rawValue.getClass().getCanonicalName());
            }
        } catch (Throwable t) {
            toReturn = super.deleteCell(rowIndex, columnIndex);
            eventBus.fireEvent(new ScenarioGridReloadEvent());
        }
        return toReturn;
    }

    /**
     * Return the first index to the left of the given group, i.e. <b>excluded</b> the left-most index of <b>that</b> group
     * @param groupName
     * @return
     */
    public int getFirstIndexLeftOfGroup(String groupName) {
        // HORRIBLE TRICK BECAUSE gridColumn.getIndex() DOES NOT REFLECT ACTUAL POSITION, BUT ONLY ORDER OF INSERTION
        final Optional<Integer> first = this.getColumns()    // Retrieving the column list
                .stream()  // streaming
                .filter(gridColumn -> gridColumn.getHeaderMetaData().get(1).getColumnGroup().equals(groupName))  // filtering by group name
                .findFirst()
                .map(gridColumn -> {
                    int indexOfColumn = this.getColumns().indexOf(gridColumn);
                    return indexOfColumn > -1 ? indexOfColumn : 0;   // mapping the retrieved column to its index inside the list, or 0
                });
        return first.orElseGet(() -> 0); // returning the retrieved value or, if null, 0
    }

    /**
     * Return the first index to the right of the given group, i.e. <b>excluded</b> the right-most index of <b>that</b> group
     * @param groupName
     * @return
     */
    public int getFirstIndexRightOfGroup(String groupName) {
        // HORRIBLE TRICK BECAUSE gridColumn.getIndex() DOES NOT REFLECT ACTUAL POSITION, BUT ONLY ORDER OF INSERTION
        final Optional<Integer> last = this.getColumns()    // Retrieving the column list
                .stream()  // streaming
                .filter(gridColumn -> gridColumn.getHeaderMetaData().get(1).getColumnGroup().equals(groupName))  // filtering by group name
                .reduce((first, second) -> second)  // reducing to have only the last element
                .map(gridColumn -> {
                    int indexOfColumn = this.getColumns().indexOf(gridColumn);
                    return indexOfColumn > -1 ? indexOfColumn + 1 : getColumnCount();   // mapping the retrieved column to its index inside the list +1, or to the total number of columns
                });
        return last.orElseGet(this::getColumnCount); // returning the retrieved value or, if null, the total number of columns
    }

    /**
     * Returns how many columns are already in place for the given group
     * @param groupName
     * @return
     */
    public long getGroupSize(String groupName) {
        return this.getColumns()
                .stream()
                .filter(gridColumn -> gridColumn.getHeaderMetaData().get(1).getColumnGroup().equals(groupName))
                .count();
    }

    public void updateHeader(int columnIndex, int rowIndex, String value) {
        getColumns().get(columnIndex).getHeaderMetaData().get(rowIndex).setTitle(value);
        simulation.getSimulationDescriptor().getFactMappingByIndex(columnIndex).setExpressionAlias(value);
    }

    public Range setNewCellValue(int rowIndex, int columnIndex, GridCellValue<?> value) {
        return setNewCell(rowIndex, columnIndex, () -> new ScenarioGridCell((GridCellValue<String>) value));
    }

    public Range deleteNewCell(int rowIndex, int columnIndex) {
        FactMapping factMapping = simulation.getSimulationDescriptor().getFactMappingByIndex(columnIndex);
        simulation.getScenarioByIndex(rowIndex)
                .removeFactMappingValueByIdentifiers(factMapping.getFactIdentifier(), factMapping.getExpressionIdentifier());
        return super.deleteCell(rowIndex, columnIndex);
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
        if (simulation != null) {
            simulation.clear();
        }
    }

    /**
     * Select all the cells of the given column
     * @param columnIndex
     */
    public void selectColumn(int columnIndex) {
        if (columnIndex > getColumnCount() - 1) {
            return;
        }
        int rows = getRowCount();
        IntStream.range(0, rows).forEach(rowIndex -> selectCell(rowIndex, columnIndex));
    }

    /**
     * Select all the cells of the given row
     * @param rowIndex
     */
    public void selectRow(int rowIndex) {
        if (rowIndex > getRowCount() - 1) {
            return;
        }
        int columns = getColumnCount();
        IntStream.range(0, columns).forEach(columnIndex -> selectCell(rowIndex, columnIndex));
    }

    public Optional<Simulation> getSimulation() {
        return Optional.ofNullable(simulation);
    }

    /**
     * This method <i>add</i> or <i>insert</i> a new column to the grid <b>and</b> to the underlying model, depending on the index value.
     * If index == -1 -> add, otherwise insert. It automatically creates default <code>FactIdentifier</code>  (for String class) and <code>ExpressionIdentifier</code>
     * @param index
     * @param column
     */
    protected void commonAddColumn(final int index, final GridColumn<?> column) {
        ScenarioHeaderMetaData scenarioHeaderMetaData = (ScenarioHeaderMetaData) column.getHeaderMetaData().get(1);
        String group = scenarioHeaderMetaData.getColumnGroup();
        String columnId = scenarioHeaderMetaData.getColumnId();
        FactIdentifier factIdentifier = FactIdentifier.create(columnId, String.class.getCanonicalName());
        ExpressionIdentifier ei = ExpressionIdentifier.create(columnId, FactMappingType.valueOf(group));
        commonAddColumn(index, column, factIdentifier, ei);
    }

    /**
     * This method <i>add</i> or <i>insert</i> a new column to the grid <b>and</b> to the underlying model, depending on the index value.
     * If index == -1 -> add, otherwise insert.
     * @param index
     * @param column
     * @param factIdentifier
     * @param ei
     */
    protected void commonAddColumn(final int index, final GridColumn<?> column, FactIdentifier factIdentifier, ExpressionIdentifier ei) {
        SimulationDescriptor simulationDescriptor = simulation.getSimulationDescriptor();
        ScenarioHeaderMetaData scenarioHeaderMetaData = (ScenarioHeaderMetaData) column.getHeaderMetaData().get(1);
        String title = scenarioHeaderMetaData.getTitle();
        final int columnIndex = index == -1 ? getColumnCount() : index;
        try {
            simulationDescriptor.addFactMapping(columnIndex, title, factIdentifier, ei);
            if (index == -1) {  // This is actually an append
                super.appendColumn(column);
            } else {
                super.insertColumn(index, column);
            }
        } catch (Throwable t) {
            eventBus.fireEvent(new ScenarioGridReloadEvent());
            return;
        }
        final List<Scenario> scenarios = simulation.getUnmodifiableScenarios();
        IntStream.range(0, scenarios.size())
                .forEach(rowIndex -> {
                    String value = FactMappingValue.getPlaceHolder(rowIndex, columnIndex);
                    setNewCell(rowIndex, columnIndex, () -> new ScenarioGridCell(new ScenarioGridCellValue(value)));
                });
    }

    protected void commonAddRow(int rowIndex) {
        Scenario scenario = simulation.addScenario(rowIndex);
        final SimulationDescriptor simulationDescriptor = simulation.getSimulationDescriptor();
        IntStream.range(0, getColumnCount()).forEach(columnIndex -> {
            final FactMapping factMappingByIndex = simulationDescriptor.getFactMappingByIndex(columnIndex);
            String value = FactMappingValue.getPlaceHolder(rowIndex, columnIndex);
            scenario.addMappingValue(factMappingByIndex.getFactIdentifier(), factMappingByIndex.getExpressionIdentifier(), value);
            setNewCell(rowIndex, columnIndex, () -> new ScenarioGridCell(new ScenarioGridCellValue(value)));
        });
    }

    void checkSimulation() {
        Objects.requireNonNull(simulation, "Bind a simulation to the ScenarioGridModel to use it");
    }

    // Helper method to avoid potential NPE
    private Optional<?> getCellValue(GridCell<?> gridCell) {
        if (gridCell == null || gridCell.getValue() == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(gridCell.getValue().getValue());
    }
}