/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.bpmn.client.emf;

import com.google.gwt.junit.client.GWTTestCase;
import org.eclipse.bpmn2.DocumentRoot;
import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.StartEvent;
import org.eclipse.emf.common.util.EList;
import org.junit.Test;

import static org.kie.workbench.common.stunner.bpmn.client.emf.Bpmn2Marshalling.marshall;
import static org.kie.workbench.common.stunner.bpmn.client.emf.Bpmn2Marshalling.unmarshall;

public class Bpmn2MarshallingTest extends GWTTestCase {

    @Test
    public void testUnmarshallSomeProcess() {
        DocumentRoot doc = unmarshall(SOME_PROCESS);
        assertNotNull(doc);
        Process process = getProcess(doc);
        assertNotNull(process);
        String processId = process.getId();
        assertNotNull(processId);
        assertEquals("test.process1", processId);
        String processName = process.getName();
        assertNotNull(processName);
        assertEquals("process1", processName);
        EList<FlowElement> flowElements = process.getFlowElements();
        assertNotNull(flowElements);
        FlowElement startEvent = flowElements.get(0);
        assertNotNull(startEvent);
        assertTrue(startEvent instanceof StartEvent);
        String startEventId = startEvent.getId();
        assertEquals("_B801DDDE-29E9-41C2-BF36-0045EA55F573", startEventId);
    }

    @Test
    public void testUnmarshallSomeProcessWithComments() {
        DocumentRoot doc = unmarshall(SOME_PROCESS_WITH_COMMENTS);
        assertNotNull(doc);
        Process process = getProcess(doc);
        assertNotNull(process);
        String processId = process.getId();
        assertNotNull(processId);
        assertEquals("test.process1", processId);
        String processName = process.getName();
        assertNotNull(processName);
        assertEquals("process1", processName);
        EList<FlowElement> flowElements = process.getFlowElements();
        assertNotNull(flowElements);
        FlowElement startEvent = flowElements.get(0);
        assertNotNull(startEvent);
        assertTrue(startEvent instanceof StartEvent);
        String startEventId = startEvent.getId();
        assertEquals("_B801DDDE-29E9-41C2-BF36-0045EA55F573", startEventId);
    }

    @Test
    public void testMarshallSomeProcess() {
        DocumentRoot doc = unmarshall(SOME_PROCESS);
        String raw = marshall(doc);
        assertNotNull(raw);
        assertEquals(SOME_PROCESS, raw);
    }

    public static Process getProcess(DocumentRoot docRoot) {
        return (Process) docRoot.getDefinitions().getRootElements().stream()
                .filter(p -> p instanceof Process)
                .findAny()
                .get();
    }

    private static final String SOME_PROCESS = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<bpmn2:definitions" +
            " id=\"_GUUj8KUCEemjtN0xRqbezg\"" +
            " exporter=\"jBPM Process Modeler\"" +
            " exporterVersion=\"2.0\"" +
            " targetNamespace=\"http://www.omg.org/bpmn20\"" +
            " xmlns:bpmn2=\"http://www.omg.org/spec/BPMN/20100524/MODEL\"" +
            " xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\"" +
            " xmlns:bpsim=\"http://www.bpsim.org/schemas/1.0\"" +
            " xmlns:dc=\"http://www.omg.org/spec/DD/20100524/DC\"" +
            " xmlns:drools=\"http://www.jboss.org/drools\">" +
            "<bpmn2:process id=\"test.process1\" drools:packageName=\"com.myspace.test\" drools:version=\"1.0\" name=\"process1\" isExecutable=\"true\">" +
            "<bpmn2:startEvent id=\"_B801DDDE-29E9-41C2-BF36-0045EA55F573\"/>" +
            "</bpmn2:process>" +
            "<bpmndi:BPMNDiagram id=\"_GUUj8aUCEemjtN0xRqbezg\">" +
            "<bpmndi:BPMNPlane id=\"_GUUj8qUCEemjtN0xRqbezg\" bpmnElement=\"test.process1\">" +
            "<bpmndi:BPMNShape id=\"shape__B801DDDE-29E9-41C2-BF36-0045EA55F573\" bpmnElement=\"_B801DDDE-29E9-41C2-BF36-0045EA55F573\">" +
            "<dc:Bounds height=\"56\" width=\"56\" x=\"100\" y=\"100\"/>" +
            "</bpmndi:BPMNShape>" +
            "</bpmndi:BPMNPlane>" +
            "</bpmndi:BPMNDiagram>" +
            "<bpmn2:relationship id=\"_GUUj86UCEemjtN0xRqbezg\" type=\"BPSimData\">" +
            "<bpmn2:extensionElements>" +
            "<bpsim:BPSimData>" +
            "<bpsim:Scenario id=\"default\" name=\"Simulationscenario\">" +
            "<bpsim:ScenarioParameters/>" +
            "<bpsim:ElementParameters elementRef=\"_B801DDDE-29E9-41C2-BF36-0045EA55F573\" id=\"_GUUj9KUCEemjtN0xRqbezg\">" +
            "<bpsim:TimeParameters>" +
            "<bpsim:ProcessingTime>" +
            "<bpsim:NormalDistribution mean=\"0\" standardDeviation=\"0\"/>" +
            "</bpsim:ProcessingTime>" +
            "</bpsim:TimeParameters>" +
            "</bpsim:ElementParameters>" +
            "</bpsim:Scenario>" +
            "</bpsim:BPSimData>" +
            "</bpmn2:extensionElements>" +
            "<bpmn2:source>_GUUj8KUCEemjtN0xRqbezg</bpmn2:source>" +
            "<bpmn2:target>_GUUj8KUCEemjtN0xRqbezg</bpmn2:target>" +
            "</bpmn2:relationship>" +
            "</bpmn2:definitions>";

    private static final String SOME_PROCESS_WITH_COMMENTS = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<bpmn2:definitions" +
            " id=\"_GUUj8KUCEemjtN0xRqbezg\"" +
            " exporter=\"jBPM Process Modeler\"" +
            " exporterVersion=\"2.0\"" +
            " targetNamespace=\"http://www.omg.org/bpmn20\"" +
            " xmlns:bpmn2=\"http://www.omg.org/spec/BPMN/20100524/MODEL\"" +
            " xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\"" +
            " xmlns:bpsim=\"http://www.bpsim.org/schemas/1.0\"" +
            " xmlns:dc=\"http://www.omg.org/spec/DD/20100524/DC\"" +
            " xmlns:drools=\"http://www.jboss.org/drools\">" +
            "<bpmn2:process id=\"test.process1\" drools:packageName=\"com.myspace.test\" drools:version=\"1.0\" name=\"process1\" isExecutable=\"true\">" +
            "<bpmn2:startEvent id=\"_B801DDDE-29E9-41C2-BF36-0045EA55F573\"/>" +
            "</bpmn2:process>" +
            "<!-- This is a comment that should be parseable-->" +
            "<bpmndi:BPMNDiagram id=\"_GUUj8aUCEemjtN0xRqbezg\">" +
            "<bpmndi:BPMNPlane id=\"_GUUj8qUCEemjtN0xRqbezg\" bpmnElement=\"test.process1\">" +
            "<bpmndi:BPMNShape id=\"shape__B801DDDE-29E9-41C2-BF36-0045EA55F573\" bpmnElement=\"_B801DDDE-29E9-41C2-BF36-0045EA55F573\">" +
            "<dc:Bounds height=\"56\" width=\"56\" x=\"100\" y=\"100\"/>" +
            "</bpmndi:BPMNShape>" +
            "</bpmndi:BPMNPlane>" +
            "</bpmndi:BPMNDiagram>" +
            "<bpmn2:relationship id=\"_GUUj86UCEemjtN0xRqbezg\" type=\"BPSimData\">" +
            "<bpmn2:extensionElements>" +
            "<bpsim:BPSimData>" +
            "<bpsim:Scenario id=\"default\" name=\"Simulationscenario\">" +
            "<bpsim:ScenarioParameters/>" +
            "<bpsim:ElementParameters elementRef=\"_B801DDDE-29E9-41C2-BF36-0045EA55F573\" id=\"_GUUj9KUCEemjtN0xRqbezg\">" +
            "<bpsim:TimeParameters>" +
            "<bpsim:ProcessingTime>" +
            "<bpsim:NormalDistribution mean=\"0\" standardDeviation=\"0\"/>" +
            "</bpsim:ProcessingTime>" +
            "</bpsim:TimeParameters>" +
            "</bpsim:ElementParameters>" +
            "</bpsim:Scenario>" +
            "</bpsim:BPSimData>" +
            "</bpmn2:extensionElements>" +
            "<bpmn2:source>_GUUj8KUCEemjtN0xRqbezg</bpmn2:source>" +
            "<bpmn2:target>_GUUj8KUCEemjtN0xRqbezg</bpmn2:target>" +
            "</bpmn2:relationship>" +
            "</bpmn2:definitions>";


    @Override
    public String getModuleName() {
        return Bpmn2MarshallingTest.class.getName();
    }
}
