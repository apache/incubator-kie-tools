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
import org.drools.scenariosimulation.api.model.ScesimModelDescriptor;
import org.drools.scenariosimulation.api.model.Settings;
import org.drools.scenariosimulation.api.model.Simulation;
import org.uberfire.backend.vfs.Path;

import static org.drools.scenariosimulation.api.model.FactMappingType.EXPECT;
import static org.drools.scenariosimulation.api.model.FactMappingType.GIVEN;

@ApplicationScoped
public class RULESimulationSettingsCreationStrategy implements SimulationSettingsCreationStrategy {

    @Override
    public Simulation createSimulation(Path context, String value) {
        Simulation toReturn = new Simulation();
        ScesimModelDescriptor simulationDescriptor = toReturn.getScesimModelDescriptor();
        ScenarioWithIndex scenarioWithIndex = createScesimDataWithIndex(toReturn, simulationDescriptor, ScenarioWithIndex::new);

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

    @Override
    public Settings createSettings(Path context, String dmoSession) {
        Settings toReturn = new Settings();
        toReturn.setType(ScenarioSimulationModel.Type.RULE);
        toReturn.setDmoSession(dmoSession);
        return toReturn;
    }
}
