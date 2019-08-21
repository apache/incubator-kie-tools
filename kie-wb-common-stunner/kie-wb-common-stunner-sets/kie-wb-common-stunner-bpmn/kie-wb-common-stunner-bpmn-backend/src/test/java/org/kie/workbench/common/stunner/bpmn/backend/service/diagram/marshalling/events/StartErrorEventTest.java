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

package org.kie.workbench.common.stunner.bpmn.backend.service.diagram.marshalling.events;

import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.definition.StartErrorEvent;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.error.InterruptingErrorEventExecutionSet;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class StartErrorEventTest extends StartEventTest<StartErrorEvent> {

    private static final String BPMN_START_EVENT_FILE_PATH = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/startErrorEvents.bpmn";

    private static final String FILLED_TOP_LEVEL_EVENT_ID = "470CB3B0-B2E6-4252-B41E-353AED109847";
    private static final String EMPTY_TOP_LEVEL_EVENT_ID = "A180871D-2E3A-4CD3-AED7-43E8397FF30C";
    private static final String FILLED_SUBPROCESS_LEVEL_EVENT_ID = "25676AF3-FD4D-4A07-BA58-4D0E331D0579";
    private static final String EMPTY_SUBPROCESS_LEVEL_EVENT_ID = "1BB182E3-B7B9-45DB-8579-66A2F1B4DC53";

    private static final String SLA_DUE_DATE = "12/25/1983";

    private static final int AMOUNT_OF_NODES_IN_DIAGRAM = 11;

    public StartErrorEventTest() throws Exception {
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEventFilledProperties() throws Exception {
        final String EVENT_NAME = "Filled Top-Level Error start event";
        final String EVENT_DOCUMENTATION = "Some documentation\n~`!@#$%^&*()_+=-{}|\\][:\";'?><,./\n";
        final String ERROR_REF = "Error1";
        final String EVENT_DATA_OUTPUT = "||someVar:String||[dout]someVar->prVar";

        Diagram<Graph, Metadata> diagram = getDiagram();
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        StartErrorEvent filledTop = getStartNodeById(diagram, FILLED_TOP_LEVEL_EVENT_ID);
        assertGeneralSet(filledTop.getGeneral(), EVENT_NAME, EVENT_DOCUMENTATION);
        assertErrorEventExecutionSet(filledTop.getExecutionSet(), ERROR_REF, INTERRUPTING, SLA_DUE_DATE);
        assertDataIOSet(filledTop.getDataIOSet(), EVENT_DATA_OUTPUT);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEmptyEventProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = getDiagram();
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        StartErrorEvent emptyTop = getStartNodeById(diagram, EMPTY_TOP_LEVEL_EVENT_ID);
        assertGeneralSet(emptyTop.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertErrorEventExecutionSet(emptyTop.getExecutionSet(), EMPTY_VALUE, NON_INTERRUPTING, EMPTY_VALUE);
        assertDataIOSet(emptyTop.getDataIOSet(), EMPTY_VALUE);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventFilledProperties() throws Exception {
        final String EVENT_NAME = "Event subprocess filled error event";
        final String EVENT_DOCUMENTATION = "Some documentation as well\n~`!@#$%^&*()_+=-{}|\\][:\";'?><,./";
        final String EVENT_REF = "Error2";
        final String EVENT_DATA_OUTPUT = "||newVar:String||[dout]newVar->prVar";

        Diagram<Graph, Metadata> diagram = getDiagram();
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        StartErrorEvent filledSubprocess = getStartNodeById(diagram, FILLED_SUBPROCESS_LEVEL_EVENT_ID);
        assertGeneralSet(filledSubprocess.getGeneral(), EVENT_NAME, EVENT_DOCUMENTATION);
        assertErrorEventExecutionSet(filledSubprocess.getExecutionSet(), EVENT_REF, INTERRUPTING, SLA_DUE_DATE);
        assertDataIOSet(filledSubprocess.getDataIOSet(), EVENT_DATA_OUTPUT);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventEmptyProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = getDiagram();
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        StartErrorEvent emptySubprocess = getStartNodeById(diagram, EMPTY_SUBPROCESS_LEVEL_EVENT_ID);
        assertGeneralSet(emptySubprocess.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertErrorEventExecutionSet(emptySubprocess.getExecutionSet(), EMPTY_VALUE, NON_INTERRUPTING, EMPTY_VALUE);
        assertDataIOSet(emptySubprocess.getDataIOSet(), EMPTY_VALUE);
    }

    @Override
    public Class<StartErrorEvent> getStartEventType() {
        return StartErrorEvent.class;
    }

    @Override
    String getBpmnStartEventFilePath() {
        return BPMN_START_EVENT_FILE_PATH;
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

    private void assertErrorEventExecutionSet(InterruptingErrorEventExecutionSet executionSet, String eventName, boolean isInterrupting, String slaDueDate) {
        assertNotNull(executionSet);
        assertNotNull(executionSet.getErrorRef());
        assertEquals(eventName, executionSet.getErrorRef().getValue());

        assertStartEventIsInterrupting(executionSet, isInterrupting);
        assertStartEventSlaDueDate(executionSet, slaDueDate);
    }
}
