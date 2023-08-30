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


package org.kie.workbench.common.stunner.bpmn.workitem;

import java.util.HashMap;
import java.util.function.Consumer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class WorkItemDefinitionRegistriesTest {

    @Mock
    private Consumer<WorkItemDefinitionCacheRegistry> registryDestroyer;

    @Mock
    private WorkItemDefinitionCacheRegistry registry1;

    @Mock
    private WorkItemDefinitionCacheRegistry registry2;

    private WorkItemDefinitionRegistries<String> tested;

    @Before
    public void init() {
        tested = new WorkItemDefinitionRegistries<>(key -> key,
                                                    new HashMap<>(),
                                                    registryDestroyer);
    }

    @Test
    public void testPut() {
        tested.put("1",
                   registry1);
        tested.put("2",
                   registry2);
        assertTrue(tested.contains("1"));
        assertTrue(tested.contains("2"));
        assertFalse(tested.contains("3"));
    }

    @Test
    public void testObtain() {
        tested.put("1",
                   registry1);
        tested.put("2",
                   registry2);
        assertEquals(registry1, tested.registries().apply("1"));
        assertEquals(registry2, tested.registries().apply("2"));
    }

    @Test
    public void testRemove() {
        tested.put("1",
                   registry1);
        tested.put("2",
                   registry2);
        assertTrue(tested.contains("1"));
        assertTrue(tested.contains("2"));
        tested.remove("1");
        verify(registryDestroyer, times(1)).accept(eq(registry1));
        assertFalse(tested.contains("1"));
        assertTrue(tested.contains("2"));
        tested.remove("2");
        verify(registryDestroyer, times(1)).accept(eq(registry2));
        assertFalse(tested.contains("1"));
        assertFalse(tested.contains("2"));
    }

    @Test
    public void testClear() {
        tested.put("1",
                   registry1);
        tested.put("2",
                   registry2);
        assertTrue(tested.contains("1"));
        assertTrue(tested.contains("2"));
        tested.clear();
        assertFalse(tested.contains("1"));
        assertFalse(tested.contains("2"));
        verify(registryDestroyer, times(1)).accept(eq(registry1));
        verify(registryDestroyer, times(1)).accept(eq(registry2));
    }
}
