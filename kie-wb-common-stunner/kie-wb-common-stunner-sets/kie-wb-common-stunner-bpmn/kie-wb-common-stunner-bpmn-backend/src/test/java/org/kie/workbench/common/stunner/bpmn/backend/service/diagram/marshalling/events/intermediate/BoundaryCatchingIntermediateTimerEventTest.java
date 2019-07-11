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
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateTimerEvent;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.timer.CancellingTimerEventExecutionSet;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class BoundaryCatchingIntermediateTimerEventTest extends BoundaryCatchingIntermediateEvent<IntermediateTimerEvent> {

    private static final String BPMN_CATCHING_INTERMEDIATE_EVENT_FILE_PATH = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/boundaryTimerEvents.bpmn";

    private static final String EMPTY_TOP_LEVEL_EVENT_ID = "_688FAF0B-C5E7-4FEB-A294-316AC5BBEC24";
    private static final String FILLED_TOP_LEVEL_EVENT_AFTER_DURATION_ID = "_630B9DBB-9E07-4156-8463-FB306734FECA";
    private static final String FILLED_TOP_LEVEL_EVENT_MULTIPLE_ID = "_0171E9A9-A11F-4011-AD46-1A8CD95FB492";
    private static final String FILLED_TOP_LEVEL_EVENT_SPECIFIC_DATE_ID = "_7E6589B6-8087-4139-9D52-83E343D906C4";

    private static final String EMPTY_SUBPROCESS_LEVEL_EVENT_ID = "_A9BCC6D7-A4C6-49DD-A555-B900170B05DA";
    private static final String FILLED_SUBPROCESS_LEVEL_EVENT_AFTER_DURATION_ID = "_2A428CE5-2001-477E-B069-A6003281E4B1";
    private static final String FILLED_SUBPROCESS_LEVEL_EVENT_MULTIPLE_ID = "_A6DAAF25-2DC9-468C-9454-72A6DF9775CF";
    private static final String FILLED_SUBPROCESS_LEVEL_EVENT_SPECIFIC_DATE_ID = "_26ED8166-0AFB-4102-8646-3CE907DEF3D1";

    private static final String EMPTY_WITH_OUTGOING_EDGE_TOP_LEVEL_EVENT_ID = "_9EAB6F97-349C-4353-9B90-28AFA1D17F44";
    private static final String FILLED_WITH_OUTGOING_EDGE_TOP_LEVEL_EVENT_AFTER_DURATION_ID = "_F9DA40E6-60A9-476A-8EDB-590C7930E6BE";
    private static final String FILLED_WITH_OUTGOING_EDGE_TOP_LEVEL_EVENT_MULTIPLE_ID = "_227A685B-E144-4D15-B4F4-1F46343C7E58";
    private static final String FILLED_WITH_OUTGOING_EDGE_TOP_LEVEL_EVENT_SPECIFIC_DATE_ID = "_1BBE39ED-9D13-4A85-AA43-3B2FDDD18DD2";

    private static final String EMPTY_WITH_OUTGOING_EDGE_SUBPROCESS_LEVEL_EVENT_ID = "_10D8BFEE-C25C-4059-81F0-5D68EEB8B5A5";
    private static final String FILLED_WITH_OUTGOING_EDGE_SUBPROCESS_LEVEL_EVENT_AFTER_DURATION_ID = "_992EC0ED-950C-4CF0-B813-115BCE52AC46";
    private static final String FILLED_WITH_OUTGOING_EDGE_SUBPROCESS_LEVEL_EVENT_MULTIPLE_ID = "_3613674E-1331-4F26-8DF0-FDEAE44981A9";
    private static final String FILLED_WITH_OUTGOING_EDGE_SUBPROCESS_LEVEL_EVENT_SPECIFIC_DATE_ID = "_8B0BD7BC-32F1-42AA-8D5F-EF60E9AFB60D";

    private static final String SLA_DUE_DATE = "12/25/1983";

    private static final int AMOUNT_OF_NODES_IN_DIAGRAM = 47;

    public BoundaryCatchingIntermediateTimerEventTest(Marshaller marshallerType) {
        super(marshallerType);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEventFilledProperties() throws Exception {
        final String EVENT_NAME_AFTER_DURATION = "Boundary timer01 ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION_AFTER_DURATION = "boundary timer01 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String EVENT_TIMER_VALUE_AFTER_DURATION = "PT1H15M";

        final String EVENT_NAME_MULTIPLE = "Boundary timer02 ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION_MULTIPLE = "boundary timer02 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String EVENT_TIMER_VALUE_MULTIPLE = "5m10s";
        final String EVENT_TIMER_VALUE_LANGUAGE_MULTIPLE = "cron";

        final String EVENT_NAME_SPECIFIC_DATE = "Boundary timer03 ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION_SPECIFIC_DATE = "boundary timer03 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String EVENT_TIMER_VALUE_SPECIFIC_DATE = "2018-08-13T15:10:27+02:00";

        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_CATCHING_INTERMEDIATE_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateTimerEvent filledTopEventAfterDuration = getCatchingIntermediateNodeById(diagram,
                                                                                             FILLED_TOP_LEVEL_EVENT_AFTER_DURATION_ID,
                                                                                             HAS_NO_INCOME_EDGE,
                                                                                             HAS_NO_OUTGOING_EDGE);
        assertGeneralSet(filledTopEventAfterDuration.getGeneral(), EVENT_NAME_AFTER_DURATION, EVENT_DOCUMENTATION_AFTER_DURATION);
        assertTimerEventAfterDuration(filledTopEventAfterDuration.getExecutionSet(), EVENT_TIMER_VALUE_AFTER_DURATION, CANCELLING, SLA_DUE_DATE);

        IntermediateTimerEvent filledTopEventMultiple = getCatchingIntermediateNodeById(diagram,
                                                                                        FILLED_TOP_LEVEL_EVENT_MULTIPLE_ID,
                                                                                        HAS_NO_INCOME_EDGE,
                                                                                        HAS_NO_OUTGOING_EDGE);
        assertGeneralSet(filledTopEventMultiple.getGeneral(), EVENT_NAME_MULTIPLE, EVENT_DOCUMENTATION_MULTIPLE);
        assertTimerEventMultiple(filledTopEventMultiple.getExecutionSet(), EVENT_TIMER_VALUE_MULTIPLE, EVENT_TIMER_VALUE_LANGUAGE_MULTIPLE, CANCELLING, SLA_DUE_DATE);

        IntermediateTimerEvent filledTopEventSpecificDate = getCatchingIntermediateNodeById(diagram,
                                                                                            FILLED_TOP_LEVEL_EVENT_SPECIFIC_DATE_ID,
                                                                                            HAS_NO_INCOME_EDGE,
                                                                                            HAS_NO_OUTGOING_EDGE);
        assertGeneralSet(filledTopEventSpecificDate.getGeneral(), EVENT_NAME_SPECIFIC_DATE, EVENT_DOCUMENTATION_SPECIFIC_DATE);
        assertTimerEventSpecificDate(filledTopEventSpecificDate.getExecutionSet(), EVENT_TIMER_VALUE_SPECIFIC_DATE, CANCELLING, SLA_DUE_DATE);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEmptyEventProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_CATCHING_INTERMEDIATE_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateTimerEvent emptyTopEvent = getCatchingIntermediateNodeById(diagram,
                                                                               EMPTY_TOP_LEVEL_EVENT_ID,
                                                                               HAS_NO_INCOME_EDGE,
                                                                               HAS_NO_OUTGOING_EDGE);
        assertGeneralSet(emptyTopEvent.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertTimerEventEmpty(emptyTopEvent.getExecutionSet(), NON_CANCELLING, EMPTY_VALUE);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventFilledProperties() throws Exception {
        final String EVENT_NAME_AFTER_DURATION = "Boundary timer07 ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION_AFTER_DURATION = "boundary timer07 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String EVENT_TIMER_VALUE_AFTER_DURATION = "PT1H15M";

        final String EVENT_NAME_MULTIPLE = "Boundary timer08 ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION_MULTIPLE = "boundary timer08 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String EVENT_TIMER_VALUE_MULTIPLE = "R3/PT8M3S";
        //  "none" is a "not a cron" for engine and represents ISO in GUI
        final String EVENT_TIMER_VALUE_LANGUAGE_MULTIPLE = "none";

        final String EVENT_NAME_SPECIFIC_DATE = "Boundary timer09 ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION_SPECIFIC_DATE = "boundary timer09 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String EVENT_TIMER_VALUE_SPECIFIC_DATE = "2018-08-13T15:10:27+02:00";

        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_CATCHING_INTERMEDIATE_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateTimerEvent filledSubprocessEventAfterDuration = getCatchingIntermediateNodeById(diagram,
                                                                                                    FILLED_SUBPROCESS_LEVEL_EVENT_AFTER_DURATION_ID,
                                                                                                    HAS_NO_INCOME_EDGE,
                                                                                                    HAS_NO_OUTGOING_EDGE);
        assertGeneralSet(filledSubprocessEventAfterDuration.getGeneral(), EVENT_NAME_AFTER_DURATION, EVENT_DOCUMENTATION_AFTER_DURATION);
        assertTimerEventAfterDuration(filledSubprocessEventAfterDuration.getExecutionSet(), EVENT_TIMER_VALUE_AFTER_DURATION, CANCELLING, SLA_DUE_DATE);

        IntermediateTimerEvent filledSubprocessEventMultiple = getCatchingIntermediateNodeById(diagram,
                                                                                               FILLED_SUBPROCESS_LEVEL_EVENT_MULTIPLE_ID,
                                                                                               HAS_NO_INCOME_EDGE,
                                                                                               HAS_NO_OUTGOING_EDGE);
        assertGeneralSet(filledSubprocessEventMultiple.getGeneral(), EVENT_NAME_MULTIPLE, EVENT_DOCUMENTATION_MULTIPLE);
        assertTimerEventMultiple(filledSubprocessEventMultiple.getExecutionSet(), EVENT_TIMER_VALUE_MULTIPLE, EVENT_TIMER_VALUE_LANGUAGE_MULTIPLE, CANCELLING, SLA_DUE_DATE);

        IntermediateTimerEvent filledSubprocessEventSpecificDate = getCatchingIntermediateNodeById(diagram,
                                                                                                   FILLED_SUBPROCESS_LEVEL_EVENT_SPECIFIC_DATE_ID,
                                                                                                   HAS_NO_INCOME_EDGE,
                                                                                                   HAS_NO_OUTGOING_EDGE);
        assertGeneralSet(filledSubprocessEventSpecificDate.getGeneral(), EVENT_NAME_SPECIFIC_DATE, EVENT_DOCUMENTATION_SPECIFIC_DATE);
        assertTimerEventSpecificDate(filledSubprocessEventSpecificDate.getExecutionSet(), EVENT_TIMER_VALUE_SPECIFIC_DATE, CANCELLING, SLA_DUE_DATE);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventEmptyProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_CATCHING_INTERMEDIATE_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateTimerEvent emptySubprocessEvent = getCatchingIntermediateNodeById(diagram,
                                                                                      EMPTY_SUBPROCESS_LEVEL_EVENT_ID,
                                                                                      HAS_NO_INCOME_EDGE,
                                                                                      HAS_NO_OUTGOING_EDGE);
        assertGeneralSet(emptySubprocessEvent.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertTimerEventEmpty(emptySubprocessEvent.getExecutionSet(), NON_CANCELLING, EMPTY_VALUE);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEventWithEdgesFilledProperties() throws Exception {
        final String EVENT_NAME_AFTER_DURATION = "Boundary timer04 ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION_AFTER_DURATION = "boundary timer04 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String EVENT_TIMER_VALUE_AFTER_DURATION = "PT1H15M";

        final String EVENT_NAME_MULTIPLE = "Boundary timer05 ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION_MULTIPLE = "boundary timer05 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String EVENT_TIMER_VALUE_MULTIPLE = "R3/PT8M3S";
        //  "none" is a "not a cron" for engine and represents ISO in GUI
        final String EVENT_TIMER_VALUE_LANGUAGE_MULTIPLE = "none";

        final String EVENT_NAME_SPECIFIC_DATE = "Boundary timer06 ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION_SPECIFIC_DATE = "boundary timer06 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String EVENT_TIMER_VALUE_SPECIFIC_DATE = "2018-08-13T15:10:27+02:00";

        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_CATCHING_INTERMEDIATE_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateTimerEvent filledTopEventAfterDuration = getCatchingIntermediateNodeById(diagram,
                                                                                             FILLED_WITH_OUTGOING_EDGE_TOP_LEVEL_EVENT_AFTER_DURATION_ID,
                                                                                             HAS_NO_INCOME_EDGE,
                                                                                             HAS_OUTGOING_EDGE);
        assertGeneralSet(filledTopEventAfterDuration.getGeneral(), EVENT_NAME_AFTER_DURATION, EVENT_DOCUMENTATION_AFTER_DURATION);
        assertTimerEventAfterDuration(filledTopEventAfterDuration.getExecutionSet(), EVENT_TIMER_VALUE_AFTER_DURATION, CANCELLING, SLA_DUE_DATE);

        IntermediateTimerEvent filledTopEventMultiple = getCatchingIntermediateNodeById(diagram,
                                                                                        FILLED_WITH_OUTGOING_EDGE_TOP_LEVEL_EVENT_MULTIPLE_ID,
                                                                                        HAS_NO_INCOME_EDGE,
                                                                                        HAS_OUTGOING_EDGE);
        assertGeneralSet(filledTopEventMultiple.getGeneral(), EVENT_NAME_MULTIPLE, EVENT_DOCUMENTATION_MULTIPLE);
        assertTimerEventMultiple(filledTopEventMultiple.getExecutionSet(), EVENT_TIMER_VALUE_MULTIPLE, EVENT_TIMER_VALUE_LANGUAGE_MULTIPLE, CANCELLING, SLA_DUE_DATE);

        IntermediateTimerEvent filledTopEventSpecificDate = getCatchingIntermediateNodeById(diagram,
                                                                                            FILLED_WITH_OUTGOING_EDGE_TOP_LEVEL_EVENT_SPECIFIC_DATE_ID,
                                                                                            HAS_NO_INCOME_EDGE,
                                                                                            HAS_OUTGOING_EDGE);
        assertGeneralSet(filledTopEventSpecificDate.getGeneral(), EVENT_NAME_SPECIFIC_DATE, EVENT_DOCUMENTATION_SPECIFIC_DATE);
        assertTimerEventSpecificDate(filledTopEventSpecificDate.getExecutionSet(), EVENT_TIMER_VALUE_SPECIFIC_DATE, CANCELLING, SLA_DUE_DATE);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEventWithEdgesEmptyProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_CATCHING_INTERMEDIATE_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateTimerEvent emptyEvent = getCatchingIntermediateNodeById(diagram,
                                                                            EMPTY_WITH_OUTGOING_EDGE_TOP_LEVEL_EVENT_ID,
                                                                            HAS_NO_INCOME_EDGE,
                                                                            HAS_OUTGOING_EDGE);
        assertGeneralSet(emptyEvent.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertTimerEventEmpty(emptyEvent.getExecutionSet(), NON_CANCELLING, EMPTY_VALUE);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventWithEdgesEmptyProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_CATCHING_INTERMEDIATE_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateTimerEvent emptySubprocessEvent = getCatchingIntermediateNodeById(diagram,
                                                                                      EMPTY_WITH_OUTGOING_EDGE_SUBPROCESS_LEVEL_EVENT_ID,
                                                                                      HAS_NO_INCOME_EDGE,
                                                                                      HAS_OUTGOING_EDGE);
        assertGeneralSet(emptySubprocessEvent.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertTimerEventEmpty(emptySubprocessEvent.getExecutionSet(), NON_CANCELLING, EMPTY_VALUE);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventWithEdgesFilledProperties() throws Exception {
        final String EVENT_NAME_AFTER_DURATION = "Boundary timer10 ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION_AFTER_DURATION = "boundary timer10 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String EVENT_TIMER_VALUE_AFTER_DURATION = "PT1H15M";

        final String EVENT_NAME_MULTIPLE = "Boundary timer11 ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION_MULTIPLE = "boundary timer11 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String EVENT_TIMER_VALUE_MULTIPLE = "5m10s";
        final String EVENT_TIMER_VALUE_LANGUAGE_MULTIPLE = "cron";

        final String EVENT_NAME_SPECIFIC_DATE = "Boundary timer12 ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION_SPECIFIC_DATE = "boundary timer12 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String EVENT_TIMER_VALUE_SPECIFIC_DATE = "2018-08-13T15:10:27+02:00";

        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_CATCHING_INTERMEDIATE_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateTimerEvent filledTopEventAfterDuration = getCatchingIntermediateNodeById(diagram,
                                                                                             FILLED_WITH_OUTGOING_EDGE_SUBPROCESS_LEVEL_EVENT_AFTER_DURATION_ID,
                                                                                             HAS_NO_INCOME_EDGE,
                                                                                             HAS_OUTGOING_EDGE);
        assertGeneralSet(filledTopEventAfterDuration.getGeneral(), EVENT_NAME_AFTER_DURATION, EVENT_DOCUMENTATION_AFTER_DURATION);
        assertTimerEventAfterDuration(filledTopEventAfterDuration.getExecutionSet(), EVENT_TIMER_VALUE_AFTER_DURATION, CANCELLING, SLA_DUE_DATE);

        IntermediateTimerEvent filledTopEventMultiple = getCatchingIntermediateNodeById(diagram,
                                                                                        FILLED_WITH_OUTGOING_EDGE_SUBPROCESS_LEVEL_EVENT_MULTIPLE_ID,
                                                                                        HAS_NO_INCOME_EDGE,
                                                                                        HAS_OUTGOING_EDGE);
        assertGeneralSet(filledTopEventMultiple.getGeneral(), EVENT_NAME_MULTIPLE, EVENT_DOCUMENTATION_MULTIPLE);
        assertTimerEventMultiple(filledTopEventMultiple.getExecutionSet(), EVENT_TIMER_VALUE_MULTIPLE, EVENT_TIMER_VALUE_LANGUAGE_MULTIPLE, CANCELLING, SLA_DUE_DATE);

        IntermediateTimerEvent filledTopEventSpecificDate = getCatchingIntermediateNodeById(diagram,
                                                                                            FILLED_WITH_OUTGOING_EDGE_SUBPROCESS_LEVEL_EVENT_SPECIFIC_DATE_ID,
                                                                                            HAS_NO_INCOME_EDGE,
                                                                                            HAS_OUTGOING_EDGE);
        assertGeneralSet(filledTopEventSpecificDate.getGeneral(), EVENT_NAME_SPECIFIC_DATE, EVENT_DOCUMENTATION_SPECIFIC_DATE);
        assertTimerEventSpecificDate(filledTopEventSpecificDate.getExecutionSet(), EVENT_TIMER_VALUE_SPECIFIC_DATE, CANCELLING, SLA_DUE_DATE);
    }

    @Test
    @Override
    public void testMarshallTopLevelEventFilledProperties() throws Exception {
        checkEventMarshalling(FILLED_TOP_LEVEL_EVENT_AFTER_DURATION_ID, HAS_NO_INCOME_EDGE, HAS_NO_OUTGOING_EDGE);
        checkEventMarshalling(FILLED_TOP_LEVEL_EVENT_MULTIPLE_ID, HAS_NO_INCOME_EDGE, HAS_NO_OUTGOING_EDGE);
        checkEventMarshalling(FILLED_TOP_LEVEL_EVENT_SPECIFIC_DATE_ID, HAS_NO_INCOME_EDGE, HAS_NO_OUTGOING_EDGE);
    }

    @Test
    @Override
    public void testMarshallSubprocessLevelEventFilledProperties() throws Exception {
        checkEventMarshalling(FILLED_SUBPROCESS_LEVEL_EVENT_AFTER_DURATION_ID, HAS_NO_INCOME_EDGE, HAS_NO_OUTGOING_EDGE);
        checkEventMarshalling(FILLED_SUBPROCESS_LEVEL_EVENT_MULTIPLE_ID, HAS_NO_INCOME_EDGE, HAS_NO_OUTGOING_EDGE);
        checkEventMarshalling(FILLED_SUBPROCESS_LEVEL_EVENT_SPECIFIC_DATE_ID, HAS_NO_INCOME_EDGE, HAS_NO_OUTGOING_EDGE);
    }

    @Test
    @Override
    public void testMarshallTopLevelEventWithEdgesFilledProperties() throws Exception {
        checkEventMarshalling(FILLED_WITH_OUTGOING_EDGE_TOP_LEVEL_EVENT_AFTER_DURATION_ID, HAS_NO_INCOME_EDGE, HAS_OUTGOING_EDGE);
        checkEventMarshalling(FILLED_WITH_OUTGOING_EDGE_TOP_LEVEL_EVENT_MULTIPLE_ID, HAS_NO_INCOME_EDGE, HAS_OUTGOING_EDGE);
        checkEventMarshalling(FILLED_WITH_OUTGOING_EDGE_TOP_LEVEL_EVENT_SPECIFIC_DATE_ID, HAS_NO_INCOME_EDGE, HAS_OUTGOING_EDGE);
    }

    @Test
    @Override
    public void testMarshallSubprocessLevelEventWithEdgesFilledProperties() throws Exception {
        checkEventMarshalling(FILLED_WITH_OUTGOING_EDGE_SUBPROCESS_LEVEL_EVENT_AFTER_DURATION_ID, HAS_NO_INCOME_EDGE, HAS_OUTGOING_EDGE);
        checkEventMarshalling(FILLED_WITH_OUTGOING_EDGE_SUBPROCESS_LEVEL_EVENT_MULTIPLE_ID, HAS_NO_INCOME_EDGE, HAS_OUTGOING_EDGE);
        checkEventMarshalling(FILLED_WITH_OUTGOING_EDGE_SUBPROCESS_LEVEL_EVENT_SPECIFIC_DATE_ID, HAS_NO_INCOME_EDGE, HAS_OUTGOING_EDGE);
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
    String getFilledTopLevelEventId() {
        // There are several Filled events, test method is overwritten
        return null;
    }

    @Override
    String getEmptyTopLevelEventId() {
        return EMPTY_TOP_LEVEL_EVENT_ID;
    }

    @Override
    String getFilledSubprocessLevelEventId() {
        // There are several Filled events, test method is overwritten
        return null;
    }

    @Override
    String getEmptySubprocessLevelEventId() {
        return EMPTY_SUBPROCESS_LEVEL_EVENT_ID;
    }

    @Override
    String getFilledTopLevelEventWithEdgesId() {
        // There are several Filled events, test method is overwritten
        return null;
    }

    @Override
    String getEmptyTopLevelEventWithEdgesId() {
        return EMPTY_WITH_OUTGOING_EDGE_TOP_LEVEL_EVENT_ID;
    }

    @Override
    String getFilledSubprocessLevelEventWithEdgesId() {
        // There are several Filled events, test method is overwritten
        return null;
    }

    @Override
    String getEmptySubprocessLevelEventWithEdgesId() {
        return EMPTY_WITH_OUTGOING_EDGE_SUBPROCESS_LEVEL_EVENT_ID;
    }

    private void assertTimerEventMultiple(CancellingTimerEventExecutionSet executionSet, String timerValue, String timeCycleLanguage, boolean isCancelling, String slaDueDate) {
        assertNotNull(executionSet);
        assertNotNull(executionSet.getTimerSettings());
        assertEquals(timerValue, executionSet.getTimerSettings().getValue().getTimeCycle());
        assertEquals(timeCycleLanguage, executionSet.getTimerSettings().getValue().getTimeCycleLanguage());

        assertNull(executionSet.getTimerSettings().getValue().getTimeDuration());
        assertNull(executionSet.getTimerSettings().getValue().getTimeDate());

        assertEventCancelActivity(executionSet, isCancelling);
        assertTimerEventSlaDueDate(executionSet, slaDueDate);
    }

    private void assertTimerEventAfterDuration(CancellingTimerEventExecutionSet executionSet, String timerValue, boolean isCancelling, String slaDueDate) {
        assertNotNull(executionSet);
        assertNotNull(executionSet.getTimerSettings());
        assertEquals(timerValue, executionSet.getTimerSettings().getValue().getTimeDuration());

        assertNull(executionSet.getTimerSettings().getValue().getTimeDate());
        assertNull(executionSet.getTimerSettings().getValue().getTimeCycle());
        assertNull(executionSet.getTimerSettings().getValue().getTimeCycleLanguage());

        assertEventCancelActivity(executionSet, isCancelling);
        assertTimerEventSlaDueDate(executionSet, slaDueDate);
    }

    private void assertTimerEventSpecificDate(CancellingTimerEventExecutionSet executionSet, String dateValue, boolean isCancelling, String slaDueDate) {
        assertNotNull(executionSet);
        assertNotNull(executionSet.getTimerSettings());
        assertEquals(dateValue, executionSet.getTimerSettings().getValue().getTimeDate());

        assertNull(executionSet.getTimerSettings().getValue().getTimeCycle());
        assertNull(executionSet.getTimerSettings().getValue().getTimeDuration());
        assertNull(executionSet.getTimerSettings().getValue().getTimeCycleLanguage());

        assertEventCancelActivity(executionSet, isCancelling);
        assertTimerEventSlaDueDate(executionSet, slaDueDate);
    }

    private void assertTimerEventEmpty(CancellingTimerEventExecutionSet executionSet, boolean isCancelling, String slaDueDate) {
        assertNotNull(executionSet);
        assertNotNull(executionSet.getTimerSettings());

        assertNull(executionSet.getTimerSettings().getValue().getTimeDate());
        assertNull(executionSet.getTimerSettings().getValue().getTimeCycle());
        assertNull(executionSet.getTimerSettings().getValue().getTimeDuration());
        assertNull(executionSet.getTimerSettings().getValue().getTimeCycleLanguage());

        assertEventCancelActivity(executionSet, isCancelling);
        assertTimerEventSlaDueDate(executionSet, slaDueDate);
    }
}
