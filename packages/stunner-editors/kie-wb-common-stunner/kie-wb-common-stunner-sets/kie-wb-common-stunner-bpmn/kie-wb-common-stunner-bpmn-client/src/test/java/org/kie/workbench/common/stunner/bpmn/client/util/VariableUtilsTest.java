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

package org.kie.workbench.common.stunner.bpmn.client.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagramImpl;
import org.kie.workbench.common.stunner.bpmn.definition.BaseCatchingIntermediateEvent;
import org.kie.workbench.common.stunner.bpmn.definition.BaseEndEvent;
import org.kie.workbench.common.stunner.bpmn.definition.BaseReusableSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.BaseStartEvent;
import org.kie.workbench.common.stunner.bpmn.definition.BaseThrowingIntermediateEvent;
import org.kie.workbench.common.stunner.bpmn.definition.BaseUserTask;
import org.kie.workbench.common.stunner.bpmn.definition.BusinessRuleTask;
import org.kie.workbench.common.stunner.bpmn.definition.EndErrorEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndEscalationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndMessageEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndSignalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateErrorEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateEscalationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateEscalationEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateMessageEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateMessageEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateSignalEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateSignalEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.MultipleInstanceSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.ReusableSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.StartErrorEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartEscalationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartMessageEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartSignalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.UserTask;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.AssignmentsInfo;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.DataIOSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.Id;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.TaskGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.IsMultipleInstance;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceCollectionInput;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceCollectionOutput;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceDataInput;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceDataOutput;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceSubprocessTaskExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ReusableSubprocessTaskExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.UserTaskExecutionSet;
import org.kie.workbench.common.stunner.bpmn.workitem.CustomTask;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.stunner.bpmn.client.util.VariableUtils.FindVariableUsagesFlag;
import static org.kie.workbench.common.stunner.bpmn.client.util.VariableUtils.findVariableUsages;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class VariableUtilsTest {

    private static final String CASE_FILE_VARIABLE_PREFIX = "caseFile_";
    private static final String NODE_NAME = "NODE_NAME";
    private static final String ASSIGNMENTS_INFO_VALUE = "|input1:String,input3:String||output2:String,output3:String|[din]var1->input1,[din]var3->input3,[dout]output2->var2,[dout]output3->var3";
    private static final String ASSIGNMENTS_INFO_VALUE_CFV = "|input1:String,input3:String||output2:String,output3:String|[din]"
            + CASE_FILE_VARIABLE_PREFIX + "var1->input1,[din]" + CASE_FILE_VARIABLE_PREFIX + "var3->input3,[dout]output2->"
            + CASE_FILE_VARIABLE_PREFIX + "var2,[dout]output3->" + CASE_FILE_VARIABLE_PREFIX + "var3";
    private static final String EVENT_INPUT_ASSIGNMENTS_INFO_VALUE = "|input1:String|||[din]var1->input1";
    private static final String EVENT_INPUT_ASSIGNMENTS_INFO_VALUE_CFV = "|input1:String|||[din]" + CASE_FILE_VARIABLE_PREFIX + "var1->input1";
    private static final String EVENT_OUTPUT_ASSIGNMENTS_INFO_VALUE = "||output1:var1||[dout]output1->var1";
    private static final String EVENT_OUTPUT_ASSIGNMENTS_INFO_VALUE_CFV = "||output1:var1||[dout]output1->" + CASE_FILE_VARIABLE_PREFIX + "var1";
    private static final String COLLECTION = "COLLECTION";
    private static final String COLLECTION_CFV = CASE_FILE_VARIABLE_PREFIX + COLLECTION;
    private static final String PROCESS_ID = "process_ID";

    @Mock
    private Graph graph;

    @Test
    public void testFindVariableUsagesForBusinessRuleTask() {
        testFindVariableUsagesForTask(mockBusinessRuleTask(NODE_NAME, ASSIGNMENTS_INFO_VALUE));
    }

    @Test
    public void testFindVariableUsagesForBusinessRuleTask_caseFileVariable() {
        testFindVariableUsagesForTask_caseFileVariable(mockBusinessRuleTask(NODE_NAME, ASSIGNMENTS_INFO_VALUE_CFV));
    }

    @Test
    public void testFindVariableUsagesForUserTask() {
        testFindVariableUsagesForTask(mockUserTask(NODE_NAME, ASSIGNMENTS_INFO_VALUE));
    }

    @Test
    public void testFindVariableUsagesForUserTask_caseFileVariable() {
        testFindVariableUsagesForTask_caseFileVariable(mockUserTask(NODE_NAME, ASSIGNMENTS_INFO_VALUE_CFV));
    }

    @Test
    public void testFindVariableUsagesForUserTaskInputCollection() {
        testFindVariableUsagesForTaskCollections(mockUserTask(NODE_NAME, ASSIGNMENTS_INFO_VALUE, COLLECTION, null), COLLECTION, COLLECTION, null);
    }

    @Test
    public void testFindVariableUsagesForUserTaskInputCollection_caseFileVariable() {
        testFindVariableUsagesForTaskCollections_caseFileVariable(mockUserTask(NODE_NAME, ASSIGNMENTS_INFO_VALUE, COLLECTION_CFV, null), COLLECTION, COLLECTION_CFV, null);
    }

    @Test
    public void testFindVariableUsagesForUserTaskOutputCollection() {
        testFindVariableUsagesForTaskCollections(mockUserTask(NODE_NAME, ASSIGNMENTS_INFO_VALUE, null, COLLECTION), COLLECTION, null, COLLECTION);
    }

    @Test
    public void testFindVariableUsagesForUserTaskOutputCollection_caseFileVariable() {
        testFindVariableUsagesForTaskCollections_caseFileVariable(mockUserTask(NODE_NAME, ASSIGNMENTS_INFO_VALUE, null, COLLECTION_CFV), COLLECTION, null, COLLECTION_CFV);
    }

    @Test
    public void testFindVariableUsagesForUserTaskInputOutputCollection() {
        testFindVariableUsagesForTaskCollections(mockUserTask(NODE_NAME, ASSIGNMENTS_INFO_VALUE, COLLECTION, COLLECTION), COLLECTION, COLLECTION, COLLECTION);
    }

    @Test
    public void testFindVariableUsagesForUserTaskInputOutputCollection_caseFileVariable() {
        testFindVariableUsagesForTaskCollections_caseFileVariable(mockUserTask(NODE_NAME, ASSIGNMENTS_INFO_VALUE, COLLECTION_CFV, COLLECTION_CFV), COLLECTION, COLLECTION_CFV, COLLECTION_CFV);
    }

    @Test
    public void testFindVariableUsagesForServiceTask() {
        testFindVariableUsagesForTask(mockServiceTask(NODE_NAME, ASSIGNMENTS_INFO_VALUE));
    }

    @Test
    public void testFindVariableUsagesForServiceTask_caseFileVariable() {
        testFindVariableUsagesForTask_caseFileVariable(mockServiceTask(NODE_NAME, ASSIGNMENTS_INFO_VALUE_CFV));
    }

    @Test
    public void testFindVariableUsagesForEndErrorEvent() {
        testFindVariableUsagesForEventWithInput(mockEndErrorEvent(NODE_NAME, EVENT_INPUT_ASSIGNMENTS_INFO_VALUE));
    }

    @Test
    public void testFindVariableUsagesForEndErrorEvent_caseFileVariable() {
        testFindVariableUsagesForEventWithInput_caseFileVariable(mockEndErrorEvent(NODE_NAME, EVENT_INPUT_ASSIGNMENTS_INFO_VALUE_CFV));
    }

    @Test
    public void testFindVariableUsagesForEndEscalationEvent() {
        testFindVariableUsagesForEventWithInput(mockEndEscalationEvent(NODE_NAME, EVENT_INPUT_ASSIGNMENTS_INFO_VALUE));
    }

    @Test
    public void testFindVariableUsagesForEndEscalationEvent_caseFileVariable() {
        testFindVariableUsagesForEventWithInput_caseFileVariable(mockEndEscalationEvent(NODE_NAME, EVENT_INPUT_ASSIGNMENTS_INFO_VALUE_CFV));
    }

    @Test
    public void testFindVariableUsagesForEndMessageEvent() {
        testFindVariableUsagesForEventWithInput(mockEndMessageEvent(NODE_NAME, EVENT_INPUT_ASSIGNMENTS_INFO_VALUE));
    }

    @Test
    public void testFindVariableUsagesForEndMessageEvent_caseFileVariable() {
        testFindVariableUsagesForEventWithInput_caseFileVariable(mockEndMessageEvent(NODE_NAME, EVENT_INPUT_ASSIGNMENTS_INFO_VALUE_CFV));
    }

    @Test
    public void testFindVariableUsagesForEndSignalEvent() {
        testFindVariableUsagesForEventWithInput(mockEndSignalEvent(NODE_NAME, EVENT_INPUT_ASSIGNMENTS_INFO_VALUE));
    }

    @Test
    public void testFindVariableUsagesForEndSignalEvent_caseFileVariable() {
        testFindVariableUsagesForEventWithInput_caseFileVariable(mockEndSignalEvent(NODE_NAME, EVENT_INPUT_ASSIGNMENTS_INFO_VALUE_CFV));
    }

    private void testFindVariableUsagesForEventWithInput(Object event) {
        List<Node> nodes = mockNodeList(event);
        List<VariableUsage> expectedUsages = Arrays.asList(new VariableUsage("var1", VariableUsage.USAGE_TYPE.INPUT_VARIABLE, nodes.get(0), NODE_NAME));
        testFindVariableUsages("var1", nodes, expectedUsages);
    }

    private void testFindVariableUsagesForEventWithInput_caseFileVariable(Object event) {
        List<Node> nodes = mockNodeList(event);
        List<VariableUsage> expectedUsages = Arrays.asList(new VariableUsage(CASE_FILE_VARIABLE_PREFIX + "var1", VariableUsage.USAGE_TYPE.INPUT_VARIABLE, nodes.get(0), NODE_NAME));
        testFindVariableUsages_caseFileVariable("var1", nodes, expectedUsages);
    }

    @Test
    public void testFindVariableUsagesForIntermediateErrorEventCatching() {
        testFindVariableUsagesForEventWithOutput(mockIntermediateErrorEventCatching(NODE_NAME, EVENT_OUTPUT_ASSIGNMENTS_INFO_VALUE));
    }

    @Test
    public void testFindVariableUsagesForIntermediateErrorEventCatching_caseFileVariable() {
        testFindVariableUsagesForEventWithOutput_caseFileVariable(mockIntermediateErrorEventCatching(NODE_NAME, EVENT_OUTPUT_ASSIGNMENTS_INFO_VALUE_CFV));
    }

    @Test
    public void testFindVariableUsagesForIntermediateMessageEventCatching() {
        testFindVariableUsagesForEventWithOutput(mockIntermediateMessageEventCatching(NODE_NAME, EVENT_OUTPUT_ASSIGNMENTS_INFO_VALUE));
    }

    @Test
    public void testFindVariableUsagesForIntermediateMessageEventCatching_caseFileVariable() {
        testFindVariableUsagesForEventWithOutput_caseFileVariable(mockIntermediateMessageEventCatching(NODE_NAME, EVENT_OUTPUT_ASSIGNMENTS_INFO_VALUE_CFV));
    }

    @Test
    public void testFindVariableUsagesForIntermediateSignalEventCatching() {
        testFindVariableUsagesForEventWithOutput(mockIntermediateSignalEventCatching(NODE_NAME, EVENT_OUTPUT_ASSIGNMENTS_INFO_VALUE));
    }

    @Test
    public void testFindVariableUsagesForIntermediateSignalEventCatching_caseFileVariable() {
        testFindVariableUsagesForEventWithOutput_caseFileVariable(mockIntermediateSignalEventCatching(NODE_NAME, EVENT_OUTPUT_ASSIGNMENTS_INFO_VALUE_CFV));
    }

    @Test
    public void testFindVariableUsagesForIntermediateEscalationEvent() {
        testFindVariableUsagesForEventWithOutput(mockIntermediateEscalationEvent(NODE_NAME, EVENT_OUTPUT_ASSIGNMENTS_INFO_VALUE));
    }

    @Test
    public void testFindVariableUsagesForIntermediateEscalationEvent_caseFileVariable() {
        testFindVariableUsagesForEventWithOutput_caseFileVariable(mockIntermediateEscalationEvent(NODE_NAME, EVENT_OUTPUT_ASSIGNMENTS_INFO_VALUE_CFV));
    }

    @Test
    public void testFindVariableUsagesForIntermediateEscalationEventThrowing() {
        testFindVariableUsagesForEventWithInput(mockIntermediateEscalationEventThrowing(NODE_NAME, EVENT_INPUT_ASSIGNMENTS_INFO_VALUE));
    }

    @Test
    public void testFindVariableUsagesForIntermediateEscalationEventThrowing_caseFileVariable() {
        testFindVariableUsagesForEventWithInput_caseFileVariable(mockIntermediateEscalationEventThrowing(NODE_NAME, EVENT_INPUT_ASSIGNMENTS_INFO_VALUE_CFV));
    }

    @Test
    public void testFindVariableUsagesForIntermediateMessageEventThrowing() {
        testFindVariableUsagesForEventWithInput(mockIntermediateMessageEventThrowing(NODE_NAME, EVENT_INPUT_ASSIGNMENTS_INFO_VALUE));
    }

    @Test
    public void testFindVariableUsagesForIntermediateMessageEventThrowing_caseFileVariable() {
        testFindVariableUsagesForEventWithInput_caseFileVariable(mockIntermediateMessageEventThrowing(NODE_NAME, EVENT_INPUT_ASSIGNMENTS_INFO_VALUE_CFV));
    }

    @Test
    public void testFindVariableUsagesForIntermediateSignalEventThrowing() {
        testFindVariableUsagesForEventWithInput(mockIntermediateSignalEventThrowing(NODE_NAME, EVENT_INPUT_ASSIGNMENTS_INFO_VALUE));
    }

    @Test
    public void testFindVariableUsagesForIntermediateSignalEventThrowing_caseFileVariable() {
        testFindVariableUsagesForEventWithInput_caseFileVariable(mockIntermediateSignalEventThrowing(NODE_NAME, EVENT_INPUT_ASSIGNMENTS_INFO_VALUE_CFV));
    }

    @Test
    public void testFindVariableUsagesForStartErrorEvent() {
        testFindVariableUsagesForEventWithOutput(mockStartErrorEvent(NODE_NAME, EVENT_OUTPUT_ASSIGNMENTS_INFO_VALUE));
    }

    @Test
    public void testFindVariableUsagesForStartErrorEvent_caseFileVariable() {
        testFindVariableUsagesForEventWithOutput_caseFileVariable(mockStartErrorEvent(NODE_NAME, EVENT_OUTPUT_ASSIGNMENTS_INFO_VALUE_CFV));
    }

    @Test
    public void testFindVariableUsagesForStartEscalationEvent() {
        testFindVariableUsagesForEventWithOutput(mockStartEscalationEvent(NODE_NAME, EVENT_OUTPUT_ASSIGNMENTS_INFO_VALUE));
    }

    @Test
    public void testFindVariableUsagesForStartEscalationEvent_caseFileVariable() {
        testFindVariableUsagesForEventWithOutput_caseFileVariable(mockStartEscalationEvent(NODE_NAME, EVENT_OUTPUT_ASSIGNMENTS_INFO_VALUE_CFV));
    }

    @Test
    public void testFindVariableUsagesForStartMessageEvent() {
        testFindVariableUsagesForEventWithOutput(mockStartMessageEvent(NODE_NAME, EVENT_OUTPUT_ASSIGNMENTS_INFO_VALUE));
    }

    @Test
    public void testFindVariableUsagesForStartMessageEvent_caseFileVariable() {
        testFindVariableUsagesForEventWithOutput_caseFileVariable(mockStartMessageEvent(NODE_NAME, EVENT_OUTPUT_ASSIGNMENTS_INFO_VALUE_CFV));
    }

    @Test
    public void testFindVariableUsagesForStartSignalEvent() {
        testFindVariableUsagesForEventWithOutput(mockStartSignalEvent(NODE_NAME, EVENT_OUTPUT_ASSIGNMENTS_INFO_VALUE));
    }

    @Test
    public void testFindVariableUsagesForStartSignalEvent_caseFileVariable() {
        testFindVariableUsagesForEventWithOutput_caseFileVariable(mockStartSignalEvent(NODE_NAME, EVENT_OUTPUT_ASSIGNMENTS_INFO_VALUE_CFV));
    }

    private void testFindVariableUsagesForEventWithOutput(Object event) {
        List<Node> nodes = mockNodeList(event);
        List<VariableUsage> expectedUsages = Arrays.asList(new VariableUsage("var1", VariableUsage.USAGE_TYPE.OUTPUT_VARIABLE, nodes.get(0), NODE_NAME));
        testFindVariableUsages("var1", nodes, expectedUsages);
    }

    private void testFindVariableUsagesForEventWithOutput_caseFileVariable(Object event) {
        List<Node> nodes = mockNodeList(event);
        List<VariableUsage> expectedUsages = Arrays.asList(new VariableUsage(CASE_FILE_VARIABLE_PREFIX + "var1", VariableUsage.USAGE_TYPE.OUTPUT_VARIABLE, nodes.get(0), NODE_NAME));
        testFindVariableUsages_caseFileVariable("var1", nodes, expectedUsages);
    }

    @Test
    public void testFindVariableUsagesForReusableSubprocess() {
        testFindVariableUsagesForTask(mockReusableSubprocess(NODE_NAME, ASSIGNMENTS_INFO_VALUE));
    }

    @Test
    public void testFindVariableUsagesForReusableSubprocess_caseFileVariable() {
        testFindVariableUsagesForTask_caseFileVariable(mockReusableSubprocess(NODE_NAME, ASSIGNMENTS_INFO_VALUE_CFV));
    }

    @Test
    public void testFindVariableUsagesForReusableSubprocessInputCollection() {
        testFindVariableUsagesForTaskCollections(mockReusableSubprocess(NODE_NAME, ASSIGNMENTS_INFO_VALUE, COLLECTION, null), COLLECTION, COLLECTION, null);
    }

    @Test
    public void testFindVariableUsagesForReusableSubprocessInputCollection_caseFileVariable() {
        testFindVariableUsagesForTaskCollections_caseFileVariable(mockReusableSubprocess(NODE_NAME, ASSIGNMENTS_INFO_VALUE, COLLECTION_CFV, null), COLLECTION, COLLECTION_CFV, null);
    }

    @Test
    public void testFindVariableUsagesForReusableSubprocessOutputCollection() {
        testFindVariableUsagesForTaskCollections(mockReusableSubprocess(NODE_NAME, ASSIGNMENTS_INFO_VALUE, null, COLLECTION), COLLECTION, null, COLLECTION);
    }

    @Test
    public void testFindVariableUsagesForReusableSubprocessOutputCollection_caseFileVariable() {
        testFindVariableUsagesForTaskCollections_caseFileVariable(mockReusableSubprocess(NODE_NAME, ASSIGNMENTS_INFO_VALUE, null, COLLECTION_CFV), COLLECTION, null, COLLECTION_CFV);
    }

    @Test
    public void testFindVariableUsagesForReusableSuprocessTaskInputOutputCollection() {
        testFindVariableUsagesForTaskCollections(mockReusableSubprocess(NODE_NAME, ASSIGNMENTS_INFO_VALUE, COLLECTION, COLLECTION), COLLECTION, COLLECTION, COLLECTION);
    }

    @Test
    public void testFindVariableUsagesForReusableSuprocessTaskInputOutputCollection_caseFileVariable() {
        testFindVariableUsagesForTaskCollections_caseFileVariable(mockReusableSubprocess(NODE_NAME, ASSIGNMENTS_INFO_VALUE, COLLECTION_CFV, COLLECTION_CFV), COLLECTION, COLLECTION_CFV, COLLECTION_CFV);
    }

    @Test
    public void testFindVariableUsagesForMultipleInstanceSubProcessWithOnlyInput() {
        List<Node> nodes = mockNodeList(mockMultipleInstanceSubprocess(NODE_NAME, "var1", null));
        List<VariableUsage> expectedUsages = Arrays.asList(new VariableUsage("var1", VariableUsage.USAGE_TYPE.MULTIPLE_INSTANCE_INPUT_COLLECTION, nodes.get(0), NODE_NAME));
        testFindVariableUsages("var1", nodes, expectedUsages);
    }

    @Test
    public void testFindVariableUsagesForMultipleInstanceSubProcessWithOnlyInput_caseFileVariable() {
        List<Node> nodes = mockNodeList(mockMultipleInstanceSubprocess(NODE_NAME, CASE_FILE_VARIABLE_PREFIX + "var1", null));
        List<VariableUsage> expectedUsages = Arrays.asList(new VariableUsage(CASE_FILE_VARIABLE_PREFIX + "var1", VariableUsage.USAGE_TYPE.MULTIPLE_INSTANCE_INPUT_COLLECTION, nodes.get(0), NODE_NAME));
        testFindVariableUsages_caseFileVariable("var1", nodes, expectedUsages);
    }

    @Test
    public void testFindVariableUsagesForMultipleInstanceSubProcessWithOnlyOutput() {
        List<Node> nodes = mockNodeList(mockMultipleInstanceSubprocess(NODE_NAME, null, "var1"));
        List<VariableUsage> expectedUsages = Arrays.asList(new VariableUsage("var1", VariableUsage.USAGE_TYPE.MULTIPLE_INSTANCE_OUTPUT_COLLECTION, nodes.get(0), NODE_NAME));
        testFindVariableUsages("var1", nodes, expectedUsages);
    }

    @Test
    public void testFindVariableUsagesForMultipleInstanceSubProcessWithOnlyOutput_caseFileVariable() {
        List<Node> nodes = mockNodeList(mockMultipleInstanceSubprocess(NODE_NAME, null, CASE_FILE_VARIABLE_PREFIX + "var1"));
        List<VariableUsage> expectedUsages = Arrays.asList(new VariableUsage(CASE_FILE_VARIABLE_PREFIX + "var1", VariableUsage.USAGE_TYPE.MULTIPLE_INSTANCE_OUTPUT_COLLECTION, nodes.get(0), NODE_NAME));
        testFindVariableUsages_caseFileVariable("var1", nodes, expectedUsages);
    }

    @Test
    public void testFindVariableUsagesForMultipleInstanceSubProcessWithInputAndOutput() {
        List<Node> nodes = mockNodeList(mockMultipleInstanceSubprocess(NODE_NAME, "var1", "var1"));
        List<VariableUsage> expectedUsages = Arrays.asList(new VariableUsage("var1", VariableUsage.USAGE_TYPE.MULTIPLE_INSTANCE_INPUT_COLLECTION, nodes.get(0), NODE_NAME),
                                                           new VariableUsage("var1", VariableUsage.USAGE_TYPE.MULTIPLE_INSTANCE_OUTPUT_COLLECTION, nodes.get(0), NODE_NAME));
        testFindVariableUsages("var1", nodes, expectedUsages);
    }

    @Test
    public void testFindVariableUsagesForMultipleInstanceSubProcessWithInputAndOutput_caseFileVariable() {
        List<Node> nodes = mockNodeList(mockMultipleInstanceSubprocess(NODE_NAME, CASE_FILE_VARIABLE_PREFIX + "var1", CASE_FILE_VARIABLE_PREFIX + "var1"));
        List<VariableUsage> expectedUsages = Arrays.asList(new VariableUsage(CASE_FILE_VARIABLE_PREFIX + "var1", VariableUsage.USAGE_TYPE.MULTIPLE_INSTANCE_INPUT_COLLECTION, nodes.get(0), NODE_NAME),
                                                           new VariableUsage(CASE_FILE_VARIABLE_PREFIX + "var1", VariableUsage.USAGE_TYPE.MULTIPLE_INSTANCE_OUTPUT_COLLECTION, nodes.get(0), NODE_NAME));
        testFindVariableUsages_caseFileVariable("var1", nodes, expectedUsages);
    }

    @Test
    public void testMatchesProcessID() {
        BPMNDiagramImpl bpmnDiagram = mockBpmnDiagram();
        List<Node> nodes = mockNodeList(bpmnDiagram);
        when(graph.nodes()).thenReturn(nodes);
        boolean result1 = VariableUtils.matchesProcessID(graph, PROCESS_ID);
        boolean result2 = VariableUtils.matchesProcessID(graph, "NOT_PROCESS_ID");
        assertTrue(result1);
        assertFalse(result2);
    }

    @Test
    public void testIsBPMNDiagramImpl() {
        BPMNDiagramImpl bpmnDiagram = mockBpmnDiagram();
        BaseUserTask userTask = mockUserTask(NODE_NAME, ASSIGNMENTS_INFO_VALUE);

        List<Node> nodes1 = mockNodeList(bpmnDiagram);
        List<Node> nodes2 = mockNodeList(userTask);

        boolean result1 = VariableUtils.isBPMNDiagramImpl(nodes1.get(0));
        boolean result2 = VariableUtils.isBPMNDiagramImpl(nodes2.get(0));

        assertTrue(result1);
        assertFalse(result2);
    }

    private void testFindVariableUsages(String variableName, List<Node> nodes, List<VariableUsage> expectedUsages) {
        when(graph.nodes()).thenReturn(nodes);
        Collection<VariableUsage> result = findVariableUsages(graph, variableName, EnumSet.noneOf(FindVariableUsagesFlag.class));
        assertEquals(expectedUsages, result);
    }

    private void testFindVariableUsages_caseFileVariable(String variableName, List<Node> nodes, List<VariableUsage> expectedUsages) {
        when(graph.nodes()).thenReturn(nodes);
        Collection<VariableUsage> result = findVariableUsages(graph, variableName, EnumSet.of(FindVariableUsagesFlag.CASE_FILE_VARIABLE));
        assertEquals(expectedUsages, result);
    }

    private void testFindVariableUsagesForTask(Object task) {
        List<Node> nodes = mockNodeList(task);
        List<VariableUsage> expectedUsages = Arrays.asList(new VariableUsage("var1", VariableUsage.USAGE_TYPE.INPUT_VARIABLE, nodes.get(0), NODE_NAME));
        testFindVariableUsages("var1", nodes, expectedUsages);
        expectedUsages = Arrays.asList(new VariableUsage("var2", VariableUsage.USAGE_TYPE.OUTPUT_VARIABLE, nodes.get(0), NODE_NAME));
        testFindVariableUsages("var2", nodes, expectedUsages);
        expectedUsages = Arrays.asList(new VariableUsage("var3", VariableUsage.USAGE_TYPE.INPUT_OUTPUT_VARIABLE, nodes.get(0), NODE_NAME));
        testFindVariableUsages("var3", nodes, expectedUsages);
    }

    private void testFindVariableUsagesForTask_caseFileVariable(Object task) {
        List<Node> nodes = mockNodeList(task);
        List<VariableUsage> expectedUsages = Arrays.asList(new VariableUsage(CASE_FILE_VARIABLE_PREFIX + "var1", VariableUsage.USAGE_TYPE.INPUT_VARIABLE, nodes.get(0), NODE_NAME));
        testFindVariableUsages_caseFileVariable("var1", nodes, expectedUsages);
        expectedUsages = Arrays.asList(new VariableUsage(CASE_FILE_VARIABLE_PREFIX + "var2", VariableUsage.USAGE_TYPE.OUTPUT_VARIABLE, nodes.get(0), NODE_NAME));
        testFindVariableUsages_caseFileVariable("var2", nodes, expectedUsages);
        expectedUsages = Arrays.asList(new VariableUsage(CASE_FILE_VARIABLE_PREFIX + "var3", VariableUsage.USAGE_TYPE.INPUT_OUTPUT_VARIABLE, nodes.get(0), NODE_NAME));
        testFindVariableUsages_caseFileVariable("var3", nodes, expectedUsages);
    }

    private void testFindVariableUsagesForTaskCollections(Object task, String var, String inputCollection, String outputCollection) {
        List<Node> nodes = mockNodeList(task);
        List<VariableUsage> expectedUsages = new ArrayList<>();
        addInputOutputCollections(expectedUsages, inputCollection, outputCollection, nodes.get(0));
        testFindVariableUsages(var, nodes, expectedUsages);
    }

    private void testFindVariableUsagesForTaskCollections_caseFileVariable(Object task, String var, String inputCollection, String outputCollection) {
        List<Node> nodes = mockNodeList(task);
        List<VariableUsage> expectedUsages = new ArrayList<>();
        addInputOutputCollections(expectedUsages, inputCollection, outputCollection, nodes.get(0));
        testFindVariableUsages_caseFileVariable(var, nodes, expectedUsages);
    }

    private void addInputOutputCollections(List<VariableUsage> expectedResult, String inputCollection, String outputCollection, Node node) {
        if (inputCollection != null) {
            expectedResult.add(new VariableUsage(inputCollection, VariableUsage.USAGE_TYPE.MULTIPLE_INSTANCE_INPUT_COLLECTION, node, NODE_NAME));
        }
        if (outputCollection != null) {
            expectedResult.add(new VariableUsage(outputCollection, VariableUsage.USAGE_TYPE.MULTIPLE_INSTANCE_OUTPUT_COLLECTION, node, NODE_NAME));
        }
    }

    private List<Node> mockNodeList(Object... contents) {
        List<Node> result = new ArrayList<>();
        View view;
        Node node;
        for (Object content : contents) {
            node = mock(Node.class);
            view = mock(View.class);
            when(node.getContent()).thenReturn(view);
            when(view.getDefinition()).thenReturn(content);
            result.add(node);
        }
        return result;
    }

    private BaseUserTask mockUserTask(String name, String assignmentsInfoValue) {
        return mockUserTask(name, assignmentsInfoValue, null, null);
    }

    private BaseUserTask mockUserTask(String name, String assignmentsInfoValue, String inputCollection, String outputCollection) {
        UserTask result = mock(UserTask.class);
        TaskGeneralSet generalSet = mockTaskGeneralSet(name);
        when(result.getGeneral()).thenReturn(generalSet);
        UserTaskExecutionSet executionSet = mock(UserTaskExecutionSet.class);
        when(result.getExecutionSet()).thenReturn(executionSet);
        AssignmentsInfo assignmentsInfo = mockAssignmentsInfo(assignmentsInfoValue);
        when(executionSet.getAssignmentsinfo()).thenReturn(assignmentsInfo);
        IsMultipleInstance isMultipleInstance = mock(IsMultipleInstance.class);
        when(isMultipleInstance.getValue()).thenReturn(true);
        MultipleInstanceCollectionInput miInputCollection = mock(MultipleInstanceCollectionInput.class);
        when(miInputCollection.getValue()).thenReturn(inputCollection);
        MultipleInstanceDataInput miDataInput = mock(MultipleInstanceDataInput.class);
        MultipleInstanceCollectionOutput miOutputCollection = mock(MultipleInstanceCollectionOutput.class);
        MultipleInstanceDataOutput miDataOutput = mock(MultipleInstanceDataOutput.class);
        when(miOutputCollection.getValue()).thenReturn(outputCollection);
        when(executionSet.getIsMultipleInstance()).thenReturn(isMultipleInstance);
        when(executionSet.getMultipleInstanceCollectionInput()).thenReturn(miInputCollection);
        when(executionSet.getMultipleInstanceDataInput()).thenReturn(miDataInput);
        when(executionSet.getMultipleInstanceCollectionOutput()).thenReturn(miOutputCollection);
        when(executionSet.getMultipleInstanceDataOutput()).thenReturn(miDataOutput);
        return result;
    }

    private BusinessRuleTask mockBusinessRuleTask(String name, String assignmentsInfoValue) {
        BusinessRuleTask result = mock(BusinessRuleTask.class);
        TaskGeneralSet generalSet = mockTaskGeneralSet(name);
        when(result.getGeneral()).thenReturn(generalSet);
        AssignmentsInfo assignmentsInfo = mockAssignmentsInfo(assignmentsInfoValue);
        DataIOSet dataIOSet = mockIOSet(assignmentsInfo);
        when(result.getDataIOSet()).thenReturn(dataIOSet);
        return result;
    }

    private CustomTask mockServiceTask(String name, String assignmentsInfoValue) {
        CustomTask result = mock(CustomTask.class);
        TaskGeneralSet generalSet = mockTaskGeneralSet(name);
        when(result.getGeneral()).thenReturn(generalSet);
        AssignmentsInfo assignmentsInfo = mockAssignmentsInfo(assignmentsInfoValue);
        DataIOSet dataIOSet = mockIOSet(assignmentsInfo);
        when(result.getDataIOSet()).thenReturn(dataIOSet);
        return result;
    }

    private EndErrorEvent mockEndErrorEvent(String name, String assignmentsInfoValue) {
        EndErrorEvent result = mockEndEvent(name, EndErrorEvent.class);
        AssignmentsInfo assignmentsInfo = mockAssignmentsInfo(assignmentsInfoValue);
        DataIOSet dataIOSet = mockIOSet(assignmentsInfo);
        when(result.getDataIOSet()).thenReturn(dataIOSet);
        return result;
    }

    private EndEscalationEvent mockEndEscalationEvent(String name, String assignmentsInfoValue) {
        EndEscalationEvent result = mockEndEvent(name, EndEscalationEvent.class);
        AssignmentsInfo assignmentsInfo = mockAssignmentsInfo(assignmentsInfoValue);
        DataIOSet dataIOSet = mockIOSet(assignmentsInfo);
        when(result.getDataIOSet()).thenReturn(dataIOSet);
        return result;
    }

    private EndMessageEvent mockEndMessageEvent(String name, String assignmentsInfoValue) {
        EndMessageEvent result = mockEndEvent(name, EndMessageEvent.class);
        AssignmentsInfo assignmentsInfo = mockAssignmentsInfo(assignmentsInfoValue);
        DataIOSet dataIOSet = mockIOSet(assignmentsInfo);
        when(result.getDataIOSet()).thenReturn(dataIOSet);
        return result;
    }

    private EndSignalEvent mockEndSignalEvent(String name, String assignmentsInfoValue) {
        EndSignalEvent result = mockEndEvent(name, EndSignalEvent.class);
        AssignmentsInfo assignmentsInfo = mockAssignmentsInfo(assignmentsInfoValue);
        DataIOSet dataIOSet = mockIOSet(assignmentsInfo);
        when(result.getDataIOSet()).thenReturn(dataIOSet);
        return result;
    }

    private <T extends BaseEndEvent> T mockEndEvent(String name, Class<T> clazz) {
        T result = mock(clazz);
        BPMNGeneralSet generalSet = mockGeneralSet(name);
        when(result.getGeneral()).thenReturn(generalSet);
        return result;
    }

    private IntermediateErrorEventCatching mockIntermediateErrorEventCatching(String name, String assignmentsInfoValue) {
        IntermediateErrorEventCatching result = mockCatchingEvent(name, IntermediateErrorEventCatching.class);
        AssignmentsInfo assignmentsInfo = mockAssignmentsInfo(assignmentsInfoValue);
        DataIOSet dataIOSet = mockIOSet(assignmentsInfo);
        when(result.getDataIOSet()).thenReturn(dataIOSet);
        return result;
    }

    private IntermediateMessageEventCatching mockIntermediateMessageEventCatching(String name, String assignmentsInfoValue) {
        IntermediateMessageEventCatching result = mockCatchingEvent(name, IntermediateMessageEventCatching.class);
        AssignmentsInfo assignmentsInfo = mockAssignmentsInfo(assignmentsInfoValue);
        DataIOSet dataIOSet = mockIOSet(assignmentsInfo);
        when(result.getDataIOSet()).thenReturn(dataIOSet);
        return result;
    }

    private IntermediateSignalEventCatching mockIntermediateSignalEventCatching(String name, String assignmentsInfoValue) {
        IntermediateSignalEventCatching result = mockCatchingEvent(name, IntermediateSignalEventCatching.class);
        AssignmentsInfo assignmentsInfo = mockAssignmentsInfo(assignmentsInfoValue);
        DataIOSet dataIOSet = mockIOSet(assignmentsInfo);
        when(result.getDataIOSet()).thenReturn(dataIOSet);
        return result;
    }

    private IntermediateEscalationEvent mockIntermediateEscalationEvent(String name, String assignmentsInfoValue) {
        IntermediateEscalationEvent result = mockCatchingEvent(name, IntermediateEscalationEvent.class);
        AssignmentsInfo assignmentsInfo = mockAssignmentsInfo(assignmentsInfoValue);
        DataIOSet dataIOSet = mockIOSet(assignmentsInfo);
        when(result.getDataIOSet()).thenReturn(dataIOSet);
        return result;
    }

    private <T extends BaseCatchingIntermediateEvent> T mockCatchingEvent(String name, Class<T> clazz) {
        T result = mock(clazz);
        BPMNGeneralSet generalSet = mockGeneralSet(name);
        when(result.getGeneral()).thenReturn(generalSet);
        return result;
    }

    private IntermediateEscalationEventThrowing mockIntermediateEscalationEventThrowing(String name, String assignmentsInfoValue) {
        IntermediateEscalationEventThrowing result = mockThrowingEvent(name, IntermediateEscalationEventThrowing.class);
        AssignmentsInfo assignmentsInfo = mockAssignmentsInfo(assignmentsInfoValue);
        DataIOSet dataIOSet = mockIOSet(assignmentsInfo);
        when(result.getDataIOSet()).thenReturn(dataIOSet);
        return result;
    }

    private IntermediateMessageEventThrowing mockIntermediateMessageEventThrowing(String name, String assignmentsInfoValue) {
        IntermediateMessageEventThrowing result = mockThrowingEvent(name, IntermediateMessageEventThrowing.class);
        AssignmentsInfo assignmentsInfo = mockAssignmentsInfo(assignmentsInfoValue);
        DataIOSet dataIOSet = mockIOSet(assignmentsInfo);
        when(result.getDataIOSet()).thenReturn(dataIOSet);
        return result;
    }

    private IntermediateSignalEventThrowing mockIntermediateSignalEventThrowing(String name, String assignmentsInfoValue) {
        IntermediateSignalEventThrowing result = mockThrowingEvent(name, IntermediateSignalEventThrowing.class);
        AssignmentsInfo assignmentsInfo = mockAssignmentsInfo(assignmentsInfoValue);
        DataIOSet dataIOSet = mockIOSet(assignmentsInfo);
        when(result.getDataIOSet()).thenReturn(dataIOSet);
        return result;
    }

    private <T extends BaseThrowingIntermediateEvent> T mockThrowingEvent(String name, Class<T> clazz) {
        T result = mock(clazz);
        BPMNGeneralSet generalSet = mockGeneralSet(name);
        when(result.getGeneral()).thenReturn(generalSet);
        return result;
    }

    private StartErrorEvent mockStartErrorEvent(String name, String assignmentsInfoValue) {
        StartErrorEvent result = mockStartEvent(name, StartErrorEvent.class);
        AssignmentsInfo assignmentsInfo = mockAssignmentsInfo(assignmentsInfoValue);
        DataIOSet dataIOSet = mockIOSet(assignmentsInfo);
        when(result.getDataIOSet()).thenReturn(dataIOSet);
        return result;
    }

    private StartEscalationEvent mockStartEscalationEvent(String name, String assignmentsInfoValue) {
        StartEscalationEvent result = mockStartEvent(name, StartEscalationEvent.class);
        AssignmentsInfo assignmentsInfo = mockAssignmentsInfo(assignmentsInfoValue);
        DataIOSet dataIOSet = mockIOSet(assignmentsInfo);
        when(result.getDataIOSet()).thenReturn(dataIOSet);
        return result;
    }

    private StartMessageEvent mockStartMessageEvent(String name, String assignmentsInfoValue) {
        StartMessageEvent result = mockStartEvent(name, StartMessageEvent.class);
        AssignmentsInfo assignmentsInfo = mockAssignmentsInfo(assignmentsInfoValue);
        DataIOSet dataIOSet = mockIOSet(assignmentsInfo);
        when(result.getDataIOSet()).thenReturn(dataIOSet);
        return result;
    }

    private StartSignalEvent mockStartSignalEvent(String name, String assignmentsInfoValue) {
        StartSignalEvent result = mockStartEvent(name, StartSignalEvent.class);
        AssignmentsInfo assignmentsInfo = mockAssignmentsInfo(assignmentsInfoValue);
        DataIOSet dataIOSet = mockIOSet(assignmentsInfo);
        when(result.getDataIOSet()).thenReturn(dataIOSet);
        return result;
    }

    private <T extends BaseStartEvent> T mockStartEvent(String name, Class<T> clazz) {
        T result = mock(clazz);
        BPMNGeneralSet generalSet = mockGeneralSet(name);
        when(result.getGeneral()).thenReturn(generalSet);
        return result;
    }

    private BaseReusableSubprocess mockReusableSubprocess(String name, String assignmentsInfoValue) {
        return mockReusableSubprocess(name, assignmentsInfoValue, null, null);
    }

    private BaseReusableSubprocess mockReusableSubprocess(String name, String assignmentsInfoValue, String inputCollection, String outputCollection) {
        ReusableSubprocess result = mock(ReusableSubprocess.class);
        BPMNGeneralSet generalSet = mockGeneralSet(name);
        when(result.getGeneral()).thenReturn(generalSet);
        AssignmentsInfo assignmentsInfo = mockAssignmentsInfo(assignmentsInfoValue);
        DataIOSet dataIOSet = mockIOSet(assignmentsInfo);
        when(result.getDataIOSet()).thenReturn(dataIOSet);
        ReusableSubprocessTaskExecutionSet executionSet = mock(ReusableSubprocessTaskExecutionSet.class);
        when(result.getExecutionSet()).thenReturn(executionSet);
        IsMultipleInstance isMultipleInstance = mock(IsMultipleInstance.class);
        when(isMultipleInstance.getValue()).thenReturn(true);
        MultipleInstanceCollectionInput miInputCollection = mock(MultipleInstanceCollectionInput.class);
        when(miInputCollection.getValue()).thenReturn(inputCollection);
        MultipleInstanceDataInput miDataInput = mock(MultipleInstanceDataInput.class);
        MultipleInstanceCollectionOutput miOutputCollection = mock(MultipleInstanceCollectionOutput.class);
        MultipleInstanceDataOutput miDataOutput = mock(MultipleInstanceDataOutput.class);
        when(miOutputCollection.getValue()).thenReturn(outputCollection);
        when(executionSet.getIsMultipleInstance()).thenReturn(isMultipleInstance);
        when(executionSet.getMultipleInstanceCollectionInput()).thenReturn(miInputCollection);
        when(executionSet.getMultipleInstanceDataInput()).thenReturn(miDataInput);
        when(executionSet.getMultipleInstanceCollectionOutput()).thenReturn(miOutputCollection);
        when(executionSet.getMultipleInstanceDataOutput()).thenReturn(miDataOutput);
        return result;
    }

    private MultipleInstanceSubprocess mockMultipleInstanceSubprocess(String name, String inputVariable, String outputVariable) {
        MultipleInstanceSubprocess result = mock(MultipleInstanceSubprocess.class);
        BPMNGeneralSet generalSet = mockGeneralSet(name);
        when(result.getGeneral()).thenReturn(generalSet);
        MultipleInstanceSubprocessTaskExecutionSet executionSet = mock(MultipleInstanceSubprocessTaskExecutionSet.class);
        when(result.getExecutionSet()).thenReturn(executionSet);
        MultipleInstanceCollectionInput input = mock(MultipleInstanceCollectionInput.class);
        when(input.getValue()).thenReturn(inputVariable);
        when(executionSet.getMultipleInstanceCollectionInput()).thenReturn(input);
        MultipleInstanceDataInput dataInput = mock(MultipleInstanceDataInput.class);
        when(executionSet.getMultipleInstanceDataInput()).thenReturn(dataInput);
        MultipleInstanceCollectionOutput output = mock(MultipleInstanceCollectionOutput.class);
        when(output.getValue()).thenReturn(outputVariable);
        when(executionSet.getMultipleInstanceCollectionOutput()).thenReturn(output);
        MultipleInstanceDataOutput dataOutput = mock(MultipleInstanceDataOutput.class);
        when(executionSet.getMultipleInstanceDataOutput()).thenReturn(dataOutput);
        return result;
    }

    private TaskGeneralSet mockTaskGeneralSet(String name) {
        TaskGeneralSet result = mock(TaskGeneralSet.class);
        Name nameProperty = mockName(name);
        when(result.getName()).thenReturn(nameProperty);
        return result;
    }

    private BPMNGeneralSet mockGeneralSet(String name) {
        BPMNGeneralSet result = mock(BPMNGeneralSet.class);
        Name nameProperty = mockName(name);
        when(result.getName()).thenReturn(nameProperty);
        return result;
    }

    private DataIOSet mockIOSet(AssignmentsInfo assignmentsInfo) {
        DataIOSet result = mock(DataIOSet.class);
        when(result.getAssignmentsinfo()).thenReturn(assignmentsInfo);
        return result;
    }

    private AssignmentsInfo mockAssignmentsInfo(String value) {
        AssignmentsInfo result = mock(AssignmentsInfo.class);
        when(result.getValue()).thenReturn(value);
        return result;
    }

    private Name mockName(String value) {
        Name result = mock(Name.class);
        when(result.getValue()).thenReturn(value);
        return result;
    }

    private BPMNDiagramImpl mockBpmnDiagram() {
        Id id = new Id(PROCESS_ID);
        BPMNDiagramImpl bpmnDiagram = new BPMNDiagramImpl();
        bpmnDiagram.getDiagramSet().setId(id);
        return bpmnDiagram;
    }
}
