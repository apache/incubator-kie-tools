/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.backend;

import org.kie.workbench.common.stunner.core.StunnerTestingMockAPI;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.backend.definition.adapter.reflect.BackendDefinitionAdapter;
import org.kie.workbench.common.stunner.core.backend.definition.adapter.reflect.BackendDefinitionSetAdapter;
import org.kie.workbench.common.stunner.core.backend.definition.adapter.reflect.BackendPropertyAdapter;
import org.kie.workbench.common.stunner.core.backend.definition.adapter.reflect.BackendPropertySetAdapter;
import org.kie.workbench.common.stunner.core.backend.registry.impl.BackendRegistryFactoryImpl;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionSetRuleAdapter;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StunnerTestingBackendAPI extends StunnerTestingMockAPI {

    public BackendRegistryFactoryImpl registryFactory;

    @Override
    protected void initFactory() {
        registryFactory = new BackendRegistryFactoryImpl(adapterManager);
        factoryRegistry = registryFactory.newFactoryRegistry();
        factoryManager = mock(FactoryManager.class);
        when(factoryManager.registry()).thenReturn(factoryRegistry);
    }

    @Override
    protected void initAdapters() {
        definitionAdapter = new BackendDefinitionAdapter(definitionUtils);
        definitionSetAdapter = new BackendDefinitionSetAdapter((BackendDefinitionAdapter) definitionAdapter);
        propertySetAdapter = new BackendPropertySetAdapter();
        propertyAdapter = new BackendPropertyAdapter();
        ruleAdapter = mock(DefinitionSetRuleAdapter.class);
    }
}
