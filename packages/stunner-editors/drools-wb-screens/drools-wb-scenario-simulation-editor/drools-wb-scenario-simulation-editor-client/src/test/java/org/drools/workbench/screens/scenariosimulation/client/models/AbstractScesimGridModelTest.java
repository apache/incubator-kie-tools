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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.drools.scenariosimulation.api.model.AbstractScesimData;
import org.drools.scenariosimulation.api.model.ExpressionElement;
import org.drools.scenariosimulation.api.model.ExpressionIdentifier;
import org.drools.scenariosimulation.api.model.FactIdentifier;
import org.drools.scenariosimulation.api.model.FactMapping;
import org.drools.scenariosimulation.api.model.FactMappingValue;
import org.drools.scenariosimulation.api.model.FactMappingValueStatus;
import org.drools.scenariosimulation.api.model.FactMappingValueType;
import org.drools.scenariosimulation.api.model.Scenario;
import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.drools.workbench.screens.scenariosimulation.client.AbstractScenarioSimulationTest;
import org.drools.workbench.screens.scenariosimulation.client.enums.GridWidget;
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
import org.uberfire.ext.wires.core.grids.client.model.GridRow;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridRow;
import org.uberfire.ext.wires.core.grids.client.widget.dom.single.impl.BaseSingletonDOMElementFactory;

import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.CLASS_NAME;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.COLUMN_INDEX;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.COLUMN_NUMBER;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.FULL_CLASS_NAME;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.GRID_CELL_TEXT;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.GRID_COLUMN_GROUP;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.GRID_COLUMN_ID;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.GRID_COLUMN_TITLE;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.GRID_PROPERTY_TITLE;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.GRID_ROWS;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.HEADER_META_DATA;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.MULTIPART_VALUE;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.MULTIPART_VALUE_ELEMENTS;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.ROW_COUNT;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.ROW_INDEX;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.VALUE_CLASS_NAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
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
public class AbstractScesimGridModelTest extends AbstractScenarioSimulationTest {

    private AbstractScesimGridModel abstractScesimGridModelSpy;

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

        gridCellSupplier = () -> gridCellMock;
        abstractScesimGridModelSpy = spy(new AbstractScesimGridModel(false) {

            {
                this.abstractScesimModel = simulationMock;
                this.eventBus = eventBusMock;
                this.rows = GRID_ROWS;
                this.columns = gridColumns;
                this.scenarioExpressionCellTextAreaSingletonDOMElementFactory = scenarioExpressionCellTextAreaSingletonDOMElementFactorySpy;
                this.collectionEditorSingletonDOMElementFactory = collectionEditorSingletonDOMElementFactoryTest;
                this.scenarioCellTextAreaSingletonDOMElementFactory = scenarioCellTextAreaSingletonDOMElementFactorySpy;
                this.scenarioHeaderTextBoxSingletonDOMElementFactory = scenarioHeaderTextBoxSingletonDOMElementFactorySpy;
            }

            @Override
            public void insertRowGridOnly(int rowIndex, GridRow row, AbstractScesimData abstractScesimData) {

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

            @Override
            public GridWidget getGridWidget() {
                return GridWidget.SIMULATION;
            }

            @Override
            protected void commonAddRow(int rowIndex) {

            }

            @Override
            public Range getInstanceLimits(int columnIndex) {
                return new Range(0, 0);
            }
        });
    }

    @Test
    public void bindContent() {
        assertTrue(abstractScesimGridModelSpy.getAbstractScesimModel().isPresent());
    }

    @Test
    public void setEventBus() {
        abstractScesimGridModelSpy.setEventBus(eventBusMock);
        assertEquals(eventBusMock, abstractScesimGridModelSpy.eventBus);
    }

    @Test
    public void getValidPlaceholders() {
        doReturn(false).when(abstractScesimGridModelSpy).isSameInstanceType(eq(FactMapping.getInstancePlaceHolder(0)));
        doReturn(false).when(abstractScesimGridModelSpy).isSameInstanceType(eq(FactMapping.getInstancePlaceHolder(1)));
        doReturn(true).when(abstractScesimGridModelSpy).isSameInstanceType(eq(FactMapping.getInstancePlaceHolder(2)));
        doReturn(false).when(abstractScesimGridModelSpy).isNewPropertyName(eq(FactMapping.getPropertyPlaceHolder(0)));
        doReturn(false).when(abstractScesimGridModelSpy).isNewPropertyName(eq(FactMapping.getPropertyPlaceHolder(1)));
        doReturn(false).when(abstractScesimGridModelSpy).isNewPropertyName(eq(FactMapping.getPropertyPlaceHolder(2)));
        final Map.Entry<String, String> retrieved = abstractScesimGridModelSpy.getValidPlaceholders();
        assertNotNull(retrieved);
        assertEquals(FactMapping.getInstancePlaceHolder(3), retrieved.getKey());
        assertEquals(FactMapping.getPropertyPlaceHolder(3), retrieved.getValue());
    }

    @Test
    public void appendRow() {
        abstractScesimGridModelSpy.appendRow(gridRowMock);
        verify(abstractScesimGridModelSpy, atLeast(1)).checkSimulation();
        verify(abstractScesimGridModelSpy, times(1)).commonAddRow(eq(ROW_COUNT));
    }

    @Test
    public void insertRow() {
        abstractScesimGridModelSpy.insertRow(ROW_INDEX, gridRowMock);
        verify(abstractScesimGridModelSpy, atLeast(1)).checkSimulation();
        verify(abstractScesimGridModelSpy, times(1)).commonAddRow(eq(ROW_INDEX));
    }

    @Test
    public void deleteRow() {
        abstractScesimGridModelSpy.deleteRow(ROW_INDEX);
        verify(abstractScesimGridModelSpy, atLeast(1)).checkSimulation();
        verify(simulationMock, times(1)).removeDataByIndex(eq(ROW_INDEX));
        verify(abstractScesimGridModelSpy, times(1)).updateIndexColumn();
    }

    @Test
    public void duplicateRow() {
        abstractScesimGridModelSpy.duplicateRow(ROW_INDEX, gridRowMock);
        verify(abstractScesimGridModelSpy, atLeast(1)).checkSimulation();
        verify(simulationMock, times(1)).cloneData(eq(ROW_INDEX), eq(ROW_INDEX + 1));
        verify(abstractScesimGridModelSpy, times(1)).insertRowGridOnly(eq(ROW_INDEX + 1), eq(gridRowMock), isA(Scenario.class));
        verify(abstractScesimGridModelSpy, never()).insertRow(eq(ROW_INDEX), eq(gridRowMock));
    }

    @Test
    public void duplicateColumnValues() {
        abstractScesimGridModelSpy.duplicateColumnValues(COLUMN_INDEX, COLUMN_INDEX - 1);
        verify(abstractScesimGridModelSpy, times(abstractScesimGridModelSpy.getRowCount())).getCell(anyInt(), eq(COLUMN_INDEX));
        verify(abstractScesimGridModelSpy, times(abstractScesimGridModelSpy.getRowCount())).setCellValue(anyInt(), eq(COLUMN_INDEX - 1), any());
        assertTrue(IntStream.range(0, abstractScesimGridModelSpy.getRowCount())
                           .allMatch(i -> abstractScesimGridModelSpy.getCell(i, COLUMN_INDEX).getValue().equals(abstractScesimGridModelSpy.getCell(i, COLUMN_INDEX).getValue())));
    }

    @Test
    public void insertColumnGridOnly() {
        abstractScesimGridModelSpy.insertColumnGridOnly(COLUMN_INDEX, gridColumnMock);
        verify(abstractScesimGridModelSpy, times(1)).checkSimulation();
    }

    @Test
    public void insertColumn() {
        abstractScesimGridModelSpy.insertColumn(COLUMN_INDEX, gridColumnMock);
        verify(abstractScesimGridModelSpy, times(1)).checkSimulation();
        verify(abstractScesimGridModelSpy, times(1)).commonAddColumn(eq(COLUMN_INDEX), eq(gridColumnMock));
    }

    @Test
    public void deleteColumn() {
        abstractScesimGridModelSpy.deleteColumn(COLUMN_INDEX);
        verify(abstractScesimGridModelSpy, times(1)).checkSimulation();
        verify(abstractScesimGridModelSpy, times(1)).deleteColumn(eq(gridColumnMock));
        verify(simulationMock, times(1)).removeFactMappingByIndex(eq(COLUMN_INDEX));
    }

    @Test
    public void updateColumnTypeFalse() {
        abstractScesimGridModelSpy.updateColumnProperty(COLUMN_INDEX, gridColumnMock, MULTIPART_VALUE_ELEMENTS, VALUE_CLASS_NAME, false, FactMappingValueType.NOT_EXPRESSION, ScenarioSimulationModel.Type.RULE);
        verify(abstractScesimGridModelSpy, times(2)).checkSimulation();
        verify(factMappingMock, times(1)).setFactMappingValueType(eq(FactMappingValueType.NOT_EXPRESSION));
        verify(abstractScesimGridModelSpy, times(1)).deleteColumn(eq(COLUMN_INDEX));
        verify(abstractScesimGridModelSpy, times(1)).commonAddColumn(eq(COLUMN_INDEX), eq(gridColumnMock), isA(ExpressionIdentifier.class));
        verify(abstractScesimGridModelSpy, never()).setCellValue(anyInt(), anyInt(), any());
    }

    @Test
    public void updateColumnTypeTrue() {
        abstractScesimGridModelSpy.updateColumnProperty(COLUMN_INDEX, gridColumnMock, MULTIPART_VALUE_ELEMENTS, VALUE_CLASS_NAME, true, FactMappingValueType.NOT_EXPRESSION, ScenarioSimulationModel.Type.RULE);
        verify(abstractScesimGridModelSpy, atLeast(2)).checkSimulation();
        verify(abstractScesimGridModelSpy, atLeast(ROW_COUNT - 1)).getCell(anyInt(), eq(COLUMN_INDEX));
        verify(factMappingMock, times(1)).setFactMappingValueType(eq(FactMappingValueType.NOT_EXPRESSION));
        verify(abstractScesimGridModelSpy, times(1)).deleteColumn(eq(COLUMN_INDEX));
        verify(abstractScesimGridModelSpy, times(1)).commonAddColumn(eq(COLUMN_INDEX), eq(gridColumnMock), isA(ExpressionIdentifier.class));
        verify(abstractScesimGridModelSpy, atLeast(ROW_COUNT - 1)).setCellValue(anyInt(), eq(COLUMN_INDEX), isA(ScenarioGridCellValue.class));
    }

    @Test
    public void replaceColumnTest() {
        abstractScesimGridModelSpy.replaceColumn(ROW_INDEX, gridColumnMock);
        verify(gridColumnMock, times(COLUMN_NUMBER)).getWidth();
        verify(gridColumnMock, times(COLUMN_NUMBER)).setWidth(anyDouble());
        verify(abstractScesimGridModelSpy, times(1)).deleteColumn(eq(ROW_INDEX));
        verify(abstractScesimGridModelSpy, times(1)).commonAddColumn(eq(ROW_INDEX), eq(gridColumnMock), isA(ExpressionIdentifier.class));
    }

    @Test
    public void setCellGridOnly() {
        abstractScesimGridModelSpy.setCellGridOnly(ROW_INDEX, COLUMN_INDEX, gridCellSupplier);
        verify(abstractScesimGridModelSpy, times(1)).checkSimulation();
    }

    @Test
    public void setCell() {
        abstractScesimGridModelSpy.setCell(ROW_INDEX, COLUMN_INDEX, gridCellSupplier);
        verify(abstractScesimGridModelSpy, times(1)).setCell(eq(ROW_INDEX), eq(COLUMN_INDEX), eq(gridCellSupplier));
    }

    @Test
    public void setSelectedColumn() {
        abstractScesimGridModelSpy.selectColumn(COLUMN_INDEX);
        assertEquals(gridColumnMock, abstractScesimGridModelSpy.getSelectedColumn());
    }

    @Test(expected = IllegalArgumentException.class)
    public void setSelectedColumn_NegativeIndex() {
        int columnIndex = -1;
        abstractScesimGridModelSpy.selectColumn(columnIndex);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setSelectedColumn_OverflowIndex() {
        int columnIndex = 7;
        abstractScesimGridModelSpy.selectColumn(columnIndex);
    }

    @Test
    public void getInstancesCount() {
        long count = abstractScesimGridModelSpy.getInstancesCount(FULL_CLASS_NAME);
        assertEquals(1, count);
    }

    @Test
    public void getInstanceScenarioGridColumns() {
        final List<ScenarioGridColumn> retrieved = abstractScesimGridModelSpy.getInstanceScenarioGridColumns((ScenarioGridColumn) gridColumns.get(3));
        assertNotNull(retrieved);
        assertEquals(1, retrieved.size());
    }

    @Test
    public void updateHeader() {
        String newValue = "NEW_VALUE";
        abstractScesimGridModelSpy.updateHeader(COLUMN_INDEX, 1, newValue); // This is instance header
        verify(eventBusMock, times(1)).fireEvent(isA(ReloadTestToolsEvent.class));
        reset(eventBusMock);
        abstractScesimGridModelSpy.updateHeader(COLUMN_INDEX, 2, newValue); // This is property header
        verify(eventBusMock, never()).fireEvent(any());
        reset(eventBusMock);
        // if update with same value, no event should be raised
        String title = abstractScesimGridModelSpy.getColumns().get(COLUMN_INDEX).getHeaderMetaData().get(1).getTitle();
        abstractScesimGridModelSpy.updateHeader(COLUMN_INDEX, 1, title);
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
        abstractScesimGridModelSpy.updateFactMapping(simulationDescriptorMock, factMappingReference, INDEX, MULTIPART_VALUE, ScenarioHeaderMetaData.MetadataType.INSTANCE);
        verify(informationHeaderMetaDataMock, times(1)).setTitle(eq(MULTIPART_VALUE));
        verify(factMappingToCheck, times(1)).setFactAlias(eq(MULTIPART_VALUE));
        reset(informationHeaderMetaDataMock);
        reset(factMappingToCheck);
        // Should not execute the if in the first if
        when(factMappingToCheck.getFactAlias()).thenReturn(ALIAS_2);
        abstractScesimGridModelSpy.updateFactMapping(simulationDescriptorMock, factMappingReference, INDEX, MULTIPART_VALUE, ScenarioHeaderMetaData.MetadataType.INSTANCE);
        verify(informationHeaderMetaDataMock, never()).setTitle(eq(MULTIPART_VALUE));
        verify(factMappingToCheck, never()).setFactAlias(eq(MULTIPART_VALUE));
        reset(informationHeaderMetaDataMock);
        reset(factMappingToCheck);
        // Should not execute the if in the first if
        when(factMappingReference.getFactIdentifier()).thenReturn(factIdentifier1);
        when(factMappingToCheck.getFactAlias()).thenReturn(ALIAS_1);
        abstractScesimGridModelSpy.updateFactMapping(simulationDescriptorMock, factMappingReference, INDEX, MULTIPART_VALUE, ScenarioHeaderMetaData.MetadataType.INSTANCE);
        verify(informationHeaderMetaDataMock, never()).setTitle(eq(MULTIPART_VALUE));
        verify(factMappingToCheck, never()).setFactAlias(eq(MULTIPART_VALUE));
        reset(informationHeaderMetaDataMock);
        reset(factMappingToCheck);
        // Should execute the second if
        when(factMappingToCheck.getFactIdentifier()).thenReturn(factIdentifier1);
        abstractScesimGridModelSpy.updateFactMapping(simulationDescriptorMock, factMappingReference, INDEX, MULTIPART_VALUE, ScenarioHeaderMetaData.MetadataType.INSTANCE);
        verify(informationHeaderMetaDataMock, times(1)).setTitle(eq(MULTIPART_VALUE));
        verify(factMappingToCheck, times(1)).setFactAlias(eq(MULTIPART_VALUE));
        reset(informationHeaderMetaDataMock);
        reset(factMappingToCheck);
        // Should not execute the second if
        when(factMappingToCheck.getFactIdentifier()).thenReturn(factIdentifier2);
        abstractScesimGridModelSpy.updateFactMapping(simulationDescriptorMock, factMappingReference, INDEX, MULTIPART_VALUE, ScenarioHeaderMetaData.MetadataType.INSTANCE);
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
        abstractScesimGridModelSpy.updateFactMapping(simulationDescriptorMock, factMappingReference, INDEX, MULTIPART_VALUE, ScenarioHeaderMetaData.MetadataType.PROPERTY);
        verify(propertyHeaderMetaDataMock, times(1)).setTitle(eq(MULTIPART_VALUE));
        verify(factMappingToCheck, times(1)).setExpressionAlias(eq(MULTIPART_VALUE));
        reset(propertyHeaderMetaDataMock);
        reset(factMappingToCheck);
        // Should not execute
        when(factMappingToCheck.getFactAlias()).thenReturn(ALIAS_2);
        abstractScesimGridModelSpy.updateFactMapping(simulationDescriptorMock, factMappingReference, INDEX, MULTIPART_VALUE, ScenarioHeaderMetaData.MetadataType.PROPERTY);
        verify(propertyHeaderMetaDataMock, never()).setTitle(eq(MULTIPART_VALUE));
        verify(factMappingToCheck, never()).setExpressionAlias(eq(MULTIPART_VALUE));
        reset(propertyHeaderMetaDataMock);
        reset(factMappingToCheck);
        // Should not execute
        when(factMappingReference.getFactIdentifier()).thenReturn(factIdentifier1);
        when(factMappingToCheck.getFactAlias()).thenReturn(ALIAS_1);
        when(factMappingToCheck.getFullExpression()).thenReturn(ALIAS_2);
        abstractScesimGridModelSpy.updateFactMapping(simulationDescriptorMock, factMappingReference, INDEX, MULTIPART_VALUE, ScenarioHeaderMetaData.MetadataType.PROPERTY);
        verify(propertyHeaderMetaDataMock, never()).setTitle(eq(MULTIPART_VALUE));
        verify(factMappingToCheck, never()).setExpressionAlias(eq(MULTIPART_VALUE));
    }

    @Test
    public void commonAddColumn() {
        abstractScesimGridModelSpy.commonAddColumn(COLUMN_INDEX, gridColumnMock);
        verify(abstractScesimGridModelSpy, times(0)).checkSimulation();
    }

    @Test
    public void updateIndexColumn() {
        abstractScesimGridModelSpy.updateIndexColumn();
        verify(abstractScesimGridModelSpy, never()).setCellValue(anyInt(), anyInt(), isA(ScenarioGridCellValue.class));
        reset(abstractScesimGridModelSpy);
        when(abstractScesimGridModelSpy.getRowCount()).thenReturn(ROW_COUNT);
        int indexColumnPosition = 0;
        gridColumns.add(indexColumnPosition, scenarioIndexGridColumnMock);
        when(abstractScesimGridModelSpy.getColumns()).thenReturn(gridColumns);
        abstractScesimGridModelSpy.updateIndexColumn();
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
    public void isSameInstanceHeader_Different() {
        isSameInstanceType(CLASS_NAME, "TOAST", false);
    }

    @Test
    public void isSameInstanceHeader_Equal() {
        isSameInstanceType(CLASS_NAME, CLASS_NAME, true);
    }

    @Test
    public void refreshErrorsTest() {
        int expectedCalls = abstractScesimGridModelSpy.getRowCount() * abstractScesimGridModelSpy.getColumnCount();
        abstractScesimGridModelSpy.refreshErrors();
        verify(gridCellMock, times(expectedCalls)).setErrorMode(eq(true));

        when(factMappingValueMock.getStatus()).thenReturn(FactMappingValueStatus.SUCCESS);
        abstractScesimGridModelSpy.refreshErrors();
        verify(gridCellMock, times(expectedCalls)).setErrorMode(eq(false));
    }

    @Test
    public void refreshErrorsRow() {
        int expectedCalls = abstractScesimGridModelSpy.getColumnCount();
        FactMappingValue factMappingValue = mock(FactMappingValue.class);
        when(factMappingValue.getStatus()).thenReturn(FactMappingValueStatus.FAILED_WITH_ERROR);

        when(scenarioMock.getFactMappingValue(any(), any())).thenReturn(Optional.empty());
        abstractScesimGridModelSpy.refreshErrorsRow(0);
        verify(gridCellMock, times(expectedCalls)).setErrorMode(false);

        when(scenarioMock.getFactMappingValue(any(), any())).thenReturn(Optional.of(factMappingValue));
        abstractScesimGridModelSpy.refreshErrorsRow(0);
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

    private void isSameInstanceType(String columnClassName, String value, boolean expectedResult) {
        FactIdentifier factIdentifierMock = mock(FactIdentifier.class);
        when(factIdentifierMock.getClassNameWithoutPackage()).thenReturn(columnClassName);
        when(factMappingMock.getFactIdentifier()).thenReturn(factIdentifierMock);
        when(simulationDescriptorMock.getFactMappingByIndex(COLUMN_INDEX)).thenReturn(factMappingMock);
        boolean result = abstractScesimGridModelSpy.isSameInstanceType(COLUMN_INDEX, value);
        verify(simulationMock, times(1)).getScesimModelDescriptor();
        verify(simulationDescriptorMock, times(1)).getFactMappingByIndex(eq(COLUMN_INDEX));
        assertEquals(result, expectedResult);
    }

    private void commonCheckSameInstanceHeader(String columnClassName, String value, boolean expected)  {
        FactIdentifier factIdentifierMock = mock(FactIdentifier.class);
        when(factIdentifierMock.getClassNameWithoutPackage()).thenReturn(columnClassName);
        when(factMappingMock.getFactIdentifier()).thenReturn(factIdentifierMock);
        when(simulationDescriptorMock.getFactMappingByIndex(COLUMN_INDEX)).thenReturn(factMappingMock);
        try {
            abstractScesimGridModelSpy.checkSameInstanceHeader(COLUMN_INDEX, value);
            verify(abstractScesimGridModelSpy, times(1)).isSameInstanceType(eq(COLUMN_INDEX), eq(value));
        } catch (Exception e) {
            if (expected) {
                fail("No exception expected, retrieved " + e.getMessage());
            }
        }
    }

    private void commonValidateInstanceHeaderUpdate(int columnIndex, boolean isADataType, boolean isSameInstanceHeader, boolean isUnique, boolean expectedValid) throws Exception {
        if (isSameInstanceHeader) {
            doThrow(new IllegalArgumentException("isSameInstanceHeader")).when(abstractScesimGridModelSpy).checkSameInstanceHeader(columnIndex, MULTIPART_VALUE_ELEMENTS.get(MULTIPART_VALUE_ELEMENTS.size() - 1));
        } else {
            doNothing().when(abstractScesimGridModelSpy).checkSameInstanceHeader(columnIndex, MULTIPART_VALUE_ELEMENTS.get(MULTIPART_VALUE_ELEMENTS.size() - 1));
        }
        if (isUnique) {
            doNothing().when(abstractScesimGridModelSpy).checkValidAndUniqueInstanceHeaderTitle(MULTIPART_VALUE, columnIndex);
        } else {
            doThrow(new IllegalArgumentException("isUnique")).when(abstractScesimGridModelSpy).checkValidAndUniqueInstanceHeaderTitle(MULTIPART_VALUE, columnIndex);
        }
        try {
            abstractScesimGridModelSpy.validateInstanceHeaderUpdate(MULTIPART_VALUE, columnIndex, isADataType);
        } catch (Exception e) {
            if (expectedValid) {
                fail("No exception expected, retrieved:  " + e.getMessage());
            }
        }
        reset(eventBusMock);
    }

    private void commonValidatePropertyUpdate(int columnIndex, boolean isPropertyType, boolean isSamePropertyHeader, boolean isUnique, boolean expectedValid) throws Exception {
        if (isSamePropertyHeader) {
            doThrow(new IllegalArgumentException("isSamePropertyHeader")).when(abstractScesimGridModelSpy).checkSamePropertyHeader(columnIndex, MULTIPART_VALUE_ELEMENTS);
        } else {
            doNothing().when(abstractScesimGridModelSpy).checkSamePropertyHeader(columnIndex, MULTIPART_VALUE_ELEMENTS);
        }
        if (isUnique) {
            doNothing().when(abstractScesimGridModelSpy).checkValidAndUniquePropertyHeaderTitle(MULTIPART_VALUE, columnIndex);
        } else {
            doThrow(new IllegalArgumentException("isUnique")).when(abstractScesimGridModelSpy).checkValidAndUniquePropertyHeaderTitle(MULTIPART_VALUE, columnIndex);
        }
        try {
            abstractScesimGridModelSpy.validatePropertyHeaderUpdate(MULTIPART_VALUE, columnIndex, isPropertyType);
        } catch (Exception e) {
            if (expectedValid) {
                fail("No exception expected, retrieved:  " + e.getMessage());
            }
        }
        reset(eventBusMock);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkUniquePropertyHeaderTitle_AssignedLabel() {
        abstractScesimGridModelSpy.checkUniquePropertyHeaderTitle(GRID_PROPERTY_TITLE, COLUMN_INDEX);
    }

    @Test
    public void resetError() {
        abstractScesimGridModelSpy = spy(new ScenarioGridModel(false) {
            {
                this.abstractScesimModel = simulationMock;
                this.eventBus = eventBusMock;
                this.rows = GRID_ROWS;
                this.columns = gridColumns;
            }

            @Override
            public void refreshErrors() {
                //Do nothing
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
        abstractScesimGridModelSpy.resetError(ROW_INDEX, COLUMN_INDEX);
        verify(simulationMock, times(1)).getDataByIndex(eq(ROW_INDEX));
        verify(simulationDescriptorMock, times(1)).getFactMappingByIndex(eq(COLUMN_INDEX));
        verify(scenarioMock, times(1)).getFactMappingValue(eq(factMappingMock));
        verify(factMappingValueMock, times(1)).resetStatus();
        verify(abstractScesimGridModelSpy, times(1)).refreshErrors();
    }

    @Test
    public void synchronizeFactMappingsWidths() {
        abstractScesimGridModelSpy.synchronizeFactMappingsWidths();
        verify(abstractScesimGridModelSpy, times(gridColumns.size())).synchronizeFactMappingWidth(isA(GridColumn.class));
    }

    @Test
    public void synchronizeFactMappingWidth() {
        when(gridColumnMock.isVisible()).thenReturn(true);
        abstractScesimGridModelSpy.synchronizeFactMappingWidth(gridColumnMock);
        verify(factMappingMock, times(1)).setColumnWidth(eq(gridColumnMock.getWidth()));
    }

    @Test
    public void synchronizeFactMappingWidth_NotVisibleColumn() {
        when(gridColumnMock.isVisible()).thenReturn(false);
        abstractScesimGridModelSpy.synchronizeFactMappingWidth(gridColumnMock);
        verify(factMappingMock, never()).setColumnWidth(any());
    }

    @Test
    public void loadFactMappingsWidth_FactMappingWithoutWidth() {
        when(factMappingMock.getColumnWidth()).thenReturn(null);
        gridColumns.clear();
        gridColumns.add(gridColumnMock);
        abstractScesimGridModelSpy.loadFactMappingsWidth();
        verify(gridColumnMock, never()).setWidth(anyDouble());
    }

    @Test
    public void loadFactMappingsWidth_FactMappingWithWidth() {
        when(factMappingMock.getColumnWidth()).thenReturn(54.32);
        gridColumns.clear();
        gridColumns.add(gridColumnMock);
        abstractScesimGridModelSpy.loadFactMappingsWidth();
        verify(factMappingMock, never()).setColumnWidth(anyDouble());
        verify(gridColumnMock, times(1)).setWidth(eq(factMappingMock.getColumnWidth()));
    }

    @Test
    public void getDOMElementFactory_Collection() {
        BaseSingletonDOMElementFactory factory = abstractScesimGridModelSpy.getDOMElementFactory("java.util.List", ScenarioSimulationModel.Type.RULE, FactMappingValueType.NOT_EXPRESSION);
        assertSame(collectionEditorSingletonDOMElementFactoryTest, factory);
        factory = abstractScesimGridModelSpy.getDOMElementFactory("java.util.Map", ScenarioSimulationModel.Type.RULE, FactMappingValueType.NOT_EXPRESSION);
        assertSame(collectionEditorSingletonDOMElementFactoryTest, factory);
        factory = abstractScesimGridModelSpy.getDOMElementFactory("java.util.List", ScenarioSimulationModel.Type.DMN, FactMappingValueType.NOT_EXPRESSION);
        assertSame(collectionEditorSingletonDOMElementFactoryTest, factory);
        factory = abstractScesimGridModelSpy.getDOMElementFactory("java.util.List", ScenarioSimulationModel.Type.DMN, FactMappingValueType.EXPRESSION);
        assertSame(scenarioCellTextAreaSingletonDOMElementFactorySpy, factory);
        factory = abstractScesimGridModelSpy.getDOMElementFactory("java.util.List", ScenarioSimulationModel.Type.RULE, FactMappingValueType.EXPRESSION);
        assertSame(scenarioExpressionCellTextAreaSingletonDOMElementFactorySpy, factory);
    }

    @Test
    public void getDOMElementFactory_Expression() {
        BaseSingletonDOMElementFactory factory = abstractScesimGridModelSpy.getDOMElementFactory("com.Test", ScenarioSimulationModel.Type.DMN, FactMappingValueType.EXPRESSION);
        assertSame(scenarioCellTextAreaSingletonDOMElementFactorySpy, factory);
        factory = abstractScesimGridModelSpy.getDOMElementFactory("com.Test", ScenarioSimulationModel.Type.RULE, FactMappingValueType.EXPRESSION);
        assertSame(scenarioExpressionCellTextAreaSingletonDOMElementFactorySpy, factory);
    }

    @Test
    public void getDOMElementFactory_NotExpressionNotCollection() {
        BaseSingletonDOMElementFactory factory = abstractScesimGridModelSpy.getDOMElementFactory("com.Test", ScenarioSimulationModel.Type.DMN, FactMappingValueType.NOT_EXPRESSION);
        assertSame(scenarioCellTextAreaSingletonDOMElementFactorySpy, factory);
        factory = abstractScesimGridModelSpy.getDOMElementFactory("com.Test", ScenarioSimulationModel.Type.RULE, FactMappingValueType.NOT_EXPRESSION);
        assertSame(scenarioCellTextAreaSingletonDOMElementFactorySpy, factory);
    }

    @Test
    public void destroyAllTextAreaDOMElementFactoryResources() {
        abstractScesimGridModelSpy.destroyAllTextAreaDOMElementFactoryResources();
        verify(scenarioCellTextAreaSingletonDOMElementFactorySpy, times(1)).destroyResources();
        verify(scenarioExpressionCellTextAreaSingletonDOMElementFactorySpy, times(1)).destroyResources();
        verify(scenarioHeaderTextBoxSingletonDOMElementFactorySpy, times(1)).destroyResources();
    }

    @Test
    public void isSameSelectedColumnProperty() {
        List<ExpressionElement> expressionList = Arrays.asList(new ExpressionElement("Fact"), new ExpressionElement("property"));
        when(factMappingMock.getExpressionElements()).thenReturn(expressionList);
        when(factMappingMock.getFactMappingValueType()).thenReturn(FactMappingValueType.NOT_EXPRESSION);
        assertTrue(abstractScesimGridModelSpy.isSameSelectedColumnProperty(1, Arrays.asList("Fact", "property"), FactMappingValueType.NOT_EXPRESSION));
        assertFalse(abstractScesimGridModelSpy.isSameSelectedColumnProperty(1, Arrays.asList("Fact", "property2"), FactMappingValueType.NOT_EXPRESSION));
        assertFalse(abstractScesimGridModelSpy.isSameSelectedColumnProperty(1, Arrays.asList("property2"), FactMappingValueType.NOT_EXPRESSION));
        assertFalse(abstractScesimGridModelSpy.isSameSelectedColumnProperty(1, Arrays.asList("Fact", "property"), FactMappingValueType.EXPRESSION));
        assertFalse(abstractScesimGridModelSpy.isSameSelectedColumnProperty(1, Arrays.asList("Fact", "property2"), FactMappingValueType.EXPRESSION));
        assertFalse(abstractScesimGridModelSpy.isSameSelectedColumnProperty(1, Arrays.asList("property2"), FactMappingValueType.EXPRESSION));
        when(factMappingMock.getFactMappingValueType()).thenReturn(FactMappingValueType.EXPRESSION);
        assertFalse(abstractScesimGridModelSpy.isSameSelectedColumnProperty(1, Arrays.asList("Fact", "property"), FactMappingValueType.NOT_EXPRESSION));
        assertFalse(abstractScesimGridModelSpy.isSameSelectedColumnProperty(1, Arrays.asList("Fact", "property2"), FactMappingValueType.NOT_EXPRESSION));
        assertFalse(abstractScesimGridModelSpy.isSameSelectedColumnProperty(1, Arrays.asList("property2"), FactMappingValueType.NOT_EXPRESSION));
        assertTrue(abstractScesimGridModelSpy.isSameSelectedColumnProperty(1, Arrays.asList("Fact", "property"), FactMappingValueType.EXPRESSION));
        assertFalse(abstractScesimGridModelSpy.isSameSelectedColumnProperty(1, Arrays.asList("Fact", "property2"), FactMappingValueType.EXPRESSION));
        assertFalse(abstractScesimGridModelSpy.isSameSelectedColumnProperty(1, Arrays.asList("property2"), FactMappingValueType.EXPRESSION));
    }
}