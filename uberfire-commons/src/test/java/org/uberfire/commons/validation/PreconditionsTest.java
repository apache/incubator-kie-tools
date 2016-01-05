/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.commons.validation;

import org.junit.Test;
import org.uberfire.commons.validation.Preconditions;

import static org.hamcrest.core.Is.*;
import static org.junit.Assert.*;
import static org.uberfire.commons.validation.Preconditions.*;

/**
 * Test class for {@link Preconditions}
 */
public class PreconditionsTest {

    @Test
    public void shouldDoNotThrowExceptionWhenGettingNotEmptyArray() {
        checkNotEmpty( "notEmpty", new Object[]{ 1, 2, 3 } );
    }

    @Test
    public void shouldDoNotThrowExceptionWhenGettingNotEmptyParameter() {
        checkNotEmpty( "notEmpty", "notEmpty" );
    }

    @Test
    public void shouldDoNotThrowExceptionWhenGettingNotNullParameter() {
        checkNotNull( "notNullable", "notNullValue" );
    }

    @Test
    public void shouldDoNotThrowExceptionWhenGettingNullParameter() {
        checkNullMandatory( "nullable", null );
    }

    @Test
    public void shouldDoNotThrowExceptionWhenGettingValidConditionParameter() {
        checkCondition( "valid", true );
    }

    @Test
    public void shouldDoNotThrowExceptionWhenGettinOnlyNonNullParameters() {
        checkEachParameterNotNull( "notNullable", "nonNull" );
        checkEachParameterNotNull( "notNullable", "nonNull", "anotherNonNull" );
    }

    @Test
    public void shouldGetCorrectErrorNessage() {
        try {
            checkNotNull( "notNullable", null );
        } catch ( final IllegalArgumentException e ) {
            assertThat( e.getMessage(), is( "Parameter named 'notNullable' should be not null!" ) );
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionOnCheckEmptyWhenGettingNullParameter() {
        checkNotEmpty( "notEmpty", (String) null );
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenGettinAllNullParameter() {
        checkEachParameterNotNull( "notNullable", (Object) null );
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenGettingEmptyArray() {
        checkNotEmpty( "empty", new Object[]{ } );
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenGettingEmptyParameter() {
        checkNotEmpty( "notEmpty", "" );
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptionWhenGettingInvalidConditionParameter() {
        checkCondition( "valid", false );
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenGettingNonNullParameter() {
        checkNullMandatory( "nullable", "non null" );
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenGettingNullArray() {
        checkNotEmpty( "empty", (Object[]) null );
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenGettingNullParameter() {
        checkNotNull( "notNullable", null );
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenGettingSpacedParameter() {
        checkNotEmpty( "notEmpty", "    " );
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenGettinOneNullParameter() {
        checkEachParameterNotNull( "notNullable", "nonNull", null );
    }

}
