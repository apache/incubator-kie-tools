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
import java.util.Random;
import java.util.stream.IntStream;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.drools.scenariosimulation.api.model.AuditLog;
import org.drools.scenariosimulation.api.model.AuditLogLine;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ScenarioCsvDownloadReportTest {

    private static final String[] HEADER_COLUMNS = {"Scenario index", "Scenario", "Result index", "Result", "Level"};
    private final ScenarioCsvDownloadReport scenarioCsvDownloadReport = new ScenarioCsvDownloadReport();

    @Test
    public void getReport() throws IOException {
        AuditLog auditLog = new AuditLog();
        IntStream.range(0, 6).forEach(index -> auditLog.addAuditLogLine(getAuditLogLine()));
        String retrieved = scenarioCsvDownloadReport.getReport(auditLog);
        assertNotNull(retrieved);
        String[] retrievedLines = retrieved.split("\r\n");
        commonCheckHeader(retrievedLines[0]);
        for (int i = 1; i < retrievedLines.length; i++) {
            commonCheckRetrievedString(retrievedLines[i], auditLog.getAuditLogLines().get(i-1));
        }
    }

    @Test
    public void printAuditLogLine() throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        CSVPrinter printer = getCSVPrinter(stringBuilder);
        AuditLogLine auditLogLine = getAuditLogLine();
        scenarioCsvDownloadReport.printAuditLogLine(auditLogLine, printer);
        String retrieved = stringBuilder.toString();
        commonCheckRetrievedString(retrieved, auditLogLine);
    }

    @Test
    public void generateHeader() throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        CSVPrinter printer = getCSVPrinter(stringBuilder);
        scenarioCsvDownloadReport.generateHeader(printer);
        String retrieved = stringBuilder.toString();
        commonCheckHeader(retrieved);
    }

    private void commonCheckHeader(String toCheck) {
        assertNotNull(toCheck);
        toCheck = toCheck.replace("\r\n", "");
        String[] retrievedColumns = toCheck.split(",");
        assertEquals(HEADER_COLUMNS.length, retrievedColumns.length);
        for (int i = 0; i < retrievedColumns.length; i++) {
            assertEquals(HEADER_COLUMNS[i], retrievedColumns[i]);
        }
    }

    private void commonCheckRetrievedString(String toCheck, AuditLogLine auditLogLine) {
        assertNotNull(toCheck);
        toCheck = toCheck.replace("\r\n", "");
        assertTrue(toCheck.contains(String.valueOf(auditLogLine.getScenarioIndex())));
        toCheck = toCheck.replaceFirst(String.valueOf(auditLogLine.getScenarioIndex()), "");
        String fieldToCheck = auditLogLine.getScenario();
        if (fieldToCheck.contains(",")) {
            fieldToCheck = "\"" + fieldToCheck + "\"";
        }
        assertTrue(toCheck.contains(fieldToCheck));
        toCheck = toCheck.replaceFirst(fieldToCheck, "");
        assertTrue(toCheck.contains(String.valueOf(auditLogLine.getExecutionIndex())));
        toCheck = toCheck.replaceFirst(String.valueOf(auditLogLine.getExecutionIndex()), "");
        fieldToCheck = auditLogLine.getMessage();
        if (fieldToCheck.contains(",")) {
            fieldToCheck = "\"" + fieldToCheck + "\"";
        }
        assertTrue(toCheck.contains(fieldToCheck));
        toCheck = toCheck.replaceFirst(fieldToCheck, "");
        fieldToCheck = auditLogLine.getLevel();
        if (fieldToCheck.contains(",")) {
            fieldToCheck = "\"" + fieldToCheck + "\"";
        }
        assertTrue(toCheck.contains(fieldToCheck));
        toCheck = toCheck.replaceFirst(fieldToCheck, "");
        assertEquals(",,,,", toCheck);

    }

    private CSVPrinter getCSVPrinter(StringBuilder stringBuilder) throws IOException {
        return new CSVPrinter(stringBuilder, CSVFormat.DEFAULT);
    }

    private AuditLogLine getAuditLogLine() {
        Random random = new Random();
        return new AuditLogLine(random.nextInt(3), "sce,nario-" + random.nextInt(5), random.nextInt(6), "Ru,le-" + random.nextInt(), "INFO");
    }
}