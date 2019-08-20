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

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.drools.scenariosimulation.api.model.AuditLog;
import org.drools.scenariosimulation.api.model.AuditLogLine;

public class ScenarioCsvDownloadReport {

    /**
     * @param auditLog the <code>AuditLog</code> to print out
     * @return
     * @throws IOException
     */
    public String getReport(AuditLog auditLog) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        CSVPrinter printer = new CSVPrinter(stringBuilder, CSVFormat.DEFAULT);
        generateHeader(printer);
        for (AuditLogLine auditLogLine : auditLog.getAuditLogLines()) {
            printAuditLogLine(auditLogLine, printer);
        }
        printer.close();
        return stringBuilder.toString();
    }

    protected void printAuditLogLine(AuditLogLine toPrint, CSVPrinter printer) throws IOException {
        printer.print(toPrint.getScenarioIndex());
        printer.print(toPrint.getScenario());
        printer.print(toPrint.getExecutionIndex());
        printer.print(toPrint.getMessage());
        printer.print(toPrint.getLevel());
        printer.println();
    }

    protected void generateHeader(CSVPrinter printer) throws IOException {
        printer.printRecord(Arrays.asList("Scenario index", "Scenario", "Result index", "Result", "Level"));
    }
}
