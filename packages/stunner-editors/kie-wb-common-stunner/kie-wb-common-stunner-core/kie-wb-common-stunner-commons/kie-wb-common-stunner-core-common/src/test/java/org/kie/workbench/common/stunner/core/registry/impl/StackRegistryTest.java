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

import java.util.Stack;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StackRegistryTest {

    @Mock
    private Stack<Object> stack;

    private StackRegistry<Object> tested;

    private final KeyProvider<Object> keyProvider = Object::toString;

    @Before
    public void setup() throws Exception {
        tested = new StackRegistry<>(keyProvider,
                                     stack);
    }

    @Test
    public void testRegister() {
        final String s = "an string";
        tested.register(s);
        verify(stack,
               times(1)).add(s);
    }

    @Test
    public void testGetStack() {
        assertEquals(stack,
                     tested.getStack());
    }

    @Test
    public void testPeek() {
        tested.peek();
        verify(stack,
               times(1)).peek();
    }

    @Test
    public void testPop() {
        tested.pop();
        verify(stack,
               times(1)).pop();
    }

    @Test
    public void testRemove() {
        final String s = "an string";
        tested.remove(s);
        verify(stack,
               times(1)).remove(s);
    }

    @Test
    public void testContains() {
        final String s = "an string";
        tested.contains(s);
        verify(stack,
               times(1)).contains(s);
    }

    @Test
    public void testIndexOf() {
        final String s = "an string";
        tested.indexOf(s);
        verify(stack,
               times(1)).indexOf(s);
    }

    @Test
    public void testGetItemByKey() {
        final String s1 = "an string 1";
        final String s2 = "an string 2";
        tested = new StackRegistry<>(keyProvider,
                                     new Stack<Object>() {{
                                         push(s1);
                                         push(s2);
                                     }});
        Object o1 = tested.getItemByKey(s1);
        Object o2 = tested.getItemByKey(s2);
        assertEquals(s1,
                     o1);
        assertEquals(s2,
                     o2);
        assertEquals(null,
                     tested.getItemByKey(null));
        assertEquals(null,
                     tested.getItemByKey("unregistered string"));
    }

    @Test
    public void testEmpty() {
        when(stack.isEmpty()).thenReturn(true);
        boolean empty = tested.isEmpty();
        assertTrue(empty);
    }

    @Test
    public void testNotEmpty() {
        when(stack.isEmpty()).thenReturn(false);
        boolean empty = tested.isEmpty();
        assertFalse(empty);
    }
}
