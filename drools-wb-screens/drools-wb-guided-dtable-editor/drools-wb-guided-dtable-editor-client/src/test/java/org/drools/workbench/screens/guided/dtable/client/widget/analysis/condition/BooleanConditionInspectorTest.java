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

public class BooleanConditionInspectorTest {

    @Test
    public void testConflicts001() throws Exception {
        BooleanConditionInspector a = getCondition( true, "!=" );
        BooleanConditionInspector b = getCondition( true, "!=" );

        assertFalse( a.conflicts( b ) );
        assertFalse( b.conflicts( a ) );
    }

    @Test
    public void testConflicts002() throws Exception {
        BooleanConditionInspector a = getCondition( true, "==" );
        BooleanConditionInspector b = getCondition( true, "!=" );

        assertTrue( a.conflicts( b ) );
        assertTrue( b.conflicts( a ) );
    }

    @Test
    public void testConflicts003() throws Exception {
        BooleanConditionInspector a = getCondition( true, "!=" );
        BooleanConditionInspector b = getCondition( false, "!=" );

        assertTrue( a.conflicts( b ) );
        assertTrue( b.conflicts( a ) );
    }

    @Test
    public void testConflicts004() throws Exception {
        BooleanConditionInspector a = getCondition( true, "!=" );
        BooleanConditionInspector b = getCondition( false, "==" );

        assertFalse( a.conflicts( b ) );
        assertFalse( b.conflicts( a ) );
    }

    @Test
    public void testConflicts005() throws Exception {
        BooleanConditionInspector a = getCondition( false, "!=" );
        BooleanConditionInspector b = getCondition( false, "==" );

        assertTrue( a.conflicts( b ) );
        assertTrue( b.conflicts( a ) );
    }

    @Test
    public void testRedundancy001() throws Exception {
        BooleanConditionInspector a = getCondition( true, "!=" );
        BooleanConditionInspector b = getCondition( true, "!=" );

        assertTrue( a.isRedundant( b ) );
        assertTrue( b.isRedundant( a ) );
    }

    @Test
    public void testRedundancy002() throws Exception {
        BooleanConditionInspector a = getCondition( true, "==" );
        BooleanConditionInspector b = getCondition( true, "!=" );

        assertFalse( a.isRedundant( b ) );
        assertFalse( b.isRedundant( a ) );
    }

    @Test
    public void testRedundancy003() throws Exception {
        BooleanConditionInspector a = getCondition( true, "!=" );
        BooleanConditionInspector b = getCondition( false, "!=" );

        assertFalse( a.isRedundant( b ) );
        assertFalse( b.isRedundant( a ) );
    }

    @Test
    public void testRedundancy004() throws Exception {
        BooleanConditionInspector a = getCondition( true, "!=" );
        BooleanConditionInspector b = getCondition( false, "==" );

        assertTrue( a.isRedundant( b ) );
        assertTrue( b.isRedundant( a ) );
    }

    @Test
    public void testRedundancy005() throws Exception {
        BooleanConditionInspector a = getCondition( false, "!=" );
        BooleanConditionInspector b = getCondition( false, "==" );

        assertFalse( a.isRedundant( b ) );
        assertFalse( b.isRedundant( a ) );
    }

    private BooleanConditionInspector getCondition( boolean value,
                                                    String operator ) {
        return new BooleanConditionInspector( mock( Pattern52.class ), "approved", value, operator );
    }

}