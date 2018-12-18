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
import java.util.function.BiFunction;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.drools.workbench.screens.scenariosimulation.backend.server.runner.AbstractScenarioRunner;
import org.drools.workbench.screens.scenariosimulation.model.ScenarioSimulationModel;
import org.drools.workbench.screens.scenariosimulation.model.Simulation;
import org.drools.workbench.screens.scenariosimulation.service.ScenarioRunnerService;
import org.guvnor.common.services.shared.test.Failure;
import org.guvnor.common.services.shared.test.TestResultMessage;
import org.jboss.errai.bus.server.annotations.Service;
import org.junit.runner.Result;
import org.junit.runner.Runner;
import org.kie.api.runtime.KieContainer;
import org.kie.workbench.common.services.backend.builder.service.BuildInfoService;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.uberfire.backend.vfs.Path;

import static org.drools.workbench.screens.scenariosimulation.backend.server.util.JunitRunnerHelper.runWithJunit;

@Service
@ApplicationScoped
public class ScenarioRunnerServiceImpl
        implements ScenarioRunnerService {

    @Inject
    private Event<TestResultMessage> defaultTestResultMessageEvent;

    @Inject
    private KieModuleService moduleService;

    @Inject
    private BuildInfoService buildInfoService;

    private BiFunction<KieContainer, Simulation, AbstractScenarioRunner> runnerSupplier = null;

    @Override
    public void runAllTests(final String identifier,
                            final Path path) {

        defaultTestResultMessageEvent.fire(
                new TestResultMessage(
                        identifier,
                        1,
                        1,
                        new ArrayList<>()));
    }

    @Override
    public void runAllTests(final String identifier,
                            final Path path,
                            final Event<TestResultMessage> customTestResultEvent) {

        customTestResultEvent.fire(
                new TestResultMessage(
                        identifier,
                        1,
                        1,
                        new ArrayList<>()));
    }

    @Override
    public ScenarioSimulationModel runTest(final String identifier,
                                           final Path path,
                                           final ScenarioSimulationModel model) {

        KieModule kieModule = getKieModule(path);
        KieContainer kieContainer = getKieContainer(kieModule);
        Runner scenarioRunner = getOrCreateRunnerSupplier(model.getSimulation())
                .apply(kieContainer, model.getSimulation());

        final List<Failure> failures = new ArrayList<>();

        final List<Failure> failureDetails = new ArrayList<>();

        Result result = runWithJunit(scenarioRunner, failures, failureDetails);

        defaultTestResultMessageEvent.fire(
                new TestResultMessage(
                        identifier,
                        result.getRunCount(),
                        result.getRunTime(),
                        failures));

        return model;
    }

    protected KieModule getKieModule(Path path) {
        return moduleService.resolveModule(path);
    }

    protected KieContainer getKieContainer(KieModule kieModule) {
        return buildInfoService.getBuildInfo(kieModule).getKieContainer();
    }

    public BiFunction<KieContainer, Simulation, AbstractScenarioRunner> getOrCreateRunnerSupplier(Simulation simulation) {
        if (runnerSupplier != null) {
            return runnerSupplier;
        }
        return AbstractScenarioRunner.getSpecificRunnerProvider(simulation);
    }

    public void setRunnerSupplier(BiFunction<KieContainer, Simulation, AbstractScenarioRunner> runnerSupplier) {
        this.runnerSupplier = runnerSupplier;
    }
}
