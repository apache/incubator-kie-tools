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
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateCompensationEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.compensation.CompensationEventExecutionSet;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;

import static org.assertj.core.api.Assertions.assertThat;

public class ThrowingIntermediateCompensationEventTest extends ThrowingIntermediateEventTest<IntermediateCompensationEventThrowing> {

    private static final String BPMN_THROWING_INTERMEDIATE_EVENT_FILE_PATH = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/throwingIntermediateCompensationEvents.bpmn";

    private static final String EMPTY_TOP_LEVEL_EVENT_ID = "_1FA90A0E-DAB4-4667-A136-DA580CBFF039";
    private static final String FILLED_TOP_LEVEL_EVENT_ID = "_290C92D3-F521-471A-8EFE-BC4AD550AFD4";
    private static final String EMPTY_SUBPROCESS_LEVEL_EVENT_ID = "_D912D44D-C8AD-4094-B598-595E9FB95CA9";
    private static final String FILLED_SUBPROCESS_LEVEL_EVENT_ID = "_15715D7B-BCBB-4F31-9498-B5DF039BB55D";

    private static final String EMPTY_WITH_EDGES_TOP_LEVEL_EVENT_ID = "_1FBE977B-0DA6-44AB-920D-3C730AE99B6B";
    private static final String FILLED_WITH_EDGES_TOP_LEVEL_EVENT_ID = "_0C20130D-E5B6-4AD4-8F71-C4C09E200C7D";
    private static final String EMPTY_WITH_EDGES_SUBPROCESS_LEVEL_EVENT_ID = "_2A9670B9-AD67-4674-96C7-332BF1DBE5AB";
    private static final String FILLED_WITH_EDGES_SUBPROCESS_LEVEL_EVENT_ID = "_F00653FB-26FF-40DA-87D8-1D85AB15D190";

    private static final int AMOUNT_OF_NODES_IN_DIAGRAM = 23;

    private static final String DEFAULT_REFERENCE_ACTIVITY = null;

    public ThrowingIntermediateCompensationEventTest() throws Exception {
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEventFilledProperties() throws Exception {
        final String EVENT_NAME = "Compensation event01 name\n ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION = "Compensation event01 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String ACTIVITY_REFERENCE = "_1FDF93CC-5677-48C2-9760-B7B98FA08DD6";

        Diagram<Graph, Metadata> diagram = getDiagram();
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateCompensationEventThrowing filledTopEvent = getThrowingIntermediateNodeById(diagram,
                                                                                               FILLED_TOP_LEVEL_EVENT_ID,
                                                                                               HAS_NO_INCOME_EDGE, ZERO_OUTGOING_EDGES);
        assertGeneralSet(filledTopEvent.getGeneral(), EVENT_NAME, EVENT_DOCUMENTATION);
        assertCompensationEventExecutionSet(filledTopEvent.getExecutionSet(), ACTIVITY_REFERENCE);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEmptyEventProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = getDiagram();
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateCompensationEventThrowing emptyTopEvent = getThrowingIntermediateNodeById(diagram,
                                                                                              EMPTY_TOP_LEVEL_EVENT_ID,
                                                                                              HAS_NO_INCOME_EDGE, ZERO_OUTGOING_EDGES);
        assertGeneralSet(emptyTopEvent.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertCompensationEventExecutionSet(emptyTopEvent.getExecutionSet(), DEFAULT_REFERENCE_ACTIVITY);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventFilledProperties() throws Exception {
        final String EVENT_NAME = "Compensation event03 name\n ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION = "Compensation event03 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String ACTIVITY_REFERENCE = "_4305E23D-496B-42AA-AEE0-2840B4E75F15";

        Diagram<Graph, Metadata> diagram = getDiagram();
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateCompensationEventThrowing filledSubprocessEvent = getThrowingIntermediateNodeById(diagram,
                                                                                                      FILLED_SUBPROCESS_LEVEL_EVENT_ID,
                                                                                                      HAS_NO_INCOME_EDGE, ZERO_OUTGOING_EDGES);
        assertGeneralSet(filledSubprocessEvent.getGeneral(), EVENT_NAME, EVENT_DOCUMENTATION);
        assertCompensationEventExecutionSet(filledSubprocessEvent.getExecutionSet(), ACTIVITY_REFERENCE);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventEmptyProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = getDiagram();
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateCompensationEventThrowing emptySubprocessEvent = getThrowingIntermediateNodeById(diagram,
                                                                                                     EMPTY_SUBPROCESS_LEVEL_EVENT_ID,
                                                                                                     HAS_NO_INCOME_EDGE, ZERO_OUTGOING_EDGES);
        assertGeneralSet(emptySubprocessEvent.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertCompensationEventExecutionSet(emptySubprocessEvent.getExecutionSet(), DEFAULT_REFERENCE_ACTIVITY);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEventWithEdgesFilledProperties() {
        final String EVENT_NAME = "Compensation event02 name\n ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION = "Compensation event02 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String ACTIVITY_REFERENCE = "_1FDF93CC-5677-48C2-9760-B7B98FA08DD6";

        Diagram<Graph, Metadata> diagram = getDiagram();
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateCompensationEventThrowing filledSubprocessEvent = getThrowingIntermediateNodeById(diagram,
                                                                                                      FILLED_WITH_EDGES_TOP_LEVEL_EVENT_ID,
                                                                                                      HAS_INCOME_EDGE, TWO_OUTGOING_EDGES);
        assertGeneralSet(filledSubprocessEvent.getGeneral(), EVENT_NAME, EVENT_DOCUMENTATION);
        assertCompensationEventExecutionSet(filledSubprocessEvent.getExecutionSet(), ACTIVITY_REFERENCE);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEventWithEdgesEmptyProperties() {
        Diagram<Graph, Metadata> diagram = getDiagram();
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateCompensationEventThrowing emptyEvent = getThrowingIntermediateNodeById(diagram,
                                                                                           EMPTY_WITH_EDGES_TOP_LEVEL_EVENT_ID,
                                                                                           HAS_INCOME_EDGE, TWO_OUTGOING_EDGES);
        assertGeneralSet(emptyEvent.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertCompensationEventExecutionSet(emptyEvent.getExecutionSet(), DEFAULT_REFERENCE_ACTIVITY);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventWithEdgesEmptyProperties() {
        Diagram<Graph, Metadata> diagram = getDiagram();
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateCompensationEventThrowing emptySubprocessEvent = getThrowingIntermediateNodeById(diagram,
                                                                                                     EMPTY_WITH_EDGES_SUBPROCESS_LEVEL_EVENT_ID,
                                                                                                     HAS_INCOME_EDGE, TWO_OUTGOING_EDGES);
        assertGeneralSet(emptySubprocessEvent.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertCompensationEventExecutionSet(emptySubprocessEvent.getExecutionSet(), DEFAULT_REFERENCE_ACTIVITY);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventWithEdgesFilledProperties() {
        final String EVENT_NAME = "Compensation event04 name\n ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION = "Compensation event04 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String ACTIVITY_REFERENCE = "_4305E23D-496B-42AA-AEE0-2840B4E75F15";

        Diagram<Graph, Metadata> diagram = getDiagram();
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateCompensationEventThrowing filledSubprocessEvent = getThrowingIntermediateNodeById(diagram,
                                                                                                      FILLED_WITH_EDGES_SUBPROCESS_LEVEL_EVENT_ID,
                                                                                                      HAS_INCOME_EDGE, TWO_OUTGOING_EDGES);
        assertGeneralSet(filledSubprocessEvent.getGeneral(), EVENT_NAME, EVENT_DOCUMENTATION);
        assertCompensationEventExecutionSet(filledSubprocessEvent.getExecutionSet(), ACTIVITY_REFERENCE);
    }

    @Override
    String getBpmnThrowingIntermediateEventFilePath() {
        return BPMN_THROWING_INTERMEDIATE_EVENT_FILE_PATH;
    }

    @Override
    Class<IntermediateCompensationEventThrowing> getThrowingIntermediateEventType() {
        return IntermediateCompensationEventThrowing.class;
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

    private void assertCompensationEventExecutionSet(CompensationEventExecutionSet executionSet, String activityReference) {
        assertThat(executionSet).isNotNull();
        assertThat(executionSet.getActivityRef()).isNotNull();
        assertThat(executionSet.getActivityRef().getValue()).isEqualTo(activityReference);
    }
}


