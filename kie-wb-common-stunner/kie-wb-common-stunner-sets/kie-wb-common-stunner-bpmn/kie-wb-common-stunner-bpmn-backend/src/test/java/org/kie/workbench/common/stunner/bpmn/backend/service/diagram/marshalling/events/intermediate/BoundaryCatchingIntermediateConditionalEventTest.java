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
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateConditionalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.conditional.CancellingConditionalEventExecutionSet;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;

import static org.assertj.core.api.Assertions.assertThat;

public class BoundaryCatchingIntermediateConditionalEventTest extends BoundaryCatchingIntermediateEventTest<IntermediateConditionalEvent> {

    private static final String BPMN_CATCHING_INTERMEDIATE_EVENT_FILE_PATH = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/boundaryConditionalEvents.bpmn";

    private static final String EMPTY_TOP_LEVEL_EVENT_ID = "_8284FFB2-83A5-4AED-923C-31B5D3BB5E40";
    private static final String FILLED_TOP_LEVEL_EVENT_ID = "_467655EB-2470-463C-B957-944F4592856D";
    private static final String EMPTY_SUBPROCESS_LEVEL_EVENT_ID = "_7BEE831A-6679-4BF8-B420-EAC9A904828A";
    private static final String FILLED_SUBPROCESS_LEVEL_EVENT_ID = "_3FCB8D12-4889-4A60-9125-1CB8685381C4";

    private static final String EMPTY_WITH_EDGES_SUBPROCESS_LEVEL_EVENT_ID = "_8F3A4A49-486A-414B-B3C0-2AB6A70653F3";
    private static final String FILLED_WITH_EDGES_SUBPROCESS_LEVEL_EVENT_ID = "_877C1825-122D-408B-A8F2-00B1AD798544";
    private static final String EMPTY_WITH_EDGES_TOP_LEVEL_EVENT_ID = "_F114AEE8-DA28-4A26-8B77-1E69AA39BB63";
    private static final String FILLED_WITH_EDGES_TOP_LEVEL_EVENT_ID = "_A1BB3E61-B2BD-4EA2-AF80-73EAC4DF5F3E";

    private static final String SLA_DUE_DATE = "12/25/1983";

    private static final int AMOUNT_OF_NODES_IN_DIAGRAM = 35;

    private static final String CONDITION_EXPRESSION_SCRIPT_DEFAULT_VALUE = null;
    private static final String CONDITION_EXPRESSION_LANGUAGE = "drools";
    private static final String CONDITION_ERPRESSION_TYPE = "stunner.bpmn.ScriptType";

    public BoundaryCatchingIntermediateConditionalEventTest() throws Exception {
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEventFilledProperties() throws Exception {
        final String EVENT_NAME = "Boundary01 ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION = "Boundary 01 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String CONDITION_EXPRESSION_SCRIPT = "com.myspace.testproject.Person(name == \"John\")";

        Diagram<Graph, Metadata> diagram = getDiagram();
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateConditionalEvent filledTopEvent = getCatchingIntermediateNodeById(diagram,
                                                                                      FILLED_TOP_LEVEL_EVENT_ID,
                                                                                      HAS_NO_INCOME_EDGE,
                                                                                      ZERO_OUTGOING_EDGES);
        assertGeneralSet(filledTopEvent.getGeneral(), EVENT_NAME, EVENT_DOCUMENTATION);
        assertConditionalEventExecutionSet(filledTopEvent.getExecutionSet(),
                                           CONDITION_EXPRESSION_SCRIPT,
                                           CONDITION_EXPRESSION_LANGUAGE,
                                           CONDITION_ERPRESSION_TYPE,
                                           CANCELLING,
                                           SLA_DUE_DATE);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEmptyEventProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = getDiagram();
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateConditionalEvent emptyTopEvent = getCatchingIntermediateNodeById(diagram,
                                                                                     EMPTY_TOP_LEVEL_EVENT_ID,
                                                                                     HAS_NO_INCOME_EDGE,
                                                                                     ZERO_OUTGOING_EDGES);
        assertGeneralSet(emptyTopEvent.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertConditionalEventExecutionSet(emptyTopEvent.getExecutionSet(),
                                           CONDITION_EXPRESSION_SCRIPT_DEFAULT_VALUE,
                                           CONDITION_EXPRESSION_LANGUAGE,
                                           CONDITION_ERPRESSION_TYPE,
                                           NON_CANCELLING,
                                           EMPTY_VALUE);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventFilledProperties() throws Exception {
        final String EVENT_NAME = "Boundary03 ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION = "Boundary 03 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String CONDITION_EXPRESSION_SCRIPT = "com.myspace.testproject.Person(name == \"John\")";

        Diagram<Graph, Metadata> diagram = getDiagram();
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateConditionalEvent filledSubprocessEvent = getCatchingIntermediateNodeById(diagram,
                                                                                             FILLED_SUBPROCESS_LEVEL_EVENT_ID,
                                                                                             HAS_NO_INCOME_EDGE,
                                                                                             ZERO_OUTGOING_EDGES);
        assertGeneralSet(filledSubprocessEvent.getGeneral(), EVENT_NAME, EVENT_DOCUMENTATION);
        assertConditionalEventExecutionSet(filledSubprocessEvent.getExecutionSet(),
                                           CONDITION_EXPRESSION_SCRIPT,
                                           CONDITION_EXPRESSION_LANGUAGE,
                                           CONDITION_ERPRESSION_TYPE,
                                           CANCELLING,
                                           SLA_DUE_DATE);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventEmptyProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = getDiagram();
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateConditionalEvent emptySubprocessEvent = getCatchingIntermediateNodeById(diagram,
                                                                                            EMPTY_SUBPROCESS_LEVEL_EVENT_ID,
                                                                                            HAS_NO_INCOME_EDGE,
                                                                                            ZERO_OUTGOING_EDGES);
        assertGeneralSet(emptySubprocessEvent.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertConditionalEventExecutionSet(emptySubprocessEvent.getExecutionSet(),
                                           CONDITION_EXPRESSION_SCRIPT_DEFAULT_VALUE,
                                           CONDITION_EXPRESSION_LANGUAGE,
                                           CONDITION_ERPRESSION_TYPE,
                                           NON_CANCELLING,
                                           EMPTY_VALUE);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEventWithEdgesFilledProperties() {
        final String EVENT_NAME = "Boundary02 ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION = "Boundary 02 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String CONDITION_EXPRESSION_SCRIPT = "com.myspace.testproject.Person(name == \"John\")";

        Diagram<Graph, Metadata> diagram = getDiagram();
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateConditionalEvent filledSubprocessEvent = getCatchingIntermediateNodeById(diagram,
                                                                                             FILLED_WITH_EDGES_TOP_LEVEL_EVENT_ID,
                                                                                             HAS_NO_INCOME_EDGE,
                                                                                             TWO_OUTGOING_EDGES);
        assertGeneralSet(filledSubprocessEvent.getGeneral(), EVENT_NAME, EVENT_DOCUMENTATION);
        assertConditionalEventExecutionSet(filledSubprocessEvent.getExecutionSet(),
                                           CONDITION_EXPRESSION_SCRIPT,
                                           CONDITION_EXPRESSION_LANGUAGE,
                                           CONDITION_ERPRESSION_TYPE,
                                           CANCELLING,
                                           SLA_DUE_DATE);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEventWithEdgesEmptyProperties() {
        Diagram<Graph, Metadata> diagram = getDiagram();
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateConditionalEvent emptyEvent = getCatchingIntermediateNodeById(diagram,
                                                                                  EMPTY_WITH_EDGES_TOP_LEVEL_EVENT_ID,
                                                                                  HAS_NO_INCOME_EDGE,
                                                                                  TWO_OUTGOING_EDGES);
        assertGeneralSet(emptyEvent.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertConditionalEventExecutionSet(emptyEvent.getExecutionSet(),
                                           CONDITION_EXPRESSION_SCRIPT_DEFAULT_VALUE,
                                           CONDITION_EXPRESSION_LANGUAGE,
                                           CONDITION_ERPRESSION_TYPE,
                                           NON_CANCELLING,
                                           EMPTY_VALUE);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventWithEdgesEmptyProperties() {
        Diagram<Graph, Metadata> diagram = getDiagram();
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateConditionalEvent emptySubprocessEvent = getCatchingIntermediateNodeById(diagram,
                                                                                            EMPTY_WITH_EDGES_SUBPROCESS_LEVEL_EVENT_ID,
                                                                                            HAS_NO_INCOME_EDGE,
                                                                                            TWO_OUTGOING_EDGES);
        assertGeneralSet(emptySubprocessEvent.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertConditionalEventExecutionSet(emptySubprocessEvent.getExecutionSet(),
                                           CONDITION_EXPRESSION_SCRIPT_DEFAULT_VALUE,
                                           CONDITION_EXPRESSION_LANGUAGE,
                                           CONDITION_ERPRESSION_TYPE,
                                           NON_CANCELLING,
                                           EMPTY_VALUE);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventWithEdgesFilledProperties() {
        final String EVENT_NAME = "Boundary04 ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION = "Boundary 04 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String CONDITION_EXPRESSION_SCRIPT = "com.myspace.testproject.Person(name == \"John\")";

        Diagram<Graph, Metadata> diagram = getDiagram();
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateConditionalEvent filledSubprocessEvent = getCatchingIntermediateNodeById(diagram,
                                                                                             FILLED_WITH_EDGES_SUBPROCESS_LEVEL_EVENT_ID,
                                                                                             HAS_NO_INCOME_EDGE,
                                                                                             TWO_OUTGOING_EDGES);
        assertGeneralSet(filledSubprocessEvent.getGeneral(), EVENT_NAME, EVENT_DOCUMENTATION);
        assertConditionalEventExecutionSet(filledSubprocessEvent.getExecutionSet(),
                                           CONDITION_EXPRESSION_SCRIPT,
                                           CONDITION_EXPRESSION_LANGUAGE,
                                           CONDITION_ERPRESSION_TYPE,
                                           CANCELLING,
                                           SLA_DUE_DATE);
    }

    @Override
    String getBpmnCatchingIntermediateEventFilePath() {
        return BPMN_CATCHING_INTERMEDIATE_EVENT_FILE_PATH;
    }

    @Override
    Class<IntermediateConditionalEvent> getCatchingIntermediateEventType() {
        return IntermediateConditionalEvent.class;
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

    private void assertConditionalEventExecutionSet(CancellingConditionalEventExecutionSet executionSet,
                                                    String conditionExpressionScript,
                                                    String conditionExpressionLanguage,
                                                    String conditionExpressionType,
                                                    boolean isCancelling,
                                                    String slaDueDate) {
        assertThat(executionSet).isNotNull();
        assertThat(executionSet.getConditionExpression()).isNotNull();
        assertThat(executionSet.getConditionExpression().getValue()).isNotNull();
        assertThat(executionSet.getConditionExpression().getType()).isNotNull();

        assertThat(executionSet.getConditionExpression().getValue().getLanguage()).isEqualTo(conditionExpressionLanguage);
        assertThat(executionSet.getConditionExpression().getValue().getScript()).isEqualTo(conditionExpressionScript);
        assertThat(executionSet.getConditionExpression().getType().getName()).isEqualTo(conditionExpressionType);

        assertEventCancelActivity(executionSet, isCancelling);
        assertEventSlaDueDate(executionSet, slaDueDate);
    }
}
