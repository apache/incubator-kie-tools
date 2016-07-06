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

import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.HasKeys;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.InspectorList;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.keys.Key;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.keys.UUIDKey;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class RelationResolverConflictsTest {

    private RelationResolver relationResolver;
    private InspectorList    a;
    private InspectorList    b;
    private Person           isConflicting;
    private Person           firstItemInA;

    @Before
    public void setUp() throws Exception {
        a = new InspectorList();
        b = new InspectorList();

        firstItemInA = spy( new Person( 10 ) );
        isConflicting = spy( new Person( 15 ) );
        a.add( firstItemInA );
        a.add( isConflicting );

        b.add( new Person( 10 ) );

        relationResolver = new RelationResolver( a,
                                                 true );
    }

    @Test
    public void empty() throws Exception {

        relationResolver = new RelationResolver( new InspectorList() );
        assertFalse( relationResolver.isConflicting( new InspectorList() ) );
    }

    @Test
    public void recheck() throws Exception {

        assertTrue( relationResolver.isConflicting( b ) );

        verify( firstItemInA ).conflicts( any() );

        reset( firstItemInA );

        assertTrue( relationResolver.isConflicting( b ) );

        verify( firstItemInA, never() ).conflicts( any() );
    }

    @Test
    public void recheckWithUpdate() throws Exception {

        assertTrue( relationResolver.isConflicting( b ) );

        reset( firstItemInA );

        // UPDATE
        isConflicting.setAge( 10 );

        assertFalse( relationResolver.isConflicting( b ) );

        verify( firstItemInA ).conflicts( any() );
    }

    @Test
    public void recheckConflictingItemRemoved() throws Exception {

        assertTrue( relationResolver.isConflicting( b ) );

        reset( firstItemInA );

        // UPDATE
        a.remove( isConflicting );

        assertFalse( relationResolver.isConflicting( b ) );

        verify( firstItemInA ).conflicts( any() );
    }

    @Test
    public void recheckOtherListBecomesEmpty() throws Exception {

        assertTrue( relationResolver.isConflicting( b ) );

        reset( firstItemInA, isConflicting );

        // UPDATE
        b.clear();

        assertFalse( relationResolver.isConflicting( b ) );

        verify( firstItemInA, never() ).conflicts( any() );
        verify( isConflicting, never() ).conflicts( any() );
    }

    public class Person
            implements IsConflicting,
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
        public boolean conflicts( final Object other ) {
            if ( other instanceof Person ) {
                return age != (( Person ) other).age;
            } else {
                return false;
            }
        }

    }
}