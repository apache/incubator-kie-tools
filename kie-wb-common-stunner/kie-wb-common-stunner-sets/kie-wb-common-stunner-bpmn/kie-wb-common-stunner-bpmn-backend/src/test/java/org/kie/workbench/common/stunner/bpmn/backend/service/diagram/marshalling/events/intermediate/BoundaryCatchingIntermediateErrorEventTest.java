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
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateErrorEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.error.CancellingErrorEventExecutionSet;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class BoundaryCatchingIntermediateErrorEventTest extends BoundaryCatchingIntermediateEventTest<IntermediateErrorEventCatching> {

    private static final String BPMN_CATCHING_INTERMEDIATE_EVENT_FILE_PATH = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/boundaryErrorEvents.bpmn";

    private static final String EMPTY_TOP_LEVEL_EVENT_ID = "_DFCE579D-A3FB-4CA3-A41C-8D2C9C3E45E1";
    private static final String FILLED_TOP_LEVEL_EVENT_ID = "_12D62443-49D0-4AEB-B39A-06AB0470E66C";
    private static final String EMPTY_SUBPROCESS_LEVEL_EVENT_ID = "_81024F0B-3151-48B4-B17C-AE7F0CA419A6";
    private static final String FILLED_SUBPROCESS_LEVEL_EVENT_ID = "_2CA5D491-6AD9-42B1-8FE8-B18AA20D7D1C";

    private static final String EMPTY_WITH_OUTGOING_EDGE_TOP_LEVEL_EVENT_ID = "_10E9AA6A-5BAA-4A23-A9E9-63D285677BFC";
    private static final String FILLED_WITH_OUTGOING_EDGE_TOP_LEVEL_EVENT_ID = "_C5C81EDA-355E-4431-97EC-9F0ED0A78685";
    private static final String EMPTY_WITH_OUTGOING_EDGE_SUBPROCESS_LEVEL_EVENT_ID = "_38C1EB4D-2196-487E-A1C6-C3BC16FDC5D6";
    private static final String FILLED_WITH_OUTGOING_EDGE_SUBPROCESS_LEVEL_EVENT_ID = "_00D797D8-20D0-4CF0-A6C0-B9FE3034FAE6";

    private static final String SLA_DUE_DATE = "12/25/1983";

    private static final int AMOUNT_OF_NODES_IN_DIAGRAM = 31;

    @Test
    @Override
    public void testUnmarshallTopLevelEventFilledProperties() throws Exception {
        final String EVENT_NAME = "Boundary error01 ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION = "error01 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String EVENT_REF = "error01";
        final String EVENT_DATA_OUTPUT = "||error01:String||[dout]error01->processGlobalVar";

        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_CATCHING_INTERMEDIATE_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateErrorEventCatching filledTopEvent = getCatchingIntermediateNodeById(diagram,
                                                                                        FILLED_TOP_LEVEL_EVENT_ID,
                                                                                        HAS_NO_INCOME_EDGE,
                                                                                        HAS_NO_OUTGOING_EDGE);
        assertGeneralSet(filledTopEvent.getGeneral(), EVENT_NAME, EVENT_DOCUMENTATION);
        assertErrorEventExecutionSet(filledTopEvent.getExecutionSet(), EVENT_REF, CANCELLING, SLA_DUE_DATE);
        assertDataIOSet(filledTopEvent.getDataIOSet(), EVENT_DATA_OUTPUT);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEmptyEventProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_CATCHING_INTERMEDIATE_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateErrorEventCatching emptyTopEvent = getCatchingIntermediateNodeById(diagram,
                                                                                       EMPTY_TOP_LEVEL_EVENT_ID,
                                                                                       HAS_NO_INCOME_EDGE,
                                                                                       HAS_NO_OUTGOING_EDGE);
        assertGeneralSet(emptyTopEvent.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertErrorEventExecutionSet(emptyTopEvent.getExecutionSet(), EMPTY_VALUE, CANCELLING, EMPTY_VALUE);
        assertDataIOSet(emptyTopEvent.getDataIOSet(), EMPTY_VALUE);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventFilledProperties() throws Exception {
        final String EVENT_NAME = "Boundary error03 ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION = "error03 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String EVENT_REF = "error03";
        final String EVENT_DATA_OUTPUT = "||error03:String||[dout]error03->processGlobalVar";

        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_CATCHING_INTERMEDIATE_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateErrorEventCatching filledSubprocessEvent = getCatchingIntermediateNodeById(diagram,
                                                                                               FILLED_SUBPROCESS_LEVEL_EVENT_ID,
                                                                                               HAS_NO_INCOME_EDGE,
                                                                                               HAS_NO_OUTGOING_EDGE);
        assertGeneralSet(filledSubprocessEvent.getGeneral(), EVENT_NAME, EVENT_DOCUMENTATION);
        assertErrorEventExecutionSet(filledSubprocessEvent.getExecutionSet(), EVENT_REF, CANCELLING, SLA_DUE_DATE);
        assertDataIOSet(filledSubprocessEvent.getDataIOSet(), EVENT_DATA_OUTPUT);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventEmptyProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_CATCHING_INTERMEDIATE_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateErrorEventCatching emptySubprocessEvent = getCatchingIntermediateNodeById(diagram,
                                                                                              EMPTY_SUBPROCESS_LEVEL_EVENT_ID,
                                                                                              HAS_NO_INCOME_EDGE,
                                                                                              HAS_NO_OUTGOING_EDGE);
        assertGeneralSet(emptySubprocessEvent.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertErrorEventExecutionSet(emptySubprocessEvent.getExecutionSet(), EMPTY_VALUE, CANCELLING, EMPTY_VALUE);
        assertDataIOSet(emptySubprocessEvent.getDataIOSet(), EMPTY_VALUE);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEventWithEdgesFilledProperties() throws Exception {
        final String EVENT_NAME = "Boundary error02 ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION = "error02 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String EVENT_REF = "error02";
        final String EVENT_DATA_OUTPUT = "||error02:String||[dout]error02->processGlobalVar";

        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_CATCHING_INTERMEDIATE_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateErrorEventCatching filledSubprocessEvent = getCatchingIntermediateNodeById(diagram,
                                                                                               FILLED_WITH_OUTGOING_EDGE_TOP_LEVEL_EVENT_ID,
                                                                                               HAS_NO_INCOME_EDGE,
                                                                                               HAS_OUTGOING_EDGE);
        assertGeneralSet(filledSubprocessEvent.getGeneral(), EVENT_NAME, EVENT_DOCUMENTATION);
        assertErrorEventExecutionSet(filledSubprocessEvent.getExecutionSet(), EVENT_REF, CANCELLING, SLA_DUE_DATE);
        assertDataIOSet(filledSubprocessEvent.getDataIOSet(), EVENT_DATA_OUTPUT);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEventWithEdgesEmptyProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_CATCHING_INTERMEDIATE_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateErrorEventCatching emptyEvent = getCatchingIntermediateNodeById(diagram,
                                                                                    EMPTY_WITH_OUTGOING_EDGE_TOP_LEVEL_EVENT_ID,
                                                                                    HAS_NO_INCOME_EDGE,
                                                                                    HAS_OUTGOING_EDGE);
        assertGeneralSet(emptyEvent.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertErrorEventExecutionSet(emptyEvent.getExecutionSet(), EMPTY_VALUE, CANCELLING, EMPTY_VALUE);
        assertDataIOSet(emptyEvent.getDataIOSet(), EMPTY_VALUE);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventWithEdgesEmptyProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_CATCHING_INTERMEDIATE_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateErrorEventCatching emptySubprocessEvent = getCatchingIntermediateNodeById(diagram,
                                                                                              EMPTY_WITH_OUTGOING_EDGE_SUBPROCESS_LEVEL_EVENT_ID,
                                                                                              HAS_NO_INCOME_EDGE,
                                                                                              HAS_OUTGOING_EDGE);
        assertGeneralSet(emptySubprocessEvent.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertErrorEventExecutionSet(emptySubprocessEvent.getExecutionSet(), EMPTY_VALUE, CANCELLING, EMPTY_VALUE);
        assertDataIOSet(emptySubprocessEvent.getDataIOSet(), EMPTY_VALUE);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventWithEdgesFilledProperties() throws Exception {
        final String EVENT_NAME = "Boundary error04 ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION = "error04 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String EVENT_REF = "error04";
        final String EVENT_DATA_OUTPUT = "||error04:String||[dout]error04->processGlobalVar";

        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_CATCHING_INTERMEDIATE_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateErrorEventCatching filledSubprocessEvent = getCatchingIntermediateNodeById(diagram,
                                                                                               FILLED_WITH_OUTGOING_EDGE_SUBPROCESS_LEVEL_EVENT_ID,
                                                                                               HAS_NO_INCOME_EDGE,
                                                                                               HAS_OUTGOING_EDGE);
        assertGeneralSet(filledSubprocessEvent.getGeneral(), EVENT_NAME, EVENT_DOCUMENTATION);
        assertErrorEventExecutionSet(filledSubprocessEvent.getExecutionSet(), EVENT_REF, CANCELLING, SLA_DUE_DATE);
        assertDataIOSet(filledSubprocessEvent.getDataIOSet(), EVENT_DATA_OUTPUT);
    }

    @Override
    String getBpmnCatchingIntermediateEventFilePath() {
        return BPMN_CATCHING_INTERMEDIATE_EVENT_FILE_PATH;
    }

    @Override
    Class<IntermediateErrorEventCatching> getCatchingIntermediateEventType() {
        return IntermediateErrorEventCatching.class;
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

    private void assertErrorEventExecutionSet(CancellingErrorEventExecutionSet executionSet, String eventName, boolean isCancelling, String slaDueDate) {
        assertNotNull(executionSet);
        assertNotNull(executionSet.getErrorRef());

        assertEventCancelActivity(executionSet, isCancelling);
        assertTimerEventSlaDueDate(executionSet, slaDueDate);

        assertEquals(eventName, executionSet.getErrorRef().getValue());

    }
}
