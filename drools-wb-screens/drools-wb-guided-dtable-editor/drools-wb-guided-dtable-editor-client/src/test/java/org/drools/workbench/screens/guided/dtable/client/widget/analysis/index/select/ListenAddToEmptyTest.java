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
package org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.select;

import java.util.Collection;

import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.ChangeHandledMultiMap;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.keys.Value;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.matchers.ExactMatcher;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ListenAddToEmptyTest {

    private Listen<Person>                listen;
    private ChangeHandledMultiMap<Person> map;

    private Collection<Person> all;
    private Person             first;
    private Person             last;

    @Before
    public void setUp() throws Exception {
        map = new ChangeHandledMultiMap<>();
        listen = new Listen<>( map,
                               new ExactMatcher( "ID",
                                                 "notInTheList",
                                                 true ) );

        listen.all( new AllListener<Person>() {
            @Override
            public void onAllChanged( final Collection<Person> all ) {
                ListenAddToEmptyTest.this.all = all;
            }
        } );

        listen.first( new FirstListener<Person>() {
            @Override
            public void onFirstChanged( final Person first ) {
                ListenAddToEmptyTest.this.first = first;
            }
        } );

        listen.last( new LastListener<Person>() {
            @Override
            public void onLastChanged( final Person last ) {
                ListenAddToEmptyTest.this.last = last;
            }
        } );
    }

    @Test
    public void testEmpty() throws Exception {
        assertNull( all );
        assertNull( first );
        assertNull( last );
    }

    @Test
    public void testBeginning() throws Exception {
        final Person baby = new Person( 0,
                                        "baby" );
        map.put( new Value( 0 ),
                 baby );

        assertEquals( baby, first );
        assertEquals( baby, last );
        assertEquals( 1, all.size() );
    }

    @Test
    public void testEnd() throws Exception {
        final Person grandpa = new Person( 100,
                                           "grandpa" );
        map.put( new Value( 100 ),
                 grandpa );

        assertEquals( grandpa,
                      first );
        assertEquals( grandpa,
                      last );
        assertEquals( 1, all.size() );
    }

    class Person {
        int    age;
        String name;

        public Person( final int age,
                       final String name ) {
            this.age = age;
            this.name = name;
        }
    }
}