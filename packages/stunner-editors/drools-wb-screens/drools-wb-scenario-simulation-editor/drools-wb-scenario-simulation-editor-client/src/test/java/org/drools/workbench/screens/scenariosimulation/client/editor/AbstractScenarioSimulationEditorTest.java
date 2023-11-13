/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.drools.workbench.screens.scenariosimulation.client.editor;

import org.drools.scenariosimulation.api.model.ExpressionIdentifier;
import org.drools.scenariosimulation.api.model.FactIdentifier;
import org.drools.scenariosimulation.api.model.FactMapping;
import org.drools.scenariosimulation.api.model.FactMappingType;
import org.drools.scenariosimulation.api.model.Scenario;
import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.drools.scenariosimulation.api.model.ScesimModelDescriptor;
import org.drools.scenariosimulation.api.model.Simulation;
import org.drools.workbench.screens.scenariosimulation.client.AbstractScenarioSimulationTest;
import org.drools.workbench.screens.scenariosimulation.client.editor.menu.BaseMenuView;
import org.drools.workbench.screens.scenariosimulation.client.editor.menu.GridContextMenu;
import org.drools.workbench.screens.scenariosimulation.client.editor.menu.HeaderGivenContextMenu;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.CheatSheetPresenter;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.SettingsPresenter;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.TestToolsPresenter;
import org.drools.workbench.screens.scenariosimulation.service.DMNTypeService;
import org.mockito.Mock;
import org.uberfire.backend.vfs.ObservablePath;

import static org.mockito.Mockito.when;

public abstract class AbstractScenarioSimulationEditorTest extends AbstractScenarioSimulationTest {

    protected static final String SCENARIO_PACKAGE = "scenario.package";

    @Mock
    protected DMNTypeService dmnTypeServiceMock;
    @Mock
    protected ObservablePath observablePathMock;
    @Mock
    protected GridContextMenu gridContextMenuMock;
    @Mock
    protected HeaderGivenContextMenu headerGivenContextMenuMock;
    @Mock
    protected BaseMenuView gridContextMenuViewMock;
    @Mock
    protected BaseMenuView headerContextMenuViewMock;
    @Mock
    protected TestToolsPresenter testToolsPresenterMock;
    @Mock
    protected CheatSheetPresenter cheatSheetPresenterMock;
    @Mock
    protected SettingsPresenter settingsPresenterMock;

    protected ScenarioSimulationModel modelLocal;

    public void setup() {
        super.setup();
        when(gridContextMenuMock.getView()).thenReturn(gridContextMenuViewMock);
        when(headerGivenContextMenuMock.getView()).thenReturn(headerContextMenuViewMock);
        this.modelLocal = new ScenarioSimulationModel();
        modelLocal.setSimulation(getSimulation());
        modelLocal.setSettings(settingsLocal);
        settingsLocal.setType(ScenarioSimulationModel.Type.RULE);
        settingsLocal.setDmoSession(null);
        modelLocal.setBackground(backgroundLocal);
    }

    protected Simulation getSimulation() {
        Simulation toReturn = new Simulation();
        ScesimModelDescriptor simulationDescriptor = toReturn.getScesimModelDescriptor();
        simulationDescriptor.addFactMapping(FactIdentifier.INDEX.getName(), FactIdentifier.INDEX, ExpressionIdentifier.INDEX);
        simulationDescriptor.addFactMapping(FactIdentifier.DESCRIPTION.getName(), FactIdentifier.DESCRIPTION, ExpressionIdentifier.DESCRIPTION);

        Scenario scenario = toReturn.addData();
        int row = toReturn.getUnmodifiableData().indexOf(scenario);
        scenario.setDescription(null);

        // Add GIVEN Fact
        int id = 1;
        ExpressionIdentifier givenExpression = ExpressionIdentifier.create(row + "|" + id, FactMappingType.GIVEN);
        final FactMapping givenFactMapping = simulationDescriptor.addFactMapping(FactMapping.getInstancePlaceHolder(id), FactIdentifier.EMPTY, givenExpression);
        givenFactMapping.setExpressionAlias(FactMapping.getPropertyPlaceHolder(id));
        scenario.addMappingValue(FactIdentifier.EMPTY, givenExpression, null);

        // Add EXPECT Fact
        id = 2;
        ExpressionIdentifier expectedExpression = ExpressionIdentifier.create(row + "|" + id, FactMappingType.EXPECT);
        final FactMapping expectedFactMapping = simulationDescriptor.addFactMapping(FactMapping.getInstancePlaceHolder(id), FactIdentifier.EMPTY, expectedExpression);
        expectedFactMapping.setExpressionAlias(FactMapping.getPropertyPlaceHolder(id));
        scenario.addMappingValue(FactIdentifier.EMPTY, expectedExpression, null);
        return toReturn;
    }
}
