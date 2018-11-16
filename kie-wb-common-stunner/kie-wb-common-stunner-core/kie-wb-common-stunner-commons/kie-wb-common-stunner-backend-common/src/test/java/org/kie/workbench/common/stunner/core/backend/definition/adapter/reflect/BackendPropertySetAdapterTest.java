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

import java.util.Optional;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.backend.definition.adapter.FooPropertySetTestBean;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BackendPropertySetAdapterTest extends AbstractBackendAdapterTest {

    private BackendPropertySetAdapter<Object> tested;

    @Before
    public void setup() {
        super.setup();
        tested = new BackendPropertySetAdapter<>();
        when(adapterManager.forPropertySet()).thenReturn(tested);
    }

    @Test
    public void testGetId() {
        final String id = tested.getId(instancePropertySet);
        assertEquals(FooPropertySetTestBean.class.getName(), id);
    }

    @Test
    public void testName() {
        final String name = tested.getName(instancePropertySet);
        assertEquals(FooPropertySetTestBean.NAME, name);
    }

    @Test
    public void testProperties() {
        final Set properties = tested.getProperties(instancePropertySet);
        assertEquals(1, properties.size());
        assertTrue(properties.contains(instancePropertySet.fooProperty));
    }

    @Test
    public void testGetPropertyByName() {
        final Optional<?> property = tested.getProperty(instancePropertySet, FooPropertySetTestBean.FOO_PROPERTY_NAME);
        assertEquals(property.get(), instancePropertySet.fooProperty);
    }
}