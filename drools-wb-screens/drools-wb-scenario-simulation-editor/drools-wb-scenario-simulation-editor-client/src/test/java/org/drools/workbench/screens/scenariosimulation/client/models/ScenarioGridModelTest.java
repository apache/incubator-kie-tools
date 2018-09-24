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

import java.util.List;
import java.util.function.Supplier;

import com.google.gwt.event.shared.EventBus;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.screens.scenariosimulation.client.metadata.ScenarioHeaderMetaData;
import org.drools.workbench.screens.scenariosimulation.model.ExpressionIdentifier;
import org.drools.workbench.screens.scenariosimulation.model.FactIdentifier;
import org.drools.workbench.screens.scenariosimulation.model.FactMapping;
import org.drools.workbench.screens.scenariosimulation.model.FactMappingValue;
import org.drools.workbench.screens.scenariosimulation.model.Scenario;
import org.drools.workbench.screens.scenariosimulation.model.Simulation;
import org.drools.workbench.screens.scenariosimulation.model.SimulationDescriptor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ScenarioGridModelTest {

    private ScenarioGridModel scenarioGridModel;

    @Mock
    private GridColumn<String> mockGridColumn;

    @Mock
    private GridRow mockGridRow;

    @Mock
    private List<GridColumn.HeaderMetaData> mockHeaderMetaDataList;

    @Mock
    private ScenarioHeaderMetaData mockHeaderMetaData;

    @Mock
    private List<GridColumn<String>> mockGridColumns;

    @Mock
    private GridCell<String> mockGridCell;

    @Mock
    private GridCellValue<String> mockGridCellValue;

    @Mock
    private EventBus mockEventBus;

    @Mock
    private Simulation mockSimulation;

    @Mock
    private SimulationDescriptor mockSimulationDescriptor;

    @Mock
    private Scenario mockScenario;

    @Mock
    private FactMapping mockFactMapping;

    @Mock
    private List<FactMappingValue> mockFactMappingValues;

    @Mock
    private List<GridRow> mockRows;

    @Mock
    private List<GridColumn<?>> mockColumns;

    private Supplier<GridCell<?>> gridCellSupplier;

    private final String GRID_COLUMN_TITLE = "GRID_COLUMN_TITLE";
    private final String GRID_COLUMN_GROUP = "GIVEN";
    private final String GRID_COLUMN_ID = "GRID_COLUMN_ID";
    private final String GRID_CELL_TEXT = "GRID_CELL_TEXT";
    private final String FULL_PACKAGE = "test.scesim";
    private final String VALUE = "VALUE";
    private final String VALUE_CLASS_NAME = String.class.getName();
    private final int ROW_COUNT = 4;
    private final int ROW_INDEX = 3;
    private final int COLUMN_INDEX = 5;

    @Before
    public void setup() {
        when(mockGridColumns.get(COLUMN_INDEX)).thenReturn(mockGridColumn);
        when(mockRows.get(ROW_INDEX)).thenReturn(mockGridRow);
        when(mockSimulationDescriptor.getFactMappingByIndex(COLUMN_INDEX)).thenReturn(mockFactMapping);
        when(mockSimulation.getSimulationDescriptor()).thenReturn(mockSimulationDescriptor);
        when(mockGridColumn.getHeaderMetaData()).thenReturn(mockHeaderMetaDataList);
        when(mockHeaderMetaDataList.get(1)).thenReturn(mockHeaderMetaData);
        when(mockHeaderMetaData.getTitle()).thenReturn(GRID_COLUMN_TITLE);
        when(mockHeaderMetaData.getColumnGroup()).thenReturn(GRID_COLUMN_GROUP);
        when(mockHeaderMetaData.getColumnId()).thenReturn(GRID_COLUMN_ID);

        when(mockGridCell.getValue()).thenReturn(mockGridCellValue);
        when(mockGridCellValue.getValue()).thenReturn(GRID_CELL_TEXT);

        when(mockScenario.getUnmodifiableFactMappingValues()).thenReturn(mockFactMappingValues);
        when(mockSimulation.getScenarioByIndex(ROW_INDEX)).thenReturn(mockScenario);
        when(mockSimulation.cloneScenario(ROW_INDEX, ROW_INDEX + 1)).thenReturn(mockScenario);
        when(mockSimulation.cloneScenario(ROW_INDEX, ROW_INDEX + 1)).thenReturn(mockScenario);
        gridCellSupplier = () -> mockGridCell;
        scenarioGridModel = spy(new ScenarioGridModel() {
            {
                this.simulation = mockSimulation;
                this.eventBus = mockEventBus;
                this.rows = mockRows;
                this.columns = mockColumns;
            }

            @Override
            public int getRowCount() {
                return ROW_COUNT;
            }

            @Override
            public void deleteColumn(GridColumn<?> column) {
                //
            }
        });
    }

    @Test
    public void bindContent() {
        assertTrue(scenarioGridModel.getSimulation().isPresent());
    }

    @Test
    public void setEventBus() {
        scenarioGridModel.setEventBus(mockEventBus);
        assertEquals(mockEventBus, scenarioGridModel.eventBus);
    }

    @Test
    public void appendNewColumn() {
        reset(scenarioGridModel);
        scenarioGridModel.appendNewColumn(mockGridColumn);
        verify(scenarioGridModel, times(1)).commonAddColumn(eq(-1), eq(mockGridColumn));
    }

    @Test
    public void appendNewRow() {
        reset(scenarioGridModel);
        scenarioGridModel.appendNewRow(mockGridRow);
        verify(scenarioGridModel, times(1)).checkSimulation();
        verify(scenarioGridModel, times(1)).commonAddRow(eq(ROW_COUNT - 1));
    }

    @Test
    public void insertRow() {
        reset(scenarioGridModel);
        scenarioGridModel.insertRow(ROW_INDEX, mockGridRow, mockScenario);
        verify(scenarioGridModel, times(1)).insertRow(eq(ROW_INDEX), eq(mockGridRow));
    }

    @Test
    public void insertNewRow() {
        reset(scenarioGridModel);
        scenarioGridModel.insertNewRow(ROW_INDEX, mockGridRow);
        verify(scenarioGridModel, times(1)).checkSimulation();
        verify(scenarioGridModel, times(1)).commonAddRow(eq(ROW_INDEX));
    }

    @Test
    public void deleteNewRow() {
        reset(scenarioGridModel);
        scenarioGridModel.deleteNewRow(ROW_INDEX);
        verify(scenarioGridModel, times(1)).checkSimulation();
        verify(mockSimulation, times(1)).removeScenarioByIndex(eq(ROW_INDEX));
    }

    @Test
    public void duplicateNewRow() {
        reset(scenarioGridModel);
        scenarioGridModel.duplicateNewRow(ROW_INDEX, mockGridRow);
        verify(scenarioGridModel, times(2)).checkSimulation();
        verify(mockSimulation, times(1)).cloneScenario(eq(ROW_INDEX), eq(ROW_INDEX + 1));
        verify(scenarioGridModel, times(1)).insertRow(eq(ROW_INDEX + 1), eq(mockGridRow), isA(Scenario.class));
    }

    @Test
    public void insertColumn() {
        reset(scenarioGridModel);
        scenarioGridModel.insertColumn(COLUMN_INDEX, mockGridColumn);
        verify(scenarioGridModel, times(1)).checkSimulation();
    }

    @Test
    public void insertNewColumn() {
        reset(scenarioGridModel);
        scenarioGridModel.insertNewColumn(COLUMN_INDEX, mockGridColumn);
        verify(scenarioGridModel, times(1)).checkSimulation();
        verify(scenarioGridModel, times(1)).commonAddColumn(eq(COLUMN_INDEX), eq(mockGridColumn));
    }

    @Test
    public void deleteNewColumn() {
        scenarioGridModel.deleteNewColumn(COLUMN_INDEX);
        verify(scenarioGridModel, times(1)).checkSimulation();
        verify(mockSimulation, times(1)).removeFactMappingByIndex(eq(COLUMN_INDEX));
    }

    @Test
    public void updateColumnType() {
        reset(scenarioGridModel);
        scenarioGridModel.updateColumnType(COLUMN_INDEX, mockGridColumn, FULL_PACKAGE, VALUE, VALUE_CLASS_NAME);
        verify(scenarioGridModel, times(2)).checkSimulation();
        verify(scenarioGridModel, times(1)).deleteNewColumn(eq(COLUMN_INDEX));
        verify(scenarioGridModel, times(1)).commonAddColumn(eq(COLUMN_INDEX), eq(mockGridColumn), isA(FactIdentifier.class), isA(ExpressionIdentifier.class));
    }

    @Test
    public void setCell() {
        scenarioGridModel.setCell(ROW_INDEX, COLUMN_INDEX, gridCellSupplier);
        verify(scenarioGridModel, times(1)).checkSimulation();
    }

    @Test
    public void setNewCell() {
        scenarioGridModel.setNewCell(ROW_INDEX, COLUMN_INDEX, gridCellSupplier);
        verify(scenarioGridModel, times(1)).setCell(eq(ROW_INDEX), eq(COLUMN_INDEX), eq(gridCellSupplier));
    }

    @Test
    public void commonAddColumn() {
        reset(scenarioGridModel);
        scenarioGridModel.commonAddColumn(COLUMN_INDEX, mockGridColumn);
        verify(scenarioGridModel, times(0)).checkSimulation();
    }

    @Test
    public void commonAddRow() {
        scenarioGridModel.commonAddRow(ROW_INDEX);
    }
}