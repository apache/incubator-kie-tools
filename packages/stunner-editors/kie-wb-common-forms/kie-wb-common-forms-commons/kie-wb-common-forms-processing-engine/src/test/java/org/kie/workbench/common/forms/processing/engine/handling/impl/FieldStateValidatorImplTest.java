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


package org.kie.workbench.common.forms.processing.engine.handling.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ValueLabel;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.processing.engine.handling.CustomFieldValidator;
import org.kie.workbench.common.forms.processing.engine.handling.FormField;
import org.kie.workbench.common.forms.processing.engine.handling.ValidationResult;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class FieldStateValidatorImplTest extends AbstractFormEngineTest {

    private static final String MESSAGE = "message";

    @Mock
    TranslationService translationService;

    @Mock
    FormField labelField;

    @Mock
    FormField valueLabelField;

    @Mock
    FormField textBoxField;

    @Mock
    TextBox textBox;

    List<FormField> fieldCollection;

    FieldStateValidatorImpl fieldStateValidator;

    @Before
    public void init() {
        super.init();

        fieldStateValidator = spy(new FieldStateValidatorImpl(translationService));
        testInitialize();

        initFields();
    }

    private void initFields() {
        when(labelField.getWidget()).thenReturn(mock(Label.class));
        when(labelField.isRequired()).thenReturn(true);
        when(labelField.isContentValid()).thenReturn(true);

        when(textBoxField.getWidget()).thenReturn(textBox);
        when(textBoxField.isRequired()).thenReturn(true);
        when(textBoxField.isContentValid()).thenReturn(true);

        when(valueLabelField.getWidget()).thenReturn(mock(ValueLabel.class));
        when(valueLabelField.isRequired()).thenReturn(true);
        when(valueLabelField.isContentValid()).thenReturn(true);

        fieldCollection = Arrays.asList(labelField,
                                        textBoxField,
                                        valueLabelField);
    }

    @Test
    public void testInitialize() {
        fieldStateValidator.initialize();
        assertThat(fieldStateValidator.validators).hasSize(2);
        assertThat(fieldStateValidator.validators).containsKeys(String.class,
                                                                ArrayList.class);
    }

    @Test
    public void testValidateFieldValueRequired() {

        // String values
        assertTrue(fieldStateValidator.validateFieldValueRequired(labelField,
                                                                  "cc"));
        assertFalse(fieldStateValidator.validateFieldValueRequired(labelField,
                                                                   ""));
        assertFalse(fieldStateValidator.validateFieldValueRequired(labelField,
                                                                   null));

        // ArrayList values
        ArrayList<String> nonEmptyArrayList = new ArrayList<>();
        nonEmptyArrayList.add("something");
        ArrayList<String> emptyArrayList = new ArrayList<>();
        assertTrue(fieldStateValidator.validateFieldValueRequired(labelField,
                                                                  nonEmptyArrayList));
        assertFalse(fieldStateValidator.validateFieldValueRequired(labelField,
                                                                   emptyArrayList));
    }

    @Test
    public void testValidateFieldHasText() {
        testValidateField(labelField,
                          false);
        testValidateField(labelField,
                          true);
    }

    @Test
    public void testValidateFieldHasValue() {
        testValidateField(textBoxField,
                          false);
        testValidateField(textBoxField,
                          true);
    }

    @Test
    public void testValidateFieldTakesValue() {
        testValidateField(valueLabelField,
                          false);
        testValidateField(valueLabelField,
                          true);
    }

    @Test
    public void testValidateFieldUnexpectedWidget() {
        FormField field = mock(FormField.class);
        IsWidget widget = mock(IsWidget.class);
        when(field.getWidget()).thenReturn(widget);
        when(field.isRequired()).thenReturn(true);
        assertThatThrownBy(() -> fieldStateValidator.validate(field))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Unexpected widget type: impossible to read the value");
    }

    private void testValidateField(FormField field,
                                   boolean required) {
        when(field.isRequired()).thenReturn(required);
        fieldStateValidator.validate(field);
        verify(fieldStateValidator,
               times(required ? 1 : 0)).validateFieldValueRequired(any(),
                                                                   any());
    }

    @Test
    public void testValidateCollectionInvalid() {
        boolean result = fieldStateValidator.validate(fieldCollection);
        assertFalse(result);
    }

    @Test
    public void testValidateCollectionValid() {
        fieldCollection.forEach((FormField field) -> {
            when(field.isRequired()).thenReturn(false);
        });
        boolean result = fieldStateValidator.validate(fieldCollection);
        assertTrue(result);
    }

    @Test
    public void testValidateCustomValidatorsWithError() {
        when(textBox.getValue()).thenReturn("abcd");

        CustomFieldValidator validator = o -> ValidationResult.error(MESSAGE);
        when(textBoxField.getCustomValidators()).thenReturn(Collections.singletonList(validator));

        assertFalse(fieldStateValidator.validate(textBoxField));

        verify(textBoxField).showError(eq(MESSAGE));
        verify(textBoxField, never()).showWarning(any());
    }

    @Test
    public void testValidateCustomValidatorsWithWarning() {
        when(textBox.getValue()).thenReturn("abcd");

        CustomFieldValidator validator = o -> ValidationResult.warning(MESSAGE);
        when(textBoxField.getCustomValidators()).thenReturn(Collections.singletonList(validator));

        assertTrue(fieldStateValidator.validate(textBoxField));

        verify(textBoxField).showWarning(eq(MESSAGE));
        verify(textBoxField, never()).showError(any());
    }

    @Test
    public void testValidateCustomValidatorsWithSucces() {
        when(textBox.getValue()).thenReturn("abcd");

        CustomFieldValidator validator = o -> ValidationResult.valid();
        when(textBoxField.getCustomValidators()).thenReturn(Collections.singletonList(validator));

        assertTrue(fieldStateValidator.validate(textBoxField));

        verify(textBoxField, never()).showWarning(any());
        verify(textBoxField, never()).showError(any());
    }
}
