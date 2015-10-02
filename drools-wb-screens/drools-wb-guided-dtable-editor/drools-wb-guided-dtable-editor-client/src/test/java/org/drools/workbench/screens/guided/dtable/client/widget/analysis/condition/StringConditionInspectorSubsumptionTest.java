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

import static java.lang.String.format;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collection;

import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith( Parameterized.class )
public class StringConditionInspectorSubsumptionTest {

    private final String value1;
    private final String value2;
    private final String operator1;
    private final String operator2;
    private final boolean aSubsumesB;
    private final boolean bSubsumesA;

    @Test
    public void testASubsumesB() {
        StringConditionInspector a = getCondition( value1, operator1 );
        StringConditionInspector b = getCondition( value2, operator2 );

        assertEquals( getAssertDescription( a, b, aSubsumesB ), aSubsumesB, a.subsumes( b ) );
    }

    @Test
    public void testBSubsumesA() {
        StringConditionInspector a = getCondition( value1, operator1 );
        StringConditionInspector b = getCondition( value2, operator2 );

        assertEquals( getAssertDescription( b, a, bSubsumesA ), bSubsumesA, b.subsumes( a ) );
    }

    public StringConditionInspectorSubsumptionTest( String operator1,
                                                    String value1,
                                                    String operator2,
                                                    String value2,
                                                    boolean aSubsumesB,
                                                    boolean bSubsumesA ) {
        this.value1 = value1;
        this.value2 = value2;
        this.operator1 = operator1;
        this.operator2 = operator2;
        this.aSubsumesB = aSubsumesB;
        this.bSubsumesA = bSubsumesA;
    }

    @Parameters
    public static Collection<Object[]> testData() {
        return Arrays.asList( new Object[][] {
            // op1, val1, op2, val2, aSubsumesB, bSubsumesA
            { "==", "a", "==", "a", true, true },
            { "!=", "a", "!=", "a", true, true },
            { ">", "a", ">", "a", true, true },
            { ">=", "a", ">=", "a", true, true },
            { "<", "a", "<", "a", true, true },
            { "<=", "a", "<=", "a", true, true },
            { "in", "a,b", "in", "a,b", true, true },
            { "not in", "a,b", "not in", "a,b", true, true },
            { "matches", "a", "matches", "a", true, true },
            { "soundslike", "a", "soundslike", "a", true, true },

            { "==", "a", "soundslike", "a", true, true },
            { "matches", "a", "soundslike", "a", true, true },
            { "==", "a", "==", "b", false, false },
            { "==", "a", "!=", "a", false, false },
            { "==", "a", "!=", "b", false, true },
            { "==", "a", ">", "a", false, false },
            { "==", "a", ">", " ", false, false },
            { "==", "a", ">=", "b", false, false },
            { "==", "a", ">=", "a", false, true },
            { "==", "a", ">=", " ", false, false },

            { "==", "a", "<", "a", false, false },
            { "==", "a", "<", "b", false, false },
            { "==", "a", "<=", "a", false, true },
            { "==", "a", "<=", " ", false, false },
            { "==", "a", "<=", "b", false, false },
            { "==", "a", "in", "a,b", false, true },
            { "==", "a", "in", "b,c", false, false },
            { "==", "a", "not in", "a,b", false, false },
            { "==", "a", "not in", "b,c", false, true },
            { "!=", "a", "!=", "b", false, false },

            { "!=", "a", ">", " ", false, false },
            { "!=", "a", ">", "b", false, false },
            { "!=", "a", ">=", " ", false, false },
            { "!=", "a", ">=", "a", false, false },
            { "!=", "a", ">=", "b", false, false },
            { "!=", "a", "<", "b", false, false },
            { "!=", "a", "<", " ", false, false },
            { "!=", "a", "<=", "b", false, false },
            { "!=", "a", "<=", "a", false, false },
            { "!=", "a", "<=", " ", false, false },

            // This is tricky since, != a conflicts with ==a, but subsumes ==b
            // At this point we need to ignore this, since we do not support "or"
            { "!=", "a", "in", "a,b", false, false },

            { "!=", "a", "in", "b,c", true, false },

            { "!=", "a", "not in", "a,b", false, true },
            { "!=", "a", "not in", "b,c", false, false },

            { ">", "a", ">", "b", false, false },
            { ">", "a", ">=", "a", false, true },
            { ">", "a", ">=", "b", false, false },
            { ">", "a", "<", " ", false, false },
            { ">", "a", "<", "c", false, false },
            { ">", "a", "<=", " ", false, false },
            { ">", "a", "<=", "b", false, false },
            { ">", "a", "in", "a,b", false, false },
            { ">", "a", "in", "b,c", false, false },
            { ">", "a", "not in", "a,b", false, false },
            { ">", "a", "not in", "0,1", false, false },

            { ">=", "a", ">=", "b", false, false },
            { ">=", "a", "<", " ", false, false },
            { ">=", "a", "<", "b", false, false },
            { ">=", "a", "<=", " ", false, false },
            { ">=", "a", "<=", "c", false, false },
            { ">=", "a", "in", "0,b", false, false },
            { ">=", "a", "in", "b,c", false, false },
            { ">=", "a", "not in", "a,b", false, false },
            { ">=", "a", "not in", "0,1", false, false },

            { "<", "a", "<", "b", false, false },
            { "<", "a", "<=", " ", false, false },
            { "<", "a", "<=", "c", false, false },
            { "<", "a", "in", "a,b", false, false },
            { "<", "a", "in", "0,1", false, false },
            { "<", "a", "not in", "a,b", false, false },
            { "<", "a", "not in", "0,1", false, false },

            { "<=", "a", "<=", "b", false, false },
            { "<=", "a", "in", "a,b", false, false },
            { "<=", "a", "in", "0,1", false, false },
            { "<=", "a", "not in", "b,c", false, false },
            { "<=", "a", "not in", "0,1", false, false },

            { "in", "a", "in", "a,b", false, true },
            { "in", "b,a", "in", "a,b", true, true },
            { "in", "a", "in", "0,1", false, false },
            { "in", "a", "not in", "a,b", false, false },
            { "in", "a", "not in", "b,c", false, true },

            { "not in", "b,a", "not in", "a,b", true, true },
            { "not in", "a", "not in", "a,b", false, true },
            { "not in", "a", "not in", "b,c", false, false },
        } );
    }

    private String getAssertDescription( StringConditionInspector a,
                                         StringConditionInspector b,
                                         boolean conflictExpected ) {
        return format( "Expected condition '%s' %sto subsume condition '%s':",
                       a.toHumanReadableString(),
                       conflictExpected ? "" : "not ",
                       b.toHumanReadableString() );
    }

    private StringConditionInspector getCondition( String value,
                                                   String operator ) {
        return new StringConditionInspector( mock( Pattern52.class ), "name", value, operator );
    }
}