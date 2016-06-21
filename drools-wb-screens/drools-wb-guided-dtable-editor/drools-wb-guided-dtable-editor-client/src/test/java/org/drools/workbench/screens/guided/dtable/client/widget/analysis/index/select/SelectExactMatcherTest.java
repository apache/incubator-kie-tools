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

import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.MultiMap;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.keys.Value;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.matchers.ExactMatcher;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class SelectExactMatcherTest {

    private Select<Item>          select;
    private MultiMap<Value, Item> map;
    private Item                  thirteen;

    @Before
    public void setUp() throws Exception {
        map = new MultiMap<>();
        thirteen = new Item( 13 );

        map.put( new Value( 0 ),
                 new Item( 0 ) );
        map.put( new Value( 13 ),
                 thirteen );
        map.put( new Value( 56 ),
                 new Item( 56 ) );
        map.put( new Value( 100 ),
                 new Item( 100 ) );
        map.put( new Value( 1200 ),
                 new Item( 1200 ) );

        select = new Select<>( map,
                               new ExactMatcher( "cost",
                                                 13 ) );
    }

    @Test
    public void testAll() throws Exception {
        final Collection<Item> all = select.all();

        assertEquals( 1, all.size() );
    }

    @Test
    public void testFirst() throws Exception {
        assertEquals( thirteen,
                      select.first() );
    }

    @Test
    public void testLast() throws Exception {
        assertEquals( thirteen,
                      select.last() );
    }

    private class Item {

        private Integer cost;

        public Item( final Integer cost ) {
            this.cost = cost;
        }
    }
}