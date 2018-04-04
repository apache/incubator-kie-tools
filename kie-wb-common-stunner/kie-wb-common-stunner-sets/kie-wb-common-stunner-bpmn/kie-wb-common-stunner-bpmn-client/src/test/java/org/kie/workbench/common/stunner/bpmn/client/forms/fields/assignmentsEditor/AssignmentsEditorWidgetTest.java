/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.assignmentsEditor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockito;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.AssignmentBaseTest;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.StringUtils;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.BusinessRuleTask;
import org.kie.workbench.common.stunner.bpmn.definition.EndNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndTerminateEvent;
import org.kie.workbench.common.stunner.bpmn.definition.SequenceFlow;
import org.kie.workbench.common.stunner.bpmn.definition.StartNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartSignalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.UserTask;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.TaskGeneralSet;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(StringUtils.class)
public class AssignmentsEditorWidgetTest extends AssignmentBaseTest {

    private static final String TASK_NAME = "Get Address";

    private static final String ASSIGNMENTS_INFO = "|input1:com.test.Employee,input2:String,input3:String,input4:String,Skippable||output1:com.test.Employee,output2:String|[din]employee->input1,[din]input2=ab%7Ccd%7Cef,[din]input3=yes,[din]input4=%22Hello%22+then+%22Goodbye%22,[dout]output1->employee,[dout]output2->reason";
    public static final String DATA_INPUT = "input1:com.test.Employee";
    public static final String DATA_INPUT_SET = "input1:com.test.Employee,input2:String,input3:String,input4:String,Skippable";
    public static final String DATA_OUTPUT = "output1:com.test.Employee";
    public static final String DATA_OUTPUT_SET = "output1:com.test.Employee,output2:String";
    public static final String PROCESS_VARS = "employee:java.lang.String,reason:java.lang.String,performance:java.lang.String";
    public static final String ASSIGNMENTS_SINGLE_INPUT = "[din]employee->input1";
    public static final String ASSIGNMENTS_SINGLE_OUTPUT = "[dout]output1->employee";
    public static final String ASSIGNMENTS_MULTIPLE = "[din]employee->input1,[din]input2=ab%7Ccd%7Cef,[din]input3=yes,[din]input4=%22Hello%22+then+%22Goodbye%22,[dout]output1->employee,[dout]output2->reason";
    public static final List<String> DATATYPES = new ArrayList<String>(Arrays.asList("myorg.myproject1.Cardboard",
                                                                                     "yourorg.materials.Paper",
                                                                                     "org.documents.Articles"));
    public static final String FORMATTED_DATATYPES = "Articles [org.documents]:org.documents.Articles,Cardboard [myorg.myproject1]:myorg.myproject1.Cardboard,Paper [yourorg.materials]:yourorg.materials.Paper";

    @GwtMock
    private AssignmentsEditorWidget widget;

    @GwtMock
    private ActivityDataIOEditor activityDataIOEditor;

    @GwtMock
    private ActivityDataIOEditorView activityDataIOEditorView;

    @Mock
    UserTask userTask;

    @Mock
    BusinessRuleTask businessRuleTask;

    @Mock
    StartNoneEvent startNoneEvent;

    @Mock
    StartSignalEvent startSignalEvent;

    @Mock
    EndTerminateEvent endTerminateEvent;

    @Mock
    EndNoneEvent endNoneEvent;

    @Mock
    SequenceFlow sequenceFlow;

    @Mock
    TaskGeneralSet taskGeneralSet;

    @Mock
    Name taskName;

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

    @Before
    public void setUp() throws Exception {
        super.setUp();
        GwtMockito.initMocks(this);
        widget.activityDataIOEditor = activityDataIOEditor;
        activityDataIOEditor.view = activityDataIOEditorView;
        doCallRealMethod().when(widget).parseAssignmentsInfo();
        doCallRealMethod().when(widget).getVariableCountsString(anyString(),
                                                                anyString(),
                                                                anyString(),
                                                                anyString(),
                                                                anyString(),
                                                                anyString(),
                                                                anyString());
        doCallRealMethod().when(widget).showAssignmentsDialog();
        doCallRealMethod().when(widget).showDataIOEditor(anyString());
        doCallRealMethod().when(widget).setBPMNModel(any(BPMNDefinition.class));
        doCallRealMethod().when(widget).formatDataTypes(any(List.class));
        doCallRealMethod().when(widget).getTaskName();
        doCallRealMethod().when(widget).getDisallowedPropertyNames();
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
        doCallRealMethod().when(activityDataIOEditor).configureDialog(anyString(),
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
        widget.assignmentsInfo = ASSIGNMENTS_INFO;
        Map<String, String> assignmentsProperties = widget.parseAssignmentsInfo();
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
        Map<String, String> assignmentsProperties = widget.parseAssignmentsInfo();

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
        String formattedDataTypes = widget.formatDataTypes(DATATYPES);
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
}

