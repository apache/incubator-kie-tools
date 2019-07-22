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
import org.kie.workbench.common.stunner.bpmn.definition.EndTerminateEvent;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;

public class EndTerminateEventTest extends EndEventTest<EndTerminateEvent> {

    private static final String BPMN_END_EVENT_FILE_PATH = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/endTerminateEvents.bpmn";

    private static final String EMPTY_TOP_LEVEL_EVENT_ID = "_1052A06B-1754-4CE4-ABF0-04A9772209DD";
    private static final String FILLED_TOP_LEVEL_EVENT_ID = "_56ADEAE8-A2E3-4BC5-A6EB-04DD7F5307AD";
    private static final String EMPTY_SUBPROCESS_LEVEL_EVENT_ID = "_36852679-C224-4244-899E-B6967F207B30";
    private static final String FILLED_SUBPROCESS_LEVEL_EVENT_ID = "_819BDB1B-9F61-4A8B-B2E5-D7625AC40CF1";

    private static final String EMPTY_WITH_INCOME_TOP_LEVEL_EVENT_ID = "_0E43EF74-ED54-4B8B-B58C-A6FE56DEC30F";
    private static final String FILLED_WITH_INCOME_TOP_LEVEL_EVENT_ID = "_67312F45-72EF-49BC-B60C-01C4EBC847CE";
    private static final String EMPTY_WITH_INCOME_SUBPROCESS_LEVEL_EVENT_ID = "_8B8AA175-ACCB-4B39-BB87-EAE2E0F093D1";
    private static final String FILLED_WITH_INCOME_SUBPROCESS_LEVEL_EVENT_ID = "_C910096C-ED66-47E1-A274-163759FF51BD";

    private static final int AMOUNT_OF_NODES_IN_DIAGRAM = 14;

    @Test
    @Override
    public void testUnmarshallTopLevelEventFilledProperties() throws Exception {
        final String EVENT_NAME = "end event01 ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION = "end event01 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";

        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_END_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        EndTerminateEvent filledTopEvent = getEndNodeById(diagram,
                                                          FILLED_TOP_LEVEL_EVENT_ID,
                                                          HAS_NO_INCOME_EDGE);
        assertGeneralSet(filledTopEvent.getGeneral(), EVENT_NAME, EVENT_DOCUMENTATION);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEmptyEventProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_END_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        EndTerminateEvent emptyTopEvent = getEndNodeById(diagram,
                                                         EMPTY_TOP_LEVEL_EVENT_ID,
                                                         HAS_NO_INCOME_EDGE);
        assertGeneralSet(emptyTopEvent.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventFilledProperties() throws Exception {
        final String EVENT_NAME = "end event03 ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION = "end event03 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";

        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_END_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        EndTerminateEvent filledSubprocessEvent = getEndNodeById(diagram,
                                                                 FILLED_SUBPROCESS_LEVEL_EVENT_ID,
                                                                 HAS_NO_INCOME_EDGE);
        assertGeneralSet(filledSubprocessEvent.getGeneral(), EVENT_NAME, EVENT_DOCUMENTATION);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventEmptyProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_END_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        EndTerminateEvent emptySubprocessEvent = getEndNodeById(diagram,
                                                                EMPTY_SUBPROCESS_LEVEL_EVENT_ID,
                                                                HAS_NO_INCOME_EDGE);
        assertGeneralSet(emptySubprocessEvent.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEventWithIncomeFilledProperties() throws Exception {
        final String EVENT_NAME = "end event02 ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION = "end event02 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";

        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_END_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        EndTerminateEvent filledSubprocessEvent = getEndNodeById(diagram,
                                                                 FILLED_WITH_INCOME_TOP_LEVEL_EVENT_ID,
                                                                 HAS_INCOME_EDGE);
        assertGeneralSet(filledSubprocessEvent.getGeneral(), EVENT_NAME, EVENT_DOCUMENTATION);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEventWithIncomeEmptyProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_END_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        EndTerminateEvent emptyEvent = getEndNodeById(diagram,
                                                      EMPTY_WITH_INCOME_TOP_LEVEL_EVENT_ID,
                                                      HAS_INCOME_EDGE);
        assertGeneralSet(emptyEvent.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventWithIncomeEmptyProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_END_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        EndTerminateEvent emptySubprocessEvent = getEndNodeById(diagram,
                                                                EMPTY_WITH_INCOME_SUBPROCESS_LEVEL_EVENT_ID,
                                                                HAS_INCOME_EDGE);
        assertGeneralSet(emptySubprocessEvent.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventWithIncomeFilledProperties() throws Exception {
        final String EVENT_NAME = "end event04 ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION = "end event04 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";

        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_END_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        EndTerminateEvent filledSubprocessEvent = getEndNodeById(diagram,
                                                                 FILLED_WITH_INCOME_SUBPROCESS_LEVEL_EVENT_ID,
                                                                 HAS_INCOME_EDGE);
        assertGeneralSet(filledSubprocessEvent.getGeneral(), EVENT_NAME, EVENT_DOCUMENTATION);
    }

    @Override
    String getBpmnEndEventFilePath() {
        return BPMN_END_EVENT_FILE_PATH;
    }

    @Override
    Class<EndTerminateEvent> getEndEventType() {
        return EndTerminateEvent.class;
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
}
