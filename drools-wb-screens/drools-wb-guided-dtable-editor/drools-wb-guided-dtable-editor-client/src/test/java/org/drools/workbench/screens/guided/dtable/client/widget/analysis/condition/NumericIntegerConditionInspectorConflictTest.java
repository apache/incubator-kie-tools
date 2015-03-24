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

public class NumericIntegerConditionInspectorConflictTest {

    @Test
    public void testConflict001() throws Exception {
        NumericIntegerConditionInspector a = getCondition( 0, "==" );
        NumericIntegerConditionInspector b = getCondition( 0, "!=" );

        assertTrue( a.conflicts( b ) );
        assertTrue( b.conflicts( a ) );
    }

    @Test
    public void testConflict002() throws Exception {
        NumericIntegerConditionInspector a = getCondition( 0, "==" );
        NumericIntegerConditionInspector b = getCondition( 0, "==" );

        assertFalse( a.conflicts( b ) );
        assertFalse( b.conflicts( a ) );
    }

    @Test
    public void testConflict003() throws Exception {
        NumericIntegerConditionInspector a = getCondition( 0, "==" );
        NumericIntegerConditionInspector b = getCondition( 1, "==" );

        assertTrue( a.conflicts( b ) );
        assertTrue( b.conflicts( a ) );
    }

    @Test
    public void testConflict004() throws Exception {
        NumericIntegerConditionInspector a = getCondition( 0, "!=" );
        NumericIntegerConditionInspector b = getCondition( 0, "!=" );

        assertFalse( a.conflicts( b ) );
        assertFalse( b.conflicts( a ) );
    }

    @Test
    public void testConflict005() throws Exception {
        NumericIntegerConditionInspector a = getCondition( 0, "!=" );
        NumericIntegerConditionInspector b = getCondition( 1, "!=" );

        assertFalse( a.conflicts( b ) );
        assertFalse( b.conflicts( a ) );
    }

    @Test
    public void testConflict006() throws Exception {
        NumericIntegerConditionInspector a = getCondition( 0, "<" );
        NumericIntegerConditionInspector b = getCondition( 10, ">" );

        assertTrue( a.conflicts( b ) );
        assertTrue( b.conflicts( a ) );
    }

    @Test
    public void testConflict007() throws Exception {
        NumericIntegerConditionInspector a = getCondition( 10, "<" );
        NumericIntegerConditionInspector b = getCondition( 0, ">" );

        assertFalse( a.conflicts( b ) );
        assertFalse( b.conflicts( a ) );
    }

    @Test
    public void testConflict008() throws Exception {
        NumericIntegerConditionInspector a = getCondition( 0, "==" );
        NumericIntegerConditionInspector b = getCondition( 10, ">" );

        assertTrue( a.conflicts( b ) );
        assertTrue( b.conflicts( a ) );
    }

    @Test
    public void testConflict009() throws Exception {
        NumericIntegerConditionInspector a = getCondition( 10, "==" );
        NumericIntegerConditionInspector b = getCondition( 0, ">" );

        assertFalse( a.conflicts( b ) );
        assertFalse( b.conflicts( a ) );
    }

    @Test
    public void testConflict010() throws Exception {
        NumericIntegerConditionInspector a = getCondition( 10, ">" );
        NumericIntegerConditionInspector b = getCondition( 0, "<=" );

        assertTrue( a.conflicts( b ) );
        assertTrue( b.conflicts( a ) );
    }

    @Test
    public void testConflict011() throws Exception {
        NumericIntegerConditionInspector a = getCondition( 100, ">" );
        NumericIntegerConditionInspector b = getCondition( 100, ">" );

        assertFalse( a.conflicts( b ) );
        assertFalse( b.conflicts( a ) );
    }

    private NumericIntegerConditionInspector getCondition( int value,
                                                           String operator ) {
        return new NumericIntegerConditionInspector( mock( Pattern52.class ), "age", value, operator );
    }

}