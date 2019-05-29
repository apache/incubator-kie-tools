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
package org.drools.workbench.screens.scenariosimulation.backend.server.importexport;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.drools.scenariosimulation.api.model.ExpressionIdentifier;
import org.drools.scenariosimulation.api.model.FactIdentifier;
import org.drools.scenariosimulation.api.model.FactMapping;
import org.drools.scenariosimulation.api.model.FactMappingType;
import org.drools.scenariosimulation.api.model.Scenario;
import org.drools.scenariosimulation.api.model.Simulation;
import org.drools.scenariosimulation.api.model.SimulationDescriptor;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.drools.workbench.screens.scenariosimulation.backend.server.importexport.ScenarioCsvImportExport.HEADER_SIZE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ScenarioCsvImportExportTest {

    static String instanceName = "instanceName";
    static String propertyName = "propertyName";

    CSVPrinter printer;

    ScenarioCsvImportExport scenarioCsvImportExport;

    StringBuilder output;

    @Before
    public void setup() throws IOException {
        output = new StringBuilder();
        printer = new CSVPrinter(output, CSVFormat.EXCEL);
        scenarioCsvImportExport = new ScenarioCsvImportExport();
    }

    @Test
    public void exportData() throws IOException {
        int numberOfRow = 2;
        int numberOfColumn = 1;
        Simulation simulation = createDummySimulation(numberOfColumn, numberOfRow);
        List<String> exportData = Arrays.asList(scenarioCsvImportExport.exportData(simulation).split("\r\n"));

        assertEquals(numberOfRow + HEADER_SIZE, exportData.size());
        assertEquals("1,My scenario 1,value_1_0", exportData.get(4));
    }

    @Test
    public void importData() throws IOException {
        Simulation originalSimulation = createDummySimulation(3, 1);

        assertEquals(1, originalSimulation.getUnmodifiableScenarios().size());

        String rawCSV = "OTHER,OTHER,GIVEN,GIVEN,GIVEN\r\n" +
                "#,Scenario description,instance1,instance2,instance3\r\n" +
                "Index,Description,property1,property2,property3\r\n" +
                "1,My Scenario,value1,value2,";

        Simulation simulation = scenarioCsvImportExport.importData(rawCSV, originalSimulation);

        assertEquals(1, simulation.getUnmodifiableScenarios().size());

        assertEquals("value1", simulation.getScenarioByIndex(0).getFactMappingValue(simulation.getSimulationDescriptor().getFactMappingByIndex(2)).get().getRawValue());
        assertEquals("value2", simulation.getScenarioByIndex(0).getFactMappingValue(simulation.getSimulationDescriptor().getFactMappingByIndex(3)).get().getRawValue());
        assertNull(simulation.getScenarioByIndex(0).getFactMappingValue(simulation.getSimulationDescriptor().getFactMappingByIndex(4)).get().getRawValue());

        assertThatThrownBy(() -> scenarioCsvImportExport.importData("", originalSimulation))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Malformed file, missing header");
    }

    @Test
    public void generateHeader() throws IOException {
        SimulationDescriptor simulationDescriptor = new SimulationDescriptor();
        FactMapping test1FactMapping = createFactMapping(simulationDescriptor, 1);
        FactMapping test2FactMapping = createFactMapping(simulationDescriptor, 2);

        scenarioCsvImportExport.generateHeader(Arrays.asList(test1FactMapping, test2FactMapping), printer);

        System.out.println("output.toString() = " + output.toString());

        String[] result = output.toString().split("\r\n");

        assertEquals(3, result.length);
        assertEquals("GIVEN,GIVEN", result[0]);
        assertEquals(instanceName + 1 + "," + instanceName + 2, result[1]);
        assertEquals(propertyName + 1 + "," + propertyName + 2, result[2]);
    }

    private FactMapping createFactMapping(SimulationDescriptor simulationDescriptor, int number) {
        FactMapping toReturn = simulationDescriptor.addFactMapping(
                FactIdentifier.create(instanceName + number, String.class.getCanonicalName()),
                ExpressionIdentifier.create(propertyName + number, FactMappingType.GIVEN));
        toReturn.setExpressionAlias(propertyName + number);
        return toReturn;
    }

    private Simulation createDummySimulation(int numberOfColumn, int numberOfRow) {
        Simulation simulation = new Simulation();
        SimulationDescriptor simulationDescriptor = simulation.getSimulationDescriptor();
        simulationDescriptor.addFactMapping(FactIdentifier.INDEX, ExpressionIdentifier.INDEX)
                .setExpressionAlias("Index");
        simulationDescriptor.addFactMapping(FactIdentifier.DESCRIPTION, ExpressionIdentifier.DESCRIPTION)
                .setExpressionAlias("Description");

        for (int col = 0; col < numberOfColumn; col += 1) {
            createFactMapping(simulationDescriptor, col);
        }

        for (int row = 0; row < numberOfRow; row += 1) {
            Scenario scenario = simulation.addScenario();
            scenario.addMappingValue(FactIdentifier.INDEX, ExpressionIdentifier.INDEX, row);
            scenario.setDescription("My scenario " + row);
            for (int col = 2; col < numberOfColumn + 2; col += 1) {
                FactMapping factMappingByIndex = simulationDescriptor.getFactMappingByIndex(col);
                scenario.addMappingValue(factMappingByIndex.getFactIdentifier(),
                                         factMappingByIndex.getExpressionIdentifier(),
                                         "value_" + row + "_" + (col - 2));
            }
        }

        return simulation;
    }
}