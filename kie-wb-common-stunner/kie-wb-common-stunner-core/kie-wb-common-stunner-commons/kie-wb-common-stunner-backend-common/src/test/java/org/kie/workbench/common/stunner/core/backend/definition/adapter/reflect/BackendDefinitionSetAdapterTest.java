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

import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.backend.definition.adapter.FooTestBean;
import org.kie.workbench.common.stunner.core.backend.definition.adapter.FooTestSet;
import org.kie.workbench.common.stunner.core.factory.graph.ElementFactory;
import org.kie.workbench.common.stunner.core.factory.graph.GraphFactory;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BackendDefinitionSetAdapterTest extends AbstractBackendAdapterTest {

    private BackendDefinitionSetAdapter<Object> tested;
    private FooTestSet instance;

    @Before
    public void setup() {
        super.setup();
        instance = new FooTestSet();
        tested = new BackendDefinitionSetAdapter<>(new BackendDefinitionAdapter(utils));
        when(adapterManager.forDefinitionSet()).thenReturn(tested);
    }

    @Test
    public void testGetId() {
        final String id = tested.getId(instance);
        assertEquals(FooTestSet.class.getName(), id);
    }

    @Test
    public void testDomain() {
        final String category = tested.getDomain(instance);
        assertEquals(FooTestSet.class.getPackage().getName(), category);
    }

    @Test
    public void testGetDescription() {
        final String description = tested.getDescription(instance);
        assertEquals(FooTestSet.DESC, description);
    }

    @Test
    public void testGraphFactory() {
        final Class<? extends ElementFactory> graphFactoryType = tested.getGraphFactoryType(instance);
        assertEquals(GraphFactory.class, graphFactoryType);
    }

    @Test
    public void testGetDefinitions() {
        final Set<String> definitions = tested.getDefinitions(instance);
        assertEquals(1, definitions.size());
        assertEquals(FooTestBean.class.getName(), definitions.iterator().next());
    }
}
