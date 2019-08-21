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
import org.kie.workbench.common.stunner.bpmn.definition.StartCompensationEvent;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;

import static org.junit.Assert.assertNotNull;

public class StartCompensationEventTest extends StartEventTest<StartCompensationEvent> {

    private static final String BPMN_START_EVENT_FILE_PATH = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/startCompensationEvents.bpmn";

    private static final String FILLED_TOP_LEVEL_EVENT_ID = "_444DDF3E-FFE6-457F-8A67-3ED71EDB9926";
    private static final String EMPTY_TOP_LEVEL_EVENT_ID = "_3F8B50F0-66F7-4992-B279-41E6A6E20A6B";
    private static final String FILLED_SUBPROCESS_LEVEL_EVENT_ID = "_B02E67E6-2248-41E7-A284-9EB84496CEDE";
    private static final String EMPTY_SUBPROCESS_LEVEL_EVENT_ID = "_5BFE7259-E518-4D2E-9FE9-7BD9680390EC";

    private static final int AMOUNT_OF_NODES_IN_DIAGRAM = 10;

    private static final String SLA_DUE_DATE = "P1y8m17dT23h19m38s";

    public StartCompensationEventTest() throws Exception {
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEventFilledProperties() throws Exception {
        final String EVENT_NAME = "Start compensation event01 name\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION = "Start compensation event01 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";

        Diagram<Graph, Metadata> diagram = getDiagram();
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        StartCompensationEvent filledTopLevelEvent = getStartNodeById(diagram, FILLED_TOP_LEVEL_EVENT_ID);
        assertGeneralSet(filledTopLevelEvent.getGeneral(), EVENT_NAME, EVENT_DOCUMENTATION);
        assertNotNull(filledTopLevelEvent.getExecutionSet());
        assertStartEventSlaDueDate(filledTopLevelEvent.getExecutionSet(), SLA_DUE_DATE);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEmptyEventProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = getDiagram();
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        StartCompensationEvent emptyTopLevelEvent = getStartNodeById(diagram, EMPTY_TOP_LEVEL_EVENT_ID);
        assertGeneralSet(emptyTopLevelEvent.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertNotNull(emptyTopLevelEvent.getExecutionSet());
        assertStartEventSlaDueDate(emptyTopLevelEvent.getExecutionSet(), EMPTY_VALUE);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventFilledProperties() throws Exception {
        final String EVENT_NAME = "Start compensation event02 name\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION = "Start compensation event02 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";

        Diagram<Graph, Metadata> diagram = getDiagram();
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        StartCompensationEvent filledSubprocessLevelEvent = getStartNodeById(diagram, FILLED_SUBPROCESS_LEVEL_EVENT_ID);
        assertGeneralSet(filledSubprocessLevelEvent.getGeneral(), EVENT_NAME, EVENT_DOCUMENTATION);
        assertNotNull(filledSubprocessLevelEvent.getExecutionSet());
        assertStartEventSlaDueDate(filledSubprocessLevelEvent.getExecutionSet(), SLA_DUE_DATE);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventEmptyProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = getDiagram();
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        StartCompensationEvent emptySubprocessLevelEvent = getStartNodeById(diagram, EMPTY_SUBPROCESS_LEVEL_EVENT_ID);
        assertGeneralSet(emptySubprocessLevelEvent.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);

        assertNotNull(emptySubprocessLevelEvent.getExecutionSet());
        assertStartEventSlaDueDate(emptySubprocessLevelEvent.getExecutionSet(), EMPTY_VALUE);
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

    @Override
    Class<StartCompensationEvent> getStartEventType() {
        return StartCompensationEvent.class;
    }
}
