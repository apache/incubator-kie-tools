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
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateMessageEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.message.MessageEventExecutionSet;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ThrowingIntermediateMessageEventTest extends ThrowingIntermediateEventTest<IntermediateMessageEventThrowing> {

    private static final String BPMN_THROWING_INTERMEDIATE_EVENT_FILE_PATH = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/throwingIntermediateMessageEvents.bpmn";

    private static final String EMPTY_TOP_LEVEL_EVENT_ID = "560D560D-5A7B-4904-830D-BA7757A09EEC";
    private static final String FILLED_TOP_LEVEL_EVENT_ID = "A7B54476-6EDE-4091-ABF5-B4CE92641825";
    private static final String EMPTY_SUBPROCESS_LEVEL_EVENT_ID = "882E78A5-450C-4BE1-9F7E-4F61367F257C";
    private static final String FILLED_SUBPROCESS_LEVEL_EVENT_ID = "252328FA-FA08-42B0-BD60-971A94DFA755";

    private static final String EMPTY_WITH_INCOME_TOP_LEVEL_EVENT_ID = "A38272BC-35C4-4F28-808C-08E3B6969BBE";
    private static final String FILLED_WITH_INCOME_TOP_LEVEL_EVENT_ID = "5CD4BF0B-AD61-4DBD-B08D-C8B0F7364BE0";
    private static final String EMPTY_WITH_INCOME_SUBPROCESS_LEVEL_EVENT_ID = "ACF512B7-D95D-42F0-99E2-8427F2B23D49";
    private static final String FILLED_WITH_INCOME_SUBPROCESS_LEVEL_EVENT_ID = "A893CEAD-3027-447F-84A6-F33679EFD770";

    private static final int AMOUNT_OF_NODES_IN_DIAGRAM = 23;

    @Test
    @Override
    public void testUnmarshallTopLevelEventFilledProperties() throws Exception {
        final String EVENT_NAME = "message01 ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION = "message01 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String EVENT_REF = "message01";
        final String EVENT_DATA_INPUT = "message01:String||||[din]ProcessGlobalVar->message01";

        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_THROWING_INTERMEDIATE_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateMessageEventThrowing filledTopEvent = getThrowingIntermediateNodeById(diagram,
                                                                                          FILLED_TOP_LEVEL_EVENT_ID,
                                                                                          HAS_NO_INCOME_EDGE);
        assertGeneralSet(filledTopEvent.getGeneral(), EVENT_NAME, EVENT_DOCUMENTATION);
        assertMessageEventExecutionSet(filledTopEvent.getExecutionSet(), EVENT_REF);
        assertDataIOSet(filledTopEvent.getDataIOSet(), EVENT_DATA_INPUT);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEmptyEventProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_THROWING_INTERMEDIATE_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateMessageEventThrowing emptyTopEvent = getThrowingIntermediateNodeById(diagram,
                                                                                         EMPTY_TOP_LEVEL_EVENT_ID,
                                                                                         HAS_NO_INCOME_EDGE);
        assertGeneralSet(emptyTopEvent.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertMessageEventExecutionSet(emptyTopEvent.getExecutionSet(), EMPTY_VALUE);
        assertDataIOSet(emptyTopEvent.getDataIOSet(), EMPTY_VALUE);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventFilledProperties() throws Exception {
        final String EVENT_NAME = "message03 ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION = "message03 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String EVENT_REF = "message03";
        final String EVENT_DATA_INPUT = "message03:String||||[din]ProcessGlobalVar->message03";

        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_THROWING_INTERMEDIATE_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateMessageEventThrowing filledSubprocessEvent = getThrowingIntermediateNodeById(diagram,
                                                                                                 FILLED_SUBPROCESS_LEVEL_EVENT_ID,
                                                                                                 HAS_NO_INCOME_EDGE);
        assertGeneralSet(filledSubprocessEvent.getGeneral(), EVENT_NAME, EVENT_DOCUMENTATION);
        assertMessageEventExecutionSet(filledSubprocessEvent.getExecutionSet(), EVENT_REF);
        assertDataIOSet(filledSubprocessEvent.getDataIOSet(), EVENT_DATA_INPUT);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventEmptyProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_THROWING_INTERMEDIATE_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateMessageEventThrowing emptySubprocessEvent = getThrowingIntermediateNodeById(diagram,
                                                                                                EMPTY_SUBPROCESS_LEVEL_EVENT_ID,
                                                                                                HAS_NO_INCOME_EDGE);
        assertGeneralSet(emptySubprocessEvent.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertMessageEventExecutionSet(emptySubprocessEvent.getExecutionSet(), EMPTY_VALUE);
        assertDataIOSet(emptySubprocessEvent.getDataIOSet(), EMPTY_VALUE);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEventWithIncomeFilledProperties() throws Exception {
        final String EVENT_NAME = "message02 ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION = "message02 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String EVENT_REF = "message02";
        final String EVENT_DATA_INPUT = "message02:String||||[din]ProcessGlobalVar->message02";

        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_THROWING_INTERMEDIATE_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateMessageEventThrowing filledSubprocessEvent = getThrowingIntermediateNodeById(diagram,
                                                                                                 FILLED_WITH_INCOME_TOP_LEVEL_EVENT_ID,
                                                                                                 HAS_INCOME_EDGE);
        assertGeneralSet(filledSubprocessEvent.getGeneral(), EVENT_NAME, EVENT_DOCUMENTATION);
        assertMessageEventExecutionSet(filledSubprocessEvent.getExecutionSet(), EVENT_REF);
        assertDataIOSet(filledSubprocessEvent.getDataIOSet(), EVENT_DATA_INPUT);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEventWithIncomeEmptyProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_THROWING_INTERMEDIATE_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateMessageEventThrowing emptyEvent = getThrowingIntermediateNodeById(diagram,
                                                                                      EMPTY_WITH_INCOME_TOP_LEVEL_EVENT_ID,
                                                                                      HAS_INCOME_EDGE);
        assertGeneralSet(emptyEvent.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertMessageEventExecutionSet(emptyEvent.getExecutionSet(), EMPTY_VALUE);
        assertDataIOSet(emptyEvent.getDataIOSet(), EMPTY_VALUE);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventWithIncomeEmptyProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_THROWING_INTERMEDIATE_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateMessageEventThrowing emptySubprocessEvent = getThrowingIntermediateNodeById(diagram,
                                                                                                EMPTY_WITH_INCOME_SUBPROCESS_LEVEL_EVENT_ID,
                                                                                                HAS_INCOME_EDGE);
        assertGeneralSet(emptySubprocessEvent.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertMessageEventExecutionSet(emptySubprocessEvent.getExecutionSet(), EMPTY_VALUE);
        assertDataIOSet(emptySubprocessEvent.getDataIOSet(), EMPTY_VALUE);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventWithIncomeFilledProperties() throws Exception {
        final String EVENT_NAME = "message04 ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION = "message04 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String EVENT_REF = "message04";
        final String EVENT_DATA_INPUT = "message04:String||||[din]ProcessGlobalVar->message04";

        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_THROWING_INTERMEDIATE_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateMessageEventThrowing filledSubprocessEvent = getThrowingIntermediateNodeById(diagram,
                                                                                                 FILLED_WITH_INCOME_SUBPROCESS_LEVEL_EVENT_ID,
                                                                                                 HAS_INCOME_EDGE);
        assertGeneralSet(filledSubprocessEvent.getGeneral(), EVENT_NAME, EVENT_DOCUMENTATION);
        assertMessageEventExecutionSet(filledSubprocessEvent.getExecutionSet(), EVENT_REF);
        assertDataIOSet(filledSubprocessEvent.getDataIOSet(), EVENT_DATA_INPUT);
    }

    @Override
    String getBpmnThrowingIntermediateEventFilePath() {
        return BPMN_THROWING_INTERMEDIATE_EVENT_FILE_PATH;
    }

    @Override
    Class<IntermediateMessageEventThrowing> getThrowingIntermediateEventType() {
        return IntermediateMessageEventThrowing.class;
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

    private void assertMessageEventExecutionSet(MessageEventExecutionSet executionSet, String messageReference) {
        assertNotNull(executionSet);
        assertNotNull(executionSet.getMessageRef());
        assertEquals(messageReference, executionSet.getMessageRef().getValue());
    }
}
