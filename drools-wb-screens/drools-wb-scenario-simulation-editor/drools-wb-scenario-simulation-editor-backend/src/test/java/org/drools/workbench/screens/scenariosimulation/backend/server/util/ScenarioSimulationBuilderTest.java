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

package org.drools.workbench.screens.scenariosimulation.backend.server.util;

import org.drools.scenariosimulation.api.model.Background;
import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.drools.scenariosimulation.api.model.Settings;
import org.drools.scenariosimulation.api.model.Simulation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class ScenarioSimulationBuilderTest {

    private final String VALUE = "VALUE";
    @Mock
    private Simulation dmnSimulationMock;
    @Mock
    private Simulation ruleSimulationMock;
    @Mock
    private Background dmnBackgroundMock;
    @Mock
    private Background ruleBackgroundMock;
    @Mock
    private Settings dmnSettingsMock;
    @Mock
    private Settings ruleSettingsMock;
    @Mock
    private RULESimulationSettingsCreationStrategy ruleSimulationCreationStrategyMock;
    @Mock
    private DMNSimulationSettingsCreationStrategy dmnSimulationCreationStrategyMock;
    @Mock
    private Path contextMock;
    private ScenarioSimulationBuilder scenarioSimulationBuilder;

    @Before
    public void setup() throws Exception {
        scenarioSimulationBuilder = new ScenarioSimulationBuilder();
        scenarioSimulationBuilder.dmnSimulationCreationStrategy = dmnSimulationCreationStrategyMock;
        scenarioSimulationBuilder.ruleSimulationCreationStrategy = ruleSimulationCreationStrategyMock;
        doReturn(ruleBackgroundMock).when(ruleSimulationCreationStrategyMock).createBackground(eq(contextMock), eq(VALUE));
        doReturn(dmnBackgroundMock).when(dmnSimulationCreationStrategyMock).createBackground(eq(contextMock), eq(VALUE));
        doReturn(ruleSimulationMock).when(ruleSimulationCreationStrategyMock).createSimulation(eq(contextMock), eq(VALUE));
        doReturn(dmnSimulationMock).when(dmnSimulationCreationStrategyMock).createSimulation(eq(contextMock), eq(VALUE));
        doReturn(ruleSettingsMock).when(ruleSimulationCreationStrategyMock).createSettings(eq(contextMock), eq(VALUE));
        doReturn(dmnSettingsMock).when(dmnSimulationCreationStrategyMock).createSettings(eq(contextMock), eq(VALUE));
    }

    @Test
    public void createSimulation() throws Exception {
        Simulation simulationRetrieved = scenarioSimulationBuilder.createSimulation(contextMock, ScenarioSimulationModel.Type.RULE, VALUE);
        assertNotNull(simulationRetrieved);
        assertEquals(ruleSimulationMock, simulationRetrieved);
        simulationRetrieved = scenarioSimulationBuilder.createSimulation(contextMock, ScenarioSimulationModel.Type.DMN, VALUE);
        assertNotNull(simulationRetrieved);
        assertEquals(dmnSimulationMock, simulationRetrieved);
    }

    @Test
    public void createBackground() throws Exception {
        Background backgroundRetrieved = scenarioSimulationBuilder.createBackground(contextMock, ScenarioSimulationModel.Type.RULE, VALUE);
        assertNotNull(backgroundRetrieved);
        assertEquals(ruleBackgroundMock, backgroundRetrieved);
        backgroundRetrieved = scenarioSimulationBuilder.createBackground(contextMock, ScenarioSimulationModel.Type.DMN, VALUE);
        assertNotNull(backgroundRetrieved);
        assertEquals(dmnBackgroundMock, backgroundRetrieved);
    }

    @Test
    public void createSettings() throws Exception {
        Settings settingsRetrieved = scenarioSimulationBuilder.createSettings(contextMock, ScenarioSimulationModel.Type.RULE, VALUE);
        assertNotNull(settingsRetrieved);
        assertEquals(ruleSettingsMock, settingsRetrieved);
        settingsRetrieved = scenarioSimulationBuilder.createSettings(contextMock, ScenarioSimulationModel.Type.DMN, VALUE);
        assertNotNull(settingsRetrieved);
        assertEquals(dmnSettingsMock, settingsRetrieved);
    }
}