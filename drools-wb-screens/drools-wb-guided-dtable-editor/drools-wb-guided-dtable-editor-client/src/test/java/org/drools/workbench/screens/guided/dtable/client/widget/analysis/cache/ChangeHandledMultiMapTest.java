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

import java.util.ArrayList;

import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.keys.Value;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ChangeHandledMultiMapTest {

    private ChangeHandledMultiMap<String>           map;
    private MultiMapChangeHandler.ChangeSet<String> changeSet;

    private int timesCalled = 0;

    @Before
    public void setUp() throws Exception {
        this.timesCalled = 0;

        this.map = new ChangeHandledMultiMap<>();
        this.map.addChangeListener( new MultiMapChangeHandler<String>() {
            @Override
            public void onChange( final ChangeSet<String> changeSet ) {
                ChangeHandledMultiMapTest.this.changeSet = changeSet;
                timesCalled++;
            }
        } );
    }

    @Test
    public void testSize() throws Exception {
        assertNull( changeSet );
        assertEquals( 0, timesCalled );
    }

    @Test
    public void testPut() throws Exception {
        map.put( new Value( "hello" ),
                 "test" );

        assertTrue( changeSet.added.get( new Value( "hello" ) ).contains( "test" ) );

        assertEquals( 1, timesCalled );
    }

    @Test
    public void testAddAllValues() throws Exception {
        final ArrayList<String> list = new ArrayList<>();
        list.add( "a" );
        list.add( "b" );
        list.add( "c" );

        map.addAllValues( new Value( "hello" ),
                          list );

        assertEquals( 3, changeSet.added.get( new Value( "hello" ) ).size() );
        assertTrue( changeSet.added.get( new Value( "hello" ) ).contains( "a" ) );
        assertTrue( changeSet.added.get( new Value( "hello" ) ).contains( "b" ) );
        assertTrue( changeSet.added.get( new Value( "hello" ) ).contains( "c" ) );

        assertEquals( 1, timesCalled );
    }
}