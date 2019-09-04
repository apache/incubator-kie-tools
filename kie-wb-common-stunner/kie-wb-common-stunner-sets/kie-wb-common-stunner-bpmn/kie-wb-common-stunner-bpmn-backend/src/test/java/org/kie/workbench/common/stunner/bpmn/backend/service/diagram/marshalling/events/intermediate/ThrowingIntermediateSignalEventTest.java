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
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateSignalEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.signal.ScopedSignalEventExecutionSet;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;

import static org.assertj.core.api.Assertions.assertThat;

public class ThrowingIntermediateSignalEventTest extends ThrowingIntermediateEventTest<IntermediateSignalEventThrowing> {

    private static final String BPMN_THROWING_INTERMEDIATE_EVENT_FILE_PATH = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/throwingIntermediateSignalEvents.bpmn";

    private static final String EMPTY_TOP_LEVEL_EVENT_ID = "44E0C631-6300-42D8-A6A6-186A6F8D393B";
    private static final String FILLED_TOP_LEVEL_EVENT_PROCESS_INSTANCE_SCOPE_ID = "713EE943-5A9C-4FA4-81C1-4F3676695DCC";
    private static final String FILLED_TOP_LEVEL_EVENT_PROJECT_SCOPE_ID = "2FABA224-9652-4410-9346-00669015F7C6";
    private static final String FILLED_TOP_LEVEL_EVENT_EXTERNAL_SCOPE_ID = "3A0E67C8-64E1-46ED-9173-68DC8BB1FCCA";

    private static final String EMPTY_SUBPROCESS_LEVEL_EVENT_ID = "4FF06CD9-C0E4-4737-AE20-62A3EAA716DA";
    private static final String FILLED_SUBPROCESS_LEVEL_PROCESS_INSTANCE_SCOPE_ID = "F7123277-CA50-4695-BA89-46CE0B23AEA1";
    private static final String FILLED_SUBPROCESS_LEVEL_PROJECT_SCOPE_ID = "1CFE1286-A741-4199-9589-3C5E818223AC";
    private static final String FILLED_SUBPROCESS_LEVEL_EVENT_EXTERNAL_SCOPE_ID = "35E9A791-ED38-4DDD-9853-6214E528A2E7";

    private static final String EMPTY_WITH_EDGES_TOP_LEVEL_EVENT_ID = "4ABCB559-F522-47E7-84A8-9D9F9189CDCA";
    private static final String FILLED_WITH_EDGES_TOP_LEVEL_EVENT_PROCESS_INSTANCE_SCOPE_ID = "0F011259-FA4F-458A-A9AB-A6C5C18257CE";
    private static final String FILLED_WITH_EDGES_TOP_LEVEL_PROJECT_SCOPE_ID = "CCDF61B4-2795-4DFD-95EC-667D56E110DB";
    private static final String FILLED_WITH_EDGES_TOP_LEVEL_EVENT_EXTERNAL_SCOPE_ID = "3E469F07-B684-49D7-B97A-60DD22F12E01";

    private static final String EMPTY_WITH_EDGES_SUBPROCESS_LEVEL_EVENT_ID = "E7566B4F-FA5A-447F-963D-6C41C5C5A871";
    private static final String FILLED_WITH_EDGES_SUBPROCESS_LEVEL_PROCESS_INSTANCE_SCOPE_ID = "EFD41F08-FED7-43E9-9393-C356D7EFF7BA";
    private static final String FILLED_WITH_EDGES_SUBPROCESS_LEVEL_PROJECT_SCOPE_ID = "11FD55C3-5370-4F15-8FC2-CCDC657C2D39";
    private static final String FILLED_WITH_EDGES_SUBPROCESS_LEVEL_EVENT_EXTERNAL_SCOPE_ID = "84FC0F13-BD60-4E18-848C-6DD63E1D93AD";

    private static final int AMOUNT_OF_NODES_IN_DIAGRAM = 43;

    private static final String EVENT_SIGNAL_SCOPE_DEFAULT = "";
    private static final String EVENT_SIGNAL_SCOPE_PROCESS_INSTANCE = "processInstance";
    private static final String EVENT_SIGNAL_SCOPE_PROJECT = "project";
    private static final String EVENT_SIGNAL_SCOPE_EXTERNAL = "external";

    public ThrowingIntermediateSignalEventTest() throws Exception {
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEventFilledProperties() throws Exception {
        final String EVENT_NAME_PROCESS_INSTANCE_SCOPE = "signal01 ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION_PROCESS_INSTANCE_SCOPE = "signal01 doc\n~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String EVENT_REF_PROCESS_INSTANCE_SCOPE = "signal01";
        final String EVENT_DATA_INPUT_PROCESS_INSTANCE_SCOPE = "signal01:String||||[din]GlobalVar->signal01";

        final String EVENT_NAME_PROJECT_SCOPE = "signal02 ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION_PROJECT_SCOPE = "signal02 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String EVENT_REF_PROJECT_SCOPE = "signal02";
        final String EVENT_DATA_INPUT_PROJECT_SCOPE = "signal02:String||||[din]GlobalVar->signal02";

        final String EVENT_NAME_EXTERNAL_SCOPE = "signal03 ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION_EXTERNAL_SCOPE = "signal03 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String EVENT_REF_EXTERNAL_SCOPE = "signal03";
        final String EVENT_DATA_INPUT_EXTERNAL_SCOPE = "signal03:String||||[din]GlobalVar->signal03";

        Diagram<Graph, Metadata> diagram = getDiagram();
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateSignalEventThrowing filledTopEventProcessInstance = getThrowingIntermediateNodeById(diagram,
                                                                                                        FILLED_TOP_LEVEL_EVENT_PROCESS_INSTANCE_SCOPE_ID,
                                                                                                        HAS_NO_INCOME_EDGE, ZERO_OUTGOING_EDGES);
        assertGeneralSet(filledTopEventProcessInstance.getGeneral(), EVENT_NAME_PROCESS_INSTANCE_SCOPE, EVENT_DOCUMENTATION_PROCESS_INSTANCE_SCOPE);
        assertSignalEventExecutionSet(filledTopEventProcessInstance.getExecutionSet(), EVENT_REF_PROCESS_INSTANCE_SCOPE, EVENT_SIGNAL_SCOPE_PROCESS_INSTANCE);
        assertDataIOSet(filledTopEventProcessInstance.getDataIOSet(), EVENT_DATA_INPUT_PROCESS_INSTANCE_SCOPE);

        IntermediateSignalEventThrowing filledTopEventProject = getThrowingIntermediateNodeById(diagram,
                                                                                                FILLED_TOP_LEVEL_EVENT_PROJECT_SCOPE_ID,
                                                                                                HAS_NO_INCOME_EDGE, ZERO_OUTGOING_EDGES);
        assertGeneralSet(filledTopEventProject.getGeneral(), EVENT_NAME_PROJECT_SCOPE, EVENT_DOCUMENTATION_PROJECT_SCOPE);
        assertSignalEventExecutionSet(filledTopEventProject.getExecutionSet(), EVENT_REF_PROJECT_SCOPE, EVENT_SIGNAL_SCOPE_PROJECT);
        assertDataIOSet(filledTopEventProject.getDataIOSet(), EVENT_DATA_INPUT_PROJECT_SCOPE);

        IntermediateSignalEventThrowing filledTopEventExternal = getThrowingIntermediateNodeById(diagram,
                                                                                                 FILLED_TOP_LEVEL_EVENT_EXTERNAL_SCOPE_ID,
                                                                                                 HAS_NO_INCOME_EDGE, ZERO_OUTGOING_EDGES);
        assertGeneralSet(filledTopEventExternal.getGeneral(), EVENT_NAME_EXTERNAL_SCOPE, EVENT_DOCUMENTATION_EXTERNAL_SCOPE);
        assertSignalEventExecutionSet(filledTopEventExternal.getExecutionSet(), EVENT_REF_EXTERNAL_SCOPE, EVENT_SIGNAL_SCOPE_EXTERNAL);
        assertDataIOSet(filledTopEventExternal.getDataIOSet(), EVENT_DATA_INPUT_EXTERNAL_SCOPE);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEmptyEventProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = getDiagram();
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateSignalEventThrowing emptyTopEvent = getThrowingIntermediateNodeById(diagram,
                                                                                        EMPTY_TOP_LEVEL_EVENT_ID,
                                                                                        HAS_NO_INCOME_EDGE, ZERO_OUTGOING_EDGES);
        assertGeneralSet(emptyTopEvent.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertSignalEventExecutionSet(emptyTopEvent.getExecutionSet(), EMPTY_VALUE, EVENT_SIGNAL_SCOPE_DEFAULT);
        assertDataIOSet(emptyTopEvent.getDataIOSet(), EMPTY_VALUE);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventFilledProperties() throws Exception {
        final String EVENT_NAME_PROCESS_INSTANCE_SCOPE = "signal07 ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION_PROCESS_INSTANCE_SCOPE = "signal07 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String EVENT_REF_PROCESS_INSTANCE_SCOPE = "signal07";
        final String EVENT_DATA_INPUT_PROCESS_INSTANCE_SCOPE = "signal07:String||||[din]GlobalVar->signal07";

        final String EVENT_NAME_PROJECT_SCOPE = "signal08 ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION_PROJECT_SCOPE = "signal08 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String EVENT_REF_PROJECT_SCOPE = "signal08";
        final String EVENT_DATA_INPUT_PROJECT_SCOPE = "signal08:String||||[din]GlobalVar->signal08";

        final String EVENT_NAME_EXTERNAL_SCOPE = "signal09 ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION_EXTERNAL_SCOPE = "signal09 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String EVENT_REF_EXTERNAL_SCOPE = "signal09";
        final String EVENT_DATA_INPUT_EXTERNAL_SCOPE = "signal09:String||||[din]GlobalVar->signal09";

        Diagram<Graph, Metadata> diagram = getDiagram();
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateSignalEventThrowing filledSubprocessEventProcessInstance = getThrowingIntermediateNodeById(diagram,
                                                                                                               FILLED_SUBPROCESS_LEVEL_PROCESS_INSTANCE_SCOPE_ID,
                                                                                                               HAS_NO_INCOME_EDGE, ZERO_OUTGOING_EDGES);
        assertGeneralSet(filledSubprocessEventProcessInstance.getGeneral(), EVENT_NAME_PROCESS_INSTANCE_SCOPE, EVENT_DOCUMENTATION_PROCESS_INSTANCE_SCOPE);
        assertSignalEventExecutionSet(filledSubprocessEventProcessInstance.getExecutionSet(), EVENT_REF_PROCESS_INSTANCE_SCOPE, EVENT_SIGNAL_SCOPE_PROCESS_INSTANCE);
        assertDataIOSet(filledSubprocessEventProcessInstance.getDataIOSet(), EVENT_DATA_INPUT_PROCESS_INSTANCE_SCOPE);

        IntermediateSignalEventThrowing filledSubprocessEventProject = getThrowingIntermediateNodeById(diagram,
                                                                                                       FILLED_SUBPROCESS_LEVEL_PROJECT_SCOPE_ID,
                                                                                                       HAS_NO_INCOME_EDGE, ZERO_OUTGOING_EDGES);
        assertGeneralSet(filledSubprocessEventProject.getGeneral(), EVENT_NAME_PROJECT_SCOPE, EVENT_DOCUMENTATION_PROJECT_SCOPE);
        assertSignalEventExecutionSet(filledSubprocessEventProject.getExecutionSet(), EVENT_REF_PROJECT_SCOPE, EVENT_SIGNAL_SCOPE_PROJECT);
        assertDataIOSet(filledSubprocessEventProject.getDataIOSet(), EVENT_DATA_INPUT_PROJECT_SCOPE);

        IntermediateSignalEventThrowing filledSubprocessEventExternal = getThrowingIntermediateNodeById(diagram,
                                                                                                        FILLED_SUBPROCESS_LEVEL_EVENT_EXTERNAL_SCOPE_ID,
                                                                                                        HAS_NO_INCOME_EDGE, ZERO_OUTGOING_EDGES);
        assertGeneralSet(filledSubprocessEventExternal.getGeneral(), EVENT_NAME_EXTERNAL_SCOPE, EVENT_DOCUMENTATION_EXTERNAL_SCOPE);
        assertSignalEventExecutionSet(filledSubprocessEventExternal.getExecutionSet(), EVENT_REF_EXTERNAL_SCOPE, EVENT_SIGNAL_SCOPE_EXTERNAL);
        assertDataIOSet(filledSubprocessEventExternal.getDataIOSet(), EVENT_DATA_INPUT_EXTERNAL_SCOPE);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventEmptyProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = getDiagram();
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateSignalEventThrowing emptySubprocessEvent = getThrowingIntermediateNodeById(diagram,
                                                                                               EMPTY_SUBPROCESS_LEVEL_EVENT_ID,
                                                                                               HAS_NO_INCOME_EDGE, ZERO_OUTGOING_EDGES);
        assertGeneralSet(emptySubprocessEvent.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertSignalEventExecutionSet(emptySubprocessEvent.getExecutionSet(), EMPTY_VALUE, EVENT_SIGNAL_SCOPE_DEFAULT);
        assertDataIOSet(emptySubprocessEvent.getDataIOSet(), EMPTY_VALUE);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEventWithEdgesFilledProperties() {
        final String EVENT_NAME_PROCESS_INSTANCE_SCOPE = "signal04 ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION_PROCESS_INSTANCE_SCOPE = "signal04 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String EVENT_REF_PROCESS_INSTANCE_SCOPE = "signal04";
        final String EVENT_DATA_INPUT_PROCESS_INSTANCE_SCOPE = "signal04:String||||[din]GlobalVar->signal04";

        final String EVENT_NAME_PROJECT_SCOPE = "signal05 ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION_PROJECT_SCOPE = "signal05 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String EVENT_REF_PROJECT_SCOPE = "signal05";
        final String EVENT_DATA_INPUT_PROJECT_SCOPE = "signal05:String||||[din]GlobalVar->signal05";

        final String EVENT_NAME_EXTERNAL_SCOPE = "signal06 ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION_EXTERNAL_SCOPE = "signal06 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String EVENT_REF_EXTERNAL_SCOPE = "signal06";
        final String EVENT_DATA_INPUT_EXTERNAL_SCOPE = "signal06:String||||[din]GlobalVar->signal06";

        Diagram<Graph, Metadata> diagram = getDiagram();
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateSignalEventThrowing filledTopEventProcessInstance = getThrowingIntermediateNodeById(diagram,
                                                                                                        FILLED_WITH_EDGES_TOP_LEVEL_EVENT_PROCESS_INSTANCE_SCOPE_ID,
                                                                                                        HAS_INCOME_EDGE, TWO_OUTGOING_EDGES);
        assertGeneralSet(filledTopEventProcessInstance.getGeneral(), EVENT_NAME_PROCESS_INSTANCE_SCOPE, EVENT_DOCUMENTATION_PROCESS_INSTANCE_SCOPE);
        assertSignalEventExecutionSet(filledTopEventProcessInstance.getExecutionSet(), EVENT_REF_PROCESS_INSTANCE_SCOPE, EVENT_SIGNAL_SCOPE_PROCESS_INSTANCE);
        assertDataIOSet(filledTopEventProcessInstance.getDataIOSet(), EVENT_DATA_INPUT_PROCESS_INSTANCE_SCOPE);

        IntermediateSignalEventThrowing filledTopEventProject = getThrowingIntermediateNodeById(diagram,
                                                                                                FILLED_WITH_EDGES_TOP_LEVEL_PROJECT_SCOPE_ID,
                                                                                                HAS_INCOME_EDGE, TWO_OUTGOING_EDGES);
        assertGeneralSet(filledTopEventProject.getGeneral(), EVENT_NAME_PROJECT_SCOPE, EVENT_DOCUMENTATION_PROJECT_SCOPE);
        assertSignalEventExecutionSet(filledTopEventProject.getExecutionSet(), EVENT_REF_PROJECT_SCOPE, EVENT_SIGNAL_SCOPE_PROJECT);
        assertDataIOSet(filledTopEventProject.getDataIOSet(), EVENT_DATA_INPUT_PROJECT_SCOPE);

        IntermediateSignalEventThrowing filledTopEventExternal = getThrowingIntermediateNodeById(diagram,
                                                                                                 FILLED_WITH_EDGES_TOP_LEVEL_EVENT_EXTERNAL_SCOPE_ID,
                                                                                                 HAS_INCOME_EDGE, TWO_OUTGOING_EDGES);
        assertGeneralSet(filledTopEventExternal.getGeneral(), EVENT_NAME_EXTERNAL_SCOPE, EVENT_DOCUMENTATION_EXTERNAL_SCOPE);
        assertSignalEventExecutionSet(filledTopEventExternal.getExecutionSet(), EVENT_REF_EXTERNAL_SCOPE, EVENT_SIGNAL_SCOPE_EXTERNAL);
        assertDataIOSet(filledTopEventExternal.getDataIOSet(), EVENT_DATA_INPUT_EXTERNAL_SCOPE);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEventWithEdgesEmptyProperties() {
        Diagram<Graph, Metadata> diagram = getDiagram();
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateSignalEventThrowing emptyEvent = getThrowingIntermediateNodeById(diagram,
                                                                                     EMPTY_WITH_EDGES_TOP_LEVEL_EVENT_ID,
                                                                                     HAS_INCOME_EDGE, TWO_OUTGOING_EDGES);
        assertGeneralSet(emptyEvent.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertSignalEventExecutionSet(emptyEvent.getExecutionSet(), EMPTY_VALUE, EVENT_SIGNAL_SCOPE_DEFAULT);
        assertDataIOSet(emptyEvent.getDataIOSet(), EMPTY_VALUE);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventWithEdgesEmptyProperties() {
        Diagram<Graph, Metadata> diagram = getDiagram();
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateSignalEventThrowing emptySubprocessEvent = getThrowingIntermediateNodeById(diagram,
                                                                                               EMPTY_WITH_EDGES_SUBPROCESS_LEVEL_EVENT_ID,
                                                                                               HAS_INCOME_EDGE, TWO_OUTGOING_EDGES);
        assertGeneralSet(emptySubprocessEvent.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertSignalEventExecutionSet(emptySubprocessEvent.getExecutionSet(), EMPTY_VALUE, EVENT_SIGNAL_SCOPE_DEFAULT);
        assertDataIOSet(emptySubprocessEvent.getDataIOSet(), EMPTY_VALUE);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventWithEdgesFilledProperties() {
        final String EVENT_NAME_PROCESS_INSTANCE_SCOPE = "signal10 ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION_PROCESS_INSTANCE_SCOPE = "signal10 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String EVENT_REF_PROCESS_INSTANCE_SCOPE = "signal10";
        final String EVENT_DATA_INPUT_PROCESS_INSTANCE_SCOPE = "signal10:String||||[din]GlobalVar->signal10";

        final String EVENT_NAME_PROJECT_SCOPE = "signal11 ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION_PROJECT_SCOPE = "signal11 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String EVENT_REF_PROJECT_SCOPE = "signal11";
        final String EVENT_DATA_INPUT_PROJECT_SCOPE = "signal11:String||||[din]GlobalVar->signal11";

        final String EVENT_NAME_EXTERNAL_SCOPE = "signal12 ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String EVENT_DOCUMENTATION_EXTERNAL_SCOPE = "signal12 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String EVENT_REF_EXTERNAL_SCOPE = "signal12";
        final String EVENT_DATA_INPUT_EXTERNAL_SCOPE = "signal12:String||||[din]GlobalVar->signal12";

        Diagram<Graph, Metadata> diagram = getDiagram();
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        IntermediateSignalEventThrowing filledSubprocessEventProcessInstance = getThrowingIntermediateNodeById(diagram,
                                                                                                               FILLED_WITH_EDGES_SUBPROCESS_LEVEL_PROCESS_INSTANCE_SCOPE_ID,
                                                                                                               HAS_INCOME_EDGE, TWO_OUTGOING_EDGES);
        assertGeneralSet(filledSubprocessEventProcessInstance.getGeneral(), EVENT_NAME_PROCESS_INSTANCE_SCOPE, EVENT_DOCUMENTATION_PROCESS_INSTANCE_SCOPE);
        assertSignalEventExecutionSet(filledSubprocessEventProcessInstance.getExecutionSet(), EVENT_REF_PROCESS_INSTANCE_SCOPE, EVENT_SIGNAL_SCOPE_PROCESS_INSTANCE);
        assertDataIOSet(filledSubprocessEventProcessInstance.getDataIOSet(), EVENT_DATA_INPUT_PROCESS_INSTANCE_SCOPE);

        IntermediateSignalEventThrowing filledSubprocessEventProject = getThrowingIntermediateNodeById(diagram,
                                                                                                       FILLED_WITH_EDGES_SUBPROCESS_LEVEL_PROJECT_SCOPE_ID,
                                                                                                       HAS_INCOME_EDGE, TWO_OUTGOING_EDGES);
        assertGeneralSet(filledSubprocessEventProject.getGeneral(), EVENT_NAME_PROJECT_SCOPE, EVENT_DOCUMENTATION_PROJECT_SCOPE);
        assertSignalEventExecutionSet(filledSubprocessEventProject.getExecutionSet(), EVENT_REF_PROJECT_SCOPE, EVENT_SIGNAL_SCOPE_PROJECT);
        assertDataIOSet(filledSubprocessEventProject.getDataIOSet(), EVENT_DATA_INPUT_PROJECT_SCOPE);

        IntermediateSignalEventThrowing filledSubprocessEventExternal = getThrowingIntermediateNodeById(diagram,
                                                                                                        FILLED_WITH_EDGES_SUBPROCESS_LEVEL_EVENT_EXTERNAL_SCOPE_ID,
                                                                                                        HAS_INCOME_EDGE, TWO_OUTGOING_EDGES);
        assertGeneralSet(filledSubprocessEventExternal.getGeneral(), EVENT_NAME_EXTERNAL_SCOPE, EVENT_DOCUMENTATION_EXTERNAL_SCOPE);
        assertSignalEventExecutionSet(filledSubprocessEventExternal.getExecutionSet(), EVENT_REF_EXTERNAL_SCOPE, EVENT_SIGNAL_SCOPE_EXTERNAL);
        assertDataIOSet(filledSubprocessEventExternal.getDataIOSet(), EVENT_DATA_INPUT_EXTERNAL_SCOPE);
    }

    @Override
    String getBpmnThrowingIntermediateEventFilePath() {
        return BPMN_THROWING_INTERMEDIATE_EVENT_FILE_PATH;
    }

    @Override
    Class<IntermediateSignalEventThrowing> getThrowingIntermediateEventType() {
        return IntermediateSignalEventThrowing.class;
    }

    @Override
    String[] getFilledTopLevelEventIds() {
        return new String[]{FILLED_TOP_LEVEL_EVENT_PROCESS_INSTANCE_SCOPE_ID,
                FILLED_TOP_LEVEL_EVENT_PROJECT_SCOPE_ID,
                FILLED_TOP_LEVEL_EVENT_EXTERNAL_SCOPE_ID};
    }

    @Override
    String getEmptyTopLevelEventId() {
        return EMPTY_TOP_LEVEL_EVENT_ID;
    }

    @Override
    String[] getFilledSubprocessLevelEventIds() {
        return new String[]{FILLED_SUBPROCESS_LEVEL_PROCESS_INSTANCE_SCOPE_ID,
                FILLED_SUBPROCESS_LEVEL_PROJECT_SCOPE_ID,
                FILLED_SUBPROCESS_LEVEL_EVENT_EXTERNAL_SCOPE_ID};
    }

    @Override
    String getEmptySubprocessLevelEventId() {
        return EMPTY_SUBPROCESS_LEVEL_EVENT_ID;
    }

    @Override
    String[] getFilledTopLevelEventWithEdgesIds() {
        return new String[]{FILLED_WITH_EDGES_TOP_LEVEL_EVENT_PROCESS_INSTANCE_SCOPE_ID,
                FILLED_WITH_EDGES_TOP_LEVEL_PROJECT_SCOPE_ID,
                FILLED_WITH_EDGES_TOP_LEVEL_EVENT_EXTERNAL_SCOPE_ID};
    }

    @Override
    String getEmptyTopLevelEventWithEdgesId() {
        return EMPTY_WITH_EDGES_TOP_LEVEL_EVENT_ID;
    }

    @Override
    String[] getFilledSubprocessLevelEventWithEdgesIds() {
        return new String[]{FILLED_WITH_EDGES_SUBPROCESS_LEVEL_PROCESS_INSTANCE_SCOPE_ID,
                FILLED_WITH_EDGES_SUBPROCESS_LEVEL_PROJECT_SCOPE_ID,
                FILLED_WITH_EDGES_SUBPROCESS_LEVEL_EVENT_EXTERNAL_SCOPE_ID};
    }

    @Override
    String getEmptySubprocessLevelEventWithEdgesId() {
        return EMPTY_WITH_EDGES_SUBPROCESS_LEVEL_EVENT_ID;
    }

    private void assertSignalEventExecutionSet(ScopedSignalEventExecutionSet executionSet, String signalReference, String signalScope) {
        assertThat(executionSet).isNotNull();
        assertThat(executionSet.getSignalRef()).isNotNull();
        assertThat(executionSet.getSignalScope()).isNotNull();
        assertThat(executionSet.getSignalRef().getValue()).isEqualTo(signalReference);
        assertThat(executionSet.getSignalScope().getValue()).isEqualTo(signalScope);
    }
}
