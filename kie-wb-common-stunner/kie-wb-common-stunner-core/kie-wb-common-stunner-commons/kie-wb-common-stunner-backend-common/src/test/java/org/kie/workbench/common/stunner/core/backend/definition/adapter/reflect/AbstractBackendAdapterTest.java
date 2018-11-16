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

import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.backend.definition.adapter.FooPropertySetTestBean;
import org.kie.workbench.common.stunner.core.backend.definition.adapter.FooTestBean;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.mockito.Mock;

import static org.mockito.Mockito.when;

public abstract class AbstractBackendAdapterTest {

    protected static final String FOO1_VALUE = "foo1";
    protected static final String FOO2_VALUE = "foo2";

    @Mock
    protected DefinitionUtils utils;

    @Mock
    protected DefinitionManager definitionManager;

    @Mock
    protected AdapterManager adapterManager;

    protected FooTestBean instance;

    protected FooPropertySetTestBean instancePropertySet;

    public void setup() {
        when(utils.getDefinitionManager()).thenReturn(definitionManager);
        when(definitionManager.adapters()).thenReturn(adapterManager);
        when(adapterManager.forDefinition()).thenReturn(new BackendDefinitionAdapter<Object>(utils));
        when(adapterManager.forPropertySet()).thenReturn(new BackendPropertySetAdapter<Object>());
        when(adapterManager.forProperty()).thenReturn(new BackendPropertyAdapter<Object>());
        instance = new FooTestBean(FOO1_VALUE, FOO2_VALUE);
        instancePropertySet = new FooPropertySetTestBean(FOO1_VALUE);
    }
}
