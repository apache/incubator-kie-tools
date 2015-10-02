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
import java.util.Date;

import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import static java.lang.String.format;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(Parameterized.class)
public class DoubleComparableConditionInspectorCoverTest {

    private final Comparable conditionValue;
    private final Comparable value;
    private final String conditionOperator;
    private final boolean coverExpected;

    @Test
    public void parametrizedTest() {
        ComparableConditionInspector a = getCondition( conditionValue, conditionOperator );

        assertEquals( getAssertDescription( a, value, coverExpected ), coverExpected, a.covers( value ) );
    }

    public DoubleComparableConditionInspectorCoverTest( Comparable conditionValue,
                                                        String conditionOperator,
                                                        Comparable value,
                                                        boolean coverExpected ) {
        this.conditionValue = conditionValue;
        this.value = value;
        this.conditionOperator = conditionOperator;
        this.coverExpected = coverExpected;
    }

    @Parameters
    public static Collection<Object[]> testData() {
        return Arrays.asList( new Object[][]{
                // condition value, condition operator, value, covers
                {0.5d, "==", 0.5d, true},
                {0.5d, "<", 0.5d, false},
                {0.5d, "<", 10.5d, false},
                {10.5d, "<", 0.5d, true},
                {0.5d, ">", 0.5d, false},
                {0.5d, ">", 10.5d, true},
                {10.5d, "==", 0.5d, false},
                {0.5d, "==", 10.5d, false},
                {10.5d, ">", 0.5d, false},
                {-1.5d, ">", 0.5d, true},
                {0.5d, ">", -1.5d, false},
                {-1.5d, "==", 0.5d, false},
                {0.5d, "==", -1.5d, false},
                {new Date( 0 ), "==", new Date( 0 ), true},
                {new Date( 0 ), "==", new Date( 1 ), false},
                {new Date( 0 ), "!=", new Date( 0 ), false},
                {new Date( 0 ), "!=", new Date( 1 ), true},
                {new Date( 0 ), "after", new Date( 1 ), true},
                {new Date( 1 ), "after", new Date( 0 ), false},
                {new Date( 0 ), "before", new Date( 1 ), false},
                {new Date( 1 ), "before", new Date( 0 ), true}
        } );
    }

    private String getAssertDescription( ComparableConditionInspector a,
                                         Comparable b,
                                         boolean conflictExpected ) {
        return format( "Expected condition '%s' %sto cover value '%s':",
                       a.toHumanReadableString(),
                       conflictExpected ? "" : "not ",
                       b.toString() );
    }

    private ComparableConditionInspector getCondition( Comparable value,
                                                       String operator ) {
        return new ComparableConditionInspector( mock( Pattern52.class ), "age", value, operator );
    }
}