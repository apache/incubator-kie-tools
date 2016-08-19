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

package org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util;

import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.util.HasKeys;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.util.maps.InspectorList;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.keys.Key;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.keys.UUIDKey;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class RelationResolverSubsumptionTest {

    private RelationResolver relationResolver;
    private InspectorList    a;
    private InspectorList    b;
    private Person           firstItemInB;
    private Person           blockingItem;

    @Before
    public void setUp() throws Exception {
        a = new InspectorList();
        b = new InspectorList();

        a.add( new Person( 15 ) );

        firstItemInB = spy( new Person( 15 ) );
        b.add( firstItemInB );
        blockingItem = spy( new Person( 10 ) );
        b.add( blockingItem );

        relationResolver = new RelationResolver( a,
                                                 true );
    }

    @Test
    public void empty() throws Exception {
        relationResolver = new RelationResolver( new InspectorList() );
        assertTrue( relationResolver.subsumes( new InspectorList() ) );
    }

    @Test
    public void emptyListWithItemsSubsumesEmptyLists() throws Exception {
        assertTrue( relationResolver.subsumes( new InspectorList() ) );
    }

    @Test
    public void recheck() throws Exception {

        assertFalse( relationResolver.subsumes( b ) );

        verify( firstItemInB ).subsumes( any() );

        reset( firstItemInB );

        assertFalse( relationResolver.subsumes( b ) );

        verify( firstItemInB, never() ).subsumes( any() );
    }

    @Test
    public void recheckWithUpdate() throws Exception {

        assertFalse( relationResolver.subsumes( b ) );

        reset( firstItemInB );

        // UPDATE
        blockingItem.setAge( 15 );

        assertTrue( relationResolver.subsumes( b ) );

        verify( firstItemInB ).subsumes( any() );
    }

    @Test
    public void recheckConflictingItemRemoved() throws Exception {

        assertFalse( relationResolver.subsumes( b ) );

        reset( firstItemInB );

        // UPDATE
        b.remove( blockingItem );

        assertTrue( relationResolver.subsumes( b ) );

        verify( firstItemInB ).subsumes( any() );
    }

    @Test
    public void recheckOtherListBecomesEmpty() throws Exception {

        assertFalse( relationResolver.subsumes( b ) );

        reset( firstItemInB, blockingItem );

        // UPDATE
        b.clear();

        assertTrue( relationResolver.subsumes( b ) );

        verify( firstItemInB, never() ).subsumes( any() );
        verify( blockingItem, never() ).subsumes( any() );
    }

    public class Person
            implements IsSubsuming,
                       HasKeys {

        int age;

        private UUIDKey uuidKey = new UUIDKey( this );

        public Person( final int age ) {
            this.age = age;
        }

        @Override
        public UUIDKey getUuidKey() {
            return uuidKey;
        }

        @Override
        public Key[] keys() {
            return new Key[]{
                    uuidKey
            };
        }

        public void setAge( final int age ) {
            this.age = age;
        }

        @Override
        public boolean subsumes( final Object other ) {
            if ( other instanceof Person ) {
                return age == (( Person ) other).age;
            } else {
                return false;
            }
        }
    }
}