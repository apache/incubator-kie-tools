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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.IntStream;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.drools.scenariosimulation.api.model.AuditLog;
import org.drools.scenariosimulation.api.model.AuditLogLine;
import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.drools.scenariosimulation.api.model.SimulationRunMetadata;
import org.junit.Test;

import static org.drools.scenariosimulation.api.model.ScenarioSimulationModel.Type.DMN;
import static org.drools.scenariosimulation.api.model.ScenarioSimulationModel.Type.RULE;
import static org.drools.workbench.screens.scenariosimulation.backend.server.downloadreport.ScenarioCsvDownloadReport.DMN_AUDIT_HEADER;
import static org.drools.workbench.screens.scenariosimulation.backend.server.downloadreport.ScenarioCsvDownloadReport.DMN_COUNTER_HEADER;
import static org.drools.workbench.screens.scenariosimulation.backend.server.downloadreport.ScenarioCsvDownloadReport.DMN_OVERALL_STATS_HEADER;
import static org.drools.workbench.screens.scenariosimulation.backend.server.downloadreport.ScenarioCsvDownloadReport.RULE_AUDIT_HEADER;
import static org.drools.workbench.screens.scenariosimulation.backend.server.downloadreport.ScenarioCsvDownloadReport.RULE_COUNTER_HEADER;
import static org.drools.workbench.screens.scenariosimulation.backend.server.downloadreport.ScenarioCsvDownloadReport.RULE_OVERALL_STATS_HEADER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ScenarioCsvDownloadReportTest {

    private final ScenarioCsvDownloadReport scenarioCsvDownloadReport = new ScenarioCsvDownloadReport();

    @Test
    public void getReport_RULE() {
        commonGetReport(RULE, RULE_OVERALL_STATS_HEADER, RULE_COUNTER_HEADER, RULE_AUDIT_HEADER);
    }

    @Test
    public void getReport_DMN() {
        commonGetReport(DMN, DMN_OVERALL_STATS_HEADER, DMN_COUNTER_HEADER, DMN_AUDIT_HEADER);
    }

    private void commonGetReport(ScenarioSimulationModel.Type type, String[] overallStats, String[] counterHeader, String[] auditHeader) {
        AuditLog auditLog = new AuditLog();
        IntStream.range(0, 6).forEach(index -> auditLog.addAuditLogLine(getAuditLogLine()));
        SimulationRunMetadata simulationRunMetadata = getSimulationRunMetadata(auditLog);
        String retrieved = scenarioCsvDownloadReport.getReport(simulationRunMetadata, type);
        assertNotNull(retrieved);
        String[] retrievedLines = retrieved.split("\r\n");
        commonCheckHeader(overallStats, retrievedLines[0]);
        List<String> overallStatsData = Arrays.asList(String.valueOf(simulationRunMetadata.getAvailable()), String.valueOf(simulationRunMetadata.getExecuted()), String.valueOf(simulationRunMetadata.getCoveragePercentage()));
        commonCheckRetrievedString(retrievedLines[1], overallStatsData);
        assertTrue(retrievedLines[2].isEmpty());
        commonCheckHeader(counterHeader, retrievedLines[3]);
        Map.Entry<String, Integer> entry = simulationRunMetadata.getOutputCounter().entrySet().iterator().next();
        List<String> rulesCounterData = Arrays.asList("\"" + entry.getKey() + "\"", String.valueOf(entry.getValue()));
        commonCheckRetrievedString(retrievedLines[4], rulesCounterData);
        assertTrue(retrievedLines[5].isEmpty());
        commonCheckHeader(auditHeader, retrievedLines[6]);
        for (int i = 7; i < retrievedLines.length; i++) {
            AuditLogLine auditLogLine = auditLog.getAuditLogLines().get(i - 7);
            List<String> auditData = Arrays.asList(String.valueOf(auditLogLine.getScenarioIndex()), "\"" + auditLogLine.getScenario() + "\"", String.valueOf(auditLogLine.getExecutionIndex()), "\"" + auditLogLine.getDecisionOrRuleName() + "\"", auditLogLine.getResult(), auditLogLine.getMessage().orElse(""));
            commonCheckRetrievedString(retrievedLines[i], auditData);
        }
    }

    @Test
    public void generateOverallStatsHeaderRULE() throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        CSVPrinter printer = getCSVPrinter(stringBuilder);
        scenarioCsvDownloadReport.generateOverallStatsHeader(printer, ScenarioSimulationModel.Type.RULE);
        String retrieved = stringBuilder.toString();
        commonCheckHeader(RULE_OVERALL_STATS_HEADER, retrieved);
    }

    @Test
    public void generateOverallStatsHeaderDMN() throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        CSVPrinter printer = getCSVPrinter(stringBuilder);
        scenarioCsvDownloadReport.generateOverallStatsHeader(printer, DMN);
        String retrieved = stringBuilder.toString();
        commonCheckHeader(DMN_OVERALL_STATS_HEADER, retrieved);
    }

    @Test
    public void printOverallStatsLine() throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        CSVPrinter printer = getCSVPrinter(stringBuilder);
        AuditLog auditLog = new AuditLog();
        auditLog.addAuditLogLine(getAuditLogLine());
        SimulationRunMetadata simulationRunMetadata = getSimulationRunMetadata(auditLog);
        scenarioCsvDownloadReport.printOverallStatsLine(printer, simulationRunMetadata.getAvailable(), simulationRunMetadata.getExecuted(), simulationRunMetadata.getCoveragePercentage());
        String retrieved = stringBuilder.toString();
        List<String> data = Arrays.asList(String.valueOf(simulationRunMetadata.getAvailable()), String.valueOf(simulationRunMetadata.getExecuted()), String.valueOf(simulationRunMetadata.getCoveragePercentage()));
        commonCheckRetrievedString(retrieved, data);
    }

    @Test
    public void generateRulesCounterHeaderRULE() throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        CSVPrinter printer = getCSVPrinter(stringBuilder);
        scenarioCsvDownloadReport.generateRulesCounterHeader(printer, ScenarioSimulationModel.Type.RULE);
        String retrieved = stringBuilder.toString();
        commonCheckHeader(RULE_COUNTER_HEADER, retrieved);
    }

    @Test
    public void generateRulesCounterHeaderDMN() throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        CSVPrinter printer = getCSVPrinter(stringBuilder);
        scenarioCsvDownloadReport.generateRulesCounterHeader(printer, DMN);
        String retrieved = stringBuilder.toString();
        commonCheckHeader(DMN_COUNTER_HEADER, retrieved);
    }

    @Test
    public void printRulesCounterLine() throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        CSVPrinter printer = getCSVPrinter(stringBuilder);
        AuditLog auditLog = new AuditLog();
        auditLog.addAuditLogLine(getAuditLogLine());
        SimulationRunMetadata simulationRunMetadata = getSimulationRunMetadata(auditLog);
        Map.Entry<String, Integer> entry = simulationRunMetadata.getOutputCounter().entrySet().iterator().next();
        scenarioCsvDownloadReport.printRulesCounterLine(printer, entry.getKey(), entry.getValue());
        String retrieved = stringBuilder.toString();
        List<String> data = Arrays.asList("\"" + entry.getKey() + "\"", String.valueOf(entry.getValue()));
        commonCheckRetrievedString(retrieved, data);
    }

    @Test
    public void generateAuditLogHeaderRULE() throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        CSVPrinter printer = getCSVPrinter(stringBuilder);
        scenarioCsvDownloadReport.generateAuditLogHeader(printer, RULE);
        String retrieved = stringBuilder.toString();
        commonCheckHeader(RULE_AUDIT_HEADER, retrieved);
    }

    @Test
    public void generateAuditLogHeaderDMN() throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        CSVPrinter printer = getCSVPrinter(stringBuilder);
        scenarioCsvDownloadReport.generateAuditLogHeader(printer, DMN);
        String retrieved = stringBuilder.toString();
        commonCheckHeader(DMN_AUDIT_HEADER, retrieved);
    }

    @Test
    public void printAuditLogLine() throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        CSVPrinter printer = getCSVPrinter(stringBuilder);
        AuditLogLine auditLogLine = getAuditLogLine();
        scenarioCsvDownloadReport.printAuditLogLine(auditLogLine, printer);
        String retrieved = stringBuilder.toString();
        List<String> data = Arrays.asList(String.valueOf(auditLogLine.getScenarioIndex()), "\"" + auditLogLine.getScenario() + "\"", String.valueOf(auditLogLine.getExecutionIndex()), "\"" + auditLogLine.getDecisionOrRuleName() + "\"", auditLogLine.getResult(), auditLogLine.getMessage().orElse(""));
        commonCheckRetrievedString(retrieved, data);
    }

    private void commonCheckHeader(String[] columns, String toCheck) {
        assertNotNull(toCheck);
        toCheck = toCheck.replace("\r\n", "");
        String[] retrievedColumns = toCheck.split(",");
        assertEquals(columns.length, retrievedColumns.length);
        for (int i = 0; i < retrievedColumns.length; i++) {
            assertEquals(columns[i], retrievedColumns[i]);
        }
    }

    private void commonCheckRetrievedString(String toCheck, List<String> rowData) {
        assertNotNull(toCheck);
        toCheck = toCheck.replace("\r\n", "");
        for (String data : rowData) {
            assertTrue(toCheck.contains(String.valueOf(data)));
            toCheck = toCheck.replaceFirst(String.valueOf(data), "");
        }

        assertEquals(rowData.size() - 1, toCheck.length());
        assertEquals(rowData.size() - 1, toCheck.chars().filter(ch -> ch == ',').count());
    }

    private CSVPrinter getCSVPrinter(StringBuilder stringBuilder) throws IOException {
        return new CSVPrinter(stringBuilder, CSVFormat.DEFAULT.withNullString(""));
    }

    private AuditLogLine getAuditLogLine() {
        Random random = new Random();
        String message = random.nextBoolean() ? "WARN: Error during evaluation" : null;
        return new AuditLogLine(random.nextInt(3), "sce,nario-" + random.nextInt(5), random.nextInt(6), "Ru,le-" + random.nextInt(), "INFO", message);
    }

    private SimulationRunMetadata getSimulationRunMetadata(AuditLog auditLog) {
        Random random = new Random();
        Map<String, Integer> rules = new HashMap<>();
        rules.put("Rule,1", random.nextInt(7));
        return new SimulationRunMetadata(random.nextInt(10), random.nextInt(5), rules, Collections.emptyMap(), auditLog);
    }
}