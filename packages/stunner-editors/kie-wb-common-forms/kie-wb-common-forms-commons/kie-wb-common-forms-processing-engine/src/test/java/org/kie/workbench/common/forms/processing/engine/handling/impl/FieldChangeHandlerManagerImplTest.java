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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Mockito.anyObject;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class FieldChangeHandlerManagerImplTest extends AbstractFormEngineTest {

    public static final String MODIFIED_USER_NAME_FIELD = USER_NAME_FIELD_NAME + ".name";

    @Mock
    protected TranslationService translationService;

    protected FieldChangeHandlerManagerImpl fieldChangeHandlerManager;

    @Before
    public void init() {
        super.init();

        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

        FormValidatorImpl formValidator = new FormValidatorImpl(new DefaultModelValidator(validator),
                                                                new FieldStateValidatorImpl(translationService));

        fieldChangeHandlerManager = spy(new FieldChangeHandlerManagerImpl());
        fieldChangeHandlerManager.setValidator(formValidator);

        executionCounts = 0;

        fieldChangeHandlerManager.registerField(valueField);
        fieldChangeHandlerManager.registerField(nameField);
        fieldChangeHandlerManager.registerField(lastNameField);
        fieldChangeHandlerManager.registerField(birthdayField);
        fieldChangeHandlerManager.registerField(marriedField);
        fieldChangeHandlerManager.registerField(addressField);
    }

    @After
    public void after() {
        fieldChangeHandlerManager.clear();
    }

    @Test
    public void testAnonymousFieldChangeProcessing() {
        registerFields(false);

        fieldChangeHandlerManager.addFieldChangeHandler(anonymous);

        fieldChangeHandlerManager.processFieldChange(VALUE_FIELD_NAME,
                                                     model.getValue(),
                                                     model);

        assertEquals(1,
                     executionCounts);

        verify(anonymous).onFieldChange(anyString(),
                                        anyObject());

        fieldChangeHandlerManager.processFieldChange(USER_NAME_FIELD_NAME,
                                                     model.getUser().getName(),
                                                     model);

        assertEquals(2,
                     executionCounts);

        verify(anonymous,
               times(2)).onFieldChange(anyString(),
                                       anyObject());

        fieldChangeHandlerManager.processFieldChange(USER_LAST_NAME_FIELD_NAME,
                                                     model.getUser().getLastName(),
                                                     model);
        fieldChangeHandlerManager.processFieldChange(USER_ADDRESS_FIELD_NAME,
                                                     model.getUser().getName(),
                                                     model);

        assertEquals(4,
                     executionCounts);
        verify(anonymous,
               times(4)).onFieldChange(anyString(),
                                       anyObject());
    }

    @Test
    public void testAnonymousFieldChangeProcessingWithValidation() {
        registerFields(true);

        fieldChangeHandlerManager.addFieldChangeHandler(anonymous);

        fieldChangeHandlerManager.processFieldChange(VALUE_FIELD_NAME,
                                                     model.getValue(),
                                                     model);

        assertEquals(1,
                     executionCounts);

        verify(anonymous).onFieldChange(anyString(),
                                        anyObject());

        fieldChangeHandlerManager.processFieldChange(MODIFIED_USER_NAME_FIELD,
                                                     model.getUser().getName(),
                                                     model);

        assertEquals(2,
                     executionCounts);

        verify(anonymous,
               times(2)).onFieldChange(anyString(),
                                       anyObject());

        fieldChangeHandlerManager.processFieldChange(USER_LAST_NAME_FIELD_NAME,
                                                     model.getUser().getLastName(),
                                                     model);
        fieldChangeHandlerManager.processFieldChange(USER_ADDRESS_FIELD_NAME,
                                                     model.getUser().getName(),
                                                     model);

        assertEquals(4,
                     executionCounts);
        verify(anonymous,
               times(4)).onFieldChange(anyString(),
                                       anyObject());
    }

    @Test
    public void testAnonymousFieldChangeProcessingWithValidationFailure() {
        registerFields(true);

        fieldChangeHandlerManager.addFieldChangeHandler(anonymous);

        model.setValue(60);
        fieldChangeHandlerManager.processFieldChange(VALUE_FIELD_NAME,
                                                     model.getValue(),
                                                     model);

        // Validation must file model.value must be between 0 : 50
        assertEquals(0,
                     executionCounts);
        verify(anonymous,
               never()).onFieldChange(anyString(),
                                      anyObject());

        fieldChangeHandlerManager.processFieldChange(USER_NAME_FIELD_NAME,
                                                     model.getUser().getName(),
                                                     model);

        // Validation must work!
        assertEquals(1,
                     executionCounts);
        verify(anonymous).onFieldChange(anyString(),
                                        anyObject());

        model.getUser().setAddress("Pentos");

        fieldChangeHandlerManager.processFieldChange(USER_LAST_NAME_FIELD_NAME,
                                                     model.getUser().getLastName(),
                                                     model);
        fieldChangeHandlerManager.processFieldChange(USER_ADDRESS_FIELD_NAME,
                                                     model.getUser().getName(),
                                                     model);

        // Validation must fail for USER_ADDRESS_FIELD_NAME -> address between 10 : 100
        assertEquals(2,
                     executionCounts);
        verify(anonymous,
               times(2)).onFieldChange(anyString(),
                                       anyObject());
    }

    @Test
    public void testNamedFieldChangeProcessing() {
        testAnonymousFieldProcessing(false);
    }

    @Test
    public void testNamedFieldChangeProcessingWithValidation() {
        registerFields(true);

        fieldChangeHandlerManager.addFieldChangeHandler(VALUE_FIELD_NAME,
                                                        value);
        fieldChangeHandlerManager.addFieldChangeHandler(USER_NAME_FIELD_NAME,
                                                        userName);
        fieldChangeHandlerManager.addFieldChangeHandler(USER_LAST_NAME_FIELD_NAME,
                                                        userLastName);
        fieldChangeHandlerManager.addFieldChangeHandler(USER_BIRTHDAY_FIELD_NAME,
                                                        userBirthday);
        fieldChangeHandlerManager.addFieldChangeHandler(USER_MARRIED_FIELD_NAME,
                                                        userMarried);
        fieldChangeHandlerManager.addFieldChangeHandler(USER_ADDRESS_FIELD_NAME,
                                                        userAddress);

        // Validation must work here
        fieldChangeHandlerManager.processFieldChange(VALUE_FIELD_NAME,
                                                     model.getValue(),
                                                     model);
        assertEquals(1,
                     executionCounts);

        verify(value).onFieldChange(anyString(),
                                    anyObject());
        verify(userName,
               never()).onFieldChange(anyString(),
                                      anyObject());
        verify(userLastName,
               never()).onFieldChange(anyString(),
                                      anyObject());
        verify(userBirthday,
               never()).onFieldChange(anyString(),
                                      anyObject());
        verify(userMarried,
               never()).onFieldChange(anyString(),
                                      anyObject());
        verify(userAddress,
               never()).onFieldChange(anyString(),
                                      anyObject());

        model.getUser().setName(null);
        model.getUser().setLastName(null);
        fieldChangeHandlerManager.processFieldChange(USER_NAME_FIELD_NAME,
                                                     model.getUser().getName(),
                                                     model);
        fieldChangeHandlerManager.processFieldChange(USER_LAST_NAME_FIELD_NAME,
                                                     model.getUser().getName(),
                                                     model);
        fieldChangeHandlerManager.processFieldChange(USER_MARRIED_FIELD_NAME,
                                                     model.getUser().getName(),
                                                     model);

        // Validation must fail for USER_NAME_FIELD_NAME && USER_LAST_NAME_FIELD_NAME (cannot be null or empty string)
        assertEquals(2,
                     executionCounts);

        verify(value).onFieldChange(anyString(),
                                    anyObject());
        verify(userName,
               never()).onFieldChange(anyString(),
                                      anyObject());
        verify(userLastName,
               never()).onFieldChange(anyString(),
                                      anyObject());
        verify(userBirthday,
               never()).onFieldChange(anyString(),
                                      anyObject());
        verify(userMarried).onFieldChange(anyString(),
                                          anyObject());
        verify(userAddress,
               never()).onFieldChange(anyString(),
                                      anyObject());
    }

    @Test
    public void testAnonymousFieldChangeNotify() {
        testAnonymousFieldProcessing(true);
    }

    protected void testAnonymousFieldProcessing(boolean notifyOnly) {
        registerFields(false);

        fieldChangeHandlerManager.addFieldChangeHandler(VALUE_FIELD_NAME,
                                                        value);
        fieldChangeHandlerManager.addFieldChangeHandler(USER_NAME_FIELD_NAME,
                                                        userName);
        fieldChangeHandlerManager.addFieldChangeHandler(USER_LAST_NAME_FIELD_NAME,
                                                        userLastName);
        fieldChangeHandlerManager.addFieldChangeHandler(USER_BIRTHDAY_FIELD_NAME,
                                                        userBirthday);
        fieldChangeHandlerManager.addFieldChangeHandler(USER_MARRIED_FIELD_NAME,
                                                        userMarried);
        fieldChangeHandlerManager.addFieldChangeHandler(USER_ADDRESS_FIELD_NAME,
                                                        userAddress);

        if (notifyOnly) {
            fieldChangeHandlerManager.notifyFieldChange(VALUE_FIELD_NAME,
                                                        model.getValue());
        } else {
            fieldChangeHandlerManager.processFieldChange(VALUE_FIELD_NAME,
                                                         model.getValue(),
                                                         model);
        }

        assertEquals(1,
                     executionCounts);

        verify(value).onFieldChange(anyString(),
                                    anyObject());
        verify(userName,
               never()).onFieldChange(anyString(),
                                      anyObject());
        verify(userLastName,
               never()).onFieldChange(anyString(),
                                      anyObject());
        verify(userBirthday,
               never()).onFieldChange(anyString(),
                                      anyObject());
        verify(userMarried,
               never()).onFieldChange(anyString(),
                                      anyObject());
        verify(userAddress,
               never()).onFieldChange(anyString(),
                                      anyObject());

        if (notifyOnly) {
            fieldChangeHandlerManager.notifyFieldChange(USER_NAME_FIELD_NAME,
                                                        model.getUser().getName());
            fieldChangeHandlerManager.notifyFieldChange(USER_LAST_NAME_FIELD_NAME,
                                                        model.getUser().getName());
            fieldChangeHandlerManager.notifyFieldChange(USER_MARRIED_FIELD_NAME,
                                                        model.getUser().getName());
        } else {
            fieldChangeHandlerManager.processFieldChange(USER_NAME_FIELD_NAME,
                                                         model.getUser().getName(),
                                                         model);
            fieldChangeHandlerManager.processFieldChange(USER_LAST_NAME_FIELD_NAME,
                                                         model.getUser().getName(),
                                                         model);
            fieldChangeHandlerManager.processFieldChange(USER_MARRIED_FIELD_NAME,
                                                         model.getUser().getName(),
                                                         model);
        }

        assertEquals(4,
                     executionCounts);

        verify(value).onFieldChange(anyString(),
                                    anyObject());
        verify(userName).onFieldChange(anyString(),
                                       anyObject());
        verify(userLastName).onFieldChange(anyString(),
                                           anyObject());
        verify(userBirthday,
               never()).onFieldChange(anyString(),
                                      anyObject());
        verify(userMarried).onFieldChange(anyString(),
                                          anyObject());
        verify(userAddress,
               never()).onFieldChange(anyString(),
                                      anyObject());
    }

    protected void registerFields(boolean validateOnChange) {
        when(valueField.isValidateOnChange()).thenReturn(validateOnChange);
        when(nameField.isValidateOnChange()).thenReturn(validateOnChange);
        when(lastNameField.isValidateOnChange()).thenReturn(validateOnChange);
        when(birthdayField.isValidateOnChange()).thenReturn(validateOnChange);
        when(marriedField.isValidateOnChange()).thenReturn(validateOnChange);
        when(addressField.isValidateOnChange()).thenReturn(validateOnChange);
    }
}
