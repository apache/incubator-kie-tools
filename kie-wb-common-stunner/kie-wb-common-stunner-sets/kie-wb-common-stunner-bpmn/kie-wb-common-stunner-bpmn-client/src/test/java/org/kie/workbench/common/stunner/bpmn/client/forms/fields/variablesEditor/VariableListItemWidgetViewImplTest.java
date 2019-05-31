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

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.variablesEditor;

import javax.enterprise.event.Event;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
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
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.i18n.StunnerFormsClientFieldsConstants;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.Variable;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.VariableRow;
import org.kie.workbench.common.stunner.bpmn.client.forms.widgets.ComboBox;
import org.kie.workbench.common.stunner.bpmn.client.forms.widgets.CustomDataTypeTextBox;
import org.kie.workbench.common.stunner.bpmn.client.forms.widgets.VariableNameTextBox;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.workbench.events.NotificationEvent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class VariableListItemWidgetViewImplTest {

    private static final String VARIABLE_NAME = "variableName";
    private static final String MODEL_TO_STRING = "modelToString";
    private static final String CUST_DATA_TYPE_NAME = "custDataTypeName";
    private static final String DATA_TYPE_NAME = "dataTypeName";
    private static final String VARIABLE_NEW_NAME = "variableNewName";
    private static final String MODEL_NEW_TO_STRING = "modelNewToString";

    @GwtMock
    private DataBinder<VariableRow> variableRow;

    @GwtMock
    private VariableNameTextBox name;

    @GwtMock
    private Button deleteButton;

    private CustomDataTypeTextBox customDataType;

    private ValueListBox<String> dataType;

    private ComboBox dataTypeComboBox;

    private ComboBox processVarComboBox;

    @GwtMock
    private KeyDownEvent keyDownEvent;

    private Event<NotificationEvent> notification = mock(EventSourceMock.class);

    @Captor
    private ArgumentCaptor<KeyDownHandler> keyDownHandlerCaptor;

    @Captor
    private ArgumentCaptor<ChangeHandler> changeHandlerCaptor;

    private VariableListItemWidgetViewImpl view;

    @Mock
    private VariablesEditorWidgetView.Presenter parent;

    @Mock
    private ErrorPopupPresenter errorPopupPresenter;

    @Before
    public void setUp() throws Exception {
        GwtMockito.initMocks(this);
        customDataType = mock(CustomDataTypeTextBox.class);
        dataType = mock(ValueListBox.class);
        dataTypeComboBox = mock(ComboBox.class);
        processVarComboBox = mock(ComboBox.class);
        view = mock(VariableListItemWidgetViewImpl.class);
        view.variableRow = variableRow;
        view.name = name;
        view.deleteButton = deleteButton;
        view.customDataType = customDataType;
        view.dataType = dataType;
        view.dataTypeComboBox = dataTypeComboBox;
        view.notification = notification;
        view.errorPopupPresenter = errorPopupPresenter;
        doCallRealMethod().when(view).init();
        doCallRealMethod().when(view).getCustomDataType();
        doCallRealMethod().when(view).setCustomDataType(anyString());
        doCallRealMethod().when(view).getModel();
        doCallRealMethod().when(view).setModel(any(VariableRow.class));
        doCallRealMethod().when(view).getModelValue(any(ValueListBox.class));
        doCallRealMethod().when(view).setTextBoxModelValue(any(TextBox.class),
                                                           anyString());
        doCallRealMethod().when(view).setListBoxModelValue(any(ValueListBox.class),
                                                           anyString());
        doCallRealMethod().when(view).getDataTypeDisplayName();
        doCallRealMethod().when(view).setDataTypeDisplayName(anyString());
        doCallRealMethod().when(view).getVariableType();
        doCallRealMethod().when(view).setParentWidget(any(VariablesEditorWidgetView.Presenter.class));
        doCallRealMethod().when(view).handleDeleteButton(any(ClickEvent.class));
        doCallRealMethod().when(view).setReadOnly(anyBoolean());
        doCallRealMethod().when(view).notifyModelChanged();
        VariableRow row = new VariableRow();
        doReturn(row).when(variableRow).getModel();
    }

    @Test
    public void testSetProcessVarCustomDataType() {
        VariableRow row = new VariableRow();
        row.setName(VARIABLE_NAME);
        row.setCustomDataType(CUST_DATA_TYPE_NAME);
        row.setDataTypeDisplayName(null);
        row.setVariableType(Variable.VariableType.PROCESS);
        doReturn(row).when(variableRow).getModel();
        view.setModel(row);
        verify(variableRow,
               times(1)).setModel(row);
        verify(deleteButton,
               times(1)).setIcon(IconType.TRASH);
        verify(customDataType,
               times(1)).setValue(CUST_DATA_TYPE_NAME);
        verify(dataType,
               times(1)).setValue(CUST_DATA_TYPE_NAME);
    }

    @Test
    public void testSetProcessVarDataType() {
        VariableRow row = new VariableRow();
        row.setName(VARIABLE_NAME);
        row.setCustomDataType(null);
        row.setDataTypeDisplayName(DATA_TYPE_NAME);
        row.setVariableType(Variable.VariableType.PROCESS);
        doReturn(row).when(variableRow).getModel();
        view.setModel(row);
        verify(variableRow,
               times(1)).setModel(row);
        verify(deleteButton,
               times(1)).setIcon(IconType.TRASH);
        verify(customDataType,
               never()).setValue(DATA_TYPE_NAME);
        verify(dataType,
               times(1)).setValue(DATA_TYPE_NAME);
    }

    @Test
    public void testSetTextBoxModelValueCustomDataType() {
        assertNull(view.getModel().getCustomDataType());
        view.setTextBoxModelValue(customDataType,
                                  "abc");
        assertEquals("abc",
                     view.getModel().getCustomDataType());
        assertEquals("abc",
                     view.getModelValue(dataType));
    }

    @Test
    public void testSetListBoxModelValueDataType() {
        assertNull(view.getModel().getDataTypeDisplayName());
        view.setListBoxModelValue(dataType,
                                  "abc");
        assertEquals("abc",
                     view.getModel().getDataTypeDisplayName());
        assertNull(view.getModel().getCustomDataType());
        assertEquals("abc",
                     view.getModelValue(dataType));
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
    public void testNameChangeHandlerWhenDuplicate() {
        when(parent.isDuplicateName(VARIABLE_NEW_NAME)).thenReturn(true);
        prepareNameChange(VARIABLE_NEW_NAME, MODEL_NEW_TO_STRING);
        verify(parent).isDuplicateName(VARIABLE_NEW_NAME);
        verify(notification).fire(new NotificationEvent(StunnerFormsClientFieldsConstants.INSTANCE.DuplicatedVariableNameError(VARIABLE_NEW_NAME),
                                                        NotificationEvent.NotificationType.ERROR));
        verify(name).setValue(VARIABLE_NAME);
    }

    @Test
    public void testNameChangeHandlerWhenNotDuplicateAndNotBoundToNodes() {
        when(parent.isDuplicateName(VARIABLE_NEW_NAME)).thenReturn(false);
        when(parent.isBoundToNodes(VARIABLE_NAME)).thenReturn(false);
        prepareNameChange(VARIABLE_NEW_NAME, MODEL_NEW_TO_STRING);
        verify(parent).isDuplicateName(VARIABLE_NEW_NAME);
        verify(parent).isBoundToNodes(VARIABLE_NAME);
        verify(parent).notifyModelChanged();
    }

    @Test
    public void testNameChangeHandlerWhenNotDuplicateAndBoundToNodes() {
        when(parent.isDuplicateName(VARIABLE_NEW_NAME)).thenReturn(false);
        when(parent.isBoundToNodes(VARIABLE_NAME)).thenReturn(true);
        prepareNameChange(VARIABLE_NEW_NAME, MODEL_NEW_TO_STRING);
        verify(parent).isDuplicateName(VARIABLE_NEW_NAME);
        verify(parent).isBoundToNodes(VARIABLE_NAME);
        verify(name).setValue(VARIABLE_NAME);
        verify(errorPopupPresenter).showMessage(StunnerFormsClientFieldsConstants.INSTANCE.RenameDiagramVariableError());
    }

    private void prepareNameChange(String newName, String newToString) {
        doReturn(newName).when(name).getText();
        VariableRow model = mock(VariableRow.class);
        when(model.getName()).thenReturn(VARIABLE_NAME);
        when(model.toString()).thenReturn(MODEL_TO_STRING);
        doReturn(model).when(variableRow).getModel();
        view.setParentWidget(parent);
        view.init();
        view.setModel(model);
        when(model.toString()).thenReturn(newToString);
        verify(name).addChangeHandler(changeHandlerCaptor.capture());
        ChangeHandler handler = changeHandlerCaptor.getValue();
        handler.onChange(mock(ChangeEvent.class));
    }

    @Test
    public void testHandleDeleteButton() {
        VariableRow model = mock(VariableRow.class);
        when(view.getModel()).thenReturn(model);
        view.setParentWidget(parent);
        view.handleDeleteButton(null);
        verify(parent).removeVariable(model);
    }

    @Test
    public void testSetReadOnlyTrue() {
        view.setReadOnly(true);
        verify(deleteButton,
               times(1)).setEnabled(false);
        verify(dataTypeComboBox,
               times(1)).setReadOnly(true);
        verify(name,
               times(1)).setEnabled(false);
    }

    @Test
    public void testSetReadOnlyFalse() {
        view.setReadOnly(false);
        verify(deleteButton,
               times(1)).setEnabled(true);
        verify(dataTypeComboBox,
               times(1)).setReadOnly(false);
        verify(name,
               times(1)).setEnabled(true);
    }
}
