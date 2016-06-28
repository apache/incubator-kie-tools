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
package org.drools.workbench.screens.guided.dtable.client.widget.analysis.index;

import java.util.ArrayList;
import java.util.List;

import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.HasIndex;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.keys.Values;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class QueryTest {

    private Index index;

    private Rule rule0;
    private Rule rule1;

    private Pattern rule0PersonPattern;
    private Pattern rule1PersonPattern;
    private Pattern address;
    private Pattern order;

    private Condition personNameEqualsToni;
    private Condition personAgeIs50;
    private Condition personAgeIsNot50;

    private Action orderAccepted;

    private Query query;
    private Field rule0PersonAgeField;
    private Field rule1PersonAgeField;
    private Field orderAcceptedField;

    @Before
    public void setUp() throws Exception {
        index = new Index();

        final ObjectType personObjectType = new ObjectType( "Person" );
        final ObjectType addressObjectType = new ObjectType( "Address" );
        final ObjectType orderObjectType = new ObjectType( "Order" );

        index.rules = new Rules( getRules() );


        rule0PersonAgeField = new Field( mock(ObjectField.class),
                                         "Person",
                                         "Integer",
                                         "age" );
        final Field rule0PersonNameField = new Field( mock(ObjectField.class),
                                                      "Person",
                                                      "String",
                                                      "name" );
        rule1PersonAgeField = new Field( mock(ObjectField.class),
                                         "Person",
                                         "Integer",
                                         "age" );

        personNameEqualsToni = new FieldCondition( rule0PersonNameField, mock( Column.class ), "==", new Values( "Toni" ) );
        personAgeIs50 = new FieldCondition( rule0PersonAgeField, mock( Column.class ), "==", new Values( 50 ) );
        rule0PersonAgeField.getConditions().add( personAgeIs50 );
        personAgeIsNot50 = new FieldCondition( rule1PersonAgeField, mock( Column.class ), "!=", new Values( 50 ) );
        rule1PersonAgeField.getConditions().add( personAgeIsNot50 );

        rule0PersonPattern = new Pattern( "",
                                          personObjectType );
        rule0PersonPattern.getFields().add( rule0PersonAgeField );
        address = new Pattern( "",
                               addressObjectType );
        order = new Pattern( "",
                             orderObjectType );
        rule0.getPatterns().add( rule0PersonPattern,
                                 address,
                                 order );

        rule1PersonPattern = new Pattern( "",
                                          personObjectType );
        rule1PersonPattern.getFields().add( rule1PersonAgeField );
        rule1.getPatterns().add( rule1PersonPattern );

        final ArrayList<Pattern> patterns = new ArrayList<>();
        patterns.add( rule0PersonPattern );
        patterns.add( rule1PersonPattern );
        patterns.add( address );
        patterns.add( order );

        index.patterns = new Patterns( patterns );


        final ArrayList<Condition> conditions = new ArrayList<>();
        conditions.add( personNameEqualsToni );
        conditions.add( personAgeIs50 );
        conditions.add( personAgeIsNot50 );
        index.conditions = new Conditions( conditions );

        orderAcceptedField = new Field( mock(ObjectField.class),
                                        "Order",
                                        "Boolean",
                                        "accepted" );
        order.getFields().add( orderAcceptedField );

        orderAccepted = new FieldAction( orderAcceptedField,
                                         mock( Column.class ),
                                         DataType.DataTypes.BOOLEAN,
                                         new Values( "true" ) );
        final ArrayList<Action> actions = new ArrayList<>();
        actions.add( orderAccepted );
        index.actions = new Actions( actions );

        query = new Query( index );
    }

    private ArrayList<Rule> getRules() {
        final ArrayList<Rule> rules = new ArrayList<>();
        rule0 = new Rule( 0 );
        rule1 = new Rule( 1 );
        rules.add( rule0 );
        rules.add( rule1 );
        return rules;
    }

    @Test
    public void testQueryRule() throws Exception {

        assertEquals( rule0,
                      query.from().rules()
                           .where( HasIndex.index().is( 0 ) )
                           .select().first() );
    }

    @Test
    public void testQueryRules() throws Exception {

        assertEquals( 2,
                      query.from().rules()
                           .where( Rule.uuid().any() )
                           .select().all().size() );
    }

    @Test
    public void testQueryPattern() throws Exception {

        assertEquals( rule0PersonPattern,
                      query.from().patterns()
                           .where( Pattern.name().is( "Person" ) )
                           .select().first() );
        assertEquals( address,
                      query.from().patterns()
                           .where( Pattern.name().is( "Address" ) )
                           .select().first() );
        assertEquals( order,
                      query.from().patterns()
                           .where( Pattern.name().is( "Order" ) )
                           .select().first() );
    }

    @Test
    public void testQueryPatterns() throws Exception {

        assertEquals( 2,
                      query.from().patterns()
                           .where( Pattern.name().is( "Person" ) )
                           .select().all().size() );
    }

    @Test
    public void testQueryPatternAmountInAllRules() throws Exception {

        assertEquals( 4,
                      query.from()
                           .rules()
                           .where( Rule.uuid().any() )
                           .select().patterns()
                           .where( Pattern.uuid().any() )
                           .select().all().size() );
    }

    @Test
    public void testQueryAllPatterns() throws Exception {

        assertEquals( 4,
                      query.from().patterns()
                           .where( Pattern.uuid().any() )
                           .select().all().size() );
    }

    @Test
    public void testQueryAllPatternsInRule() throws Exception {

        assertEquals( 3,
                      query.from().rules()
                           .where( HasIndex.index().is( 0 ) )
                           .select().patterns()
                           .where( Pattern.uuid().any() )
                           .select().all().size() );
    }

    @Test
    public void testQueryCondition() throws Exception {

        assertEquals( personNameEqualsToni,
                      query.from().conditions()
                           .where( Condition.value().is( "Toni" ) )
                           .select().first() );
    }

    @Test
    public void testQueryConditionAgeLess0() throws Exception {

        assertEquals( rule0PersonPattern,
                      query.from().patterns()
                           .where( Pattern.name().is( "Person" ) )
                           .select().first() );
    }

    @Test
    public void testQueryConditionAgeLess00() throws Exception {

        assertEquals( rule1PersonPattern,
                      query.from().patterns()
                           .where( Pattern.name().is( "Person" ) )
                           .select().last() );
    }

    @Test
    public void testQueryConditionAgeLess1() throws Exception {

        assertEquals( 2,
                      query.from().patterns()
                           .where( Pattern.name().is( "Person" ) )
                           .select().fields()
                           .where( Field.name().is( "age" ) )
                           .select().all().size() );
    }

    @Test
    public void testQueryConditionAgeLess() throws Exception {

        assertEquals( personAgeIs50,
                      query.from().patterns()
                           .where( Pattern.name().is( "Person" ) )
                           .select().fields()
                           .where( Field.name().is( "age" ) )
                           .select().conditions()
                           .where( Condition.value().lessThan( 100 ) )
                           .select().first() );
    }

    @Test
    public void testNonExistingField01() throws Exception {

        assertNull( query.from().patterns()
                         .where( Pattern.name().is( "Person" ) )
                         .select().fields()
                         .where( Field.name().is( "doesNotExist" ) )
                         .select().first() );
    }

    @Test
    public void testNonExistingField02() throws Exception {

        assertNull( query.from().patterns()
                         .where( Pattern.name().is( "Person" ) )
                         .select().fields()
                         .where( Field.name().is( "doesNotExist" ) )
                         .select().conditions()
                         .where( Condition.value().lessThan( 100 ) )
                         .select().first() );
    }

    @Test
    public void testQueryByField() throws Exception {

        assertEquals( personAgeIs50,
                      query.from().conditions()
                           .where( FieldCondition.field().factType( "Person" ).fieldName( "age" ) )
                           .select().first() );
    }

    @Test
    public void testQueryConditionAgeGreater() throws Exception {

        assertEquals( personAgeIs50,
                      query.from().conditions()
                           .where( Condition.value().greaterThan( 0 ) )
                           .select().first() );
    }

    @Test
    public void testQueryConditionOperator() throws Exception {

        assertEquals( personAgeIsNot50,
                      query.from().conditions()
                           .where( FieldCondition.operator().is( "!=" ) )
                           .select().first() );
    }

    @Test
    public void testQueryAction() throws Exception {

        assertEquals( orderAccepted,
                      query.from().actions()
                           .where( Action.value().is( "true" ) )
                           .select().first() );
    }

    @Test
    public void testQueryMapByField() throws Exception {

        final MapBy<Field, Action> map = query.from().actions()
                                              .<Field>mapBy( FieldAction.field() );

        assertTrue( map.containsKey( orderAcceptedField ) );
        final List<Action> actions = map.get( orderAcceptedField );
        assertEquals( 1, actions.size() );
        assertTrue( actions.contains( orderAccepted ) );
    }
}