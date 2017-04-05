/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.datamodeller.backend.server.validation;

import java.text.MessageFormat;
import java.util.List;

import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.kie.workbench.common.screens.datamodeller.backend.server.validation.PersistenceDescriptorValidationMessages.*;

public class PropertyValidatorTest {

    private static final String PROPERTY_NAME = "propertyName";

    private PropertyValidator validator;

    @Before
    public void setUp( ) {
        validator = new PropertyValidator( );
    }

    @Test
    public void testValidateValidProperties( ) {
        //test a valid property
        List< ValidationMessage > result = validator.validate( PROPERTY_NAME, "value" );
        assertTrue( result.isEmpty( ) );

        //test a valid property indexed
        result = validator.validate( PROPERTY_NAME, "value", 0 );
        assertTrue( result.isEmpty( ) );
    }

    @Test
    public void testValidateInvalidProperties( ) {
        //test an invalid property with the empty String as name.
        List< ValidationMessage > result = validator.validate( "", "value" );
        assertEquals( 1, result.size( ) );
        ValidationMessage expectedMessage = newErrorMessage( PersistenceDescriptorValidationMessages.PROPERTY_NAME_EMPTY_ID,
                PersistenceDescriptorValidationMessages.PROPERTY_NAME_EMPTY );
        assertEquals( expectedMessage, result.get( 0 ) );

        //test an invalid property with the null String as name.
        result = validator.validate( null, "value" );
        assertEquals( 1, result.size( ) );
        assertEquals( expectedMessage, result.get( 0 ) );

        //test an invalid property with the empty String as name and index
        result = validator.validate( "", "value", 1 );
        assertEquals( 1, result.size( ) );
        expectedMessage = newErrorMessage( PersistenceDescriptorValidationMessages.INDEXED_PROPERTY_NAME_EMPTY_ID,
                MessageFormat.format( PersistenceDescriptorValidationMessages.INDEXED_PROPERTY_NAME_EMPTY, Integer.toString( 1 ) ), Integer.toString( 1 ) );
        assertEquals( expectedMessage, result.get( 0 ) );

        //test an invalid property with the empty String as name and index
        result = validator.validate( null, "value", 1 );
        assertEquals( 1, result.size( ) );
        assertEquals( expectedMessage, result.get( 0 ) );

        //test a property with name but no value, a warning should be produced.
        result = validator.validate( PROPERTY_NAME, "" );
        assertEquals( 1, result.size( ) );
        expectedMessage = newWarningMessage( PersistenceDescriptorValidationMessages.PROPERTY_VALUE_EMPTY_ID,
                MessageFormat.format( PersistenceDescriptorValidationMessages.PROPERTY_VALUE_EMPTY, PROPERTY_NAME ), PROPERTY_NAME );
        assertEquals( expectedMessage, result.get( 0 ) );

        //test a property with name but a null value, a warning should be produced.
        result = validator.validate( PROPERTY_NAME, null );
        assertEquals( 1, result.size( ) );
        assertEquals( expectedMessage, result.get( 0 ) );
    }
}