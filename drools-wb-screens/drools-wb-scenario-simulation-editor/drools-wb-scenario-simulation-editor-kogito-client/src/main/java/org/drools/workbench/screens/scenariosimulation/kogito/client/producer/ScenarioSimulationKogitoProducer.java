/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.scenariosimulation.kogito.client.producer;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.drools.workbench.screens.scenariosimulation.client.producers.AbstractScenarioSimulationProducer;
import org.drools.workbench.screens.scenariosimulation.kogito.client.commands.ScenarioSimulationKogitoStateControlManager;

@Dependent
public class ScenarioSimulationKogitoProducer extends AbstractScenarioSimulationProducer {

    @Inject
    protected ScenarioSimulationKogitoStateControlManager scenarioSimulationStateControlManager;

    @Override
    public void init() {
        super.init();

        scenarioSimulationStateControlManager.initialize(scenarioCommandRegistryManager,
                scenarioGridPanelProducer.getScenarioSimulationContext());
    }
}
