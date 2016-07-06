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

import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util.RelationResolver;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.ObjectType;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.Pattern;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class PatternInspectorTest {


    private PatternInspector a;
    private PatternInspector b;

    @Before
    public void setUp() throws Exception {
        a = new PatternInspector( new Pattern( "a",
                                               new ObjectType( "org.Person" ) ) );
        b = new PatternInspector( new Pattern( "b",
                                               new ObjectType( "org.Person" ) ));
    }

    @Test
    public void testRedundancy01() throws Exception {
        assertTrue( a.isRedundant( b ) );
        assertTrue( b.isRedundant( a ) );
    }

    @Test
    public void testRedundancy02() throws Exception {
        final PatternInspector x = new PatternInspector( new Pattern( "x",
                                                                      new ObjectType( "org.Address" ) ));

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
        final PatternInspector x = new PatternInspector( new Pattern( "x",
                                                                      new ObjectType( "org.Address" ) ));

        assertFalse( x.subsumes( b ) );
        assertFalse( b.subsumes( x ) );
    }

}