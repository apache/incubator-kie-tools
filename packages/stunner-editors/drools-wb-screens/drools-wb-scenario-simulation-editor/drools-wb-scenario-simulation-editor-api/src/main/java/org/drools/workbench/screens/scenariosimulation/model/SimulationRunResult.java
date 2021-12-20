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
package org.drools.workbench.screens.scenariosimulation.model;

import java.util.List;

import org.drools.scenariosimulation.api.model.BackgroundDataWithIndex;
import org.drools.scenariosimulation.api.model.ScenarioWithIndex;
import org.drools.scenariosimulation.api.model.SimulationRunMetadata;
import org.guvnor.common.services.shared.test.TestResultMessage;
import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * Transport object that contains run result data and report
 */
@Portable
public class SimulationRunResult {

    protected List<ScenarioWithIndex> scenarioWithIndex;

    protected List<BackgroundDataWithIndex> backgroundDataWithIndex;

    protected SimulationRunMetadata simulationRunMetadata;

    private TestResultMessage testResultMessage;

    public SimulationRunResult() {
        // CDI
    }

    public SimulationRunResult(List<ScenarioWithIndex> scenarioWithIndex,
                               List<BackgroundDataWithIndex> backgroundDataWithIndex,
                               SimulationRunMetadata simulationRunMetadata,
                               TestResultMessage testResultMessage) {
        this.scenarioWithIndex = scenarioWithIndex;
        this.backgroundDataWithIndex = backgroundDataWithIndex;
        this.simulationRunMetadata = simulationRunMetadata;
        this.testResultMessage = testResultMessage;
    }

    public List<ScenarioWithIndex> getScenarioWithIndex() {
        return scenarioWithIndex;
    }

    public List<BackgroundDataWithIndex> getBackgroundDataWithIndex() {
        return backgroundDataWithIndex;
    }

    public SimulationRunMetadata getSimulationRunMetadata() {
        return simulationRunMetadata;
    }

    public TestResultMessage getTestResultMessage() {
        return testResultMessage;
    }
}
