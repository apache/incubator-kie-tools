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
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.drools.workbench.screens.scenariosimulation.model.FactMapping;
import org.drools.workbench.screens.scenariosimulation.model.FactMappingType;
import org.drools.workbench.screens.scenariosimulation.model.FactMappingValue;
import org.drools.workbench.screens.scenariosimulation.model.Scenario;
import org.drools.workbench.screens.scenariosimulation.model.Simulation;

public class ScenarioCsvImportExport {

    public static int HEADER_SIZE = 3;

    public String exportData(Simulation simulation) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        List<FactMapping> factMappings = simulation.getSimulationDescriptor().getUnmodifiableFactMappings();

        CSVPrinter printer = new CSVPrinter(stringBuilder, CSVFormat.DEFAULT);

        generateHeader(factMappings, printer);

        for (Scenario scenario : simulation.getUnmodifiableScenarios()) {
            List<Object> values = new ArrayList<>();
            for (FactMapping factMapping : factMappings) {
                Optional<FactMappingValue> factMappingValue = scenario.getFactMappingValue(factMapping.getFactIdentifier(),
                                                                                           factMapping.getExpressionIdentifier());
                values.add(factMappingValue.map(FactMappingValue::getRawValue).orElse(""));
            }
            printer.printRecord(values.toArray());
        }

        printer.close();

        return stringBuilder.toString();
    }

    public Simulation importData(String raw, Simulation originalSimulation) throws IOException {

        CSVParser csvParser = CSVFormat.DEFAULT.parse(new StringReader(raw));

        Simulation toReturn = originalSimulation.cloneSimulation();
        toReturn.clearScenarios();

        List<FactMapping> factMappings = toReturn.getSimulationDescriptor().getUnmodifiableFactMappings();

        List<CSVRecord> csvRecords = csvParser.getRecords();
        if(csvRecords.size() < HEADER_SIZE) {
            throw new IllegalArgumentException("Malformed file, missing header");
        }
        csvRecords = csvRecords.subList(HEADER_SIZE, csvRecords.size());

        for (CSVRecord csvRecord : csvRecords) {
            Scenario scenarioToFill = toReturn.addScenario();
            if (csvRecord.size() != factMappings.size()) {
                throw new IllegalArgumentException("Malformed row " + csvRecord);
            }
            for (int i = 0; i < factMappings.size(); i += 1) {
                FactMapping factMapping = factMappings.get(i);
                scenarioToFill.addMappingValue(factMapping.getFactIdentifier(),
                                               factMapping.getExpressionIdentifier(),
                                               csvRecord.get(i));
            }
        }
        return toReturn;
    }

    protected void generateHeader(List<FactMapping> factMappings, CSVPrinter printer) throws IOException {
        List<String> firstLineHeader = new ArrayList<>();
        List<String> secondLineHeader = new ArrayList<>();
        List<String> thirdLineHeader = new ArrayList<>();

        for (FactMapping factMapping : factMappings) {
            if (FactMappingType.OTHER.equals(factMapping.getExpressionIdentifier().getType())) {
                // OTHER
                String factAlias = factMapping.getFactAlias();
                firstLineHeader.add(factAlias);
                secondLineHeader.add(factAlias);
                thirdLineHeader.add(factAlias);
            }
            else {
                // GIVEN/EXPECT
                firstLineHeader.add(factMapping.getExpressionIdentifier().getType().name());
                // Instance
                secondLineHeader.add("#".equals(factMapping.getFactAlias()) ? "" : factMapping.getFactAlias());
                // Property
                thirdLineHeader.add(factMapping.getExpressionAlias());
            }
        }

        printer.printRecord(firstLineHeader.toArray());
        printer.printRecord(secondLineHeader.toArray());
        printer.printRecord(thirdLineHeader.toArray());
    }
}
