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

package org.kie.workbench.common.stunner.bpmn.backend.service.diagram.marshalling.events;

import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.backend.service.diagram.marshalling.Marshaller;
import org.kie.workbench.common.stunner.bpmn.definition.StartMessageEvent;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.message.InterruptingMessageEventExecutionSet;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class StartMessageEventTest extends StartEvent<StartMessageEvent> {

    private static final String BPMN_START_EVENT_FILE_PATH = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/messageStartEvents.bpmn";

    private static final String FILLED_TOP_LEVEL_EVENT_ID = "2B967C25-C1FE-4945-8511-7A9E5465BA22";
    private static final String EMPTY_TOP_LEVEL_EVENT_ID = "D78124CD-19B0-45C6-AF0A-CD7C16F4F3BD";
    private static final String FILLED_SUBPROCESS_LEVEL_EVENT_ID = "464FB8BC-F752-4428-A3DC-D5DDCEE2353F";
    private static final String EMPTY_SUBPROCESS_LEVEL_EVENT_ID = "372D12E1-30F9-4504-8ED5-5F7D1735FEDB";

    private static final String SLA_DUE_DATE = "12/25/1983";

    private static final int AMOUNT_OF_NODES_IN_DIAGRAM = 11;

    public StartMessageEventTest(Marshaller marshallerType) {
        super(marshallerType);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEventFilledProperties() throws Exception {
        final String EVENT_NAME = "Message message name ~`!@#$%^&*()_+=-{}|\\][:\";'?><,./";
        final String EVENT_DOCUMENTATION = "Message documentation\n~`!@#$%^&*()_+=-{}|\\][:\";'?><,./\n";
        final String EVENT_REF = "Message1";
        final String EVENT_DATA_OUTPUT = "||messageReceived:String||[dout]messageReceived->helloProcess";

        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_START_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        StartMessageEvent filledTop = getStartNodeById(diagram, FILLED_TOP_LEVEL_EVENT_ID, StartMessageEvent.class);
        assertGeneralSet(filledTop.getGeneral(), EVENT_NAME, EVENT_DOCUMENTATION);
        assertMessageEventExecutionSet(filledTop.getExecutionSet(), EVENT_REF, INTERRUPTING, SLA_DUE_DATE);
        assertDataIOSet(filledTop.getDataIOSet(), EVENT_DATA_OUTPUT);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEmptyEventProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_START_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        StartMessageEvent emptyTop = getStartNodeById(diagram, EMPTY_TOP_LEVEL_EVENT_ID, StartMessageEvent.class);
        assertGeneralSet(emptyTop.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertMessageEventExecutionSet(emptyTop.getExecutionSet(), EMPTY_VALUE, NON_INTERRUPTING, EMPTY_VALUE);
        assertDataIOSet(emptyTop.getDataIOSet(), EMPTY_VALUE);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventFilledProperties() throws Exception {
        final String EVENT_NAME = "Message name ~`!@#$%^&*()_+=-{}|\\][:\";'?><,./";
        final String EVENT_DOCUMENTATION = "Doc is here\n~`!@#$%^&*()_+=-{}|\\][:\";'?><,./\n";
        final String EVENT_REF = "Message2";
        final String EVENT_DATA_OUTPUT = "||messageR:String||[dout]messageR->helloProcess";

        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_START_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        StartMessageEvent filledSubprocess = getStartNodeById(diagram, FILLED_SUBPROCESS_LEVEL_EVENT_ID, StartMessageEvent.class);
        assertGeneralSet(filledSubprocess.getGeneral(), EVENT_NAME, EVENT_DOCUMENTATION);
        assertMessageEventExecutionSet(filledSubprocess.getExecutionSet(), EVENT_REF, INTERRUPTING, SLA_DUE_DATE);
        assertDataIOSet(filledSubprocess.getDataIOSet(), EVENT_DATA_OUTPUT);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventEmptyProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_START_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        StartMessageEvent emptySubprocess = getStartNodeById(diagram, EMPTY_SUBPROCESS_LEVEL_EVENT_ID, StartMessageEvent.class);
        assertGeneralSet(emptySubprocess.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertMessageEventExecutionSet(emptySubprocess.getExecutionSet(), EMPTY_VALUE, NON_INTERRUPTING, EMPTY_VALUE);
        assertDataIOSet(emptySubprocess.getDataIOSet(), EMPTY_VALUE);
    }

    @Override
    String getBpmnStartEventFilePath() {
        return BPMN_START_EVENT_FILE_PATH;
    }

    @Override
    String getFilledTopLevelEventId() {
        return FILLED_TOP_LEVEL_EVENT_ID;
    }

    @Override
    String getEmptyTopLevelEventId() {
        return EMPTY_TOP_LEVEL_EVENT_ID;
    }

    @Override
    String getFilledSubprocessLevelEventId() {
        return FILLED_SUBPROCESS_LEVEL_EVENT_ID;
    }

    @Override
    String getEmptySubprocessLevelEventId() {
        return EMPTY_SUBPROCESS_LEVEL_EVENT_ID;
    }

    @Override
    Class<StartMessageEvent> getStartEventType() {
        return StartMessageEvent.class;
    }

    private void assertMessageEventExecutionSet(InterruptingMessageEventExecutionSet executionSet, String eventName, boolean isInterrupting, String slaDueDate) {
        assertNotNull(executionSet);
        assertNotNull(executionSet.getMessageRef());
        assertEquals(eventName, executionSet.getMessageRef().getValue());

        assertStartEventIsInterrupting(executionSet, isInterrupting);
        assertStartEventSlaDueDate(executionSet, slaDueDate);
    }
}
