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

import org.drools.scenariosimulation.api.model.Background;
import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.drools.scenariosimulation.api.model.ScenarioWithIndex;
import org.drools.scenariosimulation.api.model.ScesimModelDescriptor;
import org.drools.scenariosimulation.api.model.Settings;
import org.drools.scenariosimulation.api.model.Simulation;
import org.drools.scenariosimulation.backend.runner.AbstractScenarioRunner;
import org.drools.scenariosimulation.backend.runner.ScenarioRunnerProvider;
import org.drools.scenariosimulation.backend.runner.model.ScenarioRunnerDTO;
import org.drools.workbench.screens.scenariosimulation.model.SimulationRunResult;
import org.drools.workbench.screens.scenariosimulation.service.ScenarioRunnerService;
import org.guvnor.common.services.shared.test.Failure;
import org.guvnor.common.services.shared.test.TestResultMessage;
import org.jboss.errai.bus.server.annotations.Service;
import org.junit.runner.Result;
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

            final Simulation simulation = entry.getValue().getSimulation();
            final Settings settings = entry.getValue().getSettings();
            if (!settings.isSkipFromBuild()) {

                testResultMessages.add(runTest(identifier,
                                               entry.getKey(),
                                               simulation.getScesimModelDescriptor(),
                                               simulation.getScenarioWithIndex(),
                                               settings,
                                               entry.getValue().getBackground())
                                               .getTestResultMessage());
            }
        }

        return testResultMessages;
    }

    @Override
    public SimulationRunResult runTest(final String identifier,
                                       final Path path,
                                       final ScesimModelDescriptor simulationDescriptor,
                                       final List<ScenarioWithIndex> scenarios,
                                       final Settings settings,
                                       final Background background) {
        final KieContainer kieContainer = getKieContainer(path);
        final ScenarioRunnerDTO scenarioRunnerDTO = new ScenarioRunnerDTO(simulationDescriptor, scenarios, null, settings, background);
        final AbstractScenarioRunner scenarioRunner = getOrCreateRunnerSupplier(settings.getType())
                .create(kieContainer, scenarioRunnerDTO);

        final List<Failure> failures = new ArrayList<>();

        final List<Failure> failureDetails = new ArrayList<>();

        final Result result = runWithJunit(path, scenarioRunner, failures, failureDetails);

        return new SimulationRunResult(scenarios,
                                       background.getBackgroundDataWithIndex(),
                                       scenarioRunner.getLastRunResultMetadata()
                                               .orElseThrow(() -> new IllegalStateException("SimulationRunMetadata should be available after a run")),
                                       new TestResultMessage(
                                               identifier,
                                               result.getRunCount(),
                                               result.getRunTime(),
                                               failures));
    }

    public ScenarioRunnerProvider getOrCreateRunnerSupplier(ScenarioSimulationModel.Type type) {
        if (runnerSupplier != null) {
            return runnerSupplier;
        }
        return AbstractScenarioRunner.getSpecificRunnerProvider(type);
    }

    public void setRunnerSupplier(ScenarioRunnerProvider runnerSupplier) {
        this.runnerSupplier = runnerSupplier;
    }
}
