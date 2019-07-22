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
import org.kie.workbench.common.stunner.bpmn.definition.EndSignalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.signal.ScopedSignalEventExecutionSet;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class EndSignalEventTest extends EndEventTest<EndSignalEvent> {

    private static final String BPMN_END_EVENT_FILE_PATH = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/endSignalEvents.bpmn";

    private static final String EMPTY_TOP_LEVEL_EVENT_ID = "_CC780D57-DE0C-4D87-B574-B853E1080322";
    private static final String FILLED_TOP_LEVEL_EVENT_PROCESS_INSTANCE_SCOPE_ID = "_6EC8E2EC-4E6F-4CED-AF70-86DB72F6B62E";
    private static final String FILLED_TOP_LEVEL_EVENT_PROJECT_SCOPE_ID = "_0649DF6B-2567-4203-9B9F-51F4CE4A2175";
    private static final String FILLED_TOP_LEVEL_EVENT_EXTERNAL_SCOPE_ID = "_CC495DF0-6CA7-4F21-ACF5-DDC28F30A302";

    private static final String EMPTY_SUBPROCESS_LEVEL_EVENT_ID = "_81EA04C5-0F79-4293-B78F-B93598A4152C";
    private static final String FILLED_SUBPROCESS_LEVEL_PROCESS_INSTANCE_SCOPE_ID = "_169E8E1F-A3C4-4B8D-9BEC-F43435BFC882";
    private static final String FILLED_SUBPROCESS_LEVEL_PROJECT_SCOPE_ID = "_B0A8166F-3893-4CAB-A232-8E96C9B28204";
    private static final String FILLED_SUBPROCESS_LEVEL_EVENT_EXTERNAL_SCOPE_ID = "_D9D2B905-247D-454B-93F6-9B9D0E88767A";

    private static final String EMPTY_WITH_INCOME_TOP_LEVEL_EVENT_ID = "_ECBC3872-2949-4715-9649-0918057B3B9D";
    private static final String FILLED_WITH_INCOME_TOP_LEVEL_EVENT_PROCESS_INSTANCE_SCOPE_ID = "_94469674-2F60-451C-AFBB-8701E53DA085";
    private static final String FILLED_WITH_INCOME_TOP_LEVEL_PROJECT_SCOPE_ID = "_6E805887-A67D-4CA8-A96F-1038A8154182";
    private static final String FILLED_WITH_INCOME_TOP_LEVEL_EVENT_EXTERNAL_SCOPE_ID = "_3D27EE98-B57D-453F-BC33-A2D1E15B3ACB";

    private static final String EMPTY_WITH_INCOME_SUBPROCESS_LEVEL_EVENT_ID = "_A7D0EDD8-34CA-4537-82A4-F156DDD6C30C";
    private static final String FILLED_WITH_INCOME_SUBPROCESS_LEVEL_PROCESS_INSTANCE_SCOPE_ID = "_2D60DFA5-595A-4125-9249-7474FBCBB582";
    private static final String FILLED_WITH_INCOME_SUBPROCESS_LEVEL_PROJECT_SCOPE_ID = "_553653BE-F237-4D43-8601-351E7B062956";
    private static final String FILLED_WITH_INCOME_SUBPROCESS_LEVEL_EVENT_EXTERNAL_SCOPE_ID = "_EF073D03-2A7E-488C-9A1A-B4F30AD88FA2";

    private static final int AMOUNT_OF_NODES_IN_DIAGRAM = 26;

    private static final String EVENT_SIGNAL_SCOPE_DEFAULT = "";
    private static final String EVENT_SIGNAL_SCOPE_PROCESS_INSTANCE = "processInstance";
    private static final String EVENT_SIGNAL_SCOPE_PROJECT = "project";
    private static final String EVENT_SIGNAL_SCOPE_EXTERNAL = "external";

    @Test
    @Override
    public void testUnmarshallTopLevelEventFilledProperties() throws Exception {
        final String EVENT_NAME_PROCESS_INSTANCE_SCOPE = "signal name01 ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION_PROCESS_INSTANCE_SCOPE = "signal01 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String EVENT_REF_PROCESS_INSTANCE_SCOPE = "signal01";
        final String EVENT_DATA_INPUT_PROCESS_INSTANCE_SCOPE = "signal01:String||||[din]processGlobalVar->signal01";

        final String EVENT_NAME_PROJECT_SCOPE = "signal name02 ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION_PROJECT_SCOPE = "signal02 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String EVENT_REF_PROJECT_SCOPE = "signal02";
        final String EVENT_DATA_INPUT_PROJECT_SCOPE = "signal02:String||||[din]processGlobalVar->signal02";

        final String EVENT_NAME_EXTERNAL_SCOPE = "signal name03 ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION_EXTERNAL_SCOPE = "signal03 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String EVENT_REF_EXTERNAL_SCOPE = "signal03";
        final String EVENT_DATA_INPUT_EXTERNAL_SCOPE = "signal03:String||||[din]processGlobalVar->signal03";

        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_END_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        EndSignalEvent filledTopEventProcessInstance = getEndNodeById(diagram,
                                                                      FILLED_TOP_LEVEL_EVENT_PROCESS_INSTANCE_SCOPE_ID,
                                                                      HAS_NO_INCOME_EDGE);
        assertGeneralSet(filledTopEventProcessInstance.getGeneral(), EVENT_NAME_PROCESS_INSTANCE_SCOPE, EVENT_DOCUMENTATION_PROCESS_INSTANCE_SCOPE);
        assertSignalEventExecutionSet(filledTopEventProcessInstance.getExecutionSet(), EVENT_REF_PROCESS_INSTANCE_SCOPE, EVENT_SIGNAL_SCOPE_PROCESS_INSTANCE);
        assertDataIOSet(filledTopEventProcessInstance.getDataIOSet(), EVENT_DATA_INPUT_PROCESS_INSTANCE_SCOPE);

        EndSignalEvent filledTopEventProject = getEndNodeById(diagram,
                                                              FILLED_TOP_LEVEL_EVENT_PROJECT_SCOPE_ID,
                                                              HAS_NO_INCOME_EDGE);
        assertGeneralSet(filledTopEventProject.getGeneral(), EVENT_NAME_PROJECT_SCOPE, EVENT_DOCUMENTATION_PROJECT_SCOPE);
        assertSignalEventExecutionSet(filledTopEventProject.getExecutionSet(), EVENT_REF_PROJECT_SCOPE, EVENT_SIGNAL_SCOPE_PROJECT);
        assertDataIOSet(filledTopEventProject.getDataIOSet(), EVENT_DATA_INPUT_PROJECT_SCOPE);

        EndSignalEvent filledTopEventExternal = getEndNodeById(diagram,
                                                               FILLED_TOP_LEVEL_EVENT_EXTERNAL_SCOPE_ID,
                                                               HAS_NO_INCOME_EDGE);
        assertGeneralSet(filledTopEventExternal.getGeneral(), EVENT_NAME_EXTERNAL_SCOPE, EVENT_DOCUMENTATION_EXTERNAL_SCOPE);
        assertSignalEventExecutionSet(filledTopEventExternal.getExecutionSet(), EVENT_REF_EXTERNAL_SCOPE, EVENT_SIGNAL_SCOPE_EXTERNAL);
        assertDataIOSet(filledTopEventExternal.getDataIOSet(), EVENT_DATA_INPUT_EXTERNAL_SCOPE);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEmptyEventProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_END_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        EndSignalEvent emptyTopEvent = getEndNodeById(diagram,
                                                      EMPTY_TOP_LEVEL_EVENT_ID,
                                                      HAS_NO_INCOME_EDGE);
        assertGeneralSet(emptyTopEvent.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertSignalEventExecutionSet(emptyTopEvent.getExecutionSet(), EMPTY_VALUE, EVENT_SIGNAL_SCOPE_DEFAULT);
        assertDataIOSet(emptyTopEvent.getDataIOSet(), EMPTY_VALUE);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventFilledProperties() throws Exception {
        final String EVENT_NAME_PROCESS_INSTANCE_SCOPE = "signal name07 ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION_PROCESS_INSTANCE_SCOPE = "signal07 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String EVENT_REF_PROCESS_INSTANCE_SCOPE = "signal07";
        final String EVENT_DATA_INPUT_PROCESS_INSTANCE_SCOPE = "signal07:String||||[din]processGlobalVar->signal07";

        final String EVENT_NAME_PROJECT_SCOPE = "signal name08 ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION_PROJECT_SCOPE = "signal08 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String EVENT_REF_PROJECT_SCOPE = "signal08";
        final String EVENT_DATA_INPUT_PROJECT_SCOPE = "signal08:String||||[din]processGlobalVar->signal08";

        final String EVENT_NAME_EXTERNAL_SCOPE = "signal name09 ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION_EXTERNAL_SCOPE = "signal09 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String EVENT_REF_EXTERNAL_SCOPE = "signal09";
        final String EVENT_DATA_INPUT_EXTERNAL_SCOPE = "signal09:String||||[din]processGlobalVar->signal09";

        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_END_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        EndSignalEvent filledSubprocessEventProcessInstance = getEndNodeById(diagram,
                                                                             FILLED_SUBPROCESS_LEVEL_PROCESS_INSTANCE_SCOPE_ID,
                                                                             HAS_NO_INCOME_EDGE);
        assertGeneralSet(filledSubprocessEventProcessInstance.getGeneral(), EVENT_NAME_PROCESS_INSTANCE_SCOPE, EVENT_DOCUMENTATION_PROCESS_INSTANCE_SCOPE);
        assertSignalEventExecutionSet(filledSubprocessEventProcessInstance.getExecutionSet(), EVENT_REF_PROCESS_INSTANCE_SCOPE, EVENT_SIGNAL_SCOPE_PROCESS_INSTANCE);
        assertDataIOSet(filledSubprocessEventProcessInstance.getDataIOSet(), EVENT_DATA_INPUT_PROCESS_INSTANCE_SCOPE);

        EndSignalEvent filledSubprocessEventProject = getEndNodeById(diagram,
                                                                     FILLED_SUBPROCESS_LEVEL_PROJECT_SCOPE_ID,
                                                                     HAS_NO_INCOME_EDGE);
        assertGeneralSet(filledSubprocessEventProject.getGeneral(), EVENT_NAME_PROJECT_SCOPE, EVENT_DOCUMENTATION_PROJECT_SCOPE);
        assertSignalEventExecutionSet(filledSubprocessEventProject.getExecutionSet(), EVENT_REF_PROJECT_SCOPE, EVENT_SIGNAL_SCOPE_PROJECT);
        assertDataIOSet(filledSubprocessEventProject.getDataIOSet(), EVENT_DATA_INPUT_PROJECT_SCOPE);

        EndSignalEvent filledSubprocessEventExternal = getEndNodeById(diagram,
                                                                      FILLED_SUBPROCESS_LEVEL_EVENT_EXTERNAL_SCOPE_ID,
                                                                      HAS_NO_INCOME_EDGE);
        assertGeneralSet(filledSubprocessEventExternal.getGeneral(), EVENT_NAME_EXTERNAL_SCOPE, EVENT_DOCUMENTATION_EXTERNAL_SCOPE);
        assertSignalEventExecutionSet(filledSubprocessEventExternal.getExecutionSet(), EVENT_REF_EXTERNAL_SCOPE, EVENT_SIGNAL_SCOPE_EXTERNAL);
        assertDataIOSet(filledSubprocessEventExternal.getDataIOSet(), EVENT_DATA_INPUT_EXTERNAL_SCOPE);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventEmptyProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_END_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        EndSignalEvent emptySubprocessEvent = getEndNodeById(diagram,
                                                             EMPTY_SUBPROCESS_LEVEL_EVENT_ID,
                                                             HAS_NO_INCOME_EDGE);
        assertGeneralSet(emptySubprocessEvent.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertSignalEventExecutionSet(emptySubprocessEvent.getExecutionSet(), EMPTY_VALUE, EVENT_SIGNAL_SCOPE_DEFAULT);
        assertDataIOSet(emptySubprocessEvent.getDataIOSet(), EMPTY_VALUE);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEventWithIncomeFilledProperties() throws Exception {
        final String EVENT_NAME_PROCESS_INSTANCE_SCOPE = "signal name04 ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION_PROCESS_INSTANCE_SCOPE = "signal04 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String EVENT_REF_PROCESS_INSTANCE_SCOPE = "signal04";
        final String EVENT_DATA_INPUT_PROCESS_INSTANCE_SCOPE = "signal04:String||||[din]processGlobalVar->signal04";

        final String EVENT_NAME_PROJECT_SCOPE = "signal name05 ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION_PROJECT_SCOPE = "signal05 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String EVENT_REF_PROJECT_SCOPE = "signal05";
        final String EVENT_DATA_INPUT_PROJECT_SCOPE = "signal05:String||||[din]processGlobalVar->signal05";

        final String EVENT_NAME_EXTERNAL_SCOPE = "signal name06 ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION_EXTERNAL_SCOPE = "signal06 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String EVENT_REF_EXTERNAL_SCOPE = "signal06";
        final String EVENT_DATA_INPUT_EXTERNAL_SCOPE = "signal06:String||||[din]processGlobalVar->signal06";

        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_END_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        EndSignalEvent filledTopEventProcessInstance = getEndNodeById(diagram,
                                                                      FILLED_WITH_INCOME_TOP_LEVEL_EVENT_PROCESS_INSTANCE_SCOPE_ID,
                                                                      HAS_INCOME_EDGE);
        assertGeneralSet(filledTopEventProcessInstance.getGeneral(), EVENT_NAME_PROCESS_INSTANCE_SCOPE, EVENT_DOCUMENTATION_PROCESS_INSTANCE_SCOPE);
        assertSignalEventExecutionSet(filledTopEventProcessInstance.getExecutionSet(), EVENT_REF_PROCESS_INSTANCE_SCOPE, EVENT_SIGNAL_SCOPE_PROCESS_INSTANCE);
        assertDataIOSet(filledTopEventProcessInstance.getDataIOSet(), EVENT_DATA_INPUT_PROCESS_INSTANCE_SCOPE);

        EndSignalEvent filledTopEventProject = getEndNodeById(diagram,
                                                              FILLED_WITH_INCOME_TOP_LEVEL_PROJECT_SCOPE_ID,
                                                              HAS_INCOME_EDGE);
        assertGeneralSet(filledTopEventProject.getGeneral(), EVENT_NAME_PROJECT_SCOPE, EVENT_DOCUMENTATION_PROJECT_SCOPE);
        assertSignalEventExecutionSet(filledTopEventProject.getExecutionSet(), EVENT_REF_PROJECT_SCOPE, EVENT_SIGNAL_SCOPE_PROJECT);
        assertDataIOSet(filledTopEventProject.getDataIOSet(), EVENT_DATA_INPUT_PROJECT_SCOPE);

        EndSignalEvent filledTopEventExternal = getEndNodeById(diagram,
                                                               FILLED_WITH_INCOME_TOP_LEVEL_EVENT_EXTERNAL_SCOPE_ID,
                                                               HAS_INCOME_EDGE);
        assertGeneralSet(filledTopEventExternal.getGeneral(), EVENT_NAME_EXTERNAL_SCOPE, EVENT_DOCUMENTATION_EXTERNAL_SCOPE);
        assertSignalEventExecutionSet(filledTopEventExternal.getExecutionSet(), EVENT_REF_EXTERNAL_SCOPE, EVENT_SIGNAL_SCOPE_EXTERNAL);
        assertDataIOSet(filledTopEventExternal.getDataIOSet(), EVENT_DATA_INPUT_EXTERNAL_SCOPE);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEventWithIncomeEmptyProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_END_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        EndSignalEvent emptyEvent = getEndNodeById(diagram,
                                                   EMPTY_WITH_INCOME_TOP_LEVEL_EVENT_ID,
                                                   HAS_INCOME_EDGE);
        assertGeneralSet(emptyEvent.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertSignalEventExecutionSet(emptyEvent.getExecutionSet(), EMPTY_VALUE, EVENT_SIGNAL_SCOPE_DEFAULT);
        assertDataIOSet(emptyEvent.getDataIOSet(), EMPTY_VALUE);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventWithIncomeEmptyProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_END_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        EndSignalEvent emptySubprocessEvent = getEndNodeById(diagram,
                                                             EMPTY_WITH_INCOME_SUBPROCESS_LEVEL_EVENT_ID,
                                                             HAS_INCOME_EDGE);
        assertGeneralSet(emptySubprocessEvent.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertSignalEventExecutionSet(emptySubprocessEvent.getExecutionSet(), EMPTY_VALUE, EVENT_SIGNAL_SCOPE_DEFAULT);
        assertDataIOSet(emptySubprocessEvent.getDataIOSet(), EMPTY_VALUE);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventWithIncomeFilledProperties() throws Exception {
        final String EVENT_NAME_PROCESS_INSTANCE_SCOPE = "signal name10 ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION_PROCESS_INSTANCE_SCOPE = "signal10 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String EVENT_REF_PROCESS_INSTANCE_SCOPE = "signal10";
        final String EVENT_DATA_INPUT_PROCESS_INSTANCE_SCOPE = "signal10:String||||[din]processGlobalVar->signal10";

        final String EVENT_NAME_PROJECT_SCOPE = "signal name11 ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION_PROJECT_SCOPE = "signal11 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String EVENT_REF_PROJECT_SCOPE = "signal11";
        final String EVENT_DATA_INPUT_PROJECT_SCOPE = "signal11:String||||[din]processGlobalVar->signal11";

        final String EVENT_NAME_EXTERNAL_SCOPE = "signal name12 ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION_EXTERNAL_SCOPE = "signal12 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String EVENT_REF_EXTERNAL_SCOPE = "signal12";
        final String EVENT_DATA_INPUT_EXTERNAL_SCOPE = "signal12:String||||[din]processGlobalVar->signal12";

        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_END_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        EndSignalEvent filledSubprocessEventProcessInstance = getEndNodeById(diagram,
                                                                             FILLED_WITH_INCOME_SUBPROCESS_LEVEL_PROCESS_INSTANCE_SCOPE_ID,
                                                                             HAS_INCOME_EDGE);
        assertGeneralSet(filledSubprocessEventProcessInstance.getGeneral(), EVENT_NAME_PROCESS_INSTANCE_SCOPE, EVENT_DOCUMENTATION_PROCESS_INSTANCE_SCOPE);
        assertSignalEventExecutionSet(filledSubprocessEventProcessInstance.getExecutionSet(), EVENT_REF_PROCESS_INSTANCE_SCOPE, EVENT_SIGNAL_SCOPE_PROCESS_INSTANCE);
        assertDataIOSet(filledSubprocessEventProcessInstance.getDataIOSet(), EVENT_DATA_INPUT_PROCESS_INSTANCE_SCOPE);

        EndSignalEvent filledSubprocessEventProject = getEndNodeById(diagram,
                                                                     FILLED_WITH_INCOME_SUBPROCESS_LEVEL_PROJECT_SCOPE_ID,
                                                                     HAS_INCOME_EDGE);
        assertGeneralSet(filledSubprocessEventProject.getGeneral(), EVENT_NAME_PROJECT_SCOPE, EVENT_DOCUMENTATION_PROJECT_SCOPE);
        assertSignalEventExecutionSet(filledSubprocessEventProject.getExecutionSet(), EVENT_REF_PROJECT_SCOPE, EVENT_SIGNAL_SCOPE_PROJECT);
        assertDataIOSet(filledSubprocessEventProject.getDataIOSet(), EVENT_DATA_INPUT_PROJECT_SCOPE);

        EndSignalEvent filledSubprocessEventExternal = getEndNodeById(diagram,
                                                                      FILLED_WITH_INCOME_SUBPROCESS_LEVEL_EVENT_EXTERNAL_SCOPE_ID,
                                                                      HAS_INCOME_EDGE);
        assertGeneralSet(filledSubprocessEventExternal.getGeneral(), EVENT_NAME_EXTERNAL_SCOPE, EVENT_DOCUMENTATION_EXTERNAL_SCOPE);
        assertSignalEventExecutionSet(filledSubprocessEventExternal.getExecutionSet(), EVENT_REF_EXTERNAL_SCOPE, EVENT_SIGNAL_SCOPE_EXTERNAL);
        assertDataIOSet(filledSubprocessEventExternal.getDataIOSet(), EVENT_DATA_INPUT_EXTERNAL_SCOPE);
    }

    @Test
    @Override
    public void testMarshallTopLevelEventFilledProperties() throws Exception {
        checkEventMarshalling(FILLED_TOP_LEVEL_EVENT_PROCESS_INSTANCE_SCOPE_ID, HAS_NO_INCOME_EDGE);
        checkEventMarshalling(FILLED_TOP_LEVEL_EVENT_PROJECT_SCOPE_ID, HAS_NO_INCOME_EDGE);
        checkEventMarshalling(FILLED_TOP_LEVEL_EVENT_EXTERNAL_SCOPE_ID, HAS_NO_INCOME_EDGE);
    }

    @Test
    @Override
    public void testMarshallSubprocessLevelEventFilledProperties() throws Exception {
        checkEventMarshalling(FILLED_SUBPROCESS_LEVEL_PROCESS_INSTANCE_SCOPE_ID, HAS_NO_INCOME_EDGE);
        checkEventMarshalling(FILLED_SUBPROCESS_LEVEL_PROJECT_SCOPE_ID, HAS_NO_INCOME_EDGE);
        checkEventMarshalling(FILLED_SUBPROCESS_LEVEL_EVENT_EXTERNAL_SCOPE_ID, HAS_NO_INCOME_EDGE);
    }

    @Test
    @Override
    public void testMarshallTopLevelEventWithIncomeFilledProperties() throws Exception {
        checkEventMarshalling(FILLED_WITH_INCOME_TOP_LEVEL_EVENT_PROCESS_INSTANCE_SCOPE_ID, HAS_INCOME_EDGE);
        checkEventMarshalling(FILLED_WITH_INCOME_TOP_LEVEL_PROJECT_SCOPE_ID, HAS_INCOME_EDGE);
        checkEventMarshalling(FILLED_WITH_INCOME_TOP_LEVEL_EVENT_EXTERNAL_SCOPE_ID, HAS_INCOME_EDGE);
    }

    @Test
    @Override
    public void testMarshallSubprocessLevelEventWithIncomeFilledProperties() throws Exception {
        checkEventMarshalling(FILLED_WITH_INCOME_SUBPROCESS_LEVEL_PROCESS_INSTANCE_SCOPE_ID, HAS_INCOME_EDGE);
        checkEventMarshalling(FILLED_WITH_INCOME_SUBPROCESS_LEVEL_PROJECT_SCOPE_ID, HAS_INCOME_EDGE);
        checkEventMarshalling(FILLED_WITH_INCOME_SUBPROCESS_LEVEL_EVENT_EXTERNAL_SCOPE_ID, HAS_INCOME_EDGE);
    }

    @Override
    String getBpmnEndEventFilePath() {
        return BPMN_END_EVENT_FILE_PATH;
    }

    @Override
    Class<EndSignalEvent> getEndEventType() {
        return EndSignalEvent.class;
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
    String getFilledTopLevelEventWithIncomeId() {
        // There are several Filled events, test method is overwritten
        return null;
    }

    @Override
    String getEmptyTopLevelEventWithIncomeId() {
        return EMPTY_WITH_INCOME_TOP_LEVEL_EVENT_ID;
    }

    @Override
    String getFilledSubprocessLevelEventWithIncomeId() {
        // There are several Filled events, test method is overwritten
        return null;
    }

    @Override
    String getEmptySubprocessLevelEventWithIncomeId() {
        return EMPTY_WITH_INCOME_SUBPROCESS_LEVEL_EVENT_ID;
    }

    private void assertSignalEventExecutionSet(ScopedSignalEventExecutionSet executionSet, String signalReference, String signalScope) {
        assertNotNull(executionSet);
        assertNotNull(executionSet.getSignalRef());
        assertNotNull(executionSet.getSignalScope());
        assertEquals(signalReference, executionSet.getSignalRef().getValue());
        assertEquals(signalScope, executionSet.getSignalScope().getValue());
    }
}
