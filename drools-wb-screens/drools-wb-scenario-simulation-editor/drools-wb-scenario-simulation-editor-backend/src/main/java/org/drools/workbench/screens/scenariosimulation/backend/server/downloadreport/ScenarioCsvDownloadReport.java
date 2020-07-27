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
package org.drools.workbench.screens.scenariosimulation.backend.server.downloadreport;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.drools.scenariosimulation.api.model.AuditLogLine;
import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.drools.scenariosimulation.api.model.SimulationRunMetadata;
import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;

public class ScenarioCsvDownloadReport {

    protected static final String[] DMN_OVERALL_STATS_HEADER = {"AVAILABLE DECISIONS", "DECISIONS FIRED", "PERCENTAGE OF DECISIONS FIRED"};
    protected static final String[] RULE_OVERALL_STATS_HEADER = {"AVAILABLE RULES", "RULES FIRED", "PERCENTAGE OF RULES FIRED"};
    protected static final String[] DMN_COUNTER_HEADER = {"DECISION", "NUMBER OF TIMES"};
    protected static final String[] RULE_COUNTER_HEADER = {"RULE", "NUMBER OF TIMES"};
    protected static final String[] DMN_AUDIT_HEADER = {"TEST SCENARIO INDEX", "TEST SCENARIO NAME", "DECISION INDEX", "EVALUATED DECISION", "DECISION STATUS", "MESSAGE"};
    protected static final String[] RULE_AUDIT_HEADER = {"TEST SCENARIO INDEX", "TEST SCENARIO NAME", "RULE INDEX", "FIRED RULE", "RULE STATUS", "MESSAGE"};

    /**
     * @param simulationRunMetadata the <code>SimulationRunMetadata</code> to print out
     * @param modelType
     * @return
     * @throws IOException
     */
    public String getReport(SimulationRunMetadata simulationRunMetadata, ScenarioSimulationModel.Type modelType) {
        StringBuilder stringBuilder = new StringBuilder();

        try (CSVPrinter printer = new CSVPrinter(stringBuilder, CSVFormat.DEFAULT.withNullString(""))) {
            generateOverallStatsHeader(printer, modelType);
            printOverallStatsLine(printer,
                                  simulationRunMetadata.getAvailable(),
                                  simulationRunMetadata.getExecuted(),
                                  simulationRunMetadata.getCoveragePercentage());

            Map<String, Integer> outputCounter = simulationRunMetadata.getOutputCounter();
            if (outputCounter != null && !outputCounter.isEmpty()) {
                printer.println();
                generateRulesCounterHeader(printer, modelType);

                for (Map.Entry<String, Integer> entry : outputCounter.entrySet()) {
                    printRulesCounterLine(printer, entry.getKey(), entry.getValue());
                }
            }

            List<AuditLogLine> auditLogLines = simulationRunMetadata.getAuditLog().getAuditLogLines();
            if (auditLogLines != null && !auditLogLines.isEmpty()) {
                printer.println();
                generateAuditLogHeader(printer, modelType);

                for (AuditLogLine auditLogLine : auditLogLines) {
                    printAuditLogLine(auditLogLine, printer);
                }
            }
        } catch (IOException e) {
            throw ExceptionUtilities.handleException(e);
        }

        return stringBuilder.toString();
    }

    protected void generateOverallStatsHeader(CSVPrinter printer, ScenarioSimulationModel.Type modelType) throws IOException {
        if (ScenarioSimulationModel.Type.DMN.equals(modelType)) {
            printer.printRecord(DMN_OVERALL_STATS_HEADER);
        } else {
            printer.printRecord(RULE_OVERALL_STATS_HEADER);
        }
    }

    protected void printOverallStatsLine(CSVPrinter printer, int available, int executed, double coveragePercentage) throws IOException {
        printer.printRecord(Arrays.asList(available, executed, coveragePercentage));
    }

    protected void generateRulesCounterHeader(CSVPrinter printer, ScenarioSimulationModel.Type modelType) throws IOException {
        if (ScenarioSimulationModel.Type.DMN.equals(modelType)) {
            printer.printRecord(DMN_COUNTER_HEADER);
        } else {
            printer.printRecord(RULE_COUNTER_HEADER);
        }
    }

    protected void printRulesCounterLine(CSVPrinter printer, String rule, int times) throws IOException {
        printer.printRecord(Arrays.asList(rule, times));
    }

    protected void generateAuditLogHeader(CSVPrinter printer, ScenarioSimulationModel.Type modelType) throws IOException {
        if (ScenarioSimulationModel.Type.DMN.equals(modelType)) {
            printer.printRecord(DMN_AUDIT_HEADER);
        } else {
            printer.printRecord(RULE_AUDIT_HEADER);
        }
    }

    protected void printAuditLogLine(AuditLogLine toPrint, CSVPrinter printer) throws IOException {
        printer.print(toPrint.getScenarioIndex());
        printer.print(toPrint.getScenario());
        printer.print(toPrint.getExecutionIndex());
        printer.print(toPrint.getDecisionOrRuleName());
        printer.print(toPrint.getResult());
        printer.print(toPrint.getMessage().orElse(""));
        printer.println();
    }
}
