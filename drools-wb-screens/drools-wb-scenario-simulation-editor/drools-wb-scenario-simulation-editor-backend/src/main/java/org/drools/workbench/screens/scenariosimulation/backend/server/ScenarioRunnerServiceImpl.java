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
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.drools.workbench.screens.scenariosimulation.backend.server.runner.AbstractScenarioRunner;
import org.drools.workbench.screens.scenariosimulation.backend.server.runner.ScenarioRunnerProvider;
import org.drools.workbench.screens.scenariosimulation.model.Scenario;
import org.drools.workbench.screens.scenariosimulation.model.ScenarioSimulationModel;
import org.drools.workbench.screens.scenariosimulation.model.SimulationDescriptor;
import org.drools.workbench.screens.scenariosimulation.model.TestRunResult;
import org.drools.workbench.screens.scenariosimulation.service.ScenarioRunnerService;
import org.guvnor.common.services.shared.test.Failure;
import org.guvnor.common.services.shared.test.TestResultMessage;
import org.jboss.errai.bus.server.annotations.Service;
import org.junit.runner.Result;
import org.junit.runner.Runner;
import org.kie.api.runtime.KieContainer;
import org.uberfire.backend.vfs.Path;

import static org.drools.workbench.screens.scenariosimulation.backend.server.util.JunitRunnerHelper.runWithJunit;

@Service
@ApplicationScoped
public class ScenarioRunnerServiceImpl extends AbstractKieContainerService
        implements ScenarioRunnerService {

    @Inject
    private ScenarioLoader scenarioLoader;

    private ScenarioRunnerProvider runnerSupplier = null;

    @Override
    public List<TestResultMessage> runAllTests(final String identifier,
                                               final Path path) {
        final List<TestResultMessage> testResultMessages = new ArrayList<>();

        for (Map.Entry<Path, ScenarioSimulationModel> entry : scenarioLoader.loadScenarios(path).entrySet()) {

            final ScenarioSimulationModel scenarioSimulationModel = entry.getValue();

            testResultMessages.add(runTest(identifier,
                                           entry.getKey(),
                                           scenarioSimulationModel.getSimulation().getSimulationDescriptor(),
                                           scenarioSimulationModel.getSimulation().getScenarioMap()).getTestResultMessage());
        }

        return testResultMessages;
    }

    @Override
    public TestRunResult runTest(final String identifier,
                                 final Path path,
                                 final SimulationDescriptor simulationDescriptor,
                                 final Map<Integer, Scenario> scenarioMap) {
        final KieContainer kieContainer = getKieContainer(path);
        final Runner scenarioRunner = getOrCreateRunnerSupplier(simulationDescriptor)
                .create(kieContainer, simulationDescriptor, scenarioMap);

        final List<Failure> failures = new ArrayList<>();

        final List<Failure> failureDetails = new ArrayList<>();

        final Result result = runWithJunit(scenarioRunner, failures, failureDetails);

        return new TestRunResult(scenarioMap,
                                 new TestResultMessage(
                                         identifier,
                                         result.getRunCount(),
                                         result.getRunTime(),
                                         failures));
    }

    public ScenarioRunnerProvider getOrCreateRunnerSupplier(SimulationDescriptor simulationDescriptor) {
        if (runnerSupplier != null) {
            return runnerSupplier;
        }
        return AbstractScenarioRunner.getSpecificRunnerProvider(simulationDescriptor);
    }

    public void setRunnerSupplier(ScenarioRunnerProvider runnerSupplier) {
        this.runnerSupplier = runnerSupplier;
    }
}
