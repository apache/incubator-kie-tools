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

package org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache;

import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.condition.ComparableConditionInspector;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.condition.ConditionInspector;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.Field;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith( MockitoJUnitRunner.class )
public class ConditionsInspectorTest {

    private Pattern52 pattern;

    Field field;

    @Before
    public void setUp() throws Exception {
        field = new Field( "Person", "Integer", "age" );
        pattern = mock( Pattern52.class );

    }

    @Test
    public void testSubsume001() throws Exception {
        ConditionsInspector a = getConditions( new ComparableConditionInspector<Integer>( field, 1, "==" ) );
        ConditionsInspector b = getConditions( new ComparableConditionInspector<Integer>( field, 1, "==" ) );

        assertTrue( a.subsumes( b ) );
        assertTrue( b.subsumes( a ) );
    }

    @Test
    public void testSubsume002() throws Exception {
        ConditionsInspector a = getConditions( new ComparableConditionInspector<Integer>( field, 1, "==" ) );

        ConditionsInspector b = getConditions( new ComparableConditionInspector<Integer>( field, 1, "==" ),
                                               new ComparableConditionInspector<Integer>( new Field( "Person", "Integer", "balance" ), 111111111, "==" ) );

        assertFalse( a.subsumes( b ) );
        assertTrue( b.subsumes( a ) );
    }

    @Test
    public void testSubsume003() throws Exception {
        Field nameField = new Field( "Person", "String", "name" );
        Field lastNameField = new Field( "Person", "String", "lastName" );
        ConditionsInspector a = getConditions( new ComparableConditionInspector<String>( nameField, "Toni", "==" ) );

        ConditionsInspector b = getConditions( new ComparableConditionInspector<Integer>( field, 12, "==" ),
                                               new ComparableConditionInspector<String>( nameField, "Toni", "==" ),
                                               new ComparableConditionInspector<String>( lastNameField, "Rikkola", "==" ) );

        assertFalse( a.subsumes( b ) );
        assertTrue( b.subsumes( a ) );
    }

    private ConditionsInspector getConditions( ConditionInspector... numericIntegerConditions ) {
        ConditionsInspector conditionsInspector = new ConditionsInspector();
        for ( ConditionInspector inspector : numericIntegerConditions ) {
            conditionsInspector.put( inspector.getField(), inspector );
        }
        return conditionsInspector;
    }

}