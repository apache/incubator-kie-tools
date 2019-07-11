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

public class CatchingIntermediateSignalEventTest extends CatchingIntermediateEvent<IntermediateSignalEventCatching> {

    private static final String BPMN_CATCHING_INTERMEDIATE_EVENT_FILE_PATH = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/catchingIntermediateSignalEvents.bpmn";

    private static final String EMPTY_TOP_LEVEL_EVENT_ID = "061A48FF-5BDC-4A9F-B4A4-1379FBBB23C5";
    private static final String FILLED_TOP_LEVEL_EVENT_ID = "A45D6C7A-24BE-46AA-A6B9-25C480F7FFB5";
    private static final String EMPTY_SUBPROCESS_LEVEL_EVENT_ID = "FFA6848C-267E-46FA-806B-DF1F38BA3A3B";
    private static final String FILLED_SUBPROCESS_LEVEL_EVENT_ID = "85C587F5-5C3A-4F48-B21B-887CF19FF3F2";

    private static final String EMPTY_WITH_INCOME_SUBPROCESS_LEVEL_EVENT_ID = "3DC11DED-4E87-4ECD-B2DA-F8AAB279C372";
    private static final String FILLED_WITH_INCOME_SUBPROCESS_LEVEL_EVENT_ID = "A1A7E835-EBDA-4727-AAA9-7E230541B0F8";
    private static final String EMPTY_WITH_INCOME_TOP_LEVEL_EVENT_ID = "C9F04A4E-107B-41B0-8A38-2CA7CD8D327C";
    private static final String FILLED_WITH_INCOME_TOP_LEVEL_EVENT_ID = "4257742E-5360-4D20-968A-FE14E68D9991";

    private static final String SLA_DUE_DATE = "12/25/1983";

    private static final int AMOUNT_OF_NODES_IN_DIAGRAM = 23;

    public CatchingIntermediateSignalEventTest(Marshaller marshallerType) {
        super(marshallerType);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEventFilledProperties() throws Exception {
        final String EVENT_NAME = "signal name ~`!@#$%^&*()_+=-{}|\\][:\";'?><,./";
        final String EVENT_DOCUMENTATION = "signal documentation\n ~`!@#$%^&*()_+=-{}|\\][:\";'?><,./";
        final String EVENT_REF = "signal1";
        final String EVENT_DATA_OUTPUT = "||signal01:String||[dout]signal01->processGlobalVar";

        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_CATCHING_INTERMEDIATE_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateSignalEventCatching filledTopEvent = getCatchingIntermediateNodeById(diagram,
                                                                                         FILLED_TOP_LEVEL_EVENT_ID,
                                                                                         HAS_NO_INCOME_EDGE,
                                                                                         HAS_OUTGOING_EDGE);
        assertGeneralSet(filledTopEvent.getGeneral(), EVENT_NAME, EVENT_DOCUMENTATION);
        assertSignalEventExecutionSet(filledTopEvent.getExecutionSet(), EVENT_REF, CANCELLING, SLA_DUE_DATE);
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
                                                                                        HAS_OUTGOING_EDGE);
        assertGeneralSet(emptyTopEvent.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertSignalEventExecutionSet(emptyTopEvent.getExecutionSet(), EMPTY_VALUE, CANCELLING, EMPTY_VALUE);
        assertDataIOSet(emptyTopEvent.getDataIOSet(), EMPTY_VALUE);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventFilledProperties() throws Exception {
        final String EVENT_NAME = "signal name ~`!@#$%^&*()_+=-{}|\\][:\";'?><,./";
        final String EVENT_DOCUMENTATION = "signal documentation\n ~`!@#$%^&*()_+=-{}|\\][:\";'?><,./";
        final String EVENT_REF = "signal3";
        final String EVENT_DATA_OUTPUT = "||signal03:String||[dout]signal03->processGlobalVar";

        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_CATCHING_INTERMEDIATE_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateSignalEventCatching filledSubprocessEvent = getCatchingIntermediateNodeById(diagram,
                                                                                                FILLED_SUBPROCESS_LEVEL_EVENT_ID,
                                                                                                HAS_NO_INCOME_EDGE,
                                                                                                HAS_OUTGOING_EDGE);
        assertGeneralSet(filledSubprocessEvent.getGeneral(), EVENT_NAME, EVENT_DOCUMENTATION);
        assertSignalEventExecutionSet(filledSubprocessEvent.getExecutionSet(), EVENT_REF, CANCELLING, SLA_DUE_DATE);
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
                                                                                               HAS_OUTGOING_EDGE);
        assertGeneralSet(emptySubprocessEvent.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertSignalEventExecutionSet(emptySubprocessEvent.getExecutionSet(), EMPTY_VALUE, CANCELLING, EMPTY_VALUE);
        assertDataIOSet(emptySubprocessEvent.getDataIOSet(), EMPTY_VALUE);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEventWithEdgesFilledProperties() throws Exception {
        final String EVENT_NAME = "signal name ~`!@#$%^&*()_+=-{}|\\][:\";'?><,./";
        final String EVENT_DOCUMENTATION = "signal documentation\n ~`!@#$%^&*()_+=-{}|\\][:\";'?><,./";
        final String EVENT_REF = "signal2";
        final String EVENT_DATA_OUTPUT = "||signal02:String||[dout]signal02->processGlobalVar";

        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_CATCHING_INTERMEDIATE_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateSignalEventCatching filledSubprocessEvent = getCatchingIntermediateNodeById(diagram,
                                                                                                FILLED_WITH_INCOME_TOP_LEVEL_EVENT_ID,
                                                                                                HAS_INCOME_EDGE,
                                                                                                HAS_OUTGOING_EDGE);
        assertGeneralSet(filledSubprocessEvent.getGeneral(), EVENT_NAME, EVENT_DOCUMENTATION);
        assertSignalEventExecutionSet(filledSubprocessEvent.getExecutionSet(), EVENT_REF, CANCELLING, SLA_DUE_DATE);
        assertDataIOSet(filledSubprocessEvent.getDataIOSet(), EVENT_DATA_OUTPUT);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEventWithEdgesEmptyProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_CATCHING_INTERMEDIATE_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateSignalEventCatching emptyEvent = getCatchingIntermediateNodeById(diagram,
                                                                                     EMPTY_WITH_INCOME_TOP_LEVEL_EVENT_ID,
                                                                                     HAS_INCOME_EDGE,
                                                                                     HAS_OUTGOING_EDGE);
        assertGeneralSet(emptyEvent.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertSignalEventExecutionSet(emptyEvent.getExecutionSet(), EMPTY_VALUE, CANCELLING, EMPTY_VALUE);
        assertDataIOSet(emptyEvent.getDataIOSet(), EMPTY_VALUE);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventWithEdgesEmptyProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_CATCHING_INTERMEDIATE_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateSignalEventCatching emptySubprocessEvent = getCatchingIntermediateNodeById(diagram,
                                                                                               EMPTY_WITH_INCOME_SUBPROCESS_LEVEL_EVENT_ID,
                                                                                               HAS_INCOME_EDGE,
                                                                                               HAS_OUTGOING_EDGE);
        assertGeneralSet(emptySubprocessEvent.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertSignalEventExecutionSet(emptySubprocessEvent.getExecutionSet(), EMPTY_VALUE, CANCELLING, EMPTY_VALUE);
        assertDataIOSet(emptySubprocessEvent.getDataIOSet(), EMPTY_VALUE);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventWithEdgesFilledProperties() throws Exception {
        final String EVENT_NAME = "signal name ~`!@#$%^&*()_+=-{}|\\][:\";'?><,./";
        final String EVENT_DOCUMENTATION = "signal documentation\n ~`!@#$%^&*()_+=-{}|\\][:\";'?><,./";
        final String EVENT_REF = "signal4";
        final String EVENT_DATA_OUTPUT = "||signal04:String||[dout]signal04->processGlobalVar";

        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_CATCHING_INTERMEDIATE_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateSignalEventCatching filledSubprocessEvent = getCatchingIntermediateNodeById(diagram,
                                                                                                FILLED_WITH_INCOME_SUBPROCESS_LEVEL_EVENT_ID,
                                                                                                HAS_INCOME_EDGE,
                                                                                                HAS_OUTGOING_EDGE);
        assertGeneralSet(filledSubprocessEvent.getGeneral(), EVENT_NAME, EVENT_DOCUMENTATION);
        assertSignalEventExecutionSet(filledSubprocessEvent.getExecutionSet(), EVENT_REF, CANCELLING, SLA_DUE_DATE);
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

    private void assertSignalEventExecutionSet(CancellingSignalEventExecutionSet executionSet, String eventName, boolean isCancelling, String slaDueDate) {
        assertNotNull(executionSet);
        assertNotNull(executionSet.getSignalRef());
        assertEquals(eventName, executionSet.getSignalRef().getValue());

        assertEventCancelActivity(executionSet, isCancelling);
        assertTimerEventSlaDueDate(executionSet, slaDueDate);
    }
}
