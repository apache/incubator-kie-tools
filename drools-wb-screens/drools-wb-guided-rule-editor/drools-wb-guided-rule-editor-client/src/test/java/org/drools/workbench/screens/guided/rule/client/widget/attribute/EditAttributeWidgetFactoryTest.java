/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.rule.client.widget.attribute;

import java.util.Date;

import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.drools.workbench.models.datamodel.rule.RuleAttribute;
import org.gwtbootstrap3.client.ui.TextBox;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.soup.project.datamodel.oracle.DataType;
import org.kie.workbench.common.widgets.client.widget.LiteralTextBox;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.ext.widgets.common.client.common.DatePicker;
import org.uberfire.ext.widgets.common.client.common.NumericLongTextBox;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
// Need to stub used things from: org.kie.workbench.common.widgets.client.widget.TextBoxFactory.getTextBox()
@WithClassesToStub({NumericLongTextBox.class, LiteralTextBox.class, DatePicker.class, DateTimeFormat.class})
public class EditAttributeWidgetFactoryTest {

    private boolean isReadOnly = false;

    @Mock
    private RuleAttribute ruleAttribute;

    @Captor
    private ArgumentCaptor<ValueChangeHandler> valueChangeHandlerArgumentCaptor;

    EditAttributeWidgetFactory factory;

    @Before
    public void setUp() throws Exception {
        factory = spy(new EditAttributeWidgetFactory(isReadOnly));
    }

    @Test
    public void testLiteralEditBox() {
        final TextBox textBox = factory.textBox(ruleAttribute, DataType.TYPE_STRING);
        assertThat(textBox).isInstanceOf(LiteralTextBox.class);
        verify(factory).initTextBoxByRuleAttribute(textBox, ruleAttribute);
    }

    @Test
    public void testNumericLongEditBox() {
        final TextBox textBox = factory.textBox(ruleAttribute, DataType.TYPE_NUMERIC_LONG);
        assertThat(textBox).isInstanceOf(NumericLongTextBox.class);
        verify(factory).initTextBoxByRuleAttribute(textBox, ruleAttribute);
    }

    @Test
    public void testDatePicker() {
        final DatePicker picker = factory.datePicker(ruleAttribute, false);
        verify(factory).initDatePickerByRuleAttribute(picker, ruleAttribute);
    }

    @Test
    public void testInitTextBoxByRuleAttribute() {
        final TextBox textBox = mock(TextBox.class);
        final String attributeValue = "123";
        doReturn(attributeValue).when(ruleAttribute).getValue();

        factory.initTextBoxByRuleAttribute(textBox, ruleAttribute);
        verify(textBox).setEnabled(!isReadOnly);
        verify(textBox).setValue(attributeValue);
    }

    @Test
    public void testTextBoxValueChangeHandler() {
        final TextBox textBox = mock(TextBox.class);
        final String textBoxValue = "123";
        doReturn(textBoxValue).when(textBox).getValue();

        factory.initTextBoxByRuleAttribute(textBox, ruleAttribute);
        verify(textBox).addValueChangeHandler(valueChangeHandlerArgumentCaptor.capture());

        valueChangeHandlerArgumentCaptor.getValue().onValueChange(null);
        verify(ruleAttribute).setValue(textBoxValue);
    }

    @Test
    public void testInitDatePickerByRuleAttribute() {
        final DatePicker datePicker = mock(DatePicker.class);
        final String attributeValue = "31-May-2018";
        doReturn(attributeValue).when(ruleAttribute).getValue();

        factory.initDatePickerByRuleAttribute(datePicker, ruleAttribute);
        // not robust verifications because of Date formatting / parsing is mocked by GwtMockito
        verify(datePicker).setFormat(any());
        verify(datePicker).setValue(notNull(Date.class));
    }

    @Test
    public void testDatePickerValueChangeHandler() {
        final DatePicker datePicker = mock(DatePicker.class);
        final Date datePickerValue = new Date();
        doReturn(datePickerValue).when(datePicker).getValue();

        factory.initDatePickerByRuleAttribute(datePicker, ruleAttribute);
        verify(datePicker).addValueChangeHandler(valueChangeHandlerArgumentCaptor.capture());

        valueChangeHandlerArgumentCaptor.getValue().onValueChange(null);
        // not robust verifications because of Date formatting / parsing is mocked by GwtMockito
        verify(ruleAttribute).setValue(anyString());
    }

    @Test
    public void testDatePickerValueChangeHandlerNullValue() {
        final DatePicker datePicker = mock(DatePicker.class);
        final Date datePickerValue = null;
        doReturn(datePickerValue).when(datePicker).getValue();

        factory.initDatePickerByRuleAttribute(datePicker, ruleAttribute);
        verify(datePicker).addValueChangeHandler(valueChangeHandlerArgumentCaptor.capture());

        valueChangeHandlerArgumentCaptor.getValue().onValueChange(null);
        verify(ruleAttribute).setValue(eq(null));
    }
}
