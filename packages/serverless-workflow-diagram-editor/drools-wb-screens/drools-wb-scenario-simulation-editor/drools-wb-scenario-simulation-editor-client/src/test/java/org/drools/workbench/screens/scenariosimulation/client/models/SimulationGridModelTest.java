/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
import java.util.function.Supplier;
import java.util.stream.IntStream;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.drools.scenariosimulation.api.model.ExpressionIdentifier;
import org.drools.scenariosimulation.api.model.FactMapping;
import org.drools.scenariosimulation.api.model.FactMappingValueStatus;
import org.drools.scenariosimulation.api.model.Scenario;
import org.drools.workbench.screens.scenariosimulation.client.AbstractScenarioSimulationTest;
import org.drools.workbench.screens.scenariosimulation.client.enums.GridWidget;
import org.drools.workbench.screens.scenariosimulation.client.metadata.ScenarioHeaderMetaData;
import org.drools.workbench.screens.scenariosimulation.client.values.ScenarioGridCellValue;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridCell;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridColumn;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridRow;

import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.GRID_CELL_TEXT;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.GRID_COLUMN_GROUP;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.GRID_COLUMN_ID;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.GRID_COLUMN_TITLE;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.GRID_ROWS;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.HEADER_META_DATA;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.ROW_COUNT;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.ROW_INDEX;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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
public class SimulationGridModelTest extends AbstractScenarioSimulationTest {

    private ScenarioGridModel scenarioGridModelSpy;

    @Mock
    private ScenarioGridColumn scenarioIndexGridColumnMock;

    @Mock
    private BaseGridRow gridRowMock;

    @Mock
    private ScenarioHeaderMetaData groupHeaderMetaDataMock;

    @Mock
    private ScenarioHeaderMetaData indexHeaderMetaDataMock;

    @Mock
    private ScenarioGridCell gridCellMock;

    @Mock
    private ScenarioGridCellValue gridCellValueMock;

    @Mock
    private Scenario scenarioMock;


    @Before
    public void setup() {
        super.setup();
        HEADER_META_DATA.add(groupHeaderMetaDataMock);
        HEADER_META_DATA.add(informationHeaderMetaDataMock);
        HEADER_META_DATA.add(propertyHeaderMetaDataMock);

        doReturn(gridCellValueMock).when(gridCellMock).getValue();

        when(informationHeaderMetaDataMock.getMetadataType()).thenReturn(ScenarioHeaderMetaData.MetadataType.INSTANCE);
        when(informationHeaderMetaDataMock.getTitle()).thenReturn(GRID_COLUMN_TITLE);
        when(informationHeaderMetaDataMock.getColumnGroup()).thenReturn(GRID_COLUMN_GROUP);
        when(informationHeaderMetaDataMock.getColumnId()).thenReturn(GRID_COLUMN_ID);
        when(indexHeaderMetaDataMock.getTitle()).thenReturn(ExpressionIdentifier.INDEX.getName());
        when(scenarioIndexGridColumnMock.getInformationHeaderMetaData()).thenReturn(indexHeaderMetaDataMock);

        when(gridColumnMock.getHeaderMetaData()).thenReturn(HEADER_META_DATA);

        when(gridCellMock.getValue()).thenReturn(gridCellValueMock);
        when(gridCellValueMock.getValue()).thenReturn(GRID_CELL_TEXT);

        when(scenarioMock.getUnmodifiableFactMappingValues()).thenReturn(factMappingValuesLocal);
        GRID_ROWS.clear();
        IntStream.range(0, ROW_COUNT).forEach(rowIndex -> {
            when(simulationMock.addData(rowIndex)).thenReturn(scenarioMock);
            when(simulationMock.getDataByIndex(rowIndex)).thenReturn(scenarioMock);
            when(simulationMock.cloneData(rowIndex, rowIndex + 1)).thenReturn(scenarioMock);
            GRID_ROWS.add(gridRowMock);
        });
        when(simulationMock.addData(ROW_COUNT)).thenReturn(scenarioMock);
        when(simulationMock.getDataByIndex(ROW_COUNT)).thenReturn(scenarioMock);
        when(simulationMock.cloneData(ROW_COUNT, ROW_COUNT + 1)).thenReturn(scenarioMock);

        when(scenarioMock.getFactMappingValue(any(), any())).thenReturn(Optional.of(factMappingValueMock));
        when(scenarioMock.getFactMappingValue(isA(FactMapping.class))).thenReturn(Optional.of(factMappingValueMock));
        when(factMappingValueMock.getStatus()).thenReturn(FactMappingValueStatus.FAILED_WITH_ERROR);

        scenarioGridModelSpy = spy(new ScenarioGridModel(false) {
            {
                this.abstractScesimModel = simulationMock;
                this.eventBus = eventBusMock;
                this.rows = GRID_ROWS;
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
                return gridCellMock;
            }
        });
    }

    @Test
    public void getGridWidget() {
        assertEquals(GridWidget.SIMULATION, scenarioGridModelSpy.getGridWidget());
    }

    @Test
    public void getInstanceLimits() {
        final GridData.Range retrieved = scenarioGridModelSpy.getInstanceLimits(2);
        assertNotNull(retrieved);
        assertEquals(1, retrieved.getMinRowIndex());
        assertEquals(3, retrieved.getMaxRowIndex());
    }

    @Test
    public void insertRowGridOnly() {
        int setCellInvocations = scenarioMock.getUnmodifiableFactMappingValues().size();
        scenarioGridModelSpy.insertRowGridOnly(ROW_INDEX, gridRowMock, scenarioMock);
        verify(scenarioGridModelSpy, atLeast(1)).checkSimulation();
        verify(scenarioGridModelSpy, never()).insertRow(eq(ROW_INDEX), eq(gridRowMock));
        verify(scenarioGridModelSpy, times(1)).updateIndexColumn();
        verify(scenarioGridModelSpy, times(setCellInvocations)).setCell(anyInt(), anyInt(), isA(Supplier.class));
        reset(scenarioGridModelSpy);
        FactMapping factMappingByIndexMock = mock(FactMapping.class);
        when(factMappingByIndexMock.getClassName()).thenReturn(List.class.getName());
        when(simulationDescriptorMock.getFactMappingByIndex(2)).thenReturn(factMappingByIndexMock);
        scenarioGridModelSpy.insertRowGridOnly(ROW_INDEX, gridRowMock, scenarioMock);
        verify(scenarioGridModelSpy, atLeast(1)).checkSimulation();
        verify(scenarioGridModelSpy, never()).insertRow(eq(ROW_INDEX), eq(gridRowMock));
        verify(scenarioGridModelSpy, times(1)).updateIndexColumn();
        verify(scenarioGridModelSpy, times(setCellInvocations)).setCell(anyInt(), anyInt(), isA(Supplier.class));
    }

    @Test
    public void commonAddRow() {
        scenarioGridModelSpy.commonAddRow(1);
        verify(scenarioGridModelSpy, times(1)).commonAddRow(1, 1);
    }

}