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

import java.util.stream.IntStream;

import com.ait.lienzo.client.core.event.NodeMouseDoubleClickHandler;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.drools.workbench.screens.scenariosimulation.client.factories.ScenarioHeaderTextBoxSingletonDOMElementFactory;
import org.drools.workbench.screens.scenariosimulation.client.metadata.ScenarioHeaderMetaData;
import org.drools.workbench.screens.scenariosimulation.client.models.ScenarioGridModel;
import org.drools.workbench.screens.scenariosimulation.client.renderers.ScenarioGridRenderer;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.drools.workbench.screens.scenariosimulation.client.utils.ScenarioSimulationBuilders;
import org.drools.workbench.screens.scenariosimulation.model.ExpressionElement;
import org.drools.workbench.screens.scenariosimulation.model.ExpressionIdentifier;
import org.drools.workbench.screens.scenariosimulation.model.FactIdentifier;
import org.drools.workbench.screens.scenariosimulation.model.FactMapping;
import org.drools.workbench.screens.scenariosimulation.model.FactMappingType;
import org.drools.workbench.screens.scenariosimulation.model.Scenario;
import org.drools.workbench.screens.scenariosimulation.model.Simulation;
import org.drools.workbench.screens.scenariosimulation.model.SimulationDescriptor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridSelectionManager;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.GridPinnedModeManager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class ScenarioGridTest {

    @Mock
    private ScenarioGridModel mockScenarioGridModel;
    @Mock
    private ScenarioGridLayer mockScenarioGridLayer;
    @Mock
    private ScenarioGridRenderer mockScenarioGridRenderer;
    @Mock
    private ScenarioGridPanel mockScenarioGridPanel;
    @Mock
    private ScenarioHeaderTextBoxSingletonDOMElementFactory scenarioHeaderTextBoxSingletonDOMElementFactoryMock;
    @Mock
    private ScenarioSimulationBuilders.HeaderBuilder headerBuilderMock;
    @Mock
    private ScenarioGridColumn scenarioGridColumnMock;
    @Mock
    private ScenarioHeaderMetaData propertyHeaderMetadataMock;

    private final FactMappingType factMappingType = FactMappingType.valueOf("OTHER");
    private final String EXPRESSION_ALIAS_DESCRIPTION = "EXPRESSION_ALIAS_DESCRIPTION";
    private final String EXPRESSION_ALIAS_GIVEN = "EXPRESSION_ALIAS_GIVEN";
    private final String EXPRESSION_ALIAS_INDEX = "EXPRESSION_ALIAS_INDEX";

    private FactMapping factMappingDescription;
    private FactMapping factMappingIndex;
    private FactMapping factMappingGiven;
    private FactIdentifier factIdentifierGiven;

    private Simulation simulation = new Simulation();

    private final int COLUMNS = 6;

    private ScenarioGrid scenarioGrid;

    @Before
    public void setup() {
        when(scenarioGridColumnMock.getPropertyHeaderMetaData()).thenReturn(propertyHeaderMetadataMock);
        factIdentifierGiven = new FactIdentifier("GIVEN", "GIVEN");
        factMappingDescription = new FactMapping(EXPRESSION_ALIAS_DESCRIPTION, FactIdentifier.DESCRIPTION, ExpressionIdentifier.DESCRIPTION);
        factMappingGiven = new FactMapping(EXPRESSION_ALIAS_GIVEN, factIdentifierGiven, new ExpressionIdentifier("GIVEN", FactMappingType.GIVEN));
        factMappingIndex = new FactMapping(EXPRESSION_ALIAS_INDEX, FactIdentifier.INDEX, ExpressionIdentifier.INDEX);
        simulation = getSimulation();
        scenarioGrid = spy(new ScenarioGrid(mockScenarioGridModel, mockScenarioGridLayer, mockScenarioGridRenderer, mockScenarioGridPanel) {

            @Override
            protected void appendRow(int rowIndex, Scenario scenario) {
                // do nothing
            }

            @Override
            protected ScenarioHeaderTextBoxSingletonDOMElementFactory getScenarioHeaderTextBoxSingletonDOMElementFactory() {
                return scenarioHeaderTextBoxSingletonDOMElementFactoryMock;
            }

            @Override
            protected ScenarioSimulationBuilders.HeaderBuilder getHeaderBuilderLocal(String instanceTitle, String propertyTitle, String columnId, String columnGroup, FactMappingType factMappingType, ScenarioHeaderTextBoxSingletonDOMElementFactory factoryHeader) {
                return headerBuilderMock;
            }

            @Override
            protected ScenarioGridColumn getScenarioGridColumnLocal(ScenarioSimulationBuilders.HeaderBuilder headerBuilder, boolean readOnly, String placeHolder) {
                return scenarioGridColumnMock;
            }
        });
    }

    @Test
    public void getGridMouseDoubleClickHandler() {
        NodeMouseDoubleClickHandler retrieved = scenarioGrid.getGridMouseDoubleClickHandler(mock(GridSelectionManager.class), mock(GridPinnedModeManager.class));
        assertNotNull(retrieved);
    }

    @Test
    public void setContent() {
        scenarioGrid.setContent(simulation);
        verify(mockScenarioGridModel, times(1)).clear();
        verify(mockScenarioGridModel, times(1)).bindContent(eq(simulation));
        verify(scenarioGrid, times(1)).setHeaderColumns(eq(simulation));
        verify(scenarioGrid, times(1)).appendRows(eq(simulation));
    }

    @Test
    public void setHeaderColumns() {
        scenarioGrid.setHeaderColumns(simulation);
        verify(scenarioGrid, times(COLUMNS)).setHeaderColumn(anyInt(), isA(FactMapping.class));
    }

    @Test
    public void setHeaderColumn() {
        String columnId = factMappingDescription.getExpressionIdentifier().getName();
        FactMappingType type = factMappingDescription.getExpressionIdentifier().getType();
        String columnGroup = type.name();
        scenarioGrid.setHeaderColumn(1, factMappingDescription);
        verify(scenarioGrid, times(1)).isPropertyAssigned(eq(true), eq(factMappingDescription));
        verify(scenarioGrid, times(1)).getPlaceholder(eq(true));
        verify(scenarioGrid, times(1)).getScenarioGridColumnLocal(eq(EXPRESSION_ALIAS_DESCRIPTION),
                                                                  any(),
                                                                  eq(columnId),
                                                                  eq(columnGroup),
                                                                  eq(type),
                                                                  eq(true),
                                                                  eq(ScenarioSimulationEditorConstants.INSTANCE.insertValue()));
        reset(scenarioGrid);
        columnId = factMappingGiven.getExpressionIdentifier().getName();
        type = factMappingGiven.getExpressionIdentifier().getType();
        columnGroup = type.name();
        scenarioGrid.setHeaderColumn(1, factMappingGiven);
        verify(scenarioGrid, times(1)).isPropertyAssigned(eq(true), eq(factMappingGiven));
        verify(scenarioGrid, times(1)).getPlaceholder(eq(false));
        verify(scenarioGrid, times(1)).getScenarioGridColumnLocal(eq(EXPRESSION_ALIAS_GIVEN),
                                                                  any(),
                                                                  eq(columnId),
                                                                  eq(columnGroup),
                                                                  eq(type),
                                                                  eq(false),
                                                                  eq(ScenarioSimulationEditorConstants.INSTANCE.defineValidType()));
    }

    @Test
    public void getScenarioGridColumnLocal() {
        String columnId = factMappingDescription.getExpressionIdentifier().getName();
        String instanceTitle = factMappingDescription.getFactIdentifier().getName();
        String propertyTitle = "PROPERTY TITLE";
        final FactMappingType type = factMappingDescription.getExpressionIdentifier().getType();
        String columnGroup = type.name();
        scenarioGrid.getScenarioGridColumnLocal(instanceTitle, propertyTitle, columnId, columnGroup, type, false, ScenarioSimulationEditorConstants.INSTANCE.insertValue());
        verify(scenarioGrid, times(1)).getScenarioHeaderTextBoxSingletonDOMElementFactory();
        verify(scenarioGrid, times(1)).getHeaderBuilderLocal(eq(instanceTitle),
                                                             eq(propertyTitle),
                                                             eq(columnId),
                                                             eq(columnGroup),
                                                             eq(type),
                                                             eq(scenarioHeaderTextBoxSingletonDOMElementFactoryMock));
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
        factMappingDescription.getExpressionElements().add(new ExpressionElement("test"));
        assertTrue(scenarioGrid.isPropertyAssigned(false, factMappingDescription));
        assertTrue(scenarioGrid.isPropertyAssigned(true, factMappingDescription));
        factMappingGiven.getExpressionElements().clear();
        assertFalse(scenarioGrid.isPropertyAssigned(false, factMappingGiven));
        assertFalse(scenarioGrid.isPropertyAssigned(true, factMappingGiven));
        factMappingGiven.getExpressionElements().add(new ExpressionElement("test"));
        assertFalse(scenarioGrid.isPropertyAssigned(false, factMappingGiven));
        assertTrue(scenarioGrid.isPropertyAssigned(true, factMappingGiven));
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

    private Simulation getSimulation() {
        Simulation toReturn = new Simulation();
        SimulationDescriptor simulationDescriptor = toReturn.getSimulationDescriptor();

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

        // Add EXPECTED Facts
        IntStream.range(2, 4).forEach(id -> {
            id += 2; // This is to have consistent labels/names even when adding columns at runtime
            ExpressionIdentifier expectedExpression = ExpressionIdentifier.create(row + "|" + id, FactMappingType.EXPECTED);
            simulationDescriptor.addFactMapping(FactMapping.getInstancePlaceHolder(id), FactIdentifier.EMPTY, expectedExpression);
            scenario.addMappingValue(FactIdentifier.EMPTY, expectedExpression, null);
        });
        return toReturn;
    }
}