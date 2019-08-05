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
package org.drools.workbench.screens.scenariosimulation.backend.server.util;

import javax.enterprise.context.ApplicationScoped;

import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.drools.scenariosimulation.api.model.ScenarioWithIndex;
import org.drools.scenariosimulation.api.model.Simulation;
import org.drools.scenariosimulation.api.model.SimulationDescriptor;
import org.uberfire.backend.vfs.Path;

import static org.drools.scenariosimulation.api.model.FactMappingType.EXPECT;
import static org.drools.scenariosimulation.api.model.FactMappingType.GIVEN;

@ApplicationScoped
public class RULESimulationCreationStrategy implements SimulationCreationStrategy {

    @Override
    public Simulation createSimulation(Path context, String value) throws Exception {
        Simulation toReturn = new Simulation();
        SimulationDescriptor simulationDescriptor = toReturn.getSimulationDescriptor();
        simulationDescriptor.setType(ScenarioSimulationModel.Type.RULE);
        simulationDescriptor.setDmoSession(value);

        ScenarioWithIndex scenarioWithIndex = createScenario(toReturn, simulationDescriptor);

        // Add GIVEN Fact
        createEmptyColumn(simulationDescriptor,
                          scenarioWithIndex,
                          1,
                          GIVEN,
                          simulationDescriptor.getFactMappings().size());

        // Add EXPECT Fact
        createEmptyColumn(simulationDescriptor,
                          scenarioWithIndex,
                          2,
                          EXPECT,
                          simulationDescriptor.getFactMappings().size());
        return toReturn;
    }
}
