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


package org.kie.workbench.common.stunner.bpmn.client.forms.widgets;

import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.ValueListBox;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.ListBoxValues;
import org.kie.workbench.common.stunner.core.util.StringUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ComboBoxTest {

    @Mock
    ComboBoxView.ModelPresenter modelPresenter;

    @Mock
    ComboBoxView view;

    @Mock
    ListBoxValues listBoxValues;

    @Mock
    ValueListBox<String> listBox;

    @Mock
    TextBox textBox;

    @Spy
    @InjectMocks
    ComboBox comboBox = new ComboBox();

    final boolean processVarNotifyModelChanges = false;
    private final boolean processVarQuoteStringValues = true;
    private final boolean processVarAddCustomValues = true;
    private final String processVarConstantPrompt = "Constant ...";
    private final String processVarConstantPlaceholder = "Enter constant ...";
    private final String processVarEditPrefix = "Edit ";
    private final String processVarEditSuffix = " ...";

    @Before
    public void initMocks() {
        when(listBoxValues.getEditPrefix()).thenReturn(processVarEditPrefix);
    }

    @Test
    public void testInit() {
        comboBox.init(modelPresenter,
                      processVarNotifyModelChanges,
                      listBox,
                      textBox,
                      processVarQuoteStringValues,
                      processVarAddCustomValues,
                      processVarConstantPrompt,
                      processVarConstantPlaceholder);
        verify(view).init(comboBox,
                          modelPresenter,
                          listBox,
                          textBox,
                          processVarConstantPlaceholder);
    }

    @Test
    public void testUpdateListBoxValues_Custom() {
        comboBox.setShowCustomValues(true);
        String customValue = "a custom value";
        comboBox.updateListBoxValues(customValue);
        verify(listBoxValues).update(customValue);
        verify(view).setAcceptableValues(any(List.class));
    }

    @Test
    public void testUpdateListBoxValues_NonCustom() {
        comboBox.setShowCustomValues(false);
        String customValue = "a custom value";
        comboBox.updateListBoxValues(customValue);
        verify(listBoxValues).getAcceptableValuesWithoutCustomValues();
        verify(view).setAcceptableValues(any(List.class));
    }

    @Test
    public void testListBoxValueChanged_CustomPrompt() {
        comboBox.init(modelPresenter,
                      processVarNotifyModelChanges,
                      listBox,
                      textBox,
                      processVarQuoteStringValues,
                      processVarAddCustomValues,
                      processVarConstantPrompt,
                      processVarConstantPlaceholder);
        comboBox.listBoxValueChanged(processVarConstantPrompt);
        verify(view).setListBoxValue("");
        verify(view).setListBoxModelValue("");
        verify(view).setTextBoxValue("");
        verify(view).setTextBoxModelValue("");
        verify(view).setListBoxVisible(false);
        verify(view).setTextBoxVisible(true);
        verify(view).setTextBoxFocus(true);
    }

    @Test
    public void testListBoxValueChanged_InvalidValue() {
        comboBox.init(modelPresenter,
                      processVarNotifyModelChanges,
                      listBox,
                      textBox,
                      processVarQuoteStringValues,
                      processVarAddCustomValues,
                      processVarConstantPrompt,
                      processVarConstantPlaceholder);
        comboBox.listBoxValueChanged("********");
        verify(view).setListBoxValue("");
        verify(view).setListBoxModelValue("");
        verify(view).setTextBoxValue("");
        verify(view).setTextBoxModelValue("");
    }

    @Test
    public void testListBoxValueChanged_EditValueString() {
        comboBox.init(modelPresenter,
                      processVarNotifyModelChanges,
                      listBox,
                      textBox,
                      processVarQuoteStringValues,
                      processVarAddCustomValues,
                      processVarConstantPrompt,
                      processVarConstantPlaceholder);
        String constant = "a constant";
        when(view.getModelValue()).thenReturn(constant);
        comboBox.listBoxValueChanged(processVarEditPrefix + "\"" + constant + "\"" + processVarEditSuffix);
        verify(view).setListBoxVisible(false);
        verify(view).setTextBoxVisible(true);
        verify(view).setTextBoxFocus(true);
        verify(view).setTextBoxValue(constant);
        verify(view,
               times(0)).setListBoxValue(anyString());
    }

    @Test
    public void testListBoxValueChanged_EditValueNumber() {
        comboBox.init(modelPresenter,
                      processVarNotifyModelChanges,
                      listBox,
                      textBox,
                      processVarQuoteStringValues,
                      processVarAddCustomValues,
                      processVarConstantPrompt,
                      processVarConstantPlaceholder);
        String constant = "123";
        when(view.getModelValue()).thenReturn(constant);
        comboBox.listBoxValueChanged(processVarEditPrefix + constant + processVarEditSuffix);
        verify(view).setListBoxVisible(false);
        verify(view).setTextBoxVisible(true);
        verify(view).setTextBoxFocus(true);
        verify(view).setTextBoxValue(constant);
        verify(view,
               times(0)).setListBoxValue(anyString());
    }

    @Test
    public void testListBoxValueChanged_CustomValueString() {
        comboBox.init(modelPresenter,
                      processVarNotifyModelChanges,
                      listBox,
                      textBox,
                      processVarQuoteStringValues,
                      processVarAddCustomValues,
                      processVarConstantPrompt,
                      processVarConstantPlaceholder);
        String constant = "a constant";
        String quotedConstant = StringUtils.createQuotedStringIfNotNumeric(constant);
        when(listBoxValues.isCustomValue(quotedConstant)).thenReturn(true);
        when(listBoxValues.getValueForDisplayValue(quotedConstant)).thenReturn(quotedConstant);
        comboBox.listBoxValueChanged(quotedConstant);
        verify(view).setListBoxValue(quotedConstant);
        verify(view).setListBoxModelValue(quotedConstant);
        verify(view).setTextBoxValue(constant);
        verify(view).setTextBoxModelValue(constant);
    }

    @Test
    public void testListBoxValueChanged_CustomValueQString1() {
        comboBox.init(modelPresenter,
                      processVarNotifyModelChanges,
                      listBox,
                      textBox,
                      processVarQuoteStringValues,
                      processVarAddCustomValues,
                      processVarConstantPrompt,
                      processVarConstantPlaceholder);
        String constant = "\"hello\"";
        String quotedConstant = StringUtils.createQuotedStringIfNotNumeric(constant);
        when(listBoxValues.isCustomValue(quotedConstant)).thenReturn(true);
        when(listBoxValues.getValueForDisplayValue(quotedConstant)).thenReturn(quotedConstant);
        comboBox.listBoxValueChanged(quotedConstant);
        verify(view).setListBoxValue(quotedConstant);
        verify(view).setListBoxModelValue(quotedConstant);
        verify(view).setTextBoxValue(constant);
        verify(view).setTextBoxModelValue(constant);
    }

    @Test
    public void testListBoxValueChanged_CustomValueQString2() {
        comboBox.init(modelPresenter,
                      processVarNotifyModelChanges,
                      listBox,
                      textBox,
                      processVarQuoteStringValues,
                      processVarAddCustomValues,
                      processVarConstantPrompt,
                      processVarConstantPlaceholder);
        String constant = "greeting={\"hello\"}";
        String quotedConstant = StringUtils.createQuotedStringIfNotNumeric(constant);
        when(listBoxValues.isCustomValue(quotedConstant)).thenReturn(true);
        when(listBoxValues.getValueForDisplayValue(quotedConstant)).thenReturn(quotedConstant);
        comboBox.listBoxValueChanged(quotedConstant);
        verify(view).setListBoxValue(quotedConstant);
        verify(view).setListBoxModelValue(quotedConstant);
        verify(view).setTextBoxValue(constant);
        verify(view).setTextBoxModelValue(constant);
    }

    @Test
    public void testListBoxValueChanged_CustomValueNumber() {
        comboBox.init(modelPresenter,
                      processVarNotifyModelChanges,
                      listBox,
                      textBox,
                      processVarQuoteStringValues,
                      processVarAddCustomValues,
                      processVarConstantPrompt,
                      processVarConstantPlaceholder);
        String constant = "123";
        when(listBoxValues.isCustomValue(constant)).thenReturn(true);
        when(listBoxValues.getValueForDisplayValue(constant)).thenReturn(constant);
        comboBox.listBoxValueChanged(constant);
        verify(view).setListBoxValue(constant);
        verify(view).setListBoxModelValue(constant);
        verify(view).setTextBoxValue(constant);
        verify(view).setTextBoxModelValue(constant);
    }

    @Test
    public void testListBoxValueChanged_ExistingValueString() {
        comboBox.init(modelPresenter,
                      processVarNotifyModelChanges,
                      listBox,
                      textBox,
                      processVarQuoteStringValues,
                      processVarAddCustomValues,
                      processVarConstantPrompt,
                      processVarConstantPlaceholder);
        String value = "employee";
        when(listBoxValues.isCustomValue(value)).thenReturn(false);
        comboBox.listBoxValueChanged(value);
        verify(view).setListBoxValue(value);
        verify(view).setListBoxModelValue(value);
        verify(view).setTextBoxValue("");
        verify(view).setTextBoxModelValue("");
    }

    @Test
    public void testListBoxValueChanged_ExistingValueNumber() {
        comboBox.init(modelPresenter,
                      processVarNotifyModelChanges,
                      listBox,
                      textBox,
                      processVarQuoteStringValues,
                      processVarAddCustomValues,
                      processVarConstantPrompt,
                      processVarConstantPlaceholder);
        String value = "123";
        when(listBoxValues.isCustomValue(value)).thenReturn(false);
        comboBox.listBoxValueChanged(value);
        verify(view).setListBoxValue(value);
        verify(view).setListBoxModelValue(value);
        verify(view).setTextBoxValue("");
        verify(view).setTextBoxModelValue("");
    }

    @Test
    public void testTextBoxValueChanged_Empty() {
        comboBox.init(modelPresenter,
                      processVarNotifyModelChanges,
                      listBox,
                      textBox,
                      processVarQuoteStringValues,
                      processVarAddCustomValues,
                      processVarConstantPrompt,
                      processVarConstantPlaceholder);
        comboBox.textBoxValueChanged("");
        verify(view).setListBoxValue("");
        verify(view).setListBoxModelValue("");
        verify(view).setTextBoxValue("");
        verify(view).setTextBoxModelValue("");
        verify(view).setTextBoxVisible(false);
        verify(view).setListBoxVisible(true);
    }

    @Test
    public void testTextBoxValueChanged_NonCustomValue() {
        comboBox.init(modelPresenter,
                      processVarNotifyModelChanges,
                      listBox,
                      textBox,
                      processVarQuoteStringValues,
                      processVarAddCustomValues,
                      processVarConstantPrompt,
                      processVarConstantPlaceholder);
        String value = "employee";
        when(listBoxValues.getNonCustomValueForUserString(value)).thenReturn(value);
        comboBox.textBoxValueChanged(value);
        verify(view).setListBoxValue(value);
        verify(view).setListBoxModelValue(value);
        verify(view).setTextBoxValue("");
        verify(view).setTextBoxModelValue("");
        verify(view).setTextBoxVisible(false);
        verify(view).setListBoxVisible(true);
    }

    @Test
    public void testTextBoxValueChanged_CustomValue() {
        comboBox.init(modelPresenter,
                      processVarNotifyModelChanges,
                      listBox,
                      textBox,
                      processVarQuoteStringValues,
                      processVarAddCustomValues,
                      processVarConstantPrompt,
                      processVarConstantPlaceholder);
        String value = "something new";
        String quotedValue = StringUtils.createQuotedStringIfNotNumeric(value);
        when(listBoxValues.getNonCustomValueForUserString(value)).thenReturn(null);
        when(comboBox.addCustomValueToListBoxValues(value,
                                                    "")).thenReturn(quotedValue);
        comboBox.textBoxValueChanged(value);
        verify(comboBox,
               times(2)).addCustomValueToListBoxValues(value,
                                                       "");
        verify(view).setListBoxValue(quotedValue);
        verify(view).setListBoxModelValue(quotedValue);
        verify(view).setTextBoxValue(value);
        verify(view).setTextBoxModelValue(value);
        verify(view).setTextBoxVisible(false);
        verify(view).setListBoxVisible(true);
    }

    @Test
    public void testSetReadOnlyTrue() {
        comboBox.setReadOnly(true);
        verify(view,
               times(1)).setReadOnly(true);
    }

    @Test
    public void testSetReadOnlyFalse() {
        comboBox.setReadOnly(false);
        verify(view,
               times(1)).setReadOnly(false);
    }
}
