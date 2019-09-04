/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateEscalationEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.escalation.EscalationEventExecutionSet;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;

import static org.assertj.core.api.Assertions.assertThat;

public class ThrowingIntermediateEscalationEventTest extends ThrowingIntermediateEventTest<IntermediateEscalationEventThrowing> {

    private static final String BPMN_THROWING_INTERMEDIATE_EVENT_FILE_PATH = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/throwingIntermediateEscalationEvents.bpmn";

    private static final String EMPTY_TOP_LEVEL_EVENT_ID = "_3DD234F9-171A-4B8D-AA38-853A4B99CBC5";
    private static final String FILLED_TOP_LEVEL_EVENT_ID = "_068F0418-AB4E-4DD0-814C-AD8E001BFC50";
    private static final String EMPTY_SUBPROCESS_LEVEL_EVENT_ID = "_210EE227-2031-4750-B323-A73ADE4E2FDE";
    private static final String FILLED_SUBPROCESS_LEVEL_EVENT_ID = "_6FECB1BF-BF5A-4E04-9D0D-30AA643DCBF5";

    private static final String EMPTY_WITH_EDGES_TOP_LEVEL_EVENT_ID = "_DB9AB0EA-CA45-4224-AB4C-F0EB59397848";
    private static final String FILLED_WITH_EDGES_TOP_LEVEL_EVENT_ID = "_0019B4E6-5B69-4123-BB42-9D5F39E0E04D";
    private static final String EMPTY_WITH_EDGES_SUBPROCESS_LEVEL_EVENT_ID = "_BF3082B5-F7BF-4042-AD99-6FCB53586AA5";
    private static final String FILLED_WITH_EDGES_SUBPROCESS_LEVEL_EVENT_ID = "_CBE4DC6E-49DD-4B50-A970-352C5D412BBD";

    private static final int AMOUNT_OF_NODES_IN_DIAGRAM = 23;

    public ThrowingIntermediateEscalationEventTest() throws Exception {
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEventFilledProperties() throws Exception {
        final String EVENT_NAME = "Escalation event01 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION = "Escalation event01 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String EVENT_REF = "escalation01";
        final String EVENT_DATA_INPUT = "input:String||||[din]processGlobalVar->input";

        Diagram<Graph, Metadata> diagram = getDiagram();
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateEscalationEventThrowing filledTopEvent = getThrowingIntermediateNodeById(diagram,
                                                                                             FILLED_TOP_LEVEL_EVENT_ID,
                                                                                             HAS_NO_INCOME_EDGE, ZERO_OUTGOING_EDGES);
        assertGeneralSet(filledTopEvent.getGeneral(), EVENT_NAME, EVENT_DOCUMENTATION);
        assertEscalationEventExecutionSet(filledTopEvent.getExecutionSet(), EVENT_REF);
        assertDataIOSet(filledTopEvent.getDataIOSet(), EVENT_DATA_INPUT);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEmptyEventProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = getDiagram();
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateEscalationEventThrowing emptyTopEvent = getThrowingIntermediateNodeById(diagram,
                                                                                            EMPTY_TOP_LEVEL_EVENT_ID,
                                                                                            HAS_NO_INCOME_EDGE, ZERO_OUTGOING_EDGES);
        assertGeneralSet(emptyTopEvent.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertEscalationEventExecutionSet(emptyTopEvent.getExecutionSet(), EMPTY_VALUE);
        assertDataIOSet(emptyTopEvent.getDataIOSet(), EMPTY_VALUE);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventFilledProperties() throws Exception {
        final String EVENT_NAME = "Escalation event03 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION = "Escalation event03 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String EVENT_REF = "escalation03";
        final String EVENT_DATA_INPUT = "input:String||||[din]processGlobalVar->input";

        Diagram<Graph, Metadata> diagram = getDiagram();
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateEscalationEventThrowing filledSubprocessEvent = getThrowingIntermediateNodeById(diagram,
                                                                                                    FILLED_SUBPROCESS_LEVEL_EVENT_ID,
                                                                                                    HAS_NO_INCOME_EDGE, ZERO_OUTGOING_EDGES);
        assertGeneralSet(filledSubprocessEvent.getGeneral(), EVENT_NAME, EVENT_DOCUMENTATION);
        assertEscalationEventExecutionSet(filledSubprocessEvent.getExecutionSet(), EVENT_REF);
        assertDataIOSet(filledSubprocessEvent.getDataIOSet(), EVENT_DATA_INPUT);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventEmptyProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = getDiagram();
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateEscalationEventThrowing emptySubprocessEvent = getThrowingIntermediateNodeById(diagram,
                                                                                                   EMPTY_SUBPROCESS_LEVEL_EVENT_ID,
                                                                                                   HAS_NO_INCOME_EDGE, ZERO_OUTGOING_EDGES);
        assertGeneralSet(emptySubprocessEvent.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertEscalationEventExecutionSet(emptySubprocessEvent.getExecutionSet(), EMPTY_VALUE);
        assertDataIOSet(emptySubprocessEvent.getDataIOSet(), EMPTY_VALUE);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEventWithEdgesFilledProperties() {
        final String EVENT_NAME = "Escalation event02 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION = "Escalation event02 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String EVENT_REF = "escalation02";
        final String EVENT_DATA_INPUT = "input:String||||[din]processGlobalVar->input";

        Diagram<Graph, Metadata> diagram = getDiagram();
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateEscalationEventThrowing filledSubprocessEvent = getThrowingIntermediateNodeById(diagram,
                                                                                                    FILLED_WITH_EDGES_TOP_LEVEL_EVENT_ID,
                                                                                                    HAS_INCOME_EDGE, TWO_OUTGOING_EDGES);
        assertGeneralSet(filledSubprocessEvent.getGeneral(), EVENT_NAME, EVENT_DOCUMENTATION);
        assertEscalationEventExecutionSet(filledSubprocessEvent.getExecutionSet(), EVENT_REF);
        assertDataIOSet(filledSubprocessEvent.getDataIOSet(), EVENT_DATA_INPUT);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEventWithEdgesEmptyProperties() {
        Diagram<Graph, Metadata> diagram = getDiagram();
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateEscalationEventThrowing emptyEvent = getThrowingIntermediateNodeById(diagram,
                                                                                         EMPTY_WITH_EDGES_TOP_LEVEL_EVENT_ID,
                                                                                         HAS_INCOME_EDGE, TWO_OUTGOING_EDGES);
        assertGeneralSet(emptyEvent.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertEscalationEventExecutionSet(emptyEvent.getExecutionSet(), EMPTY_VALUE);
        assertDataIOSet(emptyEvent.getDataIOSet(), EMPTY_VALUE);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventWithEdgesEmptyProperties() {
        Diagram<Graph, Metadata> diagram = getDiagram();
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateEscalationEventThrowing emptySubprocessEvent = getThrowingIntermediateNodeById(diagram,
                                                                                                   EMPTY_WITH_EDGES_SUBPROCESS_LEVEL_EVENT_ID,
                                                                                                   HAS_INCOME_EDGE, TWO_OUTGOING_EDGES);
        assertGeneralSet(emptySubprocessEvent.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertEscalationEventExecutionSet(emptySubprocessEvent.getExecutionSet(), EMPTY_VALUE);
        assertDataIOSet(emptySubprocessEvent.getDataIOSet(), EMPTY_VALUE);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventWithEdgesFilledProperties() {
        final String EVENT_NAME = "Escalation event04 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION = "Escalation event04 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String EVENT_REF = "escalation04";
        final String EVENT_DATA_INPUT = "input:String||||[din]processGlobalVar->input";

        Diagram<Graph, Metadata> diagram = getDiagram();
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateEscalationEventThrowing filledSubprocessEvent = getThrowingIntermediateNodeById(diagram,
                                                                                                    FILLED_WITH_EDGES_SUBPROCESS_LEVEL_EVENT_ID,
                                                                                                    HAS_INCOME_EDGE, TWO_OUTGOING_EDGES);
        assertGeneralSet(filledSubprocessEvent.getGeneral(), EVENT_NAME, EVENT_DOCUMENTATION);
        assertEscalationEventExecutionSet(filledSubprocessEvent.getExecutionSet(), EVENT_REF);
        assertDataIOSet(filledSubprocessEvent.getDataIOSet(), EVENT_DATA_INPUT);
    }

    @Override
    String getBpmnThrowingIntermediateEventFilePath() {
        return BPMN_THROWING_INTERMEDIATE_EVENT_FILE_PATH;
    }

    @Override
    Class<IntermediateEscalationEventThrowing> getThrowingIntermediateEventType() {
        return IntermediateEscalationEventThrowing.class;
    }

    @Override
    String[] getFilledTopLevelEventIds() {
        return new String[]{FILLED_TOP_LEVEL_EVENT_ID};
    }

    @Override
    String getEmptyTopLevelEventId() {
        return EMPTY_TOP_LEVEL_EVENT_ID;
    }

    @Override
    String[] getFilledSubprocessLevelEventIds() {
        return new String[]{FILLED_SUBPROCESS_LEVEL_EVENT_ID};
    }

    @Override
    String getEmptySubprocessLevelEventId() {
        return EMPTY_SUBPROCESS_LEVEL_EVENT_ID;
    }

    @Override
    String[] getFilledTopLevelEventWithEdgesIds() {
        return new String[]{FILLED_WITH_EDGES_TOP_LEVEL_EVENT_ID};
    }

    @Override
    String getEmptyTopLevelEventWithEdgesId() {
        return EMPTY_WITH_EDGES_TOP_LEVEL_EVENT_ID;
    }

    @Override
    String[] getFilledSubprocessLevelEventWithEdgesIds() {
        return new String[]{FILLED_WITH_EDGES_SUBPROCESS_LEVEL_EVENT_ID};
    }

    @Override
    String getEmptySubprocessLevelEventWithEdgesId() {
        return EMPTY_WITH_EDGES_SUBPROCESS_LEVEL_EVENT_ID;
    }

    private void assertEscalationEventExecutionSet(EscalationEventExecutionSet executionSet, String eventReference) {
        assertThat(executionSet).isNotNull();
        assertThat(executionSet.getEscalationRef()).isNotNull();
        assertThat(executionSet.getEscalationRef().getValue()).isEqualTo(eventReference);
    }
}
