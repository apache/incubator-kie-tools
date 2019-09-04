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
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateCompensationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.BaseCancellingEventExecutionSet;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;

import static org.assertj.core.api.Assertions.assertThat;

public class BoundaryCatchingIntermediateCompensationEventTest extends BoundaryCatchingIntermediateEventTest<IntermediateCompensationEvent> {

    private static final String BPMN_CATCHING_INTERMEDIATE_EVENT_FILE_PATH = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/boundaryCompensationEvents.bpmn";

    private static final String EMPTY_TOP_LEVEL_EVENT_ID = "_4AAF367B-3F0C-41B9-A57C-3979D614511A";
    private static final String FILLED_TOP_LEVEL_EVENT_ID = "_5C70D78D-2204-4E90-8F06-7E03C45F843D";
    private static final String EMPTY_SUBPROCESS_LEVEL_EVENT_ID = "_47B678A0-FF16-4370-BA09-C0C9CE562D36";
    private static final String FILLED_SUBPROCESS_LEVEL_EVENT_ID = "_ECDEBCD9-25E0-40C1-B970-047D3011B4D9";

    private static final String EMPTY_WITH_EDGES_SUBPROCESS_LEVEL_EVENT_ID = "_C04BCC72-55B8-4DB4-81E6-01971E346594";
    private static final String FILLED_WITH_EDGES_SUBPROCESS_LEVEL_EVENT_ID = "_597D1EF7-0754-4843-A22E-6941138D852A";
    private static final String EMPTY_WITH_EDGES_TOP_LEVEL_EVENT_ID = "_A6663578-4B8E-4EB7-AA72-936EBA2A6713";
    private static final String FILLED_WITH_EDGES_TOP_LEVEL_EVENT_ID = "_920B9AF9-8959-4AEB-97FD-5B0C8EFF1F8B";

    private static final String SLA_DUE_DATE = "P1y8m17dT23h19m38s";

    private static final int AMOUNT_OF_NODES_IN_DIAGRAM = 27;

    public BoundaryCatchingIntermediateCompensationEventTest() throws Exception {
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEventFilledProperties() throws Exception {
        final String EVENT_NAME = "Compensation01\n ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION = "Compensation01 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";

        Diagram<Graph, Metadata> diagram = getDiagram();
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateCompensationEvent filledTopEvent = getCatchingIntermediateNodeById(diagram,
                                                                                       FILLED_TOP_LEVEL_EVENT_ID,
                                                                                       HAS_NO_INCOME_EDGE,
                                                                                       ZERO_OUTGOING_EDGES);
        assertGeneralSet(filledTopEvent.getGeneral(), EVENT_NAME, EVENT_DOCUMENTATION);
        assertCompensationEventExecutionSet(filledTopEvent.getExecutionSet(),
                                            NON_CANCELLING,
                                            SLA_DUE_DATE);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEmptyEventProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = getDiagram();
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateCompensationEvent emptyTopEvent = getCatchingIntermediateNodeById(diagram,
                                                                                      EMPTY_TOP_LEVEL_EVENT_ID,
                                                                                      HAS_NO_INCOME_EDGE,
                                                                                      ZERO_OUTGOING_EDGES);
        assertGeneralSet(emptyTopEvent.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertCompensationEventExecutionSet(emptyTopEvent.getExecutionSet(),
                                            NON_CANCELLING,
                                            EMPTY_VALUE);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventFilledProperties() throws Exception {
        final String EVENT_NAME = "Compensation03\n ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION = "Compensation03 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";

        Diagram<Graph, Metadata> diagram = getDiagram();
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateCompensationEvent filledSubprocessEvent = getCatchingIntermediateNodeById(diagram,
                                                                                              FILLED_SUBPROCESS_LEVEL_EVENT_ID,
                                                                                              HAS_NO_INCOME_EDGE,
                                                                                              ZERO_OUTGOING_EDGES);
        assertGeneralSet(filledSubprocessEvent.getGeneral(), EVENT_NAME, EVENT_DOCUMENTATION);
        assertCompensationEventExecutionSet(filledSubprocessEvent.getExecutionSet(),
                                            NON_CANCELLING,
                                            SLA_DUE_DATE);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventEmptyProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = getDiagram();
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateCompensationEvent emptySubprocessEvent = getCatchingIntermediateNodeById(diagram,
                                                                                             EMPTY_SUBPROCESS_LEVEL_EVENT_ID,
                                                                                             HAS_NO_INCOME_EDGE,
                                                                                             ZERO_OUTGOING_EDGES);
        assertGeneralSet(emptySubprocessEvent.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertCompensationEventExecutionSet(emptySubprocessEvent.getExecutionSet(),
                                            NON_CANCELLING,
                                            EMPTY_VALUE);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEventWithEdgesFilledProperties() {
        final String EVENT_NAME = "Compensation02\n ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION = "Compensation02 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";

        Diagram<Graph, Metadata> diagram = getDiagram();
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateCompensationEvent filledSubprocessEvent = getCatchingIntermediateNodeById(diagram,
                                                                                              FILLED_WITH_EDGES_TOP_LEVEL_EVENT_ID,
                                                                                              HAS_NO_INCOME_EDGE,
                                                                                              TWO_OUTGOING_EDGES);
        assertGeneralSet(filledSubprocessEvent.getGeneral(), EVENT_NAME, EVENT_DOCUMENTATION);
        assertCompensationEventExecutionSet(filledSubprocessEvent.getExecutionSet(),
                                            NON_CANCELLING,
                                            SLA_DUE_DATE);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEventWithEdgesEmptyProperties() {
        Diagram<Graph, Metadata> diagram = getDiagram();
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateCompensationEvent emptyEvent = getCatchingIntermediateNodeById(diagram,
                                                                                   EMPTY_WITH_EDGES_TOP_LEVEL_EVENT_ID,
                                                                                   HAS_NO_INCOME_EDGE,
                                                                                   TWO_OUTGOING_EDGES);
        assertGeneralSet(emptyEvent.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertCompensationEventExecutionSet(emptyEvent.getExecutionSet(),
                                            NON_CANCELLING,
                                            EMPTY_VALUE);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventWithEdgesEmptyProperties() {
        Diagram<Graph, Metadata> diagram = getDiagram();
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateCompensationEvent emptySubprocessEvent = getCatchingIntermediateNodeById(diagram,
                                                                                             EMPTY_WITH_EDGES_SUBPROCESS_LEVEL_EVENT_ID,
                                                                                             HAS_NO_INCOME_EDGE,
                                                                                             TWO_OUTGOING_EDGES);
        assertGeneralSet(emptySubprocessEvent.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertCompensationEventExecutionSet(emptySubprocessEvent.getExecutionSet(),
                                            NON_CANCELLING,
                                            EMPTY_VALUE);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventWithEdgesFilledProperties() {
        final String EVENT_NAME = "Compensation04\n ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION = "Compensation04 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";

        Diagram<Graph, Metadata> diagram = getDiagram();
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateCompensationEvent filledSubprocessEvent = getCatchingIntermediateNodeById(diagram,
                                                                                              FILLED_WITH_EDGES_SUBPROCESS_LEVEL_EVENT_ID,
                                                                                              HAS_NO_INCOME_EDGE,
                                                                                              TWO_OUTGOING_EDGES);
        assertGeneralSet(filledSubprocessEvent.getGeneral(), EVENT_NAME, EVENT_DOCUMENTATION);
        assertCompensationEventExecutionSet(filledSubprocessEvent.getExecutionSet(),
                                            NON_CANCELLING,
                                            SLA_DUE_DATE);
    }

    @Override
    String getBpmnCatchingIntermediateEventFilePath() {
        return BPMN_CATCHING_INTERMEDIATE_EVENT_FILE_PATH;
    }

    @Override
    Class<IntermediateCompensationEvent> getCatchingIntermediateEventType() {
        return IntermediateCompensationEvent.class;
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

    private void assertCompensationEventExecutionSet(BaseCancellingEventExecutionSet executionSet,
                                                     boolean isCancelling,
                                                     String slaDueDate) {
        assertThat(executionSet).isNotNull();
        assertEventCancelActivity(executionSet, isCancelling);
        assertEventSlaDueDate(executionSet, slaDueDate);
    }
}
