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
import java.util.function.Supplier;
import java.util.stream.IntStream;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.drools.scenariosimulation.api.model.ExpressionIdentifier;
import org.drools.scenariosimulation.api.model.FactIdentifier;
import org.drools.scenariosimulation.api.model.FactMapping;
import org.drools.scenariosimulation.api.model.FactMappingValue;
import org.drools.scenariosimulation.api.model.FactMappingValueStatus;
import org.drools.scenariosimulation.api.model.Scenario;
import org.drools.workbench.screens.scenariosimulation.client.AbstractScenarioSimulationTest;
import org.drools.workbench.screens.scenariosimulation.client.events.ReloadTestToolsEvent;
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

import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.CLASS_NAME;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.COLUMN_INDEX;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.COLUMN_NUMBER;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.FULL_CLASS_NAME;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.GRID_CELL_TEXT;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.GRID_COLUMN_GROUP;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.GRID_COLUMN_ID;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.GRID_COLUMN_TITLE;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.GRID_ROWS;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.HEADER_META_DATA;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.MULTIPART_VALUE;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.MULTIPART_VALUE_ELEMENTS;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.ROW_COUNT;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.ROW_INDEX;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.VALUE_CLASS_NAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
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

    private Supplier<GridCell<?>> gridCellSupplier;

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
            when(simulationMock.addScenario(rowIndex)).thenReturn(scenarioMock);
            when(simulationMock.getScenarioByIndex(rowIndex)).thenReturn(scenarioMock);
            when(simulationMock.cloneScenario(rowIndex, rowIndex + 1)).thenReturn(scenarioMock);
            GRID_ROWS.add(gridRowMock);
        });
        when(simulationMock.addScenario(ROW_COUNT)).thenReturn(scenarioMock);
        when(simulationMock.getScenarioByIndex(ROW_COUNT)).thenReturn(scenarioMock);
        when(simulationMock.cloneScenario(ROW_COUNT, ROW_COUNT + 1)).thenReturn(scenarioMock);

        when(scenarioMock.getFactMappingValue(any(), any())).thenReturn(Optional.of(factMappingValueMock));
        when(factMappingValueMock.getStatus()).thenReturn(FactMappingValueStatus.FAILED_WITH_ERROR);

        gridCellSupplier = () -> gridCellMock;
        scenarioGridModel = spy(new ScenarioGridModel(false) {
            {
                this.simulation = simulationMock;
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
        scenarioGridModel.updateColumnProperty(COLUMN_INDEX, gridColumnMock, MULTIPART_VALUE_ELEMENTS, VALUE_CLASS_NAME, false);
        verify(scenarioGridModel, times(2)).checkSimulation();
        verify(scenarioGridModel, times(1)).deleteColumn(eq(COLUMN_INDEX));
        verify(scenarioGridModel, times(1)).commonAddColumn(eq(COLUMN_INDEX), eq(gridColumnMock), isA(ExpressionIdentifier.class));
    }

    @Test
    public void updateColumnTypeTrue() {
        scenarioGridModel.updateColumnProperty(COLUMN_INDEX, gridColumnMock, MULTIPART_VALUE_ELEMENTS, VALUE_CLASS_NAME, true);
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
        scenarioGridModel.updateFactMapping(simulationDescriptorMock, factMappingReference, INDEX, MULTIPART_VALUE, ScenarioHeaderMetaData.MetadataType.INSTANCE);
        verify(informationHeaderMetaDataMock, times(1)).setTitle(eq(MULTIPART_VALUE));
        verify(factMappingToCheck, times(1)).setFactAlias(eq(MULTIPART_VALUE));
        reset(informationHeaderMetaDataMock);
        reset(factMappingToCheck);
        // Should not execute the if in the first if
        when(factMappingToCheck.getFactAlias()).thenReturn(ALIAS_2);
        scenarioGridModel.updateFactMapping(simulationDescriptorMock, factMappingReference, INDEX, MULTIPART_VALUE, ScenarioHeaderMetaData.MetadataType.INSTANCE);
        verify(informationHeaderMetaDataMock, never()).setTitle(eq(MULTIPART_VALUE));
        verify(factMappingToCheck, never()).setFactAlias(eq(MULTIPART_VALUE));
        reset(informationHeaderMetaDataMock);
        reset(factMappingToCheck);
        // Should not execute the if in the first if
        when(factMappingReference.getFactIdentifier()).thenReturn(factIdentifier1);
        when(factMappingToCheck.getFactAlias()).thenReturn(ALIAS_1);
        scenarioGridModel.updateFactMapping(simulationDescriptorMock, factMappingReference, INDEX, MULTIPART_VALUE, ScenarioHeaderMetaData.MetadataType.INSTANCE);
        verify(informationHeaderMetaDataMock, never()).setTitle(eq(MULTIPART_VALUE));
        verify(factMappingToCheck, never()).setFactAlias(eq(MULTIPART_VALUE));
        reset(informationHeaderMetaDataMock);
        reset(factMappingToCheck);
        // Should execute the second if
        when(factMappingToCheck.getFactIdentifier()).thenReturn(factIdentifier1);
        scenarioGridModel.updateFactMapping(simulationDescriptorMock, factMappingReference, INDEX, MULTIPART_VALUE, ScenarioHeaderMetaData.MetadataType.INSTANCE);
        verify(informationHeaderMetaDataMock, times(1)).setTitle(eq(MULTIPART_VALUE));
        verify(factMappingToCheck, times(1)).setFactAlias(eq(MULTIPART_VALUE));
        reset(informationHeaderMetaDataMock);
        reset(factMappingToCheck);
        // Should not execute the second if
        when(factMappingToCheck.getFactIdentifier()).thenReturn(factIdentifier2);
        scenarioGridModel.updateFactMapping(simulationDescriptorMock, factMappingReference, INDEX, MULTIPART_VALUE, ScenarioHeaderMetaData.MetadataType.INSTANCE);
        verify(informationHeaderMetaDataMock, never()).setTitle(eq(MULTIPART_VALUE));
        verify(factMappingToCheck, never()).setFactAlias(eq(MULTIPART_VALUE));
    }

    @Test
    public void updateFactMappingProperty() {
        final int INDEX = 0;
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
        scenarioGridModel.updateFactMapping(simulationDescriptorMock, factMappingReference, INDEX, MULTIPART_VALUE, ScenarioHeaderMetaData.MetadataType.PROPERTY);
        verify(propertyHeaderMetaDataMock, times(1)).setTitle(eq(MULTIPART_VALUE));
        verify(factMappingToCheck, times(1)).setExpressionAlias(eq(MULTIPART_VALUE));
        reset(propertyHeaderMetaDataMock);
        reset(factMappingToCheck);
        // Should not execute
        when(factMappingToCheck.getFactAlias()).thenReturn(ALIAS_2);
        scenarioGridModel.updateFactMapping(simulationDescriptorMock, factMappingReference, INDEX, MULTIPART_VALUE, ScenarioHeaderMetaData.MetadataType.PROPERTY);
        verify(propertyHeaderMetaDataMock, never()).setTitle(eq(MULTIPART_VALUE));
        verify(factMappingToCheck, never()).setExpressionAlias(eq(MULTIPART_VALUE));
        reset(propertyHeaderMetaDataMock);
        reset(factMappingToCheck);
        // Should not execute
        when(factMappingReference.getFactIdentifier()).thenReturn(factIdentifier1);
        when(factMappingToCheck.getFactAlias()).thenReturn(ALIAS_1);
        when(factMappingToCheck.getFullExpression()).thenReturn(ALIAS_2);
        scenarioGridModel.updateFactMapping(simulationDescriptorMock, factMappingReference, INDEX, MULTIPART_VALUE, ScenarioHeaderMetaData.MetadataType.PROPERTY);
        verify(propertyHeaderMetaDataMock, never()).setTitle(eq(MULTIPART_VALUE));
        verify(factMappingToCheck, never()).setExpressionAlias(eq(MULTIPART_VALUE));
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
        when(scenarioGridModel.getRowCount()).thenReturn(ROW_COUNT);
        int indexColumnPosition = 0;
        gridColumns.add(indexColumnPosition, scenarioIndexGridColumnMock);
        when(scenarioGridModel.getColumns()).thenReturn(gridColumns);
        scenarioGridModel.updateIndexColumn();
    }

    @Test
    public void isSameInstanceHeaderDifferent() {
        commonCheckSameInstanceHeader(CLASS_NAME, "TOAST", false);
    }

    @Test
    public void isSameInstanceHeaderEqualWithoutPackage() {
        commonCheckSameInstanceHeader(CLASS_NAME, CLASS_NAME, true);
    }

    @Test
    public void isSameInstanceHeaderEqualWithPackage() {
        commonCheckSameInstanceHeader(FULL_CLASS_NAME, CLASS_NAME, false);
    }

    @Test
    public void refreshErrorsTest() {
        int expectedCalls = scenarioGridModel.getRowCount() * scenarioGridModel.getColumnCount();
        scenarioGridModel.refreshErrors();
        verify(gridCellMock, times(expectedCalls)).setErrorMode(eq(true));

        when(factMappingValueMock.getStatus()).thenReturn(FactMappingValueStatus.SUCCESS);
        scenarioGridModel.refreshErrors();
        verify(gridCellMock, times(expectedCalls)).setErrorMode(eq(false));
    }

    @Test
    public void refreshErrorsRow() {
        int expectedCalls = scenarioGridModel.getColumnCount();
        FactMappingValue factMappingValue = mock(FactMappingValue.class);
        when(factMappingValue.getStatus()).thenReturn(FactMappingValueStatus.FAILED_WITH_ERROR);

        when(scenarioMock.getFactMappingValue(any(), any())).thenReturn(Optional.empty());
        scenarioGridModel.refreshErrorsRow(0);
        verify(gridCellMock, times(expectedCalls)).setErrorMode(false);

        when(scenarioMock.getFactMappingValue(any(), any())).thenReturn(Optional.of(factMappingValue));
        scenarioGridModel.refreshErrorsRow(0);
        verify(gridCellMock, times(expectedCalls)).setErrorMode(true);
    }

    @Test
    public void validateInstanceHeaderUpdate() throws Exception {
        commonValidateInstanceHeaderUpdate(1, false, false, false, false);
        commonValidateInstanceHeaderUpdate(1, false, false, true, true);
        commonValidateInstanceHeaderUpdate(1, false, true, false, false);
        commonValidateInstanceHeaderUpdate(1, false, true, true, true);
        commonValidateInstanceHeaderUpdate(1, true, false, false, false);
        commonValidateInstanceHeaderUpdate(1, true, false, true, false);
        commonValidateInstanceHeaderUpdate(1, true, true, false, false);
        commonValidateInstanceHeaderUpdate(1, true, true, true, false);
    }

    @Test
    public void validatePropertyHeaderUpdate() throws Exception {
        commonValidatePropertyUpdate(1, false, false, false, false);
        commonValidatePropertyUpdate(1, false, false, true, true);
        commonValidatePropertyUpdate(1, false, true, false, false);
        commonValidatePropertyUpdate(1, false, true, true, true);
        commonValidatePropertyUpdate(1, true, false, false, false);
        commonValidatePropertyUpdate(1, true, false, true, false);
        commonValidatePropertyUpdate(1, true, true, false, false);
        commonValidatePropertyUpdate(1, true, true, true, false);
    }

    private void commonCheckSameInstanceHeader(String columnClassName, String value, boolean expected)  {
        FactIdentifier factIdentifierMock = mock(FactIdentifier.class);
        when(factIdentifierMock.getClassNameWithoutPackage()).thenReturn(columnClassName);
        int colIndex = 3;
        when(factMappingMock.getFactIdentifier()).thenReturn(factIdentifierMock);
        when(simulationDescriptorMock.getFactMappingByIndex(colIndex)).thenReturn(factMappingMock);
        try {
            scenarioGridModel.checkSameInstanceHeader(colIndex, value);
            verify(simulationMock, times(1)).getSimulationDescriptor();
            verify(simulationDescriptorMock, times(1)).getFactMappingByIndex(eq(colIndex));
        } catch (Exception e) {
            if (expected) {
                fail("No exception expected, retrieved " + e.getMessage());
            }
        }
    }

    private void commonValidateInstanceHeaderUpdate(int columnIndex, boolean isADataType, boolean isSameInstanceHeader, boolean isUnique, boolean expectedValid) throws Exception {
        if (isSameInstanceHeader) {
            doThrow(new Exception("isSameInstanceHeader")).when(scenarioGridModel).checkSameInstanceHeader(columnIndex, MULTIPART_VALUE_ELEMENTS.get(MULTIPART_VALUE_ELEMENTS.size() - 1));
        } else {
            doNothing().when(scenarioGridModel).checkSameInstanceHeader(columnIndex, MULTIPART_VALUE_ELEMENTS.get(MULTIPART_VALUE_ELEMENTS.size() - 1));
        }
        if (isUnique) {
            doNothing().when(scenarioGridModel).checkValidAndUniqueInstanceHeaderTitle(MULTIPART_VALUE, columnIndex);
        } else {
            doThrow(new Exception("isUnique")).when(scenarioGridModel).checkValidAndUniqueInstanceHeaderTitle(MULTIPART_VALUE, columnIndex);
        }
        try {
            scenarioGridModel.validateInstanceHeaderUpdate(MULTIPART_VALUE, columnIndex, isADataType);
        } catch (Exception e) {
            if (expectedValid) {
                fail("No exception expected, retrieved:  " + e.getMessage());
            }
        }
        reset(eventBusMock);
    }

    private void commonValidatePropertyUpdate(int columnIndex, boolean isPropertyType, boolean isSamePropertyHeader, boolean isUnique, boolean expectedValid) throws Exception {
        if (isSamePropertyHeader) {
            doThrow(new Exception("isSamePropertyHeader")).when(scenarioGridModel).checkSamePropertyHeader(columnIndex, MULTIPART_VALUE_ELEMENTS);
        } else {
            doNothing().when(scenarioGridModel).checkSamePropertyHeader(columnIndex, MULTIPART_VALUE_ELEMENTS);
        }
        if (isUnique) {
            doNothing().when(scenarioGridModel).checkValidAndUniquePropertyHeaderTitle(MULTIPART_VALUE, columnIndex);
        } else {
            doThrow(new Exception("isUnique")).when(scenarioGridModel).checkValidAndUniquePropertyHeaderTitle(MULTIPART_VALUE, columnIndex);
        }
        try {
            scenarioGridModel.validatePropertyHeaderUpdate(MULTIPART_VALUE, columnIndex, isPropertyType);
        } catch (Exception e) {
            if (expectedValid) {
                fail("No exception expected, retrieved:  " + e.getMessage());
            }
        }
        reset(eventBusMock);
    }
}