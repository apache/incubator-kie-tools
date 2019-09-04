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
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateTimerEvent;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.timer.CancellingTimerEventExecutionSet;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;

import static org.assertj.core.api.Assertions.assertThat;

public class CatchingIntermediateTimerEventTest extends CatchingIntermediateEventTest<IntermediateTimerEvent> {

    private static final String BPMN_CATCHING_INTERMEDIATE_EVENT_FILE_PATH = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/catchingIntermediateTimerEvents.bpmn";

    private static final String EMPTY_TOP_LEVEL_EVENT_ID = "008E111E-E0EF-40F6-891F-FC0E9C2B9CEC";
    private static final String FILLED_TOP_LEVEL_EVENT_AFTER_DURATION_ID = "750BF5E5-B718-4B21-B86F-B4D55176403A";
    private static final String FILLED_TOP_LEVEL_EVENT_MULTIPLE_ID = "FDE8EFB6-6899-4074-AADD-5FEBAA90AE79";
    private static final String FILLED_TOP_LEVEL_EVENT_SPECIFIC_DATE_ID = "AA31C768-7CA4-456B-A3A3-A55CE0998FF8";

    private static final String EMPTY_SUBPROCESS_LEVEL_EVENT_ID = "55C242C9-1C54-45F4-9915-DE307C82EFC8";
    private static final String FILLED_SUBPROCESS_LEVEL_EVENT_AFTER_DURATION_ID = "05B04BBC-E071-478C-9927-008290802507";
    private static final String FILLED_SUBPROCESS_LEVEL_EVENT_MULTIPLE_ID = "8DC9B6AF-B8E3-491A-B440-DC9E5BD47EE3";
    private static final String FILLED_SUBPROCESS_LEVEL_EVENT_SPECIFIC_DATE_ID = "3B7F8E55-5110-4355-B0B7-07CEB00AA0D3";

    private static final String EMPTY_WITH_EDGES_TOP_LEVEL_EVENT_ID = "A2D7D12D-D33F-4419-A279-4641E0542CF6";
    private static final String FILLED_WITH_EDGES_TOP_LEVEL_EVENT_AFTER_DURATION_ID = "822849A7-4DBD-4BAD-8C84-63D5676E667F";
    private static final String FILLED_WITH_EDGES_TOP_LEVEL_EVENT_MULTIPLE_ID = "1549183C-9E76-4EF3-8C7B-40B18FD122EE";
    private static final String FILLED_WITH_EDGES_TOP_LEVEL_EVENT_SPECIFIC_DATE_ID = "E6D87D0A-C10B-4861-A8B0-671F34441696";

    private static final String EMPTY_WITH_EDGES_SUBPROCESS_LEVEL_EVENT_ID = "5C60A844-2BE4-4FE6-91DE-62E547400681";
    private static final String FILLED_WITH_EDGES_SUBPROCESS_LEVEL_EVENT_AFTER_DURATION_ID = "65978DFE-39BB-4279-8812-364D092BF92A";
    private static final String FILLED_WITH_EDGES_SUBPROCESS_LEVEL_EVENT_MULTIPLE_ID = "F50012CC-6345-489B-8052-6BB895163C20";
    private static final String FILLED_WITH_EDGES_SUBPROCESS_LEVEL_EVENT_SPECIFIC_DATE_ID = "3B5B9E49-E238-485D-9303-8BD0B3310D94";

    private static final String SLA_DUE_DATE = "12/25/1983";

    private static final int AMOUNT_OF_NODES_IN_DIAGRAM = 45;

    public CatchingIntermediateTimerEventTest() throws Exception {
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEventFilledProperties() throws Exception {
        final String EVENT_NAME_AFTER_DURATION = "timer01 name ~!@#$%^&*()_+-=[]\\{}|;':\",./<>?`";
        final String EVENT_DOCUMENTATION_AFTER_DURATION = "timer01 doc\n ~!@#$%^&*()_+`1234567890-=[]\\{}|;':\",./<>?";
        final String EVENT_TIMER_VALUE_AFTER_DURATION = "PT1H15M";

        final String EVENT_NAME_MULTIPLE = "timer03 name ~!@#$%^&*()_+-=[]\\{}|;':\",./<>?`";
        final String EVENT_DOCUMENTATION_MULTIPLE = "timer03 doc\n ~!@#$%^&*()_+`1234567890-=[]\\{}|;':\",./<>?";
        final String EVENT_TIMER_VALUE_MULTIPLE = "5m10s";
        final String EVENT_TIMER_VALUE_LANGUAGE_MULTIPLE = "cron";

        final String EVENT_NAME_SPECIFIC_DATE = "timer02 name ~!@#$%^&*()_+-=[]\\{}|;':\",./<>?`";
        final String EVENT_DOCUMENTATION_SPECIFIC_DATE = "timer02 doc\n ~!@#$%^&*()_+`1234567890-=[]\\{}|;':\",./<>?";
        final String EVENT_TIMER_VALUE_SPECIFIC_DATE = "2018-08-13T15:10:27+02:00";

        Diagram<Graph, Metadata> diagram = getDiagram();
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateTimerEvent filledTopEventAfterDuration = getCatchingIntermediateNodeById(diagram,
                                                                                             FILLED_TOP_LEVEL_EVENT_AFTER_DURATION_ID,
                                                                                             HAS_NO_INCOME_EDGE,
                                                                                             ZERO_OUTGOING_EDGES);
        assertGeneralSet(filledTopEventAfterDuration.getGeneral(), EVENT_NAME_AFTER_DURATION, EVENT_DOCUMENTATION_AFTER_DURATION);
        assertTimerEventAfterDuration(filledTopEventAfterDuration.getExecutionSet(), EVENT_TIMER_VALUE_AFTER_DURATION, CANCELLING, SLA_DUE_DATE);

        IntermediateTimerEvent filledTopEventMultiple = getCatchingIntermediateNodeById(diagram,
                                                                                        FILLED_TOP_LEVEL_EVENT_MULTIPLE_ID,
                                                                                        HAS_NO_INCOME_EDGE,
                                                                                        ZERO_OUTGOING_EDGES);
        assertGeneralSet(filledTopEventMultiple.getGeneral(), EVENT_NAME_MULTIPLE, EVENT_DOCUMENTATION_MULTIPLE);
        assertTimerEventMultiple(filledTopEventMultiple.getExecutionSet(), EVENT_TIMER_VALUE_MULTIPLE, EVENT_TIMER_VALUE_LANGUAGE_MULTIPLE, CANCELLING, SLA_DUE_DATE);

        IntermediateTimerEvent filledTopEventSpecificDate = getCatchingIntermediateNodeById(diagram,
                                                                                            FILLED_TOP_LEVEL_EVENT_SPECIFIC_DATE_ID,
                                                                                            HAS_NO_INCOME_EDGE,
                                                                                            ZERO_OUTGOING_EDGES);
        assertGeneralSet(filledTopEventSpecificDate.getGeneral(), EVENT_NAME_SPECIFIC_DATE, EVENT_DOCUMENTATION_SPECIFIC_DATE);
        assertTimerEventSpecificDate(filledTopEventSpecificDate.getExecutionSet(), EVENT_TIMER_VALUE_SPECIFIC_DATE, CANCELLING, SLA_DUE_DATE);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEmptyEventProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = getDiagram();
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateTimerEvent emptyTopEvent = getCatchingIntermediateNodeById(diagram,
                                                                               EMPTY_TOP_LEVEL_EVENT_ID,
                                                                               HAS_NO_INCOME_EDGE,
                                                                               ZERO_OUTGOING_EDGES);
        assertGeneralSet(emptyTopEvent.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertTimerEventEmpty(emptyTopEvent.getExecutionSet(), CANCELLING, EMPTY_VALUE);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEventWithEdgesFilledProperties() {
        final String EVENT_NAME_AFTER_DURATION = "timer04 name ~!@#$%^&*()_+-=[]\\{}|;':\",./<>?`";
        final String EVENT_DOCUMENTATION_AFTER_DURATION = "timer04 doc\n ~!@#$%^&*()_+`1234567890-=[]\\{}|;':\",./<>?";
        final String EVENT_TIMER_VALUE_AFTER_DURATION = "PT1H15M";

        final String EVENT_NAME_MULTIPLE = "timer06 name ~!@#$%^&*()_+-=[]\\{}|;':\",./<>?`";
        final String EVENT_DOCUMENTATION_MULTIPLE = "timer06 doc\n ~!@#$%^&*()_+`1234567890-=[]\\{}|;':\",./<>?";
        final String EVENT_TIMER_VALUE_MULTIPLE = "R3/PT8M3S";
        //  "none" is a "not a cron" for engine and represents ISO in GUI
        final String EVENT_TIMER_VALUE_LANGUAGE_MULTIPLE = "none";

        final String EVENT_NAME_SPECIFIC_DATE = "timer05 name ~!@#$%^&*()_+-=[]\\{}|;':\",./<>?`";
        final String EVENT_DOCUMENTATION_SPECIFIC_DATE = "timer05 doc\n ~!@#$%^&*()_+`1234567890-=[]\\{}|;':\",./<>?";
        final String EVENT_TIMER_VALUE_SPECIFIC_DATE = "2018-08-13T15:05:02+02:00";

        Diagram<Graph, Metadata> diagram = getDiagram();
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateTimerEvent filledTopEventAfterDuration = getCatchingIntermediateNodeById(diagram,
                                                                                             FILLED_WITH_EDGES_TOP_LEVEL_EVENT_AFTER_DURATION_ID,
                                                                                             HAS_INCOME_EDGE,
                                                                                             TWO_OUTGOING_EDGES);
        assertGeneralSet(filledTopEventAfterDuration.getGeneral(), EVENT_NAME_AFTER_DURATION, EVENT_DOCUMENTATION_AFTER_DURATION);
        assertTimerEventAfterDuration(filledTopEventAfterDuration.getExecutionSet(), EVENT_TIMER_VALUE_AFTER_DURATION, CANCELLING, SLA_DUE_DATE);

        IntermediateTimerEvent filledTopEventMultiple = getCatchingIntermediateNodeById(diagram,
                                                                                        FILLED_WITH_EDGES_TOP_LEVEL_EVENT_MULTIPLE_ID,
                                                                                        HAS_INCOME_EDGE,
                                                                                        TWO_OUTGOING_EDGES);
        assertGeneralSet(filledTopEventMultiple.getGeneral(), EVENT_NAME_MULTIPLE, EVENT_DOCUMENTATION_MULTIPLE);
        assertTimerEventMultiple(filledTopEventMultiple.getExecutionSet(), EVENT_TIMER_VALUE_MULTIPLE, EVENT_TIMER_VALUE_LANGUAGE_MULTIPLE, CANCELLING, SLA_DUE_DATE);

        IntermediateTimerEvent filledTopEventSpecificDate = getCatchingIntermediateNodeById(diagram,
                                                                                            FILLED_WITH_EDGES_TOP_LEVEL_EVENT_SPECIFIC_DATE_ID,
                                                                                            HAS_INCOME_EDGE,
                                                                                            TWO_OUTGOING_EDGES);
        assertGeneralSet(filledTopEventSpecificDate.getGeneral(), EVENT_NAME_SPECIFIC_DATE, EVENT_DOCUMENTATION_SPECIFIC_DATE);
        assertTimerEventSpecificDate(filledTopEventSpecificDate.getExecutionSet(), EVENT_TIMER_VALUE_SPECIFIC_DATE, CANCELLING, SLA_DUE_DATE);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEventWithEdgesEmptyProperties() {
        Diagram<Graph, Metadata> diagram = getDiagram();
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateTimerEvent emptyEvent = getCatchingIntermediateNodeById(diagram,
                                                                            EMPTY_WITH_EDGES_TOP_LEVEL_EVENT_ID,
                                                                            HAS_INCOME_EDGE,
                                                                            TWO_OUTGOING_EDGES);
        assertGeneralSet(emptyEvent.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertTimerEventEmpty(emptyEvent.getExecutionSet(), CANCELLING, EMPTY_VALUE);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventFilledProperties() throws Exception {
        final String EVENT_NAME_AFTER_DURATION = "timer07 name ~!@#$%^&*()_+-=[]\\{}|;':\",./<>?`";
        final String EVENT_DOCUMENTATION_AFTER_DURATION = "timer07 doc\n ~!@#$%^&*()_+`1234567890-=[]\\{}|;':\",./<>?";
        final String EVENT_TIMER_VALUE_AFTER_DURATION = "PT1H15M";

        final String EVENT_NAME_MULTIPLE = "timer09 name ~!@#$%^&*()_+-=[]\\{}|;':\",./<>?`";
        final String EVENT_DOCUMENTATION_MULTIPLE = "timer09 doc\n ~!@#$%^&*()_+`1234567890-=[]\\{}|;':\",./<>?";
        final String EVENT_TIMER_VALUE_MULTIPLE = "R3/PT8M3S";
        //  "none" is a "not a cron" for engine and represents ISO in GUI
        final String EVENT_TIMER_VALUE_LANGUAGE_MULTIPLE = "none";

        final String EVENT_NAME_SPECIFIC_DATE = "timer08 name ~!@#$%^&*()_+-=[]\\{}|;':\",./<>?`";
        final String EVENT_DOCUMENTATION_SPECIFIC_DATE = "timer08 doc\n ~!@#$%^&*()_+`1234567890-=[]\\{}|;':\",./<>?";
        final String EVENT_TIMER_VALUE_SPECIFIC_DATE = "2018-08-13T16:20:14+02:00";

        Diagram<Graph, Metadata> diagram = getDiagram();
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateTimerEvent filledSubprocessEventAfterDuration = getCatchingIntermediateNodeById(diagram,
                                                                                                    FILLED_SUBPROCESS_LEVEL_EVENT_AFTER_DURATION_ID,
                                                                                                    HAS_NO_INCOME_EDGE,
                                                                                                    ZERO_OUTGOING_EDGES);
        assertGeneralSet(filledSubprocessEventAfterDuration.getGeneral(), EVENT_NAME_AFTER_DURATION, EVENT_DOCUMENTATION_AFTER_DURATION);
        assertTimerEventAfterDuration(filledSubprocessEventAfterDuration.getExecutionSet(), EVENT_TIMER_VALUE_AFTER_DURATION, CANCELLING, SLA_DUE_DATE);

        IntermediateTimerEvent filledSubprocessEventMultiple = getCatchingIntermediateNodeById(diagram,
                                                                                               FILLED_SUBPROCESS_LEVEL_EVENT_MULTIPLE_ID,
                                                                                               HAS_NO_INCOME_EDGE,
                                                                                               ZERO_OUTGOING_EDGES);
        assertGeneralSet(filledSubprocessEventMultiple.getGeneral(), EVENT_NAME_MULTIPLE, EVENT_DOCUMENTATION_MULTIPLE);
        assertTimerEventMultiple(filledSubprocessEventMultiple.getExecutionSet(), EVENT_TIMER_VALUE_MULTIPLE, EVENT_TIMER_VALUE_LANGUAGE_MULTIPLE, CANCELLING, SLA_DUE_DATE);

        IntermediateTimerEvent filledSubprocessEventSpecificDate = getCatchingIntermediateNodeById(diagram,
                                                                                                   FILLED_SUBPROCESS_LEVEL_EVENT_SPECIFIC_DATE_ID,
                                                                                                   HAS_NO_INCOME_EDGE,
                                                                                                   ZERO_OUTGOING_EDGES);
        assertGeneralSet(filledSubprocessEventSpecificDate.getGeneral(), EVENT_NAME_SPECIFIC_DATE, EVENT_DOCUMENTATION_SPECIFIC_DATE);
        assertTimerEventSpecificDate(filledSubprocessEventSpecificDate.getExecutionSet(), EVENT_TIMER_VALUE_SPECIFIC_DATE, CANCELLING, SLA_DUE_DATE);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventEmptyProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = getDiagram();
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateTimerEvent emptySubprocessEvent = getCatchingIntermediateNodeById(diagram,
                                                                                      EMPTY_SUBPROCESS_LEVEL_EVENT_ID,
                                                                                      HAS_NO_INCOME_EDGE,
                                                                                      ZERO_OUTGOING_EDGES);
        assertGeneralSet(emptySubprocessEvent.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertTimerEventEmpty(emptySubprocessEvent.getExecutionSet(), CANCELLING, EMPTY_VALUE);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventWithEdgesEmptyProperties() {
        Diagram<Graph, Metadata> diagram = getDiagram();
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateTimerEvent emptySubprocessEvent = getCatchingIntermediateNodeById(diagram,
                                                                                      EMPTY_WITH_EDGES_SUBPROCESS_LEVEL_EVENT_ID,
                                                                                      HAS_INCOME_EDGE,
                                                                                      TWO_OUTGOING_EDGES);
        assertGeneralSet(emptySubprocessEvent.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertTimerEventEmpty(emptySubprocessEvent.getExecutionSet(), CANCELLING, EMPTY_VALUE);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventWithEdgesFilledProperties() {
        final String EVENT_NAME_AFTER_DURATION = "timer10 name ~!@#$%^&*()_+-=[]\\{}|;':\",./<>?`";
        final String EVENT_DOCUMENTATION_AFTER_DURATION = "timer10 doc\n ~!@#$%^&*()_+`1234567890-=[]\\{}|;':\",./<>?";
        final String EVENT_TIMER_VALUE_AFTER_DURATION = "PT1H15M";

        final String EVENT_NAME_MULTIPLE = "timer12 name ~!@#$%^&*()_+-=[]\\{}|;':\",./<>?`";
        final String EVENT_DOCUMENTATION_MULTIPLE = "timer12 doc\n ~!@#$%^&*()_+`1234567890-=[]\\{}|;':\",./<>?";
        final String EVENT_TIMER_VALUE_MULTIPLE = "5m4s";
        final String EVENT_TIMER_VALUE_LANGUAGE_MULTIPLE = "cron";

        final String EVENT_NAME_SPECIFIC_DATE = "timer11 name ~!@#$%^&*()_+-=[]\\{}|;':\",./<>?`";
        final String EVENT_DOCUMENTATION_SPECIFIC_DATE = "timer11 doc\n ~!@#$%^&*()_+`1234567890-=[]\\{}|;':\",./<>?";
        final String EVENT_TIMER_VALUE_SPECIFIC_DATE = "2018-08-13T15:35:13+02:00";

        Diagram<Graph, Metadata> diagram = getDiagram();
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateTimerEvent filledTopEventAfterDuration = getCatchingIntermediateNodeById(diagram,
                                                                                             FILLED_WITH_EDGES_SUBPROCESS_LEVEL_EVENT_AFTER_DURATION_ID,
                                                                                             HAS_INCOME_EDGE,
                                                                                             TWO_OUTGOING_EDGES);
        assertGeneralSet(filledTopEventAfterDuration.getGeneral(), EVENT_NAME_AFTER_DURATION, EVENT_DOCUMENTATION_AFTER_DURATION);
        assertTimerEventAfterDuration(filledTopEventAfterDuration.getExecutionSet(), EVENT_TIMER_VALUE_AFTER_DURATION, CANCELLING, SLA_DUE_DATE);

        IntermediateTimerEvent filledTopEventMultiple = getCatchingIntermediateNodeById(diagram,
                                                                                        FILLED_WITH_EDGES_SUBPROCESS_LEVEL_EVENT_MULTIPLE_ID,
                                                                                        HAS_INCOME_EDGE,
                                                                                        TWO_OUTGOING_EDGES);
        assertGeneralSet(filledTopEventMultiple.getGeneral(), EVENT_NAME_MULTIPLE, EVENT_DOCUMENTATION_MULTIPLE);
        assertTimerEventMultiple(filledTopEventMultiple.getExecutionSet(), EVENT_TIMER_VALUE_MULTIPLE, EVENT_TIMER_VALUE_LANGUAGE_MULTIPLE, CANCELLING, SLA_DUE_DATE);

        IntermediateTimerEvent filledTopEventSpecificDate = getCatchingIntermediateNodeById(diagram,
                                                                                            FILLED_WITH_EDGES_SUBPROCESS_LEVEL_EVENT_SPECIFIC_DATE_ID,
                                                                                            HAS_INCOME_EDGE,
                                                                                            TWO_OUTGOING_EDGES);
        assertGeneralSet(filledTopEventSpecificDate.getGeneral(), EVENT_NAME_SPECIFIC_DATE, EVENT_DOCUMENTATION_SPECIFIC_DATE);
        assertTimerEventSpecificDate(filledTopEventSpecificDate.getExecutionSet(), EVENT_TIMER_VALUE_SPECIFIC_DATE, CANCELLING, SLA_DUE_DATE);
    }

    @Override
    String getBpmnCatchingIntermediateEventFilePath() {
        return BPMN_CATCHING_INTERMEDIATE_EVENT_FILE_PATH;
    }

    @Override
    Class<IntermediateTimerEvent> getCatchingIntermediateEventType() {
        return IntermediateTimerEvent.class;
    }

    @Override
    String[] getFilledTopLevelEventIds() {
        return new String[]{FILLED_TOP_LEVEL_EVENT_AFTER_DURATION_ID,
                FILLED_TOP_LEVEL_EVENT_MULTIPLE_ID,
                FILLED_TOP_LEVEL_EVENT_SPECIFIC_DATE_ID};
    }

    @Override
    String getEmptyTopLevelEventId() {
        return EMPTY_TOP_LEVEL_EVENT_ID;
    }

    @Override
    String[] getFilledSubprocessLevelEventIds() {
        return new String[]{FILLED_SUBPROCESS_LEVEL_EVENT_AFTER_DURATION_ID,
                FILLED_SUBPROCESS_LEVEL_EVENT_MULTIPLE_ID,
                FILLED_SUBPROCESS_LEVEL_EVENT_SPECIFIC_DATE_ID};
    }

    @Override
    String getEmptySubprocessLevelEventId() {
        return EMPTY_SUBPROCESS_LEVEL_EVENT_ID;
    }

    @Override
    String[] getFilledTopLevelEventWithEdgesIds() {
        return new String[]{FILLED_WITH_EDGES_TOP_LEVEL_EVENT_AFTER_DURATION_ID,
                FILLED_WITH_EDGES_TOP_LEVEL_EVENT_MULTIPLE_ID,
                FILLED_WITH_EDGES_TOP_LEVEL_EVENT_SPECIFIC_DATE_ID};
    }

    @Override
    String getEmptyTopLevelEventWithEdgesId() {
        return EMPTY_WITH_EDGES_TOP_LEVEL_EVENT_ID;
    }

    @Override
    String[] getFilledSubprocessLevelEventWithEdgesIds() {
        return new String[]{FILLED_WITH_EDGES_SUBPROCESS_LEVEL_EVENT_AFTER_DURATION_ID,
                FILLED_WITH_EDGES_SUBPROCESS_LEVEL_EVENT_MULTIPLE_ID,
                FILLED_WITH_EDGES_SUBPROCESS_LEVEL_EVENT_SPECIFIC_DATE_ID};
    }

    @Override
    String getEmptySubprocessLevelEventWithEdgesId() {
        return EMPTY_WITH_EDGES_SUBPROCESS_LEVEL_EVENT_ID;
    }

    private void assertTimerEventMultiple(CancellingTimerEventExecutionSet executionSet, String timerValue, String timeCycleLanguage, boolean isCancelling, String slaDueDate) {
        assertThat(executionSet).isNotNull();
        assertThat(executionSet.getTimerSettings()).isNotNull();
        assertThat(executionSet.getTimerSettings().getValue().getTimeCycle()).isEqualTo(timerValue);
        assertThat(executionSet.getTimerSettings().getValue().getTimeCycleLanguage()).isEqualTo(timeCycleLanguage);

        assertThat(executionSet.getTimerSettings().getValue().getTimeDuration()).isNull();
        assertThat(executionSet.getTimerSettings().getValue().getTimeDate()).isNull();

        assertEventCancelActivity(executionSet, isCancelling);
        assertEventSlaDueDate(executionSet, slaDueDate);
    }

    private void assertTimerEventAfterDuration(CancellingTimerEventExecutionSet executionSet, String timerValue, boolean isCancelling, String slaDueDate) {
        assertThat(executionSet).isNotNull();
        assertThat(executionSet.getTimerSettings()).isNotNull();
        assertThat(executionSet.getCancelActivity()).isNotNull();
        assertThat(executionSet.getTimerSettings().getValue().getTimeDuration()).isEqualTo(timerValue);
        assertThat(executionSet.getCancelActivity().getValue()).isEqualTo(isCancelling);

        assertThat(executionSet.getTimerSettings().getValue().getTimeDate()).isNull();
        assertThat(executionSet.getTimerSettings().getValue().getTimeCycle()).isNull();
        assertThat(executionSet.getTimerSettings().getValue().getTimeCycleLanguage()).isNull();

        assertEventCancelActivity(executionSet, isCancelling);
        assertEventSlaDueDate(executionSet, slaDueDate);
    }

    private void assertTimerEventSpecificDate(CancellingTimerEventExecutionSet executionSet, String dateValue, boolean isCancelling, String slaDueDate) {
        assertThat(executionSet).isNotNull();
        assertThat(executionSet.getTimerSettings()).isNotNull();
        assertThat(executionSet.getCancelActivity()).isNotNull();
        assertThat(executionSet.getTimerSettings().getValue().getTimeDate()).isEqualTo(dateValue);
        assertThat(executionSet.getCancelActivity().getValue()).isEqualTo(isCancelling);

        assertThat(executionSet.getTimerSettings().getValue().getTimeCycle()).isNull();
        assertThat(executionSet.getTimerSettings().getValue().getTimeDuration()).isNull();
        assertThat(executionSet.getTimerSettings().getValue().getTimeCycleLanguage()).isNull();

        assertEventCancelActivity(executionSet, isCancelling);
        assertEventSlaDueDate(executionSet, slaDueDate);
    }

    private void assertTimerEventEmpty(CancellingTimerEventExecutionSet executionSet, boolean isCancelling, String slaDueDate) {
        assertThat(executionSet).isNotNull();
        assertThat(executionSet.getTimerSettings()).isNotNull();
        assertThat(executionSet.getCancelActivity()).isNotNull();
        assertThat(executionSet.getCancelActivity().getValue()).isEqualTo(isCancelling);

        assertThat(executionSet.getTimerSettings().getValue().getTimeDate()).isNull();
        assertThat(executionSet.getTimerSettings().getValue().getTimeCycle()).isNull();
        assertThat(executionSet.getTimerSettings().getValue().getTimeDuration()).isNull();
        assertThat(executionSet.getTimerSettings().getValue().getTimeCycleLanguage()).isNull();

        assertEventCancelActivity(executionSet, isCancelling);
        assertEventSlaDueDate(executionSet, slaDueDate);
    }
}
