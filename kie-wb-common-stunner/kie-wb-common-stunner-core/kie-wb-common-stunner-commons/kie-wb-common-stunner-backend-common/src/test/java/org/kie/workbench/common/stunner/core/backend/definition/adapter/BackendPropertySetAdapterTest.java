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

package org.kie.workbench.common.stunner.core.backend.definition.adapter;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.backend.definition.adapter.reflect.AbstractBackendAdapterTest;
import org.kie.workbench.common.stunner.core.backend.definition.adapter.reflect.BackendPropertySetAdapter;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BackendPropertySetAdapterTest extends AbstractBackendAdapterTest {

    private static final String FOO1_VALUE = "foo1";

    private BackendPropertySetAdapter<Object> tested;
    private FooPropertySetTestBean instance;

    @Before
    public void setup() {
        super.setup();
        instance = new FooPropertySetTestBean(FOO1_VALUE);
        tested = new BackendPropertySetAdapter<>();
        when(adapterManager.forPropertySet()).thenReturn(tested);
    }

    @Test
    public void testGetId() {
        final String id = tested.getId(instance);
        assertEquals(FooPropertySetTestBean.class.getName(), id);
    }

    @Test
    public void testName() {
        final String name = tested.getName(instance);
        assertEquals(FooPropertySetTestBean.NAME, name);
    }

    @Test
    public void testProperties() {
        final Set properties = tested.getProperties(instance);
        assertEquals(1, properties.size());
        ;
        assertTrue(properties.contains(instance.fooProperty));
    }
}
