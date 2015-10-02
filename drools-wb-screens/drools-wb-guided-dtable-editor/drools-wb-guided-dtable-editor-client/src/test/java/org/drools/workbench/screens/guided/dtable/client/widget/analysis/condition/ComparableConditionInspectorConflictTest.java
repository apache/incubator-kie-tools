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

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import static java.lang.String.format;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith( Parameterized.class )
public class ComparableConditionInspectorConflictTest {

    private final Comparable value1;
    private final Comparable value2;
    private final String operator1;
    private final String operator2;
    private final boolean conflictExpected;

    @Test
    public void parametrizedTest() {
        ComparableConditionInspector a = getCondition( value1, operator1 );
        ComparableConditionInspector b = getCondition( value2, operator2 );

        assertEquals( getAssertDescriptionConflict( a, b, conflictExpected ), conflictExpected, a.conflicts( b ) );
        assertEquals( getAssertDescriptionConflict( a, b, conflictExpected ), conflictExpected, a.conflicts( b ) );
        assertEquals( getAssertDescriptionOverlap( a, b, !conflictExpected ), !conflictExpected, a.overlaps( b ) );
        assertEquals( getAssertDescriptionOverlap( b, a, !conflictExpected ), !conflictExpected, b.overlaps( a ) );
    }

    public ComparableConditionInspectorConflictTest( String operator1,
                                                     Comparable value1,
                                                     String operator2,
                                                     Comparable value2,
                                                     boolean conflictExpected) {
        this.value1 = value1;
        this.value2 = value2;
        this.operator1 = operator1;
        this.operator2 = operator2;
        this.conflictExpected = conflictExpected;
    }

    private String getAssertDescriptionConflict( ComparableConditionInspector a,
                                                 ComparableConditionInspector b,
                                                 boolean conflictExpected ) {
        return format( "Expected condition '%s' %sto conflict with condition '%s':",
                       a.toHumanReadableString(),
                       conflictExpected ? "" : "not ",
                       b.toHumanReadableString() );
    }

    private String getAssertDescriptionOverlap( ComparableConditionInspector a,
                                                ComparableConditionInspector b,
                                                boolean conflictExpected ) {
        return format( "Expected condition '%s' %sto overlap with condition '%s':",
                       a.toHumanReadableString(),
                       conflictExpected ? "" : "not ",
                       b.toHumanReadableString() );
    }

    @Parameters
    public static Collection<Object[]> testData() {
        return Arrays.asList( new Object[][]{
                // op1, val1, op2, val2, conflicts
                {"==", 0.5d, "==", 0.5d, false},
                {"!=", 0.5d, "!=", 0.5d, false},
                {">", 0.5d, ">", 0.5d, false},
                {">=", 0.5d, ">=", 0.5d, false},
                {"<", 0.5d, "<", 0.5d, false},
                {"<=", 0.5d, "<=", 0.5d, false},

                {"==", 0.5d, "!=", 1.5d, false},
                {"==", 0.5d, ">", -1.5d, false},
                {"==", 0.5d, ">", -10.5d, false},
                {"==", 0.5d, ">=", 0.5d, false},
                {"==", 0.5d, ">=", -10.5d, false},
                {"==", 0.5d, "<", 1.5d, false},
                {"==", 0.5d, "<", 10.5d, false},
                {"==", 0.5d, "<=", 0.5d, false},
                {"==", 0.5d, "<=", 10.5d, false},

                {"==", 0.5d, "==", 1.5d, true},
                {"==", 0.5d, "!=", 0.5d, true},
                {"==", 0.5d, ">", 0.5d, true},
                {"==", 0.5d, ">", 10.5d, true},
                {"==", 0.5d, ">=", 1.5d, true},
                {"==", 0.5d, ">=", 10.5d, true},
                {"==", 0.5d, "<", 0.5d, true},
                {"==", 0.5d, "<", -10.5d, true},
                {"==", 0.5d, "<=", -1.5d, true},
                {"==", 0.5d, "<=", -10.5d, true},

                {"!=", 0.5d, "!=", 1.5d, false},
                {"!=", 0.5d, ">", -1.5d, false},
                {"!=", 0.5d, ">=", 0.5d, false},
                {"!=", 0.5d, "<", 1.5d, false},
                {"!=", 0.5d, "<=", 0.5d, false},

                {">", 0.5d, ">", 1.5d, false},
                {">", 0.5d, ">=", 0.5d, false},
                {">", 0.5d, "<", 2.5d, false},
                {">", 0.5d, "<", 10.5d, false},
                {">", 0.5d, "<=", 1.5d, false},
                {">", 0.5d, "<=", 10.5d, false},

                {">", 0.5d, "<", -1.5d, true},
                {">", 0.5d, "<", 0.5d, true},
                {">", 0.5d, "<", 1.5d, false},
                {">", 0.5d, "<=", -2.5d, true},
                {">", 0.5d, "<=", -1.5d, true},
                {">", 0.5d, "<=", 0.5d, true},

                {">=", 0.5d, ">=", 1.5d, false},
                {">=", 0.5d, "<", 1.5d, false},
                {">=", 0.5d, "<", 10.5d, false},
                {">=", 0.5d, "<=", 0.5d, false},
                {">=", 0.5d, "<=", 10.5d, false},

                {">=", 0.5d, "<", -2.5d, true},
                {">=", 0.5d, "<", -1.5d, true},
                {">=", 0.5d, "<", 0.5d, true},
                {">=", 0.5d, "<=", -3.5d, true},
                {">=", 0.5d, "<=", -2.5d, true},
                {">=", 0.5d, "<=", -1.5d, true},

                {"<", 0.5d, "<", 1.5d, false},
                {"<", 0.5d, "<=", 0.5d, false},

                {"<=", 0.5d, "<=", 1.5d, false},

                // operators only allowed for Date...
                {"after", new Date( 0 ), "after", new Date( 0 ), false},
                {"before", new Date( 0 ), "before", new Date( 0 ), false},

                {"after", new Date( 10000 ), "before", new Date( 20000 ), false},
                {"after", new Date( 20000 ), "before", new Date( 10000 ), true}
        } );
    }

    private ComparableConditionInspector getCondition( Comparable value,
                                                       String operator ) {
        return new ComparableConditionInspector( mock( Pattern52.class ), "age", value, operator );
    }
}