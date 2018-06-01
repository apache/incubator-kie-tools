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

package org.kie.workbench.common.stunner.bpmn.backend.service.diagram.marshalling.events;

import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.backend.service.diagram.marshalling.Marshaller;
import org.kie.workbench.common.stunner.bpmn.definition.EndErrorEvent;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.error.ErrorEventExecutionSet;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class EndErrorEventTest extends EndEvent<EndErrorEvent> {

    private static final String BPMN_END_EVENT_FILE_PATH = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/endErrorEvents.bpmn";

    private static final String EMPTY_TOP_LEVEL_EVENT_ID = "_2E09ED8C-6B13-49C1-B7BE-9D4D152FF691";
    private static final String FILLED_TOP_LEVEL_EVENT_ID = "_998BB72E-AA24-4814-99CC-A7A7AA9EE976";
    private static final String EMPTY_SUBPROCESS_LEVEL_EVENT_ID = "_1F52246B-41C6-495D-B91A-0703BA9F2D9E";
    private static final String FILLED_SUBPROCESS_LEVEL_EVENT_ID = "_01DE8A98-96FD-4575-B9AA-07C04A54255A";

    private static final String EMPTY_WITH_INCOME_TOP_LEVEL_EVENT_ID = "_A050459F-39A4-4D45-8F67-BBB41D49CC6B";
    private static final String FILLED_WITH_INCOME_TOP_LEVEL_EVENT_ID = "_BFDCF173-E6EB-44B3-B68E-616382B9D858";
    private static final String EMPTY_WITH_INCOME_SUBPROCESS_LEVEL_EVENT_ID = "_5DC3BF07-4A4B-4994-B571-24CA6C22C8A2";
    private static final String FILLED_WITH_INCOME_SUBPROCESS_LEVEL_EVENT_ID = "_1FC5A7E1-A787-47D4-9B81-5FF13441E814";

    private static final int AMOUNT_OF_NODES_IN_DIAGRAM = 14;

    public EndErrorEventTest(Marshaller marshallerType) {
        super(marshallerType);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEventFilledProperties() throws Exception {
        final String EVENT_NAME = "end event01 ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION = "end event01 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String EVENT_REF = "error01";
        final String EVENT_DATA_OUTPUT = "error01:String||||[din]processGlobalVar->error01";

        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_END_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        EndErrorEvent filledTopEvent = getEndNodeById(diagram,
                                                      FILLED_TOP_LEVEL_EVENT_ID,
                                                      HAS_NO_INCOME_EDGE);
        assertGeneralSet(filledTopEvent.getGeneral(), EVENT_NAME, EVENT_DOCUMENTATION);
        assertErrorEventExecutionSet(filledTopEvent.getExecutionSet(), EVENT_REF);
        assertDataIOSet(filledTopEvent.getDataIOSet(), EVENT_DATA_OUTPUT);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEmptyEventProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_END_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        EndErrorEvent emptyTopEvent = getEndNodeById(diagram,
                                                     EMPTY_TOP_LEVEL_EVENT_ID,
                                                     HAS_NO_INCOME_EDGE);
        assertGeneralSet(emptyTopEvent.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventFilledProperties() throws Exception {
        final String EVENT_NAME = "end event03 ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION = "end event03 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String EVENT_REF = "error03";
        final String EVENT_DATA_OUTPUT = "error03:String||||[din]processGlobalVar->error03";

        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_END_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        EndErrorEvent filledSubprocessEvent = getEndNodeById(diagram,
                                                             FILLED_SUBPROCESS_LEVEL_EVENT_ID,
                                                             HAS_NO_INCOME_EDGE);
        assertGeneralSet(filledSubprocessEvent.getGeneral(), EVENT_NAME, EVENT_DOCUMENTATION);
        assertErrorEventExecutionSet(filledSubprocessEvent.getExecutionSet(), EVENT_REF);
        assertDataIOSet(filledSubprocessEvent.getDataIOSet(), EVENT_DATA_OUTPUT);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventEmptyProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_END_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        EndErrorEvent emptySubprocessEvent = getEndNodeById(diagram,
                                                            EMPTY_SUBPROCESS_LEVEL_EVENT_ID,
                                                            HAS_NO_INCOME_EDGE);
        assertGeneralSet(emptySubprocessEvent.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEventWithIncomeFilledProperties() throws Exception {
        final String EVENT_NAME = "end event02 ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION = "end event02 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String EVENT_REF = "error02";
        final String EVENT_DATA_OUTPUT = "error02:String||||[din]processGlobalVar->error02";

        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_END_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        EndErrorEvent filledSubprocessEvent = getEndNodeById(diagram,
                                                             FILLED_WITH_INCOME_TOP_LEVEL_EVENT_ID,
                                                             HAS_INCOME_EDGE);
        assertGeneralSet(filledSubprocessEvent.getGeneral(), EVENT_NAME, EVENT_DOCUMENTATION);
        assertErrorEventExecutionSet(filledSubprocessEvent.getExecutionSet(), EVENT_REF);
        assertDataIOSet(filledSubprocessEvent.getDataIOSet(), EVENT_DATA_OUTPUT);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEventWithIncomeEmptyProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_END_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        EndErrorEvent emptyEvent = getEndNodeById(diagram,
                                                  EMPTY_WITH_INCOME_TOP_LEVEL_EVENT_ID,
                                                  HAS_INCOME_EDGE);
        assertGeneralSet(emptyEvent.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventWithIncomeEmptyProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_END_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        EndErrorEvent emptySubprocessEvent = getEndNodeById(diagram,
                                                            EMPTY_WITH_INCOME_SUBPROCESS_LEVEL_EVENT_ID,
                                                            HAS_INCOME_EDGE);
        assertGeneralSet(emptySubprocessEvent.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventWithIncomeFilledProperties() throws Exception {
        final String EVENT_NAME = "end event04 ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION = "end event04 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String EVENT_REF = "error04";
        final String EVENT_DATA_OUTPUT = "error04:String||||[din]processGlobalVar->error04";

        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_END_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        EndErrorEvent filledSubprocessEvent = getEndNodeById(diagram,
                                                             FILLED_WITH_INCOME_SUBPROCESS_LEVEL_EVENT_ID,
                                                             HAS_INCOME_EDGE);
        assertGeneralSet(filledSubprocessEvent.getGeneral(), EVENT_NAME, EVENT_DOCUMENTATION);
        assertErrorEventExecutionSet(filledSubprocessEvent.getExecutionSet(), EVENT_REF);
        assertDataIOSet(filledSubprocessEvent.getDataIOSet(), EVENT_DATA_OUTPUT);
    }

    @Override
    String getBpmnEndEventFilePath() {
        return BPMN_END_EVENT_FILE_PATH;
    }

    @Override
    Class<EndErrorEvent> getEndEventType() {
        return EndErrorEvent.class;
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
    String getFilledTopLevelEventWithIncomeId() {
        return FILLED_WITH_INCOME_TOP_LEVEL_EVENT_ID;
    }

    @Override
    String getEmptyTopLevelEventWithIncomeId() {
        return EMPTY_WITH_INCOME_TOP_LEVEL_EVENT_ID;
    }

    @Override
    String getFilledSubprocessLevelEventWithIncomeId() {
        return FILLED_WITH_INCOME_SUBPROCESS_LEVEL_EVENT_ID;
    }

    @Override
    String getEmptySubprocessLevelEventWithIncomeId() {
        return EMPTY_WITH_INCOME_SUBPROCESS_LEVEL_EVENT_ID;
    }

    private void assertErrorEventExecutionSet(ErrorEventExecutionSet executionSet, String eventName) {
        assertNotNull(executionSet);
        assertNotNull(executionSet.getErrorRef());
        assertEquals(eventName, executionSet.getErrorRef().getValue());
    }
}
