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
import java.util.Optional;
import java.util.stream.IntStream;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridColumn;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridRow;
import org.drools.workbench.screens.scenariosimulation.model.FactMapping;
import org.drools.workbench.screens.scenariosimulation.model.FactMappingValue;
import org.drools.workbench.screens.scenariosimulation.model.Scenario;
import org.drools.workbench.screens.scenariosimulation.model.Simulation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCell;

import static org.drools.workbench.screens.scenariosimulation.client.TestUtils.getCellValue;
import static org.drools.workbench.screens.scenariosimulation.client.TestUtils.getColName;
import static org.drools.workbench.screens.scenariosimulation.client.TestUtils.getSimulation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ScenarioGridModelTest {

    private ScenarioGridModel scenarioGridModel;

    @Mock
    private GridColumn<String> mockGridColumn;

    @Mock
    private List<GridColumn.HeaderMetaData> mockHeaderMetaDataList;

    @Mock
    private GridColumn.HeaderMetaData mockHeaderMetaData;

    @Mock
    private GridColumn<String> mockGridColumnAppend;

    @Mock
    private List<GridColumn.HeaderMetaData> mockHeaderMetaDataListAppend;

    @Mock
    private GridColumn.HeaderMetaData mockHeaderMetaDataAppend;

    @Mock
    private BaseGridCell<String> mockGridCell;

    @Mock
    private GridCellValue<String> mockGridCellValue;

    private Simulation simulation;

    private final String GRID_COLUMN_TITLE = getColName(1);
    private final String GRID_CELL_TEXT = getCellValue(1, 1);

    @Before
    public void setup() {
        int cols = 1;
        int rows = 1;
        scenarioGridModel = new ScenarioGridModel();
        simulation = getSimulation(cols, rows);
        scenarioGridModel.bindContent(simulation);

        when(mockGridColumn.getHeaderMetaData()).thenReturn(mockHeaderMetaDataList);
        when(mockHeaderMetaDataList.get(0)).thenReturn(mockHeaderMetaData);
        when(mockHeaderMetaData.getTitle()).thenReturn(GRID_COLUMN_TITLE);

        when(mockGridCell.getValue()).thenReturn(mockGridCellValue);
        when(mockGridCellValue.getValue()).thenReturn(GRID_CELL_TEXT);

        final ScenarioGridColumn scenarioGridColumn = mock(ScenarioGridColumn.class);
        when(scenarioGridColumn.getIndex()).thenReturn(1);
        scenarioGridModel.appendColumn(mockGridColumn);
        IntStream.range(0, rows).forEach(i -> scenarioGridModel.appendRow(new ScenarioGridRow()));
    }

    @Test
    public void bindContent() {
        assertTrue(scenarioGridModel.getSimulation().isPresent());
    }

    @Test
    public void appendColumn() {

        String colName = getColName(2);

        when(mockGridColumnAppend.getHeaderMetaData()).thenReturn(mockHeaderMetaDataListAppend);
        when(mockHeaderMetaDataListAppend.get(0)).thenReturn(mockHeaderMetaDataAppend);
        when(mockHeaderMetaDataAppend.getTitle()).thenReturn(colName);

        int expectedColumnCount = scenarioGridModel.getColumnCount() + 1;
        scenarioGridModel.appendColumn(mockGridColumnAppend);
        int currentColumnCount = scenarioGridModel.getColumnCount();
        assertEquals(currentColumnCount, expectedColumnCount);
        int insertedColumnIndex = currentColumnCount - 1;
        String insertedString = simulation.getSimulationDescriptor().getFactMappingByIndex(insertedColumnIndex).getExpressionIdentifier().getName();
        assertNotNull(insertedString);
        assertEquals(insertedString, colName);
    }

    @Test
    public void testSetCell() {
        int insertedRowIndex = 0;
        int insertedColumnIndex = 0;

        final GridData.Range r = scenarioGridModel.setCell(insertedRowIndex, insertedColumnIndex, () -> mockGridCell);
        assertEquals(insertedRowIndex, r.getMinRowIndex());
        assertEquals(insertedRowIndex, r.getMaxRowIndex());
        assertEquals(mockGridCell, scenarioGridModel.getCell(insertedRowIndex, insertedColumnIndex));
        Scenario scenarioByIndex = simulation.getScenarioByIndex(insertedRowIndex);
        assertNotNull(scenarioByIndex);

        FactMapping factMappingByIndex = simulation.getSimulationDescriptor().getFactMappingByIndex(insertedColumnIndex);
        Optional<FactMappingValue> factMappingValue = scenarioByIndex
                .getFactMappingValue(factMappingByIndex.getFactIdentifier(), factMappingByIndex.getExpressionIdentifier());
        String insertedString = (String) factMappingValue.get().getRawValue();
        assertNotNull(insertedString);
        assertNotNull(insertedString);
        assertEquals(insertedString, GRID_CELL_TEXT);
    }

    @Test
    public void clear() {
        assertNotEquals(0, scenarioGridModel.getRowCount());
        assertNotEquals(0, scenarioGridModel.getColumnCount());
        scenarioGridModel.clear();
        assertTrue(simulation.getScenarios().isEmpty());
        assertTrue(simulation.getSimulationDescriptor().getFactMappings().isEmpty());
        assertEquals(0, scenarioGridModel.getRowCount());
        assertEquals(0, scenarioGridModel.getColumnCount());
    }
}