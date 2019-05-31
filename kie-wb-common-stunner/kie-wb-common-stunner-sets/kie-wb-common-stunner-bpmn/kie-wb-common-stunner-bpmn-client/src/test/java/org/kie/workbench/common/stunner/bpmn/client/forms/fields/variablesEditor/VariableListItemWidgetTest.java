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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.regexp.shared.RegExp;
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
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.Variable;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.VariableRow;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.ListBoxValues;
import org.kie.workbench.common.stunner.bpmn.client.forms.widgets.ComboBox;
import org.kie.workbench.common.stunner.bpmn.client.forms.widgets.CustomDataTypeTextBox;
import org.kie.workbench.common.stunner.bpmn.client.forms.widgets.VariableNameTextBox;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests the data get/set methods of VariableListItemWidget
 */
@RunWith(MockitoJUnitRunner.class)
public class VariableListItemWidgetTest {

    ValueListBox<String> dataType;

    CustomDataTypeTextBox customDataType;

    ComboBox dataTypeComboBox;

    @GwtMock
    VariableNameTextBox name;

    @GwtMock
    Button deleteButton;

    @GwtMock
    DataBinder<VariableRow> variable;

    //@Spy  // - cannot make Spy because of GWT error
    //@InjectMocks // - cannot InjectMocks because of GWT error
    private VariableListItemWidgetViewImpl widget;

    @Before
    public void initTestCase() {
        GwtMockito.initMocks(this);
        dataType = mock(ValueListBox.class);
        customDataType = mock(CustomDataTypeTextBox.class);
        dataTypeComboBox = mock(ComboBox.class);
        widget = GWT.create(VariableListItemWidgetViewImpl.class);
        VariableRow variableRow = new VariableRow();
        widget.dataType = dataType;
        widget.customDataType = customDataType;
        widget.dataTypeComboBox = dataTypeComboBox;
        widget.name = name;
        widget.deleteButton = deleteButton;
        widget.variableRow = variable;
        Mockito.doCallRealMethod().when(widget).setTextBoxModelValue(any(TextBox.class),
                                                                     anyString());
        Mockito.doCallRealMethod().when(widget).setListBoxModelValue(any(ValueListBox.class),
                                                                     anyString());
        Mockito.doCallRealMethod().when(widget).getModelValue(any(ValueListBox.class));
        Mockito.doCallRealMethod().when(widget).setDataTypeDisplayName(anyString());
        Mockito.doCallRealMethod().when(widget).getDataTypeDisplayName();
        Mockito.doCallRealMethod().when(widget).setCustomDataType(anyString());
        Mockito.doCallRealMethod().when(widget).getCustomDataType();
        Mockito.doCallRealMethod().when(widget).setDataTypes(any(ListBoxValues.class));
        Mockito.doCallRealMethod().when(widget).init();
        Mockito.doCallRealMethod().when(widget).setModel(any(VariableRow.class));
        when(widget.getModel()).thenReturn(variableRow);
    }

    @Test
    public void testInitWidget() {
        widget.init();
        verify(widget,
               times(1)).init();
        verify(dataTypeComboBox,
               times(1)).init(widget,
                              true,
                              dataType,
                              customDataType,
                              false,
                              true,
                              VariableListItemWidgetView.CUSTOM_PROMPT,
                              VariableListItemWidgetView.ENTER_TYPE_PROMPT);
        ArgumentCaptor<String> nameRegExpCaptor = ArgumentCaptor.forClass(String.class);
        verify(name,
               times(1)).setRegExp(nameRegExpCaptor.capture(),
                                   anyString(),
                                   anyString());
        RegExp nameRegExp = RegExp.compile(nameRegExpCaptor.getValue());
        assertEquals(false, nameRegExp.test("a 1"));
        assertEquals(false, nameRegExp.test("a@1"));
        assertEquals(true, nameRegExp.test("a1"));
        verify(name, times(1)).addChangeHandler(any(ChangeHandler.class));
        ArgumentCaptor<String> customValueRegExpCaptor = ArgumentCaptor.forClass(String.class);
        verify(customDataType,
               times(1)).setRegExp(customValueRegExpCaptor.capture(),
                                   anyString(),
                                   anyString());
        RegExp customValueRegExp = RegExp.compile(customValueRegExpCaptor.getValue());
        assertEquals(false, customValueRegExp.test("a 1"));
        assertEquals(false, customValueRegExp.test("<a1"));
        assertEquals(false, customValueRegExp.test("a1>"));
        assertEquals(false, customValueRegExp.test("<a1>"));
        assertEquals(false, customValueRegExp.test("<a1/>"));
        assertEquals(false, customValueRegExp.test("<a1\\>"));
        assertEquals(false, customValueRegExp.test("a@1"));
        assertEquals(true, customValueRegExp.test("a1"));
        assertEquals(true, customValueRegExp.test("org.kie.Object"));
        verify(customDataType, times(1)).addKeyDownHandler(any(KeyDownHandler.class));
    }

    @Test
    public void testSetTextBoxModelValue() {
        widget.setTextBoxModelValue(customDataType,
                                    "com.test.Pencil");
        verify(widget,
               times(1)).setCustomDataType("com.test.Pencil");
    }

    @Test
    public void testSetListBoxModelValue() {
        widget.setListBoxModelValue(dataType,
                                    "Paper [org.stationery");
        verify(widget,
               times(1)).setDataTypeDisplayName("Paper [org.stationery");
    }

    @Test
    public void testSetModel() {
        when(widget.getVariableType()).thenReturn(Variable.VariableType.PROCESS);
        widget.setModel(new VariableRow());
        verify(deleteButton).setIcon(IconType.TRASH);
        verify(widget).getCustomDataType();
        verify(widget).getDataTypeDisplayName();
    }

    @Test
    public void testSetGetCustomDataType() {
        String customDataType = "com.test.MyType";
        widget.setTextBoxModelValue(widget.customDataType,
                                    customDataType);
        String returnedCustomDataType1 = widget.getCustomDataType();
        assertEquals(customDataType,
                     returnedCustomDataType1);
        String returnedCustomDataType2 = widget.getModelValue(widget.dataType);
        assertEquals(customDataType,
                     returnedCustomDataType2);
    }

    @Test
    public void testSetGetDataType() {
        String sDataType = "Boolean";
        widget.setListBoxModelValue(widget.dataType,
                                    sDataType);
        String returnedDataType1 = widget.getDataTypeDisplayName();
        assertEquals(sDataType,
                     returnedDataType1);
        String returnedDataType2 = widget.getModelValue(widget.dataType);
        assertEquals(sDataType,
                     returnedDataType2);
    }

    @Test
    public void testSetDataTypes() {
        ListBoxValues dataTypeListBoxValues = new ListBoxValues(null,
                                                                null,
                                                                null);
        String sCustomType = "com.test.CustomType";
        widget.setCustomDataType(sCustomType);
        widget.setDataTypes(dataTypeListBoxValues);
        verify(dataTypeComboBox).setListBoxValues(dataTypeListBoxValues);
        verify(dataTypeComboBox).addCustomValueToListBoxValues(sCustomType,
                                                               "");
    }
}
