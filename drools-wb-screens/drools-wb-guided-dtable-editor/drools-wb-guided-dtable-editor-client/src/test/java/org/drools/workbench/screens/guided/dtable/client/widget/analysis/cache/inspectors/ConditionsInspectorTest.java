/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.inspectors;

import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.inspectors.condition.ComparableConditionInspector;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.inspectors.condition.ConditionInspector;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.inspectors.condition.ConditionsInspectorMultiMap;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.Column;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.Field;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.FieldCondition;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.ObjectField;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.keys.Values;
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
        field = new Field( new ObjectField( "Person",
                                            "Integer",
                                            "age" ),
                           "Person",
                           "Integer",
                           "age" );
        pattern = mock( Pattern52.class );

    }

    @Test
    public void testSubsume001() throws Exception {
        final ConditionsInspectorMultiMap a = getConditions( new ComparableConditionInspector<Integer>( new FieldCondition( field, mock( Column.class ), "==", new Values<>( 1 ) ) ) );
        final ConditionsInspectorMultiMap b = getConditions( new ComparableConditionInspector<Integer>( new FieldCondition( field, mock( Column.class ), "==", new Values<>( 1 ) ) ) );

        assertTrue( a.subsumes( b ) );
        assertTrue( b.subsumes( a ) );
    }

    @Test
    public void testSubsume002() throws Exception {
        final ConditionsInspectorMultiMap a = getConditions( new ComparableConditionInspector<Integer>( new FieldCondition( field, mock( Column.class ), "==", new Values<>( 1 ) ) ) );

        final ConditionsInspectorMultiMap b = getConditions( new ComparableConditionInspector<Integer>( new FieldCondition( field, mock( Column.class ), "==", new Values<>( 1 ) ) ),
                                                             new ComparableConditionInspector<Integer>( new FieldCondition( new Field( mock( ObjectField.class ), "Person", "Integer", "balance" ), mock( Column.class ), "==", new Values<>( 111111111 ) ) ) );

        assertFalse( a.subsumes( b ) );
        assertTrue( b.subsumes( a ) );
    }

    @Test
    public void testSubsume003() throws Exception {
        final Field nameField = new Field( new ObjectField( "Person",
                                                            "String",
                                                            "name" ),
                                           "Person",
                                           "String",
                                           "name" );
        final Field lastNameField = new Field( new ObjectField( "Person",
                                                                "String",
                                                                "lastName" ),
                                               "Person",
                                               "String",
                                               "lastName" );
        final ConditionsInspectorMultiMap a = getConditions( new ComparableConditionInspector<String>( new FieldCondition( nameField,
                                                                                                                           mock( Column.class ),
                                                                                                                           "==",
                                                                                                                           new Values<>( "Toni" ) ) ) );

        final ConditionsInspectorMultiMap b = getConditions( new ComparableConditionInspector<Integer>( new FieldCondition( field,
                                                                                                                            mock( Column.class ),
                                                                                                                            "==",
                                                                                                                            new Values<>( 12 ) ) ),
                                                             new ComparableConditionInspector<String>( new FieldCondition( nameField,
                                                                                                                           mock( Column.class ),
                                                                                                                           "==",
                                                                                                                           new Values<>( "Toni" ) ) ),
                                                             new ComparableConditionInspector<String>( new FieldCondition( lastNameField,
                                                                                                                           mock( Column.class ),
                                                                                                                           "==",
                                                                                                                           new Values<>( "Rikkola" ) ) ) );

        assertFalse( a.subsumes( b ) );
        assertTrue( b.subsumes( a ) );
    }

    private ConditionsInspectorMultiMap getConditions( final ConditionInspector... numericIntegerConditions ) {
        final ConditionsInspectorMultiMap conditionsInspector = new ConditionsInspectorMultiMap(  );
        for ( final ConditionInspector inspector : numericIntegerConditions ) {
            conditionsInspector.put( (( ComparableConditionInspector ) inspector).getField().getObjectField(), inspector );
        }
        return conditionsInspector;
    }

}