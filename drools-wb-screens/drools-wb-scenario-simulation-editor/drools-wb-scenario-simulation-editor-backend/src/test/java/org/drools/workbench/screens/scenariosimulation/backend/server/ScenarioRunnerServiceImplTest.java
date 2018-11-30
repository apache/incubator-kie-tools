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

import java.util.Collections;
import java.util.List;

import org.drools.workbench.screens.scenariosimulation.backend.server.runner.IndexedScenarioException;
import org.drools.workbench.screens.scenariosimulation.backend.server.runner.ScenarioRunnerImpl;
import org.drools.workbench.screens.scenariosimulation.backend.server.runner.model.ScenarioResult;
import org.drools.workbench.screens.scenariosimulation.model.Scenario;
import org.drools.workbench.screens.scenariosimulation.model.ScenarioSimulationModel;
import org.drools.workbench.screens.scenariosimulation.model.Simulation;
import org.guvnor.common.services.shared.test.TestResultMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.kie.api.runtime.KieContainer;
import org.kie.workbench.common.services.backend.builder.service.BuildInfo;
import org.kie.workbench.common.services.backend.builder.service.BuildInfoService;
import org.kie.workbench.common.services.backend.project.ModuleClassLoaderHelper;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ScenarioRunnerServiceImplTest {

    @Mock
    private EventSourceMock<TestResultMessage> defaultTestResultMessageEventMock;

    @Mock
    private Runner runnerMock;

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

    @Captor
    private ArgumentCaptor<TestResultMessage> testResultMessageArgumentCaptor;

    @InjectMocks
    private ScenarioRunnerServiceImpl scenarioRunnerService = new ScenarioRunnerServiceImpl();

    @Before
    public void setup() {
        when(classLoaderHelperMock.getModuleClassLoader(any())).thenReturn(ClassLoader.getSystemClassLoader());
    }

    @Test
    public void runAllTests() throws Exception {
        scenarioRunnerService.runAllTests("test", mock(Path.class));

        verify(defaultTestResultMessageEventMock).fire(any());
    }

    @Test
    public void runTest() throws Exception {
        when(buildInfoServiceMock.getBuildInfo(any())).thenReturn(buildInfoMock);
        when(buildInfoMock.getKieContainer()).thenReturn(kieContainerMock);
        final ScenarioSimulationModel scenarioSimulationModel = mock(ScenarioSimulationModel.class);
        when(scenarioSimulationModel.getSimulation()).thenReturn(mock(Simulation.class));
        scenarioRunnerService.runTest("test", mock(Path.class), scenarioSimulationModel);

        verify(defaultTestResultMessageEventMock).fire(any());
    }

    @Test
    public void runAllTestsSpecifiedEvent() throws Exception {
        final EventSourceMock customTestResultEvent = mock(EventSourceMock.class);

        scenarioRunnerService.setRunnerSupplier((kieContainer, simulation) -> runnerMock);

        scenarioRunnerService.runAllTests("test", mock(Path.class), customTestResultEvent);

        verify(defaultTestResultMessageEventMock, never()).fire(any());
        verify(customTestResultEvent).fire(any());
    }

    @Test
    public void runFailed() throws Exception {
        when(buildInfoServiceMock.getBuildInfo(any())).thenReturn(buildInfoMock);
        when(buildInfoMock.getKieContainer()).thenReturn(kieContainerMock);
        ScenarioSimulationModel scenarioSimulationModel = new ScenarioSimulationModel();
        Scenario scenario = scenarioSimulationModel.getSimulation().getScenarioByIndex(0);
        scenario.setDescription("Test Scenario");
        String errorMessage = "Test Error";

        scenarioRunnerService.setRunnerSupplier((kieContainer, simulation) -> new ScenarioRunnerImpl(kieContainer, simulation) {

            @Override
            protected List<ScenarioResult> internalRunScenario(int index, Scenario scenario, RunNotifier runNotifier) {

                runNotifier.fireTestStarted(getDescriptionForScenario(getFileName(), index, scenario));

                runNotifier.fireTestFailure(new Failure(getDescriptionForScenario(getFileName(), index, scenario),
                                                        new IndexedScenarioException(index, errorMessage)));
                runNotifier.fireTestFinished(getDescriptionForScenario(getFileName(), index, scenario));
                return Collections.emptyList();
            }
        });
        scenarioRunnerService.runTest("test", mock(Path.class), scenarioSimulationModel);
        verify(defaultTestResultMessageEventMock, times(1)).fire(testResultMessageArgumentCaptor.capture());
        TestResultMessage value = testResultMessageArgumentCaptor.getValue();
        List<org.guvnor.common.services.shared.test.Failure> failures = value.getFailures();
        assertEquals(1, failures.size());

        String testDescription = String.format("#%d: %s", 1, scenario.getDescription());
        String errorMessageFormatted = String.format("#%d: %s", 1, errorMessage);
        org.guvnor.common.services.shared.test.Failure failure = failures.get(0);
        assertEquals(errorMessageFormatted, failure.getMessage());
        assertEquals(1, value.getRunCount());
        assertTrue(failure.getDisplayName().startsWith(testDescription));
    }
}