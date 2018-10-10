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
import org.drools.workbench.screens.scenariosimulation.client.models.ScenarioGridModel;
import org.drools.workbench.screens.scenariosimulation.client.renderers.ScenarioGridRenderer;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.drools.workbench.screens.scenariosimulation.client.utils.ScenarioSimulationBuilders;
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

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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

    private final FactMappingType factMappingType = FactMappingType.valueOf("OTHER");
    private final String EXPRESSION_ALIAS = "EXPRESSION_ALIAS";

    private FactMapping factMapping;

    private Simulation simulation = new Simulation();

    private final int COLUMNS = 6;

    private ScenarioGrid scenarioGrid;

    @Before
    public void setup() {
        factMapping = new FactMapping(EXPRESSION_ALIAS, FactIdentifier.DESCRIPTION, ExpressionIdentifier.DESCRIPTION);
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
            protected ScenarioSimulationBuilders.HeaderBuilder getHeaderBuilderLocal(String columnTitle, String columnId, String columnGroup, FactMappingType factMappingType, ScenarioHeaderTextBoxSingletonDOMElementFactory factoryHeader) {
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
        scenarioGrid.setHeaderColumn(1, factMapping);
        verify(scenarioGrid, times(1)).getScenarioHeaderTextBoxSingletonDOMElementFactory();
        verify(scenarioGrid, times(1)).getHeaderBuilderLocal(eq(factMapping.getExpressionAlias()),
                                                             eq(factMapping.getExpressionIdentifier().getName()),
                                                             eq(factMapping.getExpressionIdentifier().getType().name()),
                                                             eq(factMapping.getExpressionIdentifier().getType()),
                                                             eq(scenarioHeaderTextBoxSingletonDOMElementFactoryMock));
        verify(scenarioGrid, times(1)).getScenarioGridColumnLocal(eq(headerBuilderMock),
                                                             eq(false),
                                                             eq(ScenarioSimulationEditorConstants.INSTANCE.insertValue()));
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
            simulationDescriptor.addFactMapping(FactMapping.getPlaceHolder(FactMappingType.GIVEN, id), FactIdentifier.EMPTY, givenExpression);
            scenario.addMappingValue(FactIdentifier.EMPTY, givenExpression, null);
        });

        // Add EXPECTED Facts
        IntStream.range(2, 4).forEach(id -> {
            id += 2; // This is to have consistent labels/names even when adding columns at runtime
            ExpressionIdentifier expectedExpression = ExpressionIdentifier.create(row + "|" + id, FactMappingType.EXPECTED);
            simulationDescriptor.addFactMapping(FactMapping.getPlaceHolder(FactMappingType.EXPECTED, id), FactIdentifier.EMPTY, expectedExpression);
            scenario.addMappingValue(FactIdentifier.EMPTY, expectedExpression, null);
        });
        return toReturn;
    }
}