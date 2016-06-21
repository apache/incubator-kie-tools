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
package org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache;

import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.Field;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class FieldInspectorTest {


    private FieldInspector a;
    private FieldInspector b;

    @Before
    public void setUp() throws Exception {
        a = new FieldInspector( new Field( "org.Person",
                                           "String",
                                           "name" ) );
        b = new FieldInspector( new Field( "org.Person",
                                           "String",
                                           "name" ) );

    }

    @Test
    public void testRedundancy01() throws Exception {
        assertTrue( a.isRedundant( b ) );
        assertTrue( b.isRedundant( a ) );
    }

    @Test
    public void testRedundancy02() throws Exception {
        final FieldInspector x = new FieldInspector( new Field( "org.Address",
                                                                "String",
                                                                "name" ) );

        assertFalse( x.isRedundant( b ) );
        assertFalse( b.isRedundant( x ) );
    }

    @Test
    public void testSubsumpt01() throws Exception {
        assertTrue( a.subsumes( b ) );
        assertTrue( b.subsumes( a ) );
    }

    @Test
    public void testSubsumpt02() throws Exception {
        final FieldInspector x = new FieldInspector( new Field( "org.Address",
                                                                "String",
                                                                "name" ) );

        assertFalse( x.subsumes( b ) );
        assertFalse( b.subsumes( x ) );
    }

}