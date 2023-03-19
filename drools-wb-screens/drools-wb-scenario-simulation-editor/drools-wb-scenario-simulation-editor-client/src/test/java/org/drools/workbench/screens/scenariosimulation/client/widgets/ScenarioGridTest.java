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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.event.shared.EventBus;
import org.drools.scenariosimulation.api.model.AbstractScesimData;
import org.drools.scenariosimulation.api.model.Background;
import org.drools.scenariosimulation.api.model.ExpressionElement;
import org.drools.scenariosimulation.api.model.ExpressionIdentifier;
import org.drools.scenariosimulation.api.model.FactIdentifier;
import org.drools.scenariosimulation.api.model.FactMapping;
import org.drools.scenariosimulation.api.model.FactMappingType;
import org.drools.scenariosimulation.api.model.FactMappingValueType;
import org.drools.scenariosimulation.api.model.Scenario;
import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.drools.scenariosimulation.api.model.ScesimModelDescriptor;
import org.drools.scenariosimulation.api.model.Simulation;
import org.drools.workbench.screens.scenariosimulation.client.enums.GridWidget;
import org.drools.workbench.screens.scenariosimulation.client.events.EnableTestToolsEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.ReloadTestToolsEvent;
import org.drools.workbench.screens.scenariosimulation.client.factories.CollectionEditorSingletonDOMElementFactory;
import org.drools.workbench.screens.scenariosimulation.client.factories.ScenarioExpressionCellTextAreaSingletonDOMElementFactory;
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
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
    @Mock
    private ScenarioExpressionCellTextAreaSingletonDOMElementFactory expressionCellTextAreaSingletonDOMElementFactoryMock;
    @Mock
    private CollectionEditorSingletonDOMElementFactory collectionEditorSingletonDOMElementFactory;

    private FactMapping factMappingDescription;
    private FactMapping factMappingGiven;
    private FactMapping factMappingInteger;
    private FactIdentifier factIdentifierGiven;
    private FactIdentifier factIdentifierInteger;

    private Simulation simulation;
    private ScenarioGrid scenarioGridSpy;

    @Before
    public void setup() {
        simulation = getSimulation();
        when(scenarioGridColumnMock.getPropertyHeaderMetaData()).thenReturn(propertyHeaderMetadataMock);
        when(scenarioGridModelMock.getAbstractScesimModel()).thenReturn(Optional.of(simulation));
        when(scenarioGridModelMock.getGridWidget()).thenReturn(GridWidget.SIMULATION);
        when(scenarioGridModelMock.getScenarioExpressionCellTextAreaSingletonDOMElementFactory()).thenReturn(expressionCellTextAreaSingletonDOMElementFactoryMock);
        when(scenarioGridModelMock.getCollectionEditorSingletonDOMElementFactory()).thenReturn(collectionEditorSingletonDOMElementFactory);
        factIdentifierGiven = FactIdentifier.create("GIVEN", "GIVEN");
        factIdentifierInteger = FactIdentifier.create("Integer", "java.lang.Integer");
        factMappingDescription = new FactMapping(EXPRESSION_ALIAS_DESCRIPTION, FactIdentifier.DESCRIPTION, ExpressionIdentifier.DESCRIPTION);
        factMappingGiven = new FactMapping(EXPRESSION_ALIAS_GIVEN, factIdentifierGiven, new ExpressionIdentifier("GIVEN", FactMappingType.GIVEN));
        factMappingInteger = new FactMapping(EXPRESSION_ALIAS_INTEGER, factIdentifierInteger, new ExpressionIdentifier("GIVEN", FactMappingType.GIVEN));

        scenarioGridSpy = spy(new ScenarioGrid(scenarioGridModelMock,
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
        scenarioGridSpy.setEventBus(eventBusMock);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDefaultNodeMouseClickHandlers() {
        final List<NodeMouseEventHandler> handlers = scenarioGridSpy.getNodeMouseClickEventHandlers(scenarioGridLayerMock);

        assertEquals(1, handlers.size());
        assertTrue(handlers.get(0) instanceof DefaultGridWidgetCellSelectorMouseEventHandler);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDefaultNodeMouseDoubleClickHandlers() {
        final List<NodeMouseEventHandler> handlers = scenarioGridSpy.getNodeMouseDoubleClickEventHandlers(scenarioGridLayerMock,
                                                                                                          scenarioGridLayerMock);

        assertEquals(1, handlers.size());
        assertTrue(handlers.get(0) instanceof ScenarioSimulationGridWidgetMouseEventHandler);
    }

    @Test
    public void setContent() {
        InOrder callsOrder = inOrder(scenarioGridModelMock, scenarioGridSpy);
        scenarioGridSpy.setContent(simulation, ScenarioSimulationModel.Type.RULE);
        callsOrder.verify(scenarioGridModelMock, times(1)).clear();
        callsOrder.verify(scenarioGridModelMock, times(1)).bindContent(eq(simulation));
        callsOrder.verify(scenarioGridSpy, times(1)).setHeaderColumns(eq(simulation), eq(ScenarioSimulationModel.Type.RULE));
        callsOrder.verify(scenarioGridSpy, times(1)).appendRows(eq(simulation));
        callsOrder.verify(scenarioGridModelMock, times(1)).loadFactMappingsWidth();
        callsOrder.verify(scenarioGridModelMock, times(1)).forceRefreshWidth();
    }

    @Test
    public void getGridWidget() {
        final GridWidget retrieved = scenarioGridSpy.getGridWidget();
        assertNotNull(retrieved);
        assertEquals(GridWidget.SIMULATION, retrieved);
        verify(scenarioGridSpy.getModel(), times(1)).getGridWidget();
    }


    @Test
    public void clearSelections() {
        scenarioGridSpy.clearSelections();
        verify(scenarioGridModelMock, times(1)).clearSelections();
        verify(scenarioGridLayerMock, times(1)).batch();
    }

    @Test
    public void setSelectedColumn() {
        int columnIndex = 1;
        scenarioGridSpy.setSelectedColumn(columnIndex);
        verify(scenarioGridModelMock, times(1)).selectColumn(eq(columnIndex));
    }

    @Test
    public void setSelectedColumnAndHeader() {
        int headerRowIndex = 1;
        int columnIndex = 1;
        scenarioGridSpy.setSelectedColumnAndHeader(headerRowIndex, columnIndex);
        InOrder callsOrder = inOrder(scenarioGridSpy, scenarioGridLayerMock);
        callsOrder.verify(scenarioGridSpy, times(1)).selectHeaderCell(eq(headerRowIndex), eq(columnIndex), eq(false), eq(false));
        callsOrder.verify(scenarioGridSpy, times(1)).setSelectedColumn(eq(columnIndex));
        callsOrder.verify(scenarioGridLayerMock, times(1)).batch();
    }

    @Test
    public void setSelectedCell() {
        int rowIndex = 1;
        int columnIndex = 1;
        scenarioGridSpy.setSelectedCell(rowIndex, columnIndex);
        verify(scenarioGridSpy, times(1)).selectCell(eq(rowIndex), eq(columnIndex), eq(false), eq(false));
        verify(scenarioGridLayerMock, times(1)).batch();
    }

    @Test
    public void setHeaderColumns() {
        scenarioGridSpy.setHeaderColumns(simulation, ScenarioSimulationModel.Type.RULE);
        verify(scenarioGridSpy, times(COLUMNS)).setHeaderColumn(anyInt(), isA(FactMapping.class), eq(true));
        scenarioGridSpy.setHeaderColumns(simulation, ScenarioSimulationModel.Type.DMN);
        verify(scenarioGridSpy, times(COLUMNS)).setHeaderColumn(anyInt(), isA(FactMapping.class), eq(false));
        Background background = new Background();
        scenarioGridSpy.setHeaderColumns(background, ScenarioSimulationModel.Type.RULE);
        verify(scenarioGridSpy, times(COLUMNS)).setHeaderColumn(anyInt(), isA(FactMapping.class), eq(false));
        scenarioGridSpy.setHeaderColumns(background, ScenarioSimulationModel.Type.DMN);
        verify(scenarioGridSpy, times(COLUMNS)).setHeaderColumn(anyInt(), isA(FactMapping.class), eq(false));
    }

    @Test
    public void setHeaderColumn() {
        String columnId = factMappingDescription.getExpressionIdentifier().getName();
        FactMappingType type = factMappingDescription.getExpressionIdentifier().getType();
        String columnGroup = type.name();
        scenarioGridSpy.setType(ScenarioSimulationModel.Type.RULE);
        scenarioGridSpy.setHeaderColumn(1, factMappingDescription, true);
        verify(scenarioGridSpy, times(1)).isPropertyAssigned(eq(true), eq(factMappingDescription));
        verify(scenarioGridSpy, times(1)).getPlaceHolder(eq(true), eq(true), isA(FactMappingValueType.class), anyString());
        verify(scenarioGridSpy, times(1)).getScenarioGridColumnLocal(eq(EXPRESSION_ALIAS_DESCRIPTION),
                                                                  any(),
                                                                  eq(columnId),
                                                                  eq(columnGroup),
                                                                  eq(type),
                                                                  anyString());
        verify(scenarioGridColumnMock, times(1)).setColumnWidthMode(ColumnWidthMode.FIXED);
        verify(scenarioGridModelMock, times(1)).getDOMElementFactory(anyString(), eq(ScenarioSimulationModel.Type.RULE), eq(FactMappingValueType.NOT_EXPRESSION));

        reset(scenarioGridSpy);
        reset(scenarioGridColumnMock);
        reset(scenarioGridModelMock);
        columnId = factMappingGiven.getExpressionIdentifier().getName();
        type = factMappingGiven.getExpressionIdentifier().getType();
        columnGroup = type.name();
        scenarioGridSpy.setHeaderColumn(1, factMappingGiven, true);
        verify(scenarioGridSpy, times(1)).isPropertyAssigned(eq(true), eq(factMappingGiven));
        verify(scenarioGridSpy, times(1)).getPlaceHolder(eq(true), eq(false), eq(FactMappingValueType.NOT_EXPRESSION), anyString());
        verify(scenarioGridSpy, times(1)).getScenarioGridColumnLocal(eq(EXPRESSION_ALIAS_GIVEN),
                                                                     any(),
                                                                     eq(columnId),
                                                                     eq(columnGroup),
                                                                     eq(type),
                                                                     anyString());
        verify(scenarioGridColumnMock, never()).setColumnWidthMode(any());
        verify(scenarioGridModelMock, never()).getDOMElementFactory(any(), any(), any());
    }

    @Test
    public void getScenarioGridColumnLocal() {
        String columnId = factMappingDescription.getExpressionIdentifier().getName();
        String instanceTitle = factMappingDescription.getFactIdentifier().getName();
        String propertyTitle = "PROPERTY TITLE";
        final FactMappingType type = factMappingDescription.getExpressionIdentifier().getType();
        String columnGroup = type.name();
        scenarioGridSpy.getScenarioGridColumnLocal(instanceTitle, propertyTitle, columnId, columnGroup, type, ScenarioSimulationEditorConstants.INSTANCE.insertValue());
        verify(scenarioGridSpy, times(1)).getHeaderBuilderLocal(eq(instanceTitle),
                                                                eq(propertyTitle),
                                                                eq(columnId),
                                                                eq(columnGroup),
                                                                eq(type));
    }

    @Test
    public void isInstanceAssigned() {
        assertTrue(scenarioGridSpy.isInstanceAssigned(FactIdentifier.DESCRIPTION));
        assertFalse(scenarioGridSpy.isInstanceAssigned(FactIdentifier.INDEX));
        assertFalse(scenarioGridSpy.isInstanceAssigned(FactIdentifier.EMPTY));
        assertTrue(scenarioGridSpy.isInstanceAssigned(factIdentifierGiven));
    }

    @Test
    public void isPropertyAssigned() {
        factMappingDescription.getExpressionElements().clear();
        assertTrue(scenarioGridSpy.isPropertyAssigned(false, factMappingDescription));
        assertTrue(scenarioGridSpy.isPropertyAssigned(true, factMappingDescription));
        factMappingDescription.getExpressionElements().add(new ExpressionElement(TEST));
        assertTrue(scenarioGridSpy.isPropertyAssigned(false, factMappingDescription));
        assertTrue(scenarioGridSpy.isPropertyAssigned(true, factMappingDescription));
        factMappingGiven.getExpressionElements().clear();
        assertFalse(scenarioGridSpy.isPropertyAssigned(false, factMappingGiven));
        assertFalse(scenarioGridSpy.isPropertyAssigned(true, factMappingGiven));
        factMappingGiven.getExpressionElements().add(new ExpressionElement("test"));
        assertFalse(scenarioGridSpy.isPropertyAssigned(false, factMappingGiven));
        assertTrue(scenarioGridSpy.isPropertyAssigned(true, factMappingGiven));
        factMappingInteger.getExpressionElements().clear();
        assertFalse(scenarioGridSpy.isPropertyAssigned(false, factMappingInteger));
        assertTrue(scenarioGridSpy.isPropertyAssigned(true, factMappingInteger));
    }

    @Test
    public void appendRows() {
        scenarioGridSpy.appendRows(simulation);
        verify(scenarioGridSpy, times(1)).appendRow(anyInt(), isA(Scenario.class));
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

        scenarioGridSpy.adjustSelection(mock(SelectionExtension.class), false);

        verify(scenarioGridSpy).signalTestTools();
        verify(scenarioGridSpy).setSelectedColumn(eq(uiColumnIndex));
        verify(eventBusMock).fireEvent(any());

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
        scenarioGridSpy.signalTestToolsHeaderCellSelected(columnMock, selectedHeaderCell, uiColumnIndex);
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
        scenarioGridSpy.signalTestToolsHeaderCellSelected(columnMock, selectedHeaderCell, uiColumnIndex);
        verify(eventBusMock, times(1)).fireEvent(isA(ReloadTestToolsEvent.class));
    }

    @Test
    public void ensureCellIsSelected_EmptyGrid() {
        when(scenarioGridModelMock.getColumnCount()).thenReturn(0);
        scenarioGridSpy.ensureCellIsSelected();
        verify(scenarioGridSpy, never()).selectCell(anyInt(), anyInt(), anyBoolean(), anyBoolean());
        verify(scenarioGridSpy, never()).signalTestTools();
    }

    @Test
    public void reselectCurrentHeaderCell() {
        final ScenarioGridColumn columnMock = mock(ScenarioGridColumn.class);
        when(columnMock.getIndex()).thenReturn(0);
        when(scenarioGridModelMock.getColumns()).thenReturn(Collections.singletonList(columnMock));
        List<GridData.SelectedCell> selectedHeaderCells = new ArrayList<>();
        selectedHeaderCells.add(new GridData.SelectedCell(0, 0));
        when(scenarioGridModelMock.getSelectedHeaderCells()).thenReturn(selectedHeaderCells);
        scenarioGridSpy.selectCurrentHeaderCellGroup();
        verify(scenarioGridSpy, times(1)).setSelectedColumnAndHeader(eq(0), eq(0));
    }

    @Test
    public void reselectCurrentHeaderCellMultipleColumns() {
        final ScenarioGridColumn columnMock = mock(ScenarioGridColumn.class);
        when(columnMock.getIndex()).thenReturn(0);
        final ScenarioGridColumn columnMock2 = mock(ScenarioGridColumn.class);
        when(columnMock2.getIndex()).thenReturn(1);
        final ScenarioGridColumn columnMock3 = mock(ScenarioGridColumn.class);
        when(columnMock3.getIndex()).thenReturn(2);
        when(scenarioGridModelMock.getColumns()).thenReturn(Arrays.asList(columnMock, columnMock2, columnMock3));
        List<GridData.SelectedCell> selectedHeaderCells = new ArrayList<>();
        selectedHeaderCells.add(new GridData.SelectedCell(0, 0));
        selectedHeaderCells.add(new GridData.SelectedCell(0, 1));
        selectedHeaderCells.add(new GridData.SelectedCell(0, 2));
        when(scenarioGridModelMock.getSelectedHeaderCells()).thenReturn(selectedHeaderCells);
        scenarioGridSpy.selectCurrentHeaderCellGroup();
        verify(scenarioGridSpy, times(1)).setSelectedColumnAndHeader(eq(0), eq(0));
    }

    @Test
    public void reselectCurrentHeaderCellMultipleMiddleColumns() {
        final ScenarioGridColumn columnMock = mock(ScenarioGridColumn.class);
        when(columnMock.getIndex()).thenReturn(0);
        final ScenarioGridColumn columnMock2 = mock(ScenarioGridColumn.class);
        when(columnMock2.getIndex()).thenReturn(1);
        final ScenarioGridColumn columnMock3 = mock(ScenarioGridColumn.class);
        when(columnMock3.getIndex()).thenReturn(2);
        when(scenarioGridModelMock.getColumns()).thenReturn(Arrays.asList(columnMock, columnMock2, columnMock3));
        List<GridData.SelectedCell> selectedHeaderCells = new ArrayList<>();
        selectedHeaderCells.add(new GridData.SelectedCell(0, 1));
        selectedHeaderCells.add(new GridData.SelectedCell(0, 2));
        when(scenarioGridModelMock.getSelectedHeaderCells()).thenReturn(selectedHeaderCells);
        scenarioGridSpy.selectCurrentHeaderCellGroup();
        verify(scenarioGridSpy, times(1)).setSelectedColumnAndHeader(eq(0), eq(1));
    }

    @Test
    public void reselectCurrentHeaderCell_NoHeaderCellsSelected() {
        List<GridData.SelectedCell> selectedHeaderCells = new ArrayList<>();
        when(scenarioGridModelMock.getSelectedHeaderCells()).thenReturn(selectedHeaderCells);
        scenarioGridSpy.selectCurrentHeaderCellGroup();
        verify(scenarioGridSpy, never()).setSelectedColumnAndHeader(anyInt(), anyInt());
    }
}