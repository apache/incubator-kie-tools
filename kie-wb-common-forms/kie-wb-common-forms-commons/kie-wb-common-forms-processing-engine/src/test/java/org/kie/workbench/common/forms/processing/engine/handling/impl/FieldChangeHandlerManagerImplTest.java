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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.processing.engine.handling.FormField;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class FieldChangeHandlerManagerImplTest extends AbstractFormEngineTest {

    protected FieldChangeHandlerManagerImpl fieldChangeHandlerManager;

    @Before
    public void init() {
        super.init();

        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

        FormValidatorImpl formValidator = new FormValidatorImpl( validator );

        formValidator.setFormFieldProvider( formFieldProvider );

        fieldChangeHandlerManager = new FieldChangeHandlerManagerImpl();
        fieldChangeHandlerManager.setValidator( formValidator );

        executionCounts = 0;
    }

    @After
    public void after() {
        fieldChangeHandlerManager.clear();
    }

    @Test
    public void testAnonymousFieldChangeProcessing() {
        registerFields( false );

        fieldChangeHandlerManager.addFieldChangeHandler( anonymous );

        fieldChangeHandlerManager.processFieldChange( VALUE_FIELD, model.getValue(), model );

        assertEquals( 1, executionCounts );

        verify( anonymous ).onFieldChange( anyString(), anyObject() );

        fieldChangeHandlerManager.processFieldChange( USER_NAME_FIELD, model.getUser().getName(), model );

        assertEquals( 2, executionCounts );

        verify( anonymous, times( 2 ) ).onFieldChange( anyString(), anyObject() );

        fieldChangeHandlerManager.processFieldChange( USER_LAST_NAME_FIELD, model.getUser().getLastName(), model );
        fieldChangeHandlerManager.processFieldChange( USER_ADDRESS_FIELD, model.getUser().getName(), model );

        assertEquals( 4, executionCounts );
        verify( anonymous, times( 4 ) ).onFieldChange( anyString(), anyObject() );
    }


    @Test
    public void testAnonymousFieldChangeProcessingWithValidation() {
        registerFields( true );

        fieldChangeHandlerManager.addFieldChangeHandler( anonymous );

        fieldChangeHandlerManager.processFieldChange( VALUE_FIELD, model.getValue(), model );

        assertEquals( 1, executionCounts );

        verify( anonymous ).onFieldChange( anyString(), anyObject() );

        fieldChangeHandlerManager.processFieldChange( USER_NAME_FIELD, model.getUser().getName(), model );

        assertEquals( 2, executionCounts );

        verify( anonymous, times( 2 ) ).onFieldChange( anyString(), anyObject() );

        fieldChangeHandlerManager.processFieldChange( USER_LAST_NAME_FIELD, model.getUser().getLastName(), model );
        fieldChangeHandlerManager.processFieldChange( USER_ADDRESS_FIELD, model.getUser().getName(), model );

        assertEquals( 4, executionCounts );
        verify( anonymous, times( 4 ) ).onFieldChange( anyString(), anyObject() );
    }

    @Test
    public void testAnonymousFieldChangeProcessingWithValidationFailure() {
        registerFields( true );

        fieldChangeHandlerManager.addFieldChangeHandler( anonymous );

        model.setValue( 60 );
        fieldChangeHandlerManager.processFieldChange( VALUE_FIELD, model.getValue(), model );

        // Validation must file model.value must be between 0 : 50
        assertEquals( 0, executionCounts );
        verify( anonymous, never() ).onFieldChange( anyString(), anyObject() );

        fieldChangeHandlerManager.processFieldChange( USER_NAME_FIELD, model.getUser().getName(), model );

        // Validation must work!
        assertEquals( 1, executionCounts );
        verify( anonymous ).onFieldChange( anyString(), anyObject() );

        model.getUser().setAddress( "Pentos" );

        fieldChangeHandlerManager.processFieldChange( USER_LAST_NAME_FIELD, model.getUser().getLastName(), model );
        fieldChangeHandlerManager.processFieldChange( USER_ADDRESS_FIELD, model.getUser().getName(), model );

        // Validation must fail for USER_ADDRESS_FIELD -> address between 10 : 100
        assertEquals( 2, executionCounts );
        verify( anonymous, times( 2 ) ).onFieldChange( anyString(), anyObject() );
    }


    @Test
    public void testNamedFieldChangeProcessing() {
        registerFields( false );

        fieldChangeHandlerManager.addFieldChangeHandler( VALUE_FIELD, value );
        fieldChangeHandlerManager.addFieldChangeHandler( USER_NAME_FIELD, userName );
        fieldChangeHandlerManager.addFieldChangeHandler( USER_LAST_NAME_FIELD, userLastName );
        fieldChangeHandlerManager.addFieldChangeHandler( USER_BIRTHDAY_FIELD, userBirthday );
        fieldChangeHandlerManager.addFieldChangeHandler( USER_MARRIED_FIELD, userMarried );
        fieldChangeHandlerManager.addFieldChangeHandler( USER_ADDRESS_FIELD, userAddress );

        fieldChangeHandlerManager.processFieldChange( VALUE_FIELD, model.getValue(), model );

        assertEquals( 1, executionCounts );

        verify( value ).onFieldChange( anyString(), anyObject() );
        verify( userName, never() ).onFieldChange( anyString(), anyObject() );
        verify( userLastName, never() ).onFieldChange( anyString(), anyObject() );
        verify( userBirthday, never() ).onFieldChange( anyString(), anyObject() );
        verify( userMarried, never() ).onFieldChange( anyString(), anyObject() );
        verify( userAddress, never() ).onFieldChange( anyString(), anyObject() );


        fieldChangeHandlerManager.processFieldChange( USER_NAME_FIELD, model.getUser().getName(), model );
        fieldChangeHandlerManager.processFieldChange( USER_LAST_NAME_FIELD, model.getUser().getName(), model );
        fieldChangeHandlerManager.processFieldChange( USER_MARRIED_FIELD, model.getUser().getName(), model );

        assertEquals( 4, executionCounts );

        verify( value ).onFieldChange( anyString(), anyObject() );
        verify( userName ).onFieldChange( anyString(), anyObject() );
        verify( userLastName ).onFieldChange( anyString(), anyObject() );
        verify( userBirthday, never() ).onFieldChange( anyString(), anyObject() );
        verify( userMarried ).onFieldChange( anyString(), anyObject() );
        verify( userAddress, never() ).onFieldChange( anyString(), anyObject() );
    }

    @Test
    public void testNamedFieldChangeProcessingWithValidation() {
        registerFields( true );

        fieldChangeHandlerManager.addFieldChangeHandler( VALUE_FIELD, value );
        fieldChangeHandlerManager.addFieldChangeHandler( USER_NAME_FIELD, userName );
        fieldChangeHandlerManager.addFieldChangeHandler( USER_LAST_NAME_FIELD, userLastName );
        fieldChangeHandlerManager.addFieldChangeHandler( USER_BIRTHDAY_FIELD, userBirthday );
        fieldChangeHandlerManager.addFieldChangeHandler( USER_MARRIED_FIELD, userMarried );
        fieldChangeHandlerManager.addFieldChangeHandler( USER_ADDRESS_FIELD, userAddress );

        // Validation must work here
        fieldChangeHandlerManager.processFieldChange( VALUE_FIELD, model.getValue(), model );
        assertEquals( 1, executionCounts );

        verify( value ).onFieldChange( anyString(), anyObject() );
        verify( userName, never() ).onFieldChange( anyString(), anyObject() );
        verify( userLastName, never() ).onFieldChange( anyString(), anyObject() );
        verify( userBirthday, never() ).onFieldChange( anyString(), anyObject() );
        verify( userMarried, never() ).onFieldChange( anyString(), anyObject() );
        verify( userAddress, never() ).onFieldChange( anyString(), anyObject() );


        model.getUser().setName( null );
        model.getUser().setLastName( null );
        fieldChangeHandlerManager.processFieldChange( USER_NAME_FIELD, model.getUser().getName(), model );
        fieldChangeHandlerManager.processFieldChange( USER_LAST_NAME_FIELD, model.getUser().getName(), model );
        fieldChangeHandlerManager.processFieldChange( USER_MARRIED_FIELD, model.getUser().getName(), model );

        // Validation must fail for USER_NAME_FIELD && USER_LAST_NAME_FIELD (cannot be null or empty string)
        assertEquals( 2, executionCounts );

        verify( value ).onFieldChange( anyString(), anyObject() );
        verify( userName, never() ).onFieldChange( anyString(), anyObject() );
        verify( userLastName, never() ).onFieldChange( anyString(), anyObject() );
        verify( userBirthday, never() ).onFieldChange( anyString(), anyObject() );
        verify( userMarried ).onFieldChange( anyString(), anyObject() );
        verify( userAddress, never() ).onFieldChange( anyString(), anyObject() );
    }





    protected void registerFields( boolean validateOnChange ) {
        for ( FormField formField : formFieldProvider.getAll() ) {
            fieldChangeHandlerManager.registerField( formField.getFieldName(), validateOnChange );
        }
    }

}
