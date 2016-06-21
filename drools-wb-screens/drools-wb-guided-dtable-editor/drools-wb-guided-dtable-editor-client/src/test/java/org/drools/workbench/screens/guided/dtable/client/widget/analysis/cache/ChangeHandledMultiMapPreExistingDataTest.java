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

import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.keys.Value;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ChangeHandledMultiMapPreExistingDataTest {


    private ChangeHandledMultiMap<String>           map;
    private MultiMapChangeHandler.ChangeSet<String> changeSet;

    private int timesCalled = 0;

    @Before
    public void setUp() throws Exception {
        this.timesCalled = 0;

        this.map = new ChangeHandledMultiMap<>();

        this.map.put( new Value( "hello" ), "a" );
        this.map.put( new Value( "ok" ), "b" );
        this.map.put( new Value( "ok" ), "c" );

        this.map.addChangeListener( new MultiMapChangeHandler<String>() {
            @Override
            public void onChange( final ChangeSet<String> changeSet ) {
                ChangeHandledMultiMapPreExistingDataTest.this.changeSet = changeSet;
                timesCalled++;
            }
        } );
    }

    @Test
    public void testRemove() throws Exception {
        map.remove( new Value( "ok" ) );

        assertEquals( 2, changeSet.removed.get( new Value( "ok" ) ).size() );

        assertEquals( 1, timesCalled );
    }

    @Test
    public void testRemoveValue() throws Exception {
        map.removeValue( new Value( "ok" ),
                         "b" );

        assertEquals( 1, changeSet.removed.get( new Value( "ok" ) ).size() );
        assertTrue( changeSet.removed.get( new Value( "ok" ) ).contains( "b" ) );

        assertEquals( 1, timesCalled );
    }

    @Test
    public void testClear() throws Exception {
        map.clear();

        assertEquals( 1, changeSet.removed.get( new Value( "hello" ) ).size() );
        assertTrue( changeSet.removed.get( new Value( "hello" ) ).contains( "a" ) );
        assertEquals( 2, changeSet.removed.get( new Value( "ok" ) ).size() );
        assertTrue( changeSet.removed.get( new Value( "ok" ) ).contains( "b" ) );
        assertTrue( changeSet.removed.get( new Value( "ok" ) ).contains( "c" ) );

        assertEquals( 1, timesCalled );
    }

    @Test
    public void testMerge() throws Exception {
        final MultiMap<Value, String> other = new MultiMap<>();
        other.put( new Value( "hello" ), "d" );
        other.put( new Value( "ok" ), "e" );
        other.put( new Value( "newOne" ), "f" );

        map.merge( other );

        assertEquals( 1, changeSet.added.get( new Value( "hello" ) ).size() );
        assertTrue( changeSet.added.get( new Value( "hello" ) ).contains( "d" ) );
        assertEquals( 1, changeSet.added.get( new Value( "ok" ) ).size() );
        assertTrue( changeSet.added.get( new Value( "ok" ) ).contains( "e" ) );
        assertEquals( 1, changeSet.added.get( new Value( "newOne" ) ).size() );
        assertTrue( changeSet.added.get( new Value( "newOne" ) ).contains( "f" ) );

        assertEquals( 1, timesCalled );
    }
}