/*
 * Copyright 2014 JBoss Inc
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
package org.kie.workbench.common.services.datamodel.backend.server.builder.util;

import org.junit.Test;

import static org.jgroups.util.Util.*;

/**
 * DataEnumLoader tests
 */
public class DataEnumLoaderTests {

    @Test
    public void testValidDependentEnum() {
        final String e = "'Fact.field2[field1=A]':'[1, 2, 3]'";
        final DataEnumLoader loader = new DataEnumLoader( e );
        assertFalse( loader.hasErrors() );
    }

    @Test
    public void testInvalidDependentEnum_Empty() {
        final String e = "'Fact.field2[]':'[1, 2, 3]'";
        final DataEnumLoader loader = new DataEnumLoader( e );
        assertTrue( loader.hasErrors() );
        assertTrue( loader.getErrors().contains( "Invalid dependent definition: Empty [] detected." ) );
    }

    @Test
    public void testInvalidDependentEnum_NoEquals() {
        final String e = "'Fact.field2[field1]':'[1, 2, 3]'";
        final DataEnumLoader loader = new DataEnumLoader( e );
        assertTrue( loader.hasErrors() );
        assertTrue( loader.getErrors().contains( "Invalid dependent definition: Expected to find '='." ) );
    }

    @Test
    public void testInvalidDependentEnum_EqualsNoField() {
        final String e = "'Fact.field2[=A]':'[1, 2, 3]'";
        final DataEnumLoader loader = new DataEnumLoader( e );
        assertTrue( loader.hasErrors() );
        assertTrue( loader.getErrors().contains( "Invalid dependent definition: No field detected." ) );
    }

    @Test
    public void testInvalidDependentEnum_EqualsNoValue() {
        final String e = "'Fact.field2[field1=]':'[1, 2, 3]'";
        final DataEnumLoader loader = new DataEnumLoader( e );
        assertTrue( loader.hasErrors() );
        assertTrue( loader.getErrors().contains( "Invalid dependent definition: No value detected." ) );
    }

    @Test
    public void testInvalidDependentEnum_ContainsQuotes1() {
        final String e = "'Fact.field2[field1=\"A\"]':'[1, 2, 3]'";
        final DataEnumLoader loader = new DataEnumLoader( e );
        assertTrue( loader.hasErrors() );
        assertTrue( loader.getErrors().contains( "Invalid dependent definition: Found quote literal." ) );
    }

    @Test
    public void testInvalidDependentEnum_ContainsQuotes2() {
        final String e = "'Fact.field2[field1=\"A]':'[1, 2, 3]'";
        final DataEnumLoader loader = new DataEnumLoader( e );
        assertTrue( loader.hasErrors() );
        assertTrue( loader.getErrors().contains( "Invalid dependent definition: Found quote literal." ) );
    }

    @Test
    public void testInvalidDependentEnum_ContainsQuotes3() {
        final String e = "'Fact.field2[field1=A\"]':'[1, 2, 3]'";
        final DataEnumLoader loader = new DataEnumLoader( e );
        assertTrue( loader.hasErrors() );
        assertTrue( loader.getErrors().contains( "Invalid dependent definition: Found quote literal." ) );
    }

}
