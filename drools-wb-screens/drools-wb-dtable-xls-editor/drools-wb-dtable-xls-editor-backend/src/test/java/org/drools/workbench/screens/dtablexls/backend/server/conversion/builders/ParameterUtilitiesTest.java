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

package org.drools.workbench.screens.dtablexls.backend.server.conversion.builders;

import org.junit.Test;

import static org.junit.Assert.*;

public class ParameterUtilitiesTest {

    private ParameterUtilities utilities = new ParameterUtilities();

    @Test
    public void testIndexedParameters_LHSFieldValue() {
        final String result = utilities.convertIndexedParametersToTemplateKeys( "field1 == $1, field2 == $2",
                                                                                ParameterizedValueBuilder.Part.LHS );
        assertEquals( "field1 == \"@{param1}\", field2 == \"@{param2}\"",
                      result );
    }

    @Test
    public void testIndexedParameters_LHSFieldName() {
        final String result = utilities.convertIndexedParametersToTemplateKeys( "$1 != null, $2 != null",
                                                                                ParameterizedValueBuilder.Part.LHS );
        assertEquals( "@{param1} != null, @{param2} != null",
                      result );
    }

    @Test
    public void testSingleParameter_LHSFieldValue() {
        final String result = utilities.convertSingleParameterToTemplateKey( "field1 == $param",
                                                                             ParameterizedValueBuilder.Part.LHS );
        assertEquals( "field1 == \"@{param1}\"",
                      result );
    }

    @Test
    public void testSingleParameter_LHSFieldName() {
        final String result = utilities.convertSingleParameterToTemplateKey( "$param != null",
                                                                             ParameterizedValueBuilder.Part.LHS );
        assertEquals( "@{param1} != null",
                      result );
    }

    @Test
    public void testIndexedParameters_RHSFieldValue() {
        final String result = utilities.convertIndexedParametersToTemplateKeys( "setField1( $1 ); setField2( $2 )",
                                                                                ParameterizedValueBuilder.Part.RHS );
        assertEquals( "setField1( @{param1} ); setField2( @{param2} )",
                      result );
    }

    @Test
    public void testSingleParameter_RHSFieldValue() {
        final String result = utilities.convertSingleParameterToTemplateKey( "setField( $param )",
                                                                             ParameterizedValueBuilder.Part.RHS );
        assertEquals( "setField( @{param1} )",
                      result );
    }

}
