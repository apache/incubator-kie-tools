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

import java.util.Arrays;
import java.util.Collection;

import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import static java.lang.String.format;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith( Parameterized.class )
public class StringConditionInspectorConflictOverlapTest {

    private final String value1;
    private final String value2;
    private final String operator1;
    private final String operator2;
    private final boolean conflictExpected;
    private final boolean overlapExpected;

    @Test
    public void parametrizedConflictTest() {
        StringConditionInspector a = getCondition( value1, operator1 );
        StringConditionInspector b = getCondition( value2, operator2 );

        boolean conflicts = a.conflicts( b );
        assertEquals( getAssertDescription( a, b, conflictExpected, "conflict" ), conflictExpected, conflicts );
        boolean conflicts1 = b.conflicts( a );
        assertEquals( getAssertDescription( b, a, conflictExpected, "conflict" ), conflictExpected, conflicts1 );
    }

    @Test
    public void parametrizedOverlapTest() {
        StringConditionInspector a = getCondition( value1, operator1 );
        StringConditionInspector b = getCondition( value2, operator2 );

        boolean overlaps = a.overlaps( b );
        assertEquals( getAssertDescription( a, b, overlapExpected, "overlap" ), overlapExpected, overlaps );
        boolean overlaps1 = b.overlaps( a );
        assertEquals( getAssertDescription( b, a, overlapExpected, "overlap" ), overlapExpected, overlaps1 );
    }

    public StringConditionInspectorConflictOverlapTest( String operator1,
                                                        String value1,
                                                        String operator2,
                                                        String value2,
                                                        boolean conflictExpected,
                                                        boolean overlapExpected ) {
        this.value1 = value1;
        this.value2 = value2;
        this.operator1 = operator1;
        this.operator2 = operator2;
        this.conflictExpected = conflictExpected;
        this.overlapExpected = overlapExpected;
    }

    @Parameters
    public static Collection<Object[]> testData() {
        return Arrays.asList( new Object[][] {
            // matches and soundslike are probably not doable...
                // op1, val1, op2, val2, conflicts, overlaps
                {"==", "a", "==", "a", false, true},
                {"!=", "a", "!=", "a", false, true},
                {">", "a", ">", "a", false, true},
                {">=", "a", ">=", "a", false, true},
                {"<", "a", "<", "a", false, true},
                {"<=", "a", "<=", "a", false, true},
                {"in", "a,b", "in", "a,b", false, true},
                {"not in", "a,b", "not in", "a,b", false, true},
                {"matches", "a", "matches", "a", false, true},
                {"soundslike", "a", "soundslike", "a", false, true},

                {"==", "a", "!=", "b", false, true},
                {"==", "a", ">", " ", false, false},
                {"==", "a", ">=", "a", false, true},
                {"==", "a", "<", "b", false, false},
                {"==", "a", "<=", "a", false, true},
                {"==", "a", "in", "a,b", false, true},
                {"==", "a", "not in", "b,c,d", false, true},
                {"==", "a", "matches", "a", false, true},
                {"==", "a", "soundslike", "a", false, true},

                {"==", "a", "!=", "a", true, false},
                {"==", "a", ">", "a", true, false},
                {"==", "a", ">=", "b", false, false},
                {"==", "a", "<", "a", true, false},
                {"==", "a", "<=", " ", false, false},
                {"==", "a", "in", "b,c,d", true, false},
                {"==", "a", "not in", "a,b", true, false},
                {"==", "a", "matches", "b", true, false},
                {"==", "a", "soundslike", "b", true, false},

                {"!=", "a", "!=", "b", false, true},
                {"!=", "a", ">", " ", false, false},
                {"!=", "a", ">=", "a", true, false},
                {"!=", "a", "<", "b", false, false},
                {"!=", "a", "<=", "a", true, false},
                {"!=", "a", "in", "a,b", false, true},
                {"!=", "a", "in", "b,c,d", false, true},
                {"!=", "a", "not in", "b,c,d", false, true},
                {"!=", "a", "matches", "b", false, true},
                {"!=", "a", "soundslike", "b", false, true},

                {"!=", "a", "in", "a", true, false},
                {"!=", "a", "matches", "a", true, false},
                {"!=", "a", "soundslike", "a", true, false},

                {">", "a", ">", "b", false, true},
                {">", "a", ">=", "a", false, true},
                {">", "a", "<", "c", false, false},
                {">", "a", "<=", "b", false, false},
                {">", "a", "in", "a,b", false, false},
                {">", "a", "not in", "b,c,d", false, false},
                {">", "a", "matches", "b", false, false},
                {">", "a", "soundslike", "b", false, false},

                {">", "a", "<", "a", true, false},
                {">", "a", "<=", "a", true, false},
                {">", "a", "in", "0,1,A,B,a", false, false},
                {">", "a", "matches", "a", true, false},
                {">", "a", "soundslike", "", false, false},

                {">=", "a", ">=", "b", false, true},
                {">=", "a", "<", "b", false, false},
                {">=", "a", "<=", "a", false, true},
                {">=", "a", "in", "a", false, true},
                {">=", "a", "not in", "b,c,d", false, true},
                {">=", "a", "matches", "a", false, true},
                {">=", "a", "soundslike", "a", false, true},

                {">=", "a", "<", " ", false, false},
                {">=", "a", "<=", " ", false, false},
                {">=", "a", "in", "0,1,A,B", false, false},
                {">=", "a", "matches", "A", false, false},
                {">=", "a", "soundslike", "", false, false},

                {"<", "a", "<", "b", false, true},
                {"<", "a", "<=", "a", false, true},
                {"<", "a", "in", "A,B,a,b", false, false},
                {"<", "a", "not in", "b,c,d", false, false},
                {"<", "a", "matches", "A", false, false},
                {"<", "a", "soundslike", "", false, false},

                {"<", "a", "in", "b,c,d", false, false},
                {"<", "a", "matches", "b", false, false},
                {"<", "a", "soundslike", "b", false, false},

                {"<=", "a", "<=", "b", false, true},
                {"<=", "a", "in", "A,B,a,b", false, true},
                {"<=", "a", "not in", "b,c,d", false, true},
                {"<=", "a", "matches", "A", false, false},
                {"<=", "a", "soundslike", "", false, false},

                {"<=", "a", "in", "b,c,d", false, false},
                {"<=", "a", "matches", "b", false, false},
                {"<=", "a", "soundslike", "b", false, false},

                {"in", "a,b", "in", "b,c,d", false, true},
                {"in", "a,b", "not in", "b,c,d", false, true},
                {"in", "a,b", "matches", "a", false, true},
                {"in", "a,b", "soundslike", "a", false, true},

                {"in", "a,b", "in", "c,d", true, false},
                {"in", "a,b", "not in", "a,b", true, false},
                {"in", "a,b", "matches", "c", true, false},
                {"in", "a,b", "soundslike", "c", true, false},

                {"not in", "a,b", "matches", "c", false, true},
                {"not in", "a,b", "soundslike", "c", false, true},

                {"not in", "a,b", "matches", "a", true, false},

                {"matches", "a", "soundslike", "a", false, true},

                {"matches", "a", "soundslike", "b", true, false},
        } );
    }

    private StringConditionInspector getCondition( String value,
                                                   String operator ) {
        return new StringConditionInspector( mock( Pattern52.class ), "name", value, operator );
    }

    private String getAssertDescription( StringConditionInspector a,
                                         StringConditionInspector b,
                                         boolean conflictExpected,
                                         String condition ) {
        return format( "Expected condition '%s' %sto %s with condition '%s':",
                       a.toHumanReadableString(),
                       conflictExpected ? "" : "not ",
                       condition,
                       b.toHumanReadableString() );
    }
}