/*
 * Copyright 2015 JBoss by Red Hat.
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
public class NumericIntegerConditionInspectorRedundancyTest {

    private final Integer value1;
    private final Integer value2;
    private final String operator1;
    private final String operator2;
    private final boolean redundancyExpected;

    @Test
    public void parametrizedTest() {
        NumericIntegerConditionInspector a = getCondition( value1, operator1 );
        NumericIntegerConditionInspector b = getCondition( value2, operator2 );

        assertEquals( getAssertDescription(a, b, redundancyExpected), redundancyExpected, a.isRedundant( b ) );
        assertEquals( getAssertDescription(b, a, redundancyExpected), redundancyExpected, b.isRedundant( a ) );
    }

    public NumericIntegerConditionInspectorRedundancyTest( String operator1,
                                                           Integer value1,
                                                           String operator2,
                                                           Integer value2,
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
            { "==", 0, "==", 0, true },
            { "!=", 0, "!=", 0, true },
            { ">", 0, ">", 0, true },
            { ">=", 0, ">=", 0, true },
            { "<", 0, "<", 0, true },
            { "<=", 0, "<=", 0, true },

            { "==", 0, "==", 1, false },
            { "!=", 0, "!=", 1, false },
            { ">", 0, ">", 1, false },
            { ">=", 0, ">=", 1, false },
            { "<", 0, "<", 1, false },
            { "<=", 0, "<=", 1, false },

            { "==", 0, "!=", 0, false },
            { "==", 0, ">", 0, false },
            { "==", 0, ">=", 0, false },
            { "==", 0, "<", 0, false },
            { "==", 0, "<=", 0, false },

            { "!=", 0, ">", 0, false },
            { "!=", 0, ">=", 0, false },
            { "!=", 0, "<", 0, false },
            { "!=", 0, "<=", 0, false },

            { ">", 0, ">=", 0, false },
            { ">", 0, "<", 0, false },
            { ">", 0, "<=", 0, false },

            { ">=", 0, "<", 0, false },
            { ">=", 0, "<=", 0, false },

            { "<", 0, "<=", 0, false },

            { "==", 0, "!=", 1, false },
            { "==", 0, ">", 1, false },
            { "==", 0, ">=", 1, false },
            { "==", 0, "<", 1, false },
            { "==", 0, "<=", 1, false },

            { "!=", 0, ">", 1, false },
            { "!=", 0, ">=", 1, false },
            { "!=", 0, "<", 1, false },
            { "!=", 0, "<=", 1, false },

            { ">", 0, ">=", 1, true },
            { ">", 0, "<", 1, false },
            { ">", 0, "<=", 1, false },

            { ">=", 0, "<", 1, false },
            { ">=", 0, "<=", 1, false },

            { "<", 0, "<=", 1, false },

            // integer specific
            { ">", 0, ">=", 1, true },
            { "<", 1, "<=", 0, true },
        } );
    }

    private String getAssertDescription( NumericIntegerConditionInspector a,
                                         NumericIntegerConditionInspector b,
                                         boolean conflictExpected ) {
        return format( "Expected conditions '%s' and '%s' %sto be redundant:",
                       a.toHumanReadableString(),
                       b.toHumanReadableString(),
                       conflictExpected ? "" : "not " );
    }

    private NumericIntegerConditionInspector getCondition( int value,
                                                           String operator ) {
        return new NumericIntegerConditionInspector( mock( Pattern52.class ), "age", value, operator );
    }
}