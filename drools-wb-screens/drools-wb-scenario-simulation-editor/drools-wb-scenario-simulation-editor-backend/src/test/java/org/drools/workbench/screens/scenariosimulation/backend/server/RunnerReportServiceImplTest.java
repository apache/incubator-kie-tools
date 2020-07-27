/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.scenariosimulation.backend.server;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.IntStream;

import org.drools.scenariosimulation.api.model.AuditLog;
import org.drools.scenariosimulation.api.model.AuditLogLine;
import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.drools.scenariosimulation.api.model.SimulationRunMetadata;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class RunnerReportServiceImplTest {

    private RunnerReportServiceImpl runnerReportServiceImpl;

    @Before
    public void setup() {
        runnerReportServiceImpl = new RunnerReportServiceImpl();
    }

    @Test
    public void getReportRULE() {
        AuditLog auditLog = new AuditLog();
        IntStream.range(0, 6).forEach(index -> auditLog.addAuditLogLine(getAuditLogLine()));
        String report = runnerReportServiceImpl.getReport(getSimulationRunMetadata(auditLog), ScenarioSimulationModel.Type.RULE);
        assertNotNull(report);
        assertFalse(report.isEmpty());
        String[] reportLine = report.split("\\r\\n");
        assertTrue(report.contains("RULE"));
        assertFalse(report.contains("DECISION"));
        assertEquals(7 + auditLog.getAuditLogLines().size(), reportLine.length);
        for (int i = 0; i < reportLine.length; i++) {
            assertNotNull(reportLine[i]);
        }
    }

    @Test
    public void getReportDMN() {
        AuditLog auditLog = new AuditLog();
        IntStream.range(0, 6).forEach(index -> auditLog.addAuditLogLine(getAuditLogLine()));
        String report = runnerReportServiceImpl.getReport(getSimulationRunMetadata(auditLog), ScenarioSimulationModel.Type.DMN);
        assertNotNull(report);
        assertFalse(report.isEmpty());
        String[] reportLine = report.split("\\r\\n");
        assertTrue(report.contains("DECISION"));
        assertFalse(report.contains("RULE"));
        assertEquals(7 + auditLog.getAuditLogLines().size(),  reportLine.length);
        for (int i = 0; i < reportLine.length; i++) {
            assertNotNull(reportLine[i]);
        }
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
