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

package org.kie.workbench.common.stunner.bpmn.backend.service.diagram.marshalling.subprocesses;

import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.definition.EventSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.property.subProcess.execution.EventSubprocessExecutionSet;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;

import static org.assertj.core.api.Assertions.assertThat;

public class EventSubProcessTest extends SubProcessTest<EventSubprocess> {

    private static final String BPMN_SUB_PROCESS_FILE_PATH = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/eventSubProcesses.bpmn";

    private static final String TOP_LEVEL_EMPTY_SUBPROCESS_ID = "_BFAB4FD7-E901-466E-A628-D22047469503";
    private static final String TOP_LEVEL_FILLED_SUBPROCESS_ID = "_C331C78A-65AC-46B4-A423-3CF3574372BA";

    private static final String SUBPROCESS_LEVEL_EMPTY_SUBPROCESS_ID = "_51FEAA5A-43FF-4EC4-8770-33A54BA98A6B";
    private static final String SUBPROCESS_LEVEL_FILLED_SUBPROCESS_ID = "_6A441CA4-0E47-4C84-8473-33F274DEF547";

    private static final String TOP_LEVEL_SUBPROCESS_WITH_EDGES = "_FCB7D840-3B83-4E34-A9DC-C5C0F6C790CB";
    private static final String SUBPROCESS_LEVEL_SUBPROCESS_WITH_EDGES = "_CFA473E5-6CEC-4DE8-A387-F76E49A5FF00";

    private static final int AMOUNT_OF_NODES_IN_DIAGRAM = 17;

    private static final String SLA_DUE_DATE = "12/25/1983";

    public EventSubProcessTest() throws Exception {
    }

    @Test
    @Override
    public void testMarshallTopLevelSubProcessWithEdges() {
        // event sub-process cannot have any incoming edges relating to bpmn specification
        checkSubProcessMarshalling(getTopLevelSubProcessWithEdgesId(), EMPTY_INCOME_EDGES, FOUR_OUTCOME_EDGES);
    }

    @Test
    @Override
    public void testMarshallSubProcessLevelSubProcessWithEdges() {
        // event sub-process cannot have any incoming edges relating to bpmn specification
        checkSubProcessMarshalling(getSubProcessLevelSubProcessWithEdgesId(), EMPTY_INCOME_EDGES, FOUR_OUTCOME_EDGES);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEmptyPropertiesSubProcess() {
        Diagram<Graph, Metadata> diagram = getDiagram();
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        EventSubprocess topLevelSubProcess = getSubProcessNodeById(diagram,
                                                                   TOP_LEVEL_EMPTY_SUBPROCESS_ID,
                                                                   EMPTY_INCOME_EDGES,
                                                                   EMPTY_OUTCOME_EDGES);

        assertGeneralSet(topLevelSubProcess.getGeneral(), DEFAULT_NAME, DEFAULT_DOCUMENTATION);
        assertEventSubProcessExecutionSet(topLevelSubProcess.getExecutionSet(),
                                          IS_NOT_ASYNC,
                                          EMPTY_VALUE);
        assertSubProcessProcessData(topLevelSubProcess.getProcessData(), EMPTY_VALUE);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelFilledPropertiesSubProcess() {
        final String SUB_PROCESS_NAME = "Event process01 name ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String SUB_PROCESS_DOCUMENTATION = "Event process01 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String SUB_PROCESS_VARIABLES = "subVarString:String:false,subVarCustom:Custom:false,subVarBoolean:Boolean:false,subVarFloat:Float:false,subVarInteger:Integer:false,subVarObject:Object:false";

        Diagram<Graph, Metadata> diagram = getDiagram();
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        EventSubprocess topLevelSubProcessJava = getSubProcessNodeById(diagram,
                                                                       TOP_LEVEL_FILLED_SUBPROCESS_ID,
                                                                       EMPTY_INCOME_EDGES,
                                                                       EMPTY_OUTCOME_EDGES);

        assertGeneralSet(topLevelSubProcessJava.getGeneral(), SUB_PROCESS_NAME, SUB_PROCESS_DOCUMENTATION);
        assertEventSubProcessExecutionSet(topLevelSubProcessJava.getExecutionSet(),
                                          IS_ASYNC,
                                          SLA_DUE_DATE);
        assertSubProcessProcessData(topLevelSubProcessJava.getProcessData(), SUB_PROCESS_VARIABLES);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelSubProcessWithEdges() {
        final String SUB_PROCESS_NAME = "Event Sub-process";

        Diagram<Graph, Metadata> diagram = getDiagram();
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        EventSubprocess topLevelSubProcess = getSubProcessNodeById(diagram,
                                                                   TOP_LEVEL_SUBPROCESS_WITH_EDGES,
                                                                   EMPTY_INCOME_EDGES,
                                                                   FOUR_OUTCOME_EDGES);

        assertGeneralSet(topLevelSubProcess.getGeneral(), SUB_PROCESS_NAME, DEFAULT_DOCUMENTATION);
        assertEventSubProcessExecutionSet(topLevelSubProcess.getExecutionSet(),
                                          IS_NOT_ASYNC,
                                          EMPTY_VALUE);
        assertSubProcessProcessData(topLevelSubProcess.getProcessData(), EMPTY_VALUE);
    }

    @Test
    @Override
    public void testUnmarshallSubProcessLevelEmptyPropertiesSubProcess() {
        Diagram<Graph, Metadata> diagram = getDiagram();
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        EventSubprocess subProcessLevelSubProcess = getSubProcessNodeById(diagram,
                                                                          SUBPROCESS_LEVEL_EMPTY_SUBPROCESS_ID,
                                                                          EMPTY_INCOME_EDGES,
                                                                          EMPTY_OUTCOME_EDGES);

        assertGeneralSet(subProcessLevelSubProcess.getGeneral(), DEFAULT_NAME, DEFAULT_DOCUMENTATION);
        assertEventSubProcessExecutionSet(subProcessLevelSubProcess.getExecutionSet(),
                                          IS_NOT_ASYNC,
                                          EMPTY_VALUE);
        assertSubProcessProcessData(subProcessLevelSubProcess.getProcessData(), EMPTY_VALUE);
    }

    @Test
    @Override
    public void testUnmarshallSubProcessLevelFilledPropertiesSubProcess() {
        final String SUB_PROCESS_NAME = "Event process02 name ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String SUB_PROCESS_DOCUMENTATION = "Event process02 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String SUB_PROCESS_VARIABLES = "subVarString:String:false,subVarCustom:Custom:false,subVarBoolean:Boolean:false,subVarFloat:Float:false,subVarInteger:Integer:false,subVarObject:Object:false";

        Diagram<Graph, Metadata> diagram = getDiagram();
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        EventSubprocess subProcessLevelSubProcessJava = getSubProcessNodeById(diagram,
                                                                              SUBPROCESS_LEVEL_FILLED_SUBPROCESS_ID,
                                                                              EMPTY_INCOME_EDGES,
                                                                              EMPTY_OUTCOME_EDGES);

        assertGeneralSet(subProcessLevelSubProcessJava.getGeneral(), SUB_PROCESS_NAME, SUB_PROCESS_DOCUMENTATION);
        assertEventSubProcessExecutionSet(subProcessLevelSubProcessJava.getExecutionSet(),
                                          IS_ASYNC,
                                          SLA_DUE_DATE);
        assertSubProcessProcessData(subProcessLevelSubProcessJava.getProcessData(), SUB_PROCESS_VARIABLES);
    }

    @Test
    @Override
    public void testUnmarshallSubProcessLevelSubProcessWithEdges() {
        final String SUB_PROCESS_NAME = "Event Sub-process";

        Diagram<Graph, Metadata> diagram = getDiagram();
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        EventSubprocess subProcessLevelSubProcess = getSubProcessNodeById(diagram,
                                                                          SUBPROCESS_LEVEL_SUBPROCESS_WITH_EDGES,
                                                                          EMPTY_INCOME_EDGES,
                                                                          FOUR_OUTCOME_EDGES);

        assertGeneralSet(subProcessLevelSubProcess.getGeneral(), SUB_PROCESS_NAME, DEFAULT_DOCUMENTATION);
        assertEventSubProcessExecutionSet(subProcessLevelSubProcess.getExecutionSet(),
                                          IS_NOT_ASYNC,
                                          EMPTY_VALUE);
        assertSubProcessProcessData(subProcessLevelSubProcess.getProcessData(), EMPTY_VALUE);
    }

    @Override
    Class<EventSubprocess> getSubProcessType() {
        return EventSubprocess.class;
    }

    @Override
    String getBpmnSubProcessFilePath() {
        return BPMN_SUB_PROCESS_FILE_PATH;
    }

    @Override
    String getTopLevelEmptyPropertiesSubProcessId() {
        return TOP_LEVEL_EMPTY_SUBPROCESS_ID;
    }

    @Override
    String[] getTopLevelFilledPropertiesSubProcessesIds() {
        return new String[]{TOP_LEVEL_FILLED_SUBPROCESS_ID};
    }

    @Override
    String getTopLevelSubProcessWithEdgesId() {
        return TOP_LEVEL_SUBPROCESS_WITH_EDGES;
    }

    @Override
    String getSubProcessLevelEmptyPropertiesSubProcessId() {
        return SUBPROCESS_LEVEL_EMPTY_SUBPROCESS_ID;
    }

    @Override
    String[] getSubProcessLevelFilledPropertiesSubProcessesIds() {
        return new String[]{SUBPROCESS_LEVEL_FILLED_SUBPROCESS_ID};
    }

    @Override
    String getSubProcessLevelSubProcessWithEdgesId() {
        return SUBPROCESS_LEVEL_SUBPROCESS_WITH_EDGES;
    }

    private void assertEventSubProcessExecutionSet(EventSubprocessExecutionSet executionSet,
                                                   boolean isAsync,
                                                   String slaDueDate) {
        assertThat(executionSet).isNotNull();
        assertThat(executionSet.getIsAsync()).isNotNull();
        assertThat(executionSet.getIsAsync().getValue()).isEqualTo(isAsync);
        assertThat(executionSet.getSlaDueDate()).isNotNull();
        assertThat(executionSet.getSlaDueDate().getValue()).isEqualTo(slaDueDate);
    }
}
