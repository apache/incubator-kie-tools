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
package org.drools.workbench.screens.scenariosimulation.backend.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.drools.scenariosimulation.api.model.Scenario;
import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.drools.scenariosimulation.api.model.ScenarioWithIndex;
import org.drools.scenariosimulation.api.model.Simulation;
import org.drools.scenariosimulation.api.model.SimulationDescriptor;
import org.drools.scenariosimulation.backend.runner.AbstractScenarioRunner;
import org.drools.scenariosimulation.backend.runner.RuleScenarioRunner;
import org.drools.scenariosimulation.backend.runner.ScenarioException;
import org.drools.scenariosimulation.backend.runner.model.ScenarioRunnerData;
import org.drools.workbench.screens.scenariosimulation.model.SimulationRunResult;
import org.guvnor.common.services.shared.test.TestResultMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.runtime.KieContainer;
import org.kie.workbench.common.services.backend.builder.service.BuildInfo;
import org.kie.workbench.common.services.backend.builder.service.BuildInfoService;
import org.kie.workbench.common.services.backend.project.ModuleClassLoaderHelper;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ScenarioRunnerServiceImplTest {

    @Mock
    ScenarioLoader scenarioLoader;
    @Mock
    private AbstractScenarioRunner runnerMock;
    @Mock
    private KieModuleService moduleServiceMock;
    @Mock
    private BuildInfoService buildInfoServiceMock;
    @Mock
    private BuildInfo buildInfoMock;
    @Mock
    private KieContainer kieContainerMock;
    @Mock
    private ModuleClassLoaderHelper classLoaderHelperMock;

    @InjectMocks
    private ScenarioRunnerServiceImpl scenarioRunnerService = new ScenarioRunnerServiceImpl();

    @Before
    public void setup() {
        when(classLoaderHelperMock.getModuleClassLoader(any())).thenReturn(ClassLoader.getSystemClassLoader());
    }

    @Test
    public void runAllTests() throws Exception {
        List<TestResultMessage> testResultMessages = scenarioRunnerService.runAllTests("test", mock(Path.class));

        assertNotNull(testResultMessages);
    }

    @Test
    public void runTest() throws Exception {
        when(buildInfoServiceMock.getBuildInfo(any())).thenReturn(buildInfoMock);
        when(buildInfoMock.getKieContainer()).thenReturn(kieContainerMock);
        Simulation simulation = new Simulation();
        simulation.getSimulationDescriptor().setType(ScenarioSimulationModel.Type.RULE);

        SimulationRunResult test = scenarioRunnerService.runTest("test",
                                                                 mock(Path.class),
                                                                 simulation.getSimulationDescriptor(),
                                                                 simulation.getScenarioWithIndex());

        assertNotNull(test.getTestResultMessage());
        assertNotNull(test.getScenarioWithIndex());
        assertNotNull(test.getSimulationRunMetadata());

        when(runnerMock.getLastRunResultMetadata()).thenReturn(Optional.empty());
        scenarioRunnerService.setRunnerSupplier((kieContainer, simulationDescriptor, scenarios) -> runnerMock);

        assertThatThrownBy(() -> scenarioRunnerService.runTest("test",
                                                               mock(Path.class),
                                                               simulation.getSimulationDescriptor(),
                                                               simulation.getScenarioWithIndex()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("SimulationRunMetadata should be available after a run");
    }

    @Test
    public void runTestWithScenarios() throws Exception {
        when(buildInfoServiceMock.getBuildInfo(any())).thenReturn(buildInfoMock);
        when(buildInfoMock.getKieContainer()).thenReturn(kieContainerMock);
        SimulationDescriptor simulationDescriptor = new SimulationDescriptor();
        simulationDescriptor.setType(ScenarioSimulationModel.Type.RULE);
        List<ScenarioWithIndex> scenarios = new ArrayList<>();

        SimulationRunResult test = scenarioRunnerService.runTest("test",
                                                                 mock(Path.class),
                                                                 simulationDescriptor,
                                                                 scenarios);

        assertNotNull(test.getTestResultMessage());
        assertNotNull(test.getScenarioWithIndex());
        assertNotNull(test.getSimulationRunMetadata());
    }

    @Test
    public void runFailed() throws Exception {
        when(buildInfoServiceMock.getBuildInfo(any())).thenReturn(buildInfoMock);
        when(buildInfoMock.getKieContainer()).thenReturn(kieContainerMock);
        Simulation simulation = new Simulation();
        simulation.addScenario();
        Scenario scenario = simulation.getScenarioByIndex(0);
        scenario.setDescription("Test Scenario");
        String errorMessage = "Test Error";

        scenarioRunnerService.setRunnerSupplier(
                (kieContainer, simulationDescriptor, scenarios) ->
                        new RuleScenarioRunner(kieContainer, simulationDescriptor, scenarios, "") {

                            @Override
                            protected void internalRunScenario(ScenarioWithIndex scenarioWithIndex, ScenarioRunnerData scenarioRunnerData) {
                                throw new ScenarioException(errorMessage);
                            }
                        });
        SimulationRunResult test = scenarioRunnerService.runTest("test",
                                                                 mock(Path.class),
                                                                 simulation.getSimulationDescriptor(),
                                                                 simulation.getScenarioWithIndex());
        TestResultMessage value = test.getTestResultMessage();
        List<org.guvnor.common.services.shared.test.Failure> failures = value.getFailures();
        assertEquals(1, failures.size());

        String testDescription = String.format("#%d: %s", 1, scenario.getDescription());
        String errorMessageFormatted = String.format("#%d: %s()", 1, errorMessage);
        org.guvnor.common.services.shared.test.Failure failure = failures.get(0);
        assertEquals(errorMessageFormatted, failure.getMessage());
        assertEquals(1, value.getRunCount());
        assertTrue(failure.getDisplayName().startsWith(testDescription));
    }

    @Test
    public void kieContainerTest() {
        when(buildInfoServiceMock.getBuildInfo(any())).thenReturn(buildInfoMock);
        when(buildInfoMock.getKieContainer()).thenReturn(null);
        assertThatThrownBy(() -> scenarioRunnerService.getKieContainer(mock(Path.class)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Retrieving KieContainer has failed. Fix all compilation errors within the " +
                                    "project and build the project again.");
    }
}