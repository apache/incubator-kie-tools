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


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.assignmentsEditor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockito;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.Assignment;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.AssignmentBaseTest;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.AssignmentData;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.AssignmentParser;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.Variable;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.StringUtils;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagramImpl;
import org.kie.workbench.common.stunner.bpmn.definition.BusinessRuleTask;
import org.kie.workbench.common.stunner.bpmn.definition.DataObject;
import org.kie.workbench.common.stunner.bpmn.definition.EndNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndTerminateEvent;
import org.kie.workbench.common.stunner.bpmn.definition.SequenceFlow;
import org.kie.workbench.common.stunner.bpmn.definition.StartNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartSignalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.UserTask;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.TaskGeneralSet;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.SelectionControl;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewImpl;
import org.kie.workbench.common.stunner.core.graph.impl.EdgeImpl;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.uberfire.commons.Pair;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(StringUtils.class)
public class AssignmentsEditorWidgetTest extends AssignmentBaseTest {

    private static final String TASK_NAME = "Get Address";

    private static final String ASSIGNMENTS_INFO = "|input1:com.test.Employee:,input2:String:,input3:String:,input4:String:,Skippable::||output1:com.test.Employee:,output2:String:|[din]employee->input1,[din]input2=ab%7Ccd%7Cef,[din]input3=yes,[din]input4=%22Hello%22+then+%22Goodbye%22,[dout]output1->employee,[dout]output2->reason";
    public static final String DATA_INPUT = "input1:com.test.Employee:";
    public static final String DATA_INPUT_SET = "input1:com.test.Employee:,input2:String:,input3:String:,input4:String:,Skippable::";
    public static final String DATA_OUTPUT = "output1:com.test.Employee:";
    public static final String DATA_OUTPUT_SET = "output1:com.test.Employee:,output2:String:";
    public static final String PROCESS_VARS = "employee:java.lang.String:,reason:java.lang.String:,performance:java.lang.String:";
    public static final String ASSIGNMENTS_SINGLE_INPUT = "[din]employee->input1";
    public static final String ASSIGNMENTS_SINGLE_OUTPUT = "[dout]output1->employee";
    public static final String ASSIGNMENTS_MULTIPLE = "[din]employee->input1,[din]input2=ab%7Ccd%7Cef,[din]input3=yes,[din]input4=%22Hello%22+then+%22Goodbye%22,[dout]output1->employee,[dout]output2->reason";
    public static final List<String> DATATYPES = new ArrayList<>(Arrays.asList("myorg.myproject1.Cardboard",
                                                                                     "yourorg.materials.Paper",
                                                                                     "org.documents.Articles"));
    public static final String FORMATTED_DATATYPES = "Articles [org.documents]:org.documents.Articles,Cardboard [myorg.myproject1]:myorg.myproject1.Cardboard,Paper [yourorg.materials]:yourorg.materials.Paper";

    public static final String SIMPLE_DATA_TYPES = "Boolean:Boolean,Float:Float,Integer:Integer,Object:Object,String:String";
    public static final String NORMAL_TASK_WITH_INPUTS_OUTPUTS_CASE = "|input1:Boolean:,input2:Object:||output1:Object:,output2:Integer:|[din]processVar1->input1,[din]processVar2->input2,[dout]output1->processVar3,[dout]output2->processVar4";
    public static final String NORMAL_TASK_WITH_ONLY_INPUTS_CASE = "|input1:Boolean:,input2:Object:|||[din]processVar1->input1,[din]processVar2->input2";
    public static final String NORMAL_TASK_WITH_ONLY_OUTPUTS_CASE = "|||output1:Object:,output2:Integer:|[dout]output1->processVar1,[dout]output2->processVar2";
    public static final String EVENT_WITH_INPUT_CASE = "||eventOutput:Object:||[dout]eventOutput->processVar1";
    public static final String EVENT_WITH_OUTPUT_CASE = "eventInput:Object:||||[din]processVar1->eventInput";

    @GwtMock
    private AssignmentsEditorWidget widget;

    @GwtMock
    private ActivityDataIOEditor activityDataIOEditor;

    @GwtMock
    private ActivityDataIOEditorView activityDataIOEditorView;

    @Mock
    private UserTask userTask;

    @Mock
    private BusinessRuleTask businessRuleTask;

    @Mock
    private StartNoneEvent startNoneEvent;

    @Mock
    private StartSignalEvent startSignalEvent;

    @Mock
    private EndTerminateEvent endTerminateEvent;

    @Mock
    private EndNoneEvent endNoneEvent;

    @Mock
    private SequenceFlow sequenceFlow;

    @Mock
    private TaskGeneralSet taskGeneralSet;

    @Mock
    private Name taskName;

    @Mock
    private SessionManager canvasSessionManager;

    @Mock
    private EditorSession session;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private Metadata metadata;

    @Mock
    private SelectionControl selectionControl;

    @Mock
    private Diagram diagram;

    @Mock
    private Graph graph;

    @Captor
    private ArgumentCaptor<String> taskNameCaptor;

    @Captor
    private ArgumentCaptor<Boolean> hasInputVarsCaptor;

    @Captor
    private ArgumentCaptor<Boolean> isSingleInputVarCaptor;

    @Captor
    private ArgumentCaptor<Boolean> hasOutputVarsCaptor;

    @Captor
    private ArgumentCaptor<Boolean> isSingleOutputVarCaptor;

    @Captor
    private ArgumentCaptor<AssignmentData> assignmentDataCaptor;

    private List<Variable> processVariables;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        GwtMockito.initMocks(this);
        widget.activityDataIOEditor = activityDataIOEditor;
        widget.canvasSessionManager = canvasSessionManager;
        activityDataIOEditor.view = activityDataIOEditorView;
        doCallRealMethod().when(widget).getVariableCountsString(any(),
                                                                any(),
                                                                any(),
                                                                any(),
                                                                any(),
                                                                any(),
                                                                any());
        doCallRealMethod().when(widget).showAssignmentsDialog();
        doCallRealMethod().when(widget).showDataIOEditor(any());
        doCallRealMethod().when(widget).setBPMNModel(any(BPMNDefinition.class));
        doCallRealMethod().when(widget).formatDataTypes(any(List.class));
        doCallRealMethod().when(widget).getTaskName();
        doCallRealMethod().when(widget).getDisallowedPropertyNames();
        doCallRealMethod().when(widget).setReadOnly(anyBoolean());
        when(userTask.getGeneral()).thenReturn(taskGeneralSet);
        when(taskGeneralSet.getName()).thenReturn(taskName);
        when(taskName.getValue()).thenReturn(TASK_NAME);
        doCallRealMethod().when(userTask).hasInputVars();
        doCallRealMethod().when(userTask).isSingleInputVar();
        doCallRealMethod().when(userTask).hasOutputVars();
        doCallRealMethod().when(userTask).isSingleOutputVar();
        doCallRealMethod().when(businessRuleTask).hasInputVars();
        doCallRealMethod().when(businessRuleTask).isSingleInputVar();
        doCallRealMethod().when(businessRuleTask).hasOutputVars();
        doCallRealMethod().when(businessRuleTask).isSingleOutputVar();
        doCallRealMethod().when(startNoneEvent).hasInputVars();
        doCallRealMethod().when(startNoneEvent).isSingleInputVar();
        doCallRealMethod().when(startNoneEvent).hasOutputVars();
        doCallRealMethod().when(startNoneEvent).isSingleOutputVar();
        doCallRealMethod().when(startSignalEvent).hasInputVars();
        doCallRealMethod().when(startSignalEvent).isSingleInputVar();
        doCallRealMethod().when(startSignalEvent).hasOutputVars();
        doCallRealMethod().when(startSignalEvent).isSingleOutputVar();
        doCallRealMethod().when(endTerminateEvent).hasInputVars();
        doCallRealMethod().when(endTerminateEvent).isSingleInputVar();
        doCallRealMethod().when(endTerminateEvent).hasOutputVars();
        doCallRealMethod().when(endTerminateEvent).isSingleOutputVar();
        doCallRealMethod().when(endNoneEvent).hasInputVars();
        doCallRealMethod().when(endNoneEvent).isSingleInputVar();
        doCallRealMethod().when(endNoneEvent).hasOutputVars();
        doCallRealMethod().when(endNoneEvent).isSingleOutputVar();
        doCallRealMethod().when(activityDataIOEditor).configureDialog(any(),
                                                                      anyBoolean(),
                                                                      anyBoolean(),
                                                                      anyBoolean(),
                                                                      anyBoolean());

        widget.setBPMNModel(userTask);
    }

    @After
    public void tearDown() {
        super.tearDown();
    }

    @Test
    public void testParseAssignmentsInfo() {
        Map<String, String> assignmentsProperties = AssignmentParser.parseAssignmentsInfo(ASSIGNMENTS_INFO);
        assertEquals(DATA_INPUT_SET,
                     assignmentsProperties.get("datainputset"));
        assertEquals(DATA_OUTPUT_SET,
                     assignmentsProperties.get("dataoutputset"));
        assertEquals(ASSIGNMENTS_MULTIPLE,
                     assignmentsProperties.get("assignments"));
    }

    @Test
    public void testGetVariableCountsString() {
        String variableCountsString = widget.getVariableCountsString(null,
                                                                     DATA_INPUT_SET,
                                                                     null,
                                                                     DATA_OUTPUT_SET,
                                                                     PROCESS_VARS,
                                                                     ASSIGNMENTS_MULTIPLE,
                                                                     widget.getDisallowedPropertyNames());
        assertEquals("4 Data_Inputs, 2 Data_Outputs",
                     variableCountsString);
    }

    @Test
    public void testShowAssignmentsDialog() {
        widget.setBPMNModel(userTask);
        widget.assignmentsInfo = ASSIGNMENTS_INFO;
        Map<String, String> assignmentsProperties = AssignmentParser.parseAssignmentsInfo(ASSIGNMENTS_INFO);

        widget.showAssignmentsDialog();

        verify(widget).getDataTypes();
    }

    @Test
    public void testShowDataIOEditor() {
        widget.showDataIOEditor(null);

        verify(activityDataIOEditor).configureDialog(taskNameCaptor.capture(),
                                                     hasInputVarsCaptor.capture(),
                                                     isSingleInputVarCaptor.capture(),
                                                     hasOutputVarsCaptor.capture(),
                                                     isSingleOutputVarCaptor.capture());
        assertEquals(TASK_NAME,
                     taskNameCaptor.getValue());
        assertEquals(true,
                     hasInputVarsCaptor.getValue());
        assertEquals(false,
                     isSingleInputVarCaptor.getValue());
        assertEquals(true,
                     hasOutputVarsCaptor.getValue());
        assertEquals(false,
                     isSingleOutputVarCaptor.getValue());
    }

    @Test
    public void testFormatDataTypes() {
        String formattedDataTypes = widget.formatDataTypes(null);
        assertEquals(null,
                     formattedDataTypes);

        List<String> list = new ArrayList<>();
        formattedDataTypes = widget.formatDataTypes(list);
        assertEquals("",
                     formattedDataTypes);

        formattedDataTypes = widget.formatDataTypes(DATATYPES);
        assertEquals(FORMATTED_DATATYPES,
                     formattedDataTypes);
    }

    @Test
    public void testGetTaskName() {
        String taskName = widget.getTaskName();
        assertEquals(TASK_NAME,
                     taskName);
    }

    @Test
    public void testSetBPMNModelUserTask() {
        widget.setBPMNModel(userTask);
        assertEquals(true,
                     widget.hasInputVars);
        assertEquals(false,
                     widget.isSingleInputVar);
        assertEquals(true,
                     widget.hasOutputVars);
        assertEquals(false,
                     widget.isSingleOutputVar);

        String assignmentsInfoString = widget.getVariableCountsString(null,
                                                                      DATA_INPUT_SET,
                                                                      null,
                                                                      DATA_OUTPUT_SET,
                                                                      PROCESS_VARS,
                                                                      ASSIGNMENTS_MULTIPLE,
                                                                      widget.getDisallowedPropertyNames());
        assertEquals("4 Data_Inputs, 2 Data_Outputs",
                     assignmentsInfoString);
    }

    @Test
    public void testSetBPMNModelBusinessRuleTask() {
        widget.setBPMNModel(businessRuleTask);
        assertEquals(true,
                     widget.hasInputVars);
        assertEquals(false,
                     widget.isSingleInputVar);
        assertEquals(true,
                     widget.hasOutputVars);
        assertEquals(false,
                     widget.isSingleOutputVar);

        String assignmentsInfoString = widget.getVariableCountsString(null,
                                                                      DATA_INPUT_SET,
                                                                      null,
                                                                      DATA_OUTPUT_SET,
                                                                      PROCESS_VARS,
                                                                      ASSIGNMENTS_MULTIPLE,
                                                                      widget.getDisallowedPropertyNames());
        assertEquals("5 Data_Inputs, 2 Data_Outputs",
                     assignmentsInfoString);
    }

    @Test
    public void testSetBPMNModelStartNoneEvent() {
        widget.setBPMNModel(startNoneEvent);
        assertEquals(false,
                     widget.hasInputVars);
        assertEquals(false,
                     widget.isSingleInputVar);
        assertEquals(false,
                     widget.hasOutputVars);
        assertEquals(false,
                     widget.isSingleOutputVar);

        String assignmentsInfoString = widget.getVariableCountsString(null,
                                                                      null,
                                                                      DATA_OUTPUT,
                                                                      null,
                                                                      PROCESS_VARS,
                                                                      ASSIGNMENTS_SINGLE_OUTPUT,
                                                                      widget.getDisallowedPropertyNames());
        assertEquals("",
                     assignmentsInfoString);
    }

    @Test
    public void testSetBPMNModelStartSignalEvent() {
        widget.setBPMNModel(startSignalEvent);
        assertEquals(false,
                     widget.hasInputVars);
        assertEquals(false,
                     widget.isSingleInputVar);
        assertEquals(true,
                     widget.hasOutputVars);
        assertEquals(true,
                     widget.isSingleOutputVar);

        String assignmentsInfoString = widget.getVariableCountsString(null,
                                                                      null,
                                                                      DATA_OUTPUT,
                                                                      null,
                                                                      PROCESS_VARS,
                                                                      ASSIGNMENTS_SINGLE_OUTPUT,
                                                                      widget.getDisallowedPropertyNames());
        assertEquals("1 Data_Output",
                     assignmentsInfoString);
    }

    @Test
    public void testSetBPMNModelEndTerminateEvent() {
        widget.setBPMNModel(endTerminateEvent);
        assertEquals(false,
                     widget.hasInputVars);
        assertEquals(false,
                     widget.isSingleInputVar);
        assertEquals(false,
                     widget.hasOutputVars);
        assertEquals(false,
                     widget.isSingleOutputVar);

        String assignmentsInfoString = widget.getVariableCountsString(DATA_INPUT,
                                                                      null,
                                                                      null,
                                                                      null,
                                                                      PROCESS_VARS,
                                                                      ASSIGNMENTS_SINGLE_OUTPUT,
                                                                      widget.getDisallowedPropertyNames());
        assertEquals("",
                     assignmentsInfoString);
    }

    @Test
    public void testSetBPMNModelEndNoneEvent() {
        widget.setBPMNModel(endNoneEvent);
        assertEquals(false,
                     widget.hasInputVars);
        assertEquals(false,
                     widget.isSingleInputVar);
        assertEquals(false,
                     widget.hasOutputVars);
        assertEquals(false,
                     widget.isSingleOutputVar);

        String assignmentsInfoString = widget.getVariableCountsString(DATA_INPUT,
                                                                      null,
                                                                      null,
                                                                      null,
                                                                      PROCESS_VARS,
                                                                      ASSIGNMENTS_SINGLE_OUTPUT,
                                                                      widget.getDisallowedPropertyNames());
        assertEquals("",
                     assignmentsInfoString);
    }

    @Test
    public void testSetBPMNModelNoDataIO() {
        widget.setBPMNModel(sequenceFlow);
        assertEquals(false,
                     widget.hasInputVars);
        assertEquals(false,
                     widget.isSingleInputVar);
        assertEquals(false,
                     widget.hasOutputVars);
        assertEquals(false,
                     widget.isSingleOutputVar);
    }

    @Test
    public void testSetReadOnlyTrue() {
        widget.setReadOnly(true);
        verify(activityDataIOEditor,
               times(1)).setReadOnly(true);
    }

    @Test
    public void testSetReadOnlyFalse() {
        widget.setReadOnly(false);
        verify(activityDataIOEditor,
               times(1)).setReadOnly(false);
    }

    @Test
    public void testDataObjectsNewLine() {
        DataObject dataObject = new DataObject();
        dataObject.getName().setValue("Data\nObject");
        String doNameFilter = widget.dataObjectToProcessVariableFormat(dataObject);
        assertEquals(doNameFilter, "DataObject:Object");
     }

    @Test
    public void testAssignmentsForNormalTaskWithInputOutput() {
        setUpProcessVariables();
        Variable inputVariable1 = new Variable("input1", Variable.VariableType.INPUT, "Boolean", null);
        Variable inputVariable2 = new Variable("input2", Variable.VariableType.INPUT, "Object", null);
        Variable outputVariable1 = new Variable("output1", Variable.VariableType.OUTPUT, "Object", null);
        Variable outputVariable2 = new Variable("output2", Variable.VariableType.OUTPUT, "Integer", null);

        List<Variable> expectedInputVariables = Arrays.asList(inputVariable1, inputVariable2);
        List<Variable> expectedOutputVariables = Arrays.asList(outputVariable1, outputVariable2);
        List<Pair<Variable, Variable>> expectedAssignments = Arrays.asList(Pair.newPair(inputVariable1, processVariables.get(0)),
                                                                           Pair.newPair(inputVariable2, processVariables.get(1)),
                                                                           Pair.newPair(outputVariable1, processVariables.get(2)),
                                                                           Pair.newPair(outputVariable2, processVariables.get(3)));
        testAssignments(NORMAL_TASK_WITH_INPUTS_OUTPUTS_CASE, expectedInputVariables, expectedOutputVariables, expectedAssignments);
    }

    @Test
    public void testAssignmentsForNormalTaskWithOnlyInput() {
        setUpProcessVariables();
        Variable inputVariable1 = new Variable("input1", Variable.VariableType.INPUT, "Boolean", null);
        Variable inputVariable2 = new Variable("input2", Variable.VariableType.INPUT, "Object", null);
        List<Variable> expectedInputVariables = Arrays.asList(inputVariable1, inputVariable2);
        List<Pair<Variable, Variable>> expectedAssignments = Arrays.asList(Pair.newPair(inputVariable1, processVariables.get(0)),
                                                                           Pair.newPair(inputVariable2, processVariables.get(1)));
        testAssignments(NORMAL_TASK_WITH_ONLY_INPUTS_CASE, expectedInputVariables, Collections.emptyList(), expectedAssignments);
    }

    @Test
    public void testAssignmentsForNormalTaskWithOnlyOutput() {
        setUpProcessVariables();
        Variable outputVariable1 = new Variable("output1", Variable.VariableType.OUTPUT, "Object", null);
        Variable outputVariable2 = new Variable("output2", Variable.VariableType.OUTPUT, "Integer", null);
        List<Variable> expectedOutputVariables = Arrays.asList(outputVariable1, outputVariable2);
        List<Pair<Variable, Variable>> expectedAssignments = Arrays.asList(Pair.newPair(outputVariable1, processVariables.get(0)),
                                                                           Pair.newPair(outputVariable2, processVariables.get(1)));
        testAssignments(NORMAL_TASK_WITH_ONLY_OUTPUTS_CASE, Collections.emptyList(), expectedOutputVariables, expectedAssignments);
    }

    @Test
    public void testAssignmentsForEventWithOutput() {
        setUpProcessVariables();
        Variable outputVariable1 = new Variable("eventOutput", Variable.VariableType.OUTPUT, "Object", null);
        List<Variable> expectedOutputVariables = Collections.singletonList(outputVariable1);
        List<Pair<Variable, Variable>> expectedAssignments = Collections.singletonList(Pair.newPair(outputVariable1, processVariables.get(0)));
        testAssignments(EVENT_WITH_INPUT_CASE, Collections.emptyList(), expectedOutputVariables, expectedAssignments);
    }

    @Test
    public void testAssignmentsForEventWithInput() {
        setUpProcessVariables();
        Variable inputVariable1 = new Variable("eventInput", Variable.VariableType.INPUT, "Object", null);
        List<Variable> expectedInputVariables = Collections.singletonList(inputVariable1);
        List<Pair<Variable, Variable>> expectedAssignments = Collections.singletonList(Pair.newPair(inputVariable1, processVariables.get(0)));
        testAssignments(EVENT_WITH_OUTPUT_CASE, expectedInputVariables, Collections.emptyList(), expectedAssignments);
    }

    @SuppressWarnings("unchecked")
    private void testAssignments(String encodedAssignments,
                                 List<Variable> expectedInputVariables,
                                 List<Variable> expectedOutputVariables,
                                 List<Pair<Variable, Variable>> expectedAssignments) {
        doCallRealMethod().when(widget).setValue(anyString());
        doCallRealMethod().when(widget).setValue(anyString(), anyBoolean());
        doCallRealMethod().when(widget).getProcessVariables();
        doCallRealMethod().when(widget).getSelectedElement();
        doCallRealMethod().when(widget).getSelectedElementUUID(anyObject());

        when(canvasSessionManager.getCurrentSession()).thenReturn(session);
        when(session.getCanvasHandler()).thenReturn(canvasHandler);
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(diagram.getMetadata()).thenReturn(metadata);
        when(metadata.getCanvasRootUUID()).thenReturn("rootId");
        when(widget.getParentIds()).thenReturn(new HashSet<>());
        when(diagram.getGraph()).thenReturn(graph);

        when(session.getSelectionControl()).thenReturn(selectionControl);
        List<String> selectedItems = Collections.singletonList("UUID");
        when(selectionControl.getSelectedItems()).thenReturn(selectedItems);

        BPMNDiagramImpl bpmnDiagram = new BPMNDiagramImpl();
        Node parentNode = new NodeImpl("PARENT_UUID");
        View parentNodeView = new ViewImpl(bpmnDiagram, Bounds.create());
        parentNode.setContent(parentNodeView);

        bpmnDiagram.getProcessData().getProcessVariables().setValue("processVar1:Object:,processVar2:Object:,processVar3:Object:,processVar4:Object:");
        Node node = new NodeImpl("UUID");
        Edge edge = new EdgeImpl("edge");
        Child child = mock(Child.class);
        edge.setContent(child);
        edge.setSourceNode(parentNode);
        node.getInEdges().add(edge);

        when(graph.getNode("UUID")).thenReturn(node);

        List<Element> nodes = Arrays.asList(node, parentNode);
        when(graph.nodes()).thenReturn(nodes);

        widget.setValue(encodedAssignments);
        widget.showDataIOEditor(SIMPLE_DATA_TYPES);
        verify(activityDataIOEditor).setAssignmentData(assignmentDataCaptor.capture());

        AssignmentData assignmentData = assignmentDataCaptor.getValue();
        verifyProcessVariables(assignmentData);

        assertVariables(expectedInputVariables, assignmentData.getInputVariables());
        assertVariables(expectedOutputVariables, assignmentData.getOutputVariables());
        assertAssignments(expectedAssignments, assignmentData.getAssignments());
    }

    private void setUpProcessVariables() {

        processVariables = Arrays.asList(new Variable("processVar1", Variable.VariableType.PROCESS, "Object", null),
                                         new Variable("processVar2", Variable.VariableType.PROCESS, "Object", null),
                                         new Variable("processVar3", Variable.VariableType.PROCESS, "Object", null),
                                         new Variable("processVar4", Variable.VariableType.PROCESS, "Object", null));
    }

    private void verifyProcessVariables(AssignmentData assignmentData) {
        assertEquals(processVariables.get(0), assignmentData.getProcessVariables().get(0));
        assertEquals(processVariables.get(1), assignmentData.getProcessVariables().get(1));
        assertEquals(processVariables.get(2), assignmentData.getProcessVariables().get(2));
        assertEquals(processVariables.get(3), assignmentData.getProcessVariables().get(3));
    }

    private void assertAssignments(List<Pair<Variable, Variable>> expectedAssignments, List<Assignment> assignments) {
        assertEquals(expectedAssignments.size(), assignments.size());
        Pair<Variable, Variable> expectedAssignment;
        Assignment assignment;
        for (int i = 0; i < expectedAssignments.size(); i++) {
            expectedAssignment = expectedAssignments.get(i);
            assignment = assignments.get(i);
            assertEquals(expectedAssignment.getK1(), assignment.getVariable());
            assertEquals(expectedAssignment.getK2().getName(), assignment.getProcessVarName());
        }
    }

    private void assertVariables(List<Variable> expectedVariables, List<Variable> variables) {
        assertEquals(expectedVariables, variables);
    }
}

