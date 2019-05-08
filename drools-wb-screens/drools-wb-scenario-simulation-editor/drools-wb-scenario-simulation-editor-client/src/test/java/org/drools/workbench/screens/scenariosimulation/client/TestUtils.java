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
package org.drools.workbench.screens.scenariosimulation.client;

import java.util.stream.IntStream;

import org.drools.scenariosimulation.api.model.ExpressionIdentifier;
import org.drools.scenariosimulation.api.model.FactIdentifier;
import org.drools.scenariosimulation.api.model.FactMappingType;
import org.drools.scenariosimulation.api.model.Scenario;
import org.drools.scenariosimulation.api.model.Simulation;
import org.drools.scenariosimulation.api.model.SimulationDescriptor;

/**
 * Class used to provide common methods used by different classes
 */
public class TestUtils {

    public static Simulation getSimulation(int numberOfColumns, int numberOfRows) {
        Simulation simulation = new Simulation();
        SimulationDescriptor simulationDescriptor = simulation.getSimulationDescriptor();
        simulationDescriptor.addFactMapping(FactIdentifier.DESCRIPTION, ExpressionIdentifier.DESCRIPTION);
        // generate simulationDescriptor
        IntStream.range(0, numberOfColumns).forEach(columnIndex -> {
            simulationDescriptor.addFactMapping(FactIdentifier.create(getFactName(columnIndex), String.class.getCanonicalName()),
                                                ExpressionIdentifier.create(getColName(columnIndex), FactMappingType.EXPECT)
            );
        });
        // generate scenarios
        IntStream.range(0, numberOfRows).forEach(rowIndex -> {
            final Scenario scenario = simulation.addScenario();
            scenario.setDescription(getRowName(rowIndex));
            IntStream.range(0, numberOfColumns).forEach( columnIndex -> {
                scenario.addMappingValue(FactIdentifier.create(getFactName(columnIndex), String.class.getCanonicalName()),
                                         ExpressionIdentifier.create(getColName(columnIndex), FactMappingType.EXPECT),
                                         getCellValue(columnIndex, rowIndex));
            });
        });
        return simulation;
    }

    public static String getColName(int index) {
        return "COL-" + index;
    }

    public static String getRowName(int index) {
        return "ROW-" + index;
    }

    public static String getFactName(int index) {
        return "GROUP_COL-" + index;
    }

    public static String getCellValue(int col, int row) {
        return "VAL_COL-" + col + "-ROW-" + row;
    }

}
