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
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collection;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith( Parameterized.class )
public class StringConditionInspectorRedundancyTest {

    private final String value1;
    private final String value2;
    private final String operator1;
    private final String operator2;
    private final boolean redundancyExpected;

    @Test
    public void parametrizedTest() {
        StringConditionInspector a = getCondition( value1, operator1 );
        StringConditionInspector b = getCondition( value2, operator2 );

        assertEquals( getAssertDescription( a, b, redundancyExpected ), redundancyExpected, a.isRedundant( b ) );
        assertEquals( getAssertDescription( b, a, redundancyExpected ), redundancyExpected, b.isRedundant( a ) );
    }

    public StringConditionInspectorRedundancyTest( String operator1,
                                                   String value1,
                                                   String operator2,
                                                   String value2,
                                                   boolean redundancyExpected) {
        this.value1 = value1;
        this.value2 = value2;
        this.operator1 = operator1;
        this.operator2 = operator2;
        this.redundancyExpected = redundancyExpected;
    }

    @Parameters
    public static Collection<Object[]> testData() {
        return Arrays.asList( new Object[][] {
            // op1, val1, op2, val2, redundant
            { "==", "a", "==", "a", true },
            { "!=", "a", "!=", "a", true },
            { ">", "a", ">", "a", true },
            { ">=", "a", ">=", "a", true },
            { "<", "a", "<", "a", true },
            { "<=", "a", "<=", "a", true },
            { "in", "a,b", "in", "a,b", true },
            { "not in", "a,b", "not in", "a,b", true },
            { "matches", "a", "matches", "a", true },
            { "soundslike", "a", "soundslike", "a", true },

            { "==", "a", "==", "b", false },
            { "==", "a", "!=", "a", false },
            { "==", "a", ">", "a", false },
            { "==", "a", ">=", "a", false },
            { "==", "a", "<", "a", false },
            { "==", "a", "<=", "a", false },
            { "==", "a", "in", "a,b", false },
            { "==", "a", "not in", "a,b", false },

            { "==", "a", "in", "a", true },

            { "!=", "a", "!=", "b", false },
            { "!=", "a", ">", "a", false },
            { "!=", "a", ">=", "a", false },
            { "!=", "a", "<", "a", false },
            { "!=", "a", "<=", "a", false },
            { "!=", "a", "in", "a,b", false },
            { "!=", "a", "not in", "a,b", false },

            { "!=", "a", "not in", "a", true },

            { ">", "a", ">", "b", false },
            { ">", "a", ">=", "a", false },
            { ">", "a", "<", "a", false },
            { ">", "a", "<=", "a", false },
            { ">", "a", "in", "a,b", false },
            { ">", "a", "not in", "a,b", false },

            { ">=", "a", ">=", "b", false },
            { ">=", "a", "<", "a", false },
            { ">=", "a", "<=", "a", false },
            { ">=", "a", "in", "a,b", false },
            { ">=", "a", "not in", "a,b", false },

            { "<", "a", "<", "b", false },
            { "<", "a", "<=", "a", false },
            { "<", "a", "in", "a,b", false },
            { "<", "a", "not in", "a,b", false },

            { "<=", "a", "<=", "b", false },
            { "<=", "a", "in", "a,b", false },
            { "<=", "a", "not in", "a,b", false },

            { "in", "a", "in", "b", false },
            { "in", "a", "not in", "a,b", false },

            { "in", "a,b", "in", "b,a", true },

            { "not in", "a", "not in", "b", false },

            { "not in", "a,b", "not in", "b,a", true },

            { ">", "a", ">=", "b", false },
            { "<", "b", "<=", "a", false },
        } );
    }

    private String getAssertDescription( StringConditionInspector a,
                                         StringConditionInspector b,
                                         boolean conflictExpected ) {
        return format( "Expected conditions '%s' and '%s' %sto be redundant:",
                       a.toHumanReadableString(),
                       b.toHumanReadableString(),
                       conflictExpected ? "" : "not " );
    }

    private StringConditionInspector getCondition( String value,
                                                   String operator ) {
        return new StringConditionInspector( mock( Pattern52.class ), "name", value, operator );
    }

}