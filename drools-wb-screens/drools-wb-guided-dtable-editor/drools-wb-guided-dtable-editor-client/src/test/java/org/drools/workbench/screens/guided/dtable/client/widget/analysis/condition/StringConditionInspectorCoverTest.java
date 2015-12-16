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

package org.drools.workbench.screens.guided.dtable.client.widget.analysis.condition;

import java.util.Arrays;
import java.util.Collection;

import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util.Operator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import static java.lang.String.format;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(Parameterized.class)
public class StringConditionInspectorCoverTest {

    private final String value1;
    private final String value2;
    private final String operator;
    private final boolean covers;

    @Test
    public void parametrizedTest() {
        StringConditionInspector a = getCondition( value1, operator );

        assertEquals( getAssertDescription( a,
                                            covers,
                                            value2 ),
                      covers,
                      a.covers( value2 ) );
    }

    public StringConditionInspectorCoverTest( String value1,
                                              String operator,
                                              String value2,
                                              boolean covers ) {
        this.operator = operator;
        this.value1 = value1;
        this.value2 = value2;
        this.covers = covers;
    }

    @Parameters
    public static Collection<Object[]> testData() {
        return Arrays.asList( new Object[][]{
                {"toni", Operator.EQUALS.toString(), "toni", true},
                {"toni", Operator.MATCHES.toString(), "toni", true},
                {"toni", Operator.SOUNDSLIKE.toString(), "toni", true},
                {"toni,eder", Operator.IN.toString(), "toni", true},
                {"toni", Operator.GREATER_OR_EQUAL.toString(), "toni", true},
                {"toni", Operator.LESS_OR_EQUAL.toString(), "toni", true},

                {"toni", Operator.LESS_THAN.toString(), "toni", false},
                {"toni", Operator.GREATER_THAN.toString(), "toni", false},

                {"toni", Operator.EQUALS.toString(), "michael", false},
                {"toni", Operator.MATCHES.toString(), "michael", false},
                {"toni", Operator.SOUNDSLIKE.toString(), "michael", false},
                {"toni,eder", Operator.IN.toString(), "michael", false},
                {"toni", Operator.GREATER_OR_EQUAL.toString(), "michael", false},
                {"toni", Operator.LESS_OR_EQUAL.toString(), "michael", false},

                {"toni,eder", Operator.NOT_IN.toString(), "michael", true},
                {"toni,eder", Operator.NOT_IN.toString(), "eder", false},

                {"toni", Operator.NOT_EQUALS.toString(), "toni", false},
                {"toni", Operator.NOT_EQUALS.toString(), "eder", true},

                {"toni", Operator.NOT_MATCHES.toString(), "toni", false},
                {"toni", Operator.NOT_MATCHES.toString(), "eder", true},

                {"toni rikkola", Operator.STR_ENDS_WITH.toString(), "rikkola", true},
                {"toni rikkola", Operator.STR_ENDS_WITH.toString(), "toni", false},
                {"toni rikkola", Operator.STR_STARTS_WITH.toString(), "toni", true},
                {"toni rikkola", Operator.STR_STARTS_WITH.toString(), "rikkola", false},

                // No matter what we do this returns false
                {"array", Operator.CONTAINS.toString(), "toni,eder", false},
                {"array", Operator.CONTAINS.toString(), "toni", false},
                {"array", Operator.CONTAINS.toString(), "eder", false},
                {"array", Operator.NOT_CONTAINS.toString(), "toni,eder", false},
                {"array", Operator.NOT_CONTAINS.toString(), "toni", false},
                {"array", Operator.NOT_CONTAINS.toString(), "eder", false},

        } );
    }

    private StringConditionInspector getCondition( String value,
                                                   String operator ) {
        return new StringConditionInspector( mock( Pattern52.class ), "name", value, operator );
    }

    private String getAssertDescription( StringConditionInspector a,
                                         boolean covers,
                                         String condition ) {
        return format( "Expected condition '%s' to %s cover '%s':",
                       a.toHumanReadableString(),
                       covers ? "" : "not ",
                       condition );
    }
}