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

import java.util.HashSet;
import java.util.Set;

import javax.enterprise.event.Event;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockito;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.ValueListBox;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.AssignmentRow;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.Variable;
import org.kie.workbench.common.stunner.bpmn.client.forms.widgets.ComboBox;
import org.kie.workbench.common.stunner.bpmn.client.forms.widgets.VariableNameTextBox;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.workbench.events.NotificationEvent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
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

@RunWith(MockitoJUnitRunner.class)
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

    private TextBox customDataType;

    private TextBox constant;

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
    private ArgumentCaptor<BlurHandler> blurHandlerCaptor;

    private AssignmentListItemWidgetViewImpl view;

    @Before
    public void setUp() throws Exception {
        GwtMockito.initMocks(this);
        customDataType = mock(TextBox.class);
        constant = mock(TextBox.class);
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
        view.constant = constant;
        view.processVar = processVar;
        view.dataTypeComboBox = dataTypeComboBox;
        view.processVarComboBox = processVarComboBox;
        view.notification = notification;
        doCallRealMethod().when(view).init();
        doCallRealMethod().when(view).getConstant();
        doCallRealMethod().when(view).setConstant(anyString());
        doCallRealMethod().when(view).getCustomDataType();
        doCallRealMethod().when(view).setCustomDataType(anyString());
        doCallRealMethod().when(view).getModel();
        doCallRealMethod().when(view).setModel(any(AssignmentRow.class));
        doCallRealMethod().when(view).getModelValue(any(ValueListBox.class));
        doCallRealMethod().when(view).setTextBoxModelValue(any(TextBox.class),
                                                           anyString());
        doCallRealMethod().when(view).setListBoxModelValue(any(ValueListBox.class),
                                                           anyString());
        doCallRealMethod().when(view).getDataType();
        doCallRealMethod().when(view).setDataType(anyString());
        doCallRealMethod().when(view).getProcessVar();
        doCallRealMethod().when(view).setProcessVar(anyString());
        doCallRealMethod().when(view).getVariableType();
        doCallRealMethod().when(view).setAllowDuplicateNames(anyBoolean(),
                                                             anyString());
        doCallRealMethod().when(view).setParentWidget(any(ActivityDataIOEditorWidget.class));
        doCallRealMethod().when(view).isDuplicateName(anyString());
        doCallRealMethod().when(view).setShowConstants(anyBoolean());
        doCallRealMethod().when(view).setDisallowedNames(anySet(),
                                                         anyString());
        doCallRealMethod().when(view).handleDeleteButton(any(ClickEvent.class));
        doCallRealMethod().when(view).setReadOnly(anyBoolean());
        AssignmentRow row = new AssignmentRow();
        doReturn(row).when(assignment).getModel();
    }

    @Test
    public void testSetModelInputCustomProcessVar() {
        AssignmentRow row = new AssignmentRow();
        row.setProcessVar(VARIABLE_NAME);
        row.setConstant(null);
        row.setName(VARIABLE_NAME);
        row.setCustomDataType(CUST_DATA_TYPE_NAME);
        row.setDataType(null);
        row.setVariableType(Variable.VariableType.INPUT);
        doReturn(row).when(assignment).getModel();
        view.setModel(row);
        verify(assignment,
               times(1)).setModel(row);
        verify(deleteButton,
               times(1)).setIcon(IconType.TRASH);
        verify(constant,
               never()).setVisible(false);
        verify(customDataType,
               times(1)).setValue(CUST_DATA_TYPE_NAME);
        verify(dataType,
               times(1)).setValue(CUST_DATA_TYPE_NAME);
        verify(processVar,
               times(1)).setValue(VARIABLE_NAME);
        verify(constant,
               never()).setValue(anyString());
    }

    @Test
    public void testSetModelOutputNormalConstant() {
        AssignmentRow row = new AssignmentRow();
        row.setProcessVar(null);
        row.setConstant(CONSTANT_NAME);
        row.setName(VARIABLE_NAME);
        row.setCustomDataType(null);
        row.setDataType(DATA_TYPE_NAME);
        row.setVariableType(Variable.VariableType.OUTPUT);
        doReturn(row).when(assignment).getModel();
        view.setModel(row);
        verify(assignment,
               times(1)).setModel(row);
        verify(deleteButton,
               times(1)).setIcon(IconType.TRASH);
        verify(constant,
               times(1)).setVisible(false);
        verify(customDataType,
               never()).setValue(DATA_TYPE_NAME);
        verify(dataType,
               times(1)).setValue(DATA_TYPE_NAME);
        verify(constant,
               times(1)).setValue(CONSTANT_NAME);
    }

    @Test
    public void testSetTextBoxModelValueCustomDataType() {
        assertNull(view.getModel().getCustomDataType());
        view.setTextBoxModelValue(customDataType,
                                  "abc");
        assertEquals("abc",
                     view.getModel().getCustomDataType());
        assertNull(view.getModel().getConstant());
        assertEquals("abc",
                     view.getModelValue(dataType));
    }

    @Test
    public void testSetTextBoxModelValueConstant() {
        assertNull(view.getModel().getConstant());
        view.setTextBoxModelValue(constant,
                                  "abc");
        assertEquals("abc",
                     view.getModel().getConstant());
        assertNull(view.getModel().getCustomDataType());
        assertEquals("abc",
                     view.getModelValue(processVar));
    }

    @Test
    public void testSetListBoxModelValueDataType() {
        assertNull(view.getModel().getDataType());
        view.setListBoxModelValue(dataType,
                                  "abc");
        assertEquals("abc",
                     view.getModel().getDataType());
        assertNull(view.getModel().getCustomDataType());
        assertNull(view.getModel().getProcessVar());
        assertEquals("abc",
                     view.getModelValue(dataType));
    }

    @Test
    public void testSetListBoxModelValueProcessVar() {
        assertNull(view.getModel().getProcessVar());
        view.setListBoxModelValue(processVar,
                                  "abc");
        assertEquals("abc",
                     view.getModel().getProcessVar());
        assertNull(view.getModel().getConstant());
        assertNull(view.getModel().getDataType());
        assertEquals("abc",
                     view.getModelValue(processVar));
    }

    @Test
    public void testDataTypeHandlerSpace() {
        view.init();
        verify(customDataType,
               times(1)).addKeyDownHandler(keyDownHandlerCaptor.capture());
        KeyDownHandler handler = keyDownHandlerCaptor.getValue();
        doReturn(Integer.valueOf(' ')).when(keyDownEvent).getNativeKeyCode();
        handler.onKeyDown(keyDownEvent);
        verify(keyDownEvent,
               times(1)).preventDefault();
    }

    @Test
    public void testDataTypeHandlerAlphabetical() {
        view.init();
        verify(customDataType,
               times(1)).addKeyDownHandler(keyDownHandlerCaptor.capture());
        KeyDownHandler handler = keyDownHandlerCaptor.getValue();
        doReturn(Integer.valueOf('a')).when(keyDownEvent).getNativeKeyCode();
        handler.onKeyDown(keyDownEvent);
        verify(keyDownEvent,
               never()).preventDefault();
    }

    @Test
    public void testNameBlurHandler() {
        ActivityDataIOEditorWidget parent = mock(ActivityDataIOEditorWidget.class);
        when(parent.isDuplicateName(anyString())).thenReturn(true);
        doReturn("anyName").when(name).getText();
        view.setAllowDuplicateNames(false,
                                    "ErrorMessage");
        view.setParentWidget(parent);
        view.init();
        verify(name,
               times(1)).addBlurHandler(blurHandlerCaptor.capture());
        BlurHandler handler = blurHandlerCaptor.getValue();
        handler.onBlur(mock(BlurEvent.class));
        verify(parent,
               times(1)).isDuplicateName("anyName");
        verify(notification,
               times(1)).fire(new NotificationEvent("ErrorMessage",
                                                    NotificationEvent.NotificationType.ERROR));
        verify(name,
               times(1)).setValue("");
    }

    @Test
    public void testSetShowConstantsTrue() {
        view.setShowConstants(true);
        verify(processVarComboBox).setShowCustomValues(true);
    }

    @Test
    public void testSetShowConstantsFalse() {
        view.setShowConstants(false);
        verify(processVarComboBox).setShowCustomValues(false);
    }

    @Test
    public void testSetDisallowedNames() {
        Set<String> disallowedNames = new HashSet<String>();
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
