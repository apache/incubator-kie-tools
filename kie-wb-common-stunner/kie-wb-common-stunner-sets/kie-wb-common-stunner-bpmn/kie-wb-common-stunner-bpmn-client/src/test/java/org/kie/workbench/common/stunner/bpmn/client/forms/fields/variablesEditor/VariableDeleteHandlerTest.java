/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.variablesEditor;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.definition.BusinessRuleTask;
import org.kie.workbench.common.stunner.bpmn.definition.UserTask;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.AssignmentsInfo;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.DataIOSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.UserTaskExecutionSet;
import org.kie.workbench.common.stunner.core.TestingGraphInstanceBuilder;
import org.kie.workbench.common.stunner.core.TestingGraphMockHandler;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class VariableDeleteHandlerTest {

    private final String PROPERTY_ID_INPUT = "[din]test_input_task->test_input_process";
    private final String PROPERTY_ID_OUTPUT = "[dout]test_output_task->test_output_process";
    private final String VARIABLE_ID_INPUT = "test_input_process";
    private final String VARIABLE_ID_OUTPUT = "test_output_process";
    private final String VARIABLE_ID_INPUT_NO_RESULT = "test_input_process_no_result";
    private final String VARIABLE_ID_OUTPUT_NO_RESULT = "test_output_process_no_result";

    private String assignmentsInfoString;
    private TestingGraphMockHandler graphTestHandler;
    private TestingGraphInstanceBuilder.TestGraph2 graphInstance;

    @Mock
    private UserTask userTask;

    @Mock
    private UserTaskExecutionSet userTaskExecutionSet;

    @Mock
    private BusinessRuleTask businessRuleTask;

    @Mock
    private DataIOSet businessRuleDataIOSet;

    @Mock
    private AssignmentsInfo assignmentsInfo;

    @Mock
    private Definition definition;

    private VariableDeleteHandler variableDeleteHandler;

    @Before
    public void setUp() throws Exception {
        assignmentsInfoString = PROPERTY_ID_INPUT + "," + PROPERTY_ID_OUTPUT;
        variableDeleteHandler = new VariableDeleteHandler();
        this.graphTestHandler = new TestingGraphMockHandler();
        graphInstance = TestingGraphInstanceBuilder.newGraph2(graphTestHandler);
        when(userTask.getExecutionSet()).thenReturn(userTaskExecutionSet);
        when(userTask.getExecutionSet().getAssignmentsinfo()).thenReturn(assignmentsInfo);
        when(userTask.getExecutionSet().getAssignmentsinfo().getValue()).thenReturn(assignmentsInfoString);
        when(businessRuleTask.getDataIOSet()).thenReturn(businessRuleDataIOSet);
        when(businessRuleTask.getDataIOSet().getAssignmentsinfo()).thenReturn(assignmentsInfo);
        when(businessRuleTask.getDataIOSet().getAssignmentsinfo().getValue()).thenReturn(assignmentsInfoString);
        graphInstance.intermNode.setContent(definition);
    }

    @Test
    public void testGetVariableListInputUserTask() {
        when(definition.getDefinition()).thenReturn(userTask);
        boolean isVariableBound = variableDeleteHandler.isVariableBoundToNodes(graphInstance.graph,
                                                                               VARIABLE_ID_INPUT);
        assertTrue(isVariableBound);
    }

    @Test
    public void testGetVariableListOutputUserTask() {
        when(definition.getDefinition()).thenReturn(userTask);
        boolean isVariableBound = variableDeleteHandler.isVariableBoundToNodes(graphInstance.graph,
                                                                               VARIABLE_ID_OUTPUT);
        assertTrue(isVariableBound);
    }

    @Test
    public void testGetVariableListInputBusinessRuleTask() {
        when(definition.getDefinition()).thenReturn(businessRuleTask);
        boolean isVariableBound = variableDeleteHandler.isVariableBoundToNodes(graphInstance.graph,
                                                                               VARIABLE_ID_INPUT);
        assertTrue(isVariableBound);
    }

    @Test
    public void testGetVariableListOutputBusinessRule() {
        when(definition.getDefinition()).thenReturn(businessRuleTask);
        boolean isVariableBound = variableDeleteHandler.isVariableBoundToNodes(graphInstance.graph,
                                                                               VARIABLE_ID_OUTPUT);
        assertTrue(isVariableBound);
    }

    @Test
    public void testGetVariableListInputUserTaskNoResult() {
        when(definition.getDefinition()).thenReturn(userTask);
        boolean isVariableBound = variableDeleteHandler.isVariableBoundToNodes(graphInstance.graph,
                                                                               VARIABLE_ID_INPUT_NO_RESULT);
        assertFalse(isVariableBound);
    }

    @Test
    public void testGetVariableListOutputUserTaskNoResult() {
        when(definition.getDefinition()).thenReturn(userTask);
        boolean isVariableBound = variableDeleteHandler.isVariableBoundToNodes(graphInstance.graph,
                                                                               VARIABLE_ID_OUTPUT_NO_RESULT);
        assertFalse(isVariableBound);
    }

    @Test
    public void testGetVariableListInputBusinessRuleTaskNoResult() {
        when(definition.getDefinition()).thenReturn(businessRuleTask);
        boolean isVariableBound = variableDeleteHandler.isVariableBoundToNodes(graphInstance.graph,
                                                                               VARIABLE_ID_INPUT_NO_RESULT);
        assertFalse(isVariableBound);
    }

    @Test
    public void testGetVariableListOutputBusinessRuleNoResult() {
        when(definition.getDefinition()).thenReturn(businessRuleTask);
        boolean isVariableBound = variableDeleteHandler.isVariableBoundToNodes(graphInstance.graph,
                                                                               VARIABLE_ID_OUTPUT_NO_RESULT);
        assertFalse(isVariableBound);
    }
}
