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
package org.drools.workbench.screens.scenariosimulation.client.widgets;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.event.shared.EventBus;
import org.drools.scenariosimulation.api.model.AbstractScesimData;
import org.drools.scenariosimulation.api.model.ExpressionElement;
import org.drools.scenariosimulation.api.model.ExpressionIdentifier;
import org.drools.scenariosimulation.api.model.FactIdentifier;
import org.drools.scenariosimulation.api.model.FactMapping;
import org.drools.scenariosimulation.api.model.FactMappingType;
import org.drools.scenariosimulation.api.model.Scenario;
import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.drools.scenariosimulation.api.model.ScesimModelDescriptor;
import org.drools.scenariosimulation.api.model.Simulation;
import org.drools.workbench.screens.scenariosimulation.client.events.EnableTestToolsEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.ReloadTestToolsEvent;
import org.drools.workbench.screens.scenariosimulation.client.handlers.ScenarioSimulationGridWidgetMouseEventHandler;
import org.drools.workbench.screens.scenariosimulation.client.menu.ScenarioContextMenuRegistry;
import org.drools.workbench.screens.scenariosimulation.client.metadata.ScenarioHeaderMetaData;
import org.drools.workbench.screens.scenariosimulation.client.models.ScenarioGridModel;
import org.drools.workbench.screens.scenariosimulation.client.renderers.ScenarioGridRenderer;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.drools.workbench.screens.scenariosimulation.client.utils.ScenarioSimulationBuilders;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn.ColumnWidthMode;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;
import org.uberfire.ext.wires.core.grids.client.widget.grid.NodeMouseEventHandler;
import org.uberfire.ext.wires.core.grids.client.widget.grid.impl.DefaultGridWidgetCellSelectorMouseEventHandler;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper;
import org.uberfire.ext.wires.core.grids.client.widget.grid.selections.SelectionExtension;

import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.COLUMNS;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.EXPRESSION_ALIAS_DESCRIPTION;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.EXPRESSION_ALIAS_GIVEN;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.EXPRESSION_ALIAS_INTEGER;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.GRID_COLUMN_GROUP;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.HEADER_ROWS_HEIGHT;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.TEST;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class ScenarioGridTest {

    @Mock
    private ScenarioGridModel scenarioGridModelMock;
    @Mock
    private ScenarioGridLayer scenarioGridLayerMock;
    @Mock
    private ScenarioGridRenderer scenarioGridRendererMock;
    @Mock
    private ScenarioSimulationBuilders.HeaderBuilder headerBuilderMock;
    @Mock
    private ScenarioGridColumn scenarioGridColumnMock;
    @Mock
    private ScenarioHeaderMetaData propertyHeaderMetadataMock;
    @Mock
    private EventBus eventBusMock;
    @Mock
    private BaseGridRendererHelper rendererHelperMock;
    @Mock
    private BaseGridRendererHelper.RenderingInformation renderingInformationMock;
    @Mock
    private BaseGridRendererHelper.ColumnInformation columnInformationMock;
    @Mock
    private BaseGridRendererHelper.RenderingBlockInformation floatingBlockInformationMock;
    @Mock
    private Viewport viewportMock;
    @Mock
    private ScenarioContextMenuRegistry scenarioContextMenuRegistryMock;

    private FactMapping factMappingDescription;
    private FactMapping factMappingGiven;
    private FactMapping factMappingInteger;
    private FactIdentifier factIdentifierGiven;
    private FactIdentifier factIdentifierInteger;

    private Simulation simulation = new Simulation();
    private ScenarioGrid scenarioGrid;

    @Before
    public void setup() {
        when(scenarioGridColumnMock.getPropertyHeaderMetaData()).thenReturn(propertyHeaderMetadataMock);
        when(scenarioGridModelMock.getAbstractScesimModel()).thenReturn(Optional.of(simulation));
        factIdentifierGiven = new FactIdentifier("GIVEN", "GIVEN");
        factIdentifierInteger = new FactIdentifier("Integer", "java.lang.Integer");
        factMappingDescription = new FactMapping(EXPRESSION_ALIAS_DESCRIPTION, FactIdentifier.DESCRIPTION, ExpressionIdentifier.DESCRIPTION);
        factMappingGiven = new FactMapping(EXPRESSION_ALIAS_GIVEN, factIdentifierGiven, new ExpressionIdentifier("GIVEN", FactMappingType.GIVEN));
        factMappingInteger = new FactMapping(EXPRESSION_ALIAS_INTEGER, factIdentifierInteger, new ExpressionIdentifier("GIVEN", FactMappingType.GIVEN));
        simulation = getSimulation();

        scenarioGrid = spy(new ScenarioGrid(scenarioGridModelMock,
                                            scenarioGridLayerMock,
                                            scenarioGridRendererMock,
                                            scenarioContextMenuRegistryMock) {

            @Override
            protected <T extends AbstractScesimData> void appendRow(int rowIndex, T scesimData) {
                // do nothing
            }

            @Override
            protected ScenarioSimulationBuilders.HeaderBuilder getHeaderBuilderLocal(String instanceTitle, String
                    propertyTitle, String columnId, String columnGroup, FactMappingType factMappingType) {
                return headerBuilderMock;
            }

            @Override
            protected ScenarioGridColumn getScenarioGridColumnLocal(ScenarioSimulationBuilders.HeaderBuilder headerBuilder, String placeHolder) {
                return scenarioGridColumnMock;
            }

            @Override
            protected BaseGridRendererHelper getBaseGridRendererHelper() {
                return rendererHelperMock;
            }

            @Override
            public Viewport getViewport() {
                return viewportMock;
            }

            @Override
            protected ScenarioHeaderMetaData getColumnScenarioHeaderMetaData(final ScenarioGridColumn scenarioGridColumn,
                                                                             final int rowIndex) {
                return propertyHeaderMetadataMock;
            }

            @Override
            protected EnableTestToolsEvent getEnableTestToolsEvent(final ScenarioGrid scenarioGrid,
                                                                   final ScenarioGridColumn scenarioGridColumn,
                                                                   final ScenarioHeaderMetaData scenarioHeaderMetaData,
                                                                   int uiColumnIndex,
                                                                   String group) {
                return new EnableTestToolsEvent();
            }

            @Override
            public Layer getLayer() {
                return scenarioGridLayerMock;
            }
        });
        when(rendererHelperMock.getRenderingInformation()).thenReturn(renderingInformationMock);
        when(renderingInformationMock.getHeaderRowsHeight()).thenReturn(HEADER_ROWS_HEIGHT);
        when(renderingInformationMock.getFloatingBlockInformation()).thenReturn(floatingBlockInformationMock);
        when(propertyHeaderMetadataMock.getColumnGroup()).thenReturn(GRID_COLUMN_GROUP);
        scenarioGrid.setEventBus(eventBusMock);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDefaultNodeMouseClickHandlers() {
        final List<NodeMouseEventHandler> handlers = scenarioGrid.getNodeMouseClickEventHandlers(scenarioGridLayerMock);

        assertEquals(1, handlers.size());
        assertTrue(handlers.get(0) instanceof DefaultGridWidgetCellSelectorMouseEventHandler);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDefaultNodeMouseDoubleClickHandlers() {
        final List<NodeMouseEventHandler> handlers = scenarioGrid.getNodeMouseDoubleClickEventHandlers(scenarioGridLayerMock,
                                                                                                       scenarioGridLayerMock);

        assertEquals(1, handlers.size());
        assertTrue(handlers.get(0) instanceof ScenarioSimulationGridWidgetMouseEventHandler);
    }

    @Test
    public void setContent() {
        InOrder callsOrder = inOrder(scenarioGridModelMock, scenarioGrid);
        scenarioGrid.setContent(simulation, ScenarioSimulationModel.Type.RULE);
        callsOrder.verify(scenarioGridModelMock, times(1)).clear();
        callsOrder.verify(scenarioGridModelMock, times(1)).bindContent(eq(simulation));
        callsOrder.verify(scenarioGrid, times(1)).setHeaderColumns(eq(simulation), eq(ScenarioSimulationModel.Type.RULE));
        callsOrder.verify(scenarioGrid, times(1)).appendRows(eq(simulation));
        callsOrder.verify(scenarioGridModelMock, times(1)).loadFactMappingsWidth();
        callsOrder.verify(scenarioGridModelMock, times(1)).forceRefreshWidth();
    }

    @Test
    public void clearSelections() {
        scenarioGrid.clearSelections();
        verify(scenarioGridModelMock, times(1)).clearSelections();
        verify(scenarioGridLayerMock, times(1)).batch();
    }

    @Test
    public void setSelectedColumn() {
        int columnIndex = 1;
        scenarioGrid.setSelectedColumn(columnIndex);
        verify(scenarioGridModelMock, times(1)).selectColumn(eq(columnIndex));
    }

    @Test
    public void setSelectedColumnAndHeader() {
        int headerRowIndex = 1;
        int columnIndex = 1;
        scenarioGrid.setSelectedColumnAndHeader(headerRowIndex, columnIndex);
        InOrder callsOrder = inOrder(scenarioGrid, scenarioGridLayerMock);
        callsOrder.verify(scenarioGrid, times(1)).selectHeaderCell(eq(headerRowIndex), eq(columnIndex), eq(false), eq(false));
        callsOrder.verify(scenarioGrid, times(1)).setSelectedColumn(eq(columnIndex));
        callsOrder.verify(scenarioGridLayerMock, times(1)).batch();
    }

    @Test
    public void setSelectedCell() {
        int rowIndex = 1;
        int columnIndex = 1;
        scenarioGrid.setSelectedCell(rowIndex, columnIndex);
        verify(scenarioGrid, times(1)).selectCell(eq(rowIndex), eq(columnIndex), eq(false), eq(false));
        verify(scenarioGridLayerMock, times(1)).batch();
    }

    @Test
    public void setHeaderColumns() {
        scenarioGrid.setHeaderColumns(simulation, ScenarioSimulationModel.Type.RULE);
        verify(scenarioGrid, times(COLUMNS)).setHeaderColumn(anyInt(), isA(FactMapping.class), eq(true));
    }

    @Test
    public void setHeaderColumn() {
        String columnId = factMappingDescription.getExpressionIdentifier().getName();
        FactMappingType type = factMappingDescription.getExpressionIdentifier().getType();
        String columnGroup = type.name();
        scenarioGrid.setHeaderColumn(1, factMappingDescription, true);
        verify(scenarioGrid, times(1)).isPropertyAssigned(eq(true), eq(factMappingDescription));
        verify(scenarioGrid, times(1)).getPlaceholder(eq(true), any());
        verify(scenarioGrid, times(1)).getScenarioGridColumnLocal(eq(EXPRESSION_ALIAS_DESCRIPTION),
                                                                  any(),
                                                                  eq(columnId),
                                                                  eq(columnGroup),
                                                                  eq(type),
                                                                  anyString());
        verify(scenarioGridColumnMock, times(1)).setColumnWidthMode(ColumnWidthMode.FIXED);

        reset(scenarioGrid);
        reset(scenarioGridColumnMock);
        reset(scenarioGridModelMock);
        columnId = factMappingGiven.getExpressionIdentifier().getName();
        type = factMappingGiven.getExpressionIdentifier().getType();
        columnGroup = type.name();
        scenarioGrid.setHeaderColumn(1, factMappingGiven, true);
        verify(scenarioGrid, times(1)).isPropertyAssigned(eq(true), eq(factMappingGiven));
        verify(scenarioGrid, times(1)).getPlaceholder(eq(false), any());
        verify(scenarioGrid, times(1)).getScenarioGridColumnLocal(eq(EXPRESSION_ALIAS_GIVEN),
                                                                  any(),
                                                                  eq(columnId),
                                                                  eq(columnGroup),
                                                                  eq(type),
                                                                  anyString());
        verify(scenarioGridColumnMock, never()).setColumnWidthMode(any());
    }

    @Test
    public void getScenarioGridColumnLocal() {
        String columnId = factMappingDescription.getExpressionIdentifier().getName();
        String instanceTitle = factMappingDescription.getFactIdentifier().getName();
        String propertyTitle = "PROPERTY TITLE";
        final FactMappingType type = factMappingDescription.getExpressionIdentifier().getType();
        String columnGroup = type.name();
        scenarioGrid.getScenarioGridColumnLocal(instanceTitle, propertyTitle, columnId, columnGroup, type, ScenarioSimulationEditorConstants.INSTANCE.insertValue());
        verify(scenarioGrid, times(1)).getHeaderBuilderLocal(eq(instanceTitle),
                                                             eq(propertyTitle),
                                                             eq(columnId),
                                                             eq(columnGroup),
                                                             eq(type));
    }

    @Test
    public void isInstanceAssigned() {
        assertTrue(scenarioGrid.isInstanceAssigned(FactIdentifier.DESCRIPTION));
        assertFalse(scenarioGrid.isInstanceAssigned(FactIdentifier.INDEX));
        assertFalse(scenarioGrid.isInstanceAssigned(FactIdentifier.EMPTY));
        assertTrue(scenarioGrid.isInstanceAssigned(factIdentifierGiven));
    }

    @Test
    public void isPropertyAssigned() {
        factMappingDescription.getExpressionElements().clear();
        assertTrue(scenarioGrid.isPropertyAssigned(false, factMappingDescription));
        assertTrue(scenarioGrid.isPropertyAssigned(true, factMappingDescription));
        factMappingDescription.getExpressionElements().add(new ExpressionElement(TEST));
        assertTrue(scenarioGrid.isPropertyAssigned(false, factMappingDescription));
        assertTrue(scenarioGrid.isPropertyAssigned(true, factMappingDescription));
        factMappingGiven.getExpressionElements().clear();
        assertFalse(scenarioGrid.isPropertyAssigned(false, factMappingGiven));
        assertFalse(scenarioGrid.isPropertyAssigned(true, factMappingGiven));
        factMappingGiven.getExpressionElements().add(new ExpressionElement("test"));
        assertFalse(scenarioGrid.isPropertyAssigned(false, factMappingGiven));
        assertTrue(scenarioGrid.isPropertyAssigned(true, factMappingGiven));
        factMappingInteger.getExpressionElements().clear();
        assertFalse(scenarioGrid.isPropertyAssigned(false, factMappingInteger));
        assertTrue(scenarioGrid.isPropertyAssigned(true, factMappingInteger));
    }

    @Test
    public void getPlaceholder() {
        FactMapping stringFactMapping = new FactMapping(
                FactIdentifier.create("test", String.class.getCanonicalName()),
                ExpressionIdentifier.create("test", FactMappingType.GIVEN));
        assertEquals(ScenarioSimulationEditorConstants.INSTANCE.insertValue(),
                     scenarioGrid.getPlaceholder(true, stringFactMapping));
        assertEquals(ScenarioSimulationEditorConstants.INSTANCE.defineValidType(),
                     scenarioGrid.getPlaceholder(false, stringFactMapping));

        FactMapping localDateFactMapping = new FactMapping(
                FactIdentifier.create("test", LocalDate.class.getCanonicalName()),
                ExpressionIdentifier.create("test", FactMappingType.GIVEN));
        assertEquals(ScenarioSimulationEditorConstants.INSTANCE.dateFormatPlaceholder(),
                     scenarioGrid.getPlaceholder(true, localDateFactMapping));
        assertEquals(ScenarioSimulationEditorConstants.INSTANCE.defineValidType(),
                     scenarioGrid.getPlaceholder(false, localDateFactMapping));
    }

    @Test
    public void appendRows() {
        scenarioGrid.appendRows(simulation);
        verify(scenarioGrid, times(1)).appendRow(anyInt(), isA(Scenario.class));
    }

    @Test
    public void testAdjustSelection() {
        final int uiColumnIndex = 0;
        final int uiRowIndex = 0;
        final ScenarioGridColumn columnMock = mock(ScenarioGridColumn.class);
        when(columnMock.getIndex()).thenReturn(uiColumnIndex);
        when(scenarioGridModelMock.getColumns()).thenReturn(Collections.singletonList(columnMock));

        final GridData.SelectedCell selectedHeaderCell = mock(GridData.SelectedCell.class);
        when(selectedHeaderCell.getColumnIndex()).thenReturn(uiColumnIndex);
        when(selectedHeaderCell.getRowIndex()).thenReturn(uiRowIndex);
        when(scenarioGridModelMock.getSelectedHeaderCells()).thenReturn(Collections.singletonList(selectedHeaderCell));

        scenarioGrid.adjustSelection(mock(SelectionExtension.class), false);

        verify(scenarioGrid).signalTestTools();
        verify(scenarioGrid).setSelectedColumn(eq(uiColumnIndex));
        verify(eventBusMock).fireEvent(any(EnableTestToolsEvent.class));

        // context menus could be shown
        verify(scenarioContextMenuRegistryMock, times(1)).hideMenus();
        verify(scenarioContextMenuRegistryMock, times(1)).hideErrorReportPopover();
    }

    @Test
    public void testShowContextMenuDescription() {
        final int uiColumnIndex = 0;
        final int uiRowIndex = 0;
        final double columnWidth = 100.0;
        final GridColumn columnMock = mock(ScenarioGridColumn.class);
        when(columnMock.getIndex()).thenReturn(uiColumnIndex);
        when(columnMock.getWidth()).thenReturn(columnWidth);
        when(((ScenarioGridColumn) columnMock).getFactIdentifier()).thenReturn(FactIdentifier.DESCRIPTION);
        when(columnMock.getHeaderMetaData()).thenReturn(Collections.singletonList(mock(GridColumn.HeaderMetaData.class)));
        when(scenarioGridModelMock.getColumns()).thenReturn(Collections.singletonList(columnMock));

        final double rowHeight = 40.0;
        final GridRow gridRow = mock(ScenarioGridRow.class);
        when(gridRow.getHeight()).thenReturn(rowHeight);
        when(scenarioGridModelMock.getRow(uiRowIndex)).thenReturn(gridRow);

        when(rendererHelperMock.getColumnInformation(50.0)).thenReturn(columnInformationMock);
        when(columnInformationMock.getColumn()).thenReturn(columnMock);
    }

    @Test
    public void testShowContextMenuGivenOrExpect() {
        final String columnGroup = "grp";
        final ScenarioHeaderMetaData scenarioHeaderMetaDataMock = mock(ScenarioHeaderMetaData.class);
        when(scenarioHeaderMetaDataMock.getColumnGroup()).thenReturn(columnGroup);

        final int uiColumnIndex = 0;
        final int uiRowIndex = 0;
        final double columnWidth = 100.0;
        final GridColumn columnMock = mock(ScenarioGridColumn.class);
        when(columnMock.getIndex()).thenReturn(uiColumnIndex);
        when(columnMock.getWidth()).thenReturn(columnWidth);
        when(((ScenarioGridColumn) columnMock).getFactIdentifier()).thenReturn(FactIdentifier.EMPTY);
        when(columnMock.getHeaderMetaData()).thenReturn(Collections.singletonList(scenarioHeaderMetaDataMock));
        when(((ScenarioGridColumn) columnMock).getInformationHeaderMetaData()).thenReturn(scenarioHeaderMetaDataMock);
        when(scenarioGridModelMock.getColumns()).thenReturn(Collections.singletonList(columnMock));

        final double rowHeight = 40.0;
        final GridRow gridRow = mock(ScenarioGridRow.class);
        when(gridRow.getHeight()).thenReturn(rowHeight);
        when(scenarioGridModelMock.getRow(uiRowIndex)).thenReturn(gridRow);

        when(rendererHelperMock.getColumnInformation(50.0)).thenReturn(columnInformationMock);
        when(columnInformationMock.getColumn()).thenReturn(columnMock);
    }

    private Simulation getSimulation() {
        Simulation toReturn = new Simulation();
        ScesimModelDescriptor simulationDescriptor = toReturn.getScesimModelDescriptor();
        simulationDescriptor.addFactMapping(FactIdentifier.INDEX.getName(), FactIdentifier.INDEX, ExpressionIdentifier.INDEX);
        simulationDescriptor.addFactMapping(FactIdentifier.DESCRIPTION.getName(), FactIdentifier.DESCRIPTION, ExpressionIdentifier.DESCRIPTION);

        Scenario scenario = toReturn.addData();
        int row = toReturn.getUnmodifiableData().indexOf(scenario);
        scenario.setDescription(null);

        // Add GIVEN Facts
        IntStream.range(2, 4).forEach(id -> {
            ExpressionIdentifier givenExpression = ExpressionIdentifier.create(row + "|" + id, FactMappingType.GIVEN);
            simulationDescriptor.addFactMapping(FactMapping.getInstancePlaceHolder(id), FactIdentifier.EMPTY, givenExpression);
            scenario.addMappingValue(FactIdentifier.EMPTY, givenExpression, null);
        });

        // Add EXPECT Facts
        IntStream.range(2, 4).forEach(id -> {
            id += 2; // This is to have consistent labels/names even when adding columns at runtime
            ExpressionIdentifier expectedExpression = ExpressionIdentifier.create(row + "|" + id, FactMappingType.EXPECT);
            simulationDescriptor.addFactMapping(FactMapping.getInstancePlaceHolder(id), FactIdentifier.EMPTY, expectedExpression);
            scenario.addMappingValue(FactIdentifier.EMPTY, expectedExpression, null);
        });
        return toReturn;
    }

    @Test
    public void signalTestToolsHeaderCellSelected() {
        final int uiColumnIndex = 0;
        final int uiRowIndex = 0;
        final ScenarioGridColumn columnMock = mock(ScenarioGridColumn.class);
        when(columnMock.getIndex()).thenReturn(uiColumnIndex);
        when(scenarioGridModelMock.getColumns()).thenReturn(Collections.singletonList(columnMock));

        final GridData.SelectedCell selectedHeaderCell = mock(GridData.SelectedCell.class);
        when(selectedHeaderCell.getColumnIndex()).thenReturn(uiColumnIndex);
        when(selectedHeaderCell.getRowIndex()).thenReturn(uiRowIndex);
        when(scenarioGridModelMock.getSelectedHeaderCells()).thenReturn(Collections.singletonList(selectedHeaderCell));
        scenarioGrid.signalTestToolsHeaderCellSelected(columnMock, selectedHeaderCell, uiColumnIndex);
        verify(eventBusMock, times(1)).fireEvent(isA(EnableTestToolsEvent.class));
    }

    @Test
    public void signalTestToolsHeaderCellSelected_EmptyInstance() {
        final int uiColumnIndex = 0;
        final int uiRowIndex = 0;
        final ScenarioGridColumn columnMock = mock(ScenarioGridColumn.class);
        when(columnMock.getIndex()).thenReturn(uiColumnIndex);
        when(scenarioGridModelMock.getColumns()).thenReturn(Collections.singletonList(columnMock));
        when(columnMock.isInstanceAssigned()).thenReturn(true);
        when(propertyHeaderMetadataMock.getMetadataType()).thenReturn(ScenarioHeaderMetaData.MetadataType.INSTANCE);

        final GridData.SelectedCell selectedHeaderCell = mock(GridData.SelectedCell.class);
        when(selectedHeaderCell.getColumnIndex()).thenReturn(uiColumnIndex);
        when(selectedHeaderCell.getRowIndex()).thenReturn(uiRowIndex);
        when(scenarioGridModelMock.getSelectedHeaderCells()).thenReturn(Collections.singletonList(selectedHeaderCell));
        scenarioGrid.signalTestToolsHeaderCellSelected(columnMock, selectedHeaderCell, uiColumnIndex);
        verify(eventBusMock, times(1)).fireEvent(isA(ReloadTestToolsEvent.class));
    }
}