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
import org.jboss.errai.databinding.client.PropertyChangeUnsubscribeHandle;
import org.jboss.errai.databinding.client.api.Converter;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.databinding.client.api.StateSync;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.processing.engine.handling.IsNestedModel;
import org.kie.workbench.common.forms.processing.engine.handling.NeedsFlush;
import org.kie.workbench.common.forms.processing.engine.handling.impl.model.ModelProxy;
import org.kie.workbench.common.forms.processing.engine.handling.impl.test.TestFormHandler;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyObject;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class FormHandlerImplTest extends AbstractFormEngineTest {

    @Mock
    protected DataBinder binder;

    @Mock
    protected PropertyChangeUnsubscribeHandle unsubscribeHandle;

    @Mock
    protected TranslationService translationService;

    private FieldChangeHandlerManagerImpl fieldChangeHandlerManager;

    protected TestFormHandler formHandler;

    protected ModelProxy proxy;

    protected boolean checkBindings = false;

    protected int fieldhandlers = 5;

    @Before
    public void init() {
        super.init();

        proxy = spy(new ModelProxy(model));

        when(binder.getModel()).thenReturn(proxy);

        when(binder.addPropertyChangeHandler(any())).thenReturn(unsubscribeHandle);
        when(binder.addPropertyChangeHandler(anyString(),
                                             any())).thenReturn(unsubscribeHandle);

        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

        FormValidatorImpl formValidator = new FormValidatorImpl(new DefaultModelValidator(validator),
                                                                new FieldStateValidatorImpl(translationService));

        fieldChangeHandlerManager = spy(new FieldChangeHandlerManagerImpl());
        fieldChangeHandlerManager.setValidator(formValidator);

        formHandler = new TestFormHandler(formValidator,
                                          fieldChangeHandlerManager,
                                          binder);
    }

    @Test
    public void testHandlerModelSetup() {
        formHandler.setUp(model);

        checkBindings = true;
        runSetupTest();
    }

    protected void runSetupTest() {
        Converter integerConverter = mock(Converter.class);
        when(integerConverter.toWidgetValue(25)).thenReturn(25l);
        when(integerConverter.toModelValue(25l)).thenReturn(25);

        formHandler.registerInput(valueField, integerConverter);
        formHandler.registerInput(nameField);
        formHandler.registerInput(lastNameField);
        formHandler.registerInput(birthdayField);
        formHandler.registerInput(marriedField);
        formHandler.registerInput(addressField);

        formHandler.addFieldChangeHandler(anonymous);
        formHandler.addFieldChangeHandler(VALUE_FIELD_NAME, value);
        formHandler.addFieldChangeHandler(USER_NAME_FIELD_NAME, userName);
        formHandler.addFieldChangeHandler(USER_LAST_NAME_FIELD_NAME, userLastName);
        formHandler.addFieldChangeHandler(USER_BIRTHDAY_FIELD_NAME, userBirthday);
        formHandler.addFieldChangeHandler(USER_MARRIED_FIELD_NAME, userMarried);
        formHandler.addFieldChangeHandler(USER_ADDRESS_FIELD_NAME, userAddress);

        if (checkBindings) {
            verify(binder,
                   times(6)).bind(anyObject(),
                                  anyString(),
                                  any(),
                                  any());
        } else {
            verify(binder,
                   never()).bind(anyObject(),
                                 anyString());
        }
        verify(binder, times(5)).addPropertyChangeHandler(anyString(), any());
        verify((IsNestedModel) addressField.getWidget()).addFieldChangeHandler(any());
    }

    @Test
    public void testHandlerModelCorrectValidation() {
        testHandlerModelSetup();
        runCorrectValidationTest(true);
    }

    protected void runCorrectValidationTest(boolean skipGetModel) {
        assertTrue(formHandler.validate());

        verify(proxy).deepUnwrap();

        if (!skipGetModel) {
            int expectedTimes = 7;

            verify(binder,
                   times(expectedTimes)).getModel();
            // checking if property is null
            verify(proxy,
                   times(expectedTimes - 1)).get(anyString());
        }

        assertTrue(formHandler.validate(VALUE_FIELD_NAME));
        assertTrue(formHandler.validate(USER_NAME_FIELD_NAME));
        assertTrue(formHandler.validate(USER_LAST_NAME_FIELD_NAME));
        assertTrue(formHandler.validate(USER_BIRTHDAY_FIELD_NAME));
        assertTrue(formHandler.validate(USER_MARRIED_FIELD_NAME));
        assertTrue(formHandler.validate(USER_ADDRESS_FIELD_NAME));

        verify(proxy,
               times(7)).deepUnwrap();
    }

    @Test
    public void testHandlerModelWrongValidationWithoutBindings() {
        testHandlerModelSetup();
        runWrongValidationTest(true);
    }

    @Test
    public void testHandlerFlushWithValidValue() {
        testHandlerModelSetup();

        formHandler.maybeFlush();

        verify(binder, times(8)).getModel();

        verify(proxy, times(3)).deepUnwrap();

        verify((NeedsFlush) addressField.getWidget()).flush();

        verify(binder).setModel(any(), same(StateSync.FROM_UI), eq(true));
    }

    @Test
    public void testHandlerFlushWithInValidValue() {
        testHandlerModelSetup();

        model.setValue(-123);

        formHandler.maybeFlush();

        verify(binder, times(9)).getModel();

        verify(proxy, times(4)).deepUnwrap();

        verify((NeedsFlush) addressField.getWidget()).flush();

        verify(binder).setModel(any(), same(StateSync.FROM_UI), eq(true));

        // If validation failed test rebinding the model
        verify(binder).setModel(any(), same(StateSync.FROM_MODEL), eq(true));
    }

    @Test
    public void testNotifyChange() {
        testHandlerModelSetup();

        final String name = "Bart";

        formHandler.notifyFieldChange(USER_NAME_FIELD_NAME, name);

        verify(fieldChangeHandlerManager).notifyFieldChange(eq(USER_NAME_FIELD_NAME), eq(name));
        verify(userName).onFieldChange(eq(USER_NAME_FIELD_NAME), eq(name));
    }

    @Test
    public void testProcessFieldChange() {
        testHandlerModelSetup();

        final String address = "Springsfield";

        formHandler.processFieldChange(addressField, address);

        verify(fieldChangeHandlerManager).processFieldChange(eq(USER_ADDRESS_FIELD_NAME), eq(address), any());
        verify(fieldChangeHandlerManager, never()).notifyFieldChange(eq(USER_ADDRESS_FIELD_NAME), eq(address));

        verify(userAddress).onFieldChange(eq(USER_ADDRESS_FIELD_NAME), eq(address));
    }

    @Test
    public void testProcessFieldSkippingValidation() {
        testHandlerModelSetup();

        formHandler.setEnabledOnChangeValidations(false);

        final String address = "Springsfield";

        formHandler.processFieldChange(addressField, address);

        verify(fieldChangeHandlerManager, never()).processFieldChange(eq(USER_ADDRESS_FIELD_NAME), eq(address), any());
        verify(fieldChangeHandlerManager).notifyFieldChange(eq(USER_ADDRESS_FIELD_NAME), eq(address));
        verify(userAddress).onFieldChange(eq(USER_ADDRESS_FIELD_NAME), eq(address));
    }


    protected void runWrongValidationTest(boolean skipGetModel) {
        model.setValue(-123);
        model.getUser().setLastName("");
        model.getUser().setAddress("");

        assertFalse(formHandler.validate());

        verify(proxy).deepUnwrap();

        if (!skipGetModel) {
            verify(binder,
                   times(7)).getModel();
            // checking if property is null
            verify(proxy,
                   times(6)).get(anyString());
        }

        assertFalse(formHandler.validate(VALUE_FIELD_NAME));
        assertTrue(formHandler.validate(USER_NAME_FIELD_NAME));
        assertFalse(formHandler.validate(USER_LAST_NAME_FIELD_NAME));
        assertTrue(formHandler.validate(USER_BIRTHDAY_FIELD_NAME));
        assertTrue(formHandler.validate(USER_MARRIED_FIELD_NAME));
        assertFalse(formHandler.validate(USER_ADDRESS_FIELD_NAME));

        verify(proxy,
               times(7)).deepUnwrap();
    }

    @After
    public void end() {

        formHandler.clear();

        verify(binder).unbind();
        verify(unsubscribeHandle,
               times(fieldhandlers)).unsubscribe();
    }
}
