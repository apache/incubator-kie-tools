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
package org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache;

import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.keys.IndexKey;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.keys.Key;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.keys.UUIDKey;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.keys.UpdatableKey;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.keys.Value;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class IndexedKeyTreeMapTest {

    private IndexedKeyTreeMap<Person> map;
    private Person                      toni;
    private Person                      eder;
    private Person                      michael;

    @Before
    public void setUp() throws Exception {
        map = new IndexedKeyTreeMap<>();

        toni = new Person( "Toni", 20 );
        eder = new Person( "Eder", 20 );
        michael = new Person( "Michael", 30 );

        put( toni );
        put( eder );
        put( michael );
    }

    private void put( final Person person ) {
        map.put( person );
    }

    @Test
    public void testIndexOrder() throws Exception {
        assertEquals( toni, map.get( IndexKey.INDEX_ID ).get( new Value( 0 ) ).iterator().next() );
        assertEquals( eder, map.get( IndexKey.INDEX_ID ).get( new Value( 1 ) ).iterator().next() );
        assertEquals( michael, map.get( IndexKey.INDEX_ID ).get( new Value( 2 ) ).iterator().next() );
    }

    @Test
    public void testAddToMiddle() throws Exception {

        final Person smurf = new Person( "Smurf",
                                         55 );

        map.put( smurf,
                 1 );

        final ChangeHandledMultiMap<Person> personChangeHandledMultiMap = map.get( IndexKey.INDEX_ID );

        assertEquals( 4, map.get( IndexKey.INDEX_ID ).size() );
        assertEquals( toni, map.get( IndexKey.INDEX_ID ).get( new Value( 0 ) ).iterator().next() );
        assertEquals( smurf, map.get( IndexKey.INDEX_ID ).get( new Value( 1 ) ).iterator().next() );
        assertEquals( eder, map.get( IndexKey.INDEX_ID ).get( new Value( 2 ) ).iterator().next() );
        assertEquals( michael, map.get( IndexKey.INDEX_ID ).get( new Value( 3 ) ).iterator().next() );
    }

    @Test
    public void testRemove() throws Exception {

        // Removing one by one to check the index stays on track.

        toni.uuidKey.retract();

        assertEquals( eder, map.get( IndexKey.INDEX_ID ).get( new Value( 0 ) ).iterator().next() );
        assertEquals( michael, map.get( IndexKey.INDEX_ID ).get( new Value( 1 ) ).iterator().next() );

        eder.uuidKey.retract();

        Person next = map.get( IndexKey.INDEX_ID ).get( new Value( 0 ) ).iterator().next();
        assertEquals( michael, next );
    }

    @Test
    public void testUpdateAge() throws Exception {
        toni.setAge( 100 );

        assertEquals( 100, toni.getAge() );

        final Person person = map.get( KeyDefinition.newKeyDefinition().withId( "age" ).build() ).get( new Value( 100 ) ).iterator().next();
        assertEquals( toni, person );
        assertEquals( 100, person.getAge() );
    }

    class Person
            implements HasIndex,
                       HasKeys {

        private final UUIDKey uuidKey = new UUIDKey( this );

        private UpdatableKey<Person> indexKey;

        final String name;

        private UpdatableKey<Person> ageKey;

        public Person( final String name,
                       final int age ) {
            this.name = name;
            ageKey = new UpdatableKey<Person>( KeyDefinition.newKeyDefinition().withId( "age" ).build(),
                                               age );
        }

        public Key[] keys() {
            return new Key[]{
                    uuidKey,
                    indexKey,
                    new Key( KeyDefinition.newKeyDefinition().withId( "name" ).build(),
                             name ),
                    ageKey
            };
        }

        @Override
        public int getIndex() {
            return ( int ) indexKey.getSingleValueComparator();
        }

        @Override
        public void setIndex( final int index ) {
            UpdatableKey<Person> oldKey = indexKey;
            final UpdatableKey<Person> newKey = new UpdatableKey<>( IndexKey.INDEX_ID,
                                                                    index );
            indexKey = newKey;

            if ( oldKey != null ) {
                oldKey.update( newKey,
                               this );
            }


        }

        public int getAge() {
            return ( Integer ) ageKey.getSingleValueComparator();
        }

        public void setAge( final int age ) {

            if ( ageKey.getSingleValue().equals( age ) ) {
                return;
            } else {
                final UpdatableKey<Person> oldKey = ageKey;

                final UpdatableKey<Person> newKey = new UpdatableKey<>( KeyDefinition.newKeyDefinition().withId( "age" ).build(),
                                                                        age );
                ageKey = newKey;

                oldKey.update( newKey,
                               this );
            }
        }
    }
}