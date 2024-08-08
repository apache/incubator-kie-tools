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


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.importsEditor;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwtmockito.GwtMockito;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.TextBox;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.i18n.StunnerFormsClientFieldsConstants;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.importsEditor.popup.ImportsEditor;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.imports.DefaultImport;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.imports.ImportsValue;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.imports.WSDLImport;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Ignore("https://github.com/apache/incubator-kie-issues/issues/1431")
@RunWith(MockitoJUnitRunner.class)
public class ImportsFieldEditorWidgetTest {

    private static final String CLASSNAME = "Classname";
    private static final String LOCATION = "Location";
    private static final String NAMESPACE = "Namespace";

    private ImportsFieldEditorWidget tested;

    @Before
    public void setUp() {
        GwtMockito.initMocks(this);

        tested = GWT.create(ImportsFieldEditorWidget.class);
        tested.importsEditor = mock(ImportsEditor.class);
        tested.importsButton = mock(Button.class);
        tested.importsTextBox = mock(TextBox.class);

        doCallRealMethod().when(tested).getValue();
        doCallRealMethod().when(tested).setValue(any());
        doCallRealMethod().when(tested).setValue(any(), anyBoolean());

        doCallRealMethod().when(tested).setImportsCount(any());
        doCallRealMethod().when(tested).buildImportsCountString(anyInt(), anyInt());
        doCallRealMethod().when(tested).buildDefaultImportsCountString(anyInt());
        doCallRealMethod().when(tested).buildWSDLImportsCountString(anyInt());

        doCallRealMethod().when(tested).copyImportsValue(any());

        doCallRealMethod().when(tested).showImportsEditor();

        doCallRealMethod().when(tested).onClickImportsButton(any());
        doCallRealMethod().when(tested).onClickImportsTextBox(any());
    }

    @Test
    public void getValue() {
        tested.importsValue = createImportsValue(2, 2);

        ImportsValue result = tested.getValue();
        assertEquals(tested.importsValue, result);
    }

    @Test
    public void setValue() {
        ImportsValue importsValue = createImportsValue(3, 3);
        tested.setValue(importsValue);

        verify(tested, times(1)).copyImportsValue(any());
        assertEquals(tested.importsValue, importsValue);
        verify(tested, times(1)).setImportsCount(importsValue);
    }

    @Test
    public void setImportsCount() {
        int defaultImportsQty = 5;
        int wsdlImportsQty = 4;
        ImportsValue importsValue = createImportsValue(defaultImportsQty, wsdlImportsQty);
        tested.setImportsCount(importsValue);
        verify(tested, times(1)).buildImportsCountString(defaultImportsQty, wsdlImportsQty);
        verify(tested.importsTextBox, times(1)).setText(anyString());
    }

    @Test
    public void buildImportsCountString() {
        String result1 = tested.buildImportsCountString(0, 0);
        assertEquals(StunnerFormsClientFieldsConstants.CONSTANTS.No_Imports(), result1);

        String result2 = tested.buildImportsCountString(5, 3);
        assertNotEquals(StunnerFormsClientFieldsConstants.CONSTANTS.No_Imports(), result2);

        String result3 = tested.buildImportsCountString(5, 0);
        assertNotEquals(StunnerFormsClientFieldsConstants.CONSTANTS.No_Imports(), result3);

        String result4 = tested.buildImportsCountString(0, 3);
        assertNotEquals(StunnerFormsClientFieldsConstants.CONSTANTS.No_Imports(), result4);
    }

    @Test
    public void buildDefaultImportsCountString() {
        String result1 = tested.buildDefaultImportsCountString(0);
        assertEquals(StunnerFormsClientFieldsConstants.CONSTANTS.No_Data_Type_Import(), result1);

        String result2 = tested.buildDefaultImportsCountString(1);
        assertEquals(StunnerFormsClientFieldsConstants.CONSTANTS.Data_Type_Import(), result2);

        String result3 = tested.buildDefaultImportsCountString(15);
        assertTrue(result3.contains(StunnerFormsClientFieldsConstants.CONSTANTS.Data_Type_Imports()));
    }

    @Test
    public void buildWSDLImportsCountString() {
        String result1 = tested.buildWSDLImportsCountString(0);
        assertEquals(StunnerFormsClientFieldsConstants.CONSTANTS.No_WSDL_Import(), result1);

        String result2 = tested.buildWSDLImportsCountString(1);
        assertEquals(StunnerFormsClientFieldsConstants.CONSTANTS.WSDL_Import(), result2);

        String result3 = tested.buildWSDLImportsCountString(15);
        assertTrue(result3.contains(StunnerFormsClientFieldsConstants.CONSTANTS.WSDL_Imports()));
    }

    @Test
    public void copyImportsValue() {
        ImportsValue result1 = tested.copyImportsValue(null);
        assertNotNull(result1);

        ImportsValue importsValue = createImportsValue(5, 5);
        ImportsValue result2 = tested.copyImportsValue(importsValue);
        assertEquals(importsValue.getDefaultImports().size(), result2.getDefaultImports().size());
        assertEquals(importsValue.getWSDLImports().size(), result2.getWSDLImports().size());

        importsValue.setDefaultImports(new ArrayList<>());
        importsValue.setWSDLImports(new ArrayList<>());
        assertNotEquals(importsValue.getDefaultImports().size(), result2.getDefaultImports().size());
        assertNotEquals(importsValue.getWSDLImports().size(), result2.getWSDLImports().size());
    }

    @Test
    public void showImportsEditor() {
        tested.showImportsEditor();
        verify(tested.importsEditor, times(1)).setImportsValue(any(ImportsValue.class));
    }

    @Test
    public void onClickImportsButton() {
        tested.onClickImportsButton(any(ClickEvent.class));
        verify(tested, times(1)).showImportsEditor();
    }

    @Test
    public void onClickImportsTextBox() {
        tested.onClickImportsTextBox(any(ClickEvent.class));
        verify(tested, times(1)).showImportsEditor();
    }

    private ImportsValue createImportsValue(int defaultImportsQty, int wsdlImportsQty) {
        ImportsValue importsValue = new ImportsValue();

        for (int i = 0; i < defaultImportsQty; i++) {
            DefaultImport defaultImport = new DefaultImport(CLASSNAME);
            importsValue.addImport(defaultImport);
        }

        for (int i = 0; i < wsdlImportsQty; i++) {
            WSDLImport wsdlImport = new WSDLImport(LOCATION, NAMESPACE);
            importsValue.addImport(wsdlImport);
        }

        return importsValue;
    }
}