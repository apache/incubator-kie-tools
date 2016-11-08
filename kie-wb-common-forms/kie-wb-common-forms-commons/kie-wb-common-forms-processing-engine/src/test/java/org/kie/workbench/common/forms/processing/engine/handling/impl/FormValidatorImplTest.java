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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(GwtMockitoTestRunner.class)
public class FormValidatorImplTest extends AbstractFormEngineTest {

    protected Validator validator;

    protected FormValidatorImpl formValidator;

    @Before
    public void init() {
        super.init();

        validator = Validation.buildDefaultValidatorFactory().getValidator();

        formValidator = new FormValidatorImpl( validator );

        formValidator.setFormFieldProvider( formFieldProvider );
    }

    @Test
    public void testFullModelSuccesfulValidation() {
        assertTrue( formValidator.validate( model ) );
        checkClearedFields( ALL_FIELDS );
        checkValidFields( ALL_FIELDS );
    }

    @Test
    public void testFullModelFailedValidation() {
        model.setValue( 600 );
        model.getUser().setName( null );
        model.getUser().setLastName( "" );

        assertFalse( formValidator.validate( model ) );
        checkClearedFields( ALL_FIELDS );
        checkWrongFields( VALUE_FIELD, USER_NAME_FIELD, USER_LAST_NAME_FIELD );
        checkValidFields( USER_BIRTHDAY_FIELD, USER_MARRIED_FIELD );
    }

    @Test
    public void testpropertySuccesfullValidation() {
        assertTrue( formValidator.validate( VALUE_FIELD, model ) );

        checkClearedFields( VALUE_FIELD );
        checkValidFields( VALUE_FIELD );

        assertTrue( formValidator.validate( USER_NAME_FIELD, model ) );

        checkClearedFields( USER_NAME_FIELD );
        checkValidFields( USER_NAME_FIELD );

        assertTrue( formValidator.validate( USER_ADDRESS_FIELD, model ) );

        checkClearedFields( USER_ADDRESS_FIELD );
        checkValidFields( USER_ADDRESS_FIELD );
    }

    @Test
    public void testPropertyFailedValidation() {
        model.setValue( 70 );
        model.getUser().setName( "" );
        model.getUser().setLastName( "abc" );

        assertFalse( formValidator.validate( VALUE_FIELD, model ) );

        checkClearedFields( VALUE_FIELD );
        checkWrongFields( VALUE_FIELD );

        assertFalse( formValidator.validate( USER_NAME_FIELD, model ) );

        checkClearedFields( USER_NAME_FIELD );
        checkWrongFields( USER_NAME_FIELD );

        assertFalse( formValidator.validate( USER_LAST_NAME_FIELD, model ) );

        checkClearedFields( USER_LAST_NAME_FIELD );
        checkWrongFields( USER_LAST_NAME_FIELD );
    }
}
