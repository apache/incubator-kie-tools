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
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.event.shared.EventBus;
import org.drools.workbench.screens.scenariosimulation.client.metadata.ScenarioHeaderMetaData;
import org.drools.workbench.screens.scenariosimulation.client.values.ScenarioGridCellValue;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridCell;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridColumn;
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
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridRow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class ScenarioGridModelTest {

    private ScenarioGridModel scenarioGridModel;

    @Mock
    private ScenarioGridColumn mockScenarioGridColumn;

    @Mock
    private ScenarioGridColumn mockScenarioIndexGridColumn;

    @Mock
    private BaseGridRow mockGridRow;

    @Mock
    private List<GridColumn.HeaderMetaData> mockHeaderMetaDataList;

    @Mock
    private ScenarioHeaderMetaData mockHeaderMetaData;

    @Mock
    private ScenarioHeaderMetaData mockIndexHeaderMetaData;

    @Mock
    private ScenarioGridCell mockGridCell;

    @Mock
    private ScenarioGridCellValue mockGridCellValue;

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
    private FactMappingValue mockFactMappingValue;

    private List<GridRow> gridRows = new ArrayList<>();

    private List<GridColumn<?>> gridColumns = new ArrayList<>();

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

        doReturn(mockGridCellValue).when(mockGridCell).getValue();

        when(mockHeaderMetaData.getTitle()).thenReturn(GRID_COLUMN_TITLE);
        when(mockHeaderMetaData.getColumnGroup()).thenReturn(GRID_COLUMN_GROUP);
        when(mockHeaderMetaData.getColumnId()).thenReturn(GRID_COLUMN_ID);
        when(mockHeaderMetaDataList.get(1)).thenReturn(mockHeaderMetaData);

        when(mockIndexHeaderMetaData.getTitle()).thenReturn(FactIdentifier.INDEX.getName());
        when(mockScenarioIndexGridColumn.getInformationHeaderMetaData()).thenReturn(mockIndexHeaderMetaData);

        when(mockScenarioGridColumn.getInformationHeaderMetaData()).thenReturn(mockHeaderMetaData);
        when(mockScenarioGridColumn.getHeaderMetaData()).thenReturn(mockHeaderMetaDataList);
        IntStream.range(0, COLUMN_INDEX + 1).forEach(columnIndex -> {
            gridColumns.add(mockScenarioGridColumn);
            when(mockSimulationDescriptor.getFactMappingByIndex(columnIndex)).thenReturn(mockFactMapping);
        });
        when(mockSimulation.getSimulationDescriptor()).thenReturn(mockSimulationDescriptor);
        when(mockGridCell.getValue()).thenReturn(mockGridCellValue);
        when(mockGridCellValue.getValue()).thenReturn(GRID_CELL_TEXT);

        when(mockScenario.getUnmodifiableFactMappingValues()).thenReturn(mockFactMappingValues);

        IntStream.range(0, ROW_COUNT).forEach(rowIndex -> {
            when(mockSimulation.addScenario(rowIndex)).thenReturn(mockScenario);
            when(mockSimulation.getScenarioByIndex(rowIndex)).thenReturn(mockScenario);
            when(mockSimulation.cloneScenario(rowIndex, rowIndex + 1)).thenReturn(mockScenario);
            gridRows.add(mockGridRow);
        });
        when(mockSimulation.addScenario(ROW_COUNT)).thenReturn(mockScenario);
        when(mockSimulation.getScenarioByIndex(ROW_COUNT)).thenReturn(mockScenario);
        when(mockSimulation.cloneScenario(ROW_COUNT, ROW_COUNT + 1)).thenReturn(mockScenario);

        when(mockScenario.getFactMappingValue(any(), any())).thenReturn(Optional.of(mockFactMappingValue));
        when(mockFactMappingValue.isError()).thenReturn(true);

        gridCellSupplier = () -> mockGridCell;
        scenarioGridModel = spy(new ScenarioGridModel(false) {
            {
                this.simulation = mockSimulation;
                this.eventBus = mockEventBus;
                this.rows = gridRows;
                this.columns = gridColumns;
            }

            @Override
            public void deleteColumn(GridColumn<?> column) {
            }

            @Override
            public GridCell<?> getCell(final int rowIndex,
                                       final int columnIndex) {
                if (rowIndex < 0 || rowIndex > rows.size() - 1) {
                    return null;
                }
                return mockGridCell;
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
    public void appendRow() {
        reset(scenarioGridModel);
        scenarioGridModel.appendRow(mockGridRow);
        verify(scenarioGridModel, atLeast(1)).checkSimulation();
        verify(scenarioGridModel, times(1)).commonAddRow(eq(ROW_COUNT));
    }

    @Test
    public void insertRowGridOnly() {
        reset(scenarioGridModel);
        scenarioGridModel.insertRowGridOnly(ROW_INDEX, mockGridRow, mockScenario);
        verify(scenarioGridModel, atLeast(1)).checkSimulation();
        verify(scenarioGridModel, never()).insertRow(eq(ROW_INDEX), eq(mockGridRow));
        verify(scenarioGridModel, times(1)).updateIndexColumn();
    }

    @Test
    public void insertRow() {
        reset(scenarioGridModel);
        scenarioGridModel.insertRow(ROW_INDEX, mockGridRow);
        verify(scenarioGridModel, atLeast(1)).checkSimulation();
        verify(scenarioGridModel, times(1)).commonAddRow(eq(ROW_INDEX));
        verify(scenarioGridModel, times(1)).updateIndexColumn();
    }

    @Test
    public void deleteRow() {
        reset(scenarioGridModel);
        scenarioGridModel.deleteRow(ROW_INDEX);
        verify(scenarioGridModel, atLeast(1)).checkSimulation();
        verify(mockSimulation, times(1)).removeScenarioByIndex(eq(ROW_INDEX));
        verify(scenarioGridModel, times(1)).updateIndexColumn();
    }

    @Test
    public void duplicateRow() {
        reset(scenarioGridModel);
        scenarioGridModel.duplicateRow(ROW_INDEX, mockGridRow);
        verify(scenarioGridModel, atLeast(1)).checkSimulation();
        verify(mockSimulation, times(1)).cloneScenario(eq(ROW_INDEX), eq(ROW_INDEX + 1));
        verify(scenarioGridModel, times(1)).insertRowGridOnly(eq(ROW_INDEX + 1), eq(mockGridRow), isA(Scenario.class));
        verify(scenarioGridModel, never()).insertRow(eq(ROW_INDEX), eq(mockGridRow));
        verify(scenarioGridModel, times(1)).updateIndexColumn();
    }

    @Test
    public void insertColumnGridOnly() {
        reset(scenarioGridModel);
        scenarioGridModel.insertColumnGridOnly(COLUMN_INDEX, mockScenarioGridColumn);
        verify(scenarioGridModel, times(1)).checkSimulation();
    }

    @Test
    public void insertColumn() {
        reset(scenarioGridModel);
        scenarioGridModel.insertColumn(COLUMN_INDEX, mockScenarioGridColumn);
        verify(scenarioGridModel, times(1)).checkSimulation();
        verify(scenarioGridModel, times(1)).commonAddColumn(eq(COLUMN_INDEX), eq(mockScenarioGridColumn));
    }

    @Test
    public void deleteColumn() {
        scenarioGridModel.deleteColumn(COLUMN_INDEX);
        verify(scenarioGridModel, times(1)).checkSimulation();
        verify(mockSimulation, times(1)).removeFactMappingByIndex(eq(COLUMN_INDEX));
    }

    @Test
    public void updateColumnTypeFalse() {
        reset(scenarioGridModel);
        scenarioGridModel.updateColumnType(COLUMN_INDEX, mockScenarioGridColumn, FULL_PACKAGE, VALUE, VALUE_CLASS_NAME, false);
        verify(scenarioGridModel, times(2)).checkSimulation();
        verify(scenarioGridModel, times(1)).deleteColumn(eq(COLUMN_INDEX));
        verify(scenarioGridModel, times(1)).commonAddColumn(eq(COLUMN_INDEX), eq(mockScenarioGridColumn), isA(FactIdentifier.class), isA(ExpressionIdentifier.class));
        verify(scenarioGridModel, times(1)).selectColumn(eq(COLUMN_INDEX));
    }

    @Test
    public void updateColumnTypeTrue() {
        reset(scenarioGridModel);
        scenarioGridModel.updateColumnType(COLUMN_INDEX, mockScenarioGridColumn, FULL_PACKAGE, VALUE, VALUE_CLASS_NAME, true);
        verify(scenarioGridModel, atLeast(2)).checkSimulation();
        verify(scenarioGridModel, atLeast(ROW_COUNT - 1)).getCell(anyInt(), eq(COLUMN_INDEX));
        verify(scenarioGridModel, times(1)).deleteColumn(eq(COLUMN_INDEX));
        verify(scenarioGridModel, times(1)).commonAddColumn(eq(COLUMN_INDEX), eq(mockScenarioGridColumn), isA(FactIdentifier.class), isA(ExpressionIdentifier.class));
        verify(scenarioGridModel, atLeast(ROW_COUNT - 1)).setCellValue(anyInt(), eq(COLUMN_INDEX), isA(ScenarioGridCellValue.class));
        verify(scenarioGridModel, times(1)).selectColumn(eq(COLUMN_INDEX));
    }

    @Test
    public void setCellGridOnly() {
        scenarioGridModel.setCellGridOnly(ROW_INDEX, COLUMN_INDEX, gridCellSupplier);
        verify(scenarioGridModel, times(1)).checkSimulation();
    }

    @Test
    public void setCell() {
        scenarioGridModel.setCell(ROW_INDEX, COLUMN_INDEX, gridCellSupplier);
        verify(scenarioGridModel, times(1)).setCell(eq(ROW_INDEX), eq(COLUMN_INDEX), eq(gridCellSupplier));
    }

    @Test
    public void commonAddColumn() {
        reset(scenarioGridModel);
        scenarioGridModel.commonAddColumn(COLUMN_INDEX, mockScenarioGridColumn);
        verify(scenarioGridModel, times(0)).checkSimulation();
    }

    @Test
    public void commonAddRow() {
        scenarioGridModel.commonAddRow(ROW_INDEX);
    }

    @Test
    public void updateIndexColumn() {
        reset(scenarioGridModel);
        scenarioGridModel.updateIndexColumn();
        verify(scenarioGridModel, never()).setCellValue(anyInt(), anyInt(), isA(ScenarioGridCellValue.class));
        reset(scenarioGridModel);
        when(scenarioGridModel.getRowCount()).thenReturn(3);
        int indexColumnPosition = 0;
        gridColumns.add(indexColumnPosition, mockScenarioIndexGridColumn);
        scenarioGridModel.updateIndexColumn();
        verify(scenarioGridModel, times(3)).setCellValue(anyInt(), eq(indexColumnPosition), isA(ScenarioGridCellValue.class));
    }

    @Test
    public void refreshErrorsTest() {
        scenarioGridModel.refreshErrors();
        verify(mockGridCell, times(24)).setError(eq(true));

        reset(mockGridCell);
        when(mockFactMappingValue.isError()).thenReturn(false);
        scenarioGridModel.refreshErrors();
        verify(mockGridCell, times(24)).setError(eq(false));
    }

    @Test
    public void refreshErrorsRow() {
        FactMappingValue factMappingValue = mock(FactMappingValue.class);
        when(factMappingValue.isError()).thenReturn(true);

        when(mockScenario.getFactMappingValue(any(), any())).thenReturn(Optional.empty());
        scenarioGridModel.refreshErrorsRow(0);
        verify(mockGridCell, times(6)).setError(false);

        when(mockScenario.getFactMappingValue(any(), any())).thenReturn(Optional.of(factMappingValue));
        scenarioGridModel.refreshErrorsRow(0);
        verify(mockGridCell, times(6)).setError(true);
    }
}