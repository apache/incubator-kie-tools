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
import org.kie.workbench.common.stunner.bpmn.definition.EndNoneEvent;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;

public class EndNoneEventTest extends EndEvent<EndNoneEvent> {

    private static final String BPMN_END_EVENT_FILE_PATH = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/endNoneEvents.bpmn";

    private static final String EMPTY_TOP_LEVEL_EVENT_ID = "_AA76CC5D-C353-4356-8688-5868A1D37EC7";
    private static final String FILLED_TOP_LEVEL_EVENT_ID = "_D868D848-CBD6-447A-BD6C-105E15129672";
    private static final String EMPTY_SUBPROCESS_LEVEL_EVENT_ID = "_B51BA48B-E065-45B6-B2D9-F216AD566594";
    private static final String FILLED_SUBPROCESS_LEVEL_EVENT_ID = "_3BDD589E-4AA9-456D-B9F6-021BF66C361F";

    private static final String EMPTY_WITH_INCOME_TOP_LEVEL_EVENT_ID = "_8BC9A9CB-4B66-4C35-B8FC-68611645443A";
    private static final String FILLED_WITH_INCOME_TOP_LEVEL_EVENT_ID = "_EABF720E-04DA-4793-ACD1-ABBDD35AB854";
    private static final String EMPTY_WITH_INCOME_SUBPROCESS_LEVEL_EVENT_ID = "_DA8CF3C6-15ED-41CD-AD74-E7307B85E713";
    private static final String FILLED_WITH_INCOME_SUBPROCESS_LEVEL_EVENT_ID = "_D559AC42-91D2-4113-8F31-0A0E1BA90F7A";

    private static final int AMOUNT_OF_NODES_IN_DIAGRAM = 14;

    public EndNoneEventTest(Marshaller marshallerType) {
        super(marshallerType);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEventFilledProperties() throws Exception {
        final String EVENT_NAME = "end event01 ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION = "end event01 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";

        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_END_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        EndNoneEvent filledTopEvent = getEndNodeById(diagram,
                                                     FILLED_TOP_LEVEL_EVENT_ID,
                                                     HAS_NO_INCOME_EDGE);
        assertGeneralSet(filledTopEvent.getGeneral(), EVENT_NAME, EVENT_DOCUMENTATION);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEmptyEventProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_END_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        EndNoneEvent emptyTopEvent = getEndNodeById(diagram,
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

        EndNoneEvent filledSubprocessEvent = getEndNodeById(diagram,
                                                            FILLED_SUBPROCESS_LEVEL_EVENT_ID,
                                                            HAS_NO_INCOME_EDGE);
        assertGeneralSet(filledSubprocessEvent.getGeneral(), EVENT_NAME, EVENT_DOCUMENTATION);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventEmptyProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_END_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        EndNoneEvent emptySubprocessEvent = getEndNodeById(diagram,
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

        EndNoneEvent filledSubprocessEvent = getEndNodeById(diagram,
                                                            FILLED_WITH_INCOME_TOP_LEVEL_EVENT_ID,
                                                            HAS_INCOME_EDGE);
        assertGeneralSet(filledSubprocessEvent.getGeneral(), EVENT_NAME, EVENT_DOCUMENTATION);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEventWithIncomeEmptyProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_END_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        EndNoneEvent emptyEvent = getEndNodeById(diagram,
                                                 EMPTY_WITH_INCOME_TOP_LEVEL_EVENT_ID,
                                                 HAS_INCOME_EDGE);
        assertGeneralSet(emptyEvent.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventWithIncomeEmptyProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_END_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        EndNoneEvent emptySubprocessEvent = getEndNodeById(diagram,
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

        EndNoneEvent filledSubprocessEvent = getEndNodeById(diagram,
                                                            FILLED_WITH_INCOME_SUBPROCESS_LEVEL_EVENT_ID,
                                                            HAS_INCOME_EDGE);
        assertGeneralSet(filledSubprocessEvent.getGeneral(), EVENT_NAME, EVENT_DOCUMENTATION);
    }

    @Override
    String getBpmnEndEventFilePath() {
        return BPMN_END_EVENT_FILE_PATH;
    }

    @Override
    Class<EndNoneEvent> getEndEventType() {
        return EndNoneEvent.class;
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
