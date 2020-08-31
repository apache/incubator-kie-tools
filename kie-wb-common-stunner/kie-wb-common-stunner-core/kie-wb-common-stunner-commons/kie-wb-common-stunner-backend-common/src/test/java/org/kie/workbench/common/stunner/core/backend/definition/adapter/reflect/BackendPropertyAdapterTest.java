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

package org.kie.workbench.common.stunner.core.backend.definition.adapter.reflect;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.backend.definition.adapter.FooProperty1TestBean;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BackendPropertyAdapterTest extends AbstractBackendAdapterTest {

    private static final String FOO1_VALUE = "foo1";

    private BackendPropertyAdapter<Object> tested;
    private FooProperty1TestBean instance;

    @Before
    public void setup() {
        super.setup();
        instance = new FooProperty1TestBean(FOO1_VALUE);
        tested = new BackendPropertyAdapter<>();
        when(adapterManager.forProperty()).thenReturn(tested);
    }

    @Test
    public void testGetId() {
        final String id = tested.getId(instance);
        assertEquals(FooProperty1TestBean.class.getName(), id);
    }

    @Test
    public void testGetCaption() {
        final String title = tested.getCaption(instance);
        assertEquals(FooProperty1TestBean.CAPTION, title);
    }

    @Test
    public void testGetValue() {
        final Object value = tested.getValue(instance);
        assertEquals(FOO1_VALUE, value);
    }

    @Test
    public void testSetValue() {
        tested.setValue(instance, "someNewValue");
        final Object value = tested.getValue(instance);
        assertEquals("someNewValue", value);
    }
}
