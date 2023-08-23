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

package org.kie.workbench.common.dmn.client.commands.factory.graph;

import java.util.Optional;

import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionSetAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionSetRuleAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.PropertyAdapter;
import org.kie.workbench.common.stunner.core.registry.definition.AdapterRegistry;
import org.kie.workbench.common.stunner.core.registry.definition.TypeDefinitionSetRegistry;
import org.kie.workbench.common.stunner.core.registry.factory.FactoryRegistry;
import org.kie.workbench.common.stunner.core.registry.impl.DefaultDefinitionsCacheRegistry;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class StunnerTestingMockAPI extends StunnerTestingAPI {

    @SuppressWarnings("unchecked")
    protected void init() {
        initInstances();
        initBehaviors();
    }

    protected void initInstances() {
        definitionManager = mock(DefinitionManager.class);
        definitionSetRegistry = mock(TypeDefinitionSetRegistry.class);
        adapterManager = mock(AdapterManager.class);
        adapterRegistry = mock(AdapterRegistry.class);
        ruleManager = mock(RuleManager.class);
        initFactory();
        definitionUtils = spy(new DefinitionUtils(definitionManager,
                                                  new DefaultDefinitionsCacheRegistry(factoryManager,
                                                                                      adapterManager)));
        initAdapters();
    }

    protected void initFactory() {
        factoryRegistry = mock(FactoryRegistry.class);
        factoryManager = mock(FactoryManager.class);
        when(factoryManager.registry()).thenReturn(factoryRegistry);
    }

    @SuppressWarnings("unchecked")
    protected void initAdapters() {
        definitionSetAdapter = mock(DefinitionSetAdapter.class);
        definitionAdapter = mock(DefinitionAdapter.class);
        propertyAdapter = mock(PropertyAdapter.class);
        ruleAdapter = mock(DefinitionSetRuleAdapter.class);
        when(definitionAdapter.getProperty(anyObject(), anyString())).thenReturn(Optional.empty());
    }

    @SuppressWarnings("unchecked")
    protected void initBehaviors() {
        when(definitionManager.definitionSets()).thenReturn(definitionSetRegistry);
        when(definitionManager.adapters()).thenReturn(adapterManager);
        when(adapterManager.registry()).thenReturn(adapterRegistry);
        when(adapterManager.forDefinitionSet()).thenReturn(definitionSetAdapter);
        when(adapterManager.forDefinition()).thenReturn(definitionAdapter);
        when(adapterManager.forProperty()).thenReturn(propertyAdapter);
        when(adapterManager.forRules()).thenReturn(ruleAdapter);
        when(adapterRegistry.getDefinitionSetAdapter(any(Class.class))).thenReturn(definitionSetAdapter);
        when(adapterRegistry.getDefinitionAdapter(any(Class.class))).thenReturn(definitionAdapter);
        when(adapterRegistry.getPropertyAdapter(any(Class.class))).thenReturn(propertyAdapter);
        when(adapterRegistry.getDefinitionSetRuleAdapter(any(Class.class))).thenReturn(ruleAdapter);
        when(ruleManager.evaluate(any(),
                                  any())).thenReturn(DEFAULT_RULE_VIOLATIONS);
    }
}