/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.backend.service.diagram.marshalling.subprocesses;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.backend.service.diagram.Unmarshalling;
import org.kie.workbench.common.stunner.bpmn.backend.service.diagram.marshalling.BPMNDiagramMarshallerBase;
import org.kie.workbench.common.stunner.bpmn.definition.EventSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.StartErrorEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartMessageEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartSignalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.subProcess.execution.EventSubprocessExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.ProcessData;
import org.kie.workbench.common.stunner.core.definition.service.DiagramMarshaller;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class EventSubProcessTest extends BPMNDiagramMarshallerBase {

    private static final String BPMN_EVENT_SUBPROCESSES =
            "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/eventSubProcesses.bpmn";
    private static final String BPMN_EVENT_SUBPROCESS_SPECIAL_CHARACTERS =
            "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/eventSubProcessSpecialCharacters.bpmn";

    @Before
    public void setUp() {
        super.init();
    }

    @Test
    public void testOldMarshaller() throws Exception {
        unmarshallEventSubprocessStartEvents(oldMarshaller);
    }

    @Test
    public void testNewMarshaller() throws Exception {
        unmarshallEventSubprocessStartEvents(newMarshaller);
    }

    @Test
    public void testOldMarshallerSpecialCharacters() throws Exception {
        unmarshallEventSubprocessSpecialCharacters(oldMarshaller);
    }

    @Test
    public void testNewMarshallerSpecialCharacters() throws Exception {
        unmarshallEventSubprocessSpecialCharacters(newMarshaller);
    }

    @Test
    public void testMigration() throws Exception {
        Diagram<Graph, Metadata> oldDiagram = Unmarshalling.unmarshall(oldMarshaller, BPMN_EVENT_SUBPROCESSES);
        Diagram<Graph, Metadata> newDiagram = Unmarshalling.unmarshall(newMarshaller, BPMN_EVENT_SUBPROCESSES);

        assertDiagramEquals(oldDiagram, newDiagram, BPMN_EVENT_SUBPROCESSES);
    }

    @Test
    public void testMigrationSpecialCharacters() throws Exception {
        Diagram<Graph, Metadata> oldDiagram = Unmarshalling.unmarshall(oldMarshaller, BPMN_EVENT_SUBPROCESS_SPECIAL_CHARACTERS);
        Diagram<Graph, Metadata> newDiagram = Unmarshalling.unmarshall(newMarshaller, BPMN_EVENT_SUBPROCESS_SPECIAL_CHARACTERS);

        assertDiagramEquals(oldDiagram, newDiagram, BPMN_EVENT_SUBPROCESS_SPECIAL_CHARACTERS);
    }

    @SuppressWarnings("unchecked")
    private void unmarshallEventSubprocessStartEvents(final DiagramMarshaller marshaller) throws Exception {
        final String ERROR_ID = "_CB6A1195-C5CE-4EEB-AAD7-81E5206B5C7E";
        final String SIGNAL_ID = "_A5A451B8-ADD4-4901-BAC3-047421A467E6";
        final String SIGNAL_INTERRUPTING_ID = "_F9D7DEF1-5E2E-4097-9743-83F8ABE22F84";
        final String MESSAGE_ID = "_1C802556-6FF9-4D03-8D4D-BDCF4DD1E264";
        final String MESSAGE_INTERRUPTING_ID = "_08BAB054-DD82-45F4-BDA0-3809EEF134BD";

        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_EVENT_SUBPROCESSES);

        Node<? extends Definition, ?> errorNode = diagram.getGraph().getNode(ERROR_ID);
        StartErrorEvent startError = (StartErrorEvent) errorNode.getContent().getDefinition();
        assertTrue(startError.getExecutionSet().getIsInterrupting().getValue());
        assertEquals("standardError",
                     startError.getExecutionSet().getErrorRef().getValue());
        assertEquals("error",
                     startError.getGeneral().getName().getValue());

        Node<? extends Definition, ?> signalOneNode = diagram.getGraph().getNode(SIGNAL_ID);
        StartSignalEvent startSignal = (StartSignalEvent) signalOneNode.getContent().getDefinition();
        assertFalse(startSignal.getExecutionSet().getIsInterrupting().getValue());
        assertEquals("standardSignal",
                     startSignal.getExecutionSet().getSignalRef().getValue());
        assertEquals("signalOne",
                     startSignal.getGeneral().getName().getValue());

        Node<? extends Definition, ?> signalTwoNode = diagram.getGraph().getNode(SIGNAL_INTERRUPTING_ID);
        StartSignalEvent startSignalInterrupting = (StartSignalEvent) signalTwoNode.getContent().getDefinition();
        assertTrue(startSignalInterrupting.getExecutionSet().getIsInterrupting().getValue());
        assertEquals("standardSignal",
                     startSignalInterrupting.getExecutionSet().getSignalRef().getValue());
        assertEquals("signal two",
                     startSignalInterrupting.getGeneral().getName().getValue());

        Node<? extends Definition, ?> messageOneNode = diagram.getGraph().getNode(MESSAGE_ID);
        StartMessageEvent startMessage = (StartMessageEvent) messageOneNode.getContent().getDefinition();
        assertFalse(startMessage.getExecutionSet().getIsInterrupting().getValue());
        assertEquals("standardMessage",
                     startMessage.getExecutionSet().getMessageRef().getValue());
        assertEquals("message one node",
                     startMessage.getGeneral().getName().getValue());

        Node<? extends Definition, ?> messageTwoNode = diagram.getGraph().getNode(MESSAGE_INTERRUPTING_ID);
        StartMessageEvent startMessageInterrupting = (StartMessageEvent) messageTwoNode.getContent().getDefinition();
        assertTrue(startMessageInterrupting.getExecutionSet().getIsInterrupting().getValue());
        assertEquals("standardMessage",
                     startMessageInterrupting.getExecutionSet().getMessageRef().getValue());
        assertEquals("message two node",
                     startMessageInterrupting.getGeneral().getName().getValue());
    }

    @SuppressWarnings("unchecked")
    private void unmarshallEventSubprocessSpecialCharacters(final DiagramMarshaller marshaller) throws
            Exception {
        final String EVENT_SUBPROCESS_ID = "_FFD7F3F0-4B66-4F11-87A5-0193C2DE9EBE";

        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_EVENT_SUBPROCESS_SPECIAL_CHARACTERS);

        Node<? extends Definition, ?> eventSubProcessNode = diagram.getGraph().getNode(EVENT_SUBPROCESS_ID);
        EventSubprocess eventSubprocess = (EventSubprocess) eventSubProcessNode.getContent().getDefinition();

        assertNotNull(eventSubprocess);

        BPMNGeneralSet generalSet = eventSubprocess.getGeneral();
        EventSubprocessExecutionSet executionSet = eventSubprocess.getExecutionSet();
        ProcessData processData = eventSubprocess.getProcessData();

        assertEquals("~`!@#$%^&*()_+|}{[]\":;'<>?/.,",
                     generalSet.getName().getValue());
        assertEquals("式\nmultiline\n式",
                     generalSet.getDocumentation().getValue());

        assertTrue(executionSet.getIsAsync().getValue());

        assertEquals("localVariable:Integer",
                     processData.getProcessVariables().getValue());
    }
}
