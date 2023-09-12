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

import javax.validation.Validation;
import javax.validation.Validator;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.processing.engine.handling.FieldStateValidator;
import org.kie.workbench.common.forms.processing.engine.handling.Form;
import org.kie.workbench.common.forms.processing.engine.handling.FormField;
import org.kie.workbench.common.forms.processing.engine.handling.ModelValidator;
import org.mockito.Mock;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyCollection;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class FormValidatorImplTest extends AbstractFormEngineTest {

    @Mock
    protected TranslationService translationService;

    @Mock
    protected FieldStateValidator fieldStateValidator;

    private Form form = new Form();

    protected Validator validator;

    protected FormValidatorImpl formValidator;

    @Before
    public void init() {
        super.init();

        validator = Validation.buildDefaultValidatorFactory().getValidator();

        formValidator = new FormValidatorImpl(new DefaultModelValidator(validator),
                                              fieldStateValidator);

        form.addField(valueField);
        form.addField(nameField);
        form.addField(lastNameField);
        form.addField(birthdayField);
        form.addField(marriedField);
        form.addField(addressField);

        when(fieldStateValidator.validate(any(FormField.class))).thenReturn(true);
        when(fieldStateValidator.validate(anyCollection())).thenReturn(true);
    }

    @Test
    public void testModelFieldStateValidationOnSuccess() {
        testModelAndFieldStateValidatorsCalled(true, true);
    }

    @Test
    public void testModelFieldStateValidationOnModelFailure() {
        testModelAndFieldStateValidatorsCalled(false, true);
    }

    @Test
    public void testModelFieldStateValidationOnFieldStateFailure() {
        testModelAndFieldStateValidatorsCalled(true, false);
    }

    @Test
    public void testModelFieldStateValidationOnFailure() {
        testModelAndFieldStateValidatorsCalled(false, false);
    }

    private void testModelAndFieldStateValidatorsCalled(boolean modelSuccess, boolean fieldStateSuccess) {
        ModelValidator modelValidator = mock(ModelValidator.class);
        when(modelValidator.validate(anyCollection(), any())).thenReturn(modelSuccess);
        when(fieldStateValidator.validate(anyCollection())).thenReturn(fieldStateSuccess);
        formValidator.setModelValidator(modelValidator);
        assertEquals(formValidator.validate(form, model), modelSuccess && fieldStateSuccess);
        verify(fieldStateValidator).validate(form.getFields());
        verify(modelValidator).validate(form.getFields(), model);
    }

    private FormField[] getAllFields() {
        return form.getFields().toArray(new FormField[form.getFields().size()]);
    }

    @Test
    public void testFullModelSuccesfulValidation() {
        assertTrue(formValidator.validate(form, model));
        checkClearedFields(getAllFields());
        checkValidFields(getAllFields());
    }

    @Test
    public void testModelStateFailedValidation() {
        when(fieldStateValidator.validate(anyCollection())).thenReturn(false);
        assertFalse(formValidator.validate(form, model));
    }

    @Test
    public void testFullModelFailedValidation() {
        model.setValue(600);
        model.getUser().setName(null);
        model.getUser().setLastName("");

        assertFalse(formValidator.validate(form, model));
        checkClearedFields();
        checkWrongFields(valueField,
                         nameField,
                         lastNameField);
        checkValidFields(birthdayField,
                         marriedField);
    }

    @Test
    public void testpropertySuccesfullValidation() {
        assertTrue(formValidator.validate(valueField,
                                          model));

        checkClearedFields(valueField);
        checkValidFields(valueField);

        assertTrue(formValidator.validate(nameField,
                                          model));

        checkClearedFields(nameField);
        checkValidFields(nameField);

        assertTrue(formValidator.validate(addressField,
                                          model));

        checkClearedFields(addressField);
        checkValidFields(addressField);
    }

    @Test
    public void testPropertyFailedValidation() {
        model.setValue(70);
        model.getUser().setName("");
        model.getUser().setLastName("abc");

        assertFalse(formValidator.validate(valueField,
                                           model));

        checkClearedFields(valueField);
        checkWrongFields(valueField);

        assertFalse(formValidator.validate(nameField,
                                           model));

        checkClearedFields(nameField);
        checkWrongFields(nameField);

        assertFalse(formValidator.validate(lastNameField,
                                           model));

        checkClearedFields(lastNameField);
        checkWrongFields(lastNameField);
    }

    @Test
    public void testPropertyStateFailedValidation() {

        when(fieldStateValidator.validate(any(FormField.class))).thenReturn(false);

        assertFalse(formValidator.validate(valueField,
                                           model));
        assertFalse(formValidator.validate(nameField,
                                           model));
        assertFalse(formValidator.validate(lastNameField,
                                           model));
    }

    @Test
    public void testSetModelValidator() {
        ModelValidator modelValidator = mock(ModelValidator.class);
        formValidator.setModelValidator(modelValidator);
        assertSame(formValidator.getModelValidator(),
                   modelValidator);
    }
}
