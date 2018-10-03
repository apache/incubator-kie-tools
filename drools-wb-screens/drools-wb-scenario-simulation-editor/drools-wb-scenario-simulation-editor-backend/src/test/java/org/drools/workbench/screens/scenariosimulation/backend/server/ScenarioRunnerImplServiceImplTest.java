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

import org.drools.workbench.screens.scenariosimulation.backend.server.runner.AbstractScenarioRunner;
import org.drools.workbench.screens.scenariosimulation.model.ScenarioSimulationModel;
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
import org.uberfire.mocks.EventSourceMock;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ScenarioRunnerImplServiceImplTest {

    @Mock
    private EventSourceMock<TestResultMessage> defaultTestResultMessageEvent;

    @Mock
    private AbstractScenarioRunner runnerMock;

    @Mock
    private KieModuleService moduleService;

    @Mock
    private BuildInfoService buildInfoService;

    @Mock
    private BuildInfo buildInfo;

    @Mock
    private KieContainer kieContainer;

    @Mock
    private ModuleClassLoaderHelper classLoaderHelper;

    @InjectMocks
    private ScenarioRunnerServiceImpl scenarioRunnerService = new ScenarioRunnerServiceImpl();

    @Before
    public void setup() {
        when(classLoaderHelper.getModuleClassLoader(any())).thenReturn(ClassLoader.getSystemClassLoader());
    }

    @Test
    public void runAllTests() throws Exception {
        scenarioRunnerService.runAllTests("test", mock(Path.class));

        verify(defaultTestResultMessageEvent).fire(any());
    }

    @Test
    public void runTest() throws Exception {
        when(buildInfoService.getBuildInfo(any())).thenReturn(buildInfo);
        when(buildInfo.getKieContainer()).thenReturn(kieContainer);
        scenarioRunnerService.runTest("test", mock(Path.class), new ScenarioSimulationModel());

        verify(defaultTestResultMessageEvent).fire(any());
    }

    @Test
    public void runAllTestsSpecifiedEvent() throws Exception {
        final EventSourceMock customTestResultEvent = mock(EventSourceMock.class);

        scenarioRunnerService.setRunnerSupplier((kieContainer, simulation) -> runnerMock);

        scenarioRunnerService.runAllTests("test", mock(Path.class), customTestResultEvent);

        verify(defaultTestResultMessageEvent, never()).fire(any());
        verify(customTestResultEvent).fire(any());
    }
}