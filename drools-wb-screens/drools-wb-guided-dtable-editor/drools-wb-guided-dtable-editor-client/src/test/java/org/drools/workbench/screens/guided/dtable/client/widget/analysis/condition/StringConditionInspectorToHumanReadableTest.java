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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.drools.workbench.models.datamodel.oracle.OperatorsOracle;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import static java.lang.String.format;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith( Parameterized.class )
public class StringConditionInspectorToHumanReadableTest {

    private static final String FIELD_NAME = "name";
    private static final String VALUE = "someValue";

    private final String operator;

    @Test
    public void testToHumanReadableString() {
        StringConditionInspector inspector = new StringConditionInspector( mock( Pattern52.class ), FIELD_NAME, VALUE, operator );

        if ( operator.equals( "!= null" ) ) {
            assertEquals( format( "%s %s", FIELD_NAME, operator ), inspector.toHumanReadableString() );
        } else if ( operator.equals( "== null" ) ) {
            assertEquals( format( "%s %s", FIELD_NAME, operator ), inspector.toHumanReadableString() );
        } else {
            assertEquals( format( "%s %s %s", FIELD_NAME, operator, VALUE ), inspector.toHumanReadableString() );
        }
    }

    public StringConditionInspectorToHumanReadableTest( String operator ) {
        this.operator = operator;
    }

    @Parameters
    public static Collection<Object[]> testData() {
        // not sure if '== null' and '!= null' from OperatorsOracle.STRING_OPERATORS make much sense here
        ArrayList<Object> data = new ArrayList<Object>( Arrays.asList( OperatorsOracle.STRING_OPERATORS ) );
        data.addAll( Arrays.asList( OperatorsOracle.EXPLICIT_LIST_OPERATORS ) );
        Collection<Object[]> data2 = new ArrayList<Object[]>();
        for ( Object operator : data ) {
            data2.add( new Object[]{ operator } );
        }
        return data2;
    }
}