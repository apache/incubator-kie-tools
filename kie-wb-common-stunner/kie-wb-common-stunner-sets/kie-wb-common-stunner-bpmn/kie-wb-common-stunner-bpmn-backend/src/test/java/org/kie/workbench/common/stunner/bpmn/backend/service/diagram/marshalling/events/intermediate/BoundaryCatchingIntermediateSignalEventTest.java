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
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateSignalEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.signal.CancellingSignalEventExecutionSet;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class BoundaryCatchingIntermediateSignalEventTest extends BoundaryCatchingIntermediateEvent<IntermediateSignalEventCatching> {

    private static final String BPMN_CATCHING_INTERMEDIATE_EVENT_FILE_PATH = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/boundarySignalEvents.bpmn";

    private static final String EMPTY_TOP_LEVEL_EVENT_ID = "_53B585C7-7515-4842-9312-29C830D8FE8C";
    private static final String FILLED_TOP_LEVEL_EVENT_ID = "_7DA9CD2F-8A42-41FA-AB34-4FBD0688E095";
    private static final String EMPTY_SUBPROCESS_LEVEL_EVENT_ID = "_E852903C-50C5-4969-9C55-DE998FA5300B";
    private static final String FILLED_SUBPROCESS_LEVEL_EVENT_ID = "_0541428C-F510-4AC3-AC4A-27DBAC940406";

    private static final String EMPTY_WITH_OUTGOING_EDGE_TOP_LEVEL_EVENT_ID = "_B6249505-965A-4F69-9C15-154D12C0CF3D";
    private static final String FILLED_WITH_OUTGOING_EDGE_TOP_LEVEL_EVENT_ID = "_2DFEFD1C-3E0D-4BCB-A250-C7BDAB741B55";
    private static final String EMPTY_WITH_OUTGOING_EDGE_SUBPROCESS_LEVEL_EVENT_ID = "_4F8B8564-5EA5-494D-9E86-3154F1C0D7C8";
    private static final String FILLED_WITH_OUTGOING_EDGE_SUBPROCESS_LEVEL_EVENT_ID = "_73B535D0-C805-4F76-B00E-22D6DEE29C8F";

    private static final int AMOUNT_OF_NODES_IN_DIAGRAM = 31;

    public BoundaryCatchingIntermediateSignalEventTest(Marshaller marshallerType) {
        super(marshallerType);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEventFilledProperties() throws Exception {
        final String EVENT_NAME = "Boundary signal01 ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION = "signal01 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String EVENT_REF = "signal01";
        final String EVENT_DATA_OUTPUT = "||signal01:String||[dout]signal01->processGlobalVar";

        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_CATCHING_INTERMEDIATE_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateSignalEventCatching filledTopEvent = getCatchingIntermediateNodeById(diagram,
                                                                                         FILLED_TOP_LEVEL_EVENT_ID,
                                                                                         HAS_NO_INCOME_EDGE,
                                                                                         HAS_NO_OUTGOING_EDGE);
        assertGeneralSet(filledTopEvent.getGeneral(), EVENT_NAME, EVENT_DOCUMENTATION);
        assertSignalEventExecutionSet(filledTopEvent.getExecutionSet(), EVENT_REF, CANCELLING);
        assertDataIOSet(filledTopEvent.getDataIOSet(), EVENT_DATA_OUTPUT);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEmptyEventProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_CATCHING_INTERMEDIATE_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateSignalEventCatching emptyTopEvent = getCatchingIntermediateNodeById(diagram,
                                                                                        EMPTY_TOP_LEVEL_EVENT_ID,
                                                                                        HAS_NO_INCOME_EDGE,
                                                                                        HAS_NO_OUTGOING_EDGE);
        assertGeneralSet(emptyTopEvent.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertSignalEventExecutionSet(emptyTopEvent.getExecutionSet(), EMPTY_VALUE, NON_CANCELLING);
        assertDataIOSet(emptyTopEvent.getDataIOSet(), EMPTY_VALUE);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventFilledProperties() throws Exception {
        final String EVENT_NAME = "Boundary signal03 ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION = "signal03 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String EVENT_REF = "signal03";
        final String EVENT_DATA_OUTPUT = "||signal03:String||[dout]signal03->processGlobalVar";

        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_CATCHING_INTERMEDIATE_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateSignalEventCatching filledSubprocessEvent = getCatchingIntermediateNodeById(diagram,
                                                                                                FILLED_SUBPROCESS_LEVEL_EVENT_ID,
                                                                                                HAS_NO_INCOME_EDGE,
                                                                                                HAS_NO_OUTGOING_EDGE);
        assertGeneralSet(filledSubprocessEvent.getGeneral(), EVENT_NAME, EVENT_DOCUMENTATION);
        assertSignalEventExecutionSet(filledSubprocessEvent.getExecutionSet(), EVENT_REF, CANCELLING);
        assertDataIOSet(filledSubprocessEvent.getDataIOSet(), EVENT_DATA_OUTPUT);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventEmptyProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_CATCHING_INTERMEDIATE_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateSignalEventCatching emptySubprocessEvent = getCatchingIntermediateNodeById(diagram,
                                                                                               EMPTY_SUBPROCESS_LEVEL_EVENT_ID,
                                                                                               HAS_NO_INCOME_EDGE,
                                                                                               HAS_NO_OUTGOING_EDGE);
        assertGeneralSet(emptySubprocessEvent.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertSignalEventExecutionSet(emptySubprocessEvent.getExecutionSet(), EMPTY_VALUE, NON_CANCELLING);
        assertDataIOSet(emptySubprocessEvent.getDataIOSet(), EMPTY_VALUE);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEventWithEdgesFilledProperties() throws Exception {
        final String EVENT_NAME = "Boundary signal02 ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION = "signal02 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String EVENT_REF = "signal02";
        final String EVENT_DATA_OUTPUT = "||signal02:String||[dout]signal02->processGlobalVar";

        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_CATCHING_INTERMEDIATE_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateSignalEventCatching filledSubprocessEvent = getCatchingIntermediateNodeById(diagram,
                                                                                                FILLED_WITH_OUTGOING_EDGE_TOP_LEVEL_EVENT_ID,
                                                                                                HAS_NO_INCOME_EDGE,
                                                                                                HAS_OUTGOING_EDGE);
        assertGeneralSet(filledSubprocessEvent.getGeneral(), EVENT_NAME, EVENT_DOCUMENTATION);
        assertSignalEventExecutionSet(filledSubprocessEvent.getExecutionSet(), EVENT_REF, CANCELLING);
        assertDataIOSet(filledSubprocessEvent.getDataIOSet(), EVENT_DATA_OUTPUT);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEventWithEdgesEmptyProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_CATCHING_INTERMEDIATE_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateSignalEventCatching emptyEvent = getCatchingIntermediateNodeById(diagram,
                                                                                     EMPTY_WITH_OUTGOING_EDGE_TOP_LEVEL_EVENT_ID,
                                                                                     HAS_NO_INCOME_EDGE,
                                                                                     HAS_OUTGOING_EDGE);
        assertGeneralSet(emptyEvent.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertSignalEventExecutionSet(emptyEvent.getExecutionSet(), EMPTY_VALUE, NON_CANCELLING);
        assertDataIOSet(emptyEvent.getDataIOSet(), EMPTY_VALUE);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventWithEdgesEmptyProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_CATCHING_INTERMEDIATE_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateSignalEventCatching emptySubprocessEvent = getCatchingIntermediateNodeById(diagram,
                                                                                               EMPTY_WITH_OUTGOING_EDGE_SUBPROCESS_LEVEL_EVENT_ID,
                                                                                               HAS_NO_INCOME_EDGE,
                                                                                               HAS_OUTGOING_EDGE);
        assertGeneralSet(emptySubprocessEvent.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertSignalEventExecutionSet(emptySubprocessEvent.getExecutionSet(), EMPTY_VALUE, NON_CANCELLING);
        assertDataIOSet(emptySubprocessEvent.getDataIOSet(), EMPTY_VALUE);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventWithEdgesFilledProperties() throws Exception {
        final String EVENT_NAME = "Boundary signal04 ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION = "signal04 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String EVENT_REF = "signal04";
        final String EVENT_DATA_OUTPUT = "||signal04:String||[dout]signal04->processGlobalVar";

        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_CATCHING_INTERMEDIATE_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateSignalEventCatching filledSubprocessEvent = getCatchingIntermediateNodeById(diagram,
                                                                                                FILLED_WITH_OUTGOING_EDGE_SUBPROCESS_LEVEL_EVENT_ID,
                                                                                                HAS_NO_INCOME_EDGE,
                                                                                                HAS_OUTGOING_EDGE);
        assertGeneralSet(filledSubprocessEvent.getGeneral(), EVENT_NAME, EVENT_DOCUMENTATION);
        assertSignalEventExecutionSet(filledSubprocessEvent.getExecutionSet(), EVENT_REF, CANCELLING);
        assertDataIOSet(filledSubprocessEvent.getDataIOSet(), EVENT_DATA_OUTPUT);
    }

    @Override
    String getBpmnCatchingIntermediateEventFilePath() {
        return BPMN_CATCHING_INTERMEDIATE_EVENT_FILE_PATH;
    }

    @Override
    Class<IntermediateSignalEventCatching> getCatchingIntermediateEventType() {
        return IntermediateSignalEventCatching.class;
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

    private void assertSignalEventExecutionSet(CancellingSignalEventExecutionSet executionSet, String eventName, boolean isCancelling) {
        assertNotNull(executionSet);
        assertNotNull(executionSet.getSignalRef());
        assertNotNull(executionSet.getCancelActivity());
        assertEquals(eventName, executionSet.getSignalRef().getValue());
        assertEquals(isCancelling, executionSet.getCancelActivity().getValue());
    }
}
