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

public class PersistableClassValidatorTest {

    private static final String NOT_EXISTING_CLASS = "not.exists.NoExistingClass";

    private PersistableClassValidator validator;

    private ClassLoader classLoader;

    @Before
    public void setUp( ) {
        validator = new PersistableClassValidator( );
        classLoader = this.getClass( ).getClassLoader( );
    }

    @Test
    public void testValidateValidPersistableClasses( ) {
        assertTrue( validator.validate( PersistableClass1.class.getName( ), classLoader ).isEmpty( ) );
        assertTrue( validator.validate( PersistableClass2.class.getName( ), classLoader ).isEmpty( ) );
        assertTrue( validator.validate( PersistableClass3.class.getName( ), classLoader ).isEmpty( ) );
    }

    @Test
    public void testValidateInvalidPersistableClasses( ) {
        //tests an existing class but not persistable.
        List< ValidationMessage > result = validator.validate( NonPersistableClass1.class.getName( ), classLoader );
        assertEquals( 1, result.size( ) );
        ValidationMessage expectedMessage = newErrorMessage( PersistenceDescriptorValidationMessages.CLASS_NOT_PERSISTABLE_ID,
                MessageFormat.format( PersistenceDescriptorValidationMessages.CLASS_NOT_PERSISTABLE, NonPersistableClass1.class.getName( ) ), NonPersistableClass1.class.getName( ) );
        assertEquals( expectedMessage, result.get( 0 ) );

        //tests a class not existing in current classloader.
        result = validator.validate( NOT_EXISTING_CLASS, classLoader );
        assertEquals( 1, result.size( ) );
        expectedMessage = newErrorMessage( PersistenceDescriptorValidationMessages.CLASS_NOT_FOUND_ID,
                MessageFormat.format( PersistenceDescriptorValidationMessages.CLASS_NOT_FOUND, NOT_EXISTING_CLASS ), NOT_EXISTING_CLASS );
        assertEquals( expectedMessage, result.get( 0 ) );

        //test the case of the empty String as class name.
        result = validator.validate( "", classLoader );
        assertEquals( 1, result.size( ) );
        expectedMessage = newErrorMessage( PersistenceDescriptorValidationMessages.PERSISTABLE_CLASS_NAME_EMPTY_ID,
                PersistenceDescriptorValidationMessages.PERSISTABLE_CLASS_NAME_EMPTY );
        assertEquals( expectedMessage, result.get( 0 ) );

        //test the case of the null String as class name.
        result = validator.validate( null, classLoader );
        assertEquals( 1, result.size( ) );
        //same error message as the empty String case.
        assertEquals( expectedMessage, result.get( 0 ) );
    }
}