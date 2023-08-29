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


package org.kie.workbench.common.stunner.core;

import java.util.Optional;

import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionId;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionSetAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionSetRuleAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.PropertyAdapter;
import org.kie.workbench.common.stunner.core.definition.annotation.Definition;
import org.kie.workbench.common.stunner.core.definition.property.PropertyMetaTypes;
import org.kie.workbench.common.stunner.core.factory.graph.EdgeFactory;
import org.kie.workbench.common.stunner.core.factory.graph.ElementFactory;
import org.kie.workbench.common.stunner.core.factory.graph.NodeFactory;
import org.kie.workbench.common.stunner.core.factory.impl.EdgeFactoryImpl;
import org.kie.workbench.common.stunner.core.factory.impl.NodeFactoryImpl;
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
        when(factoryRegistry.getElementFactory(NodeFactory.class)).thenReturn(spy(new NodeFactoryImpl(definitionUtils)));
        when(factoryRegistry.getElementFactory(EdgeFactory.class)).thenReturn(spy(new EdgeFactoryImpl(definitionManager)));
    }

    protected void initFactory() {
        factoryRegistry = mock(FactoryRegistry.class);
        factoryManager = mock(FactoryManager.class);
        when(factoryManager.registry()).thenReturn(factoryRegistry);
    }

    @SuppressWarnings("unchecked")
    protected void initAdapters() {
        definitionSetAdapter = mock(DefinitionSetAdapter.class);
        definitionAdapter = spy(new DefinitionAdapter() {
            @Override
            public DefinitionId getId(Object pojo) {
                if (null == pojo) {
                    return null;
                }
                return DefinitionId.build(pojo.getClass().getName());
            }

            @Override
            public String getCategory(Object pojo) {
                return "";
            }

            @Override
            public Class<? extends ElementFactory> getElementFactory(Object pojo) {
                return ElementFactory.class;
            }

            @Override
            public String getTitle(Object pojo) {
                return "";
            }

            @Override
            public String getDescription(Object pojo) {
                return "";
            }

            @Override
            public String[] getLabels(Object pojo) {
                return new String[0];
            }

            @Override
            public String[] getPropertyFields(Object pojo) {
                return new String[0];
            }

            @Override
            public Optional<?> getProperty(Object pojo, String field) {
                return Optional.empty();
            }

            @Override
            public String getMetaPropertyField(Object pojo, PropertyMetaTypes metaType) {
                return null;
            }

            @Override
            public Class<? extends ElementFactory> getGraphFactoryType(Object pojo) {
                Class<? extends ElementFactory> factoryClass = pojo.getClass().getAnnotation(Definition.class).graphFactory();
                return factoryClass;
            }

            @Override
            public int getPriority() {
                return 0;
            }

            @Override
            public boolean accepts(Class<?> type) {
                return true;
            }
        });
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
