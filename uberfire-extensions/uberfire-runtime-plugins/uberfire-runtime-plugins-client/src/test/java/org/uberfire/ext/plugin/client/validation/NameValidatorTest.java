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

package org.uberfire.ext.plugin.client.validation;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class NameValidatorTest {

    private static final String EMPTY_NAME = "";
    private static final String INVALID_NAME = "invalid*";
    private static final String VALID_NAME = "valid";

    private static final String EMPTY_ERROR_MESSAGE = "emptyError";
    private static final String INVALID_ERROR_MESSAGE = "invalidError";

    private NameValidator nameValidator;

    @Before
    public void setup() {
        nameValidator = NameValidator.createNameValidator( EMPTY_ERROR_MESSAGE, INVALID_ERROR_MESSAGE );
    }

    @Test
    public void emptyNameTest() {
        nameValidator.isValid( EMPTY_NAME );

        assertEquals( EMPTY_ERROR_MESSAGE, nameValidator.getValidationError() );
    }

    @Test
    public void invalidNameTest() {
        nameValidator.isValid( INVALID_NAME );

        assertEquals( INVALID_ERROR_MESSAGE, nameValidator.getValidationError() );
    }

    @Test
    public void validNameTest() {
        nameValidator.isValid( VALID_NAME );

        assertNull( nameValidator.getValidationError() );
    }
}
