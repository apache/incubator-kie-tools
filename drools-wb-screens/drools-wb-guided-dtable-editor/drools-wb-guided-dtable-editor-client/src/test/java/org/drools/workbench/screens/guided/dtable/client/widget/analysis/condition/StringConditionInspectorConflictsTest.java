/*
 * Copyright 2015 JBoss Inc
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

package org.drools.workbench.screens.guided.dtable.client.widget.analysis.condition;

import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class StringConditionInspectorConflictsTest {

    @Test
    public void test000() throws Exception {
        StringConditionInspector a = getCondition( "Toni", "!=" );
        StringConditionInspector b = getCondition( "Toni", "!=" );

        assertFalse( a.conflicts( b ) );
        assertFalse( b.conflicts( a ) );
    }

    @Test
    public void test001() throws Exception {
        StringConditionInspector a = getCondition( "Toni", "==" );
        StringConditionInspector b = getCondition( "Toni", "!=" );

        assertTrue( a.conflicts( b ) );
        assertTrue( b.conflicts( a ) );
    }

    @Test
    public void test002() throws Exception {
        StringConditionInspector a = getCondition( "Toni", "==" );
        StringConditionInspector b = getCondition( "Eder", "!=" );

        assertFalse( a.conflicts( b ) );
        assertFalse( b.conflicts( a ) );
    }

    @Test
    public void test003() throws Exception {
        StringConditionInspector a = getCondition( "Toni, Michael, Eder", "in" );
        StringConditionInspector b = getCondition( "Toni", "!=" );

        assertTrue( a.conflicts( b ) );
        assertTrue( b.conflicts( a ) );
    }

    @Test
    public void test004() throws Exception {
        StringConditionInspector a = getCondition( "Toni, Michael, Eder", "in" );
        StringConditionInspector b = getCondition( "Toni", "==" );

        assertFalse( a.conflicts( b ) );
        assertFalse( b.conflicts( a ) );
    }

    @Test
    public void test005() throws Exception {
        StringConditionInspector a = getCondition( "Toni, Michael", "in" );
        StringConditionInspector b = getCondition( "Eder", "==" );

        assertTrue( a.conflicts( b ) );
        assertTrue( b.conflicts( a ) );
    }

    @Test
    public void test006() throws Exception {
        StringConditionInspector a = getCondition( "Toni, Michael", "in" );
        StringConditionInspector b = getCondition( "Eder", "!=" );

        assertFalse( a.conflicts( b ) );
        assertFalse( b.conflicts( a ) );
    }

    @Test
    public void test007() throws Exception {
        StringConditionInspector a = getCondition( "Toni, Michael", "in" );
        StringConditionInspector b = getCondition( "Eder, John", "in" );

        assertTrue( a.conflicts( b ) );
        assertTrue( b.conflicts( a ) );
    }

    @Test
    public void test008() throws Exception {
        StringConditionInspector a = getCondition( "Toni, Michael", "in" );
        StringConditionInspector b = getCondition( "Toni, Eder", "in" );

        assertFalse( a.conflicts( b ) );
        assertFalse( b.conflicts( a ) );
    }

    private StringConditionInspector getCondition( String value,
                                                   String operator ) {
        return new StringConditionInspector( mock( Pattern52.class ), "name", value, operator );
    }
}