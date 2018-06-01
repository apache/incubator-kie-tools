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
import org.kie.workbench.common.stunner.bpmn.definition.EndMessageEvent;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.message.MessageEventExecutionSet;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class EndMessageEventTest extends EndEvent<EndMessageEvent> {

    private static final String BPMN_END_EVENT_FILE_PATH = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/endMessageEvents.bpmn";

    private static final String EMPTY_TOP_LEVEL_EVENT_ID = "_C16E011F-1891-48C9-8619-2FEF678DEA39";
    private static final String FILLED_TOP_LEVEL_EVENT_ID = "_86928E1F-5B98-43F3-AF00-FF92A2D8C19C";
    private static final String EMPTY_SUBPROCESS_LEVEL_EVENT_ID = "_1A5F9123-053C-46D7-BBC9-BBB5AB794609";
    private static final String FILLED_SUBPROCESS_LEVEL_EVENT_ID = "_8FD9C393-5605-4451-B8F6-EB5E79BC9922";

    private static final String EMPTY_WITH_INCOME_TOP_LEVEL_EVENT_ID = "_97C6413B-F73E-4D41-8AA8-D23F30691B27";
    private static final String FILLED_WITH_INCOME_TOP_LEVEL_EVENT_ID = "_DC2F1675-18E8-4918-9B61-8D8AD26799C0";
    private static final String EMPTY_WITH_INCOME_SUBPROCESS_LEVEL_EVENT_ID = "_D4D69CB0-46B6-47DA-AFB2-DF4A0AEFECA2";
    private static final String FILLED_WITH_INCOME_SUBPROCESS_LEVEL_EVENT_ID = "_6131912B-1ED5-454F-ACB4-EC623EE0C3A3";

    private static final int AMOUNT_OF_NODES_IN_DIAGRAM = 14;

    public EndMessageEventTest(Marshaller marshallerType) {
        super(marshallerType);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEventFilledProperties() throws Exception {
        final String EVENT_NAME = "end event01 ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION = "end event01 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String EVENT_REF = "message01";
        final String EVENT_DATA_OUTPUT = "message01:String||||[din]processGlobalVar->message01";

        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_END_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        EndMessageEvent filledTopEvent = getEndNodeById(diagram,
                                                        FILLED_TOP_LEVEL_EVENT_ID,
                                                        HAS_NO_INCOME_EDGE);
        assertGeneralSet(filledTopEvent.getGeneral(), EVENT_NAME, EVENT_DOCUMENTATION);
        assertMessageEventExecutionSet(filledTopEvent.getExecutionSet(), EVENT_REF);
        assertDataIOSet(filledTopEvent.getDataIOSet(), EVENT_DATA_OUTPUT);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEmptyEventProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_END_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        EndMessageEvent emptyTopEvent = getEndNodeById(diagram,
                                                       EMPTY_TOP_LEVEL_EVENT_ID,
                                                       HAS_NO_INCOME_EDGE);
        assertGeneralSet(emptyTopEvent.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventFilledProperties() throws Exception {
        final String EVENT_NAME = "end event03 ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION = "end event03 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String EVENT_REF = "message03";
        final String EVENT_DATA_OUTPUT = "message03:String||||[din]processGlobalVar->message03";

        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_END_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        EndMessageEvent filledSubprocessEvent = getEndNodeById(diagram,
                                                               FILLED_SUBPROCESS_LEVEL_EVENT_ID,
                                                               HAS_NO_INCOME_EDGE);
        assertGeneralSet(filledSubprocessEvent.getGeneral(), EVENT_NAME, EVENT_DOCUMENTATION);
        assertMessageEventExecutionSet(filledSubprocessEvent.getExecutionSet(), EVENT_REF);
        assertDataIOSet(filledSubprocessEvent.getDataIOSet(), EVENT_DATA_OUTPUT);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventEmptyProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_END_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        EndMessageEvent emptySubprocessEvent = getEndNodeById(diagram,
                                                              EMPTY_SUBPROCESS_LEVEL_EVENT_ID,
                                                              HAS_NO_INCOME_EDGE);
        assertGeneralSet(emptySubprocessEvent.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEventWithIncomeFilledProperties() throws Exception {
        final String EVENT_NAME = "end event02 ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION = "end event02 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String EVENT_REF = "message02";
        final String EVENT_DATA_OUTPUT = "message02:String||||[din]processGlobalVar->message02";

        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_END_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        EndMessageEvent filledSubprocessEvent = getEndNodeById(diagram,
                                                               FILLED_WITH_INCOME_TOP_LEVEL_EVENT_ID,
                                                               HAS_INCOME_EDGE);
        assertGeneralSet(filledSubprocessEvent.getGeneral(), EVENT_NAME, EVENT_DOCUMENTATION);
        assertMessageEventExecutionSet(filledSubprocessEvent.getExecutionSet(), EVENT_REF);
        assertDataIOSet(filledSubprocessEvent.getDataIOSet(), EVENT_DATA_OUTPUT);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEventWithIncomeEmptyProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_END_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        EndMessageEvent emptyEvent = getEndNodeById(diagram,
                                                    EMPTY_WITH_INCOME_TOP_LEVEL_EVENT_ID,
                                                    HAS_INCOME_EDGE);
        assertGeneralSet(emptyEvent.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventWithIncomeEmptyProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_END_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        EndMessageEvent emptySubprocessEvent = getEndNodeById(diagram,
                                                              EMPTY_WITH_INCOME_SUBPROCESS_LEVEL_EVENT_ID,
                                                              HAS_INCOME_EDGE);
        assertGeneralSet(emptySubprocessEvent.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventWithIncomeFilledProperties() throws Exception {
        final String EVENT_NAME = "end event04 ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION = "end event04 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String EVENT_REF = "message04";
        final String EVENT_DATA_OUTPUT = "message04:String||||[din]processGlobalVar->message04";

        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_END_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        EndMessageEvent filledSubprocessEvent = getEndNodeById(diagram,
                                                               FILLED_WITH_INCOME_SUBPROCESS_LEVEL_EVENT_ID,
                                                               HAS_INCOME_EDGE);
        assertGeneralSet(filledSubprocessEvent.getGeneral(), EVENT_NAME, EVENT_DOCUMENTATION);
        assertMessageEventExecutionSet(filledSubprocessEvent.getExecutionSet(), EVENT_REF);
        assertDataIOSet(filledSubprocessEvent.getDataIOSet(), EVENT_DATA_OUTPUT);
    }

    @Override
    String getBpmnEndEventFilePath() {
        return BPMN_END_EVENT_FILE_PATH;
    }

    @Override
    Class<EndMessageEvent> getEndEventType() {
        return EndMessageEvent.class;
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

    private void assertMessageEventExecutionSet(MessageEventExecutionSet executionSet, String eventName) {
        assertNotNull(executionSet);
        assertNotNull(executionSet.getMessageRef());
        assertEquals(eventName, executionSet.getMessageRef().getValue());
    }
}
