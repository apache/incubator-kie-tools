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

package org.kie.workbench.common.stunner.bpmn.backend.service.diagram.marshalling.subprocesses;

import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.definition.ReusableSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ReusableSubprocessTaskExecutionSet;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;

import static org.assertj.core.api.Assertions.assertThat;

public class ReusableSubProcessTest extends SubProcess<ReusableSubprocess> {

    private static final String BPMN_SUB_PROCESS_FILE_PATH = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/reusableSubProcesses.bpmn";

    private static final String TOP_LEVEL_EMPTY_SUBPROCESS_ID = "_83336319-6615-4FCC-B2EF-3376CB552CA9";
    private static final String TOP_LEVEL_FILLED_SUBPROCESS_ID = "_B26D5827-370D-40F4-A92E-0D7F36E56BFD";
    private static final String TOP_LEVEL_SUBPROCESS_WITH_EDGES_ID = "_BEBE4D01-648F-4DC5-8ED5-72D2F61F63D3";

    private static final String SUBPROCESS_LEVEL_EMPTY_SUBPROCESS_ID = "_0C801E16-60D9-4F68-BE7F-1C296DEC9B3A";
    private static final String SUBPROCESS_LEVEL_FILLED_SUBPROCESS_ID = "_335A3BD6-BFC0-4D77-A9CC-BA2F15CE4D53";
    private static final String SUBPROCESS_LEVEL_SUBPROCESS_WITH_EDGES_ID = "_362B7975-3E50-4B53-B351-C9D8C62B0C96";

    private static final int AMOUNT_OF_NODES_IN_DIAGRAM = 14;

    private static final String DEFAULT_CALLED_ELEMENT = "";
    private static final String DEFAULT_SUB_PROCESS_DATA_INPUT_OUTPUT = "";

    private static final boolean IS_INDEPENDENT = true;
    private static final boolean IS_ASYNC = true;
    private static final boolean WAIT_FOR_COMPLETION = true;
    private static final boolean IS_NOT_INDEPENDENT = false;
    private static final boolean IS_NOT_ASYNC = false;
    private static final boolean DO_NOT_WAIT_FOR_COMPLETION = false;

    @Test
    @Override
    public void testUnmarshallTopLevelEmptyPropertiesSubProcess() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_SUB_PROCESS_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        ReusableSubprocess topLevelSubProcess = getSubProcessNodeById(diagram,
                                                                      TOP_LEVEL_EMPTY_SUBPROCESS_ID,
                                                                      EMPTY_INCOME_EDGES,
                                                                      EMPTY_OUTCOME_EDGES);
        assertGeneralSet(topLevelSubProcess.getGeneral(), DEFAULT_NAME, DEFAULT_DOCUMENTATION);

        assertReusableSubProcessExecutionSet(topLevelSubProcess.getExecutionSet(),
                                             DEFAULT_CALLED_ELEMENT,
                                             IS_NOT_INDEPENDENT,
                                             IS_NOT_ASYNC,
                                             DO_NOT_WAIT_FOR_COMPLETION);
        assertDataIOSet(topLevelSubProcess.getDataIOSet(), DEFAULT_SUB_PROCESS_DATA_INPUT_OUTPUT);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelFilledPropertiesSubProcess() throws Exception {
        final String SUB_PROCESS_NAME = "Reusable process01 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String SUB_PROCESS_DOCUMENTATION = "Reusable process01 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String CALLED_ELEMENT = "testProject.linkedReusableSubProcess";
        final String SUB_PROCESS_DATA_INPUT_OUTPUT = "|input:String||output:String|[din]processGlobalVar->input,[dout]output->processGlobalVar";

        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_SUB_PROCESS_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        ReusableSubprocess topLevelSubProcess = getSubProcessNodeById(diagram,
                                                                      TOP_LEVEL_FILLED_SUBPROCESS_ID,
                                                                      EMPTY_INCOME_EDGES,
                                                                      EMPTY_OUTCOME_EDGES);
        assertGeneralSet(topLevelSubProcess.getGeneral(), SUB_PROCESS_NAME, SUB_PROCESS_DOCUMENTATION);

        assertReusableSubProcessExecutionSet(topLevelSubProcess.getExecutionSet(),
                                             CALLED_ELEMENT,
                                             IS_INDEPENDENT,
                                             IS_ASYNC,
                                             WAIT_FOR_COMPLETION);
        assertDataIOSet(topLevelSubProcess.getDataIOSet(), SUB_PROCESS_DATA_INPUT_OUTPUT);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelSubProcessWithEdges() throws Exception {
        final String SUB_PROCESS_NAME = "Sub-process";

        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_SUB_PROCESS_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        ReusableSubprocess topLevelSubProcess = getSubProcessNodeById(diagram,
                                                                      TOP_LEVEL_SUBPROCESS_WITH_EDGES_ID,
                                                                      ONE_INCOME_EDGE,
                                                                      TWO_OUTCOME_EDGES);
        assertGeneralSet(topLevelSubProcess.getGeneral(), SUB_PROCESS_NAME, DEFAULT_DOCUMENTATION);

        assertReusableSubProcessExecutionSet(topLevelSubProcess.getExecutionSet(),
                                             DEFAULT_CALLED_ELEMENT,
                                             IS_NOT_INDEPENDENT,
                                             IS_NOT_ASYNC,
                                             WAIT_FOR_COMPLETION);
        assertDataIOSet(topLevelSubProcess.getDataIOSet(), DEFAULT_SUB_PROCESS_DATA_INPUT_OUTPUT);
    }

    @Test
    @Override
    public void testUnmarshallSubProcessLevelEmptyPropertiesSubProcess() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_SUB_PROCESS_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        ReusableSubprocess subProcessLevelSubProcess = getSubProcessNodeById(diagram,
                                                                             SUBPROCESS_LEVEL_EMPTY_SUBPROCESS_ID,
                                                                             EMPTY_INCOME_EDGES,
                                                                             EMPTY_OUTCOME_EDGES);
        assertGeneralSet(subProcessLevelSubProcess.getGeneral(), DEFAULT_NAME, DEFAULT_DOCUMENTATION);

        assertReusableSubProcessExecutionSet(subProcessLevelSubProcess.getExecutionSet(),
                                             DEFAULT_CALLED_ELEMENT,
                                             IS_NOT_INDEPENDENT,
                                             IS_NOT_ASYNC,
                                             DO_NOT_WAIT_FOR_COMPLETION);
        assertDataIOSet(subProcessLevelSubProcess.getDataIOSet(), DEFAULT_SUB_PROCESS_DATA_INPUT_OUTPUT);
    }

    @Test
    @Override
    public void testUnmarshallSubProcessLevelFilledPropertiesSubProcess() throws Exception {
        final String SUB_PROCESS_NAME = "Reusable process03 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String SUB_PROCESS_DOCUMENTATION = "Reusable process03 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String CALLED_ELEMENT = "testProject.linkedReusableSubProcess";
        final String SUB_PROCESS_DATA_INPUT_OUTPUT = "|input:String||output:String|[din]processGlobalVar->input,[dout]output->processGlobalVar";

        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_SUB_PROCESS_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        ReusableSubprocess subProcessLevelSubProcess = getSubProcessNodeById(diagram,
                                                                             SUBPROCESS_LEVEL_FILLED_SUBPROCESS_ID,
                                                                             EMPTY_INCOME_EDGES,
                                                                             EMPTY_OUTCOME_EDGES);
        assertGeneralSet(subProcessLevelSubProcess.getGeneral(), SUB_PROCESS_NAME, SUB_PROCESS_DOCUMENTATION);

        assertReusableSubProcessExecutionSet(subProcessLevelSubProcess.getExecutionSet(),
                                             CALLED_ELEMENT,
                                             IS_INDEPENDENT,
                                             IS_ASYNC,
                                             WAIT_FOR_COMPLETION);
        assertDataIOSet(subProcessLevelSubProcess.getDataIOSet(), SUB_PROCESS_DATA_INPUT_OUTPUT);
    }

    @Test
    @Override
    public void testUnmarshallSubProcessLevelSubProcessWithEdges() throws Exception {
        final String SUB_PROCESS_NAME = "Sub-process";

        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_SUB_PROCESS_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        ReusableSubprocess subProcessLevelSubProcess = getSubProcessNodeById(diagram,
                                                                             SUBPROCESS_LEVEL_SUBPROCESS_WITH_EDGES_ID,
                                                                             ONE_INCOME_EDGE,
                                                                             TWO_OUTCOME_EDGES);
        assertGeneralSet(subProcessLevelSubProcess.getGeneral(), SUB_PROCESS_NAME, DEFAULT_DOCUMENTATION);

        assertReusableSubProcessExecutionSet(subProcessLevelSubProcess.getExecutionSet(),
                                             DEFAULT_CALLED_ELEMENT,
                                             IS_NOT_INDEPENDENT,
                                             IS_NOT_ASYNC,
                                             WAIT_FOR_COMPLETION);
        assertDataIOSet(subProcessLevelSubProcess.getDataIOSet(), DEFAULT_SUB_PROCESS_DATA_INPUT_OUTPUT);
    }

    @Override
    Class<ReusableSubprocess> getSubProcessType() {
        return ReusableSubprocess.class;
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
    String getTopLevelFilledPropertiesSubProcessId() {
        return TOP_LEVEL_FILLED_SUBPROCESS_ID;
    }

    @Override
    String getTopLevelSubProcessWithEdgesId() {
        return TOP_LEVEL_SUBPROCESS_WITH_EDGES_ID;
    }

    @Override
    String getSubProcessLevelEmptyPropertiesSubProcessId() {
        return SUBPROCESS_LEVEL_EMPTY_SUBPROCESS_ID;
    }

    @Override
    String getSubProcessLevelFilledPropertiesSubProcessId() {
        return SUBPROCESS_LEVEL_FILLED_SUBPROCESS_ID;
    }

    @Override
    String getSubProcessLevelSubProcessWithEdgesId() {
        return SUBPROCESS_LEVEL_SUBPROCESS_WITH_EDGES_ID;
    }

    private void assertReusableSubProcessExecutionSet(ReusableSubprocessTaskExecutionSet executionSet,
                                                      String calledElement,
                                                      boolean independent,
                                                      boolean isAsync,
                                                      boolean waitForCompletion) {
        assertThat(executionSet).isNotNull();
        assertThat(executionSet.getCalledElement()).isNotNull();
        assertThat(executionSet.getIndependent()).isNotNull();
        assertThat(executionSet.getIsAsync()).isNotNull();
        assertThat(executionSet.getWaitForCompletion()).isNotNull();

        assertThat(executionSet.getCalledElement().getValue()).isEqualTo(calledElement);
        assertThat(executionSet.getIndependent().getValue()).isEqualTo(independent);
        assertThat(executionSet.getIsAsync().getValue()).isEqualTo(isAsync);
        assertThat(executionSet.getWaitForCompletion().getValue()).isEqualTo(waitForCompletion);
    }
}
