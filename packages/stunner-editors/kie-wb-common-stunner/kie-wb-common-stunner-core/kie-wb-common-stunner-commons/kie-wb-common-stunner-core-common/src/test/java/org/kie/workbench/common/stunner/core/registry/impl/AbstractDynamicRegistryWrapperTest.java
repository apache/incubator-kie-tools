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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.registry.DynamicRegistry;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AbstractDynamicRegistryWrapperTest {

    @Mock
    DynamicRegistry<Object> registry;

    private AbstractDynamicRegistryWrapper tested;
    private String s1 = "s1";
    private String s2 = "s2";

    @Before
    public void setup() throws Exception {
        tested = new AbstractDynamicRegistryWrapper<Object, DynamicRegistry<Object>>(registry) {
        };
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRegister() {
        tested.register(s1);
        verify(registry,
               times(1)).register(eq(s1));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRemove() {
        tested.remove(s1);
        verify(registry,
               times(1)).remove(eq(s1));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testContains() {
        when(registry.contains(anyObject())).thenReturn(false);
        when(registry.contains(eq(s1))).thenReturn(true);
        when(registry.contains(eq(s2))).thenReturn(true);
        assertTrue(tested.contains(s1));
        assertTrue(tested.contains(s2));
        assertFalse(tested.contains(""));
    }
}
