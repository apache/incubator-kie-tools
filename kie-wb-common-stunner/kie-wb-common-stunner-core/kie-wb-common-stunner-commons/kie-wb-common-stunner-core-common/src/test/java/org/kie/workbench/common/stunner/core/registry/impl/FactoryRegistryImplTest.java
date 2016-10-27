/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.registry.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.factory.definition.DefinitionFactory;
import org.kie.workbench.common.stunner.core.factory.impl.EdgeFactoryImpl;
import org.kie.workbench.common.stunner.core.factory.impl.NodeFactoryImpl;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith( MockitoJUnitRunner.class )
public class FactoryRegistryImplTest {

    private static final String NOT_VALID_ID = "Not valid ID";
    @Mock
    private AdapterManager adapter;
    @Mock
    private DefinitionFactory definitionFactory;
    @Mock
    private DefinitionFactory missingFactory;
    @Mock
    private EdgeFactoryImpl elementFactory;
    @Mock
    private NodeFactoryImpl elementFactory2;

    private FactoryRegistryImpl factory;

    @Before
    public void setup() {
        factory = new FactoryRegistryImpl( adapter );
        Class clazz = EdgeFactoryImpl.class;
        when( elementFactory.getFactoryType() ).thenReturn( clazz );
        when( definitionFactory.accepts( DefinitionFactory.class.getName() ) ).thenReturn( true );
    }

    @Test
    public void testGetDefinitionFactory() {
        assertNull( factory.getDefinitionFactory( DefinitionFactory.class.getName() ) );
        factory.register( definitionFactory );
        assertNull( factory.getDefinitionFactory( NOT_VALID_ID ) );
        assertEquals( definitionFactory, factory.getDefinitionFactory( DefinitionFactory.class.getName() ) );
        assertEquals( definitionFactory, factory.getDefinitionFactory( DefinitionFactory.class ) );
    }

    @Test
    public void testGetGraphFactory() {
        assertNull( factory.getGraphFactory( elementFactory.getFactoryType() ) );
        factory.register( elementFactory );
        assertEquals( elementFactory, factory.getGraphFactory( elementFactory.getFactoryType() ) );
    }

    @Test
    public void testGetItems() {
        factory.register( elementFactory );
        factory.register( definitionFactory );
        assertArrayEquals( new Object[]{ definitionFactory, elementFactory }, factory.getAllFactories().toArray() );
    }

    @Test
    public void testContains() {
        assertFalse( factory.contains( elementFactory ) );
        assertFalse( factory.contains( definitionFactory ) );
        assertFalse( factory.contains( null ) );
        factory.register( elementFactory );
        factory.register( definitionFactory );
        assertTrue( factory.contains( elementFactory ) );
        assertTrue( factory.contains( definitionFactory ) );
        assertFalse( factory.contains( missingFactory ) );
        assertFalse( factory.contains( null ) );
    }

    @Test
    public void testClear() {
        factory.register( elementFactory );
        factory.register( definitionFactory );
        factory.register( missingFactory );
        factory.clear();
        assertArrayEquals( new Object[ 0 ], factory.getAllFactories().toArray() );
    }

    @Test
    public void testRemove() {
        factory.register( elementFactory );
        factory.register( definitionFactory );
        assertFalse( factory.remove( missingFactory ) );
        assertTrue( factory.contains( elementFactory ) );
        assertTrue( factory.contains( definitionFactory ) );
        assertTrue( factory.remove( elementFactory ) );
        assertFalse( factory.contains( elementFactory ) );
        assertTrue( factory.contains( definitionFactory ) );
        assertFalse( factory.remove( null ) );
        assertTrue( factory.contains( definitionFactory ) );
        assertTrue( factory.remove( definitionFactory ) );
        assertFalse( factory.contains( definitionFactory ) );
        assertArrayEquals( new Object[ 0 ], factory.getAllFactories().toArray() );
    }
}
