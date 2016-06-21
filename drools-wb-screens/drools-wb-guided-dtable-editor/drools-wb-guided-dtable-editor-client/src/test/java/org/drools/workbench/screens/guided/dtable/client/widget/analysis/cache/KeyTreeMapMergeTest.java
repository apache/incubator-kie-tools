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

import java.util.Collection;
import java.util.Collections;

import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.keys.Key;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.keys.UUIDKey;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.keys.Value;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.matchers.Matcher;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class KeyTreeMapMergeTest {

    private KeyTreeMap<Person> treeMap;
    private Person             pat;

    private KeyTreeMap<Person> otherKeyTreeMap;
    private Person             mat;

    @Before
    public void setUp() throws Exception {

        treeMap = new KeyTreeMap<>();
        pat = new Person( "Pat",
                          10 );
        add( treeMap,
             pat );

        otherKeyTreeMap = new KeyTreeMap<>();
        mat = new Person( "mat",
                          15 );
        add( otherKeyTreeMap,
             mat );

    }

    private void add( final KeyTreeMap<Person> treeMap,
                      final Person person ) {
        treeMap.put( person );
    }

    @Test
    public void testNames() throws Exception {
        treeMap.merge( otherKeyTreeMap );

        final Matcher name = new Matcher( "name" );
        final MultiMap<Value, Person> multiMap = treeMap.get( name.getId() );

        assertEquals( 2, multiMap.allValues().size() );
    }

    @Test
    public void testAge() throws Exception {
        treeMap.merge( otherKeyTreeMap );

        assertEquals( 2, allPersons( treeMap ).size() );
    }

    @Test
    public void testRetract() throws Exception {
        KeyTreeMap<Person> thirdKeyTreeMap = new KeyTreeMap<>();
        thirdKeyTreeMap.merge( treeMap );
        thirdKeyTreeMap.merge( otherKeyTreeMap );

        assertEquals( 2, allPersons( thirdKeyTreeMap ).size() );
        assertEquals( 1, allPersons( treeMap ).size() );
        assertEquals( 1, allPersons( otherKeyTreeMap ).size() );

        pat.uuidKey.retract();

        assertEquals( 1, allPersons( thirdKeyTreeMap ).size() );
        assertEquals( 0, allPersons( treeMap ).size() );
        assertEquals( 1, allPersons( otherKeyTreeMap ).size() );

    }

    private Collection<Person> allPersons( final KeyTreeMap<Person> personKeyTreeMap ) {
        final Matcher age = new Matcher( "age" );
        final ChangeHandledMultiMap<Person> personChangeHandledMultiMap = personKeyTreeMap.get( age.getId() );
        if ( personChangeHandledMultiMap != null ) {
            return personChangeHandledMultiMap.allValues();
        } else {
            return Collections.emptyList();
        }
    }

    private class Person
            implements HasKeys {
        private String  name;
        private Integer age;
        private UUIDKey uuidKey = new UUIDKey( this );

        public Person( final String name,
                       final Integer age ) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public Integer getAge() {
            return age;
        }

        @Override
        public Key[] keys() {
            return new Key[]{
                    uuidKey,
                    new Key( "name",
                             name ),
                    new Key( "age",
                             age )};
        }
    }
}