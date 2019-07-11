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

package org.kie.workbench.common.stunner.bpmn.backend.service.diagram.marshalling.events.intermediate;

import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.backend.service.diagram.marshalling.Marshaller;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateMessageEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.message.CancellingMessageEventExecutionSet;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CatchingIntermediateMessageEventTest extends CatchingIntermediateEvent<IntermediateMessageEventCatching> {

    private static final String BPMN_CATCHING_INTERMEDIATE_EVENT_FILE_PATH = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/catchingIntermediateMessageEvents.bpmn";

    private static final String EMPTY_TOP_LEVEL_EVENT_ID = "D9C771AC-6C9D-459F-960F-B3361B75228D";
    private static final String FILLED_TOP_LEVEL_EVENT_ID = "81518D94-1E20-4448-9F4C-FCFB8E416612";
    private static final String EMPTY_SUBPROCESS_LEVEL_EVENT_ID = "9E62353C-C4B6-4341-9A5A-DA0DBB53F9DC";
    private static final String FILLED_SUBPROCESS_LEVEL_EVENT_ID = "465C4B06-20B3-4D90-B42B-CD6EBDCD9702";

    private static final String EMPTY_WITH_INCOME_SUBPROCESS_LEVEL_EVENT_ID = "4FFBDA99-0EF9-4EDD-997A-68BEDB6A4B72";
    private static final String FILLED_WITH_INCOME_SUBPROCESS_LEVEL_EVENT_ID = "D6C1E0FC-D83E-45A2-A052-354781156E5B";
    private static final String EMPTY_WITH_INCOME_TOP_LEVEL_EVENT_ID = "663A5EEE-9A2F-4E49-8D50-9C4FD5318854";
    private static final String FILLED_WITH_INCOME_TOP_LEVEL_EVENT_ID = "FEDEDC9B-C4EE-47B1-89F2-95F9593E534F";

    private static final String SLA_DUE_DATE = "12/25/1983";

    private static final int AMOUNT_OF_NODES_IN_DIAGRAM = 23;

    public CatchingIntermediateMessageEventTest(Marshaller marshallerType) {
        super(marshallerType);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEventFilledProperties() throws Exception {
        final String EVENT_NAME = "message name ~`!@#$%^&*()_+=-{}|\\][:\";'?><,./";
        final String EVENT_DOCUMENTATION = "message documentation\n ~`!@#$%^&*()_+=-{}|\\][:\";'?><,./";
        final String EVENT_REF = "message1";
        final String EVENT_DATA_OUTPUT = "||message01:String||[dout]message01->processGlobalVar";

        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_CATCHING_INTERMEDIATE_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateMessageEventCatching filledTopEvent = getCatchingIntermediateNodeById(diagram,
                                                                                          FILLED_TOP_LEVEL_EVENT_ID,
                                                                                          HAS_NO_INCOME_EDGE,
                                                                                          HAS_OUTGOING_EDGE);
        assertGeneralSet(filledTopEvent.getGeneral(), EVENT_NAME, EVENT_DOCUMENTATION);
        assertMessageEventExecutionSet(filledTopEvent.getExecutionSet(), EVENT_REF, CANCELLING, SLA_DUE_DATE);
        assertDataIOSet(filledTopEvent.getDataIOSet(), EVENT_DATA_OUTPUT);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEmptyEventProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_CATCHING_INTERMEDIATE_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateMessageEventCatching emptyTopEvent = getCatchingIntermediateNodeById(diagram,
                                                                                         EMPTY_TOP_LEVEL_EVENT_ID,
                                                                                         HAS_NO_INCOME_EDGE,
                                                                                         HAS_OUTGOING_EDGE);
        assertGeneralSet(emptyTopEvent.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertMessageEventExecutionSet(emptyTopEvent.getExecutionSet(), EMPTY_VALUE, CANCELLING, EMPTY_VALUE);
        assertDataIOSet(emptyTopEvent.getDataIOSet(), EMPTY_VALUE);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventFilledProperties() throws Exception {
        final String EVENT_NAME = "message name ~`!@#$%^&*()_+=-{}|\\][:\";'?><,./";
        final String EVENT_DOCUMENTATION = "message documentation\n ~`!@#$%^&*()_+=-{}|\\][:\";'?><,./";
        final String EVENT_REF = "message3";
        final String EVENT_DATA_OUTPUT = "||message03:String||[dout]message03->processGlobalVar";

        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_CATCHING_INTERMEDIATE_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateMessageEventCatching filledSubprocessEvent = getCatchingIntermediateNodeById(diagram,
                                                                                                 FILLED_SUBPROCESS_LEVEL_EVENT_ID,
                                                                                                 HAS_NO_INCOME_EDGE,
                                                                                                 HAS_OUTGOING_EDGE);
        assertGeneralSet(filledSubprocessEvent.getGeneral(), EVENT_NAME, EVENT_DOCUMENTATION);
        assertMessageEventExecutionSet(filledSubprocessEvent.getExecutionSet(), EVENT_REF, CANCELLING, SLA_DUE_DATE);
        assertDataIOSet(filledSubprocessEvent.getDataIOSet(), EVENT_DATA_OUTPUT);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventEmptyProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_CATCHING_INTERMEDIATE_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateMessageEventCatching emptySubprocessEvent = getCatchingIntermediateNodeById(diagram,
                                                                                                EMPTY_SUBPROCESS_LEVEL_EVENT_ID,
                                                                                                HAS_NO_INCOME_EDGE,
                                                                                                HAS_OUTGOING_EDGE);
        assertGeneralSet(emptySubprocessEvent.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertMessageEventExecutionSet(emptySubprocessEvent.getExecutionSet(), EMPTY_VALUE, CANCELLING, EMPTY_VALUE);
        assertDataIOSet(emptySubprocessEvent.getDataIOSet(), EMPTY_VALUE);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEventWithEdgesFilledProperties() throws Exception {
        final String EVENT_NAME = "message name ~`!@#$%^&*()_+=-{}|\\][:\";'?><,./";
        final String EVENT_DOCUMENTATION = "message documentation\n ~`!@#$%^&*()_+=-{}|\\][:\";'?><,./";
        final String EVENT_REF = "message2";
        final String EVENT_DATA_OUTPUT = "||message02:String||[dout]message02->processGlobalVar";

        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_CATCHING_INTERMEDIATE_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateMessageEventCatching filledSubprocessEvent = getCatchingIntermediateNodeById(diagram,
                                                                                                 FILLED_WITH_INCOME_TOP_LEVEL_EVENT_ID,
                                                                                                 HAS_INCOME_EDGE,
                                                                                                 HAS_OUTGOING_EDGE);
        assertGeneralSet(filledSubprocessEvent.getGeneral(), EVENT_NAME, EVENT_DOCUMENTATION);
        assertMessageEventExecutionSet(filledSubprocessEvent.getExecutionSet(), EVENT_REF, CANCELLING, SLA_DUE_DATE);
        assertDataIOSet(filledSubprocessEvent.getDataIOSet(), EVENT_DATA_OUTPUT);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEventWithEdgesEmptyProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_CATCHING_INTERMEDIATE_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateMessageEventCatching emptyEvent = getCatchingIntermediateNodeById(diagram,
                                                                                      EMPTY_WITH_INCOME_TOP_LEVEL_EVENT_ID,
                                                                                      HAS_INCOME_EDGE,
                                                                                      HAS_OUTGOING_EDGE);
        assertGeneralSet(emptyEvent.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertMessageEventExecutionSet(emptyEvent.getExecutionSet(), EMPTY_VALUE, CANCELLING, EMPTY_VALUE);
        assertDataIOSet(emptyEvent.getDataIOSet(), EMPTY_VALUE);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventWithEdgesEmptyProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_CATCHING_INTERMEDIATE_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateMessageEventCatching emptySubprocessEvent = getCatchingIntermediateNodeById(diagram,
                                                                                                EMPTY_WITH_INCOME_SUBPROCESS_LEVEL_EVENT_ID,
                                                                                                HAS_INCOME_EDGE,
                                                                                                HAS_OUTGOING_EDGE);
        assertGeneralSet(emptySubprocessEvent.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertMessageEventExecutionSet(emptySubprocessEvent.getExecutionSet(), EMPTY_VALUE, CANCELLING, EMPTY_VALUE);
        assertDataIOSet(emptySubprocessEvent.getDataIOSet(), EMPTY_VALUE);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventWithEdgesFilledProperties() throws Exception {
        final String EVENT_NAME = "message name ~`!@#$%^&*()_+=-{}|\\][:\";'?><,./";
        final String EVENT_DOCUMENTATION = "message documentation\n ~`!@#$%^&*()_+=-{}|\\][:\";'?><,./";
        final String EVENT_REF = "message4";
        final String EVENT_DATA_OUTPUT = "||message04:String||[dout]message04->processGlobalVar";

        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_CATCHING_INTERMEDIATE_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateMessageEventCatching filledSubprocessEvent = getCatchingIntermediateNodeById(diagram,
                                                                                                 FILLED_WITH_INCOME_SUBPROCESS_LEVEL_EVENT_ID,
                                                                                                 HAS_INCOME_EDGE,
                                                                                                 HAS_OUTGOING_EDGE);
        assertGeneralSet(filledSubprocessEvent.getGeneral(), EVENT_NAME, EVENT_DOCUMENTATION);
        assertMessageEventExecutionSet(filledSubprocessEvent.getExecutionSet(), EVENT_REF, CANCELLING, SLA_DUE_DATE);
        assertDataIOSet(filledSubprocessEvent.getDataIOSet(), EVENT_DATA_OUTPUT);
    }

    @Override
    String getBpmnCatchingIntermediateEventFilePath() {
        return BPMN_CATCHING_INTERMEDIATE_EVENT_FILE_PATH;
    }

    @Override
    Class<IntermediateMessageEventCatching> getCatchingIntermediateEventType() {
        return IntermediateMessageEventCatching.class;
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
    String getFilledTopLevelEventWithEdgesId() {
        return FILLED_WITH_INCOME_TOP_LEVEL_EVENT_ID;
    }

    @Override
    String getEmptyTopLevelEventWithEdgesId() {
        return EMPTY_WITH_INCOME_TOP_LEVEL_EVENT_ID;
    }

    @Override
    String getFilledSubprocessLevelEventWithEdgesId() {
        return FILLED_WITH_INCOME_SUBPROCESS_LEVEL_EVENT_ID;
    }

    @Override
    String getEmptySubprocessLevelEventWithEdgesId() {
        return EMPTY_WITH_INCOME_SUBPROCESS_LEVEL_EVENT_ID;
    }

    private void assertMessageEventExecutionSet(CancellingMessageEventExecutionSet executionSet, String eventName, boolean isCancelling, String slaDueDate) {
        assertNotNull(executionSet);
        assertNotNull(executionSet.getMessageRef());
        assertEquals(eventName, executionSet.getMessageRef().getValue());

        assertEventCancelActivity(executionSet, isCancelling);
        assertTimerEventSlaDueDate(executionSet, slaDueDate);
    }
}
