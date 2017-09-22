/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.stunner.bpmn.backend.service.simulation;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.jbpm.simulation.SimulationRepository;
import org.jbpm.simulation.SimulationRunner;
import org.jbpm.simulation.impl.WorkingMemorySimulationRepository;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class SimulationRunnerTest {

    @Test
    public void testRunSimulationOnSendTask() throws Exception {
        String bpsimSendTaskSource = readFile("BPSim_sendtask.bpmn2");
        String bpsimSendTaskProcessId = "evaluation.simtest";

        WorkingMemorySimulationRepository wmsRepo = runSimulation(bpsimSendTaskProcessId,
                                                                  bpsimSendTaskSource,
                                                                  50,
                                                                  5);

        assertNotNull(wmsRepo);
        assertNotNull(wmsRepo.getAggregatedEvents());
        assertTrue(wmsRepo.getAggregatedEvents().size() > 0);
    }

    private WorkingMemorySimulationRepository runSimulation(String processId,
                                                            String processXML,
                                                            int numInstances,
                                                            long interval) {
        SimulationRepository repo = SimulationRunner.runSimulation(processId,
                                                                   processXML,
                                                                   numInstances,
                                                                   interval,
                                                                   true,
                                                                   "onevent.simulation.rules.drl");

        return (WorkingMemorySimulationRepository) repo;
    }

    private String readFile(String fileName) throws Exception {
        URL fileURL = SimulationRunnerTest.class.getResource(fileName);
        return new String(Files.readAllBytes(Paths.get(fileURL.toURI())));
    }
}
