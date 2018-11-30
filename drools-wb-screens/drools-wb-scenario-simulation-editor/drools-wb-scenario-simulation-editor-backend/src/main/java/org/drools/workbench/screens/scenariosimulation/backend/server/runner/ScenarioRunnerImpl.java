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

package org.drools.workbench.screens.scenariosimulation.backend.server.runner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.drools.workbench.screens.scenariosimulation.backend.server.expression.BaseExpressionEvaluator;
import org.drools.workbench.screens.scenariosimulation.backend.server.expression.ExpressionEvaluator;
import org.drools.workbench.screens.scenariosimulation.backend.server.runner.model.ScenarioResult;
import org.drools.workbench.screens.scenariosimulation.backend.server.runner.model.ScenarioRunnerData;
import org.drools.workbench.screens.scenariosimulation.model.Scenario;
import org.drools.workbench.screens.scenariosimulation.model.Simulation;
import org.drools.workbench.screens.scenariosimulation.model.SimulationDescriptor;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.kie.api.runtime.KieContainer;

import static org.drools.workbench.screens.scenariosimulation.backend.server.runner.ScenarioRunnerHelper.executeScenario;
import static org.drools.workbench.screens.scenariosimulation.backend.server.runner.ScenarioRunnerHelper.extractExpectedValues;
import static org.drools.workbench.screens.scenariosimulation.backend.server.runner.ScenarioRunnerHelper.extractGivenValues;
import static org.drools.workbench.screens.scenariosimulation.backend.server.runner.ScenarioRunnerHelper.validateAssertion;
import static org.drools.workbench.screens.scenariosimulation.backend.server.runner.ScenarioRunnerHelper.verifyConditions;

public class ScenarioRunnerImpl extends Runner {

    private final ClassLoader classLoader;
    private Function<ClassLoader, ExpressionEvaluator> expressionEvaluatorFactory = BaseExpressionEvaluator::new;
    private final Description desc;
    private final KieContainer kieContainer;
    private final SimulationDescriptor simulationDescriptor;
    private Map<Integer, Scenario> scenarios;
    private String fileName;

    public ScenarioRunnerImpl(KieContainer kieContainer, Simulation simulation) {
        this(kieContainer, simulation.getSimulationDescriptor(), toScenarioMap(simulation), null);
    }

    public ScenarioRunnerImpl(KieContainer kieContainer, Simulation simulation, String fileName) {
        this(kieContainer, simulation.getSimulationDescriptor(), toScenarioMap(simulation), fileName);
    }

    public ScenarioRunnerImpl(KieContainer kieContainer, SimulationDescriptor simulationDescriptor, Map<Integer, Scenario> scenarios, String fileName) {
        this.kieContainer = kieContainer;
        this.simulationDescriptor = simulationDescriptor;
        this.scenarios = scenarios;
        this.fileName = fileName;
        this.desc = getDescriptionForSimulation(getFileName(), simulationDescriptor, scenarios);
        this.classLoader = kieContainer.getClassLoader();
    }

    @Override
    public void run(RunNotifier notifier) {

        notifier.fireTestStarted(getDescription());
        for (Map.Entry<Integer, Scenario> integerScenarioEntry : scenarios.entrySet()) {
            Scenario scenario = integerScenarioEntry.getValue();
            Integer index = integerScenarioEntry.getKey();
            internalRunScenario(index, scenario, notifier);
        }
        notifier.fireTestStarted(getDescription());
    }

    @Override
    public Description getDescription() {
        return this.desc;
    }

    protected List<ScenarioResult> internalRunScenario(int index, Scenario scenario, RunNotifier runNotifier) {
        ScenarioRunnerData scenarioRunnerData = new ScenarioRunnerData();

        Description descriptionForScenario = getDescriptionForScenario(getFileName(), index, scenario);
        runNotifier.fireTestStarted(descriptionForScenario);

        try {
            ExpressionEvaluator expressionEvaluator = createExpressionEvaluator();
            extractGivenValues(simulationDescriptor, scenario.getUnmodifiableFactMappingValues(), classLoader, expressionEvaluator)
                    .forEach(scenarioRunnerData::addInput);
            extractExpectedValues(scenario.getUnmodifiableFactMappingValues()).forEach(scenarioRunnerData::addOutput);

            executeScenario(kieContainer,
                            scenarioRunnerData,
                            expressionEvaluator,
                            simulationDescriptor);

            verifyConditions(simulationDescriptor,
                             scenarioRunnerData,
                             expressionEvaluator);
            validateAssertion(scenarioRunnerData.getResultData(),
                              scenario);
        } catch (ScenarioException e) {
            IndexedScenarioException indexedScenarioException = new IndexedScenarioException(index, e);
            indexedScenarioException.setFileName(fileName);
            runNotifier.fireTestFailure(new Failure(descriptionForScenario, indexedScenarioException));
        } catch (Throwable e) {
            IndexedScenarioException indexedScenarioException = new IndexedScenarioException(index, new StringBuilder().append("Unexpected test error in scenario '")
                    .append(scenario.getDescription()).append("'").toString(), e);
            indexedScenarioException.setFileName(fileName);
            runNotifier.fireTestFailure(new Failure(descriptionForScenario, indexedScenarioException));
        }
        
        runNotifier.fireTestFinished(descriptionForScenario);

        return scenarioRunnerData.getResultData();
    }

    public ExpressionEvaluator createExpressionEvaluator() {
        return expressionEvaluatorFactory.apply(classLoader);
    }

    public void setExpressionEvaluatorFactory(Function<ClassLoader, ExpressionEvaluator> expressionEvaluatorFactory) {
        this.expressionEvaluatorFactory = expressionEvaluatorFactory;
    }

    public Optional<String> getFileName() {
        return Optional.ofNullable(fileName);
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public static Description getDescriptionForSimulation(Optional<String> filename, Simulation simulation) {
        return getDescriptionForSimulation(filename, simulation.getSimulationDescriptor(), toScenarioMap(simulation));
    }

    public static Description getDescriptionForSimulation(Optional<String> filename, SimulationDescriptor simulationDescriptor, Map<Integer, Scenario> scenarios) {
        Description suiteDescription = Description.createSuiteDescription("Test Scenarios (Preview) tests");
        scenarios.forEach((index, scenario) -> suiteDescription.addChild(getDescriptionForScenario(filename, index, scenario)));
        return suiteDescription;
    }

    public static Map<Integer, Scenario> toScenarioMap(Simulation simulation) {
        List<Scenario> scenarios = simulation.getUnmodifiableScenarios();
        Map<Integer, Scenario> indexToScenario = new HashMap<>();
        for (int index = 0; index < scenarios.size(); index += 1) {
            indexToScenario.put(index + 1, scenarios.get(index));
        }
        return indexToScenario;
    }

    public static Description getDescriptionForScenario(Optional<String> className, int index, Scenario scenario) {
        return Description.createTestDescription(className.orElse(ScenarioRunnerImpl.class.getCanonicalName()),
                                                 String.format("#%d: %s", index, scenario.getDescription()));
    }
}
