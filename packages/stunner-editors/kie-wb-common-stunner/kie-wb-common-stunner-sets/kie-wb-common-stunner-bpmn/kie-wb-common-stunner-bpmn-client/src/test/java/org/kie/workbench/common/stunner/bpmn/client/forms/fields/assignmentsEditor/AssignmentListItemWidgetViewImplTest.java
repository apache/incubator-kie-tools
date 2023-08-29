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

import java.util.HashSet;
import java.util.Set;

import javax.enterprise.event.Event;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockito;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.ValueListBox;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.AssignmentRow;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.StringUtils;
import org.kie.workbench.common.stunner.bpmn.client.forms.widgets.ComboBox;
import org.kie.workbench.common.stunner.bpmn.client.forms.widgets.CustomDataTypeTextBox;
import org.kie.workbench.common.stunner.bpmn.client.forms.widgets.VariableNameTextBox;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.workbench.events.NotificationEvent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.Variable.VariableType.INPUT;
import static org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.Variable.VariableType.OUTPUT;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anySet;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class AssignmentListItemWidgetViewImplTest {

    private static final String VARIABLE_NAME = "variableName";
    private static final String CONSTANT_NAME = "constantName";
    private static final String CUST_DATA_TYPE_NAME = "custDataTypeName";
    private static final String DATA_TYPE_NAME = "dataTypeName";

    @GwtMock
    private DataBinder<AssignmentRow> assignment;

    @GwtMock
    private VariableNameTextBox name;

    @GwtMock
    private Button deleteButton;

    private CustomDataTypeTextBox customDataType;

    private TextBox expression;

    private ValueListBox<String> dataType;

    private ValueListBox<String> processVar;

    private ComboBox dataTypeComboBox;

    private ComboBox processVarComboBox;

    @GwtMock
    private KeyDownEvent keyDownEvent;

    private Event<NotificationEvent> notification = mock(EventSourceMock.class);

    @Captor
    private ArgumentCaptor<KeyDownHandler> keyDownHandlerCaptor;

    @Captor
    private ArgumentCaptor<ChangeHandler> changeHandlerCaptor;

    private AssignmentListItemWidgetViewImpl view;

    @Before
    public void setUp() throws Exception {
        GwtMockito.initMocks(this);
        customDataType = mock(CustomDataTypeTextBox.class);
        expression = mock(TextBox.class);
        dataType = mock(ValueListBox.class);
        processVar = mock(ValueListBox.class);
        dataTypeComboBox = mock(ComboBox.class);
        processVarComboBox = mock(ComboBox.class);
        view = mock(AssignmentListItemWidgetViewImpl.class);
        view.assignment = assignment;
        view.name = name;
        view.deleteButton = deleteButton;
        view.customDataType = customDataType;
        view.dataType = dataType;
        view.expression = expression;
        view.processVar = processVar;
        view.dataTypeComboBox = dataTypeComboBox;
        view.processVarComboBox = processVarComboBox;
        view.notification = notification;
        doCallRealMethod().when(view).init();
        doCallRealMethod().when(view).getExpression();
        doCallRealMethod().when(view).setExpression(anyString());
        doCallRealMethod().when(view).getCustomDataType();
        doCallRealMethod().when(view).setCustomDataType(anyString());
        doCallRealMethod().when(view).getModel();
        doCallRealMethod().when(view).setModel(any());
        doCallRealMethod().when(view).getModelValue(any());
        doCallRealMethod().when(view).setTextBoxModelValue(any(),
                                                           any());
        doCallRealMethod().when(view).setListBoxModelValue(any(),
                                                           any());
        doCallRealMethod().when(view).getDataType();
        doCallRealMethod().when(view).setDataType(any());
        doCallRealMethod().when(view).getProcessVar();
        doCallRealMethod().when(view).setProcessVar(any());
        doCallRealMethod().when(view).getVariableType();
        doCallRealMethod().when(view).setAllowDuplicateNames(anyBoolean(),
                                                             any());
        doCallRealMethod().when(view).setParentWidget(any());
        doCallRealMethod().when(view).isDuplicateName(any());
        doCallRealMethod().when(view).isMultipleInstanceVariable(any());
        doCallRealMethod().when(view).setShowExpressions(anyBoolean());
        doCallRealMethod().when(view).setDisallowedNames(anySet(),
                                                         any());
        doCallRealMethod().when(view).handleDeleteButton(any());
        doCallRealMethod().when(view).setReadOnly(anyBoolean());
        AssignmentRow row = new AssignmentRow();
        doReturn(row).when(assignment).getModel();
    }

    @Test
    public void testSetModelInputCustomProcessVar() {
        AssignmentRow row = new AssignmentRow();
        row.setProcessVar(VARIABLE_NAME);
        row.setExpression(null);
        row.setName(VARIABLE_NAME);
        row.setCustomDataType(CUST_DATA_TYPE_NAME);
        row.setDataType(null);
        row.setVariableType(INPUT);
        doReturn(row).when(assignment).getModel();
        view.setModel(row);
        verify(assignment,
               times(1)).setModel(row);
        verify(deleteButton,
               times(1)).setIcon(IconType.TRASH);
        verify(expression,
               never()).setVisible(false);
        verify(customDataType,
               times(1)).setValue(CUST_DATA_TYPE_NAME);
        verify(dataType,
               times(1)).setValue(CUST_DATA_TYPE_NAME);
        verify(processVar,
               times(1)).setValue(VARIABLE_NAME);
        verify(expression,
               never()).setValue(anyString());
    }

    @Test
    public void testSetModelOutputNormalConstant() {
        AssignmentRow row = new AssignmentRow();
        row.setProcessVar(null);
        row.setExpression(CONSTANT_NAME);
        row.setName(VARIABLE_NAME);
        row.setCustomDataType(null);
        row.setDataType(DATA_TYPE_NAME);
        row.setVariableType(OUTPUT);
        doReturn(row).when(assignment).getModel();
        view.setModel(row);
        verify(assignment,
               times(1)).setModel(row);
        verify(deleteButton,
               times(1)).setIcon(IconType.TRASH);
        verify(expression,
               times(1)).setVisible(false);
        verify(customDataType,
               never()).setValue(DATA_TYPE_NAME);
        verify(dataType,
               times(1)).setValue(DATA_TYPE_NAME);
        verify(expression,
               times(1)).setValue(CONSTANT_NAME);
    }

    @Test
    public void testSetTextBoxModelValueCustomDataType() {
        ActivityDataIOEditorWidget parent = mock(ActivityDataIOEditorWidget.class);
        when(parent.isDuplicateName(anyString())).thenReturn(true);

        assertNull(view.getModel().getCustomDataType());
        view.setParentWidget(parent);
        view.setTextBoxModelValue(customDataType, "abc");

        assertEquals("abc", view.getModel().getCustomDataType());
        assertNull(view.getModel().getExpression());
        assertEquals("abc", view.getModelValue(dataType));
    }

    @Test
    public void testSetTextBoxModelValueConstant() {
        view.setTextBoxModelValue(null, "abc");
        assertNull(view.getModel().getExpression());
        view.setTextBoxModelValue(expression, "abc");

        assertEquals("abc", view.getModel().getExpression());
        assertNull(view.getModel().getCustomDataType());
        assertEquals("abc", view.getModelValue(processVar));
    }

    @Test
    public void testSetListBoxModelValueDataType() {
        assertEquals("Object", view.getModel().getDataType());
        view.setListBoxModelValue(dataType, "abc");

        assertEquals("abc", view.getModel().getDataType());
        assertNull(view.getModel().getCustomDataType());
        assertNull(view.getModel().getProcessVar());
        assertEquals("abc", view.getModelValue(dataType));
    }

    @Test
    public void testSetListBoxModelValueProcessVar() {
        assertNull(view.getModel().getProcessVar());
        view.setListBoxModelValue(processVar, "abc");

        assertEquals("abc", view.getModel().getProcessVar());
        assertNull(view.getModel().getExpression());
        assertEquals("Object", view.getModel().getDataType());
        assertEquals("abc", view.getModelValue(processVar));
    }

    @Test
    public void testEmptyValueOfModel() {
        assertEquals("", view.getModelValue(null));
    }

    @Test
    public void testDataTypeHandlerSpace() {
        view.init();
        verify(customDataType, times(1)).setRegExp(eq(StringUtils.ALPHA_NUM_UNDERSCORE_DOT_GT_LT_REGEXP), anyString(), anyString(), anyString());
        verify(customDataType, times(1)).addKeyDownHandler(keyDownHandlerCaptor.capture());
        KeyDownHandler handler = keyDownHandlerCaptor.getValue();
        doReturn((int) ' ').when(keyDownEvent).getNativeKeyCode();
        handler.onKeyDown(keyDownEvent);
        verify(keyDownEvent, times(1)).preventDefault();
    }

    @Test
    public void testDataTypeHandlerAlphabetical() {
        view.init();
        verify(customDataType,
               times(1)).addKeyDownHandler(keyDownHandlerCaptor.capture());
        KeyDownHandler handler = keyDownHandlerCaptor.getValue();
        doReturn((int) 'a').when(keyDownEvent).getNativeKeyCode();
        handler.onKeyDown(keyDownEvent);
        verify(keyDownEvent,
               never()).preventDefault();
    }

    @Test
    public void testNameChangeHandler() {
        ActivityDataIOEditorWidget parent = mock(ActivityDataIOEditorWidget.class);
        when(parent.isDuplicateName(anyString())).thenReturn(true);
        doReturn("anyName").when(name).getText();
        view.setAllowDuplicateNames(false,
                                    "ErrorMessage");
        view.setParentWidget(parent);
        view.init();
        verify(name,
               times(1)).addChangeHandler(changeHandlerCaptor.capture());
        ChangeHandler handler = changeHandlerCaptor.getValue();
        handler.onChange(mock(ChangeEvent.class));
        verify(parent,
               times(1)).isDuplicateName("anyName");
        verify(notification,
               times(1)).fire(new NotificationEvent("ErrorMessage",
                                                    NotificationEvent.NotificationType.ERROR));
        verify(name,
               times(1)).setValue("");
    }

    @Test
    public void testNameChangeToMIVariableName() {
        ActivityDataIOEditorWidget parent = mock(ActivityDataIOEditorWidget.class);
        when(parent.isDuplicateName(anyString())).thenReturn(false);
        when(parent.isMultipleInstanceVariable("anyName")).thenReturn(true);
        doReturn("anyName").when(name).getText();
        view.setParentWidget(parent);
        view.init();
        verify(name, times(1)).addChangeHandler(changeHandlerCaptor.capture());
        ChangeHandler handler = changeHandlerCaptor.getValue();
        handler.onChange(mock(ChangeEvent.class));
        verify(parent, times(1)).isMultipleInstanceVariable("anyName");
        verify(notification, times(1)).fire(new NotificationEvent("AssignmentNameAlreadyInUseAsMultipleInstanceInputOutputVariable(anyName)", NotificationEvent.NotificationType.ERROR));
        verify(name, times(1)).setValue("");
    }

    @Test
    public void testSetShowConstantsTrue() {
        view.setShowExpressions(true);
        verify(processVarComboBox).setShowCustomValues(true);
    }

    @Test
    public void testSetShowConstantsFalse() {
        view.setShowExpressions(false);
        verify(processVarComboBox).setShowCustomValues(false);
    }

    @Test
    public void testSetInputExpression() {
        AssignmentRow row = new AssignmentRow(null, INPUT, null, null, null, null);
        when(view.getModel()).thenReturn(row);
        view.setExpression("hello");

        assertEquals("hello", view.getModel().getExpression());
    }

    @Test
    public void testSetEmptyExpressionToOutput() {
        AssignmentRow row = new AssignmentRow(null, OUTPUT, null, null, null, null);
        when(view.getModel()).thenReturn(row);
        view.setExpression("");

        assertEquals("", view.getModel().getExpression());
    }

    @Test
    public void testSetExpressionToOutput() {
        AssignmentRow row = new AssignmentRow(null, OUTPUT, null, null, null, null);
        when(view.getModel()).thenReturn(row);
        view.setExpression("#{hello}");

        assertEquals("#{hello}", view.getModel().getExpression());
    }

    @Test
    public void testSetConstantToOutput() {
        AssignmentRow row = new AssignmentRow(null, OUTPUT, null, null, null, null);
        when(view.getModel()).thenReturn(row);
        view.setExpression("hello");
        assertEquals(view.getModel().getExpression(), "hello");
    }

    @Test
    public void testSetDisallowedNames() {
        Set<String> disallowedNames = new HashSet<>();
        String disallowedNameErrorMessage = "error value";
        view.setDisallowedNames(disallowedNames,
                                disallowedNameErrorMessage);
        verify(name).setInvalidValues(disallowedNames,
                                      false,
                                      disallowedNameErrorMessage);
    }

    @Test
    public void testHandleDeleteButton() {
        ActivityDataIOEditorWidget widget = mock(ActivityDataIOEditorWidget.class);
        AssignmentRow model = mock(AssignmentRow.class);
        when(view.getModel()).thenReturn(model);
        view.setParentWidget(widget);
        view.handleDeleteButton(null);
        verify(widget).removeAssignment(model);
    }

    @Test
    public void testSetReadOnlyTrue() {
        view.setReadOnly(true);
        verify(name,
               times(1)).setReadOnly(true);
        verify(dataType,
               times(1)).setEnabled(false);
        verify(processVar,
               times(1)).setEnabled(false);
        verify(deleteButton,
               times(1)).setEnabled(false);
    }

    @Test
    public void testSetReadOnlyFalse() {
        view.setReadOnly(false);
        verify(name,
               times(1)).setReadOnly(false);
        verify(dataType,
               times(1)).setEnabled(true);
        verify(processVar,
               times(1)).setEnabled(true);
        verify(deleteButton,
               times(1)).setEnabled(true);
    }
}
