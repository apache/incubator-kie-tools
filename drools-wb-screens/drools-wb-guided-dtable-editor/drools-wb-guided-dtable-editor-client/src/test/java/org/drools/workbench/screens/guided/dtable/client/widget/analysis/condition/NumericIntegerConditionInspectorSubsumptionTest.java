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

public class NumericIntegerConditionInspectorSubsumptionTest {

    @Test
    public void testSubsume001() throws Exception {
        NumericIntegerConditionInspector a = getCondition( 0, "!=" );
        NumericIntegerConditionInspector b = getCondition( 0, "!=" );

        assertTrue( a.subsumes( b ) );
        assertTrue( b.subsumes( a ) );
    }

    @Test
    public void testSubsumeEquals001() throws Exception {
        NumericIntegerConditionInspector a = getCondition( 10, "==" );
        NumericIntegerConditionInspector b = getCondition( 0, ">" );

        assertFalse( a.subsumes( b ) );
        assertTrue( b.subsumes( a ) );
    }

    @Test
    public void testSubsumeEquals002() throws Exception {
        NumericIntegerConditionInspector a = getCondition( 0, "==" );
        NumericIntegerConditionInspector b = getCondition( 10, ">" );

        assertFalse( a.subsumes( b ) );
        assertFalse( b.subsumes( a ) );
    }

    @Test
    public void testSubsumeEquals003() throws Exception {
        NumericIntegerConditionInspector a = getCondition( 0, "==" );
        NumericIntegerConditionInspector b = getCondition( 10, "<" );

        assertFalse( a.subsumes( b ) );
        assertTrue( b.subsumes( a ) );
    }

    @Test
    public void testSubsumeEquals004() throws Exception {
        NumericIntegerConditionInspector a = getCondition( 10, "==" );
        NumericIntegerConditionInspector b = getCondition( 0, "<" );

        assertFalse( a.subsumes( b ) );
        assertFalse( b.subsumes( a ) );
    }

    @Test
    public void testSubsumeEquals005() throws Exception {
        NumericIntegerConditionInspector a = getCondition( 10, "==" );
        NumericIntegerConditionInspector b = getCondition( 0, "==" );

        assertFalse( a.subsumes( b ) );
        assertFalse( b.subsumes( a ) );
    }

    @Test
    public void testSubsumeEquals006() throws Exception {
        NumericIntegerConditionInspector a = getCondition( 0, "==" );
        NumericIntegerConditionInspector b = getCondition( 0, "==" );

        assertTrue( a.subsumes( b ) );
        assertTrue( b.subsumes( a ) );
    }

    @Test
    public void testSubsumeEquals007() throws Exception {
        NumericIntegerConditionInspector a = getCondition( 0, "==" );
        NumericIntegerConditionInspector b = getCondition( 10, "<" );

        assertFalse( a.subsumes( b ) );
        assertTrue( b.subsumes( a ) );
    }

    @Test
    public void testSubsumeEquals008() throws Exception {
        NumericIntegerConditionInspector a = getCondition( 0, "==" );
        NumericIntegerConditionInspector b = getCondition( 0, "<" );

        assertFalse( a.subsumes( b ) );
        assertFalse( b.subsumes( a ) );
    }

    @Test
    public void testSubsumeGreaterThan001() throws Exception {
        NumericIntegerConditionInspector a = getCondition( 10, ">" );
        NumericIntegerConditionInspector b = getCondition( 0, ">" );

        assertFalse( a.subsumes( b ) );
        assertTrue( b.subsumes( a ) );
    }

    @Test
    public void testSubsumeGreaterThan002() throws Exception {
        NumericIntegerConditionInspector a = getCondition( 0, ">" );
        NumericIntegerConditionInspector b = getCondition( 0, ">" );

        assertTrue( a.subsumes( b ) );
        assertTrue( b.subsumes( a ) );
    }

    @Test
    public void testSubsumeGreaterThan003() throws Exception {
        NumericIntegerConditionInspector a = getCondition( 0, ">" );
        NumericIntegerConditionInspector b = getCondition( 10, ">" );

        assertTrue( a.subsumes( b ) );
        assertFalse( b.subsumes( a ) );
    }

    @Test
    public void testSubsume004() throws Exception {
        NumericIntegerConditionInspector a = getCondition( 0, ">" );
        NumericIntegerConditionInspector b = getCondition( 0, ">=" );

        assertFalse( a.subsumes( b ) );
        assertTrue( b.subsumes( a ) );
    }

    @Test
    public void testSubsume005() throws Exception {
        NumericIntegerConditionInspector a = getCondition( 0, ">" );
        NumericIntegerConditionInspector b = getCondition( 1, ">=" );

        assertTrue( a.subsumes( b ) );
        assertTrue( b.subsumes( a ) );
    }

    @Test
    public void testSubsume006() throws Exception {
        NumericIntegerConditionInspector a = getCondition( 1, ">" );
        NumericIntegerConditionInspector b = getCondition( 0, ">=" );

        assertFalse( a.subsumes( b ) );
        assertTrue( b.subsumes( a ) );
    }

    @Test
    public void testSubsume007AndLicenseToTest() throws Exception {
        NumericIntegerConditionInspector a = getCondition( 0, ">" );
        NumericIntegerConditionInspector b = getCondition( -10, "==" );

        assertFalse( a.subsumes( b ) );
        assertFalse( b.subsumes( a ) );
    }

    @Test
    public void testSubsume008() throws Exception {
        NumericIntegerConditionInspector a = getCondition( 0, ">" );
        NumericIntegerConditionInspector b = getCondition( 0, "<" );

        assertFalse( a.subsumes( b ) );
        assertFalse( b.subsumes( a ) );
    }

    @Test
    public void testSubsume009() throws Exception {
        NumericIntegerConditionInspector a = getCondition( 0, ">" );
        NumericIntegerConditionInspector b = getCondition( 10, "<" );

        assertFalse( a.subsumes( b ) );
        assertFalse( b.subsumes( a ) );
    }

    @Test
    public void testSubsume010() throws Exception {
        NumericIntegerConditionInspector a = getCondition( 0, ">" );
        NumericIntegerConditionInspector b = getCondition( 0, "<=" );

        assertFalse( a.subsumes( b ) );
        assertFalse( b.subsumes( a ) );
    }

    @Test
    public void testSubsume011() throws Exception {
        NumericIntegerConditionInspector a = getCondition( 0, ">" );
        NumericIntegerConditionInspector b = getCondition( 10, "<=" );

        assertFalse( a.subsumes( b ) );
        assertFalse( b.subsumes( a ) );
    }

    @Test
    public void testSubsume012() throws Exception {
        NumericIntegerConditionInspector a = getCondition( 0, ">" );
        NumericIntegerConditionInspector b = getCondition( 10, "==" );

        assertTrue( a.subsumes( b ) );
        assertFalse( b.subsumes( a ) );
    }

    @Test
    public void testSubsumeEqualsOrLessThan001() throws Exception {
        NumericIntegerConditionInspector a = getCondition( 0, "<=" );
        NumericIntegerConditionInspector b = getCondition( 10, "==" );

        assertFalse( a.subsumes( b ) );
        assertFalse( b.subsumes( a ) );
    }

    @Test
    public void testSubsumeEqualsOrLessThan002() throws Exception {
        NumericIntegerConditionInspector a = getCondition( 10, "<=" );
        NumericIntegerConditionInspector b = getCondition( 0, "==" );

        assertTrue( a.subsumes( b ) );
        assertFalse( b.subsumes( a ) );
    }

    @Test
    public void testSubsumeEqualsOrLessThan003() throws Exception {
        NumericIntegerConditionInspector a = getCondition( 10, "<=" );
        NumericIntegerConditionInspector b = getCondition( 0, "<=" );

        assertTrue( a.subsumes( b ) );
        assertFalse( b.subsumes( a ) );
    }

    @Test
    public void testSubsumeEqualsOrLessThan004() throws Exception {
        NumericIntegerConditionInspector a = getCondition( 0, "<=" );
        NumericIntegerConditionInspector b = getCondition( 0, "<=" );

        assertTrue( a.subsumes( b ) );
        assertTrue( b.subsumes( a ) );
    }

    @Test
    public void testSubsumeEqualsOrLessThan005() throws Exception {
        NumericIntegerConditionInspector a = getCondition( 10, "<=" );
        NumericIntegerConditionInspector b = getCondition( 0, ">" );

        assertFalse( a.subsumes( b ) );
        assertFalse( b.subsumes( a ) );
    }

    @Test
    public void testSubsumeEqualsOrLessThan006() throws Exception {
        NumericIntegerConditionInspector a = getCondition( 10, "<=" );
        NumericIntegerConditionInspector b = getCondition( 0, "<" );

        assertTrue( a.subsumes( b ) );
        assertFalse( b.subsumes( a ) );
    }

    @Test
    public void testSubsumeEqualsOrLessThan007() throws Exception {
        NumericIntegerConditionInspector a = getCondition( 0, "<=" );
        NumericIntegerConditionInspector b = getCondition( 0, "<" );

        assertTrue( a.subsumes( b ) );
        assertFalse( b.subsumes( a ) );
    }

    @Test
    public void testSubsumeEqualsOrLessThan008() throws Exception {
        NumericIntegerConditionInspector a = getCondition( 0, "<=" );
        NumericIntegerConditionInspector b = getCondition( 10, "<" );

        assertFalse( a.subsumes( b ) );
        assertTrue( b.subsumes( a ) );
    }

    @Test
    public void testSubsumeEqualsOrLessThan009() throws Exception {
        NumericIntegerConditionInspector a = getCondition( 0, "<=" );
        NumericIntegerConditionInspector b = getCondition( 1, "<" );

        assertTrue( a.subsumes( b ) );
        assertTrue( b.subsumes( a ) );
    }

    @Test
    public void testSubsumeEqualsOrLessThan010() throws Exception {
        NumericIntegerConditionInspector a = getCondition( 0, "<=" );
        NumericIntegerConditionInspector b = getCondition( 1, "<" );

        assertTrue( a.subsumes( b ) );
        assertTrue( b.subsumes( a ) );
    }

    @Test
    public void testSubsumeEqualsOrLessThan011() throws Exception {
        NumericIntegerConditionInspector a = getCondition( 0, "<=" );
        NumericIntegerConditionInspector b = getCondition( 10, "!=" );

        assertTrue( a.subsumes( b ) );
        assertTrue( b.subsumes( a ) );
    }

    @Test
    public void testSubsumeEqualsOrLessThan012() throws Exception {
        NumericIntegerConditionInspector a = getCondition( 10, "<=" );
        NumericIntegerConditionInspector b = getCondition( 0, "!=" );

        assertFalse( a.subsumes( b ) );
        assertFalse( b.subsumes( a ) );
    }

    @Test
    public void testSubsumeEqualsOrLessThan013() throws Exception {
        NumericIntegerConditionInspector a = getCondition( 0, "<=" );
        NumericIntegerConditionInspector b = getCondition( 0, "!=" );

        assertFalse( a.subsumes( b ) );
        assertFalse( b.subsumes( a ) );
    }

    @Test
    public void testSubsumeNotEqual001() throws Exception {
        NumericIntegerConditionInspector a = getCondition( 0, "!=" );
        NumericIntegerConditionInspector b = getCondition( 0, "!=" );

        assertTrue( a.subsumes( b ) );
        assertTrue( b.subsumes( a ) );
    }

    @Test
    public void testSubsumeNotEqual002() throws Exception {
        NumericIntegerConditionInspector a = getCondition( 0, "!=" );
        NumericIntegerConditionInspector b = getCondition( 1, "!=" );

        assertFalse( a.subsumes( b ) );
        assertFalse( b.subsumes( a ) );
    }

    @Test
    public void testSubsumeNotEqual003() throws Exception {
        NumericIntegerConditionInspector a = getCondition( 0, "!=" );
        NumericIntegerConditionInspector b = getCondition( 0, "==" );

        assertFalse( a.subsumes( b ) );
        assertFalse( b.subsumes( a ) );
    }

    @Test
    public void testSubsumeNotEqual004() throws Exception {
        NumericIntegerConditionInspector a = getCondition( 0, "!=" );
        NumericIntegerConditionInspector b = getCondition( 10, "==" );

        assertTrue( a.subsumes( b ) );
        assertTrue( b.subsumes( a ) );
    }

    @Test
    public void testSubsumeNotEqual005() throws Exception {
        NumericIntegerConditionInspector a = getCondition( 0, "!=" );
        NumericIntegerConditionInspector b = getCondition( 0, ">" );

        assertTrue( a.subsumes( b ) );
        assertTrue( b.subsumes( a ) );
    }

    @Test
    public void testSubsumeNotEqual006() throws Exception {
        NumericIntegerConditionInspector a = getCondition( 10, "!=" );
        NumericIntegerConditionInspector b = getCondition( 0, ">" );

        assertFalse( a.subsumes( b ) );
        assertFalse( b.subsumes( a ) );
    }

    @Test
    public void testSubsumeNotEqual007AndYouOnlyTestTwice() throws Exception {
        NumericIntegerConditionInspector a = getCondition( 10, "!=" );
        NumericIntegerConditionInspector b = getCondition( 0, ">=" );

        assertFalse( a.subsumes( b ) );
        assertFalse( b.subsumes( a ) );
    }

    @Test
    public void testSubsumeNotEqual008() throws Exception {
        NumericIntegerConditionInspector a = getCondition( 0, "!=" );
        NumericIntegerConditionInspector b = getCondition( 0, "<=" );

        assertFalse( a.subsumes( b ) );
        assertFalse( b.subsumes( a ) );
    }

    @Test
    public void testSubsumeNotEqual009() throws Exception {
        NumericIntegerConditionInspector a = getCondition( 0, "!=" );
        NumericIntegerConditionInspector b = getCondition( 10, ">=" );

        assertTrue( a.subsumes( b ) );
        assertTrue( b.subsumes( a ) );
    }

    @Test
    public void testSubsumeNotEqual010() throws Exception {
        NumericIntegerConditionInspector a = getCondition( 0, "!=" );
        NumericIntegerConditionInspector b = getCondition( 10, "<" );

        assertFalse( a.subsumes( b ) );
        assertFalse( b.subsumes( a ) );
    }

    @Test
    public void testSubsumeNotEqual011() throws Exception {
        NumericIntegerConditionInspector a = getCondition( 0, "!=" );
        NumericIntegerConditionInspector b = getCondition( 0, "<" );

        assertTrue( a.subsumes( b ) );
        assertTrue( b.subsumes( a ) );
    }

    @Test
    public void testSubsumeNotEqual012() throws Exception {
        NumericIntegerConditionInspector a = getCondition( 0, "!=" );
        NumericIntegerConditionInspector b = getCondition( 10, ">" );

        assertTrue( a.subsumes( b ) );
        assertTrue( b.subsumes( a ) );
    }

    @Test
    public void testSubsumeLessThan001() throws Exception {
        NumericIntegerConditionInspector a = getCondition( 0, "<" );
        NumericIntegerConditionInspector b = getCondition( 0, "<" );

        assertTrue( a.subsumes( b ) );
        assertTrue( b.subsumes( a ) );
    }

    @Test
    public void testSubsumeLessThan002() throws Exception {
        NumericIntegerConditionInspector a = getCondition( 10, "<" );
        NumericIntegerConditionInspector b = getCondition( 0, "<" );

        assertTrue( a.subsumes( b ) );
        assertFalse( b.subsumes( a ) );
    }

    @Test
    public void testSubsumeLessThan003() throws Exception {
        NumericIntegerConditionInspector a = getCondition( 0, "<" );
        NumericIntegerConditionInspector b = getCondition( 10, "<" );

        assertFalse( a.subsumes( b ) );
        assertTrue( b.subsumes( a ) );
    }

    private NumericIntegerConditionInspector getCondition( int value,
                                                           String operator ) {
        return new NumericIntegerConditionInspector( mock( Pattern52.class ), "age", value, operator );
    }
}