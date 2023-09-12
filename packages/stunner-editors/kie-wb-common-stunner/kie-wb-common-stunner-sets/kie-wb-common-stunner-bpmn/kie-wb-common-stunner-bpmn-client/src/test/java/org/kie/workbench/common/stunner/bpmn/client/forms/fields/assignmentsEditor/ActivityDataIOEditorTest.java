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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.AssignmentRow;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.Variable;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class ActivityDataIOEditorTest {

    @Captor
    private ArgumentCaptor<Set<String>> setCaptor;

    @Captor
    private ArgumentCaptor<List<String>> listCaptor;

    @Captor
    ArgumentCaptor<List<AssignmentRow>> listAssignmentCaptor;

    @Mock
    private ActivityDataIOEditorView ioEditorView;

    @Spy
    @InjectMocks
    private ActivityDataIOEditor ioEditor;

    @Test
    public void testInitIoEditor() {
        ioEditor.init();
        verify(ioEditorView,
               times(1)).init(ioEditor);
    }

    @Test
    public void testSaveClickCallback() {
        AssignmentRow row = new AssignmentRow("name",
                                              Variable.VariableType.INPUT,
                                              "String",
                                              "Object",
                                              "var",
                                              null);
        List<AssignmentRow> input = new ArrayList<AssignmentRow>();
        input.add(row);
        List<AssignmentRow> output = new ArrayList<AssignmentRow>();
        output.add(row);
        when(ioEditorView.getInputAssignmentData()).thenReturn(input);
        when(ioEditorView.getOutputAssignmentData()).thenReturn(output);
        List<String> dataTypes = new ArrayList<String>();
        List<String> dataTypesDisplayNames = new ArrayList<String>();
        dataTypes.add("a.b.c.Name");
        dataTypesDisplayNames.add("Name");
        ActivityDataIOEditor.GetDataCallback mockCallback = mock(ActivityDataIOEditor.GetDataCallback.class);
        ioEditor.setCallback(mockCallback);
        ioEditor.handleOkClick();
        verify(ioEditorView).getInputAssignmentData();
        verify(ioEditorView).getOutputAssignmentData();
        verify(ioEditorView).hideView();
        verify((ioEditor.callback)).getData(any());
    }

    @Test
    public void testSaveClickHide() {
        ioEditor.handleOkClick();
        verify(ioEditorView).hideView();
    }

    @Test
    public void testCancelClick() {
        ActivityDataIOEditor.GetDataCallback mockCallback = mock(ActivityDataIOEditor.GetDataCallback.class);
        ioEditor.setCallback(mockCallback);
        ioEditor.handleCancelClick();
        verify(ioEditorView).hideView();
        verify(mockCallback,
               never()).getData(any());
    }

    @Test
    public void testPossibleDataTypes() {
        List<String> dataTypes = new ArrayList<String>();
        List<String> dataTypesDisplayNames = new ArrayList<String>();
        dataTypes.add("a.b.c.Name");
        dataTypesDisplayNames.add("Name");
        ioEditor.setDataTypes(dataTypes,
                              dataTypesDisplayNames);
        verify(ioEditorView).setPossibleInputAssignmentsDataTypes(listCaptor.capture());
        assertEquals(1,
                     listCaptor.getValue().size());
        assertEquals(dataTypesDisplayNames.get(0),
                     listCaptor.getValue().get(0));
        verify(ioEditorView).setPossibleOutputAssignmentsDataTypes(listCaptor.capture());
        assertEquals(1,
                     listCaptor.getValue().size());
        assertEquals(dataTypesDisplayNames.get(0),
                     listCaptor.getValue().get(0));
    }

    @Test
    public void testConfigureDialogBoolean() {
        ioEditor.configureDialog("task name",
                                 true,
                                 false,
                                 true,
                                 false);
        verify(ioEditorView).setInputAssignmentsVisibility(true);
        verify(ioEditorView).setIsInputAssignmentSingleVar(false);
        verify(ioEditorView).setOutputAssignmentsVisibility(true);
        verify(ioEditorView).setIsOutputAssignmentSingleVar(false);
        ioEditor.configureDialog("task name",
                                 false,
                                 true,
                                 false,
                                 true);
        verify(ioEditorView).setInputAssignmentsVisibility(false);
        verify(ioEditorView).setIsInputAssignmentSingleVar(true);
        verify(ioEditorView).setOutputAssignmentsVisibility(false);
        verify(ioEditorView).setIsOutputAssignmentSingleVar(true);
    }

    @Test
    public void testConfigureDialogTaskNameEmpty() {
        ioEditor.configureDialog("",
                                 true,
                                 true,
                                 true,
                                 true);
        verify(ioEditorView,
               times(1)).setDefaultViewTitle();
        verify(ioEditorView,
               never()).setCustomViewTitle(anyString());
    }

    @Test
    public void testConfigureDialogTaskNameNull() {
        ioEditor.configureDialog(null,
                                 true,
                                 true,
                                 true,
                                 true);
        verify(ioEditorView,
               times(1)).setDefaultViewTitle();
        verify(ioEditorView,
               never()).setCustomViewTitle(anyString());
    }

    @Test
    public void testConfigureDialogTaskNameCustom() {
        ioEditor.configureDialog("abc",
                                 true,
                                 true,
                                 true,
                                 true);
        verify(ioEditorView,
               times(1)).setCustomViewTitle("abc");
        verify(ioEditorView,
               never()).setDefaultViewTitle();
    }

    @Test
    public void testDisallowedPropertyNames() {
        List<String> disallowedNames = new ArrayList<String>();
        disallowedNames.add("Abc");
        disallowedNames.add("xyZ");
        ioEditor.setDisallowedPropertyNames(disallowedNames);
        verify(ioEditorView).setInputAssignmentsDisallowedNames(setCaptor.capture());
        assertEquals("should be 2 disallowed names",
                     2,
                     setCaptor.getValue().size());
        assertTrue("disallowed names should contain: abc",
                   setCaptor.getValue().contains("abc"));
        assertTrue("disallowed names should contain: xyz",
                   setCaptor.getValue().contains("xyz"));
    }

    @Test
    public void testNullDisallowedPropertyNames() {
        Set<String> disallowedNames = new HashSet<String>();
        ioEditor.setDisallowedPropertyNames(null);
        verify(ioEditorView).setInputAssignmentsDisallowedNames(disallowedNames);
    }

    @Test
    public void testProcessVariables() {
        List<String> variables = new ArrayList<String>();
        variables.add("variable");
        ioEditor.setProcessVariables(variables);
        verify(ioEditorView).setInputAssignmentsProcessVariables(listCaptor.capture());
        assertEquals(1,
                     listCaptor.getValue().size());
        assertEquals(variables.get(0),
                     listCaptor.getValue().get(0));
        verify(ioEditorView).setOutputAssignmentsProcessVariables(variables);
        assertEquals(1,
                     listCaptor.getValue().size());
        assertEquals(variables.get(0),
                     listCaptor.getValue().get(0));
    }

    @Test
    public void testInputAssignmentsRows() {
        List<AssignmentRow> rows = getAssignmentsWithSameNames();
        ioEditor.setInputAssignmentRows(rows);
        verify(ioEditorView).setInputAssignmentRows(listAssignmentCaptor.capture());
        checkAssignmentsWithSameNames(rows);
    }

    @Test
    public void testOutputAssignmentsRows() {
        List<AssignmentRow> rows = getAssignmentsWithSameNames();
        ioEditor.setOutputAssignmentRows(rows);
        verify(ioEditorView).setOutputAssignmentRows(listAssignmentCaptor.capture());
        checkAssignmentsWithSameNames(rows);
    }

    private List<AssignmentRow> getAssignmentsWithSameNames() {
        List<AssignmentRow> rows = new ArrayList<AssignmentRow>();
        rows.add(new AssignmentRow("varName",
                                   null,
                                   null,
                                   null,
                                   "varName",
                                   null));
        rows.add(new AssignmentRow("varName2",
                                   null,
                                   null,
                                   null,
                                   "varName2",
                                   null));
        return rows;
    }

    private void checkAssignmentsWithSameNames(List<AssignmentRow> assignments) {
        assertEquals(2,
                     listAssignmentCaptor.getValue().size());
        assertEquals(assignments.get(0),
                     listAssignmentCaptor.getValue().get(0));
        assertEquals("varName",
                     listAssignmentCaptor.getValue().get(0).getName());
        assertEquals("varName",
                     listAssignmentCaptor.getValue().get(0).getProcessVar());
        assertEquals(assignments.get(1),
                     listAssignmentCaptor.getValue().get(1));
        assertEquals("varName2",
                     listAssignmentCaptor.getValue().get(1).getName());
        assertEquals("varName2",
                     listAssignmentCaptor.getValue().get(1).getProcessVar());
    }

    @Test
    public void testShow() {
        ioEditor.show();
        verify(ioEditorView).showView();
        verify(ioEditorView,
               never()).hideView();
    }

    @Test
    public void testSetReadOnlyTrue() {
        ioEditor.setReadOnly(true);
        verify(ioEditorView,
               times(1)).setReadOnly(true);
    }

    @Test
    public void testSetReadOnlyFalse() {
        ioEditor.setReadOnly(false);
        verify(ioEditorView,
               times(1)).setReadOnly(false);
    }
}