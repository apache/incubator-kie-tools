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
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateEscalationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.escalation.CancellingEscalationEventExecutionSet;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CatchingIntermediateEscalationEventTest extends CatchingIntermediateEvent<IntermediateEscalationEvent> {

    private static final String BPMN_CATCHING_INTERMEDIATE_EVENT_FILE_PATH = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/catchingIntermediateEscalationEvents.bpmn";

    private static final String EMPTY_TOP_LEVEL_EVENT_ID = "_5D25E05B-8085-451B-BDCC-1E945BB5D21F";
    private static final String FILLED_TOP_LEVEL_EVENT_ID = "_AC6A3989-D050-4385-B011-B05291F02964";
    private static final String EMPTY_SUBPROCESS_LEVEL_EVENT_ID = "_9AEC54C1-7E9A-4763-992E-DFD6330451EE";
    private static final String FILLED_SUBPROCESS_LEVEL_EVENT_ID = "_DC14DEB1-483D-455B-95E4-882073C92823";

    private static final String EMPTY_WITH_INCOME_SUBPROCESS_LEVEL_EVENT_ID = "_0B297A34-733E-48CD-B9FE-7531C036BC3C";
    private static final String FILLED_WITH_INCOME_SUBPROCESS_LEVEL_EVENT_ID = "_2155B7EA-648A-4B49-B784-DE3E04E47B48";
    private static final String EMPTY_WITH_INCOME_TOP_LEVEL_EVENT_ID = "_C9A91991-AF34-489E-A7C9-1840DF0D8E6E";
    private static final String FILLED_WITH_INCOME_TOP_LEVEL_EVENT_ID = "_13BC7C98-398C-47CE-B4FE-CF388F7159BC";

    private static final String SLA_DUE_DATE = "12/25/1983";

    private static final int AMOUNT_OF_NODES_IN_DIAGRAM = 23;

    public CatchingIntermediateEscalationEventTest(Marshaller marshallerType) {
        super(marshallerType);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEventFilledProperties() throws Exception {
        final String EVENT_NAME = "Escalation event01 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION = "Escalation event01 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String EVENT_REF = "escalation01";
        final String EVENT_DATA_OUTPUT = "||output:String||[dout]output->processGlobalVar";

        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_CATCHING_INTERMEDIATE_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateEscalationEvent filledTopEvent = getCatchingIntermediateNodeById(diagram,
                                                                                     FILLED_TOP_LEVEL_EVENT_ID,
                                                                                     HAS_NO_INCOME_EDGE,
                                                                                     HAS_OUTGOING_EDGE);
        assertGeneralSet(filledTopEvent.getGeneral(), EVENT_NAME, EVENT_DOCUMENTATION);
        assertEscalationEventExecutionSet(filledTopEvent.getExecutionSet(), EVENT_REF, CANCELLING, SLA_DUE_DATE);
        assertDataIOSet(filledTopEvent.getDataIOSet(), EVENT_DATA_OUTPUT);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEmptyEventProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_CATCHING_INTERMEDIATE_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateEscalationEvent emptyTopEvent = getCatchingIntermediateNodeById(diagram,
                                                                                    EMPTY_TOP_LEVEL_EVENT_ID,
                                                                                    HAS_NO_INCOME_EDGE,
                                                                                    HAS_OUTGOING_EDGE);
        assertGeneralSet(emptyTopEvent.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertEscalationEventExecutionSet(emptyTopEvent.getExecutionSet(), EMPTY_VALUE, CANCELLING, EMPTY_VALUE);
        assertDataIOSet(emptyTopEvent.getDataIOSet(), EMPTY_VALUE);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventFilledProperties() throws Exception {
        final String EVENT_NAME = "Escalation event03 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION = "Escalation event03 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String EVENT_REF = "escalation03";
        final String EVENT_DATA_OUTPUT = "||output:String||[dout]output->processGlobalVar";

        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_CATCHING_INTERMEDIATE_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateEscalationEvent filledSubprocessEvent = getCatchingIntermediateNodeById(diagram,
                                                                                            FILLED_SUBPROCESS_LEVEL_EVENT_ID,
                                                                                            HAS_NO_INCOME_EDGE,
                                                                                            HAS_OUTGOING_EDGE);
        assertGeneralSet(filledSubprocessEvent.getGeneral(), EVENT_NAME, EVENT_DOCUMENTATION);
        assertEscalationEventExecutionSet(filledSubprocessEvent.getExecutionSet(), EVENT_REF, CANCELLING, SLA_DUE_DATE);
        assertDataIOSet(filledSubprocessEvent.getDataIOSet(), EVENT_DATA_OUTPUT);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventEmptyProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_CATCHING_INTERMEDIATE_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateEscalationEvent emptySubprocessEvent = getCatchingIntermediateNodeById(diagram,
                                                                                           EMPTY_SUBPROCESS_LEVEL_EVENT_ID,
                                                                                           HAS_NO_INCOME_EDGE,
                                                                                           HAS_OUTGOING_EDGE);
        assertGeneralSet(emptySubprocessEvent.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertEscalationEventExecutionSet(emptySubprocessEvent.getExecutionSet(), EMPTY_VALUE, CANCELLING, EMPTY_VALUE);
        assertDataIOSet(emptySubprocessEvent.getDataIOSet(), EMPTY_VALUE);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEventWithEdgesFilledProperties() throws Exception {
        final String EVENT_NAME = "Escalation event02 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION = "Escalation event02 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String EVENT_REF = "escalation02";
        final String EVENT_DATA_OUTPUT = "||output:String||[dout]output->processGlobalVar";

        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_CATCHING_INTERMEDIATE_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateEscalationEvent filledSubprocessEvent = getCatchingIntermediateNodeById(diagram,
                                                                                            FILLED_WITH_INCOME_TOP_LEVEL_EVENT_ID,
                                                                                            HAS_INCOME_EDGE,
                                                                                            HAS_OUTGOING_EDGE);
        assertGeneralSet(filledSubprocessEvent.getGeneral(), EVENT_NAME, EVENT_DOCUMENTATION);
        assertEscalationEventExecutionSet(filledSubprocessEvent.getExecutionSet(), EVENT_REF, CANCELLING, SLA_DUE_DATE);
        assertDataIOSet(filledSubprocessEvent.getDataIOSet(), EVENT_DATA_OUTPUT);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEventWithEdgesEmptyProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_CATCHING_INTERMEDIATE_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateEscalationEvent emptyEvent = getCatchingIntermediateNodeById(diagram,
                                                                                 EMPTY_WITH_INCOME_TOP_LEVEL_EVENT_ID,
                                                                                 HAS_INCOME_EDGE,
                                                                                 HAS_OUTGOING_EDGE);
        assertGeneralSet(emptyEvent.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertEscalationEventExecutionSet(emptyEvent.getExecutionSet(), EMPTY_VALUE, CANCELLING, EMPTY_VALUE);
        assertDataIOSet(emptyEvent.getDataIOSet(), EMPTY_VALUE);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventWithEdgesEmptyProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_CATCHING_INTERMEDIATE_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateEscalationEvent emptySubprocessEvent = getCatchingIntermediateNodeById(diagram,
                                                                                           EMPTY_WITH_INCOME_SUBPROCESS_LEVEL_EVENT_ID,
                                                                                           HAS_INCOME_EDGE,
                                                                                           HAS_OUTGOING_EDGE);
        assertGeneralSet(emptySubprocessEvent.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertEscalationEventExecutionSet(emptySubprocessEvent.getExecutionSet(), EMPTY_VALUE, CANCELLING, EMPTY_VALUE);
        assertDataIOSet(emptySubprocessEvent.getDataIOSet(), EMPTY_VALUE);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventWithEdgesFilledProperties() throws Exception {
        final String EVENT_NAME = "Escalation event04 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION = "Escalation event04 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String EVENT_REF = "escalation04";
        final String EVENT_DATA_OUTPUT = "||output:String||[dout]output->processGlobalVar";

        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_CATCHING_INTERMEDIATE_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateEscalationEvent filledSubprocessEvent = getCatchingIntermediateNodeById(diagram,
                                                                                            FILLED_WITH_INCOME_SUBPROCESS_LEVEL_EVENT_ID,
                                                                                            HAS_INCOME_EDGE,
                                                                                            HAS_OUTGOING_EDGE);
        assertGeneralSet(filledSubprocessEvent.getGeneral(), EVENT_NAME, EVENT_DOCUMENTATION);
        assertEscalationEventExecutionSet(filledSubprocessEvent.getExecutionSet(), EVENT_REF, CANCELLING, SLA_DUE_DATE);
        assertDataIOSet(filledSubprocessEvent.getDataIOSet(), EVENT_DATA_OUTPUT);
    }

    @Override
    String getBpmnCatchingIntermediateEventFilePath() {
        return BPMN_CATCHING_INTERMEDIATE_EVENT_FILE_PATH;
    }

    @Override
    Class<IntermediateEscalationEvent> getCatchingIntermediateEventType() {
        return IntermediateEscalationEvent.class;
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

    private void assertEscalationEventExecutionSet(CancellingEscalationEventExecutionSet executionSet, String escalationRef, boolean isCancelling, String slaDueDate) {
        assertNotNull(executionSet);
        assertNotNull(executionSet.getEscalationRef());
        assertEquals(escalationRef, executionSet.getEscalationRef().getValue());

        assertEventCancelActivity(executionSet, isCancelling);
        assertTimerEventSlaDueDate(executionSet, slaDueDate);
    }
}
