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


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.importsEditor.popup.editor.defaultImport;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockito;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.ValueListBox;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.ListBoxValues;
import org.kie.workbench.common.stunner.bpmn.client.forms.widgets.ComboBox;
import org.kie.workbench.common.stunner.bpmn.client.forms.widgets.CustomDataTypeTextBox;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.imports.DefaultImport;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.kie.workbench.common.stunner.bpmn.client.forms.fields.importsEditor.popup.editor.defaultImport.DefaultImportListItemWidgetView.CUSTOM_PROMPT;
import static org.kie.workbench.common.stunner.bpmn.client.forms.fields.importsEditor.popup.editor.defaultImport.DefaultImportListItemWidgetView.ENTER_TYPE_PROMPT;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultImportListItemWidgetViewTest {

    @GwtMock
    private DataBinder<DefaultImport> defaultImportDataBinder;

    @GwtMock
    private Button deleteButton;

    @GwtMock
    private KeyDownEvent keyDownEvent;

    @Captor
    private ArgumentCaptor<KeyDownHandler> keyDownHandlerCaptor;

    @Mock
    private DefaultImportsEditorWidget parent;

    private CustomDataTypeTextBox customClassName;

    private ValueListBox<String> defaultClassNames;

    private ComboBox classNamesComboBox;

    private DefaultImportListItemWidgetView tested;

    @Before
    public void setUp() throws Exception {
        GwtMockito.initMocks(this);
        customClassName = mock(CustomDataTypeTextBox.class);
        defaultClassNames = mock(ValueListBox.class);
        classNamesComboBox = mock(ComboBox.class);
        tested = mock(DefaultImportListItemWidgetView.class);
        tested.defaultImportDataBinder = defaultImportDataBinder;
        tested.deleteButton = deleteButton;
        tested.customClassName = customClassName;
        tested.defaultClassNames = defaultClassNames;
        tested.classNamesComboBox = classNamesComboBox;

//        tested.parentWidget = new DefaultImportsEditorWidget(mock(SessionManager.class),
//                                                             mock(DataTypeNamesService.class),
//                                                             mock(Event.class));
        tested.parentWidget = parent;
        Map<String, String> map = new TreeMap<>();
        map.put("Boolean", "Boolean");
        when(parent.getDataTypes()).thenReturn(map);
        when(parent.getDataType("Boolean")).thenReturn("Boolean");
        when(parent.getDataType("randomValue")).thenReturn("randomValue");

        doCallRealMethod().when(tested).init();
        doCallRealMethod().when(tested).initListItem();
        doCallRealMethod().when(tested).getModel();
        doCallRealMethod().when(tested).setModel(any());
        doCallRealMethod().when(tested).getModelValue(any());
        doCallRealMethod().when(tested).setTextBoxModelValue(any(),
                                                             anyString());
        doCallRealMethod().when(tested).setListBoxModelValue(any(),
                                                             anyString());
        doCallRealMethod().when(tested).setParentWidget(any());
        doCallRealMethod().when(tested).handleDeleteButton(any());

        DefaultImport defaultImport = new DefaultImport();
        doReturn(defaultImport).when(defaultImportDataBinder).getModel();
    }

    @Test
    public void setTextBoxModelValue() {
        tested.setTextBoxModelValue(customClassName, "abc");
        tested.setTextBoxModelValue(customClassName, "");
        tested.setTextBoxModelValue(customClassName, null);

        assertEquals("abc", tested.getModel().getClassName());
        assertEquals("abc", tested.getModelValue(null));
    }

    @Test
    public void setListBoxModelValue() {
        assertNull(tested.getModel().getClassName());
        tested.setListBoxModelValue(defaultClassNames, "Boolean");
        assertEquals("Boolean", tested.getModel().getClassName());
        assertEquals("Boolean", tested.getModelValue(defaultClassNames));
    }

    @Test
    public void getModelValue() {
        tested.getModelValue(null);
        verify(tested).getModel();
        verify(defaultImportDataBinder).getModel();
    }

    @Test
    public void init() {
        tested.init();
        verify(customClassName, times(1)).addKeyDownHandler(keyDownHandlerCaptor.capture());
        KeyDownHandler handler = keyDownHandlerCaptor.getValue();
        doReturn(Integer.valueOf('1')).when(keyDownEvent).getNativeKeyCode();
        handler.onKeyDown(keyDownEvent);
        verify(keyDownEvent, times(0)).preventDefault();

        doReturn(Integer.valueOf(' ')).when(keyDownEvent).getNativeKeyCode();
        handler.onKeyDown(keyDownEvent);
        verify(keyDownEvent, times(1)).preventDefault();
    }

    @Test
    public void initListItem() {
        DefaultImport defaultImport = tested.getModel();
        defaultImport.setClassName(null);
        tested.initListItem();

        defaultImport = tested.getModel();
        defaultImport.setClassName("");
        tested.initListItem();

        defaultImport = tested.getModel();
        defaultImport.setClassName("randomValue");
        Map<String, String> map = new HashMap<>();
        map.put("randomValue", "randomValue");
        map.put("Boolean", "Boolean");

        when(parent.getDataTypes()).thenReturn(map);
        tested.initListItem();

        defaultImport = tested.getModel();
        defaultImport.setClassName("Boolean");
        tested.initListItem();

        verify(defaultClassNames, times(2)).setValue(null);
        verify(defaultClassNames, times(1)).setValue("Boolean");
        verify(defaultClassNames, times(1)).setValue("randomValue");
        verify(classNamesComboBox, times(4)).setShowCustomValues(true);
        verify(classNamesComboBox, times(4)).setListBoxValues(any(ListBoxValues.class));
        verify(classNamesComboBox, times(4)).init(tested,
                                                  true,
                                                  defaultClassNames,
                                                  customClassName,
                                                  false,
                                                  true,
                                                  CUSTOM_PROMPT,
                                                  ENTER_TYPE_PROMPT);
    }

    @Test
    public void getModel() {
        tested.getModel();
        verify(defaultImportDataBinder).getModel();
    }

    @Test
    public void setModel() {
        DefaultImport model = mock(DefaultImport.class);
        tested.setModel(model);
        verify(defaultImportDataBinder).setModel(model);
    }

    @Test
    public void handleDeleteButton() {
        DefaultImport model = mock(DefaultImport.class);
        when(tested.getModel()).thenReturn(model);
        tested.setParentWidget(parent);
        tested.handleDeleteButton(null);
        verify(parent).removeImport(model);
    }
}