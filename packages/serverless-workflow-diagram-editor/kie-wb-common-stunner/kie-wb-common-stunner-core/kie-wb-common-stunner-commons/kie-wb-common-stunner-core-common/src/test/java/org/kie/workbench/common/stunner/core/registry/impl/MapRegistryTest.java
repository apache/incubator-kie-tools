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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MapRegistryTest {

    @Mock
    private Map<String, Object> map;

    private MapRegistry<Object> tested;

    private final KeyProvider<Object> keyProvider = Object::toString;

    @Before
    public void setup() throws Exception {
        tested = new MapRegistry<Object>(keyProvider,
                                         map);
    }

    @Test
    public void testRegister() {
        final String s = "an string";
        tested.register(s);
        verify(map,
               times(1)).put(s,
                             s);
    }

    @Test
    public void testRemove() {
        final String s = "an string";
        tested.remove(s);
        verify(map,
               times(1)).remove(s);
    }

    @Test
    public void testContains() {
        final String s = "an string";
        tested.contains(s);
        verify(map,
               times(1)).containsValue(s);
    }

    @Test
    public void testClear() {
        tested.clear();
        verify(map,
               times(1)).clear();
    }

    @Test
    public void testGetItems() {
        final String s1 = "an string 1";
        final String s2 = "an string 2";
        tested = new MapRegistry<Object>(keyProvider,
                                         new HashMap<String, Object>(2) {{
                                             put(s1,
                                                 s1);
                                             put(s2,
                                                 s2);
                                         }});
        Collection<Object> items = tested.getItems();
        assertNotNull(items);
        assertEquals(2,
                     items.size());
        Iterator<Object> it = items.iterator();
        assertEquals(s1,
                     it.next());
        assertEquals(s2,
                     it.next());
    }

    @Test
    public void testGetItemByKey() {
        final String s1 = "an string 1";
        final String s2 = "an string 2";
        tested = new MapRegistry<Object>(keyProvider,
                                         new HashMap<String, Object>(2) {{
                                             put(s1,
                                                 s1);
                                             put(s2,
                                                 s2);
                                         }});
        Object o1 = tested.getItemByKey(s1);
        Object o2 = tested.getItemByKey(s2);
        assertEquals(s1,
                     o1);
        assertEquals(s2,
                     o2);
    }

    @Test
    public void testEmpty() {
        when(map.isEmpty()).thenReturn(true);
        boolean empty = tested.isEmpty();
        assertTrue(empty);
    }

    @Test
    public void testNotEmpty() {
        when(map.isEmpty()).thenReturn(false);
        boolean empty = tested.isEmpty();
        assertFalse(empty);
    }
}
