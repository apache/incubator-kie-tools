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

package org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.condition;

import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.Column;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.Field;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.FieldCondition;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.keys.Values;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith( MockitoJUnitRunner.class )
public class StringConditionInspectorOverlapTest {

    @Mock
    private Field field;

    @Test
    public void test001() throws Exception {
        StringConditionInspector a = getCondition( new Values<>( "Toni" ), "==" );
        StringConditionInspector b = getCondition( new Values<>( "Toni" ), "!=" );

        assertFalse( a.overlaps( b ) );
        assertFalse( b.overlaps( a ) );
    }

    @Test
    public void test002() throws Exception {
        StringConditionInspector a = getCondition( new Values<>( "Toni" ), "==" );
        StringConditionInspector b = getCondition( new Values<>( "Eder" ), "!=" );

        assertTrue( a.overlaps( b ) );
        assertTrue( b.overlaps( a ) );
    }

    @Test
    public void test003() throws Exception {
        StringConditionInspector a = getCondition( new Values<>( "Toni", "Michael", "Eder" ), "in" );
        StringConditionInspector b = getCondition( new Values<>( "Toni" ), "!=" );

        assertTrue( a.overlaps( b ) );
        assertTrue( b.overlaps( a ) );
    }

    @Test
    public void test004() throws Exception {
        StringConditionInspector a = getCondition( new Values<>( "Toni", "Michael", "Eder" ), "in" );
        StringConditionInspector b = getCondition( new Values<>( "Toni" ), "==" );

        assertTrue( a.overlaps( b ) );
        assertTrue( b.overlaps( a ) );
    }

    @Test
    public void test005() throws Exception {
        StringConditionInspector a = getCondition( new Values<>( "Toni", "Michael" ), "in" );
        StringConditionInspector b = getCondition( new Values<>( "Eder" ), "==" );

        assertFalse( a.overlaps( b ) );
        assertFalse( b.overlaps( a ) );
    }

    @Test
    public void test006() throws Exception {
        StringConditionInspector a = getCondition( new Values<>( "Toni", "Michael" ), "in" );
        StringConditionInspector b = getCondition( new Values<>( "Eder" ), "!=" );

        assertTrue( a.overlaps( b ) );
        assertTrue( b.overlaps( a ) );
    }

    @Test
    public void test007() throws Exception {
        StringConditionInspector a = getCondition( new Values<>( "Toni", "Michael" ), "in" );
        StringConditionInspector b = getCondition( new Values<>( "Eder", "John" ), "in" );

        assertFalse( a.overlaps( b ) );
        assertFalse( b.overlaps( a ) );
    }

    @Test
    public void test008() throws Exception {
        StringConditionInspector a = getCondition( new Values<>( "Toni", "Michael" ), "in" );
        StringConditionInspector b = getCondition( new Values<>( "Toni", "Eder" ), "in" );

        assertTrue( a.overlaps( b ) );
        assertTrue( b.overlaps( a ) );
    }

    @Test
    public void test009() throws Exception {
        StringConditionInspector a = getCondition( new Values<>( "Toni" ), "in" );
        StringConditionInspector b = getCondition( new Values<>( "Eder", "Toni" ), "in" );

        assertTrue( a.overlaps( b ) );
        assertTrue( b.overlaps( a ) );
    }

    @Test
    public void test010() throws Exception {
        StringConditionInspector a = getCondition( new Values<>( "" ), "==" );
        StringConditionInspector b = getCondition( new Values<>( "" ), "==" );

        assertFalse( a.overlaps( b ) );
        assertFalse( b.overlaps( a ) );
    }

    @Test
    public void test011() throws Exception {
        StringConditionInspector a = getCondition( new Values<>( "Toni" ), "==" );
        StringConditionInspector b = getCondition( new Values<>( "Toni" ), "==" );

        assertTrue( a.overlaps( b ) );
        assertTrue( b.overlaps( a ) );
    }

    private StringConditionInspector getCondition( final Values values,
                                                   final String operator ) {
        return new StringConditionInspector( new FieldCondition<String>( field, mock( Column.class ), operator, values ) );
    }
}