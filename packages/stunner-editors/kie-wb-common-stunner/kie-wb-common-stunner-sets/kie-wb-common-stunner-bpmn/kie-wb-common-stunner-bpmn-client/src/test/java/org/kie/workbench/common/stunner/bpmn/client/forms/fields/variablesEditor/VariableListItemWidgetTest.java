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


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.variablesEditor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockito;
import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLLabelElement;
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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests the data get/set methods of VariableListItemWidget
 */
@RunWith(GwtMockitoTestRunner.class)
public class VariableListItemWidgetTest {

    ValueListBox<String> dataType;

    CustomDataTypeTextBox customDataType;

    ComboBox dataTypeComboBox;

    ComboBox tagNamesComboBox;

    @GwtMock
    ValueListBox<String> defaultTagNames;

    @GwtMock
    CustomDataTypeTextBox customTagName;

    @GwtMock
    HTMLLabelElement tagCount;

    @GwtMock
    HTMLAnchorElement variableTagsSettings;

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
        tagNamesComboBox = mock(ComboBox.class);
        customTagName = mock(CustomDataTypeTextBox.class);
        variableTagsSettings = mock(HTMLAnchorElement.class);
        defaultTagNames = mock(ValueListBox.class);
        tagCount = mock(HTMLLabelElement.class);
        widget = GWT.create(VariableListItemWidgetViewImpl.class);
        VariableRow variableRow = new VariableRow();
        widget.dataType = dataType;
        widget.customDataType = customDataType;
        widget.dataTypeComboBox = dataTypeComboBox;
        widget.name = name;
        widget.deleteButton = deleteButton;
        widget.variableRow = variable;
        widget.variableTagsSettings = variableTagsSettings;
        widget.tagCount = tagCount;
        widget.customTagName = customTagName;
        widget.tagNamesComboBox = tagNamesComboBox;
        widget.defaultTagNames = defaultTagNames;
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
        Mockito.doCallRealMethod().when(widget).setTagTypes(any(List.class));
        Mockito.doCallRealMethod().when(widget).getCustomTags();
        Mockito.doCallRealMethod().when(widget).renderTagElementsBadges();
        Mockito.doCallRealMethod().when(widget).handleBadgeCloseEvent(anyString(), any(), any(), any());
        Mockito.doCallRealMethod().when(widget).setParentWidget(any());
        Mockito.doCallRealMethod().when(widget).setTagSet(any());

        Mockito.doCallRealMethod().when(tagNamesComboBox).setListBoxValues(any());
        Mockito.doCallRealMethod().when(tagNamesComboBox).addCustomValueToListBoxValues(anyString(), anyString());
        Mockito.doCallRealMethod().when(tagNamesComboBox).getListBoxValues();

        when(widget.getModel()).thenReturn(variableRow);
    }

    @Test
    public void testInitWidget() {
        VariablesEditorWidgetView.Presenter presenter = mock(VariablesEditorWidgetView.Presenter.class);
        widget.setParentWidget(presenter);
        widget.tagNamesList = new ArrayList<>();
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
                                   anyString(),
                                   anyString());
        RegExp customValueRegExp = RegExp.compile(customValueRegExpCaptor.getValue());
        assertEquals(false, customValueRegExp.test("a 1"));
        assertEquals(true, customValueRegExp.test("<a1"));
        assertEquals(true, customValueRegExp.test("a1>"));
        assertEquals(true, customValueRegExp.test("<a1>"));
        assertEquals(false, customValueRegExp.test("<a1/>"));
        assertEquals(false, customValueRegExp.test("<a1\\>"));
        assertEquals(false, customValueRegExp.test("a@1"));
        assertEquals(true, customValueRegExp.test("a1"));
        assertEquals(true, customValueRegExp.test("org.kie.Object"));
        verify(customDataType, times(1)).addKeyDownHandler(any(KeyDownHandler.class));
    }

    @Test
    public void testSetTextBoxModelValue() {
        VariablesEditorWidgetView.Presenter presenter = mock(VariablesEditorWidgetView.Presenter.class);
        widget.setParentWidget(presenter);
        widget.setTextBoxModelValue(customDataType,
                                    "com.test.Pencil");
        verify(widget,
               times(1)).setCustomDataType("com.test.Pencil");
    }

    @Test
    public void testSetTextBoxModelValueForCustomTags() {
        widget.setTextBoxModelValue(customTagName,
                                    "myCustomTag");
        verify(widget,
               never()).setCustomDataType(anyString());
    }

    @Test
    public void testSetListBoxModelValue() {
        widget.setListBoxModelValue(dataType,
                                    "Paper [org.stationery");
        verify(widget,
               times(1)).setDataTypeDisplayName("Paper [org.stationery");
    }

    @Test
    public void testSetListBoxModelValueForCustomTags() {
        widget.setListBoxModelValue(defaultTagNames,
                                    "myCustomTag");
        verify(widget,
               never()).setDataTypeDisplayName(anyString());
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
        VariablesEditorWidgetView.Presenter presenter = mock(VariablesEditorWidgetView.Presenter.class);
        widget.setParentWidget(presenter);

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

    @Test
    public void testSetTags() {
        List<String> tags = Arrays.asList("internal", "input", "customTag");
        VariablesEditorWidgetView.Presenter presenter = mock(VariablesEditorWidgetView.Presenter.class);
        widget.setParentWidget(presenter);

        widget.setTagSet(new HashSet<>());
        HTMLLabelElement tagLabel = mock(HTMLLabelElement.class);
        HTMLAnchorElement tagCloseButton = mock(HTMLAnchorElement.class);

        when(widget.getBadgeElement(anyString())).thenReturn(tagLabel);
        when(widget.getBadgeCloseButton()).thenReturn(tagCloseButton);
        HTMLDivElement tagsContainer = mock(HTMLDivElement.class);
        widget.tagsContainer = tagsContainer;

        widget.removeButtons = new HashMap<>();
        widget.setTagTypes(tags);
        verify(tagNamesComboBox, times(1)).addCustomValueToListBoxValues("customTag", "");
    }

    @Test
    public void testHandleBadgeCloseEvent() {
        VariablesEditorWidgetView.Presenter presenter = mock(VariablesEditorWidgetView.Presenter.class);
        widget.setParentWidget(presenter);

        HTMLLabelElement tagLabel = mock(HTMLLabelElement.class);
        HTMLAnchorElement tagCloseButton = mock(HTMLAnchorElement.class);

        elemental2.dom.Event ex = mock(elemental2.dom.Event.class);
        ex.type = "SomeType";
        widget.setTagSet(new HashSet<>());

        widget.handleBadgeCloseEvent("internal", tagLabel, tagCloseButton, ex);

        verify(tagLabel, times(1)).remove();
        verify(tagCloseButton, times(1)).remove();
        // Updated Model
        verify(widget, times(1)).notifyModelChanged();

        ex.type = "DoNotUpdateModel";
        widget.setTagSet(new HashSet<>());

        widget.handleBadgeCloseEvent("internal", tagLabel, tagCloseButton, ex);

        verify(tagLabel, times(2)).remove();
        verify(tagCloseButton, times(2)).remove();
        // No New Calls to Updated Model
        verify(widget, times(1)).notifyModelChanged();
    }
}
