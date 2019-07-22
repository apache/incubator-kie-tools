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
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateMessageEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.message.CancellingMessageEventExecutionSet;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class BoundaryCatchingIntermediateMessageEventTest extends BoundaryCatchingIntermediateEventTest<IntermediateMessageEventCatching> {

    private static final String BPMN_CATCHING_INTERMEDIATE_EVENT_FILE_PATH = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/boundaryMessageEvents.bpmn";

    private static final String EMPTY_TOP_LEVEL_EVENT_ID = "_7BE9AB24-2B9A-4F2B-A19B-B8447FFDEE67";
    private static final String FILLED_TOP_LEVEL_EVENT_ID = "_33449EF2-534F-44D1-A2E2-8B3F37D34D25";
    private static final String EMPTY_SUBPROCESS_LEVEL_EVENT_ID = "_839ABE26-8FA1-416A-96ED-809918924998";
    private static final String FILLED_SUBPROCESS_LEVEL_EVENT_ID = "_8F760A19-0D09-43B7-92BB-6469DCCA9AA1";

    private static final String EMPTY_WITH_OUTGOING_EDGE_TOP_LEVEL_EVENT_ID = "_DA50630D-D3E1-4450-AFF5-15A040162764";
    private static final String FILLED_WITH_OUTGOING_EDGE_TOP_LEVEL_EVENT_ID = "_39105190-6879-4782-8B2B-5CCBB0DDDFFF";
    private static final String EMPTY_WITH_OUTGOING_EDGE_SUBPROCESS_LEVEL_EVENT_ID = "_0E6B0C33-1B06-463C-92B2-D3C833A69EC9";
    private static final String FILLED_WITH_OUTGOING_EDGE_SUBPROCESS_LEVEL_EVENT_ID = "_8D5F7444-C0A4-4584-8C9D-CA9D8A76FABC";

    private static final String SLA_DUE_DATE = "12/25/1983";

    private static final int AMOUNT_OF_NODES_IN_DIAGRAM = 27;

    @Test
    @Override
    public void testUnmarshallTopLevelEventFilledProperties() throws Exception {
        final String EVENT_NAME = "Boundary message01 ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION = "message01 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String EVENT_REF = "message01";
        final String EVENT_DATA_OUTPUT = "||message01:String||[dout]message01->processGlobalVar";

        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_CATCHING_INTERMEDIATE_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateMessageEventCatching filledTopEvent = getCatchingIntermediateNodeById(diagram,
                                                                                          FILLED_TOP_LEVEL_EVENT_ID,
                                                                                          HAS_NO_INCOME_EDGE,
                                                                                          HAS_NO_OUTGOING_EDGE);
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
                                                                                         HAS_NO_OUTGOING_EDGE);
        assertGeneralSet(emptyTopEvent.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertMessageEventExecutionSet(emptyTopEvent.getExecutionSet(), EMPTY_VALUE, NON_CANCELLING, EMPTY_VALUE);
        assertDataIOSet(emptyTopEvent.getDataIOSet(), EMPTY_VALUE);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventFilledProperties() throws Exception {
        final String EVENT_NAME = "Boundary message03 ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION = "message03 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String EVENT_REF = "message03";
        final String EVENT_DATA_OUTPUT = "||message03:String||[dout]message03->processGlobalVar";

        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_CATCHING_INTERMEDIATE_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateMessageEventCatching filledSubprocessEvent = getCatchingIntermediateNodeById(diagram,
                                                                                                 FILLED_SUBPROCESS_LEVEL_EVENT_ID,
                                                                                                 HAS_NO_INCOME_EDGE,
                                                                                                 HAS_NO_OUTGOING_EDGE);
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
                                                                                                HAS_NO_OUTGOING_EDGE);
        assertGeneralSet(emptySubprocessEvent.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertMessageEventExecutionSet(emptySubprocessEvent.getExecutionSet(), EMPTY_VALUE, NON_CANCELLING, EMPTY_VALUE);
        assertDataIOSet(emptySubprocessEvent.getDataIOSet(), EMPTY_VALUE);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEventWithEdgesFilledProperties() throws Exception {
        final String EVENT_NAME = "Boundary message02 ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION = "message02 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String EVENT_REF = "message02";
        final String EVENT_DATA_OUTPUT = "||message02:String||[dout]message02->processGlobalVar";

        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_CATCHING_INTERMEDIATE_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateMessageEventCatching filledSubprocessEvent = getCatchingIntermediateNodeById(diagram,
                                                                                                 FILLED_WITH_OUTGOING_EDGE_TOP_LEVEL_EVENT_ID,
                                                                                                 HAS_NO_INCOME_EDGE,
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
                                                                                      EMPTY_WITH_OUTGOING_EDGE_TOP_LEVEL_EVENT_ID,
                                                                                      HAS_NO_INCOME_EDGE,
                                                                                      HAS_OUTGOING_EDGE);
        assertGeneralSet(emptyEvent.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertMessageEventExecutionSet(emptyEvent.getExecutionSet(), EMPTY_VALUE, NON_CANCELLING, EMPTY_VALUE);
        assertDataIOSet(emptyEvent.getDataIOSet(), EMPTY_VALUE);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventWithEdgesEmptyProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_CATCHING_INTERMEDIATE_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateMessageEventCatching emptySubprocessEvent = getCatchingIntermediateNodeById(diagram,
                                                                                                EMPTY_WITH_OUTGOING_EDGE_SUBPROCESS_LEVEL_EVENT_ID,
                                                                                                HAS_NO_INCOME_EDGE,
                                                                                                HAS_OUTGOING_EDGE);
        assertGeneralSet(emptySubprocessEvent.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertMessageEventExecutionSet(emptySubprocessEvent.getExecutionSet(), EMPTY_VALUE, NON_CANCELLING, EMPTY_VALUE);
        assertDataIOSet(emptySubprocessEvent.getDataIOSet(), EMPTY_VALUE);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventWithEdgesFilledProperties() throws Exception {
        final String EVENT_NAME = "Boundary message04 ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION = "message04 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String EVENT_REF = "message04";
        final String EVENT_DATA_OUTPUT = "||message04:String||[dout]message04->processGlobalVar";

        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_CATCHING_INTERMEDIATE_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateMessageEventCatching filledSubprocessEvent = getCatchingIntermediateNodeById(diagram,
                                                                                                 FILLED_WITH_OUTGOING_EDGE_SUBPROCESS_LEVEL_EVENT_ID,
                                                                                                 HAS_NO_INCOME_EDGE,
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
        return FILLED_WITH_OUTGOING_EDGE_TOP_LEVEL_EVENT_ID;
    }

    @Override
    String getEmptyTopLevelEventWithEdgesId() {
        return EMPTY_WITH_OUTGOING_EDGE_TOP_LEVEL_EVENT_ID;
    }

    @Override
    String getFilledSubprocessLevelEventWithEdgesId() {
        return FILLED_WITH_OUTGOING_EDGE_SUBPROCESS_LEVEL_EVENT_ID;
    }

    @Override
    String getEmptySubprocessLevelEventWithEdgesId() {
        return EMPTY_WITH_OUTGOING_EDGE_SUBPROCESS_LEVEL_EVENT_ID;
    }

    private void assertMessageEventExecutionSet(CancellingMessageEventExecutionSet executionSet, String eventName, boolean isCancelling, String slaDueDate) {
        assertNotNull(executionSet);
        assertNotNull(executionSet.getMessageRef());
        assertEquals(eventName, executionSet.getMessageRef().getValue());

        assertEventCancelActivity(executionSet, isCancelling);
        assertTimerEventSlaDueDate(executionSet, slaDueDate);
    }
}
