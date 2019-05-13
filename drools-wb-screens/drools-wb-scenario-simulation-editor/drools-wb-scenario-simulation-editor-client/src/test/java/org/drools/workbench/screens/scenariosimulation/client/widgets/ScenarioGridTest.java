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

import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.event.shared.EventBus;
import org.drools.scenariosimulation.api.model.ExpressionElement;
import org.drools.scenariosimulation.api.model.ExpressionIdentifier;
import org.drools.scenariosimulation.api.model.FactIdentifier;
import org.drools.scenariosimulation.api.model.FactMapping;
import org.drools.scenariosimulation.api.model.FactMappingType;
import org.drools.scenariosimulation.api.model.Scenario;
import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.drools.scenariosimulation.api.model.Simulation;
import org.drools.scenariosimulation.api.model.SimulationDescriptor;
import org.drools.workbench.screens.scenariosimulation.client.events.EnableTestToolsEvent;
import org.drools.workbench.screens.scenariosimulation.client.factories.ScenarioHeaderTextBoxSingletonDOMElementFactory;
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
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn.ColumnWidthMode;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;
import org.uberfire.ext.wires.core.grids.client.widget.grid.NodeMouseEventHandler;
import org.uberfire.ext.wires.core.grids.client.widget.grid.impl.DefaultGridWidgetCellSelectorMouseEventHandler;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper;
import org.uberfire.ext.wires.core.grids.client.widget.grid.selections.SelectionExtension;

import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.TEST;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
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
    private ScenarioHeaderTextBoxSingletonDOMElementFactory scenarioHeaderTextBoxSingletonDOMElementFactoryMock;
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

    private final String EXPRESSION_ALIAS_DESCRIPTION = "EXPRESSION_ALIAS_DESCRIPTION";
    private final String EXPRESSION_ALIAS_GIVEN = "EXPRESSION_ALIAS_GIVEN";
    private final String EXPRESSION_ALIAS_INTEGER = "EXPRESSION_ALIAS_INTEGER";
    private final String EXPRESSION_ALIAS_INDEX = "EXPRESSION_ALIAS_INDEX";

    private FactMapping factMappingDescription;
    private FactMapping factMappingIndex;
    private FactMapping factMappingGiven;
    private FactMapping factMappingInteger;
    private FactIdentifier factIdentifierGiven;
    private FactIdentifier factIdentifierInteger;

    private Simulation simulation = new Simulation();

    private final int COLUMNS = 6;

    private final double HEADER_ROWS_HEIGHT = 100.0;

    private ScenarioGrid scenarioGrid;

    @Before
    public void setup() {
        when(scenarioGridColumnMock.getPropertyHeaderMetaData()).thenReturn(propertyHeaderMetadataMock);
        factIdentifierGiven = new FactIdentifier("GIVEN", "GIVEN");
        factIdentifierInteger = new FactIdentifier("Integer", "java.lang.Integer");
        factMappingDescription = new FactMapping(EXPRESSION_ALIAS_DESCRIPTION, FactIdentifier.DESCRIPTION, ExpressionIdentifier.DESCRIPTION);
        factMappingGiven = new FactMapping(EXPRESSION_ALIAS_GIVEN, factIdentifierGiven, new ExpressionIdentifier("GIVEN", FactMappingType.GIVEN));
        factMappingInteger = new FactMapping(EXPRESSION_ALIAS_INTEGER, factIdentifierInteger, new ExpressionIdentifier("GIVEN", FactMappingType.GIVEN));
        factMappingIndex = new FactMapping(EXPRESSION_ALIAS_INDEX, FactIdentifier.INDEX, ExpressionIdentifier.INDEX);
        simulation = getSimulation();

        scenarioGrid = spy(new ScenarioGrid(scenarioGridModelMock,
                                            scenarioGridLayerMock,
                                            scenarioGridRendererMock,
                                            scenarioContextMenuRegistryMock) {

            @Override
            protected void appendRow(int rowIndex, Scenario scenario) {
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
        });
        when(rendererHelperMock.getRenderingInformation()).thenReturn(renderingInformationMock);
        when(renderingInformationMock.getHeaderRowsHeight()).thenReturn(HEADER_ROWS_HEIGHT);
        when(renderingInformationMock.getFloatingBlockInformation()).thenReturn(floatingBlockInformationMock);
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
        scenarioGrid.setContent(simulation);
        verify(scenarioGridModelMock, times(1)).clear();
        verify(scenarioGridModelMock, times(1)).bindContent(eq(simulation));
        verify(scenarioGrid, times(1)).setHeaderColumns(eq(simulation));
        verify(scenarioGrid, times(1)).appendRows(eq(simulation));
    }

    @Test
    public void setHeaderColumns() {
        scenarioGrid.setHeaderColumns(simulation);
        verify(scenarioGrid, times(COLUMNS)).setHeaderColumn(anyInt(), isA(FactMapping.class), eq(true));
    }

    @Test
    public void setHeaderColumn() {
        String columnId = factMappingDescription.getExpressionIdentifier().getName();
        FactMappingType type = factMappingDescription.getExpressionIdentifier().getType();
        String columnGroup = type.name();
        scenarioGrid.setHeaderColumn(1, factMappingDescription, true);
        verify(scenarioGrid, times(1)).isPropertyAssigned(eq(true), eq(factMappingDescription));
        verify(scenarioGrid, times(1)).getPlaceholder(eq(true));
        verify(scenarioGrid, times(1)).getScenarioGridColumnLocal(eq(EXPRESSION_ALIAS_DESCRIPTION),
                                                                  any(),
                                                                  eq(columnId),
                                                                  eq(columnGroup),
                                                                  eq(type),
                                                                  eq(ScenarioSimulationEditorConstants.INSTANCE.insertValue()));
        verify(scenarioGridColumnMock, times(1)).setColumnWidthMode(ColumnWidthMode.FIXED);

        reset(scenarioGrid);
        reset(scenarioGridColumnMock);
        reset(scenarioGridModelMock);
        columnId = factMappingGiven.getExpressionIdentifier().getName();
        type = factMappingGiven.getExpressionIdentifier().getType();
        columnGroup = type.name();
        scenarioGrid.setHeaderColumn(1, factMappingGiven, true);
        verify(scenarioGrid, times(1)).isPropertyAssigned(eq(true), eq(factMappingGiven));
        verify(scenarioGrid, times(1)).getPlaceholder(eq(false));
        verify(scenarioGrid, times(1)).getScenarioGridColumnLocal(eq(EXPRESSION_ALIAS_GIVEN),
                                                                  any(),
                                                                  eq(columnId),
                                                                  eq(columnGroup),
                                                                  eq(type),
                                                                  eq(ScenarioSimulationEditorConstants.INSTANCE.defineValidType()));
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
        assertEquals(ScenarioSimulationEditorConstants.INSTANCE.insertValue(), scenarioGrid.getPlaceholder(true));
        assertEquals(ScenarioSimulationEditorConstants.INSTANCE.defineValidType(), scenarioGrid.getPlaceholder(false));
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

        verify(scenarioGrid).signalTestToolsAboutSelectedHeaderCells();
        verify(scenarioGrid).setSelectedColumnAndHeader(uiRowIndex, uiColumnIndex);
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
        SimulationDescriptor simulationDescriptor = toReturn.getSimulationDescriptor();
        simulationDescriptor.setType(ScenarioSimulationModel.Type.RULE);

        simulationDescriptor.addFactMapping(FactIdentifier.INDEX.getName(), FactIdentifier.INDEX, ExpressionIdentifier.INDEX);
        simulationDescriptor.addFactMapping(FactIdentifier.DESCRIPTION.getName(), FactIdentifier.DESCRIPTION, ExpressionIdentifier.DESCRIPTION);

        Scenario scenario = toReturn.addScenario();
        int row = toReturn.getUnmodifiableScenarios().indexOf(scenario);
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
}