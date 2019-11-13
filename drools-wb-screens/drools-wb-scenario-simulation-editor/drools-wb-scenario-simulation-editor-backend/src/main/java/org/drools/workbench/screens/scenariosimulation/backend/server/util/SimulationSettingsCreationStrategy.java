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

import java.util.function.BiFunction;

import org.drools.scenariosimulation.api.model.AbstractScesimData;
import org.drools.scenariosimulation.api.model.AbstractScesimModel;
import org.drools.scenariosimulation.api.model.Background;
import org.drools.scenariosimulation.api.model.BackgroundData;
import org.drools.scenariosimulation.api.model.BackgroundDataWithIndex;
import org.drools.scenariosimulation.api.model.ExpressionIdentifier;
import org.drools.scenariosimulation.api.model.FactIdentifier;
import org.drools.scenariosimulation.api.model.FactMapping;
import org.drools.scenariosimulation.api.model.FactMappingType;
import org.drools.scenariosimulation.api.model.ScesimDataWithIndex;
import org.drools.scenariosimulation.api.model.ScesimModelDescriptor;
import org.drools.scenariosimulation.api.model.Settings;
import org.drools.scenariosimulation.api.model.Simulation;
import org.uberfire.backend.vfs.Path;

import static org.drools.scenariosimulation.api.model.FactMappingType.GIVEN;

/**
 * <b>Strategy</b> that actually builds the required <code>Simulation</code> and <code>Settings</code> based on <code>ScenarioSimulationModel.Type</code>
 */
public interface SimulationSettingsCreationStrategy {

    Simulation createSimulation(Path context, String value);

    Settings createSettings(Path context, String value);

    default <T extends AbstractScesimData, E extends ScesimDataWithIndex<T>> E createScesimDataWithIndex(AbstractScesimModel<T> abstractScesimModel, ScesimModelDescriptor simulationDescriptor, BiFunction<Integer, T, E> producer) {
        simulationDescriptor.addFactMapping(FactIdentifier.INDEX.getName(), FactIdentifier.INDEX, ExpressionIdentifier.INDEX);
        simulationDescriptor.addFactMapping(FactIdentifier.DESCRIPTION.getName(), FactIdentifier.DESCRIPTION, ExpressionIdentifier.DESCRIPTION);
        T scenario = abstractScesimModel.addData();
        scenario.setDescription(null);
        int index = abstractScesimModel.getUnmodifiableData().indexOf(scenario) + 1;
        return producer.apply(index, scenario);
    }

    default Background createBackground(Path context, String dmnFilePath) {
        Background toReturn = new Background();
        ScesimModelDescriptor simulationDescriptor = toReturn.getScesimModelDescriptor();
        int index = toReturn.getUnmodifiableData().size() + 1;
        BackgroundData backgroundData = toReturn.addData();
        BackgroundDataWithIndex backgroundDataWithIndex = new BackgroundDataWithIndex(index, backgroundData);

        // Add GIVEN Fact
        createEmptyColumn(simulationDescriptor,
                          backgroundDataWithIndex,
                          1,
                          GIVEN,
                          simulationDescriptor.getFactMappings().size());
        return toReturn;
    }

    /**
     * Create an empty column using factMappingType defined. The new column will be added as last column of
     * the group (GIVEN/EXPECT) (see findLastIndexOfGroup)
     *
     * @param simulationDescriptor
     * @param scesimDataWithIndex
     * @param placeholderId
     * @param factMappingType
     * @param columnIndex
     */
    default void createEmptyColumn(ScesimModelDescriptor simulationDescriptor,
                                   ScesimDataWithIndex scesimDataWithIndex,
                                   int placeholderId,
                                   FactMappingType factMappingType,
                                   int columnIndex) {
        int row = scesimDataWithIndex.getIndex();
        final ExpressionIdentifier expressionIdentifier = ExpressionIdentifier.create(row + "|" + placeholderId, factMappingType);

        final FactMapping factMapping = simulationDescriptor
                .addFactMapping(
                        columnIndex,
                        FactMapping.getInstancePlaceHolder(placeholderId),
                        FactIdentifier.EMPTY,
                        expressionIdentifier);
        factMapping.setExpressionAlias(FactMapping.getPropertyPlaceHolder(placeholderId));
        scesimDataWithIndex.getScesimData().addMappingValue(FactIdentifier.EMPTY, expressionIdentifier, null);
    }
}
