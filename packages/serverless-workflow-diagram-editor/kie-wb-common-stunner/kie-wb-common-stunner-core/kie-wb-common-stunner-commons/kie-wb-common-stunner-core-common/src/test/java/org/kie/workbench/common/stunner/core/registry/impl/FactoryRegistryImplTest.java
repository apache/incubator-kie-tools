/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.core.registry.impl;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.factory.Factory;
import org.kie.workbench.common.stunner.core.factory.definition.DefinitionFactory;
import org.kie.workbench.common.stunner.core.factory.diagram.DiagramFactory;
import org.kie.workbench.common.stunner.core.factory.graph.ElementFactory;
import org.kie.workbench.common.stunner.core.factory.graph.GraphFactory;
import org.kie.workbench.common.stunner.core.factory.impl.EdgeFactoryImpl;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
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

    private FactoryRegistryImpl tested;

    @Before
    public void setup() {
        tested = new FactoryRegistryImpl(adapter);
        Class clazz = EdgeFactoryImpl.class;
        when(elementFactory.getFactoryType()).thenReturn(clazz);
        when(definitionFactory.accepts(DefinitionFactory.class.getName())).thenReturn(true);
    }

    @Test
    public void testGetDefinitionFactory() {
        assertNull(tested.getDefinitionFactory(DefinitionFactory.class.getName()));
        tested.register(definitionFactory);
        assertNull(tested.getDefinitionFactory(NOT_VALID_ID));
        assertEquals(definitionFactory,
                     tested.getDefinitionFactory(DefinitionFactory.class.getName()));
        assertEquals(definitionFactory,
                     tested.getDefinitionFactory(DefinitionFactory.class));
    }

    @Test
    public void testGetGraphFactory() {
        assertNull(tested.getElementFactory(elementFactory.getFactoryType()));
        tested.register(elementFactory);
        assertEquals(elementFactory,
                     tested.getElementFactory(elementFactory.getFactoryType()));
    }

    @Test
    public void testGetItems() {
        tested.register(elementFactory);
        tested.register(definitionFactory);
        assertArrayEquals(new Object[]{definitionFactory, elementFactory},
                          tested.getAllFactories().toArray());
    }

    @Test
    public void testContains() {
        assertFalse(tested.contains(elementFactory));
        assertFalse(tested.contains(definitionFactory));
        assertFalse(tested.contains(null));
        tested.register(elementFactory);
        tested.register(definitionFactory);
        assertTrue(tested.contains(elementFactory));
        assertTrue(tested.contains(definitionFactory));
        assertFalse(tested.contains(missingFactory));
        assertFalse(tested.contains(null));
    }

    @Test
    public void testClear() {
        tested.register(elementFactory);
        tested.register(definitionFactory);
        tested.register(missingFactory);
        tested.clear();
        assertArrayEquals(new Object[0],
                          tested.getAllFactories().toArray());
    }

    @Test
    public void testEmpty() {
        boolean empty = tested.isEmpty();
        assertTrue(empty);
    }

    @Test
    public void testNotEmpty() {
        tested.register(definitionFactory);
        boolean empty = tested.isEmpty();
        assertFalse(empty);
    }

    @Test
    public void testRemove() {
        tested.register(elementFactory);
        tested.register(definitionFactory);
        assertFalse(tested.remove(missingFactory));
        assertTrue(tested.contains(elementFactory));
        assertTrue(tested.contains(definitionFactory));
        assertTrue(tested.remove(elementFactory));
        assertFalse(tested.contains(elementFactory));
        assertTrue(tested.contains(definitionFactory));
        assertFalse(tested.remove(null));
        assertTrue(tested.contains(definitionFactory));
        assertTrue(tested.remove(definitionFactory));
        assertFalse(tested.contains(definitionFactory));
        assertArrayEquals(new Object[0],
                          tested.getAllFactories().toArray());
    }

    @Test
    public void testRegister() {
        DefinitionFactory definitionFactory = mock(DefinitionFactory.class);
        ElementFactory graphFactory = mock(ElementFactory.class);
        DiagramFactory diagramFactory = mock(DiagramFactory.class);
        Factory randomFactory = mock(Factory.class);

        FactoryRegistryImpl factory = new FactoryRegistryImpl(adapter);
        factory.register(definitionFactory);
        factory.register(graphFactory);
        factory.register(diagramFactory);
        factory.register(randomFactory);

        Collection<?> factories = factory.getAllFactories();
        assertEquals(3, factories.stream().count());
    }

    @Test
    public void testRegisterGraphFactory() {
        Class<? extends ElementFactory> factoryType = GraphFactory.class;
        ElementFactory graphFactory1 = createGraphFactory(factoryType);
        ElementFactory graphFactory2 = createGraphFactory(factoryType);
        ElementFactory delegateGraphFactory = createDelegateGraphFactory(factoryType);

        FactoryRegistryImpl factory = new FactoryRegistryImpl(adapter);

        factory.register(null);
        assertTrue(factory.getAllFactories().isEmpty());

        factory.registerGraphFactory(graphFactory1);
        assertEquals(graphFactory1, factory.getElementFactory(factoryType));

        factory.clear();
        factory.registerGraphFactory(delegateGraphFactory);
        assertEquals(delegateGraphFactory, factory.getElementFactory(factoryType));

        factory.clear();
        factory.registerGraphFactory(graphFactory1);
        factory.registerGraphFactory(graphFactory2);
        assertEquals(graphFactory1, factory.getElementFactory(factoryType));

        factory.registerGraphFactory(graphFactory1);
        factory.registerGraphFactory(delegateGraphFactory);
        assertEquals(delegateGraphFactory, factory.getElementFactory(factoryType));
    }

    private static ElementFactory createGraphFactory(final Class<? extends ElementFactory> factoryType) {
        return new ElementFactory() {
            @Override
            public Class<? extends ElementFactory> getFactoryType() {
                return factoryType;
            }

            @Override
            public Element build(String uuid, Object definition) {
                return null;
            }

            @Override
            public boolean accepts(Object source) {
                return false;
            }
        };
    }

    private static ElementFactory createDelegateGraphFactory(final Class<? extends ElementFactory> factoryType) {
        return new ElementFactory() {
            @Override
            public Class<? extends ElementFactory> getFactoryType() {
                return factoryType;
            }

            @Override
            public boolean isDelegateFactory() {
                return true;
            }

            @Override
            public Element build(String uuid, Object definition) {
                return null;
            }

            @Override
            public boolean accepts(Object source) {
                return false;
            }
        };
    }
}