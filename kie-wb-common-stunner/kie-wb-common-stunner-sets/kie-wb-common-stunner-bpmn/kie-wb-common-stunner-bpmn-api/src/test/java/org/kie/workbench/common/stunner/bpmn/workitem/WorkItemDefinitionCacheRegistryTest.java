/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.bpmn.workitem;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class WorkItemDefinitionCacheRegistryTest {

    private static final WorkItemDefinition DEF1 = new WorkItemDefinition().setName("def1");
    private static final WorkItemDefinition DEF2 = new WorkItemDefinition().setName("def2");

    private WorkItemDefinitionCacheRegistry tested;

    @Before
    public void init() {
        tested = new WorkItemDefinitionCacheRegistry();
    }

    @Test
    public void testEmpty() {
        assertTrue(tested.isEmpty());
        assertTrue(tested.items().isEmpty());
        assertNull(tested.get("def1"));
    }

    @Test
    public void testPopulateCache() {
        assertTrue(tested.isEmpty());
        assertTrue(tested.items().isEmpty());
        assertNull(tested.get("def1"));
        tested.register(DEF1);
        tested.register(DEF2);
        assertFalse(tested.isEmpty());
        assertFalse(tested.items().isEmpty());
        assertEquals(2, tested.items().size());
        assertEquals(DEF1.getName(), tested.get("def1").getName());
        assertEquals(DEF2.getName(), tested.get("def2").getName());
    }

    @Test
    public void testRemoveCache() {
        assertTrue(tested.isEmpty());
        assertTrue(tested.items().isEmpty());
        assertNull(tested.get("def1"));
        tested.register(DEF1);
        tested.register(DEF2);
        assertFalse(tested.isEmpty());
        assertFalse(tested.items().isEmpty());
        assertEquals(2, tested.items().size());
        tested.remove("def1");
        assertEquals(1, tested.items().size());
        assertNull(tested.get("def1"));
        tested.clear();
        assertTrue(tested.isEmpty());
        assertTrue(tested.items().isEmpty());
        assertNull(tested.get("def1"));
    }
}
