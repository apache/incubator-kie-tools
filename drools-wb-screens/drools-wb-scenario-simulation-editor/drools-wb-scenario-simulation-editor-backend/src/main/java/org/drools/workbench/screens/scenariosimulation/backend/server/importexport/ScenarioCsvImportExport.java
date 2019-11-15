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
import org.drools.scenariosimulation.api.model.AbstractScesimData;
import org.drools.scenariosimulation.api.model.AbstractScesimModel;
import org.drools.scenariosimulation.api.model.FactMapping;
import org.drools.scenariosimulation.api.model.FactMappingType;
import org.drools.scenariosimulation.api.model.FactMappingValue;

public class ScenarioCsvImportExport {

    public static final int HEADER_SIZE = 3;

    public String exportData(AbstractScesimModel<? extends AbstractScesimData> scesimModel) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        List<FactMapping> factMappings = scesimModel.getScesimModelDescriptor().getUnmodifiableFactMappings();

        CSVPrinter printer = new CSVPrinter(stringBuilder, CSVFormat.DEFAULT);

        generateHeader(factMappings, printer);

        for (AbstractScesimData scesimData : scesimModel.getUnmodifiableData()) {
            List<Object> values = new ArrayList<>();
            for (FactMapping factMapping : factMappings) {
                Optional<FactMappingValue> factMappingValue = scesimData.getFactMappingValue(factMapping.getFactIdentifier(),
                                                                                           factMapping.getExpressionIdentifier());
                values.add(factMappingValue.map(FactMappingValue::getRawValue).orElse(""));
            }
            printer.printRecord(values.toArray());
        }

        printer.close();

        return stringBuilder.toString();
    }

    public <T extends AbstractScesimData> AbstractScesimModel<T> importData(String raw, AbstractScesimModel<T> originalScesimModel) throws IOException {

        CSVParser csvParser = CSVFormat.DEFAULT.parse(new StringReader(raw));

        AbstractScesimModel<T> toReturn = originalScesimModel.cloneModel();
        toReturn.clearDatas();

        List<FactMapping> factMappings = toReturn.getScesimModelDescriptor().getUnmodifiableFactMappings();

        List<CSVRecord> csvRecords = csvParser.getRecords();
        if (csvRecords.size() < HEADER_SIZE) {
            throw new IllegalArgumentException("Malformed file, missing header");
        }
        csvRecords = csvRecords.subList(HEADER_SIZE, csvRecords.size());

        for (CSVRecord csvRecord : csvRecords) {
            T scesimDataToFill = toReturn.addData();
            if (csvRecord.size() != factMappings.size()) {
                throw new IllegalArgumentException("Malformed row " + csvRecord);
            }
            for (int i = 0; i < factMappings.size(); i += 1) {
                FactMapping factMapping = factMappings.get(i);
                String valueToImport = "".equals(csvRecord.get(i)) ? null : csvRecord.get(i);
                scesimDataToFill.addMappingValue(factMapping.getFactIdentifier(),
                                               factMapping.getExpressionIdentifier(),
                                               valueToImport);
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
            } else {
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
