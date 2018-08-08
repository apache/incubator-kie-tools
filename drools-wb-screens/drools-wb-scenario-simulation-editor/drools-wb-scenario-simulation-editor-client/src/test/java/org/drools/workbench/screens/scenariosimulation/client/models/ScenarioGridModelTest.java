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
import java.util.Map;
import java.util.stream.IntStream;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridColumn;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridRow;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCell;

import static org.drools.workbench.screens.scenariosimulation.client.TestUtils.NUMBER_OF_COLUMNS;
import static org.drools.workbench.screens.scenariosimulation.client.TestUtils.NUMBER_OF_ROWS;
import static org.drools.workbench.screens.scenariosimulation.client.TestUtils.getHeadersMap;
import static org.drools.workbench.screens.scenariosimulation.client.TestUtils.getRowsMap;
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
    private BaseGridCell<String> mockGridCell;

    @Mock
    private GridCellValue<String> mockGridCellValue;

    private Map<Integer, String> headersMap;
    private Map<Integer, Map<Integer, String>> rowsMap;

    private final String GRID_COLUMN_TITLE = "MOCKED GRID COLUMN TITLE";
    private final String GRID_CELL_TEXT = "MOCKED GRID CELL TEXT";

    @Before
    public void setup() {
        scenarioGridModel = new ScenarioGridModel();

        when(mockGridColumn.getHeaderMetaData()).thenReturn(mockHeaderMetaDataList);
        when(mockHeaderMetaDataList.get(0)).thenReturn(mockHeaderMetaData);
        when(mockHeaderMetaData.getTitle()).thenReturn(GRID_COLUMN_TITLE);

        when(mockGridCell.getValue()).thenReturn(mockGridCellValue);
        when(mockGridCellValue.getValue()).thenReturn(GRID_CELL_TEXT);

        IntStream.range(0, NUMBER_OF_ROWS).forEach(i -> scenarioGridModel.appendRow(new ScenarioGridRow()));
        IntStream.range(0, NUMBER_OF_COLUMNS).forEach(i -> {
            final ScenarioGridColumn scenarioGridColumn = mock(ScenarioGridColumn.class);
            when(scenarioGridColumn.getIndex()).thenReturn(i);
            scenarioGridModel.appendColumn(mockGridColumn);
        });
        // headersMap represents the column_id:column_title map
        headersMap = getHeadersMap();
        // rowsMap represents the row_id : (column_id:cell_text) map
        rowsMap = getRowsMap();
        // This is here because different tests need to check the actual modification of the bind maps
        scenarioGridModel.bindContent(headersMap, rowsMap);
    }

    @Test
    public void bindContent() {
        assertTrue(scenarioGridModel.getOptionalHeadersMap().isPresent());
        assertTrue(scenarioGridModel.getOptionalRowsMap().isPresent());
    }

    @Test
    public void appendColumn() {
        int expectedColumnCount = scenarioGridModel.getColumnCount() + 1;
        scenarioGridModel.appendColumn(mockGridColumn);
        int currentColumnCount = scenarioGridModel.getColumnCount();
        assertEquals(currentColumnCount, expectedColumnCount);
        int insertedColumnIndex = currentColumnCount - 1;
        String insertedString = headersMap.get(insertedColumnIndex);
        assertNotNull(insertedString);
        assertEquals(insertedString, GRID_COLUMN_TITLE);
    }

    @Test
    public void testSetCell() {
        int insertedRowIndex = 0;
        int insertedColumnIndex = 1;

        final GridData.Range r = scenarioGridModel.setCell(insertedRowIndex, insertedColumnIndex, () -> mockGridCell);
        assertEquals(insertedRowIndex, r.getMinRowIndex());
        assertEquals(insertedRowIndex, r.getMaxRowIndex());
        assertEquals(mockGridCell, scenarioGridModel.getCell(insertedRowIndex, insertedColumnIndex));
        Map<Integer, String> insertedMap = rowsMap.get(insertedRowIndex);
        assertNotNull(insertedMap);
        String insertedString = insertedMap.get(insertedColumnIndex);
        assertNotNull(insertedString);
        assertNotNull(insertedString);
        assertEquals(insertedString, GRID_CELL_TEXT);
    }

    @Test
    public void clear() {
        assertNotEquals(0, scenarioGridModel.getRowCount());
        assertNotEquals(0, scenarioGridModel.getColumnCount());
        scenarioGridModel.clear();
        assertTrue(headersMap.isEmpty());
        assertTrue(rowsMap.isEmpty());
        assertEquals(0, scenarioGridModel.getRowCount());
        assertEquals(0, scenarioGridModel.getColumnCount());
    }
}