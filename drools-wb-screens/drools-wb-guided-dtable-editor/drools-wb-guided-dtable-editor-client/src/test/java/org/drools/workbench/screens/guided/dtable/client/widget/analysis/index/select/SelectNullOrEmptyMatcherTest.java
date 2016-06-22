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

import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.KeyDefinition;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.MultiMap;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.keys.Value;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.matchers.NullOrEmptyMatcher;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class SelectNullOrEmptyMatcherTest {

    private MultiMap<Value, Item> map;

    private Item hello;
    private Item valueEmpty;
    private Item valueNull;

    @Before
    public void setUp() throws Exception {
        map = new MultiMap<>();
        hello = new Item( "hello" );
        valueNull = new Item( null );
        valueNull = new Item( "" );

        map.put( new Value( "hello" ),
                 hello );
        map.put( new Value( null ),
                 valueNull );
        map.put( new Value( "" ),
                 valueEmpty );

    }

    @Test
    public void testAll() throws Exception {
        final Select<Item> select = new Select<>( map,
                                                  new NullOrEmptyMatcher( KeyDefinition.newKeyDefinition().withId( "value" ).build() ) );

        final Collection<Item> all = select.all();

        assertEquals( 2, all.size() );
    }

    @Test
    public void testAllNegate() throws Exception {
        final Select<Item> select = new Select<>( map,
                                                  new NullOrEmptyMatcher( KeyDefinition.newKeyDefinition().withId( "value" ).build(),
                                                                          true ) );

        final Collection<Item> all = select.all();

        assertEquals( 1, all.size() );
    }

    @Test
    public void testFirst() throws Exception {
        final Select<Item> select = new Select<>( map,
                                                  new NullOrEmptyMatcher( KeyDefinition.newKeyDefinition().withId(  "value" ).build() ) );
        assertEquals( valueNull,
                      select.first() );
    }

    @Test
    public void testFirstNegate() throws Exception {
        final Select<Item> select = new Select<>( map,
                                                  new NullOrEmptyMatcher( KeyDefinition.newKeyDefinition().withId( "value" ).build(),
                                                                          true ) );
        assertEquals( hello,
                      select.first() );
    }

    @Test
    public void testLast() throws Exception {
        final Select<Item> select = new Select<>( map,
                                                  new NullOrEmptyMatcher( KeyDefinition.newKeyDefinition().withId( "value" ).build() ) );
        assertEquals( valueEmpty,
                      select.last() );
    }

    @Test
    public void testLastNegate() throws Exception {
        final Select<Item> select = new Select<>( map,
                                                  new NullOrEmptyMatcher( KeyDefinition.newKeyDefinition().withId( "value" ).build(),
                                                                          true ) );
        assertEquals( hello,
                      select.last() );
    }

    private class Item {

        private String value;

        public Item( final String value ) {
            this.value = value;
        }
    }
}