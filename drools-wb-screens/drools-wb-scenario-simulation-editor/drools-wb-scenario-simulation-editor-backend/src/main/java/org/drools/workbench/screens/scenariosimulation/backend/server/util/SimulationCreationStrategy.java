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

import org.drools.scenariosimulation.api.model.ExpressionIdentifier;
import org.drools.scenariosimulation.api.model.FactIdentifier;
import org.drools.scenariosimulation.api.model.FactMapping;
import org.drools.scenariosimulation.api.model.FactMappingType;
import org.drools.scenariosimulation.api.model.Scenario;
import org.drools.scenariosimulation.api.model.ScenarioWithIndex;
import org.drools.scenariosimulation.api.model.Simulation;
import org.drools.scenariosimulation.api.model.SimulationDescriptor;
import org.uberfire.backend.vfs.Path;

/**
 * <b>Strategy</b> that actually builds the required <code>Simulation</code> based on <code>ScenarioSimulationModel.Type</code>
 */
public interface SimulationCreationStrategy {

    Simulation createSimulation(Path context, String value) throws Exception;

    default ScenarioWithIndex createScenario(Simulation simulation, SimulationDescriptor simulationDescriptor) {
        simulationDescriptor.addFactMapping(FactIdentifier.INDEX.getName(), FactIdentifier.INDEX, ExpressionIdentifier.INDEX);
        simulationDescriptor.addFactMapping(FactIdentifier.DESCRIPTION.getName(), FactIdentifier.DESCRIPTION, ExpressionIdentifier.DESCRIPTION);
        Scenario scenario = simulation.addScenario();
        scenario.setDescription(null);
        int index = simulation.getUnmodifiableScenarios().indexOf(scenario) + 1;
        return new ScenarioWithIndex(index, scenario);
    }

    /**
     * Create an empty column using factMappingType defined. The new column will be added as last column of
     * the group (GIVEN/EXPECT) (see findLastIndexOfGroup)
     * @param simulationDescriptor
     * @param scenarioWithIndex
     * @param placeholderId
     * @param factMappingType
     */
    default void createEmptyColumn(SimulationDescriptor simulationDescriptor,
                                   ScenarioWithIndex scenarioWithIndex,
                                   int placeholderId,
                                   FactMappingType factMappingType,
                                   int columnIndex) {
        int row = scenarioWithIndex.getIndex();
        final ExpressionIdentifier expressionIdentifier = ExpressionIdentifier.create(row + "|" + placeholderId, factMappingType);

        final FactMapping factMapping = simulationDescriptor
                .addFactMapping(
                        columnIndex,
                        FactMapping.getInstancePlaceHolder(placeholderId),
                        FactIdentifier.EMPTY,
                        expressionIdentifier);
        factMapping.setExpressionAlias(FactMapping.getPropertyPlaceHolder(placeholderId));
        scenarioWithIndex.getScenario().addMappingValue(FactIdentifier.EMPTY, expressionIdentifier, null);
    }
}
