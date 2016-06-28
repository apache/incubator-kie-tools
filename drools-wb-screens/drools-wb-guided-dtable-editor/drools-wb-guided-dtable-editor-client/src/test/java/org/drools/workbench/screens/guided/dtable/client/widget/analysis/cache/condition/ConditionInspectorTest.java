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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.Column;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.Field;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.FieldCondition;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.ObjectField;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.keys.Values;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import static java.lang.String.format;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(Parameterized.class)
public class ConditionInspectorTest {

    private static final List<String> ALL_VALUE_LIST = Arrays.asList( "value", "val01", "val02" );

    private final ConditionInspector a;
    private final ConditionInspector b;
    private final boolean inspectorsEqual;

    @Test
    public void testEquals() {
        assertEquals( getDescription(), inspectorsEqual, a.equals( b ) );
        assertEquals( getDescription(), inspectorsEqual, b.equals( a ) );
    }

    public ConditionInspectorTest( final ConditionInspector a,
                                   final ConditionInspector b,
                                   final boolean inspectorsEqual ) {
        this.a = a;
        this.b = b;
        this.inspectorsEqual = inspectorsEqual;
    }

    @Parameters
    public static Collection<Object[]> getData() {
        return Arrays.asList( new Object[][]{
                { getStringCondition( "strField", "value", "==" ), getStringCondition( "strField", "value", "==" ), true },
                { getStringCondition( "strField", "value", "==" ), getStringCondition( "strField", "value", "!=" ), false },
                { getStringCondition( "strField", "val01", "==" ), getStringCondition( "strField", "val02", "==" ), false },
                { getStringCondition( "strFld01", "value", "==" ), getStringCondition( "strFld02", "value", "==" ), false },

                { getBooleanCondition( "boolField", true, "==" ), getBooleanCondition( "boolField", true, "==" ), true },
                { getBooleanCondition( "boolField", true, "==" ), getBooleanCondition( "boolField", true, "!=" ), false },
                { getBooleanCondition( "boolField", true, "==" ), getBooleanCondition( "boolField", false, "==" ), false },
                { getBooleanCondition( "boolFld01", true, "==" ), getBooleanCondition( "boolFld02", true, "==" ), false },

                { getComparableCondition( "comparable", 0, "==" ), getComparableCondition( "comparable", 0, "==" ), true },
                { getComparableCondition( "comparable", 0, "==" ), getComparableCondition( "comparable", 0, "!=" ), false },
                { getComparableCondition( "comparable", 0, "==" ), getComparableCondition( "comparable", 1, "==" ), false },
                { getComparableCondition( "comparab01", 0, "==" ), getComparableCondition( "comparab02", 0, "==" ), false },


                { getNumericIntegerCondition( "comparable", 0, "==" ), getNumericIntegerCondition( "comparable", 0, "==" ), true },
                { getNumericIntegerCondition( "comparable", 0, "==" ), getNumericIntegerCondition( "comparable", 0, "!=" ), false },
                { getNumericIntegerCondition( "comparable", 0, "==" ), getNumericIntegerCondition( "comparable", 1, "==" ), false },
                { getNumericIntegerCondition( "comparab01", 0, "==" ), getNumericIntegerCondition( "comparab02", 0, "==" ), false },

                { getStringCondition( "strField", "value", "==" ), getBooleanCondition( "boolField", true, "==" ), false },
                { getStringCondition( "strField", "value", "==" ), getComparableCondition( "comparable", 0, "==" ), false },
                { getStringCondition( "strField", "value", "==" ), getNumericIntegerCondition( "comparable", 0, "==" ), false },
                { getBooleanCondition( "boolField", true, "==" ), getComparableCondition( "comparable", 0, "==" ), false },
                { getBooleanCondition( "boolField", true, "==" ), getNumericIntegerCondition( "comparable", 0, "==" ), false },
                { getComparableCondition( "comparable", 0, "==" ), getNumericIntegerCondition( "comparable", 0, "==" ), false },
        } );
    }

    public String getDescription() {
        return format( "Expected '%s' %sto be equal to '%s'.",
                       a.toHumanReadableString(),
                       inspectorsEqual ? "" : "not ",
                       b.toHumanReadableString() );
    }

    private static StringConditionInspector getStringCondition( final String field,
                                                                final String value,
                                                                final String operator ) {
        return new StringConditionInspector( new FieldCondition<>( new Field( mock( ObjectField.class ), "Test", value.getClass().toString(), field ),
                                                                   mock( Column.class ),
                                                                   operator,
                                                                   new Values<>( value ) ) );
    }

    private static BooleanConditionInspector getBooleanCondition( final String field,
                                                                  final Boolean value,
                                                                  final String operator ) {
        return new BooleanConditionInspector( new FieldCondition<>( new Field( mock( ObjectField.class ), "Test", value.getClass().toString(), field ),
                                                                    mock( Column.class ),
                                                                    operator,
                                                                    new Values<>( value ) ) );
    }

    private static ComparableConditionInspector getComparableCondition( final String field,
                                                                        final Comparable value,
                                                                        final String operator ) {
        return new ComparableConditionInspector( new FieldCondition<>( new Field( mock( ObjectField.class ), "Test", value.getClass().toString(), field ),
                                                                       mock( Column.class ),
                                                                       operator,
                                                                       new Values<>( value ) ) );
    }

    private static NumericIntegerConditionInspector getNumericIntegerCondition( final String field,
                                                                                final Integer value,
                                                                                final String operator ) {
        return new NumericIntegerConditionInspector( new FieldCondition<>( new Field( mock( ObjectField.class ), "Test", value.getClass().toString(), field ),
                                                                           mock( Column.class ),
                                                                           operator,
                                                                           new Values<>( value ) ) );
    }
}