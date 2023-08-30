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



package org.kie.workbench.common.stunner.bpmn.client.forms.fields.multipleInstanceVariableEditor;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.gwtbootstrap3.client.ui.TextBox;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.forms.DataTypeNamesService;
import org.kie.workbench.common.stunner.bpmn.client.forms.widgets.ComboBox;
import org.kie.workbench.common.stunner.bpmn.client.forms.widgets.CustomDataTypeTextBox;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class MultipleInstanceVariableEditorViewTest {

    @Mock
    private MultipleInstanceVariableEditorPresenter multipleInstanceVariableEditorPresenter;

    @Mock
    private MultipleInstanceVariableEditorView multipleInstanceVariableEditorView;

    @Mock
    protected ComboBox dataTypeComboBox;

    @Mock
    protected CustomDataTypeTextBox customDataType;

    @Mock
    DataTypeNamesService clientDataTypesService;

    @Test
    public void testInit() {
        Mockito.doCallRealMethod().when(multipleInstanceVariableEditorView).init(any());
        Mockito.doCallRealMethod().when(multipleInstanceVariableEditorView).setTextBoxModelValue(any(), any());

        multipleInstanceVariableEditorView.customDataType = customDataType;
        multipleInstanceVariableEditorView.dataTypeComboBox = dataTypeComboBox;
        multipleInstanceVariableEditorView.clientDataTypesService = clientDataTypesService;

        final TextBox textBox = new TextBox();

        multipleInstanceVariableEditorView.setTextBoxModelValue(textBox, "MyModel");
        verify(clientDataTypesService, times(1)).add("MyModel", null);
        multipleInstanceVariableEditorView.setTextBoxModelValue(textBox, "");
        verify(clientDataTypesService, times(1)).add("MyModel", null);
        multipleInstanceVariableEditorView.setTextBoxModelValue(textBox, null);
        verify(clientDataTypesService, times(1)).add("MyModel", null);
    }
}