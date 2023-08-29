/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.multipleInstanceVariableEditor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.processing.engine.handling.ValidationResult;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.MultipleInstanceSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.ReusableSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.UserTask;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.AssignmentsInfo;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.DataIOSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.UserTaskExecutionSet;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.forms.processing.engine.handling.ValidationResult.State.ERROR;
import static org.kie.workbench.common.forms.processing.engine.handling.ValidationResult.State.VALID;
import static org.kie.workbench.common.stunner.bpmn.client.forms.fields.multipleInstanceVariableEditor.MultipleInstanceVariableValidator.INPUT_ASSIGNMENT_ALREADY_EXISTS_ERROR;
import static org.kie.workbench.common.stunner.bpmn.client.forms.fields.multipleInstanceVariableEditor.MultipleInstanceVariableValidator.INVALID_VARIABLE_NAME_ERROR;
import static org.kie.workbench.common.stunner.bpmn.client.forms.fields.multipleInstanceVariableEditor.MultipleInstanceVariableValidator.OUTPUT_ASSIGNMENT_ALREADY_EXISTS_ERROR;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MultipleInstanceVariableEditorTest {

    private static final String ASSIGNMENTS_INFO_VALUE = "|input1:String,input2:String||output1:String,output2:String|[din]var1->input1,[din]var2->input2,[dout]output1->var3,[dout]output2->var4";
    private static final String INPUT1 = "input1";
    private static final String OUTPUT1 = "output1";
    private static final String NOT_USED_NAME = "NOT_USED_NAME";
    private static final String ERROR_MESSAGE = "ERROR_MESSAGE";

    @Mock
    private Node<View<BPMNDefinition>, Edge> node;

    @Mock
    private View<BPMNDefinition> view;

    @Mock
    private ClientTranslationService translationService;

    @Test
    public void testValidateUserTaskValueOk() {
        testValidateValueOk(mockUserTask(ASSIGNMENTS_INFO_VALUE));
    }

    @Test
    public void testValidateUserTaskValueAlreadyUsedAsInput() {
        testValidateValueAlreadyUsedAsInput(mockUserTask(ASSIGNMENTS_INFO_VALUE));
    }

    @Test
    public void testValidateUserTaskValueAlreadyUsedAsOutput() {
        testValidateValueAlreadyUsedAsOutput(mockUserTask(ASSIGNMENTS_INFO_VALUE));
    }

    @Test
    public void testValidateUserTaskInvalidValue() {
        testValidateInvalidValue(mockUserTask(ASSIGNMENTS_INFO_VALUE));
    }

    @Test
    public void testValidateReusableSubprocessValueNotUsed() {
        testValidateValueOk(mockReusableSubprocess(ASSIGNMENTS_INFO_VALUE));
    }

    @Test
    public void testValidateReusableSubprocessValueAlreadyUsedAsInput() {
        testValidateValueAlreadyUsedAsInput(mockReusableSubprocess(ASSIGNMENTS_INFO_VALUE));
    }

    @Test
    public void testValidateReusableSubprocessValueAlreadyUsedAsOutput() {
        testValidateValueAlreadyUsedAsOutput(mockReusableSubprocess(ASSIGNMENTS_INFO_VALUE));
    }

    @Test
    public void testValidateReusableSubprocessInvalidValue() {
        testValidateValueAlreadyUsedAsOutput(mockReusableSubprocess(ASSIGNMENTS_INFO_VALUE));
    }

    @Test
    public void testValidateMultipleInstanceSubprocessValueOk() {
        testValidateValueOk(mock(MultipleInstanceSubprocess.class));
    }

    @Test
    public void testValidateMultipleInstanceSubprocessInvalidValue() {
        testValidateInvalidValue(mock(MultipleInstanceSubprocess.class));
    }

    private void testValidateValueOk(BPMNDefinition definition) {
        doTest(definition, NOT_USED_NAME, VALID, "");
    }

    private void testValidateValueAlreadyUsedAsInput(BPMNDefinition definition) {
        when(translationService.getValue(INPUT_ASSIGNMENT_ALREADY_EXISTS_ERROR, INPUT1)).thenReturn(ERROR_MESSAGE);
        doTest(definition, INPUT1, ERROR, ERROR_MESSAGE);
    }

    private void testValidateValueAlreadyUsedAsOutput(BPMNDefinition definition) {
        when(translationService.getValue(OUTPUT_ASSIGNMENT_ALREADY_EXISTS_ERROR, OUTPUT1)).thenReturn(ERROR_MESSAGE);
        doTest(definition, OUTPUT1, ERROR, ERROR_MESSAGE);
    }

    private void testValidateInvalidValue(BPMNDefinition definition) {
        when(translationService.getValue(INVALID_VARIABLE_NAME_ERROR)).thenReturn(ERROR_MESSAGE);
        doTest(definition, "####", ERROR, ERROR_MESSAGE);
    }

    private void doTest(BPMNDefinition definition, String value, ValidationResult.State expectedState, String expectedMessage) {
        when(node.getContent()).thenReturn(view);
        when(view.getDefinition()).thenReturn(definition);
        MultipleInstanceVariableValidator validator = new MultipleInstanceVariableValidator(node, translationService);
        ValidationResult result = validator.validate(value);
        assertEquals(expectedState, result.getStatus());
        assertEquals(expectedMessage, result.getMessage());
    }

    private static UserTask mockUserTask(String assignmentsInfoValue) {
        UserTask result = mock(UserTask.class);
        UserTaskExecutionSet executionSet = mock(UserTaskExecutionSet.class);
        when(result.getExecutionSet()).thenReturn(executionSet);
        AssignmentsInfo assignmentsInfo = mock(AssignmentsInfo.class);
        when(assignmentsInfo.getValue()).thenReturn(assignmentsInfoValue);
        when(executionSet.getAssignmentsinfo()).thenReturn(assignmentsInfo);
        return result;
    }

    private static ReusableSubprocess mockReusableSubprocess(String assignmentsInfoValue) {
        ReusableSubprocess result = mock(ReusableSubprocess.class);
        AssignmentsInfo assignmentsInfo = mock(AssignmentsInfo.class);
        when(assignmentsInfo.getValue()).thenReturn(assignmentsInfoValue);
        DataIOSet dataIOSet = mockIOSet(assignmentsInfo);
        when(result.getDataIOSet()).thenReturn(dataIOSet);
        return result;
    }

    private static DataIOSet mockIOSet(AssignmentsInfo assignmentsInfo) {
        DataIOSet result = mock(DataIOSet.class);
        when(result.getAssignmentsinfo()).thenReturn(assignmentsInfo);
        return result;
    }
}
