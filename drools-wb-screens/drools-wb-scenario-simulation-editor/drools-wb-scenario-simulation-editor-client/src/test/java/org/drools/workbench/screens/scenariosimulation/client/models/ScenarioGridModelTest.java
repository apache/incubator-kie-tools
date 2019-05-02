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
import junit.framework.TestCase;
import org.drools.workbench.screens.scenariosimulation.client.AbstractScenarioSimulationTest;
import org.drools.workbench.screens.scenariosimulation.client.events.ReloadTestToolsEvent;
import org.drools.workbench.screens.scenariosimulation.client.metadata.ScenarioHeaderMetaData;
import org.drools.workbench.screens.scenariosimulation.client.values.ScenarioGridCellValue;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridCell;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridColumn;
import org.drools.workbench.screens.scenariosimulation.model.ExpressionIdentifier;
import org.drools.workbench.screens.scenariosimulation.model.FactIdentifier;
import org.drools.workbench.screens.scenariosimulation.model.FactMapping;
import org.drools.workbench.screens.scenariosimulation.model.FactMappingValue;
import org.drools.workbench.screens.scenariosimulation.model.Scenario;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridRow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyDouble;
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
public class ScenarioGridModelTest extends AbstractScenarioSimulationTest {

    private ScenarioGridModel scenarioGridModel;

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

    private List<GridColumn.HeaderMetaData> headerMetaDataList = new ArrayList<>();

    private List<GridRow> gridRows = new ArrayList<>();

    private Supplier<GridCell<?>> gridCellSupplier;

    private final String GRID_COLUMN_TITLE = "GRID_COLUMN_TITLE";
    private final String GRID_CELL_TEXT = "GRID_CELL_TEXT";
    private final String VALUE = "VALUE";
    private final String VALUE_CLASS_NAME = String.class.getName();
    private final int ROW_COUNT = 4;
    private final int ROW_INDEX = 3;

    @Before
    public void setup() {
        super.setup();
        headerMetaDataList.add(groupHeaderMetaDataMock);
        headerMetaDataList.add(informationHeaderMetaDataMock);
        headerMetaDataList.add(propertyHeaderMetaDataMock);

        doReturn(gridCellValueMock).when(gridCellMock).getValue();

        when(informationHeaderMetaDataMock.getMetadataType()).thenReturn(ScenarioHeaderMetaData.MetadataType.INSTANCE);
        when(informationHeaderMetaDataMock.getTitle()).thenReturn(GRID_COLUMN_TITLE);
        when(informationHeaderMetaDataMock.getColumnGroup()).thenReturn(GRID_COLUMN_GROUP);
        when(informationHeaderMetaDataMock.getColumnId()).thenReturn(GRID_COLUMN_ID);
        when(indexHeaderMetaDataMock.getTitle()).thenReturn(ExpressionIdentifier.INDEX.getName());
        when(scenarioIndexGridColumnMock.getInformationHeaderMetaData()).thenReturn(indexHeaderMetaDataMock);

        when(gridColumnMock.getHeaderMetaData()).thenReturn(headerMetaDataList);

        when(gridCellMock.getValue()).thenReturn(gridCellValueMock);
        when(gridCellValueMock.getValue()).thenReturn(GRID_CELL_TEXT);

        when(scenarioMock.getUnmodifiableFactMappingValues()).thenReturn(factMappingValuesLocal);

        IntStream.range(0, ROW_COUNT).forEach(rowIndex -> {
            when(simulationMock.addScenario(rowIndex)).thenReturn(scenarioMock);
            when(simulationMock.getScenarioByIndex(rowIndex)).thenReturn(scenarioMock);
            when(simulationMock.cloneScenario(rowIndex, rowIndex + 1)).thenReturn(scenarioMock);
            gridRows.add(gridRowMock);
        });
        when(simulationMock.addScenario(ROW_COUNT)).thenReturn(scenarioMock);
        when(simulationMock.getScenarioByIndex(ROW_COUNT)).thenReturn(scenarioMock);
        when(simulationMock.cloneScenario(ROW_COUNT, ROW_COUNT + 1)).thenReturn(scenarioMock);

        when(scenarioMock.getFactMappingValue(any(), any())).thenReturn(Optional.of(factMappingValueMock));
        when(factMappingValueMock.isError()).thenReturn(true);

        gridCellSupplier = () -> gridCellMock;
        scenarioGridModel = spy(new ScenarioGridModel(false) {
            {
                this.simulation = simulationMock;
                this.eventBus = eventBusMock;
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
                return gridCellMock;
            }
        });
    }

    @Test
    public void bindContent() {
        assertTrue(scenarioGridModel.getSimulation().isPresent());
    }

    @Test
    public void setEventBus() {
        scenarioGridModel.setEventBus(eventBusMock);
        assertEquals(eventBusMock, scenarioGridModel.eventBus);
    }

    @Test
    public void appendRow() {
        scenarioGridModel.appendRow(gridRowMock);
        verify(scenarioGridModel, atLeast(1)).checkSimulation();
        verify(scenarioGridModel, times(1)).commonAddRow(eq(ROW_COUNT));
    }

    @Test
    public void insertRowGridOnly() {
        int setCellInvocations = scenarioMock.getUnmodifiableFactMappingValues().size();
        scenarioGridModel.insertRowGridOnly(ROW_INDEX, gridRowMock, scenarioMock);
        verify(scenarioGridModel, atLeast(1)).checkSimulation();
        verify(scenarioGridModel, never()).insertRow(eq(ROW_INDEX), eq(gridRowMock));
        verify(scenarioGridModel, times(1)).updateIndexColumn();
        verify(scenarioGridModel, times(setCellInvocations)).setCell(anyInt(), anyInt(), isA(Supplier.class));
        reset(scenarioGridModel);
        FactMapping factMappingByIndexMock = mock(FactMapping.class);
        when(factMappingByIndexMock.getClassName()).thenReturn(List.class.getName());
        when(simulationDescriptorMock.getFactMappingByIndex(2)).thenReturn(factMappingByIndexMock);
        scenarioGridModel.insertRowGridOnly(ROW_INDEX, gridRowMock, scenarioMock);
        verify(scenarioGridModel, atLeast(1)).checkSimulation();
        verify(scenarioGridModel, never()).insertRow(eq(ROW_INDEX), eq(gridRowMock));
        verify(scenarioGridModel, times(1)).updateIndexColumn();
        verify(scenarioGridModel, times(setCellInvocations)).setCell(anyInt(), anyInt(), isA(Supplier.class));
    }

    @Test
    public void insertRow() {
        scenarioGridModel.insertRow(ROW_INDEX, gridRowMock);
        verify(scenarioGridModel, atLeast(1)).checkSimulation();
        verify(scenarioGridModel, times(1)).commonAddRow(eq(ROW_INDEX));
        verify(scenarioGridModel, times(1)).updateIndexColumn();
    }

    @Test
    public void deleteRow() {
        scenarioGridModel.deleteRow(ROW_INDEX);
        verify(scenarioGridModel, atLeast(1)).checkSimulation();
        verify(simulationMock, times(1)).removeScenarioByIndex(eq(ROW_INDEX));
        verify(scenarioGridModel, times(1)).updateIndexColumn();
    }

    @Test
    public void duplicateRow() {
        scenarioGridModel.duplicateRow(ROW_INDEX, gridRowMock);
        verify(scenarioGridModel, atLeast(1)).checkSimulation();
        verify(simulationMock, times(1)).cloneScenario(eq(ROW_INDEX), eq(ROW_INDEX + 1));
        verify(scenarioGridModel, times(1)).insertRowGridOnly(eq(ROW_INDEX + 1), eq(gridRowMock), isA(Scenario.class));
        verify(scenarioGridModel, never()).insertRow(eq(ROW_INDEX), eq(gridRowMock));
        verify(scenarioGridModel, times(1)).updateIndexColumn();
    }

    @Test
    public void duplicateColumnValues() {
        scenarioGridModel.duplicateColumnValues(COLUMN_INDEX, COLUMN_INDEX - 1);
        verify(scenarioGridModel, times(scenarioGridModel.getRowCount())).getCell(anyInt(), eq(COLUMN_INDEX));
        verify(scenarioGridModel, times(scenarioGridModel.getRowCount())).setCellValue(anyInt(), eq(COLUMN_INDEX - 1), any());
        assertTrue(IntStream.range(0, scenarioGridModel.getRowCount())
                           .allMatch(i -> scenarioGridModel.getCell(i, COLUMN_INDEX).getValue().equals(scenarioGridModel.getCell(i, COLUMN_INDEX).getValue())));
    }

    @Test
    public void insertColumnGridOnly() {
        scenarioGridModel.insertColumnGridOnly(COLUMN_INDEX, gridColumnMock);
        verify(scenarioGridModel, times(1)).checkSimulation();
    }

    @Test
    public void insertColumn() {
        scenarioGridModel.insertColumn(COLUMN_INDEX, gridColumnMock);
        verify(scenarioGridModel, times(1)).checkSimulation();
        verify(scenarioGridModel, times(1)).commonAddColumn(eq(COLUMN_INDEX), eq(gridColumnMock));
    }

    @Test
    public void deleteColumn() {
        scenarioGridModel.deleteColumn(COLUMN_INDEX);
        verify(scenarioGridModel, times(1)).checkSimulation();
        verify(scenarioGridModel, times(1)).deleteColumn(eq(gridColumnMock));
        verify(simulationMock, times(1)).removeFactMappingByIndex(eq(COLUMN_INDEX));
        verify(eventBusMock, times(1)).fireEvent(isA(ReloadTestToolsEvent.class));
    }

    @Test
    public void updateColumnTypeFalse() {
        scenarioGridModel.updateColumnProperty(COLUMN_INDEX, gridColumnMock, VALUE, VALUE_CLASS_NAME, false);
        verify(scenarioGridModel, times(2)).checkSimulation();
        verify(scenarioGridModel, times(1)).deleteColumn(eq(COLUMN_INDEX));
        verify(scenarioGridModel, times(1)).commonAddColumn(eq(COLUMN_INDEX), eq(gridColumnMock), isA(ExpressionIdentifier.class));
    }

    @Test
    public void updateColumnTypeTrue() {
        scenarioGridModel.updateColumnProperty(COLUMN_INDEX, gridColumnMock, VALUE, VALUE_CLASS_NAME, true);
        verify(scenarioGridModel, atLeast(2)).checkSimulation();
        verify(scenarioGridModel, atLeast(ROW_COUNT - 1)).getCell(anyInt(), eq(COLUMN_INDEX));
        verify(scenarioGridModel, times(1)).deleteColumn(eq(COLUMN_INDEX));
        verify(scenarioGridModel, times(1)).commonAddColumn(eq(COLUMN_INDEX), eq(gridColumnMock), isA(ExpressionIdentifier.class));
        verify(scenarioGridModel, atLeast(ROW_COUNT - 1)).setCellValue(anyInt(), eq(COLUMN_INDEX), isA(ScenarioGridCellValue.class));
    }

    @Test
    public void replaceColumnTest() {
        scenarioGridModel.replaceColumn(ROW_INDEX, gridColumnMock);
        verify(gridColumnMock, times(COLUMN_NUMBER)).getWidth();
        verify(gridColumnMock, times(COLUMN_NUMBER)).setWidth(anyDouble());
        verify(scenarioGridModel, times(1)).deleteColumn(eq(ROW_INDEX));
        verify(scenarioGridModel, times(1)).commonAddColumn(eq(ROW_INDEX), eq(gridColumnMock), isA(ExpressionIdentifier.class));
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
    public void getInstancesCount() {
        long count = scenarioGridModel.getInstancesCount(FULL_CLASS_NAME);
        assertEquals(1, count);
    }

    @Test
    public void getInstanceLimits() {
        final GridData.Range retrieved = scenarioGridModel.getInstanceLimits(2);
        assertNotNull(retrieved);
        assertEquals(1, retrieved.getMinRowIndex());
        assertEquals(3, retrieved.getMaxRowIndex());
    }

    @Test
    public void getInstanceScenarioGridColumns() {
        final List<ScenarioGridColumn> retrieved = scenarioGridModel.getInstanceScenarioGridColumns((ScenarioGridColumn) gridColumns.get(3));
        assertNotNull(retrieved);
        assertEquals(4, retrieved.size());
    }

    @Test
    public void updateHeader() {
        String newValue = "NEW_VALUE";
        scenarioGridModel.updateHeader(COLUMN_INDEX, 1, newValue); // This is instance header
        verify(eventBusMock, times(1)).fireEvent(isA(ReloadTestToolsEvent.class));
        reset(eventBusMock);
        scenarioGridModel.updateHeader(COLUMN_INDEX, 2, newValue); // This is property header
        verify(eventBusMock, never()).fireEvent(any());
        reset(eventBusMock);
        // if update with same value, no event should be raised
        String title = scenarioGridModel.getColumns().get(COLUMN_INDEX).getHeaderMetaData().get(1).getTitle();
        scenarioGridModel.updateHeader(COLUMN_INDEX, 1, title);
        verify(eventBusMock, never()).fireEvent(any());
    }

    @Test
    public void updateFactMappingInstance() {
        final int INDEX = 0;
        final String VALUE = "VALUE";
        final String ALIAS_1 = "ALIAS_1";
        final String ALIAS_2 = "ALIAS_2";
        FactMapping factMappingReference = mock(FactMapping.class);
        FactMapping factMappingToCheck = mock(FactMapping.class);
        FactIdentifier factIdentifier1 = mock(FactIdentifier.class);
        FactIdentifier factIdentifier2 = mock(FactIdentifier.class);
        // Should execute the if in the first if
        when(factMappingReference.getFactIdentifier()).thenReturn(FactIdentifier.EMPTY);
        when(factMappingToCheck.getFactIdentifier()).thenReturn(FactIdentifier.EMPTY);
        when(factMappingReference.getFactAlias()).thenReturn(ALIAS_1);
        when(factMappingToCheck.getFactAlias()).thenReturn(ALIAS_1);
        when(simulationDescriptorMock.getFactMappingByIndex(INDEX)).thenReturn(factMappingToCheck);
        scenarioGridModel.updateFactMapping(simulationDescriptorMock, factMappingReference, INDEX, VALUE, ScenarioHeaderMetaData.MetadataType.INSTANCE);
        verify(informationHeaderMetaDataMock, times(1)).setTitle(eq(VALUE));
        verify(factMappingToCheck, times(1)).setFactAlias(eq(VALUE));
        reset(informationHeaderMetaDataMock);
        reset(factMappingToCheck);
        // Should not execute the if in the first if
        when(factMappingToCheck.getFactAlias()).thenReturn(ALIAS_2);
        scenarioGridModel.updateFactMapping(simulationDescriptorMock, factMappingReference, INDEX, VALUE, ScenarioHeaderMetaData.MetadataType.INSTANCE);
        verify(informationHeaderMetaDataMock, never()).setTitle(eq(VALUE));
        verify(factMappingToCheck, never()).setFactAlias(eq(VALUE));
        reset(informationHeaderMetaDataMock);
        reset(factMappingToCheck);
        // Should not execute the if in the first if
        when(factMappingReference.getFactIdentifier()).thenReturn(factIdentifier1);
        when(factMappingToCheck.getFactAlias()).thenReturn(ALIAS_1);
        scenarioGridModel.updateFactMapping(simulationDescriptorMock, factMappingReference, INDEX, VALUE, ScenarioHeaderMetaData.MetadataType.INSTANCE);
        verify(informationHeaderMetaDataMock, never()).setTitle(eq(VALUE));
        verify(factMappingToCheck, never()).setFactAlias(eq(VALUE));
        reset(informationHeaderMetaDataMock);
        reset(factMappingToCheck);
        // Should execute the second if
        when(factMappingToCheck.getFactIdentifier()).thenReturn(factIdentifier1);
        scenarioGridModel.updateFactMapping(simulationDescriptorMock, factMappingReference, INDEX, VALUE, ScenarioHeaderMetaData.MetadataType.INSTANCE);
        verify(informationHeaderMetaDataMock, times(1)).setTitle(eq(VALUE));
        verify(factMappingToCheck, times(1)).setFactAlias(eq(VALUE));
        reset(informationHeaderMetaDataMock);
        reset(factMappingToCheck);
        // Should not execute the second if
        when(factMappingToCheck.getFactIdentifier()).thenReturn(factIdentifier2);
        scenarioGridModel.updateFactMapping(simulationDescriptorMock, factMappingReference, INDEX, VALUE, ScenarioHeaderMetaData.MetadataType.INSTANCE);
        verify(informationHeaderMetaDataMock, never()).setTitle(eq(VALUE));
        verify(factMappingToCheck, never()).setFactAlias(eq(VALUE));
    }

    @Test
    public void updateFactMappingProperty() {
        final int INDEX = 0;
        final String VALUE = "VALUE";
        final String ALIAS_1 = "ALIAS_1";
        final String ALIAS_2 = "ALIAS_2";
        FactMapping factMappingReference = mock(FactMapping.class);
        FactMapping factMappingToCheck = mock(FactMapping.class);
        FactIdentifier factIdentifier1 = mock(FactIdentifier.class);
        // Should execute
        when(factMappingReference.getFactIdentifier()).thenReturn(FactIdentifier.EMPTY);
        when(factMappingToCheck.getFactIdentifier()).thenReturn(FactIdentifier.EMPTY);
        when(factMappingReference.getFactAlias()).thenReturn(ALIAS_1);
        when(factMappingToCheck.getFactAlias()).thenReturn(ALIAS_1);
        when(factMappingReference.getFullExpression()).thenReturn(ALIAS_1);
        when(factMappingToCheck.getFullExpression()).thenReturn(ALIAS_1);
        when(simulationDescriptorMock.getFactMappingByIndex(INDEX)).thenReturn(factMappingToCheck);
        scenarioGridModel.updateFactMapping(simulationDescriptorMock, factMappingReference, INDEX, VALUE, ScenarioHeaderMetaData.MetadataType.PROPERTY);
        verify(propertyHeaderMetaDataMock, times(1)).setTitle(eq(VALUE));
        verify(factMappingToCheck, times(1)).setExpressionAlias(eq(VALUE));
        reset(propertyHeaderMetaDataMock);
        reset(factMappingToCheck);
        // Should not execute
        when(factMappingToCheck.getFactAlias()).thenReturn(ALIAS_2);
        scenarioGridModel.updateFactMapping(simulationDescriptorMock, factMappingReference, INDEX, VALUE, ScenarioHeaderMetaData.MetadataType.PROPERTY);
        verify(propertyHeaderMetaDataMock, never()).setTitle(eq(VALUE));
        verify(factMappingToCheck, never()).setExpressionAlias(eq(VALUE));
        reset(propertyHeaderMetaDataMock);
        reset(factMappingToCheck);
        // Should not execute
        when(factMappingReference.getFactIdentifier()).thenReturn(factIdentifier1);
        when(factMappingToCheck.getFactAlias()).thenReturn(ALIAS_1);
        when(factMappingToCheck.getFullExpression()).thenReturn(ALIAS_2);
        scenarioGridModel.updateFactMapping(simulationDescriptorMock, factMappingReference, INDEX, VALUE, ScenarioHeaderMetaData.MetadataType.PROPERTY);
        verify(propertyHeaderMetaDataMock, never()).setTitle(eq(VALUE));
        verify(factMappingToCheck, never()).setExpressionAlias(eq(VALUE));
    }

    @Test
    public void commonAddColumn() {
        scenarioGridModel.commonAddColumn(COLUMN_INDEX, gridColumnMock);
        verify(scenarioGridModel, times(0)).checkSimulation();
    }

    @Test
    public void commonAddRow() {
        scenarioGridModel.commonAddRow(ROW_INDEX);
    }

    @Test
    public void updateIndexColumn() {
        scenarioGridModel.updateIndexColumn();
        verify(scenarioGridModel, never()).setCellValue(anyInt(), anyInt(), isA(ScenarioGridCellValue.class));
        reset(scenarioGridModel);
        when(scenarioGridModel.getRowCount()).thenReturn(3);
        int indexColumnPosition = 0;
        gridColumns.add(indexColumnPosition, scenarioIndexGridColumnMock);
        when(scenarioGridModel.getColumns()).thenReturn(gridColumns);
        scenarioGridModel.updateIndexColumn();
    }

    @Test
    public void isSameInstanceHeaderDifferent() {
        commonIsSameInstanceHeader("TEST", "TOAST", false);
    }

    @Test
    public void isSameInstanceHeaderEqualWithoutPackage() {
        commonIsSameInstanceHeader("TEST", "TEST", true);
    }

    @Test
    public void isSameInstanceHeaderEqualWithPackage() {
        commonIsSameInstanceHeader("com.something.TEST", "TEST", true);
    }

    @Test
    public void refreshErrorsTest() {
        int expectedCalls = scenarioGridModel.getRowCount() * scenarioGridModel.getColumnCount();
        scenarioGridModel.refreshErrors();
        verify(gridCellMock, times(expectedCalls)).setErrorMode(eq(true));

        reset(gridCellMock);
        when(factMappingValueMock.isError()).thenReturn(false);
        scenarioGridModel.refreshErrors();
        verify(gridCellMock, times(expectedCalls)).setErrorMode(eq(false));
    }

    @Test
    public void refreshErrorsRow() {
        int expectedCalls = scenarioGridModel.getColumnCount();
        FactMappingValue factMappingValue = mock(FactMappingValue.class);
        when(factMappingValue.isError()).thenReturn(true);

        when(scenarioMock.getFactMappingValue(any(), any())).thenReturn(Optional.empty());
        scenarioGridModel.refreshErrorsRow(0);
        verify(gridCellMock, times(expectedCalls)).setErrorMode(false);

        when(scenarioMock.getFactMappingValue(any(), any())).thenReturn(Optional.of(factMappingValue));
        scenarioGridModel.refreshErrorsRow(0);
        verify(gridCellMock, times(expectedCalls)).setErrorMode(true);
    }

    @Test
    public void validateInstanceHeaderUpdate() {
        commonValidateInstanceHeaderUpdate(1, false, false, false, false);
        commonValidateInstanceHeaderUpdate(1, false, false, true, true);
        commonValidateInstanceHeaderUpdate(1, false, true, false, false);
        commonValidateInstanceHeaderUpdate(1, false, true, true, true);
        commonValidateInstanceHeaderUpdate(1, true, false, false, false);
        commonValidateInstanceHeaderUpdate(1, true, false, true, false);
        commonValidateInstanceHeaderUpdate(1, true, true, false, false);
        commonValidateInstanceHeaderUpdate(1, true, true, true, true);
    }

    @Test
    public void validatePropertyHeaderUpdate() {
        commonValidatePropertyUpdate(1, false, false, false, false);
        commonValidatePropertyUpdate(1, false, false, true, true);
        commonValidatePropertyUpdate(1, false, true, false, false);
        commonValidatePropertyUpdate(1, false, true, true, true);
        commonValidatePropertyUpdate(1, true, false, false, false);
        commonValidatePropertyUpdate(1, true, false, true, false);
        commonValidatePropertyUpdate(1, true, true, false, false);
        commonValidatePropertyUpdate(1, true, true, true, true);
    }

    private void commonIsSameInstanceHeader(String columnClassName, String value, boolean expected) {
        FactIdentifier factIdentifierMock = mock(FactIdentifier.class);
        when(factIdentifierMock.getClassName()).thenReturn(columnClassName);
        int colIndex = 3;
        when(factMappingMock.getFactIdentifier()).thenReturn(factIdentifierMock);
        when(simulationDescriptorMock.getFactMappingByIndex(colIndex)).thenReturn(factMappingMock);
        boolean retrieved = scenarioGridModel.isSameInstanceHeader(colIndex, value);
        if (expected) {
            TestCase.assertTrue(retrieved);
        } else {
            assertFalse(retrieved);
        }
        verify(simulationMock, times(1)).getSimulationDescriptor();
        verify(simulationDescriptorMock, times(1)).getFactMappingByIndex(eq(colIndex));
    }

    private void commonValidateInstanceHeaderUpdate(int columnIndex, boolean isADataType, boolean isSameInstanceHeader, boolean isUnique, boolean expectedValid) {
        String value = "VALUE";
        doReturn(isSameInstanceHeader).when(scenarioGridModel).isSameInstanceHeader(columnIndex, value);
        doReturn(isUnique).when(scenarioGridModel).isUniqueInstanceHeaderTitle(value, columnIndex);
        boolean retrieved = scenarioGridModel.validateInstanceHeaderUpdate(value, columnIndex, isADataType);
        assertEquals(retrieved, expectedValid);
        reset(eventBusMock);
    }

    private void commonValidatePropertyUpdate(int columnIndex, boolean isPropertyType, boolean isSamePropertyHeader, boolean isUnique, boolean expectedValid) {
        String value = "VALUE";
        doReturn(isSamePropertyHeader).when(scenarioGridModel).isSamePropertyHeader(columnIndex, value);
        doReturn(isUnique).when(scenarioGridModel).isUniquePropertyHeaderTitle(value, columnIndex);
        boolean retrieved = scenarioGridModel.validatePropertyHeaderUpdate(value, columnIndex, isPropertyType);
        assertEquals(retrieved, expectedValid);
        reset(eventBusMock);
    }
}