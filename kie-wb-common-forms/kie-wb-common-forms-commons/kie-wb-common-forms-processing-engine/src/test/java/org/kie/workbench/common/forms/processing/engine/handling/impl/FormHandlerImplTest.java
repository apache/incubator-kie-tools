/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.forms.processing.engine.handling.impl;

import javax.validation.Validation;
import javax.validation.Validator;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.databinding.client.PropertyChangeUnsubscribeHandle;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.processing.engine.handling.FormField;
import org.kie.workbench.common.forms.processing.engine.handling.impl.mock.TestFormHandler;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class FormHandlerImplTest extends AbstractFormEngineTest {

    @Mock
    protected DataBinder binder;

    @Mock
    protected PropertyChangeUnsubscribeHandle unsubscribeHandle;

    protected FormHandlerImpl formHandler;

    protected boolean checkBindings = false;

    @Before
    public void init() {
        super.init();

        when( binder.getModel() ).thenReturn( model );

        when( binder.addPropertyChangeHandler( any() ) ).thenReturn( unsubscribeHandle );
        when( binder.addPropertyChangeHandler( anyString(), any() ) ).thenReturn( unsubscribeHandle );

        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

        FormValidatorImpl formValidator = new FormValidatorImpl( validator );

        formValidator.setFormFieldProvider( formFieldProvider );

        FieldChangeHandlerManagerImpl fieldChangeHandlerManager = new FieldChangeHandlerManagerImpl();
        fieldChangeHandlerManager.setValidator( formValidator );

        formHandler = new TestFormHandler( formValidator, fieldChangeHandlerManager, binder );

        formHandler.getAll().addAll( formFieldProvider.getAll() );
    }

    @Test
    public void testHandlerDataBinderSetupWithBindings() {
        formHandler.setUp( binder, true );

        checkBindings = true;
        runSetupTest();
    }

    @Test
    public void testHandlerDataBinderSetupWithoutBindings() {
        formHandler.setUp( binder );

        checkBindings = false;
        runSetupTest();
    }

    @Test
    public void testHandlerModelSetup() {
        formHandler.setUp( model );

        checkBindings = true;
        runSetupTest();
    }

    protected void runSetupTest() {
        for ( FormField formField : formFieldProvider.getAll() ) {
            formHandler.registerInput( formField );
        }
        formHandler.addFieldChangeHandler( anonymous );
        formHandler.addFieldChangeHandler( VALUE_FIELD, value );
        formHandler.addFieldChangeHandler( USER_NAME_FIELD, userName );
        formHandler.addFieldChangeHandler( USER_LAST_NAME_FIELD, userLastName );
        formHandler.addFieldChangeHandler( USER_BIRTHDAY_FIELD, userBirthday );
        formHandler.addFieldChangeHandler( USER_MARRIED_FIELD, userMarried );
        formHandler.addFieldChangeHandler( USER_ADDRESS_FIELD, userAddress );

        if ( checkBindings ) {
            verify( binder, times( formFieldProvider.getAll().size() ) ).bind( anyObject(), anyString() );
        } else {
            verify( binder, never() ).bind( anyObject(), anyString() );
        }
        verify( binder, times( formFieldProvider.getAll().size() ) ).addPropertyChangeHandler( anyString(), any() );
    }

    @Test
    public void testHandlerDataBinderCorrectValidationBindings() {
        testHandlerDataBinderSetupWithBindings();
        runCorrectValidationTest( false );
    }

    @Test
    public void testHandlerDataBinderCorrectValidationWithoutBindings() {
        testHandlerDataBinderSetupWithoutBindings();
        runCorrectValidationTest( false );
    }

    @Test
    public void testHandlerModelCorrectValidation() {
        testHandlerModelSetup();
        runCorrectValidationTest( true );
    }

    protected void runCorrectValidationTest( boolean skipGetModel ) {
        assertTrue( formHandler.validate() );
        if ( !skipGetModel ) {
            verify( binder ).getModel();
        }

        assertTrue( formHandler.validate( VALUE_FIELD ) );
        assertTrue( formHandler.validate( USER_NAME_FIELD ) );
        assertTrue( formHandler.validate( USER_LAST_NAME_FIELD ) );
        assertTrue( formHandler.validate( USER_BIRTHDAY_FIELD ) );
        assertTrue( formHandler.validate( USER_MARRIED_FIELD ) );
        assertTrue( formHandler.validate( USER_ADDRESS_FIELD ) );

        if ( !skipGetModel ) {
            verify( binder, times( formFieldProvider.getAll().size() + 1 ) ).getModel();
        }
    }

    @Test
    public void testHandlerDataBinderWrongValidationWithBindings() {
        testHandlerDataBinderSetupWithBindings();
        runWrongValidationTest( false );
    }

    @Test
    public void testHandlerDataBinderWrongValidationWithoutBindings() {
        testHandlerDataBinderSetupWithBindings();
        runWrongValidationTest( false );
    }

    @Test
    public void testHandlerModelWrongValidationWithoutBindings() {
        testHandlerModelSetup();
        runWrongValidationTest( true );
    }

    protected void runWrongValidationTest( boolean skipGetModel ) {
        model.setValue( -123 );
        model.getUser().setLastName( "" );
        model.getUser().setAddress( "" );

        assertFalse( formHandler.validate() );

        if ( !skipGetModel ) {
            verify( binder ).getModel();
        }

        assertFalse( formHandler.validate( VALUE_FIELD ) );
        assertTrue( formHandler.validate( USER_NAME_FIELD ) );
        assertFalse( formHandler.validate( USER_LAST_NAME_FIELD ) );
        assertTrue( formHandler.validate( USER_BIRTHDAY_FIELD ) );
        assertTrue( formHandler.validate( USER_MARRIED_FIELD ) );
        assertFalse( formHandler.validate( USER_ADDRESS_FIELD ) );

        if ( !skipGetModel ) {
            verify( binder, times( formFieldProvider.getAll().size() + 1 ) ).getModel();
        }
    }

    @After
    public void end() {
        formHandler.clear();

        if ( checkBindings ) {
            verify( binder ).unbind();
        } else {
            verify( binder, never() ).unbind();
        }
        verify( unsubscribeHandle, times( formFieldProvider.getAll().size() ) ).unsubscribe();
    }

}
